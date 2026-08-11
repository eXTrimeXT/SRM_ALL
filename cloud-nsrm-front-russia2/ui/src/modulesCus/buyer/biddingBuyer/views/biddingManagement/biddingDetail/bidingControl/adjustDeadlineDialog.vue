<template>
  <SrmDialog
    :title="$t('cusEntry.bidMod.adjustDeadline')"
    size="small"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <el-form
      ref="endTimeForm"
      :model="endTimeForm"
      label-position="right"
      label-width="110px"
      style="width: 100%"
    >
      <!--当前投标截止时间-->
      <el-form-item :label="$t('cusEntry.bidMod.currentEndTime')">
        <el-date-picker
          v-model="currentEndTime"
          type="datetime"
          :placeholder="$t('bidMod.optionDate')"
          style="width: 100%"
          :format="$formatDatePickerTime"
          disabled
        />
      </el-form-item>

      <!-- 调整截止时间为 -->
      <el-form-item
        prop="adjustEndTime"
        :label="$t('cusEntry.bidMod.adjustEndTime')"
        :rules="[{required: true, message: $t('cusEntry.bidMod.selectEndTime'), trigger: ['blur', 'change']}]"
      >
        <el-date-picker
          v-model="endTimeForm.adjustEndTime"
          type="datetime"
          :format="$formatDatePickerTime"
          value-format="yyyy-MM-dd HH:mm:ss"
          :placeholder="$t('common.pleaseSelect')"
          :picker-options="pickerOptions"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item
        prop="adjustReason"
        :label="$t('bidMod.bidingExtendReason1')"
        :rules="[{required: true, message: $t('quota.quotaModulationDetailTip5'), trigger: ['blur', 'change']}]"
      >
        <el-input
          v-model="endTimeForm.adjustReason"
          type="textarea"
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="dialogVisible = false">
        {{ $t("common.cancel") }}
      </el-button>

      <el-button type="primary" @click="confirm">
        {{ $t("common.confirm") }}
      </el-button>
    </template>
  </SrmDialog>
</template>

<script>
/**
 * 调整截止时间
 */
import { bidBuyerHttp } from 'modcb@/biddingBuyer/api'

export default {
  name: 'AdjustDeadlineDialog',

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    projectId: {
      type: [String, Number],
      required: true
    },
    currentEndTime: {
      type: String,
      required: true
    }

  },

  data () {
    return {
      pickerOptions: {
        disabledDate (time) {
          const nowDate = new Date()
          nowDate.setHours(0)
          nowDate.setMinutes(0)
          nowDate.setSeconds(0)
          nowDate.setMilliseconds(0)
          return time.getTime() < nowDate.getTime()
        }
      },
      endTimeForm: {
        adjustEndTime: '',
        adjustReason: ''
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
    /* 调整截止时间 */
    confirm () {
      this.$refs.endTimeForm.validate(valid => {
        if (valid) {
          bidBuyerHttp.control.changeOrderEndTime({
            projectId: this.projectId,
            currentEndTime: this.currentEndTime,
            adjustEndTime: this.endTimeForm.adjustEndTime,
            adjustReason: this.endTimeForm.adjustReason
          }).then(res => {
            this.dialogVisible = false
            this.$message.success(res.message)
            this.$emit('success')
          })
        } else {
          return false
        }
      })
    }
  }
}
</script>
