<template>
  <el-container
    class="flex-container the_contractTemplateList_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        :pre-form-obj="preFormObj"
        @getFormData="getQuerydata"
      />
      <MainHeader>
        <AuthorityButton
          type="primary"
          code="cm.supInspectionBill.add"
          @click="addOne"
        >
          {{ $t('contractMod.CreateAcceptanceApplication') }}
        </AuthorityButton>
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
import supInspectionBillDetail from './supInspectionBillDetail'
import { parseTime } from '@/utils'

export default {
  name: 'SupInspectionBillList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    supInspectionBillDetail
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      preFormObj: {},
      curRole: this.$store.getters.userType,
      name: 'contractTemplateTable',
      tableName: 'supInspectionBillList',
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
          prop: 'ceeaAcceptApplicationNum',
          label: this.$t('contractMod.acceptApplicationNum') // 验收申请单号
        },
        {
          prop: 'startAcceptDate',
          label: this.$t('contractMod.startAcceptDate'), // 申请日期从
          type: 'date'
        },
        {
          prop: 'endAcceptDate',
          label: this.$t('contractMod.endAcceptDate'), // 申请日期至
          type: 'date'
        },
        {
          prop: 'orgIdList',
          label: this.$t('contractMod.buId'), // 业务实体
          type: 'OUorganizationSelector',
          multiple: true
        },
        {
          prop: 'acceptStatus',
          label: this.$t('contractMod.applicationStatus'), // 申请状态
          type: 'dict',
          code: 'YSSQ_STATUS'
        }
      ],
      queryParam: {},
      acceptTypeList: []
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
          this.$route.params.funName === 'supInspectionBillList'
        ) {
          // 供应商 工作台跳转
          this.queryParam.acceptStatus = this.$route.params.acceptStatus
          // this.firstLoad = false;
          this.preFormObj = Object.assign({}, { acceptStatus: this.$route.params.acceptStatus })
        }
      }
    }
  },
  created () {
    let _this = this
    this.tableHeader = [
      {
        prop: 'ceeaAcceptApplicationNum',
        label: this.$t('contractMod.acceptApplicationNum'), // 验收申请单号
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
        label: this.$t('purchaseDemand.applyDate'), /// 申请日期
        width: 100,
        formattor: (val) => (val ? parseTime(val, '{y}-{m}-{d}') : '')
      },
      {
        prop: 'acceptStatus',
        label: this.$t('contractMod.applicationStatus'), // 申请状态
        width: 100,
        dataType: 'dict',
        code: 'YSSQ_STATUS'
      },
      {
        prop: 'ceeaOrgName',
        label: this.$t('contractMod.buId'), // 业务实体
        minWidth: 150
      },
      {
        prop: 'createdUserName', // createdBy
        label: _this.$t('contractMod.createdBy'),
        width: 100
      },
      {
        prop: 'creationDate',
        label: _this.$t('contractMod.creationDate'),
        width: 100,
        formattor: (val) => (val ? parseTime(val, '{y}-{m}-{d}') : '')
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
              this.editOne(row)
            }.bind(this),
            code: 'cm.supInspectionBill.edit',
            formattor (val) {
              return _this.$t('common.edit')
            },
            show: (row) => row.acceptStatus === 'APPLY_DRAFT'
          },
          {
            callback: function (row) {
              this.submitOne(row)
            }.bind(this),
            code: 'cm.supInspectionBill.submit',
            formattor (val) {
              return _this.$t('common.submit')
            },
            show: (row) => row.acceptStatus === 'APPLY_DRAFT'
          },
          {
            callback: function (row) {
              this.withdrawOne(row)
            }.bind(this),
            code: 'cm.supInspectionBill.withdraw',
            formattor (val) {
              return _this.$t('bidMod.withdraw')
            },
            show: (row) => row.acceptStatus === 'UNDER_REVIEW'
          },
          {
            callback: function (row) {
              this.deleteOne(row)
            }.bind(this),
            code: 'cm.supInspectionBill.delete',
            formattor (val) {
              return _this.$t('common.delete')
            },
            show: (row) => row.acceptStatus === 'APPLY_DRAFT'
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
      let query = v || this.preFormObj
      this.queryParam = Object.assign({ roofScheme: 'string' }, query)
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      this.currentRow = val
    },
    addOne () {
      this.$emit('tab-add', {
        component: supInspectionBillDetail,
        params: {
          flag: 'add'
        },
        title: this.$t('contractMod.acceptApplicationDetail'), // 验收申请详情
        name: 'supInspectionBillDetail'
      })
    },
    editOne (row) {
      this.$emit('tab-add', {
        component: supInspectionBillDetail,
        params: {
          flag: 'edit',
          row: row
        },
        title: row.ceeaAcceptApplicationNum,
        name: 'supInspectionBillDetail' + row.ceeaAcceptApplicationNum
      })
    },
    approvalOne (row) {
      this.$emit('tab-add', {
        component: supInspectionBillDetail,
        params: {
          flag: 'approvalOnly',
          row: row
        },
        title: row.ceeaAcceptApplicationNum,
        name: 'supInspectionBillDetail' + row.ceeaAcceptApplicationNum
      })
    },
    readOne (row) {
      this.$emit('tab-add', {
        component: supInspectionBillDetail,
        params: {
          flag: 'readOnly',
          row: row
        },
        title: row.ceeaAcceptApplicationNum,
        name: 'supInspectionBillDetail' + row.ceeaAcceptApplicationNum
      })
    },
    enableOne () {},
    disableOne () {},
    submitOne (row) {
      this.$http({
        url: '/api-cm/accept/acceptOrder/vendorSubmit',
        method: 'GET',
        params: { id: row.acceptOrderId },
        loading: true
      })
        .then((data) => {
          this.$message.success(this.$t('common.success'))
          this.getQuerydata()
        })
        .catch((err) => {
          console.log(err)
        })
    },
    withdrawOne (row) {
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
            .then((data) => {
              this.$message.success(this.$t('common.successWithdraw'))
              this.getQuerydata()
            })
            .catch((err) => {
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
            .then((data) => {
              this.$message.success(this.$t('common.successDelete'))
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
