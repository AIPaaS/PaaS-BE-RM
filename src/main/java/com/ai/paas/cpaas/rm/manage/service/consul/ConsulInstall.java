package com.ai.paas.cpaas.rm.manage.service.consul;

import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ai.paas.cpaas.rm.util.AnsibleCommand;
import com.ai.paas.cpaas.rm.util.ExceptionCodeConstants;
import com.ai.paas.cpaas.rm.util.OpenPortUtil;
import com.ai.paas.cpaas.rm.util.TaskUtil;
import com.ai.paas.cpaas.rm.vo.MesosInstance;
import com.ai.paas.cpaas.rm.vo.OpenResourceParamVo;
import com.ai.paas.ipaas.PaasException;
import com.esotericsoftware.minlog.Log;

public class ConsulInstall implements Tasklet {
	@Override
	public RepeatStatus execute(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		OpenResourceParamVo openParam = TaskUtil.createOpenParam(chunkContext);
		InputStream in = OpenPortUtil.class
				.getResourceAsStream("/playbook/consul/consulinstall.yml");

		String content = TaskUtil.getFile(in);
		String aid = openParam.getAid();
		Boolean useAgent = openParam.getUseAgent();
		TaskUtil.uploadFile("consulinstall.yml", content, useAgent, aid);
		TaskUtil.uploadFile("00-defaults.json.j2", content, useAgent, aid);

		StringBuffer shellContext = TaskUtil.createBashFile();
		List<MesosInstance> mesosMaster = openParam.getMesosMaster();

		String datacenter = openParam.getDataCenter();
		String domain = openParam.getDomain();
		for (int i = 0; i < mesosMaster.size(); i++) {
			String node_name = "master-" + i;
			String password = mesosMaster.get(i).getPasswd();
			this.genCommand(mesosMaster.get(i), openParam, shellContext,
					datacenter, domain, node_name, password, i, "master");
		}
		Timestamp start = new Timestamp(System.currentTimeMillis());

		String result = new String();
		try {
			result = TaskUtil.executeFile("consulinstall",
					shellContext.toString(), useAgent, aid);
		} catch (Exception e) {
			Log.error(e.toString());
			result = e.toString();
			throw new PaasException(ExceptionCodeConstants.DubboServiceCode.SYSTEM_ERROR_CODE,
					e.toString());
		} finally {
			// insert log and task record
			int taskId = TaskUtil.insertResJobDetail(start,
					openParam.getClusterId(), shellContext.toString(), 28);
			TaskUtil.insertResTaskLog(openParam.getClusterId(), taskId, result);
		}

		return RepeatStatus.FINISHED;
	}

	public void genCommand(MesosInstance instance,
			OpenResourceParamVo openParam, StringBuffer shellContext,
			String datacenter, String domain, String node_name,
			String password, int i, String type) {
		List<String> vars = new ArrayList<String>();
		vars.add("ansible_ssh_pass=" + password);
		vars.add("ansible_become_pass=" + password);
		vars.add("datacenter='" + datacenter.toString() + "'");
		vars.add("domain='" + domain + "'");
		vars.add("node_name='" + node_name + "'");

		AnsibleCommand command = new AnsibleCommand(
				TaskUtil.getSystemProperty("filepath") + "/consulinstall.yml",
				"root", vars);
		shellContext.append(command.toString()).append("\n");
	}
}
