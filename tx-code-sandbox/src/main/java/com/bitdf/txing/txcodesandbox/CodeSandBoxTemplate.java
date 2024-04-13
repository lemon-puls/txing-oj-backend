package com.bitdf.txing.txcodesandbox;

import cn.hutool.core.io.FileUtil;
import com.bitdf.txing.txcodesandbox.config.MyThreadPool;
import com.bitdf.txing.txcodesandbox.dto.ExecCodeRequest;
import com.bitdf.txing.txcodesandbox.dto.ExecCodeResponse;
import com.bitdf.txing.txcodesandbox.model.ExecMessage;
import com.bitdf.txing.txcodesandbox.model.JudgeInfo;
import com.bitdf.txing.txcodesandbox.util.ProcessUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Lizhiwei
 * @date 2023/11/14 1:22:30
 * 注释：使用了 模板方法设计模式 将执行代码过程拆分为多个步骤（方法），这样一来，不同的代码沙箱只需要根据自己的实际情况 调整某个步骤的实现即可 而无需写大量的重复代码
 */
@Slf4j
public abstract class CodeSandBoxTemplate implements CodeSandBox {

    public static final String CODE_DIR_NAME = "tempCode";

    public static final String JAVA_CLASS_NAME = "Main.java";

    public static final String TIME_FLAG_START = "time&&&";

    public static final String TIME_FLAG_END = "&&&time";

    public static final String MEMORY_FLAG_START = "memory&&&";

    public static final String MEMORY_FLAG_END = "&&&memory";

    public static final Long TIME_OUT = 1000L;

//    public static void main(String[] args) {
//        String code = "import java.util.Scanner;\n" +
//                "\n" +
//                "public class Main {\n" +
//                "    public static void main(String[] args) {\n" +
//                "        Scanner scanner = new Scanner(System.in);\n" +
//                "\n" +
//                "        System.out.print(\"请输入第一个整数：\");\n" +
//                "        int a = scanner.nextInt();\n" +
//                "\n" +
//                "        System.out.print(\"请输入第二个整数：\");\n" +
//                "        int b = scanner.nextInt();\n" +
//                "\n" +
//                "        int sum = a + b;\n" +
//                "        System.out.println(\"两者的和为：\" + sum);\n" +
//                "    }\n" +
//                "}";
//        List<String> inputs = new ArrayList<>();
//        inputs.add("1 2");
//        inputs.add("3 5");
//        ExecCodeRequest execCodeRequest = new ExecCodeRequest("java", code, inputs);
//        CodeSandBoxTemplate codeSandBoxTemplate = new CodeSandBoxTemplate();
//        ExecCodeResponse execCodeResponse = codeSandBoxTemplate.execCode(execCodeRequest);
//    }

    @Override
    public ExecCodeResponse execCode(ExecCodeRequest execCodeRequest) {
        // 1、保存： 将用户提交的代码保存为文件
        File file = saveCode(execCodeRequest.getCode());
        // 2. 编译：将.java文件编译为.class文件
        ExecMessage execMessage = compileCode(file);
        if (execMessage != null && execMessage.getExitCode() != 0) {
            return buildComplieErrResponse();
        }
        // 3、执行：执行编译后的字节码文件 拿到执行结果
        List<ExecMessage> execMessageList = RunCode(file, execCodeRequest.getInputs());
        if (execMessageList == null) {
            return buildComplieErrResponse();
        }
        // 4、整理结果
        ExecCodeResponse execCodeResponse = buildExecResult(execMessageList);
        // 5、删除文件：避免造成不必要的空间浪费
        boolean b = deleteTempFile(file);
        if (!b) {
            log.info("文件删除失败：{}", file.getParentFile().getAbsoluteFile());
        }
        return execCodeResponse;
    }

    /**
     * 删除文件：避免造成不必要的空间浪费
     *
     * @param file
     * @return
     */
    public boolean deleteTempFile(File file) {
        if (file.getParentFile() != null) {
            File absoluteFile = file.getParentFile().getAbsoluteFile();
            boolean del = FileUtil.del(absoluteFile);
            log.info("删除文件：{}", del);
            return del;
        }
        return true;
    }

    /**
     * 整理执行结果
     *
     * @param execMessageList
     * @return
     */
    public ExecCodeResponse buildExecResult(List<ExecMessage> execMessageList) {
        ExecCodeResponse execCodeResponse = new ExecCodeResponse();
        Long maxTimes = 0L;
        Long maxMemory = 0L;
        List<String> outputs = new ArrayList<>();
        for (ExecMessage execMessage : execMessageList) {
            String errorMessage = execMessage.getErrorMessage();
            String message = execMessage.getMessage();
            if (StringUtils.isNotEmpty(errorMessage) || StringUtils.isEmpty(message)) {
                // 出错情况
                execCodeResponse.setStatus(3);
                execCodeResponse.setMessage(errorMessage);
                break;
            }
            outputs.add(execMessage.getMessage());

            maxTimes = Math.max(maxTimes, execMessage.getTime());
            maxMemory = Math.max(maxMemory, execMessage.getMemory());
        }
        // 所用用例都正常执行
        if (outputs.size() == execMessageList.size()) {
            execCodeResponse.setStatus(1);
        }
        execCodeResponse.setOutputs(outputs);
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setTime(maxTimes == 0 ? 1 : maxTimes);
        judgeInfo.setMemory(maxMemory == 0 ? 1024 : maxMemory);
        execCodeResponse.setJudgeInfo(judgeInfo);
        return execCodeResponse;
    }

    /**
     * 执行代码
     *
     * @param file
     * @param inputs
     * @return
     */
    public List<ExecMessage> RunCode(File file, List<String> inputs) {
        File absoluteFile = file.getParentFile().getAbsoluteFile();
        List<ExecMessage> ans = new ArrayList<>();
        // 生成执行命令 -Xmx256m：避免占用过多空间
        String runCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s Main", absoluteFile);
        for (String input : inputs) {
            //执行命令
            try {
                Process process = Runtime.getRuntime().exec(runCmd);
                //进行超时控制
                MyThreadPool.myScheduledExecutor.schedule(() -> {
                    // TODO 可以添加更多判断
                    log.info("超时中断");
                    process.destroy();
                }, TIME_OUT, TimeUnit.MILLISECONDS);
                ExecMessage execMessage = ProcessUtil.runInteractProcessAndGetMessage(process, input);
                ans.add(execMessage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return ans;
    }

    /**
     * 编译代码
     *
     * @param file
     * @return
     */
    public ExecMessage compileCode(File file) {
        String compileCmd = String.format("javac -encoding utf-8 %s", file.getAbsoluteFile());
        try {
            Process process = Runtime.getRuntime().exec(compileCmd);
            ExecMessage execMessage = ProcessUtil.runProcessAndGetMessage(process);
            return execMessage;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将代码保存为文件
     *
     * @param code
     * @return
     */
    public File saveCode(String code) {
        String property = System.getProperty("user.dir");
        String codePath = property + File.separator + CODE_DIR_NAME;
        /**
         * 不存在 则创建
         */
        if (!FileUtil.exist(codePath)) {
            FileUtil.mkdir(codePath);
        }
        codePath = codePath + File.separator + UUID.randomUUID() + File.separator + JAVA_CLASS_NAME;
        File file = FileUtil.writeString(code, codePath, StandardCharsets.UTF_8);
        return file;
    }

    public static ExecCodeResponse buildComplieErrResponse() {
        ExecCodeResponse response = new ExecCodeResponse();
        response.setStatus(0);
        response.setMessage("编译错误");
        return response;
    }


}
