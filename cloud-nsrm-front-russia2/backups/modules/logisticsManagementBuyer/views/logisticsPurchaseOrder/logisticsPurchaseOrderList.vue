<template>
  <el-container
    class="flex-container the_contractTemplateList_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :form-array="preArr"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />

      <main-header
        v-if="curRole === 'BUYER'"
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <el-button
            type="primary"
            @click="addOne"
          >
            {{
              $t("common.add")
            }}
          </el-button>
          <el-button
            type="primary"
            @click="delMore"
          >
            {{
              $t("common.delete")
            }}
          </el-button>
          <el-button
            type="primary"
            @click="asyncTMS"
          >
            {{
              $t("logisticsMod.syncTMS")
            }}
          </el-button>
          <el-button
            type="primary"
            @click="toPrint"
          >
            {{
              $t("route.pdfPrint")
            }}
          </el-button>
        </template>
      </main-header>
      <table-view
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :check-change="checkChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :checkbox="true"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-pd/po/order-head/listPage"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { parseTime } from '@/utils'
import logisticsPurchaseOrderDetail from './logisticsPurchaseOrderDetail'

export default {
  name: 'LogisticsPurchaseApplyList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    logisticsPurchaseOrderDetail
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      curRole: this.$store.getters.userType,
      name: 'contractTemplateTable',
      tableName: 'logisticsPurchaseApplyList',
      reviewFormNumber: '',
      gridData: [],
      pageSize: 15,
      gridId: 'list',
      selectList: [],
      currentRow: null,
      currentRows: [],
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      isModify: false,
      globalNickname: null,
      preArr: [
        {
          prop: 'orderHeadNum',
          label: this.$t('logisticsMod.orderNum') // 订单编号
        },
        {
          prop: 'vendorName',
          label: this.$t('common.vendorName'), // 供应商名称
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info'
        },
        {
          prop: 'contractCode',
          label: this.$t('purchaseDemand.contractNum') // 合同编号
        },
        {
          prop: 'ceeaApplyUserNickname',
          label: this.$t('bidMod.quotePurchasor') // 采购员
        },
        {
          prop: 'businessModeCode',
          label: this.$t('logisticsMod.businessMode'),
          type: 'dict',
          code: 'BUSINESS_MODE' // 业务模式
        },
        {
          prop: 'transportModeCode',
          label: this.$t('bid_mod.transportType'),
          type: 'dict',
          code: 'TRANSPORT_MODE' // 运输方式
        },
        {
          prop: 'ifNeedVendorComfirm',
          label: this.$t('oneStopShopping.ifSupplierConfirm'), // 是否供方确认
          type: 'dict',
          code: 'YES_OR_NO'
        },
        {
          prop: 'orderStatus',
          label: this.$t('orderMod.buyerOrderSynergy.orderStatus'),
          type: 'dict',
          code: 'LOGISTICS_PRICE_STATUS'
        }, // 订单状态
        {
          prop: 'requirementHeadNum',
          label: this.$t('logisticsMod.applyDocumentNum')
        }, // 申请单据号
        {
          prop: 'serviceProjectName',
          label: this.$t('logisticsMod.serviceProjectName'), // 服务项目名称
          type: 'quicksearch',
          showKey: 'projectName',
          name: 'ceea_logistics_project_info'
        },
        { prop: 'tmsStatus',
          label: this.$t('contractMod.syncStatus'),
          type: 'dict',
          code: 'LOGISTICS_ORDER_TMS_STATUS'
        }, // 同步状态
        {
        prop: 'orderSourceFrom',
        label: this.$t('logisticsMod.orderSourceFrom'), // 来源方式
        type: 'dict',
        code: 'SOURCE_DATA'
        }
      ],
      queryParam: {},
      prTypeList: [],
      purchaseTypeList: []
    }
  },
  watch: {
    $route: {
      // 当前功能已经打开的时候监听是否是从其他功能跳转过来的
      deep: true,
      immediate: true,
      handler () {
        if (
          this.$route.params.from === 'fromFun' &&
          this.$route.params.funName === 'purchaseApplication'
        ) {
          let requirementHeadId = Number(this.$route.params.formId)
          let formNo = this.$route.params.formNo // 流程标题
          let row = {
            ...this.$route.params,
            requirementHeadId,
            requirementHeadNum: formNo // tab 标题显示
          }
          this.readOne(row)
        }
      }
    }
  },
  created () {
    let _this = this
    this.tableHeader = [
      {
        prop: 'orderHeadNum',
        label: this.$t('logisticsMod.orderNum'), // 订单编号
        width: 120,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.readOne(row)
        }.bind(this),
        formattor (val) {
          return val || '--'
        }
      },
      {
        prop: 'orderDate',
        label: this.$t('oneStopShopping.orderDate'), // 订单日期
        width: 150,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'orderStatus',
        label: this.$t('orderMod.buyerOrderSynergy.orderStatus'), // 订单状态
        width: 100,
        dataType: 'dict',
        code: 'LOGISTICS_PRICE_STATUS'
      },
      {
        prop: 'requirementHeadNum',
        label: this.$t('logisticsMod.applyDocumentNum'),
        width: 150
      }, // 申请单据号
      { prop: 'vendorCode', label: this.$t('common.vendorCode'), width: 150 }, // 供应商编码
      { prop: 'vendorName', label: this.$t('common.vendorName'), width: 150 }, // 供应商名称
      {
        prop: 'ceeaApplyUserNickname',
        label: this.$t('bidMod.quotePurchasor'),
        width: 150
      }, // 采购员
      {
        prop: 'ifNeedVendorComfirm',
        label: this.$t('logisticsMod.ifNeedVendorComfirm'), // 是否需供方确认
        width: 150,
        dataType: 'dict',
        code: 'YES_OR_NO'

      },
      {
        prop: 'businessModeCode',
        label: this.$t('logisticsMod.businessMode'), // 业务模式
        width: 100,
        dataType: 'dict',
        code: 'BUSINESS_MODE'

      },
      {
        prop: 'transportModeCode',
        label: this.$t('bid_mod.transportType'), // 运输方式
        width: 100,
        dataType: 'dict',
        code: 'TRANSPORT_MODE'

      },
      {
        prop: 'orderSourceFrom',
        label: this.$t('logisticsMod.orderSourceFrom'), // 来源方式
        width: 100,
        dataType: 'dict',
        code: 'SOURCE_DATA'

      },
      {
        prop: 'serviceProjectName',
        label: this.$t('logisticsMod.serviceProjectName'),
        width: 120
      }, // 服务项目名称
      {
        prop: 'createdUserName', // createdBy
        label: this.$t('common.creator'),
width: 100
      }, // 创建人
      {
        prop: 'creationDate',
        label: this.$t('qualitySynergy.creationDate'), // 创建日期
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'lastUpdatedUserName', // lastUpdatedBy
        label: this.$t('common.updatePeople'),
        width: 100
      }, // 更新人
      {
        prop: 'lastUpdateDate',
        label: this.$t('qualitySynergy.updateDate'), // 更新日期
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'tmsStatus',
        label: this.$t('contractMod.syncStatus'), // 同步状态
        width: 100,
        dataType: 'dict',
        code: 'LOGISTICS_ORDER_TMS_STATUS'

      },
      {
        prop: 'tmsInfo',
        label: this.$t('logisticsMod.syncMessage'),
        width: 120
      }, // 同步消息
      {
        prop: 'operation',
        label: _this.$t('common.operation'),
        width: 150,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            callback: function (row) {
              this.editOne(row)
            }.bind(this),
            formattor (val, row) {
              return _this.$t('common.edit') // 编辑
            },
            show: row => ['DRAFT', 'WAITING_CONFIRM'].includes(row.orderStatus)
          },
          {
            callback: function (row) {
              this.readOne(row)
            }.bind(this),
            formattor (val, row) {
              return _this.$t('common.view') // 查看
            }
          },
          {
            callback: function (row) {
              this.toVoid(row)
            }.bind(this),
            formattor (val, row) {
              return _this.$t('common.cancelled') // 作废
            },
            show: row =>
              row.orderStatus == 'COMPLETED' && row.tmsStatus == 'FAIL_SYNC'
          }
        ]
      }
    ]
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    getQuerydata (v) {
      this.queryParam = Object.assign({}, v)
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    syncFilterParams (values) {
      this.queryParam = values
    },
    handleCurrentChange (val) {
      this.currentRow = val
    },
    checkChange (val) {
      this.currentRows = val
    },
    delMore () {
      if (!this.currentRows.length) {
        this.$message.error(this.$t('logisticsMod.msgSelOneDataDel'))
        return
      }
      if (this.currentRows.some(i => i.orderStatus != 'DRAFT')) {
        this.$message.error(this.$t('logisticsMod.msgDraftDataDel')) // 只有拟定的数据可以删除
        return
      }
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url: '/api-pd/po/order-head/batchDelete',
            method: 'post',
            data: this.currentRows.map(i => i.orderHeadId),
            loading: true
          })
            .then(data => {
              this.$message.success(this.$t('common.successDelete'))
              this.getQuerydata()
            })
            .catch(err => {
              console.log(err)
            })
        })
        .catch(() => {})
    },
    asyncTMS () {
      if (!this.currentRows.length) {
        this.$message({
          message: this.$t('logisticsMod.msgPurchaseApply[18]'), // 请选择要同步的数据
          type: 'error'
        })
        return
      }
      if (this.currentRows.length > 1) {
        this.$message({
          message: this.$t('logisticsMod.msgPurchaseApply[19]'), // 只能选择一条数据同步
          type: 'error'
        })
        return
      }
      if (this.currentRows.some(i => i.orderStatus !== 'COMPLELE')) {
        this.$message({
          message: this.$t('logisticsMod.msgPurchaseApply[20]'), // 只有完成状态的数据能同步
          type: 'error'
        })
        return
      }
      this.$http({
        url: '/api-pd/po/order-head/syncTms',
        method: 'get',
        params: { orderHeadId: this.currentRows[0].orderHeadId },
        loading: true
      })
        .then(data => {
          this.$message({
            type: 'success',
            message: data.message
          })
          this.getQuerydata()
        })
        .catch(err => {
          console.log(err)
        })
    },
    toVoid (row) {
      this.$http({
        url: '/api-pd/po/order-head/batchCancel',
        method: 'post',
        data: [row.orderHeadId],
        loading: true
      })
        .then(data => {
          this.$message({
            type: 'success',
            message: data.message
          })
          this.getQuerydata()
        })
        .catch(err => {
          console.log(err)
        })
    },
    toPrint () {},
    addOne () {
      this.$emit('tab-add', {
        component: logisticsPurchaseOrderDetail,
        params: {
          flag: 'add',
          tableName: 'logisticsPurchaseOrderDetail'
        },
        title: this.$t('logisticsMod.addLogisticsPurchaseOrder'), // 创建物流采购订单
        name: 'logisticsPurchaseOrderDetail'
      })
    },
    editOne (row) {
      this.$emit('tab-add', {
        component: logisticsPurchaseOrderDetail,
        params: {
          flag: 'edit',
          row: row,
          tableName: 'logisticsPurchaseOrderDetail' + row.orderHeadNum
        },
        title: row.orderHeadNum,
        name: 'logisticsPurchaseOrderDetail' + row.orderHeadNum
      })
    },
    readOne (row) {
      this.$emit('tab-add', {
        component: logisticsPurchaseOrderDetail,
        params: {
          flag: 'readOnly',
          row: row,
          tableName: 'logisticsPurchaseOrderDetail' + row.orderHeadNum
        },
        title: row.orderHeadNum,
        name: 'logisticsPurchaseOrderDetail' + row.orderHeadNum
      })
    }
  }
}
</script>
<style scoped lang="scss"></style>
