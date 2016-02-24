package com.ai.paas.cpaas.rm.manage.service.etcd;

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

public class EtcdStart implements Tasklet {

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
    OpenResourceParamVo openParam = TaskUtil.createOpenParam(chunkContext);
    List<MesosInstance> mesosMaster = openParam.getMesosMaster();
    MesosInstance masternode = mesosMaster.get(0);
    String url = "http://" + masternode.getIp() + ":2379";
    String password = (String) chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().get("password");
    List<String> vars = new ArrayList<String>();
    vars.add("ansible_ssh_pass=" + password);
    vars.add("ansible_become_pass=" + password);
    vars.add("etcdhost='" + url + "'");
    AnsibleCommand command = new AnsibleCommand(TaskUtil.filepath + "etcdstart.yml", "rcflannel", vars);
    StringEntity entity = TaskUtil.genCommandParam(command.toString());
    TaskUtil.executeCommand(entity);
    return RepeatStatus.FINISHED;
  }

}
