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
@Component("jsDockerCodeSandBox")
public class JsDockerCodeSandBox extends CodeSandBoxTemplate {

    @Autowired
    DockerClient dockerClient;

    @Override
    public File saveCode(String code) {
        code = code + "\n" +
                "function Main() {\n" +
                "        // 记录开始时间\n" +
                "        const startTime = new Date().getTime();\n" +
                "        answer();\n" +
                "        // 记录结束时间\n" +
                "        const endTime = new Date().getTime();\n" +
                "        // 输出函数执行时间\n" +
                "        console.log('time&&&' + (endTime - startTime) + '&&&time');\n" +
                "}\n" +
                "Main();";

        String property = System.getProperty("user.dir");
        String codePath = property + File.separator + CODE_DIR_NAME;
        /**
         * 不存在 则创建
         */
        if (!FileUtil.exist(codePath)) {
            FileUtil.mkdir(codePath);
        }
        codePath = codePath + File.separator + UUID.randomUUID() + File.separator + "Main.js";
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
        String pythonImage = "my_js_app:1.0";
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
                    .withCmd("/sh/run_js.sh", input, "/app")
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

            if (errorMessage[0] != "") {
                int start = errorMessage[0].indexOf("\n");
                int end = errorMessage[0].indexOf("\n    at ");
                execMessage.setErrorMessage(errorMessage[0].substring(start, end));
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

    /**
     * 执行python代码
     *
     * @return
     */
//    public String execPythonCode(ExecCodeRequest request) {
//        try {
//            // 创建一个临时文件来保存 Python 代码
//            File tempFile = File.createTempFile("python_code", ".py");
//            String pythonCode = "# 输入两个整数\n" +
//                    "a = int(input(\"请输入第一个整数：\"))\n" +
//                    "b = int(input(\"请输入第二个整数：\"))\n" +
//                    "# 计算它们的和\n" +
//                    "result = a + b\n" +
//                    "# 输出结果\n" +
//                    "print(f\"{a} + {b} = {result}\")";
//            try (FileOutputStream fileOutputStream = new FileOutputStream(tempFile)) {
//                fileOutputStream.write(pythonCode.getBytes());
//            }
//
//            // 创建 Docker 容器执行 Python 代码
//            CreateContainerResponse container = dockerClient.createContainerCmd("python")
//                    .withHostConfig(HostConfig.newHostConfig().withBinds(new Bind(tempFile.getAbsolutePath(), new Volume("/app.py"))))
//                    .withCmd("python", "/app.py")
//                    .exec();
//
//            dockerClient.startContainerCmd(container.getId()).exec();
//
//            // 等待容器执行完毕
//            dockerClient.waitContainerCmd(container.getId())
//                    .exec(new WaitContainerResultCallback())
//                    .awaitCompletion(30, TimeUnit.SECONDS);
//
//            // 获取容器的日志
//            String logs = dockerClient.logContainerCmd(container.getId())
//                    .withStdOut(true)
//                    .withStdErr(true)
//                    .exec(new LogContainerResultCallback())
//                    .awaitCompletion(30, TimeUnit.SECONDS);
//
//            System.out.println("执行结果：\n" + logs);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "";
//    }
}
