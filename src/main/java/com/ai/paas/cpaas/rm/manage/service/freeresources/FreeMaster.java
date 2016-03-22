package com.ai.paas.cpaas.rm.manage.service.freeresources;

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

public class FreeMaster implements Tasklet {
  private static Logger logger = Logger.getLogger(FreeMaster.class);

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    InputStream in =
        OpenPortUtil.class.getResourceAsStream("/playbook/freeresources/freemaster.yml");
    String content = TaskUtil.getFile(in);
    OpenResourceParamVo openParam = TaskUtil.createOpenParam(chunkContext);
    Boolean useAgent = openParam.getUseAgent();
    String aid = openParam.getAid();
    TaskUtil.uploadFile("freemaster.yml", content, useAgent, aid);

    List<MesosInstance> mesosMaster = openParam.getMesosMaster();
    MesosInstance instance = mesosMaster.get(0);
    String password = instance.getPasswd();
    List<String> configvars = new ArrayList<String>();
    configvars.add("ansible_ssh_pass=" + password);
    configvars.add("ansible_become_pass=" + password);
    configvars.add("dockerpath=" + openParam.getImagePath());
    AnsibleCommand dockerinstall =
        new AnsibleCommand(TaskUtil.getSystemProperty("filepath") + "/freemaster.yml",
            instance.getRoot(), configvars);
    Timestamp start = new Timestamp(System.currentTimeMillis());

    String result = new String();
    int status = TaskUtil.FINISHED;
    try {
      result = TaskUtil.executeFile("freeMaster", dockerinstall.toString(), useAgent, aid);
    } catch (Exception e) {
      logger.error("free master resources:", e);
      result = e.toString();
      status = TaskUtil.FAILED;
      throw new PaasException(ExceptionCodeConstants.DubboServiceCode.SYSTEM_ERROR_CODE,
          e.toString());
    } finally {
      // insert log and task record
      int taskId =
          TaskUtil.insertResJobDetail(start, openParam.getClusterId(), dockerinstall.toString(),
              TaskUtil.getTypeId("freeMaster"), status);
      TaskUtil.insertResTaskLog(openParam.getClusterId(), taskId, result);

    }
    return RepeatStatus.FINISHED;
  }

}
