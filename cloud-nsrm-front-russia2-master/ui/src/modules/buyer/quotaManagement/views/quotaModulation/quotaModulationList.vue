<template>
  <el-container
    class="flex-container the_inquiryApprovalFlow_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        @getFormData="getQueryData"
      />
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            code="inq:quotaModulation:add"
            type="primary"
            @click="createProjectDetail"
          >
            {{ $t('common.add') }}
          </AuthorityButton>
        </template>
      </MainHeader>

      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :page-size="15"
        :pre-query-data="queryParam"
        :comActive="$attrs['changeTab']"
        url="/api-inq/inquiry/quotaAdjust/quotaAdjustListPage"
        :open-custom-table="true"
      />
    </el-main>
  </el-container>
</template>

<script>
import { parseTime } from '@/utils'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import quotaModulationDetail from './quotaModulationDetail'

export default {
  name: 'QuotaModulationList',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      gridId: 'list',
      tableHeader: [],
      tableData: [],
      queryParam: {},
      preArr: [
        {
          prop: 'orgIds',
          label: () => this.$t('dataConfMod.organizationId'), // 库存组织
          type: 'INVorganizationSelector',
          multiple: true
        },
        { prop: 'quotaName', label: () => this.$t('quota.subcategory') }, // 小类
        { prop: 'createdBy', label: () => this.$t('quota.createdBy') }, // 创建人
        {
          prop: 'vendorName',
          label: () => this.$t('quota.vendorName'), // 供应商名称
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info_display_buyer'
        },
        {
          prop: 'itemCode',
          label: () => this.$t('bidMod.itemCode'),
          type: 'quicksearch',
          showKey: 'materialCode',
          name: 'scc_base_material_item'
        },

        { prop: 'status',
          label: () => this.$t('quota.status'),
          type: 'dict', // 数据类型为字典
          code: 'ADJUST_STATUS' // 字典code
        } // 状态
      ]
    }
  },
  provide () {
    return { context: this }
  },
  watch: {
    $route: {
      // 当前功能已经打开的时候监听是否是从其他功能跳转过来的
      deep: true,
      immediate: true,
      handler () {
        if (
          this.$route.params.from === 'fromFun' &&
          this.$route.params.funName === 'quotaModulation'
        ) {
          let quotaAdjustId = Number(this.$route.params.formId)
          let formNo = this.$route.params.formNo // 流程标题
          let row = {
            ...this.$route.params,
            quotaAdjustId,
            quotaAdjustCode: formNo // tab 标题显示
          }
          this.readApproval(row)
        }
      }
    }
  },
  created () {
    this.tableHeader = [
      {
        prop: 'quotaAdjustCode',
        label: this.$t('quota.orderNumber'), // 单据编号
        width: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.readApproval(row)
      },
      {
        prop: 'quotaAdjustName',
        label: this.$t('quota.orderTitle'), // 单据标题
        minWidth: 150
      },
      {
        prop: 'ceeaSourceNo',
        label: this.$t('quota.ceeaSourceNo'), // 寻源单号
        width: 150
      },
      {
        prop: 'sourceType',
        label: this.$t('quota.sourceType'), // 寻源方式
        width: 150
      },
      {
        prop: 'status',
        label: this.$t('common.approvalStatus'), // 审批状态
        width: 100,
        dataType: 'dict', // 数据类型为字典
        code: 'ADJUST_STATUS' // 字典code
      },
      {
        prop: 'createdUserName', // createdBy
        label: this.$t('bidMod.bidingCreatedBy'),
        width: 150
      },
      {
        prop: 'creationDate',
        label: this.$t('bidMod.creationDate'),
        width: 150,
        formattor: val => val ? parseTime(val) : ''
      },
      {
        prop: 'operation',
        label: this.$t('bidMod.operation'),
        width: 200,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          // 编辑
          {
            show: row => row.status === 'DRAFT',
            callback: row => this.editOneItem(row),
            formattor: () => this.$t('common.edit')
          },
          // 审批提交
          {
            show: (row) => row.status === 'DRAFT',
            callback: row => this.doApproval(row),
            formattor: () => this.$t('quota.approveSubmit')
          },
          // 审批通过
          {
            show: row => row.status === 'SUBMITTED',
            callback: row => this.doApprovalPass(row),
            formattor: () => this.$t('quota.approvalPass'),
            code: 'inq:quotaModulation:doApproval'
          },
          // 删除
          {
            callback: row => this.deleteRow(row),
            formattor: () => this.$t('quota.approvalPass')
          }
        ]
      }
    ]
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQueryData()
    })
  },
  mounted () {},
  methods: {
    /* 审批提交 */
    deleteRow (row) {
      this.$http({
        url: '/api-inq/inquiry/quotaAdjust/delete',
        method: 'GET',
        params: { id: row.quotaAdjustId }
      }).then((res) => {
        this.$message({ type: 'success', message: res.message })
        this.getQueryData()
      })
    },

    /* 审批提交 */
    doApproval (row) {
      this.$http({
        url: '/api-inq/inquiry/quotaAdjust/submitQuotaAdjust',
        method: 'GET',
        params: { id: row.quotaAdjustId }
      }).then((res) => {
        this.$message({ type: 'success', message: res.message })
        this.getQueryData()
      })
    },

    /* 审批通过 */
    doApprovalPass (row) {
      this.$http({
        url: '/api-inq/inquiry/quotaAdjust/getApprove',
        method: 'GET',
        params: { id: row.quotaAdjustId }
      }).then((res) => {
        this.$message({ type: 'success', message: res.message })
        this.getQueryData()
      })
    },

    /* 查询数据 */
    getQueryData (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },

    /* 新增 */
    createProjectDetail () {
      this.$emit('tab-add', {
        component: quotaModulationDetail,
        params: {
          flag: 'add'
        },
        title: this.$t('quota.newQuotaModulation'), // 新建配额调整单
        name: 'quotaModulationDetail'
      })
    },

    /* 只读 */
    readApproval (row) {
      this.$emit('tab-add', {
        component: quotaModulationDetail,
        params: {
          flag: 'isReadOnly',
          isReadonly: true,
          row: row
        },
        title: row.quotaAdjustCode,
        name: 'quotaModulationDetail' + row.quotaAdjustCode
      })
    },

    /* 编辑 */
    editOneItem (row) {
      this.$emit('tab-add', {
        component: quotaModulationDetail,
        params: {
          flag: 'edit',
          isEdit: true,
          row: row
        },
        title: row.quotaAdjustCode,
        name: 'quotaModulationDetail' + row.quotaAdjustCode
      })
    }
  }
}
</script>
<style scoped lang="scss">
.el-dialog__body {
  padding-top: 0 !important;
}
</style>
