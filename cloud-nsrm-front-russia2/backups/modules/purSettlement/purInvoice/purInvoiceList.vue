<template>
  <el-container
    class="flex-container the_quotationPrices_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :form-array="preArr"
        :pre-form-obj="preFormObj"
        @getFormData="getQuerydata"
      />
      <main-header
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <el-button
            v-if="curRole==='VENDOR'"
            type="primary"
            @click="editTab('add')"
          >
            {{ $t("common.add") }}
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
        :url="queryUrl"
      />
    </el-main>
  </el-container>
</template>
<script>
  import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
  import TableView from 'lib@/components/Table/TableView'
  import MainHeader from 'lib@/components/Table/MainHeader'
  import FormWrapper from 'lib@/components/Table/FormWrapper'
  import purInvoiceDetail from './purInvoiceDetail'
  import { adaptDictData, parseTime } from '@/utils'
  import {
  getDictItemList
} from '@/api/common'

  export default {
    name: 'PurInvoiceList',
    components: {
      TableView, MainHeader, FormWrapper
    },
    mixins: [tabTodoWatch, tabTodoMixin],
    provide () {
      return { context: this }
    },
    data () {
      return {
        name: '',
        reviewFormNumber: '',
        gridData: [],
        pageSize: 15,
        tableName: 'purInvoiceList',
        gridId: 'purInvoiceList',
        selectList: [],
        currentRow: null,
        showFilterBar: 1,
        tableHeader: [],
        tableData: [],
        invoiceStatus: [],
        currencyList: [],
        preArr: [
          { prop: 'invoiceNoticeNumber',
            label: () => this.$t('purSettlementMod.invoiceNoticeNumber')
          },
          { prop: 'orgId',
            label: () => this.$t('purSettlementMod.fullPathId'),
            type: 'OUorganizationSelector'
          }, { prop: 'invoiceNoticeStatus',
            label: () => this.$t('purSettlementMod.paymentPlanStatus'),
            type: 'select',
            options: []
          }, { prop: 'creationDate',
            label: () => this.$t('common.creationTime'),
            type: 'date'
          }
        ],
        queryParam: {},
        curRole: this.$store.getters.userType, // VENDOR BUYER curRole==='VENDOR'
        rolePermissions: '', // 操作角色 Buyer 采购员\ AccountSpecialist 财务专员
        userInfo: this.$store.getters.userInfo,
        queryUrl: '',
        preFormObj: {}
      }
    },
    watch: {
      $route: {
        // 当前功能已经打开的时候监听是否是从其他功能跳转过来的
        deep: true,
        immediate: true,
        handler () {
          if (
            this.$route.params.from === 'workCount' &&
            this.$route.params.funName === 'purInvoice'
          ) { // 供应商 工作台跳转
            this.queryParam.invoiceNoticeStatus = this.$route.params.invoiceNoticeStatus
            this.preFormObj = Object.assign({}, { invoiceNoticeStatus: this.$route.params.invoiceNoticeStatus })
          } else if (
            this.$route.params.from === 'fromFun' &&
            this.$route.params.funName === 'purInvoice'
          ) { // 采购商 工作台跳转
            let invoiceNoticeId = Number(this.$route.params.formId)
            let formNo = this.$route.params.formNo // 流程标题
            let row = {
              ...this.$route.params,
              invoiceNoticeId,
              invoiceNoticeNumber: formNo // tab 标题显示
            }
            this.editTab('view', row)
          }
        }
      }
    },
    created () {
      this.rolePermissions = this.userInfo.rolePermissions[0].roleCode // 通过这个角色的code去判断如果在角色设置里面修改的话，程序要对应修改
      if (this.curRole === 'VENDOR') {
        this.queryUrl = '/api-sup-ce/invoice/invoiceNotice/vendorListPageByParm'
      } else {
        this.queryUrl = '/api-sup-ce/ps/invoice/invoiceNotice/buyerListPageByParm'
      }
      this.fatchDictData() // 字典
      let _this = this
      this.tableHeader = [
        { prop: 'invoiceNoticeNumber',
          label: _this.$t('purSettlementMod.invoiceNoticeNumber'),
          width: 120,
          showType: 'button',
          btnStyle: 'text',
          callback: function (row) {
            this.currentRow = row
            this.editTab('view', row)
          }.bind(this),
          formattor (val) {
            return val || '--'
          }
        }, { prop: 'orgName',
          label: _this.$t('purSettlementMod.fullPathId'),
          width: 200
        }, { prop: 'invoiceNoticeStatus',
          label: _this.$t('purSettlementMod.paymentPlanStatus'),
          width: 100,
          formattor (val) {
            return _this.$getDictLabelByValue(_this.invoiceStatus, val)
          }
        }, { prop: 'statementTotalAmount',
          label: _this.$t('purSettlementMod.statementTotalAmount'),
          width: 150,
          align: 'right'
        }, { prop: 'invoiceTotalAmount',
          label: _this.$t('purSettlementMod.invoiceTotalAmount'),
          width: 160,
          align: 'right'
        }, { prop: 'creationDate',
          label: _this.$t('common.creationTime'),
          width: 100
        }, { prop: 'rejectReason',
          label: _this.$t('contractMod.rejectReason'),
          minWidth: '120'
        },
        {
          prop: 'operation',
          label: _this.$t('common.operation'),
          width: 160,
          showType: 'buttons',
          fixed: 'right',
          btnStyle: 'text',
          buttons: [
            {
              callback: function (row) {
                this.editTab('edit', row)
              }.bind(this),
              formattor (val) {
                return _this.$t('common.edit')
              },
              show: function (row) {
                if (row.invoiceNoticeStatus === 'DRAFT' || row.invoiceNoticeStatus === 'REJECTED' && _this.curRole === 'VENDOR') { // 拟定和驳回状态可编辑
                  return true
                } else {
                  return false
                }
              }
            },
            {
              callback: function (row) {
                this.delRowData(row)
              }.bind(this),
              formattor (val) {
                return _this.$t('common.delete')
              },
              show: function (row) {
                if ((row.invoiceNoticeStatus === 'DRAFT' || row.invoiceNoticeStatus === 'REJECTED' || row.invoiceNoticeStatus === 'ABANDONED') && _this.curRole === 'VENDOR') { // 拟定状态可删除
                  return true
                } else {
                  return false
                }
              }
            },
            {
              callback: function (row) {
                this.editTab('approve', row)
              }.bind(this),
              formattor (val) {
                return _this.$t('common.approve')
              },
              show: function (row) {
                // this.rolePermissions==='Buyer'
                if (((row.invoiceNoticeStatus === 'SUBMITTED' && _this.rolePermissions === 'Buyer') || (row.invoiceNoticeStatus === 'FIRST_REVIEW_APPROVED' && _this.rolePermissions === 'AccountSpecialist')) && _this.curRole === 'BUYER') { // 提交状态可审批
                  return true
                } else {
                  return false
                }
              }
            },
            {
              callback: function (row) {
                this.abandonHandel(row)
              }.bind(this),
              formattor (val) {
                return _this.$t('common.cancelled')
              },
              show: function (row) {
                if (row.invoiceNoticeStatus !== 'FIRST_REVIEW_APPROVED' && row.invoiceNoticeStatus !== 'FINAL_REVIEW_APPROVED' && row.invoiceNoticeStatus !== 'ABANDONED' && _this.curRole === 'VENDOR') { // 除审批通过的其他状态可废弃
                  return true
                } else {
                  return false
                }
              }
            },
            {
              callback: function (row) {
                this.editTab('view', row)
              }.bind(this),
              formattor (val) {
                return _this.$t('common.view')
              },
              show: function (row) {
                return true
              }
            }
          ]
        }
      ]

      this.$nextTick(() => {
        this.getQuerydata()
      })
    },
    methods: {
      fatchDictData () {
        // 批量查询字典
        let dictParamsArr = [
          { dictCode: 'INVOICE_NOTICE_STATUS' } // 审批状态
        ]
        getDictItemList(dictParamsArr).then(res => {
          const [INVOICE_NOTICE_STATUS] = res.data
          this.invoiceStatus = adaptDictData(
            INVOICE_NOTICE_STATUS.INVOICE_NOTICE_STATUS,
            'dict'
          )
          this.preArr[2].options = this.invoiceStatus
        })
      },
      getQuerydata (v) {
        let query = v || this.preFormObj
        this.queryParam = query
        this.$nextTick(() => {
          this.$refs[this.gridId].query()
        })
      },
      handleCurrentChange (val) {
        this.currentRow = val
      },
      // 废弃
      abandonHandel (row) {
        let invoiceNoticeId = row.invoiceNoticeId
        this.$api.pur.vendorabandon({ invoiceNoticeId }).then(res => {
          this.$message({
            message: res.message,
            type: 'success'
          })
          this.getQuerydata()
        })
      },
      // 删除
      delRowData (row) {
        let invoiceNoticeId = row.invoiceNoticeId
        this.$api.pur.invoiceNoticeDel({ invoiceNoticeId }).then(res => {
          this.$message({
            message: res.message,
            type: 'success'
          })
          this.getQuerydata()
        })
      },
      editTab (type, row) {
        let tab = {}
        if (type === 'add') {
          // 新增
          tab = {
            component: purInvoiceDetail,
            params: {
              flag: 'add',
              tabName: 'purInvoiceDetail'
            },
            title: this.$t('purSettlementMod.newInvoiceNotice'),
            name: 'purInvoiceDetail'
          }
        } else {
          // 修改
          tab = {
            component: purInvoiceDetail,
            params: {
              flag: type,
              orderId: row.invoiceNoticeId,
              tabName: 'purInvoiceDetail' + row.invoiceNoticeNumber
            },
            title: row.invoiceNoticeNumber,
            name: 'purInvoiceDetail' + row.invoiceNoticeNumber
          }
        }
        this.$emit('tab-add', tab)
      }
    }
  }
</script>
<style scoped lang="scss">

</style>
