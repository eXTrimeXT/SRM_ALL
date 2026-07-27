package com.midea.cloud.srm.biz.pj.sou.metadata.result;

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
 *  修改日期: 2022/7/19 16:34
 *  修改内容:
 * </pre>
 */
public interface MetadataPermissionHandler {
    /**
     * 备注
     * @param dbName
     * @param rs
     * @return
     * @throws SQLException
     */
    boolean handleCheck(String dbName,ResultSet rs) throws SQLException;
}
