package com.midea.cloud.srm.sou.bid.enums;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public enum ReviewFileTypeEnum {
    /** pdf*/
    PDF,
    /** docx*/
    DOCX,
    /** doc*/
    DOC;


    public static boolean isValidReviewFileType(String fileName) {
        return Arrays.stream(ReviewFileTypeEnum.values())
                .anyMatch(e ->  StringUtils.equalsIgnoreCase(e.name(), FilenameUtils.getExtension(fileName)));
    }
}
