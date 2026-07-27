package com.midea.cloud.srm.sup.ext.pjorgcategory.repo;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.ProxyRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.QueryParam;
import com.midea.cloud.meiql.core.util.ResultUtil;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.sup.orgcategory.entity.PjOrgCategory;
import com.midea.cloud.srm.ql.component.RecordConverter;
import com.midea.cloud.srm.sup.ext.pjorgcategory.service.PjOrgCategoryService;
import com.midea.cloud.srm.utils.MqlType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 品类库
 *
 * @author LUXC18
 * @date 2023/9/28 17:42
 */
@Slf4j
@Component
public class PjOrgCategoryRepository extends ProxyRepository {

    @Autowired
    private PjOrgCategoryService pjOrgCategoryService;

    @Autowired
    private RecordConverter recordConverter;

    @Autowired
    private QlService qlService;

    private final String IS_BACKLIST = "isBacklist";

    public PjOrgCategoryRepository() {
        this.register("listPageHeader", this::listPageHeader, true, "二开-查询列表接口");
        this.register("listPageDetailByHeader", this::listPageDetailByHeader, true, "二开-根据头查询明细列表");
    }

    /**
     * 查询明细
     *
     * @param qlQueryAction
     * @return
     */
    private QlResult listPageDetailByHeader(QlQueryAction qlQueryAction) {
        QueryParam param = getPayloadForType(qlQueryAction, QueryParam.class);
        if (param == null) {
            param = QueryParam.build();
        }
        PageUtil.startPage(param.getPage().getPageNum(), param.getPage().getPageSize());
        Map<String, Object> objectMap = recordConverter.filterToMap(param.getFilter());
        PjOrgCategory pjOrgCategory = BeanUtil.mapToBean(objectMap, PjOrgCategory.class, true, null);
        List<PjOrgCategory> pjOrgCategories = pjOrgCategoryService.listPageDetailByHeader(pjOrgCategory);

        PageInfo<PjOrgCategory> pageInfo = new PageInfo<>(pjOrgCategories);
        QlResult result = ResultUtil.build(qlQueryAction, "orgCategoryId", pageInfo, false);
        return result;
    }

    /**
     * 查询头
     *
     * @param qlQueryAction
     * @return
     */
    private QlResult listPageHeader(QlQueryAction qlQueryAction) {
        QueryParam param = getPayloadForType(qlQueryAction, QueryParam.class);
        if (param == null) {
            param = QueryParam.build();
        }
        PageUtil.startPage(param.getPage().getPageNum(), param.getPage().getPageSize());
        Map<String, Object> objectMap = recordConverter.filterToMap(param.getFilter());
        PjOrgCategory pjOrgCategory = BeanUtil.mapToBean(objectMap, PjOrgCategory.class, true, null);
        List<PjOrgCategory> pjOrgCategories = pjOrgCategoryService.listPageHeader(pjOrgCategory);

        Long id = 1L;
        for (PjOrgCategory orgCategory : pjOrgCategories) {
            orgCategory.setOrgCategoryId(id);
            id++;
        }
        PageInfo<PjOrgCategory> pageInfo = new PageInfo<>(pjOrgCategories);
        QlResult result = ResultUtil.build(qlQueryAction, "orgCategoryId", pageInfo, false);
        return result;
    }


    @Override
    protected void afterQuery(QlQueryAction queryAction, Collection<Record> records) {
        super.afterQuery(queryAction, records);
        // 获取供应商信息

        if (CollectionUtils.isNotEmpty(records)) {
            List<Long> companyIdList = records.stream().map(item -> item.getLong("companyId")).distinct().collect(Collectors.toList());
            List<Record> companyRecordList = qlService.queryByWrapper(QlWrappers.query(MqlType.SUPPLIER)
                    .in("companyId", companyIdList), Record.class);
            Map<Long, Record> companyIdMap = companyRecordList.stream().collect(Collectors.toMap(item -> item.getLong("companyId"), Function.identity()));
            // 根据供应商过滤,返回
            records.forEach(e -> {
                Record companyRecord = companyIdMap.get(e.getLong("companyId"));
                if(Enable.Y.name().equals(companyRecord.getString(IS_BACKLIST))
                || Enable.Y.name().equals(companyRecord.getString("timeLimitFlag"))){
                    e.put("pjOrgStatus",Enable.N.name());
                    e.put("pjCategoryStatus",Enable.N.name());
                }
            });
        }

    }
}
