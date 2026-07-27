package com.midea.cloud.srm.biz.pj.supplier.entry.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.constants.SequenceCodeConstant;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.srm.biz.pj.supplier.entry.service.IPjEntryConfigService;
import com.midea.cloud.srm.biz.pj.utils.MqlType;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.pj.supplier.SupplierExtClient;
import com.midea.cloud.srm.model.base.configguide.entity.ConfigGuide;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.supplier.entry.dto.PjEntryConfigDTO;
import com.midea.cloud.srm.model.supplier.enums.OrgCategoryCreateTypeEnum;
import com.midea.cloud.srm.model.supplierauth.entry.entity.EntryCategoryConfig;
import com.midea.cloud.srm.model.supplierauth.entry.entity.EntryConfig;
import com.midea.cloud.srm.model.supplierauth.entry.entity.EntryConfigNode;
import com.midea.cloud.srm.model.suppliercooperate.inventory.constants.SupplierInventoryMqlSchemaType;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenQueryWrapper;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <pre>
 *  供应商准入流程行表（品类配置） 服务实现类
 * </pre>
 *
 * @author xiexh12@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2020-09-15 11:28:48
 *  修改内容:
 * </pre>
 */
@Lazy
@Service
@Slf4j
public class PjEntryConfigServiceImpl implements IPjEntryConfigService {

    @Autowired
    private QlOpenClient qlOpenClient;

    @Autowired
    private BaseClient baseClient;

    @Autowired
    private SupplierExtClient supplierExtClient;

    @Override
    public List<Long> batchSaveOrUpdateList(List<PjEntryConfigDTO> entryConfigList) {

        Map<Long, EntryConfig> idMap = new HashMap<>(50);
        List<PjEntryConfigDTO> createList = new ArrayList<>();
        List<PjEntryConfigDTO> updateList = new ArrayList<>();
        for (PjEntryConfigDTO entryConfig : entryConfigList) {
            if (StringUtils.isEmpty(entryConfig.getEntryConfigNum())) {
                String entryConfigNum = baseClient.seqGen(SequenceCodeConstant.SEQ_SUP_ENTRY_CONFIG_NUM);
                entryConfig.setEntryConfigNum(entryConfigNum);
                entryConfig.setEntryConfigId(IdGenrator.generate());
                createList.add(entryConfig);
            } else {
                updateList.add(entryConfig);
            }
            idMap.put(entryConfig.getEntryConfigId(), entryConfig);
        }
        if (CollectionUtils.isNotEmpty(createList)) {
            qlOpenClient.create(ContextPath.SUP, MqlType.ENTRY_CONFIG, createList);
        }
        if (CollectionUtils.isNotEmpty(updateList)) {
            qlOpenClient.update(ContextPath.SUP, MqlType.ENTRY_CONFIG, updateList);
        }
        baseClient.saveOrUpdateConfigGuide(new ConfigGuide().setFlowConfig(YesOrNo.YES.getValue()));
        List<Long> idList = new ArrayList<>();
        if (!idMap.isEmpty()) {
            // 新建时默认生成现场评审,样品确认,物料试用节点,默认不启用,更新不处理
            for (Long id : idMap.keySet()) {
                idList.add(id);
                List<EntryConfigNode> nodeList = new ArrayList<>();
                EntryConfig entryConfig = idMap.get(id);
                EntryConfigNode entryConfigNodeSf = getEntryConfigNode(id, OrgCategoryCreateTypeEnum.SF.name(), "现场评审", entryConfig.getIfAuth());
                EntryConfigNode entryConfigNodeQs = getEntryConfigNode(id, OrgCategoryCreateTypeEnum.QS.name(), "样品确认", entryConfig.getIfAuthSample());
                EntryConfigNode entryConfigNodeMt = getEntryConfigNode(id, OrgCategoryCreateTypeEnum.MT.name(), "物料试用", entryConfig.getIfMaterial());
                EntryConfigNode entryConfigNodeEf = getEntryConfigNode(id, OrgCategoryCreateTypeEnum.EF.name(), "供方生效", entryConfig.getIfAuthEffective());
                nodeList.add(entryConfigNodeSf);
                nodeList.add(entryConfigNodeQs);
                nodeList.add(entryConfigNodeMt);
                nodeList.add(entryConfigNodeEf);
                qlOpenClient.delete(ContextPath.SUP, QlOpenWrappers.update(MqlType.ENTRY_CONFIG_NODE).eq("entryConfigId", id));
                qlOpenClient.create(ContextPath.SUP, MqlType.ENTRY_CONFIG_NODE, nodeList);
            }
        }
        return idList;
    }

    @Override
    public PageInfo<PjEntryConfigDTO> listPageByParam(EntryConfig entryConfig) {

        List<PjEntryConfigDTO> resultList = new ArrayList<>();
        PageInfo<EntryConfig> entryConfigPageInfo = supplierExtClient.listEntryConfigPageByParam(entryConfig);
        List<EntryConfig> list = entryConfigPageInfo.getList();
        if(CollectionUtils.isNotEmpty(list)){
            List<Long> idLit = list.stream().map(EntryConfig::getEntryConfigId).collect(Collectors.toList());
            QlOpenQueryWrapper wrapper = QlOpenWrappers.query(MqlType.ENTRY_CONFIG);
            wrapper.in(EntryConfig::getEntryConfigId,idLit);
            wrapper.orderByDesc(EntryConfig::getLastUpdateDate);
            resultList = qlOpenClient.query(ContextPath.SUP, wrapper, PjEntryConfigDTO.class);
        }

        PageInfo<PjEntryConfigDTO> result = new PageInfo<>();
        result.setList(resultList);
        result.setPageSize(entryConfigPageInfo.getPageSize());
        result.setPageNum(entryConfigPageInfo.getPageNum());
        result.setTotal(entryConfigPageInfo.getTotal());
        result.setPages(entryConfigPageInfo.getPages());
        return result;
    }

    private EntryConfigNode getEntryConfigNode(Long entryConfigId, String nodeCode, String nodeName, String enableFlag) {
        EntryConfigNode entryConfigNode = new EntryConfigNode();
        entryConfigNode.setEntryConfigNodeId(IdGenrator.generate());
        entryConfigNode.setEntryConfigId(entryConfigId);
        entryConfigNode.setNodeCode(nodeCode);
        entryConfigNode.setNodeName(nodeName);
        entryConfigNode.setEnableFlag(StringUtils.isEmpty(enableFlag) ? Enable.N.name() : enableFlag);
        return entryConfigNode;
    }
}
