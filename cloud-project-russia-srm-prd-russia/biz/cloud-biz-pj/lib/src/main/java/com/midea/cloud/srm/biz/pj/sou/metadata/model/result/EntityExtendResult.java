package com.midea.cloud.srm.biz.pj.sou.metadata.model.result;

import lombok.Getter;
import lombok.Setter;

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
 *  修改日期: 2022/9/9 16:15
 *  修改内容:
 * </pre>
 */
@Setter
@Getter
public class EntityExtendResult<T> {
    /**
     * 是否处理原逻辑
     */
    private Boolean processOri = true;

    /**
     * 已处理结果数
     */
    private Long processedCount;

    private T data;
}
