package com.midea.cloud.srm.supcooperate.catalogonshelves.repo;

import cn.hutool.core.util.ObjectUtil;
import com.midea.cloud.meiql.api.action.DefaultAction;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.ProxyQlQueryAction;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.ProxyRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pm.pr.catalogonshelves.entity.CatalogOnShelves;
import com.midea.cloud.srm.model.pm.pr.catalogonshelves.entity.CatalogOnShelvesAttach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
 *  修改日期: 2023/11/1 17:42
 *  修改内容:
 * </pre>
 */
@Component
public class CatalogOnShelvesAttachCustomRepository extends ProxyRepository {
    @Autowired
    protected QlService qlService;

    public CatalogOnShelvesAttachCustomRepository() {
        //注册action
        this.register("batchImportPic", this::batchImportPic, true, "批量导入图片");
    }

    private QlResult batchImportPic(QlQueryAction queryAction) {
        List<Record> records = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        //转map
        Map<String, List<Record>> parentRecordMap = records.stream().collect(Collectors.groupingBy(e -> e.get(CatalogOnShelvesAttach::getFileSourceName).substring(0, 12), LinkedHashMap::new, Collectors.toList()));
        List<Record> resultList = new ArrayList<>();
        parentRecordMap.forEach((key, value) -> {
            int num12 = 12;
            if (key.length() >= num12) {
                //根据编码查询商品
                List<Record> catalogOnShelvesList = qlService.queryByWrapper(QlWrappers.query("CatalogOnShelves").eq("extGoodsCode", key), Record.class);
                Assert.isTrue(ObjectUtil.isNotEmpty(catalogOnShelvesList), "导入失败：没有商品编码为：" + key + " 的商品");
                Record catalogOnShelves = catalogOnShelvesList.get(0);
                for (int i = 0; i < value.size(); i++) {
                    value.get(i).set(CatalogOnShelvesAttach::getCatalogOnShelvesId, catalogOnShelves.get(CatalogOnShelves::getCatalogOnShelvesId));//赋值商品住建
                    if (i == 0) {
                        //查询是否有默认图，没有才设置
                        long count = qlService.countByWrapper(QlWrappers.query(CatalogOnShelvesAttach.class)
                                .eq(CatalogOnShelvesAttach::getCatalogOnShelvesId, catalogOnShelves.get(CatalogOnShelves::getCatalogOnShelvesId))
                                .eq(CatalogOnShelvesAttach::getIfDefaultPicture, Enable.Y.name())
                        );
                        if (count == 0) {
                            value.get(i).set(CatalogOnShelvesAttach::getIfDefaultPicture, Enable.Y.name());//
                        }
                    }
                }
                resultList.addAll(value);
            } else {
                throw new RuntimeException("导入失败：" + key + " 不符合命名规则");
            }
        });
        return super.create(ProxyQlQueryAction.proxy(queryAction, DefaultAction.SAVE.value(), resultList));
    }
}
