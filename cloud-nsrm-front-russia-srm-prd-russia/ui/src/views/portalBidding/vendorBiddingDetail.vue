<template>
  <el-container class="the_vendorBiddingDetail_wrapper" direction="vertical">
    <el-main>
      <!-- <AppHeader pageType="gateway" /> -->
      <img src="../login/img/bulletinBoard.jpg" style="width:100%" alt="">
      <ProjectInformation
        :biding-base="bidingBase"
        :outer-files="outerFiles"
        :has-sign-up-node="hasSignUpNode"
        class="main"
      />
      <ProjectRequirement
        :biding-base="bidingBase"
        :require-info="requireInfo"
        :pricing-type="pricingType"
        class="main"
      />
      <CToolbar>
        <template slot="right">
          <el-button type="primary" @click="signUp">
            {{ $t('sourcingBuyer.signUp') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>

<script>
import CToolbar from 'lib@/components/c-toolbar'
import { mapGetters } from 'vuex'
import Header from '@/layout/components/Header/index.vue'
import ProjectInformation from './vendorBiddingDetail/projectInformation'
import ProjectRequirement from './vendorBiddingDetail/projectRequirement'
import { getToken, setRedirectUrl } from '@/utils/auth'

export default {
  name: 'BiddingProjectDetail',

  components: {
    ProjectInformation,
    ProjectRequirement,
    AppHeader: Header,
    CToolbar
  },

  data () {
    return {
      scopeBidingId: this.$route.query.bidingId || '',
      bidingBase: {},
      outerFiles: [],
      requireInfo: [],
      hasSignUpNode: false,
      hasBondNode: false
    }
  },

  computed: {
    // 报价类型
    pricingType () {
      return {
        // 普通报价
        isSimplePricing: this.bidingBase.pricingType === 'SIMPLE_PRICING',
        // 公式报价
        isFormulPricing: this.bidingBase.pricingType === 'FORMULA_PRICING',
        // 模型报价
        isTemplatePricing: this.bidingBase.pricingType === 'TEMPLATE_PRICING'
      }
    }
  },

  created () {
    this.getFormDetail()
  },

  methods: {
    /* 查询详情 */
    async getFormDetail () {
      let res = await this.$http({
        url: `/api-sou/sou-firstPage/getSouInfo/${this.scopeBidingId}`,
        method: 'GET',
        loading: true
      })
      if (res && res.data) {
        this.bidingBase = res.data.projectInfo
        this.outerFiles = res.data.projectInfo.souFileList
        this.requireInfo = res.data.requireInfo
      }
    },
    signUp () {
      const token = getToken()
      if (!token) {
        this.$confirm('报名前需先登录系统，若无账号需先注册', '需要先登录系统', {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        }).then(() => {
          let redirectUrl = window.location.hash
          setRedirectUrl(redirectUrl)
          this.$router.push({ path: '/login' })
        })
      } else {
        // 报名
        this.$http({
          url: `/api-sou/sou-firstPage/getSouInfo/${this.scopeBidingId}`,
          method: 'GET',
          loading: true
        }).then(res => {
          if (res && res.data) {
            let { projectInfo, vendorInfo } = res.data
            let flag = projectInfo.projectStatus === 'ACCEPT_SIGN_UP' && (vendorInfo.length === 0 || vendorInfo[0].signUpStatus === 'NO_SIGN_UP') && new Date().getTime() < new Date(projectInfo.signUpEndTime).getTime()
            if (flag) {
              this.toSignUp(projectInfo, vendorInfo)
            } else {
              this.$message.info('招标单据当前状态不支持报名或投标')
            }
          }
        })
      }
    },
    toSignUp ({ projectId, souNo, souName, projectStatus, signUpEndTime }, vendorInfo) {
      this.$router.push({
        // path: '/biddingVendor/orders',
        name: 'biddingOrders',
        params: {
          from: 'portalBidding',
          funName: 'signUp',
          projectInfo: {
            projectId,
            souNo,
            souName,
            projectStatus,
            signUpEndTime,
            signUpStatus: vendorInfo.length === 0 ? 'NO_SIGN_UP' : vendorInfo[0].signUpStatus
          }
        }
      })
    }
  }
}
</script>

<style scoped lang="scss">
.main {
  width: 70%;
  max-width: 1200px;
  margin: 15px auto 0;
}

:deep(.the_vendorBiddingDetail_wrapper) {
  padding-bottom: 80px;

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
