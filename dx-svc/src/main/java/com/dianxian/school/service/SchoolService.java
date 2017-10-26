package com.dianxian.school.service;

import com.dianxian.core.exception.BizLogicException;
import com.dianxian.core.resource.QueryPaging;
import com.dianxian.core.utils.json.JsonUtils;
import com.dianxian.core.utils.time.DateUtils;
import com.dianxian.school.consts.ServiceCodes;
import com.dianxian.school.dao.SchoolDtoMapper;
import com.dianxian.school.dto.SchoolCategory;
import com.dianxian.school.dto.SchoolDto;
import com.dianxian.school.utils.SchoolUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by xuwenhao on 2016/4/21.
 */
@Component
public class SchoolService {
    private static final int DEFAULT_REGION = 10;
    @Autowired
    SchoolAppProperties schoolAppProperties;
    @Autowired
    SchoolDtoMapper schoolDtoMapper;

    public SchoolDto create(SchoolDto params) {
        SchoolDto record = getByName(params.getName());
        if (null != record) {
            String msg = String.format("School with name '%s' already exists.", params.getName());
            throw new BizLogicException(ServiceCodes.SCHOOL_ALREADY_EXISTS, msg);
        }
        if (!SchoolCategory.JUNIOR.value().equals(params.getCategory())
             && !SchoolCategory.SENIOR.value().equals(params.getCategory())) {
            throw new BizLogicException(ServiceCodes.SCHOOL_INVALID_CATEGORY, "Invalid category.");
        }
        params.setRegionId(DEFAULT_REGION);
        params.setCode(genCode());
        schoolDtoMapper.insert(params);
        return params;
    }

    private String genCode() {
        String code = null;
        for (int i = 0; i < 10; i++) {
            code = SchoolUtils.genSchoolCode();
            int count = schoolDtoMapper.countByCode(code);
            if (0 == count) {
                return code;
            }
        }
        throw new BizLogicException(ServiceCodes.SCHOOL_GEN_CODE_FAIL, "Generate code fail.");
    }

    public SchoolDto getById(Long id) {
        return schoolDtoMapper.selectByPrimaryKey(id);
    }

    public SchoolDto getByName(String name) {
        return schoolDtoMapper.getByName(name);
    }

    public SchoolDto getByCode(String code) {
        return schoolDtoMapper.getByCode(code);
    }

    public List<SchoolDto> getByIds(List<Long> ids) {
        return schoolDtoMapper.getByIds(ids);
    }

    public SchoolDto validateSchoolExists(Long id) {
        SchoolDto schoolDto = getById(id);
        if (null == schoolDto) {
            throw new BizLogicException(ServiceCodes.SCHOOL_NOT_EXISTS, "Invalid school.");
        }
        return schoolDto;
    }

    public void update(Long operatorUserId, SchoolDto params) {
        params.setUpdatedBy(operatorUserId);
        params.setUpdatedAt(new Date());
        schoolDtoMapper.updateByPrimaryKeySelective(params);
    }


    public Integer getCurrentStudyYear(int studyTerm) {
        // TODO 根据term时间计算更精确
        Integer studyYear = Calendar.getInstance().get(Calendar.YEAR);
        if (2 == studyTerm) {
            // 如果是第二学期，则学年上上一年
            studyYear = studyYear - 1;
        }
        return studyYear;
    }

    public Integer getCurrentStudyTerm() {
        return getStudyTerm(Calendar.getInstance());
    }

    public Integer getStudyTerm(Calendar date) {
        Calendar term1Start = Calendar.getInstance();
        term1Start.setTime(schoolAppProperties.getTerm1StartDate().getTime());
        term1Start.set(Calendar.YEAR, date.get(Calendar.YEAR));

        Calendar term2Start = Calendar.getInstance();
        term2Start.setTime(schoolAppProperties.getTerm2StartDate().getTime());
        term2Start.set(Calendar.YEAR, date.get(Calendar.YEAR));

        // 大于第二学期开始
        if (0 >= DateUtils.dateDiff(date, term2Start)
                // 小于第一学期开始
                && 0 < DateUtils.dateDiff(date, term1Start)) {
            return 2;
        } else {
            return 1;
        }
    }

    public Page<SchoolDto> getSchoolList(QueryPaging queryPaging) {
        Page<SchoolDto> page = PageHelper.startPage(queryPaging.getPageNum(), queryPaging.getPageSize());
        schoolDtoMapper.getSchoolList();
        return page;
    }
    
    public List<SchoolDto> getAllSchools() {
        return schoolDtoMapper.getSchoolList();
    }

    /**
     * 加入班级时，允许选择的入学年份
     * @return
     */
    public List<Integer> getAvailableStudyYears() {
        List<Integer> result = Lists.newArrayList();
        Calendar now = Calendar.getInstance();
        int currentYear = now.get(Calendar.YEAR);
        for (int i = 3; i > 0; i--) {
            result.add(currentYear - i);
        }
        result.add(currentYear);
        for (int i = 1; i <= 3; i++) {
            result.add(currentYear + i);
        }
        return result;
    }

}
