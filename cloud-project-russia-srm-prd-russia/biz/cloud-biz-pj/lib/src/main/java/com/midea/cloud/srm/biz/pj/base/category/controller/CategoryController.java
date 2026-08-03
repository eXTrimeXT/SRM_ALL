package com.midea.cloud.srm.biz.pj.base.category.controller;


import com.alibaba.fastjson.JSONObject;
import com.gwm.open.sdk.OpenClient;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.biz.pj.base.category.service.ICategoryService;
import com.midea.cloud.srm.model.pj.changchengapi.dto.CategoryApiParamDTO;
import com.midea.cloud.srm.model.pj.changchengapi.dto.CategoryDTO;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author huangbf3
 */
@Slf4j
@RestController
@RequestMapping("/external/category")
public class CategoryController {

    @Value("${gwm.appkey}")
    private String appKey;
    @Value("${gwm.secret}")
    private String secret;
    @ApiModelProperty("源系统应用")
    @Value("${gwm.bpm.src-system}")
    private String srcSystem;

    @ApiModelProperty("创建流程")
    @Value("${gwm.url.category}")
    private String categoryUrl;

    @Autowired
    private ICategoryService iCategoryService;

    @ApiOperation(value = "分页查询物料品类")
    @PostMapping("/category")
    public String category(@RequestBody CategoryDTO categoryDTO ) {
        String url = categoryUrl;

        Map<String,String> headers = new HashMap<>(50);
        headers.put("SRC-SYSTEM",srcSystem);

        OpenClient openClient = new OpenClient(appKey,secret);
        String result = openClient.sendHttpPost(url,JSONObject.toJSONString(categoryDTO),"application/json",headers);

        log.info("请求参数:"+JSONObject.toJSONString(categoryDTO));
        log.info("返回结果:"+result);

        return result;
    }

    @ApiOperation(value = "批量新增更新")
    @PostMapping("/saveOrUpdateBatch")
    public void saveOrUpdateBatch(@RequestBody CategoryDTO categoryDTO ) {
        CategoryApiParamDTO categoryApiParamDTO = new CategoryApiParamDTO();
        categoryApiParamDTO.setCategoryDTO(categoryDTO);
        categoryApiParamDTO.setHaveNextPage(true);
        categoryDTO.setSize(500);
        int length = 4;
        String serialNum = Objects.toString(IdGenrator.generate());
        for(int i=0;i<=length;i++){
            categoryDTO.setCategoryLevel(i);
            categoryDTO.setPage(1);
            iCategoryService.saveOrUpdateBatch(categoryApiParamDTO, serialNum);
        }
    }


}
