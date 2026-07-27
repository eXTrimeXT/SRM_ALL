package com.midea.cloud.srm.model.pj.sou.sourcing.entity.typehandler;

import com.alibaba.fastjson.JSON;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.order.ApiCompOrderItemHisInfoVO;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.springframework.lang.Nullable;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author zhangwk12@midea.com
 * @since 2022/11/02
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class ApiCompSouOrderItemHisInfoVOTypeHandler extends BaseTypeHandler<ApiCompOrderItemHisInfoVO> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ApiCompOrderItemHisInfoVO vars, JdbcType jdbcType) throws SQLException {
        ps.setString(i, JSON.toJSONString(vars));
    }

    @Override
    public ApiCompOrderItemHisInfoVO getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String columnValue = rs.getString(columnName);
        return this.parseValue(columnValue);
    }

    @Override
    public ApiCompOrderItemHisInfoVO getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String columnValue = rs.getString(columnIndex);
        return this.parseValue(columnValue);
    }

    @Override
    public ApiCompOrderItemHisInfoVO getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String columnValue = cs.getString(columnIndex);
        return this.parseValue(columnValue);
    }

    @Nullable
    private ApiCompOrderItemHisInfoVO parseValue(@Nullable String value) {
        if (value == null) {
            return null;
        } else {
            return JSON.parseObject(value, ApiCompOrderItemHisInfoVO.class);
        }
    }

}
