<template>
  <!-- <SrmDialog
    title="取消发货"
    size="small"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    append-to-body
  > -->
  <SrmDialog
    :title="$t('bidMod.cancelShipment')"
    size="small"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    append-to-body
  >
    <el-form ref="formRef" :model="form" label-width="80px" label-position="left">
      <!-- <el-form-item
        prop="extCancelReason"
        label="取消原因"
        :rules="[{ required: true, message: '请选择取消原因' }]"
      > -->
      <el-form-item
        prop="extCancelReason"
        :label="$t('cusEntry.bidSuperviseReport.cancelReason')"
        :rules="[{ required: true, message: $t('cusEntry.supplement20250121..pleaseSelectCancelReason') }]"
      >
        <dict-select v-model="form.extCancelReason" code="DELIVERY_NOTE_CANCEL_REASON" />
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t('common.close') }}
      </el-button>
      <el-button type="primary" @click="confirmReturn">
        {{ $t('common.confirm') }}
      </el-button>
    </div>
  </SrmDialog>
</template>

<script>
import { transformMQL } from 'lib@/utils/util'
export default {
  name: 'CancleDialog',

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    currentRow: {
      type: Object,
      default: () => {}
    },
    selectionList: {
      type: Array,
      default: () => []
    }
  },

  data () {
    return {
      form: {
        extCancelReason: null
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
    confirmReturn () {
      this.$refs.formRef.validate(valid => {
        if (valid) {
          // 列表页的取消发货
          if (!this.selectionList.length) {
            let params = {
              deliveryNoteId: this.currentRow.deliveryNoteId,
              extCancelReason: this.form.extCancelReason
            }
            const saveData = transformMQL.save('DeliveryNoteVendor', [params], 'extCancel')
            this.$http({
              url: '/api-sup-ce/api-ql/DeliveryNoteVendor/extCancel',
              method: 'POST',
              data: saveData,
              loading: true
            }).then(res => {
              this.dialogVisible = false
              this.$message.success(this.$t('common.success'))
              this.$emit('after-cancle')
            })
          } else {
            // 详情页的取消发货
            let params = {
              deliveryNoteDetailIds: this.selectionList.map(item => item.deliveryNoteDetailId),
              extCancelReason: this.form.extCancelReason
            }
            const saveData = transformMQL.save('DeliveryNoteDetailVendor', [params], 'extCancel')
            this.$http({
              url: '/api-sup-ce/api-ql/DeliveryNoteDetailVendor/extCancel',
              method: 'POST',
              data: saveData,
              loading: true
            }).then(res => {
              this.dialogVisible = false
              this.$message.success(this.$t('common.success'))
              this.$emit('after-cancle')
            })
          }
        }
      })
    }
  }
}
</script>
