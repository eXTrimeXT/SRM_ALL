<template>
  <SrmDialog
    size="xLarge"
    :title="$t('bidMod.startNewQuote')"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    append-to-body
  >
    <el-form
      ref="continuePriTalkForm"
      :model="continuePriTalkFormData"
      :rules="continuePriTalkFormRules"
      inline
      label-position="right"
      label-width="130px"
    >
      <el-form-item :label="$t('bidMod.newQuoteStartTime')" prop="orderStartTime" style="margin-bottom: 20px">
        <el-date-picker
          v-model="continuePriTalkFormData.orderStartTime"
          type="datetime"
          :format="$formatDatePickerTime"
          value-format="yyyy-MM-dd HH:mm:ss"
          :picker-options="pickerOptions"
        />
      </el-form-item>

      <el-form-item :label="$t('bidMod.newQuoteEndTime')" prop="orderEndTime" style="margin-bottom: 20px">
        <el-date-picker
          v-model="continuePriTalkFormData.orderEndTime"
          type="datetime"
          :format="$formatDatePickerTime"
          value-format="yyyy-MM-dd HH:mm:ss"
          :picker-options="pickerOptions"
        />
      </el-form-item>
    </el-form>

    <!--邀请询价-->
    <template v-if="isInviteTender">
      <p>{{ $t('bidMod.inviteNewVendor') }}</p>
      <InviteSuppliers
        ref="inviteSuppliers"
        :business-type="BUSINESS_TYPE_ENUM.INQUIRY_LTS"
        :material-data="materialData"
        :base-info="header"
        :invite-suppliers-data="newRoundVendorList"
        :showRecommendVendor="false"
        :nextRound="true"
      />
    </template>
    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t('common.cancel') }}
      </el-button>
      <el-button type="primary" @click="saveContinuePriTalkFormData">
        {{ $t('common.confirm') }}
      </el-button>
    </div>
  </SrmDialog>
</template>

<script>
/**
 * 发起新一轮议价
 */
import purInqApi from 'modcb@/centralizedPurchase/api'
import { BUSINESS_TYPE_ENUM, SOU_PUBLISH_SCOPE_ENUM } from 'lib@/composition/origin/enum'
import InviteSuppliers from 'modcb@/inquiry/views/inquiryManagement/inquiryDetail/inviteSuppliers'

export default {
  name: 'ContinuePriTalkDialog',

  components: {
    InviteSuppliers
  },

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    header: {
      type: Object,
      required: true
    },
    /* 需要发起新一轮的物料数据 */
    materialList: {
      type: Array,
      default: () => []
    },
    /* 立项阶段的所有供应商 */
    vendorList: {
      type: Array,
      default: () => []
    }
  },

  data () {
    return {
      continuePriTalkFormData: {
        orderStartTime: '',
        orderEndTime: ''
      },
      continuePriTalkFormRules: {
        orderStartTime: [
          { required: true, message: this.$t('common.pleaseSelect'), trigger: 'change' },
          { validator: this.validateDateTime, trigger: 'change' }
        ],
        orderEndTime: [
          { required: true, message: this.$t('common.pleaseSelect'), trigger: 'change' },
          { validator: this.validateDateTime, trigger: 'change' }
        ]
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
      },
      materialData: [],
      BUSINESS_TYPE_ENUM
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
    },
    // 是否邀请询价
    isInviteTender () {
      return this.header.publishScope === SOU_PUBLISH_SCOPE_ENUM.INVITE_TENDER
    },
    /* 立项阶段供应商数据 */
    newRoundVendorList () {
      return this.vendorList.map(item => ({
        ...item,
        newVendorTag: 'N'
      }))
    }
  },
  watch: {
    visible: {
      immediate: true,
      handler (newValue) {
        if (newValue) {
          this.$nextTick(() => {
            this.$refs.inviteSuppliers.renderInviteSuppliersTable()
          })
        }
      }
    }
  },
  created () {
    if (this.isInviteTender) {
      this.getInqInfo()
    }
  },

  methods: {
    /* 查询询价单信息 */
    async getInqInfo () {
      const response = await purInqApi.init.getInqInfo(this.header.projectId)
      // 获取物料列表
      if (response && response.data) {
        this.materialData = response.data.requireInfo || []
      }
    },

    /* 校验时间 */
    validateDateTime (_rule, value, callback) {
      if (!value) {
        callback(new Error(this.$t('common.pleaseSelect')))
      }
      if (this.continuePriTalkFormData.orderStartTime && this.continuePriTalkFormData.orderEndTime) {
        const orderStartTimeTime = new Date(this.continuePriTalkFormData.orderStartTime).getTime()
        const orderEndTimeTime = new Date(this.continuePriTalkFormData.orderEndTime).getTime()
        if (orderStartTimeTime >= orderEndTimeTime) {
          callback(new Error(this.$t('bidMod.startTimeLessEndTime')))
        }
      }
      callback()
    },

    /* 确定提交 */
    saveContinuePriTalkFormData () {
      this.$refs.continuePriTalkForm.validate(async valid => {
        if (!valid) {
          return
        }

        const paramsData = {
          projectId: this.header.projectId,
          startNow: false,
          inqChooseVendorList: this.$refs.inviteSuppliers.getSuppliersPermissionData(),
          inqChooseSouItemIds: this.materialList.map(item => item.souItemId),
          ...this.continuePriTalkFormData
        }
        const response = await purInqApi.select.startNewRound(paramsData)
        if (response) {
          this.$message.success(this.$t('bidMod.startNewQuoteSuccess'))
          this.dialogVisible = false
          this.$emit('success')
        }
      })
    }
  }
}
</script>
