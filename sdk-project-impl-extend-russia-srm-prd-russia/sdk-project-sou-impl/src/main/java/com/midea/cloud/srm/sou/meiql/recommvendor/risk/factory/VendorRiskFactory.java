package com.midea.cloud.srm.sou.meiql.recommvendor.risk.factory;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.service.RiskService;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.vendor.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Description: 供应商风险策略接口工厂类
 *
 * @author srm
 * @date 2024-05-20
 */
public class VendorRiskFactory extends AbstractRiskFactory{

    /**
     * 供应商风险策略接口Map：key-value, key为简单类名，value为策略接口实现类
     */
    private static Map<String, RiskService> regServices = new HashMap<>(15);

    /**
     * 供应商风险工厂类静态变量定义，实例
     */
    private static VendorRiskFactory vendorRiskFactory;

    /**
     * 静态加载区域，将策略接口实现类注册到变量regServices中
     */
    static {
        regServices.put(VendorRiskPreTreatmentContact.class.getSimpleName(), new VendorRiskPreTreatmentContact());
        regServices.put(VendorRiskPreTreatmentCrawler.class.getSimpleName(), new VendorRiskPreTreatmentCrawler());
        regServices.put(VendorRiskPreTreatmentRecomm.class.getSimpleName(), new VendorRiskPreTreatmentRecomm());
        regServices.put(VendorRiskPreTreatmentRelation.class.getSimpleName(), new VendorRiskPreTreatmentRelation());
        regServices.put(VendorRiskPreTreatmentExceptionVendor.class.getSimpleName(), new VendorRiskPreTreatmentExceptionVendor());
        regServices.put(VendorRiskPreTreatmentMonitor.class.getSimpleName(), new VendorRiskPreTreatmentMonitor());
        regServices.put(VendorRiskPreTreatmentSimpleRelation.class.getSimpleName(), new VendorRiskPreTreatmentSimpleRelation());
        regServices.put(VendorRiskPreTreatmentRecommHistoryReg.class.getSimpleName(), new VendorRiskPreTreatmentRecommHistoryReg());
        regServices.put(VendorRiskDataCategoryRestriction.class.getSimpleName(),new VendorRiskDataCategoryRestriction());

    }

    /**
     * 单例模式，私有化构造
     */
    private VendorRiskFactory() {

    }

    /**
     * 获取工厂类实例
     * @return
     */
    public static VendorRiskFactory getInstance() {
        //判断是否实例化
        if(Objects.isNull(vendorRiskFactory)) {
            //未实例化时创建一个实例
            vendorRiskFactory = new VendorRiskFactory();
        }
        //返回工厂类实例
        return vendorRiskFactory;
    }

    /**
     * 返回供应商风险生成底表数据策略接口实现类
     * @return
     */
    @Override
    public RiskService riskDataGenerator() {
        return new VendorRiskDataGenerator();
    }

    /**
     * 根据简单类名获取注册到变量regServices中的供应商风险策略接口实现类
     * @param classSimpleName
     * @return
     */
    @Override
    public RiskService riskPreTreatment(String classSimpleName) {
        //判断类名是否包含在注册的服务类里
        if(!regServices.containsKey(classSimpleName)) {
            //非法服务，未注册到工厂类中定义的供应商风险策略实现类接口中，抛出异常
            throw new BaseException("供应商风险服务未注册");
        }
        //返回简单类名对应的接口实现类
        return regServices.get(classSimpleName);
    }

    /**
     * 返回供应商风险整合异常数据的策略接口
     * @return
     */
    @Override
    public RiskService riskDataTreating() {
        return new VendorRiskDataTreating();
    }
}
