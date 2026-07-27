<template>
  <!-- <SrmDialog
    title="调整公示时间"
    size="small"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  > -->
  <SrmDialog
    :title="$t('cusEntry.supplement20250121.adjustThePublicAnnouncementTime')"
    size="small"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <el-form ref="form" :model="baseForm" :rules="formRules">
      <SrmRow>
        <SrmCol :init-col="1">
          <!-- <el-form-item prop="publicEndTime" label="当前公示截止时间"> -->
          <el-form-item prop="publicEndTime" :label="$t('cusEntry.supplement20250121.currentDeadlineForPublicAnnouncement')">
            <el-date-picker
              v-model="form.publicEndTime"
              disabled
              type="datetime"
              :format="$formatDatePicker"
              :value-format="formatTime"
            />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="1">
          <!-- <el-form-item prop="publicEndTimeNew" label="调整截止时间为"> -->
          <el-form-item prop="publicEndTimeNew" :label="$t('cusEntry.bidMod.adjustEndTime')">
            <el-date-picker
              v-model="baseForm.publicEndTimeNew"
              :picker-options="pickerOptions"
              type="datetime"
              :format="$formatDatePicker"
              :value-format="formatTime"
              default-time="17:00:00"
            />
          </el-form-item>
        </SrmCol>
      </SrmRow>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <template v-if="!readonly">
        <el-button @click="dialogVisible = false">
          {{ $t("common.cancel") }}
        </el-button>
        <el-button type="primary" @click="handleConfirm">
          {{ $t("common.confirm") }}
        </el-button>
      </template>
      <template v-else>
        <el-button @click="dialogVisible = false">
          {{ $t("common.close") }}
        </el-button>
      </template>
    </div>
  </SrmDialog>
</template>
<script>

export default {
  name: 'PublicTimeDialog',
  components: {
  },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    readonly: {
      type: Boolean,
      default: false
    },
    form: {
      type: Object,
      default: () => {}
    }
  },
  data () {
    return {
      baseForm: {
        publicEndTimeNew: null
      },
      formRules: {
        publicEndTimeNew: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }]
      },
      formatTime: 'yyyy-MM-dd HH:mm:ss',
      pickerOptions: {
        disabledDate: (val) => {
          const nowDate = new Date()
          nowDate.setHours(0)
          nowDate.setMinutes(0)
          nowDate.setSeconds(0)
          nowDate.setMilliseconds(0)
          return val.getTime() < nowDate.getTime()
        }
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
  created () {

  },
  methods: {
    async handleConfirm () {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.$emit('confirm', this.baseForm.publicEndTimeNew)
        }
      })
    },
    resetFields () {
      this.$nextTick(() => {
        this.$refs.form.resetFields()
      })
    }
  }
}
</script>
