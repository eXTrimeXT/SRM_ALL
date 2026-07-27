<template>
  <SrmDialog
    title="驳回"
    size="middle"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <el-form
      ref="form"
      label-width="80px"
      :model="form"
      :rules="rules"
    >
      <el-form-item prop="rejectDescription" label="驳回原因">
        <el-input v-model="form.rejectDescription" type="textarea" :autosize="{minRows:4,maxRows:6}" />
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
  name: 'RejectDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      form: {
        rejectDescription: null
      },
      rules: {
        rejectDescription: [{ required: true, message: '必填项', trigger: 'blur' }]
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
