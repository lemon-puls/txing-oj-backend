package com.bitdf.txing.txcodesandbox;

import cn.hutool.core.io.FileUtil;
import com.bitdf.txing.txcodesandbox.model.ExecMessage;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.StartContainerCmd;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component("gccDockerCodeSandBox")
public class GccDockerCodeSandBox extends CodeSandBoxTemplate {

    @Autowired
    DockerClient dockerClient;

    @Override
    public File saveCode(String code) {
        code = "#include <stdio.h>\n" +
                "#include <stdlib.h>\n" +
                "#include <time.h>\n" +
                "#include <string.h>\n" +
                "#include <ctype.h>\n" +
                "#include <math.h>\n" +
                "#include <stdbool.h>\n" +
                "#include <assert.h>\n" +
                "#include <unistd.h>\n" + code + "\n" +
                "\n" +
                "int main() {\n" +
                "    // 记录程序开始执行的时间\n" +
                "    clock_t start = clock();\n" +
                "\n" +
                "    // 计算数组元素和并输出\n" +
                "    answer();\n" +
                "\n" +
                "    //sleep(1);\n" +
                "\n" +
                "    // 记录程序执行完毕的时间\n" +
                "    clock_t end = clock();\n" +
                "\n" +
                "    // 计算程序执行的时间（毫秒）\n" +
                "    double time_spent = ((double)(end - start)) / CLOCKS_PER_SEC * 1000;\n" +
                "    printf(\"time&&&%.2f&&&time\\n\", time_spent);\n" +
                "\n" +
                "    return 0;\n" +
                "}";

        String property = System.getProperty("user.dir");
        String codePath = property + File.separator + CODE_DIR_NAME;
        /**
         * 不存在 则创建
         */
        if (!FileUtil.exist(codePath)) {
            FileUtil.mkdir(codePath);
        }
        codePath = codePath + File.separator + UUID.randomUUID() + File.separator + "Main.c";
        File file = FileUtil.writeString(code, codePath, StandardCharsets.UTF_8);
        return file;
    }

    @Override
    public ExecMessage compileCode(File file) {
        return null;
    }

    @Override
    public List<ExecMessage> RunCode(File file, List<String> inputs) {
        String absolutePath = file.getParentFile().getAbsolutePath();
        // 2、创建容器
        String pythonImage = "my_c_app:1.0";
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(pythonImage);
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
        StartContainerCmd startContainerCmd = dockerClient.startContainerCmd(containerId);
        startContainerCmd.exec();
        log.info("容器启动了");

        // 编译源代码文件
        ExecCreateCmdResponse response = dockerClient.execCreateCmd(containerId)
                // 使用以下这个命令不生效 没能生产编译文件来 但是也不报错
//                .withCmd("sh", "-c", "'gcc /app/Main.c -o /app/Main'")
                .withCmd("gcc", "/app/Main.c", "-o", "/app/Main")
                .withAttachStderr(true)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .exec();
        String[] errorComplieMsg = new String[1];
        try {
            dockerClient.execStartCmd(response.getId())
                    .exec(new ExecStartResultCallback() {
                        @Override
                        public void onNext(Frame frame) {
                            StreamType streamType = frame.getStreamType();
                            String outputStr = new String(frame.getPayload());
                            if (StreamType.STDERR.equals(streamType)) {
                                errorComplieMsg[0] = new String(frame.getPayload());
                                log.info("编译错误信息：", outputStr);
                            }
                            super.onNext(frame);
                        }

                        @Override
                        public void onComplete() {
                            log.info("编译完成");
                            super.onComplete();
                        }
                    })
                    .awaitCompletion(TIME_OUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if(StringUtils.isNotBlank(errorComplieMsg[0])) {
            return null;
        }

        // 4、执行代码 获取结果
        List<ExecMessage> execMessageList = new ArrayList<>();
        for (String input : inputs) {
            // 构建执行命令
//            String cmd = String.format("echo \"%s\" | java -cp /app Main", input);
//            ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
//                    .withCmd("sh", "-c", cmd)
//                    .withAttachStderr(true)
//                    .withAttachStdin(true)
//                    .withAttachStdout(true)
//                    .exec();
            String cmd = String.format("'%s'", input);
            ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                    .withCmd("/sh/run_gcc.sh", input, "/app")
                    .withAttachStderr(true)
                    .withAttachStdin(true)
                    .withAttachStdout(true)
                    .exec();
            log.info("执行命令构建完成：{}", execCreateCmdResponse);
            String execId = execCreateCmdResponse.getId();

            final String[] message = new String[1];
            final String[] errorMessage = new String[1];
            errorMessage[0] = "";
            // 获取执行时间
            final Double[] times = new Double[1];
            times[0] = 0d;
            final Long[] maxMemorry = {0L};
            String regex = TIME_FLAG_START + "(.*?)" + TIME_FLAG_END;
            Pattern pattern = Pattern.compile(regex);
            String regex1 = MEMORY_FLAG_START + "(.*?)" + MEMORY_FLAG_END;
            Pattern pattern1 = Pattern.compile(regex1);
            // 设置执行回调
            ExecStartResultCallback execStartResultCallback = new ExecStartResultCallback() {
                @Override
                public void onNext(Frame frame) {
                    StreamType streamType = frame.getStreamType();
                    if (StreamType.STDERR.equals(streamType)) {
//                        errorMessage[0] = errorMessage[0] + new String(frame.getPayload());
//                        log.info("错误信息：{}", errorMessage);
                    } else {
                        String outputStr = new String(frame.getPayload());
                        outputStr = outputStr.trim();
                        if (outputStr.contains(TIME_FLAG_START)) {
                            // 输出时间
                            Matcher matcher = pattern.matcher(outputStr);
                            if (matcher.find()) {
                                String extractedString = matcher.group(1);
                                times[0] = Double.parseDouble(extractedString);
                            }
                        }
                        if (outputStr.contains(MEMORY_FLAG_START)) {
                            // 仅输出内存
                            Matcher matcher = pattern1.matcher(outputStr);
                            if (matcher.find()) {
                                String extractedString = matcher.group(1).trim();
                                if (!"".equals(extractedString)) {
                                    maxMemorry[0] = Math.max(Long.parseLong(extractedString), maxMemorry[0]);
                                }
                            }
                        }
                        String trim = outputStr.replaceAll(regex, "").trim().replaceAll(regex1, "").trim();
                        if (!"".equals(trim)) {
                            message[0] = trim;
                        }
                        log.info("正常信息：{}", outputStr);
                    }
                    super.onNext(frame);
                }

                @Override
                public void onComplete() {
                    // 执行完成 说明未超时
                    super.onComplete();
                }
            };
            try {
                dockerClient.execStartCmd(execId)
                        .exec(execStartResultCallback)
                        .awaitCompletion(TIME_OUT, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            ExecMessage execMessage = new ExecMessage();
            execMessage.setTime((long) Math.ceil(times[0]));
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
