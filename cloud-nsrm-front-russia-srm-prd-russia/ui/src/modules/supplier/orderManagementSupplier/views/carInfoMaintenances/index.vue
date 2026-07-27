<template>
  <el-container
    class="flex-container-notab the_inventory_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        @getFormData="getQuerydata"
      />
      <MainHeader>
        <template slot="left">
          <el-button
            type="primary"
            @click="editTab('add')"
          >
            {{ $t('common.add') }}
          </el-button>
          <el-button
            :disabled="!currentRows.length"
            @click="deleteOne"
          >
            {{ $t('common.inactive') }}
          </el-button>
          <el-button
            :disabled="!currentRows.length"
            @click="submit"
          >
            {{ $t('common.submit') }}
          </el-button>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :checkbox="true"
        :open-custom-table="true"
        url="/api-sup-ce/order/carInfo/listPage"
      />
      <!-- 弹框区域-->
      <srm-dialog
        :title="title"
        :visible.sync="dialogFormVisible"
        :close-on-click-modal="false"
        size="middle"
      >
        <el-form
          ref="form"
          :model="form"
          class="form-incontainer"
          :rules="rules"
          label-width="80px"
          label-position="top"
        >
          <srm-row>
            <srm-col :initCol="2">
              <el-form-item
                :label="$t('orderMod.buyerOrderSynergy.licensePlate')"
                :label-width="formLabelWidth"
                prop="licensePlate"
              >
                <el-input v-model="form.licensePlate" />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="2">
              <el-form-item
                :label="$t('orderMod.buyerOrderSynergy.carType')"
                :label-width="formLabelWidth"
                prop="carType"
              >
                <DictSelect
                  v-model="form.carType"
                  code="CAR_TYPE"
                />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="2">
              <el-form-item
                :label="$t('orderMod.buyerOrderSynergy.effectiveDate')"
                :label-width="formLabelWidth"
                prop="effectiveDate"
                :picker-options="pickerOptions"
              >
                <el-date-picker
                  v-model="form.effectiveDate"
                  type="date"
                  :placeholder="$t('bidMod.datePicker')"
                />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="2">
              <el-form-item
                :label="$t('orderMod.buyerOrderSynergy.expirationDate')"
                :label-width="formLabelWidth"
                prop="expirationDate"
                :picker-options="pickerOptions"
              >
                <el-date-picker
                  v-model="form.expirationDate"
                  type="date"
                  :placeholder="$t('bidMod.datePicker')"
                />
              </el-form-item>
            </srm-col>
          </srm-row>
        </el-form>
        <div
          slot="footer"
          class="dialog-footer"
        >
          <el-button @click="dialogFormVisible = false">
            {{ $t('common.cancel') }}
          </el-button>
          <el-button
            v-preventReClick="3000"
            type="primary"
            @click="addOne"
          >
            {{ $t('common.confirm') }}
          </el-button>
        </div>
      </srm-dialog>
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { parseTime } from '@/utils'
import { carInfoMaintenancesApi } from 'mods@/orderManagementSupplier/api'

export default {
  name: 'AccessFlowSetting',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  provide () {
    return { context: this }
  },
  data () {
    return {
      pageSize: 15,
      gridId: 'list',
      title: this.$t('orderMod.addCarInfo'),
      selectList: [],
      currentRows: [],
      tableHeader: [],
      tableData: [],
      form: {
        licensePlate: '',
        carType: '',
        effectiveDate: '',
        expirationDate: ''
      },
      accessTypeList: [
        { label: 'v1', value: this.$t('orderMod.buyerOrderSynergy.process1') },
        { label: 'v2', value: this.$t('orderMod.buyerOrderSynergy.process2') }
      ],
      rules: {
        licensePlate: [{ required: true, message: this.$t('orderMod.msgVendorOrder[20]') }],
        carType: [{ required: true, message: this.$t('orderMod.msgOrder[28]') }],
        effectiveDate: [{ required: true, message: this.$t('purchaseDemand.startDateTips') }],
        expirationDate: [{ required: true, message: this.$t('orderMod.msgVendorOrder[21]') }]
      },
      dialogFormVisible: false,
      queryParam: {},
      formLabelWidth: '100px',
      isModify: false,
      preArr: [
        {
          prop: 'status',
          label: () => this.$t('orderMod.buyerOrderSynergy.status'),
          type: 'dict',
          code: 'CAR_INFO_STATUS'
        },
        {
          prop: 'carType',
          label: () => this.$t('orderMod.buyerOrderSynergy.carType'),
          type: 'dict',
          code: 'CAR_TYPE'
        },
        {
          prop: 'licensePlate',
          label: () => this.$t('orderMod.buyerOrderSynergy.licensePlate')
        }
      ],
      carInfoStatus: [],
      pickerOptions: {
        disabledDate (time) {
          const today = new Date()
          today.setHours(0)
          today.setMinutes(0)
          today.setSeconds(0)
          today.setMilliseconds(0)
          return time.getTime() < today.getTime()
        }
      }
    }
  },
  created () {
    this.tableHeader = [
      {
        prop: 'lastUpdateDate',
        label: () => this.$t('orderMod.buyerOrderSynergy.lastUpdateDate'),
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'status',
        label: () => this.$t('orderMod.buyerOrderSynergy.status'),
        dataType: 'dict',
        code: 'CAR_INFO_STATUS'
      },
      {
        prop: 'licensePlate',
        label: () => this.$t('orderMod.buyerOrderSynergy.licensePlate'),
        width: 100,
        showType: 'button',
        btnStyle: 'text',
        callback: (row) => this.editTab('edit', row)
      },
      {
        prop: 'carType',
        label: () => this.$t('orderMod.buyerOrderSynergy.carType'),
        dataType: 'dict',
        code: 'CAR_TYPE'
      },
      {
        prop: 'effectiveDate',
        label: () => this.$t('orderMod.buyerOrderSynergy.effectiveDate'),
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'expirationDate',
        label: () => this.$t('orderMod.buyerOrderSynergy.expirationDate'),
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      }
    ]
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => this.getQuerydata())
  },
  mounted () {
  },
  methods: {
    getQuerydata (v) {
      if (v) this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    // 编辑tab
    editTab (type, row) {
      if (type === 'add') {
        // 新增
        this.isModify = false
        this.title = this.$t('orderMod.addCarInfo')
        for (let i in this.form) {
          this.form[i] = ''
        }
      } else {
        this.isModify = true
        // 修改
        this.title = this.$t('orderMod.modifyCarInfo')
        this.form = row
      }
      this.dialogFormVisible = true
    },
    deleteOne () {
      // 批量失效
      const data = this.currentRows
      if (!data.length) {
 return this.$message({
          type: 'warning',
          message: this.$t('orderMod.msgVendorOrder[22]')
        })
}
      const submitStaus = ['EFFECTIVE']
      if (this.currentRows.some((i) => submitStaus.findIndex((j) => j === i.status) === -1)) {
        this.$message({
          type: 'warning',
          message: this.$t('orderMod.msgVendorOrder[23]')
        })
        return
      }
      const params = data.map((i) => i.carInfoId)
      carInfoMaintenancesApi.carInfoInvalidBatch(params).then((res) => {
        this.$message({ message: res.message, type: 'success' })
        this.getQuerydata()
      })
    },
    submit () {
      // 批量提交
      const data = this.currentRows
      if (!data.length) {
 return this.$message({
          type: 'warning',
          message: this.$t('orderMod.msgVendorOrder[24]')
        })
}
      const submitStaus = ['CREATE']
      if (this.currentRows.some((i) => submitStaus.findIndex((j) => j === i.status) === -1)) {
        this.$message({
          type: 'warning',
          message: this.$t('orderMod.msgVendorOrder[25]')
        })
        return
      }
      const params = data.map((i) => i.carInfoId)
      carInfoMaintenancesApi.carInfoSubmitBatch(params).then((res) => {
        this.$message({ message: res.message, type: 'success' })
        this.getQuerydata()
      })
    },
    addOne () {
      // 验证form表单
      this.$refs.form.validate((valid) => {
        if (valid) {
          const data = { ...this.form }
          if (this.isModify) {
            carInfoMaintenancesApi.carInfoUpdate(data).then((res) => {
              this.$message({ message: res.message, type: 'success' })
              this.getQuerydata()
              this.dialogFormVisible = false
            })
          } else {
            carInfoMaintenancesApi.carInfoSave(data).then((res) => {
              this.$message({ message: res.message, type: 'success' })
              this.getQuerydata()
              this.dialogFormVisible = false
            })
          }
        }
      })
    },
    handleCurrentChange (val) {
      this.currentRows = val
    }
  }
}
</script>
<style scoped lang="scss"></style>
