package com.bitdf.txing.oj.common;

import java.io.Serializable;
import lombok.Data;

/**
 * 删除请求
 *
 * @author Lizhiwei
 * @date 2023/1/24 3:44:13
 * 注释：
 */
@Data
public class DeleteRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}