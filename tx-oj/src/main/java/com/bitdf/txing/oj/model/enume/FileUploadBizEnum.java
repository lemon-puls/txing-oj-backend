package com.bitdf.txing.oj.model.enume;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;

/**
 * 文件上传业务类型枚举
 *
 * @author Lizhiwei
 * @date 2023/1/24 3:44:13
 * 注释：
 */
public enum FileUploadBizEnum {

    USER_AVATAR("用户头像", "user_avatar"),
    POST_COVER_IMG("文章封面图", "post_cover"),
    POST_CONTENT_IMG("文章内容图", "post_content_img"),
    COMMON_IMG("普通图片", "common");

    private final String text;

    private final String value;

    FileUploadBizEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static FileUploadBizEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (FileUploadBizEnum anEnum : FileUploadBizEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
