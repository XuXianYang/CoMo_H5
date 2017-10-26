package com.dianxian.school.dto;

import com.dianxian.school.domain.Discuss;
import com.dianxian.school.domain.PlaygroundAct;
import com.dianxian.school.domain.Zan;
import com.dianxian.user.domain.User;

import java.util.List;

/**
 * Created by chenzhong on 2016/11/7.
 */
public class PlaygroundActDto extends PlaygroundAct {

    private List<String> imgs;/*用户图片集合*/

    private int zanCount;/*赞数量*/

    private int discussCount;/*评论的数量*/

    private List<Discuss> discusses;/*评论详情*/

    private List<Zan> zans;/*赞详情*/

    private String strCreateAt;/*字符串创建活动时间*/

    private User userDto;/*创建者的数据*/

    public List<Zan> getZans() {
        return zans;
    }

    public void setZans(List<Zan> zans) {
        this.zans = zans;
    }

    public User getUserDto() {
        return userDto;
    }

    public void setUserDto(User userDto) {
        this.userDto = userDto;
    }

    public String getStrCreateAt() {
        return strCreateAt;
    }

    public void setStrCreateAt(String strCreateAt) {
        this.strCreateAt = strCreateAt;
    }

    public List<Discuss> getDiscusses() {
        return discusses;
    }

    public void setDiscusses(List<Discuss> discusses) {
        this.discusses = discusses;
    }


    public List<String> getImgs() {
        return imgs;
    }

    public void setImgs(List<String> imgs) {
        this.imgs = imgs;
    }

    public int getZanCount() {
        return zanCount;
    }

    public void setZanCount(int zanCount) {
        this.zanCount = zanCount;
    }

    public int getDiscussCount() {
        return discussCount;
    }

    public void setDiscussCount(int discussCount) {
        this.discussCount = discussCount;
    }
}
