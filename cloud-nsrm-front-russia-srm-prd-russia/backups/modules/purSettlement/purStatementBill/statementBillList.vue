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
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <el-button
            v-if="curRole === 'BUYER'"
            type="primary"
            @click="addOne"
          >
            {{ $t("accountMod.createStatement") }}
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
        url="/api-sup-ce/pm/ps/statementHead/listPage"
      />
    </el-main>
  </el-container>
</template>
<script>
  import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
  import TableView from 'lib@/components/Table/TableView'
  import MainHeader from 'lib@/components/Table/MainHeader'
  import FormWrapper from 'lib@/components/Table/FormWrapper'
  import statementBillDetail from './statementBillDetail'
  import statementBillDetailRead from './statementBillDetailRead'
  import { getDictItem } from '@/api/common'
  import { adaptDictData, parseTime } from '@/utils'

  export default {
    name: 'StatementBillList',
    components: {
      TableView, MainHeader, FormWrapper, statementBillDetail, statementBillDetailRead
    },
    mixins: [tabTodoWatch, tabTodoMixin],
    provide () {
    return { context: this }
  },
    data () {
      return {
        curRole: this.$store.getters.userType,
        name: 'statementBillList',
        tableName: 'statementBillList',
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
        isModify: false,
        preArr: [
          { prop: 'vendorName',
            label: () => this.$t('common.vendorName'),
            type: 'quicksearch',
            showKey: 'companyName',
            name: 'scc_sup_company_info_display'
          }, {
            prop: 'organizationId',
            label: () => this.$t('contractMod.fullPathId'),
            type: 'OUorganizationSelector'
          }, { prop: 'statementStatus',
            label: () => this.$t('purSettlementMod.statementStatus')
          }, { prop: 'statementDate',
            label: () => this.$t('purSettlementMod.statementDate'),
            type: 'daterange'
          }, { prop: 'statementNumber',
            label: () => this.$t('purSettlementMod.statementNumber')
          }
        ],
        queryParam: {},
        pubRangeList: []
      }
    },
    created () {
      let _this = this
      this.tableHeader = [
        { prop: 'statementNumber',
          label: _this.$t('purSettlementMod.statementNumber'),
          width: 120,
          showType: 'button',
          btnStyle: 'text',
          callback: function (row) {
            this.readOne(row)
          }.bind(this),
          formattor (val) {
            return val || '--'
          }
        }, { prop: 'vendorName',
          label: _this.$t('purSettlementMod.vendorName'),
          minWidth: 150
        }, { prop: 'vendorCode',
          label: _this.$t('purSettlementMod.vendorCode'),
          width: 120
        }, { prop: 'statementStatus',
          label: _this.$t('purSettlementMod.statementStatus'),
          width: 100,
          formattor (val) {
            return _this.$getDictLabelByValue(_this.statusList, val)
          }
        }, { prop: 'statementStartTime',
          label: _this.$t('purSettlementMod.statementStartTime'),
          width: 120,
          formattor (val) {
            return val ? parseTime(val, '{y}-{m}-{d}') : ''
          }
        }, { prop: 'statementEndTime',
          label: _this.$t('purSettlementMod.statementEndTime'),
          width: 120,
          formattor (val) {
            return val ? parseTime(val, '{y}-{m}-{d}') : ''
          }
        }, { prop: 'statementTotalAmount',
          label: _this.$t('purSettlementMod.statementTotalAmount'),
          width: 160
        }, { prop: 'receiptAmount',
          label: _this.$t('purSettlementMod.receiptAmount'),
          width: 160
        }, { prop: 'returnAmount',
          label: _this.$t('purSettlementMod.returnAmount'),
          width: 160
        }, { prop: 'rejectReason',
          label: _this.$t('purSettlementMod.rejectReason'),
          width: 100,
          show: function (row) {
            if (_this.curRole === 'VENDOR') {
              return true
            } else {
              return false
            }
          }
        }, { prop: 'organizationName',
          label: _this.$t('purSettlementMod.fullPathId'),
          width: 150
        }, { prop: 'operation',
          label: _this.$t('common.operation'),
          width: 100,
          showType: 'buttons',
          fixed: 'right',
          btnStyle: 'text',
          buttons: [
            {
              callback: row => this.editOne(row),
              // show: row => row.statementStatus === "CREATE",
              show: function (row) {
                if ((_this.curRole === 'BUYER') && (row.statementStatus === 'CREATE' || row.statementStatus === 'REJECTED')) {
                  return true
                } else {
                  return false
                }
              },
              formattor: () => _this.$t('common.edit')
            }, {
              callback: row => this.rollbackOne(row),
              // show: row => row.statementStatus === "SUBMITTED",
              show: function (row) {
                if ((_this.curRole === 'BUYER') && (row.statementStatus === 'SUBMITTED')) {
                  return true
                } else {
                  return false
                }
              },
              formattor: () => _this.$t('bidMod.withdraw')
            }, {
              callback: row => this.toApproveOne(row),
              // show: row => row.statementStatus === "SUBMITTED",
              show: function (row) {
                if ((_this.curRole === 'VENDOR') && (row.statementStatus === 'SUBMITTED')) {
                  return true
                } else {
                  return false
                }
              },
              formattor: () => _this.$t('common.approve')
            }, {
              callback: row => this.deleteOne(row),
              show: row => row.statementStatus === 'CREATE',
              formattor: () => _this.$t('common.delete')
            }
          ]
        }
      ]
      this.defaultTableHeader = this.tableHeader
      // 状态
      getDictItem('RECONCILIATION_STATUS').then(res => {
        this.statusList = adaptDictData(res.data, 'dict')
        this.$set(this.preArr, 2, {
          prop: 'statementStatus',
          label: this.$t('purSettlementMod.statementStatus'),
type: 'select',
          options: this.statusList
        })
      })
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
      importOne () {},
      addOne () {
        this.$emit('tab-add', {
          component: statementBillDetail,
          params: {
            flag: 'add'
          },
          title: this.$t('purSettlementMod.newStatement'),
          name: 'statementBillDetail'
        })
      },

      readOne (row) { // 查看
        this.$emit('tab-add', {
          component: statementBillDetailRead,
          params: {
            flag: 'readonly',
            row: row
          },
          title: row.statementNumber,
          name: 'statementBillDetailRead' + row.statementNumber
        })
      },
      editOne (row) { // 编辑
        this.$emit('tab-add', {
          component: statementBillDetail,
          params: {
            flag: 'edit',
            row: row
          },
          title: row.statementNumber,
          name: 'statementBillDetail' + row.statementNumber
        })
      },
      toApproveOne (row) { // 审核
        this.$emit('tab-add', {
          component: statementBillDetail,
          params: {
            flag: 'edit',
            row: row
          },
          title: row.statementNumber,
          name: 'statementBillDetail' + row.statementNumber
        })
      },
      rollbackOne (row) {
        this.$http({
          url: '/api-sup-ce/pm/ps/statementHead/recallStatement',
          method: 'POST',
          data: [row.statementHeadId],
          loading: true
        }).then(data => {
          this.getQuerydata()
        })
        .catch(err => {
          console.log(err)
        })
      },
      deleteOne (row) {
        this.$confirm(this.$t('common.confirmDelete'), {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        })
          .then(() => {
            this.$http({
              url: '/api-sup-ce/pm/ps/statementHead/deleteStatement',
              method: 'POST',
              data: [row.statementHeadId],
              loading: true
            }).then(data => {
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
<style scoped lang="scss">

</style>
