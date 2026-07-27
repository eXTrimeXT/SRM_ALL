<template>
  <SrmRow>
    <!--交流单号-->
    <SrmCol :init-col="3">
      <el-form-item
        label="交流单号"
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
        label="交流标题"
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
        label="业务实体"
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
        label="交流类型"
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
        label="预计开始时间"
        prop="technicalExchangeStartTime"
      >
        <el-date-picker
          v-model="detailFormData.technicalExchangeStartTime"
          type="datetime"
          :placeholder="$t('bidMod.datePicker')"
          :picker-options="cannotLessCurrentTimeOptions"
          :disabled="readonly"
        />
      </el-form-item>
    </SrmCol>

    <!--预计结束时间-->
    <SrmCol :init-col="3">
      <el-form-item
        label="预计结束时间"
        prop="technicalExchangeEndTime"
      >
        <el-date-picker
          v-model="detailFormData.technicalExchangeEndTime"
          type="datetime"
          :placeholder="$t('bidMod.datePicker')"
          :picker-options="cannotLessCurrentTimeOptions"
          :disabled="readonly"
        />
      </el-form-item>
    </SrmCol>

    <!--发起人-->
    <SrmCol :init-col="3">
      <el-form-item
        label="发起人"
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
        label="创建时间"
        prop="creationDate"
      >
        <el-input
          v-model="detailFormData.creationDate"
          disabled
        />
      </el-form-item>
    </SrmCol>

    <!--单据状态-->
    <SrmCol :init-col="3">
      <el-form-item
        label="单据状态"
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
        label="备注"
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
