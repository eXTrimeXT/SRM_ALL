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
            @click="openAddDialog"
          >
            {{ $t('common.add') }}
          </el-button>
          <el-button
            type="primary"
            :disabled="!currentRows.length"
            @click="handleDelete('mutil')"
          >
            {{ $t('common.delete') }}
          </el-button>
          <el-button
            type="primary"
            :disabled="!currentRows.length"
            @click="handleSave('mutil')"
          >
            {{
              $t('common.save')
            }}
          </el-button>
          <el-button
            type="primary"
            @click="detailExport"
          >
            {{
              $t('orderMod.excelExport')
            }}
          </el-button>
          <MImport
            ref="import"
            style="display: inline-block; margin-left: 15px"
            :title="iModal.title"
            :up-load-url="iModal.upLoadUrl"
            :extra-data="extraData"
            @downloadTemplate="downloadTemplate"
            @handleSuccess="getQuerydata"
          />
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :checkbox="true"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        url="/api-sup-ce/pm/receive/listPage"
      />
      <!-- 新增收货弹窗 -->
      <srm-dialog
        :title="$t('orderMod.selReceiptDetail')"
        size="large"
        :visible.sync="dialogFormVisible"
        :close-on-click-modal="false"
      >
        <FormWrapper
          :form-array="queryForm1"
          form-label-width="120px"
          @getFormData="getQuerydata1"
        />

        <MainHeader
          :l-span="22"
          :r-span="2"
        >
          <template slot="left">
            <el-button
              type="primary"
              :disabled="!currentRows1.length"
              @click="handleAdd"
            >
              {{
                $t('common.affirm')
              }}
            </el-button>
            <el-button
              type="primary"
              @click="dialogFormVisible = false"
            >
              {{
                $t('common.cancel')
              }}
            </el-button>
          </template>
        </MainHeader>
        <TableView
          ref="receivedDetailList"
          table-height="260px"
          :table-header="tableHeader1"
          :check-change="handleCurrentChange1"
          :page-size="pageSize1"
          :checkbox="true"
          :pre-query-data="queryParam1"
          url="/api-sup-ce/pm/receive/detail/listPage"
        >
          <template #receiptPlace="{ scope }">
            <RenderAsyncText :cell-value="scope.row.receiptPlace" />
          </template>
        </TableView>
      </srm-dialog>
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import RenderAsyncText from '@/library/components/provice-city/renderAsyncText'
import { parseTime, adaptDictData } from '@/utils'
import { getDictItemList } from '@/api/common'
import MImport from 'lib@/components/import'
import { downloadFileLink, downloadFileLinkByPost } from 'lib@/utils/file'

export default {
  name: 'ReceivedGoodDetail',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    MImport,
    RenderAsyncText
  },
  provide () {
    return { context: this }
  },
  data () {
    return {
      gridId: 'list',
      pageSize: 15,
      iModal: {
        title: this.$t('common.excelImport'),
        upLoadUrl: '/api-sup-ce/pm/receive/importExcel'
      },
      extraData: {
        fileModular: 'sup-ce',
        fileFunction: 'receivedGoodDetail',
        fileType: 'excel'
      },
      currentRows: [],
      tableHeader: [],
      preArr: [
        {
          prop: 'orgId',
          label: () => this.$t('oneStopShopping.businessEntity'),
          type: 'OUorganizationSelector'
        },
        {
          prop: 'vendorName',
          label: () => this.$t('orderMod.buyerOrderSynergy.vendorName'),
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info_all'
        },
        {
          prop: 'orderNumber',
          label: () => this.$t('orderMod.buyerOrderSynergy.orderNumber2')
        },
        {
          prop: 'materialCode',
          label: () => this.$t('orderMod.buyerOrderSynergy.materialCode')
        },
        {
          prop: 'materialName',
          label: () => this.$t('orderMod.buyerOrderSynergy.materialName')
        },
        {
          prop: 'deliveryNumber',
          label: () => this.$t('orderMod.buyerOrderSynergy.deliveryNumber')
        },
        {
          prop: 'startDate',
          label: () => this.$t('orderMod.buyerOrderSynergy.startReceivedDate'),
          type: 'date'
        },
        {
          prop: 'endDate',
          label: () => this.$t('orderMod.buyerOrderSynergy.endReceivedDate'),
          type: 'date'
        },
        {
          prop: 'receiveStatus',
          label: () => this.$t('orderMod.buyerOrderSynergy.status'),
          type: 'dict',
          code: 'RECEIVED_STATUS'
        }
      ],
      queryParam: {},
      // 新增弹窗参数--开始
      dialogFormVisible: false,
      queryForm1: [
        {
          prop: 'orgId',
          label: () => this.$t('oneStopShopping.businessEntity'),
          type: 'OUorganizationSelector'
        },
        {
          prop: 'vendorName',
          label: () => this.$t('orderMod.buyerOrderSynergy.vendorName'),
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info_all'
        },
        {
          prop: 'orderNumber',
          label: () => this.$t('orderMod.buyerOrderSynergy.orderNumber')
        },
        {
          prop: 'deliveryNumber',
          label: () => this.$t('orderMod.buyerOrderSynergy.deliveryNumber')
        },
        {
          prop: 'materialCode',
          label: () => this.$t('orderMod.buyerOrderSynergy.materialCode')
        },
        {
          prop: 'materialName',
          label: () => this.$t('orderMod.buyerOrderSynergy.materialName')
        }
      ],
      tableHeader1: [
        {
          prop: 'orgName',
          label: () => this.$t('oneStopShopping.businessEntity'),
          width: 150
        },
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.vendorName'),
          prop: 'vendorName',
          width: 150
        },
        {
          label: () =>
            this.$t('orderMod.buyerOrderSynergy.orderNumber') +
            '|' +
            this.$t('purchaseDemand.lineNum'),
          prop: 'orderNumber',
          width: 170,
          formattor: (val, row) => {
            return val + '|' + row.orderLineNum
          }
        },
        {
          label: () =>
            this.$t('orderMod.buyerOrderSynergy.deliveryNumber') +
            '|' +
            this.$t('purchaseDemand.lineNum'),
          prop: 'deliveryNumber',
          width: 170,
          formattor: (val, row) => {
            return val + '|' + row.orderLineNum
          }
        },
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.categoryName'),
          prop: 'categoryName',
          width: 130
        },
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.materialCode'),
          prop: 'materialCode',
          width: 100
        },
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.materialName'),
          prop: 'materialName',
          width: 100
        },
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.orderNum'),
          prop: 'orderNum',
          width: 100,
          align: 'right'
        },
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.deliveryQuantity'),
          prop: 'deliveryQuantity',
          width: 100
        },
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.deliveryDate'),
          prop: 'deliveryDate',
          width: 100,
          dataType: 'dateTime'
        },
        {
          label: () => this.$t('oneStopShopping.receiveAddress'),
          prop: 'receiptPlace',
          slot: 'receiptPlace',
          showType: 'slot',
          width: 100
        },
        {
          label: () => this.$t('oneStopShopping.receiveOrderAddress'),
          prop: 'receiveOrderAddress',
          width: 100
        }
      ],
      pageSize1: 15,
      queryParam1: {},
      currentRows1: []
      // 新增弹窗参数--结束
    }
  },
  activated () {
    this.$refs[this.gridId].doLayout()
  },
  created () {
    this.tableHeader = [
      {
        prop: 'vendorName',
        label: () => this.$t('orderMod.buyerOrderSynergy.vendorName'),
        width: 120
      },
      {
        prop: 'vendorCode',
        label: () => this.$t('orderMod.buyerOrderSynergy.vendorCode'),
        width: 120
      },
      {
        prop: 'orgName',
        label: () => this.$t('oneStopShopping.businessEntity'),
        width: 120
      },
      {
        label: () =>
          this.$t('orderMod.buyerOrderSynergy.orderNumber') +
          '|' +
          this.$t('purchaseDemand.lineNum'),
        prop: 'orderNumber',
        width: 170,
        formattor: (val, row) => {
          return val + '|' + row.orderLineNum
        }
      },
      {
        label: () =>
          this.$t('orderMod.buyerOrderSynergy.deliveryNumber') +
          '|' +
          this.$t('purchaseDemand.lineNum'),
        prop: 'deliveryNumber',
        width: 170,
        formattor: (val, row) => {
          return val + '|' + row.deliveryLineNum
        }
      },
      {
        prop: 'materialCode',
        label: () => this.$t('orderMod.buyerOrderSynergy.materialCode'),
        width: 120
      },
      {
        prop: 'materialName',
        label: () => this.$t('orderMod.buyerOrderSynergy.materialName'),
        minWidth: 150
      },
      {
        prop: 'orderNum',
        label: () => this.$t('orderMod.buyerOrderSynergy.orderNum'),
        width: 110,
        align: 'right'
      },
      {
        prop: 'deliveryQuantity',
        label: () => this.$t('orderMod.buyerOrderSynergy.deliveryQuantity'),
        width: 110,
        align: 'right'
      },
      {
        prop: 'receivedNum',
        label: () => this.$t('orderMod.buyerOrderSynergy.receivedNum'),
        width: 110,
        showType: 'input',
        inputType: 'number',
        editable: (row) => row.receiveStatus === 'PROTOCOL',
        align: 'right',
        addStarToColumn: true
      },
      {
        prop: 'badNum',
        label: () => this.$t('orderMod.buyerOrderSynergy.badNum'),
        width: 100,
        showType: 'input',
        inputType: 'number',
        editable: (row) => row.receiveStatus === 'PROTOCOL',
        align: 'right'
      },
      {
        prop: 'differenceNum',
        label: () => this.$t('orderMod.buyerOrderSynergy.differenceNum'),
        width: 100,
        showType: 'input',
        inputType: 'number',
        editable: (row) => row.receiveStatus === 'PROTOCOL',
        align: 'right'
      },
      {
        prop: 'actualReturnedNum',
        label: () => this.$t('orderMod.buyerOrderSynergy.actualReturnedNum'),
        width: 100,
        showType: 'input',
        inputType: 'number',
        editable: (row) => row.receiveStatus === 'PROTOCOL',
        align: 'right'
      },
      {
        prop: 'replenishNum',
        label: () => this.$t('orderMod.buyerOrderSynergy.replenishNum'),
        width: 100,
        showType: 'input',
        inputType: 'number',
        editable: (row) => row.receiveStatus === 'PROTOCOL',
        align: 'right'
      },
      {
        prop: 'deductionNum',
        label: () => this.$t('orderMod.buyerOrderSynergy.deductionNum'),
        width: 100,
        showType: 'input',
        inputType: 'number',
        editable: (row) => row.receiveStatus === 'PROTOCOL',
        align: 'right'
      },
      {
        prop: 'comments',
        label: () => this.$t('common.remark'),
        width: 100,
        showType: 'input',
        editable: (row) => row.receiveStatus === 'PROTOCOL'
      },
      {
        label: () => this.$t('orderMod.buyerOrderSynergy.receivedTime'),
        prop: 'receiveDate',
        width: 120,
        formattor: (val) => (val ? parseTime(val, '{y}-{m}-{d}') : '')
      },
      {
        prop: 'receiveStatus',
        label: () => this.$t('common.status'),
        dataType: 'dict',
        code: 'RECEIVED_STATUS',
        width: 120
      },
      {
        label: () => this.$t('orderMod.buyerOrderSynergy.createdBy'),
        prop: 'createdUserName',
        width: 100
      },
      {
        label: () => this.$t('orderMod.buyerOrderSynergy.creationDate'),
        prop: 'creationDate',
        width: 100,
        formattor: (val) => (val ? parseTime(val, '{y}-{m}-{d}') : '')
      },
      {
        label: () => this.$t('orderMod.buyerOrderSynergy.lastUpdateBy'),
        prop: 'createdUserName', // createdBy
        width: 120
      },
      {
        label: () => this.$t('orderMod.buyerOrderSynergy.lastUpdateDate'),
        prop: 'lastUpdateDate',
        width: 120,
        formattor: (val) => (val ? parseTime(val, '{y}-{m}-{d}') : '')
      },
      {
        prop: 'operation',
        label: () => this.$t('common.operation'),
        width: 100,
        btnStyle: 'text',
        fixed: 'right',
        showType: 'buttons',
        buttons: [
          {
            callback: (row) => this.handleDelete('one', [row]),
            formattor: () => this.$t('common.delete'),
            show: (row) => row.receiveStatus === 'PROTOCOL'
          },
          {
            callback: (row) => this.handleSave('one', [row]),
            formattor: () => this.$t('common.save'),
            show: (row) => row.receiveStatus === 'PROTOCOL'
          }
        ]
      }
    ]
  },
  mounted () {
    this.getQuerydata()
  },
  methods: {
    downloadTemplate () {
      downloadFileLink(
        '/api-sup-ce/pm/receive/exportExcelTemplate',
        this.$t('orderMod.receiptDetailUpdateXLS')
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    },
    detailExport () {
      downloadFileLinkByPost(
        '/api-sup-ce/pm/receive/exportExcel',
        this.$t('orderMod.receiptDetailExport'),
        this.queryParam || {}
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    },
    getQuerydata (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      this.currentRows = val
    },
    // 打开新增订单通知弹窗
    openAddDialog () {
      this.dialogFormVisible = true
      this.getQuerydata1()
    },
    // 查询送货明细列表
    getQuerydata1 (v) {
      this.queryParam1 = v
      this.$nextTick(() => {
        this.$refs['receivedDetailList'].query()
      })
    },
    // 选择送货明细
    handleCurrentChange1 (val) {
      this.currentRows1 = val
    },
    // 新增
    handleAdd () {
      const selectData = this.currentRows1.map(({ deliveryNumber, materialCode, orderNumber }) => ({
        deliveryNumber,
        materialCode,
        orderNumber
      }))
      this.$http({
        url: '/api-sup-ce/pm/receive/batchSaveOrUpdate',
        method: 'POST',
        data: selectData,
        loading: true
      }).then((res) => {
        this.dialogFormVisible = false
        this.getQuerydata()
      })
    },
    // 删除
    handleDelete (type, rows) {
      let list = type === 'mutil' ? this.currentRows : rows
      let count = 0
      list.forEach((item) => {
        if (item.receiveStatus != 'PROTOCOL') {
          count++
        }
      })
      this.$nextTick(() => {
        if (count <= 0) {
          let handelData = list.map((i) => i.warehousingReturnDetailId)
          this.$http({
            url: '/api-sup-ce/pm/receive/bathDelete',
            method: 'POST',
            data: handelData,
            loading: true
          }).then((res) => {
            this.$message({
              type: 'success',
              message: this.$t('common.successDelete')
            })
            this.getQuerydata()
          })
        } else {
          this.$message({
            type: 'warning',
            message: this.$t('purchaseDemand.have') + count + this.$t('orderMod.msgOrder[33]')
          })
        }
      })
    },
    // 保存
    handleSave (type, rows) {
      let list = type === 'mutil' ? this.currentRows : rows
      let count = 0
      list.forEach((item) => {
        if (item.receiveStatus != 'PROTOCOL') {
          count++
        }
      })
      this.$nextTick(() => {
        if (count <= 0) {
          let handelData = list.map(
            ({
              warehousingReturnDetailId,
              receivedNum,
              badNum,
              differenceNum,
              actualReturnedNum,
              replenishNum,
              deductionNum,
              comments
            }) => ({
              warehousingReturnDetailId,
              receivedNum,
              badNum,
              differenceNum,
              actualReturnedNum,
              replenishNum,
              deductionNum,
              comments
            })
          )
          let unFillCount = 0
          handelData.forEach((item) => {
            if (!item.receivedNum) {
              unFillCount++
            }
          })
          this.$nextTick(() => {
            if (unFillCount > 0) {
              this.$message({
                type: 'warning',
                message: this.$t('orderMod.msgOrder[34]')
              })
            } else {
              this.$http({
                url: '/api-sup-ce/pm/receive/batchSaveOrUpdate',
                method: 'POST',
                data: handelData,
                loading: true
              }).then((res) => {
                this.$message({
                  type: 'success',
                  message: this.$t('common.successSave')
                })
                this.getQuerydata()
              })
            }
          })
        } else {
          this.$message({
            type: 'warning',
            message: this.$t('purchaseDemand.have') + count + this.$t('orderMod.msgOrder[35]')
          })
        }
      })
    }
  }
}
</script>
<style scoped lang="scss"></style>
