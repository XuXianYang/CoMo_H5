package com.dianxian.core.utils.lang;

import com.google.common.collect.Maps;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by XuWenHao on 6/7/2016.
 */
public class MapUtils {
    /**
     * List -> Map. 不会返回null.
     * @param list
     * @param <I>
     * @param <T>
     * @return
     */
    public static <I, T extends Identified<I>> Map<I, T> toMap(List<T> list) {
        Map<I, T> result = Maps.newHashMap();
        if (!CollectionUtils.isEmpty(list)) {
            for (T item : list) {
                result.put(item.getId(), item);
            }
        }

        return result;
    }
}
