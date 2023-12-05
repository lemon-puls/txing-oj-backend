package com.bitdf.txing.oj.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Lizhiwei
 * @date 2023/12/5 19:31:40
 * 注释：
 */
public class CustomStringUtils {

    /**
     * 根据给定的正则表达式到文本中查出所有匹配的子串
     *
     * @param regex 正则表达式
     * @param text
     * @param list
     * @return
     */
    public static List<String> getMatchStrList(String regex, String text, List<String> list) {
        List<String> ans;
        ans = list;
        if (list == null) {
            ans = new ArrayList<String>();
        }
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regex);
        // 创建 Matcher 对象，用于匹配文本中的内容
        Matcher matcher = pattern.matcher(text);
        // 遍历匹配结果
        while (matcher.find()) {
            // 获取匹配到的链接
            String link = matcher.group();
            ans.add(link);
        }
        return ans;
    }
}
