package com.ai.paas.cpaas.rm.manage.service.cluster;

import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ai.paas.cpaas.rm.dao.interfaces.ResClusterInfoMapper;
import com.ai.paas.cpaas.rm.dao.mapper.bo.ResClusterInfo;
import com.ai.paas.cpaas.rm.util.TaskUtil;
import com.ai.paas.cpaas.rm.vo.MesosInstance;
import com.ai.paas.cpaas.rm.vo.OpenResourceParamVo;
import com.ai.paas.ipaas.ServiceUtil;

public class InsertClusterInfo implements Tasklet{

	@Override
	public RepeatStatus execute(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		OpenResourceParamVo openParam = TaskUtil.createOpenParam(chunkContext);
		List<MesosInstance> list=openParam.getMesosMaster();
		StringBuffer mesos=new StringBuffer();
		StringBuffer marathon=new StringBuffer();
		StringBuffer consul=new StringBuffer();
		StringBuffer chronos=new StringBuffer();
		for(MesosInstance instance:list)
		{
			String ip=instance.getIp();
			mesos.append(this.genMesosInfo(ip)).append(",");
			marathon.append(this.genMarathonInfo(ip)).append(",");
			consul.append(this.genConsulInfo(ip)).append(",");
			chronos.append(this.genChronosInfo(ip)).append(",");
		}
		ResClusterInfo cluster=new ResClusterInfo();
		cluster.setMesosDomain(mesos.substring(0,mesos.length()-1));
		cluster.setMarathonAddr(marathon.substring(0,marathon.length()-1));
		cluster.setChronosAddr(chronos.substring(0,chronos.length()-1));
		cluster.setConsulAddr(consul.substring(0,consul.length()-1));
		cluster.setClusterId(openParam.getClusterId());
		cluster.setClusterName(openParam.getClusterName());
		cluster.setExternalDomain(openParam.getExternalDomain());
		
		ResClusterInfoMapper mapper=ServiceUtil.getMapper(ResClusterInfoMapper.class);
		mapper.insert(cluster);
		return RepeatStatus.FINISHED;
	}

	public String genMarathonInfo(String param){
		return "http://"+param+":8080";
	}
	public String genConsulInfo(String param)
	{
		return "http://"+param+":8500";
	}
	public String genChronosInfo(String param)
	{
		return "http://"+param+":4400";
	}
	public String genMesosInfo(String param)
	{
		return "http://"+param+":5050";
	}
}
