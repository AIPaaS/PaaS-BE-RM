package com.ai.paas.cpaas.rm.dao.interfaces;

import com.ai.paas.cpaas.rm.dao.mapper.bo.ResJobDetail;
import com.ai.paas.cpaas.rm.dao.mapper.bo.ResJobDetailCriteria;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ResJobDetailMapper {
    int countByExample(ResJobDetailCriteria example);

    int deleteByExample(ResJobDetailCriteria example);

    int deleteByPrimaryKey(Integer taskId);

    int insert(ResJobDetail record);

    int insertSelective(ResJobDetail record);

    List<ResJobDetail> selectByExampleWithBLOBs(ResJobDetailCriteria example);

    List<ResJobDetail> selectByExample(ResJobDetailCriteria example);

    ResJobDetail selectByPrimaryKey(Integer taskId);

    int updateByExampleSelective(@Param("record") ResJobDetail record, @Param("example") ResJobDetailCriteria example);

    int updateByExampleWithBLOBs(@Param("record") ResJobDetail record, @Param("example") ResJobDetailCriteria example);

    int updateByExample(@Param("record") ResJobDetail record, @Param("example") ResJobDetailCriteria example);

    int updateByPrimaryKeySelective(ResJobDetail record);

    int updateByPrimaryKeyWithBLOBs(ResJobDetail record);

    int updateByPrimaryKey(ResJobDetail record);
}