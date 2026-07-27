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
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <el-button
            type="primary"
            @click="toPrint"
          >
            {{
              $t("common.pdfPrint")
            }}
          </el-button>
        </template>
      </main-header>

      <table-view
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-pd/po/order-head/listPage"
      />
    </el-main>
    <!-- 请输入拒绝原因 -->
    <srm-dialog
      :visible.sync="refuseVisible"
      :title="$t('contractMod.msgRefuseReason')"
      size="middle"
    >
      <el-form
        ref="form"
        :model="refuseForm"
        class="form-incontainer"
        :rules="rules"
      >
        <el-form-item prop="rejectReason">
          <el-input
            v-model="refuseForm.rejectReason"
            type="textarea"
            :rows="2"
            :placeholder="$t('contractMod.msgRefuseReason')"
          />
        </el-form-item>
      </el-form>
      <template
        #footer
        class="dialog-footer"
      >
        <el-button
          type="primary"
          @click="refuseing"
        >
          {{ $t("common.confirm") }}
        </el-button>
        <el-button @click="refuseVisible = false">
          {{ $t("common.cancel") }}
        </el-button>
      </template>
    </srm-dialog>
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
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      refuseVisible: false,
      isModify: false,
      yesNoOptions: [
        { value: 'Y', label: this.$t('common.yes') },
        { value: 'N', label: this.$t('common.no') }
      ],
      globalNickname: null,
      preArr: [
        {
          prop: 'orderHeadNum',
          label: this.$t('logisticsMod.orderNum') // 订单编号
        },
        {
          prop: 'date',
          label: this.$t('oneStopShopping.orderDate'),
          type: 'daterange'
        }, // 订单日期
        {
          prop: 'serviceProjectName',
          label: this.$t('logisticsMod.serviceProjectName'), // 服务项目名称
          type: 'quicksearch',
          showKey: 'projectName',
          name: 'ceea_logistics_project_info'
        },
        {
          prop: 'orderStatus',
          label: this.$t('orderMod.buyerOrderSynergy.orderStatus'),
          type: 'dict',
          code: 'LOGISTICS_PRICE_STATUS'
        } // 订单状态
      ],
      queryParam: {},
      prTypeList: [],
      purchaseTypeList: [],
      refuseForm: {
        rejectReason: null
      },
      rules: {
        rejectReason: [
          { required: true, message: this.$t('contractMod.msgRefuseReason') }
        ] // 请输入拒绝原因
      }
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
      },
      { prop: 'vendorCode', label: this.$t('common.vendorCode'), width: 150 },
      { prop: 'vendorName', label: this.$t('common.vendorName'), width: 150 },
      {
        prop: 'ceeaApplyUserNickname',
        label: this.$t('bidMod.quotePurchasor'),
        width: 150
      },
      {
        prop: 'ifNeedVendorComfirm',
        label: this.$t('logisticsMod.ifNeedVendorComfirm'), // 是否需供方确认
        width: 150,
        formattor (val) {
          return _this.$getDictLabelByValue(_this.yesNoOptions, val)
        }
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
        prop: 'serviceProjectName',
        label: this.$t('logisticsMod.serviceProjectName'),
        width: 120
      },
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
              this.readOne(row)
            }.bind(this),
            formattor (val, row) {
              return _this.$t('common.view') // 查看
            }
          },
          {
            callback: function (row) {
              this.submitVendorConfirm(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('orderMod.accept') // 接受
            },
            show: row => ['WAITING_VENDOR_CONFIRM'].includes(row.orderStatus)
          },
          {
            callback: function (row) {
              this.refuse(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.refused') // 拒绝
            },
            show: row => ['WAITING_VENDOR_CONFIRM'].includes(row.orderStatus)
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
      console.log(v)
      if (v && v.date) {
        v.fromDate = v.date[0]
        v.toDate = v.date[1]
        // delete v.happenDate
      } else if (v && !v.date) {
        delete v.fromDate
        delete v.toDate
      }
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
    toPrint () {},
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
    },
    submitVendorConfirm (row) {
      this.$http({
        url: '/api-pd/po/order-head/submitVendorConfirm',
        method: 'GET',
        params: { orderHeadId: row.orderHeadId },
        loading: true
      })
        .then(data => {
          this.$message.success(this.$t('common.success'))
          this.getQuerydata()
        })
        .catch(err => {
          console.log(err)
        })
    },
    refuse (row) {
      this.refuseVisible = true
      this.orderHeadId = row.orderHeadId
    },
    refuseing () {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.$http({
            url: '/api-pd/po/order-head/refuse',
            method: 'post',
            data: {
              orderHeadId: this.orderHeadId,
              rejectReason: this.refuseForm.rejectReason
            },
            loading: true
          })
            .then(data => {
              this.$message.success(this.$t('common.success'))
              this.refuseVisible = false
              this.getQuerydata()
            })
            .catch(err => {
              console.log(err)
            })
        } else {
          this.$message({
            message: this.$t('vendorMod.pleasefinishRequired'), // '请输入单据必填信息'
            type: 'error'
          })
        }
      })
    }
  }
}
</script>
<style scoped lang="scss"></style>
