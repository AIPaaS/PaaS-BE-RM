package com.ai.paas.cpaas.rm.dao.interfaces;

import com.ai.paas.cpaas.rm.dao.mapper.bo.ResTaskLog;
import com.ai.paas.cpaas.rm.dao.mapper.bo.ResTaskLogCriteria;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ResTaskLogMapper {
    int countByExample(ResTaskLogCriteria example);

    int deleteByExample(ResTaskLogCriteria example);

    int deleteByPrimaryKey(Long logId);

    int insert(ResTaskLog record);

    int insertSelective(ResTaskLog record);

    List<ResTaskLog> selectByExample(ResTaskLogCriteria example);

    ResTaskLog selectByPrimaryKey(Long logId);

    int updateByExampleSelective(@Param("record") ResTaskLog record, @Param("example") ResTaskLogCriteria example);

    int updateByExample(@Param("record") ResTaskLog record, @Param("example") ResTaskLogCriteria example);

    int updateByPrimaryKeySelective(ResTaskLog record);

    int updateByPrimaryKey(ResTaskLog record);
}