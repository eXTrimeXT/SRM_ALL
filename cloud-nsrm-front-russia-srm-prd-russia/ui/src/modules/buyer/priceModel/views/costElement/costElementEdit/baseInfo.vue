<template>
  <el-form
    ref="costElementForm"
    :model="costElementBaseInfo"
    label-width="80px"
    label-position="top"
    :rules="baseInfoRules"
    :disabled="readonly"
  >
    <SrmRow :gutter="32">
      <!--要素编码-->
      <SrmCol :init-col="4">
        <el-form-item :label="$t('priceModel.costElement.elementCode')" prop="elementCode">
          <el-input v-model="costElementBaseInfo.elementCode" disabled />
        </el-form-item>
      </SrmCol>

      <!--要素名称-->
      <SrmCol :init-col="4">
        <el-form-item :label="$t('priceModel.costElement.elementName')" prop="elementName">
          <el-input
            v-model="costElementBaseInfo.elementName"
            type="text"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>
      </SrmCol>

      <!--要素类型-->
      <SrmCol :init-col="4">
        <el-form-item :label="$t('priceModel.costElement.elementType')" prop="elementType">
          <DictSelect
            v-model="costElementBaseInfo.elementType"
            code="COST_ELEMENT_TYPE"
            @change="elementTypeChange"
          />
        </el-form-item>
      </SrmCol>

      <!--计算方式-->
      <SrmCol :init-col="4">
        <el-form-item :label="$t('priceModel.costElement.calculation')" prop="calculation">
          <DictSelect
            v-model="costElementBaseInfo.calculation"
            code="COST_ELEMENT_CALCULATION"
            :disabled="costElementBaseInfo.elementType && costElementBaseInfo.elementType !== 'FEE'"
            @change="elementTypeChange"
          />
        </el-form-item>
      </SrmCol>

      <!--类型-->
      <SrmCol :init-col="4">
        <el-form-item :label="$t('elementDefinition.type')" prop="enableCommon">
          <DictSelect
            v-model="costElementBaseInfo.enableCommon"
            code="COST_ENABLE_COMMON"
            @change="enableCommonChange"
          />
        </el-form-item>
      </SrmCol>

      <!--业务实体-->
      <SrmCol v-if="showOrgId" :init-col="4">
        <el-form-item
          :label="$t('priceModel.costElement.orgName')"
          prop="orgId"
        >
          <OrganizationSelector
            v-model="costElementBaseInfo.orgId"
            :parent-id="-1"
            node-type="OU"
            :placeholder="$t('common.pleaseSelect')"
            :scope="costElementBaseInfo"
            @select="setOrgInfo"
          />
        </el-form-item>
      </SrmCol>

      <!--创建日期-->
      <SrmCol :init-col="4">
        <el-form-item
          :label="$t('priceModel.costElement.creationDate')"
          prop="creationDate"
        >
          <el-input v-model="costElementBaseInfo.creationDate" disabled />
        </el-form-item>
      </SrmCol>

      <!--状态-->
      <SrmCol :init-col="4">
        <el-form-item
          :label="$t('priceModel.costElement.status')"
          prop="status"
        >
          <DictSelect
            v-model="costElementBaseInfo.status"
            code="COST_ELEMENT_STATUS"
            disabled
          />
        </el-form-item>
      </SrmCol>

      <!--单位-->
      <SrmCol :init-col="4">
        <el-form-item
          :label="$t('priceModel.costElement.unit')"
          prop="unit"
        >
          <DictSelect
            v-model="costElementBaseInfo.unit"
            code="COST_ELEMENT_UNIT"
            disabled
          />
        </el-form-item>
      </SrmCol>
    </SrmRow>
  </el-form>
</template>

<script>
/**
 * 基础信息
 */
import OrganizationSelector from 'lib@/components/organization-selector'

export default {
  name: 'BaseInfo',

  components: {
    OrganizationSelector
  },

  props: {
    baseInfo: {
      type: Object,
      default: () => { /* nothing */ }
    },
    readonly: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      baseInfoRules: {
        elementName: [{ required: true, message: this.$t('common.pleaseInput') }],
        elementType: [{ required: true, message: this.$t('common.pleaseSelect') }],
        calculation: [{ required: true, message: this.$t('common.pleaseInput') }],
        enableCommon: [{ required: true, message: this.$t('common.pleaseInput') }],
        orgId: [{ required: true, message: this.$t('common.pleaseInput') }]
      }
    }
  },

  computed: {
    costElementBaseInfo: {
      get: function () {
        return this.baseInfo
      },
      set: function (val) {
        this.$emit('update:baseInfo', val)
      }
    },

    // 是否显示业务实体
    showOrgId () {
      // 专用类型
      return this.costElementBaseInfo.enableCommon === 'SPECIAL_USE'
    }
  },

  watch: {
    showOrgId: {
      handler (val) {
        this.baseInfoRules.orgId[0].required = val
      },
      immediate: true
    }
  },

  methods: {
    /* 要素类型变更 */
    elementTypeChange () {
      if (this.costElementBaseInfo.elementType && this.costElementBaseInfo.elementType !== 'FEE') {
        this.costElementBaseInfo.calculation = 'DIRECT_CALCULATION'
      }

      this.$emit('fee-calc-visible', this.costElementBaseInfo.calculation === 'CALCULATED_BY_RATE' && this.costElementBaseInfo.elementType === 'FEE')
    },

    /* 业务实体选择 */
    setOrgInfo (value) {
      const {
        organizationId = '',
        organizationCode = '',
        organizationName = ''
      } = value || {}
      this.costElementBaseInfo.orgId = organizationId
      this.costElementBaseInfo.orgCode = organizationCode
      this.costElementBaseInfo.orgName = organizationName
    },

    /* 类型改变 */
    enableCommonChange (value) {
      if (value === 'COMMON') {
        // 通用类型 清空业务实体
        this.setOrgInfo(null)
      }
    },

    /* 表单校验，返回校验结果 */
    validateForm () {
      return new Promise(resolve => {
        this.$refs.costElementForm.validate(async valid => {
          if (valid) {
            resolve(true)
          } else {
            this.__focus_error__()
            resolve(false)
          }
        })
      })
    }
  }
}
</script>
