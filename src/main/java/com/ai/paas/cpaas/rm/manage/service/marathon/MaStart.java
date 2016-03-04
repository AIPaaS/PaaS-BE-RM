package com.ai.paas.cpaas.rm.manage.service.marathon;

import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ai.paas.cpaas.rm.util.AnsibleCommand;
import com.ai.paas.cpaas.rm.util.OpenPortUtil;
import com.ai.paas.cpaas.rm.util.TaskUtil;
import com.ai.paas.cpaas.rm.vo.MesosInstance;
import com.ai.paas.cpaas.rm.vo.OpenResourceParamVo;

public class MaStart implements Tasklet {

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    InputStream in = OpenPortUtil.class.getResourceAsStream("/playbook/marathon/startmarathon.yml");
    String content = TaskUtil.getFile(in);
    OpenResourceParamVo openParam = TaskUtil.createOpenParam(chunkContext);
    String aid = openParam.getAid();
    Boolean useAgent = openParam.getUseAgent();
    TaskUtil.uploadFile("startmarathon.yml", content, useAgent, aid);

    StringBuffer shellContext = TaskUtil.createBashFile();
    List<MesosInstance> mesosMaster = openParam.getMesosMaster();
    String password =
        (String) chunkContext.getStepContext().getStepExecution().getJobExecution()
            .getExecutionContext().get("password");
    for (int i = 0; i < mesosMaster.size(); i++) {
      MesosInstance masterInstance = mesosMaster.get(i);
      List<String> startVars = new ArrayList<String>();
      startVars.add("ansible_ssh_pass=" + password);
      startVars.add("ansible_become_pass=" + password);
      startVars.add("hosts=" + TaskUtil.genMasterName(i + 1));
      startVars.add("hostname=" + masterInstance.getIp());
      AnsibleCommand masterStart =
          new AnsibleCommand(TaskUtil.getSystemProperty("filepath") + "/startmarathon.yml",
              "rcmarathon", startVars);
      shellContext.append(masterStart.toString());
      shellContext.append("\n");
    }
    Timestamp start = new Timestamp(System.currentTimeMillis());
    String result = TaskUtil.executeFile("marathonStart", shellContext.toString(), useAgent, aid);
    // 插入日志和任务记录
    int taskId =
        TaskUtil.insertResJobDetail(start, openParam.getClusterId(), shellContext.toString(), 20);
    TaskUtil.insertResTaskLog(openParam.getClusterId(), taskId, result);
    return RepeatStatus.FINISHED;
  }

}
