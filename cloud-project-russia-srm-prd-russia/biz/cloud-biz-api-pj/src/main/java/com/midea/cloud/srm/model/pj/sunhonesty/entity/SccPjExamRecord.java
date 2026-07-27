package com.midea.cloud.srm.model.pj.sunhonesty.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * @author huangbf3
 */
@Data
@TableName("scc_pj_exam_record")
public class SccPjExamRecord extends BaseEntity {
    /**主键ID */
    @TableId
    private Long examRecordId;
    /**培训ID */
    private String trainId;
    /**账号ID */
    private String userId;
    /**账号 */
    private String username;
    /**昵称 */
    private String nickname;
    /**学员编号 */
    private String studentNo;
    /**是否完成(YES NO) */
    private String completed;
    /**完成时间 */
    private Date completedDate;
    /**公司ID */
    private String companyId;
}
