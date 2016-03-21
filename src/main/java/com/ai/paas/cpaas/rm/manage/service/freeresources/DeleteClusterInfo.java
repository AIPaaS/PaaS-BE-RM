package com.ai.paas.cpaas.rm.manage.service.freeresources;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ai.paas.cpaas.rm.dao.interfaces.ResClusterInfoMapper;
import com.ai.paas.cpaas.rm.util.TaskUtil;
import com.ai.paas.cpaas.rm.vo.OpenResourceParamVo;
import com.ai.paas.ipaas.ServiceUtil;

public class DeleteClusterInfo implements Tasklet {

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    OpenResourceParamVo openParam = TaskUtil.createOpenParam(chunkContext);
    String clusterId = openParam.getClusterId();
    ResClusterInfoMapper mapper = ServiceUtil.getMapper(ResClusterInfoMapper.class);
    mapper.deleteByPrimaryKey(clusterId);
    return RepeatStatus.FINISHED;
  }

}
