<template>
  <el-container class="quotaflowEdit" direction="vertical">
    <el-main>
      <CWorkflowMulti
        ref="workflowMulti"
        v-model="activeTabName"
        :fun-params="workflowParamsInfo"
        :button-config-info="buttonConfigInfo"
        @tab-click="workflowView"
        @workflow-handler="workflowHandler"
        @click-handler="(type) => save(type)"
        @submit-direct="(type) => save(type)"
        @confirm="(type, comment) => save(type, comment)"
        @close-tab="cancelBill"
      >
        <div class="form-container">
          <el-form ref="form" :model="form" :rules="rules">
            <srm-row>
              <!-- 审批单号 -->
              <srm-col>
                <el-form-item prop="flowCode" :label="$t('supplierRating.approvalNumber')">
                  <el-input v-model="form.flowCode" disabled />
                </el-form-item>
              </srm-col>
              <!-- 创建人 -->
              <srm-col>
                <el-form-item prop="createdBy" :label="$t('purchaseDemand.createdBy1')">
                  <el-input v-model="form.createdBy" disabled />
                </el-form-item>
              </srm-col>
              <!-- 单据状态 -->
              <srm-col>
                <el-form-item
                  prop="approveStatus"
                  :label="$t('purSettlementMod.paymentPlanStatus')"
                >
                  <el-input v-model="approveStatus" disabled />
                </el-form-item>
              </srm-col>
              <!-- 创建时间 -->
              <srm-col>
                <el-form-item prop="creationDate" :label="$t('purchaseDemand.creationDate')">
                  <el-date-picker
                    v-model="form.creationDate"
                    :format="$formatDatePickerTime"
                    value-format="yyyy-MM-dd HH:mm:ss"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <!-- 审批时间 -->
              <srm-col>
                <el-form-item prop="creationDate" :label="$t('supplierRating.approvalTime')">
                  <el-date-picker
                    v-model="form.creationDate"
                    :format="$formatDatePickerTime"
                    value-format="yyyy-MM-dd HH:mm:ss"
                    disabled
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-form>

          <TableView
            :ref="gridId"
            :table-header="tableHeader"
            :check-change="handleCurrentChange"
            :page-size="pageSize"
            :pre-query-data="queryParam"
            :open-custom-table="false"
            :source="quotaApproveApi.detailFlowPage"
          />
        </div>
      </CWorkflowMulti>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import WorkflowCommon from '@/library/mixins/workflow-common'
import { quotaApproveApi } from 'modb@/quotaManagement/api/quotaApi'

export default {
  name: 'QuotaflowEdit',
  components: {
    TableView
  },
  mixins: [tabTodoMixin, WorkflowCommon],
  data () {
    return {
      quotaApproveApi: quotaApproveApi,
      pageSize: 15,
      currentRows: [],
      gridId: 'clickDetail',
      tableHeader: [
        {
          prop: 'orgName',
          label: this.$t('purSettlementMod.fullPathId'),
          width: 100
        },
        {
          prop: 'itemCode',
          label: this.$t('materialMainData.materialCode'),
          width: 100
        },
        {
          prop: 'itemName',
          label: this.$t('materialMainData.materialName'),
          width: 100
        },
        {
          prop: 'categoryName',
          label: this.$t('materialMainData.categoryName'),
          width: 100
        },
        {
          prop: 'unitName',
          label: this.$t('materialMainData.unit'),
          width: 100
        },
        {
          prop: 'startDate',
          label: this.$t('purchaseDemand.startDate'),
          showType: 'date',
          formatter: (val) => (this.$parseTime(val) || null),
          editable: (row) => row.editable,
          width: 100
        },
        {
          prop: 'endDate',
          label: this.$t('purchaseDemand.endDate'),
          showType: 'date',
          formatter: (val) => (this.$parseTime(val) || null),
          editable: (row) => row.editable,
          width: 100
        },
        {
          prop: 'companyName',
          label: this.$t('supplierCapacityReport.vendorName'),
          width: 120
        },
        {
          prop: 'presetPercent',
          label: this.$t('quota.presetScale'),
          showType: 'input',
          editable: (row) => row.editable,
          width: 100
        },
        {
          prop: 'quotaCeilLimitPercent',
          label: this.$t('quota.quotaCeilLimitPercent'),
          showType: 'input',
          editable: (row) => row.editable,
          width: 100
        },
        {
          prop: 'treatyPercent',
          label: this.$t('quota.agreementRatio'),
          showType: 'input',
          editable: (row) => row.editable,
          width: 100
        },
        {
          prop: 'mouldPercent',
          label: this.$t('quota.mouldPercent'),
          width: 100
        },
        {
          prop: 'suggestQuotaPercent',
          label: this.$t('quota.suggestQuotaPercent'),
          width: 100
        },
        {
          prop: 'quotaPercent',
          label: this.$t('quota.quotaPercent'),
          showType: 'input',
          editable: (row) => row.editable,
          width: 100
        },
        {
          prop: 'actualQuotaPercent',
          label: this.$t('quota.actualQuotaPercent'),
          width: 100
        },
        {
          prop: 'quotaOffset',
          label: this.$t('quota.quotaOffset'),
          width: 100
        },
        {
          prop: 'quantityPerMonth',
          label: this.$t('common.estimatedMonthlyConsumption'),
          showType: 'input',
          editable: (row) => row.editable,
          width: 120
        },
        {
          prop: 'flowCode',
          label: this.$t('bidMod.billCode'),
          width: 100
        }
      ],
      form: {
        quotaFlowId: null,
        flowCode: null,
        approveStatus: null,
        approveExplain: null,
        remark: null,
        createdBy: null,
        creationDate: null,
        lastUpdatedBy: null
      },
      approveStatus: null,
      rules: {},
      isReadonly: false,
      queryParam: {
        id: ''
      }
    }
  },
  computed: {
    viewUpdateButton () {
      return !this.isReadonly && !['APPROVAL', 'DESTORY'].includes(this.form.approveStatus)
    },
    disabledUpdateButton () {
      return this.form.approveStatus === 'SUBMITTED' || this.form.approveStatus === 'APPROVAL' || this.form.approveStatus === 'DESTORY'
    },
    workflowBusinessId () {
      return this.form ? this.form.quotaFlowId : null
    },
    workflowTabDisabled () {
      return false
    }
  },
  watch: {
    viewUpdateButton () {
      // this.buttonConfigInfo.save.view = this.viewUpdateButton;
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
    },
    // approveStatus
    form: {
      handler () {
        const approveStatus = this.form.approveStatus
        switch (approveStatus) {
          case 'DRAFT':
            this.approveStatus = this.$t('vendorMod.DRAFT')  // '拟定'
            break
          case 'CALCING':
            this.approveStatus = this.$t('hierarchical.Calculating')  // '计算中'
            break
          case 'CALCED':
            this.approveStatus = this.$t('hierarchical.Calculated')  // '已计算'
            break
          case 'TOBEAPPROVED':
            this.approveStatus = this.$t('flowMod.queryTodoCurrent')  // '待审批'
            break
          case 'APPROVAL':
            this.approveStatus = this.$t('dataConfMod.passed')  // '已通过'
            break
          case 'REJECTED':
            this.approveStatus = this.$t('dataConfMod.rejected')  // '已驳回'
            break
          default:
            this.approveStatus = ''
            break
        }
      },
      deep: true,
      immediate: true
    }
  },
  created () {
    const { flag, row, isReadonly = false } = this.$attrs.params
    this.isReadonly = isReadonly
    this.queryParam.id = row.quotaFlowId
    if (flag === 'edit') {
      this.getOrderDetail(this.queryParam.id)
    }

    this.buttonConfigInfo.save.view = false
    this.buttonConfigInfo.submit.view = this.viewUpdateButton
    this.buttonConfigInfo.cancel.view = !this.isReadOnly
    this.buttonConfigInfo.close.view = this.isReadOnly
  },
  mounted () {
    this.$nextTick(() => {
      this.$refs[this.gridId].query()
    })
  },
  methods: {
    getOrderDetail (id) {
      this.$http({
        url: '/api-sup/sup/quotaflow/get',
        method: 'get',
        params: { id },
        loading: true
      }).then(res => {
        this.form = res.data
      }).catch(err => {
        console.log(err)
      })
    },
    async getWorkflowBusinessType () {
      return 'QUOTAFLOW'
    },
    getCWorkflowRefName () {
      return 'workflowMulti' // 对应CWorkflowMulti标签中的ref
    },
    handleCurrentChange (val) {
      this.currentRows = val
    },
    async save (type) {
      const { flag, row } = this.$attrs.params
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
      if (flag === 'edit') {
        this.form = row
      }
      await this.handlerAfter(type)
    },
    cancelBill () {
      const { flag, row } = this.$attrs.params
      if (flag === 'add') {
        this.$emit('tab-remove', 'quotaflowEdit')
      } else {
        this.$emit('tab-remove', 'quotaflowEdit' + row.flowCode)
      }
      this.__setTabTodo('quotaflowList.getQuerydata')
    }
  }
}
</script>
<style scoped lang="scss">
.quotaflowEdit {
  height: 100%;
}
</style>
