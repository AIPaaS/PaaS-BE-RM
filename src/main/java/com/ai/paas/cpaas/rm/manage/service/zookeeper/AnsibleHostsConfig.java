package com.ai.paas.cpaas.rm.manage.service.zookeeper;

import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ai.paas.cpaas.rm.util.TaskUtil;
import com.ai.paas.cpaas.rm.vo.MesosInstance;
import com.ai.paas.cpaas.rm.vo.MesosSlave;
import com.ai.paas.cpaas.rm.vo.OpenResourceParamVo;

public class AnsibleHostsConfig implements Tasklet {

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    OpenResourceParamVo openParam = TaskUtil.createOpenParam(chunkContext);
    // 构建执行文件
    StringBuffer shellContext = TaskUtil.createBashFile();
    shellContext.append("mv /etc/ansible/hosts /etc/ansible/hosts.bak");
    shellContext.append("\n");
    shellContext.append("cat >/etc/ansible/hosts <<-EOL");
    shellContext.append("\n");

    List<MesosInstance> mesosmaster = openParam.getMesosMaster();
    List<MesosSlave> mesosSlave = openParam.getMesosSlave();
    for (int i = 0; i < mesosmaster.size(); i++) {
      String masterName = TaskUtil.genMasterName(i + 1);
      String ip = mesosmaster.get(i).getIp();

      shellContext.append("[" + masterName + "]");
      shellContext.append("\n");
      shellContext.append(ip);
      shellContext.append("\n");
      chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext()
          .put(ip, masterName);

    }
    shellContext.append("[master]");
    shellContext.append("\n");
    for (int i = 0; i < mesosmaster.size(); i++) {
      shellContext.append(mesosmaster.get(i).getIp());
      shellContext.append("\n");
    }

    for (int i = 0; i < mesosSlave.size(); i++) {
      String slaveName = TaskUtil.genSlaveName(i + 1);
      String ip = mesosSlave.get(i).getIp();

      shellContext.append("[" + slaveName + "]");
      shellContext.append("\n");
      shellContext.append(ip);
      shellContext.append("\n");
      chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext()
          .put(ip, slaveName);
    }
    shellContext.append("[slave]");
    shellContext.append("\n");
    for (int i = 0; i < mesosSlave.size(); i++) {
      shellContext.append(mesosSlave.get(i).getIp());
      shellContext.append("\n");
    }
    shellContext.append("[nodes]");
    shellContext.append("\n");
    for (int i = 0; i < mesosmaster.size(); i++) {
      shellContext.append(mesosmaster.get(i).getIp());
      shellContext.append("\n");
    }
    for (int i = 0; i < mesosSlave.size(); i++) {
      shellContext.append(mesosSlave.get(i).getIp());
      shellContext.append("\n");
    }
    // TODO
    shellContext.append("EOL");
    shellContext.append("\n");
    TaskUtil.executeFile("configAnsibleHosts", shellContext.toString(), openParam.getUseAgent());

    return RepeatStatus.FINISHED;
  }
}
