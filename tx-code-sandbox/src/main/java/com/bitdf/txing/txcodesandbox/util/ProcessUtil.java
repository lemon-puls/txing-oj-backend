package com.bitdf.txing.txcodesandbox.util;

import cn.hutool.core.util.StrUtil;
import com.bitdf.txing.txcodesandbox.model.ExecMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StopWatch;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lizhiwei
 * @date 2023/11/14 13:10:27
 * 注释：Process相关操作
 */
@Slf4j
public class ProcessUtil {
    /**
     * 运行Process 获取运行信息
     *
     * @param process
     * @return
     */
    public static ExecMessage runProcessAndGetMessage(Process process) {
        ExecMessage execMessage = new ExecMessage();
        // 记录执行时间
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader errorBufferedReader = null;
        InputStreamReader inputStreamReader1 = null;
        try {
            int exitCode = process.waitFor();
            execMessage.setExitCode(exitCode);
            // 获取正常输出
            log.info("获取Process正常输出");
            inputStreamReader = new InputStreamReader(process.getInputStream());
            bufferedReader = new BufferedReader(inputStreamReader);
            List<String> messages = new ArrayList<>();
            String curLine;
            while ((curLine = bufferedReader.readLine()) != null) {
                messages.add(curLine);
            }
            execMessage.setMessage(StringUtils.join(messages, "\n"));
            if (exitCode == 0) {
                // 执行成功
                log.info("Process执行成功");
            } else {
                // 执行失败
                log.info("Process执行失败");
                // 获取错误输出
                log.info("获取Process错误输出");
                inputStreamReader1 = new InputStreamReader(process.getErrorStream());
                errorBufferedReader = new BufferedReader(inputStreamReader1);
                List<String> errorMessages = new ArrayList<>();
                String curErrorLine;
                while ((curErrorLine = errorBufferedReader.readLine()) != null) {
                    errorMessages.add(curErrorLine);
                }
                execMessage.setErrorMessage(StringUtils.join(errorMessages, "\n"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // 记得释放资源 否则会卡死
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (errorBufferedReader != null) {
                    errorBufferedReader.close();
                }
                if (inputStreamReader1 != null) {
                    inputStreamReader1.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        stopWatch.stop();
        execMessage.setTime(stopWatch.getLastTaskTimeMillis());
        return execMessage;
    }

    /**
     * 交互式运行Process 获取运行信息
     *
     * @param process
     * @param input
     * @return
     */
    public static ExecMessage runInteractProcessAndGetMessage(Process process, String input) {
        ExecMessage execMessage = new ExecMessage();
        // 计算执行时间
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        OutputStream outputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader errorBufferedReader = null;
        InputStreamReader inputStreamReader1 = null;
        try {
            // 输入参数
            outputStream = process.getOutputStream();
            outputStreamWriter = new OutputStreamWriter(outputStream);
            String[] inputs = input.split(" ");
            String join = StrUtil.join("\n", inputs) + "\n";
            outputStreamWriter.write(join);
            outputStreamWriter.flush();
            int exitCode = process.waitFor();
            // 获取正常输出
            inputStreamReader = new InputStreamReader(process.getInputStream());
            bufferedReader = new BufferedReader(inputStreamReader);
            String curStr;
            List<String> messages = new ArrayList<>();
            while ((curStr = bufferedReader.readLine()) != null) {
                messages.add(curStr);
            }
            execMessage.setMessage(StringUtils.join(messages, "\n"));
            if (exitCode == 0) {
                log.info("执行成功");
            } else {
                log.info("执行失败 收集错误信息");
                // 获取错误输出
                inputStreamReader1 = new InputStreamReader(process.getErrorStream());
                errorBufferedReader = new BufferedReader(inputStreamReader1);
                List<String> errorMessages = new ArrayList<>();
                String curErrorLine;
                while ((curErrorLine = errorBufferedReader.readLine()) != null) {
                    errorMessages.add(curErrorLine);
                }
                execMessage.setErrorMessage(StringUtils.join(errorMessages, "\n"));
            }
        } catch (Exception e) {
            log.info("执行代码出错了");
            throw new RuntimeException(e);
        } finally {
            // 记得释放资源 否则会卡死
            try {
                if (outputStreamWriter != null) {
                    outputStreamWriter.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (errorBufferedReader != null) {
                    errorBufferedReader.close();
                }
                if (inputStreamReader1 != null) {
                    inputStreamReader1.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        stopWatch.stop();
        execMessage.setTime(stopWatch.getLastTaskTimeMillis());
        return execMessage;
    }
}
