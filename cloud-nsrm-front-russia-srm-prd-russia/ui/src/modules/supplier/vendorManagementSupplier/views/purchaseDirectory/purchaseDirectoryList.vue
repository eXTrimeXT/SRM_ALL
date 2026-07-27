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
          <el-button v-if="curRole === 'BUYER'" type="primary" @click="editTab('add')">
            {{ $t('common.add') }}
          </el-button>
          <!-- 导入 -->
          <MImport
            v-if="curRole === 'BUYER'"
            :title="$t('common.import')"
            up-load-url="/api-sup/purchaseCataLog/importExcel"
            :extra-data="extraData"
            type="default"
            @downloadTemplate="downloadTemplate"
            @handleSuccess="handleSuccess"
          />
          <ExportExcel
            page-url="/api-sup/purchaseCataLog/listPageByParam"
            :filter-params="queryParam"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            export-mode="front"
            type="default"
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
        url="/api-sup/purchaseCataLog/listPageByParam"
      />
      <!-- 弹框区域-->
      <srm-dialog
        size="middle"
        :title="$t('vendorMod.newSourcelList')"
        :visible.sync="dialogFormVisible"
        :close-on-click-modal="false"
      >
        <el-form
          ref="catForm"
          :model="form"
          class="form-incontainer form-fill-style"
          :rules="rules"
          label-position="top"
        >
          <srm-row>
            <srm-col :initCol="2">
              <el-form-item :label="$t('common.vendorName')" prop="vendorName">
                <QuickSearch
                  :disabled="curOpt === 'edit'"
                  :show-input="form.vendorName"
                  show-key="companyName"
                  :scope-data="form"
                  name="scc_sup_company_info_display"
                  @close-quicksearch="getCompanyObj"
                />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="2">
              <el-form-item :label="$t('common.vendorCode')" prop="vendorCode">
                <el-input v-model="form.vendorCode" :disabled="curOpt === 'edit'" />
              </el-form-item>
            </srm-col>
          </srm-row>
          <srm-row>
            <srm-col :initCol="2">
              <el-form-item :label="$t('common.orgName')" prop="purchaseOrgId">
                <OrganizationSelector
                  v-if="curOpt === 'edit' || curOpt === 'add'"
                  ref="organizationSelector"
                  v-model="form.purchaseOrgId"
                  :parent-id="-1"
                  node-type="OU"
                  :placeholder="$t('common.pleaseSelect')"
                  :scope="form"
                  :disabled="curOpt === 'edit'"
                  @select="addOrgHandle"
                />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="2">
              <el-form-item :label="$t('common.materialName')" prop="materialName">
                <QuickSearch
                  :disabled="curOpt === 'edit'"
                  :show-input="form.materialName"
                  show-key="materialName"
                  :scope-data="form"
                  name="scc_base_material_item"
                  @close-quicksearch="getMaterialObj"
                />
              </el-form-item>
            </srm-col>
          </srm-row>
          <srm-row>
            <srm-col :initCol="2">
              <el-form-item :label="$t('common.materialCode')" prop="materialCode">
                <el-input v-model="form.materialCode" disabled />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="2">
              <el-form-item :label="$t('common.category')" prop="categoryFullName">
                <el-input v-model="form.categoryFullName" disabled />
              </el-form-item>
            </srm-col>
          </srm-row>
          <srm-row>
            <srm-col :initCol="2">
              <el-form-item :label="$t('vendorMod.orderStatus')" prop="catalogStatus">
                <DictSelect v-model="form.catalogStatus" code="CATALOG_STATUS" clearable />
              </el-form-item>
            </srm-col>
            <srm-col />
          </srm-row>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="dialogFormVisible = false">
            {{ $t('common.cancel') }}
          </el-button>
          <el-button type="primary" @click="saveHandle">
            {{ $t('common.submit') }}
          </el-button>
        </div>
      </srm-dialog>
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
import purchaseDirectoryDetail from './purchaseDirectoryDetail'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import { saveOrUpdateOrderByUrl } from 'mods@/vendorManagementSupplier/api'

export default {
  name: 'PurchaseDirectoryList',
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
  provide () {
    return { context: this }
  },
  data () {
    return {
      dictCodes: {
        catalogStatus: 'CATALOG_STATUS',
        dataSource: 'PURCHASE_DATA_SOURCE'
      },
      preFormObj: {},
      curRole: this.$store.getters.userType,
      extraData: {
        fileModular: 'sup',
        fileFunction: 'purchaseCatalog',
        fileType: 'excel'
      },
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
        catalogStatus: ''
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
          prop: 'catalogStatus',
          label: () => this.$t('vendorMod.orderStatus'), // '状态',
          type: 'dict',
          code: 'CATALOG_STATUS'
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
      ]
    }
  },

  created () {
    console.log('user', this.$store.getters.userInfo)
    if (this.curRole === 'VENDOR') {
      this.queryForm.splice(3, 1)
      this.preFormObj.vendorName = this.$store.getters.userInfo.companyName
    }
    let _this = this
    this.tableHeader = [
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
        prop: 'catalogStatus',
        label: () => _this.$t('vendorMod.orderStatus'), // '状态',
        width: 100,
        dataType: 'dict', // 数据类型为字典
        code: 'CATALOG_STATUS' // 字典code
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
        prop: 'dataSource',
        label: '数据来源',
        width: 180,
        dataType: 'dict',
        code: 'PURCHASE_DATA_SOURCE'
      },
      {
        prop: 'lastUpdatedUserName', // lastUpdatedBy
        label: () => _this.$t('common.updatePeople'), // '更新人',
        width: 100
      },
      {
        prop: 'updatedReason',
        label: () => _this.$t('vendorMod.updatedReason'), // '更新原因',
        width: 100
      },
      {
        prop: 'lastUpdateDate',
        label: () => _this.$t('common.updateTime'), // '更新时间',
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
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
              this.$router.push({
                name: 'purchaseDirectoryChangeV',
                params: {
                  from: 'purchaseDirectory',
                  row
                }
              })
            }.bind(this),
            formattor (val) {
              return '更新'
            },
            show: row => {
              return row.catalogStatus === 'VALID'
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
      }).then(() => {
        this.$http({
          url: '/api-sup/purchaseCataLog/failure',
          method: 'GET',
          params: {
            catalogId: row.catalogId
          }
        }).then(res => {
          this.$message.success('失效成功')
          this.getQuerydata()
        })
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
      let name = row.catalogId || ''
      let tab = {
        component: purchaseDirectoryDetail,
        ctrlHeight: true,
        params: {
          flag: type,
          row,
          tabName: '货源清单' + name
        },
        title: name ? '货源清单' + name : '新增货源清单',
        name: '货源清单' + name
      }
      this.$emit('tab-add', tab)
    },
    deleteOne (row) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url: '/api-sup/purchaseCataLog/delete',
            method: 'GET',
            params: {
              catalogId: row.catalogId
            },
            loading: true
          }).then(() => {
            this.$message.success(this.$t('common.successDelete'))
            this.getQuerydata()
          })
        })
        .catch(() => {})
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
.the_purchaseDirectory_wrapper {
}
</style>
