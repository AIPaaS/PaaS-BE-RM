package com.ai.paas.cpaas.rm.manage.service.consul;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ai.paas.cpaas.rm.util.OpenPortUtil;
import com.ai.paas.cpaas.rm.util.TaskUtil;

public class OpenConsulPort implements Tasklet {

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    String user = "rcdns";
    String portParam = "ports=[53,80,8301,8302,8300,8700,8500,8600,8400]";
    return OpenPortUtil.openPort(chunkContext, portParam, user,
        TaskUtil.getTypeId("openConsulPort"));
  }

}
