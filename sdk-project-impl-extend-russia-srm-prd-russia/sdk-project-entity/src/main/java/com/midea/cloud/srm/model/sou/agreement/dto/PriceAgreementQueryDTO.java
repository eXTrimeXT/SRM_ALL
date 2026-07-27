package com.midea.cloud.srm.model.sou.agreement.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author zenghx2
 * 物料+区域价格查询
 */
@Accessors(chain = true)
@Data
public class PriceAgreementQueryDTO implements Serializable {

   private List<Long> orgIds;

   private List<String> supplyAreas;

   private List<Long> materialIds;

}
