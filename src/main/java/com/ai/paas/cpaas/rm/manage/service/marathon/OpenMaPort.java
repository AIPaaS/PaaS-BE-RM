package com.ai.paas.cpaas.rm.manage.service.marathon;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ai.paas.cpaas.rm.util.OpenPortUtil;

public class OpenMaPort implements Tasklet {

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    String portParam = "ports=[8080]";
    String user = "rcmarathon";
    return OpenPortUtil.openPort(chunkContext, portParam, user, 18);
  }

}
