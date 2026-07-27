<template>
  <el-container
    class="flex-container-notab the_inventory_wrapper"
    direction="vertical"
  >
    <el-main>
      <div style="padding-bottom: 12px">
        <FormWrapper
          :select-dictionary="selectDictionary"
          :form-array="preArr"
          @getFormData="getQuerydata"
        />
      </div>

      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :checkbox="true"
        :open-custom-table="true"
        url="/api-sup-ce/deliver/orderDeliveryDetail/orderDeliveryDetailListPage"
      />
      <!-- 弹框区域-->
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { parseTime, adaptDictData } from '@/utils'
import { getDictItemList } from '@/api/common'
import { carInfoMaintenancesApi } from 'mods@/orderManagementSupplier/api'

export default {
  name: 'OrderDeliverList',
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
      selectDictionary: {},
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
        { prop: 'orderNumber', label: this.$t('purSettlementMod.orderNumber') },
        {
          prop: 'orderDate',
          label: this.$t('oneStopShopping.orderDate'),
          type: 'date'
        },
        {
          prop: 'orgId',
          label: this.$t('oneStopShopping.businessEntity'),
          type: 'OUorganizationSelector'
        },
        { prop: 'vendorName', label: this.$t('common.vendor') },
        {
          prop: 'materialCode',
          label: () => this.$t('purchaseDemand.itemCode'),
          type: 'quicksearch',
          showKey: 'materialCode',
          name: 'scc_base_material_item'
        },
        { prop: 'deliverPlanNum', label: this.$t('orderMod.arrivalPlanNo') }
      ],
      carInfoStatus: [],
      carTypes: []
    }
  },
  created () {
    this.tableHeader = [
      {
        prop: 'orderNumber',
        label: this.$t('purSettlementMod.orderNumber'),
        width: 150
      },
      {
        prop: 'orderDate',
        label: this.$t('oneStopShopping.orderDate'),
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      { prop: 'orgName', label: this.$t('dataConfMod.orgId'), width: 150 },
      {
        prop: 'organizationName',
        label: this.$t('dataConfMod.organizationId'),
        width: 150
      },
      { prop: 'vendorName', label: this.$t('common.vendor'), width: 150 },
      {
        prop: 'buyerName',
        label: this.$t('orderMod.buyerOrderSynergy.buyerName'),
        width: 100
      },
      {
        prop: 'categoryName',
        label: this.$t('dataConfMod.categoryLittle'),
        width: 100
      },
      {
        prop: 'materialCode',
        label: this.$t('common.materialCode'),
        width: 100
      },
      {
        prop: 'materialName',
        label: this.$t('orderMod.buyerOrderSynergy.materialName'),
        width: 150
      },
      {
        prop: 'orderQuantity',
        label: this.$t('orderMod.buyerOrderSynergy.orderNum'),
        width: 100
      },
      {
        prop: 'requirementDate',
        label: this.$t('orderMod.buyerOrderSynergy.requirementDateStr'),
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'planReceiveNum',
        label: this.$t('orderMod.planDeliveryQuantity'),
        width: 120
      },
      {
        prop: 'planReceiveDate',
        label: this.$t('orderMod.planDeliveryDate'),
        width: 120,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'deliverPlanNum',
        label: this.$t('orderMod.arrivalPlanNo'),
        width: 120
      }
    ]
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => this.getQuerydata())
  },
  mounted () {
    this.fetchDictionary()
  },
  methods: {
    fetchDictionary () {
      const dictionaryCodes = ['CAR_INFO_STATUS', 'CAR_TYPE'].map((i) => ({
        dictCode: i
      }))
      getDictItemList(dictionaryCodes).then((res) => {
        const [CAR_INFO_STATUS, CAR_TYPE] = res.data
        this.carInfoStatus = adaptDictData(CAR_INFO_STATUS.CAR_INFO_STATUS)
        this.carTypes = adaptDictData(CAR_TYPE.CAR_TYPE)
        this.selectDictionary = {
          carType: this.carTypes,
          status: this.carInfoStatus
        }
      })
    },
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
