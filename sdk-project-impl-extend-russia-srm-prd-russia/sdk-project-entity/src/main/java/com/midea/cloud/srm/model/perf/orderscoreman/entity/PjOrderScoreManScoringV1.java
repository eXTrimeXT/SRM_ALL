package com.midea.cloud.srm.model.perf.orderscoreman.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.perf.scoring.ScoreManScoringV1;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <pre>
 *  绩效评分项目供应商表 模型
 * </pre>
 *
 * @author luxc18@meiCloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023-11-02 15:10:37
 *  修改内容:
 * </pre>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("scc_perf_score_man_scoring_v1")
public class PjOrderScoreManScoringV1 extends ScoreManScoringV1 {

    @ApiModelProperty(value = "评分状态")
    @TableField("SCORING_STATUS")
    private String scoringStatus;

    @ApiModelProperty(value = "驳回原因")
    @TableField("REJECT_REMARK")
    private String rejectRemark;

    @ApiModelProperty(value = "评分时间")
    @TableField("SCORE_DATE")
    private Date scoreDate;

    @ApiModelProperty(value = "评分开始时间")
    @TableField(exist = false)
    private Date scoreStartDate;
    @ApiModelProperty(value = "评分结束时间")
    @TableField(exist = false)
    private Date scoreEndDate;

    @ApiModelProperty(value = "评分状态查询条件")
    @TableField(exist = false)
    private List<String> scoringStatusList = new ArrayList<>();

    public String checkDeatailGroupStr(){
        return this.getScoreItemsId()+"-"+this.getCompanyId()+"-"+this.getScoreUserName()+"-"+this.getCategoryId();
    }

    /**
     * 品类+维度+指标
     * @return
     */
    public String calcDimScoreDetailGroupStr(){
        return this.getScoreItemsId()+"-"+this.getCompanyId()+"-"+this.getCategoryId()+"-"+this.getIndicatorDimensionType()
                +"-"+this.getIndicatorName();
    }

}
