package com.midea.cloud.srm.feign.pj.sou;

import com.midea.cloud.srm.feign.pj.sign.SignCallbackClient;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.pj.aihelper.FileCheckDto;
import com.midea.cloud.srm.model.pj.sccpjcmscallbacktemps.entity.SccPjCmsCallbackTemp;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrder;
import com.midea.cloud.srm.model.sou.openapi.inq.dto.init.ApiInqSouInitDTO;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * <pre>
 *
 * </pre>
 *
 * @author kuangzm
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2022/11/19 17:36:28
 *  修改内容:
 * </pre>
 */
@FeignClient(value = "cloud-biz-sou", contextId = "souSign",path = "/api-sou")
public interface SouSignClient extends SignCallbackClient {

    /**
     * 根据板块生成竞价: /sou/api/v1/npmbrg/
     * 根据板块生成竞价单号
     * @param invbuCode
     * @return
     */
    @GetMapping("/sou/api/v1/npmbrg/generateSeq")
    public String generateSeq(@RequestParam("invbuCode") String invbuCode);


    /**
     * editInq
     * @param param
     * @return
     */
    @PostMapping({"/buyer/inq/init/editInq"})
    @ApiOperation("暂存询价单")
    public long editInq(@RequestBody ApiInqSouInitDTO param);

    @PostMapping({"/ccApi/payment/callBackAsApiPayment"})
    @ApiOperation("财务共享接口-回调SRM")
    public void callBackAsApiPayment(@RequestBody List<SccPjCmsCallbackTemp> sccPjCmsCallbackTempList);
    /**
     * 获取供应商投标信息
     * @param vendorId 参数
     * @return 结果
     */
    @ApiOperation(value = "获取供应商投标信息")
    @GetMapping("/ext/buyer/bid/init/getLastDateBySup")
    List<SouOrder> getLastDateBySup(@RequestParam(value = "vendorId") Long vendorId);

    /**
     * 智能化建设技术文件
     * @param fileCheckDto 参数
     * @return 结果
     */
    @ApiOperation(value = "智能化建设技术文件")
    @PostMapping("/fileCheck/api/findOrderTransFile")
    List<Fileupload> findOrderTransFile(@RequestBody FileCheckDto fileCheckDto);

    /**
     * 智能化建设澄清文件
     * @param fileCheckDto 参数
     * @return 结果
     */
    @ApiOperation(value = "智能化建设澄清文件")
    @PostMapping("/fileCheck/api/findAnswerTransFile")
    List<Fileupload> findAnswerTransFile(@RequestBody FileCheckDto fileCheckDto);

}
