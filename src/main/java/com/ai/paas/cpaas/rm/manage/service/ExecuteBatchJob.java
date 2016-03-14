package com.ai.paas.cpaas.rm.manage.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ExecuteBatchJob {

  private static Logger logger = Logger.getLogger(ExecuteBatchJob.class);

  public void executeOpenService(String param) throws Exception {
    // TODO
    String[] springConfig = {"batch/openServiceBatch.xml"};
    // String[] springConfig = {"batch/testBatch.xml"};
    ApplicationContext context = new ClassPathXmlApplicationContext(springConfig);
    JobLauncher jobLauncher = (JobLauncher) context.getBean("jobLauncher");
    Job job = (Job) context.getBean("openService");
    Map<String, JobParameter> parameters = new HashMap<String, JobParameter>();
    JobParameter jobparameter = new JobParameter(param);
    parameters.put("openParameter", jobparameter);

    JobParameters jobParameters = new JobParameters(parameters);
    try {
      JobExecution execution = jobLauncher.run(job, jobParameters);
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
  /*
   * public static void main(String[] args) throws Exception {
   * 
   * String[] springConfig = {"batch/openServiceBatch.xml"}; ApplicationContext context = new
   * ClassPathXmlApplicationContext(springConfig);
   * 
   * JobLauncher jobLauncher = (JobLauncher) context.getBean("jobLauncher");
   * 
   * Job job = (Job) context.getBean("firstBatchJob");
   * 
   * Map<String, JobParameter> parameters = new HashMap<String, JobParameter>(); JobParameter
   * jobparameter = new JobParameter("phonenix"); parameters.put("river", jobparameter);
   * 
   * JobParameters jobParameters = new JobParameters(parameters); try { JobExecution execution =
   * jobLauncher.run(job, jobParameters);
   * 
   * logger.info("Exit Status : " + execution.getStatus()); System.out.println("Exit Status : " +
   * execution.getStatus());
   * 
   * } catch (Exception e) { e.printStackTrace(); throw e; } finally { if (context != null) {
   * context = null; } }
   * 
   * }
   */
}
