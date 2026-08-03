package com.midea.cloud.srm.biz.pj.sou.quotetemplate.validator.attr;

import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTempAttr;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTempField;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTempFormula;
import lombok.Data;

import java.util.List;

/**
 * 寻源 - 模型报价模板属性 - 编辑保存信息
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/27
 */
@Data
public class SouQuoteTempAttrEditPO {

    /** 报价属性头信息 */
    private SouQuoteTempAttr attr;
    /** 字段定义 */
    private List<SouQuoteTempField> fieldList;
    /** 公式定义 */
    private List<SouQuoteTempFormula> formulaList;

}
