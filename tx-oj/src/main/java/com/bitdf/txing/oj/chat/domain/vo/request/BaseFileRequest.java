package com.bitdf.txing.oj.chat.domain.vo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Lizhiwei
 * @date 2024/1/24 15:08:49
 * 注释：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseFileRequest implements Serializable {
    /**
     * 大小 单位：Byte
     */
    private Long size;
    /**
     * 下载地址
     */
    private String url;
}
