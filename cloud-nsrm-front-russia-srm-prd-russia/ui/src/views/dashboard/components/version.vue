<template>
  <div class="copyright">
    <span>Copyright © 2016-2023 | 长城汽车股份有限公司版权所有 | 冀ICP备05008632号 | 版本信息：V2.0</span>
    <!-- <span>{{ getLogoInfo.copyright }}</span> -->
    <!-- <span class="version">{{ $t("vendorMod.copyrightVersion") }}</span> -->
  </div>
</template>
<script>
import { getSystemTheme } from '@/config/logo-config'
export default {
  name: 'Version',
  data () {
    return {
      getLogoInfo: getSystemTheme()
    }
  }
}
</script>
<style scoped lang="scss">
.copyright{
  font-size: 12px;
  line-height: 20px;
  color: #96999C;
  text-align: center;
  padding-top: 16px;
  span{
    display: inline-block;
    vertical-align: middle;
    padding: 0 8px;
    &.version{
      padding-left: 0px;
      &::before{
        content: "|";
        padding-right: 8px;
      }
    }
  }
}
</style>
