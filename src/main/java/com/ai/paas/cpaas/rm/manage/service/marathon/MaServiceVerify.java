package com.ai.paas.cpaas.rm.manage.service.marathon;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ai.paas.cpaas.rm.util.TaskUtil;
import com.ai.paas.cpaas.rm.util.VerifyWebService;

public class MaServiceVerify implements Tasklet {
  private static Logger logger = Logger.getLogger(MaServiceVerify.class);

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    logger.info("verify marathon service ");
    VerifyWebService.checkwebService(chunkContext, "8080", "marathonServiceVerify.yml",
        "/playbook/marathon/marathonServiceVerify.yml", TaskUtil.getTypeId("maServiceVerify"));
    return RepeatStatus.FINISHED;
  }

}
