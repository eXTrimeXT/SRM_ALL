package com.midea.cloud.srm.sou.req.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.PjSouClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.sou.req.SouReqHead;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.req.enums.SouReqHeadStatusEnum;
import com.midea.cloud.srm.sou.req.service.SouReqApplyService;
import com.midea.cloud.srm.sou.req.vo.SouReqHeadVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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
 *  修改日期: 2023/10/28 11:03
 *  修改内容:
 * </pre>
 */
@Api(value = "SouAnnoController", tags = {"寻源不需要鉴权接口控制器"})
@RestController
@Slf4j
@RequestMapping("/require")
public class SouReqController {
    @Autowired
    private SouReqApplyService reqApplyService;


    @ApiOperation(value = "统计公示和不公示供应商推荐数量", notes = "统计公示和不公示供应商推荐数量")
    @PostMapping("/countRecomm")
    Map<String, Object> countRecomm(@RequestBody Map<String, Object> param) {
        try {
            return reqApplyService.countRecomm(param);
        } catch (Exception e) {
            log.error("countRecomm Exception", e);
            throw new BaseException(e.getMessage());
        }
    }


}
