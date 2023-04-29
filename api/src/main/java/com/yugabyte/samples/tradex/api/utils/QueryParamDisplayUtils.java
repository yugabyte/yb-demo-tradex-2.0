package com.yugabyte.samples.tradex.api.utils;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.util.Arrays;
import java.util.Map;

public class QueryParamDisplayUtils {

    public static String getParameters(Map<String, Object> params) {
        if (null == params || params.isEmpty()) {
            return "";
        }

        StringBuilder paramsAsString = new StringBuilder("Parameters: { ");
        params.entrySet().forEach(entry -> {
            String key = entry.getKey();
            if (key.contains("passwd") || entry.getKey().contains("securitypin")) {
                paramsAsString.append(key).append("=*******");
            } else {
                paramsAsString.append(entry.getKey() + "=" + getFormattedValue(entry.getValue()));
            }
            paramsAsString.append(" ");
        });
        return paramsAsString.append("} ").toString();
    }

    public static String getParameters(MapSqlParameterSource params) {
        if (null == params || params.getParameterNames().length == 0) {
            return "";
        }

        StringBuilder paramsAsString = new StringBuilder("Parameters: { ");
        Arrays.stream(params.getParameterNames()).forEach(entry -> {
            if (entry.contains("passwd") || entry.contains("securitypin")) {
                paramsAsString.append(entry).append("=*******");
            } else {
                paramsAsString.append(entry + "=" + getFormattedValue(params.getValue(entry)));
            }
            paramsAsString.append(" ");
        });
        return paramsAsString.append("} ").toString();
    }


    private static String getFormattedValue(Object input) {
        if (null == input) {
            return "";
        }

        if (input instanceof Integer[]) {
            return Arrays.toString((Integer[]) input);
        }

        return input.toString();
    }
}


