package com.midea.cloud.srm.supcooperate.job;

import com.alibaba.fastjson.JSON;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.result.BaseResult;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.ql.QlQueryWrapper;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.quartz.bind.Job;
import com.midea.cloud.quartz.handler.ExecuteableJob;
import com.midea.cloud.srm.mall.request.base.BaseRequestDTO;
import com.midea.cloud.srm.mall.result.jd.Order.OrderDetailHasChildResultDTO;
import com.midea.cloud.srm.mall.result.jd.Order.OrderDetailNoneChildResultDTO;
import com.midea.cloud.srm.mall.result.jd.Order.OrderDetailResultDTO;
import com.midea.cloud.srm.mall.service.jd.MallService;
import com.midea.cloud.srm.model.supcooperate.enums.MallTypeEnum;
import com.midea.cloud.srm.model.supcooperate.ext.JdMsgPush;
import com.midea.cloud.srm.supcooperate.ext.order.dto.ExtOrder;
import com.midea.cloud.srm.supcooperate.ext.order.dto.JDOrderDetailRequestDTO;
import com.midea.cloud.srm.supcooperate.mtmapping.service.JdMsgPushService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 *  更新京东订单推送信息-定时任务
 *
 *  订单详情中新增两个字段：extJdOrderId，extJdState(1已提交订单，待确认拆单消息；2已确认拆单消息；3校验未通过，已拒绝)，
 * 	extJdOrderId对应返回的主订单号；extJdState是用于定时任务判断的状态
 *
 * 	定时任务：查询订单列表中，所有 jdOrderId 不为空且 jdstate 值为1的数据
 * 			根据jdOrderId调用京东详情的接口，有两种返回数据结构，数据满足[jdOrderState>5或者是jdOrderState=5&orderState=0]则说明拆单完成
 * 			如果jdOrderState>5，代表订单正常流转，如果jdOrderState=5&orderState=0，代表该子订单取消（此状态只会出现在子订单上）
 * 				（1）、如果直接调用的返回结果没有拆单，一个SRM订单明细行的京东订单号只返回了父订单的数据结构，将此数据保存到查询推送的表中，设置子订单号和父订单号都为此京东订单号
 * 				（2）、如果直接调用的返回结果有拆单（拆单只会拆这一次，不会循环拆单），则用子订单的id再去调用查询详情的接口，返回几个没有子订单数据结构，
 * 					当查询的结构都满足[jdOrderState>5或者是jdOrderState=5&orderState=0]的时候，才能将这些子订单数据保存到推送信息表；
 * 			    （3）、推送信息保存后，需要更新订单表中的jdstate为2
 * </pre>
 *
 * @author chenlong182@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2024-3-13 19:04
 *  修改内容:
 * </pre>
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
@Job("OrderDetailJDStateJob")
@Slf4j
public class OrderJDStateJob implements ExecuteableJob {

    @Resource
    protected QlService qlService;

    @Resource
    private MallService mallService;

    @Resource
    private JdMsgPushService jdMsgPushService;

    @Override
    public BaseResult executeJob(Map<String, String> params) {

        //查询订单列表中，所有 jdOrderId 不为空且 jdstate 值为1的数据
        QlQueryWrapper qw = QlWrappers.query(ExtOrder.class)
                .select(ExtOrder::getExtJdOrderId,ExtOrder::getExtJdState)
                .isNotNull(ExtOrder::getExtJdOrderId)
                .eq(ExtOrder::getExtJdState,1);
        List<ExtOrder> extOrderList = qlService.queryByWrapper(qw, ExtOrder.class);
        log.info("京东拆单查询extOrderList==>", JSON.toJSONString(extOrderList));

        //调用京东查询订单详情接口查询状态
        if (CollectionUtils.isNotEmpty(extOrderList)){

            //记录要保存的推送信息
            List<JdMsgPush> jdMsgPushSaveList = new ArrayList<>();
            //记录要更新状态的订单信息
            List<Long> updateOrderId = new ArrayList<>();

            //查询订单详情接口为单个id进行查询，循环调用
            for (ExtOrder extOrder : extOrderList) {
                //组装入参对象
                JDOrderDetailRequestDTO jdOrderDetailRequestDTO = new JDOrderDetailRequestDTO();
                jdOrderDetailRequestDTO.setMallType(MallTypeEnum.JD.getCode());
                jdOrderDetailRequestDTO.setJdOrderId(extOrder.getExtJdOrderId());
                //调用查询京东订单详情接口，获取订单
                OrderDetailResultDTO orderDetailResultDTO = mallService.queryOrderTetailInfo(jdOrderDetailRequestDTO);
                log.info("京东拆单查询orderDetailResultDTO==>", JSON.toJSONString(orderDetailResultDTO));
                //判断是否存储数据
                boolean isSave = false;

                if (orderDetailResultDTO.isSuccess() && orderDetailResultDTO.getResult() != null){
                    //查询到京东订单信息
                    //根据类型转换为不同结构的实体类对象,以通过JSON.parseObject转成实体类
                    String type = orderDetailResultDTO.getResult().get("type").toString();

                    if (StringUtils.isNotEmpty(type) && "1".equals(type)) {
                        //type=1表示有子单结构OrderDetailHasChildResultDTO
                        OrderDetailHasChildResultDTO orderDetailHasChildResultDTO = JSON.parseObject(orderDetailResultDTO.getResult().toString(), OrderDetailHasChildResultDTO.class);
                        log.info("京东拆单查询orderDetailHasChildResultDTO==>", JSON.toJSONString(orderDetailHasChildResultDTO));
                        //获取子单信息
                        List<OrderDetailHasChildResultDTO.COrder> cOrders = orderDetailHasChildResultDTO.getCOrder();
                        log.info("京东拆单查询cOrders==>", JSON.toJSONString(cOrders));
                        //判断是否所有子单都满足条件
                        boolean canBeSaved = true;
                        //确认要保存的子单信息
                        List<JdMsgPush> childSaveList = new ArrayList<>();
                        //对子单再调用接口查询，所有子单满足条件则保存
                        for (OrderDetailHasChildResultDTO.COrder cOrder : cOrders) {

                            //使用子单号查询订单详情
                            //组装入参对象
                            JDOrderDetailRequestDTO childRequestDTO = new JDOrderDetailRequestDTO();
                            childRequestDTO.setMallType(MallTypeEnum.JD.getCode());
                            childRequestDTO.setJdOrderId(cOrder.getJdOrderId());
                            //调用查询京东订单详情接口，获取订单
                            OrderDetailResultDTO childDetailResultDTO = mallService.queryOrderTetailInfo(childRequestDTO);
                            log.info("京东拆单查询childDetailResultDTO==>", JSON.toJSONString(childDetailResultDTO));
                            //转换为无子单的结构OrderDetailNoneChildResultDTO，如果子订单的结构type不为2，说明这里设计有问题，需要更改
                            if ("2".equals(childDetailResultDTO.getResult().get("type").toString())) {
                                OrderDetailNoneChildResultDTO childDetailNoneChildResultDTO = JSON.parseObject(childDetailResultDTO.getResult().toString(), OrderDetailNoneChildResultDTO.class);
                                if (childDetailNoneChildResultDTO.getJdOrderState() > 5
                                        || (childDetailNoneChildResultDTO.getJdOrderState() == 5 && childDetailNoneChildResultDTO.getOrderState() == 0)) {
                                    //设置要保存到查询推送表的数据
                                    JdMsgPush jdMsgPush = new JdMsgPush();
                                    //设置主订单号和子订单号为改京东订单号
                                    jdMsgPush.setOrderId(childDetailNoneChildResultDTO.getJdOrderId().toString());
                                    jdMsgPush.setSubOrderId(childDetailNoneChildResultDTO.getJdOrderId().toString());
                                    //添加对象到保存队列
                                    childSaveList.add(jdMsgPush);
                                } else {
                                    //修改判断标识
                                    canBeSaved = false;
                                }
                            } else {
                                throw new BaseException("拆单异常，存在层级拆单");
                            }
                        }
                        if (canBeSaved){
                            //修改存储标识，当有子单不满足条件时，canBeSaved设置为false，不保存数据
                            isSave = canBeSaved;
                            jdMsgPushSaveList.addAll(childSaveList);
                        }

                    } else if (StringUtils.isNotEmpty(type) && "2".equals(type)) {
                        //type=2表示无子单的结构OrderDetailNoneChildResultDTO
                        OrderDetailNoneChildResultDTO orderDetailNoneChildResultDTO = JSON.parseObject(orderDetailResultDTO.getResult().toString(), OrderDetailNoneChildResultDTO.class);
                        log.info("京东拆单查询orderDetailNoneChildResultDTO==>", JSON.toJSONString(orderDetailNoneChildResultDTO));
                        //jdOrderState>5或者是jdOrderState=5&orderState=0
                        if (orderDetailNoneChildResultDTO.getJdOrderState() > 5
                                || (orderDetailNoneChildResultDTO.getJdOrderState() == 5 && orderDetailNoneChildResultDTO.getOrderState() == 0)) {
                            //保存订单数据到查询推送的表
                            JdMsgPush jdMsgPush = new JdMsgPush();
                            //未拆分，设置主订单号和子订单号为改京东订单号
                            jdMsgPush.setOrderId(orderDetailNoneChildResultDTO.getJdOrderId().toString());
                            jdMsgPush.setSubOrderId(orderDetailNoneChildResultDTO.getJdOrderId().toString());
                            //添加对象到保存队列
                            jdMsgPushSaveList.add(jdMsgPush);
                            //修改存储标识
                            isSave = true;
                        }
                    } else {
                        log.info("订单:"+extOrder.getOrderId()+"对应的京东订单:"+extOrder.getExtJdOrderId()+"存在未识别订单类型");
                    }
                } else {
                    log.info("订单:"+extOrder.getOrderId()+"对应的京东订单:"+extOrder.getExtJdOrderId()+"未查询到结果或结果为空");
                }
                if (isSave) {
                    updateOrderId.add(extOrder.getOrderId());
                }
            }
            //保存推送信息
            if (CollectionUtils.isNotEmpty(jdMsgPushSaveList)) {
                log.info("京东拆单查询jdMsgPushSaveList==>", JSON.toJSONString(jdMsgPushSaveList));
                jdMsgPushService.saveBatch(jdMsgPushSaveList);
            }
            //更新订单拆单状态
            if (CollectionUtils.isNotEmpty(updateOrderId)) {
                qlService.updateByWrapper(QlWrappers.update(ExtOrder.class)
                        .in(ExtOrder::getOrderId,updateOrderId)
                        .set(ExtOrder::getExtJdState,2));
            }
        }
        return BaseResult.buildSuccess("执行成功！");
    }
}
