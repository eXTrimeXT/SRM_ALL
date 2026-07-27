package com.midea.cloud.srm.sup.ext.effectform.repo;

import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.ql.QlQueryWrapper;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.ProxyRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.srm.sup.meiql.dto.effectform.vo.EffectFormImportSceneVO;
import com.midea.cloud.srm.sup.meiql.dto.effectform.vo.EffectFormVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 供方生效
 *
 * @author liangwl23
 * @date 2023/10/08  11:13
 */
@Slf4j
@Component
public class EffectFormCustomRepository extends ProxyRepository {

    @Autowired
    private QlService qlService;

    public EffectFormCustomRepository() {
        //注册标准根据资质审查获取详情action的扩展后置处理
        this.registerAfter("getDetailByReviewForm", this::afterGetDetailByReviewForm);
    }

    private void afterGetDetailByReviewForm(QlQueryAction qlQueryAction, QlResult qlResult, Map<String, Collection<Record>> stringCollectionMap) {
        List records=qlResult.getRecords();
        if(CollectionUtils.isNotEmpty(records)){
            Object record=records.get(0);
            if(record instanceof EffectFormVO){
                EffectFormVO effectFormVO=(EffectFormVO)record;
                List<EffectFormImportSceneVO> effectFormImportSceneVOList=effectFormVO.getEffectFormImportScenes();
                if(CollectionUtils.isNotEmpty(effectFormImportSceneVOList)){
                    EffectFormImportSceneVO effectFormImportSceneVO=effectFormImportSceneVOList.get(0);
                    List<Record> payload = PayloadWrapper.of(qlQueryAction.getType(), qlQueryAction.getPayload()).asRecords();
                    Long effectFormId=payload.get(0).getLong("effectFormId");
                    //获取供应商准入流程头表记
                    QlQueryWrapper ecrQueryWrapper= QlWrappers.query("sup_entry_config_record_ide");
                    ecrQueryWrapper.eq("reviewFormId",effectFormId);
                    List<Record> entryConfigreRcords=qlService.queryByWrapper(ecrQueryWrapper,Record.class);
                    if(CollectionUtils.isNotEmpty(entryConfigreRcords)){
                        Long entryConfigId=entryConfigreRcords.get(0).getLong("entryConfigId");
                        Record entryConfig=qlService.readByKey("sup_entry_config_ide",entryConfigId, Record.class);
                        if(entryConfig!=null){
                            //是否现场评审
                            effectFormImportSceneVO.setIfSite(entryConfig.getString("pjIfAuth"));
                            //是否样品确认
                            effectFormImportSceneVO.setIfSample(entryConfig.getString("pjIfAuthSample"));
                            //是否物料试用
                            effectFormImportSceneVO.setIfMaterial(entryConfig.getString("pjIfMaterial"));
                            //供方生效
                            effectFormImportSceneVO.setIfEffective(entryConfig.getString("ifAuthEffective"));
                        }
                    }
                    effectFormVO.setEffectFormImportScenes(Arrays.asList(effectFormImportSceneVO));
                }
                qlResult.setRecords(Arrays.asList(effectFormVO));
            }
        }
    }
}
