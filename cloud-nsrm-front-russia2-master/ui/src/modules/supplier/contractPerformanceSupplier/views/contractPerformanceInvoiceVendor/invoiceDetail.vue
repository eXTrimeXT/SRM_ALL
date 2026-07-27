<template>
  <div>
    <el-button type="primary" :disabled="disabledFlag" @click="addInvoice">
      {{ $t('bidMod.affairsIncreased') }}
    </el-button>
    <el-table border stripe class="mt-10" :data="value">
      <el-table-column min-width="180" show-overflow-tooltip>
        <template slot="header">
          <i class="toRequired">*</i>
          {{ $t('contractMod.contractNo_1') }}
        </template>
        <template slot-scope="scope">
          <el-input v-model="scope.row.contractNo" :disabled="disabledFlag">
            <el-button slot="append" icon="el-icon-search" :disabled="disabledFlag" @click="contractRowClick(scope)" />
          </el-input>
        </template>
      </el-table-column>
      <el-table-column min-width="180" show-overflow-tooltip>
        <template slot="header">
          <i class="toRequired">*</i>
          {{ $t('other.key23') }}
          <!-- 一个开票单只能选择一种里程碑 -->
          <el-tooltip class="item" effect="dark" :content="$t('cusEntry.supplement20250211.openInvoiceMilestoneChoice')" placement="top">
            <i class="el-icon-info" />
          </el-tooltip>
        </template>
        <template slot-scope="scope">
          <el-input :value="$getDictLabel('MILESTONE_SCHEDULE', scope.row.milestoneType)" :disabled="disabledFlag">
            <el-button slot="append" icon="el-icon-search" :disabled="disabledFlag" @click="contractRowClick2(scope)" />
          </el-input>
        </template>
      </el-table-column>
      <el-table-column prop="materialCode" :label="$t('mould.itemNumber')" min-width="130" show-overflow-tooltip />
      <el-table-column prop="materialName" :label="$t('purSettlementMod.materialId')" min-width="150" show-overflow-tooltip />
      <el-table-column prop="contractQuantity" :label="$t('bid_mod.quantity')" min-width="100" show-overflow-tooltip />
      <el-table-column prop="invName" :label="$t('purchaseDemand.invOrg')" min-width="130" show-overflow-tooltip />
      <el-table-column prop="amount" :label="$t('other.key22')" min-width="130" show-overflow-tooltip />
      <el-table-column prop="unAmount" :label="$t('other.key21')" min-width="130" show-overflow-tooltip />
      <el-table-column prop="invoicedTaxedAmount" :label="$t('other.key20')" min-width="130" show-overflow-tooltip />
      <el-table-column prop="invoicedUntaxedAmount" :label="$t('other.key19')" min-width="130" show-overflow-tooltip />
      <el-table-column prop="occupyInvoicedTaxedAmount" :label="$t('other.key18')" min-width="130" show-overflow-tooltip />
      <el-table-column prop="occupyInvoicedUntaxedAmount" :label="$t('other.key17')" min-width="130" show-overflow-tooltip />
      <el-table-column prop="currentUntaxedAmount" min-width="130" show-overflow-tooltip>
        <template slot="header">
          <i class="toRequired">*</i>
          {{ $t('other.key16') }}
        </template>
        <template slot-scope="scope">
          <el-input v-model="scope.row.currentUntaxedAmount" type="number" v-input-format="inputFormat" :disabled="disabledFlag" @change="value => amoutChange(value,scope.row)" />
        </template>
      </el-table-column>
      <el-table-column prop="taxRate" :label="$t('purSettlementMod.taxRate2')" min-width="120" show-overflow-tooltip />
      <el-table-column prop="currentTaxedAmount" :label="$t('other.key15')" min-width="130" show-overflow-tooltip />
      <el-table-column :label="$t('formula.handle')" min-width="100" show-overflow-tooltip fixed="right">
        <template slot-scope="scope">
          <el-button type="text" :disabled="disabledFlag" @click="deleteInvoice(scope)">
            {{ $t('components.common.delete') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 合同选择弹窗 -->
    <srm-dialog
      size="large"
      :title="$t('contractMod.selContract')"
      :visible.sync="contractShow"
      :close-on-click-modal="false"
      :destroy-on-close="true"
      @close="resetContract"
    >
      <el-form ref="contractForm" :model="contractForm" :inline="true" class="demo-form-inline">
        <el-form-item :label="$t('contractMod.contractNo_1')" prop="contractNo">
          <el-input v-model="contractForm.contractNo" />
        </el-form-item>
        <el-form-item :label="$t('bidMod.contractName')" prop="contractName">
          <el-input v-model="contractForm.contractName" />
        </el-form-item>
        <el-form-item :label="$t('dataConfMod.createdBy')" prop="createdFullName">
          <el-input v-model="contractForm.createdFullName" />
        </el-form-item>
        <span style="float:right;">
          <el-button type="primary" @click="getContractList">
            {{ $t('bidMod.inquire') }}
          </el-button>
          <el-button @click="resetContract">
            {{ $t('common.reset') }}
          </el-button>
        </span>
        <br>
        <el-form-item :label="$t('purSettlementMod.materialId')" prop="materialName">
          <el-input v-model="contractForm.materialName" />
        </el-form-item>
      </el-form>
      <el-table
        class="mt-10"
        :data="contractData"
        border
        stripe
        max-height="300"
        @row-click="rowClick"
      >
        <el-table-column prop="contractNo" :label="$t('contractMod.contractNo_1')" show-overflow-tooltip />
        <el-table-column prop="contractName" :label="$t('bidMod.contractName')" show-overflow-tooltip />
        <el-table-column prop="materialCode" :label="$t('mould.itemNumber')" show-overflow-tooltip />
        <el-table-column prop="materialName" :label="$t('purSettlementMod.materialId')" show-overflow-tooltip />
        <el-table-column prop="surplusUntaxedAmount" :label="$t('other.key14')" show-overflow-tooltip />
        <el-table-column prop="surplusTaxedAmount" :label="$t('other.key27')" show-overflow-tooltip />
        <el-table-column prop="createdFullName" :label="$t('dataConfMod.createdBy')" show-overflow-tooltip />
        <el-table-column prop="creationDate" :label="$t('common.creationTime')" :formatter="(row, column, cellValue) => $parseTime(cellValue)" show-overflow-tooltip />
      </el-table>
      <CPagination
        :pageNum="contractPageNum"
        :pageSize="contractPageSize"
        :total="contractTotal"
        @current-change="contractPageChange"
        @size-change="contractSizeChange"
      />
    </srm-dialog>
    <!-- 里程碑选择弹窗 -->
    <srm-dialog
      size="large"
      :title="$t('other.key26')"
      :visible.sync="contractShow2"
      :close-on-click-modal="false"
      :destroy-on-close="true"
      @close="resetContract"
    >
      <el-table
        class="mt-10"
        :data="contractData2"
        border
        stripe
        max-height="300"
        @row-click="rowClick2"
      >
        <el-table-column prop="milestoneType" :label="$t('contract_mod.processNodeName')" show-overflow-tooltip>
          <template slot-scope="scope">
            {{ $getDictLabel('MILESTONE_SCHEDULE', scope.row.milestoneType) }}
          </template>
        </el-table-column>
        <el-table-column prop="paymentStage" :label="$t('bidMod.category_stage')" show-overflow-tooltip>
          <template slot-scope="scope">
            {{ $getDictLabel('PAYMENT_STAGE', scope.row.paymentStage) }}
          </template>
        </el-table-column>
        <el-table-column prop="payExplain" :label="$t('route.contractPaymentType')" show-overflow-tooltip>
          <template slot-scope="scope">
            <dict-select
              :value="num(scope.row.payExplain)"
              code="payExplain"
              custom-select-type="payExplain"
              :disabled="true"
            />
          </template>
        </el-table-column>
        <el-table-column prop="payMethod" :label="$t('bidMod.category_paymentMethod')" show-overflow-tooltip>
          <template slot-scope="scope">
            {{ $getDictLabel('PAYMENT_MODE', scope.row.payMethod) }}
          </template>
        </el-table-column>
        <el-table-column prop="paymentRatio" :label="$t('bidMod.paymentRatio')" show-overflow-tooltip />
        <el-table-column prop="stagePaymentAmount" :label="$t('contractMod.stagePaymentAmount')" show-overflow-tooltip />
      </el-table>
      <CPagination
        :pageNum="contractPageNum"
        :pageSize="contractPageSize"
        :total="contractTotal"
        @current-change="contractPageChange"
        @size-change="contractSizeChange"
      />
    </srm-dialog>
  </div>
</template>

<script>
import uploadTable from '@/library/composition/purSettlement/uploadTableShow.vue'
import { createDictClass } from '@/library/utils/dict/dict-utils'
import CPagination from 'lib@/components/c-pagination'

const initContractForm = () => ({
  contractNo: '',
  contractName: '',
  createdFullName: '',
  materialName: ''
})

export default {
  name: 'InvoiceInfo',
  components: {
    uploadTable,
    CPagination
  },
  props: {
    disabledFlag: {
      type: Boolean,
      default: false
    },
    value: {
      type: Array,
      default: () => []
    },
    validate: Function,
    buId: { type: [String, Number] },
    buCode: { type: String },
    currencyId: { type: Number },
    currencyCode: { type: String },
    taxId: { type: String },
    taxKey: { type: String }
  },
  data () {
    return {
      dictClass: createDictClass({
        MILESTONE_SCHEDULE: [], // 里程碑名称
        CONTRACT_INVOICE_STATUS: [] // 履约状态
      }),
      userInfo: this.$store.getters.user,
      inputFormat: { type: 'float', digits: 2, negative: false, zero: false },
      contractShow2: false,
      contractShow: false,
      contractForm: initContractForm(),
      contractPageNum: 1,
      contractPageSize: 15,
      contractTotal: 0,
      contractData: [],
      contractData2: []
    }
  },
  computed: {
    MILESTONE_SCHEDULE () {
      return this.dictClass.getDict('MILESTONE_SCHEDULE')
    }
  },
  methods: {
    num (nums) {
      return parseInt(nums) ? parseInt(nums) : ''
    },
    // 点击里程碑选择确定后回显与关掉弹框
    rowClick2 (row, column, event) {
      this.value[this.contractScope.$index].milestoneType = row.milestoneType
      this.value[this.contractScope.$index].perPayPlanId = row.perPayPlanId
      this.$emit('change', this.value)
      this.contractShow2 = false
    },
    async rowClick (row, column, event) {
      let { $index } = this.contractScope
      if ($index || $index === 0) {
        let { getMileByContractNo } = this.$api.cmPerform.vendor.inv.performInvoice
        let contractNo = row.contractNo
        if (contractNo) {
          let res = await getMileByContractNo(contractNo)
          let list = res.data || []
          if (list.length) {
            row.milestoneTypeList = []
            for (let item of list) {
              for (let innerItem of this.MILESTONE_SCHEDULE) {
                if (item.milestoneType === innerItem.value) {
                  row.milestoneTypeList.push({
                    value: innerItem.value,
                    label: innerItem.label,
                    performTemplLineId: item.performTemplLineId,
                    perPlanMilestoneId: item.perPlanMilestoneId
                  })
                }
              }
            }
          }
        }
        this.value.splice($index, 1, row)
        this.$emit('change', this.value)
        this.contractShow = false
      }
    },
    contractSizeChange (value) {
      this.contractPageNum = 1
      this.contractPageSize = value
      this.getContractList()
    },
    contractPageChange (value) {
      this.contractPageNum = value
      this.getContractList()
    },
    resetContract () {
      // destroy-on-close=true 会导致失效
      // this.$refs.contractForm.resetFields()

      this.contractForm = initContractForm()
    },
    async getContractList () {
      let { queryContractMaterial, getMileByContractNo } = this.$api.cmPerform.vendor.inv.performInvoice
      let res = await queryContractMaterial({
        pageNum: this.contractPageNum,
        pageSize: this.contractPageSize,
        vendorId: this.userInfo.userInfo.companyId,
        buId: this.buId,
        buCode: this.buCode,
        taxId: this.taxId,
        taxKey: this.taxKey,
        currencyId: this.currencyId,
        currencyCode: this.currencyCode,
        ...this.contractForm
      })
      let list = res.data.list || []
      this.contractData = list
      this.contractTotal = res.data.total || 0
    },
    amoutChange (value, row) {
      if (isNaN(+value)) {
        row.currentTaxedAmount = null
        return false
      }
      let currentTaxedAmount = Math.abs(value) * (100 + row.taxRate || 0) / 100
      currentTaxedAmount = Math.round(currentTaxedAmount * 100) / 100
      row.currentTaxedAmount = currentTaxedAmount

      this.$emit('change', this.value)
    },
    deleteInvoice (scope) {
      let perInvoiceDetailId = scope.row.perInvoiceDetailId
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          if (perInvoiceDetailId) {
            let { deletePerInvoiceDetailById } = this.$api.cmPerform.vendor.inv.performInvoice
            deletePerInvoiceDetailById(perInvoiceDetailId).then(res => {
              this.value.splice(scope.$index, 1)
            })
          } else {
            this.value.splice(scope.$index, 1)
          }
          this.$emit('change', this.value)
        })
        .catch(() => {})
    },
    contractRowClick2 (scope) {
      let validForm
      // this.$refs.form.validate(valid => (validForm = valid))
      // if (!validForm) {
      //   this.__focus_error__()
      //   return
      // }
      if (!scope.row.contractNo) {
        // 请选择合同序号
        this.$message.success(this.$t('cusEntry.supplement20250211.selectContractNumber'))
        return false
      }
      this.contractScope = scope
      this.contractShow2 = true
      this.getContractList2(scope.row.contractNo)
    },
    async getContractList2 (contractNo) {
      let { getPerPayPlanByContractNo } = this.$api.cmPerform.vendor.inv.performInvoice
      let res = await getPerPayPlanByContractNo(contractNo)
      let list = res.data || []
      this.contractData2 = list
      this.contractTotal = res.data.total || 0
    },
    async contractRowClick (scope) {
      // try {
        if (this.validate) {
          await this.validate(scope)
        }

        let validForm
        // this.$refs.form.validate(valid => (validForm = valid))
        // if (!validForm) {
        //   this.__focus_error__()
        //   return
        // }
        this.contractScope = scope
        this.contractShow = true
        this.getContractList()
      // } catch {
        //
      // }
    },
    addInvoice () {
      this.value.push({
        contractNo: null,
        milestoneType: null,
        performTemplLineId: null,
        perPlanMilestoneId: null,
        milestoneTypeList: [],
        materialCode: null,
        materialName: null,
        contractQuantity: null,
        invName: null,
        taxedPrice: null,
        untaxedPrice: null,
        invoicedTaxedAmount: null,
        invoicedUntaxedAmount: null,
        currentUntaxedAmount: null,
        taxRate: null,
        currentTaxedAmount: null
      })
      this.$emit('change', this.value)
    }
  }
}
</script>

<style scoped>
::v-deep .mt-10 {
  margin-top: 10px;
  margin-bottom: 10px;
}
</style>
