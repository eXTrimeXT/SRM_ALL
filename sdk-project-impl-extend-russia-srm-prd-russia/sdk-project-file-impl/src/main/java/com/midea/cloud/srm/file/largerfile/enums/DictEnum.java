package com.midea.cloud.srm.file.largerfile.enums;
/**
 * @author fubiao
 */
public enum DictEnum {
    /**
     * 备注
     */
    FILE_SIZE("FILE_SIZE", "文件大小限制"),
    LARGE_FILE_MIN_SIZE("LARGE_FILE_MIN_SIZE", "大文件下限");
    private String code;
    private String name;

    DictEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
