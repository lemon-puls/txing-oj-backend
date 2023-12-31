package com.bitdf.txing.oj.service.impl;

import com.bitdf.txing.oj.chat.service.RoomService;
import com.bitdf.txing.oj.chat.service.adapter.MessageAdapter;
import com.bitdf.txing.oj.chat.service.business.ChatService;
import com.bitdf.txing.oj.event.UserApplyEvent;
import com.bitdf.txing.oj.model.entity.chat.RoomFriend;
import com.bitdf.txing.oj.model.enume.TxCodeEnume;
import com.bitdf.txing.oj.exception.ThrowUtils;
import com.bitdf.txing.oj.mapper.UserApplyMapper;
import com.bitdf.txing.oj.model.dto.user.UserApplyRequest;
import com.bitdf.txing.oj.model.entity.user.UserApply;
import com.bitdf.txing.oj.model.entity.user.UserFriend;
import com.bitdf.txing.oj.model.enume.UserApplyStatusEnum;
import com.bitdf.txing.oj.service.UserFriendService;
import com.bitdf.txing.oj.service.adapter.FriendAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitdf.txing.oj.service.UserApplyService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;


@Service("userApplyService")
public class UserApplyServiceImpl extends ServiceImpl<UserApplyMapper, UserApply> implements UserApplyService {

    @Autowired
    UserFriendService userFriendService;

    @Autowired
    ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    RoomService roomService;
    @Autowired
    ChatService chatService;

//    @Override
//    public PageUtils queryPage(Map<String, Object> params) {
//        IPage<UserApplyEntity> page = this.page(
//                new Query<UserApplyEntity>().getPage(params),
//                new QueryWrapper<UserApplyEntity>()
//        );
//
//        return new PageUtils(page);
//    }

    /**
     * 申请加为好友
     *
     * @param userId
     * @param request
     */
    @Override
    public void applyFriend(Long userId, UserApplyRequest request) {
        // 判断是否已是好友
        UserFriend userFriend = userFriendService.getByFriend(userId, request.getTargetUserId());
        ThrowUtils.throwIf(userFriend != null, TxCodeEnume.USER_ALEARDY_IS_FRIEND_EXCEPTION);
        // 存在申请记录 防止重复申请
        UserApply userApply = lambdaQuery().eq(UserApply::getUserId, userId)
                .eq(UserApply::getTargetId, request.getTargetUserId()).one();
        ThrowUtils.throwIf(userApply != null, TxCodeEnume.USER_REPEAT_APPLY_EXCEPTION);
        // 是否目标用户已申请过了 这样就直接同意对方的申请即可成为好友
        // TODO
        // 申请入库
        UserApply insert = FriendAdapter.buildFriendApply(userId, request);
        this.save(insert);
        // 触发申请事件
        applicationEventPublisher.publishEvent(new UserApplyEvent(this, insert));
    }

    /**
     * 同意好友申请
     *
     * @param userId
     * @param applyId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void agreeApply(Long userId, Long applyId) {
        // 判断是否合法
        UserApply userApply = this.getById(applyId);
        ThrowUtils.throwIf(userApply == null, "申请记录不存在");
        ThrowUtils.throwIf(!userApply.getTargetId().equals(userId), "无权限操作");
        ThrowUtils.throwIf(userApply.getStatus().equals(UserApplyStatusEnum.AGREE), "已同意，无需重复操作");
        // 同意申请
        lambdaUpdate().set(UserApply::getStatus, UserApplyStatusEnum.AGREE.getCode())
                .eq(UserApply::getId, applyId).update();
        // 创建双方好友关系
        userFriendService.createFriendRelate(userId, userApply.getUserId());
        // 创建聊天房间
        RoomFriend roomFriend = roomService.createRoomAndRoomFriend(Arrays.asList(userId, userApply.getUserId()));
        // 同意后发送一条招呼语 我们已经是好友啦，开始聊天吧！
        chatService.sendMsg(MessageAdapter.buildAgreeMessage(roomFriend.getRoomId()), userId);
    }

}