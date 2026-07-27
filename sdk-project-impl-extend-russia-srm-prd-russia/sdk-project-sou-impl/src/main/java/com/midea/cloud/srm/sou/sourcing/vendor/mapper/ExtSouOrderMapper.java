package com.midea.cloud.srm.sou.sourcing.vendor.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.midea.cloud.component.mphelper.mapper.CustomMapper;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ExtSouOrderDto;
import com.midea.cloud.srm.model.sou.sourcing.dto.MarginRecordVo;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Mapper
public interface ExtSouOrderMapper extends CustomMapper<ExtSouOrder> {

    /**
     * getSouMarginRecord
     * @param projectId
     * @param companyId
     * @return
     */
    @Select("SELECT  a.*,b.REFUND_STATUS,b.REFUND_PAYMENT_DATE from scc_npm_sou_margin_record a join scc_npm_sou_margin b where a.VENDOR_ID = #{companyId} and a.PROJECT_ID = #{projectId} and b.VENDOR_ID = #{companyId} and b.PROJECT_ID = #{projectId} limit 1")
    List<MarginRecordVo>getSouMarginRecord(Long projectId, Long companyId);
    /**
     * where ${ew.sqlSegment}
     * @param queryWrapper
     * @return
     */
    @ResultType(value = ExtSouOrderDto.class)
    @Select("SELECT p.sou_no, p.sou_name, p.sou_type, p.project_status, p.ext_project_no, p.ext_sou_process,p.ext_sou_mode, p.order_type, p.current_round, \n" +
            "p.ext_org_ou_id, p.ext_org_ou_code, p.ext_org_ou_name, p.publish_time, p.creation_date project_creation_date,  r.order_end_time, o.ORDER_ID, o.ORDER_NO, o.PROJECT_ID, o.VENDOR_ID, o.`ROUND`, o.ORDER_STATUS, o.SUBMIT_BY_ID, o.SUBMIT_BY, o.SUBMIT_BY_IP, o.SUBMIT_FULL_NAME\n" +
            ",o.SUBMIT_TIME, o.WITHDRAW_REASON, o.WITHDRAW_TIME, o.REJECT_REASON, o.REJECT_TIME, o.IS_PROXY, o.PROXY_DOC_ID, o.PROXY_FILE_NAME, o.PROXY_FILE_NAME, o.ext_order_type, \n" +
            "o.EXT_TENDER_NAME, o.EXT_TENDER_PHONE, o.EXT_TENDER_EMAIL, o.EXT_TENDER_FLAG, o.EXT_DOWN_BID_FILE_TIME,\n" +
            "p.CREATED_ID, p.CREATED_BY, p.CREATION_DATE, p.CREATED_BY_IP, p.CREATED_FULL_NAME, p.EXT_EARNEST_FLAG, p.TEL\n" +
            " FROM scc_sou_order o \n" +
            "JOIN scc_sou_project p ON p.project_id = o.project_id\n" +
            "left JOIN scc_sou_round r ON r.project_id = p.project_id AND r.`round` = o.`round` where ${ew.sqlSegment} ")
    List<ExtSouOrderDto> listOrder(@Param(Constants.WRAPPER) QueryWrapper queryWrapper);

}
