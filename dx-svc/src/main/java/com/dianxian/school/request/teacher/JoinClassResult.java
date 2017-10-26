package com.dianxian.school.request.teacher;

import com.dianxian.school.dto.ClassDto;

import java.util.List;
import java.util.Map;

/**
 * Created by xuwenhao on 2016/10/22.
 */
public class JoinClassResult {
    private List<ClassDto> successClasses;
    private Map<Integer, Integer> failureClasses;

    public List<ClassDto> getSuccessClasses() {
        return successClasses;
    }

    public void setSuccessClasses(List<ClassDto> successClasses) {
        this.successClasses = successClasses;
    }

    public Map<Integer, Integer> getFailureClasses() {
        return failureClasses;
    }

    public void setFailureClasses(Map<Integer, Integer> failureClasses) {
        this.failureClasses = failureClasses;
    }
}
