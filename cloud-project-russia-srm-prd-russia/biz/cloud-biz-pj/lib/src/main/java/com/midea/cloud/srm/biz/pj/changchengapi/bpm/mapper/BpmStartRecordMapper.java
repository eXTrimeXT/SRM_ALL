package com.midea.cloud.srm.biz.pj.changchengapi.bpm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.BpmNewFlag;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.BpmStartRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * bpm审批发起记录表
 * @author ex_liuxy46
 */
@Mapper
public interface BpmStartRecordMapper extends BaseMapper<BpmStartRecord> {

}
