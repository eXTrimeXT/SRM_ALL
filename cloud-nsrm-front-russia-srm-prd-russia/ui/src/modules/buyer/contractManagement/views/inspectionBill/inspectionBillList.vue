<template>
  <el-container
    class="flex-container the_contractTemplateList_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :form-array="preArr"
        @getFormData="getQuerydata"
      />

      <main-header
        v-if="curRole === 'BUYER'"
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            type="primary"
            code="cm:inspectionBill:add"
            @click="addOne"
          >
            {{ $t("common.add") }}
          </AuthorityButton>
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
        url="/api-cm/accept/acceptOrder/listPageByParm"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import inspectionBillDetail from './inspectionBillDetail'
import { parseTime } from '@/utils'

export default {
  name: 'InspectionBillList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    inspectionBillDetail
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      curRole: this.$store.getters.userType,
      name: 'contractTemplateTable',
      tableName: 'inspectionBillList',
      reviewFormNumber: '',
      gridData: [],
      pageSize: 15,
      gridId: 'list',
      selectList: [],
      currentRow: null,
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      isModify: false,
      preArr: [
        {
          prop: 'acceptNumber',
          label: this.$t('contractMod.acceptNumber') // 验收单号
        },
        {
          prop: 'ceeaAssetType',
          label: this.$t('contractMod.acceptType'), // 验收类型
          type: 'dict',
          code: 'AQL_ASSET_TYPE'
        },
        {
          prop: 'acceptStatus',
          label: this.$t('contractMod.contractStatus'), // 状态
          type: 'dict',
          code: 'ACCEPTANCE_STATUS'
        },
        {
          prop: 'orgIdList',
          label: this.$t('contractMod.buId'), // 业务实体
          type: 'OUorganizationSelector',
          multiple: true
        },
        {
          prop: 'acceptDate',
          label: this.$t('contractMod.acceptDate1'), // 验收日期
          type: 'daterange'
        },
        {
          prop: 'vendorName',
          label: this.$t('contractMod.vendorName'), // 供应商名称
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info_display_buyer',
          disabled: this.$store.getters.userType === 'VENDOR'
        },
        {
          prop: 'orderNumber',
          label: this.$t('purSettlementMod.orderNumber') // 采购订单号
        }
      ],
      queryParam: {},
      globalNickname: null
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
          this.$route.params.funName === 'inspectionBill'
        ) {
          let acceptOrderId = Number(this.$route.params.formId)
          let formNo = this.$route.params.formNo // 流程标题
          let row = {
            ...this.$route.params,
            acceptOrderId,
            acceptNumber: formNo // tab 标题显示
          }
          this.readOne(row)
        }
      }
    }
  },
  created () {
    let _this = this
    this.globalNickname = this.$store.getters.userInfo
      ? this.$store.getters.userInfo.username
      : null
    console.log('globalNickname', this.globalNickname)
    console.log('this.$store.getters.userInfo', this.$store.getters.userInfo)
    this.tableHeader = [
      {
        prop: 'acceptNumber',
        label: _this.$t('contractMod.acceptNumber'),
        width: 120,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.readOne(row)
        }.bind(this),
        formattor (val) {
          return val || '-'
        }
      },
      {
        prop: 'ceeaAssetType',
        label: _this.$t('oneStopShopping.assetClass'), // 资产类别
        width: 100,
                dataType: 'dict',
code: 'AQL_ASSET_TYPE'
      },
      {
        prop: 'acceptDate',
        label: _this.$t('contractMod.acceptDate1'), // 验收日期
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'acceptStatus',
        label: _this.$t('contractMod.acceptStatus'),
        width: 100,
        dataType: 'dict',
code: 'ACCEPTANCE_STATUS'
      },
      {
        prop: 'ceeaOrgName',
        label: _this.$t('contractMod.buId'), // 业务实体
        width: 120
      },
      {
        prop: 'vendorCode',
        label: _this.$t('contractMod.vendorCode'),
        width: 120
      },
      {
        prop: 'vendorName',
        label: _this.$t('contractMod.vendorName'),
        minWidth: 150
      },
      {
        prop: 'ceeaApplictionCode',
        label: _this.$t('contractMod.applicationOrderNum'), // 申请单号
        width: 150
      },
      {
        prop: 'ceeaMoneyType',
        label: _this.$t('contractMod.currencyCode'),
        width: 100
      },
      { prop: 'remark', label: _this.$t('contractMod.remark'), minWidth: 150 },
      {
        prop: 'createdUserName', // createdBy
        label: _this.$t('contractMod.createdBy'),
        width: 100
      },
      {
        prop: 'creationDate',
        label: _this.$t('contractMod.creationDate'),
        width: 150,
        formattor (val) {
          return val ? parseTime(val) : ''
        }
      },
      {
        prop: 'operation',
        label: _this.$t('common.operation'),
        width: 180,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            callback: function (row) {
              this.approvalOne(row)
            }.bind(this),
            code: 'cm:inspectionBill:approval',
            formattor (val) {
              return _this.$t('common.approve') // 审批
            },
            show: function (row) {
              if (
                _this.curRole === 'BUYER' &&
                row.acceptStatus === 'UNDER_REVIEW' &&
                row.createdBy === _this.globalNickname
              ) {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              this.editOne(row)
            }.bind(this),
            code: 'cm:inspectionBill:edit',
            formattor (val) {
              return _this.$t('common.edit') // 编辑
            },
            show: row =>
              _this.curRole === 'BUYER' &&
              ['DRAFT', 'WITHDRAW', 'REJECTED'].includes(row.acceptStatus) &&
              row.createdBy === _this.globalNickname
          },
          {
            callback: function (row) {
              this.withdrawOne(row)
            }.bind(this),
            code: 'cm:inspectionBill:withdraw',
            formattor (val) {
              return _this.$t('bidMod.withdraw') // 撤回
            },
            show: function (row) {
              if (
                _this.curRole === 'BUYER' &&
                row.acceptStatus === 'UNDER_REVIEW' &&
                row.createdBy === _this.globalNickname
              ) {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              this.deleteOne(row)
            }.bind(this),
            code: 'cm:inspectionBill:delete',
            formattor (val) {
              return _this.$t('common.delete') // 删除
            },
            show: row =>
              _this.curRole === 'BUYER' &&
              ['DRAFT'].includes(row.acceptStatus) &&
              row.createdBy === _this.globalNickname
          },
          {
            callback: function (row) {
              this.abandonOne(row)
            }.bind(this),
            code: 'cm:inspectionBill:abandon',
            formattor (val) {
              return _this.$t('common.abandon') // 废弃
            },
            show: row =>
              (row.acceptStatus === 'WITHDRAW' ||
                row.acceptStatus === 'REJECTED') &&
              row.createdBy === _this.globalNickname
          }
          // change by liwenhong  增加废弃按钮
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
      const { acceptDate, ...rest } = v || {}
      let params = { ...rest }
      if (acceptDate) {
        const [startAcceptDate, endAcceptDate] = acceptDate
        params = { ...rest, startAcceptDate, endAcceptDate }
      }
      this.queryParam = params
      delete this.queryParam.acceptDate
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    abandonOne (row) {
      this.$http({
        url: '/api-cm/accept/acceptOrder/abandon',
        method: 'GET',
        params: { acceptOrderId: row.acceptOrderId },
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
    handleCurrentChange (val) {
      this.currentRow = val
    },
    addOne () {
      this.$emit('tab-add', {
        component: inspectionBillDetail,
        params: {
          flag: 'add'
        },
        title: this.$t('contractMod.addAcceptanceSheet'), // 创建验收单
        name: 'inspectionBillDetail'
      })
    },
    editOne (row) {
      this.$emit('tab-add', {
        component: inspectionBillDetail,
        params: {
          flag: 'edit',
          row: row
        },
        title: row.acceptNumber,
        name: 'inspectionBillDetail' + row.acceptNumber
      })
    },
    approvalOne (row) {
      this.$emit('tab-add', {
        component: inspectionBillDetail,
        params: {
          flag: 'approvalOnly',
          row: row
        },
        title: row.acceptNumber,
        name: 'inspectionBillDetail' + row.acceptNumber
      })
    },
    readOne (row) {
      this.$emit('tab-add', {
        component: inspectionBillDetail,
        params: {
          flag: 'readOnly',
          row: row
        },
        title: row.acceptNumber,
        name: 'inspectionBillDetail' + row.acceptNumber
      })
    },
    enableOne () {},
    disableOne () {},
    withdrawOne (row) {
      // 确认撤回这条数据？
      this.$confirm(this.$t('contractMod.msgWithdrawData'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url: '/api-cm/accept/acceptOrder/buyerWithdraw',
            method: 'GET',
            params: { acceptOrderId: row.acceptOrderId },
            loading: true
          })
            .then(data => {
              this.$message.success(this.$t('common.successWithdraw'))
              this.getQuerydata()
            })
            .catch(err => {
              console.log(err)
            })
        })
        .catch(() => {})
    },
    deleteOne (row) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url: '/api-cm/accept/acceptOrder/deleteAcceptDTO',
            method: 'GET',
            params: { acceptOrderId: row.acceptOrderId },
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
    }
  }
}
</script>
<style scoped lang="scss"></style>
