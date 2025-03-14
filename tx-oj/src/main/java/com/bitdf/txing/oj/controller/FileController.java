package com.bitdf.txing.oj.controller;

import cn.hutool.core.io.FileUtil;
import com.bitdf.txing.oj.annotation.AuthCheck;
import com.bitdf.txing.oj.aop.AuthInterceptor;
import com.bitdf.txing.oj.constant.FileConstant;
import com.bitdf.txing.oj.constant.RedisKeyConstant;
import com.bitdf.txing.oj.exception.BusinessException;
import com.bitdf.txing.oj.manager.CosManager;
import com.bitdf.txing.oj.model.dto.file.UploadFileRequest;
import com.bitdf.txing.oj.model.entity.user.User;
import com.bitdf.txing.oj.model.enume.FileUploadBizEnum;
import com.bitdf.txing.oj.model.enume.TxCodeEnume;
import com.bitdf.txing.oj.model.vo.cos.CosCredentialsVO;
import com.bitdf.txing.oj.service.UserService;
import com.bitdf.txing.oj.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Arrays;

/**
 * 文件接口
 *
 * @author Lizhiwei
 * @date 2023/1/24 3:44:13
 * 注释：
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Resource
    private UserService userService;

    @Resource
    private CosManager cosManager;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    /**
     * 文件上传
     *
     * @param multipartFile
     * @param uploadFileRequest
     * @param request
     * @return
     */
    @PostMapping("/upload")
    @AuthCheck(mustRole = "login")
    public R uploadFile(@RequestPart("file") MultipartFile multipartFile,
                        UploadFileRequest uploadFileRequest, HttpServletRequest request) {
        String biz = uploadFileRequest.getBiz();
        String oldImg = uploadFileRequest.getOldImg();
        Long postId = uploadFileRequest.getPostId();
        if (StringUtils.isNotBlank(oldImg)) {
            // 先删除原图片
            String prefix = "myqcloud.com";
            cosManager.deleteOject(oldImg.substring(oldImg.indexOf(prefix) + prefix.length()));
        }

        FileUploadBizEnum fileUploadBizEnum = FileUploadBizEnum.getEnumByValue(biz);
        if (fileUploadBizEnum == null) {
            throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION);
        }
        validFile(multipartFile, fileUploadBizEnum);
//        User loginUser = userService.getLoginUser(request);
        User loginUser = AuthInterceptor.userThreadLocal.get();
        // 文件目录：根据业务、用户来划分
        String uuid = RandomStringUtils.randomAlphanumeric(8);
        String filename = uuid + "-" + multipartFile.getOriginalFilename();
        String filepath = String.format("/%s/%s/%s" , fileUploadBizEnum.getValue(), loginUser.getId(), filename);
        File file = null;
        try {
            // 上传文件
            file = File.createTempFile(filepath, null);
            multipartFile.transferTo(file);
            cosManager.putObject(filepath, file);
            String url = FileConstant.COS_HOST + filepath;
            if (FileUploadBizEnum.POST_CONTENT_IMG.getValue().equals(biz)) {
                // 记录该图片地址到Redis
                String key = postId != null && postId != -1 ? RedisKeyConstant.POST_CONTENT_IMGS_UPDATE + postId : RedisKeyConstant.POST_CONTENT_IMGS_ADD;
                BoundHashOperations<String, Object, Object> imgsHashOps = stringRedisTemplate.boundHashOps(key);
                imgsHashOps.put(url, System.currentTimeMillis() + "");
            }
            // 返回可访问地址
            return R.ok().put("data" , url);
        } catch (Exception e) {
            log.error("file upload error, filepath = " + filepath, e);
            throw new BusinessException(TxCodeEnume.COMMON_SYSTEM_UNKNOWN_EXCEPTION, "上传失败");
        } finally {
            if (file != null) {
                // 删除临时文件
                boolean delete = file.delete();
                if (!delete) {
                    log.error("file delete error, filepath = {}" , filepath);
                }
            }
        }
    }

    /**
     * 校验文件
     *
     * @param multipartFile
     * @param fileUploadBizEnum 业务类型
     */
    private void validFile(MultipartFile multipartFile, FileUploadBizEnum fileUploadBizEnum) {
        // 文件大小
        long fileSize = multipartFile.getSize();
        // 文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        final long ONE_M = 1024 * 1024L;
        if (FileUploadBizEnum.USER_AVATAR.equals(fileUploadBizEnum)) {
            if (fileSize > ONE_M) {
                throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION, "文件大小不能超过 1M");
            }
            if (!Arrays.asList("jpeg" , "jpg" , "svg" , "png" , "webp").contains(fileSuffix)) {
                throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION, "文件类型错误");
            }
        }
    }

    @GetMapping("/credential/get")
    @AuthCheck(mustRole = "login")
    public R getCosCredentials() {
        CosCredentialsVO cosCredentialsVO = cosManager.generateCreDentials();
        return R.ok(cosCredentialsVO);
    }


    /**
     * 文件上传 提供给后台管理系统使用
     *
     * @param multipartFile
     * @param uploadFileRequest
     * @return
     */
    @PostMapping("/upload/12423534672436234")
    public R uploadFilePrivate(@RequestPart("file") MultipartFile multipartFile,
                               UploadFileRequest uploadFileRequest) {
        String biz = uploadFileRequest.getBiz();
        String oldImg = uploadFileRequest.getOldImg();
        Long postId = uploadFileRequest.getPostId();
        if (StringUtils.isNotBlank(oldImg)) {
            // 先删除原图片
            String prefix = "myqcloud.com";
            cosManager.deleteOject(oldImg.substring(oldImg.indexOf(prefix) + prefix.length()));
        }

        FileUploadBizEnum fileUploadBizEnum = FileUploadBizEnum.getEnumByValue(biz);
        if (fileUploadBizEnum == null) {
            throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION);
        }
        validFile(multipartFile, fileUploadBizEnum);
//        User loginUser = userService.getLoginUser(request);
        User loginUser = AuthInterceptor.userThreadLocal.get();
        // 文件目录：根据业务、用户来划分
        String uuid = RandomStringUtils.randomAlphanumeric(8);
        String filename = uuid + "-" + multipartFile.getOriginalFilename();
        String filepath = String.format("/%s/%s/%s" , fileUploadBizEnum.getValue(), 0, filename);
        File file = null;
        try {
            // 上传文件
            file = File.createTempFile(filepath, null);
            multipartFile.transferTo(file);
            cosManager.putObject(filepath, file);
            String url = FileConstant.COS_HOST + filepath;
            if (FileUploadBizEnum.POST_CONTENT_IMG.getValue().equals(biz)) {
                // 记录该图片地址到Redis
                String key = postId != null && postId != -1 ? RedisKeyConstant.POST_CONTENT_IMGS_UPDATE + postId : RedisKeyConstant.POST_CONTENT_IMGS_ADD;
                BoundHashOperations<String, Object, Object> imgsHashOps = stringRedisTemplate.boundHashOps(key);
                imgsHashOps.put(url, System.currentTimeMillis() + "");
            }
            // 返回可访问地址
            return R.ok().put("data" , url);
        } catch (Exception e) {
            log.error("file upload error, filepath = " + filepath, e);
            throw new BusinessException(TxCodeEnume.COMMON_SYSTEM_UNKNOWN_EXCEPTION, "上传失败");
        } finally {
            if (file != null) {
                // 删除临时文件
                boolean delete = file.delete();
                if (!delete) {
                    log.error("file delete error, filepath = {}" , filepath);
                }
            }
        }
    }

}
