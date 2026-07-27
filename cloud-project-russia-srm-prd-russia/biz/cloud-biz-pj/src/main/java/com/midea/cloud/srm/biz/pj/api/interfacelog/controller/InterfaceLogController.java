package com.midea.cloud.srm.biz.pj.api.interfacelog.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.srm.biz.pj.api.interfacelog.service.IInterfaceLogLineService;
import com.midea.cloud.srm.biz.pj.api.interfacelog.service.IInterfaceLogService;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.pj.api.interfacelog.dto.InterfaceLogDTO;
import com.midea.cloud.srm.model.pj.api.interfacelog.entity.InterfaceLog;
import com.midea.cloud.srm.model.pj.api.interfacelog.entity.InterfaceLogLine;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
*  <pre>
 *  接口日志表 前端控制器
 * </pre>
*
* @author kuangzm@meicloud.com
* @version 1.00.00
*
*  <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2020-05-28 10:58:43
 *  修改内容:
 * </pre>
*/
@RestController
@RequestMapping("/interfacelog")
public class InterfaceLogController extends BaseController {

    @Autowired
    private IInterfaceLogService iInterfaceLogService;
    @Autowired
    private IInterfaceLogLineService iInterfaceLogLineService;
    
    /**
    * 获取
    * @param logId
    */
    @GetMapping("/get")
    public InterfaceLogDTO get(@RequestParam("logId") Long logId) {
        Assert.notNull(logId, "logId不能为空");
        InterfaceLogDTO dto = new InterfaceLogDTO();
        InterfaceLog log = iInterfaceLogService.getById(logId);
        dto.setServiceName(log.getServiceName());
        dto.setServiceType(log.getServiceType());
        dto.setType(log.getType());
        dto.setBillId(log.getBillId());
        dto.setBillType(log.getBillType());
        dto.setDealTime(log.getDealTime());
        dto.setFinishDate(log.getFinishDate());
        dto.setLogId(dto.getLogId());
        dto.setStatus(log.getStatus());
        dto.setTargetSys(log.getTargetSys());
        InterfaceLogLine line = this.iInterfaceLogLineService.getById(log.getLineLogId());
        dto.setServiceInfo(line.getServiceInfo());
        dto.setReturnInfo(line.getReturnInfo());
        dto.setErrorInfo(line.getErrorInfo());
        return dto;
    }

    /**
    * 添加日志或再次发送
    * @param interfaceLog
    */
    @PostMapping("/add")
    public void add(@RequestBody InterfaceLogDTO interfaceLog) {
    	iInterfaceLogService.createInterfaceLog(interfaceLog);
    }
    /**
    * 删除
    * @param id
    */
    @GetMapping("/delete")
    public void delete(Long id) {
        Assert.notNull(id, "id不能为空");
        iInterfaceLogService.removeById(id);
    }
    
    /**
     * 接口调用返回后修改日志记录
     * @param interfaceLog
     */
     @PostMapping("/update")
     public void update(@RequestBody InterfaceLogDTO interfaceLog) {
    	 iInterfaceLogService.updateInterfaceLog(interfaceLog);
     }
     
     

    /**
    * 修改
    * @param interfaceLog
    */
    @PostMapping("/modify")
    public void modify(@RequestBody InterfaceLog interfaceLog) {
        iInterfaceLogService.updateById(interfaceLog);
    }

    /**
    * 分页查询
    * @param interfaceLog
    * @return
    */
    @PostMapping("/listPage")
    public PageInfo<InterfaceLog> listPage(@RequestBody InterfaceLogDTO interfaceLog) {
        PageUtil.startPage(interfaceLog.getPageNum(), interfaceLog.getPageSize());
        String serviceName = interfaceLog.getServiceName();
        interfaceLog.setServiceName(null);
        String billId = interfaceLog.getBillId();
        interfaceLog.setBillId(null);
        QueryWrapper<InterfaceLog> wrapper = new QueryWrapper<InterfaceLog>(interfaceLog);
        
        
        if (StringUtils.isNoneBlank(billId)) {
        	wrapper.like("BILL_ID", billId);
        }
        if (null != interfaceLog.getCreationDateBegin()) {
        	wrapper.ge("CREATION_DATE", interfaceLog.getCreationDateBegin());
        }
        if (null != interfaceLog.getCreationDateEnd()) {
        	wrapper.le("CREATION_DATE", interfaceLog.getCreationDateEnd());
        }
        if (StringUtils.isNoneBlank(interfaceLog.getServiceInfo())) {
        	wrapper.inSql("LOG_ID", "SELECT LOG_ID FROM scc_api_interface_log_line where service_info like '%"+interfaceLog.getServiceInfo()+"%' ");
        }
        if (StringUtils.isNoneBlank(serviceName)) {
            wrapper.like("SERVICE_NAME",serviceName);
        }
        wrapper.orderByDesc("CREATION_DATE");
//        throw new BaseException("测试");
        return new PageInfo<InterfaceLog>(iInterfaceLogService.list(wrapper));
    }

    /**
    * 查询所有
    * @return
    */
    @PostMapping("/listAll")
    public List<InterfaceLog> listAll() {
        return iInterfaceLogService.list();
    }
 
}
