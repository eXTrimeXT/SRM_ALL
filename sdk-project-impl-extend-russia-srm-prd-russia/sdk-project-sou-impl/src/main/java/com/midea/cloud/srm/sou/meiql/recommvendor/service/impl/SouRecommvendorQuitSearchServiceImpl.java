package com.midea.cloud.srm.sou.meiql.recommvendor.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.srm.model.pj.changchengapi.dto.CompanyAQCApiDTO;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorDto;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorProjectDto;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorQuickQueryParam;
import com.midea.cloud.srm.model.sou.recommvendor.enums.RecommType;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.meiql.recommvendor.mapper.RecommvendorMapper;
import com.midea.cloud.srm.sou.meiql.recommvendor.service.SouRecommvendorQuitSearchService;
import com.midea.cloud.srm.sou.meiql.recommvendor.service.SouRecommvendorRiskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class SouRecommvendorQuitSearchServiceImpl implements SouRecommvendorQuitSearchService {

    @Autowired
    private QlOpenClient qlOpenClient;

    @Autowired
    private RecommvendorMapper recommvendorMapper;

    @Autowired
    private SouRecommvendorRiskService recommvendorRiskService;

    @Autowired
    private QlService qlService;

    /**
     * 说明
     * 寻源需求来源：可选择寻源需求报名成功的供应商&供应商库选择（供应商仅可选择  组织状态：生效，供应商品类状态：合格、验证中的供应商）
     *
     * 13新增
     * 说明
     * 需求池转推荐供应商&追加供应商时，仅可选择  组织状态：生
     * @param param
     * @return
     */
    @Override
    public PageInfo queryVendor(RecommvendorQuickQueryParam  param) {
        //来自寻源需求单
        RecommvendorProjectDto recommvendorProjectDto = qlService.readByKey(RecommType.RecommvendorProject.name(), param.getProjectId(), RecommvendorProjectDto.class);
        param.setCategoryCode(recommvendorProjectDto.getExtCategoryCode());

        PageUtil.startPage(param.getPageNum(), param.getPageSize());
        List<RecommvendorDto> recommvendorDtoList = recommvendorMapper.recommvendorQuickQuery(param);
        //大数据爬虫接口
        recommvendorRiskService.crawler(recommvendorDtoList, false, null);
        return new PageInfo(recommvendorDtoList);
    }

    public static void main(String[] args) {
        List<Object> list = new ArrayList<>();
        list.add("string");
        list.add("string0");
        list.add("string00");
        list.add("string1");

        PageInfo page = PageUtil.pagingByFullData(3, 5, list);
        log.info(JSON.toJSONString(page));

        PageInfo pageInfo =  PageUtil.pagingByTotalPage(3, 11L, 2,5, list);
        log.info(JSON.toJSONString(pageInfo.getList()));
    }
}
