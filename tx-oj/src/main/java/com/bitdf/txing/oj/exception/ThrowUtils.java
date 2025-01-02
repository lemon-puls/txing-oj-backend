package com.bitdf.txing.oj.exception;

import com.bitdf.txing.oj.model.enume.TxCodeEnume;

/**
 * 抛异常工具类
 *
 * @author Lizhiwei
 * @date 2023/1/24 3:44:13
 * 注释：
 */
public class ThrowUtils {

    /**
     * 条件成立则抛异常
     *
     * @param condition
     * @param runtimeException
     */
    public static void throwIf(boolean condition, RuntimeException runtimeException) {
        if (condition) {
            throw runtimeException;
        }
    }

    /**
     * 条件成立则抛异常
     *
     * @param condition
     * @param errorCode
     */
    public static void throwIf(boolean condition, TxCodeEnume errorCode) {
        throwIf(condition, new BusinessException(errorCode));
    }

    /**
     * 条件成立则抛异常
     *
     * @param condition
     * @param errorCode
     * @param message
     */
    public static void throwIf(boolean condition, TxCodeEnume errorCode, String message) {
        throwIf(condition, new BusinessException(errorCode, message));
    }

    /**
     * 条件成立则抛异常
     *
     * @param condition
     * @param message
     */
    public static void throwIf(boolean condition, String message) {
        throwIf(condition, new BusinessException(TxCodeEnume.COMMON_CUSTOM_EXCEPTION.getCode(), message));
    }
}
