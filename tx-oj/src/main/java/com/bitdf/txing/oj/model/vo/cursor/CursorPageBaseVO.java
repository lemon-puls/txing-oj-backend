package com.bitdf.txing.oj.model.vo.cursor;

import cn.hutool.core.collection.CollectionUtil;
import com.bitdf.txing.oj.model.entity.user.UserFriend;
import com.bitdf.txing.oj.model.vo.user.FriendVO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lizhiwei
 * @date 2023/12/30 9:30:50
 * 注释：
 */
@Data
@ApiModel("游标分页结果")
@AllArgsConstructor
@NoArgsConstructor
public class CursorPageBaseVO<T> {
    @ApiModelProperty("游标（翻页时带上这个参数）")
    private String cursor;
    @ApiModelProperty("是否是最后一页数据")
    private Boolean isLast;
    @ApiModelProperty("数据列表")
    private List<T> list;

    public static <T> CursorPageBaseVO<T> empty() {
        CursorPageBaseVO<T> cursorPageBaseVO = new CursorPageBaseVO<>();
        cursorPageBaseVO.setList(new ArrayList<T>());
        cursorPageBaseVO.setIsLast(true);
        return cursorPageBaseVO;
    }

    public static <T> CursorPageBaseVO<T> init(CursorPageBaseVO baseVO, List<T> list) {
        CursorPageBaseVO<T> cursorPageBaseVO = new CursorPageBaseVO<>();
        cursorPageBaseVO.setCursor(baseVO.getCursor());
        cursorPageBaseVO.setIsLast(baseVO.getIsLast());
        cursorPageBaseVO.setList(list);
        return cursorPageBaseVO;
    }

    @JsonIgnore
    public Boolean isEmpty() {
        return CollectionUtil.isEmpty(list);
    }
}
