<template>
  <SrmDialog
    :title="$t('cusEntry.inq.batchPayMethodOrPayterm')"
    size="middle"
    :visible.sync="newVisible"
    :close-on-click-modal="false"
  >
    <el-form
      ref="form"
      :model="form"
      :rules="rules"
    >
      <SrmRow>
        <SrmCol :initCol="2">
          <el-form-item :label="$t('cusEntry.inq.payMethod')" prop="paymentMethod">
            <DictSelect
              v-model="form.paymentMethod"
              code="JC_PAYMENT_WAY"
            />
          </el-form-item>
        </SrmCol>
        <SrmCol :initCol="2">
          <el-form-item :label="$t('cusEntry.inq.payment')" prop="paymentTerm">
            <DictSelect
              v-model="form.paymentTerm"
              code="PAYMENT_PROVISION"
            />
          </el-form-item>
        </SrmCol>
      </SrmRow>
    </el-form>
    <div slot="footer">
      <el-button @click="newVisible = false">
        {{ $t('common.cancel') }}
      </el-button>
      <el-button
        type="primary"
        @click="confirmHandler"
      >
        {{ $t('common.confirm') }}
      </el-button>
    </div>
  </SrmDialog>
</template>

<script>
export default {
  name: 'BatchPayMethodOrPaytermDialog',
  props: {
    /* 弹窗显隐控制 */
    visible: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      form: {
        paymentMethod: '',
        paymentTerm: ''
      },
      rules: {
        paymentMethod: [{ required: true, message: this.$t('cusEntry.tipMessage.paymentMethodMsg') }],
        paymentTerm: [{ required: true, message: this.$t('cusEntry.tipMessage.paymentTermMsg') }]
      }
    }
  },
  computed: {
    newVisible: {
      get () {
        return this.visible
      },
      set (value) {
        this.$emit('update:visible', value)
      }
    }
  },
  methods: {
    /* 确认 */
    confirmHandler () {
      const {
        paymentMethod,
        paymentTerm
      } = this.form
      if (!paymentMethod && !paymentTerm) {
        this.$message.warning(this.$t('cusEntry.tipMessage.leastOneItem'))
        return false
      }
      this.newVisible = false
      this.$emit('updateRow', this.form)
    }
  }
}
</script>
