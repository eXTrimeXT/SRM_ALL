package com.midea.cloud.srm.biz.pj.sou.quotetemplate.validator;

import com.midea.cloud.common.constants.SequenceCodeConstant;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.dao.SouQuoteTempAttrRepositoryImpl;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.dao.SouQuoteTempRepositoryImpl;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.service.impl.SouQuoteTempServiceImpl;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.bid.quotetemplate.dto.SouQuoteTempEditDTO;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTemp;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTempAttr;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTempLine;
import com.midea.cloud.srm.model.bid.quotetemplate.enums.SouQuoteTempRelateTypeEnum;
import com.midea.cloud.srm.model.bid.quotetemplate.enums.SouQuoteTempStatusEnum;
import com.midea.cloud.srm.model.bid.quotetemplate.vo.SouQuoteTempAttrRelateVO;
import com.midea.cloud.srm.model.bid.quotetemplate.vo.SouQuoteTempAttrTreeVO;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 寻源 - 模型报价模板 - 校验服务
 *
 * @author zhangwk12@midea.com
 * @since 2022/08/06
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class SouQuoteTempValidator {

    @Autowired
    private SouQuoteTempRepositoryImpl souQuoteTempRepository;
    @Autowired
    private SouQuoteTempServiceImpl souQuoteTempService;
    @Autowired
    private SouQuoteTempAttrRepositoryImpl souQuoteTempAttrRepository;
    @Autowired
    private BaseClient baseClient;

    public SouQuoteTempEditPO formatValidateAndConvert(SouQuoteTempEditDTO param, boolean isTempSave) {
        // 1: 数据格式化及校验
        this.formatAndValidate(param, isTempSave);
        // 2: 数据转换
        SouQuoteTempEditPO po = this.convert(param, isTempSave);
        // 3: 确保引入的报价属性都是完整的，并且没有循环引用
        this.validateAttrRelation(po);

        return po;
    }

    /**
     * 数据格式化及校验
     * @param param
     * @param isTempSave
     */
    private void formatAndValidate(SouQuoteTempEditDTO param, boolean isTempSave) {
        // 1: 格式化及校验模板头信息
        this.doFormatAndValidateTemp(param.getTemp(), isTempSave);
        // 2: 格式化及校验模板明细信息
        this.doFormatAndValidateTempLines(param.getTempLineList(), isTempSave);
    }

    private void doFormatAndValidateTemp(@Nullable SouQuoteTemp temp, boolean isTempSave) {
        AssertUtils.notNull(temp, "缺少temp参数信息");
        // 模板名称
        temp.setTempName(StringUtils.trimToNull(temp.getTempName()));
        AssertUtils.notNull(temp.getTempName(), "请输入模板名称");
        AssertUtils.isTrue(temp.getTempName().length() <= 100, "模板名称的长度不能超过100");
        long existCount = souQuoteTempRepository.lambdaQuery()
                    .ne(temp.getTempId() != null, SouQuoteTemp::getTempId, temp.getTempId())
                    .eq(SouQuoteTemp::getTempName, temp.getTempName())
                    .count();
        AssertUtils.isTrue(existCount <= 0, "模板名称不能重复");
        // 报价模板的关联类型
        if (temp.getTempRelateType() == null) {
            temp.setTempRelateType(SouQuoteTempRelateTypeEnum.ALL_DIM);
        }
    }

    private void doFormatAndValidateTempLines(List<SouQuoteTempLine> tempLineList, boolean isTempSave) {
        if (CollectionUtils.isEmpty(tempLineList)) {
            AssertUtils.isTrue(isTempSave, "请选择报价属性");
            return;
        }
        for (SouQuoteTempLine line : tempLineList) {
            // 1: ID(略)
            // 2: 关联模板表ID(略)
            // 3: 属性ID
            AssertUtils.notNull(line.getAttrId(), "模板明细缺少attrId参数");
            // 4: 属性编码(略 - 根据属性ID带出)
            line.setAttrNo(null);
            // 5: 属性名称(略 - 根据属性ID带出)
            line.setAttrName(null);
            // 6: 是否必填
            if (line.getRequired() == null) {
                line.setRequired(Enable.Y);
            }
            // 7: 是否总价
            if (line.getIsTotal() == null) {
                line.setIsTotal(Enable.N);
            }
            // 8: 排序(置空 - 后端处理)
            line.setSortIndex(null);
            // 9: 模板属性执行顺序(置空 - 稍后处理)
            line.setExecuteIndex(null);
        }
        long totalTempLineCount = tempLineList.stream().map(SouQuoteTempLine::getIsTotal).filter(Enable.Y::equals).count();
        if (totalTempLineCount > 0) {
            AssertUtils.isTrue(totalTempLineCount == 1, "模板明细中仅有一个成本属性可作为最终总价");
        }
    }

    /**
     * 数据转换
     * @param param
     * @param isTempSave
     * @return
     */
    private SouQuoteTempEditPO convert(SouQuoteTempEditDTO param, boolean isTempSave) {
        SouQuoteTempEditPO po = new SouQuoteTempEditPO();
        // 1: 转换得到模板头信息
        po.setTemp(this.doConvertTemp(param.getTemp(), isTempSave));
        // 2: 转换得到模板明细信息
        po.setTempLineList(this.doConvertTempLines(po.getTemp().getTempId(), param.getTempLineList(), isTempSave));
        return po;
    }

    private SouQuoteTemp doConvertTemp(SouQuoteTemp temp, boolean isTempSave) {
        SouQuoteTemp entity;
        if (temp.getTempId() == null) {
            entity = new SouQuoteTemp();
            BaseEntity.copyFieldValuesOnlySpecified(entity, temp,
                    SouQuoteTemp::getTempName);
            // ID
            entity.setTempId(IdGenrator.generate());
            // 模板编码
            entity.setTempNo(baseClient.seqGen(SequenceCodeConstant.SOU.SOU_QUOTE_TEMP_NO));
        } else {
            entity = souQuoteTempRepository.getById(temp.getTempId());
            entity.setTempName(temp.getTempName());
        }
        entity.setTempRelateType(temp.getTempRelateType());
        entity.setTempStatus(isTempSave ? SouQuoteTempStatusEnum.DRAFT : SouQuoteTempStatusEnum.VALID);
        return entity;
    }

    private List<SouQuoteTempLine> doConvertTempLines(long tempId, @Nullable List<SouQuoteTempLine> tempLineList, boolean isTempSave) {
        if (CollectionUtils.isEmpty(tempLineList)) { return null; }

        Map<Long/* attrId */, SouQuoteTempAttr> attrMap = souQuoteTempAttrRepository.listByIds(
                tempLineList.stream().map(SouQuoteTempLine::getAttrId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(SouQuoteTempAttr::getAttrId, Function.identity()));

        int index = 0;
        for (SouQuoteTempLine tempLine : tempLineList) {
            // ID
            if (tempLine.getTempLineId() == null) {
                tempLine.setTempLineId(IdGenrator.generate());
            }
            // 模板表ID
            tempLine.setTempId(tempId);
            // 属性编码
            SouQuoteTempAttr attr = attrMap.get(tempLine.getAttrId());
            AssertUtils.notNull(attr, LocaleHandler.getLocaleMsg("报价属性[{0}]不存在"), tempLine.getAttrId());
            tempLine.setAttrNo(attr.getAttrNo());
            // 属性名称
            tempLine.setAttrName(attr.getAttrName());
            // 排序
            tempLine.setSortIndex(index++);
        }

        return tempLineList;
    }

    private void validateAttrRelation(SouQuoteTempEditPO po) {
        if (CollectionUtils.isEmpty(po.getTempLineList())) { return; }
        // 获取报价属性引用树，并确保引用属性遗漏+不存在循环引用
        SouQuoteTempAttrRelateVO attrRelateVO = souQuoteTempService.checkTempAttrs(po.getTempLineList().stream().map(SouQuoteTempLine::getAttrId)
                .collect(Collectors.toSet()));
        // 获取推荐的报价属性执行顺序
        Map<String/* node */, Integer/* index */> executeIndexMap = attrRelateVO.getExecuteIndex();
        int maxIndex = -1;
        for (Integer i : executeIndexMap.values()) {
            if (i > maxIndex) {
                maxIndex = i;
            }
        }
        for (SouQuoteTempLine tempLine : po.getTempLineList()) {
            tempLine.setExecuteIndex(executeIndexMap.get(tempLine.getAttrName()));
            if (tempLine.getExecuteIndex() == null) {
                tempLine.setExecuteIndex(++maxIndex);
            }
        }
        // 记录信息给模板头字段
        SouQuoteTempAttrTreeVO treeVO = new SouQuoteTempAttrTreeVO();
        BeanUtils.copyProperties(attrRelateVO, treeVO);
        po.getTemp().setAttrRelateVars(treeVO);

        // +校验: 如果属性间有依赖性关联，那么被依赖的必须是必填的
        // TODO
    }

}
