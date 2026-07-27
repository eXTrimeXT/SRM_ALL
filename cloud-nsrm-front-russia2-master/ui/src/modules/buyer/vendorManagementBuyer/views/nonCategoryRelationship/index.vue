<template>
  <el-container
    class="flex-container-notab the_purchaseDirectory_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        @getFormData="getQuerydata"
      />
      <MainHeader
        :l-span="22"
        :r-span="2"
        style="padding-left: 8px;height:50px"
      >
        <template slot="left">
        <!-- code="sup:categoryRelationship:import" -->
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-sup/info/serviceOrgCategory/listPageOrgCategoryByParam"
      />
      <!-- 弹框区域-->
      <!-- 品类状态变更记录 -->
      <srm-dialog
        :title="$t('vendorMod.cateStatusChangeRecord')"
        :visible.sync="dialogFormVisible"
        :close-on-click-modal="false"
        size="large"
      >
        <el-table
          :data="displayList"
          style="width: 100%"
          border
          height="251px"
        >
          <!-- 变更前状态 -->
          <el-table-column
            align="center"
            width="120"
            prop="beforeServiceStatus"
            :label="$t('vendorMod.stateBeforeChange')"
            :formatter="formatterStatus"
            show-overflow-tooltip
          />
          <!-- 变更后状态 -->
          <el-table-column
            align="center"
            width="120"
            prop="afterServiceStatus"
            :label="$t('vendorMod.stateAfterChange')"
            :formatter="formatterStatus"
            show-overflow-tooltip
          />
          <!-- 单据编号 -->
          <el-table-column
            align="center"
            width="150"
            prop="formNum"
            :label="$t('vendorMod.reviewFormNumber')"
            show-overflow-tooltip
          />
          <!-- 单据类型 -->
          <el-table-column
            align="center"
            width="120"
            prop="formType"
            :label="$t('bidMod.billType')"
            :formatter="formatterStatus2"
            show-overflow-tooltip
          />
          <!-- 创建时间 -->
          <el-table-column
            align="center"
            width="150"
            prop="creationDate"
            :label="$t('common.creationTime')"
            show-overflow-tooltip
            :formatter="(row, column, cellValue) => $parseTime(cellValue)"
          />
          <!-- 创建人 -->
          <el-table-column
            align="center"
            width="120"
            prop="createdUserName"
            :label="$t('common.creator')"
            show-overflow-tooltip
          />
        </el-table>
      </srm-dialog>

      <!-- 合同详情弹框 -->
      <srm-dialog
        :title="$t('vendorMod.contractDetails')"
        :visible.sync="dialogFormVisible2"
        :close-on-click-modal="false"
        size="middle"
      >
        <el-table
          :data="contractData"
          style="width: 100%"
          border
          max-height="250px"
        >
          <el-table-column
            align="center"
            type="index"
            :label="$t('purSettlementMod.tabindex')"
            width="50"
          />
          <!-- 合同模板 -->
          <el-table-column
            align="center"
            prop="modelName"
            :label="$t('vendorMod.modelName')"
          />
          <!-- 合同名称 -->
          <el-table-column
            align="center"
            prop="contractName"
            :label="$t('vendorMod.contractName')"
          >
            <template slot-scope="scope">
              <el-link
                type="primary"
                :underline="false"
                @click="contractNameClick(scope.row, scope)"
              >
                {{ scope.row.contractName }}
              </el-link>
            </template>
          </el-table-column>
          <!-- 合同状态 -->
          <el-table-column
            align="center"
            prop="contract"
            :label="$t('vendorMod.contract')"
            width="80"
          >
            <template slot-scope="scope">
              <div v-if="scope.row.contractName">
                {{ $t('vendorMod.signed') }}
              </div>
              <div v-else>
                {{ $t('vendorMod.notSigned') }}
              </div>
            </template>
          </el-table-column>
          <!-- 删除 -->
          <el-table-column
            :label="$t('common.operation')"
            width="80"
          >
            <template slot-scope="scope">
              <div v-if="scope.row.contractName" />
              <el-button
                v-else
                type="text"
                @click="createContractClick(scope.row, scope)"
              >
                {{
                  $t('bidMod.createContract')
                }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </srm-dialog>
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import QuickSearch from 'lib@/components/QuickSearch'
import { parseTime } from '@/utils'
import ExportExcel from 'lib@/components/export-excel'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import { saveOrUpdateOrderByUrl } from 'modb@/vendorManagementBuyer/api/vendorManagement'
import { supCommonApi } from 'modb@/vendorManagementBuyer/api/supApi'

// import contractInformation from '@/pages/contractManagement/contractMaintainList/edit'

export default {
  name: 'NonCategoryRelationship',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    QuickSearch,
    ExportExcel,
    MImport
  },
  provide () {
    return { context: this }
  },
  data () {
    return {
      contractData: [],
      iModal: {
        title: this.$t('common.import'), // 导入
        upLoadUrl: '/api-sup/info/orgCategory/importExcel'
      },
      tableName: 'purchaseDirectoryList',
      defaultTableHeader: [],
      pageSize: 15,
      gridId: 'purchaseDirectoryList',
      curOpt: 'add',
      queryParam: {},
      filterParams: {},
      catStatus: [],
      formTypeList: [],
      currentRow: null,
      tableHeader: [],
      tableData: [],
      dialogFormVisible: false,
      dialogFormVisible2: false,
      displayList: [],
      queryForm: [
        {
          prop: 'companyName',
          label: () => this.$t('common.vendorName'), //
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info_display_buyer'
        },
        {
          prop: 'serviceStatus',
          label: () => this.$t('vendorMod.catServiceStatus'), // 品类状态
          type: 'dict', // 字典类型
          code: 'CATEGORY_STATUS' // 字典code
        },
        {
          prop: 'orgIds',
          label: () => this.$t('dataConfMod.orgId'), // 业务实体
          type: 'OUorganizationSelector',
          multiple: true
        },
        {
          prop: 'categoryName',
          label: () => this.$t('dataConfMod.categoryLittle'), // 物料小类
          type: 'quicksearch',
          showKey: 'categoryName',
          // propKey: "categoryName",
          name: 'scc_base_purchase_category2'
        },
        {
          prop: 'warningStatus',
          label: () => this.$t('vendorMod.warningStatus'), // 预警状态
          type: 'dict', // 字典类型
          code: 'WARNING_STATUS' // 字典code
        },
        {
          prop: 'categoryLevel',
          label: () => this.$t('vendorMod.categoryLevel'), // 品类等级
          type: 'dict', // 字典类型
          code: 'VENDOR_LEVEL' // 字典code
        },
        {
          prop: 'tempCompanyFlag',
          label: () => this.$t('vendorMod.tempCompanyFlag'), // 是否临时供应商
          type: 'dict', // 字典类型
          code: 'YES_OR_NO' // 字典code
        },
        {
          prop: 'companyStatus',
          label: () => this.$t('bidMod.vendorStatus'), // 供应商状态
          type: 'dict', // 字典类型
          code: 'VENDOR_STATUS' // 字典code
        }
      ]
    }
  },
  created () {
    let _this = this
    this.tableHeader = [
      {
        prop: 'companyCode',
        label: () => _this.$t('common.vendorCode'), // 供应商code
        width: 120
      },
      {
        prop: 'companyName',
        label: () => _this.$t('common.vendorName'), // 供应商名称
        minWidth: 150
      },
      {
        prop: 'orgName',
        label: () => _this.$t('dataConfMod.orgId'), //  业务实体
        minWidth: 150
      },
      {
        prop: 'categoryFullName',
        label: () => _this.$t('vendorMod.categoryFullName'), // 品类全路径
        minWidth: 150
      },
      {
        prop: 'categoryName',
        label: () => _this.$t('vendorMod.littleCategory'), // 小类
        minWidth: 150
      },
      {
        prop: 'companyStatus',
        label: () => _this.$t('bidMod.vendorStatus'), // 供应商状态
        width: 120,
        dataType: 'dict', // 数据类型为字典
        code: 'VENDOR_STATUS' // 字典code
      },
      {
        prop: 'tempCompanyFlag',
        label: () => _this.$t('vendorMod.tempCompanyFlag'), // 是否临时供应商
        width: 130,
        dataType: 'dict', // 数据类型为字典
        code: 'YES_OR_NO' // 字典code
      },
      {
        prop: 'warningStatus',
        label: () => _this.$t('vendorMod.warningStatus'), // 预警状态
        width: 100,
        dataType: 'dict', // 数据类型为字典
        code: 'WARNING_STATUS' // 字典code
      },
      {
        prop: 'categoryLevel',
        label: () => _this.$t('vendorMod.categoryLevel'), // 品类等级
        width: 100,
        dataType: 'dict', // 数据类型为字典
        code: 'VENDOR_LEVEL' // 字典code
      },
      {
        prop: 'serviceStatus',
        label: () => _this.$t('vendorMod.catServiceStatus'), // 品类状态
        width: 100,
        dataType: 'dict', // 数据类型为字典
        code: 'CATEGORY_STATUS' // 字典code
      },
      {
        prop: 'lastUpdateDate',
        label: () => _this.$t('common.updateTime'), // 更新时间
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      }
    ]
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    // 创建合同
    // createContractClick(row, spoce) {
    //   this.dialogFormVisible2 = false
    //   let rowId = null
    //   let contractOldCode = null
    //   let mainContractNo = null
    //   const contractType = 'MIAN_CONTRACT_ADD'
    //   this.$emit('tab-add', {
    //     component: contractInformation,
    //     params: {
    //       flag: 'add',
    //       rowId,
    //       contractType,
    //       contractOldCode,
    //       mainContractNo,
    //       isReadOnly: false
    //     },
    //     title: this.$t('contractMod.createContract'), // 创建合同
    //     name: 'contractInformation'
    //   })
    // },
    // 点击合同详情的合同名称跳转
    // contractNameClick(row, spoce) {
    //   const rowId = row.contractHeadId
    //   const contractType = 'MIAN_CONTRACT_ALTER'
    //   this.dialogFormVisible2 = false
    //   this.$emit('tab-add', {
    //     component: contractInformation,
    //     params: {
    //       flag: 'edit',
    //       rowId,
    //       contractType,
    //       // contractOldCode,
    //       // mainContractNo,
    //       isReadOnly: false
    //     },
    //     title: this.$t('contractMod.createContract'), // 创建合同
    //     name: 'contractInformation'
    //   })
    // },
    // 点击合同详情弹窗
    contractDetail (row) {
      this.contractData = [] // 清空
      const companyId = {
        companyId: row.companyId,
        categoryId: row.categoryId
      }
      this.dialogFormVisible2 = true
      supCommonApi.listContractDetail(companyId).then(res => {
        this.contractData = res.data
      })
    },
    downloadTemplate () {
      // 供应商组织与品类关系导入模板.xlsx
      downloadFileLink(
        '/api-sup/info/orgCategory/importModelDownload',
        this.$t('vendorMod.cooCateImpTemplate')
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
      })
    },
    handleSuccess () {
      this.getQuerydata()
    },
    syncFilterParams (values) {
      this.filterParams = values
    },
    getQuerydata (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    // 确认选中的品类
    comfirmSelect (node, scope) {
      scope.categoryId = node ? node.categoryId : null
      scope.categoryName = node ? node.categoryName : ''
      scope.categoryCode = node ? node.categoryCode : ''
    },
    deleteOne (val) {
      this.$confirm(this.$t('common.delRow'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {})
        .catch(() => {})
    },
    getLabel (dictionary = [], val) {
      const labelOpt = dictionary.find(i => i.value === val)
      if (labelOpt) return labelOpt.label
      return val
    },
    formatterStatus (row, column, cellValue, index) {
      return this.getLabel(this.catStatus, cellValue)
    },
    formatterStatus2 (row, column, cellValue, index) {
      return this.getLabel(this.formTypeList, cellValue)
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
    },
    handleCurrentChange (val) {
      this.currentRow = val
    }
  }
}
</script>
<style scoped lang="scss"></style>
