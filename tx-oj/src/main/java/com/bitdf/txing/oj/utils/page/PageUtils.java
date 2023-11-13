package com.bitdf.txing.oj.utils.page;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;

/**
 * @author Lizhiwei
 * @date 2023/8/22 11:13:25
 * @description 分页工具类
 */
@Slf4j
public class PageUtils implements Serializable {
    private static final long serialVersionUID = 1L;


    /**
     * 总记录数
     */
    private int total;
    /**
     * 每页记录数
     */
    private int size;
    /**
     * 总页数
     */
    private int pageCount;
    /**
     * 当前页数
     */
    private int current;
    /**
     * 列表数据
     */
    private List<?> list;

    /**
     * 分页
     *
     * @param list     列表数据
     * @param total    总记录数
     * @param size 每页记录数
     * @param current  当前页数
     */
    public PageUtils(List<?> list, int total, int size, int current) {
        this.list = list;
        this.total = total;
        this.size = size;
        this.current = current;
        this.pageCount = (int) Math.ceil((double) total / size);
    }

    public PageUtils() {
    }

    /**
     * 分页
     */
    public PageUtils(IPage<?> page) {
        this.list = page.getRecords();
        this.total = (int) page.getTotal();
        this.size = (int) page.getSize();
        this.current = (int) page.getCurrent();
        this.pageCount = (int) page.getPages();
    }

    public int gettotal() {
        return total;
    }

    public void settotal(int total) {
        this.total = total;
    }

    public int getsize() {
        return size;
    }

    public void setsize(int size) {
        this.size = size;
    }

    public int getpageCount() {
        return pageCount;
    }

    public void setpageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getcurrent() {
        return current;
    }

    public void setcurrent(int current) {
        this.current = current;
    }

    public List<?> getList() {
        return list;
    }

    public void setList(List<?> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

}