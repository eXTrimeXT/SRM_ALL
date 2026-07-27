<template>
  <SrmDialog
    title="取消类型"
    size="small"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    append-to-body
  >
    <el-form ref="formRef" :model="form">
      <el-form-item
        prop="extReturnRequiremnt"
        label="是否回退需求池"
        :rules="[{ required: true, message: '请选择是否回退需求池', trigger: 'blur' }]"
      >
        <el-radio-group v-model="form.extReturnRequiremnt" @input="getExtReturnRequiremnt">
          <el-radio label="Y">
            是
          </el-radio>
          <el-radio label="N">
            否
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item
        prop="closedCause"
        label="取消原因"
        :rules="[{ required: true, message: '请输入取消原因', trigger: 'blur' }]"
      >
        <el-input v-model="form.closedCause" />
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
    orderInfoList: {
      type: Array,
      default: () => []
    },
    cancleFlag: {
      type: String,
      default: ''
    }
  },

  data () {
    return {
      form: {
        closedCause: '',
        extReturnRequiremnt: ''
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
    getExtReturnRequiremnt (val) {
      this.form.extReturnRequirement = val
    },
    confirmReturn () {
      this.$refs.formRef.validate(valid => {
        if (valid) {
          let url = ''
          let saveData = {}
          let params = {
            extReturnRequirement: this.form.extReturnRequirement,
            closedCause: this.form.closedCause
          }
          if (this.cancleFlag == 'detailList') { // 订单明细列表退回
            params.orderDetailIds = this.orderInfoList
            saveData = transformMQL.save('OrderDetail', [params], 'extCancel')
            url = '/api-sup-ce/api-ql/OrderDetail/extCancel'
          } else { // 订单列表退回
            params.orderId = this.currentRow.orderId
            saveData = transformMQL.save('Order', [params], 'extCancel')
            url = '/api-sup-ce/api-ql/Order/extCancel'
          }
          this.$http({
            url,
            method: 'POST',
            data: saveData,
            loading: true
          }).then(res => {
            this.dialogVisible = false
            this.$message.success('操作成功！')
            this.$emit('after-cancle')
          })
        } else return false
      })
    }
  }
}
</script>
