package com.midea.cloud.srm.model.pj.sou.car.meet.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * 功能名称
 * </pre>
 *
 * @author ex_nongtb@partner.midea.com
 * @version 1.00.00
 *
 * <pre>
 * 修改记录
 * 修改后版本:
 * 修改人: ex_nongtb
 * 修改日期: 2022/6/23 15:45
 * 修改内容:
 * </pre>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeetBusiness {


    /**
     * 业务客户端
     */
    private String businessClient;

    /**
     * 业务方法
     */
    private String businessClass;

}
