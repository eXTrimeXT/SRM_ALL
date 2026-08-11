<template>
  <el-container
    class="flex-container sitereviewplan_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="filterConfig"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />
      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        :source="nonMaterialApi.potentialList"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
import MImport from 'lib@/components/import'
import ExportExcel from 'lib@/components/export-excel'
import vendorProfileDetailRead from 'modb@/vendorManagementBuyer/views/vendorProfile/vendorProfileDetailRead'
import { nonMaterialApi } from 'modb@/vendorManagementBuyer/api/supApi'

export default {
  name: 'SitereviewplanList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    QuickSearch,
    MImport,
    OrganizationSelector,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      nonMaterialApi: nonMaterialApi,
      displayItem: [],
      dialogFormVisible2: false,
      name: 'sitereviewplanList',
      tableName: 'sitereviewplanTable',
      pageSize: 15,
      gridId: 'list',
      currentRows: [],
      visible: false,
      mode: 'add',
      dialogTitle: '详情',
      form: {

      },
      rules: {},
      extraData: {
        sourceType: 'WEB_APP',
        uploadType: 'MINIO',
        fileModular: 'base',
        fileFunction: 'sitereviewplan',
        fileType: 'excel'
      },
      dictCodes: {},
      filterParams: {},
      tableHeader: [
        {
          prop: 'companyName',
          label: () => this.$t('common.vendorName'), // 供应商名称
          showType: 'button',
          btnStyle: 'text',
          callback: function (row) {
            this.showCompany(row)
          }.bind(this)
        },
        {
          prop: 'companyCode',
          label: () => this.$t('common.vendorCode') // 供应商编码
        },
        {
          prop: 'companyType',
          code: 'COMPANY_NATURE',
          dataType: 'dict',
          label: () => this.$t('vendorMod.companyType') // 企业性质
        },
        {
          prop: 'lcCode',
          label: () => this.$t('vendorMod.lcCode') // 社会统一信用代码
        },
        {
          prop: 'status',
          code: 'SUPPLIER_LIST_STATUS',
          dataType: 'dict',
          label: () => this.$t('vendorMod.registerStatus') // 注册状态
        },
        {
          prop: 'dataSources',
          code: 'DATA_SOURCE',
          dataType: 'dict',
          label: () => this.$t('vendorMod.dataSources')// 数据来源
        },
        {
          prop: 'lastUpdateDate',
          dataType: 'dateTime',
          label: () => this.$t('bidMod.updateTime')// 更新时间
        }
      ],
      filterConfig: [
        {
          prop: 'companyName',
          label: () => this.$t('common.vendorName'), // 供应商名称
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info_display_buyer'
        },
        {
          prop: 'companyCode',
          label: () => this.$t('common.vendorCode')// 供应商编码
        },
        {
          prop: 'companyType',
          code: 'COMPANY_NATURE',
          type: 'dict',
          label: () => this.$t('vendorMod.companyType')// 企业性质
        },
        {
          prop: 'dataSources',
          code: 'DATA_SOURCE',
          type: 'dict',
          label: () => this.$t('vendorMod.dataSources')// 数据来源
        }
      ],
      queryParam: {}
    }
  },
  created () {
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
    // 项目下拉
    nonMaterialApi.findCalculatedScoreItemsList().then(res => {
      if (res.data) {
        this.projectList = res.data.map(i => ({
          value: i.scoreItemsId,
          label: i.projectName
        }))
        this.filterConfig[2].options = this.projectList
      }
    })
  },
  methods: {
    showCompany (row) {
      this.$emit('tab-add', {
        component: vendorProfileDetailRead,
        params: {
          flag: 'view',
          companyId: row.companyId,
          tabName: 'vendorProfileDetailRead' + row.companyName
        },
        title: row.companyName,
        name: 'vendorProfileDetailRead' + row.companyName
      })
    },
    getCategoryObj (val, scope) {
      scope.categoryId = val ? val.categoryId : ''
      scope.categoryCode = val ? val.categoryCode : ''
      scope.categoryName = val ? val.categoryName : ''
    },
    handleItemSelection (val) {
      this.multipleSelection = val
    },
    // 新增单个品类
    addOneItem () {
      this.displayItem.push({
        categoryId: null,
        categoryCode: null,
        categoryName: null
      })
    },
    // 点击新增采购组织
    selectHandler2 (node, value) {
      this.form.orgId = node.organizationId
      this.form.orgCode = node.organizationCode
      this.form.orgName = node.organizationName
      console.log(this.form)
    },
    // 快查供应商名称
    getDepObjName (val, scpoe) {
      scpoe.vendorId = val.companyId
      scpoe.vendorName = val.companyName
      scpoe.vendorCode = val.companyCode
      console.log(this.form)
    },
    handleSuccess () {
      this.getQuerydata()
    },
    cancel () {
      this.visible = false
    },
    confirmSave () {

    },
    confirm () {

    },
    syncFilterParams (values) {
      this.filterParams = values
    },
    getQuerydata (params) {
      let obj = {
        supplierType: 'NO_MATERIAL'
      }
      this.queryParam = { ...params, ...obj }
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    }

  }
}
</script>
