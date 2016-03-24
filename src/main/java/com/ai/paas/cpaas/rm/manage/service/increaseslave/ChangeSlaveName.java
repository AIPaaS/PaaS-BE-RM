package com.ai.paas.cpaas.rm.manage.service.increaseslave;

import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ai.paas.cpaas.rm.manage.service.zookeeper.ChangeHostNameStep;
import com.ai.paas.cpaas.rm.util.AnsibleCommand;
import com.ai.paas.cpaas.rm.util.ExceptionCodeConstants;
import com.ai.paas.cpaas.rm.util.TaskUtil;
import com.ai.paas.cpaas.rm.vo.MesosSlave;
import com.ai.paas.cpaas.rm.vo.OpenResourceParamVo;
import com.ai.paas.ipaas.PaasException;

public class ChangeSlaveName implements Tasklet {
  private static Logger logger = Logger.getLogger(ChangeSlaveName.class);

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    String[] files = {"hostnamectl.yml"};
    OpenResourceParamVo openParam = TaskUtil.createOpenParam(chunkContext);
    String aid = openParam.getAid();
    Boolean useAgent = openParam.getUseAgent();
    for (String filename : files) {
      InputStream in =
          ChangeHostNameStep.class.getResourceAsStream("/playbook/increasesources/" + filename);
      String content = TaskUtil.getFile(in);
      TaskUtil.uploadFile(filename, content, useAgent, aid);
    }
    List<MesosSlave> mesosSlave = openParam.getMesosSlave();
    String password = mesosSlave.get(0).getPasswd();
    StringBuffer shellContext = TaskUtil.createBashFile();

    for (MesosSlave instance : mesosSlave) {
      int id = instance.getId();
      String name = TaskUtil.genSlaveName(id);
      this.genCommand(password, shellContext, name);
    }

    Timestamp start = new Timestamp(System.currentTimeMillis());
    // upload shellContext and execute it

    String result = new String();
    int status = TaskUtil.FINISHED;
    try {
      result = TaskUtil.executeFile("changehostnames", shellContext.toString(), useAgent, aid);
    } catch (Exception e) {
      logger.error("change host name :", e);
      result = e.toString();
      status = TaskUtil.FAILED;
      throw new PaasException(ExceptionCodeConstants.DubboServiceCode.SYSTEM_ERROR_CODE,
          e.toString());
    } finally {
      // insert log and task record
      int taskId =
          TaskUtil.insertResJobDetail(start, openParam.getClusterId(), shellContext.toString(),
              TaskUtil.getTypeId("changeSlaveName"), status);
      TaskUtil.insertResTaskLog(openParam.getClusterId(), taskId, result);
    }


    // return RepeatStatus.FINISHED;
    return RepeatStatus.FINISHED;
  }

  public void genCommand(String password, StringBuffer shellContext, String hosts) {
    List<String> vars = new ArrayList<String>();
    StringBuffer hostname = new StringBuffer();
    hostname.append("hostname=");
    hostname.append(hosts);
    String hostsParam = "hosts=" + hosts;
    vars.add(hostname.toString());
    vars.add("ansible_ssh_pass=" + password);
    vars.add(hostsParam);
    AnsibleCommand ansibleCommand =
        new AnsibleCommand(TaskUtil.getSystemProperty("filepath") + "/hostnamectl.yml", "root",
            vars);
    shellContext.append(ansibleCommand.toString());
    shellContext.append("\n");
  }
}
