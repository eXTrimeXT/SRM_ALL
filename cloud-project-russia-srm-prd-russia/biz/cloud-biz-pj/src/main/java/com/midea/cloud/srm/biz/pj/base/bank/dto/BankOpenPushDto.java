package com.midea.cloud.srm.biz.pj.base.bank.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;

/**
 * @author G
 */
@ApiModel(description = "对接开放平台-查询支行信息入参")
public class BankOpenPushDto {
    //{
    //    "Cmscloud": {
    //        "Head": {
    //            "TenantId": "商户号",
    //            "Timestamp": "时间戳",
    //            "SystemNo": "接入系统编号",
    //            "SystemName": "接入系统名称",
    //            "RequestNo": "请求号",
    //            "MesgNo": "报文标识号"
    //        },
    //        "Body": {
    //            "Data": {
    //                "ParamSet": {
    //                    "BankName": "银行名称",
    //                    "DirectCode": "直联编号（大行编码）",
    //                    "BranchCode": "银行机构号",
    //                    "UniteCode": "行内行号",
    //                    "BicCode": "BIC码",
    //                    "CnapsCode": "联行号",
    //                    "StartTime": "查询开始时间区间",
    //                    "EndTime": "查询结束时间区间",
    //                    "NextTag": "查询下页标识"
    //                }
    //            },
    //            "Sign": "数据签名"
    //        }
    //    }
    //}

    private Cmscloud Cmscloud;

    public Cmscloud getCmscloud() {
        return Cmscloud;
    }

    public void setCmscloud(Cmscloud cmscloud) {
        this.Cmscloud = cmscloud;
    }

    public static class Cmscloud {

        private Head Head;
        private Body Body;

        // Getter and Setter for Head
        public Head getHead() {
            return Head;
        }

        public void setHead(Head head) {
            this.Head = head;
        }

        // Getter and Setter for Body
        public Body getBody() {
            return Body;
        }

        public void setBody(Body body) {
            this.Body = body;
        }

        public static class Head  {
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
                this.TenantId = tenantId;
            }

            public String getTimestamp() {
                return Timestamp;
            }

            public void setTimestamp(String timestamp) {
                this.Timestamp = timestamp;
            }

            public String getSystemNo() {
                return SystemNo;
            }

            public void setSystemNo(String systemNo) {
                this.SystemNo = systemNo;
            }

            public String getSystemName() {
                return SystemName;
            }

            public void setSystemName(String systemName) {
                this.SystemName = systemName;
            }

            public String getRequestNo() {
                return RequestNo;
            }

            public void setRequestNo(String requestNo) {
                this.RequestNo = requestNo;
            }

            public String getMesgNo() {
                return MesgNo;
            }

            public void setMesgNo(String mesgNo) {
                this.MesgNo = mesgNo;
            }
        }

        public static class Body  {
            private Data Data;
            private String Sign;

            // Getter and Setter for Data
            public Data getData() {
                return Data;
            }

            public void setData(Data data) {
                this.Data = data;
            }

            // Getter and Setter for Sign
            public String getSign() {
                return Sign;
            }

            public void setSign(String sign) {
                this.Sign = sign;
            }

            public static class Data  {
                private ParamSet ParamSet;

                // Getter and Setter for ParamSet
                public ParamSet getParamSet() {
                    return ParamSet;
                }

                public void setParamSet(ParamSet paramSet) {
                    this.ParamSet = paramSet;
                }

                public static class ParamSet  {
                    private String BankName;
                    private String DirectCode;
                    private String BranchCode;
                    private String UniteCode;
                    private String BicCode;
                    private String CnapsCode;
                    private String StartTime;
                    private String EndTime;
                    private String NextTag;

                    // Getters and Setters
                    public String getBankName() {
                        return BankName;
                    }

                    public void setBankName(String bankName) {
                        this.BankName = bankName;
                    }

                    public String getDirectCode() {
                        return DirectCode;
                    }

                    public void setDirectCode(String directCode) {
                        this.DirectCode = directCode;
                    }

                    public String getBranchCode() {
                        return BranchCode;
                    }

                    public void setBranchCode(String branchCode) {
                        this.BranchCode = branchCode;
                    }

                    public String getUniteCode() {
                        return UniteCode;
                    }

                    public void setUniteCode(String uniteCode) {
                        this.UniteCode = uniteCode;
                    }

                    public String getBicCode() {
                        return BicCode;
                    }

                    public void setBicCode(String bicCode) {
                        this.BicCode = bicCode;
                    }

                    public String getCnapsCode() {
                        return CnapsCode;
                    }

                    public void setCnapsCode(String cnapsCode) {
                        this.CnapsCode = cnapsCode;
                    }

                    public String getStartTime() {
                        return StartTime;
                    }

                    public void setStartTime(String startTime) {
                        this.StartTime = startTime;
                    }

                    public String getEndTime() {
                        return EndTime;
                    }

                    public void setEndTime(String endTime) {
                        this.EndTime = endTime;
                    }

                    public String getNextTag() {
                        return NextTag;
                    }

                    public void setNextTag(String nextTag) {
                        this.NextTag = nextTag;
                    }
                }
            }
        }
    }
}

