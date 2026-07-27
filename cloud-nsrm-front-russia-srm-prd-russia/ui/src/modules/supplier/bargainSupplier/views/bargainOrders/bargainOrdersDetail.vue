<template>
  <el-container class="flex-container bargain-orders-detail-wrap" direction="vertical">
    <el-main>
      <el-tabs v-model="editableTabsValue" type="border-card">
        <!--项目信息-->
        <el-tab-pane :label="$t('bidMod.projectInformation')" name="t11">
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
          lazy
        >
          <ProjectRequirement
            :is-current-tab="editableTabsValue === 't12'"
            :project-id="projectId"
            :bargain-base="bargainBase"
            :pricing-type="pricingType"
          />
        </el-tab-pane>

        <!--保证金信息-->
        <el-tab-pane
          v-if="hasBondNode"
          label="保证金信息"
          name="t16"
          lazy
        >
          <BondPayInfo
            :business-type="BUSINESS_TYPE_ENUM.BARGAIN_LTS"
            :base-info="{ id: projectId, idKey: 'projectId' }"
            :is-current-tab="editableTabsValue === 't16'"
          />
        </el-tab-pane>

        <!--报名信息-->
        <el-tab-pane
          v-if="hasSignUpNode"
          :label="$t('bidMod.registerInfo')"
          name="t13"
          lazy
        >
          <SignUpInfo :is-current-tab="editableTabsValue === 't13'" :project-id="projectId" />
        </el-tab-pane>

        <!--报价明细-->
        <el-tab-pane
          :label="$t('bidMod.quoteDetails')"
          name="t14"
          lazy
        >
          <QuoteDetail
            :is-current-tab="editableTabsValue === 't14'"
            :project-id="projectId"
            :bargain-base="bargainBase"
            :pricing-type="pricingType"
          />
        </el-tab-pane>

        <!--报价结果-->
        <el-tab-pane
          :label="$t('bidMod.quoteResult')"
          name="t15"
          lazy
        >
          <QuoteResult :is-current-tab="editableTabsValue === 't15'" :project-id="projectId" />
        </el-tab-pane>
      </el-tabs>
    </el-main>
  </el-container>
</template>

<script>
import { brgSupplierHttp } from 'mods@/bargainSupplier/api'
import { SOU_ORDER_TYPE_ENUM, BUSINESS_TYPE_ENUM } from 'lib@/composition/origin/enum'
import BondPayInfo from 'lib@/composition/origin/bondPay/bondPayInfoNew.vue'
import ProjectInformation from './bargainOrdersDetail/projectInformation.vue'
import ProjectRequirement from './bargainOrdersDetail/projectRequirement.vue'
import SignUpInfo from './bargainOrdersDetail/signUpInfo.vue'
import QuoteDetail from './bargainOrdersDetail/quoteDetail.vue'
import QuoteResult from './bargainOrdersDetail/quoteResult.vue'

export default {
  name: 'BargainOrdersDetail',

  components: {
    ProjectInformation,
    ProjectRequirement,
    SignUpInfo,
    QuoteDetail,
    QuoteResult,
    BondPayInfo
  },

  data () {
    return {
      editableTabsValue: 't11',
      projectId: this.$attrs.params.row.projectId || '',
      vendorId: this.$attrs.params.row.vendorId || '',
      bargainBase: {},
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
        isSimplePricing: this.bargainBase.orderType === SOU_ORDER_TYPE_ENUM.SIMPLE,
        // 公式报价
        isFormulaPricing: this.bargainBase.orderType === SOU_ORDER_TYPE_ENUM.FORMULA,
        // 模型报价 已废弃
        isModelPricing: this.bargainBase.orderType === SOU_ORDER_TYPE_ENUM.MODEL
      }
    }
  },

  created () {
    this.getFormDetail()
  },

  methods: {
    /* 查询详情 */
    async getFormDetail () {
      const response = await brgSupplierHttp.order.projectInfo(this.projectId)

      if (response && response.data) {
        const {
          currencyList = [],
          souFileList = [],
          processConfig = {},
          ...project
        } = response.data

        this.bargainBase = {
          ...project,
          // 过滤去掉本币
          currencyList: (currencyList || []).filter(item => item.currencyCode !== project.standardCurrency)
        }
        // 可用外币
        this.currencyList = currencyList.concat()
        // 供应商投标附件
        this.outerFiles = souFileList.concat()
        // 报名节点
        this.hasSignUpNode = processConfig.signUpManagement === 'Y'
        // 保证金节点
        this.hasBondNode = processConfig.bondManagement === 'Y'
      }
    }
  }
}
</script>

<style scoped lang="scss">
.bargain-orders-detail-wrap ::v-deep {
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
