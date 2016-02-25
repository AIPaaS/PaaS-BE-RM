package com.ai.paas.cpaas.rm.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.springframework.batch.core.scope.context.ChunkContext;

import com.ai.paas.cpaas.rm.vo.MesosInstance;
import com.ai.paas.cpaas.rm.vo.OpenResourceParamVo;
import com.ai.paas.ipaas.PaasException;

public class GenUserUtil {

  public static void genUser(ChunkContext chunkContext, String fileName, String user, String hosts) throws ClientProtocolException, IOException,
      PaasException {
    //上传hostnamectl.yml
    InputStream in = GenUserUtil.class.getResourceAsStream("/playbook/adduser.yml");
    TaskUtil.uploadFile("adduser.yml", in);
    OpenResourceParamVo openParam = TaskUtil.createOpenParam(chunkContext);
    StringBuffer shellContext = TaskUtil.createBashFile();
    List<MesosInstance> mesosMaster = openParam.getMesosMaster();
    // 创建用户
    MesosInstance mesosInstance = mesosMaster.get(0);
    List<String> userVars = new ArrayList<String>();
    String passwd = mesosInstance.getPasswd();
    userVars.add("ansible_ssh_pass=" + passwd);
    userVars.add("ansible_become_pass=" + passwd);
    userVars.add("username=" + user);
    userVars.add("hosts=" + hosts);
    userVars.add("line='" + user + "    ALL=(ALL)      ALL'");
    // 获取密码
    String password = TaskUtil.generatePasswd();
    // 将生成密码存入上下文中
    chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("password", password);
    shellContext.append("passwd=`python -c \"from passlib.hash import sha512_crypt; import getpass; print sha512_crypt.encrypt('" + password
        + "')\"`");
    shellContext.append(System.lineSeparator());
    userVars.add("password=$passwd");
    AnsibleCommand genUserCommand = new AnsibleCommand(TaskUtil.getSystemProperty("filepath") + "/adduser.yml", mesosInstance.getRoot(), userVars);
    shellContext.append(genUserCommand.toString());
    shellContext.append(System.lineSeparator());
    TaskUtil.executeFile(fileName, shellContext.toString());
  }
}
