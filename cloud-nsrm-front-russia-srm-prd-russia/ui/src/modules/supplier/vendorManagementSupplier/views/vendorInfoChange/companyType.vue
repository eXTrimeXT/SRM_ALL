// 只有供应商变更才用这个业务组件,这个是基本信息模块的变更前
<template>
  <div class="companyInfo">
    <div class="changeTitle">
      <i />{{ $t("supplierChange.beforeChange") }}
    </div>
    <el-form
      ref="baseInfoForm"
      class="base-form-info form-fill-style"
      :model="baseInfoModel.baseInfoForm"
      :rules="rules"
      :show-message="false"
      :disabled="true"
    >
      <srm-row :gutter="32">
        <srm-col :initCol="1">
          <!-- 境内外关系 -->
          <el-form-item
            prop="overseasRelation"
            :label="$t('vendorMod.overseasRelation')"
            :required="true"
          >
            <DictSelect
              v-model="baseInfoModel.baseInfoForm.overseasRelation"
              code="RELATION"
              :disabled="true"
            />
          </el-form-item>
        </srm-col>
        <srm-col
          v-if="curRel === 'INSIDE'"
          :initCol="1"
        >
          <!-- 企业性质 -->
          <el-form-item
            prop="companyType"
            :label="$t('vendorMod.companyType')"
            :required="curRel === 'INSIDE'"
          >
            <DictSelect
              v-model="baseInfoModel.baseInfoForm.companyType"
              code="COMPANY_NATURE"
              @change="companyTypeChangeHandle"
            />
          </el-form-item>
        </srm-col>
        <srm-col :initCol="1">
          <!-- 是否长期供应商 -->
          <el-form-item :label="$t('vendorMod.ifLongTermSupplier')">
            <el-radio v-model="baseInfoModel.baseInfoForm.ifLongPeriod" label="Y" class="formCheckbox">
              {{ $t('common.yes') }}
            </el-radio>
            <el-radio v-model="baseInfoModel.baseInfoForm.ifLongPeriod" label="N" class="formCheckbox">
              {{ $t('common.no') }}
            </el-radio>
          </el-form-item>
        </srm-col>
        <srm-col :initCol="1">
          <!-- 供应商业务类型 -->
          <el-form-item
            :label="$t('vendorMod.vendorBusinessType')"
            prop="ceeaSupBusinessType"
          >
            <DictSelect
              v-model="baseInfoModel.baseInfoForm.ceeaSupBusinessType"
              code="SUP_BUSINESS_TYPE"
              :disabled="true"
            />
          </el-form-item>
        </srm-col>
        <srm-col :initCol="1">
          <!-- 供应商类型 -->
          <el-form-item
            :rules="{
              required: true,
              message: $t('vendorMod.supplierTypeMgs')
            }"
            :label="$t('supplierRating.supplierType')"
            prop="supplierType"
          >
            <DictSelect
              v-model="baseInfoModel.baseInfoForm.supplierType"
              code="SUPPLIER_TYPE"
            />
          </el-form-item>
        </srm-col>
        <srm-col :initCol="1">
          <!-- 商业模式 -->
          <el-form-item :label="$t('vendorMod.bizModel')">
            <DictSelect
              v-model="baseInfoModel.baseInfoForm.ceeaBusinessModel"
              code="BIZ_MODEL"
            />
          </el-form-item>
        </srm-col>
      </srm-row>
    </el-form>
  </div>
</template>
<script>

export default {
  name: 'CompanyType',
  components: {
  },
  model: {
    prop: 'data',
    event: 'change'
  },
  props: {
    infoChangeModel: {
      type: Object,
      default () {
        return {}
      }
    },
    cityList: {
      type: Array,
      default () {
        return []
      }
    },
    provinceList: {
      type: Array,
      default () {
        return []
      }
    },
    curType: {
      type: String,
      default () {
        return ''
      }
    },
    curRel: {
      type: String,
      default () {
        return ''
      }
    },
    baseInfoModel: {
      type: Object,
      default () {
        return {}
      }
    },
    rules: {
      type: Object,
      default () {
        return {}
      }
    },
    disabled: {
      type: Boolean,
      default () {
        return false
      }
    },
    baseInfoDimFieldContexts: {
      type: [Object, Array],
      default () {
        return []
      }
    },
    baseDimModel: {
      type: Object,
      default () {
        return []
      }
    },
    baseDimRules: {
      type: Object,
      default () {
        return []
      }
    }
  },
  data () {
    return {

    }
  },
  computed: {

  },
  watch: {

  },
  mounted () {

  },
  methods: {

  }
}
</script>

<style scope>
.formClassAll .el-select,.formClassAll .el-input-all,.formClassAll .el-date-editor,.formClassAll .el-input-group{
  width: 320px !important;
}
.formClassAll form{
  padding-left: 18px
}
.changeTitle{
  background-color: #F6F6F6;
  font-size: 14px ;
  color: #393E45 ;
  overflow: hidden;
  line-height: 40px;
  margin-bottom:20px;
  font-weight: 400;
}
.changeTitle i{
  width: 4px;
  height: 18px;
  background-color: #0077FF;
  margin: 11px 10px 11px 16px;
  display: block;
  float: left;
}
</style>
