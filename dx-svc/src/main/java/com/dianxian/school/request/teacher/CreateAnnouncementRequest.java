package com.dianxian.school.request.teacher;

import com.dianxian.school.consts.Comments;
import com.dianxian.school.consts.SchoolConstants;
import com.dianxian.school.dto.AnnouncementType;
import io.swagger.annotations.ApiParam;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;

/**
 * Created by XuWenHao on 5/26/2016.
 */
public class CreateAnnouncementRequest {
    public static class GradeRequest {
        @FormParam("enrolYear")
        @NotNull
        @Min(0)
        public Integer enrolYear;
    }
    public static class ClassRequest {
        @FormParam("classId")
        @NotNull
        public Long classId;
    }

    @ApiParam(value = "公告类型", allowableValues = AnnouncementType.SCHOOL + "," + AnnouncementType.GRADE + "," + AnnouncementType.CLASS)
    @FormParam("type")
    @NotNull
    @Min(value = AnnouncementType.SCHOOL)
    @Max(value = AnnouncementType.CLASS)
    private Integer type;

    @ApiParam(value = "年级")
    @FormParam("enrolYear")
    private Integer enrolYear;

    @ApiParam(value = Comments.CLASS_ID)
    @FormParam("classId")
    private Long classId;

    @ApiParam(value = Comments.ANNOUNCEMENT_TITLE)
    @FormParam("title")
    @NotNull
    @NotBlank
    @Length(max = SchoolConstants.ANNOUNCEMENT_TITLE_MAX_LEGNTH)
    private String title;

    @ApiParam(value = "公告内容")
    @FormParam("content")
    @NotNull
    @NotBlank
    private String content;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getEnrolYear() {
        return enrolYear;
    }

    public void setEnrolYear(Integer enrolYear) {
        this.enrolYear = enrolYear;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public GradeRequest getGradeRequest() {
        GradeRequest info = new GradeRequest();
        info.enrolYear = this.enrolYear;
        return info;
    }

    public ClassRequest getClassRequest() {
        ClassRequest info = new ClassRequest();
        info.classId = this.classId;
        return info;
    }
}
