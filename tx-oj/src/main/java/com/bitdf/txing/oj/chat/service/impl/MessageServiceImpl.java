package com.bitdf.txing.oj.chat.service.impl;

import com.bitdf.txing.oj.chat.domain.vo.request.MessagePageRequest;
import com.bitdf.txing.oj.chat.mapper.MessageMapper;
import com.bitdf.txing.oj.model.dto.cursor.CursorPageBaseRequest;
import com.bitdf.txing.oj.model.entity.chat.Message;
import com.bitdf.txing.oj.model.vo.cursor.CursorPageBaseVO;
import com.bitdf.txing.oj.utils.CursorUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitdf.txing.oj.chat.service.MessageService;


@Service("messageService")
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

//    @Override
//    public PageUtils queryPage(Map<String, Object> params) {
//        IPage<MessageEntity> page = this.page(
//                new Query<MessageEntity>().getPage(params),
//                new QueryWrapper<MessageEntity>()
//        );
//
//        return new PageUtils(page);
//    }


    /**
     * 游标翻页
     * @param roomId
     * @param pageRequest
     * @param lastMsgTime
     * @return
     */
    @Override
    public CursorPageBaseVO<Message> getPageByCursor(Long roomId, CursorPageBaseRequest pageRequest, Date lastMsgTime) {
        CursorPageBaseVO<Message> cursorPageByMysql = CursorUtils.getCursorPageByMysql(this, pageRequest, wrapper -> {
            wrapper.eq(Message::getRoomId, roomId);
            wrapper.le(lastMsgTime != null, Message::getCreateTime, lastMsgTime);
        }, Message::getCreateTime);
        return cursorPageByMysql;
    }

    /**
     * 获取未读消息数目
     * @param roomId
     * @param readTime
     * @return
     */
    @Override
    public Integer getUnReadCount(Long roomId, Date readTime) {
        return lambdaQuery().eq(Message::getRoomId, roomId)
                .gt(Message::getCreateTime, readTime)
                .count();
    }
}
