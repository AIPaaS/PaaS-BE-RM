package com.ai.paas.cpaas.rm.dao.interfaces;

import com.ai.paas.cpaas.rm.dao.mapper.bo.ResInstanceProps;
import com.ai.paas.cpaas.rm.dao.mapper.bo.ResInstancePropsCriteria;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ResInstancePropsMapper {
    int countByExample(ResInstancePropsCriteria example);

    int deleteByExample(ResInstancePropsCriteria example);

    int deleteByPrimaryKey(Integer keyId);

    int insert(ResInstanceProps record);

    int insertSelective(ResInstanceProps record);

    List<ResInstanceProps> selectByExampleWithBLOBs(ResInstancePropsCriteria example);

    List<ResInstanceProps> selectByExample(ResInstancePropsCriteria example);

    ResInstanceProps selectByPrimaryKey(Integer keyId);

    int updateByExampleSelective(@Param("record") ResInstanceProps record, @Param("example") ResInstancePropsCriteria example);

    int updateByExampleWithBLOBs(@Param("record") ResInstanceProps record, @Param("example") ResInstancePropsCriteria example);

    int updateByExample(@Param("record") ResInstanceProps record, @Param("example") ResInstancePropsCriteria example);

    int updateByPrimaryKeySelective(ResInstanceProps record);

    int updateByPrimaryKeyWithBLOBs(ResInstanceProps record);

    int updateByPrimaryKey(ResInstanceProps record);
}