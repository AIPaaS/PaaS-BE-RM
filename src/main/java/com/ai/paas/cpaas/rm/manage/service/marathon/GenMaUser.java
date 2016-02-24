package com.ai.paas.cpaas.rm.manage.service.marathon;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ai.paas.cpaas.rm.util.GenUserUtil;

public class GenMaUser implements Tasklet {

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
    GenUserUtil.genUser(chunkContext, "genMarathonUser", "rcmarathon", "master");
    return RepeatStatus.FINISHED;
  }

}
