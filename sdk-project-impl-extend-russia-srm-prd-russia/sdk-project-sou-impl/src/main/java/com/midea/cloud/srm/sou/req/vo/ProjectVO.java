package com.midea.cloud.srm.sou.req.vo;

import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.srm.model.constant.SrmConstant;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/7/18
 */
@Data
public class ProjectVO {
    private String tel;
    private String souName;
    private String extProjectNo;

    private BigDecimal score;

    private Date lastUpdateDate;

    private String linkmanName;

    private String phone;

    private Long companyId;

    private String sortNumber;

    public String formateResult() {
        //时间-项目名称-编号-联系人-电话-评价结果
        List<String> eleList = new ArrayList<>();
        if(StringUtils.isNotBlank(sortNumber)){
            eleList.add(sortNumber);
        }
        if(ObjectUtils.allNotNull(lastUpdateDate)) {
            eleList.add(DateUtil.format(lastUpdateDate, DateUtil.DATE_FORMAT_10));
        }
        if(StringUtils.isNotBlank(souName)) {
            eleList.add(souName);
        }
        if(StringUtils.isNotBlank(extProjectNo)) {
            eleList.add(extProjectNo);
        }
        if(StringUtils.isNotBlank(linkmanName)) {
            eleList.add(linkmanName);
        }
        if(StringUtils.isNotBlank(phone)) {
            eleList.add(phone);
        }
        if(ObjectUtils.allNotNull(score)) {
            eleList.add(score.stripTrailingZeros().toPlainString());
        }
        return eleList.stream().collect(Collectors.joining(SrmConstant.UNDER_LINE));
    }

}
