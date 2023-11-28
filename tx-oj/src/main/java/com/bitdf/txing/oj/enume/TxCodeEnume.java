package com.bitdf.txing.oj.enume;

/***
 * 错误码和错误信息定义类
 * 1. 错误码定义规则为5为数字
 * 2. 前两位表示业务场景，最后三位表示错误码。例如：10001。10:通用 001:系统未知异常
 * 3. 维护错误码后需要维护错误描述，将他们定义为枚举形式
 * 错误码列表：
 *  10: 通用
 *      001：参数格式校验
 *
 */
public enum TxCodeEnume {
    /**
     * 10  公共
     */
    COMMON_REQUEST_SUCCESS(200, "SUCCESS"),
    COMMON_TARGET_NOT_EXIST_EXCEPTION(404, "操作对象不存在"),
    COMMON_SYSTEM_UNKNOWN_EXCEPTION(500, "系统异常"),
    COMMON_REQUEST_METHOD_EXCEPTION(10000, "请求方法异常"),
    COMMON_SUBMIT_DATA_EXCEPTION(10001, "包含非法数据异常"),
    COMMON_DATABASE_RECORD_EXIST_EXCEPTION(10002, "数据库中已存在该记录"),
    COMMON_SMS_CODE_EXCEPTION(10003, "验证码获取频率太高，稍后再试"),
    COMMON_OPS_FAILURE_EXCEPTION(10004, "操作失败异常"),
    COMMON_NOT_PERM_EXCEPTION(10005, "无权限异常"),
    COMMON_NOT_LOGIN_EXCEPTION(10005, "未登录异常"),
    COMMON_FORBIDDEN_EXCEPTION(10006, "禁止异常"),

    CONDITION_NOT_VALID_EXCEPTION(10009, "不满足相关条件，该操作不予以执行"),

    /**
     * 11 判题相关
     */
    JUDGE_SUMBIT_STATUS_MODIFY_EXCEPTION(11001, "题目状态修改异常"),
    JUDGE_SUMBIT_INFO_MODIFY_EXCEPTION(11001, "题目信息修改异常"),

    TO_MANY_REQUEST_EXCEPTION(10010, "请求流量过大，请稍后再试！"),

    /**
     * 12用户相关
     */
    USER_PWD_INCONSISTENT_EXCEPTION(12001, "两次输入密码不一致"),

    USER_PWD_ERROR_EXCEPTION(12002,"密码错误");

    private int code;
    private String msg;

    TxCodeEnume(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }


    public static String getMsgByCode(int code) {
        TxCodeEnume[] enums = values();
        for (TxCodeEnume enume : enums) {
            if (enume.getCode() == code) {
                return enume.getMsg();
            }
        }
        return null;
    }
}
