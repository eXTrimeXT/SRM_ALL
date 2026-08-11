<template>
  <!-- 报名登记 -->
  <SrmDialog
    :title="$t('cusEntry.common.signUpInfo')"
    size="middle"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <el-form ref="form" :model="form" :rules="formRules">
      <SrmRow>
        <SrmCol :init-col="1">
          <!-- 报名公司 -->
          <el-form-item prop="companyName" :label="$t('cusEntry.common.signUpCompany')">
            <el-input v-model="form.companyName" />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="1">
          <!-- 联系人 -->
          <el-form-item prop="contactName" :label="$t('cusEntry.vendorMod.contactName')">
            <el-input v-model="form.contactName" />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="1">
          <!-- 联系电话 -->
          <el-form-item prop="contactMobile" :label="$t('cusEntry.bidSuperviseReport.phone')">
            <el-input v-model="form.contactMobile" />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="1">
          <el-form-item prop="contactEmail" :label="$t('common.email')">
            <el-input v-model="form.contactEmail" />
          </el-form-item>
        </SrmCol>
      </SrmRow>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t("common.cancel") }}
      </el-button>
      <el-button type="primary" @click="handleConfirm">
        {{ $t("common.confirm") }}
      </el-button>
    </div>
  </SrmDialog>
</template>
<script>

export default {
  name: 'SignUpDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      form: {
        companyName: null,
        contactName: null,
        contactMobile: null,
        contactEmail: null
      },
      formRules: {
        companyName: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        contactName: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        contactMobile: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        contactEmail: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }]
      }
    }
  },
  computed: {
    dialogVisible: {
      get: function () {
        return this.visible
      },
      set: function (val) {
        this.$emit('update:visible', val)
      }
    }
  },
  methods: {
    handleConfirm () {
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.$emit('confirm', this.form)
        }
      })
    },
    resetFields () {
      this.$nextTick(() => {
        this.$refs.form.resetFields()
      })
    }
  }
}
</script>
