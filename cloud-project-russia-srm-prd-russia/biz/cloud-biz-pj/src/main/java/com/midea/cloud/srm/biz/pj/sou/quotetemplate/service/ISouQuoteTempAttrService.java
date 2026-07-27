package com.midea.cloud.srm.biz.pj.sou.quotetemplate.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.srm.model.bid.quotetemplate.dto.SouQuoteTempAttrEditDTO;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTempAttr;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTempField;
import com.midea.cloud.srm.model.bid.quotetemplate.vo.SouQuoteTempAttrVO;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 寻源 - 模型报价模板 - 报价属性
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/27
 */
public interface ISouQuoteTempAttrService {

    /**
     * 采购商端: 报价属性分页查询
     * @param queryWrapper
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<SouQuoteTempAttr> listAttrsById(@Nullable LambdaQueryWrapper<SouQuoteTempAttr> queryWrapper,
                                         @Nullable Integer pageNum, @Nullable Integer pageSize);

    /**
     * 采购商端: 查询报价属性详情信息
     * @param attrId {@link SouQuoteTempAttr#getAttrId}
     * @return
     */
    SouQuoteTempAttrVO getAttr(long attrId);

    /**
     * 采购商/供应商端: 查询多个报价属性详情信息
     * @param attrIds
     * @return
     */
    List<SouQuoteTempAttrVO> listAttrsById(Set<Long> attrIds);

    /**
     * 采购商/供应商端: 查询多个报价属性详情信息
     * @param attrNames
     * @return
     */
    List<SouQuoteTempAttrVO> listAttrsByName(Set<String> attrNames);

    /**
     * 采购商端: 编辑/提交报价属性
     * PS: 提交的同时需要生效该报价属性的其他版本数据(非拟定)
     * @param param 报价属性信息
     * @param isTempSave true-暂存/false-提交
     * @return
     */
    long/* attrId */ editAttr(SouQuoteTempAttrEditDTO param, boolean isTempSave);

    /**
     * 采购商端: 删除报价属性
     * @param attrId {@link SouQuoteTempAttr#getAttrId}
     */
    void removeAttr(long attrId);

    /**
     * 采购商端: 生效报价属性
     * PS: 同时需要生效该报价属性的其他版本数据(非拟定)
     * @param attrId {@link SouQuoteTempAttr#getAttrId}
     * @param loginAppUser 用户信息
     */
    void validAttr(long attrId, @Nullable LoginAppUser loginAppUser);

    /**
     * 采购商端: 失效报价属性
     * @param attrId {@link SouQuoteTempAttr#getAttrId}
     */
    void invalidAttr(long attrId);

    /**
     *
     * 采购商端: 复制报价属性
     * @param attrId {@link SouQuoteTempAttr#getAttrId}
     * @return
     */
    SouQuoteTempAttr copyAttr(long attrId);

    /**
     * 计算表格数据
     * @param attrId 报价属性ID
     * @param businessId 业务单据ID
     * @param dataList 表格数据
     * @param needValid 是否需要校验入参数据情况
     */
    void executeCompute(final long attrId, @Nullable String businessId, @Nullable List<Map<String/* fieldId */, Object>> dataList, boolean needValid);

    /**
     *
     * 专门用于查询动态表数据
     * @param attrId 报价属性ID
     * @param businessIds 业务单据ID集合
     * @param pageNum 分页信息
     * @param pageSize 分页信息
     * @return
     */
    List<Map<String/* fieldId */, Object>> queryAttrData(final long attrId, Collection<String> businessIds,
                                                         @Nullable Integer pageNum, @Nullable Integer pageSize);

    /**
     * 根据报价属性的字段IDs（公式类型字段），查找其引用的其他报价属性的 attrIds 集合
     * @param fieldIds {@link SouQuoteTempField#getFieldId}
     * @return {@link SouQuoteTempAttr#getAttrId}
     */
    Map<Long/* fieldId */, List<Long/* attrId */>> getRefAttrIdsByFieldIds(Set<Long> fieldIds);

}
