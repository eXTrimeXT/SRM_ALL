<template>
  <!-- 保证金管理 -->
  <div style="padding-top: 10px">
    <div style="margin-bottom:16px">
      <!-- 保证金扣款 -->
      <el-button
        type="primary"
        :disabled="selectedList.length==0"
        @click="openDeductDialog('edit')"
      >
        {{ $t('cusEntry.bidMod.deductM') }}
      </el-button>
      <!-- 保证金退回 -->
      <el-button
        v-if="showRefund"
        type="primary"
        :disabled="selectedList.length==0"
        @click="openRefundDialog('edit')"
      >
        {{ $t('cusEntry.bidMod.refundM') }}
      </el-button>
      <!-- 扣款历史 -->
      <el-button @click="openDeductDialog">
        {{ $t('cusEntry.bidMod.deductHis') }}
      </el-button>
      <!-- 退款历史 -->
      <el-button
        v-if="showRefund"
        @click="openRefundDialog"
      >
        {{ $t('cusEntry.bidMod.refundHis') }}
      </el-button>
    </div>

    <el-table
      border
      :data="bondList"
      style="width: 100%"
      @selection-change="handleSelectionChange"
    >
      <el-table-column
        align="center"
        type="selection"
        fixed="left"
        width="55"
      />
      <el-table-column
        align="center"
        type="index"
        :label="$t('common.sort')"
        fixed="left"
        width="50"
      />
      <el-table-column
        align="center"
        prop="vendorCode"
        :label="$t('common.vendorCode')"
        min-width="120"
      />
      <el-table-column
        align="center"
        prop="vendorName"
        :label="$t('common.vendorName')"
        min-width="120"
      />
      <!-- <el-table-column
        align="center"
        prop="payFlag"
        :label="$t('cusEntry.bidMod.payFlag')"
        :formatter="(row, column, cellValue) => $getDictLabel('YES_OR_NO', cellValue)"
        min-width="120"
      /> -->
      <el-table-column
        align="center"
        prop="marginStatus"
        label="缴纳状态"
        :formatter="(row, column, cellValue) => $getDictLabel('SOU_BID_MARGIN_STATUS', cellValue)"
        min-width="120"
      />
      <el-table-column
        align="center"
        prop="handerMode"
        label="缴纳处理"
        :formatter="(row, column, cellValue) => $getDictLabel('MARGIN_HANDER_MODE', cellValue)"
        min-width="120"
      />
      <el-table-column
        align="center"
        prop="causeDesc"
        label="原因说明"
        min-width="120"
      />
      <el-table-column
        align="center"
        prop="payVoucher"
        :label="$t('cusEntry.bidMod.payVoucher')"
        min-width="120"
      >
        <template slot-scope="scope">
          <SrmCommonFile
            :default-file="{
              fileId: scope.row.payVoucherFileId,
              fileName: scope.row.payVoucher
            }"
            readonly
          />
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        prop="payAccount"
        :label="$t('cusEntry.bidMod.payAccount')"
        min-width="120"
      />
      <el-table-column
        align="center"
        prop="payAccountName"
        :label="$t('cusEntry.bidMod.payAccountName')"
        min-width="150"
      />
      <el-table-column
        align="center"
        prop="payBank"
        :label="$t('cusEntry.bidMod.payBank')"
        min-width="150"
      />
      <el-table-column
        align="center"
        prop="bankLine"
        :label="$t('cusEntry.bidMod.bankNum')"
        min-width="150"
      />
      <el-table-column
        align="center"
        prop="payAmount"
        :label="$t('cusEntry.bidMod.payAmount')"
        min-width="150"
      />
      <el-table-column
        align="center"
        prop="chargeAmount"
        :label="$t('cusEntry.bidMod.chargeAmount')"
        min-width="150"
      />
      <el-table-column
        align="center"
        prop="refundAmount"
        :label="$t('cusEntry.bidMod.chargeAmount1')"
        min-width="150"
      />
      <el-table-column
        align="center"
        prop="yearFlag"
        :label="$t('cusEntry.bidMod.yearFlag')"
        :formatter="(row, column, cellValue) => $getDictLabel('YES_OR_NO', cellValue)"
        min-width="150"
      />
      <el-table-column
        align="center"
        prop="innerFlag"
        :label="$t('cusEntry.bidMod.innerFlag')"
        :formatter="(row, column, cellValue) => $getDictLabel('YES_OR_NO', cellValue)"
        min-width="150"
      />
      <!-- <el-table-column
        align="center"
        prop="noNeedPayFlag"
        :label="$t('cusEntry.bidMod.noNeedPayFlag')"
        :formatter="(row, column, cellValue) => $getDictLabel('YES_OR_NO', cellValue)"
        min-width="150"
      />
      <el-table-column
        align="center"
        prop="noPayCause"
        :label="$t('cusEntry.bidMod.noPayCause')"
        min-width="150"
      /> -->
      <el-table-column
        align="center"
        prop="refundStatus"
        :label="$t('cusEntry.bidMod.refundStatus')"
        :formatter="(row, column, cellValue) => $getDictLabel('SOU_INT_DEPOSIT_REFUND_STATUS', cellValue)"
        min-width="150"
      />
      <el-table-column
        align="center"
        prop="refundPaymentDate"
        :label="$t('cusEntry.bidMod.refundPaymentDate')"
        min-width="150"
      />
      <el-table-column
        align="center"
        prop="refundFailCause"
        :label="$t('cusEntry.bidMod.refundFailCause')"
        min-width="150"
      />
      <el-table-column
        align="center"
        :label="$t('common.operation')"
        fixed="right"
        width="150"
      >
        <template slot-scope="scope">
          <!-- <el-button
            type="text"
            @click="confirmNoPayCause(scope.row)"
          >
            {{ $t('cusEntry.bidMod.noPayFlag') }}
          </el-button> -->
          <!-- 未缴纳、待确认 && 未进行缴纳处理 展示缴纳处理按钮 -->
          <el-button
            v-if="['NOT_PAY', 'CONFIRM_TODO'].includes(scope.row.marginStatus) && !scope.row.handerMode"
            type="text"
            @click="handlerPay(scope.row)"
          >
            缴纳处理
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 保证金扣款 -->
    <srm-dialog
      v-if="deductVisible"
      size="xLarge"
      :visible.sync="deductVisible"
      :title="deductFlag ? $t('cusEntry.bidMod.deduct') : $t('cusEntry.bidMod.deductHistory')"
      append-to-body
      :close-on-click-modal="false"
    >
      <el-table
        :data="deductData"
        style="width: 100%"
        :height="deductFlag ? 240 : 380"
        border
      >
        <el-table-column
          align="center"
          type="index"
          :label="$t('common.sort')"
          width="50"
        />
        <el-table-column
          align="center"
          prop="vendorName"
          :label="$t('cusEntry.bidMod.vendorName')"
          min-width="120"
        />
        <el-table-column
          align="center"
          prop="ouOrgName"
          :label="$t('cusEntry.bidMod.ouOrgName')"
          min-width="120"
        />
        <el-table-column
          align="center"
          prop="amount"
          :label="$t('cusEntry.bidMod.chargeAmount')"
          :render-header="_addStarToColumn"
          min-width="120"
        >
          <template slot-scope="scope">
            <el-input-number
              v-if="deductFlag"
              v-model="scope.row.amount"
              style="width: 100%"
              :controls="false"
              :min="0"
            />
            <span v-else> {{ scope.row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          prop="description"
          :label="$t('cusEntry.bidMod.description1')"
          :render-header="_addStarToColumn"
          min-width="120"
        >
          <template slot-scope="scope">
            <el-input
              v-if="deductFlag"
              v-model="scope.row.description"
            />
            <span v-else> {{ scope.row.description }}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="extDeductionType"
          align="center"
          :label="$t('cusEntry.bidMod.deductType')"
          min-width="120"
        >
          <template slot-scope="scope">
            <DictSelect
              v-if="deductFlag"
              v-model="scope.row.extDeductionType"
              code="EXT_DEDUCTION_TYPE"
            />
            <span v-else> {{ $getDictLabel('EXT_DEDUCTION_TYPE', scope.row.extDeductionType) }}</span>
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          prop="fileName"
          :label="$t('cusEntry.bidMod.attachment')"
          :render-header="_addStarToColumn"
          min-width="120"
        >
          <template slot-scope="scope">
            <SrmCommonFile
              :default-file="{
                fileId: scope.row.fileId,
                fileName: scope.row.fileName
              }"
              :readonly="!deductFlag"
              @on-change="({file}) => handleUploadSuccess(file,scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column
          prop="blackFlag"
          align="center"
          :label="$t('cusEntry.bidMod.blackFlag')"
          min-width="120"
          :render-header="_addStarToColumn"
        >
          <template slot-scope="scope">
            <DictSelect
              v-if="deductFlag"
              v-model="scope.row.blackFlag"
              code="YES_OR_NO"
            />
            <span v-else> {{ $getDictLabel('YES_OR_NO', scope.row.blackFlag) }}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="creationDate"
          align="center"
          :label="$t('common.creationDate')"
          min-width="120"
        />
        <el-table-column
          v-if="deductFlag"
          align="center"
          :label="$t('common.operation')"
          width="80"
        >
          <template slot-scope="scope">
            <el-button type="text" @click="deleteDeductRow(scope.$index)">
              {{ $t('common.delete') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div slot="footer">
        <el-button @click="deductVisible = false">
          {{ $t('common.close') }}
        </el-button>
        <el-button v-if="deductFlag" type="primary" @click="confirmDeduct">
          {{ $t('common.confirm') }}
        </el-button>
      </div>
    </srm-dialog>

    <!-- 保证金退回 -->
    <srm-dialog
      v-if="refundVisible"
      size="xLarge"
      :visible.sync="refundVisible"
      :title="refundFlag ? $t('cusEntry.bidMod.refund') : $t('cusEntry.bidMod.refundHistory')"
      append-to-body
      :close-on-click-modal="false"
    >
      <el-table
        :data="refundData"
        style="width: 100%"
        :height="refundFlag ? 240 : 380"
        border
      >
        <el-table-column
          align="center"
          type="index"
          :label="$t('common.sort')"
          width="50"
        />
        <el-table-column
          align="center"
          prop="refundAccount"
          :label="$t('cusEntry.bidMod.refundAccount')"
          :render-header="_addStarToColumn"
          min-width="100"
        >
          <template slot-scope="scope">
            <el-input
              v-if="refundFlag"
              v-model="scope.row.refundAccount"
            />
            <span v-else> {{ scope.row.refundAccount }}</span>
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          prop="refundAccountName"
          :label="$t('cusEntry.bidMod.refundAccountName')"
          :render-header="_addStarToColumn"
          min-width="100"
        >
          <!-- <template slot-scope="scope">
            <el-input
              v-if="refundFlag"
              v-model="scope.row.refundAccountName"
            />
            <span v-else> {{ scope.row.refundAccountName }}</span>
          </template> -->
        </el-table-column>
        <el-table-column
          align="center"
          prop="refundBank"
          :label="$t('cusEntry.bidMod.refundBank')"
          :render-header="_addStarToColumn"
          min-width="100"
        >
          <template slot-scope="scope">
            <el-input
              v-if="refundFlag"
              v-model="scope.row.refundBank"
            />
            <span v-else> {{ scope.row.refundBank }}</span>
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          prop="refundBankNum"
          :label="$t('cusEntry.bidMod.refundBankNum')"
          :render-header="_addStarToColumn"
          min-width="150"
        >
          <template slot-scope="scope">
            <el-input
              v-if="refundFlag"
              v-model="scope.row.refundBankNum"
            />
            <span v-else> {{ scope.row.refundBankNum }}</span>
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          prop="refundAmount"
          :label="$t('cusEntry.bidMod.chargeAmount1')"
          :render-header="_addStarToColumn"
          min-width="150"
        >
          <template slot-scope="scope">
            <el-input
              v-if="refundFlag"
              v-model="scope.row.refundAmount"
            />
            <span v-else> {{ scope.row.refundAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="expectRefundTime"
          :label="$t('cusEntry.bidMod.expectRefundTime')"
          minWidth="150"
          :render-header="_addStarToColumn"
        >
          <template v-slot="scope">
            <el-date-picker
              v-if="refundFlag"
              v-model="scope.row.expectRefundTime"
              type="date"
              value-format="yyyy-MM-dd"
              :picker-options="pickerOptions"
            />
            <span v-else>{{ scope.row.expectRefundTime }}</span>
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          prop="description"
          :label="$t('cusEntry.bidMod.description2')"
          :render-header="_addStarToColumn"
          min-width="120"
        >
          <template slot-scope="scope">
            <el-input
              v-if="refundFlag"
              v-model="scope.row.description"
            />
            <span v-else> {{ scope.row.description }}</span>
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          prop="fileName"
          :label="$t('cusEntry.bidMod.attachment')"
          min-width="120"
        >
          <template slot-scope="scope">
            <SrmCommonFile
              :default-file="{
                fileId: scope.row.fileId,
                fileName: scope.row.fileName
              }"
              :readonly="!refundFlag"
              @on-change="({file}) => handleUploadSuccess(file,scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column
          v-if="refundFlag"
          align="center"
          :label="$t('common.operation')"
          width="80"
        >
          <template slot-scope="scope">
            <el-button type="text" @click="deleteRefundRow(scope.$index)">
              {{ $t('common.delete') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div slot="footer">
        <el-button @click="refundVisible = false">
          {{ $t('common.close') }}
        </el-button>
        <el-button v-if="refundFlag" type="primary" @click="confirmRefund">
          {{ $t('common.confirm') }}
        </el-button>
      </div>
    </srm-dialog>

    <!-- 缴纳处理 -->
    <srm-dialog
      v-if="handlerPayVisible"
      :visible.sync="handlerPayVisible"
      size="small"
      title="缴纳处理"
      append-to-body
      :destroy-on-close="true"
      :close-on-click-modal="false"
    >
      <el-form ref="payForm" :model="payForm">
        <el-form-item
          prop="handerMode"
          label="处理方式"
          :rules="[{ required: true, message: '请选择'}]"
        >
          <dict-select
            v-model="payForm.handerMode"
            code="MARGIN_HANDER_MODE"
            :transformOptions="transformOptions"
          />
        </el-form-item>
        <el-form-item
          prop="causeDesc"
          label="原因说明"
          :rules="[{ required: true, message: '请填写'}]"
        >
          <el-input v-model="payForm.causeDesc" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="handlerPayVisible = false">
          {{ $t('common.close') }}
        </el-button>
        <el-button type="primary" @click="confirmHanderMode">
          {{ $t('common.confirm') }}
        </el-button>
      </div>
    </srm-dialog>
  </div>
</template>

<script>
import { bidBuyerHttp } from 'modcb@/biddingBuyer/api'

export default {
  name: 'BondManagement',

  props: {
    readonly: {
      type: Boolean,
      default: false
    },
    // 投标基础信息
    biddingBase: {
      type: Object,
      default: () => ({})
    },
    isActiveMenu: {
      type: Boolean,
      required: true
    }
  },

  data () {
    return {
      bondList: [],
      selectedList: [],
      deductFlag: false, // 区分发起扣款or扣款历史
      refundFlag: false, // 区分发起退款or退款历史
      deductVisible: false,
      refundVisible: false,
      deductData: [],
      refundData: [],
      editRow: {},
      handlerPayVisible: false,
      payForm: {
        handerMode: '',
        causeDesc: ''
      },
      pickerOptions: {
        disabledDate: time => {
          const day = time.getDate()
          return !(day === 10 || day === 20 || day > 21)
        }
      }
    }
  },
  computed: {
    showRefund () {
      // 由于保证金数据是统一类型，故根据顾问需求取第一条判断
      return this.bondList[0]?.yearFlag !== 'Y'
    }
  },
  watch: {
    isActiveMenu: {
      async handler (val) {
        if (val) {
          this.getBondsData()
        }
      },
      immediate: true
    }
  },
  methods: {
    transformOptions (options) {
      // 未缴纳仅可选择【允许不缴纳】过滤其他选项
      // 待确认可选择【线下核对缴纳】【缴纳失败】过滤其他选项
      let arr = this.editRow.marginStatus == 'NOT_PAY' ? ['ON_LINE', 'OFF_LINE', 'ERROR_PAY', 'EXT_PAY'] : ['ON_LINE', 'CAN_NOTPAY']
      let res = options.filter(item => !arr.includes(item.value))
      return res
    },
    handleSelectionChange (val) {
      this.selectedList = val
    },
    handleUploadSuccess (file, row) {
      const { fileId = '', fileName = '' } = file || {}
      row.fileId = fileId
      row.fileName = fileName
    },
    // 查询保证金管理列表
    async getBondsData () {
      const id = this.biddingBase.projectId
      if (!id) {
        // id不存在，不需要查询
        return
      }
      const response = await bidBuyerHttp.init.getBondsData(id)
      if (response && response.data) {
        this.bondList = response.data
      }
    },
    // 缴纳处理
    handlerPay (row) {
      this.editRow = row
      this.payForm = {
        handerMode: '',
        causeDesc: ''
      }
      this.handlerPayVisible = true
    },
    // 确认缴纳处理
    confirmHanderMode () {
      this.$refs.payForm.validate(valid => {
        if (valid) {
          const params = {
            ...this.editRow,
            ...this.payForm
          }
          this.$http({
            url: '/api-sou/ext/buyer/bid/init/canNotNeedPayMargin',
            method: 'POST',
            data: params,
            loading: true
          }).then(res => {
            this.handlerPayVisible = false
            this.$message.success(res.message)
            this.getBondsData()
          })
        }
      })
    },
    // 允许不缴纳保证金
    confirmNoPayCause (row) {
      this.$prompt(this.$t('cusEntry.bidMod.noPayCause1'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.close'),
        closeOnClickModal: false,
        inputPlaceholder: this.$t('cusEntry.common.pleaseFill'),
        inputValidator: (value) => {
          if (!value) {
            return this.$t('cusEntry.bidMod.pleaseInputCause')
          }
        } }).then(({ value }) => {
        const params = {
          marginId: row.marginId,
          noPayCause: value
        }
        this.$http({
          url: '/api-sou/ext/buyer/bid/init/canNotNeedPayMargin',
          method: 'POST',
          data: params,
          loading: true
        }).then(res => {
          this.$message.success(res.message)
          this.getBondsData()
        })
      })
    },
    // 打开保证金扣款弹框  if[type]为edit代表发起保证金扣款  else代表查看历史
    async openDeductDialog (flag) {
      // 校验退款状态
      let validRefundStatus = true
      this.selectedList.some(item => {
        if (!['NOT_REFUNDED', 'REFUND_FAILED'].includes(item.refundStatus || 'NOT_REFUNDED')) {
          validRefundStatus = false
          return false
        }
      })
      if (!validRefundStatus) {
        this.$message.warning(this.$t('cusEntry.tipMessage.refundStatusMsg'))
        return false
      }
      if (flag == 'edit') {
        this.deductFlag = true
        this.deductData = this.selectedList
        // 缴纳处理：线上核对缴纳 才允许发起保证金扣款、保证金退款
        let onlineFlag = this.selectedList.some(item => item.handerMode !== 'ON_LINE')
        if (onlineFlag) {
          this.$message.error('缴纳处理为线上核对缴纳，才允许发起保证金扣款')
          return
        }
      } else {
        this.deductFlag = false
        await this.$http({
          url: `/api-sou/ext/buyer/bid/init/getSouMarginRecord?projectId=${this.biddingBase.projectId}&type=CHARGE`,
          method: 'GET',
          loading: true
        }).then(res => {
          this.deductData = res.data || []
        })
      }
      this.deductVisible = true
    },
    // 确认扣款
    confirmDeduct () {
      let flag = this.deductData.some(item => !item.amount || !item.description || !item.fileId || !item.blackFlag)
      if (flag) {
        this.$message.error(this.$t('common.pleasefinishRequired'))
        return
      }
      // 校验扣款金额不能大于可退金额
      let validResult = true
      this.deductData.some((item, index) => {
        if (Number(item.refundAmount) < Number(item.amount)) {
          validResult = false
          return true
        }
      })
      if (!validResult) {
        this.$message.warning(this.$t('cusEntry.tipMessage.refundAmountMsg'))
        return false
      }
      const params = {
        type: 'CHARGE',
        projectId: this.biddingBase.projectId,
        marginRecordList: this.deductData
      }
      this.$http({
        url: '/api-sou/ext/buyer/bid/init/editMarginRecord',
        method: 'POST',
        data: params,
        loading: true
      }).then(res => {
        this.$message.success(res.message)
        this.deductVisible = false
        this.getBondsData()
      })
    },
    deleteDeductRow (index) {
      this.deductData.splice(index, 1)
    },
    // 打开保证金退回弹框  if[type]为edit代表发起保证金退款 else代表查看历史
    async openRefundDialog (flag) {
      // 校验退款状态
      let validRefundStatus = true
      this.selectedList.some(item => {
        if (!['NOT_REFUNDED', 'REFUND_FAILED'].includes(item.refundStatus || 'NOT_REFUNDED')) {
          validRefundStatus = false
          return false
        }
      })
      if (!validRefundStatus) {
        this.$message.warning(this.$t('cusEntry.tipMessage.returnStatusMsg'))
        return false
      }
      if (flag == 'edit') {
        this.refundFlag = true
        this.refundData = this.selectedList.map(item => {
          const {
            payAccount,
            payAccountName,
            payBank,
            bankLine,
            fileId,
            fileName,
            ...rest
          } = item
          return {
            ...rest,
            refundAccount: payAccount,
            refundAccountName: payAccountName,
            refundBankNum: bankLine,
            refundBank: payBank,
            sourceRefundAmount: item.refundAmount
          }
        })
        // 缴纳处理：线上核对缴纳 才允许发起保证金扣款、保证金退款
        let onlineFlag = this.selectedList.some(item => item.handerMode !== 'ON_LINE')
        if (onlineFlag) {
          this.$message.error('缴纳处理为线上核对缴纳，才允许发起保证金退回')
          return
        }
      } else {
        this.refundFlag = false
        await this.$http({
          url: `/api-sou/ext/buyer/bid/init/getSouMarginRecord?projectId=${this.biddingBase.projectId}&type=REFUND`,
          method: 'GET',
          loading: true
        }).then(res => {
          this.refundData = res.data || []
        })
      }
      this.refundVisible = true
    },
    // 确认退回
    async confirmRefund () {
      let flag = this.refundData.some(item => !item.refundAccount || !item.refundAccountName || !item.refundBank ||
        !item.refundBankNum || !item.refundAmount || !item.description)
      if (flag) {
        this.$message.error(this.$t('common.pleasefinishRequired'))
        return
      }
      // 校验退款金额不能大于可退金额
      let validResult = true
      this.refundData.some((item, index) => {
        if (Number(item.sourceRefundAmount) < Number(item.refundAmount)) {
          validResult = false
          return true
        }
      })
      if (!validResult) {
        this.$message.warning(this.$t('cusEntry.tipMessage.returnAmountMsg'))
        return false
      }
      // 校验保证金是否可退回
      const { data } = await bidBuyerHttp.bond.checkBond(this.refundData.map(item => item.marginId))
      if (!data.result) {
        this.$message.warning(data.message)
        return false
      }
      const params = {
        type: 'REFUND',
        projectId: this.biddingBase.projectId,
        marginRecordList: this.refundData
      }
      this.$http({
        url: '/api-sou/ext/buyer/bid/init/editMarginRecord',
        method: 'POST',
        data: params,
        loading: true
      }).then(res => {
        this.$message.success(res.message)
        this.refundVisible = false
        this.getBondsData()
      })
    },
    deleteRefundRow (index) {
      this.refundData.splice(index, 1)
    }
  }
}
</script>
<style lang="scss" scoped></style>
