package com.ai.paas.cpaas.rm.manage.service.docker;

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

public class DockerVerify implements Tasklet {
  private static Logger logger = Logger.getLogger(DockerVerify.class);

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    OpenResourceParamVo openParam = TaskUtil.createOpenParam(chunkContext);
    String aid = openParam.getAid();
    InputStream in = OpenPortUtil.class.getResourceAsStream("/playbook/docker/dockerVerify.yml");
    String content = TaskUtil.getFile(in);
    Boolean useAgent = openParam.getUseAgent();
    TaskUtil.uploadFile("dockerVerify.yml", content, useAgent, aid);


    List<MesosInstance> mesosMaster = openParam.getMesosMaster();
    List<String> vars = new ArrayList<String>();
    MesosInstance masternode = mesosMaster.get(0);
    String passwd = masternode.getPasswd();
    vars.add("ansible_ssh_pass=" + passwd);
    vars.add("ansible_become_pass=" + passwd);
    AnsibleCommand command =
        new AnsibleCommand(TaskUtil.getSystemProperty("filepath") + "/dockerVerify.yml", "root",
            vars);

    Timestamp start = new Timestamp(System.currentTimeMillis());

    String result = new String();
    int status = TaskUtil.FINISHED;
    try {
      result = TaskUtil.executeFile("dockerVerify.sh", command.toString(), useAgent, aid);
    } catch (Exception e) {
      logger.error(e.toString());
      result = e.toString();
      status = TaskUtil.FAILED;
      throw new PaasException(ExceptionCodeConstants.DubboServiceCode.SYSTEM_ERROR_CODE,
          e.toString());
    } finally {
      // insert log and task record
      int taskId =
          TaskUtil.insertResJobDetail(start, openParam.getClusterId(), command.toString(),
              TaskUtil.getTypeId("dockerVerify"), status);
      TaskUtil.insertResTaskLog(openParam.getClusterId(), taskId, result);

    }

    return RepeatStatus.FINISHED;
  }

}
