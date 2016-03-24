package com.ai.paas.cpaas.rm.manage.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ai.paas.cpaas.rm.dao.interfaces.ResReqInfoMapper;
import com.ai.paas.cpaas.rm.dao.mapper.bo.ResJobDetail;
import com.ai.paas.cpaas.rm.dao.mapper.bo.ResReqInfo;
import com.ai.paas.cpaas.rm.dao.mapper.bo.ResTaskLog;
import com.ai.paas.cpaas.rm.interfaces.IMgmtOpenService;
import com.ai.paas.cpaas.rm.manage.service.openservice.ExecuteOpenBatchJob;
import com.ai.paas.cpaas.rm.util.ExceptionCodeConstants;
import com.ai.paas.cpaas.rm.util.TaskUtil;
import com.ai.paas.cpaas.rm.vo.LogResult;
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
    int status = TaskUtil.REQSUCCESS;
    try {
      if (StringUtils.isEmpty(param)) {
        logger.error("the parameter for appllying cluster is null");
        throw new PaasException(ExceptionCodeConstants.DubboServiceCode.PARAM_IS_NULL,
            "the parameter for appllying cluster is null");
      }
      OpenResourceParamVo openParam = gson.fromJson(param, OpenResourceParamVo.class);
      if (!openParam.getUseAgent()) {
        logger.error("it should be true for useAgent");
        throw new PaasException(ExceptionCodeConstants.TransServiceCode.ERROR_CODE,
            "it should be true for useAgent");
      }
      // ignore repeat install
      String clusterId = openParam.getClusterId();
      if (TaskUtil.clusterExist(clusterId)) {
        logger.error("the clusterId existed");
        openResultParam.setResultCode(ExceptionCodeConstants.DubboServiceCode.EXIST_CODE);
        openResultParam.setResultMsg("the clusterId is exist");
        return gson.toJson(openResultParam);
      }

      resReqInfo.setClusterId(clusterId);
      resReqInfo.setReqType(TaskUtil.REQINSERT);
      resReqInfo.setReqCnt(param);
      resReqInfo.setReqTime(new Timestamp(System.currentTimeMillis()));

      ExecuteOpenBatchJob executeBatchJob = new ExecuteOpenBatchJob();
      executeBatchJob.executeOpenService(param, openResultParam, "open");
    } catch (Exception e) {
      logger.error("open service:", e);
      status = TaskUtil.REQFAILED;
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
    String result = new String();
    try {
      if (StringUtils.isEmpty(param)) {
        throw new PaasException(ExceptionCodeConstants.DubboServiceCode.PARAM_IS_NULL,
            "the parameter for querying log is null");
      }
      List<ResTaskLog> list = TaskUtil.getResTaskLogs(param);
      List<LogResult> resultList = new ArrayList<LogResult>();
      if (list != null && list.size() != 0) {
        for (ResTaskLog resTaskLog : list) {
          int taskId = resTaskLog.getTaskId();
          ResJobDetail resJobDetail = TaskUtil.getResJobDetail(taskId);
          Timestamp startTime = resJobDetail.getTaskStartTime();
          Timestamp endTime = resJobDetail.getTaskEndTime();

          int typeId = resJobDetail.getTypeId();
          String desc = TaskUtil.getTypeDesc(typeId);
          String startdesc = TaskUtil.logStartDesc(desc);
          String enddesc = TaskUtil.logEndDesc(desc, resJobDetail.getTaskState());

          resultList.add(new LogResult(startdesc, startTime));
          resultList.add(new LogResult(enddesc, endTime));
        }
      }

      result = gson.toJson(resultList);
    } catch (PaasException e) {
      logger.error("query log:", e);
    }
    return result;
  }

  @Override
  public String freeResourcesService(String param) {
    Gson gson = new Gson();
    OpenResultParamVo openResultParam = new OpenResultParamVo();
    ResReqInfoMapper mapper = ServiceUtil.getMapper(ResReqInfoMapper.class);
    ResReqInfo resReqInfo = new ResReqInfo();
    int status = TaskUtil.REQSUCCESS;
    try {
      if (StringUtils.isEmpty(param)) {
        throw new PaasException(ExceptionCodeConstants.DubboServiceCode.PARAM_IS_NULL,
            "the parameter for free resources is null");
      }
      OpenResourceParamVo openParam = gson.fromJson(param, OpenResourceParamVo.class);
      if (!openParam.getUseAgent()) {
        throw new PaasException(ExceptionCodeConstants.TransServiceCode.ERROR_CODE,
            "it should be true for useAgent");
      }
      String clusterId = openParam.getClusterId();
      resReqInfo.setClusterId(clusterId);
      resReqInfo.setReqType(TaskUtil.REQFREE);
      resReqInfo.setReqCnt(param);
      resReqInfo.setReqTime(new Timestamp(System.currentTimeMillis()));

      ExecuteOpenBatchJob executeBatchJob = new ExecuteOpenBatchJob();
      executeBatchJob.executeOpenService(param, openResultParam, "free");
    } catch (Exception e) {
      logger.error("free resources:", e);
      status = TaskUtil.REQFAILED;
    }
    resReqInfo.setReqState(status);
    resReqInfo.setReqResp(gson.toJson(openResultParam));
    resReqInfo.setRespTime(new Timestamp(System.currentTimeMillis()));
    mapper.insert(resReqInfo);
    return gson.toJson(openResultParam);
  }

  @Override
  public String increaseSlaveService(String param) {
    Gson gson = new Gson();
    OpenResultParamVo openResultParam = new OpenResultParamVo();
    ResReqInfoMapper mapper = ServiceUtil.getMapper(ResReqInfoMapper.class);
    ResReqInfo resReqInfo = new ResReqInfo();
    int status = TaskUtil.REQSUCCESS;
    try {
      if (StringUtils.isEmpty(param)) {
        throw new PaasException(ExceptionCodeConstants.DubboServiceCode.PARAM_IS_NULL,
            "the parameter for free resources is null");
      }
      OpenResourceParamVo openParam = gson.fromJson(param, OpenResourceParamVo.class);
      if (!openParam.getUseAgent()) {
        throw new PaasException(ExceptionCodeConstants.TransServiceCode.ERROR_CODE,
            "it should be true for useAgent");
      }
      String clusterId = openParam.getClusterId();

      // TODO
      // 将clusterId相关信息查询出来，提取master信息，插入到param中


      resReqInfo.setClusterId(clusterId);
      resReqInfo.setReqType(TaskUtil.REQINCREASE);
      resReqInfo.setReqCnt(param);
      resReqInfo.setReqTime(new Timestamp(System.currentTimeMillis()));

      ExecuteOpenBatchJob executeBatchJob = new ExecuteOpenBatchJob();
      executeBatchJob.executeOpenService(param, openResultParam, "increase");
    } catch (Exception e) {
      logger.error("free resources:", e);
      status = TaskUtil.REQFAILED;
    }
    resReqInfo.setReqState(status);
    resReqInfo.setReqResp(gson.toJson(openResultParam));
    resReqInfo.setRespTime(new Timestamp(System.currentTimeMillis()));
    mapper.insert(resReqInfo);
    return gson.toJson(openResultParam);
  }

}
