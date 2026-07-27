package com.midea.cloud.srm.biz.pj.sou.metadata.service;


import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.service.BaseService;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.dto.MetadataDataDTO;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.dto.MetadataQueryDTO;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.entity.MetadataData;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.vo.MetadataDataVO;

import java.util.List;

/**
 * <pre>
 *
 * </pre>
 *
 * @author huangyq154@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2022/6/24 16:54
 *  修改内容:
 * </pre>
 */
public interface MetadataDataService extends BaseService<MetadataData> {
    /**
     * 备注
     * @param queryDto
     * @return
     */
    MetadataDataVO getSingle(MetadataQueryDTO queryDto);

    /**
     * 备注
     * @param queryDto
     * @return
     */
    MetadataDataVO getSingleById(MetadataQueryDTO queryDto);

    /**
     * 备注
     * @param queryDto
     * @return
     */
    List<MetadataDataVO> getList(MetadataQueryDTO queryDto);

    /**
     * 备注
     * @param queryDto
     * @return
     */
    PageInfo<MetadataDataVO> getListByPage(MetadataQueryDTO queryDto);

    /**
     * 备注
     * @param dataDto
     * @return
     * @throws Exception
     */
    Object add(MetadataDataDTO dataDto) throws Exception;

    /**
     * 备注
     * @param batchDataDto
     * @return
     * @throws Exception
     */
    List<Object> batchAdd(MetadataDataDTO batchDataDto) throws Exception;

    /**
     * 备注
     * @param dataDto
     * @return
     * @throws Exception
     */
    boolean update(MetadataDataDTO dataDto) throws Exception;

    /**
     * 备注
     * @param dataDto
     * @return
     * @throws Exception
     */
    boolean updateById(MetadataDataDTO dataDto) throws Exception;

    /**
     * 备注
     * @param batchDataDto
     * @return
     * @throws Exception
     */
    boolean batchUpdateById(MetadataDataDTO batchDataDto) throws Exception;

    /**
     * 备注
     * @param dataDto
     * @return
     * @throws Exception
     */
    boolean delete(MetadataDataDTO dataDto) throws Exception;

    /**
     * 备注
     * @param dataDto
     * @return
     * @throws Exception
     */
    boolean deleteById(MetadataDataDTO dataDto) throws Exception;

    /**
     * 备注
     * @param batchDataDto
     * @return
     * @throws Exception
     */
    boolean batchDeleteById(MetadataDataDTO batchDataDto) throws Exception;
}
