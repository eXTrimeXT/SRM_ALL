// by-zhaomz1
<template>
  <el-container
    class="flex-container the_dictionary_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        :pre-form-obj="preFormObj"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />
      <MainHeader>
        <template slot="left">
          <ExportExcel
            page-url="/api-sup/info/vendorInformation/listPageByDTO"
            :filterParams="queryParam"
            :table-header="tableHeader"
            :dictCodes="dictCodes"
            :timeout="1000000"
            export-mode="front"
          />
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
        url="/api-sup/info/vendorInformation/listPageByDTO"
      />
      <srm-dialog
        :title="$t('vendorMod.contractDetails')"
        :visible.sync="dialogFormVisible"
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
import vendorProfileDetailRead from './vendorProfileDetailRead'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import contractInformation from 'modb@/contractManagement/views/contractManager/edit'
import ExportExcel from 'lib@/components/export-excel'
import nonQuaOfReviewDetail from 'modb@/vendorManagementBuyer/views/nonQuaOfReview/quaOfReviewDetail'
import quaOfReviewDetail from 'modb@/vendorManagementBuyer/views/quaOfReview/quaOfReviewDetail'
import { vendorProfileApi } from 'modb@/vendorManagementBuyer/api/vendorManagement'
import { supCommonApi } from 'modb@/vendorManagementBuyer/api/supApi'

export default {
  name: 'VendorProfileList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel
  },
  provide () {
    return { context: this }
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      contractData: [], // 合同详情的列表
      tableName: 'vendorProfileList',
      defaultTableHeader: [],
      name: '',
      reviewFormNumber: '',
      gridData: [],
      pageSize: 15,
      gridId: 'vendorProfileList',
      selectList: [],
      preFormObj: {},
      currentRow: null,
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      statusList: [],
      isModify: false,
      dialogFormVisible: false,
      formLabelWidth: '100px',
      queryForm: [],
      queryParam: {},
      filterParams: {},
      dictCodes: {
        overseasRelation: 'RELATION',
        companyType: 'COMPANY_NATURE',
        status: 'SUPPLIER_LIST_STATUS',
        dataSources: 'DATA_SOURCE',
        isBacklist: 'YES_OR_NO',
        quitFlag: 'YES_OR_NO',
        supplierType: 'SUPPLIER_TYPE'
      }
    }
  },
  watch: {
    $route: {
      // 当前功能已经打开的时候监听是否是从其他功能跳转过来的
      deep: true,
      immediate: true,
      handler () {
        let row = this.$route.params.dataResources
        console.log('row:::', row)
        if (row && row.companyId && row.companyName) {
          this.editTab('view', row)
        }
      }
    }
  },
  created () {
    let _this = this
    _this.queryForm = [
      {
        prop: 'companyName',
        label: () => this.$t('common.vendorName'), // '供应商名称'
        type: 'quicksearch',
        showKey: 'companyName',
        name: 'scc_sup_company_info_all'
      },
      {
        prop: 'lcCode',
        label: () => this.$t('vendorMod.lcCode') // '社会统一信用代码'
      },
      {
        prop: 'isBacklist',
        label: () => this.$t('vendorMod.isBacklist'), // '是否黑名单'
        type: 'dict',
        code: 'YES_OR_NO'
      },
      {
        prop: 'overseasRelation',
        label: () => this.$t('vendorMod.overseasRelation'), // '境内外关系'
        type: 'dict',
        code: 'RELATION'
      },
      {
        prop: 'companyType',
        label: () => this.$t('vendorMod.companyType'), // '企业性质'
        type: 'dict',
        code: 'COMPANY_NATURE'
      },
      {
        prop: 'potentialFlag',
        label: () => this.$t('vendorMod.potentialSupplier'), // '是否潜在供应商'
        type: 'dict',
        code: 'YES_OR_NO'
      },
      {
        prop: 'dataSources',
        label: () => this.$t('vendorMod.dataSources'), // '数据来源'
        type: 'dict',
        code: 'DATA_SOURCE'
      },
      {
        prop: 'status',
        label: () => this.$t('vendorMod.registerStatus'), // '注册状态'
        type: 'dict',
        code: 'SUPPLIER_LIST_STATUS_vendorProfileList'
      },
      {
        prop: 'supplierType',
        label: () => _this.$t('supplierRating.supplierType'), // '供应商类型'
        type: 'dict', // 数据类型为字典
        code: 'SUPPLIER_TYPE' // 字典code
      },
      {
        prop: 'legalPerson',
        label: () => this.$t('vendorMod.legalPerson') // '法定代表人'
      },
      {
        prop: 'dateList',
        label: () => this.$t('vendorMod.permitDate'), // '准入日期'
        type: 'daterange'
      }
    ]
    _this.tableHeader = [
      {
        prop: 'companyCode',
        label: () => this.$t('common.vendorCode'), // '供应商编码'
        width: 120
      },
      {
        prop: 'companyName',
        label: () => this.$t('common.vendorName'), // '供应商名称'
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.editTab('view', row)
        }.bind(this)
      },
      {
        prop: 'supplierType',
        label: () => _this.$t('supplierRating.supplierType'), // '供应商类型'
        width: 120,
        dataType: 'dict', // 数据类型为字典
        code: 'SUPPLIER_TYPE' // 字典code
      },
      {
        prop: 'overseasRelation',
        label: () => this.$t('vendorMod.overseasRelation'), // '境内外关系'
        width: 150,
        dataType: 'dict',
        code: 'RELATION'
      },
      {
        prop: 'companyType',
        label: () => this.$t('vendorMod.companyType'), // '企业性质'
        width: 120,
        dataType: 'dict',
        code: 'COMPANY_NATURE',
        formattor (val, row) {
          if (row.overseasRelation === 'OUT') {
            return ''
          } else {
            return _this.$getDictLabel('COMPANY_NATURE', val)
          }
        }
      },
      {
        prop: 'lcCode',
        label: () => this.$t('vendorMod.lcCode'), // '社会统一信用代码'
        minWidth: 150,
        formattor (val, row) {
          if (row.overseasRelation === 'OUT') {
            return ''
          } else {
            return val
          }
        }
      },
      {
        prop: 'legalPerson',
        label: () => this.$t('vendorMod.legalPerson'), // '法定代表人'
        width: 110
      },
      {
        prop: 'isBacklist',
        label: () => this.$t('vendorMod.isBacklist'), // '是否黑名单'
        width: 110,
        dataType: 'dict',
        code: 'YES_OR_NO'
      },
      {
        prop: 'potentialFlag',
        label: () => this.$t('vendorMod.potentialSupplier'), // '是否潜在供应商'
        width: 150,
        dataType: 'dict',
        code: 'YES_OR_NO'
      },
      {
        prop: 'status',
        label: () => this.$t('vendorMod.registerStatus'), // '审批状态'
        width: 100,
        dataType: 'dict',
        code: 'SUPPLIER_LIST_STATUS'
      },
      {
        prop: 'approvedDate',
        label: () => this.$t('vendorMod.permitDate'), // '准入日期'
        width: 120,
        dataType: 'dateTime'
      },
      {
        prop: 'dataSources',
        label: () => this.$t('vendorMod.dataSources'), // '数据来源'
        minWidth: 150,
        dataType: 'dict',
        code: 'DATA_SOURCE'
      },
      {
        prop: 'forzenFlag',
        label: () => this.$t('vendorMod.forzenFlag'), // '是否已冻结'
        minWidth: 110,
        dataType: 'dict',
        code: 'YES_OR_NO'
      },
      {
        prop: 'quitFlag',
        label: () => this.$t('bidMod.quitFlag'), // '是否已退出'
        minWidth: 110,
        dataType: 'dict',
        code: 'YES_OR_NO'
      },
      {
        prop: 'operation',
        label: () => this.$t('common.operation'), // 操作
        width: 200,
        btnStyle: 'text',
        fixed: 'right',
        showType: 'buttons',
        buttons: [
          {
            callback: function (row) {
              this.doApprovalPass(row)
            }.bind(this),
            code: 'sup:vendorProfileList:doApprovalPass',
            formattor (val) {
              return _this.$t('purchaseDemand.confirm') // 审核通过
            },
            show: row => row.status === 'SUBMITTED' && row.dataSources !== 'MANUALLY_CREATE'
          },
          {
            callback: function (row) {
              this.delRowData(row)
            }.bind(this),
            code: 'sup:vendorProfileList:delRowData',
            formattor (val) {
              return _this.$t('purchaseDemand.refuse') // 驳回
            },
            show: row => row.status === 'SUBMITTED' && row.dataSources !== 'MANUALLY_CREATE'
          },
          {
            callback: function (row) {
              this.createdQuaofReview(row)
            }.bind(this),
            code: 'sup:vendorProfileList:createdQuaofReview',
            formattor (val) {
              return _this.$t('vendorMod.createQua') // 创建资质审查
            },
            show: row => row.status === 'APPROVED' && row.isBacklist === 'N' // '已经注册且不在黑名单'
          },
          {
            callback: function (row) {
              this.createdQuestionnaire(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('quest.createdQuestionnaire') // 创建调查表
            },
            show: row => (row.status === 'APPROVED' || row.status === 'SUBMITTED') && row.isBacklist === 'N'
          }
        ]
      }
    ]
    this.defaultTableHeader = _this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    // 创建调查表
    createdQuestionnaire (row) {
      this.$router.push('/vendorManagement/questManagement')
    },
    // 创建合同
    createContractClick (row, spoce) {
      this.dialogFormVisible = false
      let rowId = null
      let contractOldCode = null
      let mainContractNo = null
      const contractType = 'MIAN_CONTRACT_ADD'
      this.$emit('tab-add', {
        component: contractInformation,
        params: {
          flag: 'add',
          rowId,
          contractType,
          contractOldCode,
          mainContractNo,
          isReadOnly: false
        },
        title: this.$t('contractMod.createContract'), // 创建合同
        name: 'contractInformation'
      })
    },
    // 点击合同详情的合同名称跳转
    contractNameClick (row, spoce) {
      const rowId = row.contractHeadId
      const contractType = 'MIAN_CONTRACT_ALTER'
      this.dialogFormVisible = false
      this.$emit('tab-add', {
        component: contractInformation,
        params: {
          flag: 'add',
          rowId,
          contractType,
          isReadOnly: false
        },
        title: this.$t('contractMod.createContract'), // 创建合同
        name: 'contractInformation'
      })
    },
    // 点击合同详情弹窗
    contractDetail (row) {
      const companyId = { companyId: row.companyId }
      this.dialogFormVisible = true
      supCommonApi.listContractDetail(companyId).then(res => {
        this.contractData = res.data
      })
    },
    getQuerydata (v) {
      if (v && v.dateList) {
        v.startDate = v.dateList[0]
        v.endDate = v.dateList[1]
      } else if (v && !v.dateList) {
        delete v.startDate
        delete v.endDate
      }
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    syncFilterParams (values) {
      this.queryParam = values
      this.filterParams = values
    },
    // 驳回
    delRowData (row) {
      // '驳回原因'
      this.$prompt('', this.$t('vendorMod.rejectReason'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('components.common.cancel'),
        inputType: 'textarea'
      }).then(({ value }) => {
        let obj = {
            companyId: row.companyId,
            flowRemark: value
          }
        vendorProfileApi.companyGreenChannelDeleteNotDelUser(obj).then(res => {
          this.$message({
            message: res.message,
            type: 'success'
          })
          this.$refs[this.gridId].query()
        })
      })
    },
    doApprovalPass (row) {
      vendorProfileApi.vendorInformationApprove({ companyId: row.companyId })
        .then(data => {
          this.getQuerydata()
        })
        .catch(err => {
          console.log(err)
        })
    },
    createdQuaofReview (row) {
      if (row.supplierType === 'NO_MATERIAL') {
        let row2 = {
          vendorId: row.companyId,
          vendorCode: row.companyCode,
          vendorName: row.companyName
        }
        let tab = {
          component: nonQuaOfReviewDetail,
          params: {
            flag: 'add',
            row: row2,
            tabName: 'quaOfReviewDetail'
          },
          title: () => this.$t('vendorMod.noAddQua'), // '资质审查新增',
          name: 'quaOfReviewDetail'
        }
        this.$emit('tab-add', tab)
      } else {
        let row2 = {
          vendorId: row.companyId,
          vendorCode: row.companyCode,
          vendorName: row.companyName
        }
        let tab = {
          component: quaOfReviewDetail,
          params: {
            flag: 'add',
            row: row2,
            tabName: 'quaOfReviewDetail'
          },
          title: () => this.$t('vendorMod.addQua'), // '资质审查新增',
          name: 'quaOfReviewDetail'
        }
        this.$emit('tab-add', tab)
      }
    },
    // 编辑编辑tab
    editTab (type, row) {
      if (type === 'view') {
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
      } else if (type === 'edit') {
        this.$emit('tab-add', {
          component: vendorProfileDetailRead,
          params: {
            flag: 'edit',
            companyId: row.companyId,
            tabName: 'vendorProfileDetailRead' + row.companyName
          },
          title: row.companyName,
          name: 'vendorProfileDetailRead' + row.companyName
        })
      }
    },
    importOne () {},
    exportOne () {},
    handleCurrentChange (val) {
      this.currentRow = val
    }
  }
}
</script>
<style scoped lang="scss"></style>
