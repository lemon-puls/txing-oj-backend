package com.bitdf.txing.oj.exception;

import com.bitdf.txing.oj.common.ErrorCode;

/**
 * 自定义异常类
 *
 * @author Lizhiwei
 * @date 2023/1/24 3:44:13
 * 注释：
 */
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    public int getCode() {
        return code;
    }
}
