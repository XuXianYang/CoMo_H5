package com.dianxian.user.domain;

import java.util.List;

/**
 * Created by XuWenHao on 5/23/2016.
 */
public class Role {
    private Integer id;
    private String name;
    private List<Integer> permissions;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Integer> permissions) {
        this.permissions = permissions;
    }
}
