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
            :bargain-base="bargainBase"
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
            :bargain-id="scopeBargainId"
            :bargain-base="bargainBase"
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
            business-type="BARGAIN"
            :base-info="{ id: scopeBargainId }"
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
            :bargain-id="scopeBargainId"
          />
        </el-tab-pane>

        <!--报价明细-->
        <el-tab-pane
          :label="$t('bidMod.quoteDetails')"
          name="t14"
        >
          <BargainDetail
            :is-current-tab="editableTabsValue === 't14'"
            :bargain-id="scopeBargainId"
            :bargain-base="bargainBase"
            :pricing-type="pricingType"
          />
        </el-tab-pane>
        <!--报价结果-->
        <el-tab-pane
          :label="$t('bidMod.quoteResult')"
          name="t15"
        >
          <BargainResult
            :is-current-tab="editableTabsValue === 't15'"
            :bargain-id="scopeBargainId"
          />
        </el-tab-pane>
      </el-tabs>
    </el-main>
  </el-container>
</template>

<script>
import ProjectInformation from './bargainVendorDetail/projectInformation'
import ProjectRequirement from './bargainVendorDetail/projectRequirement'
import SignUpInfo from './bargainVendorDetail/signUpInfo'
import BargainDetail from './bargainVendorDetail/bargainDetail'
import BargainResult from './bargainVendorDetail/bargainResult'
import BondPayInfo from 'lib@/composition/origin/bondPay/bondPayInfo'

export default {
  name: 'BargainVendorDetail',

  components: {
    ProjectInformation,
    ProjectRequirement,
    SignUpInfo,
    BargainDetail,
    BargainResult,
    BondPayInfo
  },

  data () {
    return {
      editableTabsValue: 't11',
      scopeBargainId: this.$attrs.params.row.bargainId || '',
      vendorId: this.$attrs.params.row.vendorId || '',
      bargainBase: {},
      currencyList: [],
      outerFiles: [],
      hasSignUpNode: false,
      hasBondNode: false
    }
  },

  computed: {
    // 报价类型
    pricingType () {
      return {
        // 普通报价
        isSimplePricing: this.bargainBase.pricingType === 'SIMPLE_PURCHASER',
        // 公式报价
        isFormulPricing: this.bargainBase.pricingType === 'FORMULA_PURCHASER',
        // 模型报价
        isTemplatePricing: this.bargainBase.pricingType === 'MODEL_PURCHASER'
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
        url: `/api-brg/supplierCooperate/orderHead/getBargain/projectInfo/${this.scopeBargainId}`,
        method: 'GET',
        loading: true
      }).then(data => {
        if (data && data.data) {
          const responseData = data.data
          this.bargainBase = {
            ...(responseData.bargain || {}),
            // 过滤去掉本币
            currencyList: (responseData.currencyList || []).filter(item => {
              return item.currencyCode !== responseData.bargain.standardCurrency
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
:deep(.the_vendorBiddingDetail_wrapper) {
  .el-form .el-form-item {
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
