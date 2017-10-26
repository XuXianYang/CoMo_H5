package com.dianxian.school.domain;

/**
 * Created by XuWenHao on 8/23/2016.
 *
 * @deprecated 和SimpleSchoolClass类似，在使用范围没有扩大时，不要再使用这个类
 * @see {@link com.dianxian.school.domain.SimpleSchoolClass}
 */
@Deprecated
public class ClassInfo {
    private Long classId;
    private String className;

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
