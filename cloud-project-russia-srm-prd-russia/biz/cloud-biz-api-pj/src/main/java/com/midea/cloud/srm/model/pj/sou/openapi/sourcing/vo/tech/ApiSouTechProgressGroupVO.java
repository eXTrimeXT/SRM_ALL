package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.tech;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouGroup;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouTechScoreHead;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTechScoreStatusEnum;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 寻源openAPI - 技术评委评分
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/05
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouTechProgressGroupVO extends BaseObjectX {

    /** @see SouTechScoreHead#getTechScoreHeadId */
    @ApiModelProperty("评分头表ID")
    private Long techScoreHeadId;

    /** @see SouGroup#getGroupId */
    @ApiModelProperty("评委ID")
    private Long groupId;

    /** @see SouGroup#getFullName */
    @ApiModelProperty("评委名称")
    private String fullName;

    /** @see SouGroup#getPosition */
    @ApiModelProperty("岗位")
    private String position;

    /** @see SouGroup#getPhone */
    @ApiModelProperty("电话")
    private String phone;

    /** @see SouGroup#getEmail */
    @ApiModelProperty("邮箱")
    private String email;

    /** @see SouTechScoreHead#getTotalScore */
    @ApiModelProperty("总分值")
    private BigDecimal totalScore;

    /** @see SouTechScoreHead#getScoreStatus */
    @ApiModelProperty("技术评分进度")
    private SouTechScoreStatusEnum scoreStatus;

    /** @see SouTechScoreHead#getIsProxy */
    @ApiModelProperty("是否代理评分")
    private Enable isProxy;

    public static List<ApiSouTechProgressGroupVO> convertApiVO(List<SouGroup> groupList,
                                                               List<SouTechScoreHead> techScoreHeadList) {
        if (groupList.isEmpty()) {
            return Collections.emptyList();
        }

        List<ApiSouTechProgressGroupVO> voList = new ArrayList<>(groupList.size());

        Map<Long/* groupId */, SouTechScoreHead> techScoreHeadMap = techScoreHeadList.stream()
                .collect(Collectors.toMap(SouTechScoreHead::getGroupId, Function.identity()));

        ApiSouTechProgressGroupVO vo;
        SouTechScoreHead techScoreHead;
        for (SouGroup group : groupList) {
            vo = new ApiSouTechProgressGroupVO();
            voList.add(vo);
            techScoreHead = techScoreHeadMap.get(group.getGroupId());

            // 评分头表ID
            vo.setTechScoreHeadId(techScoreHead != null ? techScoreHead.getTechScoreHeadId() : null);
            // 评委ID
            vo.setGroupId(group.getGroupId());
            // 评委名称
            vo.setFullName(group.getFullName());
            // 岗位
            vo.setPosition(group.getPosition());
            // 电话
            vo.setPhone(group.getPhone());
            // 邮箱
            vo.setEmail(group.getEmail());
            // 总分值
            vo.setTotalScore(techScoreHead != null ? techScoreHead.getTotalScore() : null);
            // 技术评分进度
            vo.setScoreStatus(techScoreHead != null ? techScoreHead.getScoreStatus() : SouTechScoreStatusEnum.UNFINISHED);
            // 是否代理评分
            vo.setIsProxy(techScoreHead != null ? techScoreHead.getIsProxy() : Enable.N);
        }

        return voList;
    }

}
