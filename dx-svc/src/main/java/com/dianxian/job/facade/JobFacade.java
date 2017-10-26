package com.dianxian.job.facade;

import com.dianxian.job.JobProperties;
import com.dianxian.school.dto.MessageType;
import com.dianxian.school.dto.SosMessageDto;
import com.dianxian.school.facade.StudentFacade;
import com.dianxian.school.request.CreateSosMessageRequest;
import com.dianxian.school.service.MessageService;
import com.dianxian.school.service.StudentService;
import com.dianxian.user.dto.StudentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by y on 2016/8/31.
 */
@Component
public class JobFacade {
    @Autowired
    StudentService studentService;
    @Autowired
    MessageService messageService;
    @Autowired
    StudentFacade studentFacade;
    @Autowired
    JobProperties jobProperties;
    public void sosMsgWarning() {
        List<StudentDto> studentsUnConfigToday = studentService.findUnConfigExceptedHomeTimeStu();
        List<StudentDto> studentsTimeout = studentService.findStudentTimeout();
        CreateSosMessageRequest request = new CreateSosMessageRequest();
        request.setContent(jobProperties.getWarnMessageContent());

        if(isAfterSystemWarningTime()){
            for (StudentDto studentDto : studentsUnConfigToday) {
                List<SosMessageDto> sosMessageDtos = messageService.findSafeAndWarningMsg(studentDto.getId());
                if (sosMessageDtos != null && sosMessageDtos.size() > 0) {
                    continue;
                }
                studentFacade.sendSafeAssistantMessage(studentDto, request, MessageType.SYS_WARNING);
            }
        }
        // TODO 所有已经设置了预计时间的学生，每次都会被扫描一遍，会有性能问题
        for(StudentDto studentDto : studentsTimeout){
            List<SosMessageDto> sosMessageDtos = messageService.findSafeAndWarningMsg(studentDto.getId());
            if (sosMessageDtos != null && sosMessageDtos.size() > 0) {
                continue;
            }
            studentFacade.sendSafeAssistantMessage(studentDto, request, MessageType.SYS_WARNING);
        }
    }
    private boolean isAfterSystemWarningTime(){
        boolean res = false;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, jobProperties.getSystemSosWarningTimeHour());
        cal.set(Calendar.MINUTE, jobProperties.getSystemSosWarningTimeMinutes());
        res = new Date().getTime() >= cal.getTime().getTime();
        cal.set(Calendar.HOUR, jobProperties.getSystemSosWarningTimeHour() + jobProperties.getWarningTimeJobExecuteBufferHour());
        res = res && new Date().getTime() <= cal.getTime().getTime();
        return res;
    }


}
