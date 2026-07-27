package com.midea.cloud.srm.biz.pj.sou.metadata.utils;

import com.baomidou.mybatisplus.annotation.TableId;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.BeanCopyUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.component.context.container.SpringContextHolder;
import com.midea.cloud.srm.biz.pj.sou.metadata.config.MetadataProperties;
import com.midea.cloud.srm.biz.pj.sou.metadata.enums.MetadataType;
import com.midea.cloud.srm.biz.pj.sou.metadata.strategy.sql.MetadataSqlHandler;
import com.midea.cloud.srm.model.base.metadata.dto.MetadataDTO;
import com.midea.cloud.srm.model.base.metadata.dto.MetadataDetailDTO;
import com.midea.cloud.srm.model.base.metadata.entity.Metadata;
import com.midea.cloud.srm.model.base.metadata.entity.MetadataDetail;
import com.midea.cloud.srm.model.base.metadata.vo.MetadataDetailVO;
import com.midea.cloud.srm.model.base.metadata.vo.MetadataVO;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.dynamic.Module;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
 *  修改日期: 2022/7/5 10:33
 *  修改内容:
 * </pre>
 */
@Slf4j
public class EntityUtil {
    private static final Pattern INVALID_CHAR_REG = Pattern.compile("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]", Pattern.CASE_INSENSITIVE);

    private static final Map<String, List<String>> KEYWORDS = new HashMap<>(50);

    public static Metadata convertDtoToEntity(MetadataDTO metadataDto, boolean add) {
        checkDto(metadataDto);
        if (CollectionUtils.isEmpty(metadataDto.getDetails())) {
            throw new BaseException("不允许空属性列表");
        }
        if (!add && null == metadataDto.getMetadataId()) {
            throw new BaseException("扩展表ID不能为空");
        }
        //非扩展表添加默认列
        if (!MetadataType.EXT.equals(MetadataType.get(metadataDto.getMetadataType()))
                && BooleanUtils.isTrue(metadataDto.getNonDefaultField())) {
            addDefaultDetails(metadataDto);
        }
        Metadata metadata = new Metadata();
        BeanCopyUtil.copyProperties(metadata, metadataDto);
        metadata.setModule(Module.get(metadataDto.getModule()));
        if (add) {
            metadata.setMetadataId(IdGenrator.generate());
        }
        //扩展表不检查默认列
        boolean isExt = MetadataType.EXT.equals(MetadataType.get(metadataDto.getMetadataType()));
        List<String> defaultFieldNames = new ArrayList<>();
        if (!isExt) {
            //默认列列表
            defaultFieldNames = getDefaultDetailVos().stream().map(MetadataDetailVO::getFieldName).collect(Collectors.toList());
        }
        //获取最大列数限制
        MetadataProperties properties = SpringContextHolder.getBean(MetadataProperties.class);
        long maxCol = null != properties.getMaximumCol() ? properties.getMaximumCol() : 30;
        if (metadataDto.getDetails().size() - defaultFieldNames.size() > maxCol) {
            //不包含默认列
            throw new BaseException("除默认列外，扩展表最多支持" + maxCol + "个列");
        }
        //待保存明细
        List<MetadataDetail> details = new ArrayList<>();
        //列名列表
        List<String> fieldNames = new ArrayList<>();
        //列属性列表
        List<String> fieldAttrs = new ArrayList<>();
        for (int i = 0; i < metadataDto.getDetails().size(); i++) {
            MetadataDetailDTO detailDto = metadataDto.getDetails().get(i);
            checkDetailDto(metadata.getModule(), detailDto, fieldNames, fieldAttrs);
            MetadataDetail detail = new MetadataDetail();
            BeanCopyUtil.copyProperties(detail, detailDto);
            detail.setFieldOrder(i);

            if (add) {
                detail.setMetadataDetailId(IdGenrator.generate());
                detail.setMetadataId(metadata.getMetadataId());
            }
            if (StringUtils.isEmpty(detail.getFieldAttr())) {
                detail.setFieldAttr(getAttrByName(detail.getFieldName()));
            }
            if ("Y".equals(detailDto.getPrimaryKeyFlag())) {
                if (null != metadata.getPrimaryKeyDetail()) {
                    throw new BaseException("不允许多主键");
                }
                metadata.setPrimaryKeyDetail(detail);
            }

            if (!isExt) {
                defaultFieldNames.remove(detail.getFieldName());
            }
            fieldNames.add(detail.getFieldName());
            fieldAttrs.add(detail.getFieldAttr());
            details.add(detail);
        }
        if (null == metadata.getPrimaryKeyDetail()) {
            throw new BaseException("不允许存在空主键实体");
        }
        if (!defaultFieldNames.isEmpty()) {
            String missingFields = defaultFieldNames.stream().map(String::valueOf).collect(Collectors.joining(","));
            throw new BaseException("默认列" + missingFields + "不存在");
        }
        metadata.setDetails(details);
        return metadata;
    }

    public static MetadataVO convertEntityToVo(Metadata metadata, boolean forQuery) {
        if (null != metadata) {
            MetadataVO metadataVo = new MetadataVO();
            BeanCopyUtil.copyProperties(metadataVo, metadata);
            metadataVo.setModule(metadata.getModule().getValue());
            List<MetadataDetail> details = metadata.getDetails();
            if (null != details) {
                List<String> defaultFieldNames = getDefaultDetailVos().stream().map(MetadataDetailVO::getFieldName).collect(Collectors.toList());
                List<MetadataDetailVO> detailVos = new ArrayList<>();
                for (MetadataDetail detail : details) {
                    //获取主键明细
                    MetadataDetailVO detailVo = new MetadataDetailVO();
                    BeanCopyUtil.copyProperties(detailVo, detail);
                    if (YesOrNo.YES.getValue().equals(detail.getPrimaryKeyFlag())) {
                        //拷贝对象，防止循环引用导致json序列化异常
                        metadataVo.setPrimaryKeyDetail(BeanCopyUtil.convertWithExtensions(detail, MetadataDetailVO.class));
                    }
                    if (forQuery) {
                        //列名-属性名映射关系
                        metadataVo.getFieldAttrMapping().put(detailVo.getFieldName(), detailVo.getFieldAttr());
                        //列名列表
                        metadataVo.getFieldNames().add(detailVo.getFieldName());
                    }
                    //是否默认列
                    detailVo.setDefaultField(defaultFieldNames.contains(detailVo.getFieldName()) ? YesOrNo.YES.getValue() : YesOrNo.NO.getValue());
                    detailVos.add(detailVo);
                }
                metadataVo.setDetails(detailVos);
            }
            return metadataVo;
        }
        return null;
    }

    public static void addDefaultDetails(MetadataDTO metadataDto) {
        List<MetadataDetailVO> defaultVos = getDefaultDetailVos();
        List<MetadataDetailDTO> detailDtos = metadataDto.getDetails();
        List<String> dtoFieldNames = CollectionUtils.isNotEmpty(detailDtos) ?
                detailDtos.stream().map(MetadataDetailDTO::getFieldName).collect(Collectors.toList()) : new ArrayList<>();
        for (MetadataDetailVO defaultVo : defaultVos) {
            if (!dtoFieldNames.contains(defaultVo.getFieldName())) {
                MetadataDetailDTO detailDto = new MetadataDetailDTO();
                BeanCopyUtil.copyProperties(detailDto, defaultVo);
                detailDtos.add(detailDto);
            }
        }
    }

    public static List<MetadataDetailVO> getDefaultDetailVos() {
        List<MetadataDetailVO> detailVos = new ArrayList<>();
        //暂时不做成配置，避免交付删除配置信息导致创建不合规的表
        detailVos.add(getDefaultDetailVo("CREATED_ID", JdbcType.BIGINT.toString(), 20, null, YesOrNo.YES, "创建人ID", null));
        detailVos.add(getDefaultDetailVo("CREATED_BY", JdbcType.VARCHAR.toString(), 50, null, YesOrNo.YES, "创建人", null));
        detailVos.add(getDefaultDetailVo("CREATION_DATE", "DATETIME", null, null, YesOrNo.YES, "创建时间", null));
        detailVos.add(getDefaultDetailVo("CREATED_BY_IP", JdbcType.VARCHAR.toString(), 150, null, YesOrNo.YES, "创建IP", null));
        detailVos.add(getDefaultDetailVo("CREATED_FULL_NAME", JdbcType.VARCHAR.toString(), 100, null, YesOrNo.NO, "创建人姓名", null));
        detailVos.add(getDefaultDetailVo("LAST_UPDATED_ID", JdbcType.BIGINT.toString(), 20, null, YesOrNo.NO, "最后更新人ID", null));
        detailVos.add(getDefaultDetailVo("LAST_UPDATED_BY", JdbcType.VARCHAR.toString(), 50, null, YesOrNo.NO, "更新人", null));
        detailVos.add(getDefaultDetailVo("LAST_UPDATE_DATE", "DATETIME", null, null, YesOrNo.NO, "最后更新时间", null));
        detailVos.add(getDefaultDetailVo("LAST_UPDATED_BY_IP", JdbcType.VARCHAR.toString(), 150, null, YesOrNo.NO, "最后更新人IP", null));
        detailVos.add(getDefaultDetailVo("LAST_UPDATED_FULL_NAME", JdbcType.VARCHAR.toString(), 100, null, YesOrNo.NO, "最后更新人姓名", null));
        detailVos.add(getDefaultDetailVo("TENANT_ID", JdbcType.VARCHAR.toString(), 30, null, YesOrNo.NO, "租户ID", null));
        detailVos.add(getDefaultDetailVo("VERSION", JdbcType.BIGINT.toString(), 20, null, YesOrNo.NO, "版本号", "0"));
        return detailVos;
    }

    public static void recreateEntity(Metadata metadata) {
        metadata.setMetadataId(IdGenrator.generate());
        metadata.setMetadataType(MetadataType.EXT.toString());
        metadata.setState(YesOrNo.YES.getValue());
        cleanupStandardFields(metadata);
        if (CollectionUtils.isNotEmpty(metadata.getDetails())) {
            for (MetadataDetail detail : metadata.getDetails()) {
                detail.setMetadataDetailId(IdGenrator.generate());
                detail.setMetadataId(metadata.getMetadataId());
                detail.setState(YesOrNo.YES.getValue());
                cleanupStandardFields(detail);
            }
        }
    }

    public static void cleanupStandardFields(BaseEntity entity) {
        entity.setCreatedId(null);
        entity.setCreatedBy(null);
        entity.setCreationDate(null);
        entity.setCreatedByIp(null);
        entity.setCreatedFullName(null);
        entity.setLastUpdatedId(null);
        entity.setLastUpdatedBy(null);
        entity.setLastUpdateDate(null);
        entity.setLastUpdatedByIp(null);
        entity.setLastUpdatedFullName(null);
    }

    private static MetadataDetailVO getDefaultDetailVo(String fieldName, String dataType, Integer dataLength, Integer dataPrecision, YesOrNo requiredFlag, String fieldDesc, String defaultValue) {
        MetadataDetailVO detailVo = new MetadataDetailVO();
        detailVo.setFieldName(fieldName);
        detailVo.setFieldAttr(getAttrByName(fieldName));
        detailVo.setDataType(dataType);
        detailVo.setDataLength(dataLength);
        detailVo.setDataPrecision(dataPrecision);
        detailVo.setRequiredFlag(requiredFlag.getValue());
        detailVo.setFieldDesc(fieldDesc);
        detailVo.setDefaultValue(defaultValue);
        detailVo.setDefaultField(YesOrNo.YES.getValue());
        return detailVo;
    }

    private static void checkDetailDto(Module module, MetadataDetailDTO detailDto, List<String> fieldNames, List<String> fieldAttrs) {
        checkTableElm(module, detailDto.getFieldName(), "列");
        if (StringUtils.isEmpty(detailDto.getDataType())) {
            throw new BaseException("数据类型不能为空");
        }
        if (fieldNames.contains(detailDto.getFieldName())) {
            throw new BaseException("列名" + detailDto.getFieldName() + "不能重复");
        }
        if (fieldAttrs.contains(detailDto.getFieldAttr())) {
            throw new BaseException("列属性名" + detailDto.getFieldName() + "不能重复");
        }
    }

    public static void checkDto(MetadataDTO metadataDto) {
        Module module = convertModule(metadataDto.getModule());
        checkTableElm(module, metadataDto.getTableName(), "表");
        MetadataType metadataType = MetadataType.get(metadataDto.getMetadataType());
        if (null == metadataType) {
            throw new BaseException("类型需为合法值");
        }
        if (MetadataType.EXT.equals(metadataType)) {
            checkTableElm(module, metadataDto.getRefTableName(), "关联表");
            String key = "_" + metadataType.toString().toLowerCase();
            if (!metadataDto.getTableName().endsWith(key)) {
                throw new BaseException("扩展表名需为" + metadataType + "结尾");
            }
            if (!metadataDto.getTableName().equals(metadataDto.getRefTableName() + key)) {
                throw new BaseException("扩展表名需为主表名加" + key);
            }
        }
    }

    public static Module convertModule(String moduleStr) {
        Module module = Module.get(moduleStr);
        if (null == module) {
            throw new BaseException("所属模块需为合法值");
        }
        return module;
    }

    private static void checkTableElm(Module module, String tableElm, String target) {
        if (StringUtils.isEmpty(tableElm)) {
            throw new BaseException(target + "名不能为空");
        }
        List<String> keywords = getKeywords(module);
        if (keywords.contains(tableElm.toUpperCase())) {
            throw new BaseException(target + "名" + tableElm + "不能为关键字");
        }
        if (INVALID_CHAR_REG.matcher(tableElm).find()) {
            throw new BaseException(target + "名" + tableElm + "不能包含特殊字符");
        }
    }


    public static String getAttrByName(String fieldName) {
        String[] elms = fieldName.split("[\\W_]+");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < elms.length; i++) {
            String elm = elms[i];
            if (i == 0) {
                elm = elm.isEmpty() ? elm : elm.toLowerCase();
            } else {
                elm = StringUtils.capitalize(elm.toLowerCase());
            }
            builder.append(elm);
        }
        return builder.toString();
    }

    private static List<String> getKeywords(Module module) {
        List<String> keywords = KEYWORDS.get(module.getValue());
        if (null == keywords) {
            try {
                keywords = SpringContextHolder.getBean(MetadataSqlHandler.class).getKeywordList(module);
                KEYWORDS.put(module.getValue(), keywords);
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
        }
        if (null == keywords) {
            keywords = new ArrayList<>();
        }
        return keywords;
    }

    public static String getEntityIdAttr(Class<?> entityClass) {
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            if (null != field.getAnnotation(TableId.class)) {
                return field.getName();
            }
        }
        return null;
    }

    public static List<String> getEntityAttrs(Class<?> entityClass) {
        List<String> entityAttrs = Arrays.stream(entityClass.getDeclaredFields()).map(Field::getName).collect(Collectors.toList());
        if (!entityClass.getSuperclass().equals(Object.class)) {
            entityAttrs.addAll(getEntityAttrs(entityClass.getSuperclass()));
        }
        return entityAttrs;
    }

    public static Field getExtendAttr(Class<?> entityClass, String entityExtendAttr) {
        try {
            return entityClass.getDeclaredField(entityExtendAttr);
        } catch (NoSuchFieldException e) {
            if (entityClass.getSuperclass().equals(Object.class)) {
                return null;
            }
            return getExtendAttr(entityClass.getSuperclass(), entityExtendAttr);
        }
    }

    public static boolean hasExtendAttr(Class<?> entityClass, String entityExtendAttr) {
        Field field = getExtendAttr(entityClass, entityExtendAttr);
        if (null != field) {
            //是否实现Map接口
            return Map.class.isAssignableFrom(field.getType());
        }
        return false;
    }
}
