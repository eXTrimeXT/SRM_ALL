package com.midea.cloud.srm.sou.expert.excelhandler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.srm.feign.client.PjProjectExtClient;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertApply;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertEducation;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.sou.pj.SccPjHrUserInfo;
import com.midea.cloud.srm.sies.pojo.SiesData;
import com.midea.cloud.srm.sies.pojo.SiesImportParam;
import com.midea.cloud.srm.sies.pojo.SiesImportResult;
import com.midea.cloud.srm.sies.pojo.SiesMediator;
import com.midea.cloud.srm.sies.validator.AbstractImportValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 专家库hr导入校验
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/11/17
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtSouExpertTempQueryHrValidator extends AbstractImportValidator {

    @Autowired
    private QlService qlService;
    @Autowired
    private PjProjectExtClient pjProjectExtClient;

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy/MM/dd");

    @Override
    public SiesImportResult doValidate(String iesTaskId, SiesImportParam param, SiesMediator curMediator, int sheetNo, String sheetName, Integer batchNo, List<SiesData> dataList) {
        SiesImportResult importResult = new SiesImportResult();
        Set<String> codes = new HashSet<>(dataList.size());
        dataList.forEach(row -> {
            String applyByCode = StringUtils.trimToNull(row.getString(SouObjectXUtil.getFieldByLambda(ExtSouExpertApply::getApplyByCode)));
            if (applyByCode != null) {
                codes.add(applyByCode);
            }
        });
        if (codes.isEmpty()) { return importResult; }
        // 查询hr数据
        Map<String/* 工号 */, SccPjHrUserInfo> hrUserInfoMap = pjProjectExtClient.listHrUserInfos(codes);

        for (SiesData siesData : dataList) {
            importResult.addSuccessRow(this.queryHrData(siesData, hrUserInfoMap));
        }

        return importResult;
    }

    private SiesImportResult.SuccessRow queryHrData(SiesData row, Map<String/* 工号 */, SccPjHrUserInfo> hrUserInfoMap) {
        SiesImportResult.SuccessRow sr = new SiesImportResult.SuccessRow(row, false);
        // 工号
        String applyByCode = StringUtils.trimToNull(row.getString(SouObjectXUtil.getFieldByLambda(ExtSouExpertApply::getApplyByCode)));
        if (applyByCode == null) { return sr; }

        // 根据工号查询hr接口
        SccPjHrUserInfo hrUserInfo = hrUserInfoMap.get(applyByCode);
        if (hrUserInfo == null) { return sr; }
        // 1: 最高学历
        String diploma = ""; {
            switch (hrUserInfo.getDiploma()) {
                case "1":
                    diploma = "博士";
                    break;
                case "2":
                    diploma = "硕士";
                    break;
                case "3":
                    diploma = "本科";
                    break;
                case "4":
                    diploma = "专科";
                    break;
                case "5":
                    diploma = "高中";
                    break;
                case "6":
                    diploma = "中专";
                    break;
                case "7":
                    diploma = "初中";
                    break;
                case "8":
                    diploma ="初中以下";
                    break;
                default:
                    break;
            }
        }
        row.put(ExtSouExpertApply::getHighestDegree, diploma);
        // 2: 毕业时间
        row.put(ExtSouExpertEducation::getStudyDateTo, hrUserInfo.getGraduateTime() != null ? SDF.format(hrUserInfo.getGraduateTime()) : null);
        // 3: 性别
        String sex = ""; {
            switch (hrUserInfo.getSex()) {
                case "1":
                    sex = "男";
                    break;
                case "2":
                    sex = "女";
                    break;
                default:
                    break;
            }
        }
        row.put(ExtSouExpertApply::getSex, sex);
        // 4: 职务
        row.put(ExtSouExpertApply::getJob, hrUserInfo.getDutyName());
        // 5: 序列等级
        row.put(ExtSouExpertApply::getJobRank, hrUserInfo.getRankName());
        // 6: 在职状态
        String state = ""; {
            switch (hrUserInfo.getState()) {
                case "1":
                    state = "在职";
                    break;
                case "2":
                    state = "离职";
                    break;
                default:
                    break;
            }
        }
        row.put(ExtSouExpertApply::getJobStatus, state);
        // 7: 手机号码(略)
        // 8: 入厂时间
        row.put(ExtSouExpertApply::getHireDate, hrUserInfo.getAdmissionDate() != null ? SDF.format(hrUserInfo.getAdmissionDate()) : null);
        // 9: 毕业学校
        row.put(ExtSouExpertEducation::getStudyCollege, hrUserInfo.getGraduateSchool());
        // 10: 所学专业
        row.put(ExtSouExpertEducation::getMajor, hrUserInfo.getProfessional());
        // 11: 申报等级(略)

        sr = new SiesImportResult.SuccessRow(row, false);
        return sr;
    }

}
