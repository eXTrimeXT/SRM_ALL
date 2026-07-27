package com.midea.cloud.srm.supcooperate.ext.requirement.pr.controller;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.ExtFixPriceTimelinessRatioDetail;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.ExtFixPriceTimelinessRatioHead;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.service.ExtFixPriceTimelinessRatioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author srm
 * @Description: 定价及时率报表
 * @date 2024/7/5
 */
@RestController
@RequestMapping("FixPriceTimelinessRatio")
public class ExtFixPriceTimelinessRatioController {
    @Autowired
    ExtFixPriceTimelinessRatioService extFixPriceTimelinessRatioService;
    /**
     * params是因为detail没有传入查询参数  直接勇哥全局变量记录head的查询参数复用好了
     */
    Map<String,Object>params=new HashMap<>();
    /**
     * 定价及时率的记录,第一次查询头表的时候进行初始化
     */
    HashMap<String,String>ratioMap=new HashMap<>();
    /**
     * 是否第一次进入头表
     */
    Boolean flag=false;
    /**
     * 头表
     */
    @PostMapping("head")
    public PageInfo<ExtFixPriceTimelinessRatioHead> list(@RequestBody Map<String,Object> query){
        if(!flag){
            flag=true;
            getRatio();
        }
        List<ExtFixPriceTimelinessRatioHead> list= extFixPriceTimelinessRatioService.get(query);
        params=query;
        for(ExtFixPriceTimelinessRatioHead extFixPriceTimelinessRatioHead:list){
            extFixPriceTimelinessRatioHead.setRatio(ratioMap.get(extFixPriceTimelinessRatioHead.getCreatedBy()));
        }
        return new PageInfo<>(list);
    }
    /**
     * 明细表
     */
    @PostMapping("/detail")
    public PageInfo<ExtFixPriceTimelinessRatioDetail> listById(@RequestBody Map<String,Object>query){
        params.put("jobNumber",query.get("jobNumber"));
        if(query.containsKey("pageNum")){
            params.put("pageNum",query.get("pageNum"));
        }
        if(query.containsKey("pageSize")){
            params.put("pageSize",query.get("pageSize"));
        }
        if(query.containsKey("__page")){
            params.put("__page",query.get("__page"));
        }
        if(query.containsKey("__pagesize")){
            params.put("__pagesize",query.get("__pagesize"));
        }
        List<ExtFixPriceTimelinessRatioDetail> list= extFixPriceTimelinessRatioService.list(params);
        list=getOnTime(list);
        return new PageInfo<>(list);
    }

    /**
     * 计算查询出来的数据是否按时完成
     * onTime字段:是否按时完成:定价单提交(creationDate)-需求审批日期(extApproveTime)<10
     */
    public List<ExtFixPriceTimelinessRatioDetail>getOnTime(List<ExtFixPriceTimelinessRatioDetail>list){
        for(ExtFixPriceTimelinessRatioDetail extFixPriceTimelinessRatioDetail:list){
            long milliseconds1 = extFixPriceTimelinessRatioDetail.getCreationDate().getTime();
            long milliseconds2 = extFixPriceTimelinessRatioDetail.getExtApproveTime().getTime();
            long diff = milliseconds1 - milliseconds2;
            long daysBetween = diff / (24 * 60 * 60 * 1000);
            if(daysBetween<=10){
                extFixPriceTimelinessRatioDetail.setOnTime("是");
            }
            else{
                extFixPriceTimelinessRatioDetail.setOnTime("否");
            }
        }
        return list;
    }

    /**
     * 第一次查询头表的时候，计算所有工号的对应及时率
     */
    public void getRatio(){
        /**
         * 查出所有的头表记录
         */
        HashMap<String,Object>temp=new HashMap();
        temp.put("pageSize",100000000);
        temp.put("__pagesize",100000000);
        List<ExtFixPriceTimelinessRatioHead> list= extFixPriceTimelinessRatioService.get(temp);
        for(ExtFixPriceTimelinessRatioHead extFixPriceTimelinessRatioHead:list){
            Map<String,Object>params=new HashMap<>(15);
            params.put("pageSize",100000000);
            params.put("__pagesize",100000000);
            params.put("jobNumber",extFixPriceTimelinessRatioHead.getCreatedBy());
            List<ExtFixPriceTimelinessRatioDetail>detailList= extFixPriceTimelinessRatioService.list(params);
            detailList=getOnTime(detailList);

            /**
             * sum:某个工号的全部记录
             * onTimeSum:某个工号的准时记录
             * 下面这一大坨都是算及时率的
             */
            HashMap<String,Integer>sum=new HashMap<>(15);
            HashMap<String,Integer>onTimeSum=new HashMap<>(15);
            String yesValue = "是";
            for(ExtFixPriceTimelinessRatioDetail extFixPriceTimelinessRatioDetail:detailList){
                if(!sum.containsKey(extFixPriceTimelinessRatioDetail.getCreatedBy())){
                    sum.put(extFixPriceTimelinessRatioDetail.getCreatedBy(),0);
                    onTimeSum.put(extFixPriceTimelinessRatioDetail.getCreatedBy(),0);
                }
                Integer oldSum=sum.get(extFixPriceTimelinessRatioDetail.getCreatedBy());
                Integer oldOnTimeSum=onTimeSum.get(extFixPriceTimelinessRatioDetail.getCreatedBy());
                sum.put(extFixPriceTimelinessRatioDetail.getCreatedBy(),oldSum+1);
                if(yesValue.equals(extFixPriceTimelinessRatioDetail.getOnTime())){
                    onTimeSum.put(extFixPriceTimelinessRatioDetail.getCreatedBy(),oldOnTimeSum+1);
                }
            }
            Double total=Double.valueOf(sum.get(extFixPriceTimelinessRatioHead.getCreatedBy()));
            Double onTimeTotal=Double.valueOf(onTimeSum.get(extFixPriceTimelinessRatioHead.getCreatedBy()));
            Double ratio=onTimeTotal/total;
            //获取格式化对象
            NumberFormat nt = NumberFormat.getPercentInstance();
            //设置百分数精确度2即保留两位小数
            nt.setMinimumFractionDigits(2);
            String res=nt.format(ratio);
            res=res.substring(0,res.length()-1);
            extFixPriceTimelinessRatioHead.setRatio(res);
            ratioMap.put(extFixPriceTimelinessRatioHead.getCreatedBy(),res);
        }
    }
}
