<template>
  <el-form
    ref="detailForm"
    :model="detailFormData"
    :rules="detailFormRules"
    label-position="top"
    class="detail-form-wrap form-incontainer"
  >
    <SrmRow :gutter="32">
      <!--模板编码-->
      <SrmCol :init-col="4">
        <el-form-item :label="$t('quoteTemplate.tempNo')" prop="tempNo">
          <el-input v-model="detailFormData.tempNo" disabled />
        </el-form-item>
      </SrmCol>

      <!--模板名称-->
      <SrmCol :init-col="4">
        <el-form-item :label="$t('quoteTemplate.tempName')" prop="tempName">
          <el-input v-model="detailFormData.tempName" :disabled="readonly" />
        </el-form-item>
      </SrmCol>

      <!--状态-->
      <SrmCol :init-col="4">
        <el-form-item :label="$t('common.status')" prop="tempStatus">
          <DictSelect
            v-model="detailFormData.tempStatus"
            code="SOU_QUOTE_TEMP_STATUS"
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

      <!--创建人-->
      <SrmCol :init-col="4">
        <el-form-item :label="$t('common.creator')" prop="createdUserName">
          <el-input v-model="detailFormData.createdUserName" disabled />
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
    return {
      detailFormRules: {
        tempName: [{ required: true, message: this.$t('common.pleaseInput') }]
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
