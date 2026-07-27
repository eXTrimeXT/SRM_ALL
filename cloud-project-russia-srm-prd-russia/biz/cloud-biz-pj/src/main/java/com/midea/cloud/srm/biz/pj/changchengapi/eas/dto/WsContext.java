package com.midea.cloud.srm.biz.pj.changchengapi.eas.dto;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-17
 */
public class WsContext implements java.io.Serializable {
    private int dbType;

    private String dcName;

    private String password;

    private String sessionId;

    private String slnName;

    private String userName;

    public WsContext() {
    }

    public WsContext(
            int dbType,
            String dcName,
            String password,
            String sessionId,
            String slnName,
            String userName) {
        this.dbType = dbType;
        this.dcName = dcName;
        this.password = password;
        this.sessionId = sessionId;
        this.slnName = slnName;
        this.userName = userName;
    }


    /**
     * Gets the dbType value for this WSContext.
     *
     * @return dbType
     */
    public int getDbType() {
        return dbType;
    }


    /**
     * Sets the dbType value for this WSContext.
     *
     * @param dbType
     */
    public void setDbType(int dbType) {
        this.dbType = dbType;
    }


    /**
     * Gets the dcName value for this WSContext.
     *
     * @return dcName
     */
    public String getDcName() {
        return dcName;
    }


    /**
     * Sets the dcName value for this WSContext.
     *
     * @param dcName
     */
    public void setDcName(String dcName) {
        this.dcName = dcName;
    }


    /**
     * Gets the password value for this WSContext.
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }


    /**
     * Sets the password value for this WSContext.
     *
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }


    /**
     * Gets the sessionId value for this WSContext.
     *
     * @return sessionId
     */
    public String getSessionId() {
        return sessionId;
    }


    /**
     * Sets the sessionId value for this WSContext.
     *
     * @param sessionId
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }


    /**
     * Gets the slnName value for this WSContext.
     *
     * @return slnName
     */
    public String getSlnName() {
        return slnName;
    }


    /**
     * Sets the slnName value for this WSContext.
     *
     * @param slnName
     */
    public void setSlnName(String slnName) {
        this.slnName = slnName;
    }


    /**
     * Gets the userName value for this WSContext.
     *
     * @return userName
     */
    public String getUserName() {
        return userName;
    }


    /**
     * Sets the userName value for this WSContext.
     *
     * @param userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    private Object equalsCalc = null;
    @Override
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof WsContext)) {
            return false;
        }
        WsContext other = (WsContext) obj;
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (equalsCalc != null) {
            return (equalsCalc == obj);
        }
        equalsCalc = obj;
        boolean otherEquals;
        otherEquals = true &&
                this.dbType == other.getDbType() &&
                ((this.dcName==null && other.getDcName()==null) ||
                        (this.dcName!=null &&
                                this.dcName.equals(other.getDcName()))) &&
                ((this.password==null && other.getPassword()==null) ||
                        (this.password!=null &&
                                this.password.equals(other.getPassword()))) &&
                ((this.sessionId==null && other.getSessionId()==null) ||
                        (this.sessionId!=null &&
                                this.sessionId.equals(other.getSessionId()))) &&
                ((this.slnName==null && other.getSlnName()==null) ||
                        (this.slnName!=null &&
                                this.slnName.equals(other.getSlnName()))) &&
                ((this.userName==null && other.getUserName()==null) ||
                        (this.userName!=null &&
                                this.userName.equals(other.getUserName())));
        equalsCalc = null;
        return otherEquals;
    }

    private boolean hashCodeCalc = false;
    @Override
    public synchronized int hashCode() {
        if (hashCodeCalc) {
            return 0;
        }
        hashCodeCalc = true;
        int hashCode = 1;
        hashCode += getDbType();
        if (getDcName() != null) {
            hashCode += getDcName().hashCode();
        }
        if (getPassword() != null) {
            hashCode += getPassword().hashCode();
        }
        if (getSessionId() != null) {
            hashCode += getSessionId().hashCode();
        }
        if (getSlnName() != null) {
            hashCode += getSlnName().hashCode();
        }
        if (getUserName() != null) {
            hashCode += getUserName().hashCode();
        }
        hashCodeCalc = false;
        return hashCode;
    }

    /**
     * Type metadata
     */
    private static org.apache.axis.description.TypeDesc typeDesc =
            new org.apache.axis.description.TypeDesc(WsContext.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:client", "WSContext"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dbType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dbType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dcName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dcName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("password");
        elemField.setXmlName(new javax.xml.namespace.QName("", "password"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sessionId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "sessionId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("slnName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "slnName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "userName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
            String mechType,
            Class javaType,
            javax.xml.namespace.QName xmlType) {
        return
                new  org.apache.axis.encoding.ser.BeanSerializer(
                        javaType, xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
            String mechType,
            Class javaType,
            javax.xml.namespace.QName xmlType) {
        return
                new  org.apache.axis.encoding.ser.BeanDeserializer(
                        javaType, xmlType, typeDesc);
    }

}
