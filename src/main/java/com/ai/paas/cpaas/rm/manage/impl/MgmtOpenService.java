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
import com.ai.paas.cpaas.rm.vo.OpenResourceParamVo;
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
    ResReqInfoMapper mapper = ServiceUtil.getMapper(ResReqInfoMapper.class);
    ResReqInfo resReqInfo = new ResReqInfo();
    try {
      if (StringUtil.isEmpty(param)) {
        throw new PaasException(ExceptionCodeConstants.DubboServiceCode.PARAM_IS_NULL,
            "the parameter for appllying database is null");
      }
      OpenResourceParamVo openParam = gson.fromJson(param, OpenResourceParamVo.class);
      resReqInfo.setClusterId(openParam.getClusterId());
      resReqInfo.setReqType(1);
      resReqInfo.setReqCnt(param);
      resReqInfo.setReqTime(new Timestamp(System.currentTimeMillis()));

      ExecuteBatchJob executeBatchJob = new ExecuteBatchJob();
      executeBatchJob.executeOpenService(param);
      openResultParam.setResultCode(ExceptionCodeConstants.DubboServiceCode.SUCCESS_CODE);
      openResultParam.setResultMsg(ExceptionCodeConstants.DubboServiceCode.SUCCESS_MESSAGE);
      resReqInfo.setReqState(new Integer(0));
    } catch (Exception e) {
      logger.error(e);
      openResultParam.setResultCode(ExceptionCodeConstants.DubboServiceCode.SYSTEM_ERROR_CODE);
      openResultParam.setResultMsg(e.toString());
      resReqInfo.setReqState(new Integer(1));
      mapper.insert(resReqInfo);
    } finally {
      resReqInfo.setReqResp(gson.toJson(openResultParam));
      resReqInfo.setRespTime(new Timestamp(System.currentTimeMillis()));
      mapper.insert(resReqInfo);
    }
    return gson.toJson(openResultParam);
  }

}
