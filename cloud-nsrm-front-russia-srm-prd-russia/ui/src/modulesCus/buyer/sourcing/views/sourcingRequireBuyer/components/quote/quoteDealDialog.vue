<template>
  <SrmDialog
    title="报名处理"
    size="middle"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <el-form ref="form" :model="form" :rules="formRules">
      <SrmRow>
        <SrmCol :init-col="1">
          <el-form-item prop="applyHandleType" label="报名处理方式">
            <DictSelect
              v-model="form.applyHandleType"
              code="SOU_APPLY_HANDLE_TYPE"
            />
          </el-form-item>
        </SrmCol>
        <SrmCol v-if="form.applyHandleType !== 'ALREADY_PAID'" :init-col="1">
          <el-form-item prop="applyHandleReason" label="报名处理原因">
            <el-input v-model="form.applyHandleReason" type="textarea" :autosize="{minRows:4,maxRows:6}" />
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
  name: 'QuoteDealDialog',
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
        applyHandleType: null,
        applyHandleReason: null
      },
      formRules: {
        applyHandleType: [{ required: true, message: this.$t('common.requiredField'), trigger: 'change' }],
        applyHandleReason: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }]
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
  created () {

  },
  methods: {
    handleConfirm () {
      this.$refs.form.validate(valid => {
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
