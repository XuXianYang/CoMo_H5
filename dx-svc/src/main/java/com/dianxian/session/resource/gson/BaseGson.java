package com.dianxian.session.resource.gson;

import com.dianxian.session.utils.BeanUtils;
import com.google.gson.Gson;

import java.io.Serializable;

public class BaseGson implements Serializable {
    @Override
    public String toString() {
        return BeanUtils.bean2map(this).toString();
    }

    private static Gson gson = new Gson();

    public String toGsonStr() {
        return gson.toJson(this);
    }
}


