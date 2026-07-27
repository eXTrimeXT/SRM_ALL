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
  name: 'ContractPlanAction',
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
          prop: 'type',
          label: this.$t('reportMod.contractClassification'), // 合同分类
          type: 'select',
          formattor: value => {
            if (this.typeOpts && this.typeOpts.length) {
              const dict = this.typeOpts.find(i => i.value === value)
              return dict ? dict.label : value
            }
          }
        },
        {
          prop: 'ifSign',
          label: this.$t('reportMod.ifSign'), // 是否签订
          type: 'select',
          formattor: value => {
            if (this.ifSignOpts && this.ifSignOpts.length) {
              const dict = this.ifSignOpts.find(i => i.value === value)
              return dict ? dict.label : value
            }
          }
        },
        {
          prop: 'person',
          label: this.$t('qualitySynergy.responsible') // 责任人
        },
        {
          prop: 'planNo',
          label: this.$t('reportMod.planNo') // 计划单号
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
      if (v.type) {
        condition += '&type=' + v.type
      }
      if (v.person) {
        condition += '&person=' + v.person
      }
      if (v.planNo) {
        condition += '&planNo=' + v.planNo
      }
      if (v.ifSign) {
        if (v.ifSign === 'Y') {
          condition += '&ifSignY=' + v.ifSign
        } else if (v.ifSign === 'N') {
          condition += '&ifSignN=' + v.ifSign
        }
      }
      var url = 'database:合同计划执行.ureport.xml'
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
