<template>
  <SrmDialog
    :title="title"
    size="small"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <el-form
      ref="form"
      :model="form"
      :rules="rules"
    >
      <el-form-item prop="reason" :label="title + '原因'">
        <el-input v-model="form.reason" type="textarea" :autosize="{minRows:4,maxRows:6}" />
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
    },
    title: {
      type: String,
      default: ''
    }
  },
  data () {
    return {
      form: {
        reason: null
      },
      rules: {
        reason: [{ required: true, message: '必填项', trigger: 'blur' }]
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
      this.$emit('confirm', this.form.reason)
    },
    resetFields () {
      this.$nextTick(() => {
        this.$refs.form.resetFields()
      })
    }
  }
}
</script>
