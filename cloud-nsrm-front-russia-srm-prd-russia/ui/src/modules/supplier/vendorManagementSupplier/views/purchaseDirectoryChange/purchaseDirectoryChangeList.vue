<template>
  <el-container class="flex-container-notab the_purchaseDirectory_wrapper" direction="vertical">
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        :preFormObj="preFormObj"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />
      <MainHeader>
        <template slot="left">
          <ExportExcel
            page-url="/api-sup/purCatalogChange/listPage"
            :filter-params="queryParam"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            export-mode="front"
            type="primary"
          />
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-sup/purCatalogChange/listPage"
      />
    </el-main>
  </el-container>
</template>
<script>
import MImport from 'lib@/components/import'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
import { adaptDictData, parseTime } from '@/utils'
import ExportExcel from 'lib@/components/export-excel'
import { downloadFileLink } from 'lib@/utils/file'
import purchaseDirectoryChangeDetail from './purchaseDirectoryChangeDetail'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import { saveOrUpdateOrderByUrl } from 'mods@/vendorManagementSupplier/api'

export default {
  name: 'PurchaseDirectoryChangeList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    QuickSearch,
    OrganizationSelector,
    MImport,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      dictCodes: {
        changeStatus: 'MATERIAL_LIST_CHANGE'
      },
      preFormObj: {},
      tableName: 'purchaseDirectoryList',
      defaultTableHeader: [],
      pageSize: 15,
      gridId: 'purchaseDirectoryList',
      curOpt: 'add',
      queryParam: {},
      filterParams: {},
      catStatus: [],
      tableHeader: [],
      tableData: [],
      form: {
        vendorId: null,
        vendorCode: '',
        vendorName: '',
        purchaseOrgId: null,
        purchaseOrgCode: '',
        purchaseOrgName: null,
        parentOrgCode: '',
        parentOrgId: null,
        parentOrgName: '',
        materialCode: '',
        materialName: '',
        materialId: null,
        categoryCode: '',
        categoryId: null,
        categoryName: '',
        categoryFullName: '',
        changeStatus: ''
      },
      rules: {
        vendorName: [{ required: true, message: this.$t('vendorMod.msgVendorId') }], // '请输入供应商名称
        purchaseOrgName: [{ required: true, message: this.$t('dataConfMod.msgInputItemCode') }], // '请输入物料编号
        materialName: [{ required: true, message: this.$t('dataConfMod.msgInputItemCode') }] // '请输入物料编号
      },
      dialogFormVisible: false,
      queryForm: [
        {
          prop: 'purchaseOrgId',
          label: () => '业务实体', // 业务实体
          type: 'OUorganizationSelector'
        },
        {
          prop: 'materialCode',
          label: () => this.$t('common.materialCode'), // 物料编码,
          type: 'quicksearch',
          showKey: 'materialCode',
          name: 'scc_base_material_item'
        },
        {
          prop: 'changeStatus',
          label: () => this.$t('vendorMod.orderStatus'), // '状态',
          type: 'dict',
          code: 'MATERIAL_LIST_CHANGE'
        },
        {
          prop: 'vendorName',
          label: () => this.$t('common.vendorName'), // 供应商名称
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info_display'
        },
        {
          prop: 'categoryName',
          label: () => this.$t('common.category'), // 品类
          type: 'catSelect',
          showKey: 'categoryName'
        },
        {
          prop: 'createdBy',
          label: () => this.$t('common.creator') // 创建人
        },
        {
          prop: 'invId',
          type: 'INVorganizationSelector',
          multiple: false,
          label: '库存组织',
          parentId: 'purchaseOrgId'
        },
        {
          prop: 'materialName',
          label: '物料名称'
        }
      ],
      curRole: this.$store.getters.userType,
      curUserName: this.$store.getters.userInfo ? this.$store.getters.userInfo.username : null
    }
  },
  watch: {
    $route: {
      handler (nVal) {
        if (nVal && nVal.params) {
          let { from, row } = nVal.params
          if (from === 'purchaseDirectory') { // 货源清单过来
            this.editTab('add', row)
          }
        }
      },
      immediate: true,
      deep: true
    }
  },
  provide () {
    return { context: this }
  },

  created () {
    if (this.curRole === 'VENDOR') {
      this.queryForm.splice(3, 1)
      this.preFormObj.vendorName = this.$store.getters.userInfo.companyName
    }
    let _this = this
    this.tableHeader = [
      {
        prop: 'changeNo',
        label: () => '货源变更单号',
        width: 130
      },
      {
        prop: 'vendorCode',
        label: () => _this.$t('common.vendorCode'), // 供应商编码
        width: 120
      },
      {
        prop: 'vendorName',
        label: () => _this.$t('common.vendorName'), // 供应商名称
        width: 150
      },
      {
        prop: 'purchaseOrgName',
        label: () => '业务实体', // 业务实体
        width: 130
      },
      {
        prop: 'invName',
        label: '库存组织',
        width: 130
      },
      {
        prop: 'materialCode',
        label: () => _this.$t('common.materialCode'), // 物料编码,
        width: 100
      },
      {
        prop: 'materialName',
        label: () => _this.$t('common.materialName'), // '物料名称',
        width: 100
      },
      {
        prop: 'categoryFullName',
        label: () => _this.$t('common.category'), // 品类
        width: 100
      },
      {
        prop: 'changeStatus',
        label: () => _this.$t('vendorMod.orderStatus'), // '状态',
        width: 100,
        dataType: 'dict', // 数据类型为字典
        code: 'MATERIAL_LIST_CHANGE' // 字典code
      },
      {
        prop: 'startDate',
        label: () => '生效时间', // 生效时间
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'endDate',
        label: () => '失效时间', // 失效时间
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'createdUserName', // createdBy
        label: () => _this.$t('common.creator'), // 创建人
        width: 100
      },
      {
        prop: 'creationDate',
        label: () => _this.$t('common.creationTime'), // 创建时间
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'updatedReason',
        label: () => _this.$t('vendorMod.updatedReason'), // '更新原因',
        width: 100
      },
      {
        prop: 'operation',
        label: () => _this.$t('common.operation'), // 操作
        width: 120,
        btnStyle: 'text',
        fixed: 'right',
        showType: 'buttons',
        buttons: [
          {
            callback: function (row) {
              this.editTab('edit', row)
            }.bind(this),
            formattor (val) {
              return '编辑'
            },
            show: row => {
              // return ['DRAFT', 'REJECTED', 'WITHDRAW'].includes(row.changeStatus) || (['FIRST_REJECT'].includes(row.changeStatus) && this.curRole === 'VENDOR')
              return ['DRAFT'].includes(row.changeStatus) || (['REJECTED', 'WITHDRAW'].includes(row.changeStatus) && this.curRole === 'BUYER') || (['FIRST_REJECT'].includes(row.changeStatus) && this.curRole === 'VENDOR')
            }
          },
          {
            callback: function (row) {
              this.editTab('view', row)
            }.bind(this),
            formattor (val) {
              return '审批'
            },
            show: row => {
              return row.changeStatus === 'SUBMITTED' && this.curRole === 'BUYER'
            }
          },
          {
            callback: function (row) {
              this.editTab('manage', row)
            }.bind(this),
            formattor (val) {
              return '管理'
            },
            show: row => {
              return row.changeStatus === 'CONFIRMING' && this.curRole === 'BUYER'
            }
          },
          {
            callback: function (row) {
              // this.editTab('view', row)
              this.editTab('submit', row)
            }.bind(this),
            formattor (val) {
              return '提交审批'
            },
            show: row => {
              return row.changeStatus === 'FIRST_APPROVED' && this.curRole === 'BUYER'
            }
          },
          {
            callback: function (row) {
              this.editTab('view', row)
            }.bind(this),
            formattor (val) {
              return '查看'
            }
          }
        ]
      }
    ]
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    inValid (row) {
      this.$confirm('确定要失效此物料信息?', {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(res => {

      })
    },
    handleSuccess () {
      this.getQuerydata()
    },
    downloadTemplate () {
      downloadFileLink('/api-sup/purchaseCataLog/importExcelTemplate', '导入模板.xlsx').catch(
        () => {
          this.$message.error(this.$t('components.eio.downloadFail'))
        }
      )
    },
    syncFilterParams (values) {
      this.filterParams = values
    },
    getQuerydata (v) {
      this.queryParam = { ...v, ...this.preFormObj }
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    // 选择供应商回调
    getCompanyObj (val, data) {
      this.form.vendorId = val ? val.companyId : null
      this.form.vendorCode = val ? val.companyCode : ''
      this.form.vendorName = val ? val.companyName : ''
    },
    // 确认选择物料
    getMaterialObj (val, data) {
      this.form.materialCode = val ? val.materialCode : ''
      this.form.materialName = val ? val.materialName : ''
      this.form.materialId = val ? val.materialId : null
      this.form.categoryId = val ? val.categoryId : null
      this.form.categoryName = val ? val.categoryName : ''
      this.form.categoryCode = val ? val.categoryCode : ''
      this.form.categoryFullName = val ? val.categoryFullName : ''
    },
    // 选择组织
    addOrgHandle (e, id, scope) {
      this.form.purchaseOrgId = e ? e.organizationId : null
      this.form.purchaseOrgCode = e ? e.organizationCode : ''
      this.form.purchaseOrgName = e ? e.organizationName : null
      this.form.parentOrgId = e ? e.parentOrganizationCode : ''
      this.form.parentOrgCode = e ? e.parentOrganizationCode : ''
      this.form.parentOrgName = e ? e.parentOrganizationName : ''
    },
    // 确认选中的品类
    comfirmSelect (node, scope) {
      scope.categoryId = node ? node.categoryId : null
      scope.categoryName = node ? node.categoryName : ''
      scope.categoryCode = node ? node.categoryCode : ''
    },
    // 编辑tab
    editTab (type, row = {}) {
      let name = row.changeNo || ''
      let tab = {
        component: purchaseDirectoryChangeDetail,
        ctrlHeight: true,
        params: {
          flag: type,
          row,
          tabName: '货源变更' + name
        },
        title: name ? '货源变更' + name : '货源变更',
        name: '货源变更' + name
      }
      this.$emit('tab-add', tab)
    },
    deleteOne (val) {
      this.$confirm(this.$t('common.delRow'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => { })
        .catch(() => { })
    },
    // 保存
    saveHandle () {
      // 验证form表单
      this.$refs.catForm.validate(valid => {
        if (valid) {
          let url = '/api-sup/purchaseCataLog/saveOrUpdateCatalog'
          let submitData = this.form
          if (this.curOpt === 'add') {
            delete submitData.catalogId
          }
          saveOrUpdateOrderByUrl(url, submitData).then(res => {
            if (res) {
              this.$message({
                message: res.message,
                type: 'success'
              })
              this.dialogFormVisible = false
              this.getQuerydata()
            }
          })
        } else {
          return false
        }
      })
    }
  }
}
</script>
<style scoped lang="scss">
.the_purchaseDirectory_wrapper {}
</style>
