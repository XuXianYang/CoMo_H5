package com.dianxian.school.service;

import com.dianxian.core.exception.BizLogicException;
import com.dianxian.school.consts.ServiceCodes;
import com.dianxian.school.dao.MarkMapper;
import com.dianxian.school.domain.Mark;
import com.dianxian.school.domain.MarkExample;
import com.dianxian.user.dto.StudentDto;
import com.dianxian.web.utils.DisplayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by chenzhong on 2016/11/15.
 */
@Service
public class MarkService {
    @Autowired
    private MarkMapper markMapper;


    /**
     * 学生签到
     * chenzhong
     *
     * @param user
     */
    public void saveMark(StudentDto user) {
        MarkExample ex = new MarkExample();
        ex.createCriteria().andUserIdEqualTo(user.getUserId()).andMarkAtEqualTo(Calendar.getInstance().getTime());
        List<Mark> marks = markMapper.selectByExample(ex);
        if (!CollectionUtils.isEmpty(marks)) throw new BizLogicException(ServiceCodes.CALORIE_MARK_RESUBMIT, "CALORIE_MARK_RESUBMIT");

        Mark mark = new Mark();
        mark.setUserId(user.getUserId());
        mark.setMarkAt(new Date());
        markMapper.insert(mark);
    }


    /**
     * 获取当前学生的签到信息
     * chenzhong
     *
     * @param user
     * @param date
     * @return
     */
    public List<Mark> findMarkRecord(StudentDto user, Date date) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(date==null?new Date():date);
        instance.set(Calendar.DAY_OF_MONTH, 1);/*日期的第一天*/
        Date startTime = instance.getTime();/*开始时间*/
        instance.add(Calendar.MONTH, 1);/*+1月*/
        Date endTime = instance.getTime();

        MarkExample ex = new MarkExample();
        ex.setOrderByClause("mark_at desc");
        ex.createCriteria()
                .andMarkAtGreaterThanOrEqualTo(startTime)
                .andMarkAtLessThan(endTime)
                .andUserIdEqualTo(user.getUserId());

        List<Mark> marks = markMapper.selectByExample(ex);/*本月签到日期集合*/
        if (CollectionUtils.isEmpty(marks)) return null;
        for (Mark mark : marks) {
            mark.setStrMarkAt(DisplayUtils.formatDate(mark.getMarkAt()));
        }

        return marks;
    }
}