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

public class ConfigHostsStep implements Tasklet {

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    OpenResourceParamVo openParam = TaskUtil.createOpenParam(chunkContext);
    List<MesosInstance> mesosMaster = openParam.getMesosMaster();
    List<MesosSlave> mesosSlave = openParam.getMesosSlave();
    StringBuffer shellContext = TaskUtil.createBashFile();
    shellContext.append("mv /etc/hosts /etc/hosts.bak");
    shellContext.append(System.lineSeparator());
    shellContext.append("cat >/etc/hosts <<EOL");
    shellContext.append(System.lineSeparator());
    shellContext.append("127.0.0.1 localhost ");
    shellContext.append(System.lineSeparator());
    for (int i = 0; i < mesosMaster.size(); i++) {
      MesosInstance instance = mesosMaster.get(i);
      String ip = instance.getIp();
      String name =
          (String) chunkContext.getStepContext().getStepExecution().getJobExecution()
              .getExecutionContext().get(ip);
      this.genHostsLine(instance, name, shellContext);
    }
    for (int i = 0; i < mesosSlave.size(); i++) {
      MesosSlave instance = mesosSlave.get(i);
      String ip = instance.getIp();
      String name =
          (String) chunkContext.getStepContext().getStepExecution().getJobExecution()
              .getExecutionContext().get(ip);
      this.genHostsLine(instance, name, shellContext);
    }
    shellContext.append("EOL");
    shellContext.append(System.lineSeparator());
    TaskUtil.executeFile("confighosts", shellContext.toString(), openParam.getUseAgent());
    return RepeatStatus.FINISHED;
  }

  public void genHostsLine(MesosInstance instance, String name, StringBuffer shellContext) {
    String ip = instance.getIp();
    shellContext.append(ip + "  " + name);
    shellContext.append(System.lineSeparator());
  }
}
