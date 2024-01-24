package com.bitdf.txing.oj.model.entity.chat.msg;

import com.bitdf.txing.oj.chat.domain.vo.request.ImgMessageRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Lizhiwei
 * @date 2023/12/28 10:28:33
 * 注释：
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageExtra implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 艾特的用户ID
     */
    private List<Long> atUserIds;
    /**
     * 图片消息
     */
    private ImgMessageRequest imgMesssageRequest;
}
