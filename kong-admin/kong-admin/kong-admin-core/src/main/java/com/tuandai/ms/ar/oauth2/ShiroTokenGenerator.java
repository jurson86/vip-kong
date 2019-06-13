package com.tuandai.ms.ar.oauth2;

import com.tuandai.ms.ar.exception.BaseRuntimeException;
import com.tuandai.ms.ar.exception.TdExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.util.UUID;

/**
 * 生成token
 *
 *
 *
 * @date 2017-05-20 14:41
 */
public class ShiroTokenGenerator {

    private static Logger logger = LoggerFactory.getLogger(TdExceptionHandler.class);

    public static String generateValue() {
        return generateValue(UUID.randomUUID().toString());
    }

    private static final char[] hexCode = "0123456789abcdef".toCharArray();

    public static String toHexString(byte[] data) {
        if(data == null) {
            return null;
        }
        StringBuilder r = new StringBuilder(data.length*2);
        for ( byte b : data) {
            r.append(hexCode[(b >> 4) & 0xF]);
            r.append(hexCode[(b & 0xF)]);
        }
        return r.toString();
    }

    public static String generateValue(String param) {
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(param.getBytes());
            byte[] messageDigest = algorithm.digest();
            return toHexString(messageDigest);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            throw new BaseRuntimeException("生成Token失败");
        }
    }
}
