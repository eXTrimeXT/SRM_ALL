package com.midea.cloud.srm.biz.pj.sou.metadata.result;

import com.midea.cloud.srm.model.base.metadata.entity.MetadataDetail;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <pre>
 *
 * </pre>
 *
 * @author huangyq154@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2022/7/8 18:59
 *  修改内容:
 * </pre>
 */
public interface MetadataResultSetHandler {
    /**
     * 备注
     * @param rs
     * @return
     * @throws SQLException
     */
    MetadataDetail handleRow(ResultSet rs) throws SQLException;
}
