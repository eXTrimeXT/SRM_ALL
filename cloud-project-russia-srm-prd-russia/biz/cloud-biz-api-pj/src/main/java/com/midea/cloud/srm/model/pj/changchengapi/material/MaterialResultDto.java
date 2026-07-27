package com.midea.cloud.srm.model.pj.changchengapi.material;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 物料接口返回参数对象
 * @author huangbf3
 */
@Data
public class MaterialResultDto {
    /**
     * 总条数
     */
    private Integer total;
    /**
     * 当前页
     */
    private Integer page;
    /**
     * 行数据
     */
    private List<RowItem> rows;

    /**
     * 物料对象
     */
    @Data
    public class RowItem {
        /**
         * 物料编码
         */
        private String materialCode;
        /**
         * 物料名称
         */
        private String materialName;
        /**
         * 更新人所属部门名称
         */
        private String updateUserDept;
        /**
         * 创建人所属部门名称
         */
        private String createUserDept;
        /**
         * 物料所属分类编码
         */
        private String materialCategoryCode;
        /**
         * 物料所属分类名称
         */
        private String materialCategoryName;
        /**
         * 物料状态 0启用，1禁用
         */
        private Integer materialStatus;
        /**
         * 物料描述
         */
        private String materialDescribe;
        /**
         * 计量单位编码
         */
        private String measurementCode;
        /**
         * 计量单位名称
         */
        private String measurementName;
        /**
         * 创建时间
         */
        private Date createTime;
        /**
         * 更新时间
         */
        private Date updateTime;
        /**
         * 顺序号
         */
        private Integer sortNo;
        /**
         * 创建人工号
         */
        private String createUserCode;
        /**
         * 创建人姓名
         */
        private String createUserName;
        /**
         * 更新人工号
         */
        private String updateUserCode;
        /**
         * 更新人姓名
         */
        private String updateUserName;
        /**
         * 删除标识 0未删除，1已删除
         */
        private Integer deleteFlag;
        /**
         * 启用/禁用标识  0禁用，1启用
         */
        private Integer activeFlag;
        /**
         * 备注
         */
        private String remark;
        /**
         * 版本
         */
        private Integer version;
        /**
         * 版本
         */
        private Integer isIntensification;
    }
}
