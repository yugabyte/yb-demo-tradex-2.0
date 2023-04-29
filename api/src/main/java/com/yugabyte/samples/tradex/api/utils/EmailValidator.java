package com.yugabyte.samples.tradex.api.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class EmailValidator {

    public static final String OWASP_EMAIL_REGEX = """
            ^[a-zA-Z0-9_+&*-] + (?:\\\\.[a-zA-Z0-9_+&*-] + )*@(?:[a-zA-Z0-9-]+\\\\.) + [a-zA-Z]{2, 7}
            """;

    public static final String SIMPLE_EMAIL_PATTERN = "^(.+)@(\\S+)$";

    private static boolean patternMatches(String emailAddress, String regexPattern) {
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }

    public static boolean isValidEmail(String inputEmail) {
        if (StringUtils.isEmpty(inputEmail)) {
            throw new IllegalArgumentException("email cannot be empty or null");
        }

        return patternMatches(inputEmail, SIMPLE_EMAIL_PATTERN);
    }

}
