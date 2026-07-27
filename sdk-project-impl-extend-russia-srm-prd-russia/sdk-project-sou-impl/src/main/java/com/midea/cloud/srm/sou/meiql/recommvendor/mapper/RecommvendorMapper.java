package com.midea.cloud.srm.sou.meiql.recommvendor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.midea.cloud.srm.model.sou.recommvendor.dto.ExceptionSupplierDto;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorDto;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorQuickQueryParam;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorSouNoticeInfoDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
/**
 * 备注
 * @author huangbf3
 */
public interface RecommvendorMapper extends BaseMapper {

    /**
     * 推荐供应商-快查列表
     * @param param
     * @return
     */
    List<RecommvendorDto> recommvendorQuickQuery(RecommvendorQuickQueryParam param);

    /**
     * 查询供应商异常名录
     * @param param
     * @return
     */
    List<ExceptionSupplierDto> queryExceptionSupplier(Map<String, Object> param);

    /**
     * 查询近期采购情况
     * @param params
     * @return
     */
    List<RecommvendorSouNoticeInfoDto> queryNoticeInfo(Map<String, Object> params);
}
