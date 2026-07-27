package com.midea.cloud.srm.perf.orderscoreman.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.midea.cloud.common.enums.ImportStatus;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.enums.perf.scoreproject.ScoreItemsProjectStatusEnum;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.listener.AnalysisEventListenerImpl;
import com.midea.cloud.common.result.ResultCode;
import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.EasyExcelUtil;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.perf.inditors.entity.IndicatorsHeader;
import com.midea.cloud.srm.model.perf.ordercheck.entity.PerfScoreItemsOrderCheckDetail;
import com.midea.cloud.srm.model.perf.ordercheck.enums.OrderCheckDetailStatusEnum;
import com.midea.cloud.srm.model.perf.ordercheck.enums.OrderCheckStatusEnum;
import com.midea.cloud.srm.model.perf.ordercheck.enums.OrderScoreManScoreStatusEnum;
import com.midea.cloud.srm.model.perf.orderscoreman.dto.PjScoreManScoringV1Import;
import com.midea.cloud.srm.model.perf.orderscoreman.entity.PjOrderScoreManScoringV1;
import com.midea.cloud.srm.model.perf.scoreproject.entity.PerfScoreItems;
import com.midea.cloud.srm.model.perf.scoreproject.entity.PerfScoreItemsMan;
import com.midea.cloud.srm.model.perf.scoreproject.entity.PerfScoreItemsOrderCheck;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.perf.common.ScoreManScoringConst;
import com.midea.cloud.srm.perf.indicators.service.IIndicatorsHeaderService;
import com.midea.cloud.srm.perf.ordercheck.service.IPerfScoreItemsOrderCheckDetailService;
import com.midea.cloud.srm.perf.orderscoreman.mapper.PjOrderScoreManScoringV1Mapper;
import com.midea.cloud.srm.perf.orderscoreman.service.IPjOrderScoreManScoringV1Service;
import com.midea.cloud.srm.perf.scoreproject.service.IPerfScoreItemsManService;
import com.midea.cloud.srm.perf.scoreproject.service.IPerfScoreItemsOrderCheckService;
import com.midea.cloud.srm.perf.scoreproject.service.IPerfScoreItemsService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <pre>
 *  绩效评分项目评分人表 服务实现类
 * </pre>
 *
 * @author luxc18@meiCloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2020-06-06 15:10:36
 *  修改内容:
 * </pre>
 */
@Service
public class PjOrderScoreManScoringV1ServiceImpl extends BaseServiceImpl<PjOrderScoreManScoringV1Mapper, PjOrderScoreManScoringV1> implements IPjOrderScoreManScoringV1Service {

    @Autowired
    private IPerfScoreItemsService iPerfScoreItemsService;

    @Autowired
    private IPerfScoreItemsManService iPerfScoreItemsManService;

    @Autowired
    private PjOrderScoreManScoringV1Mapper pjOrderScoreManScoringV1Mapper;

    @Autowired
    private IPerfScoreItemsOrderCheckDetailService perfScoreItemsOrderCheckDetailService;

    @Autowired
    private IPerfScoreItemsOrderCheckService perfScoreItemsOrderCheckService;

    @Autowired
    private FileCenterClient fileCenterClient;

    @Autowired
    private IIndicatorsHeaderService indicatorsHeaderService;

    @Override
    public List<PjOrderScoreManScoringV1> listScoreManScoringPage(PjOrderScoreManScoringV1 scoreManScoringV1) {
        // 只有采购商类型的用户才能评分
        LoginAppUser user = AppUserUtil.getLoginAppUser();
        String userName = "";
        if (null != user) {
            userName = user.getUsername();
        }
        Assert.notNull(userName, ScoreManScoringConst.USER_NAME_NOT_NULL);
        QueryWrapper<PjOrderScoreManScoringV1> wrapper = new QueryWrapper<>();
        if (null != scoreManScoringV1) {
            wrapper.like(StringUtils.isNotEmpty(scoreManScoringV1.getProjectName()), "PROJECT_NAME", scoreManScoringV1.getProjectName());
            wrapper.eq(scoreManScoringV1.getCompanyId() != null, "COMPANY_ID", scoreManScoringV1.getCompanyId());
            wrapper.like(StringUtils.isNotEmpty(scoreManScoringV1.getCompanyName()), "COMPANY_NAME", scoreManScoringV1.getCompanyName());
            wrapper.like(StringUtils.isNotEmpty(scoreManScoringV1.getIndicatorName()), "INDICATOR_NAME", scoreManScoringV1.getIndicatorName());
            wrapper.eq(null != scoreManScoringV1.getOrganizationId(), "ORGANIZATION_ID", scoreManScoringV1.getOrganizationId());
            wrapper.eq(StringUtils.isNotEmpty(scoreManScoringV1.getOrganizationName()), "ORGANIZATION_NAME", scoreManScoringV1.getOrganizationName());
            wrapper.eq(StringUtils.isNotEmpty(scoreManScoringV1.getIndicatorDimensionType()), "INDICATOR_DIMENSION_TYPE", scoreManScoringV1.getIndicatorDimensionType());
            wrapper.eq(null != scoreManScoringV1.getCategoryId(), "CATEGORY_ID", scoreManScoringV1.getCategoryId());
            wrapper.like(StringUtils.isNotEmpty(scoreManScoringV1.getCategoryName()), "CATEGORY_NAME", scoreManScoringV1.getCategoryName());
            // 是否已计算评分
            wrapper.eq(StringUtils.isNotEmpty(scoreManScoringV1.getIfEndScored()), "IF_END_SCORED", scoreManScoringV1.getIfEndScored());
            // 评分状态
            wrapper.in(CollectionUtils.isNotEmpty(scoreManScoringV1.getScoringStatusList()), "SCORING_STATUS", scoreManScoringV1.getScoringStatusList());
            // 是否以发起人身份查询
            // 是 以发起人身份查询
            if (Objects.equals(YesOrNo.YES.getValue(), scoreManScoringV1.getIfQueryByScoreItemsCreatedBy())) {
                wrapper.eq("SCORE_ITEMS_CREATED_USERNAME", userName);
            }
            // 否 不以发起人身份查询 只能查到自己作为评分人的数据
            if (Objects.equals(YesOrNo.NO.getValue(), scoreManScoringV1.getIfQueryByScoreItemsCreatedBy()) || StringUtils.isEmpty(scoreManScoringV1.getIfQueryByScoreItemsCreatedBy())) {
                /**控制评分人查看权限(当前用户只能查询到自己要评分的信息)*/
                wrapper.eq("SCORE_USER_NAME", StringUtils.isEmpty(scoreManScoringV1.getScoreUserName()) ? userName : scoreManScoringV1.getScoreUserName());
            }
            //评分时间
            wrapper.ge(scoreManScoringV1.getScoreStartDate() != null, "SCORE_DATE", scoreManScoringV1.getScoreStartDate());
            wrapper.le(scoreManScoringV1.getScoreEndDate() != null, "SCORE_DATE", scoreManScoringV1.getScoreEndDate());
        }
        wrapper.orderByDesc("LAST_UPDATE_DATE");
        List<PjOrderScoreManScoringV1> list = this.list(wrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            List<String> nameList = list.stream().map(PjOrderScoreManScoringV1::getIndicatorName).collect(Collectors.toList());
            List<IndicatorsHeader> indicatorsHeaders = indicatorsHeaderService.listIn(IndicatorsHeader::getIndicatorName, nameList);
            Map<String, String> nameMap = indicatorsHeaders.stream().collect(Collectors.toMap(IndicatorsHeader::getIndicatorName, IndicatorsHeader::getIndicatorLogic, (k1, k2) -> k2));
            for (PjOrderScoreManScoringV1 pjOrderScoreManScoringV1 : list) {
                pjOrderScoreManScoringV1.setIndicatorLogic(nameMap.get(pjOrderScoreManScoringV1.getIndicatorName()));
            }
        }
        return list;
    }

    @Override
    public String saveScoreManScoring(List<PjOrderScoreManScoringV1> scoreManScoringV1List) {
        Date currentDate = new Date();
        scoreManScoringV1List.stream().forEach(item -> {
            item.setScoringStatus(OrderScoreManScoreStatusEnum.SUBMITTED.name());
            item.setIfScored(YesOrNo.YES.getValue());
            item.setScoreDate(currentDate);
        });
        this.updateBatchById(scoreManScoringV1List);
        // 更新项目信息（主要是更新评分回应）
        updateScoreItem(scoreManScoringV1List);
        // 按项目+供应商+评分人+品类维度,更新复核明细状态,判断是部分提交还是已提交
        updateCheckDetailStatus(scoreManScoringV1List);
        return ResultCode.SUCCESS.getMessage();
    }

    @Override
    public Map<String, Object> importScoreManScoringV1Excel(MultipartFile file, Fileupload fileupload) throws Exception {
        // 文件校验
        EasyExcelUtil.checkParam(file, fileupload);
        // 读取数据
        List<PjScoreManScoringV1Import> scoreManScoringV1ImportList = readData(file);
        // 是否有报错标识
        AtomicBoolean errorFlag = new AtomicBoolean(false);
        // 获取数据
        getImportData(scoreManScoringV1ImportList, errorFlag);
        if (errorFlag.get()) {
            //报错
            fileupload.setFileSourceName("评分人绩效评分导入报错");
            Fileupload fileUpload = EasyExcelUtil.uploadErrorFile(fileCenterClient, fileupload,
                    scoreManScoringV1ImportList, PjScoreManScoringV1Import.class, file.getName(), file.getOriginalFilename(), file.getContentType());
            return ImportStatus.importError(fileUpload.getFileuploadId(), fileUpload.getFileSourceName());
        } else {
            // 更新数据库表数据
            updateScoreManScoringV1s(scoreManScoringV1ImportList);
        }
        return ImportStatus.importSuccess();
    }

    private void updateScoreManScoringV1s(List<PjScoreManScoringV1Import> scoreManScoringV1ImportList) {
        // 根据id获取数据
        List<Long> idList = scoreManScoringV1ImportList.stream().map(item -> Long.valueOf(item.getScoreManScoringId())).collect(Collectors.toList());
        Map<Long, PjScoreManScoringV1Import> idMap = scoreManScoringV1ImportList.stream().collect(Collectors.toMap(item -> Long.valueOf(item.getScoreManScoringId()), Function.identity(), (k1, k2) -> k2));
        List<PjOrderScoreManScoringV1> pjOrderScoreManScoringV1s = this.listByIds(idList);

        for (PjOrderScoreManScoringV1 item : pjOrderScoreManScoringV1s) {
            // 如果没填默认0
            PjScoreManScoringV1Import pjScoreManScoringV1Import = idMap.get(item.getScoreManScoringId());
            String scoreStr = pjScoreManScoringV1Import.getScore();
            item.setScore(StringUtils.isNotEmpty(scoreStr) ? new BigDecimal(scoreStr) : BigDecimal.ZERO);
            item.setComments(pjScoreManScoringV1Import.getComments());
        }
        this.updateBatchById(pjOrderScoreManScoringV1s);
    }

    private void getImportData(List<PjScoreManScoringV1Import> scoreManScoringV1ImportList, AtomicBoolean errorFlag) {
        if (CollectionUtils.isNotEmpty(scoreManScoringV1ImportList)) {
            for (PjScoreManScoringV1Import scoreManScoringV1Import : scoreManScoringV1ImportList) {
                StringBuffer errorMsg = new StringBuffer();
                if (StringUtils.isEmpty(scoreManScoringV1Import.getScoreManScoringId())) {
                    errorFlag.set(true);
                    errorMsg.append("唯一标识不能为空，请先导出需要评分的数据，进行评分，再进行导入。");
                }
                if (errorMsg.length() > 0) {
                    scoreManScoringV1Import.setErrorMsg(errorMsg.toString());
                } else {
                    scoreManScoringV1Import.setErrorMsg(null);
                }
            }
        }
    }

    private List<PjScoreManScoringV1Import> readData(MultipartFile file) {
        List<PjScoreManScoringV1Import> scoreManScoringV1ImportList = null;
        try {
            // 获取输入流
            InputStream inputStream = file.getInputStream();
            // 数据收集器
            AnalysisEventListenerImpl<PjScoreManScoringV1Import> listener = new AnalysisEventListenerImpl<>();
            ExcelReader excelReader = EasyExcel.read(inputStream, listener).build();
            // 第一个sheet读取类型
            ReadSheet readSheet = EasyExcel.readSheet(0).head(PjScoreManScoringV1Import.class).build();
            // 开始读取第一个sheet
            excelReader.read(readSheet);
            scoreManScoringV1ImportList = listener.getDatas();
        } catch (IOException e) {
            throw new BaseException("excel解析出错");
        }
        return scoreManScoringV1ImportList;
    }

    /**
     * 按项目+供应商+评分人+品类维度,更新复核明细状态,判断是部分提交还是已提交
     *
     * @param scoreManScoringV1List
     */
    private void updateCheckDetailStatus(List<PjOrderScoreManScoringV1> scoreManScoringV1List) {
        // 1. 找出scoreManScoringV1List相关是否全部提交的
        List<PjOrderScoreManScoringV1> dbList = pjOrderScoreManScoringV1Mapper.listByGroupList(scoreManScoringV1List);
        // 2.获取复核明细
        List<PerfScoreItemsOrderCheckDetail> detailList = pjOrderScoreManScoringV1Mapper.listCheckDetailByGroupList(scoreManScoringV1List);
        Map<String, List<PjOrderScoreManScoringV1>> groupListMap = dbList.stream().collect(Collectors.groupingBy(PjOrderScoreManScoringV1::checkDeatailGroupStr));
        // {"联合主键":"SUBMITTED/PART_SUBMITTED"}
        Map<String, String> statusMap = new HashMap<>(50);
        for (String key : groupListMap.keySet()) {
            List<PjOrderScoreManScoringV1> tempList = groupListMap.get(key);
            List<PjOrderScoreManScoringV1> unSubmittedList = tempList.stream().filter(item -> YesOrNo.NO.getValue().equals(item.getIfScored())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(unSubmittedList)) {
                statusMap.put(key, OrderCheckDetailStatusEnum.PART_SUBMITTED.name());
            } else {
                statusMap.put(key, OrderCheckDetailStatusEnum.SUBMITTED.name());
            }
        }
        // 3. 更新
        for (PerfScoreItemsOrderCheckDetail checkDetail : detailList) {
            String key = checkDetail.getScoreItemsId() + "-" + checkDetail.getCompanyId() + "-" + checkDetail.getScoreUserName() + "-" + checkDetail.getCategoryId();
            if (statusMap.containsKey(key)) {
                checkDetail.setStatus(statusMap.get(key));
            }
        }
        perfScoreItemsOrderCheckDetailService.updateBatchById(detailList);
        // 新复核主表,项目+供应商维度,明细都是已提交,才算待复核
        List<Long> checkIdList = detailList.stream().map(PerfScoreItemsOrderCheckDetail::getOrderCheckId).collect(Collectors.toList());
        List<PerfScoreItemsOrderCheckDetail> allDetailList = perfScoreItemsOrderCheckDetailService.listIn(PerfScoreItemsOrderCheckDetail::getOrderCheckId, checkIdList);
        Map<Long, List<PerfScoreItemsOrderCheckDetail>> groupMapList = allDetailList.stream().collect(Collectors.groupingBy(PerfScoreItemsOrderCheckDetail::getOrderCheckId));
        List<Long> enableCheckList = new ArrayList<>();
        for (Long key : groupMapList.keySet()) {
            List<PerfScoreItemsOrderCheckDetail> tempList = groupMapList.get(key);
            // 未完成评分的列
            List<PerfScoreItemsOrderCheckDetail> unfishList = tempList.stream().filter(item -> !OrderCheckDetailStatusEnum.SUBMITTED.name().equals(item.getStatus())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(unfishList)) {
                enableCheckList.add(key);
            }
        }
        if (CollectionUtils.isNotEmpty(enableCheckList)) {
            perfScoreItemsOrderCheckService.update(Wrappers.lambdaUpdate(PerfScoreItemsOrderCheck.class)
                    .set(PerfScoreItemsOrderCheck::getStatus, OrderCheckStatusEnum.TO_BE_REVIEWED.name())
                    .in(PerfScoreItemsOrderCheck::getOrderCheckId, enableCheckList)
            );
        }
    }

    private void updateScoreItem(List<PjOrderScoreManScoringV1> scoreManScoringV1List) {
        // 如果项目 某人评分完毕，则修改已评分人数+1
        List scoreItemsIds = scoreManScoringV1List.stream().map(PjOrderScoreManScoringV1::getScoreItemsId).filter(x -> Objects.nonNull(x)).collect(Collectors.toList());
        Map<Long, List<PjOrderScoreManScoringV1>> perfScoreManScoringMap = CollectionUtils.isEmpty(scoreItemsIds) ? Collections.emptyMap() :
                this.list(Wrappers.lambdaQuery(PjOrderScoreManScoringV1.class).in(PjOrderScoreManScoringV1::getScoreItemsId, scoreItemsIds))
                        .stream().collect(Collectors.groupingBy(PjOrderScoreManScoringV1::getScoreItemsId));

        if (MapUtils.isNotEmpty(perfScoreManScoringMap)) {
            perfScoreManScoringMap.forEach((scoreItemsId, scoreManScoringList) -> {
                PerfScoreItems updateScoreItems = iPerfScoreItemsService.getById(scoreItemsId);
                // 根据评分人分组
                Map<String, List<PjOrderScoreManScoringV1>> scoreManScoringByUserMap = scoreManScoringList.stream()
                        .collect(Collectors.groupingBy(PjOrderScoreManScoringV1::getScoreUserName));
                // 已评分
                AtomicInteger scoredCount = new AtomicInteger(0);
                if (MapUtils.isNotEmpty(scoreManScoringByUserMap)) {
                    // 对于某一个项目，当前评分人对于所有指标，品类都已经评分，则计入评分回应
                    scoreManScoringByUserMap.forEach((userName, scoringList) -> {
                        if (scoringList.stream().allMatch(e -> Objects.equals(YesOrNo.YES.getValue(), e.getIfScored()))) {
                            scoredCount.addAndGet(1);
                        }
                    });
                }
                // 获取当前评分项目的评分人集合
                List<PerfScoreItemsMan> scoreItemsManList = CollectionUtils.isEmpty(scoreItemsIds) ? Collections.emptyList() :
                        iPerfScoreItemsManService.list(Wrappers.lambdaQuery(PerfScoreItemsMan.class).eq(PerfScoreItemsMan::getScoreItemsId, scoreItemsId));
                // 已评分人数不能大于总评分人数
                int count = scoredCount.get();
                if (count < scoreItemsManList.size()) {
                    updateScoreItems.setScorePeople(Long.valueOf(count));
                    updateScoreItems.setProjectStatus("PART_SCORE_CALCULATED");// 部分计算得分
                    iPerfScoreItemsService.updateById(updateScoreItems);
                } else if (count == scoreItemsManList.size()) {
                    updateScoreItems.setProjectStatus(ScoreItemsProjectStatusEnum.SCORE_CALCULATED.getValue());
                    updateScoreItems.setScorePeople(Long.valueOf(count));
                    iPerfScoreItemsService.updateById(updateScoreItems);
                }
            });
        }
    }
}
