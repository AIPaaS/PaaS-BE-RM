package com.ai.paas.cpaas.rm.manage.service.zookeeper;

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

public class StartZkService implements Tasklet {

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    InputStream in =
        OpenPortUtil.class.getResourceAsStream("/playbook/zookeeper/zookeeperstart.yml");
    String content = TaskUtil.getFile(in);
    OpenResourceParamVo openParam = TaskUtil.createOpenParam(chunkContext);
    Boolean useAgent = openParam.getUseAgent();
    TaskUtil.uploadFile("zookeeperstart.yml", content, useAgent);

    StringBuffer shellContext = TaskUtil.createBashFile();
    List<MesosInstance> mesosMaster = openParam.getMesosMaster();
    MesosInstance instance = mesosMaster.get(0);
    String password = instance.getPasswd();
    // Æô¶¯zookeeper·þÎñ
    for (int i = 0; i < mesosMaster.size(); i++) {
      List<String> startvars = new ArrayList<String>();
      startvars.add("ansible_ssh_pass=" + password);
      startvars.add("ansible_become_pass=" + password);
      startvars.add("myid=" + (i + 1));
      startvars.add("hosts=mesos-master" + (i + 1));
      AnsibleCommand startzkCommand =
          new AnsibleCommand(TaskUtil.getSystemProperty("filepath") + "/zookeeperstart.yml",
              "root", startvars);
      shellContext.append(startzkCommand.toString());
      shellContext.append(System.lineSeparator());
    }
    TaskUtil.executeFile("StartZkServiceStep", shellContext.toString(), useAgent);
    return RepeatStatus.FINISHED;
  }

}
