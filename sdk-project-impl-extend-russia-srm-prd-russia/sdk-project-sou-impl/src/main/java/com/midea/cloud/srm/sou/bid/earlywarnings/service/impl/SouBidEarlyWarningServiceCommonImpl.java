package com.midea.cloud.srm.sou.bid.earlywarnings.service.impl;

import com.midea.cloud.common.constant.DingTalkConstant;
import com.midea.cloud.common.dingtalks.DingTalkClient;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.client.PjProjectExtClient;
import com.midea.cloud.srm.sou.bid.earlywarnings.service.SouBidEarlyWarningService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
@Service
@Slf4j
public class SouBidEarlyWarningServiceCommonImpl implements SouBidEarlyWarningService {

    @Autowired
    private BaseClient baseClient;

    @Autowired
    private PjProjectExtClient pjProjectExtClient;

    protected final static String WARNING_TIME_LIST = "warningTimeList";

    protected final static Long DIFF_THREE_DAYS = 3L;

    protected final static String PROCESS_DATA_SUBMIT = "递交申请资料";

    protected final static String PROCESS_DATA_PUBLISH = "发标";

    protected final static String PROCESS_DATA_TECH_OPEN = "技术标收标";

    protected final static String PROCESS_DATA_PRICE_OPEN = "商务标收标";

    protected final static String PROCESS_DATA_EVALUATION = "评标";

    protected final static String PROCESS_DATA_SUMREPORT = "上报";

    protected final static String PROCESS_DATA_PICKETAGE = "定标";

    protected final static String PROCESS_DATA_WIN_LOSS = "中落标通知";

    @Override
    public String doWarning() {
        return null;
    }

    @Override
    public Boolean dingTalk(List<String> userNameList, Map<String, String> var) {
        return DingTalkClient.newInstance(baseClient, pjProjectExtClient).sendDingTalk(userNameList, DingTalkConstant.SOU_BID_PROCESS_REMIND, var);
    }

    /**
     * 获取查询条件
     * @return
     */
    protected Map<String, Object> getQueryParams() {
        Map<String, Object> params = new HashMap<>(15);
        List<String> warningTimeList = new ArrayList<>(2);
        Calendar calendar = Calendar.getInstance();
        //当前时间减去1天
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        warningTimeList.add(DateUtil.format(calendar.getTime(), DateUtil.DATE_FORMAT_10));
        //当前时间减去3天
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -3);
        warningTimeList.add(DateUtil.format(calendar.getTime(), DateUtil.DATE_FORMAT_10));
        params.put(WARNING_TIME_LIST, warningTimeList);
        return params;
    }

    /**
     * 三天前
     * @return
     */
    protected String threeDayBefore() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -3);
        return DateUtil.format(calendar.getTime(), DateUtil.DATE_FORMAT_10);
    }

    @SneakyThrows(value = {Exception.class})
    protected Long diffDays(Object dateObject) {
        if(Objects.isNull(dateObject)) {
            return 0L;
        }

        Date date = null;
        if(dateObject instanceof Date) {
            date = (Date) dateObject;
        } else if(dateObject instanceof LocalDate) {
            date = DateUtil.localDateToDate((LocalDate) dateObject);
        } else {
            date = DateUtil.parseDate((String) dateObject);
        }

        Long currentMillis = Calendar.getInstance().getTimeInMillis();
        Long dateMillis = date.getTime();
        Long diff = currentMillis - dateMillis;
        return diff/(1000*60*60*24);
    }

    protected Long diffDays(Date date1, Date date2) {
        if(ObjectUtils.anyNull(date1, date2)) {
            return 0L;
        }
        return Math.abs((dateWithoutTimes(date1).getTime() - dateWithoutTimes(date2).getTime())/(1000*60*60*24));
    }

    /**
     * 格式化时间
     * @param date
     * @return
     */
    @SneakyThrows(value = {Exception.class})
    private Date dateWithoutTimes(Date date) {
        return DateUtil.parseDate(DateUtil.format(date, DateUtil.DATE_FORMAT_10), DateUtil.DATE_FORMAT_10);
    }
}
