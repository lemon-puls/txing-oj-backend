package com.bitdf.txing.oj.model.dto.cursor;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * @author Lizhiwei
 * @date 2023/12/30 9:42:14
 * 注释：
 */
@Data
//@ApiModel("游标翻页请求")
@AllArgsConstructor
@NoArgsConstructor
public class CursorPageBaseRequest {
    @ApiModelProperty("页面大小")
    @Min(0)
    @Max(100)
    private Integer pageSize = 10;

    @ApiModelProperty("游标")
    private String cursor;

    public Page getPage() {
        return new Page(1, this.pageSize, false);
    }

    @JsonIgnore
    public Boolean isFirstPage() {
        return StringUtils.isEmpty(cursor);
    }
}
