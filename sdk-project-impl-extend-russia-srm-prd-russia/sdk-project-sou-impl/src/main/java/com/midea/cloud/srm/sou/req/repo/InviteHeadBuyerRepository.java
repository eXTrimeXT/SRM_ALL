package com.midea.cloud.srm.sou.req.repo;

import cn.hutool.core.util.ObjectUtil;
import com.midea.cloud.meiql.api.action.DefaultAction;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.ProxyQlQueryAction;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.meiql.core.repository.jooq.support.QueryParam;
import com.midea.cloud.meiql.core.util.ResultUtil;
import com.midea.cloud.srm.model.sou.req.SouInviteHead;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.sou.req.mapper.SouInviteHeadMapper;
import com.midea.cloud.srm.sou.req.service.SouInviteHeadService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <pre>
 *  寻源需求邀请供应商头
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
public class InviteHeadBuyerRepository extends CrudRepository {
    @Autowired
    protected QlService qlService;
    @Autowired
    private SouInviteHeadMapper inviteHeadMapper;
    @Autowired
    private QlOpenClient qlOpenClient;
    @Autowired
    private SouInviteHeadService souInviteHeadService;
    public InviteHeadBuyerRepository() {
        //注册action
        this.register("saveOrUpdate", this::saveOrUpdate, true, "保存");
        this.register("listPage", this::listPage, false, "列表查询");
    }

    private QlResult listPage(QlQueryAction queryAction) {
        QueryParam queryParam = getPayloadForType(queryAction, QueryParam.class);
        SouInviteHead params = queryParam.getFilter().convertWithoutOperator(SouInviteHead.class);
        params.setPageSize(queryParam.getPage().getPageSize());
        params.setPageNum(queryParam.getPage().getPageNum());
        return ResultUtil.build(queryAction, "inviteHeadId", souInviteHeadService.listPage(params), false);
    }

    public QlResult saveOrUpdate(QlQueryAction queryAction) {
        List<Record> records = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        Assert.isTrue(ObjectUtil.isNotEmpty(records), "数据不能为空。");
        //初始化供应商id集合
        List<Long> vendorIds = records.stream().map(record -> record.get(SouInviteHead::getVendorId)).collect(Collectors.toList());
        //根据供应商id，查询邀请供应商表数据
        List<SouInviteHead> inviteHeadList = qlService.queryByWrapper(QlWrappers.query(MqlType.SOU_INVITE_HEAD_BUYER)
                        .in(SouInviteHead::getVendorId, vendorIds)
                , SouInviteHead.class);
        // 将 SouInviteHead 列表转换为 Map，以 vendorId 作为 key，以 inviteHeadId 作为 value
        Map<Long, SouInviteHead> souInviteHeadMap = inviteHeadList.stream().collect(Collectors.toMap(SouInviteHead::getVendorId, Function.identity()));
        // 遍历 Record 列表并更新对应的 inviteHeadId 字段
        records.forEach(record -> {
            SouInviteHead inviteHead = souInviteHeadMap.get(record.get(SouInviteHead::getVendorId));
            if (ObjectUtil.isNotEmpty(inviteHead)) {
                record.set(SouInviteHead::getInviteHeadId, inviteHead.getInviteHeadId());//主键
                record.set(SouInviteHead::getBidCount, addInteger(inviteHead.getBidCount(), record.get(SouInviteHead::getBidCount)));//投标次数
                record.set(SouInviteHead::getSuccBidCount, addInteger(inviteHead.getSuccBidCount() , record.get(SouInviteHead::getSuccBidCount)));//中标次数
                record.set(SouInviteHead::getInvalidBidCount, addInteger(inviteHead.getInvalidBidCount() , record.get(SouInviteHead::getInvalidBidCount)));//废标次数
            }
        });
        //检查数据库是否已经存在该供应商
        return super.save(ProxyQlQueryAction.proxy(queryAction, DefaultAction.SAVE.value(), records));
    }

    protected Integer addInteger(Integer value1, Integer value2) {
        return ObjectUtils.defaultIfNull(value1, 0) + ObjectUtils.defaultIfNull(value2, 0);
    }
}
