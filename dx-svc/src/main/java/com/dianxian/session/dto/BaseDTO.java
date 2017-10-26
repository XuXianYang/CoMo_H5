package com.dianxian.session.dto;


import com.dianxian.session.utils.BeanUtils;

import java.io.Serializable;

public abstract class BaseDTO implements Serializable {
    @Override
    public String toString() {
        return BeanUtils.bean2map(this).toString();
    }
}
