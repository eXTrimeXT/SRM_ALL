<template>
  <el-container>
    <el-main>
      <div style="padding:30px 20px">
        <!-- 这一块是新写的动态表格组件 -->
        <TableExtend
          :tableData="dimFieldConfigS"
          :model2="Emodel"
          ref="extendData"
        ></TableExtend>
        <!-- dimFieldConfigS为表头的值
        Emodel为数据的值
        extendData为ref的值拿组件数据的时候需要使用 -->
      </div>

    </el-main>
  </el-container>
</template>
<script>
import http from "@/utils/http";
import TableExtend from "mod@/userManage/views/companyInfoMaintain/tableExtend";
import TableView from 'lib@/components/Table/TableView'


export default {
  name: "systemDock",
  components: { TableExtend,TableView },
  provide() {
    return { context: this }
  },
  data() {
    return {
      dimFieldConfigS:[],
      tableExtendList:[],
      saveData:[],
      Emodel:[],
      pageSize: 15,
      queryParam: {
        "companyId": 310286263994496,
        "dimCode": "moneyTable"
      },
      tableHeader:[
        {
          prop: 'testleft2',
          label: '供应商名称',
        },
        {
          prop: 'TXLXED',
          label: '采购组织',
        },
      ]
    };
  },
  mounted() {
    this.getData();
    this.oldGetData();
  },
  methods: {
    getData(){//拿数据显示
      this.$http({
        url: '/api-sup/dim/dimFieldResult/listTableByCompanyIdAndDimCode',
        method: 'POST',
        data: {"companyId": 310286263994496,"dimCode": "moneyTable"},
        loading: true
      }).then(res => {
        if (res.data) {
          let datas = res.data;

          //需传入组件的值
          this.Emodel =  this.formattingData(datas)

          //需要传入组件的表头
          this.dimFieldConfigS = datas.header.dimFieldConfigS
        }
      })
    },
    formattingData(data){
        let Emodel = [];
        data.dataList.forEach(element => {
          let obj = {}
          obj[element.fieldCode] = element.fieldValue;
          Emodel.push(obj)
        });
        return Emodel
    },
    save(){//拿页面数据进行保存
      this.saveData = this.$refs[extendData][0].model
    }
  }
};
</script>
<style scoped lang="scss"></style>
