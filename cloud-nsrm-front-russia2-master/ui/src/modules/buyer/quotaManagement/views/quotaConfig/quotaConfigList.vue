<template>
  <el-container
    class="flex-container the_inquiryApprovalFlow_wrapper"
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
          <AuthorityButton
            code="inq:quotaConfig:add"
            type="primary"
            @click="createProjectDetail"
          >
            {{
              $t('common.add')
            }}
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :comActive="$attrs['changeTab']"
        url="/api-inq/inquiry/quota/quotaListPage"
        :open-custom-table="true"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import quotaConfigDetail from './quotaConfigDetail'
import { parseTime } from '@/utils'

export default {
  name: 'QuotaConfigList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    quotaConfigDetail
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      approvalFiles: [],
      show_tab2: false,
      pageSize: 15,
      gridId: 'list',
      currentRow: null,
      tableName: 'inquiryApprovalFlow',
      tableHeader: [],
      tableData: [],
      tableData2: [],
      funParams: {},
      queryParam: {},
      dialogFormVisible: false,
      editableTabsValue: 'tab1',
      formLabelWidth: '100px',
      isActive: false,
      preArr: [
        { prop: 'quotaCode', label: () => this.$t('quota.orderNumber') }, // 单据编号
        { prop: 'quotaName', label: () => this.$t('quota.orderTitle') }, // 单据标题
        { prop: 'buName', label: () => this.$t('quota.department') }, // 事业部
        { prop: 'createdBy', label: () => this.$t('quota.createdBy') }, // 创建人
        {
          prop: 'quotaStatus',
          label: () => this.$t('quota.status'),
          type: 'dict', // 字典类型
          code: 'QUOTA_STATUS'
        }, // 状态
        { prop: 'creationDate', label: () => this.$t('quota.createdDate'), type: 'date' } // 创建日期
        /* {
          prop: "itemCode",
          label: ()=>this.$t("bidMod.itemCode"),
          type: "quicksearch",
          showKey: "materialCode",
          name: "scc_base_material_item"
         },
        {
          prop: "vendorName",
          label: ()=>this.$t("bidMod.vendorName"),
          type: "quicksearch",
          showKey: "companyName",
          name: "scc_sup_company_info_display"
         }, */
      ],
      unitList: []
    }
  },
  provide () {
    return { context: this }
  },
  created () {
    let _this = this
    this.tableHeader = [
      {
        prop: 'quotaCode',
        label: _this.$t('quota.orderNumber'), // 单据编号
        width: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.readApproval(row)
        }.bind(this)
      },
      {
        prop: 'quotaName',
        label: _this.$t('quota.orderTitle'), // 单据标题
        minWidth: 150
      },
      {
        prop: 'createdUserName', // createdBy
        label: _this.$t('bidMod.bidingCreatedBy'),
        width: 100
      },
      {
        prop: 'quotaStatus',
        label: _this.$t('quota.orderStatus'), // 单据状态
        width: 100,
        dataType: 'dict', // 数据类型为字典
        code: 'QUOTA_STATUS' // 字典code
      },
      {
        prop: 'creationDate',
        label: _this.$t('bidMod.creationDate'),
        width: 150,
        formattor (val) {
          return val ? parseTime(val) : ''
        }
      },
      {
        prop: 'operation',
        label: _this.$t('bidMod.operation'),
        width: 150,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            callback: function (row) {
              this.editApproval(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.edit')
            }, // 未审批
            show: (row) => row.quotaStatus === 'DRAFT'
          },
          {
            callback: function (row) {
              this.doActive(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.active')
            }, // 生效
            show: (row) => row.quotaStatus === 'DRAFT'
          },
          {
            callback: function (row) {
              this.deleteRow(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.delete')
            },
            show: (row) => row.quotaStatus === 'DRAFT'
          }
        ]
      }
    ]
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  mounted () {},
  methods: {
    deleteRow (row) {
      this.$http({
        url: '/api-inq/inquiry/quota/delete',
        method: 'GET',
        params: { id: row.quotaId }
      }).then((res) => {
        this.$message({ type: 'success', message: res.message })
        this.getQuerydata()
      })
    },
    doActive (row) {
      this.$http({
        url: '/api-inq/inquiry/quota/getQuotaCIF',
        method: 'GET',
        params: { id: row.quotaId }
      }).then((res) => {
        this.$message({ type: 'success', message: res.message })
        this.getQuerydata()
      })
    },
    getQuerydata (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    createProjectDetail (row) {
      this.$emit('tab-add', {
        component: quotaConfigDetail,
        params: {
          flag: 'add'
        },
        title: () => this.$t('quota.newQuotaConfig'), // 新建配额单
        name: 'quotaConfigDetail'
      })
    },
    readApproval (row) {
      this.$emit('tab-add', {
        component: quotaConfigDetail,
        params: {
          flag: 'edit',
          isReadonly: true,
          row: row
        },
        title: row.quotaCode,
        name: 'quotaConfigDetail' + row.quotaCode
      })
    },
    editApproval (row) {
      this.$emit('tab-add', {
        component: quotaConfigDetail,
        params: {
          flag: 'edit',
          isEdit: true,
          row: row
        },
        title: row.quotaCode,
        name: 'quotaConfigDetail' + row.quotaCode
      })
    },
    handleCurrentChange (val) {
      this.currentRow = val
    }
  }
}
</script>
<style scoped lang="scss">
.el-dialog__body {
  padding-top: 0 !important;
}
</style>
