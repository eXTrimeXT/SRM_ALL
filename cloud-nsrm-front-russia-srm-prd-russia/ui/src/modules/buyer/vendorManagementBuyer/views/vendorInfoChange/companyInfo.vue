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
          <!-- 代理品牌 -->
          <el-form-item
            :label="$t('vendorMod.agencyBrand')"
            prop="ceeaAgentBrand"
            :rules="{
              required: itemisRequired2 ? true : false,
              message: $t('vendorMod.msgAgencyBrand')
            }"
          >
            <el-input
              v-model="baseInfoModel.baseInfoForm.ceeaAgentBrand"
            />
          </el-form-item>
        </srm-col>
        <srm-col :initCol="1">
          <!-- 是否上市 -->
          <el-form-item :label="$t('vendorMod.ifListed')">
            <el-radio v-model="baseInfoModel.baseInfoForm.ceeaIfListed" label="Y" class="formCheckbox">
              {{ $t('common.yes') }}
            </el-radio>
            <el-radio v-model="baseInfoModel.baseInfoForm.ceeaIfListed" label="N" class="formCheckbox">
              {{ $t('common.no') }}
            </el-radio>
          </el-form-item>
        </srm-col>
        <srm-col
          v-if="baseInfoModel.baseInfoForm.ceeaIfListed == 'Y'"
          :initCol="1"
        >
          <!-- 上市时间 -->
          <el-form-item
            :label="$t('vendorMod.listedDate')"
            prop="ceeaListedTime"
          >
            <el-date-picker
              v-model="baseInfoModel.baseInfoForm.ceeaListedTime"
              type="date"
              format="yyyy-MM-dd"
              value-format="yyyy-MM-dd"
            />
          </el-form-item>
        </srm-col>
        <srm-col :initCol="1">
          <!-- 是否有母公司 -->
          <el-form-item :label="$t('vendorMod.ifParentCompany')">
            <el-radio v-model="baseInfoModel.baseInfoForm.ceeaHasParentCompany" label="Y" class="formCheckbox">
              {{ $t('common.yes') }}
            </el-radio>
            <el-radio v-model="baseInfoModel.baseInfoForm.ceeaHasParentCompany" label="N" class="formCheckbox">
              {{ $t('common.no') }}
            </el-radio>
          </el-form-item>
        </srm-col>
        <srm-col
          v-if="baseInfoModel.baseInfoForm.ceeaHasParentCompany == 'Y'"
          :initCol="1"
        >
          <!-- 母公司名称 -->
          <el-form-item
            :label="$t('vendorMod.parentCompanyName')"
            prop="ceeaParentCompanyName"
          >
            <el-input
              v-model="baseInfoModel.baseInfoForm.ceeaParentCompanyName"
            />
          </el-form-item>
        </srm-col>
        <srm-col
          v-if="baseInfoModel.baseInfoForm.ceeaHasParentCompany == 'Y'"
          :initCol="1"
        >
          <!-- 母公司统一信用代码 -->
          <el-form-item
            :label="$t('vendorMod.parentCompanyLcCode')"
            prop="ceeaParentCompanyLcCode"
          >
            <el-input
              v-model="baseInfoModel.baseInfoForm.ceeaParentCompanyLcCode"
            />
          </el-form-item>
        </srm-col>
        <srm-col v-if="baseInfoModel.baseInfoForm.overseasRelation !== 'INSIDE'" :initCol="1">
          <el-form-item label="D-U-N-S">
            <el-input v-model="baseInfoModel.baseInfoForm.dunsCode" />
          </el-form-item>
        </srm-col>
        <srm-col :initCol="1">
          <!-- 企业简介 -->
          <el-form-item
            :label="$t('vendorMod.companyProfile')"
            prop="ceeaCompanyIntro"
            :rules="{
              required: itemisRequired ? true : false,
              message: this.$t('vendorMod.msgCompanyProfile')
            }"
          >
            <el-input
              v-model="baseInfoModel.baseInfoForm.ceeaCompanyIntro"
            />
          </el-form-item>
        </srm-col>
      </srm-row>
    </el-form>
    <el-form
      v-if="baseInfoDimFieldContexts.length > 0"
      ref="baseDimForm"
      :model="baseDimModel"
      :rules="baseDimRules"
      :show-message="false"
      class="base-form-info form-fill-style"
      :disabled="true"
    >
      <srm-row :gutter="32">
        <!-- 拓展字段显示 -->
        <srm-col
          v-for="item in baseInfoDimFieldContexts"
          :key="item.fieldConfigId"
          :initCol="1"
        >
          <el-form-item
            :prop="item.fieldCode"
            :label="item.languageCode ? $t(item.languageCode) : item.fieldName"
          >
            <el-input
              v-model="baseDimModel[item.fieldCode]"
              class="el-input-all"
            />
          </el-form-item>
        </srm-col>
      </srm-row>
    </el-form>
  </div>
</template>
<script>

export default {
  name: 'CompanyInfo',
  components: { },
  model: {
    prop: 'data',
    event: 'change'
  },
  props: {
    infoChangeModel: {
      type: Object,
      default () {
        return []
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
        return []
      }
    },
    baseInfoModel: {
      type: Object,
      default () {
        return []
      }
    },
    rules: {
      type: Object,
      default () {
        return []
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
      itemisRequired: false,
      itemisRequired2: false
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
  width: 60% !important;
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
