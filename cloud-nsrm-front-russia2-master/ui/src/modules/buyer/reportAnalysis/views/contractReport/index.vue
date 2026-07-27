<template>
  <el-container class="report_container">
    <el-main>
      <FormWrapper
        :form-array="preArr"
        :select-dictionary="selectDictionary"
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
  name: 'ContractReport',
  components: {
    FormWrapper
  },
  data () {
    return {
      priceCycle: null,
      currentPath: null,
      typeOpts: [],
      timeOpts: [],
      // 搜索表单配置
      selectDictionary: {},
      preArr: [
        {
          prop: 'time',
          label: this.$t('reportMod.contractExpiryDay'), // 合同到期天数
          type: 'select',
          formattor: value => {
            if (this.timeOpts && this.timeOpts.length) {
              const dict = this.timeOpts.find(i => i.value === value)
              return dict ? dict.label : value
            }
          }
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
    this.initDictionary()
  },
  methods: {
    getQuerydata (v) {
      if (v && v.creationDateList) {
        v.creationDateBegin = v.creationDateList[0]
        v.creationDateEnd = v.creationDateList[1]
        delete v.creationDateList
      }
      var condition = ''
      if (v.time) {
        condition += '&light=' + v.time
      }
      var url = 'database:合同预警报表.ureport.xml'
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
      const codes = ['REPORT_CONTRACT_TIME'].map(i => ({
        dictCode: i
      }))
      getDictItemList(codes).then(res => {
        const [REPORT_CONTRACT_TIME] = res.data
        this.timeOpts = adaptDictData(
          REPORT_CONTRACT_TIME.REPORT_CONTRACT_TIME
        )
        this.selectDictionary = {
          time: adaptDictData(REPORT_CONTRACT_TIME.REPORT_CONTRACT_TIME)
        }
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
