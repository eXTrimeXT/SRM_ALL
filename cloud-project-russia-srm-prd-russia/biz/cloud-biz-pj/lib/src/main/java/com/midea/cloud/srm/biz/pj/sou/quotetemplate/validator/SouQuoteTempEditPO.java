package com.midea.cloud.srm.biz.pj.sou.quotetemplate.validator;

import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTemp;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTempLine;
import lombok.Data;

import java.util.List;

/**
 * 寻源 - 模型报价模板 - 编辑保存信息
 *
 * @author zhangwk12@midea.com
 * @since 2022/08/06
 */
@Data
public class SouQuoteTempEditPO {

    /** 报价模板头信息 */
    private SouQuoteTemp temp;
    /** 报价模板明细 */
    private List<SouQuoteTempLine> tempLineList;

}
