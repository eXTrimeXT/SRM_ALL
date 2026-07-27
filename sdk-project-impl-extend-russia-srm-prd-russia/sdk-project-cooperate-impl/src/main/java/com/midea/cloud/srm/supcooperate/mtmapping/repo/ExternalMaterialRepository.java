package com.midea.cloud.srm.supcooperate.mtmapping.repo;

import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.meiql.core.util.ResultUtil;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.supcooperate.ext.ExternalMaterial;
import com.midea.cloud.srm.supcooperate.mtmapping.service.ExternalMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * <pre>
 *  功能名称
 * </pre>
 *
 * @author xiaym13@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2024/2/26 11:25
 *  修改内容:
 * </pre>
 */
@Slf4j
@Component
public class ExternalMaterialRepository extends CrudRepository {
    @Autowired
    private ExternalMaterialService externalMaterialService;
    @Autowired
    protected QlService qlService;
    public ExternalMaterialRepository() {
        this.register("createMaterialMapping", this::createMaterialMapping, false, "建立物料映射");
        this.register("synchExternalMaterial", this::synchExternalMaterial, true, "同步京东商品");
        this.register("removeMaterialMapping", this::removeMaterialMapping, false, "删除映射");
        this.register("getSkuImage", this::getSkuImage, false, "获取京东商品图片");
    }

    private QlResult getSkuImage(QlQueryAction queryAction) {
        Record record = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords().get(0);
        List<Fileupload> fileuploadList = externalMaterialService.getSkuImage(record.get(ExternalMaterial::getMaterialCode));
        return ResultUtil.build(queryAction, "fileuploadId",fileuploadList, false);
    }

    private QlResult removeMaterialMapping(QlQueryAction queryAction) {
        Record record = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords().get(0);
        qlService.updateByWrapper(QlWrappers.update(ExternalMaterial.class)
                .set(ExternalMaterial::getMaterialId,null)
                .set(ExternalMaterial::getMaterialCode,null)
                .set(ExternalMaterial::getMaterialName,null)
                .set(ExternalMaterial::getMappingFlag, Enable.N.name())
                .set(ExternalMaterial::getMaterialUpdateDate,new Date())
                .eq(ExternalMaterial::getExternalMaterialId, record.get(ExternalMaterial::getExternalMaterialId)));
        return new QlResult();
    }

    private QlResult synchExternalMaterial(QlQueryAction queryAction) {
        log.info("xiaym13-synchExternalMaterial-bg");
        externalMaterialService.synchExternalMaterial();
        log.info("xiaym13-synchExternalMaterial-end");
        return new QlResult();
    }

    private QlResult createMaterialMapping(QlQueryAction queryAction) {
        Record record = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords().get(0);
        qlService.updateByWrapper(QlWrappers.update(ExternalMaterial.class)
                .set(ExternalMaterial::getMaterialId,record.get(ExternalMaterial::getMaterialId))
                .set(ExternalMaterial::getMaterialCode,record.get(ExternalMaterial::getMaterialCode))
                .set(ExternalMaterial::getMaterialName,record.get(ExternalMaterial::getMaterialName))
                .set(ExternalMaterial::getMappingFlag, Enable.Y.name())
                .set(ExternalMaterial::getMaterialUpdateDate,new Date())
                .eq(ExternalMaterial::getExternalMaterialId, record.get(ExternalMaterial::getExternalMaterialId)));
        return new QlResult();
    }
}
