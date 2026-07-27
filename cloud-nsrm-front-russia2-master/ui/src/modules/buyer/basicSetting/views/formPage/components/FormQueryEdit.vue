<template>
  <el-form
    ref="wrapperForm"
    :model="value"
  >
    <el-row :gutter="32">
      <el-col :span="6">
        <el-form-item
          prop="componentType"
          :label="$t('dataConfMod.componentType')"
        >
          <DictSelect
            v-model="value.componentType"
            code="FORM_COMPONENT_TYPE"
            @change="changeComponentType"
          />
        </el-form-item>
      </el-col>
      <el-col :span="6">
        <el-form-item
          prop="elementTag"
          :label="$t('reportSetting.elementTag')"
        >
          <el-input v-model="value.elementTag" />
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
</template>
<script>
export default {
  name: 'FormQueryEdit',
  props: {
    visible: {
      type: Boolean,
      default: () => {
        return false
      }
    },
    value: {
      type: Object,
      default: () => {
        return {
          labelWidth: null,
          componentType: null,
          elementTag: null
        }
      }
    }
  },
  methods: {
    changeComponentType (componentType) {
      this.value.elementTag = this.$getDictLabel('FORM_COMPONENT_TYPE', componentType)
    },
    async validate () {
      const res = await this.$refs.pageForm.validate()
      return res
    }
  }
}
</script>
