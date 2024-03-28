package com.bitdf.txing.oj.model.vo.forum;

import com.bitdf.txing.oj.model.vo.user.UserShowVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TopicVO {
    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;
    /**
     * 配图
     */
    private List<String> imgs;

    /**
     * 标签列表 json
     */
    private String tags;

    /**
     * 点赞数
     */
    private Integer thumbNum;
    /**
     * 收藏数
     */
    private Integer favourNum;
    /**
     * 评论数
     */
    private Integer commentNum;
    /**
     * 创建用户 id
     */
    private Long userId;
    /**
     * 用户信息
     */
    private UserShowVO userShowVO;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    /**
     * 当前用户是否点赞
     */
    private boolean isThumb;
    /**
     * 当前用户是否收藏
     */
    private boolean isFavour;
}
