package com.ai.paas.cpaas.rm.manage.service.cluster;

import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ai.paas.cpaas.rm.util.AnsibleCommand;
import com.ai.paas.cpaas.rm.util.ExceptionCodeConstants;
import com.ai.paas.cpaas.rm.util.OpenPortUtil;
import com.ai.paas.cpaas.rm.util.TaskUtil;
import com.ai.paas.cpaas.rm.vo.MesosInstance;
import com.ai.paas.cpaas.rm.vo.OpenResourceParamVo;
import com.ai.paas.cpaas.rm.vo.WebHaproxy;
import com.ai.paas.ipaas.PaasException;

public class ConfigHa implements Tasklet {
  private static Logger logger = Logger.getLogger(ConfigHa.class);

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    OpenResourceParamVo openParam = TaskUtil.createOpenParam(chunkContext);
    Boolean useAgent = openParam.getUseAgent();
    String aid = openParam.getAid();
    String[] files = {"configha.yml"};
    for (String file : files) {
      InputStream in = OpenPortUtil.class.getResourceAsStream("/playbook/base/" + file);
      String content = TaskUtil.getFile(in);
      TaskUtil.uploadFile(file, content, useAgent, aid);
    }
    StringBuffer shellContext = TaskUtil.createBashFile();
    List<MesosInstance> list = openParam.getMesosMaster();
    StringBuffer consulCluster = new StringBuffer();
    consulCluster.append("lines=[");
    /*
     * consulCluster.append("lines=[").append("'") .append(this.genConsulInfo(master.getId(),
     * master.getIp())).append("'"); for (int i = 1; i < list.size(); i++) {
     * consulCluster.append(","); MesosInstance instance = list.get(i);
     * consulCluster.append("'").append(this.genConsulInfo(instance.getId(), instance.getIp()))
     * .append("'"); }
     */

    ArrayList<String> consulinfo = new ArrayList<String>();
    ArrayList<String> marathoninfo = new ArrayList<String>();
    ArrayList<String> chronosinfo = new ArrayList<String>();
    marathoninfo.add("listen marathon-cluster");
    marathoninfo.add("    bind 0.0.0.0:8080");
    marathoninfo.add("    mode tcp");
    marathoninfo.add("    balance roundrobin");

    chronosinfo.add("listen chronos-cluster");
    chronosinfo.add("    bind 0.0.0.0:4400");
    chronosinfo.add("    mode tcp");

    for (MesosInstance instance : list) {
      String ip = instance.getIp();
      int id = instance.getId();
      consulinfo.add(this.genConsulInfo(id, ip));
      marathoninfo.add(this.genMarathonInfo(id, ip));
      chronosinfo.add(this.genChronosInfo(id, ip));
    }
    StringBuffer lines = new StringBuffer();
    this.genLines(lines, consulinfo);
    this.genLines(lines, marathoninfo);
    this.genLines(lines, chronosinfo);
    StringBuffer result = new StringBuffer();
    int end = lines.length() - 1;
    result.append(lines.subSequence(0, end));
    consulCluster.append(result);
    consulCluster.append("]");

    WebHaproxy webHaproxy = openParam.getWebHaproxy();
    String agentPass = webHaproxy.getHosts().get(0).getPasswd();
    List<String> vars = new ArrayList<String>();
    vars.add("ansible_ssh_pass=" + agentPass);
    vars.add("ansible_become_pass=" + agentPass);
    vars.add(consulCluster.toString());
    AnsibleCommand configCommand =
        new AnsibleCommand(TaskUtil.getSystemProperty("filepath") + "/configha.yml", "root", vars);
    shellContext.append(configCommand.toString()).append("\n");
    Timestamp start = new Timestamp(System.currentTimeMillis());

    String resultMessage = new String();
    int status = TaskUtil.FINISHED;
    try {
      resultMessage = TaskUtil.executeFile("configha", shellContext.toString(), useAgent, aid);
    } catch (Exception e) {
      logger.error("config haproxy:", e);
      resultMessage = e.toString();
      status = TaskUtil.FAILED;
      throw new PaasException(ExceptionCodeConstants.DubboServiceCode.SYSTEM_ERROR_CODE,
          e.toString());
    } finally {
      // insert log and task record
      int taskId =
          TaskUtil.insertResJobDetail(start, openParam.getClusterId(), shellContext.toString(),
              TaskUtil.getTypeId("configha"), status);
      TaskUtil.insertResTaskLog(openParam.getClusterId(), taskId, resultMessage);
    }

    return RepeatStatus.FINISHED;
  }

  public String genConsulInfo(int i, String ip) {
    return "server master" + i + " " + ip + ":8600 check";
  }

  public String genMarathonInfo(int i, String ip) {
    return "server master" + i + " " + ip + ":8080 check";
  }

  public String genChronosInfo(int i, String ip) {
    return "server master" + i + " " + ip + ":4400 check";
  }

  public void genLines(StringBuffer lines, List<String> info) {

    for (String line : info) {
      lines.append("'").append(line).append("'");
      lines.append(",");
    }
  }
}
