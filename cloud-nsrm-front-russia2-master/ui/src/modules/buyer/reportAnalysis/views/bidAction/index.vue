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
  name: 'BidAction',
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
      selectDictionary: {},
      preArr: [
        {
          prop: 'year',
          label: this.$t('reportMod.biddingYear'), // 招标年度
          type: 'year'
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
      if (v.year) {
        condition += '&year=' + v.year
      }
      var url = 'database:招标计划执行情况.ureport.xml'
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
      const codes = ['REPORT_CONTRACT_TYPE', 'YES_OR_NO'].map(i => ({
        dictCode: i
      }))
      getDictItemList(codes).then(res => {
        const [REPORT_CONTRACT_TYPE, YES_OR_NO] = res.data
        this.timeOpts = adaptDictData(
          REPORT_CONTRACT_TYPE.REPORT_CONTRACT_TYPE
        )
        this.ifSignOpts = adaptDictData(YES_OR_NO.YES_OR_NO)
        this.selectDictionary = {
          time: adaptDictData(REPORT_CONTRACT_TYPE.REPORT_CONTRACT_TYPE),
          ifSign: adaptDictData(YES_OR_NO.YES_OR_NO)
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
