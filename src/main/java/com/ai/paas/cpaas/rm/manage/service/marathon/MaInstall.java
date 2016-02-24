package com.ai.paas.cpaas.rm.manage.service.marathon;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.entity.StringEntity;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ai.paas.cpaas.rm.util.AnsibleCommand;
import com.ai.paas.cpaas.rm.util.TaskUtil;
import com.ai.paas.cpaas.rm.vo.MesosInstance;
import com.ai.paas.cpaas.rm.vo.OpenResourceParamVo;

public class MaInstall implements Tasklet {

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
    OpenResourceParamVo openParam = TaskUtil.createOpenParam(chunkContext);
    List<MesosInstance> mesosMaster = openParam.getMesosMaster();
    MesosInstance mesosInstance = mesosMaster.get(0);
    String user = mesosInstance.getRoot();
    String passwd = mesosInstance.getPasswd();
    StringBuffer zkMessage = new StringBuffer();
    zkMessage.append(mesosInstance.getIp() + ":2181");
    for (int i = 1; i < mesosMaster.size(); i++) {
      MesosInstance mesos = mesosMaster.get(i);
      zkMessage.append("," + mesos.getIp() + ":2181");
    }
    StringBuffer zk = new StringBuffer();
    zk.append("zk= zk://");
    zk.append(zkMessage.toString());
    zk.append("/marathon");
    StringBuffer master = new StringBuffer();
    master.append("master= zk://");
    master.append(zkMessage.toString());
    master.append("/mesos");
    // °²×°marathon
    List<String> installvars = new ArrayList<String>();
    installvars.add(zk.toString());
    installvars.add(master.toString());
    installvars.add("ansible_ssh_pass=" + passwd);
    installvars.add("ansible_become_pass=" + passwd);
    AnsibleCommand installCommand = new AnsibleCommand(TaskUtil.filepath + "marathoninstall.yml", user, installvars);
    StringEntity entity = TaskUtil.genCommandParam(installCommand.toString());
    TaskUtil.executeCommand(entity);
    return RepeatStatus.FINISHED;
  }

}
