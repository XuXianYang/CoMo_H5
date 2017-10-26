package com.dianxian.school.domain;

import com.dianxian.user.domain.User;
import com.dianxian.user.dto.UserDto;

public class Zan {
    private Long id;

    private Long userId;

    private Long actId;

    private User userDto;/*创建者的数据*/

    public User getUserDto() {
        return userDto;
    }

    public void setUserDto(User userDto) {
        this.userDto = userDto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getActId() {
        return actId;
    }

    public void setActId(Long actId) {
        this.actId = actId;
    }
}