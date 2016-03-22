package com.ai.paas.cpaas.rm.manage.service.mesos;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ai.paas.cpaas.rm.util.TaskUtil;
import com.ai.paas.cpaas.rm.util.VerifyWebService;

public class MeServiceVerify implements Tasklet {
  private static Logger logger = Logger.getLogger(MeServiceVerify.class);

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    logger.info("verify mesos-master service ");
    VerifyWebService.checkwebService(chunkContext, "5050", "mesosServiceVerify.yml",
        "/playbook/mesos/mesosServiceVerify.yml", TaskUtil.getTypeId("meServiceVerify"));
    return RepeatStatus.FINISHED;
  }

}
