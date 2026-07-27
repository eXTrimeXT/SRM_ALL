package com.midea.cloud.srm.supcooperate.ext.order;

import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.srm.supcooperate.meiql.base.PurchaseRepository;
import org.apache.commons.collections4.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zenghx2
 */
public class PurchaseUtils {

    public static QlResult exeParentMethod(String methodName, QlQueryAction action, PurchaseRepository repository) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = repository.getClass().getDeclaredMethod(methodName, QlQueryAction.class);
        method.setAccessible(true);
        return (QlResult) method.invoke(repository, action);
    }

    /**
     * 是否删除标识
     */
    public static boolean isDeleteFlag(Record record) {
        return record.get("$delete") != null;
    }

    /**
     * 非删除标识
     */
    public static boolean notDeleteFlag(Record record) {
        return !isDeleteFlag(record);
    }

    /**
     * 过滤非删除标识
     */
    public static List<Record> trimDeleteFlagRecord(List<Record> source) {
        if (CollectionUtils.isEmpty(source)) {
            return new ArrayList<>();
        }
        return source.stream().filter(e -> notDeleteFlag(e)).collect(Collectors.toList());
    }

    public static Long increaseVersion(Long version) {
        return version == null ? 1L : version + 1;
    }
}
