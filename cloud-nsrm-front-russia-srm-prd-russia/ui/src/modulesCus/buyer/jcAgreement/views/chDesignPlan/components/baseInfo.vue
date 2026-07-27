<template>
  <SrmRow>
    <SrmCol :init-col="4">
      <el-form-item label="项目编号" prop="projectCode">
        <el-input v-model="baseForm.projectCode" disabled />
      </el-form-item>
    </SrmCol>
    <srmCol :init-col="4">
      <el-form-item label="项目名称" prop="projectName">
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
      <el-form-item label="轮数" prop="num">
        <el-input v-model="baseForm.num" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="创建人" prop="createdFullName">
        <el-input v-model="baseForm.createdFullName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="联系方式" prop="phone">
        <el-input v-model="baseForm.phone" :disabled="readonly" />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="部门" prop="depName">
        <el-input v-model="baseForm.depName" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="项目金额（万元）" prop="projMoney">
        <el-input v-model="baseForm.projMoney" :disabled="readonly" />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="供货区域" prop="area">
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
      <el-form-item label="状态" prop="status">
        <DictSelect
          v-model="baseForm.status"
          disabled
          code="DESIGN_PLAN_STATUS"
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="1">
      <el-form-item label="项目介绍" prop="projIntroduce">
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
      <el-form-item label="定价思路" prop="pricingIdeas">
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
