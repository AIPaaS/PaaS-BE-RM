package com.ai.paas.cpaas.rm.manage.service.etcd;

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

public class EtcdStart implements Tasklet {

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    InputStream in = OpenPortUtil.class.getResourceAsStream("/playbook/etcd/etcdstart.yml");
    String content = TaskUtil.getFile(in);
    OpenResourceParamVo openParam = TaskUtil.createOpenParam(chunkContext);
    Boolean useAgent = openParam.getUseAgent();
    TaskUtil.uploadFile("etcdstart.yml", content, useAgent);
    List<MesosInstance> mesosMaster = openParam.getMesosMaster();
    MesosInstance masternode = mesosMaster.get(0);
    String url = "http://" + masternode.getIp() + ":2379";
    String password =
        (String) chunkContext.getStepContext().getStepExecution().getJobExecution()
            .getExecutionContext().get("password");
    List<String> vars = new ArrayList<String>();
    vars.add("ansible_ssh_pass=" + password);
    vars.add("ansible_become_pass=" + password);
    vars.add("etcdhost='" + url + "'");
    AnsibleCommand command =
        new AnsibleCommand(TaskUtil.getSystemProperty("filepath") + "etcdstart.yml", "rcflannel",
            vars);
    TaskUtil.executeCommand(command.toString(), useAgent);
    return RepeatStatus.FINISHED;
  }

}
