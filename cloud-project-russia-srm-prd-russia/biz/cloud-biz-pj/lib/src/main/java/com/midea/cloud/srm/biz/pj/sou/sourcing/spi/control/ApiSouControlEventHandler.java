package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.control;

import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.control.itemrefresh.SouItemRefreshPO;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.control.vendoradd.SouVendorAddPO;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.control.vendorrecord.SouVendorRecordPO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.control.*;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProcessConfig;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.typehandler.SouPwdInfoVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 寻源openAPI - 流程控制业务
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/01
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ApiSouControlEventHandler implements ISouSpiBean {

    @ApiOperation("修改报价开始时间前的额外操作")
    public void doHandlerBeforeChangeOrderStartTime(ApiSouChangeOrderStartTimeDTO param, String souType) {
    }

    @ApiOperation("修改报价开始时间后的额外操作")
    public void doHandlerAfterChangeOrderStartTime(ApiSouChangeOrderStartTimeDTO param, String souType) {
    }

    @ApiOperation("修改报价截止时间前的额外操作")
    public void doHandlerBeforeChangeOrderEndTime(ApiSouChangeOrderEndTimeDTO param, String souType) {
    }

    @ApiOperation("修改报价截止时间后的额外操作")
    public void doHandlerAfterChangeOrderEndTime(ApiSouChangeOrderEndTimeDTO param, String souType) {
    }

    @ApiOperation("修改最早开标时间前的额外操作")
    public void doHandlerBeforeChangeEarliestBusinessOpenTime(ApiSouChangeEarliestBusinessOpenTimeDTO param, String souType) {
    }

    @ApiOperation("修改最早开标时间后的额外操作")
    public void doHandlerAfterChangeEarliestBusinessOpenTime(ApiSouChangeEarliestBusinessOpenTimeDTO param, String souType) {
    }

    @ApiOperation("开标密码生成器")
    public Map<String/* openBidType */, String/* pwd */> doHandlerForGenerateOpenBidPwd(ApiSouBidPwdGenerateDTO param, String souType) {
        Map<String/* openBidType */, String/* pwd */> pwdMap = new HashMap<>(param.getOpenBidTypes().size());
        param.getOpenBidTypes().forEach(type -> pwdMap.put(type, String.valueOf(RandomUtils.nextLong(99999, 1000000))));
        return pwdMap;
    }

    @ApiOperation("生成开标密码前的额外处理")
    public void doHandlerBeforeGenerateBidPwd(ApiSouBidPwdGenerateDTO param, String souType) {
    }

    @ApiOperation("生成开标密码后的额外处理")
    public void doHandlerAfterGenerateBidPwd(ApiSouBidPwdGenerateDTO param, String souType, Map<String/* operateAuth */, SouPwdInfoVO> pwdMap) {
    }

    @ApiOperation("确认开标密码前的额外操作")
    public void doHandlerBeforeConformOpeningBid(ApiSouOpenBidDTO param, String souType) {
    }

    @ApiOperation("确认开标密码后的额外操作")
    public void doHandlerAfterConformOpeningBid(ApiSouOpenBidDTO param, String souType) {
    }

    @ApiOperation("商务开标前的额外处理")
    public void doHandlerBeforeBusinessOpen(ApiSouBusinessOpenDTO param, String souType) {
    }

    @ApiOperation("商务开标后的额外处理")
    public void doHandlerAfterBusinessOpen(ApiSouBusinessOpenDTO param, String souType) {
    }

    @ApiOperation("报价解密前的额外处理")
    public void doHandlerBeforeDecryptPrice(ApiSouDecryptPriceDTO param, String souType) {
    }

    @ApiOperation("报价解密后的额外处理")
    public void doHandlerAfterDecryptPrice(ApiSouDecryptPriceDTO param, String souType) {
    }

    @ApiOperation("发起新一轮前的额外处理")
    public void doHandlerBeforeStartNewRound(ApiSouStartNewRoundDTO param, String souType) {
    }

    @ApiOperation("发起新一轮后的额外处理")
    public void doHandlerAfterStartNewRound(ApiSouStartNewRoundDTO param, String souType) {
    }

    @ApiOperation("报名时间截止后的额外处理")
    public void doHandlerAfterSignUpEnd(SouProject souProject, SouProcessConfig souProcessConfig) {
    }

    @ApiOperation("报价开始前的额外处理")
    public void doHandlerAfterOrderStart(SouProject souProject, SouProcessConfig souProcessConfig) {
    }

    @ApiOperation("报价截止后的额外处理")
    public void doHandlerAfterOrderEnd(SouProject souProject, SouProcessConfig souProcessConfig) {
    }

    @ApiOperation("记录物料刷新信息前的额外处理")
    public void doHandlerBeforeRecordItemRefreshInfo(ApiSouItemRecordDTO param, String souType) {
    }

    @ApiOperation("记录物料刷新信息后的额外处理")
    public void doHandlerAfterRecordItemRefreshInfo(ApiSouItemRecordDTO param, String souType) {
    }

    @ApiOperation("刷新物料前的额外处理")
    public void doHandlerBeforeItemRefresh(ApiSouItemRefreshDTO param, String souType) {
    }

    @ApiOperation("刷新物料后的额外处理")
    public void doHandlerAfterItemRefresh(ApiSouItemRefreshDTO param, String souType, SouItemRefreshPO po) {
    }

    @ApiOperation("记录追加供应商信息前的额外处理")
    public void doHandlerBeforeRecordVendorAddInfo(ApiSouVendorRecordDTO param, String souType) {
    }

    @ApiOperation("记录追加供应商信息后的额外处理")
    public void doHandlerAfterRecordVendorAddInfo(ApiSouVendorRecordDTO param, String souType, SouVendorRecordPO po) {
    }

    @ApiOperation("执行追加供应商前的额外处理")
    public void doHandlerBeforeVendorAdd(ApiSouVendorAddDTO param, String souType) {
    }

    @ApiOperation("执行追加供应商后的额外处理")
    public void doHandlerAfterVendorAdd(ApiSouVendorAddDTO param, String souType, SouVendorAddPO po) {
    }

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
