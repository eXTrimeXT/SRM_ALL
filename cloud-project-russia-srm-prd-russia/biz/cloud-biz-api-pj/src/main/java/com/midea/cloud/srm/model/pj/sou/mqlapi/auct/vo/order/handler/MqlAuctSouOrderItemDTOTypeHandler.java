package com.midea.cloud.srm.model.pj.sou.mqlapi.auct.vo.order.handler;

import com.alibaba.fastjson.JSON;
import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.dto.order.MqlAuctSouOrderItemDTO;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.springframework.lang.Nullable;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author huangbf3
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class MqlAuctSouOrderItemDTOTypeHandler extends BaseTypeHandler<MqlAuctSouOrderItemDTO> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, MqlAuctSouOrderItemDTO vars, JdbcType jdbcType) throws SQLException {
        ps.setString(i, JSON.toJSONString(vars));
    }

    @Override
    public MqlAuctSouOrderItemDTO getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String columnValue = rs.getString(columnName);
        return this.parseValue(columnValue);
    }

    @Override
    public MqlAuctSouOrderItemDTO getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String columnValue = rs.getString(columnIndex);
        return this.parseValue(columnValue);
    }

    @Override
    public MqlAuctSouOrderItemDTO getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String columnValue = cs.getString(columnIndex);
        return this.parseValue(columnValue);
    }

    @Nullable
    private MqlAuctSouOrderItemDTO parseValue(@Nullable String value) {
        if (value == null) {
            return null;
        } else {
            return JSON.parseObject(value, MqlAuctSouOrderItemDTO.class);
        }
    }

}
