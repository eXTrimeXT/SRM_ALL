package com.midea.cloud.srm.supcooperate.ext.order.repo;

import cn.hutool.core.lang.func.LambdaUtil;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.constant.SouConstant;
import com.midea.cloud.srm.supcooperate.ext.order.dto.OrderConfig;
import com.midea.cloud.srm.supcooperate.ext.order.dto.OrderConfigCategory;
import com.midea.cloud.srm.supcooperate.meiql.base.PurchaseRepository;
import com.midea.cloud.srm.supcooperate.meiql.util.PurchaseMqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author zenghx2
 */
@Slf4j
@Component
public class OrderConfigRepository extends PurchaseRepository<OrderConfig> {

    private final static String ORG_LIST_KEY = "orgList";
    private final static String CATEGORY_LIST_KEY = "categoryList";
    public final static String ORDER_CONFIG_CATEGORY = "OrderConfigCategory";

    public OrderConfigRepository() {
        super("OrderConfig", "configId", "自动转订单配置");

        this.register("list", this::list, false, "查询");
        this.register("saveOrUpdate", this::saveOrUpdate, true, "保存");
    }

    private QlResult list(QlQueryAction action) {
        QlResult result = super.query(action);

        PurchaseMqlUtils.buildResult(result, schemaType, e -> {
            Long configId = e.get(OrderConfig::getConfigId);
            List<OrderConfigCategory> detailList = qlService.queryByWrapper(QlWrappers.query(ORDER_CONFIG_CATEGORY)
                    .eq(OrderConfigCategory::getConfigId, configId), OrderConfigCategory.class);
            if (CollectionUtils.isEmpty(detailList)) {
                return;
            }
            Map<Object, Map<String, Object>> orgMap = new HashMap<>(16);
            Map<Object, Map<String, Object>> categoryMap = new HashMap<>(16);
            detailList.stream().forEach(detail -> {
                Map<String, Object> org = new HashMap(16);
                org.put(LambdaUtil.getFieldName(OrderConfigCategory::getOrgId), detail.getOrgId());
                org.put(LambdaUtil.getFieldName(OrderConfigCategory::getOrgName), detail.getOrgName());
                org.put(LambdaUtil.getFieldName(OrderConfigCategory::getOrgCode), detail.getOrgCode());
                orgMap.putIfAbsent(org.get(LambdaUtil.getFieldName(OrderConfigCategory::getOrgId)), org);
                Map<String, Object> category = new HashMap(16);
                category.put(LambdaUtil.getFieldName(OrderConfigCategory::getCategoryId), detail.getCategoryId());
                category.put(LambdaUtil.getFieldName(OrderConfigCategory::getCategoryName), detail.getCategoryName());
                category.put(LambdaUtil.getFieldName(OrderConfigCategory::getCategoryCode), detail.getCategoryCode());
                categoryMap.putIfAbsent(category.get(LambdaUtil.getFieldName(OrderConfigCategory::getCategoryId)), category);
            });
            e.put(ORG_LIST_KEY, orgMap.values());
            e.put(CATEGORY_LIST_KEY, categoryMap.values());
        });
        return result;
    }

    private QlResult saveOrUpdate(QlQueryAction action) {
        try {
            Record record = getRecord(action);
            OrderConfig orderConfig = MeiQl.toValue(record, OrderConfig.class);
            List<Map<String, Object>> orgList = (List<Map<String, Object>>) record.get(ORG_LIST_KEY);
            List<Map<String, Object>> categoryList = (List<Map<String, Object>>) record.get(CATEGORY_LIST_KEY);
            Assert.notEmpty(orgList, "公司不能为空");
            Assert.notEmpty(categoryList, "品类不能为空");

            boolean add = false;
            if (orderConfig.getConfigId() == null) {
                add = true;
                orderConfig.setConfigId(IdGenrator.generate());
            }

            List<OrderConfigCategory> list = new ArrayList<>();
            orgList.forEach(org -> {
                Long orgId = (Long) org.get(LambdaUtil.getFieldName(OrderConfigCategory::getOrgId));
                String orgCode = (String) org.get(LambdaUtil.getFieldName(OrderConfigCategory::getOrgCode));
                String orgName = (String) org.get(LambdaUtil.getFieldName(OrderConfigCategory::getOrgName));
                categoryList.forEach(category -> {
                    OrderConfigCategory obj = new OrderConfigCategory()
                            .setConfigId(orderConfig.getConfigId()).setOrgId(orgId).setOrgCode(orgCode).setOrgName(orgName)
                            .setCategoryId(Long.valueOf(Objects.toString(category.get(LambdaUtil.getFieldName(OrderConfigCategory::getCategoryId)))))
                            .setCategoryCode((String) category.get(LambdaUtil.getFieldName(OrderConfigCategory::getCategoryCode)))
                            .setCategoryName((String) category.get(LambdaUtil.getFieldName(OrderConfigCategory::getCategoryName)));
                    list.add(obj);
                });
            });

            long count = list.stream().map(e -> e.getOrgId() + "-" + e.getCategoryId()).distinct().count();
            Assert.isTrue(count == list.size(), "存在重复数据");

            if (!add) {
                qlService.deleteByWrapper(QlWrappers.update(ORDER_CONFIG_CATEGORY).eq(OrderConfigCategory::getConfigId, orderConfig.getConfigId()));
                qlService.update(schemaType, PurchaseMqlUtils.beanToRecord(orderConfig));
            } else {
                qlService.create(schemaType, PurchaseMqlUtils.beanToRecord(orderConfig));
            }

            try {
                qlService.create(ORDER_CONFIG_CATEGORY, list);
            } catch (Exception ex) {
                if (StringUtils.contains(ex.getMessage(), SouConstant.DUPLICATEENTRY)) {
                    String s = StringUtils.substringBetween(ex.getMessage(), "Duplicate entry '", "'");
                    String[] split = s.split("-");
                    OrderConfigCategory orderConfigCategory1 = list.stream().filter(e -> e.getCategoryId().toString().equals(split[0])).findFirst().get();
                    OrderConfigCategory orderConfigCategory2 = list.stream().filter(e -> e.getOrgId().toString().equals(split[1])).findFirst().get();
                    throw new BaseException(String.format("品类%s+公司%s已存在转单规则，请检查", orderConfigCategory1.getCategoryName(), orderConfigCategory1.getOrgName()));
                }
                throw ex;
            }

            return QlResult.empty();
        } catch (Exception e) {
            log.error("OrderConfig saveOrUpdateException", e);
            throw new BaseException(e.getMessage());
        }
    }

}
