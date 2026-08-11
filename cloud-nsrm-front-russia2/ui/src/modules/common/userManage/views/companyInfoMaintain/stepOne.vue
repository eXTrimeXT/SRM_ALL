<template>
  <el-container>
    <el-main class="main-stepOne">
      <section class="boxs">
        <i class="el-icon-warning icon-red icon-big margin" />
        <span class="margin mainFont">{{ $t('vendorMod.companyInformationNotCertified') }}</span>
        <span class="margin greyFont">{{ $t('vendorMod.sorryCompanyInformationNotCertified') }}</span>
        <button class="button-click margin" @click="goTo('company-nature')">
          {{ $t('vendorMod.continueFilling') }}
        </button>
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
    goTo (where) {
      this.$emit('goToWhere', where)
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
      font-size: 24px;
      font-weight: 500;
      color: #393E45;
    }
    .margin{
      margin: 0 auto;
      display: block;
      text-align: center;
    }
    .icon-red{
      color: #FF4A4D;
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
      width: 80px;
      height: 30px;
      background: #FFFFFF;
      border: 1px solid #96999C;
      border-radius: 4px;
      font-size: 12px;
      font-weight: 400;
      text-align: center;
      color: #393E45;
      line-height: 20px;
      margin-top: 21px;
      cursor:pointer;
    }
  }
}
</style>
