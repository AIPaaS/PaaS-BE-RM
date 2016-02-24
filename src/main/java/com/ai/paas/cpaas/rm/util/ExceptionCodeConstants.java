package com.ai.paas.cpaas.rm.util;

public class ExceptionCodeConstants {

  public static class DubboServiceCode {


    public static final String SUCCESS_CODE = "000000";

    public static final String SUCCESS_MESSAGE = "success";

    public static final String SYSTEM_ERROR_CODE = "999999";

    public static final String SYSTEM_ERROR_MESSAGE = "System problem, please try later!";


    // ����Ϊ��
    public static final String PARAM_IS_NULL = "999999";
    // û���㹻�Ŀ�Ͼ��Դ
    public static final String NO_FREE_RES = "999999";

    // ��ѯ������Ӧ������
    public static final String SYSTEM_QUERY_FAILED = "Unable to query the data";

  }

  public static class TransServiceCode {

    public static final String ERROR_CODE = "1";

    public static final String SUCCESS_CODE = "0";
  }
}
