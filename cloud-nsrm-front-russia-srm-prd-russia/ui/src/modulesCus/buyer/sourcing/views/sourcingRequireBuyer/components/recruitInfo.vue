<template>
  <SrmRow>
    <SrmCol :init-col="4">
      <el-form-item label="寻源单号" prop="reqHeadNo">
        <el-input v-model="baseForm.reqHeadNo" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="项目名称" prop="projectName">
        <el-input v-model="baseForm.projectName" :disabled="readonly" />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="公示截止时间" prop="publicEndTime">
        <el-date-picker
          v-model="baseForm.publicEndTime"
          :disabled="readonly"
          :picker-options="pickerOptions"
          type="datetime"
          format="yyyy-MM-dd HH:mm:ss"
          value-format="yyyy-MM-dd HH:mm:ss"
          default-time="17:00:00"
        />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="预算（万元）" prop="totalAmountByTenKilo">
        <el-input v-model="baseForm.totalAmountByTenKilo" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="品类" prop="categoryName">
        <el-input v-model="baseForm.categoryName" disabled />
      </el-form-item>
    </SrmCol>
    <!-- <SrmCol :init-col="4">
      <el-form-item label="规模数量" prop="requireQuantity">
        <el-input v-model="baseForm.requireQuantity" disabled />
      </el-form-item>
    </SrmCol> -->
    <SrmCol :init-col="4">
      <el-form-item label="申请单号" prop="requirementHeadNo">
        <el-input v-model="baseForm.requirementHeadNo" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol :init-col="4">
      <el-form-item label="需求来源" prop="requireFrom">
        <DictSelect v-model="baseForm.requireFrom" code="PR_SOU_REQUIREMENT_FROM" disabled />
      </el-form-item>
    </SrmCol>
    <SrmCol v-if="requirementHeadNoList.length > 1" :init-col="1">
      <span style="font-size:14px;">合并申请单号：</span>
      <template>
        <el-button v-for="(item,index) in requirementHeadNoList" :key="index" type="text" @click="applyNoClick(item,index)">
          {{ item }}
        </el-button>
      </template>
    </SrmCol>
    <SrmCol :init-col="1">
      <el-form-item label="项目概况与招标范围" prop="projectScope">
        <el-input
          v-model="baseForm.projectScope"
          :maxlength="3000"
          :disabled="readonly"
          :autosize="{ minRows: 4, maxRows: 6}"
          type="textarea"
        />
      </el-form-item>
    </SrmCol>
  </SrmRow>
</template>
<script>
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
import { cannotLessCurrentTime } from 'lib@/mixins/datePickerOptions'
import purchaseApplicationDetail2 from '@/modulesCus/buyer/purchasingDemand/views/purchaseApplication/purchaseApplicationDetailZhaobiao'
export default {
  components: {
    QuickSearch,
    OrganizationSelector
  },
  mixins: [cannotLessCurrentTime],
  props: {
    form: {
      type: Object,
      default: () => ({})
    },
    readonly: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {

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
    },
    requirementHeadNoList () {
      if (this.baseForm.requirementHeadNoList) {
        return this.baseForm.requirementHeadNoList.toString().split(',')
      }
      return []
    },
    pickerOptions () {
      return {
        ...this.cannotLessCurrentTimeOptions
      }
    }
  },
  methods: {
    applyNoClick (item, index) {
      if (this.form.requirementHeadIdList) {
        let curId = this.form.requirementHeadIdList.toString().split(',')[index]
        const row = {
          requirementHeadNum: item,
          requirementHeadId: curId
        }
        this.$emit('tab-add', {
          component: purchaseApplicationDetail2,
          params: {
            flag: 'approveNumber',
            row: row,
            showType: 'readOnly',
            tabName: 'purchaseApplicationDetail' + row.requirementHeadNum,
            activeWorkflowTab: false
          },
          title: row.requirementHeadNum,
          name: 'purchaseApplicationDetail' + row.requirementHeadNum
        })
      }
    }
  }
}
</script>
