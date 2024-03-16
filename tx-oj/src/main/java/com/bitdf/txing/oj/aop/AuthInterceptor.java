package com.bitdf.txing.oj.aop;

import com.bitdf.txing.oj.annotation.AuthCheck;
import com.bitdf.txing.oj.exception.BusinessException;
import com.bitdf.txing.oj.model.entity.user.User;
import com.bitdf.txing.oj.model.enume.TxCodeEnume;
import com.bitdf.txing.oj.model.enume.UserRoleEnum;
import com.bitdf.txing.oj.service.LoginService;
import com.bitdf.txing.oj.service.UserService;
import com.bitdf.txing.oj.service.cache.UserCache;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 权限校验 AOP
 *
 * @author Lizhiwei
 * @date 2023/1/24 3:44:13
 * 注释：
 */
@Aspect
@Component
public class AuthInterceptor {

    public static ThreadLocal<User> userThreadLocal = new ThreadLocal<User>();

    @Resource
    private UserService userService;
    @Autowired
    LoginService loginService;
    @Autowired
    UserCache userCache;

    /**
     * 执行拦截
     *
     * @param joinPoint
     * @param authCheck
     * @return
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        String mustRole = authCheck.mustRole();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        String token = request.getHeader("TOKEN");
        if (StringUtils.isBlank(token)) {
            token = request.getParameter("TOKEN");
        }
        Long userId = loginService.getValidUserId(token);
        if (userId == null) {
            throw new BusinessException(TxCodeEnume.COMMON_CUSTOM_EXCEPTION, "未登录异常");
        }
        // 当前登录用户
//        User loginUser = userService.getLoginUser(request);
        User loginUser = userCache.get(userId);
        // 必须有该权限才通过
        if (StringUtils.isNotBlank(mustRole) && !mustRole.equals("login")) {
            UserRoleEnum mustUserRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
            if (mustUserRoleEnum == null) {
                throw new BusinessException(TxCodeEnume.COMMON_NOT_PERM_EXCEPTION);
            }
            String userRole = loginUser.getUserRole();
            // 如果被封号，直接拒绝
            if (UserRoleEnum.BAN.equals(mustUserRoleEnum)) {
                throw new BusinessException(TxCodeEnume.COMMON_NOT_PERM_EXCEPTION);
            }
            // 必须有管理员权限
            if (UserRoleEnum.ADMIN.equals(mustUserRoleEnum)) {
                if (!mustRole.equals(userRole)) {
                    throw new BusinessException(TxCodeEnume.COMMON_NOT_PERM_EXCEPTION);
                }
            }
        }
        userThreadLocal.set(loginUser);
        // 通过权限校验，放行
        return joinPoint.proceed();
    }
}

