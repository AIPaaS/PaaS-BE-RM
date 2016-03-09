package com.ai.paas.cpaas.rm.manage.service.mesosdns;

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

public class ConfigDNS implements Tasklet {

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    OpenResourceParamVo openParam = TaskUtil.createOpenParam(chunkContext);
    Boolean useAgent = openParam.getUseAgent();
    String aid = openParam.getAid();
    InputStream in = OpenPortUtil.class.getResourceAsStream("/playbook/mesosdns/configDNS.yml");
    String content = TaskUtil.getFile(in);
    TaskUtil.uploadFile("configDNS.yml", content, useAgent, aid);
    List<MesosInstance> masterList = openParam.getMesosMaster();
    MesosInstance masterInstance = masterList.get(0);

    String password = masterInstance.getPasswd();
    StringBuffer nodes = new StringBuffer();
    nodes.append("nodes=[");
    nodes.append("'").append(masterInstance.getIp()).append("'");
    for (int i = 1; i < masterList.size(); i++) {
      nodes.append(",");
      nodes.append("'").append(masterList.get(i).getIp()).append("'");
    }
    nodes.append("]");

    List<String> vars = new ArrayList<String>();
    vars.add("ansible_ssh_pass=" + password);
    vars.add("ansible_become_pass=" + password);
    vars.add(nodes.toString());

    AnsibleCommand command =
        new AnsibleCommand(TaskUtil.getSystemProperty("filepath") + "/configDNS.yml", "root", vars);
    Timestamp start = new Timestamp(System.currentTimeMillis());

    String result = new String();
    try {
      result = TaskUtil.executeFile("configDNS", command.toString(), useAgent, aid);
    } catch (Exception e) {
      Log.error(e.toString());
      result = e.toString();
      throw new PaasException(ExceptionCodeConstants.DubboServiceCode.SYSTEM_ERROR_CODE,
          e.toString());
    } finally {
      // insert log and task record
      int taskId =
          TaskUtil.insertResJobDetail(start, openParam.getClusterId(), command.toString(),
              TaskUtil.getTypeId("configDNS"));
      TaskUtil.insertResTaskLog(openParam.getClusterId(), taskId, result);
    }
    return RepeatStatus.FINISHED;
  }

}
