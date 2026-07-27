<template>
  <srm-dialog
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
      <el-form-item :label="$t('bidMod.extBidingEndDatetime')">
        <el-date-picker
          v-model="bargainEndTime"
          type="datetime"
          :placeholder="$t('bidMod.optionDate')"
          :picker-options="cannotLessCurrentTimeOptions"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item :label="$t('bidMod.bidingExtendReason1')">
        <el-input
          v-model="adjustBargainTimeReason"
          type="textarea"
          :rows="2"
          :placeholder="$t('common.pleaseTypeContents')"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button
        @click="dialogVisible = false"
      >
        {{ $t("common.cancel") }}
      </el-button>
      <el-button
        type="primary"
        @click="adjustDeadline"
      >
        {{ $t("common.confirm") }}
      </el-button>
    </template>
  </srm-dialog>
</template>

<script>
/**
 * 调整截止时间
 */
import { cannotLessCurrentTime } from '@/library/mixins/datePickerOptions'

export default {
  name: 'AdjustDeadlineDialog',
  mixins: [cannotLessCurrentTime],
  props: {
    visible: {
      type: Boolean
    },
    bargainId: {
      type: [String, Number]
    }
  },
  data () {
    return {
      bargainEndTime: '',
      adjustBargainTimeReason: ''
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
    adjustDeadline () {
      if (!this.bargainEndTime) {
        this.$message.warning('请选择新的投标结束时间')
        return
      }
      this.$api.brg.inquiryByProject.changeBargainEndTime({
          bargainId: this.bargainId,
          bargainEndTime: this.bargainEndTime,
          adjustBargainTimeReason: this.adjustBargainTimeReason,
          endNow: 'N'
        }).then(() => {
        // 调整截至时间成功！
        this.$message.success(this.$t('bidMod.adjustDeadlineSuccess'))
        this.$emit('adjustDeadlineSuccess')
        this.dialogVisible = false
      })
    }
  }
}
</script>
