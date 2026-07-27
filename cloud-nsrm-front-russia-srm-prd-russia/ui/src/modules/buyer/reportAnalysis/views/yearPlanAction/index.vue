<template>
  <el-container class="report_container">
    <el-main>
      <FormWrapper
        :form-array="preArr"
        form-label-width="120px"
        @getFormData="getQuerydata"
      />

      <div class="iframe_wrapper">
        <iframe
          v-if="!!source"
          class="report_iframe"
          :src="source"
        />
      </div>
    </el-main>
  </el-container>
</template>
<script>
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { adaptDictData } from '@/utils'
import { getDictItemList } from '@/api/common'
import { sysPrefix } from '@/config/ipConfig'

export default {
  name: 'YearPlanAction',
  components: {
    FormWrapper
  },
  data () {
    return {
      priceCycle: null,
      currentPath: null,
      typeOpts: [],
      timeOpts: [],
      ifSignOpts: [],
      // 搜索表单配置
      preArr: [
        {
          prop: 'ym',
          label: this.$t('reportMod.monthly'),
          type: 'month'
        }
      ]
    }
  },
  computed: {
    source () {
      return this.currentPath
        ? `${sysPrefix()}/api-base/ureport/preview?_u=${this.currentPath}`
        : null
    }
  },
  created () {
  },
  methods: {
    getQuerydata (v) {
      var condition = ''
      if (v.ym) {
        condition += '&ym=' + v.ym
      }
      var url = 'database:年度计划执行情况.ureport.xml'
      if (condition != '') {
        url += condition
      }
      this.currentPath = url
      console.log(this.currentPath)
    },
    clickHandle (path) {
      this.currentPath = path
    }
  }
}
</script>
<style scoped lang="scss">
.report_container {
  height: 100%;
}
.report_iframe {
  width: 100%;
  height: 100%;
  position: absolute;
  border: 0;
  top: 0;
  bottom: 0;
}
.list-item {
  list-style: none;
}
.iframe_wrapper {
  position: relative;
  height: 100%;
}
</style>
