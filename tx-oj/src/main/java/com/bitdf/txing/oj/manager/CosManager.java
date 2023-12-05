package com.bitdf.txing.oj.manager;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.DeleteObjectsRequest;
import com.qcloud.cos.model.DeleteObjectsResult;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.bitdf.txing.oj.config.CosClientConfig;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

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
}
