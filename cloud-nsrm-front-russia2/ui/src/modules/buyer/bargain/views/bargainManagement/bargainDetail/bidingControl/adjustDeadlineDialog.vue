<template>
  <SrmDialog
    :title="$t('bidMod.adjustDeadline')"
    size="small"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <el-form
      label-position="right"
      label-width="150px"
      style="width: 100%; padding-bottom: 10px"
    >
      <!--新的投标结束时间-->
      <el-form-item :label="$t('bidMod.extBidingEndDatetime')">
        <el-date-picker
          v-model="orderEndTime"
          type="datetime"
          :format="$formatDatePickerTime"
          :placeholder="$t('bidMod.optionDate')"
          :picker-options="cannotLessCurrentTimeOptions"
          style="width: 100%"
        />
      </el-form-item>

      <!--调整原因-->
      <el-form-item :label="$t('bidMod.bidingExtendReason1')">
        <el-input
          v-model="changeOrderEndTimeReason"
          type="textarea"
          :rows="2"
          :placeholder="$t('common.pleaseTypeContents')"
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
import { brgBuyerHttp } from 'modb@/bargain/api'
import { cannotLessCurrentTime } from '@/library/mixins/datePickerOptions'

export default {
  name: 'AdjustDeadlineDialog',

  mixins: [cannotLessCurrentTime],

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    projectId: {
      type: [String, Number],
      required: true
    }
  },

  data () {
    return {
      orderEndTime: '',
      changeOrderEndTimeReason: ''
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
    async confirm () {
      if (!this.orderEndTime) {
        // '请选择新的投标结束时间'
        this.$message.warning(this.$t('bidMod.selectOrderEndTime'))
        return
      }

      const response = await brgBuyerHttp.control.changeOrderEndTime({
        projectId: this.projectId,
        orderEndTime: this.orderEndTime,
        changeOrderEndTimeReason: this.changeOrderEndTimeReason,
        endNow: false
      })
      if (response) {
        // 调整截至时间成功！
        this.$message.success(this.$t('bidMod.adjustDeadlineSuccess'))
        this.$emit('success')
        this.dialogVisible = false
      }
    }
  }
}
</script>
