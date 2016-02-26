package com.ai.paas.cpaas.rm.manage.service.mesos;

import java.io.InputStream;
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

public class MeMasterStart implements Tasklet {

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    InputStream in = OpenPortUtil.class.getResourceAsStream("/playbook/mesos/memasterstart.yml");
    String content = TaskUtil.getFile(in);
    OpenResourceParamVo openParam = TaskUtil.createOpenParam(chunkContext);
    Boolean useAgent = openParam.getUseAgent();
    TaskUtil.uploadFile("memasterstart.yml", content, useAgent);
    List<MesosInstance> mesosMaster = openParam.getMesosMaster();
    StringBuffer shellContext = TaskUtil.createBashFile();
    String password =
        (String) chunkContext.getStepContext().getStepExecution().getJobExecution()
            .getExecutionContext().get("password");
    // 添加hostname文件，并启动mesos master
    for (int i = 0; i < mesosMaster.size(); i++) {
      MesosInstance masterInstance = mesosMaster.get(i);
      List<String> startVars = new ArrayList<String>();
      startVars.add("ansible_ssh_pass=" + password);
      startVars.add("ansible_become_pass=" + password);
      startVars.add("hosts=" + TaskUtil.genMasterName(i + 1));
      startVars.add("hostname=" + masterInstance.getIp());
      AnsibleCommand masterStart =
          new AnsibleCommand(TaskUtil.getSystemProperty("filepath") + "/memasterstart.yml",
              "rcmesos", startVars);
      shellContext.append(masterStart.toString());
      shellContext.append(System.lineSeparator());
    }
    TaskUtil.executeFile("mesosMasterStart", shellContext.toString(), useAgent);
    return RepeatStatus.FINISHED;
  }
}
