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
import com.ai.paas.cpaas.rm.vo.Attributes;
import com.ai.paas.cpaas.rm.vo.MesosInstance;
import com.ai.paas.cpaas.rm.vo.OpenResourceParamVo;

public class ConfigFlannelParam implements Tasklet {

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    InputStream in = OpenPortUtil.class.getResourceAsStream("/playbook/etcd/flannelConfig.yml");
    String content = TaskUtil.getFile(in);
    OpenResourceParamVo openParam = TaskUtil.createOpenParam(chunkContext);
    Boolean useAgent = openParam.getUseAgent();
    TaskUtil.uploadFile("flannelConfig.yml", content, useAgent);
    StringBuffer shellContext = TaskUtil.createBashFile();
    List<MesosInstance> mesosMaster = openParam.getMesosMaster();
    MesosInstance masternode = mesosMaster.get(0);
    String url = "http://" + masternode.getIp() + ":2379";
    // String password =(String)
    // chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().get("password");
    String password = masternode.getPasswd();

    List<Attributes> attributesList = openParam.getAttributesList();
    for (Attributes attributes : attributesList) {
      List<String> vars = new ArrayList<String>();
      vars.add("ansible_ssh_pass=" + password);
      vars.add("ansible_become_pass=" + password);
      vars.add("hosts=" + masternode.getIp());
      vars.add("etcdhost='" + url + "'");
      vars.add("path=" + TaskUtil.genEtcdParam(openParam, attributes.getZone()));
      vars.add("subnet=" + attributes.getNetwork());
      AnsibleCommand command =
          new AnsibleCommand(TaskUtil.getSystemProperty("filepath") + "/flannelConfig.yml", "root",
              vars);
      shellContext.append(command.toString()).append("\n");
    }
    TaskUtil.executeFile("flannelConfig", shellContext.toString(), useAgent);
    return RepeatStatus.FINISHED;
  }

}
