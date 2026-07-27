package com.midea.cloud.srm.sup.association.controller;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.sup.association.dto.ApiExtSupAssociationDTO;
import com.midea.cloud.srm.model.sup.association.dto.ApiExtSupAssociationQueryDTO;
import com.midea.cloud.srm.model.sup.association.entity.ExtSupAssociation;
import com.midea.cloud.srm.sup.association.service.ExtSupAssociationEventService;
import com.midea.cloud.srm.sup.association.service.ExtSupAssociationQueryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 招投标.询价信息 - 接口层
 *
 * @author
 * @since 2022/12/21
 */
@RestController
@RequestMapping("/ext/vendorManagement/relationSuppliers")
@Api(tags = "项目式询价立项(采购商端)")
@Slf4j
public class ExtSupAssociationController extends BaseController {

    @Autowired
    private ExtSupAssociationQueryService extSouInitQueryService;

    @Autowired
    private ExtSupAssociationEventService extSouInitEventService;


    /**
     * 分页查询
     *
     * @param souProjectQuery
     * @return
     */
    @PostMapping("/page")
    PageInfo<ExtSupAssociation> pageProjects(@RequestBody ApiExtSupAssociationQueryDTO souProjectQuery) {
        try {
            return new PageInfo<>(extSouInitQueryService.listProjects(souProjectQuery));
        } catch (Exception e) {
            log.error("bid pageProjects Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    @GetMapping("/getAssociationSup")
    @ApiOperation(value = "信息查询", notes = "基本信息")
    public ApiExtSupAssociationDTO getAssociationSup(@RequestParam(value = "associationId") Long associationId) {
        try {
            return extSouInitQueryService.getProjectInfo(associationId);
        } catch (Exception e) {
            log.error("bid getProjectInfo Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    @PostMapping("/editAssociationSup")
    @ApiOperation(value = "保存基本信息", notes = "保存基本信息")
    Long editProjectInfo(@RequestBody ApiExtSupAssociationDTO param) {
        try {
            return extSouInitEventService.editProject(param);
        } catch (Exception e) {
            log.error("bid editProjectInfo Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 删除项目信息
     *
     * @param associationId
     * @return
     */
    @ApiOperation("删除信息")
    @GetMapping("/delAssociationSup")
    void delAssociationSup(@RequestParam(value = "associationId") Long associationId) {
        try {
            extSouInitEventService.removeById(associationId);
        } catch (Exception e) {
            log.error("bid delProject Exception", e);
            throw new BaseException(e.getMessage());
        }
    }
}
