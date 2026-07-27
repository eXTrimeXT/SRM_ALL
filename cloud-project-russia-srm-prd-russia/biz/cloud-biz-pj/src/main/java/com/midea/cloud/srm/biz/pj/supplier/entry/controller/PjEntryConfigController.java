package com.midea.cloud.srm.biz.pj.supplier.entry.controller;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.biz.pj.supplier.entry.service.IPjEntryConfigService;
import com.midea.cloud.srm.biz.pj.utils.MqlType;
import com.midea.cloud.srm.biz.pj.utils.PjQlOpenClientUtil;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.pj.supplier.entry.dto.PjEntryConfigDTO;
import com.midea.cloud.srm.model.supplierauth.entry.dto.EntryCategoryConfigSaveResultDTO;
import com.midea.cloud.srm.model.supplierauth.entry.entity.EntryCategoryConfig;
import com.midea.cloud.srm.model.supplierauth.entry.entity.EntryConfig;
import com.midea.cloud.srm.model.supplierauth.entry.entity.EntryFileConfig;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenQueryWrapper;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import oracle.jdbc.proxy.annotation.Post;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * <pre>
 *  准入流程配置 前端控制器
 * </pre>
 *
 * @author luxc18@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023-08-30 14:23:13
 *  修改内容:
 * </pre>
 */
@RestController
@RequestMapping("/pj/sup/entryConfig")
@Api(value = "PjEntryConfigController", tags = "准入流程配置二开")
public class PjEntryConfigController extends BaseController {

    @Autowired
    private PjQlOpenClientUtil pjQlOpenClientUtil;

    @Autowired
    private QlOpenClient qlOpenClient;

    @Autowired
    private IPjEntryConfigService iPjEntryConfigService;

    /**
     * 批量保存/更新准入流程配置头表信息
     *
     * @param entryConfigList
     */
    @ApiOperation(value = "批量保存/更新准入流程配置头表信息", notes = "校验:  \n" +
            "1.准入类型不能为空")
    @PostMapping("/batchSaveOrUpdate")
    public List<Long> batchSaveOrUpdate(@RequestBody List<PjEntryConfigDTO> entryConfigList) {
        Assert.isTrue(CollectionUtils.isNotEmpty(entryConfigList), "请选择要保存的数据");
        return iPjEntryConfigService.batchSaveOrUpdateList(entryConfigList);
    }


    /**
     * 分页条件查询-供应商准入流程数据列表
     *
     * @param entryConfig
     * @return
     */
    @ApiOperation(value = "分页条件查询-供应商准入流程数据列表", notes = "分页条件查询-供应商准入流程数据列表", httpMethod = "POST")
    @PostMapping("/listPageByParam")
    public PageInfo<PjEntryConfigDTO> listPageByParam(@RequestBody EntryConfig entryConfig) {
        Assert.notNull(entryConfig, "entryConfig不能为空");
        return iPjEntryConfigService.listPageByParam(entryConfig);
    }

}
