package com.dianxian.sms.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SmsDtoExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public SmsDtoExample() {
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

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Long value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andMobileNoIsNull() {
            addCriterion("mobile_no is null");
            return (Criteria) this;
        }

        public Criteria andMobileNoIsNotNull() {
            addCriterion("mobile_no is not null");
            return (Criteria) this;
        }

        public Criteria andMobileNoEqualTo(String value) {
            addCriterion("mobile_no =", value, "mobileNo");
            return (Criteria) this;
        }

        public Criteria andMobileNoNotEqualTo(String value) {
            addCriterion("mobile_no <>", value, "mobileNo");
            return (Criteria) this;
        }

        public Criteria andMobileNoGreaterThan(String value) {
            addCriterion("mobile_no >", value, "mobileNo");
            return (Criteria) this;
        }

        public Criteria andMobileNoGreaterThanOrEqualTo(String value) {
            addCriterion("mobile_no >=", value, "mobileNo");
            return (Criteria) this;
        }

        public Criteria andMobileNoLessThan(String value) {
            addCriterion("mobile_no <", value, "mobileNo");
            return (Criteria) this;
        }

        public Criteria andMobileNoLessThanOrEqualTo(String value) {
            addCriterion("mobile_no <=", value, "mobileNo");
            return (Criteria) this;
        }

        public Criteria andMobileNoLike(String value) {
            addCriterion("mobile_no like", value, "mobileNo");
            return (Criteria) this;
        }

        public Criteria andMobileNoNotLike(String value) {
            addCriterion("mobile_no not like", value, "mobileNo");
            return (Criteria) this;
        }

        public Criteria andMobileNoIn(List<String> values) {
            addCriterion("mobile_no in", values, "mobileNo");
            return (Criteria) this;
        }

        public Criteria andMobileNoNotIn(List<String> values) {
            addCriterion("mobile_no not in", values, "mobileNo");
            return (Criteria) this;
        }

        public Criteria andMobileNoBetween(String value1, String value2) {
            addCriterion("mobile_no between", value1, value2, "mobileNo");
            return (Criteria) this;
        }

        public Criteria andMobileNoNotBetween(String value1, String value2) {
            addCriterion("mobile_no not between", value1, value2, "mobileNo");
            return (Criteria) this;
        }

        public Criteria andAppKeyIsNull() {
            addCriterion("app_key is null");
            return (Criteria) this;
        }

        public Criteria andAppKeyIsNotNull() {
            addCriterion("app_key is not null");
            return (Criteria) this;
        }

        public Criteria andAppKeyEqualTo(String value) {
            addCriterion("app_key =", value, "appKey");
            return (Criteria) this;
        }

        public Criteria andAppKeyNotEqualTo(String value) {
            addCriterion("app_key <>", value, "appKey");
            return (Criteria) this;
        }

        public Criteria andAppKeyGreaterThan(String value) {
            addCriterion("app_key >", value, "appKey");
            return (Criteria) this;
        }

        public Criteria andAppKeyGreaterThanOrEqualTo(String value) {
            addCriterion("app_key >=", value, "appKey");
            return (Criteria) this;
        }

        public Criteria andAppKeyLessThan(String value) {
            addCriterion("app_key <", value, "appKey");
            return (Criteria) this;
        }

        public Criteria andAppKeyLessThanOrEqualTo(String value) {
            addCriterion("app_key <=", value, "appKey");
            return (Criteria) this;
        }

        public Criteria andAppKeyLike(String value) {
            addCriterion("app_key like", value, "appKey");
            return (Criteria) this;
        }

        public Criteria andAppKeyNotLike(String value) {
            addCriterion("app_key not like", value, "appKey");
            return (Criteria) this;
        }

        public Criteria andAppKeyIn(List<String> values) {
            addCriterion("app_key in", values, "appKey");
            return (Criteria) this;
        }

        public Criteria andAppKeyNotIn(List<String> values) {
            addCriterion("app_key not in", values, "appKey");
            return (Criteria) this;
        }

        public Criteria andAppKeyBetween(String value1, String value2) {
            addCriterion("app_key between", value1, value2, "appKey");
            return (Criteria) this;
        }

        public Criteria andAppKeyNotBetween(String value1, String value2) {
            addCriterion("app_key not between", value1, value2, "appKey");
            return (Criteria) this;
        }

        public Criteria andSmsTemplateIdIsNull() {
            addCriterion("sms_template_id is null");
            return (Criteria) this;
        }

        public Criteria andSmsTemplateIdIsNotNull() {
            addCriterion("sms_template_id is not null");
            return (Criteria) this;
        }

        public Criteria andSmsTemplateIdEqualTo(String value) {
            addCriterion("sms_template_id =", value, "smsTemplateId");
            return (Criteria) this;
        }

        public Criteria andSmsTemplateIdNotEqualTo(String value) {
            addCriterion("sms_template_id <>", value, "smsTemplateId");
            return (Criteria) this;
        }

        public Criteria andSmsTemplateIdGreaterThan(String value) {
            addCriterion("sms_template_id >", value, "smsTemplateId");
            return (Criteria) this;
        }

        public Criteria andSmsTemplateIdGreaterThanOrEqualTo(String value) {
            addCriterion("sms_template_id >=", value, "smsTemplateId");
            return (Criteria) this;
        }

        public Criteria andSmsTemplateIdLessThan(String value) {
            addCriterion("sms_template_id <", value, "smsTemplateId");
            return (Criteria) this;
        }

        public Criteria andSmsTemplateIdLessThanOrEqualTo(String value) {
            addCriterion("sms_template_id <=", value, "smsTemplateId");
            return (Criteria) this;
        }

        public Criteria andSmsTemplateIdLike(String value) {
            addCriterion("sms_template_id like", value, "smsTemplateId");
            return (Criteria) this;
        }

        public Criteria andSmsTemplateIdNotLike(String value) {
            addCriterion("sms_template_id not like", value, "smsTemplateId");
            return (Criteria) this;
        }

        public Criteria andSmsTemplateIdIn(List<String> values) {
            addCriterion("sms_template_id in", values, "smsTemplateId");
            return (Criteria) this;
        }

        public Criteria andSmsTemplateIdNotIn(List<String> values) {
            addCriterion("sms_template_id not in", values, "smsTemplateId");
            return (Criteria) this;
        }

        public Criteria andSmsTemplateIdBetween(String value1, String value2) {
            addCriterion("sms_template_id between", value1, value2, "smsTemplateId");
            return (Criteria) this;
        }

        public Criteria andSmsTemplateIdNotBetween(String value1, String value2) {
            addCriterion("sms_template_id not between", value1, value2, "smsTemplateId");
            return (Criteria) this;
        }

        public Criteria andSmsRequestIsNull() {
            addCriterion("sms_request is null");
            return (Criteria) this;
        }

        public Criteria andSmsRequestIsNotNull() {
            addCriterion("sms_request is not null");
            return (Criteria) this;
        }

        public Criteria andSmsRequestEqualTo(String value) {
            addCriterion("sms_request =", value, "smsRequest");
            return (Criteria) this;
        }

        public Criteria andSmsRequestNotEqualTo(String value) {
            addCriterion("sms_request <>", value, "smsRequest");
            return (Criteria) this;
        }

        public Criteria andSmsRequestGreaterThan(String value) {
            addCriterion("sms_request >", value, "smsRequest");
            return (Criteria) this;
        }

        public Criteria andSmsRequestGreaterThanOrEqualTo(String value) {
            addCriterion("sms_request >=", value, "smsRequest");
            return (Criteria) this;
        }

        public Criteria andSmsRequestLessThan(String value) {
            addCriterion("sms_request <", value, "smsRequest");
            return (Criteria) this;
        }

        public Criteria andSmsRequestLessThanOrEqualTo(String value) {
            addCriterion("sms_request <=", value, "smsRequest");
            return (Criteria) this;
        }

        public Criteria andSmsRequestLike(String value) {
            addCriterion("sms_request like", value, "smsRequest");
            return (Criteria) this;
        }

        public Criteria andSmsRequestNotLike(String value) {
            addCriterion("sms_request not like", value, "smsRequest");
            return (Criteria) this;
        }

        public Criteria andSmsRequestIn(List<String> values) {
            addCriterion("sms_request in", values, "smsRequest");
            return (Criteria) this;
        }

        public Criteria andSmsRequestNotIn(List<String> values) {
            addCriterion("sms_request not in", values, "smsRequest");
            return (Criteria) this;
        }

        public Criteria andSmsRequestBetween(String value1, String value2) {
            addCriterion("sms_request between", value1, value2, "smsRequest");
            return (Criteria) this;
        }

        public Criteria andSmsRequestNotBetween(String value1, String value2) {
            addCriterion("sms_request not between", value1, value2, "smsRequest");
            return (Criteria) this;
        }

        public Criteria andSmsResponseIsNull() {
            addCriterion("sms_response is null");
            return (Criteria) this;
        }

        public Criteria andSmsResponseIsNotNull() {
            addCriterion("sms_response is not null");
            return (Criteria) this;
        }

        public Criteria andSmsResponseEqualTo(String value) {
            addCriterion("sms_response =", value, "smsResponse");
            return (Criteria) this;
        }

        public Criteria andSmsResponseNotEqualTo(String value) {
            addCriterion("sms_response <>", value, "smsResponse");
            return (Criteria) this;
        }

        public Criteria andSmsResponseGreaterThan(String value) {
            addCriterion("sms_response >", value, "smsResponse");
            return (Criteria) this;
        }

        public Criteria andSmsResponseGreaterThanOrEqualTo(String value) {
            addCriterion("sms_response >=", value, "smsResponse");
            return (Criteria) this;
        }

        public Criteria andSmsResponseLessThan(String value) {
            addCriterion("sms_response <", value, "smsResponse");
            return (Criteria) this;
        }

        public Criteria andSmsResponseLessThanOrEqualTo(String value) {
            addCriterion("sms_response <=", value, "smsResponse");
            return (Criteria) this;
        }

        public Criteria andSmsResponseLike(String value) {
            addCriterion("sms_response like", value, "smsResponse");
            return (Criteria) this;
        }

        public Criteria andSmsResponseNotLike(String value) {
            addCriterion("sms_response not like", value, "smsResponse");
            return (Criteria) this;
        }

        public Criteria andSmsResponseIn(List<String> values) {
            addCriterion("sms_response in", values, "smsResponse");
            return (Criteria) this;
        }

        public Criteria andSmsResponseNotIn(List<String> values) {
            addCriterion("sms_response not in", values, "smsResponse");
            return (Criteria) this;
        }

        public Criteria andSmsResponseBetween(String value1, String value2) {
            addCriterion("sms_response between", value1, value2, "smsResponse");
            return (Criteria) this;
        }

        public Criteria andSmsResponseNotBetween(String value1, String value2) {
            addCriterion("sms_response not between", value1, value2, "smsResponse");
            return (Criteria) this;
        }

        public Criteria andStatusIsNull() {
            addCriterion("status is null");
            return (Criteria) this;
        }

        public Criteria andStatusIsNotNull() {
            addCriterion("status is not null");
            return (Criteria) this;
        }

        public Criteria andStatusEqualTo(Integer value) {
            addCriterion("status =", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotEqualTo(Integer value) {
            addCriterion("status <>", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThan(Integer value) {
            addCriterion("status >", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("status >=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThan(Integer value) {
            addCriterion("status <", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThanOrEqualTo(Integer value) {
            addCriterion("status <=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusIn(List<Integer> values) {
            addCriterion("status in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotIn(List<Integer> values) {
            addCriterion("status not in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusBetween(Integer value1, Integer value2) {
            addCriterion("status between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("status not between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andCreatedAtIsNull() {
            addCriterion("created_at is null");
            return (Criteria) this;
        }

        public Criteria andCreatedAtIsNotNull() {
            addCriterion("created_at is not null");
            return (Criteria) this;
        }

        public Criteria andCreatedAtEqualTo(Date value) {
            addCriterion("created_at =", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtNotEqualTo(Date value) {
            addCriterion("created_at <>", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtGreaterThan(Date value) {
            addCriterion("created_at >", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtGreaterThanOrEqualTo(Date value) {
            addCriterion("created_at >=", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtLessThan(Date value) {
            addCriterion("created_at <", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtLessThanOrEqualTo(Date value) {
            addCriterion("created_at <=", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtIn(List<Date> values) {
            addCriterion("created_at in", values, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtNotIn(List<Date> values) {
            addCriterion("created_at not in", values, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtBetween(Date value1, Date value2) {
            addCriterion("created_at between", value1, value2, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtNotBetween(Date value1, Date value2) {
            addCriterion("created_at not between", value1, value2, "createdAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtIsNull() {
            addCriterion("updated_at is null");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtIsNotNull() {
            addCriterion("updated_at is not null");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtEqualTo(Date value) {
            addCriterion("updated_at =", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtNotEqualTo(Date value) {
            addCriterion("updated_at <>", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtGreaterThan(Date value) {
            addCriterion("updated_at >", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtGreaterThanOrEqualTo(Date value) {
            addCriterion("updated_at >=", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtLessThan(Date value) {
            addCriterion("updated_at <", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtLessThanOrEqualTo(Date value) {
            addCriterion("updated_at <=", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtIn(List<Date> values) {
            addCriterion("updated_at in", values, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtNotIn(List<Date> values) {
            addCriterion("updated_at not in", values, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtBetween(Date value1, Date value2) {
            addCriterion("updated_at between", value1, value2, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtNotBetween(Date value1, Date value2) {
            addCriterion("updated_at not between", value1, value2, "updatedAt");
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