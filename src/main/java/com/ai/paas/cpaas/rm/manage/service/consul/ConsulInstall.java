package com.ai.paas.cpaas.rm.manage.service.consul;

import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
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

public class ConsulInstall implements Tasklet {
  private static Logger logger = Logger.getLogger(ConsulInstall.class);

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    OpenResourceParamVo openParam = TaskUtil.createOpenParam(chunkContext);
    Boolean useAgent = openParam.getUseAgent();
    String aid = openParam.getAid();
    String[] files = {"installconsul.yml", "consul.json", "configha.yml", "consul.service"};
    for (String file : files) {
      InputStream in = OpenPortUtil.class.getResourceAsStream("/playbook/consul/" + file);
      String content = TaskUtil.getFile(in);
      TaskUtil.uploadFile(file, content, useAgent, aid);
    }
    StringBuffer shellContext = TaskUtil.createBashFile();

    List<MesosInstance> list = openParam.getMesosMaster();
    MesosInstance master = list.get(0);
    String password = master.getPasswd();
    String consulPath = TaskUtil.getSystemProperty("consul");
    String configFile = TaskUtil.getSystemProperty("filepath") + "/consul.json";
    String consulService = TaskUtil.getSystemProperty("filepath") + "/consul.service";


    List<String> configvars = new ArrayList<String>();
    configvars.add("ansible_ssh_pass=" + password);
    configvars.add("ansible_become_pass=" + password);
    configvars.add("url=" + consulPath);
    configvars.add("configfile=" + configFile);
    configvars.add("consulService=" + consulService);
    // configvars.add(consulCluster.toString());

    AnsibleCommand installcommand =
        new AnsibleCommand(TaskUtil.getSystemProperty("filepath") + "/installconsul.yml", "root",
            configvars);
    shellContext.append(installcommand.toString()).append("\n");



    Timestamp start = new Timestamp(System.currentTimeMillis());

    String result = new String();
    int status = TaskUtil.FINISHED;
    try {
      result = TaskUtil.executeFile("installConsul", shellContext.toString(), useAgent, aid);
    } catch (Exception e) {
      logger.error("install consul:", e);
      result = e.toString();
      status = TaskUtil.FAILED;
      throw new PaasException(ExceptionCodeConstants.DubboServiceCode.SYSTEM_ERROR_CODE,
          e.toString());
    } finally {
      // insert log and task record
      int taskId =
          TaskUtil.insertResJobDetail(start, openParam.getClusterId(), shellContext.toString(),
              TaskUtil.getTypeId("consulInstall"), status);
      TaskUtil.insertResTaskLog(openParam.getClusterId(), taskId, result);
    }
    return RepeatStatus.FINISHED;
  }

  public String genConsulInfo(int i, String ip) {
    return "server master" + i + " " + ip + ":8600 check";
  }
}
