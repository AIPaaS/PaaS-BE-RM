package com.ai.paas.cpaas.rm.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.springframework.batch.core.scope.context.ChunkContext;

import com.ai.paas.cpaas.rm.vo.MesosInstance;
import com.ai.paas.cpaas.rm.vo.OpenResourceParamVo;
import com.ai.paas.ipaas.PaasException;
import com.ai.paas.ipaas.util.CiperUtil;
import com.esotericsoftware.minlog.Log;

public class GenUserUtil {

  public static void genUser(ChunkContext chunkContext, String fileName, String user, String hosts,
      int typeId) throws ClientProtocolException, IOException, PaasException {

    OpenResourceParamVo openParam = TaskUtil.createOpenParam(chunkContext);
    InputStream in = GenUserUtil.class.getResourceAsStream("/playbook/adduser.yml");
    String content = TaskUtil.getFile(in);
    Boolean useAgent = openParam.getUseAgent();
    String aid = openParam.getAid();
    TaskUtil.uploadFile("adduser.yml", content, useAgent, aid);

    StringBuffer shellContext = TaskUtil.createBashFile();
    List<MesosInstance> mesosMaster = openParam.getMesosMaster();
    // create user
    MesosInstance mesosInstance = mesosMaster.get(0);
    List<String> userVars = new ArrayList<String>();
    String passwd = mesosInstance.getPasswd();
    userVars.add("ansible_ssh_pass=" + passwd);
    userVars.add("ansible_become_pass=" + passwd);
    userVars.add("username=" + user);
    userVars.add("hosts=" + hosts);
    userVars.add("line='" + user + "    ALL=(ALL)      ALL'");
    // gen password
    String password = TaskUtil.generatePasswd();
    // store the password in context
    chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext()
        .put("password", password);
    shellContext
        .append("passwd=`python -c \"from passlib.hash import sha512_crypt; import getpass; print sha512_crypt.encrypt('"
            + password + "')\"`");
    shellContext.append("\n");
    userVars.add("password=$passwd");
    AnsibleCommand genUserCommand =
        new AnsibleCommand(TaskUtil.getSystemProperty("filepath") + "/adduser.yml",
            mesosInstance.getRoot(), userVars);
    shellContext.append(genUserCommand.toString());
    shellContext.append("\n");
    Timestamp start = new Timestamp(System.currentTimeMillis());

    String result = new String();
    try {
      result = TaskUtil.executeFile(fileName, shellContext.toString(), useAgent, aid);
    } catch (Exception e) {
      Log.error(e.toString());
      result = e.toString();
      throw new PaasException(ExceptionCodeConstants.DubboServiceCode.SYSTEM_ERROR_CODE,
          e.toString());
    } finally {
      // insert user and password in the res_instance_props
      TaskUtil.insertResInstanceProps(openParam.getClusterId(), user,
          CiperUtil.encrypt(TaskUtil.SECURITY_KEY, password));

      // insert log and task record
      int taskId =
          TaskUtil.insertResJobDetail(start, openParam.getClusterId(), shellContext.toString(),
              typeId);
      TaskUtil.insertResTaskLog(openParam.getClusterId(), taskId, result);
    }


  }
}
