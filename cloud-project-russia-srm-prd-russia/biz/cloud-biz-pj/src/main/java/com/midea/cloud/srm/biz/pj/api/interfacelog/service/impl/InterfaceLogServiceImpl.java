package com.midea.cloud.srm.biz.pj.api.interfacelog.service.impl;

import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.common.utils.NamedThreadFactory;
import com.midea.cloud.dynamicds.proxy.TtlExecutorsProxy;
import com.midea.cloud.srm.biz.pj.api.interfacelog.mapper.InterfaceLogLineMapper;
import com.midea.cloud.srm.biz.pj.api.interfacelog.mapper.InterfaceLogMapper;
import com.midea.cloud.srm.biz.pj.api.interfacelog.service.IInterfaceLogLineService;
import com.midea.cloud.srm.biz.pj.api.interfacelog.service.IInterfaceLogService;
import com.midea.cloud.srm.model.pj.api.interfacelog.dto.InterfaceLogDTO;
import com.midea.cloud.srm.model.pj.api.interfacelog.entity.InterfaceLog;
import com.midea.cloud.srm.model.pj.api.interfacelog.entity.InterfaceLogLine;
import io.seata.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * <pre>
 *  接口日志表 服务实现类
 * </pre>
 *
 * @author kuangzm@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2020-05-28 10:58:43
 *  修改内容:
 * </pre>
 */
@Slf4j
@Service
public class InterfaceLogServiceImpl extends BaseServiceImpl<InterfaceLogMapper, InterfaceLog>
		implements IInterfaceLogService {

	private static final Executor CONSUMER_EXECUTOR = TtlExecutorsProxy.getTtlExecutor(new ThreadPoolExecutor(10, 300,
			60L, TimeUnit.SECONDS,
			new SynchronousQueue<>(),
			new NamedThreadFactory("api-log-consumer", true)));

	private static final BlockingQueue<InterfaceLog> LOG_QUEUE = new LinkedBlockingQueue<>(1000);

	@Autowired
	private InterfaceLogLineMapper interfaceLogLineMapper;
	@Autowired
	private IInterfaceLogLineService iInterfaceLogLineService;

	@PostConstruct
	public void init() {
		CONSUMER_EXECUTOR.execute(() -> {
			while (true) {
				try {
					InterfaceLog logDTO = LOG_QUEUE.take();

					this.save(logDTO);

					List<InterfaceLog> logList = new ArrayList<>();
					LOG_QUEUE.drainTo(logList, 500);
					while (CollectionUtils.isNotEmpty(logList)) {
						this.saveBatch(logList);

						logList.clear();
						LOG_QUEUE.drainTo(logList, 500);
					}
				} catch (Throwable e) {
					log.error("", e);
				}
			}
		});
	}

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public InterfaceLogDTO createInterfaceLog(InterfaceLogDTO interfaceLog) {
		try{
			//保存头信息
			InterfaceLog log = null;
			Long logLineId = IdGenrator.generate();
			if (null != interfaceLog.getLogId()) {
				log = this.getById(interfaceLog.getLogId());
				log.setDealTime(log.getDealTime()+1);
				log.setStatus(interfaceLog.getStatus());
				log.setLineLogId(logLineId);
				this.updateById(log);
			} else {
				log = new InterfaceLog();
				BeanUtils.copyProperties(interfaceLog, log);
				Long id = IdGenrator.generate();
				log.setLineLogId(logLineId);
				log.setDealTime(1L);
				log.setLogId(id);
				log.setServiceInfo(null);
				this.save(log);
				interfaceLog.setLogId(id);
			}
			//保存行信息
			if(StringUtils.isNotBlank(log.getServiceInfo())){
				List<InterfaceLogLine> lines = new ArrayList<>();
				int length = log.getServiceInfo().length();
				int oneLineLength = 3000;
				for(int i=0;i*oneLineLength<length;i++){
					InterfaceLogLine line = new InterfaceLogLine();
					line.setLogId(log.getLogId());
					line.setLogLineId(logLineId);
					line.setServiceInfo(interfaceLog.getServiceInfo().substring(i*oneLineLength,(i+1)*oneLineLength>length?length:(i+1)*oneLineLength));
					line.setReturnInfo(interfaceLog.getReturnInfo());
					line.setErrorInfo(interfaceLog.getErrorInfo());
					lines.add(line);
				}
				iInterfaceLogLineService.saveBatch(lines);
			}else{
				InterfaceLogLine line = new InterfaceLogLine();
				line.setLogId(log.getLogId());
				line.setLogLineId(logLineId);
				line.setServiceInfo(interfaceLog.getServiceInfo());
				line.setReturnInfo(interfaceLog.getReturnInfo());
				line.setErrorInfo(interfaceLog.getErrorInfo());
				interfaceLogLineMapper.insert(line);
			}
		}catch (Exception e){
			log.info("保存接口日志保存："+e.getMessage());
		}
		return interfaceLog;
	}

	@Override
	public void asyncAddLog(InterfaceLog interfaceLog) {
		LOG_QUEUE.add(interfaceLog);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateInterfaceLog(InterfaceLogDTO interfaceLog) {
		//更新头信息
		InterfaceLog log = this.getById(interfaceLog.getLogId());
		log.setStatus(interfaceLog.getStatus());
		log.setFinishDate(interfaceLog.getFinishDate());
		this.updateById(log);
		//更新行信息
		InterfaceLogLine line = interfaceLogLineMapper.selectById(log.getLineLogId());
		line.setReturnInfo(interfaceLog.getReturnInfo());
		line.setErrorInfo(interfaceLog.getErrorInfo());
		interfaceLogLineMapper.updateById(line);
	}
}
