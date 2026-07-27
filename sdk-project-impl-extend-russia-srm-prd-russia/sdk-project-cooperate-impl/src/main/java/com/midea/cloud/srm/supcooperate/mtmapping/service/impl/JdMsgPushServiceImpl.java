package com.midea.cloud.srm.supcooperate.mtmapping.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.component.aop.lock.SyncLock;
import com.midea.cloud.srm.mall.request.jd.order.DelMessageRequestDTO;
import com.midea.cloud.srm.mall.request.jd.order.GetMessageRequestDTO;
import com.midea.cloud.srm.mall.result.jd.common.GetMessageResultDTO;
import com.midea.cloud.srm.mall.service.jd.MallService;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.supcooperate.enums.MallTypeEnum;
import com.midea.cloud.srm.model.supcooperate.ext.JdMsgPush;
import com.midea.cloud.srm.supcooperate.mtmapping.mapper.JdMsgPushMapper;
import com.midea.cloud.srm.supcooperate.mtmapping.service.JdMsgPushService;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 京东推送信息表（内部商城）
 *
 * @author xiaym13 xiaym13@meicloud.com
 * @since 1.0.0 2024-03-12
 */
@Service
@AllArgsConstructor
public class JdMsgPushServiceImpl extends BaseServiceImpl<JdMsgPushMapper, JdMsgPush> implements JdMsgPushService {

    @Autowired
    private MallService mallService;

    @SyncLock( // 禁止同时访问该接口
            moduleName = "SAVE_AND_DELETE_JD_MSG",
            allowInTx = true,
            lockFailureMsg = "正在执行中，请稍后")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveAndDeleteJdMsg() {
        //拉取推送信息
        GetMessageRequestDTO getMessageRequestDTO = new GetMessageRequestDTO();
        getMessageRequestDTO.setType("12");
        getMessageRequestDTO.setMallType(MallTypeEnum.JD.getCode());
        GetMessageResultDTO getMessageResultDTO = mallService.getMessage(getMessageRequestDTO);
        if (CollectionUtils.isEmpty(getMessageResultDTO.getResult())) {
            return;
        }
        List<String> SubOrderIds = getMessageResultDTO.getResult().stream().map(et -> et.getResult().getOrderId()).collect(Collectors.toList());
        //查询推送表符合条件的数据
        List<JdMsgPush> jdMsgPushList = this.list(new LambdaQueryWrapper<JdMsgPush>()
                .eq(JdMsgPush::getSendFlag, Enable.N.name())
                .in(JdMsgPush::getSubOrderId, SubOrderIds));
        //TODO 生成送货单


        //删除推送信息（防止重复拉取）
        List<String> pushIds = getMessageResultDTO.getResult().stream().map(et -> et.getId().toString()).collect(Collectors.toList());
        DelMessageRequestDTO delMessageRequestDTO = new DelMessageRequestDTO();
        delMessageRequestDTO.setMallType(MallTypeEnum.JD.getCode());
        delMessageRequestDTO.setId(String.join(",", pushIds));
        mallService.delMessage(delMessageRequestDTO);
        //更新信息
        jdMsgPushList.forEach(l -> {
            l.setSendFlag(Enable.Y.name());
            l.setDeleteFlag(Enable.Y.name());
            l.setDeliveryFlag(Enable.Y.name());
        });
        this.updateBatchById(jdMsgPushList);
    }
}
