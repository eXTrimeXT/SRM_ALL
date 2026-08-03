package com.midea.cloud.srm.model.pj.sou.sourcing.entity.typehandler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.typehandler.SouPwdInfoVO;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.springframework.lang.Nullable;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author zhangwk12@midea.com
 * @since 2022/11/02
 */
public class SouRoundOpenPwdInfoTypeHandler extends BaseTypeHandler<Map<String/* operateAuth */, SouPwdInfoVO>> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Map<String/* operateAuth */, SouPwdInfoVO> vars, JdbcType jdbcType) throws SQLException {
        ps.setString(i, JSON.toJSONString(vars));
    }

    @Override
    public Map<String/* operateAuth */, SouPwdInfoVO> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String columnValue = rs.getString(columnName);
        return this.parseValue(columnValue);
    }

    @Override
    public Map<String/* operateAuth */, SouPwdInfoVO> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String columnValue = rs.getString(columnIndex);
        return this.parseValue(columnValue);
    }

    @Override
    public Map<String/* operateAuth */, SouPwdInfoVO> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String columnValue = cs.getString(columnIndex);
        return this.parseValue(columnValue);
    }

    @Nullable
    private Map<String/* operateAuth */, SouPwdInfoVO> parseValue(@Nullable String value) {
        if (value == null) {
            return null;
        } else {
//            operateAuth
            return JSON.parseObject(value, new TypeReference<Map<String, SouPwdInfoVO>>() {});
        }
    }

}
