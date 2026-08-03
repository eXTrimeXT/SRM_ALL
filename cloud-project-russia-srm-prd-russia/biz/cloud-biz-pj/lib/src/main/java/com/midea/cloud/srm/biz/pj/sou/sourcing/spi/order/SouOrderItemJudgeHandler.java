package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.order;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
/**
 * 创建价格审批单前，校验报价行信息
 * @author: hesl41
 * @Date: 2023/2/15 11:03
 */
@Service
public class SouOrderItemJudgeHandler implements ISouSpiBean {

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    public void checkCreatePricingApproval(SouProject souProject, List<SouOrderItem> orderItemList) {
        AssertUtils.notEmpty(orderItemList, "缺少中标信息");
        orderItemList.forEach(s -> {
            AssertUtils.isTrue(Objects.nonNull(s.getWinAmount()) &&
                    s.getWinAmount().compareTo(BigDecimal.ZERO) > 0, "请填中标数量并大于0");
        });
    }
}
