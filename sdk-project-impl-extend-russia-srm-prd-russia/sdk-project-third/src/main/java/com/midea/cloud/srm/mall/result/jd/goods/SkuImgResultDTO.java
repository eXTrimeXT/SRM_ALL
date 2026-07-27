package com.midea.cloud.srm.mall.result.jd.goods;

import com.midea.cloud.srm.mall.result.CommonResultDTO;
import com.midea.cloud.srm.mall.result.jd.common.JDBaseResult;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 *  查询商品池编号响应实体类
 * </pre>
 *
 * @author xiaym13@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2024/2/28 14:28
 *  修改内容:
 * </pre>
 */
@Data
public class SkuImgResultDTO extends JDBaseResult implements CommonResultDTO {

    private Map<String, List<SkuImg>> result;

    @Data
    public static class SkuImg {
        @ApiModelProperty("编号")
        private Long id;

        @ApiModelProperty("skuId")
        private Long skuId;

        @ApiModelProperty("图片路径")
        private String path;

        @ApiModelProperty("创建日期")
        private Date created;

        @ApiModelProperty("更新时间")
        private Date modified;

        @ApiModelProperty("0:不可用;1:可用")
        private Integer yn;

        @ApiModelProperty("是否主图 1：是 0：否")
        private Integer isPrimary;

        @ApiModelProperty("排序")
        private Integer orderSort;

        @ApiModelProperty("位置")
        private Integer position;

        @ApiModelProperty("类型（0方图 1长图【服装】）")
        private Integer type;

        @ApiModelProperty("特征")
        private String features;
    }
}
