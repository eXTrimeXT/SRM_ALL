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
  name: 'DeliveryRateWerks',
  components: {
    FormWrapper
  },
  data () {
    return {
      priceCycle: null,
      currentPath: null,
      preArr: [
        {
          prop: 'creationDateList',
          label: this.$t('reportMod.queryRange'), // 查询范围
          type: 'daterange'
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
    // this.initDictionary();
  },
  methods: {
    getQuerydata (v) {
      if (v && v.creationDateList) {
        v.creationDateBegin = v.creationDateList[0]
        v.creationDateEnd = v.creationDateList[1]
        delete v.creationDateList
      }
      var condition = ''
      if (v.creationDateBegin) {
        condition += '&startDate=' + v.creationDateBegin
      }
      if (v.creationDateEnd) {
        condition += '&endDate=' + v.creationDateEnd
      }
      var url = 'database:交货达成率(工厂).ureport.xml'
      if (condition !== '') {
        url += condition
      }
      this.currentPath = url
      console.log(this.currentPath)
    },
    clickHandle (path) {
      this.currentPath = path
    },
    initDictionary () {
      const params = ['ELECTRONIC_REPORT'].map(i => ({
        dictCode: i
      }))
      getDictItemList(params).then(res => {
        const [ELECTRONIC_REPORT] = res.data
        const electronicReport = adaptDictData(
          ELECTRONIC_REPORT.ELECTRONIC_REPORT
        )
        this.electronicReport = electronicReport
        this.currentPath = electronicReport[0]
          ? electronicReport[0].value
          : null
      })
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
