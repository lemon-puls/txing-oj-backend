package com.bitdf.txing.txcodesandbox.config;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ListImagesCmd;
import com.github.dockerjava.api.command.PullImageCmd;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.PullResponseItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Lizhiwei
 * @date 2023/11/14 21:47:09
 * 注释：
 */
@Component
@Slf4j
public class MyCommandLineRunner implements CommandLineRunner {

    @Autowired
    DockerClient dockerClient;

    /**
     * 拉去所需镜像
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        String javaImage = "openjdk:17";

        ListImagesCmd listImagesCmd = dockerClient.listImagesCmd();
//        listImagesCmd.withLabelFilter("openjdk:17");
//        listImagesCmd.withImageNameFilter(javaImage);
        List<Image> images = listImagesCmd.exec();
        if (images != null) {
            boolean isExist = false;
            for (Image image : images) {
                if (image.getRepoTags().length > 0 && image.getRepoTags()[0].equals(javaImage)) {
                    isExist = true;
                }
            }
            // 如果未拉取 则进行拉取
            if (!isExist) {
                PullImageCmd pullImageCmd = dockerClient.pullImageCmd(javaImage);
                // 拉取镜像回调
                PullImageResultCallback pullImageResultCallback = new PullImageResultCallback() {
                    @Override
                    public void onNext(PullResponseItem item) {
                        log.info("拉取镜像{}：{}", javaImage, item.getStatus());
                        super.onNext(item);
                    }
                };
                try {
                    pullImageCmd.exec(pullImageResultCallback).awaitCompletion();
                } catch (InterruptedException e) {
                    log.info("镜像拉取失败");
                    throw new RuntimeException(e);
                }
                log.info("镜像已拉取成功");
            } else {
                log.info("镜像已存在 无需重复拉取");
            }
        }

    }
}
