package com.midea.cloud.srm.model.pj.sou.openapi.inq.vo.select;

import com.midea.cloud.srm.model.bid.quotetemplate.dto.SouQuoteTempDataBatchQueryDto;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTempField;
import com.midea.cloud.srm.model.bid.quotetemplate.vo.SouQuoteTempAttrTableColumnVO;
import com.midea.cloud.srm.model.bid.quotetemplate.vo.SouQuoteTempAttrVO;
import com.midea.cloud.srm.model.bid.quotetemplate.vo.SouQuoteTempBatchDataVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.select.ApiSouTempSelectDataVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.select.ApiSouTempSelectDetailDataVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.select.ApiSouTempSelectVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <pre>
 *  模板报价的比价数据模型－分项明细维度
 * </pre>
 *
 * @author haibo1.huang@meiCloud.com
 * @version 1.00.00
 *
 * <pre>
 * 修改记录修改后版本:
 * 修改人:
 * 修改日期: 2023/8/11 18:12、
 * 修改内容:
 * </pre>
 */
@Data
@Accessors(chain = true)
@ApiModel("模板报价的比价数据模型－分项明细维度")
public class ApiInqSouTempSelectDetailDataVO extends ApiSouTempSelectDetailDataVO {

}
