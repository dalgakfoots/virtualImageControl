package onthelive.kr.virtualImageControl.common;

import org.apache.commons.lang3.RandomStringUtils;

import java.security.SecureRandom;

public class RandomStringUtil {
    static int start = 0;
    static int end = 0;

    public static String randomAlphaNumeric(int count){
        return RandomStringUtils.random(
                count,
                start,
                end,
                true,
                true,
                null,
                new SecureRandom()
        );
    }
}

