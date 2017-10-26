package com.dianxian.school.dto;

/**
 * Created by xuwenhao on 2016/5/8.
 */
public enum QuizCategory {
    FinalExam(1),
    MidTerm(2),
    Monthly(3);

    public static final int FINAL_EXAM = 1;
    public static final int MID_TERM = 2;
    public static final int MONTHLY = 3;

    private Integer value = 0;
    private QuizCategory(Integer value) {
        this.value = value;
    }

    public Integer value() {
        return value;
    }

    public static QuizCategory valueOf(Integer value) {
        for (QuizCategory category : QuizCategory.values()) {
            if (category.value().equals(value)) {
                return category;
            }
        }
        return null;
    }
}
