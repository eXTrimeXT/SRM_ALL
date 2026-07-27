<template>
  <SrmDialog
    title="关闭公示信息"
    size="middle"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <el-form ref="form" :model="form" :rules="formRules">
      <SrmRow>
        <SrmCol :init-col="1">
          <el-form-item prop="closePublicReason" label="关闭公示原因">
            <el-input v-model="form.closePublicReason" type="textarea" :autosize="{minRows:4,maxRows:6}" />
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
  name: 'ClosePublicDialog',
  components: {
  },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    readonly: {
      type: Boolean,
      default: false
    },
    editRows: {
      type: Array,
      default () {
        return []
      }
    }
  },
  data () {
    return {
      form: {
        closePublicReason: null
      },
      formRules: {
        closePublicReason: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }]
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
          this.$emit('confirm', this.form.closePublicReason)
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
