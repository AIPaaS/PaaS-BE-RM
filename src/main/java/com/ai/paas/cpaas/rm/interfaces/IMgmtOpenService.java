package com.ai.paas.cpaas.rm.interfaces;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.ai.paas.ipaas.rest.manage.ISrvManager;

@Path("/deploy/manage")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
public interface IMgmtOpenService extends ISrvManager {

  @Path("/open")
  @POST
  public String openService(String param);
  
  @Path("/queryLog")
  @POST
  public String queryLog(String param);
}
