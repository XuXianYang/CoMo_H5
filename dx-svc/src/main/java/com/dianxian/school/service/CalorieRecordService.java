package com.dianxian.school.service;

import com.beust.jcommander.internal.Maps;
import com.dianxian.core.exception.BizLogicException;
import com.dianxian.school.consts.ServiceCodes;
import com.dianxian.school.dao.CalorieItemsMapper;
import com.dianxian.school.dao.CalorieRecordMapper;
import com.dianxian.school.domain.CalorieItems;
import com.dianxian.school.domain.CalorieItemsExample;
import com.dianxian.school.domain.CalorieRecord;
import com.dianxian.school.dto.CalorieRecordInfoDto;
import com.dianxian.user.domain.User;
import com.dianxian.user.service.UserService;
import com.dianxian.web.utils.DisplayUtils;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by chenzhong on 2016/11/18.
 */
@Service
public class CalorieRecordService {
    @Autowired
    private CalorieRecordMapper calorieRecordMapper;
    @Autowired
    private CalorieItemsMapper calorieItemsMapper;
    @Autowired
    private UserService userService;


    /**
     * 保存消卡记录
     * chenzhong
     *
     * @param userId
     * @param bean
     */
    public void saveRecord(Long userId, CalorieRecord bean) {

        CalorieItems calorieItems = calorieItemsMapper.selectByPrimaryKey(bean.getItemType());
        int totalCal = calorieItems.getCalorieMin() * bean.getMin();/*实际消卡数*/
        //前台提交消卡数为前台自动计算，提交后验证总消卡是否一致 ±2
        if (bean.getTotalCalorie() < totalCal - 2 || bean.getTotalCalorie() > totalCal + 2) {
            throw new BizLogicException(ServiceCodes.CALORIE_TOTAL_COMMIT_ERROR, "CALORIE_TOTAL_COMMIT_ERROR");
        }

        CalorieRecord record = new CalorieRecord();
        record.setUserId(userId);
        record.setTotalCalorie(totalCal);
        record.setItemType(bean.getItemType());
        record.setMin(bean.getMin());
        record.setCreatedAt(Calendar.getInstance().getTime());
        calorieRecordMapper.insert(record);
    }

    /**
     * 获取所有的消卡项目
     * chenzhong
     *
     * @return
     */
    public List<CalorieItems> findAllCalorieItems() {
        CalorieItemsExample ex = new CalorieItemsExample();
        ex.setOrderByClause("id desc");
        return calorieItemsMapper.selectByExample(ex);
    }

    /**
     * 获取消卡排名
     * chenzhong
     *
     * @return
     */
    public List<CalorieRecordInfoDto> findRanking(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        instance.set(Calendar.DAY_OF_MONTH, 1);
        String strStartTime = sdf.format(instance.getTime());
        instance.add(Calendar.MONTH, 1);
        String strEndTime = sdf.format(instance.getTime());

        List<CalorieRecordInfoDto> dtos = calorieRecordMapper.findRanking(strStartTime, strEndTime);
        if (CollectionUtils.isEmpty(dtos)) return Lists.newArrayList();

        List<Long> userIds = Lists.newLinkedList();
        for (CalorieRecordInfoDto dto : dtos) {
            userIds.add(dto.getUserId());
        }
        Map<Long, User> userMap = userService.getUsersByIdsToMap(userIds);
        for (CalorieRecordInfoDto dto : dtos) {
            if (CollectionUtils.isEmpty(userMap)) {
                dto.setUser(userMap.get(dto.getUserId()));
            }
        }
        return dtos;
    }

    /**
     * 获取我一个月的所有的消卡记录
     * chenzhong
     *
     * @param date
     * @param userId
     * @return
     */
    public List<CalorieRecordInfoDto> findCalorieRecordList(Long userId, Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        instance.set(Calendar.DAY_OF_MONTH, 1);
        String strStartTime = sdf.format(instance.getTime());
        instance.add(Calendar.MONTH, 1);
        String strEndTime = sdf.format(instance.getTime());

        Map<String, Object> conditions = Maps.newHashMap();
        conditions.put("startTime", strStartTime);
        conditions.put("endTime", strEndTime);
        conditions.put("userId", userId);

        List<CalorieRecordInfoDto> list = calorieRecordMapper.findCalorieRecordList(conditions);
        if (CollectionUtils.isEmpty(list)) return Lists.newArrayList();

        for (CalorieRecordInfoDto dto : list) {
            dto.setStrCreatedAt(DisplayUtils.formatDate(dto.getCreatedAt()));
        }
        return list;
    }
}
