package com.ai.paas.cpaas.rm.manage.service.mesosdns;

import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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
import com.ai.paas.ipaas.PaasException;
import com.esotericsoftware.minlog.Log;

public class MesosDnsInstall implements Tasklet {

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    OpenResourceParamVo openParam = TaskUtil.createOpenParam(chunkContext);
    Boolean useAgent = openParam.getUseAgent();
    String aid = openParam.getAid();
    String[] filenames = {"config.json", "mesos-dns.service", "mesosdnsinstall.yml"};
    for (String filename : filenames) {
      InputStream in = OpenPortUtil.class.getResourceAsStream("/playbook/mesosdns/" + filename);
      String content = TaskUtil.getFile(in);
      TaskUtil.uploadFile(filename, content, useAgent, aid);
    }

    StringBuffer zk = new StringBuffer();
    StringBuffer masters = new StringBuffer();

    zk.append("zk='\"zk://");
    masters.append("master='[");
    List<MesosInstance> masterList = openParam.getMesosMaster();

    MesosInstance masterInstance = masterList.get(0);
    String password = masterInstance.getPasswd();
    zk.append(this.genZkInfo(masterInstance.getIp()));
    masters.append("\\\\").append("\\\"").append(this.genMesosInfo(masterInstance.getIp()))
        .append("\\\\").append("\\\"");
    for (int i = 1; i < masterList.size(); i++) {
      zk.append(",");
      zk.append(this.genZkInfo(masterList.get(i).getIp()));
      masters.append(",");
      masters.append("\\\\").append("\\\"").append(this.genMesosInfo(masterList.get(i).getIp()))
          .append("\\\\").append("\\\"");
    }
    zk.append("/mesos\"'");
    masters.append("]'");
    // 获取文件存储路径
    String path = TaskUtil.getSystemProperty("filepath");
    String config = path + "/config.json";
    String mesosservice = path + "/mesos-dns.service";
    // 获取mesosdns下载位置
    String mesosPath = TaskUtil.getSystemProperty("mesosdns");
    String nameserver = TaskUtil.getSystemProperty("nameserver");
    List<String> configvars = new ArrayList<String>();
    configvars.add("ansible_ssh_pass=" + password);
    configvars.add("ansible_become_pass=" + password);
    configvars.add(zk.toString());
    configvars.add(masters.toString());
    configvars.add("config=" + config);
    configvars.add("mesosservice=" + mesosservice);
    configvars.add("filepath=" + mesosPath);
    configvars.add("resolvers='[" + nameserver + "]'");
    configvars.add("domain='\\\\\\\"" + openParam.getDomain() + "\\\\\\\"'");
    AnsibleCommand command =
        new AnsibleCommand(TaskUtil.getSystemProperty("filepath") + "/mesosdnsinstall.yml", "root",
            configvars);
    Timestamp start = new Timestamp(System.currentTimeMillis());

    String result = new String();
    try {
      result = TaskUtil.executeFile("mesosdnsinstall", command.toString(), useAgent, aid);
    } catch (Exception e) {
      Log.error(e.toString());
      result = e.toString();
      throw new PaasException(ExceptionCodeConstants.DubboServiceCode.SYSTEM_ERROR_CODE,
          e.toString());
    } finally {
      // insert log and task record
      int taskId =
          TaskUtil.insertResJobDetail(start, openParam.getClusterId(), command.toString(), 9);
      TaskUtil.insertResTaskLog(openParam.getClusterId(), taskId, result);
    }
    return RepeatStatus.FINISHED;
  }

  public String genZkInfo(String url) {
    return url + ":2181";
  }

  public String genMesosInfo(String url) {
    return url + ":5050";
  }
}
