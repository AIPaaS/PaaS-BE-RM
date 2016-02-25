package com.ai.paas.cpaas.rm.manage.service.docker;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.entity.StringEntity;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ai.paas.cpaas.rm.util.AnsibleCommand;
import com.ai.paas.cpaas.rm.util.OpenPortUtil;
import com.ai.paas.cpaas.rm.util.TaskUtil;
import com.ai.paas.cpaas.rm.vo.MesosInstance;
import com.ai.paas.cpaas.rm.vo.OpenResourceParamVo;

public class DockerInstallService implements Tasklet {

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
    InputStream in = OpenPortUtil.class.getResourceAsStream("/playbook/docker/dockerinstall.yml");
    TaskUtil.uploadFile("dockerinstall.yml", in);
    OpenResourceParamVo openParam = TaskUtil.createOpenParam(chunkContext);
    List<MesosInstance> mesosMaster = openParam.getMesosMaster();
    MesosInstance instance = mesosMaster.get(0);
    String password = instance.getPasswd();
    List<String> configvars = new ArrayList<String>();
    configvars.add("ansible_ssh_pass=" + password);
    configvars.add("ansible_become_pass=" + password);
    AnsibleCommand dockerinstall = new AnsibleCommand(TaskUtil.getSystemProperty("filepath") + "/dockerinstall.yml", instance.getRoot(), configvars);
    StringEntity entity = TaskUtil.genCommandParam(dockerinstall.toString());
    TaskUtil.executeCommand(entity,"command");
    return RepeatStatus.FINISHED;
  }

}
