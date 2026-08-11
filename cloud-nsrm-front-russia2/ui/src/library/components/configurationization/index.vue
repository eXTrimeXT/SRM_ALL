<template>
  <div>
    <template v-if="modelConfig && modelConfig.dimConfigMap[dimensionCode] && type == 'form'">
      <ModelConfigForm
        :ref="dimensionCode"
        :refss="dimensionCode"
        :dimConfig="modelConfig.dimConfigMap[dimensionCode]"
        :formValue="dimDataValue"
        :disabled="disabled"
      />
    </template>
    <template v-if="modelConfig && modelConfig.dimConfigMap[dimensionCode] && type == 'table'">
      <ModelConfigTable
        :ref="dimensionCode"
        :refss="dimensionCode"
        :dimConfig="modelConfig.dimConfigMap[dimensionCode]"
        :tableValue="dimDataValue"
        :addButtonFlag="addButtonFlag"
        :disabled="disabled"
      />
    </template>
  </div>
</template>
<script>
import ModelConfigForm from 'mod@/common/userManage/views/ModelConfig/ModelConfigForm'
import ModelConfigTable from 'mod@/common/userManage/views/ModelConfig/ModelConfigTable'
import { modelConfigApi } from '@/api/modelConfig'
export default {
  name: 'Configurationization',
  components: {
    ModelConfigForm,
    ModelConfigTable
  },
  props: {
    pageCode: {
      type: [Number, String]
    },
    dimensionCode: {
      type: [Number, String]
    },
    businessId: {
      type: [Number, String]
    },
    disabled: {
      type: Boolean
    },
    getModelConfig: {
      type: Object,
      default: null
    },
    dimDataValues: {
      type: Array,
      default: null
    }
  },
  data () {
    return {
      modelConfig: {
        dimConfigMap: {},
        formDimVOList: []
      },
      dimDataValue: [],
      type: '', // 确定该维度是什么类型的（表单还是表格）
      addButtonFlag: 'Y',
      loadings: true
    }
  },
  watch: {
    getModelConfig: {
      deep: true,
      handler (data) {
        if (data != null && data != {}) {
          this.modelConfigFun(this.getModelConfig)
        }
      }
    },
    dimDataValues: {
      deep: true,
      handler (data) {
        if (data != null && data != []) {
          this.dimDataValue = data
        }
      }
    }
  },
  created () {
    // 获取动态值(此处当在编辑或者查看使用, this.$attrs.params.row.materialTrialId为业务单据id)
    if (this.dimDataValues == null) {
      this.getDimDataById(this.businessId)
    }

    // 获取动态配置(复制粘贴即可)
    if (this.getModelConfig == null) {
      this.getConfig()
    }
  },
  methods: {
    // 校验时使用，未调通
    submitFormFather () {
      return this.$refs[this.dimensionCode].submitForm()
    },
    getConfig () {
      modelConfigApi.getModelConfig(this.pageCode).then(result => {
        this.$store.commit('app/SET_MODEL_CONFIG', result)
        this.modelConfigFun(result)
      })
    },
    modelConfigFun (result) {
      if (result) {
        this.modelConfig = result.data
        if (this.modelConfig.formDimVOList) {
          this.modelConfig.formDimVOList.forEach(item => {
            if (item.dimCode == this.dimensionCode) {
              this.type = item.dimType
              if (item.addButtonFlag) {
                this.addButtonFlag = item.addButtonFlag
              }
            }
            // 默认展示所有动态维度
            // this.activeDims.push(item.dimCode)
          })
          this.$emit('showType', this.type)
        }
      }
      this.loadings = false
    },
    getDimDataById (businessId) {
      if (businessId) {
        modelConfigApi.getDimDataById(businessId).then(result => {
          this.$store.commit('app/SET_MODEL_DATA', result)
          this.dimDataValue = result.data
        })
      }
    },
    getDimDataFromVue (businessId) {
      let that = this
      let dimDataList = []
      let formData
      if (that.$refs[this.dimensionCode]) {
        formData = that.$refs[this.dimensionCode].getDataValue()
      }
      try {
        dimDataList.push(...formData)
        if (dimDataList && dimDataList.length > 0) {
          dimDataList.forEach(item => {
            item.businessId = businessId
          })
        }
      } catch (e) {
        console.log(e, 'catch')
      }

      return dimDataList
    },
    showTileFlag (dimTitleShowFlag) {
      if (dimTitleShowFlag === 'N') {
        return 'noShow'
      }
      return ''
    },
    save (businessIdSave) {
      let dimData = this.getDimDataFromVue(businessIdSave)
      return dimData
    },
    // 这个函数主要用于校验,如果校验成功会返回true,如果失败的话会返回false
    check () {
      const datas = this.save('')
      console.log(datas)
      let bol = false // 判断检验的时候，如果为false通过检验，如果为true不通过校验
      if (this.type == 'form') { // 表单的时候
        datas.forEach((e) => {
          if (e.emptyFlag === 'Y' && e.fieldValue === '') {
            bol = true
          }
        })
      } else { // 校验表格的时候后

      }
      return bol
    }
  }
}
</script>

<style lang="scss" scoped>
.noShow > :first-child {
  display: none;
}
</style>
