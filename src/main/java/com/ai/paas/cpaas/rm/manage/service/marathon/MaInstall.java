package com.ai.paas.cpaas.rm.manage.service.marathon;

import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ai.paas.cpaas.rm.dao.interfaces.ResClusterInfoMapper;
import com.ai.paas.cpaas.rm.dao.mapper.bo.ResClusterInfo;
import com.ai.paas.cpaas.rm.util.AnsibleCommand;
import com.ai.paas.cpaas.rm.util.OpenPortUtil;
import com.ai.paas.cpaas.rm.util.TaskUtil;
import com.ai.paas.cpaas.rm.vo.MesosInstance;
import com.ai.paas.cpaas.rm.vo.OpenResourceParamVo;
import com.ai.paas.ipaas.ServiceUtil;

public class MaInstall implements Tasklet {

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    InputStream in =
        OpenPortUtil.class.getResourceAsStream("/playbook/marathon/marathoninstall.yml");
    String content = TaskUtil.getFile(in);
    OpenResourceParamVo openParam = TaskUtil.createOpenParam(chunkContext);
    String aid = openParam.getAid();
    Boolean useAgent = openParam.getUseAgent();
    TaskUtil.uploadFile("marathoninstall.yml", content, useAgent, aid);
    List<MesosInstance> mesosMaster = openParam.getMesosMaster();
    MesosInstance mesosInstance = mesosMaster.get(0);
    String password =
        (String) chunkContext.getStepContext().getStepExecution().getJobExecution()
            .getExecutionContext().get("password");
    StringBuffer zkMessage = new StringBuffer();
    zkMessage.append(mesosInstance.getIp() + ":2181");
    for (int i = 1; i < mesosMaster.size(); i++) {
      MesosInstance mesos = mesosMaster.get(i);
      zkMessage.append("," + mesos.getIp() + ":2181");
    }
    StringBuffer zk = new StringBuffer();
    zk.append("zk=zk://");
    zk.append(zkMessage.toString());
    zk.append("/marathon");
    StringBuffer master = new StringBuffer();
    master.append("master=zk://");
    master.append(zkMessage.toString());
    master.append("/mesos");
    // ��װmarathon
    List<String> installvars = new ArrayList<String>();
    installvars.add(zk.toString());
    installvars.add(master.toString());
    installvars.add("ansible_ssh_pass=" + password);
    installvars.add("ansible_become_pass=" + password);
    AnsibleCommand installCommand =
        new AnsibleCommand(TaskUtil.getSystemProperty("filepath") + "/marathoninstall.yml",
            "rcmarathon", installvars);
    Timestamp start = new Timestamp(System.currentTimeMillis());
    String result =
        TaskUtil.executeFile("marathoninstall", installCommand.toString(), useAgent, aid);
    // ������־�������¼
    int taskId =
        TaskUtil.insertResJobDetail(start, openParam.getClusterId(), installCommand.toString(), 19);
    TaskUtil.insertResTaskLog(openParam.getClusterId(), taskId, result);


    // ��Ⱥ��Ϣ���в�������
    ResClusterInfoMapper resClusterInfoMapper = ServiceUtil.getMapper(ResClusterInfoMapper.class);
    ResClusterInfo resClusterInfo = new ResClusterInfo();
    resClusterInfo.setClusterId(openParam.getClusterId());
    resClusterInfo.setClusterName(openParam.getClusterName());
    resClusterInfo.setExternalDomain(openParam.getExternalDomain());
    resClusterInfo.setMesosDomain(master.toString());
    resClusterInfo.setMarathonAddr(zk.toString());
    // TODO
    // chronosΪ��װ����ʱ������
    // resClusterInfoMapper.insert(resClusterInfo);

    return RepeatStatus.FINISHED;
  }

}
