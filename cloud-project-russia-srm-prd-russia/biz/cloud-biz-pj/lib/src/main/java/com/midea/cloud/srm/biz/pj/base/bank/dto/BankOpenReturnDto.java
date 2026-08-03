package com.midea.cloud.srm.biz.pj.base.bank.dto;

import io.swagger.annotations.ApiModel;

import java.util.List;

/**
 * @author GW00311146
 *
 */
@ApiModel(description = "对接开放平台-查询支行信息返回信息")
public class BankOpenReturnDto {
//    {
//        "Cmscloud":{
//        "Head":{
//            "TenantId":"商户号",
//                    "Timestamp":"时间戳",
//                    "SystemNo":"接入系统编号",
//                    "SystemName":"接入系统名称",
//                    "RequestNo":"请求号",
//                    "MesgNo":"报文标识号"
//        },
//        "Body":{
//            "Data":{
//                "ResultCode":"结果码",
//                        "ResultMsg":"结果信息",
//                        "NextTag":"页面标识",
//                        "Total":"总数量",
//                        "ResultSet":[
//                {
//                        "Id":"id",
//                        "BankName":"银行名称",
//                        "EnName":"英文名称",
//                        "DirectCode":"直联编号（大行编码）",
//                        "DirectName":"大行名称",
//                        "BranchCode":"银行机构号",
//                        "UniteCode":"行内行号",
//                        "BicCode":"BIC码",
//                        "CnapsCode":"联行号",
//                        "Location1":"银行地址1",
//                        "Location2":"银行地址2",
//                        "SubjectCode":"银行科目编号",
//                        "SubjectName":"银行科目名称",
//                        "CountryName":"国家",
//                        "CountryCode":"国家编码",
//                        "EnLocation1":"英文银行地址1",
//                        "EnLocation2":"英文银行地址2",
//                        "EnLocation3":"英文银行地址3",
//                        "LocationCode1":"地址一编号",
//                        "LocationCode2":"地址二编号"
//                    "Isdel":"删除标记"
//                }
//]
//            },
//            "Sign":"数据签名"
//        }
//    }
//    }

    private Cmscloud Cmscloud;

    public void setCmscloud(BankOpenReturnDto.Cmscloud cmscloud) {
        Cmscloud = cmscloud;
    }

    public BankOpenReturnDto.Cmscloud getCmscloud() {
        return Cmscloud;
    }

    public static class Cmscloud {
        private Head Head;
        private Body Body;

        // Getters and Setters
        public Head getHead() {
            return Head;
        }

        public void setHead(Head head) {
            Head = head;
        }

        public Body getBody() {
            return Body;
        }

        public void setBody(Body body) {
            Body = body;
        }

        public static class Head {
            private String TenantId;
            private String Timestamp;
            private String SystemNo;
            private String SystemName;
            private String RequestNo;
            private String MesgNo;

            // Getters and Setters
            public String getTenantId() {
                return TenantId;
            }

            public void setTenantId(String tenantId) {
                TenantId = tenantId;
            }

            public String getTimestamp() {
                return Timestamp;
            }

            public void setTimestamp(String timestamp) {
                Timestamp = timestamp;
            }

            public String getSystemNo() {
                return SystemNo;
            }

            public void setSystemNo(String systemNo) {
                SystemNo = systemNo;
            }

            public String getSystemName() {
                return SystemName;
            }

            public void setSystemName(String systemName) {
                SystemName = systemName;
            }

            public String getRequestNo() {
                return RequestNo;
            }

            public void setRequestNo(String requestNo) {
                RequestNo = requestNo;
            }

            public String getMesgNo() {
                return MesgNo;
            }

            public void setMesgNo(String mesgNo) {
                MesgNo = mesgNo;
            }
        }

        public static class Body {
            private Data Data;

            // Getters and Setters
            public Data getData() {
                return Data;
            }

            public void setData(Data data) {
                Data = data;
            }

            public static class Data {
                private String ResultCode;
                private String ResultMsg;
                private String NextTag;
                private int Total;
                private List<ResultSet> ResultSet;
                private String Sign;

                // Getters and Setters
                public String getResultCode() {
                    return ResultCode;
                }

                public void setResultCode(String resultCode) {
                    ResultCode = resultCode;
                }

                public String getResultMsg() {
                    return ResultMsg;
                }

                public void setResultMsg(String resultMsg) {
                    ResultMsg = resultMsg;
                }

                public String getNextTag() {
                    return NextTag;
                }

                public void setNextTag(String nextTag) {
                    NextTag = nextTag;
                }

                public int getTotal() {
                    return Total;
                }

                public void setTotal(int total) {
                    Total = total;
                }

                public List<ResultSet> getResultSet() {
                    return ResultSet;
                }

                public void setResultSet(List<ResultSet> resultSet) {
                    ResultSet = resultSet;
                }

                public String getSign() {
                    return Sign;
                }

                public void setSign(String sign) {
                    Sign = sign;
                }

                public static class ResultSet {
                    private String Id;
                    private String BankName;
                    private String EnName;
                    private String DirectCode;
                    private String DirectName;
                    private String BranchCode;
                    private String UniteCode;
                    private String BicCode;
                    private String CnapsCode;
                    private String Location1;
                    private String Location2;
                    private String SubjectCode;
                    private String SubjectName;
                    private String CountryName;
                    private String CountryCode;
                    private String EnLocation1;
                    private String EnLocation2;
                    private String EnLocation3;
                    private String LocationCode1;
                    private String LocationCode2;
                    private String Isdel;

                    // Getters and Setters
                    public String getId() {
                        return Id;
                    }

                    public void setId(String id) {
                        Id = id;
                    }

                    public String getBankName() {
                        return BankName;
                    }

                    public void setBankName(String bankName) {
                        BankName = bankName;
                    }

                    public String getEnName() {
                        return EnName;
                    }

                    public void setEnName(String enName) {
                        EnName = enName;
                    }

                    public String getDirectCode() {
                        return DirectCode;
                    }

                    public void setDirectCode(String directCode) {
                        DirectCode = directCode;
                    }

                    public String getDirectName() {
                        return DirectName;
                    }

                    public void setDirectName(String directName) {
                        DirectName = directName;
                    }

                    public String getBranchCode() {
                        return BranchCode;
                    }

                    public void setBranchCode(String branchCode) {
                        BranchCode = branchCode;
                    }

                    public String getUniteCode() {
                        return UniteCode;
                    }

                    public void setUniteCode(String uniteCode) {
                        UniteCode = uniteCode;
                    }

                    public String getBicCode() {
                        return BicCode;
                    }

                    public void setBicCode(String bicCode) {
                        BicCode = bicCode;
                    }

                    public String getCnapsCode() {
                        return CnapsCode;
                    }

                    public void setCnapsCode(String cnapsCode) {
                        CnapsCode = cnapsCode;
                    }

                    public String getLocation1() {
                        return Location1;
                    }

                    public void setLocation1(String location1) {
                        Location1 = location1;
                    }

                    public String getLocation2() {
                        return Location2;
                    }

                    public void setLocation2(String location2) {
                        Location2 = location2;
                    }

                    public String getSubjectCode() {
                        return SubjectCode;
                    }

                    public void setSubjectCode(String subjectCode) {
                        SubjectCode = subjectCode;
                    }

                    public String getSubjectName() {
                        return SubjectName;
                    }

                    public void setSubjectName(String subjectName) {
                        SubjectName = subjectName;
                    }

                    public String getCountryName() {
                        return CountryName;
                    }

                    public void setCountryName(String countryName) {
                        CountryName = countryName;
                    }

                    public String getCountryCode() {
                        return CountryCode;
                    }

                    public void setCountryCode(String countryCode) {
                        CountryCode = countryCode;
                    }

                    public String getEnLocation1() {
                        return EnLocation1;
                    }

                    public void setEnLocation1(String enLocation1) {
                        EnLocation1 = enLocation1;
                    }

                    public String getEnLocation2() {
                        return EnLocation2;
                    }

                    public void setEnLocation2(String enLocation2) {
                        EnLocation2 = enLocation2;
                    }

                    public String getEnLocation3() {
                        return EnLocation3;
                    }

                    public void setEnLocation3(String enLocation3) {
                        EnLocation3 = enLocation3;
                    }

                    public String getLocationCode1() {
                        return LocationCode1;
                    }

                    public void setLocationCode1(String locationCode1) {
                        LocationCode1 = locationCode1;
                    }

                    public String getLocationCode2() {
                        return LocationCode2;
                    }

                    public void setLocationCode2(String locationCode2) {
                        LocationCode2 = locationCode2;
                    }

                    public String getIsdel() {
                        return Isdel;
                    }

                    public void setIsdel(String isdel) {
                        Isdel = isdel;
                    }
                }
            }
        }
    }

}
