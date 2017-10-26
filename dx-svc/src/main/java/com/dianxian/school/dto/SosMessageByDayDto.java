package com.dianxian.school.dto;

import java.util.List;

/**
 * Created by y on 2016/8/2.
 */
public class SosMessageByDayDto {
    private Long bizTimestamp;
    private List<SosMessageDto> sosMessageDtoList;
    private long dayDistance;

    public long getDayDistance() {
        return dayDistance;
    }

    public void setDayDistance(long dayDistance) {
        this.dayDistance = dayDistance;
    }

    public Long getBizTimestamp() {
        return bizTimestamp;
    }

    public void setBizTimestamp(Long bizTimestamp) {
        this.bizTimestamp = bizTimestamp;
    }

    public List<SosMessageDto> getSosMessageDto() {
        return sosMessageDtoList;
    }

    public void setSosMessageDtoList( List<SosMessageDto> sosMessageDtoList) {
        this.sosMessageDtoList = sosMessageDtoList;
    }
}
