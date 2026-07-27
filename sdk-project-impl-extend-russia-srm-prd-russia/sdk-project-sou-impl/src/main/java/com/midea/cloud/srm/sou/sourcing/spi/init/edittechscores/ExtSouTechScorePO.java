package com.midea.cloud.srm.sou.sourcing.spi.init.edittechscores;

import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtScoreRuleDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouTechScoreLineDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouTechScoreHead;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouTechScoreLine;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Data
@EqualsAndHashCode
@ApiModel("技术评分PO类")
public class ExtSouTechScorePO extends BaseObjectX {


    @ApiModelProperty("EXCEL模板表头信息")
    public static List<String[]> TITLE_KEY_LIST = new ArrayList<>();

    static {
        TITLE_KEY_LIST.add(new String[]{"打分项", "scoreItem", "0"});
        TITLE_KEY_LIST.add(new String[]{"评分项", "reviewItem", "1"});
        TITLE_KEY_LIST.add(new String[]{"最高分值", "maxScore", "2"});
        TITLE_KEY_LIST.add(new String[]{"评分说明", "scoreDesc", "3"});

        TITLE_KEY_LIST.add(new String[]{"*详细说明", "detailDesc", "4"});
        TITLE_KEY_LIST.add(new String[]{"*评分", "score", "5"});
    }

    public static List<List<String>> getHeadList(List<ApiExtScoreRuleDto> scoreRuleDtoList) {
        List<List<String>> headList = new ArrayList<>();
        //固定列
        headList.add(Arrays.asList(TITLE_KEY_LIST.get(0)[0], TITLE_KEY_LIST.get(0)[0]));
        headList.add(Arrays.asList(TITLE_KEY_LIST.get(1)[0], TITLE_KEY_LIST.get(1)[0]));
        headList.add(Arrays.asList(TITLE_KEY_LIST.get(2)[0], TITLE_KEY_LIST.get(2)[0]));
        headList.add(Arrays.asList(TITLE_KEY_LIST.get(3)[0], TITLE_KEY_LIST.get(3)[0]));

        //动态列
        if (CollectionUtils.isNotEmpty(scoreRuleDtoList) && CollectionUtils.isNotEmpty(scoreRuleDtoList.get(0).getVendorScoreList())) {
            for (ApiExtSouTechScoreLineDto lineDto : scoreRuleDtoList.get(0).getVendorScoreList()) {
                headList.add(Arrays.asList(lineDto.getVendorName(), TITLE_KEY_LIST.get(4)[0]));
                headList.add(Arrays.asList(lineDto.getVendorName(), TITLE_KEY_LIST.get(5)[0]));
            }
        }
        return headList;
    }


    @ApiModelProperty("评分头")
    private ExtSouTechScoreHead scoreHead;

    @ApiModelProperty("评分行")
    private List<ExtSouTechScoreLine> scoreLineList;

}
