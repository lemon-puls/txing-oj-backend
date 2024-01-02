package com.bitdf.txing.oj.chat.domain.vo.request;

import com.bitdf.txing.oj.model.dto.cursor.CursorPageBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lizhiwei
 * @date 2024/1/1 20:17:52
 * 注释：
 */
@ApiModel("群聊成员-游标翻页请求")
@Data
public class GroupMemberRequest extends CursorPageBaseRequest {

    @ApiModelProperty("房间id")
    private Long roomId;
}
