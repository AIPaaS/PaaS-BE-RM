package com.ai.paas.cpaas.rm.manage.service.zookeeper;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ai.paas.cpaas.rm.util.OpenPortUtil;

public class OpenZkPort implements Tasklet {

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
    String user = "rczkp01";
    String portParam = "ports=[2888,3888]";
    return OpenPortUtil.openPort(chunkContext, portParam, user);
  }

}
