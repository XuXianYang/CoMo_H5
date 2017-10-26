package com.dianxian.school.request.teacher;

import com.dianxian.core.utils.json.JsonUtils;
import com.dianxian.school.consts.Comments;
import com.google.gson.reflect.TypeToken;
import io.swagger.annotations.ApiParam;

import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;
import javax.ws.rs.QueryParam;
import java.util.HashMap;

/**
 * Created by xuwenhao on 2016/5/29.
 */
public class SetQuizScoreRequest {
    public static class ScoreMap extends HashMap<Long, Float> {
        public static ScoreMap valueOf(String json) {
            return JsonUtils.fromJson(json, new TypeToken<ScoreMap>(){});
        }
    }
    @ApiParam(value = Comments.QUIZ_ID)
    @FormParam("quizId")
    @NotNull
    private Long quizId;
    @ApiParam(value = Comments.CLASS_ID)
    @FormParam("classId")
    @NotNull
    private Long classId;

    @ApiParam(value = "key是学生id, value是成绩的map")
    @FormParam("studentScores")
    @NotNull
    private ScoreMap studentScores;

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public ScoreMap getStudentScores() {
        return studentScores;
    }

    public void setStudentScores(ScoreMap studentScores) {
        this.studentScores = studentScores;
    }
}
