package com.ai.paas.cpaas.rm.dao.interfaces;

import com.ai.paas.cpaas.rm.dao.mapper.bo.ResTaskType;
import com.ai.paas.cpaas.rm.dao.mapper.bo.ResTaskTypeCriteria;
import com.ai.paas.cpaas.rm.dao.mapper.bo.ResTaskTypeKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ResTaskTypeMapper {
    int countByExample(ResTaskTypeCriteria example);

    int deleteByExample(ResTaskTypeCriteria example);

    int deleteByPrimaryKey(ResTaskTypeKey key);

    int insert(ResTaskType record);

    int insertSelective(ResTaskType record);

    List<ResTaskType> selectByExample(ResTaskTypeCriteria example);

    ResTaskType selectByPrimaryKey(ResTaskTypeKey key);

    int updateByExampleSelective(@Param("record") ResTaskType record, @Param("example") ResTaskTypeCriteria example);

    int updateByExample(@Param("record") ResTaskType record, @Param("example") ResTaskTypeCriteria example);

    int updateByPrimaryKeySelective(ResTaskType record);

    int updateByPrimaryKey(ResTaskType record);
}