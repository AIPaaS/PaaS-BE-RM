package com.ai.paas.cpaas.rm.manage.impl;

import java.sql.Timestamp;

import org.apache.log4j.Logger;
import org.hsqldb.lib.StringUtil;
import org.springframework.stereotype.Service;

import com.ai.paas.cpaas.rm.dao.interfaces.ResReqInfoMapper;
import com.ai.paas.cpaas.rm.dao.mapper.bo.ResReqInfo;
import com.ai.paas.cpaas.rm.interfaces.IMgmtOpenService;
import com.ai.paas.cpaas.rm.manage.service.ExecuteBatchJob;
import com.ai.paas.cpaas.rm.util.ExceptionCodeConstants;
import com.ai.paas.cpaas.rm.util.TaskUtil;
import com.ai.paas.cpaas.rm.vo.OpenResultParamVo;
import com.ai.paas.ipaas.PaasException;
import com.ai.paas.ipaas.ServiceUtil;
import com.google.gson.Gson;

@Service
public class MgmtOpenService implements IMgmtOpenService {

  private static Logger logger = Logger.getLogger(MgmtOpenService.class);

  @Override
  public String cancel(String arg0) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String create(String arg0) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getFuncList() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String modify(String arg0) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String restart(String arg0) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String start(String arg0) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String stop(String arg0) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String openService(String param) {
    Gson gson = new Gson();
    OpenResultParamVo openResultParam = new OpenResultParamVo();
    try {
      if (StringUtil.isEmpty(param)) {
        throw new PaasException(ExceptionCodeConstants.DubboServiceCode.PARAM_IS_NULL, "the parameter for appllying database is null");
      }
      ResReqInfoMapper mapper = ServiceUtil.getMapper(ResReqInfoMapper.class);
      ResReqInfo resReqInfo = new ResReqInfo();
      resReqInfo.setJobId(TaskUtil.nextValue());
      resReqInfo.setReqType(1);
      resReqInfo.setReqCnt(param);
      resReqInfo.setReqTime(new Timestamp(System.currentTimeMillis()));
      ExecuteBatchJob executeBatchJob = new ExecuteBatchJob();
      executeBatchJob.executeOpenService(param);
      mapper.insert(resReqInfo);
    } catch (Exception e) {
      logger.error(e);
      openResultParam.setResultCode(ExceptionCodeConstants.DubboServiceCode.SYSTEM_ERROR_CODE);
      openResultParam.setResultMsg(e.toString());
    }
    openResultParam.setResultCode(ExceptionCodeConstants.DubboServiceCode.SUCCESS_CODE);
    openResultParam.setResultMsg(ExceptionCodeConstants.DubboServiceCode.SUCCESS_MESSAGE);
    return gson.toJson(openResultParam);
  }

}
