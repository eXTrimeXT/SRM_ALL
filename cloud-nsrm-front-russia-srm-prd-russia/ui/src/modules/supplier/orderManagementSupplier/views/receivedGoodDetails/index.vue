<template>
  <el-container
    class="flex-container-notab the_receivedGoodDetail_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        form-label-width="120px"
        @getFormData="getQuerydata"
      />

      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <el-button
            type="primary"
            @click="detailExport"
          >
            {{
              $t('orderMod.excelExport')
            }}
          </el-button>
          <!-- <el-button type="primary" @click="detailImport" >明细导入</el-button> -->
          <!-- <el-button type="primary" @click="deleteOne">删除</el-button> -->
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :checkbox="true"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        url="/api-sup-ce/order/deliveryNoteDetail/receiveListPage"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'

export default {
  name: 'ReceivedGoodDetail',
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
      name: '',
      tableName: 'receivedGoodDetail_vendor',
      reviewFormNumber: '',
      gridData: [],
      pageSize: 15,
      gridId: 'list',
      selectList: [],
      currentRow: null,
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      statusList: [],
      form: {
        id: '',
        vendorCode: '',
        vendorCompanyName: '',
        reviewFormNumber: '',
        enabled: ''
      },
      rules: {
        vendorCode: [{ required: true, message: this.$t('bidMod.msgDictCode') }],
        vendorCompanyName: [{ required: true, message: this.$t('bidMod.msgDictName') }]
      },
      isModify: false,
      dialogFormVisible: false,
      formLabelWidth: '100px',
      preArr: [
        {
          prop: 'organizationId',
          label: () => this.$t('orderMod.buyerOrderSynergy.organizationName'),
          type: 'OUorganizationSelector'
        },
        {
          prop: 'deliveryNumber',
          label: () => this.$t('orderMod.buyerOrderSynergy.deliveryNumber')
        },
        {
          prop: 'containOld',
          label: () => this.$t('orderMod.buyerOrderSynergy.containOld'),
          type: 'dict',
          code: 'YES_OR_NO'
        },
        {
          prop: 'orderNumber',
          label: () => this.$t('orderMod.buyerOrderSynergy.orderNumber2')
        },
        {
          prop: 'materialName',
          label: () => this.$t('orderMod.buyerOrderSynergy.materialName')
        },
        {
          prop: 'materialCode',
          label: () => this.$t('orderMod.buyerOrderSynergy.materialCode')
        },
        {
          prop: 'startReceivedDate',
          label: () => this.$t('orderMod.buyerOrderSynergy.startReceivedDate'),
          type: 'date'
        },
        {
          prop: 'endReceivedDate',
          label: () => this.$t('orderMod.buyerOrderSynergy.endReceivedDate'),
          type: 'date'
        }
      ],
      queryParam: {},
      ableSelectTreeNodes: []
    }
  },
  activated () {
    console.log('[menuMaintenance activated]')
    this.$refs[this.gridId].doLayout()
  },
  created () {
    this.tableHeader = [
      {
        prop: 'organizationName',
        label: () => this.$t('orderMod.buyerOrderSynergy.organizationName'),
        width: 100
      },
      {
        prop: 'deliveryNumber',
        label: () => this.$t('orderMod.buyerOrderSynergy.deliveryNumber'),
        minWidth: 150
      },
      {
        prop: 'oldDeliveryNumber',
        label: () => this.$t('orderMod.buyerOrderSynergy.oldDeliveryNumber'),
        width: 130
      },
      {
        prop: 'orderNumber',
        label: () => this.$t('orderMod.buyerOrderSynergy.orderNumber2'),
        width: 130
      },
      {
        prop: 'actualReturnedNum',
        label: () => this.$t('orderMod.buyerOrderSynergy.actualReturnedNum'),
        width: 100,
        align: 'right'
      },
      {
        prop: 'replenishNum',
        label: () => this.$t('orderMod.buyerOrderSynergy.replenishNum'),
        width: 100,
        align: 'right'
      },
      {
        prop: 'deductionNum',
        label: () => this.$t('orderMod.buyerOrderSynergy.deductionNum'),
        width: 100,
        align: 'right'
      },
      {
        prop: 'orderNum',
        label: () => this.$t('orderMod.buyerOrderSynergy.orderNum'),
        width: 110,
        align: 'right'
      },
      {
        prop: 'receivedNum',
        label: () => this.$t('orderMod.buyerOrderSynergy.receivedNum'),
        width: 110,
        align: 'right'
      },
      {
        prop: 'badNum',
        label: () => this.$t('orderMod.buyerOrderSynergy.badNum'),
        width: 100,
        align: 'right'
      },
      {
        prop: 'badReason',
        label: () => this.$t('orderMod.buyerOrderSynergy.badReason'),
        width: 100
      },
      {
        prop: 'differenceNum',
        label: () => this.$t('orderMod.buyerOrderSynergy.differenceNum'),
        width: 100,
        align: 'right'
      },
      {
        prop: 'materialCode',
        label: () => this.$t('orderMod.buyerOrderSynergy.materialCode'),
        width: 100
      },
      {
        prop: 'materialName',
        label: () => this.$t('orderMod.buyerOrderSynergy.materialName'),
        minWidth: 150
      },
      {
        prop: 'receivedTime',
        label: () => this.$t('orderMod.buyerOrderSynergy.receivedTime'),
        width: 100
      }
    ]
    this.defaultTableHeader = [...this.tableHeader]
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    getQuerydata (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      this.currentRow = val
    },
    detailImport () {},
    deleteOne () {},
    detailExport () {}
  }
}
</script>
<style scoped lang="scss">
.the_receivedGoodDetail_wrapper {
}
</style>
