package com.midea.cloud.srm.biz.pj.changchengapi.sign.service.impl;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.midea.cloud.common.enums.contract.ContractStatus;
import com.midea.cloud.common.utils.JsonUtil;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.srm.biz.pj.changchengapi.sign.service.ISccPjSignOrderService;
import com.midea.cloud.srm.biz.pj.contractlock.SccPjSignOrderFileService;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.pj.sign.entity.SccPjSignOrder;
import com.midea.cloud.srm.model.pj.sign.entity.SccPjSignOrderFile;
import com.midea.cloud.srm.model.pj.sign.service.ISignCallbackService;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenQueryWrapper;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author huangbf3
 */
@Service
@Slf4j
public class ISccPjSignCallbackServiceImpl implements ISignCallbackService {
    @Override
    public void complete(Long businessId, String param, List<Fileupload> fileuploads) throws Exception {
        log.info("ISccPjSignCallbackServiceImplj进入"+businessId);
    }
}
