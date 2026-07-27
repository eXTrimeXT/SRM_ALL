package com.midea.cloud.srm.sou.sourcing.init.controller;


import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiSouInitDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.swagger.init.ApiSouInitSwaggerDTO;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtEditInitInfoService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <pre>
 *  功能名称
 * </pre>
 *
 * @author xiaym13@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2024/3/20 15:46
 *  修改内容:
 * </pre>
 */
@Slf4j
@RestController
@RequestMapping("/sourcing/init")
public class SouProjectController {

    @Autowired
    private IExtSouProjectService iExtSouProjectService;


    @PostMapping("/queryByProjectNo")
    @ApiOperation("根据项目编号查询项目名称")
    @ApiOperationSupport(order = 3)
    public List<ExtSouProject> queryByProjectNo(@RequestBody ExtSouProject extSouProject) {
        return iExtSouProjectService.queryByProjectNo(extSouProject);
    }
}
