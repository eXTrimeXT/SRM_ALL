<template>
  <!-- <SrmDialog
    title="废弃"
    size="small"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  > -->
  <SrmDialog
    :title="$t('components.approvalHead.headers.abandon')"
    size="small"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <el-form
      ref="form"
      label-width="120px"
      :model="form"
      :rules="rules"
    >
      <!-- <el-form-item prop="discardDescription" label="废弃原因"> -->
      <el-form-item prop="discardDescription" :label="$t('cusEntry.bidMod.instruction')">
        <el-input v-model="form.discardDescription" type="textarea" :autosize="{minRows:4,maxRows:6}" />
      </el-form-item>
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
  name: 'DiscardDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      form: {
        discardDescription: null
      },
      rules: {
        // discardDescription: [{ required: true, message: '必填项', trigger: 'blur' }]
        discardDescription: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }]
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
    async handleConfirm () {
      await this.$refs.form.validate()
      this.$emit('confirm', this.form)
    },
    resetFields () {
      this.$refs.form.resetFields()
    }
  }
}
</script>
