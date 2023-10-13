package com.muller.todolist.util;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.*;

public class Utils {

    public static void copyNonNullProperties(Object source, Object target) {
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }
    private static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] properties = src.getPropertyDescriptors();
        Set<String> emptyNames = new HashSet<>();
        Arrays.stream(properties).forEach(propertyDescriptor -> {
            var value = src.getPropertyValue(propertyDescriptor.getName());
            if (Objects.isNull(value)) {
                emptyNames.add(propertyDescriptor.getName());
            }
        });
        return emptyNames.toArray(new String[0]);
    }
}
