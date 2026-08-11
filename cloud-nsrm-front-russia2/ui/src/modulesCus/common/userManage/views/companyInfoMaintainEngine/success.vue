<template>
  <el-container class="successContainer">
    <!--已提交待审批状态显示-->
    <el-main v-if="status == 'SUBMITTED'" class="main-stepOne">
      <section class="boxs">
        <span class="success-icon margin">
          <i class="el-icon-success"></i>
        </span>
        <span class="margin mainFont">{{ $t('common.successSubmit') }}</span>
        <!-- 企业信息已提交成功请等待审批 -->
        <span class="margin greyFont">{{ $t('dashboard.companyInfoSubmitted') }}</span>
        <section class="flex">
          <!-- 撤回提交信息 -->
          <el-button
            class="margin button-click"
            @click="recall"
          >
            {{ $t("dashboard.withdrawSubmissionInfo") }}
          </el-button>
          <!-- 查看企业信息 -->
          <el-button
            type="primary"
            class="margin button-click"
            @click="backToMain"
          >
            {{ $t("vendorMod.viewCompanyInformation") }}
          </el-button>
        </section>
      </section>
    </el-main>
    <!--审批通过第一次显示-->
    <el-main v-if="status == 'APPROVED'" class="main-stepOne">
      <section class="boxs">
        <span class="success-icon margin">
          <i class="el-icon-success"></i>
        </span>
        <span class="margin mainFont">{{ $t('common.successRegister') }}</span>
        <span class="margin greyFont">{{ $t('vendorMod.companyInformationSuccess') }}</span>
        <el-button
          type="primary"
          class="margin button-click"
          @click="backToSystem"
        >
          {{ $t("vendorMod.enterSystem") }}
        </el-button>
      </section>
    </el-main>
    <el-main v-if="status == 'REJECTED'" class="main-stepOne">
      <section class="boxs">
        <i class="el-icon-error icon-grey icon-big margin" />
        <span class="margin mainFont">{{ $t('common.failedRegister') }}</span>
        <span class="margin greyFont">{{ $t('vendorMod.companyInformationFailed') }}{{ flowRemark }}</span>
        <el-button
          type="primary"
          class="margin button-click"
          @click="backToMain"
        >
          {{ $t("vendorMod.backCompanyInformation") }}
        </el-button>
      </section>
    </el-main>
  </el-container>
</template>
<script>
import * as path from '@/utils/path'
import { transformMQL } from 'lib@/utils/util'
import { vendorOptCommonApi } from 'mod@/common/userManage/api'

export default {
  components: {},
  props: {
    status: {
      type: String,
      default: 'APPROVED'
    },
    flowRemark: {
      type: String,
      default: ''
    }
  },
  data () {
    return {
      source: this.$t('cusEntry.vendorMod.withdrawRegister') // 注册撤回
    }
  },
  computed: {

  },
  async created () {

  },
  mounted () {

  },
  updated () {

  },
  methods: {
    /* ip地址监控 */
    monitorIpAddress (data) {
      return this.$http({
        url: '/api-sou/bids/ip/address/ipAddress/save',
        method: 'POST',
        data
      })
    },
    recall () {
      this.$prompt('', this.$t('bidMod.withdrawReason'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        inputType: 'textarea'
      }).then(({ value }) => {
        let saveData = transformMQL.save('CompanyInfo', [{ 'companyId': this.$store.getters.userInfo.companyId, 'flowRemark': value }], 'vendorWithdraw')
        vendorOptCommonApi.withdrawCompanyMQL(saveData).then(async () => {
          this.$message({
            message: this.$t('dashboard.withdrawSuccess'),
            type: 'success'
          })
          this.backToMain()
          const {
            companyName,
            companyCode,
            companyId
          } = this.$store.getters.userInfo
          await this.monitorIpAddress({
            supplierId: companyId,
            supplierCode: companyCode,
            supplierName: companyName,
            source: this.source
          })
        })
      })
    },
    backToSystem () {
      let saveData = transformMQL.save('CompanyInfo', [{ 'companyId': this.$store.getters.userInfo.companyId }], 'updateFirstLog')
      vendorOptCommonApi.saveCompanyMQL(saveData).then(() => {
        this.$store.dispatch('user/initSystem').then(() => {
          this.$router.push({ path: path.resolve('/dashboard') })
        })
      })
    },
    backToMain () {
      this.$emit('goToWhere', 'main')
    }
  }
}
</script>
<style lang="scss" scoped>
.success-icon{
  display: inline-block;
  text-align: center;
  width: 80px;
  height: 80px;
  padding: 10px;
  background: rgba(82, 187, 38, 0.2);
  border-radius: 50%;
  .el-icon-success{
    color: #52BB26;
    font-size: 60px;
  }
}
.successContainer{
  height: 400px;
}
.flex{
  display: flex;
  .margin{
    margin: 21px 10px 0!important;
  }
}
.main-stepOne{
  position: relative;
  .boxs{
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translate(-50%,-50%);
    .mainFont{
      font-weight: 500;
      font-size: 18px;
      color: #52BB26;
      line-height: 26px;
      font-weight: 500;
    }
    .margin{
      margin: 15px auto;
      display: block;
      text-align: center;
    }
    .icon-green{
      color: #52C718;
    }
    .icon-grey{
      color: #909399;
    }
    .icon-big{
      font-size: 64px;
      width: 64px;
      height: 64px;
      margin-bottom: 13px;
    }
    .greyFont{
      margin-top: 10px;
      font-size: 14px;
      color: #51555B;
    }
    .button-click{
      margin-top: 21px;
    }
  }
}
</style>
