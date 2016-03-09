package com.ai.paas.cpaas.rm.manage.service.haproxy;

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

public class HaproxyInstall implements Tasklet {

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    OpenResourceParamVo openParam = TaskUtil.createOpenParam(chunkContext);
    Boolean useAgent = openParam.getUseAgent();
    String aid = openParam.getAid();
    String[] filenames = {"haproxyinstall.yml", "keepalived.conf"};
    for (String filename : filenames) {
      InputStream in = OpenPortUtil.class.getResourceAsStream("/playbook/keepalived/" + filename);
      String content = TaskUtil.getFile(in);
      TaskUtil.uploadFile(filename, content, useAgent, aid);
    }

    List<MesosInstance> agents = openParam.getWebHaproxy().getHosts();
    String password = agents.get(0).getPasswd();
    String virtualIp = openParam.getWebHaproxy().getVirtualIp();

    StringBuffer shellContext = TaskUtil.createBashFile();
    // 获取文件存储路径
    String path = TaskUtil.getSystemProperty("filepath");
    for (int i = 0; i < agents.size(); i++) {
      List<String> configvars = new ArrayList<String>();
      configvars.add("ansible_ssh_pass=" + password);
      configvars.add("ansible_become_pass=" + password);
      // TODO
      // 测试需要，测试完需要删除
      if (i == 0) {
        configvars.add("hosts=master1");
      } else {
        configvars.add("hosts=master2");
      }
      // TODO
      // configvars.add("hosts=" + TaskUtil.genAgentName(i + 1));
      configvars.add("ip=" + virtualIp);
      configvars.add("configfile=" + path + "/keepalived.conf");
      if (i == 0) {
        configvars.add("role=MASTER");
        configvars.add("priority=101");
      } else {
        configvars.add("role=BACKUP");
        configvars.add("priority=100");
      }
      AnsibleCommand command =
          new AnsibleCommand(TaskUtil.getSystemProperty("filepath") + "/haproxyinstall.yml",
              "root", configvars);
      shellContext.append(command.toString());
      shellContext.append("\n");
    }

    Timestamp start = new Timestamp(System.currentTimeMillis());
    String result = new String();
    try {
      result = TaskUtil.executeFile("haproxyinstall", shellContext.toString(), useAgent, aid);
    } catch (Exception e) {
      Log.error(e.toString());
      result = e.toString();
      throw new PaasException(ExceptionCodeConstants.DubboServiceCode.SYSTEM_ERROR_CODE,
          e.toString());
    } finally {
      // insert log and task record
      int taskId =
          TaskUtil.insertResJobDetail(start, openParam.getClusterId(), shellContext.toString(),
              TaskUtil.getTypeId("haproxyInstall"));
      TaskUtil.insertResTaskLog(openParam.getClusterId(), taskId, result);
    }
    return RepeatStatus.FINISHED;
  }

}
