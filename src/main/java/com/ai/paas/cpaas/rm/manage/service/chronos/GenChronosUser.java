package com.ai.paas.cpaas.rm.manage.service.chronos;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ai.paas.cpaas.rm.util.GenUserUtil;
import com.ai.paas.cpaas.rm.util.TaskUtil;

public class GenChronosUser implements Tasklet {

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    GenUserUtil.genUser(chunkContext, "genChronosUser", "rcchronos", "master",
        TaskUtil.getTypeId("chronosUser"));
    return RepeatStatus.FINISHED;
  }

}
