package com.ai.paas.cpaas.rm.manage.service.haproxy;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ai.paas.cpaas.rm.util.GenUserUtil;
import com.ai.paas.cpaas.rm.util.TaskUtil;
import com.ai.paas.cpaas.rm.vo.HaproxyInfo;
import com.ai.paas.cpaas.rm.vo.MesosInstance;
import com.ai.paas.cpaas.rm.vo.OpenResourceParamVo;
import com.ai.paas.ipaas.util.CiperUtil;
import com.google.gson.Gson;

public class GenHaUser implements Tasklet {

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    String password = TaskUtil.generatePasswd();
    GenUserUtil.genUser(chunkContext, "genHaproxyUser", "rchaproxy", "agent",
        TaskUtil.getTypeId("genHaUser"), password);
    OpenResourceParamVo openParam = TaskUtil.createOpenParam(chunkContext);
    List<MesosInstance> agents = openParam.getWebHaproxy().getHosts();
    String ciperPassword = CiperUtil.encrypt(TaskUtil.SECURITY_KEY, password);
    Gson gson = new Gson();
    List<HaproxyInfo> haLists = new ArrayList<HaproxyInfo>();
    for (int i = 0; i < agents.size(); i++) {
      HaproxyInfo instance = new HaproxyInfo();
      instance.setIp(agents.get(i).getIp());
      instance.setPort(22);
      instance.setUser("rchaproxy");
      instance.setPwd(ciperPassword);
      haLists.add(instance);
    }
    String haInfo = gson.toJson(haLists);
    TaskUtil.insertResInstanceProps(openParam.getClusterId(), "haproxy_info", haInfo);
    return RepeatStatus.FINISHED;
  }

}
