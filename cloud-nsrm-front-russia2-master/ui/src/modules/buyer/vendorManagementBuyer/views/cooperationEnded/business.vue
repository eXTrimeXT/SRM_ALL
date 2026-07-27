<template>
  <div>
    <el-table
      :data="datas"
      border
      style="width: 100%"
    >
      <!-- 品类状态 -->
      <el-table-column
        prop="categoryStateCode"
        :label="$t('vendorMod.categoryStateCode')"
      >
        <template slot-scope="scope">
          {{ filter(scope.row.categoryStateCode) }}
        </template>
      </el-table-column>
      <!-- 是否允许询价 -->
      <el-table-column
        prop="ifAllowInquiry"
        :label="$t('vendorMod.ifAllowInquiry')"
      >
        <template slot-scope="scope">
          {{ filter2(scope.row.ifAllowInquiry) }}
        </template>
      </el-table-column>
      <!-- 是否允许招标 -->
      <el-table-column
        prop="ifAllowBid"
        :label="$t('vendorMod.ifAllowBid')"
      >
        <template slot-scope="scope">
          {{ filter2(scope.row.ifAllowBid) }}
        </template>
      </el-table-column>
      <!-- 是否允许下单 -->
      <el-table-column
        prop="ifAllowOrder"
        :label="$t('vendorMod.ifAllowOrder')"
      >
        <template slot-scope="scope">
          {{ filter2(scope.row.ifAllowOrder) }}
        </template>
      </el-table-column>
      <!-- 是否允许入库 -->
      <el-table-column
        prop="ifAllowWarehousing"
        :label="$t('vendorMod.ifAllowWarehousing')"
      >
        <template slot-scope="scope">
          {{ filter2(scope.row.ifAllowWarehousing) }}
        </template>
      </el-table-column>
      <!-- 是否允许对账 -->
      <el-table-column
        prop="ifAllowStatement"
        :label="$t('vendorMod.ifAllowStatement')"
      >
        <template slot-scope="scope">
          {{ filter2(scope.row.ifAllowStatement) }}
        </template>
      </el-table-column>
      <!-- 是否允许付款 -->
      <el-table-column
        prop="ifAllowPay"
        :label="$t('vendorMod.ifAllowPay')"
      >
        <template slot-scope="scope">
          {{ filter2(scope.row.ifAllowPay) }}
        </template>
      </el-table-column>
      <!-- 过渡期业务时间 -->
      <el-table-column
        prop="transitDay"
        :label="$t('vendorMod.transitDay')"
      />
      <!-- 采购订单次数(次) -->
      <el-table-column
        prop="orderCount"
        :label="$t('vendorMod.orderCount')"
      />
      <!-- 单笔采购金额限额(元) -->
      <el-table-column
        prop="amountLimitPerOrder"
        :label="$t('vendorMod.amountLimitPerOrder')"
      />
      <!-- 备注 -->
      <el-table-column
        prop="remark"
        :label="$t('vendorMod.remark')"
      />
    </el-table>
  </div>
</template>

<script>
import { adaptDictData } from '@/utils'
import { getDictItem } from '@/api/common'
export default {
  name: 'Business',
  components: {},
  props: {
    datas: {
      type: Object,
      default: () => {}
    }
  },
  data () {
    return {
      stateOprions: [],
      yesOrNo: [
        {
          value: 'N',
          label: this.$t('common.no')
        },
        {
          value: 'Y',
          label: this.$t('common.yes')
        }
      ]
    }
  },
  mounted () {
    let _this = this
    getDictItem('CATEGORY_STATE_CODE').then(res => {
      _this.stateOprions = adaptDictData(res.data, 'dict')
    })
  },
  created () {},

  methods: {
    filter (val) {
      let res = ''
      this.stateOprions.filter(currentValue => {
        if (currentValue.value == val) {
          res = currentValue.label
        }
      })
      return res
    },
    filter2 (val) {
      let res = ''
      this.yesOrNo.filter(currentValue => {
        if (currentValue.value == val) {
          res = currentValue.label
        }
      })
      return res
    }
  }
}
</script>

<style scoped lang="scss"></style>
