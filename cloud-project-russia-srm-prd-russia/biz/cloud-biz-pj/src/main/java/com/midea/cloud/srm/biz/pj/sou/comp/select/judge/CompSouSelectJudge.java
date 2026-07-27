package com.midea.cloud.srm.biz.pj.sou.comp.select.judge;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.biz.pj.sou.comp.order.dao.CompSouOrderItemDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouProjectDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.order.dao.SouOrderItemDAOImpl;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouOrderItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouProjectStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouSelectStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 竞价 - 接口校验服务
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/16
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class CompSouSelectJudge {

    @Autowired
    private SouOrderItemDAOImpl souOrderItemDao;
    @Autowired
    private CompSouOrderItemDAOImpl compSouOrderItemDao;
    @Autowired
    private SouProjectDAOImpl souProjectDao;

    /**
     * 当前操作人是否可以生成价格审批单
     */
    public SouProject judgeCreatePricingApprovalAuth(long projectId) {
        SouProject souProject = souProjectDao.getById(projectId);
        AssertUtils.notNull(souProject, LocaleHandler.getLocaleMsg("寻源单[{0}]不存在"), projectId);
        AssertUtils.isTrue(souProject.getSouType().equals(SouTypeEnum.comp.name()), "寻源类型不匹配");
        if (SouProjectStatusEnum.EVALUATING.equals(souProject.getProjectStatus())) {
            // 评选中
            List<SouOrderItem> souOrderItemList = souOrderItemDao.lambdaQuery()
                    .eq(SouOrderItem::getProjectId, souProject.getProjectId())
                    .eq(SouOrderItem::getRound, souProject.getCurrentRound())
                    .list();
            Map<Long/* orderItemId */, CompSouOrderItem> compSouOrderItemMap = compSouOrderItemDao.lambdaQuery()
                    .in(
                            CompSouOrderItem::getOrderItemId,
                            souOrderItemList.stream().map(SouOrderItem::getOrderItemId).collect(Collectors.toSet())
                    )
                    .list()
                    .stream()
                    .collect(Collectors.toMap(CompSouOrderItem::getOrderItemId, Function.identity(), (k1, k2) -> k1));

            souOrderItemList.stream()
                    .filter(e -> SouSelectStatusEnum.WIN.equals(e.getSelectStatus()))
                    .forEach(e -> {
                        boolean hasWinAmount = e.getWinAmount() != null && e.getWinAmount().compareTo(BigDecimal.ZERO) > 0;
                        AssertUtils.isTrue(hasWinAmount, "尚有中标供应商未分配中标数量");
                    });
        }

        return souProject;
    }

}
