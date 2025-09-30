package com.visang.aidt.lms.api.keris.utils;

import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class AidtUtils {

    private static final String TYPE_NAME_PREFIX = "class ";
    private static final String key = "-삐상보안!12";

    public static String encryptToHex(String plainText) throws NoSuchAlgorithmException {
        // 입력값 검증
        Objects.requireNonNull(plainText, "plainText는 null일 수 없습니다.");
        Objects.requireNonNull(key, "key는 null일 수 없습니다.");

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String inputText = plainText + key;

            md.update(inputText.getBytes(StandardCharsets.UTF_8));
            byte[] digest = md.digest();

            return bytesToHex2(digest);

    }

    public static String bytesToHex2(byte[] bytes) {
        StringBuilder builder = new StringBuilder();

        for (byte b: bytes) {
            builder.append(String.format("%02x", b));
        }

        return builder.toString();
    }

    static public Class<?> getClass(Type type) throws ClassNotFoundException {
        if (type instanceof Class) {
            return (Class) type;
        } else if (type instanceof ParameterizedType) {
            return getClass(((ParameterizedType) type).getRawType());
        } else if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            Class<?> componentClass = getClass(componentType);
            if (componentClass != null) {
                return Array.newInstance(componentClass, 0).getClass();
            }
        }
        String className = getClassName(type);
        if (className == null || className.isEmpty()) {
            return null;
        }
        return Class.forName(className);
    }

    static public String getClassName(Type type) {
        if (type == null) {
            return "";
        }
        String className = type.toString();
        if (className.startsWith(TYPE_NAME_PREFIX)) {
            className = className.substring(TYPE_NAME_PREFIX.length());
        }
        return className;
    }
}
