package com.ai.paas.cpaas.rm.dao.mapper.bo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ResReqInfoCriteria {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    protected Integer limitStart;

    protected Integer limitEnd;

    public ResReqInfoCriteria() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    public void setLimitStart(Integer limitStart) {
        this.limitStart=limitStart;
    }

    public Integer getLimitStart() {
        return limitStart;
    }

    public void setLimitEnd(Integer limitEnd) {
        this.limitEnd=limitEnd;
    }

    public Integer getLimitEnd() {
        return limitEnd;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andReqIdIsNull() {
            addCriterion("req_id is null");
            return (Criteria) this;
        }

        public Criteria andReqIdIsNotNull() {
            addCriterion("req_id is not null");
            return (Criteria) this;
        }

        public Criteria andReqIdEqualTo(Integer value) {
            addCriterion("req_id =", value, "reqId");
            return (Criteria) this;
        }

        public Criteria andReqIdNotEqualTo(Integer value) {
            addCriterion("req_id <>", value, "reqId");
            return (Criteria) this;
        }

        public Criteria andReqIdGreaterThan(Integer value) {
            addCriterion("req_id >", value, "reqId");
            return (Criteria) this;
        }

        public Criteria andReqIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("req_id >=", value, "reqId");
            return (Criteria) this;
        }

        public Criteria andReqIdLessThan(Integer value) {
            addCriterion("req_id <", value, "reqId");
            return (Criteria) this;
        }

        public Criteria andReqIdLessThanOrEqualTo(Integer value) {
            addCriterion("req_id <=", value, "reqId");
            return (Criteria) this;
        }

        public Criteria andReqIdIn(List<Integer> values) {
            addCriterion("req_id in", values, "reqId");
            return (Criteria) this;
        }

        public Criteria andReqIdNotIn(List<Integer> values) {
            addCriterion("req_id not in", values, "reqId");
            return (Criteria) this;
        }

        public Criteria andReqIdBetween(Integer value1, Integer value2) {
            addCriterion("req_id between", value1, value2, "reqId");
            return (Criteria) this;
        }

        public Criteria andReqIdNotBetween(Integer value1, Integer value2) {
            addCriterion("req_id not between", value1, value2, "reqId");
            return (Criteria) this;
        }

        public Criteria andJobIdIsNull() {
            addCriterion("job_id is null");
            return (Criteria) this;
        }

        public Criteria andJobIdIsNotNull() {
            addCriterion("job_id is not null");
            return (Criteria) this;
        }

        public Criteria andJobIdEqualTo(Integer value) {
            addCriterion("job_id =", value, "jobId");
            return (Criteria) this;
        }

        public Criteria andJobIdNotEqualTo(Integer value) {
            addCriterion("job_id <>", value, "jobId");
            return (Criteria) this;
        }

        public Criteria andJobIdGreaterThan(Integer value) {
            addCriterion("job_id >", value, "jobId");
            return (Criteria) this;
        }

        public Criteria andJobIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("job_id >=", value, "jobId");
            return (Criteria) this;
        }

        public Criteria andJobIdLessThan(Integer value) {
            addCriterion("job_id <", value, "jobId");
            return (Criteria) this;
        }

        public Criteria andJobIdLessThanOrEqualTo(Integer value) {
            addCriterion("job_id <=", value, "jobId");
            return (Criteria) this;
        }

        public Criteria andJobIdIn(List<Integer> values) {
            addCriterion("job_id in", values, "jobId");
            return (Criteria) this;
        }

        public Criteria andJobIdNotIn(List<Integer> values) {
            addCriterion("job_id not in", values, "jobId");
            return (Criteria) this;
        }

        public Criteria andJobIdBetween(Integer value1, Integer value2) {
            addCriterion("job_id between", value1, value2, "jobId");
            return (Criteria) this;
        }

        public Criteria andJobIdNotBetween(Integer value1, Integer value2) {
            addCriterion("job_id not between", value1, value2, "jobId");
            return (Criteria) this;
        }

        public Criteria andReqTypeIsNull() {
            addCriterion("req_type is null");
            return (Criteria) this;
        }

        public Criteria andReqTypeIsNotNull() {
            addCriterion("req_type is not null");
            return (Criteria) this;
        }

        public Criteria andReqTypeEqualTo(Integer value) {
            addCriterion("req_type =", value, "reqType");
            return (Criteria) this;
        }

        public Criteria andReqTypeNotEqualTo(Integer value) {
            addCriterion("req_type <>", value, "reqType");
            return (Criteria) this;
        }

        public Criteria andReqTypeGreaterThan(Integer value) {
            addCriterion("req_type >", value, "reqType");
            return (Criteria) this;
        }

        public Criteria andReqTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("req_type >=", value, "reqType");
            return (Criteria) this;
        }

        public Criteria andReqTypeLessThan(Integer value) {
            addCriterion("req_type <", value, "reqType");
            return (Criteria) this;
        }

        public Criteria andReqTypeLessThanOrEqualTo(Integer value) {
            addCriterion("req_type <=", value, "reqType");
            return (Criteria) this;
        }

        public Criteria andReqTypeIn(List<Integer> values) {
            addCriterion("req_type in", values, "reqType");
            return (Criteria) this;
        }

        public Criteria andReqTypeNotIn(List<Integer> values) {
            addCriterion("req_type not in", values, "reqType");
            return (Criteria) this;
        }

        public Criteria andReqTypeBetween(Integer value1, Integer value2) {
            addCriterion("req_type between", value1, value2, "reqType");
            return (Criteria) this;
        }

        public Criteria andReqTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("req_type not between", value1, value2, "reqType");
            return (Criteria) this;
        }

        public Criteria andReqCntIsNull() {
            addCriterion("req_cnt is null");
            return (Criteria) this;
        }

        public Criteria andReqCntIsNotNull() {
            addCriterion("req_cnt is not null");
            return (Criteria) this;
        }

        public Criteria andReqCntEqualTo(String value) {
            addCriterion("req_cnt =", value, "reqCnt");
            return (Criteria) this;
        }

        public Criteria andReqCntNotEqualTo(String value) {
            addCriterion("req_cnt <>", value, "reqCnt");
            return (Criteria) this;
        }

        public Criteria andReqCntGreaterThan(String value) {
            addCriterion("req_cnt >", value, "reqCnt");
            return (Criteria) this;
        }

        public Criteria andReqCntGreaterThanOrEqualTo(String value) {
            addCriterion("req_cnt >=", value, "reqCnt");
            return (Criteria) this;
        }

        public Criteria andReqCntLessThan(String value) {
            addCriterion("req_cnt <", value, "reqCnt");
            return (Criteria) this;
        }

        public Criteria andReqCntLessThanOrEqualTo(String value) {
            addCriterion("req_cnt <=", value, "reqCnt");
            return (Criteria) this;
        }

        public Criteria andReqCntLike(String value) {
            addCriterion("req_cnt like", value, "reqCnt");
            return (Criteria) this;
        }

        public Criteria andReqCntNotLike(String value) {
            addCriterion("req_cnt not like", value, "reqCnt");
            return (Criteria) this;
        }

        public Criteria andReqCntIn(List<String> values) {
            addCriterion("req_cnt in", values, "reqCnt");
            return (Criteria) this;
        }

        public Criteria andReqCntNotIn(List<String> values) {
            addCriterion("req_cnt not in", values, "reqCnt");
            return (Criteria) this;
        }

        public Criteria andReqCntBetween(String value1, String value2) {
            addCriterion("req_cnt between", value1, value2, "reqCnt");
            return (Criteria) this;
        }

        public Criteria andReqCntNotBetween(String value1, String value2) {
            addCriterion("req_cnt not between", value1, value2, "reqCnt");
            return (Criteria) this;
        }

        public Criteria andReqRespIsNull() {
            addCriterion("req_resp is null");
            return (Criteria) this;
        }

        public Criteria andReqRespIsNotNull() {
            addCriterion("req_resp is not null");
            return (Criteria) this;
        }

        public Criteria andReqRespEqualTo(String value) {
            addCriterion("req_resp =", value, "reqResp");
            return (Criteria) this;
        }

        public Criteria andReqRespNotEqualTo(String value) {
            addCriterion("req_resp <>", value, "reqResp");
            return (Criteria) this;
        }

        public Criteria andReqRespGreaterThan(String value) {
            addCriterion("req_resp >", value, "reqResp");
            return (Criteria) this;
        }

        public Criteria andReqRespGreaterThanOrEqualTo(String value) {
            addCriterion("req_resp >=", value, "reqResp");
            return (Criteria) this;
        }

        public Criteria andReqRespLessThan(String value) {
            addCriterion("req_resp <", value, "reqResp");
            return (Criteria) this;
        }

        public Criteria andReqRespLessThanOrEqualTo(String value) {
            addCriterion("req_resp <=", value, "reqResp");
            return (Criteria) this;
        }

        public Criteria andReqRespLike(String value) {
            addCriterion("req_resp like", value, "reqResp");
            return (Criteria) this;
        }

        public Criteria andReqRespNotLike(String value) {
            addCriterion("req_resp not like", value, "reqResp");
            return (Criteria) this;
        }

        public Criteria andReqRespIn(List<String> values) {
            addCriterion("req_resp in", values, "reqResp");
            return (Criteria) this;
        }

        public Criteria andReqRespNotIn(List<String> values) {
            addCriterion("req_resp not in", values, "reqResp");
            return (Criteria) this;
        }

        public Criteria andReqRespBetween(String value1, String value2) {
            addCriterion("req_resp between", value1, value2, "reqResp");
            return (Criteria) this;
        }

        public Criteria andReqRespNotBetween(String value1, String value2) {
            addCriterion("req_resp not between", value1, value2, "reqResp");
            return (Criteria) this;
        }

        public Criteria andReqTimeIsNull() {
            addCriterion("req_time is null");
            return (Criteria) this;
        }

        public Criteria andReqTimeIsNotNull() {
            addCriterion("req_time is not null");
            return (Criteria) this;
        }

        public Criteria andReqTimeEqualTo(Timestamp value) {
            addCriterion("req_time =", value, "reqTime");
            return (Criteria) this;
        }

        public Criteria andReqTimeNotEqualTo(Timestamp value) {
            addCriterion("req_time <>", value, "reqTime");
            return (Criteria) this;
        }

        public Criteria andReqTimeGreaterThan(Timestamp value) {
            addCriterion("req_time >", value, "reqTime");
            return (Criteria) this;
        }

        public Criteria andReqTimeGreaterThanOrEqualTo(Timestamp value) {
            addCriterion("req_time >=", value, "reqTime");
            return (Criteria) this;
        }

        public Criteria andReqTimeLessThan(Timestamp value) {
            addCriterion("req_time <", value, "reqTime");
            return (Criteria) this;
        }

        public Criteria andReqTimeLessThanOrEqualTo(Timestamp value) {
            addCriterion("req_time <=", value, "reqTime");
            return (Criteria) this;
        }

        public Criteria andReqTimeIn(List<Timestamp> values) {
            addCriterion("req_time in", values, "reqTime");
            return (Criteria) this;
        }

        public Criteria andReqTimeNotIn(List<Timestamp> values) {
            addCriterion("req_time not in", values, "reqTime");
            return (Criteria) this;
        }

        public Criteria andReqTimeBetween(Timestamp value1, Timestamp value2) {
            addCriterion("req_time between", value1, value2, "reqTime");
            return (Criteria) this;
        }

        public Criteria andReqTimeNotBetween(Timestamp value1, Timestamp value2) {
            addCriterion("req_time not between", value1, value2, "reqTime");
            return (Criteria) this;
        }

        public Criteria andRespTimeIsNull() {
            addCriterion("resp_time is null");
            return (Criteria) this;
        }

        public Criteria andRespTimeIsNotNull() {
            addCriterion("resp_time is not null");
            return (Criteria) this;
        }

        public Criteria andRespTimeEqualTo(Timestamp value) {
            addCriterion("resp_time =", value, "respTime");
            return (Criteria) this;
        }

        public Criteria andRespTimeNotEqualTo(Timestamp value) {
            addCriterion("resp_time <>", value, "respTime");
            return (Criteria) this;
        }

        public Criteria andRespTimeGreaterThan(Timestamp value) {
            addCriterion("resp_time >", value, "respTime");
            return (Criteria) this;
        }

        public Criteria andRespTimeGreaterThanOrEqualTo(Timestamp value) {
            addCriterion("resp_time >=", value, "respTime");
            return (Criteria) this;
        }

        public Criteria andRespTimeLessThan(Timestamp value) {
            addCriterion("resp_time <", value, "respTime");
            return (Criteria) this;
        }

        public Criteria andRespTimeLessThanOrEqualTo(Timestamp value) {
            addCriterion("resp_time <=", value, "respTime");
            return (Criteria) this;
        }

        public Criteria andRespTimeIn(List<Timestamp> values) {
            addCriterion("resp_time in", values, "respTime");
            return (Criteria) this;
        }

        public Criteria andRespTimeNotIn(List<Timestamp> values) {
            addCriterion("resp_time not in", values, "respTime");
            return (Criteria) this;
        }

        public Criteria andRespTimeBetween(Timestamp value1, Timestamp value2) {
            addCriterion("resp_time between", value1, value2, "respTime");
            return (Criteria) this;
        }

        public Criteria andRespTimeNotBetween(Timestamp value1, Timestamp value2) {
            addCriterion("resp_time not between", value1, value2, "respTime");
            return (Criteria) this;
        }

        public Criteria andReqStateIsNull() {
            addCriterion("req_state is null");
            return (Criteria) this;
        }

        public Criteria andReqStateIsNotNull() {
            addCriterion("req_state is not null");
            return (Criteria) this;
        }

        public Criteria andReqStateEqualTo(Integer value) {
            addCriterion("req_state =", value, "reqState");
            return (Criteria) this;
        }

        public Criteria andReqStateNotEqualTo(Integer value) {
            addCriterion("req_state <>", value, "reqState");
            return (Criteria) this;
        }

        public Criteria andReqStateGreaterThan(Integer value) {
            addCriterion("req_state >", value, "reqState");
            return (Criteria) this;
        }

        public Criteria andReqStateGreaterThanOrEqualTo(Integer value) {
            addCriterion("req_state >=", value, "reqState");
            return (Criteria) this;
        }

        public Criteria andReqStateLessThan(Integer value) {
            addCriterion("req_state <", value, "reqState");
            return (Criteria) this;
        }

        public Criteria andReqStateLessThanOrEqualTo(Integer value) {
            addCriterion("req_state <=", value, "reqState");
            return (Criteria) this;
        }

        public Criteria andReqStateIn(List<Integer> values) {
            addCriterion("req_state in", values, "reqState");
            return (Criteria) this;
        }

        public Criteria andReqStateNotIn(List<Integer> values) {
            addCriterion("req_state not in", values, "reqState");
            return (Criteria) this;
        }

        public Criteria andReqStateBetween(Integer value1, Integer value2) {
            addCriterion("req_state between", value1, value2, "reqState");
            return (Criteria) this;
        }

        public Criteria andReqStateNotBetween(Integer value1, Integer value2) {
            addCriterion("req_state not between", value1, value2, "reqState");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}