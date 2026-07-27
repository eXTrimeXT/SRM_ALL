package com.midea.cloud.srm.biz.pj.meiql.controller;

import com.midea.cloud.meiql.api.component.paging.Page;
import com.midea.cloud.srm.model.pj.supplier.test.entity.MeiqlTest;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenQueryWrapper;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenUpdateWrapper;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * @author huangbf3
 * meiql测试
 */
@RestController
@RequestMapping("/external/meiql")
public class MeiqlController {

    @Autowired
    QlOpenClient qlOpenClient;

    @ApiOperation("列表查询")
    @PostMapping("/findList")
    public List<MeiqlTest> findList(@RequestBody MeiqlTest meiqlTest) {
        QlOpenQueryWrapper wrapper = QlOpenWrappers.query("MeiqlTest");
        wrapper.eq(StringUtils.isNotBlank(meiqlTest.getTest()),"test","1");

        List<MeiqlTest> list = qlOpenClient.query(ContextPath.SUP,wrapper,MeiqlTest.class);
        return list;
    }


    @ApiOperation("新增")
    @PostMapping("/save")
    public void save(@RequestBody MeiqlTest meiqlTest) {
        //不需要设置ID，传ID不会新增入库
        qlOpenClient.save(ContextPath.SUP,"MeiqlTest",Arrays.asList(meiqlTest));
    }

    @ApiOperation("更新")
    @PostMapping("/update")
    public void update(@RequestBody MeiqlTest meiqlTest) {

        QlOpenUpdateWrapper wrapper = QlOpenWrappers.update("MeiqlTest")
                .set(MeiqlTest::getTest, meiqlTest.getTest())
                .eq(MeiqlTest::getRowId,meiqlTest.getRowId());
        qlOpenClient.update(ContextPath.SUP, wrapper);
//        qlOpenClient.update(ContextPath.SUP, "MeiqlTest", Arrays.asList(meiqlTest));
    }

    @ApiOperation("删除")
    @PostMapping("/delete")
    public void delete(@RequestBody MeiqlTest meiqlTest) {
        QlOpenUpdateWrapper wrapper = QlOpenWrappers.update("MeiqlTest")
                .eq(MeiqlTest::getRowId,meiqlTest.getRowId());
        qlOpenClient.delete(ContextPath.SUP,wrapper);
//        qlOpenClient.delete(ContextPath.SUP,"MeiqlTest",Arrays.asList(meiqlTest.getRowId()));//批量删除
    }


    @ApiOperation("获取明细")
    @GetMapping("/get")
    public MeiqlTest get(@RequestParam("rowId")Long rowId) {
        MeiqlTest meiqlTest = qlOpenClient.read(ContextPath.SUP,"MeiqlTest",rowId,MeiqlTest.class);
        return meiqlTest;
    }


    @ApiOperation("获取分页")
    @PostMapping("/listPage")
    public Page<MeiqlTest> listPage(@RequestBody MeiqlTest meiqlTest) {

        QlOpenQueryWrapper wrapper = QlOpenWrappers.query("MeiqlTest");
        wrapper.eq(StringUtils.isNotBlank(meiqlTest.getTest()),MeiqlTest::getTest,meiqlTest.getTest());

        Page<MeiqlTest> list = qlOpenClient.query(ContextPath.SUP,wrapper,Long.valueOf(meiqlTest.getPageNum()),Long.valueOf(meiqlTest.getPageSize()),MeiqlTest.class);
        return list;
    }
}
