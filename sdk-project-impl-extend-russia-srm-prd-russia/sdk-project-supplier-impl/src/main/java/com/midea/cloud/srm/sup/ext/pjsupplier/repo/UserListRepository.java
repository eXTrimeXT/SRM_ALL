package com.midea.cloud.srm.sup.ext.pjsupplier.repo;

import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.ql.QlQueryCondition;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.api.spec.schema.QlRelation;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.meiql.core.util.BeanUtil;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * <pre>
 *
 * </pre>
 *
 * @author kuangzm
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/1/16 16:48:28
 *  修改内容:
 * </pre>
 */
@Component
public class UserListRepository extends CrudRepository {

    @Autowired
    private QlService qlService;

    @Autowired
    private RbacClient rbacClient;

    @Autowired
    private BaseClient baseClient;

    public static String GREEN_CHANNEL_USER_TYPE = "GreenChannelUser";


    @Override
    public QlResult execute(QlQueryAction queryAction) throws Exception {
        //不用删除这个重写，SrmRepositoryManager靠这个判断！！！
        return super.execute(queryAction);
    }


    /**
     * 查询
     *
     * @param relationInfo
     * @param fks
     * @param fetchFields
     * @param meta
     * @return
     */
    @Override
    public Map<Object, List<Map>> batchFindByFk(QlRelation relationInfo, Collection fks, Collection fetchFields, QlQueryCondition meta) {
        Map<Object, List<Map>> result = new HashMap<>(fks.size());
        fks.forEach(t -> {
            List<Map> tempList = new ArrayList<>();
            Long companyId = (Long) t;
            List<User> users = rbacClient.queryByCompanyIdList(Arrays.asList(companyId));
            for (User user : users) {
                Map map = BeanUtil.convertValue(user, Map.class);
                tempList.add(map);
            }
            result.put(companyId, tempList);
        });
        return result;
    }

}
