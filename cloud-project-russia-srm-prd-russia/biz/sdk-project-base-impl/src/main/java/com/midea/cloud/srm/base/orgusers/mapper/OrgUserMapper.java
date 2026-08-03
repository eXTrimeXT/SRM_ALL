package com.midea.cloud.srm.base.orgusers.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.midea.cloud.srm.model.base.organization.entity.OrganizationUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Mapper
public interface OrgUserMapper extends BaseMapper<OrganizationUser> {
    /**
     * 班组长
     * @param userId 参数
     * @return 返回
     */
    @Select("SELECT\n" +
            "        ou.ORGANIZATION_USER_REL_ID,\n" +
            "        ou.ORGANIZATION_ID,\n" +
            "        o.ORGANIZATION_NAME,\n" +
            "        ou.USER_ID,\n" +
            "        ou.FULL_PATH_ID,\n" +
            "        ou.START_DATE,\n" +
            "        ou.END_DATE,\n" +
            "        ou.CREATED_BY,\n" +
            "        ou.CREATION_DATE,\n" +
            "        ou.CREATED_BY_IP,\n" +
            "        ou.LAST_UPDATED_BY,\n" +
            "        ou.LAST_UPDATE_DATE,\n" +
            "        ou.LAST_UPDATED_BY_IP,\n" +
            "        ou.VERSION,\n" +
            "        ou.TENANT_ID,\n" +
            "        o.ENABLED\n" +
            "        FROM\n" +
            "        scc_base_organization_user ou\n" +
            "        LEFT JOIN scc_base_organization o ON o.ORGANIZATION_ID = ou.ORGANIZATION_ID\n" +
            "        WHERE ou.USER_ID = #{userId}")
    List<OrganizationUser> listOrganUserByParam(Long userId);
}
