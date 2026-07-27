<template>
  <div>
    <!-- 步骤条 -->
    <el-steps
      v-if="status == '' || !status || status == 'DRAFT'"
      class="comInfosteps"
      :active="stepsActive"
      finish-status="success"
      :align-center="true"
    >
      <!-- 申请账号 -->
      <el-step :title="$t('vendorMod.applyUser')" />
      <!-- 选择企业性质 -->
      <el-step :title="$t('vendorMod.selectCompanyType')" />
      <!-- 填写企业信息 -->
      <el-step :title="$t('vendorMod.finishCompanyInfo')" />
      <!-- 提交成功 -->
      <el-step :title="$t('common.successSubmit')" />
    </el-steps>

    <!--审批完成后 APPROVED-->
    <section v-if="status == 'APPROVED'" class="boxs_success boxs_heater">
      <div>
        <i class="el-icon-circle-check" /><span>{{ $t('mainHeater.boxsHeater') }}</span>
      </div>
      <div class="small_font">
        {{ $t('mainHeater.boxsHeater2') }}
      </div>
    </section>

    <!--已驳回 REJECTED-->
    <section v-if="status == 'REJECTED'" class="boxs_rejected boxs_heater">
      <div>
        <i class="el-icon-circle-close" /><span>{{ $t('mainHeater.reject') }}</span>
      </div>
      <div class="small_font">
        {{ $t('mainHeater.reject2') }}
      </div>
      <div class="small_font">
        {{ $t('announcements.title4', {flowRemark: flowRemark}) }}
      </div>
    </section>

    <!--审批中 SUBMITTED-->
    <section v-if="status == 'SUBMITTED'" class="boxs_submitted boxs_heater">
      <div>
        <i class="el-icon-warning-outline" /><span>{{ $t('mainHeater.approval') }}</span>
      </div>
      <div class="small_font">
        {{ $t('mainHeater.approval2') }}
      </div>
    </section>
  </div>
</template>
<script>
import OrganizationSelector from 'lib@/components/organization-selector'

export default {
  components: {},
  props: {
    stepsActive: {
      type: Number
    },
    status: {
      type: String
    },
    flowRemark: {
      type: String
    }
  },
  data () {
    return {

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
  border-radius: 4px;
  height: 82px;
  margin-right: 220px;
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
