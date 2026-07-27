package com.midea.cloud.srm.sou.meiql.filecheck.repo;

import com.alibaba.fastjson.JSONArray;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.ql.QlQueryFieldWrapper;
import com.midea.cloud.meiql.api.spec.ql.QlQueryWrapper;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.meiql.core.util.ResultUtil;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.client.PjProjectExtClient;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.sou.filecheck.entity.OrderFileCheck;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
@Component
@Slf4j
public class OrderFileCheckRepository extends CrudRepository {

    @Autowired
    private QlService qlService;
    @Autowired
    private PjProjectExtClient pjProjectExtClient;
    @Autowired
    private BaseClient baseClient;

    public OrderFileCheckRepository() {
        super();
        this.register("detail", this::detail, "围串标识别结果");
    }


    public QlResult detail(QlQueryAction queryAction) {
        Record record = getPayloadForType(queryAction, Record.class);
        AssertUtils.notEmpty(record, "数据不能为空。");
        Long projectId = record.get(OrderFileCheck::getProjectId);
        AssertUtils.notNull(projectId,"寻源ID不能为空。");

        QlQueryWrapper qlQueryWrapper = QlWrappers.query(queryAction.getType()).eq(OrderFileCheck::getProjectId, projectId);
        long count = qlService.countByWrapper(qlQueryWrapper);
//        AssertUtils.isTrue(count == 0,"无数据，请发起围串标识别。");

        List<DictItem> partDict = baseClient.listDictItemByDictCode("AI_CHECK_FILE_PART_TYPE");
        JSONArray result = pjProjectExtClient.getFileCompare(projectId,partDict.stream().map(DictItem::getDictItemCode).collect(Collectors.joining(",")));

//        AssertUtils.isTrue(CollectionUtils.isNotEmpty(result),"结果正在识别中，请耐心等待。");

        OrderFileCheck orderFileCheck = new OrderFileCheck();
        orderFileCheck.setOrderFileCheckID(1L);
        QlResult qlResult = ResultUtil.build(queryAction, QlQueryFieldWrapper.field(OrderFileCheck::getOrderFileCheckID).getFieldName(), Collections.singletonList(orderFileCheck), false);
        qlResult.getRefValues(MqlType.ORDER_FILE_CHECK).forEach(e -> e.put("taskList",result));

        return qlResult;
    }


}
