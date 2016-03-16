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
    String[] files = {"consulstart.yml"};
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
      String ip = list.get(i).getIp();
      configvars.add("client_addr=" + ip);
      configvars.add("ip=" + ip);
      int id = list.get(i).getId();
      configvars.add("node_name=" + TaskUtil.genMasterName(id));
      configvars.add("hosts=" + TaskUtil.genMasterName(id));
      configvars.add("bootstrap=false");
      configvars.add("startjoin=" + this.genStartJoin(i, list));
      AnsibleCommand command =
          new AnsibleCommand(TaskUtil.getSystemProperty("filepath") + "/consulstart.yml", "root",
              configvars);
      shellContext.append(command.toString());
      shellContext.append("\n");
    }


    Timestamp start = new Timestamp(System.currentTimeMillis());

    String result = new String();
    int status = TaskUtil.FINISHED;
    try {
      result = TaskUtil.executeFile("consulStart", shellContext.toString(), useAgent, aid);
    } catch (Exception e) {
      Log.error(e.toString());
      result = e.toString();
      status = TaskUtil.FAILED;
      throw new PaasException(ExceptionCodeConstants.DubboServiceCode.SYSTEM_ERROR_CODE,
          e.toString());
    } finally {
      // insert log and task record
      int taskId =
          TaskUtil.insertResJobDetail(start, openParam.getClusterId(), shellContext.toString(),
              TaskUtil.getTypeId("consulStart"), status);
      TaskUtil.insertResTaskLog(openParam.getClusterId(), taskId, result);
    }
    return RepeatStatus.FINISHED;
  }

  public String genStartJoin(int num, List<MesosInstance> list) {
    StringBuffer context = new StringBuffer();
    for (int i = 0; i < list.size(); i++) {
      if (i != num) {
        MesosInstance instance = list.get(i);
        context.append("\\\\\\\"").append(instance.getIp()).append("\\\\\\\"");
        context.append(",");
      }
    }
    String result = context.substring(0, context.length() - 1);
    return result;
  }
}
