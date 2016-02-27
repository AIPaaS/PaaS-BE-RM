package com.ai.paas.cpaas.rm.manage.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ai.paas.cpaas.rm.interfaces.IMgmtOpenService;
import com.ai.paas.cpaas.rm.vo.MesosInstance;
import com.ai.paas.cpaas.rm.vo.MesosSlave;
import com.ai.paas.cpaas.rm.vo.OpenResourceParamVo;
import com.google.gson.Gson;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext-mybatis.xml"})
public class MgmtOpenServiceTest {

  @Autowired
  IMgmtOpenService service;

  @Test
  public void openServiceTest() {
    OpenResourceParamVo paramVo = new OpenResourceParamVo();
    paramVo.setClusterName("cluster-01");
    paramVo.setClusterName("south-center");
    paramVo.setDomain("asiainfo.com");
    paramVo.setExternalDomain("asia.com");
    paramVo.setLoadVirtualIP("10.1.1.10");
    MesosInstance master = new MesosInstance();
    master.setId(0);
    master.setIp("10.1.241.127");
    master.setRoot("root");
    master.setPasswd("abc@123");
    List<MesosInstance> masters = new ArrayList<MesosInstance>();
    masters.add(master);

    MesosSlave slave = new MesosSlave();
    slave.setId(0);
    slave.setIp("10.1.241.130");
    slave.setRoot("root");
    slave.setPasswd("abc@123");
    slave.setRole("web");
    slave
        .setAttributes("ds:sth;jf:nj;rack:2;ex:ex1;model:hp-150;cpu:xe3;mem:ddr3;disk:ssd;netband:1G;");
    slave.setCpuTotal(8);
    slave.setCpuOffer(6);
    slave.setMemTotal(8096);
    slave.setMemOffer(6144);
    List<MesosSlave> slaves = new ArrayList<MesosSlave>();
    slaves.add(slave);

    paramVo.setMesosMaster(masters);
    paramVo.setMesosSlave(slaves);
    paramVo.setUseAgent(Boolean.TRUE);
    paramVo.setImagePath("/aifs01/docker");
    Gson gson = new Gson();
    String param = gson.toJson(paramVo);
    service.openService(param);
  }
}
