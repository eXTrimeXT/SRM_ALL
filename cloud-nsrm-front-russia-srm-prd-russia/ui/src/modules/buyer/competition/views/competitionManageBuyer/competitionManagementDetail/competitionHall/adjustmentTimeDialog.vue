<template>
  <SrmDialog
    size="middle"
    title="调整时间"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    append-to-body
  >
    <el-form
      ref="form"
      :model="formData"
      :rules="formRules"
    >
      <SrmRow>
        <!--原竞价截止时间-->
        <SrmCol :init-col="2">
          <el-form-item :label="$t('bidMod.competitionLts.initOrderEndTime')">
            <el-date-picker
              :value="baseInfo.orderEndTime"
              type="datetime"
              value-format="yyyy-MM-dd HH:mm:ss"
              :placeholder="$t('bidMod.datePicker')"
              disabled
            />
          </el-form-item>
        </SrmCol>

        <!--竞价截止时间调整至-->
        <SrmCol :init-col="2">
          <el-form-item :label="$t('bidMod.competitionLts.orderEndTimeChange')" prop="orderEndTime">
            <el-date-picker
              v-model="formData.orderEndTime"
              type="datetime"
              value-format="yyyy-MM-dd HH:mm:ss"
              :placeholder="$t('bidMod.datePicker')"
              :picker-options="cannotLessCurrentTimeOptions"
            />
          </el-form-item>
        </SrmCol>
      </SrmRow>
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
 * 时间调整
 */
import { carBuyerHttp } from 'modb@/competition/api'
import { cannotLessCurrentTime } from '@/library/mixins/datePickerOptions'
import { transformMQL } from 'lib@/utils/util'

export default {
  name: 'AdjustmentTimeDialog',

  mixins: [cannotLessCurrentTime],

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    baseInfo: {
      type: Object,
      default: () => ({})
    }
  },

  data () {
    return {
      formData: {
        orderEndTime: ''
      },
      formRules: {
        orderEndTime: [
          { required: true, message: this.$t('common.pleaseSelect') },
          {
            validator: (_rule, value, callback) => {
              if (value) {
                const [nowDate, valueDate] = [
                  this.$dayjs().unix(),
                  this.$dayjs(value).unix()
                ]
                if (valueDate < nowDate) {
                  callback(new Error(this.$t('bidMod.competitionLts.orderEndTimeChangeTips')))
                }
              }
              callback()
            },
            trigger: 'blur'
          }
        ]
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
    /* 确认调整时间 */
    async confirm () {
      const valid = await this.$refs.form.validate().catch(() => this.__focus_error__())

      if (!valid) {
        return
      }

      let transformParams = transformMQL.save('AuctSouProjectForBuyer', [{
        projectId: this.baseInfo.projectId,
        orderEndTime: this.formData.orderEndTime,
        endNow: false
      }], 'changeOrderEndTime')
      const response = await carBuyerHttp.order.changeOrderEndTime(transformParams)

      if (response) {
        this.$message.success(this.$t('common.successUpdate'))
        this.$emit('success')
      }
    }
  }
}
</script>
