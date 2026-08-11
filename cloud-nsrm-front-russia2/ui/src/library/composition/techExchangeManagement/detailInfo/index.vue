<template>
  <SrmRow>
    <!--交流单号-->
    <SrmCol :init-col="3">
      <el-form-item
        :label="$t('hierarchical.trackingnumber')"
        prop="technicalExchangeFormCode"
      >
        <el-input
          v-model="detailFormData.technicalExchangeFormCode"
          disabled
        />
      </el-form-item>
    </SrmCol>

    <!--交流标题-->
    <SrmCol :init-col="3">
      <el-form-item
        :label="$t('hierarchical.Communicationtitles')"
        prop="technicalExchangeTitle"
      >
        <el-input
          v-model="detailFormData.technicalExchangeTitle"
          :disabled="readonly"
        />
      </el-form-item>
    </SrmCol>

    <!--业务实体-->
    <SrmCol :init-col="3">
      <el-form-item
        :label="$t('hierarchical.BusinessEntity')"
        prop="orgOuId"
      >
        <organization-selector
          v-model="detailFormData.orgOuId"
          :scope="detailFormData"
          node-type="OU"
          :parent-id="-1"
          :placeholder="$t('common.pleaseSelect')"
          :disabled="readonly"
          @select="orgOuIdChange"
        />
      </el-form-item>
    </SrmCol>

    <!--交流类型-->
    <SrmCol :init-col="3">
      <el-form-item
        :label="$t('hierarchical.Typeofcommunication')"
        prop="technicalExchangeType"
      >
        <DictSelect
          v-model="detailFormData.technicalExchangeType"
          code="TECHNICAL_EXCHANGE_TYPE"
          :disabled="readonly"
        />
      </el-form-item>
    </SrmCol>

    <!--预计开始时间-->
    <SrmCol :init-col="3">
      <el-form-item
        :label="$t('hierarchical.Estimatedstarttime')"
        prop="technicalExchangeStartTime"
      >
        <el-date-picker
          v-model="detailFormData.technicalExchangeStartTime"
          type="datetime"
          :format="$formatDatePickerTime"
          value-format="yyyy-MM-dd HH:mm:ss"
          :placeholder="$t('bidMod.datePicker')"
          :picker-options="cannotLessCurrentTimeOptions"
          :disabled="readonly"
        />
      </el-form-item>
    </SrmCol>

    <!--预计结束时间-->
    <SrmCol :init-col="3">
      <el-form-item
        :label="$t('hierarchical.Estimatedendtime')"
        prop="technicalExchangeEndTime"
      >
        <el-date-picker
          v-model="detailFormData.technicalExchangeEndTime"
          type="datetime"
          :format="$formatDatePickerTime"
          value-format="yyyy-MM-dd HH:mm:ss"
          :placeholder="$t('bidMod.datePicker')"
          :picker-options="cannotLessCurrentTimeOptions"
          :disabled="readonly"
        />
      </el-form-item>
    </SrmCol>

    <!--发起人-->
    <SrmCol :init-col="3">
      <el-form-item
        :label="$t('hierarchical.sponsor')"
        prop="createdUserName"
      >
        <el-input
          v-model="detailFormData.createdUserName"
          disabled
        />
      </el-form-item>
    </SrmCol>

    <!--创建时间-->
    <SrmCol :init-col="3">
      <el-form-item
        :label="$t('hierarchical.Creationtime')"
        prop="creationDate"
      >
        <el-date-picker
          v-model="detailFormData.creationDate"
          type="date"
          :placeholder="$t('common.pleaseSelectDate')"
          :format="$formatDatePicker"
          value-format="yyyy-MM-dd"
          disabled
        />
      </el-form-item>
    </SrmCol>

    <!--单据状态-->
    <SrmCol :init-col="3">
      <el-form-item
        :label="$t('hierarchical.Documentstatus')"
        prop="technicalExchangeFormStatus"
      >
        <DictSelect
          v-model="detailFormData.technicalExchangeFormStatus"
          code="TECHNICAL_EXCHANGE_FORM_STATUS"
          disabled
        />
      </el-form-item>
    </SrmCol>

    <!--备注-->
    <SrmCol :init-col="1">
      <el-form-item
        :label="$t('hierarchical.remark')"
        prop="remark"
      >
        <el-input
          v-model="detailFormData.remark"
          type="textarea"
          :rows="2"
          :disabled="readonly"
        />
      </el-form-item>
    </SrmCol>
  </SrmRow>
</template>

<script>
/**
 * 交流信息
 */
import { cannotLessCurrentTime } from 'lib@/mixins/datePickerOptions'
import OrganizationSelector from 'lib@/components/organization-selector'

export default {
  name: 'DetailInfo',

  components: {
    OrganizationSelector
  },

  mixins: [cannotLessCurrentTime],

  props: {
    formData: {
      type: Object,
      required: true
    },
    readonly: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      priceEndTimePickerOptions: {
        disabledDate: time => {
          const start = new Date(this.bidingBaseInfo.priceStartTime)
          return time.getTime() <= start.getTime()
        }
      }
    }
  },

  computed: {
    detailFormData: {
      get: function () {
        return this.formData
      },
      set: function (val) {
        this.$emit('update:formData', val)
      }
    }
  },

  methods: {
    /* 选择业务实体 */
    orgOuIdChange (e) {
      this.detailFormData.orgOuId = e ? e.organizationId : ''
      this.detailFormData.orgOuCode = e ? e.organizationCode : ''
      this.detailFormData.orgOuName = e ? e.organizationName : ''
    }
  }
}
</script>
