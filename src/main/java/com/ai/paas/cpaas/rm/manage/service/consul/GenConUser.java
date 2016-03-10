package com.ai.paas.cpaas.rm.manage.service.consul;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ai.paas.cpaas.rm.util.GenUserUtil;
import com.ai.paas.cpaas.rm.util.TaskUtil;

public class GenConUser implements Tasklet {

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    GenUserUtil.genUser(chunkContext, "genConsulUser", "rcdns", "master",
        TaskUtil.getTypeId("consulUser"));
    return RepeatStatus.FINISHED;
  }

}
