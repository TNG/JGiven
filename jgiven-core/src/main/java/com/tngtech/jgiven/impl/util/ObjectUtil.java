package com.tngtech.jgiven.impl.util;

public class ObjectUtil {

    public static boolean equals(Object o1, Object o2) {
        return o1 == o2 || o1 != null && o1.equals(o2);
    }
}
