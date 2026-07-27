<template>
  <div class="quote-currency">
    <SrmRow>
      <!--本位币-->
      <SrmCol :init-col="3">
        <el-form-item :label="$t('bid_mod.standardCurrency')" :prop="keyMap.standardCurrency">
          <DictSelect v-model="baseInfoData[keyMap.standardCurrency]" code="currency" />
        </el-form-item>
      </SrmCol>

      <!--价格精度-->
      <SrmCol :init-col="3">
        <el-form-item :label="$t('bid_mod.pricePrecision')" :prop="keyMap.pricePrecision">
          <DictSelect v-model="baseInfoData[keyMap.pricePrecision]" code="PRICE_PRECISION" />
        </el-form-item>
      </SrmCol>

      <!--汇率类型-->
      <SrmCol :init-col="3">
        <el-form-item :label="$t('bid_mod.exchangeRateType')" prop="exchangeRateType">
          <DictSelect v-model="baseInfoData.exchangeRateType" code="EXCHANGE_RATE_TYPE" />
        </el-form-item>
      </SrmCol>

      <!--向供应商展示汇率-->
      <SrmCol v-if="businessTypeCom.isBiding || businessTypeCom.isBargain" :init-col="3">
        <el-form-item :label="$t('bid_mod.showRateType')" prop="showRateType">
          <DictSelect v-model="baseInfoData.showRateType" code="YES_OR_NO" />
        </el-form-item>
      </SrmCol>

      <!--币种转换日期-->
      <SrmCol :init-col="3">
        <el-form-item :label="$t('bid_mod.currencyChangeDate')" :prop="keyMap.currencyChangeDate">
          <el-date-picker v-model="baseInfoData[keyMap.currencyChangeDate]" type="date" />
        </el-form-item>
      </SrmCol>
    </SrmRow>

    <div style="margin-bottom: 10px">
      <el-button
        v-if="!readonly"
        type="primary"
        :disabled="disableAddButton"
        @click="addQuoteCurrency"
      >
        {{ $t("common.add") }}
      </el-button>
      <span style="margin-left: 10px;">{{ $t("bid_mod.quoteCurrencyExplain") }}</span>
    </div>

    <BaseTable
      ref="currencyTable"
      stripe
      :data="currencyList"
      :columns="columns"
      :empty-text="$t('components.noData')"
      border
      @deleteQuoteCurrency="deleteQuoteCurrency"
    >
      <!--币种-->
      <template #currencyCode="scope">
        <DictSelect
          v-model="scope.row.currencyCode"
          code="currency"
          @change="value => getRateByCode(value, scope)"
        />
      </template>

      <!--描述-->
      <template #currencyDesc="scope">
        <el-input v-model="scope.row[keyMap.currencyDesc]" />
      </template>

      <!--价格精度-->
      <template #pricePrecision="scope">
        <DictSelect v-model="scope.row.pricePrecision" code="PRICE_PRECISION" />
      </template>
    </BaseTable>
  </div>
</template>

<script>
/**
 * 报价币种设置
 */
import { mappingPropByBusinessTypeAndKey } from './utils'
import { BUSINESS_TYPE } from 'lib@/composition/origin/composition'
import { BUSINESS_TYPE_ENUM } from 'lib@/composition/origin/enum'
import BaseTable from 'lib@/components/BaseTable'

export default {
  name: 'QuoteCurrency',

  components: { BaseTable },

  props: {
    // 业务类型
    businessType: {
      type: String,
      required: true
      // validator: value => BUSINESS_TYPE.includes(value)
    },
    // 项目信息
    baseInfo: {
      type: Object,
      required: true
    },
    // 旧数据
    detailData: {
      type: Array,
      default: () => []
    },
    // 只读
    readonly: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      columns: [],
      currencyList: []
    }
  },

  computed: {
    // 单据基本信息，双向绑定
    baseInfoData: {
      get: function () {
        this.baseInfoInit(this.baseInfo)
        return this.baseInfo
      },
      set: function (val) {
        this.$emit('update:baseInfo', val)
      }
    },

    // 业务类型
    businessTypeCom () {
      // 业务类型、简易询价、招标、项目式询价、竞价 ['INQUIRY', 'BIDING', 'BARGAIN', 'COMPETITION']
      const type = this.businessType
      return {
        isInquiry: type === BUSINESS_TYPE_ENUM.INQUIRY,
        // 简易询价[LTS]
        isInquiryNew: type === BUSINESS_TYPE_ENUM.INQUIRY_LTS,
        isBiding: type === BUSINESS_TYPE_ENUM.BIDING,
        isBidingNew: type === BUSINESS_TYPE_ENUM.BIDDING_LTS,
        isBargain: type === BUSINESS_TYPE_ENUM.BARGAIN,
        isBargainNew: type === BUSINESS_TYPE_ENUM.BARGAIN_LTS,
        isCompetition: type === BUSINESS_TYPE_ENUM.COMPETITION
      }
    },

    // 禁用新增按钮
    disableAddButton () {
      return !this.baseInfoData.exchangeRateType ||
        !this.baseInfoData[this.keyMap.standardCurrency] ||
        !this.baseInfoData[this.keyMap.currencyChangeDate]
    },

    // key map 计算一次缓存下来
    keyMap () {
      const mappingProp = key => {
        return mappingPropByBusinessTypeAndKey(this.businessType, key)
      }
      return {
        standardCurrency: mappingProp('standardCurrency'),
        currencyChangeDate: mappingProp('currencyChangeDate'),
        pricePrecision: mappingProp('pricePrecision'),
        currencyDesc: mappingProp('currencyDesc')
      }
    }
  },

  watch: {
    detailData: {
      handler (val) {
        this.currencyList = (val || [])
          .concat()
          // 过滤本位币不显示
          .filter(item => item.currencyCode !== this.baseInfoData[this.keyMap.standardCurrency])
      },
      immediate: true,
      deep: true
    },

    // 监听币种变更触发change事件
    currencyList: {
      handler (val) {
        this.$emit('change', val)
      },
      deep: true
    }
  },

  created () {
    this.columns = [
      // 币种
      {
        attrs: {
          minWidth: '100',
          label: t => t.$t('bid_mod.currencyName'),
          prop: 'currencyCode'
        },
        slot: 'currencyCode'
      },
      // 描述
      {
        attrs: {
          minWidth: '100',
          label: t => t.$t('bid_mod.currencyDesc'),
          prop: this.keyMap.currencyDesc
        },
        // 简易询价[LTS] 项目式询价[LTS]
        hidden: this.businessTypeCom.isInquiryNew || this.businessTypeCom.isBargainNew || this.businessTypeCom.isBidingNew,
        slot: 'currencyDesc'
      },
      // 汇率
      {
        attrs: {
          minWidth: '100',
          label: t => t.$t('bid_mod.priceTax'),
          prop: 'priceTax'
        }
      },
      // 价格精度
      {
        attrs: {
          minWidth: '100',
          label: t => t.$t('bid_mod.pricePrecision'),
          prop: this.keyMap.pricePrecision
        },
        slot: 'pricePrecision'
      },
      {
        attrs: {
          label: t => t.$t('common.operation'),
          fixed: 'right',
          width: 80
        },
        operations: [
          {
            key: 'deleteQuoteCurrency',
            event: 'deleteQuoteCurrency',
            name: this.$t('common.delete'),
            attrs: {
              type: 'text'
            }
          }
        ]
      }
    ]
  },

  methods: {
    // 初始化设置
    baseInfoInit (baseInfo) {
      // 初始化没值就默认人民币，精度设置2
      if (!baseInfo[this.keyMap.standardCurrency]) {
        baseInfo[this.keyMap.standardCurrency] = 'CNY'
        baseInfo[this.keyMap.pricePrecision] = 2
      }
    },
    /* 删除一行外币 */
    deleteQuoteCurrency (scope) {
      this.currencyList.splice(scope.$index, 1)
    },

    /* 新增一行外币 */
    addQuoteCurrency () {
      this.currencyList.push({
        currencyCode: '',
        [this.keyMap.currencyDesc]: '',
        priceTax: '',
        [this.keyMap.pricePrecision]: ''
      })
    },

    /* 选择一个外币，查询税率 */
    async getRateByCode (fromCode, scope) {
      if (!fromCode) {
        scope.row.priceTax = ''
        this.currencyList.splice(scope.$index, 1, scope.row)
        return
      }

      const toCode = this.baseInfoData[this.keyMap.standardCurrency]
      if (toCode === fromCode) {
        scope.row.priceTax = 1
        this.currencyList.splice(scope.$index, 1, scope.row)
        return
      }

      const paramData = {
        toCurrencyCode: toCode,
        fromCurrencyCode: fromCode,
        rateType: this.baseInfoData.exchangeRateType,
        exchangeDate: this.$dayjs(this.baseInfoData[this.keyMap.currencyChangeDate]).format('YYYY-MM-DD')
      }
      const data = await this.$api.base.purchase.purchaseExchangeRate(paramData)
      const list = data.data.list

      if (data && data.data && Array.isArray(list)) {
        if (list.length === 0) {
          this.$message.warning(
            `${fromCode} TO ${toCode} ${this.$t('bidMod.common.quoteCurrencyMsg1')}${this.$getDictLabel('EXCHANGE_RATE_TYPE', paramData.rateType)}${this.$t('bidMod.common.quoteCurrencyMsg2')}${paramData.exchangeDate}${this.$t('bidMod.common.quoteCurrencyMsg3')}`
          )
          scope.row.priceTax = ''
          this.currencyList.splice(scope.$index, 1, scope.row)
          return
        }
        scope.row.priceTax = list[0].priceTax || ''
        this.currencyList.splice(scope.$index, 1, scope.row)
      }
    },

    /* 返回当前数据 父组件外部调用 */
    getParamsData () {
      return this.currencyList
    },

    /* 清除数据 */
    clearData () {
      this.currencyList = []
    }
  }
}
</script>
