package com.bitdf.txing.oj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitdf.txing.oj.aop.AuthInterceptor;
import com.bitdf.txing.oj.constant.CommonConstant;
import com.bitdf.txing.oj.exception.BusinessException;
import com.bitdf.txing.oj.exception.ThrowUtils;
import com.bitdf.txing.oj.mapper.UserMapper;
import com.bitdf.txing.oj.model.dto.cursor.CursorPageBaseRequest;
import com.bitdf.txing.oj.model.dto.user.UserModifyPwdRequest;
import com.bitdf.txing.oj.model.dto.user.UserQueryRequest;
import com.bitdf.txing.oj.model.dto.user.UserVOBatchRequest;
import com.bitdf.txing.oj.model.entity.user.User;
import com.bitdf.txing.oj.model.enume.TxCodeEnume;
import com.bitdf.txing.oj.model.enume.UserActiveStatusEnum;
import com.bitdf.txing.oj.model.enume.UserRoleEnum;
import com.bitdf.txing.oj.model.vo.cursor.CursorPageBaseVO;
import com.bitdf.txing.oj.model.vo.user.LoginUserVO;
import com.bitdf.txing.oj.model.vo.user.UserVO;
import com.bitdf.txing.oj.service.UserService;
import com.bitdf.txing.oj.service.cache.UserCache;
import com.bitdf.txing.oj.service.cache.UserRelateCache;
import com.bitdf.txing.oj.utils.CursorUtils;
import com.bitdf.txing.oj.utils.page.SQLFilter;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.bitdf.txing.oj.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现
 *
 * @author Lizhiwei
 * @date 2023/1/24 3:44:13
 * 注释：
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "lizhiwei";

    @Autowired
    UserRelateCache userRelateCache;
    @Autowired
    @Lazy
    UserCache userCache;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION, "两次输入的密码不一致");
        }
        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_account", userAccount);
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION, "账号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 3. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            // 默认头像
            user.setUserAvatar("https://txing-oj-1311424669.cos.ap-guangzhou.myqcloud.com/user_avatar/1/DLYv6zO6-42f3f796a326707a796ec644af28e1a1.jpg");
            // 默认昵称
            user.setUserName(userAccount);
            // 默认权限
            user.setUserRole("user");
            user.setQuestionCount(0);
            user.setSubmitCount(0);
            user.setAcceptedCount(0);
            user.setAcceptedRate(0f);
            user.setSchool("未完善");
            user.setProfession("未完善");
            user.setPersonSign("此用户很懒 什么也没有留下！");

            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(TxCodeEnume.COMMON_SYSTEM_UNKNOWN_EXCEPTION, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION, "密码错误");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        queryWrapper.eq("user_password", encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION, "用户不存在或密码错误");
        }
        // 3. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return this.getLoginUserVO(user);
    }

    @Override
    public LoginUserVO userLoginByMpOpen(WxOAuth2UserInfo wxOAuth2UserInfo, HttpServletRequest request) {
        String unionId = wxOAuth2UserInfo.getUnionId();
        String mpOpenId = wxOAuth2UserInfo.getOpenid();
        // 单机锁
        synchronized (unionId.intern()) {
            // 查询用户是否已存在
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("union_id", unionId);
            User user = this.getOne(queryWrapper);
            // 被封号，禁止登录
            if (user != null && UserRoleEnum.BAN.getValue().equals(user.getUserRole())) {
                throw new BusinessException(TxCodeEnume.COMMON_FORBIDDEN_EXCEPTION, "该用户已被封，禁止登录");
            }
            // 用户不存在则创建
            if (user == null) {
                user = new User();
                user.setUnionId(unionId);
                user.setMpOpenId(mpOpenId);
                user.setUserAvatar(wxOAuth2UserInfo.getHeadImgUrl());
                user.setUserName(wxOAuth2UserInfo.getNickname());
                boolean result = this.save(user);
                if (!result) {
                    throw new BusinessException(TxCodeEnume.COMMON_SYSTEM_UNKNOWN_EXCEPTION, "登录失败");
                }
            }
            // 记录用户的登录态
            request.getSession().setAttribute(USER_LOGIN_STATE, user);
            return getLoginUserVO(user);
        }
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(TxCodeEnume.COMMON_NOT_LOGIN_EXCEPTION);
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(TxCodeEnume.COMMON_NOT_LOGIN_EXCEPTION);
        }
        return currentUser;
    }

    /**
     * 获取当前登录用户（允许未登录）
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUserPermitNull(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            return null;
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        return this.getById(userId);
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return isAdmin(user);
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
            throw new BusinessException(TxCodeEnume.COMMON_NOT_LOGIN_EXCEPTION, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVO(List<User> userList) {
        if (CollectionUtils.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(TxCodeEnume.COMMON_SUBMIT_DATA_EXCEPTION, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String unionId = userQueryRequest.getUnionId();
        String mpOpenId = userQueryRequest.getMpOpenId();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(StringUtils.isNotBlank(unionId), "union_id", unionId);
        queryWrapper.eq(StringUtils.isNotBlank(mpOpenId), "mpOpen_id", mpOpenId);
        queryWrapper.eq(StringUtils.isNotBlank(userRole), "user_role", userRole);
        queryWrapper.like(StringUtils.isNotBlank(userProfile), "user_profile", userProfile);
        queryWrapper.like(StringUtils.isNotBlank(userName), "user_name", userName);
        queryWrapper.orderBy(SQLFilter.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 修改密码
     *
     * @param userModifyPwdRequest
     * @return
     */
    @Override
    public boolean modifyPwd(UserModifyPwdRequest userModifyPwdRequest) {
        // 1、判断密码是否相等
        if (!userModifyPwdRequest.getUserPassword().equals(userModifyPwdRequest.getCheckPassword())) {
            throw new BusinessException(TxCodeEnume.USER_PWD_INCONSISTENT_EXCEPTION);
        }
        // 2、判断密码是否正确
        User loginUser = AuthInterceptor.userThreadLocal.get();
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userModifyPwdRequest.getOldPassword()).getBytes());
        User user = this.getOne(new QueryWrapper<User>().lambda().eq(User::getId, loginUser.getId()).eq(User::getUserPassword, encryptPassword));
        ThrowUtils.throwIf(user == null, TxCodeEnume.USER_PWD_ERROR_EXCEPTION);
        // 3、修改
        String digestAsHex = DigestUtils.md5DigestAsHex((SALT + userModifyPwdRequest.getUserPassword()).getBytes());
        user.setUserPassword(digestAsHex);
        boolean b = this.updateById(user);
        return b;
    }

    /**
     * 获取当前登录用户（不会抛异常）
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUserNoThrow(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        return currentUser;
    }

    /**
     * @param friendIds
     * @return
     */
    @Override
    public List<User> getFriendsByIds(List<Long> friendIds) {
        return lambdaQuery().in(User::getId, friendIds)
                .select(User::getId, User::getActiveStatus, User::getUserAvatar)
                .list();
    }

    /**
     * 获取在线人数
     *
     * @param memberIdList
     * @return
     */
    @Override
    public Integer getGroupOnlineCount(List<Long> memberIdList) {
        return lambdaQuery().eq(User::getActiveStatus, UserActiveStatusEnum.ONLINE.getCode())
                .in(CollectionUtils.isNotEmpty(memberIdList), User::getId, memberIdList)
                .count();
    }

    /**
     * 游标查询群组成员信息
     *
     * @param memberIdList
     * @param cursorPageBaseRequest
     * @return
     */
    @Override
    public CursorPageBaseVO<User> getMemberPageByCursor(List<Long> memberIdList, CursorPageBaseRequest cursorPageBaseRequest) {
        CursorPageBaseVO<User> cursorPageBaseVO = CursorUtils.getCursorPageByMysql(this, cursorPageBaseRequest, wrapper -> {
            wrapper.in(memberIdList != null, User::getId, memberIdList);
        }, User::getCreateTime);
        return cursorPageBaseVO;
    }

    @Override
    public List<UserVO> getUserVOBatch(UserVOBatchRequest request) {
        // 获取需要同步到前端的用户ID集合
        List<Long> userList = getNeedSyncUserIds(request.getRequestList());
        // 加载用户信息
        Map<Long, User> batch = userCache.getBatch(userList);
        List<UserVO> userVOS = request.getRequestList().stream().map(itemRequest -> {
            if (batch.containsKey(itemRequest.getUserId())) {
                return getUserVO(batch.get(itemRequest.getUserId()));
            } else {
                return UserVO.skip(itemRequest.getUserId());
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
        return userVOS;
    }

    private List<Long> getNeedSyncUserIds(List<UserVOBatchRequest.ItemRequest> request) {
        List<Long> needSyncUserIds = new ArrayList<>();
        List<Long> userModifyTimeBatch = userRelateCache.getUserModifyTimeBatch(request.stream()
                .map(UserVOBatchRequest.ItemRequest::getUserId).collect(Collectors.toList()));
        for (int i = 0; i < request.size(); i++) {
            UserVOBatchRequest.ItemRequest itemRequest = request.get(i);
            Long modifyTime = userModifyTimeBatch.get(i);
            if (Objects.isNull(itemRequest.getLastModifyTime()) || (Objects.nonNull(modifyTime) && modifyTime > itemRequest.getLastModifyTime())) {
                needSyncUserIds.add(itemRequest.getUserId());
            }
        }
        return needSyncUserIds;
    }
}
