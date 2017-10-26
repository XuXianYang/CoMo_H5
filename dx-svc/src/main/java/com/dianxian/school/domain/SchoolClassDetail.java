package com.dianxian.school.domain;

/**
 * 班级的详细信息。包含基本信息，学校信息，班主任
 * Created by XuWenHao on 11/2/2016.
 */
public class SchoolClassDetail {
    private SchoolClassBasicInfo basicInfo;
    private SimpleTeacher headTeacher;
    private SchoolInfo schoolInfo;

    public SchoolClassBasicInfo getBasicInfo() {
        return basicInfo;
    }

    public void setBasicInfo(SchoolClassBasicInfo basicInfo) {
        this.basicInfo = basicInfo;
    }

    public SimpleTeacher getHeadTeacher() {
        return headTeacher;
    }

    public void setHeadTeacher(SimpleTeacher headTeacher) {
        this.headTeacher = headTeacher;
    }

    public SchoolInfo getSchoolInfo() {
        return schoolInfo;
    }

    public void setSchoolInfo(SchoolInfo schoolInfo) {
        this.schoolInfo = schoolInfo;
    }
}
