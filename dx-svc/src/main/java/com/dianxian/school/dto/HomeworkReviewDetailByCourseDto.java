package com.dianxian.school.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by y on 2016/8/24.
 */
public class HomeworkReviewDetailByCourseDto {
    private BigDecimal signRatio = BigDecimal.ZERO;
    private List<String> studentsNames;

    public List<String> getStudentsNames() {
        return studentsNames;
    }

    public void setStudentsNames(List<String> studentsNames) {
        this.studentsNames = studentsNames;
    }

    public BigDecimal getSignRatio() {
        return signRatio;
    }

    public void setSignRatio(BigDecimal signRatio) {
        this.signRatio = signRatio;
    }
}
