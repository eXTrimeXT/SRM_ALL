<template>
  <SrmRow>
    <SrmCol :init-col="4">
      <!-- 项目编号 -->
      <el-form-item :label="$t('bidMod.bidingNum')" prop="projectCode">
        <el-input v-model="baseForm.projectCode" disabled />
      </el-form-item>
    </SrmCol>
    <srmCol :init-col="4">
      <!-- 项目名称 -->
      <el-form-item :label="$t('bidMod.bidingName')" prop="projectName">
        <QuickSearch
          :disabled="readonly"
          :showInput="baseForm.projectName"
          :scope-data="baseForm"
          name="sou_ch_ledger"
          @close-quicksearch="getProjectObj"
        />
        <!-- <el-select v-model="baseForm.projectName" allow-create filterable clearable>
          <el-option v-for="(item,index) in projectNameList" :key="index" :value="item.value">
            {{ item.label }}
          </el-option>
        </el-select> -->
      </el-form-item>
    </srmCol>
    <SrmCol :init-col="4">
      <!-- 轮数 -->
      <el-form-item :label="$t('bidMod.bidingRound')" prop="num">
        <el-input v-model="baseForm.num" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- 创建人 -->
      <el-form-item :label="$t('common.createdFullName')" prop="createdFullName">
        <el-input v-model="baseForm.createdFullName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- 联系方式 -->
      <el-form-item :label="$t('vendorMod.contactMethod')" prop="phone">
        <el-input v-model="baseForm.phone" :disabled="readonly" />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- 部门 -->
      <el-form-item :label="$t('vendorMod.department')" prop="depName">
        <el-input v-model="baseForm.depName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- 项目金额（卢布） -->
      <el-form-item :label="$t('cusEntry.centralizedPurchase.projectAmount')" prop="projMoney">
        <el-input v-model="baseForm.projMoney" :disabled="readonly" />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- 供货区域 -->
      <el-form-item :label="$t('sourcingBuyer.applySupplyArea')" prop="area">
        <!-- <DictSelect
          v-model="baseForm.area"
          :disabled="readonly"
          multiple
          code="REGION"
        /> -->
        <el-select
          v-model="baseForm.area"
          :disabled="readonly"
          clearable
          filterable
          multiple
        >
          <el-option
            v-for="(item,index) in areaList"
            :key="index"
            :disabled="item.disabled"
            :value="item.value"
            :label="item.label"
          />
        </el-select>
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <!-- 状态 -->
      <el-form-item :label="$t('common.status')" prop="status">
        <DictSelect
          v-model="baseForm.status"
          disabled
          code="DESIGN_PLAN_STATUS"
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="1">
      <!-- 项目介绍 -->
      <el-form-item :label="$t('cusEntry.centralizedPurchase.projectDesc')" prop="projIntroduce">
        <el-input
          v-model="baseForm.projIntroduce"
          type="textarea"
          :maxlength="100"
          :disabled="readonly"
          :autosize="{minRows:4,maxRows:6}"
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="1">
      <!-- 定价思路 -->
      <el-form-item :label="$t('cusEntry.centralizedPurchase.pricingStrategy')" prop="pricingIdeas">
        <el-input
          v-model="baseForm.pricingIdeas"
          type="textarea"
          :maxlength="100"
          :disabled="readonly"
          :autosize="{minRows:4,maxRows:6}"
        />
      </el-form-item>
    </SrmCol>
  </SrmRow>
</template>
<script>
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
import { getDictItem } from '@/api/common'
import { adaptDictData } from '@/utils'

export default {
  components: {
    QuickSearch,
    OrganizationSelector
  },
  props: {
    form: {
      type: Object,
      default: () => ({})
    },
    readonly: {
      type: Boolean,
      default: false
    },
    areaList: {
      type: Array,
      default: () => []
    }
  },
  data () {
    return {
      projectNameList: []
    }
  },
  computed: {
    baseForm: {
      get: function () {
        return this.form
      },
      set: function (val) {
        this.$emit('update:form', val)
      }
    }
  },
  watch: {
    'baseForm.area': {
      handler (nVal) {
        this.areaList.forEach(item => {
          this.$set(item, 'disabled', false)
        })
        if (nVal && nVal.length) {
          if (nVal.includes('0')) {
            this.areaList.forEach(item => {
              if (item.value !== '0') {
                this.$set(item, 'disabled', true)
              }
            })
          } else {
            this.areaList.forEach(item => {
              if (item.value === '0') {
                this.$set(item, 'disabled', true)
              }
            })
          }
        }
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    getProjectObj (val) {
      this.baseForm.projectName = val ? val.projectName : null
      this.baseForm.projectId = val ? val.ledgerId : null
      // this.baseForm.num = val ? val.addNum : null
    }
  }
}
</script>
