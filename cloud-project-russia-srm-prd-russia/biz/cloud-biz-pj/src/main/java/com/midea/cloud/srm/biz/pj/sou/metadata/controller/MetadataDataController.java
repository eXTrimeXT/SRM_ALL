package com.midea.cloud.srm.biz.pj.sou.metadata.controller;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.dto.MetadataDataDTO;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.dto.MetadataQueryDTO;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.vo.MetadataDataVO;
import com.midea.cloud.srm.biz.pj.sou.metadata.service.MetadataDataService;
import com.midea.cloud.srm.model.common.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <pre>
 * 可扩展字段数据访问
 * </pre>
 *
 * @author huangyq154@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2022/6/24 11:59
 *  修改内容:
 * </pre>
 */
@Lazy
@RestController
@RequestMapping("/metadata/data")
@Api(value = "MetadataDataController", tags = "可扩展字段执行")
public class MetadataDataController extends BaseController {
    @Autowired
    private MetadataDataService dataService;

    @PostMapping("getSingle")
    @ApiOperation(value = "获取单条数据(根据多条件)")
    public MetadataDataVO getSingle(@RequestBody MetadataQueryDTO queryDto) {
        return dataService.getSingle(queryDto);
    }

    @PostMapping("getSingleById")
    @ApiOperation(value = "获取单条数据(根据ID)")
    public MetadataDataVO getSingleById(@RequestBody MetadataQueryDTO queryDto) {
        return dataService.getSingleById(queryDto);
    }

    @PostMapping("getList")
    @ApiOperation(value = "获取列表数据")
    public List<MetadataDataVO> getList(@RequestBody MetadataQueryDTO queryDto) {
        return dataService.getList(queryDto);
    }

    @PostMapping("getListByPage")
    @ApiOperation(value = "获取分页列表数据")
    public PageInfo<MetadataDataVO> getListByPage(@RequestBody MetadataQueryDTO queryDto) {
        if (null == queryDto.getPageNum() || null == queryDto.getPageSize()) {
            throw new BaseException("请传入分页参数");
        }
        return dataService.getListByPage(queryDto);
    }

    @PostMapping("add")
    @ApiOperation(value = "新增数据")
    public Object add(@RequestBody MetadataDataDTO dataDto) throws Exception {
        return dataService.add(dataDto);
    }

    @PostMapping("batchAdd")
    @ApiOperation(value = "批量新增数据")
    public List<Object> batchAdd(@RequestBody MetadataDataDTO batchDataDto) throws Exception {
        return dataService.batchAdd(batchDataDto);
    }

    @PostMapping("update")
    @ApiOperation(value = "更新数据(根据多条件)")
    public String update(@RequestBody MetadataDataDTO dataDto) throws Exception {
        boolean result = dataService.update(dataDto);
        if (result) {
            return "更新成功";
        } else {
            return "没有数据被更新";
        }
    }

    @PostMapping("updateById")
    @ApiOperation(value = "更新数据(根据ID)")
    public String updateById(@RequestBody MetadataDataDTO dataDto) throws Exception {
        boolean result = dataService.updateById(dataDto);
        if (result) {
            return "更新成功";
        } else {
            return "没有数据被更新";
        }
    }

    @PostMapping("batchUpdateById")
    @ApiOperation(value = "批量更新数据(根据ID)")
    public String batchUpdateById(@RequestBody MetadataDataDTO batchDataDto) throws Exception {
        batchDataDto.setTableName("");
        boolean result = dataService.batchUpdateById(batchDataDto);
        if (result) {
            return "更新成功";
        } else {
            return "没有数据被更新";
        }
    }

    @PostMapping("delete")
    @ApiOperation(value = "删除数据(根据多条件)")
    public String delete(@RequestBody MetadataDataDTO dataDto) throws Exception {
        boolean result = dataService.delete(dataDto);
        if (result) {
            return "删除成功";
        } else {
            return "没有数据被删除";
        }
    }

    @PostMapping("deleteById")
    @ApiOperation(value = "删除数据(根据ID)")
    public String deleteById(@RequestBody MetadataDataDTO dataDto) throws Exception {
        boolean result = dataService.deleteById(dataDto);
        if (result) {
            return "删除成功";
        } else {
            return "没有数据被删除";
        }
    }

    @PostMapping("batchDeleteById")
    @ApiOperation(value = "批量删除数据(根据ID)")
    public String batchDeleteById(@RequestBody MetadataDataDTO batchDataDto) throws Exception {
        boolean result = dataService.batchDeleteById(batchDataDto);
        if (result) {
            return "删除成功";
        } else {
            return "没有数据被删除";
        }
    }
}
