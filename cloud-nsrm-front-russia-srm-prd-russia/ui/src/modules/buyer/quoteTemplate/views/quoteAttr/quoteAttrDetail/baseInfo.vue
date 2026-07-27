<template>
  <el-form
    ref="detailForm"
    :model="detailFormData"
    :rules="detailFormRules"
    label-position="top"
    class="detail-form-wrap form-incontainer"
  >
    <SrmRow :gutter="32">
      <!--属性编码-->
      <SrmCol :init-col="4">
        <el-form-item :label="$t('quoteTemplate.attrNo')" prop="attrNo">
          <el-input v-model="detailFormData.attrNo" disabled />
        </el-form-item>
      </SrmCol>

      <!--属性名称-->
      <SrmCol :init-col="4">
        <el-form-item :label="$t('quoteTemplate.attrName')" prop="attrName">
          <el-input v-model="detailFormData.attrName" :disabled="readonly" />
        </el-form-item>
      </SrmCol>

      <!--状态-->
      <SrmCol :init-col="4">
        <el-form-item :label="$t('common.status')" prop="attrStatus">
          <DictSelect
            v-model="detailFormData.attrStatus"
            code="SOU_QUOTE_TEMP_ATTR_STATUS"
            disabled
          />
        </el-form-item>
      </SrmCol>

      <!--创建日期-->
      <SrmCol :init-col="4">
        <el-form-item :label="$t('common.creationTime')" prop="creationDate">
          <el-input v-model="detailFormData.creationDate" disabled />
        </el-form-item>
      </SrmCol>
    </SrmRow>
  </el-form>
</template>

<script>
/**
 * 基础信息
 */
export default {
  name: 'BaseInfo',

  props: {
    formData: {
      type: Object,
      required: true
    },
    readonly: {
      type: Boolean,
      default: false
    }
  },

  data () {
    const checkAttrName = (_rule, value, callback) => {
      if (value && !/^[_0-9a-zA-Z\u4e00-\u9fa5]*$/.test(value)) {
        callback(new Error('属性名称只能输入英文字母数组下划线以及中文！'))
      }
      callback()
    }
    return {
      detailFormRules: {
        attrName: [
          { required: true, message: this.$t('common.pleaseInput') },
          { validator: checkAttrName, trigger: 'blur' }
        ]
      }
    }
  },

  computed: {
    detailFormData: {
      get: function () {
        return this.formData
      },
      set: function (val) {
        this.$emit('update:formData', val)
      }
    }
  },

  methods: {
    /* 校验表单 */
    validateForm () {
      return new Promise(resolve => {
        this.$refs.detailForm.validate(async valid => {
          resolve(!!valid)
          if (!valid) {
            this.__focus_error__()
          }
        })
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.detail-form-wrap {
  padding: 15px 0;
}
</style>
