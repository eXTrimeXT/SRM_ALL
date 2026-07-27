<template>
<!-- 费用核对 -->
  <SrmDialog
    size="large"
    :title="$t('competition.expenseReconciliation')"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    append-to-body
  >
  <!-- 请核对申请预算与实际定点供应商费用是否超预算，是则自动生成采购申请变更单，之后需求部门发起费用变更单会签审批；金额为未税本币 -->
    <p>{{ $t('bidMod.noteVerification') }}</p>

    <el-table
      :data="expenseVerificationTableData"
      style="min-width: 100%"
      border
      height="300px"
    >
      <el-table-column
        type="index"
        :label="$t('common.sort')"
        width="50"
      />

      <!--物料编码-->
      <el-table-column
        prop="itemCode"
        :label="$t('bidMod.targetNum')"
        min-width="130"
        show-overflow-tooltip
      />

      <!--物料名称-->
      <el-table-column
        prop="itemDesc"
        :label="$t('bidMod.targetDesc')"
        min-width="150"
        show-overflow-tooltip
      />

      <!--费用类型-->
      <el-table-column
        prop="feeType"
        :label="$t('purchaseDemand.typeOfFee')"
        min-width="100"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $getDictLabel('ECR_FEE_TYPE', cellValue)"
      />

      <!--预算编号-->
      <el-table-column
        prop="budgetNum"
        :label="$t('purchaseDemand.budgetNumber')"
        min-width="120"
      />

      <!--申请金额RMB-->
      <el-table-column
        prop="applyTotalMoney"
        :label="$t('bidMod.applyTotalMoney1')"
        min-width="120"
      />

      <!--定点金额RMB-->
      <el-table-column
        prop="fixedAmount"
        :label="$t('bidMod.fixedAmount1')"
        min-width="120"
      />

      <!--超费用金额RMB-->
      <el-table-column
        prop="amountOverspent"
        :label="$t('bidMod.amountOverspent1')"
        min-width="120"
      />
    </el-table>

    <template #footer>
      <el-button @click="dialogVisible = false">
        {{ $t('common.cancel') }}
      </el-button>

      <el-button type="primary" @click="submit">
        {{ $t('common.submit') }}
      </el-button>
    </template>
  </SrmDialog>
</template>

<script>
/**
 * 费用核对
 */
import { compBuyerHttp } from 'modb@/competition/api'

export default {
  name: 'ExpenseVerification',

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    projectId: {
      type: [String, Number],
      required: true
    }
  },

  data () {
    return {
      expenseVerificationTableData: []
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

  created () {
    this.getFeeReconciliation()
  },

  methods: {
    /* 查询详情 */
    async getFeeReconciliation () {
      const response = await compBuyerHttp.select.feeReconciliation(this.projectId)
      if (response) {
        this.expenseVerificationTableData = response.data || []
      }
    },

    /* 提交 */
    async submit () {
      // 生成采购申请变更单
      const response = await compBuyerHttp.select.createRequirementUpdate(this.projectId)
      if (response) {
        this.$message.success(this.$t('common.successSubmit'))
        this.dialogVisible = false
      }
    }
  }
}
</script>
