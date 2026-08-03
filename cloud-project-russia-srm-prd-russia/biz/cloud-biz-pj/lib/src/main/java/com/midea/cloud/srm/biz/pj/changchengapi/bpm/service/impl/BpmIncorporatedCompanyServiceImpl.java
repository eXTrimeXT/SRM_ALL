package com.midea.cloud.srm.biz.pj.changchengapi.bpm.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gwm.open.sdk.OpenClient;
import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.biz.pj.changchengapi.bpm.mapper.BpmIncorporatedCompanyMapper;
import com.midea.cloud.srm.biz.pj.changchengapi.bpm.service.IBpmIncorporatedCompanyService;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.BpmIncorporatedCompany;
import com.midea.cloud.srm.model.pj.changchengapi.dto.BpmIncorporatedCompanyParam;
import com.midea.cloud.srm.model.pj.changchengapi.dto.BpmIncorporatedCompanyResultDto;
import com.midea.cloud.srm.model.pj.changchengapi.yangguan.ResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author huangbf3
 * 法务-法人公司主数据实现类
 */
@Slf4j
@Service
public class BpmIncorporatedCompanyServiceImpl extends BaseServiceImpl<BpmIncorporatedCompanyMapper, BpmIncorporatedCompany> implements IBpmIncorporatedCompanyService {

    @Value("${gwm.url.incorporated-company-info-url}")
    private String incorporatedCompanyUrl;
    @Value("${gwm.appkey}")
    private String appKey;
    @Value("${gwm.secret}")
    private String secret;
    @Override
    public void pullData(BpmIncorporatedCompanyParam param) {
        log.info("param:"+ JSONObject.toJSONString(param));
        String url = incorporatedCompanyUrl;

        OpenClient openClient = new OpenClient(appKey,secret);



        String result = openClient.sendHttpPost(url,JSONObject.toJSONString(param),"application/json");

        log.info("请求url："+url);
        log.info("返回结果："+result);

        ResultDTO<JSONObject> resultDto = JSONObject.parseObject(result,ResultDTO.class);
        log.info("resultDTO:"+ JSONObject.toJSONString(resultDto));
        Integer successCode = 200;
        if(resultDto.getCode().equals(successCode)){
            JSONObject jsonObject = resultDto.getResult();
            BpmIncorporatedCompanyResultDto<BpmIncorporatedCompany> bpmIncorporatedCompanyResultDto = JSONObject.parseObject(jsonObject.toJSONString(),BpmIncorporatedCompanyResultDto.class);
            saveOrUpdateFromApi(bpmIncorporatedCompanyResultDto);
            //存在其他页数据继续拉取
            if(bpmIncorporatedCompanyResultDto.getTotal()>param.getSize()*param.getPage()){
                param.setPage(param.getPage()+1);
                pullData(param);
            }
        }
    }

    @Override
    public void saveOrUpdateFromApi(BpmIncorporatedCompanyResultDto resultDto) {
        if(resultDto.getRows()==null||resultDto.getRows().size()==0){
            return;
        }
        List<BpmIncorporatedCompany> rows = JSONArray.parseArray(JSONArray.toJSONString(resultDto.getRows()), BpmIncorporatedCompany.class);


        List<String> creditCodes = rows.stream().map(BpmIncorporatedCompany::getCreditCode).collect(Collectors.toList());

        List<BpmIncorporatedCompany> dbBpmIncorporatedCompanys = this.lambdaQuery().in(BpmIncorporatedCompany::getCreditCode,creditCodes).list();
        log.info("dbBpmIncorporatedCompanys:"+ JSONObject.toJSONString(dbBpmIncorporatedCompanys));

        Map<String,BpmIncorporatedCompany> creditCodeMap = dbBpmIncorporatedCompanys.stream().collect(Collectors.toMap(BpmIncorporatedCompany::getCreditCode, t->t));

        List<BpmIncorporatedCompany> addBpmIncorporatedCompanys = new ArrayList<>();
        List<BpmIncorporatedCompany> updateBpmIncorporatedCompanys = new ArrayList<>();

        for(BpmIncorporatedCompany bpmIncorporatedCompany:rows){
            if(creditCodeMap.containsKey(bpmIncorporatedCompany.getCreditCode())){
                bpmIncorporatedCompany.setBpmIncorporatedCompanyId(creditCodeMap.get(bpmIncorporatedCompany.getCreditCode()).getBpmIncorporatedCompanyId());
                updateBpmIncorporatedCompanys.add(bpmIncorporatedCompany);
            }else{
                bpmIncorporatedCompany.setBpmIncorporatedCompanyId(IdGenrator.generate());
                addBpmIncorporatedCompanys.add(bpmIncorporatedCompany);
            }
        }
        log.info("updateBpmIncorporatedCompanys:"+ JSONObject.toJSONString(updateBpmIncorporatedCompanys));
        log.info("addBpmIncorporatedCompanys:"+ JSONObject.toJSONString(addBpmIncorporatedCompanys));

        this.saveBatch(addBpmIncorporatedCompanys);
        this.updateBatchById(updateBpmIncorporatedCompanys);
    }
}
