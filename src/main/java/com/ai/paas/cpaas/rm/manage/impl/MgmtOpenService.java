package com.ai.paas.cpaas.rm.manage.impl;

import java.sql.Timestamp;
import java.util.List;

import org.apache.log4j.Logger;

import com.ai.paas.cpaas.rm.dao.interfaces.ResReqInfoMapper;
import com.ai.paas.cpaas.rm.dao.interfaces.ResTaskLogMapper;
import com.ai.paas.cpaas.rm.dao.mapper.bo.ResReqInfo;
import com.ai.paas.cpaas.rm.dao.mapper.bo.ResTaskLog;
import com.ai.paas.cpaas.rm.dao.mapper.bo.ResTaskLogCriteria;
import com.ai.paas.cpaas.rm.interfaces.IMgmtOpenService;
import com.ai.paas.cpaas.rm.manage.service.ExecuteBatchJob;
import com.ai.paas.cpaas.rm.util.ExceptionCodeConstants;
import com.ai.paas.cpaas.rm.vo.OpenResourceParamVo;
import com.ai.paas.cpaas.rm.vo.OpenResultParamVo;
import com.ai.paas.ipaas.PaasException;
import com.ai.paas.ipaas.ServiceUtil;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
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
    int status=0;
    try {
      if (StringUtils.isEmpty(param)) {
        throw new PaasException(ExceptionCodeConstants.DubboServiceCode.PARAM_IS_NULL,
            "the parameter for appllying database is null");
      }
      OpenResourceParamVo openParam = gson.fromJson(param, OpenResourceParamVo.class);
      if (!openParam.getUseAgent()) {
        throw new PaasException(ExceptionCodeConstants.TransServiceCode.ERROR_CODE,
            "it should be true for useAgent");
      }
      resReqInfo.setClusterId(openParam.getClusterId());
      resReqInfo.setReqType(1);
      resReqInfo.setReqCnt(param);
      resReqInfo.setReqTime(new Timestamp(System.currentTimeMillis()));

      ExecuteBatchJob executeBatchJob = new ExecuteBatchJob();
      executeBatchJob.executeOpenService(param);
      openResultParam.setResultCode(ExceptionCodeConstants.DubboServiceCode.SUCCESS_CODE);
      openResultParam.setResultMsg(ExceptionCodeConstants.DubboServiceCode.SUCCESS_MESSAGE);
   
      
    } catch (Exception e) {
      logger.error(e.getMessage(),e);
      System.out.println(e.getMessage());
      openResultParam.setResultCode(ExceptionCodeConstants.DubboServiceCode.SYSTEM_ERROR_CODE);
      openResultParam.setResultMsg(e.toString());
      status=1;
    } 
    resReqInfo.setReqState(status);
    resReqInfo.setReqResp(gson.toJson(openResultParam));
    resReqInfo.setRespTime(new Timestamp(System.currentTimeMillis()));
    mapper.insert(resReqInfo);
    return gson.toJson(openResultParam);
  }

  @Override
  public String queryLog(String param) {
	Gson gson = new Gson();
    String result=new String();
	try {
		if (StringUtils.isEmpty(param)) {
		        throw new PaasException(ExceptionCodeConstants.DubboServiceCode.PARAM_IS_NULL,
		            "the parameter for appllying database is null");
		      }
		ResTaskLogCriteria rescriteria=new ResTaskLogCriteria();
		ResTaskLogCriteria.Criteria criteria=rescriteria.createCriteria();
		criteria.andClusterIdEqualTo(new Integer(param));
		ResTaskLogMapper mapper=ServiceUtil.getMapper(ResTaskLogMapper.class);
		List<ResTaskLog> list=mapper.selectByExample(rescriteria);
		result=gson.toJson(list);
	} catch (PaasException e) {
		logger.error(e.toString());
	}
	return result;
  }

}
