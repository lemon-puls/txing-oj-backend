package com.bitdf.txing.oj.model.vo.match;

import com.bitdf.txing.oj.model.vo.user.UserShowVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OnlinePKResultVO {
    private Long userId1;

    private Long userId2;

    private UserShowVO userVo1;

    private UserShowVO userVo2;

    private Double score1;

    private Double score2;

    private Long winnerId;
}
