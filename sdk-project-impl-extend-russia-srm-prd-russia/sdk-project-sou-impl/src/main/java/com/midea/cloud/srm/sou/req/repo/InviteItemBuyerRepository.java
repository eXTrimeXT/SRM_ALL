package com.midea.cloud.srm.sou.req.repo;

import cn.hutool.core.util.ObjectUtil;
import com.midea.cloud.meiql.api.enums.QlQueryFeature;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.sou.req.SouInviteHead;
import com.midea.cloud.srm.model.sou.req.SouInviteItem;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <pre>
 *  寻源需求邀请供应商行表
 * </pre>
 *
 * @author xiaym13@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/10/8 16:18
 *  修改内容:
 * </pre>
 */
@Slf4j
@Component
public class InviteItemBuyerRepository extends CrudRepository {
    @Autowired
    protected QlService qlService;

    @Override
    protected void beforeCreate(QlQueryAction queryAction, Collection<Record> records) {
        super.beforeCreate(queryAction, records);
        this.handleInviteId(records);
        this.handleCount(records, false);
    }


    @Override
    protected void beforeUpdate(QlQueryAction queryAction, Collection<Record> records) {
        super.beforeUpdate(queryAction, records);
        this.handleInviteId(records);
        this.handleCount(records, true);
    }

    private void handleCount(Collection<Record> records, boolean isUpdate) {
        List<SouInviteHead> souInviteHeads = new ArrayList<>();
        for (Record record : records) {
            Record oldRecord = isUpdate ? qlService.readByKey(MqlType.SOU_INVITE_ITEM_BUYER, record.get(SouInviteItem::getInviteItemId), Record.class) : null;
            SouInviteHead souInviteHead = qlService.readByKey(MqlType.SOU_INVITE_HEAD_BUYER, record.get(SouInviteItem::getInviteHeadId), SouInviteHead.class);
            SouInviteHead souInviteHeadUpdate = new SouInviteHead().setInviteHeadId(record.get(SouInviteItem::getInviteHeadId));
            //投标次数
            String isBid = record.get(SouInviteItem::getIsBid);
            if (ObjectUtil.isNotEmpty(isBid)) {
                if (oldRecord == null && isBid.equals(Enable.Y.name())) {
                    souInviteHeadUpdate.setBidCount(souInviteHead.getBidCount() + 1);
                }
                if (oldRecord != null) {
                    if (oldRecord.get(SouInviteItem::getIsBid).equals(Enable.Y.name()) && isBid.equals(Enable.N.name())) {
                        souInviteHeadUpdate.setBidCount(souInviteHead.getBidCount() - 1);
                    }
                    if (oldRecord.get(SouInviteItem::getIsBid).equals(Enable.N.name()) && isBid.equals(Enable.Y.name())) {
                        souInviteHeadUpdate.setBidCount(souInviteHead.getBidCount() + 1);
                    }
                }
            }
            //废标次数
            String isInvalidBid = record.get(SouInviteItem::getIsInvalidBid);
            if (ObjectUtil.isNotEmpty(isInvalidBid)) {
                if (oldRecord == null && isInvalidBid.equals(Enable.Y.name())) {
                    souInviteHeadUpdate.setInvalidBidCount(souInviteHead.getInvalidBidCount() + 1);
                }
                if (oldRecord != null) {
                    if (oldRecord.get(SouInviteItem::getIsInvalidBid).equals(Enable.Y.name()) && isInvalidBid.equals(Enable.N.name())) {
                        souInviteHeadUpdate.setInvalidBidCount(souInviteHead.getInvalidBidCount() - 1);
                    }
                    if (oldRecord.get(SouInviteItem::getIsInvalidBid).equals(Enable.N.name()) && isInvalidBid.equals(Enable.Y.name())) {
                        souInviteHeadUpdate.setInvalidBidCount(souInviteHead.getInvalidBidCount() + 1);
                    }
                }
            }
            //中标次数
            String isSuccBid = record.get(SouInviteItem::getIsSuccBid);
            if (ObjectUtil.isNotEmpty(isSuccBid)) {
                if (oldRecord == null && isSuccBid.equals(Enable.Y.name())) {
                    souInviteHeadUpdate.setSuccBidCount(souInviteHead.getSuccBidCount() + 1);
                }
                if (oldRecord != null) {
                    if (oldRecord.get(SouInviteItem::getIsSuccBid).equals(Enable.Y.name()) && isSuccBid.equals(Enable.N.name())) {
                        souInviteHeadUpdate.setSuccBidCount(souInviteHead.getSuccBidCount() - 1);
                    }
                    if (oldRecord.get(SouInviteItem::getIsSuccBid).equals(Enable.N.name()) && isSuccBid.equals(Enable.Y.name())) {
                        souInviteHeadUpdate.setSuccBidCount(souInviteHead.getSuccBidCount() + 1);
                    }
                }
            }
            souInviteHeads.add(souInviteHeadUpdate);
        }
        //更新头表数据
        qlService.update(MqlType.SOU_INVITE_HEAD_BUYER, souInviteHeads, QlQueryFeature.EXCLUDE_NULL);
    }


    private void handleInviteId(Collection<Record> records) {
        for (Record record : records) {
            if (ObjectUtil.isEmpty(record.get(SouInviteItem::getInviteHeadId))) {
                List<Record> result = qlService.queryPageByWrapper(QlWrappers.query(MqlType.SOU_INVITE_HEAD_BUYER)
                        .eq(SouInviteHead::getVendorId, record.get(SouInviteItem::getVendorId)), 1L, 1L, Record.class).getRecords();
                Assert.isTrue(ObjectUtil.isNotEmpty(result), "供应商的邀请数据为空，不允许新增详情数据。");
                //初始化头表主键。
                record.set(SouInviteItem::getInviteHeadId, result.get(0).get(SouInviteHead::getInviteHeadId));
            }
        }
    }
}
