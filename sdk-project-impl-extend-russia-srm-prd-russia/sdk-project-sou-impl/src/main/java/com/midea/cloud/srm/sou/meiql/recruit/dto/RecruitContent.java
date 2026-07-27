package com.midea.cloud.srm.sou.meiql.recruit.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 招募内容
 * </p>
 *
 * @author zenghx2
 * @since 2023-10-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("scc_npm_recruit_content")
public class RecruitContent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
      @TableId(value = "RECRUIT_CONTENT_ID", type = IdType.AUTO)
    private Long recruitContentId;

    /**
     * 招募id
     */
    @TableField("RECRUIT_ID")
    private Long recruitId;

    /**
     * 内容
     */
    @TableField("CONTENT")
    private String content;

}
