package com.midea.cloud.srm.model.pj.aihelper;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * @author GW00302625
 */
@Data
@ApiModel(description = "智能评标结果DTO")
public class BidReviewResDto {
    private Long projectId;
    private List<ReviewItem> reviewItemList;

    @Data
    public static class ReviewItem{
        private Long itemId;
        private String itemName;
        private List<Company> companyList;

    }
    @Data
    public static class Company{
        private Long companyId;
        private String companyName;
        private List<AnswerAndQuotation> answerAndQuotationList;
    }
    @Data
    public static class AnswerAndQuotation{
        private String answer;
        private List<Quotation> quotationList;
    }
    @Data
    public static class Quotation{
        private Long fileId;
        private String fileName;
        private List<String> location;
    }

}
