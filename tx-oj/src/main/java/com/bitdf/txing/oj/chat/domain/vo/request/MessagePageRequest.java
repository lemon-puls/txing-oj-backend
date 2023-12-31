package com.bitdf.txing.oj.chat.domain.vo.request;

import com.bitdf.txing.oj.model.dto.cursor.CursorPageBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Lizhiwei
 * @date 2023/12/31 12:31:34
 * 注释：
 */
@ApiModel("消息-游标翻页请求")
@Data
public class MessagePageRequest extends CursorPageBaseRequest {

    @ApiModelProperty("房间id")
    private Long roomId;
}
