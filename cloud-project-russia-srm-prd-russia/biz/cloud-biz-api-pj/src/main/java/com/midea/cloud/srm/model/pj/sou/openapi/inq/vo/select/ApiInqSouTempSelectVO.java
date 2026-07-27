package com.midea.cloud.srm.model.pj.sou.openapi.inq.vo.select;

import com.google.common.collect.Lists;
import com.midea.cloud.srm.model.pj.sou.openapi.inq.vo.select.ApiInqSouTempSelectDataVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.select.ApiSouTempSelectDataVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.select.ApiSouTempSelectVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <pre>
 *  模板报价的比价数据模型
 * </pre>
 *
 * @author haibo1.huang@meiCloud.com
 * @version 1.00.00
 *
 * <pre>
 * 修改记录修改后版本:
 * 修改人:
 * 修改日期: 2023/8/11 10:20、
 * 修改内容:
 * </pre>
 */
@Data
@Accessors(chain = true)
@ApiModel("模板报价的比价数据模型")
public class ApiInqSouTempSelectVO extends BaseObjectX {

    @ApiModelProperty("表格数据")
    private List<ApiInqSouTempSelectDataVO> dataList;

    @ApiModelProperty("动态列数据")
    private List<ApiSouTempSelectVO.DynamicCol> dynamicColList;



}
