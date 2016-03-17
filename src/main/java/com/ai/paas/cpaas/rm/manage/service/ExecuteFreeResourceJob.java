package com.ai.paas.cpaas.rm.manage.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ai.paas.cpaas.rm.util.ExceptionCodeConstants;
import com.ai.paas.cpaas.rm.vo.OpenResultParamVo;

public class ExecuteFreeResourceJob {
  private static Logger logger = Logger.getLogger(ExecuteFreeResourceJob.class);

  public void executeOpenService(String param, OpenResultParamVo openResultParam) throws Exception {
    // TODO
    String[] springConfig = {"batch/openServiceBatch.xml"};
    // String[] springConfig = {"batch/freeResourcesBatch.xml"};
    ApplicationContext context = new ClassPathXmlApplicationContext(springConfig);
    JobLauncher jobLauncher = (JobLauncher) context.getBean("jobLauncher");
    Job job = (Job) context.getBean("freeResourcesService");
    Map<String, JobParameter> parameters = new HashMap<String, JobParameter>();
    JobParameter jobparameter = new JobParameter(param);
    parameters.put("openParameter", jobparameter);

    JobParameters jobParameters = new JobParameters(parameters);
    try {
      JobExecution execution = jobLauncher.run(job, jobParameters);
      if (execution.getStatus().equals(BatchStatus.FAILED)) {
        openResultParam.setResultCode(ExceptionCodeConstants.DubboServiceCode.SYSTEM_ERROR_CODE);
        openResultParam.setResultMsg(execution.getExitStatus().getExitDescription());
      } else {
        openResultParam.setResultCode(ExceptionCodeConstants.DubboServiceCode.SUCCESS_CODE);
        openResultParam.setResultMsg(ExceptionCodeConstants.DubboServiceCode.SUCCESS_MESSAGE);
      }
      logger.info("Exit Status : " + execution.getStatus());
    } catch (Exception e) {
      logger.error(e);
      throw e;
    } finally {
      if (context != null) {
        context = null;
      }
    }
  }
}
