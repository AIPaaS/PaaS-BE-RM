package com.ai.paas.cpaas.rm.manage.service.flannel;

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

public class FlannelInstall implements Tasklet {

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
    OpenResourceParamVo openParam = TaskUtil.createOpenParam(chunkContext);
    List<MesosInstance> mesosMaster = openParam.getMesosMaster();
    MesosInstance masternode = mesosMaster.get(0);
    StringBuffer flannelEtcd = new StringBuffer("FLANNEL_ETCD=");
    flannelEtcd.append("http://" + masternode.getIp());
    for (int i = 1; i < mesosMaster.size(); i++) {
      flannelEtcd.append(",http://");
      flannelEtcd.append(mesosMaster.get(i));
    }
    // TODO
    // �����ݿ��л�ȡrcflannel�û������룬���ܺ�ʹ��
    String password = new String();
    List<String> vars = new ArrayList<String>();
    vars.add("ansible_ssh_pass=" + password);
    vars.add("ansible_become_pass=" + password);
    vars.add("flanneletcd='" + flannelEtcd.toString() + "'");
    AnsibleCommand command = new AnsibleCommand(TaskUtil.filepath + "flannelinstall.yml", "rcflannel", vars);
    StringEntity entity = TaskUtil.genCommandParam(command.toString());
    TaskUtil.executeCommand(entity,"command");
    return RepeatStatus.FINISHED;
  }

}
