package com.midea.cloud.srm.base.dict.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.base.dict.service.IDictItemService;
import com.midea.cloud.srm.base.dict.service.IDictService;
import com.midea.cloud.srm.model.base.dict.entity.Dict;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 备注
 * @author huangbf3
 */
@RestController
@RequestMapping("/pjDictItem")
@Slf4j
public class PjDictItemController {
    @Autowired
    private IDictService iDictService ;
    @Autowired
    private IDictItemService iDictItemService ;

    /**
     * 测试接口
     * @param dictCode
     * @param dictItemCode
     */
    @GetMapping(value = "/getDictItem")
    public DictItem getDictItem(@RequestParam("dictCode") String dictCode, @RequestParam("dictItemCode") String dictItemCode) {
        String language = LocaleHandler.getLocaleKey();
        Dict dict = iDictService.getOne(Wrappers.lambdaQuery(Dict.class).eq(Dict::getDictCode,dictCode).eq(Dict::getLanguage,language));
        if(dict==null){
            return null;
        }
        return iDictItemService.getOne(Wrappers.lambdaQuery(DictItem.class)
                .eq(DictItem::getDictId,dict.getDictId())
                .eq(DictItem::getDictItemCode,dictItemCode)
        );
    }
}
