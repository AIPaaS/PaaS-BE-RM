package com.ai.paas.cpaas.rm.manage.service.zookeeper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.entity.StringEntity;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ai.paas.cpaas.rm.util.AnsibleCommand;
import com.ai.paas.cpaas.rm.util.OpenPortUtil;
import com.ai.paas.cpaas.rm.util.TaskUtil;
import com.ai.paas.cpaas.rm.vo.MesosInstance;
import com.ai.paas.cpaas.rm.vo.OpenResourceParamVo;

public class ZookeeperVerify implements Tasklet {

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
    InputStream in = OpenPortUtil.class.getResourceAsStream("/playbook/zookeeper/zookeeperverify.yml");
    TaskUtil.uploadFile("zookeeperverify.yml", in);
    
    OpenResourceParamVo openParam = TaskUtil.createOpenParam(chunkContext);
    List<MesosInstance> mesosMaster = openParam.getMesosMaster();
    List<String> vars = new ArrayList<String>();
    MesosInstance masternode = mesosMaster.get(0);
    String passwd = masternode.getPasswd();
    vars.add("ansible_ssh_pass=" + passwd);
    vars.add("ansible_become_pass=" + passwd);
    StringBuffer inventory_hosts = new StringBuffer();
    inventory_hosts.append("[");
    inventory_hosts.append("'" + masternode.getIp() + "'");
    for (int i = 1; i < mesosMaster.size(); i++) {
      inventory_hosts.append(",");
      inventory_hosts.append(mesosMaster.get(i).getIp());
    }
    inventory_hosts.append("]");
    vars.add("inventory_hosts=" + inventory_hosts.toString());
    AnsibleCommand command = new AnsibleCommand(TaskUtil.getSystemProperty("filepath") + "/zookeeperverify.yml", "root", vars);
    StringEntity entity = TaskUtil.genCommandParam(command.toString());
    TaskUtil.executeCommand(entity,"command");
    return RepeatStatus.FINISHED;
  }
}
