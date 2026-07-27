package com.midea.cloud.srm.sou.purfixprice.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.ibm.icu.text.SimpleDateFormat;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.EasyExcelUtil;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouItem;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouOrderItem;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouProject;
import com.midea.cloud.srm.model.extapi.sou.purinq.vo.init.ApiPurInqSouProjectVO;
import com.midea.cloud.srm.model.sou.purfixprice.dto.ExtPurFixPriceInqQueryDTO;
import com.midea.cloud.srm.model.sou.purfixprice.dto.ExtPurFixPriceLineGroupQueryDTO;
import com.midea.cloud.srm.model.sou.purfixprice.dto.ExtPurFixPriceQueryDTO;
import com.midea.cloud.srm.model.sou.purfixprice.entity.ExtPurFixPriceFile;
import com.midea.cloud.srm.model.sou.purfixprice.entity.ExtPurFixPriceHead;
import com.midea.cloud.srm.model.sou.purfixprice.entity.ExtPurFixPriceLine;
import com.midea.cloud.srm.model.sou.purfixprice.enums.ExtPurFixPriceStatusEnum;
import com.midea.cloud.srm.model.sou.purfixprice.vo.ExtPurFixPriceLineGroupDetailVO;
import com.midea.cloud.srm.model.sou.purfixprice.vo.ExtPurFixPriceLineGroupVO;
import com.midea.cloud.srm.model.sou.purfixprice.vo.ExtPurFixPriceVO;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouOrderItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouVendor;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouSelectStatusEnum;
import com.midea.cloud.srm.sou.purfixprice.ExtPurFixPriceDownLoadVo;
import com.midea.cloud.srm.sou.purfixprice.dao.ExtPurFixPriceFileDAO;
import com.midea.cloud.srm.sou.purfixprice.dao.ExtPurFixPriceHeadDAO;
import com.midea.cloud.srm.sou.purfixprice.dao.ExtPurFixPriceHeadMapper;
import com.midea.cloud.srm.sou.purfixprice.service.ExtPurFixPriceQueryService;
import com.midea.cloud.srm.sou.purinq.dao.ExtPurInqSouItemDAO;
import com.midea.cloud.srm.sou.purinq.dao.ExtPurInqSouOrderItemDAO;
import com.midea.cloud.srm.sou.purinq.dao.ExtPurInqSouProjectDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouItemDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouProjectDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouVendorDAO;
import com.midea.cloud.srm.sou.sourcing.order.dao.SouOrderItemDAO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtPurFixPriceQueryServiceImpl implements ExtPurFixPriceQueryService {

    @Autowired
    private ExtPurFixPriceHeadDAO extPurFixPriceHeadDAO;
    @Autowired
    private ExtPurFixPriceFileDAO extPurFixPriceFileDAO;
    @Autowired
    private ExtPurFixPriceHeadMapper extPurFixPriceHeadMapper;
    @Autowired
    private ExtPurInqSouProjectDAO extPurInqSouProjectDAO;
    @Autowired
    private SouProjectDAO souProjectDAO;
    @Autowired
    private SouOrderItemDAO souOrderItemDAO;
    @Autowired
    private ExtPurInqSouOrderItemDAO extPurInqSouOrderItemDAO;
    @Autowired
    private ExtPurInqSouItemDAO extPurInqSouItemDAO;
    @Autowired
    private SouItemDAO souItemDAO;
    @Autowired
    private SouVendorDAO souVendorDAO;
    @Autowired
    private BaseClient baseClient;
    private static final String SIGN_COMMA = ",";

    /**
     * 定价列表查询
     */
    @Override
    public List<ExtPurFixPriceHead> listFixPrices(ExtPurFixPriceQueryDTO queryParam) {
        queryParam.formatParams();
        if (queryParam.getPageSize() != null && queryParam.getPageNum() != null) {
            PageMethod.startPage(queryParam.getPageNum(), queryParam.getPageSize());
        }
        return extPurFixPriceHeadDAO.lambdaQuery()
                .like(queryParam.getDesignProjectCode() != null, ExtPurFixPriceHead::getDesignProjectCode, queryParam.getDesignProjectCode())
                .like(queryParam.getDesignProjectName() != null, ExtPurFixPriceHead::getDesignProjectName, queryParam.getDesignProjectName())
                .eq(queryParam.getFixPriceStatus() != null, ExtPurFixPriceHead::getFixPriceStatus, queryParam.getFixPriceStatus())
                .and(queryParam.getCreatedBy() != null, wrapper -> wrapper
                        .like(ExtPurFixPriceHead::getCreatedBy, queryParam.getCreatedBy())
                        .or()
                        .like(ExtPurFixPriceHead::getCreatedFullName, queryParam.getCreatedBy()))
                .ge(queryParam.getCreationDateFrom() != null, ExtPurFixPriceHead::getCreationDate, queryParam.getCreationDateFrom())
                .le(queryParam.getCreationDateTo() != null, ExtPurFixPriceHead::getCreationDate, queryParam.getCreationDateTo())
                .list();
    }

    /**
     * 查询可用的集采询比价
     */
    @Override
    public List<ApiPurInqSouProjectVO> queryPurInq(ExtPurFixPriceInqQueryDTO queryParam) {
        if (queryParam.getPageSize() != null && queryParam.getPageNum() != null) {
            PageMethod.startPage(queryParam.getPageNum(), queryParam.getPageSize());
        }
        List<ApiPurInqSouProjectVO> resultList = extPurFixPriceHeadMapper.queryPurInq(queryParam);
        if (!resultList.isEmpty()) {
            Map<Long/* projectId */, ExtPurInqSouProject> inqProjectMap = extPurInqSouProjectDAO.listByIds(resultList.stream().map(ApiPurInqSouProjectVO::getProjectId).collect(Collectors.toSet()))
                    .stream().collect(Collectors.toMap(ExtPurInqSouProject::getProjectId, Function.identity()));
            resultList.forEach(result -> BeanUtils.copyProperties(inqProjectMap.get(result.getProjectId()), result));
        }
        return resultList;
    }

    /**
     * 查询定价单详情
     */
    @Override
    public ExtPurFixPriceVO getFixPrice(long purFixPriceHeadId) {
        ExtPurFixPriceHead fixPrice = extPurFixPriceHeadDAO.getById(purFixPriceHeadId);
        AssertUtils.notNull(fixPrice, "定价单[{0}]不存在", purFixPriceHeadId);
        List<ExtPurFixPriceFile> fileList = extPurFixPriceFileDAO.list(ExtPurFixPriceFile::getPurFixPriceHeadId, purFixPriceHeadId);

        ExtPurFixPriceVO vo = new ExtPurFixPriceVO();
        BeanUtils.copyProperties(fixPrice, vo);
        vo.setFileList(fileList);
        if (fixPrice.getSouProjectId() == null) {
            vo.setItemInfo(ExtPurFixPriceLineGroupVO.empty());
        } else {
            ExtPurFixPriceLineGroupQueryDTO queryP = new ExtPurFixPriceLineGroupQueryDTO();
            queryP.setProjectId(fixPrice.getSouProjectId());
            queryP.setPageNum(1);
            queryP.setPageSize(15);
            vo.setItemInfo(this.getPurInqOrderItems(queryP));
        }
        return vo;
    }

    /**
     * 查询集采询比价中标明细
     */
    @Override
    public ExtPurFixPriceLineGroupVO getPurInqOrderItems(ExtPurFixPriceLineGroupQueryDTO queryParam){
        queryParam.formatParams();
        SouProject souProject = souProjectDAO.getById(queryParam.getProjectId());
        AssertUtils.notNull(souProject, "寻源单[{0}]不存在", queryParam.getProjectId());
        Map<String/* code */, String/* name */> areaDictMap = baseClient.listAllByDictCode("REGION").stream()
                .collect(Collectors.toMap(DictItemDTO::getDictItemCode, DictItemDTO::getDictItemName));
        // 1: 查询中标明细数据
        PageMethod.startPage(queryParam.getPageNum(), queryParam.getPageSize());
        List<Long> itemIds = extPurFixPriceHeadMapper.getItemIdsForPage(queryParam);
        if (itemIds.isEmpty()) { return ExtPurFixPriceLineGroupVO.empty(); }
        List<SouOrderItem> orderItemList = souOrderItemDAO.lambdaQuery()
                .eq(SouOrderItem::getProjectId, queryParam.getProjectId())
                .eq(SouOrderItem::getSelectStatus, SouSelectStatusEnum.WIN)
                .in(SouOrderItem::getItemId, itemIds)
                .list();
        if (orderItemList.isEmpty()) { return ExtPurFixPriceLineGroupVO.empty(); }
        Map<Long/* orderItemId */, ExtPurInqSouOrderItem> inqOrderItemMap = extPurInqSouOrderItemDAO
                .listByIds(orderItemList.stream().map(SouOrderItem::getOrderItemId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(ExtPurInqSouOrderItem::getOrderItemId, Function.identity()));
        // 2: 查询物料需求信息
        Map<Long/* souItemId */, ExtPurInqSouItem> inqSouItemMap = extPurInqSouItemDAO
                .listByIds(orderItemList.stream().map(SouOrderItem::getSouItemId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(ExtPurInqSouItem::getSouItemId, Function.identity()));
        Map<Long/* souItemId */, SouItem> souItemMap = souItemDAO.listByIds(inqSouItemMap.keySet()).stream().collect(Collectors.toMap(SouItem::getSouItemId, Function.identity()));
        // 3: 查询供应商信息
        Map<Long/* vendorId */, SouVendor> vendorMap = souVendorDAO.list(SouVendor::getProjectId, queryParam.getProjectId())
                .stream().collect(Collectors.toMap(SouVendor::getVendorId, Function.identity()));
        // 4: 组装数据
        List<ExtPurFixPriceLine> voList = new ArrayList<>(orderItemList.size());
        for (SouOrderItem orderItem : orderItemList) {
            ExtPurFixPriceLine vo = new ExtPurFixPriceLine();
            voList.add(vo);

            ExtPurInqSouOrderItem inqOrderItem = inqOrderItemMap.get(orderItem.getOrderItemId());
            ExtPurInqSouItem inqSouItem = inqSouItemMap.get(orderItem.getSouItemId());
            SouItem souItem = souItemMap.get(orderItem.getSouItemId());
            SouVendor vendor = vendorMap.get(orderItem.getVendorId());

            // 轮次
            vo.setRound(orderItem.getRound());
            // 物料
            vo.setItemId(orderItem.getItemId());
            vo.setItemCode(orderItem.getItemCode());
            vo.setItemDesc(orderItem.getItemDesc());
            // 品牌
            vo.setBrand(inqSouItem.getBrand());
            // 供货区域
            vo.setArea(inqSouItem.getArea());
            // 规格型号
            vo.setModel(inqSouItem.getModel());
            // 单位
            vo.setUnit(orderItem.getUnit());
            // 需求数量
            vo.setRequireQuantity(orderItem.getRequireQuantity());
            // 备注
            vo.setRemark(souItem.getRemark());
            // 未税单价
            vo.setNotaxPrice(orderItem.getStandardNotaxPrice());
            // 含税单价
            vo.setTaxPrice(orderItem.getStandardTaxPrice());
            // 税率
            vo.setTaxKey(orderItem.getTaxKey());
            vo.setTaxRate(orderItem.getTaxRate());
            // 质保期
            vo.setExtWarrantyPeriod(inqOrderItem.getExtWarrantyPeriod());
            // 供应商
            vo.setVendorId(vendor.getVendorId());
            vo.setVendorCode(vendor.getVendorCode());
            vo.setVendorName(vendor.getVendorName());
            // 寻源单信息
            vo.setSouProjectId(orderItem.getProjectId());
            vo.setSouItemId(orderItem.getSouItemId());
            vo.setSouOrderId(orderItem.getOrderId());
            vo.setSouOrderItemId(orderItem.getOrderItemId());
            // 单价状态
            vo.setFixPriceStatus(ExtPurFixPriceStatusEnum.DRAFT);
            // 是否已签订合同
            vo.setSignContractFlag(Enable.N);
        }

        return ExtPurFixPriceLineGroupVO.build(areaDictMap, voList, new PageInfo<>(itemIds));
    }

    @Override
    public void downloadExcel(long purFixPriceHeadId, HttpServletResponse response) throws IOException {
        //1:查询数据
        ExtPurFixPriceVO extPurFixPriceVO= getFixPrice(purFixPriceHeadId);
        List<DictItem> items = baseClient.listDictItemByDictCode("REGION");
        //2:设置导出Excel头信息
        List<List<String>> headList = new ArrayList<>(300);
        // 2.0: 序号
        headList.add(new ArrayList<>(Collections.singletonList("序号")));
        // 2.1: 项目编号
        headList.add(new ArrayList<>(Collections.singletonList("项目编号")));
        // 2.2: 项目名称
        headList.add(new ArrayList<>(Collections.singletonList("项目名称")));
        // 2.3: 轮数
        headList.add(new ArrayList<>(Collections.singletonList("轮数")));
        // 2.4: 联系方式
        headList.add(new ArrayList<>(Collections.singletonList("联系方式")));
        // 2.5: 执行时间从
        headList.add(new ArrayList<>(Collections.singletonList("执行时间从")));
        // 2.6: 执行时间到
        headList.add(new ArrayList<>(Collections.singletonList("执行时间到")));
        // 2.7 :供货范围，多个数据之间用逗号相隔
        headList.add(new ArrayList<>(Collections.singletonList("供货范围")));
        // 2.8 :项目介绍
        headList.add(new ArrayList<>(Collections.singletonList("项目介绍")));
        // 2.9: 物资编码
        headList.add(new ArrayList<>(Collections.singletonList("物资编码")));
        // 2.10: 物料名称
        headList.add(new ArrayList<>(Collections.singletonList("物料名称")));
        // 2.11 :规格型号
        headList.add(new ArrayList<>(Collections.singletonList("规格型号")));
        // 2.12 :计量单位
        headList.add(new ArrayList<>(Collections.singletonList("计量单位")));
        // 2.12 :备注
        headList.add(new ArrayList<>(Collections.singletonList("备注")));
        // 2.13 : 区域
        ExtPurFixPriceLineGroupVO extPurFixPriceLineGroupVO=extPurFixPriceVO.getItemInfo();
        List<String>areas=new ArrayList<>(extPurFixPriceLineGroupVO.getAreas().values());
        Map<String,String>areasMap=extPurFixPriceLineGroupVO.getAreas();
        Map<String,String>areaMap2=new HashMap<>(15);
        for(Map.Entry<String,String>map: areasMap.entrySet()){
            areaMap2.put(map.getValue(), map.getKey());
        }
        areas.forEach(area -> {
            List<String> tempL = Arrays.asList(area, "TODO");
            //未税单价	中标供应商	质保期
            // 2.21.1: 未税单价
            List<String> v = JSON.parseArray(JSON.toJSONString(tempL), String.class);
            v.set(1, "未税单价");
            headList.add(v);
            // 2.21.2: 中标供应商
            v = JSON.parseArray(JSON.toJSONString(tempL), String.class);
            v.set(1, "中标供应商");
            headList.add(v);
            // 2.21.3: 质保期
            v = JSON.parseArray(JSON.toJSONString(tempL), String.class);
            v.set(1, "质保期");
            headList.add(v);
        });
        // 2.14 : 审批状态
        headList.add(new ArrayList<>(Collections.singletonList("审批状态")));

        // 3 :设置行数据
        //获取单位字典
        List<ExtPurFixPriceDownLoadVo>unitDict=extPurFixPriceHeadMapper.getUnit();
        Map<String,String>unitMap=unitDict.stream().collect(Collectors.toMap(ExtPurFixPriceDownLoadVo::getUnitCode,ExtPurFixPriceDownLoadVo::getUnitName));
        List<List<Object>> dataList = new ArrayList<>(extPurFixPriceVO.getItemInfo().getItemList().getSize());
        int index=0;
        //区域
        Map<String,String>itemMap=new HashMap<>(15);
        for(DictItem item:items){
            itemMap.put(item.getDictItemCode(),item.getDictItemName());
        }
        StringBuilder sb=new StringBuilder();
        String[] str=extPurFixPriceVO.getDesignArea().split(SIGN_COMMA);
        Boolean first=true;
        for(String s:str){
            if(!first) {
                sb.append(SIGN_COMMA);
            } else{
                first=false;
            }
            sb.append(itemMap.get(s));
        }
        for(ExtPurFixPriceLineGroupDetailVO extPurFixPriceLineGroupDetailVO:extPurFixPriceVO.getItemInfo().getItemList().getList()){
            List<Object> row = new ArrayList<>(300);
            dataList.add(row);
            //3.0 序号
            row.add(++index);
            //3.1 项目编号
            row.add(extPurFixPriceVO.getDesignProjectCode());
            //3.2 项目名称
            row.add(extPurFixPriceVO.getDesignProjectName());
            //3.3 轮数
            row.add(extPurFixPriceVO.getDesignNum());
            //3.4 联系方式
            row.add(extPurFixPriceVO.getDesignCreatePhone());
            //3.5 执行时间从

            SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");
            String time=formatter.format(extPurFixPriceVO.getExecuteTimeFrom());
            row.add(time);
            //3.6 执行时间到
            time=formatter.format(extPurFixPriceVO.getExecuteTimeTo());
            row.add(time);
            //3.7 供货范围
            row.add(sb.toString());
            //3.8 项目介绍
            row.add(extPurFixPriceVO.getDesignProjIntroduce());
            //3.9 物资编码
            row.add(extPurFixPriceLineGroupDetailVO.getItemCode());
            //3.10 物料名称
            row.add(extPurFixPriceLineGroupDetailVO.getItemDesc());
            //3.11 规格型号
            row.add(extPurFixPriceLineGroupDetailVO.getModel());
            //3.12 计量单位
            row.add(unitMap.get(extPurFixPriceLineGroupDetailVO.getUnit()));
            //3.13 备注
            row.add(extPurFixPriceLineGroupDetailVO.getRemark());
            Map<String, ExtPurFixPriceLine>vendorList= extPurFixPriceLineGroupDetailVO.getVendorOrderItemList();
            for(Map.Entry<String,String>map: areasMap.entrySet()){
                //3.14 未税单价
                //3.15 中标供应商
                //3.16 质保期
                if(!vendorList.containsKey(map.getKey())){
                    row.add("");
                    row.add("");
                    row.add("");
                    continue;
                }
                ExtPurFixPriceLine extPurFixPriceLine=vendorList.get(map.getKey());
                row.add(extPurFixPriceLine.getNotaxPrice());
                row.add(extPurFixPriceLine.getVendorName());
                row.add(extPurFixPriceLine.getExtWarrantyPeriod());
            }

            //3.17 审批状态
            String status="拟定";
            ExtPurFixPriceStatusEnum temp=extPurFixPriceVO.getFixPriceStatus();
            if("SUBMITTED".equals(temp.toString())){
                status="审批中";
            }
            else if("REJECTED".equals(temp.toString())){
                status="已驳回";
            }
            else if("WITHDRAW".equals(temp.toString())){
                status="已撤回";
            }
            else  if("ABANDONED".equals(temp.toString())){
                status="已废弃";
            }
            else  if("APPROVED".equals(temp.toString())){
                status="已审批";
            }
            row.add(status);
        }
        // 4: 导出
        try (OutputStream outputStream = EasyExcelUtil.getServletOutputStream(response, "协议定价单明细.xlsx")) {
            EasyExcel.write(outputStream)
                    .sheet(0)
                    .head(headList)
                    .doWrite(dataList);
        }
    }

}
