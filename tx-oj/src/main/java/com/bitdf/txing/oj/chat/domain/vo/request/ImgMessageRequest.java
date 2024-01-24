package com.bitdf.txing.oj.chat.domain.vo.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Lizhiwei
 * @date 2024/1/24 15:34:43
 * 注释：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImgMessageRequest extends BaseFileRequest implements Serializable {
    /**
     * 宽度 单位：像素
     */
    private Integer width;
    /**
     * 高度 单位：像素
     */
    private Integer height;
}
