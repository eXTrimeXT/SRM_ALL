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
          value-format="yyyy-MM-dd HH:mm:ss"
          :picker-options="pickerOptions"
        />
      </el-form-item>

      <el-form-item :label="$t('bidMod.newQuoteEndTime')" prop="orderEndTime" style="margin-bottom: 20px">
        <el-date-picker
          v-model="continuePriTalkFormData.orderEndTime"
          type="datetime"
          value-format="yyyy-MM-dd HH:mm:ss"
          :picker-options="pickerOptions"
        />
      </el-form-item>
      <el-form-item :label="$t('cusEntry.inq.nextRoundReason')" prop="reason">
        <el-input v-model="continuePriTalkFormData.reason" />
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
    <p>{{ $t('cusEntry.inq.multipleRoundBargaining') }}</p>
    <el-table
      border
      :data="materialList"
      max-height="350"
    >
      <el-table-column
        type="index"
        width="50"
        align="center"
      />
      <!--t 业务实体-->
      <el-table-column
        prop="orgOuName"
        align="center"
        :label="$t('bidMod.affairsEntity')"
        min-width="120"
        show-overflow-tooltip
      />
      <!--t 区域-->
      <el-table-column
        align="center"
        prop="extAreaCode"
        :label="$t('cusEntry.inq.extAreaName')"
        min-width="120"
        show-overflow-tooltip
        :formatter="(row) => {
          return row.extAreaCode ? $getDictLabel('REGION', row.extAreaCode) : ''
        }"
      />
      <!--t 物料编码-->
      <el-table-column
        align="center"
        prop="itemCode"
        :label="$t('bidMod.itemCode')"
        min-width="120"
        :formatter="(row, column, value) => row.noCodeItem === 'Y' ? '' : value"
        show-overflow-tooltip
      />

      <!--t 物料名称-->
      <el-table-column
        align="center"
        prop="itemDesc"
        :label="$t('bidMod.itemName')"
        min-width="150"
        show-overflow-tooltip
      />
      <!--t 采购分类-->
      <el-table-column
        align="center"
        prop="categoryName"
        :label="$t('bidMod.purcategoryName')"
        min-width="100"
        show-overflow-tooltip
      />
      <!--t 规格型号-->
      <el-table-column
        align="center"
        prop="extMaterialModel"
        :label="$t('cusEntry.bidMod.specification')"
        min-width="120"
        show-overflow-tooltip
      />
      <!--t 数量-->
      <el-table-column
        align="center"
        prop="requireQuantity"
        :label="$t('cusEntry.inq.quantity')"
        min-width="100"
        show-overflow-tooltip
      />
      <!--t 品牌-->
      <el-table-column
        prop="extBrand"
        align="center"
        :label="$t('cusEntry.inq.brand')"
        min-width="100"
        show-overflow-tooltip
      />
      <!--t 单位-->
      <el-table-column
        align="center"
        prop="unit"
        :label="$t('cusEntry.inq.baseMeasurmentUnit')"
        min-width="120"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $getDictLabel('unit', cellValue)"
      />
    </el-table>
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
import { inqBuyerHttp } from 'modcb@/inquiry/api'
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
        reason: [{ required: true, message: this.$t('cusEntry.tipMessage.nextRoundReason') }],
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
      const response = await inqBuyerHttp.init.getInqInfo(this.header.projectId)
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
        const response = await inqBuyerHttp.select.startNewRound(paramsData)
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
