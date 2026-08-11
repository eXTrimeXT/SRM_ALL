<template>
  <div>
    <!-- 步骤条 -->
    <!--v-if="status == '' || !status || status == 'DRAFT'"-->
    <el-steps
      v-if="!registered"
      class="comInfosteps"
      :active="stepsActive"
      finish-status="success"
      :align-center="true"
    >
      <!-- 注册须知 -->
      <el-step :title="$t('vendorMod.registrationPolicy')" />
      <!-- 注册账号 -->
      <el-step :title="$t('vendorMod.registerAccount')" />
      <!-- 选择注册类型 -->
      <el-step :title="$t('vendorMod.registrationType')" />
      <!-- 填写认证信息 -->
      <el-step :title="$t('vendorMod.authenticationInformation')" />
      <!-- 等待审批 -->
      <el-step :title="$t('vendorMod.pendingApproval')" />
      <!-- 完成企业信息认证 -->
      <el-step :title="$t('vendorMod.informationAuthentication')" />
    </el-steps>

    <!--审批完成后 APPROVED-->
    <section v-if="status == 'APPROVED'" class="boxs_success boxs_heater">
      <div>
        <i class="el-icon-circle-check" /><span>{{ approvedTips.tips1 }}</span>
      </div>
      <div class="small_font">
        {{ approvedTips.tips2 }}
      </div>
    </section>

    <!--已驳回 REJECTED-->
    <section v-if="status == 'REJECTED'" class="boxs_rejected boxs_heater">
      <div>
        <i class="el-icon-circle-close" /><span>{{ rejectTips.tips1 }}</span>
      </div>
      <div class="small_font">
        {{ rejectTips.tips2 }}
      </div>
      <div class="small_font">
        {{ $t('cusEntry.vendorMod.rejectReason') }}{{ flowRemark }}
      </div>
    </section>

    <!--审批中 SUBMITTED-->
    <section v-if="status == 'SUBMITTED'" class="boxs_submitted boxs_heater">
      <div>
        <i class="el-icon-warning-outline" /><span>{{ approvingTips.tips1 }}</span>
      </div>
      <div class="small_font">
        {{ approvingTips.tips2 }}
      </div>
    </section>
  </div>
</template>
<script>

export default {
  components: {},
  props: {
    stepsActive: {
      type: Number
    },
    status: {
      type: String,
      default: 'DRAFT'
    },
    fileStatus: {
      type: String,
      default: 'DRAFT'
    },
    flowRemark: {
      type: String
    },
    registered: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {

    }
  },
  computed: {
    setFlowRemark () {
      const replaceRemark = this.flowRemark?.replace(/\s+/g, '; ') ?? ''
      return replaceRemark ? replaceRemark.includes(';') ? replaceRemark.slice(0, replaceRemark.length - 2) : replaceRemark : ''
    },
    approvedTips () {
      let res = {}
      if (this.fileStatus == 'APPROVED') {
        res = {
          tips1: this.$t('cusEntry.mainHeater.boxsHeater'),
          tips2: this.$t('cusEntry.mainHeater.boxsHeater2')
        }
      } else {
        res = {
          tips1: this.$t('mainHeater.boxsHeater'),
          tips2: this.$t('mainHeater.boxsHeater2')
        }
      }
      return res
    },
    rejectTips () {
      let res = {}
      if (this.fileStatus == 'REJECTED') {
        res = {
          tips1: this.$t('cusEntry.mainHeater.reject'),
          tips2: this.$t('cusEntry.mainHeater.reject2')
        }
      } else {
        res = {
          tips1: this.$t('mainHeater.reject'),
          tips2: this.$t('mainHeater.reject2')
        }
      }
      return res
    },
    approvingTips () {
      let res = {}
      if (this.fileStatus == 'SUBMITTED') {
        res = {
          tips1: this.$t('cusEntry.mainHeater.approval'),
          tips2: this.$t('cusEntry.mainHeater.approval2')
        }
      } else {
        res = {
          tips1: this.$t('mainHeater.approval'),
          tips2: this.$t('mainHeater.approval2')
        }
      }
      return res
    }
  },
  async created () {

  },
  mounted () {

  },
  updated () {

  },
  methods: {
    goTo (where) {
      this.$emit('goToWhere', where)
    }
  }
}
</script>
<style lang="scss" scoped>
.boxs_success{
  background: #F6FBF4;
  border: 1px solid #A8DD92;
}
.boxs_rejected{
  background: #FEF5F6;
  border: 1px solid #FB9EA3;
  padding: 12px 23px !important;
  height: 92px !important;
}
.boxs_submitted{
  background: #FEFAF4;
  border: 1px solid #FAD295;
}
.boxs_heater{
  width: 100%;
  border-radius: 4px;
  height: 82px;
  margin-right: 220px;
  margin-bottom: 10px;
  padding: 21px 23px;
  span{
    font-size: 14px;
    color: #393E45;
    line-height: 16px;
    font-weight: 500;
    margin-left: 5px;
  }
  .small_font{
    font-size: 12px;
    color: #51555B;
    line-height: 20px;
    margin: 5px 23px;
  }
}
.el-icon-circle-check{
  font-size: 18px;
  color: #52C718;
  float: left;
}
.el-icon-circle-close{
  font-size: 18px;
  color: #FF4A4D;
  float: left;
}
.el-icon-warning-outline{
  font-size: 18px;
  color: #FAAE16;
  float: left;
}
.comInfosteps{
  padding: 12px 0px 12px 0;
}
</style>
