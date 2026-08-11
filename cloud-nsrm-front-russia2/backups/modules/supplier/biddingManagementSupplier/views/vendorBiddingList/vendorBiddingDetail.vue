<template>
  <el-container
    class="flex-container the_vendorBiddingDetail_wrapper"
    direction="vertical"
  >
    <el-main>
      <el-tabs
        v-model="editableTabsValue"
        type="border-card"
      >
        <!--项目信息-->
        <el-tab-pane
          :label="$t('bidMod.projectInformation')"
          name="t11"
        >
          <ProjectInformation
            :biding-base="bidingBase"
            :outer-files="outerFiles"
            :has-sign-up-node="hasSignUpNode"
          />
        </el-tab-pane>

        <!--项目需求-->
        <el-tab-pane
          :label="$t('bidMod.projectRequirement')"
          name="t12"
        >
          <ProjectRequirement
            :is-current-tab="editableTabsValue === 't12'"
            :biding-id="scopeBidingId"
            :biding-base="bidingBase"
            :pricing-type="pricingType"
          />
        </el-tab-pane>

        <!--保证金信息-->
        <el-tab-pane
          v-if="hasBondNode"
          label="保证金信息"
          name="t16"
        >
          <BondPayInfo
            :business-type="BUSINESS_TYPE_ENUM.BIDING"
            :base-info="{ id: scopeBidingId }"
            :is-current-tab="editableTabsValue === 't16'"
          />
        </el-tab-pane>

        <!--报名信息-->
        <el-tab-pane
          v-if="hasSignUpNode"
          :label="$t('bidMod.registerInfo')"
          name="t13"
        >
          <SignUpInfo
            :is-current-tab="editableTabsValue === 't13'"
            :biding-id="scopeBidingId"
          />
        </el-tab-pane>

        <!--投标明细-->
        <el-tab-pane
          :label="$t('bidMod.bidDetail2')"
          name="t14"
        >
          <BidingDetail
            :is-current-tab="editableTabsValue === 't14'"
            :biding-id="scopeBidingId"
            :biding-base="bidingBase"
            :pricing-type="pricingType"
          />
        </el-tab-pane>

        <!--投标结果-->
        <el-tab-pane
          :label="$t('bidMod.bidResult')"
          name="t15"
        >
          <BidingResult
            :is-current-tab="editableTabsValue === 't15'"
            :biding-id="scopeBidingId"
          />
        </el-tab-pane>
      </el-tabs>
    </el-main>
  </el-container>
</template>

<script>
import { PRICING_TYPE_MAGIC } from '@/library/composition/biddingManagement/utils'
import { BUSINESS_TYPE_ENUM } from 'lib@/composition/origin/enum'
import ProjectInformation from './vendorBiddingDetail/projectInformation'
import ProjectRequirement from './vendorBiddingDetail/projectRequirement'
import SignUpInfo from './vendorBiddingDetail/signUpInfo'
import BidingDetail from './vendorBiddingDetail/bidingDetail'
import BidingResult from './vendorBiddingDetail/bidingResult'
import BondPayInfo from 'lib@/composition/origin/bondPay/bondPayInfo'

export default {
  name: 'BiddingProjectDetail',

  components: {
    ProjectInformation,
    ProjectRequirement,
    SignUpInfo,
    BidingDetail,
    BidingResult,
    BondPayInfo
  },

  data () {
    return {
      editableTabsValue: 't11',
      scopeBidingId: this.$attrs.params.row.bidingId || '',
      vendorId: this.$attrs.params.row.vendorId || '',
      bidingBase: {},
      currencyList: [],
      outerFiles: [],
      hasSignUpNode: false,
      hasBondNode: false,
      BUSINESS_TYPE_ENUM
    }
  },

  computed: {
    // 报价类型
    pricingType () {
      return {
        // 普通报价
        isSimplePricing: this.bidingBase.pricingType === PRICING_TYPE_MAGIC.SIMPLE_PRICING,
        // 公式报价
        isFormulPricing: this.bidingBase.pricingType === PRICING_TYPE_MAGIC.FORMULA_PRICING,
        // 模型报价
        isModelPricing: this.bidingBase.pricingType === PRICING_TYPE_MAGIC.MODEL_PRICING,
        // 模型报价
        isTemplatePricing: this.bidingBase.pricingType === PRICING_TYPE_MAGIC.TEMPLATE_PRICING
      }
    }
  },

  created () {
    this.getFormDetail()
  },

  methods: {
    /* 查询详情 */
    getFormDetail () {
      this.$http({
        url: `/api-bid/supplierCooperate/orderHead/getBiding/projectInfo/${this.scopeBidingId}`,
        method: 'GET',
        loading: true
      }).then(data => {
        if (data && data.data) {
          const responseData = data.data
          this.bidingBase = {
            ...(responseData.biding || {}),
            // 过滤去掉本币
            currencyList: (responseData.currencyList || []).filter(item => {
              return item.currencyCode !== responseData.biding.standardCurrency
            })
          }
          this.outerFiles = responseData.outerFiles || []
          this.hasSignUpNode = responseData.hasSignUpNode === 'Y'
          this.hasBondNode = responseData.hasBondNode === 'Y'
        }
      })
    }
  }
}
</script>

<style scoped lang="scss">
.the_vendorBiddingDetail_wrapper {
  :deep(.el-form .el-form-item) {
    margin-bottom: 0 !important;
  }
  .the_display_content {
    padding: 11px;
    .el-row {
      margin-bottom: 11px;
      font-size: 16px;
      span {
        padding-right: 11px;
        display: inline-block;
        color: #999;
      }
    }
    .the_display_footer {
      text-align: center !important;
    }
  }
}
</style>
