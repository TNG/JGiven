package com.tngtech.jgiven.impl.util;

import com.tngtech.jgiven.impl.ByteBuddyStageClassCreator;

import java.lang.reflect.Method;

public class SingleStageNameFieldGetterSetter {
    public static String getStageName(Object stage) {
        return (String) ReflectionUtil.getFieldValueOrNull(ByteBuddyStageClassCreator.STAGE_NAME_FIELD_NAME,
                stage, "stageName from stage object");
    }

    public static void setStageName(Object stage, String newStageName) {
        ReflectionUtil.setField(ByteBuddyStageClassCreator.STAGE_NAME_FIELD_NAME,
                stage, newStageName, "stageName from stage object");
    }

    public static void setStageName(Object stage, Method method) {
        setStageName(stage, getMethodName(method));
    }

    private static String getMethodName(Method method) {
        String methodName = method.getName();
        if (methodName.equals("given") || method.equals("when") || method.equals("then")) {
            return methodName.toUpperCase();
        }
        return null;
    }
}
