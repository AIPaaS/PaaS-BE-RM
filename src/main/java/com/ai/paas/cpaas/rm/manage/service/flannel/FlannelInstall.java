package com.ai.paas.cpaas.rm.manage.service.flannel;

import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ai.paas.cpaas.rm.util.AnsibleCommand;
import com.ai.paas.cpaas.rm.util.TaskUtil;
import com.ai.paas.cpaas.rm.vo.MesosInstance;
import com.ai.paas.cpaas.rm.vo.MesosSlave;
import com.ai.paas.cpaas.rm.vo.OpenResourceParamVo;

public class FlannelInstall implements Tasklet {

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    InputStream in =
        FlannelInstall.class.getResourceAsStream("/playbook/flannel/flannelinstall.yml");
    String content = TaskUtil.getFile(in);
    OpenResourceParamVo openParam = TaskUtil.createOpenParam(chunkContext);
    String aid = openParam.getAid();
    Boolean useAgent = openParam.getUseAgent();
    TaskUtil.uploadFile("flannelinstall.yml", content, useAgent, aid);
    List<MesosInstance> mesosMaster = openParam.getMesosMaster();
    List<MesosSlave> slaveList = openParam.getMesosSlave();
    StringBuffer shellContext = TaskUtil.createBashFile();

    MesosInstance masternode = mesosMaster.get(0);
    StringBuffer flannelEtcd = new StringBuffer("FLANNEL_ETCD=");
    flannelEtcd.append("http://" + masternode.getIp()).append(":2379");
    for (int i = 1; i < mesosMaster.size(); i++) {
      flannelEtcd.append(",http://");
      flannelEtcd.append(mesosMaster.get(i).getIp()).append(":2379");
    }
    String password = masternode.getPasswd();
    // 需要在参数中指定docker的启动位置，并且添加etcd master的ip地址
    StringBuffer execstart = new StringBuffer("execstart='ExecStart=/usr/bin/docker daemon -g  ");
    execstart.append(openParam.getImagePath()).append("  -H fd:// --cluster-store=etcd://");
    execstart.append(masternode.getIp());
    execstart.append("'");

    for (int i = 0; i < slaveList.size(); i++) {
      this.genCommand(slaveList.get(i), openParam, shellContext, flannelEtcd.toString(),
          execstart.toString(), password, i, "slave");
    }

    for (int i = 0; i < mesosMaster.size(); i++) {
      this.genCommand(mesosMaster.get(i), openParam, shellContext, flannelEtcd.toString(),
          execstart.toString(), password, i, "master");
    }
    Timestamp start = new Timestamp(System.currentTimeMillis());

    String result = TaskUtil.executeFile("flannelinstall", shellContext.toString(), useAgent, aid);

    // 插入日志和任务记录
    int taskId =
        TaskUtil.insertResJobDetail(start, openParam.getClusterId(), shellContext.toString(), 28);
    TaskUtil.insertResTaskLog(openParam.getClusterId(), taskId, result);
    return RepeatStatus.FINISHED;
  }

  public void genCommand(MesosInstance instance, OpenResourceParamVo openParam,
      StringBuffer shellContext, String flannelEtcd, String execstart, String password, int i,
      String type) {
    List<String> vars = new ArrayList<String>();
    vars.add("ansible_ssh_pass=" + password);
    vars.add("ansible_become_pass=" + password);
    if (type.equals("slave")) {
      vars.add("hosts=" + TaskUtil.genSlaveName(i + 1));
    } else {
      vars.add("hosts=" + TaskUtil.genMasterName(i + 1));
    }
    vars.add("flanneletcd='" + flannelEtcd.toString() + "'");
    vars.add("flanneletcdkey='FLANNEL_ETCD_KEY=" + "/" + openParam.getClusterId() + "/"
        + instance.getZone() + "'");
    vars.add(execstart);
    AnsibleCommand command =
        new AnsibleCommand(TaskUtil.getSystemProperty("filepath") + "/flannelinstall.yml", "root",
            vars);
    shellContext.append(command.toString()).append("\n");
  }
}
