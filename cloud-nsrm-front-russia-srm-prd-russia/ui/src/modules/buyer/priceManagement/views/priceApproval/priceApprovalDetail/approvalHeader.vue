<template>
  <el-form
    ref="approvalHeaderForm"
    :model="approvalHeaderData"
    :rules="approvalHeaderDataRules"
    label-width="80px"
    label-position="top"
    class="approval-header-form"
    :disabled="pageFlag.isReadonly || pageFlag.isApproval"
  >
    <srm-row>
      <srm-col>
        <!--f 标题-->
        <el-form-item
          :label="$t('bidMod.title')"
          prop="approvalTitle"
        >
          <el-input
            v-model="approvalHeaderData.approvalTitle"
            maxlength="100"
            show-word-limit
            :disabled="isHandMark"
          />
        </el-form-item>
      </srm-col>

      <srm-col>
        <!--f 寻源方式-->
        <el-form-item :label="$t('bidMod.sourceType')">
          <DictSelect
            v-model="approvalHeaderData.sourceType"
            code="SOURCING_TYPE"
            disabled
          />
        </el-form-item>
      </srm-col>

      <srm-col>
        <!--f 寻源单号-->
        <el-form-item :label="$t('bidMod.businessNo')">
          <el-input
            v-model="approvalHeaderData.sourceNo"
            disabled
          />
        </el-form-item>
      </srm-col>

      <srm-col>
        <!--f 创建人-->
        <el-form-item :label="$t('bidMod.bidingCreatedBy')">
          <el-input
            v-model="approvalHeaderData.createdUserName"
            disabled
          />
        </el-form-item>
      </srm-col>

      <srm-col>
        <!--创建时间-->
        <el-form-item :label="$t('bidMod.creationDate')">
          <el-date-picker
            v-model="approvalHeaderData.creationDate"
            disabled
            type="date"
          />
        </el-form-item>
      </srm-col>

      <srm-col>
        <!--f 状态-->
        <el-form-item :label="$t('bidMod.status')">
          <DictSelect
            v-model="approvalHeaderData.status"
            code="PRICE_APPROVAL_STATUS"
            disabled
          />
        </el-form-item>
      </srm-col>

      <srm-col>
        <!--f 决标方式-->
        <el-form-item
          :label="$t('bidMod.bidingAwardWay')"
          prop="awareWay"
        >
          <DictSelect
            v-model="approvalHeaderData.awareWay"
            code="RFQ_QUOTE_TYPE"
            :disabled="!!approvalHeaderData.sourceType"
          />
        </el-form-item>
      </srm-col>

      <srm-col>
        <!--f 是否更新至价格库-->
        <el-form-item
          :label="$t('bidMod.ifUpdateToPriceLib')"
          prop="ifUpdatePriceLibrary"
        >
          <DictSelect
            v-model="approvalHeaderData.ifUpdatePriceLibrary"
            code="YES_OR_NO"
            :disabled="pageType.isInquiry"
          />
        </el-form-item>
      </srm-col>

      <srm-col>
        <!--f 币种-->
        <el-form-item
          :label="$t('bidMod.allAurrency')"
          prop="standardCurrency"
        >
          <DictSelect
            v-model="approvalHeaderData.standardCurrency"
            code="currency"
            :disabled="pageType.isInquiry || isHandMark"
          />
        </el-form-item>
      </srm-col>

      <srm-col>
        <!--价格精度-->
        <el-form-item
          :label="$t('bid_mod.pricePrecision')"
          prop="priceNum"
        >
          <DictSelect
            v-model="approvalHeaderData.priceNum"
            code="PRICE_PRECISION"
            :dict-class="dictClass"
            clearable
            :disabled="pageType.isInquiry || isHandMark"
          />
        </el-form-item>
      </srm-col>

      <srm-col>
        <!--汇率类型-->
        <el-form-item
          :label="$t('bid_mod.exchangeRateType')"
          prop="exchangeRateType"
        >
          <DictSelect
            v-model="approvalHeaderData.exchangeRateType"
            code="EXCHANGE_RATE_TYPE"
            clearable
            :disabled="(pageType.isInquiry || isHandMark) && !pageType.isAuct"
          />
        </el-form-item>
      </srm-col>

      <srm-col>
        <!--币种转换日期-->
        <el-form-item
          :label="$t('bid_mod.currencyChangeDate')"
          prop="currencyExchangeDate"
        >
          <el-date-picker
            v-model="approvalHeaderData.currencyExchangeDate"
            type="date"
            format="yyyy-MM-dd"
            value-format="yyyy-MM-dd"
            :disabled="(pageType.isInquiry || isHandMark) && !pageType.isAuct"
          />
        </el-form-item>
      </srm-col>

      <srm-col>
        <!--f 中标金额-->
        <el-form-item :label="$t('bidMod.bidAmount')">
          <el-input
            v-model="approvalHeaderData.bidAmount"
            v-input-format
            :disabled="pageType.isInquiry || isHandMark"
          />
        </el-form-item>
      </srm-col>

      <srm-col>
        <!--f 价格审批号-->
        <el-form-item :label="$t('bidMod.approvalNo')">
          <el-input
            v-model="approvalHeaderData.approvalNo"
            disabled
          />
        </el-form-item>
      </srm-col>

      <srm-col :init-col="1">
        <!--f 需求概述-->
        <el-form-item :label="$t('bidMod.requiremenOverview')">
          <el-input
            v-model="approvalHeaderData.demandSummary"
            type="textarea"
            :autosize="{ minRows: 2, maxRows: 5 }"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </srm-col>

      <srm-col :init-col="1">
        <!--f 说明-->
        <el-form-item :label="$t('vendorMod.operationMemo')">
          <el-input
            v-model="approvalHeaderData.description"
            type="textarea"
            :autosize="{ minRows: 2, maxRows: 5 }"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </srm-col>
    </srm-row>
  </el-form>
</template>

<script>
/**
 * 展示表单
 */
import { createDictClass } from 'lib@/utils/dict/dict-utils'

export default {
  name: 'ApprovalHeader',

  props: {
    approvalHeader: {
      type: Object,
      required: true
    },
    pageFlag: {
      type: Object,
      required: true
    },
    pageType: {
      type: Object,
      required: true
    },
    approvalBiddingItemList: {
      type: Array,
      required: true
    }
  },

  data () {
    return {
      dictClass: createDictClass({
        PRICE_PRECISION: [
          { id: 0, label: '0', value: 0 },
          { id: 1, label: '1', value: 1 },
          { id: 2, label: '2', value: 2 },
          { id: 3, label: '3', value: 3 },
          { id: 4, label: '4', value: 4 },
          { id: 5, label: '5', value: 5 },
          { id: 6, label: '6', value: 6 }
        ]
      }, false)
    }
  },

  computed: {
    approvalHeaderData: {
      get: function () {
        return this.approvalHeader
      },
      set: function (val) {
        this.$emit('update:approvalHeader', val)
      }
    },
    approvalBiddingItemListData: {
      get: function () {
        return this.approvalBiddingItemList
      },
      set: function (val) {
        this.$emit('update:approvalBiddingItemList', val)
      }
    },
    approvalHeaderDataRules () {
      let rules = {
        approvalTitle: [{ required: true, message: '请输入标题' }]
      }
      if (this.pageType.isHandMake) {
        // 手工创建
        rules = {
          ...rules,
          awareWay: [{ required: true, message: '请选择决标方式' }],
          ifUpdatePriceLibrary: [{ required: true, message: '请选择是否更新到价格库' }],
          standardCurrency: [{ required: true, message: '请选择币种' }],
          priceNum: [{ required: true, message: '请选择价格精度' }],
          exchangeRateType: [{ required: true, message: '请选择汇率类型' }],
          currencyExchangeDate: [{ required: true, message: '请选择币种转换日期' }]
        }
      }
      if (this.pageType.isInquiry) {
        // 简易询价
      }
      return rules
    },
    // 是否是手工创建
    isHandMark () {
      return this.approvalHeaderData.sourceType && this.approvalHeaderData.sourceType !== 'HAND_MAKE'
    }
  },

  methods: {
    /* 校验表单 */
    validateForm () {
      return new Promise(resolve => {
        this.$refs.approvalHeaderForm.validate(valid => {
          if (!valid) {
            this.__focus_error__()
          }
          resolve(!!valid)
        })
      })
    }
  }
}
</script>
