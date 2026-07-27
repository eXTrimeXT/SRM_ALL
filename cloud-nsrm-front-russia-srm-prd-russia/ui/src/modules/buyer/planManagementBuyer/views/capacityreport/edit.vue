<template>
  <el-container
    class="capacityreportEdit"
    direction="vertical"
  >
    <el-main>
      <div class="form-container form-wrapper">
        <el-form
          ref="form"
          :model="form"
          :rules="rules"
        >
          <el-row :gutter="32">
            <el-col :span="6">
              <el-form-item
                prop="materialCode"
                :label="$t('supplierCapacityReport.materialCode')"
              >
                <QuickSearch
                  :disable="selectCategory"
                  :show-input="form.materialCode"
                  :pre-query-data="{
                    't.VENDOR_ID': $store.getters.user.companyId
                  }"
                  show-key="materialCode"
                  :scope-data="form"
                  name="scc_sup_auth_purchase_catalog"
                  @close-quicksearch="getCategoryObj"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="materialName"
                :label="$t('supplierCapacityReport.materialName')"
              >
                <el-input
                  v-model="form.materialName"
                  :disabled="disabled"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="vendorCode"
                :label="$t('supplierCapacityReport.vendorCode')"
              >
                <el-input
                  v-model="form.vendorCode"
                  :disabled="disabled"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="vendorName"
                :label="$t('supplierCapacityReport.vendorName')"
              >
                <el-input
                  v-model="form.vendorName"
                  :disabled="disabled"
                />
              </el-form-item>
            </el-col>
            <!-- <el-col :span="6">
        <el-form-item prop="vendorId" label="供应商ID">
                   <el-input v-model="form.vendorId" :disabled="disabled"/>
        </el-form-item>
         </el-col> -->
            <el-col :span="6">
              <el-form-item
                prop="categoryName"
                :label="$t('supplierCapacityReport.categoryName')"
              >
                <el-input
                  v-model="form.categoryName"
                  :disabled="disabled"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="productionDayNumber"
                :label="$t('supplierCapacityReport.productionDayNumber')"
              >
                <el-input
                  v-model="form.productionDayNumber"
                  v-input-format="{ type: 'number' }"
                  @change="changeDayNumber"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="monthlyWorkDays"
                :label="$t('supplierCapacityReport.monthlyWorkDays')"
              >
                <el-input
                  v-model="form.monthlyWorkDays"
                  v-input-format="{ type: 'number' }"
                  @change="changeWorkDays"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="productionMonthNumber"
                :label="$t('supplierCapacityReport.productionMonthNumber')"
              >
                <el-input
                  v-model="form.productionMonthNumber"
                  v-input-format="{ type: 'number' }"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                v-input-format="{ type: 'number' }"
                prop="monthTotalCapacity"
                :label="$t('supplierCapacityReport.monthTotalCapacity')"
              >
                <el-input v-model="form.monthTotalCapacity" />
              </el-form-item>
            </el-col>

            <el-col :span="6">
              <el-form-item
                prop="startTime"
                :label="$t('supplierCapacityReport.startTime')"
              >
                <el-date-picker
                  v-model="form.startTime"
                  type="date"
                  value-format="yyyy-MM-dd"
                  :placeholder="$t('vendorMod.datePicker')"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="endTime"
                :label="$t('supplierCapacityReport.endTime')"
              >
                <el-date-picker
                  v-model="form.endTime"
                  type="date"
                  value-format="yyyy-MM-dd"
                  :placeholder="$t('vendorMod.datePicker')"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
      <CToolbar>
        <template #right>
          <el-button
            @click="cancelBill"
          >
            {{ $t('common.cancel') }}
          </el-button>
          <el-button
            type="primary"
            :disabled="readOnly"
            @click="save"
          >
            {{ $t('common.save') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'
import CToolbar from 'lib@/components/c-toolbar'
import QuickSearch from 'lib@/components/QuickSearch'
import { capacityreport } from 'modb@/planManagementBuyer/api/index'

export default {
  name: 'CapacityreportEdit',
  components: {
    MainHeader,
    CToolbar,
    QuickSearch
  },
  mixins: [tabTodoMixin],
  data () {
    function calcDate (start, end) {
      if (!start || !end) return
      let startTime = new Date(start)
      let endTime = new Date(end)
      return Math.floor((endTime - startTime) / (24 * 3600 * 1000)) + 1
    }
    const validNum = (rule, value, callback) => {
      if (!/^\d+$/.test(value)) {
        callback(new Error(this.$t('supplierCapacityReport.noNegativeNumber')))
      } else {
        callback()
      }
    }
    const validCapacityTotal = (rule, value, callback) => {
      let productionMonthNumber = this.form.productionMonthNumber
      if (productionMonthNumber && value < productionMonthNumber) {
        callback(new Error(this.$t('supplierCapacityReport.notLessMonthlyCapacity')))
      } else {
        callback()
      }
    }
    const validCapacityMonth = (rule, value, callback) => {
      let monthTotalCapacity = this.form.monthTotalCapacity
      if (monthTotalCapacity && monthTotalCapacity < value) {
        callback(new Error(this.$t('supplierCapacityReport.notLessMonthlyCapacity')))
      } else {
        callback()
      }
    }
    const validWorkDays = (rule, value, callback) => {
      if (value > 31) {
        callback(new Error(this.$t('supplierCapacityReport.monthlyCannotExceed')))
      } else {
        let { startTime, endTime } = this.form
        if (startTime && endTime && calcDate(startTime, endTime) < value) {
          callback(new Error(this.$t('supplierCapacityReport.cannotSmaller')))
        } else {
          callback()
        }
      }
    }
    const validStartDate = (rule, value, callback) => {
      if (!value) {
        callback(new Error(this.$t('supplierCapacityReport.enterStartTime')))
      } else {
        let { monthlyWorkDays, endTime } = this.form
        if (endTime && new Date(value) > new Date(endTime)) {
          callback(new Error(this.$t('supplierCapacityReport.cannotEarlier')))
        } else if (monthlyWorkDays && endTime && calcDate(value, endTime) < monthlyWorkDays) {
          callback(new Error(this.$t('supplierCapacityReport.cannotSmaller')))
        } else {
          callback()
        }
      }
    }
    const validEndDate = (rule, value, callback) => {
      if (!value) {
        callback(new Error(this.$t('supplierCapacityReport.enterEndTime')))
      } else {
        let { monthlyWorkDays, startTime } = this.form
        if (startTime && new Date(startTime) > new Date(value)) {
          callback(new Error(this.$t('supplierCapacityReport.cannotEarlier')))
        } else if (monthlyWorkDays && startTime && calcDate(startTime, value) < monthlyWorkDays) {
          callback(new Error(this.$t('supplierCapacityReport.cannotSmaller')))
        } else {
          callback()
        }
      }
    }
    return {
      form: {
        materialCode: null,
        materialName: null,
        vendorCode: null,
        vendorName: null,
        categoryName: null,
        productionDayNumber: null,
        monthlyWorkDays: null,
        productionMonthNumber: null,
        monthTotalCapacity: null,
        isConfirmed: null,
        startTime: null,
        endTime: null,
        createId: null,
        createBy: null,
        lastUpdateId: null,
        lastUpdateBy: null,
        updateDate: null
      },
      rules: {
        materialCode: [{ required: true, message: this.$t('dataConfMod.msgInputItemCode') }],
        materialName: [{ required: true, message: this.$t('supplierCapacityReport.enterMaterialName') }],
        vendorCode: [{ required: true, message: this.$t('supplierCapacityReport.enterSupplierCode') }],
        vendorName: [{ required: true, message: this.$t('dataConfMod.msgVendorName') }],
        categoryName: [{ required: true, message: this.$t('supplierCapacityReport.enterMaterialSubcategory') }],
        productionDayNumber: [{ required: true, validator: validNum }],
        monthlyWorkDays: [
          { required: true, validator: validNum },
          { required: true, validator: validWorkDays }
        ],
        productionMonthNumber: [
          { required: true, validator: validNum },
          { required: true, validator: validCapacityMonth }
        ],
        monthTotalCapacity: [
          { required: true, validator: validNum },
          { required: true, validator: validCapacityTotal }
        ],
        startTime: [{ required: true, validator: validStartDate }],
        endTime: [{ required: true, validator: validEndDate }]

      },
      readOnly: false
    }
  },
  computed: {
  },
  watch: {
  },
  created () {
  },
  mounted () {
    const { flag, row, readOnly = false } = this.$attrs.params
    this.readOnly = readOnly
    if (flag === 'edit') {
      this.form = row
    }
  },
  methods: {

    getCategoryObj (v) {
      console.log(v)
      this.form.materialName = v ? v.materialName : ''
      this.form.vendorCode = v ? v.vendorCode : ''
      this.form.vendorName = v ? v.vendorName : ''
      this.form.materialCode = v ? v.materialCode : ''
      this.form.categoryName = v ? v.categoryName : ''
      this.form.vendorId = v ? v.vendorId : ''
      this.form.categoryId = v ? v.categoryId : ''
    },

    changeDayNumber (v) {
      if (this.form.monthlyWorkDays != null) {
        this.form.productionMonthNumber = v * this.form.monthlyWorkDays
      }
    },

    changeWorkDays (v) {
      if (this.form.productionDayNumber != null) {
        this.form.productionMonthNumber = v * this.form.productionDayNumber
      }
    },

    save () {
      this.$refs.form.validate(result => {
        if (result) {
          const { flag } = this.$attrs.params
          // 新增时不用提交主键值
          const { reportId, ...rest } = this.form
          if (flag === 'add') {
            capacityreport.add(rest).then(res => {
              this.$message({
                type: 'success',
                message: res.message
              })
              this.cancelBill()
            })
          } else if (flag === 'edit') {
            capacityreport.update(this.form).then(res => {
              this.$message({
                type: 'success',
                message: res.message
              })
              this.cancelBill()
            })
          }
        } else {
          this.__focus_error__()
        }
      })
    },
    cancelBill () {
      const { flag, row } = this.$attrs.params
      if (flag === 'add') {
        this.$emit('tab-remove', 'capacityreportEdit')
      } else {
        this.$emit('tab-remove', 'capacityreportEdit' + row.reportId)
      }
      this.__setTabTodo('capacityreportList.getQuerydata')
    },
    // 上传附件成功
    handleUploadSuccess (file, row, key) {
      const { id, name } = file
      row[key] = id.toString()
    },
    // 删除文件
    handleAttachmentRemove (row, key) {
      row[key] = ''
    }
  }
}
</script>
<style scoped lang="scss">
.capacityreportEdit {
  height: 100%;
  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }
  .el-table .el-date-editor {
    width: 135px;
  }
  .base-form {
    padding: 15px 30px 0;
  }
  .toRequired {
    color: #ff4949;
    padding-right: 2px;
  }
  .edit_cond {
    color: #23adf4;
    cursor: pointer;
  }
}
.form-wrapper {
  padding-bottom: 10px !important;
}
</style>
