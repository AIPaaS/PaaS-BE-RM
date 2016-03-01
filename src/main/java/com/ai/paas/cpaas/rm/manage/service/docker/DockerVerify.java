package com.ai.paas.cpaas.rm.manage.service.docker;

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

public class DockerVerify implements Tasklet {

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    OpenResourceParamVo openParam = TaskUtil.createOpenParam(chunkContext);
    InputStream in = OpenPortUtil.class.getResourceAsStream("/playbook/docker/dockerVerify.yml");
    String content = TaskUtil.getFile(in);
    Boolean useAgent = openParam.getUseAgent();
    TaskUtil.uploadFile("dockerVerify.yml", content, useAgent);


    List<MesosInstance> mesosMaster = openParam.getMesosMaster();
    List<String> vars = new ArrayList<String>();
    MesosInstance masternode = mesosMaster.get(0);
    String passwd = masternode.getPasswd();
    vars.add("ansible_ssh_pass=" + passwd);
    vars.add("ansible_become_pass=" + passwd);
    AnsibleCommand command =
        new AnsibleCommand(TaskUtil.getSystemProperty("filepath") + "/dockerVerify.yml", "root",
            vars);
    TaskUtil.executeFile("dockerVerify.sh", command.toString(), useAgent);
    return RepeatStatus.FINISHED;
  }

}
