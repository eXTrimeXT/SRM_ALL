package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.control.vendoradd;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouProcessConfigDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouProjectDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouVendorDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouVendorRecordDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.order.ApiSouOrderQueryHandler;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.control.ApiSouVendorAddDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouVendorAuthEditDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouSignUpStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouVendorAddStatusEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 寻源openAPI - 追加供应商处理
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/02
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ApiSouVendorAddHandler implements ISouSpiBean {

    @Autowired
    private SouProjectDAOImpl souProjectDao;
    @Autowired
    private SouVendorDAOImpl souVendorDao;
    @Autowired
    private SouVendorRecordDAOImpl souVendorRecordDao;
    @Autowired
    private SouProcessConfigDAOImpl souProcessConfigDao;

    public SouVendorAddPO convert(ApiSouVendorAddDTO param, String souType) {
        SouVendorAddPO po = new SouVendorAddPO();
        {
            po.setUpdateRecordList(new ArrayList<>(16));
            po.setSaveVendorList(new ArrayList<>(16));
            po.setSaveAuthList(new ArrayList<>(16));
        }

        SouProject project = souProjectDao.getById(param.getProjectId());
        SouProcessConfig processConfig = souProcessConfigDao.getById(project.getProcessConfigId());
        // 1: 查询寻源单现有的供应商信息
        Map<Long/* vendorId */, SouVendor> existVendorMap = souVendorDao.list(SouVendor::getProjectId, param.getProjectId())
                .stream().collect(Collectors.toMap(SouVendor::getVendorId, Function.identity()));
        // 2: 查询现有(未执行、执行失败的记录)
        List<SouVendorRecord> latestRecordList = souVendorRecordDao.lambdaQuery()
                .eq(SouVendorRecord::getProjectId, param.getProjectId())
                .in(SouVendorRecord::getAddStatus, SouVendorAddStatusEnum.DRAFT, SouVendorAddStatusEnum.FAIL)
                .list();
        if (latestRecordList.isEmpty()) {
            return po;
        }
        // 3: 更新记录
        for (SouVendorRecord record : latestRecordList) {
            po.getUpdateRecordList().add(record);
            if (existVendorMap.containsKey(record.getVendorId())) {
                record.setAddStatus(SouVendorAddStatusEnum.CANCEL);
            } else {
                record.setAddStatus(SouVendorAddStatusEnum.DONE);
            }
        }
        // 4: 新增供应商
        int maxIndex = 0;
        Optional<SouVendor> first = existVendorMap.values().stream().sorted(Comparator.comparing(SouVendor::getSortIndex).reversed()).findFirst();
        if (first.isPresent()) {
            maxIndex = first.get().getSortIndex();
        }
        for (SouVendorRecord record : latestRecordList) {
            if (!SouVendorAddStatusEnum.DONE.equals(record.getAddStatus())) {
                continue;
            }
            po.getSaveVendorList().add(record.getVendorInfo());
            record.getVendorInfo().setJoinRound(project.getCurrentRound());
            if (Enable.Y.equals(processConfig.getSignUpManagement())) {
                // 有报名节点，自动报名通过
                record.getVendorInfo().setSignUpStatus(SouSignUpStatusEnum.SIGN_UP_DONE);
            }
            record.getVendorInfo().setSortIndex(++maxIndex);
        }
        // 5: 新增报价权限
        Map<Long/* souItemId */, SouItem> validItemMap = SouActiveBeanUtils.getActiveBean(project.getSouType(), ApiSouOrderQueryHandler.class)
                .getValidItemsInSpecifiedRound(project.getProjectId(), null)
                .stream().collect(Collectors.toMap(SouItem::getSouItemId, Function.identity()));
        if (CollectionUtils.isNotEmpty(param.getAuthList())) {
            Map<Long/* vendorId */, SouVendor> addVendorMap = po.getSaveVendorList().stream()
                    .collect(Collectors.toMap(SouVendor::getVendorId, Function.identity()));
            for (ApiSouVendorAuthEditDTO authDTO : param.getAuthList()) {
                // 1: 校验
                AssertUtils.notNull(authDTO.getVendorId(), "缺少vendorId参数");
                SouVendor addVendor = addVendorMap.get(authDTO.getVendorId());
                if (addVendor == null) {
                    continue;
                }
                AssertUtils.notNull(authDTO.getSouItemId(), "缺少souItemId参数");
                SouItem souItem = validItemMap.get(authDTO.getSouItemId());
                if (souItem == null) {
                    continue;
                }
                if (authDTO.getForbidPrice() == null) {
                    authDTO.setForbidPrice(Enable.N);
                }
                // 2: 转换
                SouVendorAuth auth = new SouVendorAuth();
                auth.setSouVendorId(addVendor.getSouVendorId());
                po.getSaveAuthList().add(auth);
                BeanUtils.copyProperties(authDTO, auth);
                BeanUtils.copyProperties(souItem, auth);
                auth.setVendorAuthId(IdGenrator.generate());
            }
        }
        // 填补剩余的
        Set<String/* souItemId_vendorId */> addAuthsMap = po.getSaveAuthList().stream()
                .map(e -> e.getSouItemId() + "_" + e.getVendorId()).collect(Collectors.toSet());
        for (SouItem item : validItemMap.values()) {
            for (SouVendor vendor : po.getSaveVendorList()) {
                if (addAuthsMap.contains(item.getSouItemId() + "_" + vendor.getVendorId())) {
                    continue;
                }

                SouVendorAuth auth = new SouVendorAuth();
                auth.setSouVendorId(vendor.getSouVendorId());
                po.getSaveAuthList().add(auth);
                BeanUtils.copyProperties(vendor, auth);
                BeanUtils.copyProperties(item, auth);
                auth.setVendorAuthId(IdGenrator.generate());
                auth.setForbidPrice(Enable.N);
            }
        }
        return po;
    }

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
