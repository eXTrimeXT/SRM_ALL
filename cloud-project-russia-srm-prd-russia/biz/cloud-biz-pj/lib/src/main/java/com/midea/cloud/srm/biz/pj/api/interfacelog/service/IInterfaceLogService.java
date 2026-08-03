package com.midea.cloud.srm.biz.pj.api.interfacelog.service;

import com.midea.cloud.common.service.BaseService;
import com.midea.cloud.srm.model.pj.api.interfacelog.dto.InterfaceLogDTO;
import com.midea.cloud.srm.model.pj.api.interfacelog.entity.InterfaceLog;

/**
*  <pre>
 *  接口日志表 服务类
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
public interface IInterfaceLogService extends BaseService<InterfaceLog> {
	/**
	 * createInterfaceLog
	 * @param interfaceLog
	 * @return
	 */
	InterfaceLogDTO createInterfaceLog(InterfaceLogDTO interfaceLog);

	/**
	 * asyncAddLog
	 * @param interfaceLog
	 */
	void asyncAddLog(InterfaceLog interfaceLog);

	/**
	 * updateInterfaceLog
	 * @param interfaceLog
	 */
	void updateInterfaceLog(InterfaceLogDTO interfaceLog);
}
