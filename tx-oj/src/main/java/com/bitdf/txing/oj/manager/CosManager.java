package com.bitdf.txing.oj.manager;

import com.bitdf.txing.oj.config.CosClientConfig;
import com.bitdf.txing.oj.model.vo.cos.CosCredentialsVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.DeleteObjectsRequest;
import com.qcloud.cos.model.DeleteObjectsResult;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.tencent.cloud.CosStsClient;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Cos 对象存储操作
 *
 * @author Lizhiwei
 * @date 2023/1/24 3:44:13
 * 注释：
 */
@Component
public class CosManager {

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private COSClient cosClient;

    /**
     * 上传对象
     *
     * @param key           唯一键
     * @param localFilePath 本地文件路径
     * @return
     */
    public PutObjectResult putObject(String key, String localFilePath) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key,
                new File(localFilePath));
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 上传对象
     *
     * @param key  唯一键
     * @param file 文件
     * @return
     */
    public PutObjectResult putObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key,
                file);
        return cosClient.putObject(putObjectRequest);
    }

    public void deleteOject(String key) {
        cosClient.deleteObject(cosClientConfig.getBucket(), key);
    }

    /**
     * 批量删除文件
     *
     * @param keys
     */
    public void deleteOjects(List<String> keys) {
        DeleteObjectsRequest deleteRequest = new DeleteObjectsRequest(cosClientConfig.getBucket());
        List<DeleteObjectsRequest.KeyVersion> collect = keys.stream().map((str) -> {
            return new DeleteObjectsRequest.KeyVersion(str);
        }).collect(Collectors.toList());
        deleteRequest.setKeys(collect);
        DeleteObjectsResult deleteObjectsResult = cosClient.deleteObjects(deleteRequest);
    }

    /**
     * 生成临时秘钥
     */
    public CosCredentialsVO generateCreDentials() {
        TreeMap<String, Object> config = new TreeMap<String, Object>();
        try {
            //这里的 SecretId 和 SecretKey 代表了用于申请临时密钥的永久身份（主账号、子账号等），子账号需要具有操作存储桶的权限。
//            String secretId = System.getenv("secretId");//用户的 SecretId，建议使用子账号密钥，授权遵循最小权限指引，降低使用风险。子账号密钥获取可参见 https://cloud.tencent.com/document/product/598/37140
//            String secretKey = System.getenv("secretKey");//用户的 SecretKey，建议使用子账号密钥，授权遵循最小权限指引，降低使用风险。子账号密钥获取可参见 https://cloud.tencent.com/document/product/598/37140
            // 替换为您的云 api 密钥 SecretId
            config.put("secretId", cosClientConfig.getAccessKey());
            // 替换为您的云 api 密钥 SecretKey
            config.put("secretKey", cosClientConfig.getSecretKey());
            // 设置域名:
            // 如果您使用了腾讯云 cvm，可以设置内部域名
            //config.put("host", "sts.internal.tencentcloudapi.com");

            // 临时密钥有效时长，单位是秒，默认 1800 秒，目前主账号最长 2 小时（即 7200 秒），子账号最长 36 小时（即 129600）秒
            config.put("durationSeconds", 60 * 10);
            // 换成您的 bucket
            config.put("bucket", cosClientConfig.getBucket());
            // 换成 bucket 所在地区
            config.put("region", cosClientConfig.getRegion());

            // 这里改成允许的路径前缀，可以根据自己网站的用户登录态判断允许上传的具体路径
            // 列举几种典型的前缀授权场景：
            // 1、允许访问所有对象："*"
            // 2、允许访问指定的对象："a/a1.txt", "b/b1.txt"
            // 3、允许访问指定前缀的对象："a*", "a/*", "b/*"
            // 如果填写了“*”，将允许用户访问所有资源；除非业务需要，否则请按照最小权限原则授予用户相应的访问权限范围。
            config.put("allowPrefixes", new String[]{
                    "*",
            });


            // 密钥的权限列表。必须在这里指定本次临时密钥所需要的权限。
            // 简单上传、表单上传和分块上传需要以下的权限，其他权限列表请参见 https://cloud.tencent.com/document/product/436/31923
            String[] allowActions = new String[]{
                    // 简单上传
                    "name/cos:PutObject",
                    // 表单上传、小程序上传
                    "name/cos:PostObject",
                    // 分块上传
                    "name/cos:InitiateMultipartUpload",
                    "name/cos:ListMultipartUploads",
                    "name/cos:ListParts",
                    "name/cos:UploadPart",
                    "name/cos:CompleteMultipartUpload"
            };
            config.put("allowActions", allowActions);

            JSONObject credential = CosStsClient.getCredential(config);
            // 创建 ObjectMapper 实例
            ObjectMapper objectMapper = new ObjectMapper();

            // 将 JSONObject 转换为指定对象
            CosCredentialsVO cosCredentialsVO = objectMapper.readValue(credential.toString(), CosCredentialsVO.class);
            return cosCredentialsVO;
//            System.out.println(response.credentials.tmpSecretId);
//            System.out.println(response.credentials.tmpSecretKey);
//            System.out.println(response.credentials.sessionToken);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("no valid secret !");
        }
    }
}
