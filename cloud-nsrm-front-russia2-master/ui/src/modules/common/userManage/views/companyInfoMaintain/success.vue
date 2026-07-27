<template>
  <el-container>
    <el-main class="main-stepOne">
      <section class="boxs">
        <i class="el-icon-success icon-green icon-big margin" />
        <span class="margin mainFont">{{ $t('common.successSubmit') }}</span>
        <span class="margin greyFont">{{ $t('vendorMod.companyInformationCertified') }}</span>
        <el-button
          type="primary"
          class="margin button-click"
          @click="backTo"
        >
          {{ $t("vendorMod.viewCompanyInformation") }}
        </el-button>
      </section>
    </el-main>
  </el-container>
</template>
<script>
import OrganizationSelector from 'lib@/components/organization-selector'

export default {
  components: {},
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
    backTo () {
      this.$emit('goToWhere', 'main')
    }
  }
}
</script>
<style lang="scss" scoped>
.main-stepOne{
  position: relative;
  .boxs{
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translate(-50%,-50%);
    .mainFont{
      font-weight: 500;
      font-size: 24px;
      color: #393E45;
    }
    .margin{
      margin: 0 auto;
      display: block;
      text-align: center;
    }
    .icon-green{
      color: #52C718;
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
      color: #96999C;
    }
    .button-click{
      margin-top: 21px;
    }
  }
}
</style>
