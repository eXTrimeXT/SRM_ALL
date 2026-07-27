<template>
  <el-container class="flex-container innerboxcode_list_wrapper" direction="vertical">
    <el-main>
      <FormWrapper :formArray="filterConfig" @getFormData="getQuerydata" />
      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :checkChange="checkChangeChange"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :preQueryData="queryParam"
        :openCustomTable="true"
        :comActive="$attrs['changeTab']"
        url="/api-base/base/innerboxcode/listPage"
        :checkbox="true"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import innerboxcodeEdit from './edit.vue'
import CUploadFile from '@/library/components/c-upload-file'
import CDownloadLink from 'lib@/components/c-download-link'
import { innerBoxCodeApi } from 'modb@/barcodeManagement/api'

export default {
  name: 'InnerboxcodeList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    CUploadFile,
    CDownloadLink
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      pageSize: 15,
      gridId: 'list',
      // 文件上传配置信息
      fileInfo: {
        fileModular: 'workFlow', // 文件所属模块 -》审批流程
        fileFunction: 'workflowReport', // 审批流相关文件
        fileType: 'images' // 文件所属类型
      },
      currentRows: [],
      tableHeader: [
        {
          prop: 'innerBoxCode',
          label: this.$t('hierarchical.Innerboxcode'),  // '内箱编码'
          width: 120
        },
        {
          prop: 'vendorName',
          label: this.$t('common.companyName'),  // '供应商名称'
          width: 120
        },
        {
          prop: 'vendorCode',
          label: this.$t('common.vendorCode'),  // '供应商编码'
          width: 120
        },
        {
          prop: 'materialCode',
          label: this.$t('common.materialCode'),  // '物料编码'
          width: 100
        },
        {
          prop: 'materialName',
          label: this.$t('common.materialName'),  // '物料名称'
          width: 100
        },
        {
          prop: 'categoryName',
          label: this.$t('components.category.categoryName'),  // '品类名称'
          width: 100
        },
        // {
        //   prop: "printCount",
        //   label: "打印次数",
        //   width: 100,
        // },
        {
          prop: 'productionDate',
          label: this.$t('orderMod.productionDate'),  // '生产日期'
          width: 150,
          dataType: 'dateTime'
        },
        {
          prop: 'createdBy',
          label: this.$t('purchaseDemand.createdFullName'),  // '创建人名称'
          width: 120
        },
        {
          prop: 'creationDate',
          label: this.$t('common.creationDate'),  // '创建日期'
          width: 150,
          dataType: 'dateTime'
        },
        {
          prop: 'lastUpdatedBy',
          label: this.$t('common.updatePeople'),  // '更新人'
          width: 100
        }
        // {
        //   prop: 'operation',
        //   label: '操作',
        //   showType: 'buttons',
        //   btnStyle: 'text',
        //   fixed: 'right',
        //   width: 80,
        //   buttons: [
        //     {
        //       callback: row => this.viewHandle(row),
        //       formattor: () => {
        //         return this.$t('common.view')
        //       }
        //     }
        //   ]
        // }
      ],

      filterConfig: [
        { prop: 'vendorCode', label: this.$t('supplierRating.supplierCode') },  // '供应商编号'
        { prop: 'materialCode', label: this.$t('common.materialCode') },  // '物料编码'
        { prop: 'innerBoxCode', label: this.$t('hierarchical.Innerboxcode') }  // '内箱编码'
      ],
      queryParam: {},
      dictHeaderExportParam: []
    }
  },
  created () {
    console.log(innerBoxCodeApi, 'innerBoxCodeApi')
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {

    getQuerydata (params) {
      this.queryParam = params
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    viewHandle (row) {
      const tab = {
        component: innerboxcodeEdit,
        params: {
          row,
          flag: 'view'
        },
        ctrlHeight: true,
        title: this.$t('cusEntry.supplement20250211.englishinnerBoxBarcodeView'),  // '内箱条码查看'
        name: 'innerboxcodeEdit' + row.innerBoxId
      }
      this.$emit('tab-add', tab)
    },
    handleCurrentChange (val) {
      this.currentRows = val
    },
    // 多选删除
    checkChangeChange (rows) {
      this.currentHeaderRows = rows
      let rowArr = rows
      this.dictHeaderExportParam = rowArr.map(i => (i.innerBoxId))
    }
  }
}
</script>
