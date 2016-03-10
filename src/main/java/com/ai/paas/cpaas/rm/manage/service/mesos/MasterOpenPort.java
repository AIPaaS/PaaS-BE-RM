package com.ai.paas.cpaas.rm.manage.service.mesos;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ai.paas.cpaas.rm.util.OpenPortUtil;
import com.ai.paas.cpaas.rm.util.TaskUtil;


public class MasterOpenPort implements Tasklet {

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    String user = "rcmesos";
    String portParam = "ports=[5050]";
    return OpenPortUtil.openPort(chunkContext, portParam, user,
        TaskUtil.getTypeId("meMasterOpenPort"));
  }

}
