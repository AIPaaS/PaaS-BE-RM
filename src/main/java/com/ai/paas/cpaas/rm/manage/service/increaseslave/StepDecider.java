package com.ai.paas.cpaas.rm.manage.service.increaseslave;

import java.util.HashMap;
import java.util.List;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

import com.ai.paas.cpaas.rm.vo.MesosInstance;
import com.ai.paas.cpaas.rm.vo.MesosSlave;
import com.ai.paas.cpaas.rm.vo.OpenResourceParamVo;
import com.google.gson.Gson;

public class StepDecider implements JobExecutionDecider {

  @Override
  public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
    JobParameters parameters = jobExecution.getJobParameters();
    String param = parameters.getString("openParameter");
    Gson gson = new Gson();
    OpenResourceParamVo openParam = gson.fromJson(param, OpenResourceParamVo.class);
    List<MesosInstance> masterList = openParam.getMesosMaster();
    List<MesosSlave> slaveList = openParam.getMesosSlave();
    HashMap<String, String> ips = new HashMap<String, String>();
    this.extractIPs(masterList, ips);
    Boolean status = this.isExist(slaveList, ips);
    if (status) {
      FlowExecutionStatus result = new FlowExecutionStatus("ConvertSlave");
      return result;
    } else {
      return FlowExecutionStatus.COMPLETED;
    }
  }

  public void extractIPs(List<MesosInstance> list, HashMap<String, String> ips) {
    for (MesosInstance instance : list) {
      String ip = instance.getIp();
      ips.put(ip, ip);
    }
  }

  public Boolean isExist(List<MesosSlave> list, HashMap<String, String> ips) {
    for (MesosSlave instance : list) {
      String ip = instance.getIp();
      if (ips.containsKey(ip)) {
        return true;
      }
    }
    return false;
  }
}
