package com.bitdf.txing.oj.utils.page;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitdf.txing.oj.utils.CamelCaseConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Set;

/**
 * @author Lizhiwei
 * @date 2023/8/22 11:14:17
 * @description 查询参数
 */
@Slf4j
public class Query<T> {

    public IPage<T> getPage(PageVO pageModel) {
        return this.getPage(pageModel, null, false);
    }

    public IPage<T> getPage(PageVO pageModel, String defaultOrderField, boolean isAsc) throws NumberFormatException {
        //分页参数
        long curPage = (pageModel.getPage() != null && pageModel.getPage().getCurrent() != null) ? pageModel.getPage().getCurrent() : 1;
        long limit = (pageModel.getPage() != null && pageModel.getPage().getPageSize() != null) ? pageModel.getPage().getPageSize() : 10;

        //分页对象
        Page<T> page = new Page<>(curPage, limit);

        if (pageModel.getSorts() != null && pageModel.getSorts().length > 0) {
            for (PageVO.Sort sort : pageModel.getSorts()) {
                //排序字段
                String orderField = CamelCaseConvertUtil.toUnderScoreCase(sort.getSortName());
                //防止SQL注入（因为sidx、order是通过拼接SQL实现排序的，会有SQL注入风险）
                orderField = SQLFilter.sqlInject(orderField);
                //前端字段排序
                if (StringUtils.isNotEmpty(orderField) && sort.getIsAsc() != null) {
                    page.addOrder(new OrderItem(orderField, sort.getIsAsc()));
                }
            }
        } else {
            //没有排序字段，则不排序
            if (StringUtils.isBlank(defaultOrderField)) {
                return page;
            } else {
                page.addOrder(new OrderItem(defaultOrderField, isAsc));
            }
        }
        return page;
    }

    /**
     * 并且分页查询条件，并且构造IPage对象
     *
     * @param wrapper
     * @param pageVO
     * @param excludeFields 添加排除字段时 记得要写下划线形式（和数据库字段对应）
     * @return
     */
    public IPage<T> buildWrapperAndPage(QueryWrapper wrapper, PageVO pageVO, Set<String> excludeFields) {
        // 构建QueryWrapper
        buildWrapper(wrapper, pageVO.getFilter(), excludeFields);
        // 构建IPage 默认排序 ： 按时间倒序
        return getPage(pageVO, "create_time", false);
    }

    /**
     * 构建QueryWrapper
     *
     * @param wrapper
     * @param list
     * @return
     */
    public static boolean buildWrapper(QueryWrapper wrapper, List<FilterVO> list, Set<String> excludeFields) {
        if (list == null || list.isEmpty()) {
            return true;
        }
        for (FilterVO filterVO : list) {
            // 转换为下划线，以防前端传过来的是驼峰形式
            String fieldName = CamelCaseConvertUtil.toUnderScoreCase(filterVO.getFieldName());
            if (excludeFields != null && excludeFields.contains(fieldName)) {
                continue;
            }
            addCondition(wrapper, fieldName, filterVO.getValue(), filterVO.getQueryType());
        }
        return true;
    }

    /**
     * 添加查询条件
     *
     * @param wrapper
     * @param fieldName
     * @param value
     * @param queryTpe
     * @return
     */
    public static boolean addCondition(QueryWrapper wrapper, String fieldName, String value, String queryTpe) {
        switch (queryTpe) {
            case FilterVO.eq:
                wrapper.eq(fieldName, value);
                break;
            case FilterVO.ge:
                wrapper.ge(fieldName, value);
                break;
            case FilterVO.le:
                wrapper.le(fieldName, value);
                break;
            case FilterVO.like:
                if (StringUtils.isNotBlank(value)) {
                    wrapper.like(fieldName, value);
                }
                break;
            case FilterVO.between:
                String[] s = value.split("_");
                if ("".equals(s[0]) && !"".equals(s[1])) {
                    wrapper.le(fieldName, s[1]);
                } else if ("".equals(s[1]) && !"".equals(s[0])) {
                    wrapper.ge(fieldName, s[0]);
                } else if (!"".equals(s[0]) && !"".equals(s[1])) {
                    wrapper.between(fieldName, s[0], s[1]);
                }
                break;
            case FilterVO.in:
                String[] split = value.split("_");
                if (split != null && split.length != 0) {
                    wrapper.in(fieldName, split);
                }
                break;
            case FilterVO.ne:
                wrapper.ne(fieldName, value);
                break;
            case FilterVO.notIn:
                String[] split1 = value.split("_");
                wrapper.notIn(fieldName, split1);
                break;
            default:
                log.info("[分页查询] ==> fieldName: {} value: {} ==> 查询条件没成功添加");
                return false;
        }
        return true;
    }

}
