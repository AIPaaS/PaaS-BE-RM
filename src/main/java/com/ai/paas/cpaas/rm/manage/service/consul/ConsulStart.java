package com.ai.paas.cpaas.rm.manage.service.consul;

import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ai.paas.cpaas.rm.util.AnsibleCommand;
import com.ai.paas.cpaas.rm.util.ExceptionCodeConstants;
import com.ai.paas.cpaas.rm.util.OpenPortUtil;
import com.ai.paas.cpaas.rm.util.TaskUtil;
import com.ai.paas.cpaas.rm.vo.MesosInstance;
import com.ai.paas.cpaas.rm.vo.OpenResourceParamVo;
import com.ai.paas.ipaas.PaasException;
import com.esotericsoftware.minlog.Log;

public class ConsulStart implements Tasklet {

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    OpenResourceParamVo openParam = TaskUtil.createOpenParam(chunkContext);
    Boolean useAgent = openParam.getUseAgent();
    String aid = openParam.getAid();
    String[] files = {"consulstart.yml", "consuljoin.yml"};
    for (String file : files) {
      InputStream in = OpenPortUtil.class.getResourceAsStream("/playbook/consul/" + file);
      String content = TaskUtil.getFile(in);
      TaskUtil.uploadFile(file, content, useAgent, aid);
    }
    StringBuffer shellContext = TaskUtil.createBashFile();

    List<MesosInstance> list = openParam.getMesosMaster();
    MesosInstance master = list.get(0);
    String password = master.getPasswd();

    for (int i = 0; i < list.size(); i++) {
      List<String> configvars = new ArrayList<String>();
      configvars.add("ansible_ssh_pass=" + password);
      configvars.add("ansible_become_pass=" + password);
      configvars.add("datacenter=" + openParam.getDataCenter());
      configvars.add("domain=" + openParam.getExternalDomain());
      configvars.add("client_addr=" + list.get(i).getIp());
      configvars.add("node_name=" + TaskUtil.genMasterName(i + 1));
      configvars.add("hosts=" + TaskUtil.genMasterName(i + 1));
      AnsibleCommand command =
          new AnsibleCommand(TaskUtil.getSystemProperty("filepath") + "/consulstart.yml", "root",
              configvars);
      shellContext.append(command.toString());
      shellContext.append("\n");
    }

    StringBuffer nodes = new StringBuffer();
    nodes.append("nodes=[");
    nodes.append("'").append(list.get(1).getIp()).append("'");
    for (int i = 2; i < list.size(); i++) {
      nodes.append(",");
      nodes.append("'").append(list.get(i).getIp()).append("'");
    }
    nodes.append("]");

    List<String> vars = new ArrayList<String>();
    vars.add("ansible_ssh_pass=" + password);
    vars.add("ansible_become_pass=" + password);
    vars.add(nodes.toString());
    vars.add("hosts=" + TaskUtil.genMasterName(1));
    AnsibleCommand joinCommand =
        new AnsibleCommand(TaskUtil.getSystemProperty("filepath") + "/consuljoin.yml", "root", vars);
    shellContext.append(joinCommand.toString());
    shellContext.append("\n");

    Timestamp start = new Timestamp(System.currentTimeMillis());

    String result = new String();
    try {
      result = TaskUtil.executeFile("consulStart", shellContext.toString(), useAgent, aid);
    } catch (Exception e) {
      Log.error(e.toString());
      result = e.toString();
      throw new PaasException(ExceptionCodeConstants.DubboServiceCode.SYSTEM_ERROR_CODE,
          e.toString());
    } finally {
      // insert log and task record
      int taskId =
          TaskUtil.insertResJobDetail(start, openParam.getClusterId(), shellContext.toString(),
              TaskUtil.getTypeId("consulStart"));
      TaskUtil.insertResTaskLog(openParam.getClusterId(), taskId, result);
    }
    return RepeatStatus.FINISHED;
  }

}
