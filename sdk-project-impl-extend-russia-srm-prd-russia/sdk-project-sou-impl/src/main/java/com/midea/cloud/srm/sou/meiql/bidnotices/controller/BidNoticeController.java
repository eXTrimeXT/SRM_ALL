package com.midea.cloud.srm.sou.meiql.bidnotices.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.poi.word.DocUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.model.sou.bidnotices.dto.BidNoticeDTO;
import com.midea.cloud.srm.model.sou.bidnotices.dto.BidNoticeDetailTemplateDTO;
import com.midea.cloud.srm.model.sou.bidnotices.dto.BidNoticeInternalTemplateDTO;
import com.midea.cloud.srm.sou.meiql.bidnotices.service.BidNoticeService;
import com.midea.cloud.srm.sou.meiql.bidnotices.util.XwpfdUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

/**
 * 备注
 * @author huangbf3
 */
@RestController
@RequestMapping("/sou/api/v1/bidNotice")
@Api(value = "中/落标通知", tags = "中/落标通知")
public class BidNoticeController {
    @Autowired
    private BidNoticeService bidNoticeService;

    @Autowired
    private ResourceLoader resourceLoader;

    @GetMapping("/add")
    @ApiOperation("新增")
    public BidNoticeDTO add(@ApiParam(value = "寻源单ID") Long projectId) throws Exception {
        return bidNoticeService.add(projectId);
    }

    /**
     * 盖章、签字
     * @param id
     * @param type
     * @param extOrgBuCode
     * @throws Exception
     */
    @GetMapping("/sign")
    @ApiOperation("盖章、签字")
    public void sign(@RequestParam(value = "id") Long id,@RequestParam(value = "type") String type, @RequestParam(value = "extOrgBuCode") String extOrgBuCode) throws Exception {
        bidNoticeService.signByType(id,type,extOrgBuCode);
    }

    @ApiOperation("下载项目中标通知模板")
    @GetMapping("/downloadProjectNoticeTemplate")
    public void downloadProjectNoticeTemplate(@RequestParam("bidNoticeDetailId")Long bidNoticeDetailId,HttpServletResponse response){
        String fileName;
        String templatePath;
        templatePath = "/template/project_bid_notice.docx";
        BidNoticeDetailTemplateDTO bidNoticeDetailTemplateDTO = bidNoticeService.getBidNoticeDetailInfo(bidNoticeDetailId);
        fileName = bidNoticeDetailTemplateDTO.getSouName()+"中标通知书.docx";
        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
            fileName = URLEncoder.encode(fileName, "UTF-8");
            response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            response.addHeader("Content-Disposition", "attachment;filename=" + fileName + "");

            byte[] data = XwpfdUtils.instance().getWordByte(fileName,templatePath,bidNoticeDetailTemplateDTO.toTemplateParams());
            IOUtils.write(data,out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(out!=null){
                try {
                    out.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    @ApiOperation("下载项目中标内部通知模板")
    @GetMapping("/downloadProjectNoticeInternalTemplate")
    public void downloadProjectNoticeInternalTemplate(@RequestParam("bidNoticeInternalId")Long bidNoticeInternalId,HttpServletResponse response) throws Exception {
        String fileName;
        String templatePath;
        templatePath = "/template/project_bid_notice_internal.docx";
        BidNoticeInternalTemplateDTO templateDTO = bidNoticeService.getBidNoticeInternalTemplateInfo(bidNoticeInternalId);
        fileName = templateDTO.getSouName()+"中标通知书(内部).docx";
        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
            fileName = URLEncoder.encode(fileName, "UTF-8");
            response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            response.addHeader("Content-Disposition", "attachment;filename=" + fileName + "");
            byte[] data = XwpfdUtils.instance().getWordByte(fileName,templatePath,templateDTO.toTemplateParams());
            IOUtils.write(data,out);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if(out!=null){
                try {
                    out.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

}
