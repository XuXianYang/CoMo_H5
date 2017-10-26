package com.dianxian.school.request;

import com.dianxian.core.utils.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by XuWenHao on 10/21/2016.
 */
public class IntegerList extends ArrayList<Integer> {
    public IntegerList(Collection<? extends Integer> c) {
        super(c);
    }

    public static IntegerList valueOf(String value) {
        return new IntegerList(StringUtils.toIntegerList(value, ","));
    }
}
