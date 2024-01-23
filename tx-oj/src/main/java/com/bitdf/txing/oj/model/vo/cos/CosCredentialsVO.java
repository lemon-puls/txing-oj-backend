package com.bitdf.txing.oj.model.vo.cos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lizhiwei
 * @date 2024/1/23 13:14:16
 * 注释：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CosCredentialsVO {
    private Credentials credentials;
    private String expiration;
    private Long startTime;
    private Long expiredTime;
    private String requestId;

    @Data
    class Credentials {
        private String tmpSecretId;
        private String tmpSecretKey;
        private String sessionToken;
    }
}
