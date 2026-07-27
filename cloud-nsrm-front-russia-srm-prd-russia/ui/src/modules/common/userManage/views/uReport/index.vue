<template>
  <el-container
    class="flex-container-notab the_ureport_wrapper"
    direction="vertical"
  >
    <el-main>
      <iframe
        class="ureport"
        :src="iframeUrl"
      />
    </el-main>
  </el-container>
</template>
<script>
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { sysPrefix } from '@/config/ipConfig'
export default {
  name: 'MessageyMaintenance',
  components: {
    MainHeader, FormWrapper
  },
  data () {
    return {
      iframeUrl: '',
      pageSize: 15,
      gridId: 'list',
      currentRow: null,
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      preArr: [
        { prop: 'mcode',
          label: () => this.$t('dataConfMod.mcode')// '消息编码'
        }, { prop: 'mname',
          label: () => this.$t('dataConfMod.mname')// '消息值'
        }, { prop: 'mlang',
          label: () => this.$t('dataConfMod.mlang'),
          type: 'select', // '消息语言'
          options: [
            { label: this.$t('common.zh'), value: 'zh' },
            { label: this.$t('common.en'), value: 'en' },
            { label: this.$t('common.ja'), value: 'ja' }
          ]
        }
      ],
      queryParam: {}
    }
  },
  created () {
    this.iframeUrl = `${sysPrefix()}/api-base/ureport/designer`
  },
  methods: {

  }
}
</script>
<style scoped lang="scss">
.ureport{
  width: 100%;
  height: 100%;
  position: absolute;
  border: 0;
  top: 0;
  bottom: 0;
}
</style>
