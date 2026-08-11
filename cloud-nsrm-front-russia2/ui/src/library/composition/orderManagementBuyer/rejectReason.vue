<template>
  <div class="reject-reason">
    <!-- 驳回弹窗 -->
    <srm-dialog
      v-el-drag-dialog
      :title="$t('contractMod.refusedReason')"
      size="middle"
      :visible.sync="visible"
      :destroy-on-close="true"
      :close-on-click-modal="false"
      v-bind="$attrs"
      v-on="$listeners"
    >
      <el-form
        ref="form"
        :model="form"
        :rules="rules"
      >
        <el-form-item prop="rejectReason" :label="$t('contractMod.refusedReason')">
          <el-input
            v-model="form.rejectReason"
            type="textarea"
            :rows="3"
            show-word-limit
            :maxlength="250"
          />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="visible = false">
          {{ $t('common.cancel') }}
        </el-button>
        <el-button type="primary" @click="handleReject">
          {{ $t('common.confirm') }}
        </el-button>
      </div>
    </srm-dialog>
  </div>
</template>

<script>
export default {
  name: 'RejectReason',
  props: {
    visible: {
      type: Boolean,
      default: () => {
        return false
      }
    }
  },
  data () {
    return {
      form: {
        rejectReason: ''
      },
      rules: {
        rejectReason: [{ required: true, message: this.$t('common.pleaseInput'), trigger: 'blur' }]
      }
    }
  },
  methods: {
    handleReject () {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.$emit('handleReject', this.form.rejectReason)
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
  .reject-reason {
  }
</style>
