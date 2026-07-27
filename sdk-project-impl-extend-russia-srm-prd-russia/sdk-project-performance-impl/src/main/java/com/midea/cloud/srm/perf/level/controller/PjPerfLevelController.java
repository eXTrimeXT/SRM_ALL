package com.midea.cloud.srm.perf.level.controller;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.perf.level.entity.PerfLevel;
import com.midea.cloud.srm.perf.level.service.IPjPerfLevelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 *  <pre>
 *  绩效等级表 前端控制器
 * </pre>
 *
 * @author luxc18@meiCloud.com
 * @version 1.00.00
 *
 *  <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023-10-30 09:26:13
 *  修改内容:
 * </pre>
 */
@Api(value = "PjPerfLevelController", tags = {"绩效等级表-二开"})
@RestController
@RequestMapping("/pj/perfLevel")
@Slf4j
public class PjPerfLevelController extends BaseController {

    @Autowired
    private IPjPerfLevelService iPjPerfLevelService;

    /**
     * Description 新增绩效等级信息
     * @Param
     * @return
     * @Author luxc18@meicloud.com
     * @Date 2020.06.03
     * @throws BaseException
     **/
    @ApiOperation(value = "新增绩效等级信息", notes = "新增绩效等级信息", httpMethod = "POST")
    @PostMapping("/savePerfLevel")
    public String savePerfLevel(@RequestBody PerfLevel perfLevel) {
        return iPjPerfLevelService.saveOrUpdatePerfLevel(perfLevel);
    }

    /**
     * Description 修改绩效等级信息
     * @Param
     * @return
     * @Author luxc18@meicloud.com
     * @Date 2020.06.03
     * @throws BaseException
     **/
    @ApiOperation(value = "修改绩效等级信息", notes = "修改绩效等级信息", httpMethod = "POST")
    @PostMapping("/updatePerfLevel")
    public String updatePerfLevel(@RequestBody PerfLevel perfLevel) {
        return iPjPerfLevelService.saveOrUpdatePerfLevel(perfLevel);
    }


}
