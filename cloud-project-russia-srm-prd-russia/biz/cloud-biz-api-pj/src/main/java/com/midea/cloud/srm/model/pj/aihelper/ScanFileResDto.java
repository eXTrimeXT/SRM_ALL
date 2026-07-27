package com.midea.cloud.srm.model.pj.aihelper;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "智能评标扫描件DTO")
public class ScanFileResDto {
    private Long projectId;
    private List<Company> companyList;
    @Data
    public static class Company{
        private Long companyId;
        private String companyName;
        private List<File> fileList;
    }
    @Data
    public static class File{
        private String fileName;
        private Long fileId;
    }
}
