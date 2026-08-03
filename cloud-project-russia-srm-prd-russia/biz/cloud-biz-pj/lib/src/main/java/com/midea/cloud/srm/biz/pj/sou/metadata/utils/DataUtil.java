package com.midea.cloud.srm.biz.pj.sou.metadata.utils;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.constants.SysConstant;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.IPUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.component.filter.HttpServletHolder;
import com.midea.cloud.srm.biz.pj.sou.metadata.context.MetadataDataContext;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.dto.MetadataDataDTO;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.entity.MetadataData;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.vo.MetadataDataVO;
import com.midea.cloud.srm.model.base.metadata.vo.MetadataVO;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <pre>
 * 数据处理工具
 * </pre>
 *
 * @author huangyq154@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2022/7/13 9:27
 *  修改内容:
 * </pre>
 */
public class DataUtil {

    public static MetadataData convertDtoToEntity(MetadataDataDTO dataDto) {
        MetadataVO config = MetadataDataContext.get();
        MetadataData entity = new MetadataData();
        if (null != config.getPrimaryKeyDetail()) {
            entity.setIdField(config.getPrimaryKeyDetail().getFieldName());
        }
        entity.setTableName(config.getTableName());
        Iterator<Map.Entry<String, Object>> attrs = dataDto.entrySet().iterator();
        Map<String, String> mapping = config.getFieldAttrMapping().entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey, (k1, k2) -> k2));
        while (attrs.hasNext()) {
            Map.Entry<String, Object> attr = attrs.next();
            String field = mapping.get(attr.getKey());
            if (StringUtils.isNotEmpty(field)) {
                entity.put(field, attr.getValue());
            }
        }
        return entity;
    }

    public static MetadataDataVO convertEntityToVo(MetadataData entity, boolean isFixSelect) {
        MetadataVO config = MetadataDataContext.get();
        MetadataDataVO vo = new MetadataDataVO();
        if (null != config.getPrimaryKeyDetail()) {
            vo.setIdField(config.getPrimaryKeyDetail().getFieldAttr());
        }
        vo.setTableName(config.getTableName());
        Iterator<Map.Entry<String, Object>> fields = entity.entrySet().iterator();
        while (fields.hasNext()) {
            Map.Entry<String, Object> field = fields.next();
            String attr = config.getFieldAttrMapping().get(field.getKey());

            if (isFixSelect) {
                vo.put(field.getKey(), field.getValue());
            } else if (StringUtils.isNotEmpty(attr)) {
                vo.put(attr, field.getValue());
            }
        }
        return vo;
    }

    public static List<MetadataDataVO> convertEntitiesToVo(List<MetadataData> entities, boolean isFixSelect) {
        List<MetadataDataVO> vos = new ArrayList<>();
        if (null != entities) {
            entities.forEach(entity -> {
                MetadataDataVO vo = convertEntityToVo(entity, isFixSelect);
                vos.add(vo);
            });
        }
        return vos;
    }

    public static PageInfo<MetadataDataVO> convertEntitiesToPage(PageInfo<MetadataData> entityPage, boolean isFixSelect) {
        List<MetadataDataVO> vos = convertEntitiesToVo(entityPage.getList(), isFixSelect);
        PageInfo<MetadataDataVO> pageResult = new PageInfo<>(vos);
        pageResult.setEndRow(entityPage.getEndRow());
        pageResult.setNavigateFirstPage(entityPage.getNavigateFirstPage());
        pageResult.setHasNextPage(entityPage.isHasNextPage());
        pageResult.setHasPreviousPage(entityPage.isHasPreviousPage());
        pageResult.setIsFirstPage(entityPage.isIsFirstPage());
        pageResult.setIsLastPage(entityPage.isIsLastPage());
        pageResult.setNavigateLastPage(entityPage.getNavigateLastPage());
        pageResult.setNavigatePages(entityPage.getNavigatePages());
        pageResult.setNavigatepageNums(entityPage.getNavigatepageNums());
        pageResult.setNextPage(entityPage.getNextPage());
        pageResult.setPageNum(entityPage.getPageNum());
        pageResult.setPageSize(entityPage.getPageSize());
        pageResult.setPages(entityPage.getPages());
        pageResult.setPrePage(entityPage.getPrePage());
        pageResult.setSize(entityPage.getSize());
        pageResult.setStartRow(entityPage.getStartRow());
        pageResult.setTotal(entityPage.getTotal());
        return pageResult;
    }

    public static Object getIdByCheck(Object id) {
        if (null == id) {
            throw new BaseException("主键值不能为空");
        }
        return id;
    }

    public static void checkPk(String tableName) {
        if (!MetadataDataContext.isPkEntity()) {
            throw new BaseException("表" + tableName + "没有主键信息，不能执行当前方法");
        }
    }

    public static void addCreateAttr(MetadataData entity) {
        MetadataVO config = MetadataDataContext.get();
        if (MetadataDataContext.isPkEntity()) {
            //如果没有ID，则填充ID
            String idField = config.getPrimaryKeyDetail().getFieldName();
            entity.computeIfAbsent(idField, f -> IdGenrator.generate());
        }
        LoginAppUser user = AppUserUtil.getLoginAppUser();
        String ip = getIp();
        Long userId = null == user ? SysConstant.System.SYSTEM_ID : user.getUserId();
        String username = user == null ? SysConstant.System.SYSTEM_MANAGER : user.getUsername();
        String nickname = user == null ? SysConstant.System.SYSTEM_MANAGER : user.getNickname();
        entity.put("CREATED_ID", userId);
        entity.put("CREATED_BY", username);
        entity.put("CREATION_DATE", LocalDateTime.now());
        entity.put("CREATED_BY_IP", ip);
        entity.put("CREATED_FULL_NAME", nickname);
    }

    public static void addUpdateAttr(MetadataData entity) {
        LoginAppUser user = AppUserUtil.getLoginAppUser();
        String ip = getIp();
        Long userId = null == user ? SysConstant.System.SYSTEM_ID : user.getUserId();
        String username = user == null ? SysConstant.System.SYSTEM_MANAGER : user.getUsername();
        String nickname = user == null ? SysConstant.System.SYSTEM_MANAGER : user.getNickname();
        entity.put("LAST_UPDATED_ID", userId);
        entity.put("LAST_UPDATED_BY", username);
        entity.put("LAST_UPDATE_DATE", LocalDateTime.now());
        entity.put("LAST_UPDATED_BY_IP", ip);
        entity.put("LAST_UPDATED_FULL_NAME", nickname);
    }
    
    private static String getIp() {
        HttpServletRequest request = HttpServletHolder.getRequest();
        return request == null ? "127.0.0.1" : IPUtil.getRemoteIpAddr(request);
    }
}
