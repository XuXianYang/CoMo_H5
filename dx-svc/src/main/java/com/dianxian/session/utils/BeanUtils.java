package com.dianxian.session.utils;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BeanUtils {
    public static Map<String, Object> bean2map(Object bean) {
        return bean2map(bean, new HashSet());
    }

    private static Map<String, Object> bean2map(Object bean, Set hashSet) {
        if (EmptyChecker.isEmpty(bean) || hashSet.contains(bean)) {
            return null;
        }

        hashSet.add(bean);

        Map<String, Object> map = new HashMap<String, Object>();
        BeanWrapper bw = new BeanWrapperImpl(bean);

        for (PropertyDescriptor prop : bw.getPropertyDescriptors()) {
            String name = prop.getName();

            if (!bw.isReadableProperty(name)) {
                continue;
            }

            Object value = bw.getPropertyValue(name);
            if (value == null) {
                map.put(name, value);
                continue;
            }

            if (value.getClass().getName().startsWith("java") || Enum.class.isAssignableFrom(value.getClass())) {
                map.put(name, value);
                continue;
            }

            map.put(name, bean2map(value, hashSet));
        }

        hashSet.remove(bean);
        return map;
    }
}