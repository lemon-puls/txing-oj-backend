package com.bitdf.txing.txcodesandbox;

import com.bitdf.txing.txcodesandbox.model.ExecMessage;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.StartContainerCmd;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Lizhiwei
 * @date 2023/11/14 1:26:00
 * 注释：docker代码沙箱实现
 */
@Slf4j
@Component("javaDockerCodeSandBox")
public class JavaDockerCodeSandBox extends CodeSandBoxTemplate {

    @Autowired
    DockerClient dockerClient;

    @Override
    public List<ExecMessage> RunCode(File file, List<String> inputs) {
        String absolutePath = file.getParentFile().getAbsolutePath();

        // 2、创建容器
//        String javaImage = "openjdk:17";
        String javaImage = "my_java_app:2.0";
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
        StartContainerCmd startContainerCmd = dockerClient.startContainerCmd(containerId);
        startContainerCmd.exec();
        log.info("容器启动了");

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
                    .withCmd("/sh/run_java.sh", input, "/app")
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
            final Long[] times = new Long[1];
            times[0] = 0L;
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
                        errorMessage[0] = errorMessage[0] + new String(frame.getPayload());
                        log.info("错误信息：{}", errorMessage);
                    } else {
                        String outputStr = new String(frame.getPayload());
                        outputStr = outputStr.trim();
                        if (outputStr.contains(TIME_FLAG_START)) {
                            // 输出时间
                            Matcher matcher = pattern.matcher(outputStr);
                            if (matcher.find()) {
                                String extractedString = matcher.group(1);
                                times[0] = Long.parseLong(extractedString);
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
//                                String trim = outputStr.replaceAll(regex1, "").trim();
//                                if (!"".equals(trim)) {
//                                    message[0] = trim;
//                                }
                            }
                        }
                        String trim = outputStr.replaceAll(regex, "").trim().replaceAll(regex1, "").trim();
                        if (!"".equals(trim)) {
                            message[0] = trim;
                        }

//
//                        if (outputStr.startsWith(TIME_FLAG_START)) {
//                            // 仅输出时间
//                            Matcher matcher = pattern.matcher(outputStr);
//                            if (matcher.find()) {
//                                String extractedString = matcher.group(1);
//                                times[0] = Long.parseLong(extractedString);
//                            }
//                        } else if (outputStr.startsWith(MEMORY_FLAG_START)) {
//                            // 仅输出内存
//                            Matcher matcher = pattern1.matcher(outputStr);
//                            if (matcher.find()) {
//                                String extractedString = matcher.group(1).trim();
//                                if (!"".equals(extractedString)) {
//                                    maxMemorry[0] = Math.max(Long.parseLong(extractedString), maxMemorry[0]);
//                                }
//                                String trim = outputStr.replaceAll(regex1, "").trim();
//                                if (!"".equals(trim)) {
//                                    message[0] = trim;
//                                }
//                            }
//                        } else {
//                            message[0] = outputStr.replaceAll(regex, "").trim().replaceAll(regex1, "").trim();
//                            if (outputStr.contains(TIME_FLAG_START)) {
//                                Matcher matcher = pattern.matcher(outputStr);
//                                if (matcher.find()) {
//                                    String extractedString = matcher.group(1);
//                                    times[0] = Long.parseLong(extractedString);
//                                }
//                            }
//                            if (outputStr.contains(MEMORY_FLAG_START)) {
//                                Matcher matcher = pattern1.matcher(outputStr);
//                                if (matcher.find()) {
//                                    String extractedString = matcher.group(1).trim();
//                                    if (!"".equals(extractedString)) {
//                                        maxMemorry[0] = Math.max(Long.parseLong(extractedString), maxMemorry[0]);
//                                    }
//                                }
//                            }
//                        }
                        log.info("正常信息：{}", outputStr);
                    }
//                    super.onNext(frame);
                }

                @Override
                public void onComplete() {
                    // 执行完成 说明未超时
                    super.onComplete();
                }
            };
            // 5、占用内存
//            final Long[] maxMemorry = {0L};
//            StatsCmd statsCmd = dockerClient.statsCmd(containerId);
//            ResultCallback<Statistics> statisticsResultCallback = statsCmd.exec(new ResultCallback<Statistics>() {
//                @Override
//                public void onStart(Closeable closeable) {
//
//                }
//
//                @Override
//                public void onNext(Statistics statistics) {
//                    Long usage = statistics.getMemoryStats().getUsage();
//                    maxMemorry[0] = usage != null ? Math.max(usage, maxMemorry[0]) : maxMemorry[0];
//                    log.info("内存占用：{}", maxMemorry[0]);
//                }
//
//                @Override
//                public void onError(Throwable throwable) {
//
//                }
//
//                @Override
//                public void onComplete() {
//                    log.info("执行 onComplete()");
//                    statsCmd.close();
//                }
//
//                @Override
//                public void close() throws IOException {
//
//                }
//            });
//            statsCmd.exec(statisticsResultCallback);
            // 6、获取执行时间
//            Long times;
//            StopWatch stopWatch = new StopWatch();
            try {
//                stopWatch.start();
                dockerClient.execStartCmd(execId)
                        .exec(execStartResultCallback)
                        .awaitCompletion(TIME_OUT, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
//                stopWatch.stop();
//                times = stopWatch.getLastTaskTimeMillis();
                // 关闭监控
//                statsCmd.close();
//                try {
//                    statisticsResultCallback.close();
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
            }
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
            ExecMessage execMessage = new ExecMessage();
            execMessage.setTime(times[0] == 0 ? TIME_OUT : times[0]);
            execMessage.setMemory(maxMemorry[0]);
            execMessage.setMessage(message[0]);
            // 简化错误信息
            if (errorMessage[0] != "") {
                errorMessage[0] = errorMessage[0].substring(0, errorMessage[0].indexOf("\tat "));
                execMessage.setErrorMessage(errorMessage[0]);
            }
//            execMessage.setExitCode();
            execMessageList.add(execMessage);
        }
        // 删除容器
        dockerClient.removeContainerCmd(containerId)
                .withForce(true)
                .exec();
        return execMessageList;
    }


    @Override
    public File saveCode(String code) {
        code = "import java.util.*;\n" +
                "import java.util.*; \n" +
                "import java.io.*; \n" +
                "import java.net.*; \n" +
                "import java.sql.*; \n" +
                "import javax.swing.*; \n"
                +
                "\n" +
                "public class Main {\n" +
                "\n" +
                "    public static final String TIME_FLAG_START = \"time&&&\";\n" +
                "\n" +
                "    public static final String TIME_FLAG_END = \"&&&time\";\n" +
                "\n" +
                "    public static void main(String[] args) {\n" +
                "        long startTimes = System.currentTimeMillis();\n" +
                "        Solution solution = new Solution();\n" +
                "        solution.answer();\n" +
                "        long endTimes = System.currentTimeMillis();\n" +
                "        System.out.println();\n" +
                "        System.out.println(TIME_FLAG_START + (endTimes - startTimes) + TIME_FLAG_END);\n" +
                "    }\n" +
                "}" + code;
        return super.saveCode(code);
    }
}


