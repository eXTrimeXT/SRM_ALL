package com.midea.cloud.srm.model.pj.aihelper;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * @author GW00302625
 */
@Data
@ApiModel(description = "智能评标推送DTO")
public class BidReviewDto {
    private String embedClient = "SRMclient";
    private Long projectId;
    private List<Company> companyList;
    private List<ReviewItem> reviewItemList;

    @Data
    public static class Company{
        private String companyName;
        private Long companyId;
        private List<File> fileList;

    }
    @Data
    public static class File{
        private String fileName;
        private Long fileId;
        private String fileType;
    }
    @Data
    public static class ReviewItem{
        private Long itemId;
        private String itemName;
        private String itemDescript;
    }

}
