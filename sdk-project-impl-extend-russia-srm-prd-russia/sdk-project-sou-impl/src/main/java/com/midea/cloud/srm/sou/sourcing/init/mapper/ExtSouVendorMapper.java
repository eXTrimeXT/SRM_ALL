package com.midea.cloud.srm.sou.sourcing.init.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouVendor;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouVendor;
import com.midea.cloud.srm.model.supplierauth.orgcategory.entity.OrgCatForm;
import com.midea.cloud.srm.model.supplierauth.orgcategory.entity.OrgCatFormCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 备注
 * @author huangbf3
 */
@Mapper
public interface ExtSouVendorMapper extends BaseMapper<ExtSouVendor> {

    /**
     * 获取所有
     * @param theOne
     * @param theTwo
     * @param categoryId
     * @return
     */
    @Select("SELECT COUNT(1) num from (\n" +
            "SELECT t.PROJECT_ID from (\n" +
            "SELECT DISTINCT p.PROJECT_ID, o.VENDOR_ID from scc_sou_project p \n" +
            "join scc_sou_order o on o.PROJECT_ID = p.PROJECT_ID  and o.VENDOR_ID IN (#{theOne}, #{theTwo})\n" +
            "JOIN ext_scc_sou_order e on e.ORDER_ID = o.ORDER_ID and e.ORDER_STATUS IN ('SUBMISSION','CANCEL','WITHDRAW' )\n" +
            "where p.SOU_TYPE = 'bid' \n" +
            "and p.CREATE_APPROVAL_STATUS='APPROVED' and p.EXT_CATEGORY_ID = #{categoryId}\n" +
            ") t GROUP BY t.PROJECT_ID HAVING COUNT(1) = 2\n" +
            ") temp ")
    long selectVendor(@Param("theOne") Long theOne, @Param("theTwo") Long theTwo, @Param("categoryId") Long categoryId);

    /**
     * 查询同品类下的寻源供应商
     * @param paramMap
     * @return
     */
    List<SouVendor> selectProjectVendor(Map<String, Object> paramMap);

    /**
     * 备注
     * @param queryWrapper 参数
     * @return 返回
     */
    @Select("SELECT v.* FROM scc_sou_vendor v \n" +
            "JOIN scc_sou_project p ON p.project_id = v.project_id and p.SOU_TYPE = 'recomm' \n" +
            "where ${ew.sqlSegment} order by p.project_id DESC")
    @ResultType(value = ExtSouVendor.class)
    List<ExtSouVendor> listVendor(@Param(Constants.WRAPPER) QueryWrapper queryWrapper);
}
