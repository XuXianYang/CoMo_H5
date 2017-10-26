package com.dianxian.school.request;

import com.dianxian.core.utils.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by XuWenHao on 10/21/2016.
 */
public class LongList extends ArrayList<Long> {
    public LongList(Collection<? extends Long> c) {
        super(c);
    }

    public static LongList valueOf(String value) {
        return new LongList(StringUtils.toLongList(value, ","));
    }
}
