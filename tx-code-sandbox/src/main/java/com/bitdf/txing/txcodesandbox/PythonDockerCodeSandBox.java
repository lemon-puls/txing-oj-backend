package com.bitdf.txing.txcodesandbox;

import com.bitdf.txing.txcodesandbox.dto.ExecCodeRequest;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class PythonDockerCodeSandBox {

    @Autowired
    DockerClient dockerClient;

    /**
     * 执行python代码
     *
     * @return
     */
    public String execPythonCode(ExecCodeRequest request) {
        try {
            // 创建一个临时文件来保存 Python 代码
            File tempFile = File.createTempFile("python_code", ".py");
            String pythonCode = "# 输入两个整数\n" +
                    "a = int(input(\"请输入第一个整数：\"))\n" +
                    "b = int(input(\"请输入第二个整数：\"))\n" +
                    "# 计算它们的和\n" +
                    "result = a + b\n" +
                    "# 输出结果\n" +
                    "print(f\"{a} + {b} = {result}\")";
            try (FileOutputStream fileOutputStream = new FileOutputStream(tempFile)) {
                fileOutputStream.write(pythonCode.getBytes());
            }

            // 创建 Docker 容器执行 Python 代码
            CreateContainerResponse container = dockerClient.createContainerCmd("python")
                    .withHostConfig(HostConfig.newHostConfig().withBinds(new Bind(tempFile.getAbsolutePath(), new Volume("/app.py"))))
                    .withCmd("python", "/app.py")
                    .exec();

            dockerClient.startContainerCmd(container.getId()).exec();

            // 等待容器执行完毕
            dockerClient.waitContainerCmd(container.getId())
                    .exec(new WaitContainerResultCallback())
                    .awaitCompletion(30, TimeUnit.SECONDS);

            // 获取容器的日志
            String logs = dockerClient.logContainerCmd(container.getId())
                    .withStdOut(true)
                    .withStdErr(true)
                    .exec(new LogContainerResultCallback())
                    .awaitCompletion(30, TimeUnit.SECONDS);

            System.out.println("执行结果：\n" + logs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
