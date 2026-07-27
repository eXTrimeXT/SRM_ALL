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
import inspectionApplyBillDetail from './inspectionApplyBillDetail'
import { parseTime } from '@/utils'

export default {
  name: 'InspectionApplyBillList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    inspectionApplyBillDetail
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      curRole: this.$store.getters.userType,
      name: 'contractTemplateTable',
      tableName: 'inspectionApplyBillList',
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
        { prop: 'ceeaAcceptApplicationNum', label: '验收申请单号' },
        { prop: 'startOverDate', label: '申请日期从', type: 'date' },
        { prop: 'endOverDate', label: '申请日期至', type: 'date' },
        {
          prop: 'orgIds',
          label: '业务实体',
          type: 'OUorganizationSelector',
          multiple: true
        },
        { prop: 'vendorName', label: '供应商名称' },
        { prop: 'orderNumber', label: '采购订单号' }
      ],
      queryParam: {},
      acceptTypeList: []
    }
  },
  created () {
    let _this = this
    this.tableHeader = [
      {
        prop: 'ceeaAcceptApplicationNum',
        label: '验收申请单号',
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
        prop: 'ceeaApplicationDate',
        label: '申请日期',
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'acceptStatus',
        label: '申请状态',
        width: 100,
        dataType: 'dict',
        code: 'YSSQ_STATUS'
      },
      { prop: 'ceeaOrgName', label: '业务实体', width: 120 },
      { prop: 'vendorCode', label: _this.$t('contractMod.vendorCode'), width: 120 },
      { prop: 'vendorName', label: _this.$t('contractMod.vendorName'), minWidth: 150 },
      { prop: 'orderNumber', label: '采购订单号', width: 150 },
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
        width: 100,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            callback: function (row) {
              this.confirmOne(row)
            }.bind(this),
            formattor (val) {
              return '确认'
            },
            show: (row) => row.acceptStatus === 'SUBMIT'
          },
          {
            callback: function (row) {
              this.rejectOne(row)
            }.bind(this),
            formattor (val) {
              return '驳回'
            },
            show: (row) => row.acceptStatus === 'SUBMIT'
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
      this.queryParam = v || {}
      this.queryParam.acceptStatus = 'SUBMIT'
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      this.currentRow = val
    },
    addOne () {
      this.$emit('tab-add', {
        component: inspectionApplyBillDetail,
        params: {
          flag: 'add'
        },
        title: '验收申请详情',
        name: 'inspectionApplyBillDetail'
      })
    },
    editOne (row) {
      this.$emit('tab-add', {
        component: inspectionApplyBillDetail,
        params: {
          flag: 'edit',
          row: row
        },
        title: row.ceeaAcceptApplicationNum,
        name: 'inspectionApplyBillDetail' + row.ceeaAcceptApplicationNum
      })
    },
    approvalOne (row) {
      this.$emit('tab-add', {
        component: inspectionApplyBillDetail,
        params: {
          flag: 'approvalOnly',
          row: row
        },
        title: row.ceeaAcceptApplicationNum,
        name: 'inspectionApplyBillDetail' + row.ceeaAcceptApplicationNum
      })
    },
    readOne (row) {
      this.$emit('tab-add', {
        component: inspectionApplyBillDetail,
        params: {
          flag: 'readOnly',
          row: row
        },
        title: row.ceeaAcceptApplicationNum,
        name: 'inspectionApplyBillDetail' + row.ceeaAcceptApplicationNum
      })
    },
    enableOne () {},
    disableOne () {},
    confirmOne (row) {
      this.$http({
        url: '/api-cm/accept/acceptOrder/vendorPass',
        method: 'GET',
        params: { id: row.acceptOrderId },
        loading: true
      })
        .then((data) => {
          this.$message.success('操作成功!')
          this.getQuerydata()
        })
        .catch((err) => {
          console.log(err)
        })
    },
    rejectOne (row) {
      this.$prompt('驳回原因', '驳回原因', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      })
        .then(({ value }) => {
          this.$http({
            url: '/api-cm/accept/acceptOrder/vendorReject',
            method: 'POST',
            data: Object.assign(row, { rejectReason: value }),
            loading: true
          })
            .then((data) => {
              this.$message.success('驳回成功！')
              this.getQuerydata()
            })
            .catch((err) => {
              console.log(err)
            })
        })
        .catch(() => {
          this.$message.info('取消撤回！')
        })
    },
    deleteOne (row) {
      this.$confirm('当前操将永久删除这条数据，确认删除这条数据？', {
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
            .then((data) => {
              this.$message.success('删除成功')
              this.getQuerydata()
            })
            .catch((err) => {
              console.log(err)
            })
        })
        .catch(() => {})
    }
  }
}
</script>
<style scoped lang="scss"></style>
