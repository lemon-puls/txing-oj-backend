package com.bitdf.txing.oj.model.dto.file;

import java.io.Serializable;

import lombok.Data;

/**
 * 文件上传请求
 *
 * @author Lizhiwei
 * @date 2023/1/24 3:44:13
 * 注释：
 */
@Data
public class UploadFileRequest implements Serializable {

    /**
     * 业务
     */
    private String biz;
    /**
     * 原图片地址（可为空） 如果不为空 就会删除与之对应的图片
     */
    private String oldImg;
    /**
     * 文章id
     */
    private Long postId;

    private static final long serialVersionUID = 1L;
}