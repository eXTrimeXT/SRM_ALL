package com.midea.cloud.srm.sup.association.repo;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.sou.SouUserTypeCheckUtils;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.meiql.api.service.QlCondition;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.ql.QlQueryFieldWrapper;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.api.spec.schema.QlType;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.meiql.core.repository.jooq.support.QueryParam;
import com.midea.cloud.meiql.core.util.ResultUtil;
import com.midea.cloud.meiql.core.util.SchemaUtil;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorDto;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorProjectDto;
import com.midea.cloud.srm.model.sou.recommvendor.enums.RecommType;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.model.sup.association.dto.ApiExtSupAssociationDTO;
import com.midea.cloud.srm.model.sup.association.dto.ApiExtSupAssociationQueryDTO;
import com.midea.cloud.srm.model.sup.association.entity.ExtSupAssociation;
import com.midea.cloud.srm.sup.association.service.ExtSupAssociationEventService;
import com.midea.cloud.srm.sup.association.service.ExtSupAssociationQueryService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;

/**
 * <pre>
 *  寻源需求邀请供应商头
 * </pre>
 *
 * @author xiaym13@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/10/8 16:18
 *  修改内容:
 * </pre>
 */
@Slf4j
@Component
public class RelationSuppliersRepository extends CrudRepository {
    @Autowired
    protected QlService qlService;

    @Autowired
    private ExtSupAssociationQueryService extSouInitQueryService;

    @Autowired
    private ExtSupAssociationEventService extSupAssociationEventService;


    public RelationSuppliersRepository() {
        //注册action
        this.register("saveOrUpdate", this::saveOrUpdate, true, "保存");
        this.register("listPage", this::listPage, false, "列表查询");
        this.register("delRelationSup", this::delRelationSup, false, "删除");
        this.register("getRelationSupById", this::getRelationSupById, false, "查询详情");
    }

    private QlResult listPage(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();
        ApiExtSupAssociationQueryDTO queryParam;
        {
            List<ApiExtSupAssociationQueryDTO> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<ApiExtSupAssociationQueryDTO>>() {
            });
            AssertUtils.notEmpty(params, "缺少数据");
            queryParam = params.get(0);
        }
        List<ExtSupAssociation> voList = extSouInitQueryService.listProjects(queryParam);
        return ResultUtil.build(queryAction, "associationId", new PageInfo<>(voList), false);
    }

    @Override
    protected QlCondition beforeQuery(QlQueryAction queryAction, QueryParam payload) {
        QlCondition qlCondition = super.beforeQuery(queryAction, payload);
        if (null == qlCondition) {
            qlCondition = MeiQl.newCondition();
        }
        return qlCondition;
    }


    private QlResult saveOrUpdate(QlQueryAction action) {
        List<Record> records = PayloadWrapper.of(action.getType(), action.getPayload()).asRecords();
        Assert.notEmpty(records, "参数缺失");
        Assert.isTrue(records.size() == 1, "仅支持1条数据");
        Record record = records.get(0);


        Long associationId = record.get(ExtSupAssociation::getAssociationId);
        if (associationId != null) {
            Record borrow = qlService.readByKey(MqlType.SOU_RELATION_SUP_BUYER, associationId, Record.class);
            Assert.notNull(borrow, "关联供应商ID不存在");
        }
        return super.doSave(action, records);
    }

    @ApiOperation("删除")
    private QlResult delRelationSup(QlQueryAction action) {
        SouUserTypeCheckUtils.checkIsBuyer();
        List<Record> records = PayloadWrapper.of(action.getType(), action.getPayload()).asRecords();
        Assert.notEmpty(records, "关联供应商ID参数缺失");

        for (Record record : records) {
            if (ObjectUtil.isNotEmpty(record.get(ExtSupAssociation::getAssociationId))) {
                extSupAssociationEventService.removeById(record.get(ExtSupAssociation::getAssociationId));
            }
        }
        return QlResult.empty();
    }

    @ApiOperation("查询关联供应商详情")
    private QlResult getRelationSupById(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();
        long associationId;
        {
            List<ExtSupAssociation> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<ExtSupAssociation>>() {
            });
            AssertUtils.notEmpty(params, "缺少数据");
            ExtSupAssociation param = params.get(0);
            AssertUtils.notNull(param.getAssociationId(), "缺少associationId参数");
            associationId = param.getAssociationId();
        }
        ApiExtSupAssociationDTO projectInfo = extSouInitQueryService.getProjectInfo(associationId);
        if (projectInfo != null) {
            return ResultUtil.build(queryAction, "associationId", Collections.singletonList(projectInfo), false);
        } else {
            return QlResult.empty();
        }
    }
}
