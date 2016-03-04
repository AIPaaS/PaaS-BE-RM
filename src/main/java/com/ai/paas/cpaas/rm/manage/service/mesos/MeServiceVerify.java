package com.ai.paas.cpaas.rm.manage.service.mesos;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ai.paas.cpaas.rm.util.VerifyWebService;

public class MeServiceVerify implements Tasklet {

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    VerifyWebService.checkwebService(chunkContext, "5050", "mesosServiceVerify.yml",
        "/playbook/mesos/mesosServiceVerify.yml", 16);
    return RepeatStatus.FINISHED;
  }

}
