package com.ai.paas.cpaas.rm.dao.interfaces;

import com.ai.paas.cpaas.rm.dao.mapper.bo.ResReqInfo;
import com.ai.paas.cpaas.rm.dao.mapper.bo.ResReqInfoCriteria;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ResReqInfoMapper {
    int countByExample(ResReqInfoCriteria example);

    int deleteByExample(ResReqInfoCriteria example);

    int deleteByPrimaryKey(Integer reqId);

    int insert(ResReqInfo record);

    int insertSelective(ResReqInfo record);

    List<ResReqInfo> selectByExample(ResReqInfoCriteria example);

    ResReqInfo selectByPrimaryKey(Integer reqId);

    int updateByExampleSelective(@Param("record") ResReqInfo record, @Param("example") ResReqInfoCriteria example);

    int updateByExample(@Param("record") ResReqInfo record, @Param("example") ResReqInfoCriteria example);

    int updateByPrimaryKeySelective(ResReqInfo record);

    int updateByPrimaryKey(ResReqInfo record);
}