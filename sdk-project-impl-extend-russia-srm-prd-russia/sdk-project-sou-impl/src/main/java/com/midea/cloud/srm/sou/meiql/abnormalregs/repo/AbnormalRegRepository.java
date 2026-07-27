package com.midea.cloud.srm.sou.meiql.abnormalregs.repo;

import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.sou.abnormalregs.dto.SccNpmSouAbnormalFileDto;
import com.midea.cloud.srm.model.sou.abnormalregs.dto.SccNpmSouAbnormalRegDto;
import com.midea.cloud.srm.model.sou.enums.NpmAbnormalRegStatusEnum;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.sou.common.ExtSouBidComponent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@Slf4j
@Component
public class AbnormalRegRepository extends CrudRepository {

    @Autowired
    private QlService qlService;

    public AbnormalRegRepository() {
        this.register("submit", this::submit, "提交");
    }


    public QlResult submit(QlQueryAction queryAction) {
        initeStatus(queryAction, NpmAbnormalRegStatusEnum.SUBMIT.getCode(), true);
        return this.save(queryAction);
    }

    @Override
    public QlResult save(QlQueryAction queryAction) {

        initeStatus(queryAction, NpmAbnormalRegStatusEnum.DRAFT.getCode(), false);

        QlResult qlResult = super.save(queryAction);

        Collection<Record> records = qlResult.getRefValues(MqlType.SCC_NPM_SOU_ABNORMAL_REG);
        //保存附件信息
        records.forEach(record -> {
            saveFile(record);
        });
        return qlResult;
    }

    private void initeStatus(QlQueryAction queryAction, String regStatus, Boolean ignoreNotNull) {
        List<Map<String, Object>> recordList = (List<Map<String, Object>>) queryAction.getPayload();
        if(CollectionUtils.isNotEmpty(recordList)) {
            recordList.stream().forEach(record -> {
                if(ignoreNotNull) {
                    record.put(ExtSouBidComponent.fieldName(SccNpmSouAbnormalRegDto::getRegStatus), regStatus);
                } else {
                    if(Objects.isNull(record.get(ExtSouBidComponent.fieldName(SccNpmSouAbnormalRegDto::getRegStatus)))) {
                        record.put(ExtSouBidComponent.fieldName(SccNpmSouAbnormalRegDto::getRegStatus), regStatus);
                    }
                }
            });
        }
    }

    @Override
    public QlResult query(QlQueryAction queryAction) {
        QlResult qlResult = super.query(queryAction);
        Collection<Record> records = qlResult.getRefValues(MqlType.SCC_NPM_SOU_ABNORMAL_REG);

        if(CollectionUtils.isEmpty(records)) {
            return qlResult;
        }

        List<Long> regIds = records.stream().map(r -> r.get(SccNpmSouAbnormalRegDto::getRegId)).collect(Collectors.toList());

        List<Record> fileList = qlService.queryByWrapper(QlWrappers.query(MqlType.SCC_NPM_SOU_ABNORMAL_FILE).in(SccNpmSouAbnormalFileDto::getRegId, regIds), Record.class);

        Map<Long, List<Record>> fileGroup = fileList.stream().collect(Collectors.groupingBy(r -> r.get(SccNpmSouAbnormalFileDto::getRegId)));

        records.stream().forEach(record -> {
            Map<String, String> map = toFileMap(fileGroup.get(record.get(SccNpmSouAbnormalRegDto::getRegId)));
            record.put(SccNpmSouAbnormalRegDto::getFileId, map.get("fileId"));
            record.put(SccNpmSouAbnormalRegDto::getFileName, map.get("fileName"));
        });

        return qlResult;
    }

    private Map<String, String> toFileMap(List<Record> records) {
        Map<String, String> map = new HashMap<>(15);
        String fileId = "";
        String fileName = "";
        if(CollectionUtils.isNotEmpty(records)) {
            fileId = records.stream().map(r -> r.get(SccNpmSouAbnormalFileDto::getFileId).toString()).collect(Collectors.joining(SrmConstant.SIG_3));
            fileName = records.stream().map(r -> r.get(SccNpmSouAbnormalFileDto::getFileName)).collect(Collectors.joining(SrmConstant.SIG_3));
        }
        map.put("fileId", fileId);
        map.put("fileName", fileName);

        return map;
    }

    public void saveFile(Record record) {
        String fileId = record.get(SccNpmSouAbnormalRegDto::getFileId);
        String fileName = record.get(SccNpmSouAbnormalRegDto::getFileName);

        List<SccNpmSouAbnormalFileDto> saveList = new ArrayList<>();
        if(StringUtils.isNotBlank(fileId)) {
            String[] fileIdArrary = fileId.split(SrmConstant.SIG_3);
            String[] fileNameArrary = fileName.split(SrmConstant.SIG_3);

            for(int i = 0; i < fileIdArrary.length; i++) {
                SccNpmSouAbnormalFileDto fileDto = new SccNpmSouAbnormalFileDto();
                fileDto.setFileId(Long.valueOf(fileIdArrary[i]));
                fileDto.setFileName(fileNameArrary[i]);
                fileDto.setRegId(record.get(SccNpmSouAbnormalRegDto::getRegId));
                saveList.add(fileDto);
            }
        } else {
            qlService.deleteByWrapper(QlWrappers.update(MqlType.SCC_NPM_SOU_ABNORMAL_FILE).eq(SccNpmSouAbnormalFileDto::getRegId, record.get(SccNpmSouAbnormalRegDto::getRegId)));
            return;
        }

        qlService.deleteByWrapper(QlWrappers.update(MqlType.SCC_NPM_SOU_ABNORMAL_FILE).eq(SccNpmSouAbnormalFileDto::getRegId, record.get(SccNpmSouAbnormalRegDto::getRegId)).notIn(SccNpmSouAbnormalFileDto::getFileId, saveList.stream().map(f -> f.getFileId()).collect(Collectors.toList())));

        List<Record> fileList = qlService.queryByWrapper(QlWrappers.query(MqlType.SCC_NPM_SOU_ABNORMAL_FILE).eq(SccNpmSouAbnormalFileDto::getRegId, record.get(SccNpmSouAbnormalRegDto::getRegId)), Record.class);

        Map<Long, Record> fileMap = fileList.stream().collect(Collectors.toMap(f -> f.get(SccNpmSouAbnormalFileDto::getFileId), Function.identity(), (k1, k2)->k2));

        saveList.stream().forEach(file -> {
            if(fileMap.containsKey(file.getFileId())) {
                file.setAbnormalFileId(fileMap.get(file.getFileId()).get(SccNpmSouAbnormalFileDto::getAbnormalFileId));
            }
        });

        qlService.save(MqlType.SCC_NPM_SOU_ABNORMAL_FILE, saveList);
    }
}
