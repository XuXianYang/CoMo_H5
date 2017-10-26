package com.dianxian.school.service;

import com.dianxian.core.resource.QueryPaging;
import com.dianxian.school.dao.SneakingMessageDtoMapper;
import com.dianxian.school.dao.SosMessageDtoMapper;
import com.dianxian.school.dto.*;
import com.dianxian.user.dto.StudentDto;
import com.dianxian.user.dto.UserDto;
import com.dianxian.user.dto.UserInfoDto;
import com.dianxian.user.service.UserService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by XuWenHao on 6/15/2016.
 */
@Component
public class MessageService {
    @Autowired
    SneakingMessageDtoMapper sneakingMessageDtoMapper;
    @Autowired
    SosMessageDtoMapper sosMessageDtoMapper;
    @Autowired
    StudentService studentService;
    @Autowired
    UserService userService;

    public SneakingMessageDto sendSneakingMessage(Long operatorId, SneakingMessageDto params) {
        params.setCreatedBy(operatorId);
        sneakingMessageDtoMapper.insert(params);
        return sneakingMessageDtoMapper.selectByPrimaryKey(params.getId());
    }

    public Page<SneakingMessageDto> getSneakingMessagesWithContent(Long teacherId, QueryPaging queryPaging) {
        Page<SneakingMessageDto> page = PageHelper.startPage(queryPaging.getPageNum(), queryPaging.getPageSize());
        SneakingMessageDtoExample example = new SneakingMessageDtoExample();
        example.createCriteria().andTeacherIdEqualTo(teacherId);
        example.setOrderByClause("id desc");
        sneakingMessageDtoMapper.selectByExampleWithBLOBs(example);
        return page;
    }

    public boolean hasNewSneakingMessages(Long teacherId, Date lastTime) {
        SneakingMessageDtoExample example = new SneakingMessageDtoExample();
        SneakingMessageDtoExample.Criteria criteria = example.createCriteria();
        criteria.andTeacherIdEqualTo(teacherId);
        if (null != lastTime) {
            criteria.andCreatedAtGreaterThan(lastTime);
        }
        example.setOrderByClause("id desc limit 0,1");

        List<SneakingMessageDto> result = sneakingMessageDtoMapper.selectByExample(example);
        return !CollectionUtils.isEmpty(result);
    }

    public SosMessageDto sendSosMessage(Long operatorId, SosMessageDto params) {
        params.setCreatedBy(operatorId);
        sosMessageDtoMapper.insertSelective(params);
        return sosMessageDtoMapper.selectByPrimaryKey(params.getId());
    }
    public Page<SosMessageByDayDto> getSosMessagesOfStudent(Long studentUserId, QueryPaging queryPaging) {
        Page<SosMessageByDayDto> page = PageHelper.startPage(queryPaging.getPageNum(), queryPaging.getPageSize());
        SosMessageDtoExample example = new SosMessageDtoExample();

        example.createCriteria().andStudentIdEqualTo(studentUserId);
        example.setOrderByClause("biz_timestamp desc");
        sosMessageDtoMapper.selectWithDateByExample(example);
        List<SosMessageByDayDto> sosMessageByDayDtoList = page.getResult();
        setSosDetail(sosMessageByDayDtoList);
        return page;
    }

    public Page<SosMessageDto> getStudentMessageList(Long teacherId, Long classId, QueryPaging queryPaging) {
        Page<SosMessageDto> page = PageHelper.startPage(queryPaging.getPageNum(), queryPaging.getPageSize());
        page.setOrderBy("created_at desc");
        sosMessageDtoMapper.getStudentMessageList(classId);
        List<SosMessageDto> sosMessageDtos = page.getResult();
        for(SosMessageDto sosMessageDto : sosMessageDtos){
            StudentDto studentDto = userService.getStudentById(sosMessageDto.getStudentId());
            UserInfoDto userInfoDto = userService.getUserInfoById(studentDto.getUserId());
            sosMessageDto.setStudentName(userInfoDto.getName()== null ? "":userInfoDto.getName());
        }
        return page;
    }

    private void setSosDetail(List<SosMessageByDayDto> sosMessageByDayDtoList ){
        for(SosMessageByDayDto sosByDay: sosMessageByDayDtoList){
            Long bizTime = sosByDay.getBizTimestamp();
            sosByDay.setSosMessageDtoList(sosMessageDtoMapper.selectByBizTime(bizTime));
            sosByDay.setDayDistance(getDistanceDays(bizTime));
        }
    }
    private long getDistanceDays(long bizTimeStamp){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd 0:0:0");//设置日期格式
        String msg =df.format(new Date());
        Date date;
        long days = 0;
        try {
            date = df.parse(msg);
            long currentDayTimeStamp = date.getTime();
            long diff = currentDayTimeStamp - bizTimeStamp;
            days = diff / (1000 * 60 * 60 * 24);
        }catch (Exception e){
            //
        }
        return days;
    }

    public List<SosMessageDto> findSafeAndWarningMsg (Long studentId){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        List<SosMessageDto> sosMessageDtos = sosMessageDtoMapper.findSafeAndWarningMsg(studentId, cal.getTime());
        return sosMessageDtos;
    }


}
