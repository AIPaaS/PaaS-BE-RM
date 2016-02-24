package com.ai.paas.cpaas.rm.manage.service.etcd;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ai.paas.cpaas.rm.util.AnsibleCommand;
import com.ai.paas.cpaas.rm.util.TaskUtil;
import com.ai.paas.cpaas.rm.vo.MesosInstance;
import com.ai.paas.cpaas.rm.vo.OpenResourceParamVo;

public class EtcdInstall implements Tasklet {

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
    // 构建执行文件
    StringBuffer shellContext = TaskUtil.createBashFile();
    OpenResourceParamVo openParam = TaskUtil.createOpenParam(chunkContext);
    List<MesosInstance> mesosMaster = openParam.getMesosMaster();
    MesosInstance masternode = mesosMaster.get(0);
    String passwd = masternode.getPasswd();

    // 拼接initial_cluster参数
    StringBuffer initial_cluster = new StringBuffer("ETCD_INITIAL_CLUSTER=");
    initial_cluster.append("etcd-01=http://" + masternode.getIp());
    for (int i = 1; i < mesosMaster.size(); i++) {
      initial_cluster.append(",");
      initial_cluster.append(this.genEtcdName(i));
      initial_cluster.append("=http://");
      initial_cluster.append(mesosMaster.get(i));
    }

    for (int i = 0; i < mesosMaster.size(); i++) {
      String url = "http://" + mesosMaster.get(i).getIp();

      List<String> vars = new ArrayList<String>();
      vars.add("ansible_ssh_pass=" + passwd);
      vars.add("ansible_become_pass=" + passwd);
      vars.add("initial_cluster='" + initial_cluster + "'");
      vars.add("etcd_name='ETCD_NAME=" + this.genEtcdName(i) + "'");
      vars.add("listen_peer_urls='ETCD_LISTEN_PEER_URLS=" + url + ":2380'");
      vars.add("listen_client_urls='ETCD_LISTEN_CLIENT_URLS=" + url + ":2379'");
      vars.add("initial_advertise_peer_urls='ETCD_INITIAL_ADVERTISE_PEER_URLS=" + url + ":2380'");
      vars.add("advertise_client_urls='ETCD_ADVERTISE_CLIENT_URLS=" + url + ":2379'");

      AnsibleCommand command = new AnsibleCommand(TaskUtil.filepath + "etcdinstall.yml", "root", vars);
      shellContext.append(command.toString());
      shellContext.append(System.lineSeparator());
    }
    TaskUtil.executeFile("etcdInstall", shellContext.toString());
    return RepeatStatus.FINISHED;
  }

  public String genEtcdName(int i) {
    StringBuffer name = new StringBuffer();
    name.append("etcd-0");
    name.append(i + 1);
    return name.toString();
  }
}
