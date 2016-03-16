package com.ai.paas.cpaas.rm.util;

import org.springframework.batch.core.ItemProcessListener;

public class CommonProcessLisener implements ItemProcessListener<Object, Object> {

  @Override
  public void beforeProcess(Object item) {
    // TODO Auto-generated method stub

  }

  @Override
  public void afterProcess(Object item, Object result) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onProcessError(Object item, Exception e) {
    // TODO Auto-generated method stub
    System.out.println(item.toString());
    System.out.println(e.toString());
  }

}
