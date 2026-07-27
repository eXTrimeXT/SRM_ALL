package com.midea.cloud.srm.sou.sourcing.spi.init.edittechscores;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.BeanCopyUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.sou.enums.ScoreConfigItemEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtScoreRuleDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouItemDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouScoreRuleDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouTechScoreDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtScoreRule;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouGroup;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouTechScoreHead;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouTechScoreLine;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.bid.ipmonitors.IpMonitoryUtils;
import com.midea.cloud.srm.sou.sourcing.init.service.*;
import com.midea.cloud.srm.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.sou.sourcing.spi.init.editscorerules.ExtSouScoreRuleEditPO;
import com.midea.cloud.srm.sou.sourcing.spi.init.editsouitems.ApiExtSouItemEditHandler;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class ApiExtSouTechScoreEditHandler implements ISouSpiBean {

    @Autowired
    private IExtSouTechScoreHeadService techScoreHeadService;

    @Autowired
    private IExtSouTechScoreLineService techScoreLineService;

    @Autowired
    private IExtSouProjectService projectService;

    @Autowired
    private IExtSouGroupService groupService;

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    public ExtSouTechScorePO formatValidateAndConvert(ApiExtSouTechScoreDto param, String souType) {
        // 1: 数据格式化及校验
        this.formatAndValidate(param, souType);
        // 2: 数据转换
        ExtSouTechScorePO po = this.convert(param, souType);
        //校验数据
        this.validata(param, souType, po);
        return po;
    }

    protected void validata(ApiExtSouTechScoreDto param, String souType, ExtSouTechScorePO po) {
        if(ObjectUtils.anyNull(po.getScoreHead())) {
            throw new BaseException("技术评分头信息不存在！");
        }
        //查询评分人
        ExtSouGroup group = groupService.getById(po.getScoreHead().getGroupId());
        AssertUtils.notNull(group, "评分人信息有误！");
        AssertUtils.isTrue(AppUserUtil.getLoginAppUser().getUsername().equals(group.getUserName()), "非当前评分人不允许操作！");
    }

    public ExtSouTechScorePO formatValidateAndConvertFroImport(ApiExtSouTechScoreDto param, String souType) {
        // 1: 数据格式化及校验
        this.formatAndValidateForImport(param, souType);
        // 2: 数据转换
        return this.convert(param, souType);
    }

    public static final String formatInputValue(String value) {
        if(StringUtils.isBlank(value)) {
            return "";
        }
        value = value.replaceAll("\t", "");
        value = value.replaceAll("\r", "");
        value = value.replaceAll("\n", "");
        return value.trim();
    }


    /**
     * 入参格式化及校验-导入数据
     * @param param 参数
     * @param souType 参数
     */
    protected void formatAndValidateForImport(ApiExtSouTechScoreDto param, String souType) {

        //校验导入行数与导入模板行数一致，否则报错
        AssertUtils.isTrue(Integer.compare(param.getImportDataList().size(), param.getScoreRuleDtoList().size()) == 0, "评标模板存在修改，不允许导入");

        param.getImportDataList().stream().forEach(converData -> {
            //更新到写入
            String key = formatInputValue(StringUtils.joinWith("_", MapUtils.getString(converData, ExtSouTechScorePO.TITLE_KEY_LIST.get(0)[0]), MapUtils.getString(converData, ExtSouTechScorePO.TITLE_KEY_LIST.get(1)[0])));
            List<ApiExtScoreRuleDto> apiExtScoreRuleDtoList = param.getRuleDtoMap().get(key);
            if(CollectionUtils.isNotEmpty(apiExtScoreRuleDtoList)) {

                ApiExtScoreRuleDto apiExtScoreRuleDto = apiExtScoreRuleDtoList.get(0);
                if(Integer.compare(apiExtScoreRuleDtoList.size(), 1) == 1) {
                    //匹配最优值
                    String scoreDesc = formatInputValue(MapUtils.getString(converData, ExtSouTechScorePO.TITLE_KEY_LIST.get(3)[0]));
                    apiExtScoreRuleDto = apiExtScoreRuleDtoList.stream().filter(ruleDto -> formatInputValue(ObjectUtils.defaultIfNull(ruleDto.getScoreDesc(), "")).equals(scoreDesc)).findAny().orElse(apiExtScoreRuleDto);
                }

                ApiExtScoreRuleDto finalApiExtScoreRuleDto = apiExtScoreRuleDto;
                apiExtScoreRuleDto.getVendorScoreList().forEach(s -> {
                    String detailDescKey = StringUtils.joinWith("_", s.getVendorName(), ExtSouTechScorePO.TITLE_KEY_LIST.get(4)[0]);
                    String scoreKey = StringUtils.joinWith("_", s.getVendorName(), ExtSouTechScorePO.TITLE_KEY_LIST.get(5)[0]);

                    String scoreStr = MapUtils.getString(converData, scoreKey, null);
                    if(StringUtils.isNotBlank(scoreStr) && !isNumber(scoreStr)) {
                        errorChek(converData, StringUtils.joinWith(": ", scoreKey, "不是数字类型"), param);
                    }

                    s.setExtDescription(MapUtils.getString(converData, detailDescKey, ""));
                    s.setScore(toScore(scoreStr));

                    if(StringUtils.isBlank(s.getExtDescription())) {
                        errorChek(converData, StringUtils.joinWith(": ", detailDescKey, "未维护"), param);
                    }

                    //技术标
                    if(ScoreConfigItemEnum.TEH_REVIEW.getCode().equals(finalApiExtScoreRuleDto.getScoreItem()) && isNumber(finalApiExtScoreRuleDto.getMaxScore())) {
                        if(gtMaxScore(toScore(finalApiExtScoreRuleDto.getMaxScore()), s.getScore())) {
                            errorChek(converData, StringUtils.joinWith(": ", scoreKey, "评分大于最高分值"), param);
                        }
                    }
                    //非技术标
                    if(!ScoreConfigItemEnum.TEH_REVIEW.getCode().equals(finalApiExtScoreRuleDto.getScoreItem())) {
                        if(StringUtils.isNotBlank(scoreStr)) {
                            errorChek(converData, "非技术评审项，无需评分", param);
                        }
                    }
                });
            } else {
                errorChek(converData, "评审项不匹配", param);
            }
        });

    }

    private boolean gtMaxScore(BigDecimal maxScore, BigDecimal score) {
        if(ObjectUtils.anyNull(maxScore, score)) {
            return false;
        }
        return maxScore.compareTo(score) == -1;
    }

    public static boolean isNumber(String str){
        String reg = "^[0-9]+(.[0-9]+)?$";
        return str.matches(reg);
    }

    protected void errorChek(Map<String, Object> data, String errorMsg, ApiExtSouTechScoreDto param) {
        param.getImportCheck().set(false);
        if (data.containsKey(ApiExtSouItemDto.ERROR_MSG)) {
            data.put(ApiExtSouItemDto.ERROR_MSG, StringUtils.joinWith(";", MapUtils.getString(data, ApiExtSouItemDto.ERROR_MSG), errorMsg));
        } else {
            data.put(ApiExtSouItemDto.ERROR_MSG, errorMsg);
        }
    }


    private BigDecimal toScore(Object obj) {
        try {
            if(!Objects.isNull(obj)) {
                return new BigDecimal(obj.toString());
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 入参格式化及校验
     * @param param 参数
     * @param souType 参数
     */
    protected void formatAndValidate(ApiExtSouTechScoreDto param, String souType) {

    }

    /**
     * 数据转换
     * @param param 参数
     * @param souType 参数
     * @return 返回
     */
    protected ExtSouTechScorePO convert(ApiExtSouTechScoreDto param, String souType) {
        ExtSouTechScorePO po = new ExtSouTechScorePO();
        po.setScoreHead(this.doConvertScoreHead(param, souType));
        po.setScoreLineList(this.doConvertScoreLine(param, souType, po.getScoreHead()));
        return po;
    }

    /**
     * 转换得到评分行
     * @param param 参数
     * @param souType 参数
     * @param techScoreHead 参数
     * @return 返回
     */
    protected List<ExtSouTechScoreLine> doConvertScoreLine(ApiExtSouTechScoreDto param, String souType, ExtSouTechScoreHead techScoreHead) {

        List<ApiExtScoreRuleDto> scoreRuleDtoList = param.getScoreRuleDtoList();
        //查询系统中的评分项
        LambdaQueryWrapper<ExtSouTechScoreLine> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouTechScoreLine::getTechScoreHeadId, techScoreHead.getTechScoreHeadId());
        List<ExtSouTechScoreLine> lineList = techScoreLineService.list(queryWrapper);
        Map<String, ExtSouTechScoreLine> lineMap = lineList.stream().collect(Collectors.toMap(l -> StringUtils.joinWith("_", l.getGroupId(), l.getVendorId(), l.getScoreRuleLineId()), Function.identity(), (k1, k2)->k2));


        List<ExtSouTechScoreLine> scoreLineList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(scoreRuleDtoList)) {
            scoreRuleDtoList.stream().forEach(ruleDto -> {
                ruleDto.getVendorScoreList().forEach(vendorScore -> {
                    ExtSouTechScoreLine line = JSON.parseObject(JSON.toJSONString(vendorScore), ExtSouTechScoreLine.class);

                    line.setProjectId(ruleDto.getProjectId());
                    line.setScoreRuleLineId(ruleDto.getScoreRuleId());
                    if(Objects.isNull(line.getOrderId())) {
                        line.setOrderId(-1L);
                    }
                    line.setTechScoreHeadId(techScoreHead.getTechScoreHeadId());

                    //系统是否存在
                    String key = StringUtils.joinWith("_", line.getGroupId(), line.getVendorId(), line.getScoreRuleLineId());
                    ExtSouTechScoreLine existsLine = lineMap.get(key);
                    if(ObjectUtils.anyNull(existsLine)) {
                        if(Objects.isNull(line.getTechScoreLineId())) {
                            line.setTechScoreLineId(IdGenrator.generate());
                        }
                        scoreLineList.add(line);
                    } else {
                        line.setTechScoreLineId(null);
                        try {
                            BeanCopyUtil.copyProperties(existsLine, line, true);
                        } catch (Exception e) {
                            log.error("doConvertScoreLine BeanCopyUtil.copyProperties Exception", e);
                        }
                        scoreLineList.add(existsLine);
                    }

                });
            });
        }
        return scoreLineList;
    }

    /**
     * 转换得到评分头
     * @param param 参数
     * @param souType 参数
     * @return 返回
     */
    protected ExtSouTechScoreHead doConvertScoreHead(ApiExtSouTechScoreDto param, String souType) {

        List<ApiExtScoreRuleDto> scoreRuleDtoList = param.getScoreRuleDtoList();
        ExtSouTechScoreHead head = null;
        if(CollectionUtils.isNotEmpty(scoreRuleDtoList)) {
            head = techScoreHeadService.getById(scoreRuleDtoList.get(0).getVendorScoreList().get(0).getTechScoreHeadId());
        }
        return head;
    }

    @ApiOperation("评分规则信息保存前的额外处理")
    public void doHandlerBeforeEditProject(ApiExtSouTechScoreDto param, String souType) {

    }

    @ApiOperation("评分规则信息保存后的额外处理")
    public void doHandlerAfterEditProject(ApiExtSouTechScoreDto param, String souType, ExtSouTechScorePO po) {
        //监控IP地址
        LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
        IpMonitoryUtils.instance().ipMonitory(po.getScoreHead().getProjectId(), loginAppUser.getUserId(), loginAppUser.getUsername(), loginAppUser.getNickname(), SrmConstant.IP_MONITOR_TECH_SCORE);
    }


}
