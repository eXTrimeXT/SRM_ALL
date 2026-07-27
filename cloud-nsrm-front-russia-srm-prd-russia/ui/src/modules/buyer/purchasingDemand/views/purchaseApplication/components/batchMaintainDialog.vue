<template>
  <!-- 选择父级菜单弹框 -->
  <srm-dialog
    :visible.sync="visibleDialog"
    :title="$t('vendorMod.batchMaintain')"
    size="middle"
    destroy-on-close
    :close-on-click-modal="false"
    v-bind="$attrs"
    v-on="$listeners"
  >
    <el-form
      ref="batchMaintainRef"
      :model="batchMaintainForm"
      label-width="80px"
      label-position="top"
    >
      <!-- 需求数量 -->
      <el-form-item :label="$t('purchaseDemand.requirementQuantity')" prop="requirementQuantity">
        <el-input v-model="batchMaintainForm.requirementQuantity" type="Number" />
      </el-form-item>

      <!-- 需求日期 -->
      <el-form-item :label="$t('purchaseDemand.requirementDate')" prop="requirementDate">
        <el-date-picker
          v-model="batchMaintainForm.requirementDate"
          type="date"
          format="yyyy-MM-dd"
          :picker-options="pickerOptions"
          value-format="yyyy-MM-dd"
        />
      </el-form-item>
      <!-- 需求部门 -->
      <el-form-item :label="$t('purchaseDemand.dmandLineRequest')" prop="dmandLineRequest">
        <dict-select v-model="batchMaintainForm.dmandLineRequest" code="DMAND_LINE_REQUEST" />
      </el-form-item>
      <!-- 收货地址 -->
      <el-form-item :label="$t('purchaseDemand.ceeaDeliveryPlaceOut')" prop="receiveAddress">
        <DictSelect
          v-model="batchMaintainForm.receiveAddress"
          :code="requirementHead.organizationId"
          custom-select-type="RECEIVE_ADDRESS"
          @change-value="(val, element) => changeSiteInfo(batchMaintainForm, element)"
        />
      </el-form-item>
    </el-form>
    <div slot="footer" class="topComment">
      <el-button @click="visibleDialog = false">
        {{ $t('common.cancel') }}
      </el-button>
      <el-button type="primary" @click="batchMaintainSubmit">
        {{ $t('common.confirm') }}
      </el-button>
    </div>
  </srm-dialog>
</template>
<script>

export default {
  name: 'BatchMaintainDialog',
  components: { },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    requirementHead: {
      type: Object,
      default: () => {}
    }
  },
  data () {
    return {
      visibleDialog: false,
      batchMaintainForm: {
        requirementQuantity: null,
        requirementDate: null,
        dmandLineRequest: null,
        receiveAddress: null
      },
      pickerOptions: {
        disabledDate (time) {
          const today = new Date()
          today.setHours(0)
          today.setMinutes(0)
          today.setSeconds(0)
          today.setMilliseconds(0)
          return time.getTime() < today.getTime()
        }
      }
    }
  },
  watch: {
    visible (sign) {
      this.visibleDialog = sign
      if (sign) {
        this.batchMaintainForm = {}
      }
    }
  },
  methods: {
    batchMaintainSubmit () {
      this.$emit('submit', this.batchMaintainForm)
    },
    // 收货地点选择
    changeSiteInfo (row, { element }) {
      this.$set(row, 'receiveContact', element ? element.receiver : '')
      this.$set(row, 'receiveTelephone', element ? element.receiverPhone : '')
      this.$set(row, 'receiveAddress', element ? element.siteName : '')
    }
  }
}
</script>
