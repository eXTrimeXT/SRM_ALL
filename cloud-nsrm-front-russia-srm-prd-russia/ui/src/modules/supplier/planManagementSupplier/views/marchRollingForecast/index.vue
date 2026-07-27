<template>
  <el-container
    class="flex-container-notab the_marchRollingForecast_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        :select-dictionary="selectDictionary"
        :col-length="4"
        @getFormData="getQuerydata"
      />
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <el-button
            type="primary"
            @click="confirm()"
          >
            {{ $t('common.affirm') }}
          </el-button>
          <el-button
            @click="reject()"
          >
            {{ $t('common.toRefuse') }}
          </el-button>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :checkbox="true"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-sup-ce/plan/orderForecast/listPage"
        @afterQuery="getMonthLabel"
      />
    </el-main>
    <!-- 驳回原因 -->
    <srm-dialog
      :title="$t('purSettlementMod.reasonForRejection')"
      :visible.sync="isRejectResult"
      :modal-append-to-body="false"
      size="middle"
      style="text-align: center"
    >
      <el-input
        v-model="rejectReason"
        type="textarea"
        :rows="4"
        :placeholder="$t('purSettlementMod.pleaseFillReasonForRejection')"
      />
      <div class="topComment">
        <el-button @click="isRejectResult = false">
          {{ $t('common.cancel') }}
        </el-button>
        <el-button
          type="primary"
          @click="confirmReject"
        >
          {{ $t('common.confirm') }}
        </el-button>
      </div>
    </srm-dialog>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { adaptDictData } from '@/utils'
import { getDictItemList } from '@/api/common'
import { downloadFileLinkByPost } from 'lib@/utils/file'
import OrganizationSelector from 'lib@/components/organization-selector'
import { orderForecastSupplierApi } from 'mods@/planManagementSupplier/api'

export default {
  name: 'MarchRollingForecast',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    OrganizationSelector
  },
  provide () {
    return { context: this }
  },
  data () {
    return {
      rejectReason: '', // 驳回原因描述
      rejectOrderId: '', // 选择驳回的ID
      isRejectResult: false,
      name: '',
      tableName: 'marchRollingForecast_vendor',
      reviewFormNumber: '',
      selectDictionary: {},
      gridData: [],
      pageSize: 15,
      gridId: 'list',
      selectList: [],
      currentRows: [],
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      statusList: [],
      form: {
        id: '',
        vendorCode: '',
        vendorCompanyName: '',
        reviewFormNumber: '',
        enabled: ''
      },
      rules: {
        vendorCode: [{ required: true, message: this.$t('bidMod.msgDictCode') }], // 请输入字典编码
        vendorCompanyName: [{ required: true, message: this.$t('bidMod.msgDictName') }] // 请输入字典名称
      },
      isModify: false,
      dialogFormVisible: false,
      formLabelWidth: '100px',
      preArr: [
        {
          prop: 'categoryId',
          label: () => this.$t('purchaseDemand.materialCateSub'), // 物料小类
          type: 'quicksearch',
          showKey: 'categoryName',
          propKey: 'categoryId',
          name: 'scc_base_purchase_category4'
        },
        {
          prop: 'materialId',
          label: this.$t('common.materialCode'), // 物料编码
          type: 'quicksearch',
          showKey: 'materialId',
          name: 'scc_base_material_item'
        },
        {
          prop: 'planMonth',
          label: this.$t('planMod.planMonth'),
          type: 'month'
          // rules: [{required: true, message: '请输入计划月', trigger: 'blur' }]
        }, // 计划月
        { prop: 'status', label: this.$t('common.status'), type: 'select' }, // 状态
        {
          prop: 'orgId',
          label: () => this.$t('oneStopShopping.businessEntity'), // 业务实体
          type: 'OUorganizationSelector'
        },
        {
          prop: 'organizationId',
          parentId: 'orgId',
          label: () => this.$t('purchaseDemand.invOrg'),
          type: 'INVorganizationSelector' // 库存组织
        }
      ],
      queryParam: {}
    }
  },
  created () {
    var _this = this
    this.tableHeader = [
      { prop: 'planMonth', label: this.$t('planMod.planMonth'), minWidth: 100 }, // 计划月
      {
        prop: 'orgId',
        label: this.$t('purchaseDemand.businessEntity'), // 业务实体
        minWidth: 120,
        addStarToColumn: true,
        formattor: (prop, row) => {
          return row.orgName
        }
      },
      {
        prop: 'organizationId',
        label: this.$t('purchaseDemand.invOrg'), // 库存组织
        minWidth: 120,
        addStarToColumn: true,
        formattor: (prop, row) => {
          return row.organizationName
        }
      },
      {
        prop: 'materialCode',
        label: this.$t('common.materialCode'),
        minWidth: 120,
        addStarToColumn: true
      }, // 物料编码
      {
        prop: 'materialName',
        label: this.$t('common.materialName'),
        minWidth: 100
      }, // 物料名称
      {
        prop: 'vendorCode',
        label: this.$t('common.vendorCode'),
        width: 150,
        addStarToColumn: true
      }, // 供应商编码
      {
        prop: 'vendorName',
        label: this.$t('common.vendorName'),
        width: 180
      }, // 供应商名称
      {
        prop: 'lastMonthPlan',
        label: this.$t('planMod.lastMonthPlan'), // 上月计划
        minWidth: 360,
        children: [
          {
            prop: 'lastMonthFirst',
            label: this.$t('planMod.currentMonthAmount'), // 本月预计
            minWidth: 120,
            formattor: (label, row) => {
              return label ? '(' + row.lastPlanFirstMonthLabel + this.$t('planMod.monthUnit') + ')' + label : ''
            }
          },
          {
            prop: 'lastMonthSecond',
            label: this.$t('planMod.nextMonthAmount'), // 下月预计
            minWidth: 120,
            formattor: (label, row) => {
              return label ? '(' + row.lastPlanSecondMonthLabel + this.$t('planMod.monthUnit') + ')' + label : ''
            }
          },
          {
            prop: 'lastMonthThree',
            label: this.$t('planMod.afterNextMonthAmount'), // 后月预计
            minWidth: 120,
            formattor: (label, row) => {
              return label ? '(' + row.lastPlanThreeMonthLabel + this.$t('planMod.monthUnit') + ')' + label : ''
            }
          }
        ]
      },
      {
        prop: 'currentMonthPlan',
        label: this.$t('planMod.currentMonthPlan'), // 上月计划
        minWidth: 480,
        children: [
          {
            prop: 'firstMonth',
            label: this.$t('planMod.firstMonthAfterPlan'), // 计划月后第一个月
            minWidth: 160,
            addStarToColumn: true,
            formattor: (label, row) => {
              return label ? '(' + row.firstMonthLabel + this.$t('planMod.monthUnit') + ')' + label : ''
            }
          },
          {
            prop: 'secondMonth',
            label: this.$t('planMod.secondMonthAfterPlan'), // 计划月后第二个月
            minWidth: 160,
            addStarToColumn: true,
            formattor: (label, row) => {
              return label ? '(' + row.secondMonthLabel + this.$t('planMod.monthUnit') + ')' + label : ''
            }
          },
          {
            prop: 'threeMonth',
            label: this.$t('planMod.thirdMonthAfterPlan'), // 计划月后第三个月
            minWidth: 160,
            addStarToColumn: true,
            formattor: (label, row) => {
              return label ? '(' + row.threeMonthLabel + this.$t('planMod.monthUnit') + ')' + label : ''
            }
          }
        ]
      },
      {
        prop: 'actualDeliveryQuantity',
        label: this.$t('planMod.actualDeliveryQuantity'), // 送退货数量
        minWidth: 150
      },
      {
        prop: 'deviation',
        label: this.$t('planMod.deviation'), // 偏差
        minWidth: 150
      },
      { prop: 'unit', label: this.$t('bid_mod.unit'), width: 100 }, // 单位
      {
        prop: 'status',
        label: this.$t('purchaseDemand.applyStatus'), // 单据状态
        width: 100,
        formattor: val => this.getLabel(this.orderForecastStatusOpts, val)
      },
      { prop: 'categoryName', label: this.$t('purchaseDemand.materialCateSub'), width: 100 }, // 物料小类
      {
        prop: 'creationDate',
        label: this.$t('common.creationTime'),
        width: 100
      },
      {
        prop: 'createdFullName',
        label: this.$t('common.creator'),
        width: 100
      },
      {
        prop: 'operation',
        label: () => this.$t('common.operation'), // 操作
        width: 120,
        btnStyle: 'text',
        fixed: 'right',
        showType: 'buttons',
        class: 'fixed-column',
        buttons: [
          {
            callback: function (row) {
              const ids = []
              ids.push(row.orderForecastId)
              this.confirm(ids)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.affirm') // '通过'
            },
            show: function (row) {
              if (row.status === 'SUBMIT') {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              const ids = []
              ids.push(row.orderForecastId)
              this.isRejectResult = true
              this.rejectReason = ''
              this.rejectOrderId = row.orderForecastId
            }.bind(this),
            formattor (val) {
              return _this.$t('common.toRefuse') // '驳回'
            },
            show: function (row) {
              if (row.status === 'SUBMIT') {
                return true
              } else {
                return false
              }
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
  mounted () {
    this.fetchDictionary()
  },
  methods: {
    selectOrgIdHandler (node) {
      this.$set(this.queryParam, 'orgId', node.organizationId)
      this.$set(this.queryParam, 'orgCode', node.organizationCode)
      this.$set(this.queryParam, 'orgName', node.organizationName)
      this.$set(this.queryParam, 'organizationId', node.childOrganRelation[0].organizationId)
      this.$set(this.queryParam, 'organizationCode', node.childOrganRelation[0].organizationCode)
      this.$set(this.queryParam, 'organizationName', node.childOrganRelation[0].organizationName)
    },
    selectOrganizationHandler (node) {
      if (node) {
        Object.assign(this.queryParam, {
          organizationId: node.organizationId,
          organizationCode: node.organizationCode,
          organizationName: node.organizationName
        })
      }
    },
    getLabel (dictionary = [], val) {
      const labelOpt = dictionary.find(i => i.value === val)
      if (labelOpt) return labelOpt.label
      return val
    },
    fetchDictionary () {
      const dictionaryCodes = ['ORDER_FORECAST_STATUS'].map(i => ({
        dictCode: i
      }))
      getDictItemList(dictionaryCodes).then(res => {
        const [ORDER_FORECAST_STATUS] = res.data
        this.orderForecastStatusOpts = adaptDictData(ORDER_FORECAST_STATUS.ORDER_FORECAST_STATUS)
        this.selectDictionary = {
          status: this.orderForecastStatusOpts
        }
      })
    },
    getQuerydata (v) {
      this.queryParam = v || this.queryParam
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      this.currentRows = val
    },
    confirm (params) {
      if (!params) {
        const data = this.currentRows
        if (!data.length) {
          return this.$message({
            type: 'warning',
            message: this.$t('planMod.msgList[8]') //  请选择要确认的计划！
          })
        }
        const submitStaus = ['SUBMIT']
        if (this.currentRows.some(i => submitStaus.findIndex(j => j === i.status) === -1)) {
          this.$message({
            type: 'warning',
            message: this.$t('planMod.msgList[9]') // 只有状态为已提交状态的才能确认！
          })
          return
        }
        params = data.map(i => i.orderForecastId)
      }
      orderForecastSupplierApi.comfirmBatch(params).then(res => {
        this.$message({ message: res.message, type: 'success' })
        this.getQuerydata()
      })
    },
    confirmReject () {
      // 驳回确认区分行字样点击驳回和多选头部按钮驳回
      const params = !this.rejectOrderId
        ? {}
        : { rejectReason: this.rejectReason, ids: [this.rejectOrderId] }
      this.isReject(params)
    },
    async reject () {
      this.rejectOrderId = ''
      this.rejectReason = ''
      const data = this.currentRows
      if (!data.length) {
        return this.$message({
          type: 'warning',
          message: this.$t('planMod.msgList[10]') //  请选择要驳回的计划！
        })
      }
      const submitStaus = ['SUBMIT']
      if (this.currentRows.some(i => submitStaus.findIndex(j => j === i.status) === -1)) {
        this.$message({
          type: 'warning',
          message: this.$t('planMod.msgList[11]') // 只有状态为已发布状态的才能驳回！
        })
        return
      }
      this.isRejectResult = true
    },
    isReject (params = {}) {
      if (!params.ids) {
        // 表格上按钮点击拿选中行id
        params = {
          ids: this.currentRows.map(i => i.orderForecastId),
          rejectReason: this.rejectReason
        }
      }
      // 行点驳回拿传入的参数
      orderForecastSupplierApi.rejectBatch(params).then(res => {
        this.$message({ message: res.message, type: 'success' })
        this.getQuerydata()
        this.isRejectResult = false
      })
    },
    getMonthLabel (list) {
      list.forEach(row => {
        const currentMonth = parseInt(row.planMonth.split('-')[1])
        row.firstMonthLabel = currentMonth + 1 < 13 ? currentMonth + 1 : currentMonth - 11
        row.secondMonthLabel = currentMonth + 2 < 13 ? currentMonth + 2 : currentMonth - 10
        row.threeMonthLabel = currentMonth + 3 < 13 ? currentMonth + 3 : currentMonth - 9
        row.lastPlanFirstMonthLabel = currentMonth
        row.lastPlanSecondMonthLabel = currentMonth + 1 < 13 ? currentMonth + 1 : currentMonth - 11
        row.lastPlanThreeMonthLabel = currentMonth + 2 < 13 ? currentMonth + 2 : currentMonth - 10
      })
    }
  }
}
</script>
<style scoped lang="scss">
.the_marchRollingForecast_wrapper {
  .topComment {
    text-align: right;
    margin-top: 10px;
  }
}

</style>
