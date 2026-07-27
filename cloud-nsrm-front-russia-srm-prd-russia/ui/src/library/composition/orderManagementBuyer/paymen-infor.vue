<template>
  <div>
    <el-table :data="tableData" style="width: 100%" max-height="250px" border>
      <el-table-column
        align="center"
        prop="paymentPeriodsNumber"
        :label="$t('contractMod.paymentPeriod')"
        width="80"
        :show-overflow-tooltip="true"
        :disabled="isReadOnly"
      />
      <!-- 付款阶段 -->
      <el-table-column
        align="center"
        prop="paymentStage"
        :label="$t('contractMod.payStage')"
        width="120"
        :show-overflow-tooltip="true"
      >
        <template slot-scope="scope">
          <DictSelect
            v-model="scope.row.paymentStage"
            code="PAYMENT_STAGE"
            :disabled="isReadOnly"
          />
        </template>
      </el-table-column>
      <!-- 付款条件 -->
      <el-table-column
        align="center"
        prop="paymentTerm"
        :label="$t('contractMod.termOfPayment')"
        min-width="120"
        :show-overflow-tooltip="true"
      >
        <template slot-scope="scope">
          <el-select v-model="scope.row.paymentTerm" :disabled="isReadOnly">
            <el-option
              v-for="item in payExplainList"
              :key="item.payTypeId"
              :label="item.payExplain"
              :value="item.payTypeId"
            />
          </el-select>
        </template>
      </el-table-column>
      <!-- 付款帐期 -->
      <el-table-column
        align="center"
        prop="paymentPeriod"
        :label="$t('paymentType.paymentDay1')"
        min-width="150"
      >
        <template slot-scope="scope">
          <DictSelect
            v-model="scope.row.paymentPeriod"
            code="PAYMENT_PERIOD"
            :disabled="isReadOnly"
          />
        </template>
      </el-table-column>
      <!-- 付款比例 -->
      <el-table-column
        align="center"
        prop="paymentRadio"
        :label="$t('contractMod.payRatio')"
        width="100"
        :show-overflow-tooltip="true"
      >
        <template slot-scope="scope">
          <el-input v-model="scope.row.paymentRadio" type="number" :disabled="isReadOnly" />
        </template>
      </el-table-column>
      <!-- 付款方式 -->
      <el-table-column
        align="center"
        prop="paymentWay"
        :label="$t('paymentType.paymentWay')"
        min-width="120"
        :show-overflow-tooltip="true"
      >
        <template slot-scope="scope">
          <DictSelect v-model="scope.row.paymentWay" code="PAYMENT_MODE" :disabled="isReadOnly" />
        </template>
      </el-table-column>
      <el-table-column :label="$t('common.operation')" width="60" fixed="right">
        <template slot-scope="scope">
          <el-button
            :disabled="isReadOnly"
            type="text"
            @click="deletePaymentItem(scope.$index, scope.row)"
          >
            {{ $t('common.delete') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
export default {
  name: 'PaymentInfor',
  props: {
    tableData: {
      type: Array,
      default () {
        return []
      }
    },
    isReadOnly: {
      type: Boolean,
      default () {
        return false
      }
    }
  },
  data () {
    return {
      payExplainList: []
    }
  },
  created () {
    this.getPaylist()
  },
  methods: {
    deletePaymentItem (index, row) {
      this.tableData.splice(index, 1)
    },
    getPaylist () {
      const data = { pageNum: 1, pageSize: 1000 }
      this.$http({
        url: '/api-cm/template/payType/paymentTermsPage',
        method: 'POST',
        data: data,
        loading: true
      }).then(res => {
        if (res) {
          const payExplainList = res.data.list || []
          payExplainList.forEach(item => {
            item.payTypeId = String(item.payTypeId)
          })
          this.payExplainList = payExplainList
        }
      })
    }
  }
}
</script>

<style></style>
