<template>
  <SrmDialog
    :title="readonly ? '意向金退款历史' : '意向金退款详情'"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <div class="header">
      <!-- <div class="header-title">
        意向金退款详情
      </div> -->
      <div class="header-introduce">
        <p>退款说明</p>
        <p>项目取消：①、任意环节因招标方原因取消。</p>
        <p>
          未推荐：①、前置交流项目，经技术交流后供应商未入围的②、项目公示阶段，内容变更进行二次公示，导致前期已报名供应商不满足要求的③、存在关联关系供应商同时参与项目，经沟通其中一方退出不参与的
          ④、供应商已缴纳意向金，但因各种原因未进行报名的
        </p>
      </div>
    </div>
    <el-table
      border
      stripe
      :data="tableData"
    >
      <el-table-column
        type="index"
        label="序号"
        fixed="left"
        width="60"
      />
      <el-table-column
        prop="vendorName"
        label="供应商名称"
        minWidth="150"
        show-overflow-tooltip
      />
      <el-table-column
        prop="refundBankAccount"
        label="退款账户"
        minWidth="150"
        show-overflow-tooltip
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <el-input v-if="!readonly" v-model="scope.row.refundBankAccount" />
          <span v-else>{{ scope.row.refundBankAccount }}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="refundBankAccountName"
        label="退款户名"
        minWidth="150"
        show-overflow-tooltip
        :render-header="_addStarToColumn"
      >
        <!-- <template v-slot="scope">
          <el-input v-if="!readonly" v-model="scope.row.refundBankAccountName" />
          <span v-else>{{ scope.row.refundBankAccountName }}</span>
        </template> -->
      </el-table-column>
      <el-table-column
        prop="refundBankName"
        label="退款银行"
        minWidth="150"
        show-overflow-tooltip
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <el-input v-if="!readonly" v-model="scope.row.refundBankName" />
          <span v-else>{{ scope.row.refundBankName }}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="refundBankNumber"
        label="退款银行联行号"
        minWidth="150"
        show-overflow-tooltip
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <el-input v-if="!readonly" v-model="scope.row.refundBankNumber" />
          <span v-else>{{ scope.row.refundBankNumber }}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="refundAmount"
        label="退款金额（元）"
        minWidth="150"
        show-overflow-tooltip
        :render-header="_addStarToColumn"
      >
        <!-- <template v-slot="scope">
          <el-input v-if="!readonly" v-model="scope.row.refundAmount" />
          <span v-else>{{ scope.row.refundAmount }}</span>
        </template> -->
      </el-table-column>
      <el-table-column
        prop="expectRefundTime"
        label="期望付款时间"
        minWidth="150"
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <el-date-picker
            v-if="!readonly"
            v-model="scope.row.expectRefundTime"
            type="date"
            value-format="yyyy-MM-dd"
            :picker-options="pickerOptions"
          />
          <span v-else>{{ scope.row.expectRefundTime }}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="refundReason"
        label="退款原因"
        minWidth="150"
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <DictSelect
            v-if="!readonly"
            v-model="scope.row.refundReason"
            code="INT_DEPOSIT_REFUND_REASON"
          />
          <span v-else>{{ $getDictLabel('INT_DEPOSIT_REFUND_REASON',scope.row.refundReason) }}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="refundRemark"
        label="备注"
        minWidth="150"
      >
        <template v-slot="scope">
          <el-input v-if="!readonly" v-model="scope.row.refundRemark" />
          <span v-else>{{ scope.row.refundRemark }}</span>
        </template>
      </el-table-column>
      <!--附件名称-->
      <SrmCommonFile
        type="table-column"
        :table-column-options="{
          label: '附件',
          prop: 'refundFileId',
          nameProp: 'refundFileName'
        }"
        :readonly="readonly"
        @on-change="fileChange"
      />
    </el-table>

    <div slot="footer" class="dialog-footer">
      <template v-if="!readonly">
        <el-button @click="dialogVisible = false">
          {{ $t("common.cancel") }}
        </el-button>
        <el-button type="primary" @click="handleConfirm">
          {{ $t("common.confirm") }}
        </el-button>
      </template>
      <template v-else>
        <el-button @click="dialogVisible = false">
          {{ $t("common.close") }}
        </el-button>
      </template>
    </div>
  </SrmDialog>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { transformMQL } from 'lib@/utils/util'
import souHttp from '../../../../api'

export default {
  name: 'IntentionDialog',
  components: {
    TableView,
    FormWrapper
  },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    readonly: {
      type: Boolean,
      default: false
    },
    form: {
      type: Object,
      default: () => {}
    },
    editRows: {
      type: Array,
      default () {
        return []
      }
    },
    // 两种模式 退款:quit,查看历史:history
    mode: {
      type: String,
      default: 'quit'
    }
  },
  data () {
    return {
      tableData: [],
      seletedRows: [],
      pickerOptions: {
        disabledDate: time => {
          const day = time.getDate()
          return !(day === 10 || day === 20 || day > 21)
        }
      }
    }
  },
  computed: {
    dialogVisible: {
      get: function () {
        return this.visible
      },
      set: function (val) {
        this.$emit('update:visible', val)
      }
    }
  },
  watch: {
    visible: {
      handler (nVal) {
        if (nVal) {
          if (this.mode === 'quit') {
            this.getFormDetail()
          } else {
            this.getFormHistory()
          }
        }
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    handleConfirm () {
      if (!this.tableData || !this.tableData.length) return
      for (let item of this.tableData) {
        if (!item.refundReason) {
          this.$message.warning('请选择退款原因')
          return
        }
      }
      this.$emit('confirm', this.tableData)
    },
    async getFormDetail () { // applyId
      let idList = this.editRows.map(item => item.applyId)
      let transformParams = transformMQL.save('SouReqApplyBuyer', {
        filter: {
          applyId: {
            in: idList
          }
        }
      }, 'query')
      const response = await souHttp.applyDepositQuery(transformParams)
      if (response) {
        this.tableData = response.data.records.map(item => {
          const {
            vendorBankAccount,
            vendorBankAccountName,
            vendorBankName,
            vendorBankNumber,
            ...rest
          } = item
          return {
            ...rest,
            refundBankAccount: vendorBankAccount,
            refundBankAccountName: vendorBankAccountName,
            refundBankName: vendorBankName,
            refundBankNumber: vendorBankNumber,
            refundAmount: this.form.depositAmount
          }
        })
      }
    },
    async getFormHistory () {
      let transformParams = transformMQL.save('SouIntDepositRefundBuyer', {
        filter: {
          reqHeadId: {
            eq: this.form.reqHeadId
          }
        }
      }, 'query')
      const response = await souHttp.refundQuery(transformParams)
      if (response) {
        this.tableData = response.data.records
      }
    },
    /* 文件变更 */
    fileChange ({ file, $index }) {
      const { fileId = '', fileName = '' } = file || {}
      this.tableData[$index].refundFileId = fileId
      this.tableData[$index].refundFileName = fileName
    }
  }
}
</script>
