<template>
  <SrmDialog
    :title="$t('bidMod.adjustmentDeadline')"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    size="small"
  >
    <el-form
      ref="adjustmentDeadlineForm"
      :model="adjustmentDeadlineFormData"
      :rules="adjustmentDeadlineFormRules"
      label-position="right"
      label-width="150px"
      style="padding-bottom: 10px"
    >
      <!--新的报价结束时间-->
      <el-form-item :label="$t('bidMod.extBidingEndDatetime1')" prop="orderEndTime">
        <el-date-picker
          v-model="adjustmentDeadlineFormData.orderEndTime"
          type="datetime"
          :placeholder="$t('bidMod.optionDate')"
          :format="$formatDatePickerTime"
          :picker-options="cannotLessCurrentTimeOptions"
          style="width: 100%"
        />
      </el-form-item>

      <!--调整原因-->
      <el-form-item :label="$t('bidMod.bidingExtendReason1')" prop="changeOrderEndTimeReason">
        <el-input
          v-model="adjustmentDeadlineFormData.changeOrderEndTimeReason"
          type="textarea"
          :rows="2"
          :placeholder="$t('common.pleaseTypeContents')"
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="dialogVisible = false">
        {{ $t('common.cancel') }}
      </el-button>

      <el-button type="primary" @click="confirm">
        {{ $t('common.confirm') }}
      </el-button>
    </template>
  </SrmDialog>
</template>

<script>
/**
 * 调整截止时间
 */
import { compBuyerHttp } from 'modb@/competition/api'
import { cannotLessCurrentTime } from 'lib@/mixins/datePickerOptions'

export default {
  name: 'AdjustmentDeadlineDialog',

  mixins: [cannotLessCurrentTime],

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    projectId: {
      type: [Number, String],
      default: ''
    }
  },

  data () {
    return {
      adjustmentDeadlineFormData: {
        orderEndTime: '',
        changeOrderEndTimeReason: ''
      },
      adjustmentDeadlineFormRules: {
        orderEndTime: [{ required: true, message: this.$t('common.pleaseSelect') }],
        changeOrderEndTimeReason: [{ required: true, message: this.$t('common.pleaseInput') }]
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
    /* 确定 */
    async confirm () {
      const valid = await this.$refs.adjustmentDeadlineForm.validate().catch(() => { /* noting */ })
      if (!valid) {
        return
      }

      const response = await compBuyerHttp.control.changeOrderEndTime({
        projectId: this.projectId,
        orderEndTime: this.adjustmentDeadlineFormData.orderEndTime,
        changeOrderEndTimeReason: this.adjustmentDeadlineFormData.changeOrderEndTimeReason,
        endNow: false
      })

      if (response) {
        this.$message.success(this.$t('common.successSubmit'))
        this.$emit('updateParentData')
        this.dialogVisible = false
      }
    }
  }
}
</script>
