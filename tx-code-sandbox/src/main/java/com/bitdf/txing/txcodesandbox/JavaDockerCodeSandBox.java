package com.bitdf.txing.txcodesandbox;

import com.bitdf.txing.txcodesandbox.model.ExecMessage;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.StatsCmd;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Lizhiwei
 * @date 2023/11/14 1:26:00
 * 注释：docker代码沙箱实现
 */
@Slf4j
@Component
public class JavaDockerCodeSandBox extends CodeSandBoxTemplate {

    @Autowired
    DockerClient dockerClient;

    @Override
    public List<ExecMessage> RunCode(File file, List<String> inputs) {
        String absolutePath = file.getParentFile().getAbsolutePath();

        // 2、创建容器
        String javaImage = "openjdk:17";
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(javaImage);
        // 容器配置
        HostConfig hostConfig = new HostConfig();
        // 限制内存用量
        hostConfig.withMemory(100 * 1000 * 1000L);
        hostConfig.withMemorySwap(0L);
        hostConfig.withCpuCount(1L);
//        hostConfig.withSecurityOpts(Arrays.asList("seccomp=安全管理配置字符串"));
        hostConfig.setBinds(new Bind(absolutePath, new Volume("/app")));
        CreateContainerResponse createContainerResponse = containerCmd.withHostConfig(hostConfig)
                .withNetworkDisabled(true) // 限制网络使用 保证安全
                .withReadonlyRootfs(true)
                .withAttachStderr(true)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .withTty(true)
                .exec();
        log.info("成功创建容器：{}", createContainerResponse.getId());

        // 3、启动容器
        // 获取容器Id
        String containerId = createContainerResponse.getId();
        dockerClient.startContainerCmd(containerId).exec();
        log.info("容器启动了");

        // 4、执行代码 获取结果
        List<ExecMessage> execMessageList = new ArrayList<>();
        for (String input : inputs) {
            // 构建执行命令
            String cmd = String.format("echo \"%s\" | java -cp /app Main", input);
            ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                    .withCmd("sh", "-c", cmd)
                    .withAttachStderr(true)
                    .withAttachStdin(true)
                    .withAttachStdout(true)
                    .exec();
            log.info("执行命令构建完成：{}", execCreateCmdResponse);
            String execId = execCreateCmdResponse.getId();

            final String[] message = new String[1];
            final String[] errorMessage = new String[1];
            // 设置执行回调
            ExecStartResultCallback execStartResultCallback = new ExecStartResultCallback() {
                @Override
                public void onNext(Frame frame) {
                    StreamType streamType = frame.getStreamType();
                    if (StreamType.STDERR.equals(streamType)) {
                        errorMessage[0] = new String(frame.getPayload());
                        log.info("错误信息：{}", errorMessage);
                    } else {
                        message[0] = new String(frame.getPayload());
                        log.info("正常信息：{}", message[0]);
                    }
                    super.onNext(frame);
                }

                @Override
                public void onComplete() {
                    // 执行完成 说明未超时
                    super.onComplete();
                }
            };
            // 5、占用内存
            final Long[] maxMemorry = {0L};
            StatsCmd statsCmd = dockerClient.statsCmd(containerId);
            ResultCallback<Statistics> statisticsResultCallback = statsCmd.exec(new ResultCallback<Statistics>() {
                @Override
                public void onStart(Closeable closeable) {

                }

                @Override
                public void onNext(Statistics statistics) {
                    Long usage = statistics.getMemoryStats().getUsage();
                    maxMemorry[0] = usage != null ? Math.max(usage, maxMemorry[0]) : maxMemorry[0];
                    log.info("内存占用：{}", maxMemorry[0]);
                }

                @Override
                public void onError(Throwable throwable) {

                }

                @Override
                public void onComplete() {
                    log.info("执行 onComplete()");
                    statsCmd.close();
                }

                @Override
                public void close() throws IOException {

                }
            });
            statsCmd.exec(statisticsResultCallback);
            // 6、获取执行时间
            Long times;
            StopWatch stopWatch = new StopWatch();
            try {
                stopWatch.start();
                dockerClient.execStartCmd(execId)
                        .exec(execStartResultCallback)
                        .awaitCompletion(TIME_OUT, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                stopWatch.stop();
                times = stopWatch.getLastTaskTimeMillis();
                // 关闭监控
                statsCmd.close();
                try {
                    statisticsResultCallback.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            ExecMessage execMessage = new ExecMessage();
            execMessage.setTime(times);
            execMessage.setMemory(maxMemorry[0]);
            execMessage.setMessage(message[0]);
            execMessage.setErrorMessage(errorMessage[0]);
//            execMessage.setExitCode();
            execMessageList.add(execMessage);
        }
        // 删除容器
        dockerClient.removeContainerCmd(containerId)
                .withForce(true)
                .exec();
        return execMessageList;
    }


}


