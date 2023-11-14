package com.bitdf.txing.txcodesandbox.config;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * @author Lizhiwei
 * @date 2023/8/28 15:04:40
 * @description 线程池
 */
public class MyThreadPool {
    /**
     * 用于提交定时任务
     */
    public static ScheduledExecutorService myScheduledExecutor = new ScheduledThreadPoolExecutor(10);
}
