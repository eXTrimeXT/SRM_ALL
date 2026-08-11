<template>
  <el-container
    class="flex-container the_contractTemplateList_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        @getFormData="getQuerydata"
      />
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <el-button
            type="primary"
            @click="addOne"
          >
            {{ $t("common.add") }}
          </el-button>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-sup-ce/ps/paymentPlanHead/listPage"
      />
    </el-main>
  </el-container>
</template>
<script>
  import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
  import TableView from 'lib@/components/Table/TableView'
  import MainHeader from 'lib@/components/Table/MainHeader'
  import FormWrapper from 'lib@/components/Table/FormWrapper'
  import paymentPlanDetail from './paymentPlanDetail'
  import { parseTime } from '@/utils'

  export default {
    name: 'PaymentPlanList',
    components: {
      TableView, MainHeader, FormWrapper, paymentPlanDetail
    },
    mixins: [tabTodoWatch, tabTodoMixin],
    provide () {
    return { context: this }
  },
    data () {
      return {
        name: 'contractTemplateTable',
        tableName: 'paymentPlanList',
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
            prop: 'organizationId',
            label: () => this.$t('purSettlementMod.fullPathId'),
            type: 'OUorganizationSelector'
          },
          {
            prop: 'paymentPlanStatus',
            label: () => this.$t('purSettlementMod.paymentPlanStatus'),
            type: 'dict',
            code: 'PAYMENT_SCHEDULES_STATUS'

          },
          { prop: 'paymentPlanNumber',
            label: () => this.$t('purSettlementMod.paymentPlanNumber')
          },
          { prop: 'creationDate',
            label: () => this.$t('purSettlementMod.creationDate'),
            type: 'date'
          },
          { prop: 'planPaymentDate',
            label: () => this.$t('purSettlementMod.planPaymentDate'),
            type: 'daterange'
          }
        ],
        queryParam: {},
        pubRangeList: []
      }
    },
    created () {
      let _this = this
      this.tableHeader = [
        { prop: 'paymentPlanNumber',
          label: _this.$t('purSettlementMod.paymentPlanNumber'),
          width: 150,
          showType: 'button',
          btnStyle: 'text',
          callback: function (row) {
            this.readOne(row)
          }.bind(this),
          formattor (val) {
            return val || '--'
          }
        }, { prop: 'organizationName',
          label: _this.$t('purSettlementMod.fullPathId'),
          minWidth: 100
        }, { prop: 'creationDate',
          label: _this.$t('purSettlementMod.creationDate'),
        width: 100,
          formattor (val) {
            return val ? parseTime(val, '{y}-{m}-{d}') : ''
          }
        }, { prop: 'planPaymentAmountNoTax',
          label: _this.$t('purSettlementMod.planPaymentAmountNoTax'),
          width: 200
        }, { prop: 'currency',
          label: _this.$t('purSettlementMod.currency'),
          width: 100
        }, { prop: 'planPaymentDate',
          label: _this.$t('purSettlementMod.planPaymentDate'),
          width: 120,
          formattor (val) {
            return val ? parseTime(val, '{y}-{m}-{d}') : ''
          }
        }, { prop: 'paymentPlanStatus',
          label: _this.$t('purSettlementMod.paymentPlanStatus'),
          width: 100,
          dataType: 'dict',
          code: 'PAYMENT_SCHEDULES_STATUS'

        }, { prop: 'operation',
          label: _this.$t('common.operation'),
          width: 180,
          showType: 'buttons',
          fixed: 'right',
          btnStyle: 'text',
          buttons: [
            {
              callback: function (row) {
                this.editOne(row)
              }.bind(this),
              // show: row => row.paymentPlanStatus === "DRAFT",
              formattor (val) {
                return _this.$t('common.edit')
              }
            }, {
              callback: function (row) {
                this.deleteOne(row)
              }.bind(this),
              show: row => row.paymentPlanStatus === 'DRAFT',
              formattor (val) {
                return _this.$t('common.delete')
              }
            }
          ]
        }
      ]
      this.defaultTableHeader = this.tableHeader

      this.$nextTick(() => {
        // this.getQuerydata()
      })
    },
    methods: {
      getQuerydata (v) {
        if (v) {
          if (v.planPaymentDate) {
            v.planPaymentDateStart = v.planPaymentDate[0]
            v.planPaymentDateEnd = v.planPaymentDate[1]
            // delete v.planPaymentDate
          } else {
            delete v.planPaymentDateStart
            delete v.planPaymentDateEnd
            // delete v.planPaymentDate
          }
        }
        this.queryParam = v
        this.$nextTick(() => {
          this.$refs[this.gridId].query()
        })
      },
      handleCurrentChange (val) {
        this.currentRow = val
      },
      addOne () {
        this.$emit('tab-add', {
          component: paymentPlanDetail,
          params: {
            flag: 'add'
          },
          title: this.$t('purSettlementMod.newPaymentPlan'),
          name: 'paymentPlanDetail'
        })
      },
      readOne (row) {
        this.$emit('tab-add', {
          component: paymentPlanDetail,
          params: {
            flag: 'readOnly',
            row: row
          },
          title: row.paymentPlanNumber,
          name: 'paymentPlanDetail' + row.paymentPlanNumber
        })
      },
      editOne (row) {
        this.$emit('tab-add', {
          component: paymentPlanDetail,
          params: {
            flag: 'edit',
            row: row
          },
          title: row.paymentPlanNumber,
          name: 'paymentPlanDetail' + row.paymentPlanNumber
        })
      },
      enableOne () {},
      disableOne () {},
      deleteOne (row) {
        this.$confirm(this.$t('common.confirmDelete'), {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        })
          .then(() => {
            this.$http({
              url: '/api-sup-ce/ps/paymentPlanHead/deletePaymentPlan',
              method: 'POST',
              data: [row.paymentPlanHeadId],
              loading: true
            }).then(data => {
              this.$message({
                type: 'success',
                message: this.$t('common.successDelete')
              })
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
