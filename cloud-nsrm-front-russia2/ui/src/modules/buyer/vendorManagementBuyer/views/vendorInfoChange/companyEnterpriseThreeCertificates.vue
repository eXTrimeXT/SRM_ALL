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
          <!-- 营业执照 -->
          <el-form-item
            v-if="infoChangeModel.infoChange.companyName"
            prop="businessLicenseFileId"
            :label="$t('vendorMod.businessLicense')"
            :required="curType === 'FEIYINGLI' || curRel === 'OUT' ? false : true"
          >
            <div>
              <srm-common-file
                :default-file="{
                  fileId: baseInfoModel.baseInfoForm.businessLicenseFileId,
                  fileName: baseInfoModel.baseInfoForm.businessLicense
                }"
                :readonly="true"
              />
            </div>
          </el-form-item>
        </srm-col>
        <srm-col :initCol="1">
          <!-- 企业名称 -->
          <el-form-item
            prop="companyName"
            :label="$t('vendorMod.companyName')"
            :required="true"
          >
            <el-input v-model="baseInfoModel.baseInfoForm.companyName" class="el-input-all" />
          </el-form-item>
        </srm-col>
        <srm-col :initCol="1">
          <!-- 法人代表 -->
          <el-form-item
            prop="legalPerson"
            :label="$t('vendorMod.legalPerson')"
            :required="curRel === 'INSIDE' ? true : false"
          >
            <el-input v-model="baseInfoModel.baseInfoForm.legalPerson" class="el-input-all" />
          </el-form-item>
        </srm-col>
        <!-- 注册资本(万元) -->
        <srm-col
          v-if="curType !== 'GETI'"
          :initCol="1"
        >
          <el-form-item
            prop="registeredCapital"
            :label="$t('vendorMod.registeredCapital')"
            :required="curType === 'GETI' || curType === 'FEIYINGLI' ? false : true"
          >
            <el-input
              v-model="baseInfoModel.baseInfoForm.registeredCapital"
              :placeholder="$t('common.pleaseInput')"
              class="input-with-select"
            >
              <DictSelect
                slot="append"
                v-model="baseInfoModel.baseInfoForm.registCurrency"
                code="BID_TENDER_CURRENCY"
                :placeholder="$t('vendorMod.currencyCode')"
                style="width: 110px !important;"
              />
            </el-input>
          </el-form-item>
        </srm-col>
        <srm-col :initCol="1">
          <!-- 成立日期 -->
          <el-form-item
            prop="companyCreationDate"
            :label="$t('vendorMod.creationDate')"
            :required="curType === 'FEIYINGLI' ? false : true"
          >
            <el-date-picker
              v-model="baseInfoModel.baseInfoForm.companyCreationDate"
              type="date"
              :placeholder="$t('common.pleaseSelectDate')"
              :format="$formatDatePicker"
              value-format="timestamp"
            />
          </el-form-item>
        </srm-col>
        <srm-col :initCol="1">
          <!-- 企业简称 -->
          <el-form-item
            prop="companyShortName"
            :label="$t('vendorMod.companyShortName')"
          >
            <el-input v-model="baseInfoModel.baseInfoForm.companyShortName" class="el-input-all" />
          </el-form-item>
        </srm-col>
        <!-- 只有境内供应商有 -->
        <srm-col
          v-if="curRel === 'INSIDE'"
          :initCol="1"
        >
          <!-- 统一社会信用代码 -->
          <el-form-item
            prop="lcCode"
            :label="$t('vendorMod.lcCode')"
            :required="curRel === 'INSIDE' ? true : false"
          >
            <el-input v-model="baseInfoModel.baseInfoForm.lcCode" class="el-input-all" />
          </el-form-item>
        </srm-col>
        <srm-col :initCol="1">
          <!-- 登记机关 -->
          <el-form-item
            prop="registrationAuthority"
            :label="$t('vendorMod.registrationAuthority')"
          >
            <el-input
              v-model="baseInfoModel.baseInfoForm.registrationAuthority"
              class="el-input-all"
            />
          </el-form-item>
        </srm-col>
        <!-- 个体户不用显示 -->
        <srm-col
          v-if="curType !== 'GETI'"
          :initCol="1"
        >
          <!-- 营业日期从 -->
          <el-form-item
            prop="businessStartDate"
            :label="$t('vendorMod.businessStartFrom')"
            :required="curType === 'FEIYINGLI' ? false : true"
          >
            <el-date-picker
              v-model="baseInfoModel.baseInfoForm.businessStartDate"
              type="date"
              :placeholder="$t('common.pleaseSelectDate')"
              :format="$formatDatePicker"
              value-format="timestamp"
            />
          </el-form-item>
        </srm-col>
        <!-- 个体户不用显示 -->
        <srm-col
          v-if="curType !== 'GETI'"
          :initCol="1"
        >
          <!-- 营业日期至 -->
          <el-form-item
            prop="businessEndDate"
            :label="$t('vendorMod.businessEndAt')"
            :required="curType === 'FEIYINGLI' ? false : true"
          >
            <el-date-picker
              v-model="baseInfoModel.baseInfoForm.businessEndDate"
              type="date"
              :placeholder="$t('common.pleaseSelectDate')"
              :format="$formatDatePicker"
              value-format="timestamp"
            />
          </el-form-item>
        </srm-col>
        <srm-col :initCol="1">
          <!-- 营业地址（国家/地区） -->
          <el-form-item
            prop="companyCountry"
            :label="$t('vendorMod.businessAddr')"
            :required="true"
          >
            <DictSelect
              v-model="baseInfoModel.baseInfoForm.companyCountry"
              code="country"
              filterable
            />
          </el-form-item>
        </srm-col>
        <srm-col
          v-if="curRel === 'INSIDE'"
          :initCol="1"
        >
          <!-- 省份/州 -->
          <el-form-item
            prop="companyProvince"
            :label="$t('vendorMod.province')"
            :required="curRel === 'INSIDE' ? true : false"
          >
            <!-- @change="provinceChangeHandle" -->
            <el-select
              v-model="baseInfoModel.baseInfoForm.companyProvince"
              filterable
            >
              <el-option
                v-for="item in provinceList"
                :key="item.id"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </srm-col>
        <srm-col
          v-if="curRel === 'INSIDE'"
          :initCol="1"
        >
          <!-- 城市 -->
          <el-form-item
            prop="companyCity"
            :label="$t('vendorMod.city')"
            :required="curRel === 'INSIDE' ? true : false"
          >
            <el-select
              v-model="baseInfoModel.baseInfoForm.companyCity"
              filterable
            >
              <el-option
                v-for="item in cityList"
                :key="item.id"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </srm-col>
        <srm-col :init-col="1">
          <!-- 详细地址 -->
          <el-form-item
            prop="businessScope"
            :label="$t('components.address.detailAddress')"
            :required="curRel === 'INSIDE' ? true : false"
          >
            <el-input v-model="baseInfoModel.baseInfoForm.companyAddress" class="el-input-all" />
          </el-form-item>
        </srm-col>
        <srm-col :initCol="1">
          <!-- 营业范围 -->
          <el-form-item
            prop="businessScope"
            :label="$t('vendorMod.businessScope')"
            :required="curType === 'FEIYINGLI' ? false : true"
          >
            <el-input v-model="baseInfoModel.baseInfoForm.businessScope" class="el-input-all" />
          </el-form-item>
        </srm-col>
      </srm-row>
    </el-form>
  </div>
</template>
<script>

export default {
  name: 'CompanyInfo',
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
        return {}
      }
    },
    baseDimRules: {
      type: Object,
      default () {
        return {}
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
