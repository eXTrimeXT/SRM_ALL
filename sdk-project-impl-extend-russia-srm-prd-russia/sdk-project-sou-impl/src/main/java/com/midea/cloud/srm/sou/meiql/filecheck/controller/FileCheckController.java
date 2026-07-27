package com.midea.cloud.srm.sou.meiql.filecheck.controller;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.model.file.oss.DownLoadResultDto;
import com.midea.cloud.srm.model.pj.aihelper.FileCheckDto;
import com.midea.cloud.srm.sou.bid.event.ExtBidSouEvent;
import com.midea.cloud.srm.sou.meiql.filecheck.service.FileCheckService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * 备注
 * @author bs
 */
@RestController
@RequestMapping("/fileCheck/api")
@Api(value = "智能化建设", tags = "智能化建设")
public class FileCheckController {

    @Autowired
    private FileCheckService fileCheckService;
    @Autowired
    private ExtBidSouEvent extBidSouEvent;

    @PostMapping("/findOrderFile")
    @ApiOperation("智能化建设技术文件")
    public List<DownLoadResultDto> findOrderFile(@ApiParam(value = "智能化建设项目及文件") @RequestBody FileCheckDto fileCheckDto) throws Exception {
        AssertUtils.notNull(fileCheckDto,"参数不能为空");
        AssertUtils.notNull(fileCheckDto.getProjectId(),"参数不能为空");
        AssertUtils.notEmpty(fileCheckDto.getOrderDocIds(),"参数不能为空");
       return fileCheckService.findOrderFile(fileCheckDto);
    }
    @PostMapping("/findAnswerFile")
    @ApiOperation("智能化建设澄清文件")
    public List<DownLoadResultDto> findAnswerFile(@ApiParam(value = "智能化建设项目及文件") @RequestBody FileCheckDto fileCheckDto) throws Exception {
        AssertUtils.notNull(fileCheckDto,"参数不能为空");
        AssertUtils.notNull(fileCheckDto.getProjectId(),"参数不能为空");
        AssertUtils.notEmpty(fileCheckDto.getOrderDocIds(),"参数不能为空");
        return fileCheckService.findAnswerFile(fileCheckDto);
    }
    @PostMapping("/findPageFile")
    @ApiOperation("智能化建设分页文件")
    public void findPageFile(@ApiParam(value = "智能化建设分页文件") @RequestBody FileCheckDto fileCheckDto, HttpServletResponse response) throws Exception {
        AssertUtils.notNull(fileCheckDto,"参数不能为空");
        AssertUtils.notNull(fileCheckDto.getPageNum(),"参数不能为空");
        AssertUtils.notEmpty(fileCheckDto.getOrderDocIds(),"参数不能为空");
        AssertUtils.isTrue(fileCheckDto.getOrderDocIds().size()==1,"参数有误");
        fileCheckService.pageFile(fileCheckDto, response);
    }

    @PostMapping("/test")
    @ApiOperation("智能化建设测试")
    public void test(@ApiParam(value = "智能化建设测试") @RequestBody FileCheckDto fileCheckDto) throws Exception {
        extBidSouEvent.pushTechEvent(fileCheckDto.getProjectId());
    }


}
