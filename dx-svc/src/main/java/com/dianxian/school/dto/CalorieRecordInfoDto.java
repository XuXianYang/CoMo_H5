package com.dianxian.school.dto;

import com.dianxian.school.domain.CalorieRecord;
import com.dianxian.user.domain.User;

/**
 * Created by chenzhong on 2016/11/18.
 */
public class CalorieRecordInfoDto extends CalorieRecord {

    private String itemName;/*消卡项目名称*/

    private Integer calorieMin;/*每分钟消卡量*/

    private String description;/*消卡项目描述*/

    private User user;/*排名人的信息*/

    private String strCreatedAt;

    public String getStrCreatedAt() {
        return strCreatedAt;
    }

    public void setStrCreatedAt(String strCreatedAt) {
        this.strCreatedAt = strCreatedAt;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getCalorieMin() {
        return calorieMin;
    }

    public void setCalorieMin(Integer calorieMin) {
        this.calorieMin = calorieMin;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
