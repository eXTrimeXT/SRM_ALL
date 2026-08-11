<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main>
      <FormWrapper :form-array="searchFormConfig" @getFormData="getQueryData" />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <AuthorityButton code="priceAdjustApply:add" type="primary" @click="editTab('add',{})">
            {{ $t('common.add') }}
          </AuthorityButton>
        </template>
      </MainHeader>

      <TableView
        ref="list"
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :com-active="$attrs['changeTab']"
        :checkbox="false"
        :checkChange="handleCurrentChange"
        open-custom-table
        :url="tableViewUrl"
      />
    </el-main>
  </el-container>
</template>

<script>
import { designPlanHttp } from 'modcb@/jcAgreement/api'
import { tabTodoWatch } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import PriceAdjustApplyDetail from './edit'
import InquiryDetail from 'modcb@/centralizedPurchase/views/inquiry/inquiry-detail'

export default {
  name: 'PriceAdjustApplyList',

  components: {
    TableView,
    MainHeader,
    FormWrapper,
    MImport
  },

  mixins: [tabTodoWatch],

  data () {
    return {
      extraData: {
        fileModular: 'sou',
        fileFunction: 'priceAdjustApply',
        fileType: 'excel'
      },
      tableViewUrl: '/api-sou/price/adjustment/apply/getChLedgerPageList',
      tableHeader: [],
      tableData: [],
      searchFormConfig: [
        {
          prop: 'adjustCode',
          label: this.$t('cusEntry.supplement20250121.adjustCode'), // '调价申请编号
        },
        {
          prop: 'adjustName',
          label: this.$t('cusEntry.supplement20250121.adjustName'), // '调价申请名称
        },
        {
          prop: 'createdFullName',
          label: this.$t('bidMod.projectLeader'), // '项目负责人'
          type: 'quicksearch',
          showKey: 'nickname',
          propKey: 'nickname',
          name: 'scc_rbac_user_display'
        },
        {
          prop: 'creationDate',
          label: this.$t('outsource.executionTime'), // '执行时间'
          type: 'daterange'
        },
        {
          prop: 'jcCode',
          label: this.$t('cusEntry.supplement20250121.jcCode'), // '集采项目编号
        },
        {
          prop: 'status',
          label: this.$t('common.status'), // '状态'
          type: 'dict',
          code: 'APPLY_ADJUST_STATUS'
        },
        {
          prop: 'adjustType',
          label: this.$t('cusEntry.supplement20250121.adjustType'), // '调价形式'
          type: 'dict',
          code: 'APPLY_ADJUST_TYPE'
        }
      ],
      queryParam: {},
      selectedRows: [] // 标记勾选行
    }
  },

  watch: {
    '$route.params': {
      // 寻源需求等其它地方跳转过来
      handler (nVal) {
        const { from, row } = nVal
        if (from) {
          this.editTab('view', row)
        }
      },
      immediate: true,
      deep: true
    }
  },

  mounted () {
    this.tableHeader = [
      {
        prop: 'adjustCode',
        label: this.$t('cusEntry.supplement20250121.adjustCode'), // '调价申请编号'
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: (row) => {
          console.log('row:::', row)
          this.editTab('view', row)
        }
      },
      {
        prop: 'adjustName',
        label: this.$t('cusEntry.supplement20250121.adjustName'), // '调价申请名称'
        minWidth: 150
      },
      {
        prop: 'createdFullName',
        label: this.$t('bidMod.projectLeader'), // '项目负责人'
        minWidth: 100
      },
      {
        prop: 'creationDate',
        label: this.$t('outsource.executionTime'), // '创建日期'
        minWidth: 120,
        dataType: 'dateTime'
      },
      {
        prop: 'adjustType',
        label: this.$t('cusEntry.supplement20250121.adjustType'), // '调价形式'
        minWidth: 120,
        dataType: 'dict',
        code: 'APPLY_ADJUST_TYPE'
      },
      {
        prop: 'jcCode',
        label: this.$t('cusEntry.supplement20250121.linkJcCode'), // '关联集采项目编号'
        minWidth: 150
      },
      {
        prop: 'executeDateStart',
        label: this.$t('cusEntry.centralizedPurchase.executionStartTime'), // '执行开始时间'
        minWidth: 150,
        dataType: 'dateTime'
      },
      {
        prop: 'executeDateEnd',
        label: this.$t('cusEntry.centralizedPurchase.executionEndTime'), // '执行结束时间'
        minWidth: 150,
        dataType: 'dateTime'
      },
      {
        prop: 'status',
        label: this.$t('common.status'), // '调价状态'
        dataType: 'dict',
        code: 'APPLY_ADJUST_STATUS',
        minWidth: 120
      },
      {
        prop: 'createUnitName',
        label: this.$t('cusEntry.centralizedPurchase.createCompany'), // '创建单位'
        minWidth: 150
      },
      {
        prop: 'operation',
        label: this.$t('bidMod.operation'),
        minWidth: 150,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          // 拟定可以编辑
          {
            show: row => ['DRAFT'].includes(row.status),
            formattor: () => this.$t('common.edit'),
            code: 'priceAdjustApply:edit',
            callback: row => {
              this.editTab('edit', row)
            }
          },
          // 拟定可以删除
          {
            show: row => ['DRAFT'].includes(row.status),
            formattor: () => this.$t('common.delete'),
            code: 'priceAdjustApply:delete',
            callback: row => {
              this.deleteRow(row)
            }
          },
          {
            show: row => ['DRAFT'].includes(row.status) && ['1'].includes(row.adjustType),
            formattor: () => this.$t('inquiryBySimple.addInquiryBySimple'), // 创建询价单
            code: 'priceAdjustApply:createInq',
            callback: row => {
              this.createInq(row)
            }
          }
        ]
      }
    ]
    this.$nextTick(() => {
      this.getQueryData()
    })
  },

  methods: {
    // 创建询价单
    createInq(row){
      this.$http({
        url: `/api-sou/design/plan/createPurInq/${row?.designId}`,
        method: 'POST',
        data: {},
        loading: true
      }).then((response) => {
        this.$message.success(this.$t('common.success'))
        let result = response.data || {}
        this.$emit('tab-add', {
          name: `inquiryDetail${result.souNo}`,
          component: InquiryDetail,
          params: {
            tab: 'edit',
            tabName: `inquiryDetail${result.souNo}`,
            row: {
              projectId: result.projectId,
              souNo: result.souNo
            },
            readOnly: false
          },
          title: result.souNo
        })
      })
    },
    /* 查询列表数据 */
    getQueryData (params = {}) {
      let transformParams = {}
      const { creationDate, ...rest } = params
      if (creationDate && creationDate.length) {
        transformParams.executeDateStart = creationDate[0]
        transformParams.executeDateEnd = creationDate[1]
      }
      this.queryParam = {
        ...rest,
        ...transformParams
      }

      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },

    editTab (type, row) {
      const map = new Map([
        // 新增
        [
          'add',
          {
            component: PriceAdjustApplyDetail,
            params: {
              flag: type,
              row,
              tabName: 'priceAdjustApply'
            },
            title: this.$t('common.add'),
            name: 'priceAdjustApply'
          }
        ],
        // 编辑
        [
          'edit',
          {
            component: PriceAdjustApplyDetail,
            params: {
              flag: type,
              row,
              tabName: row.adjustCode
            },
            title: row.adjustCode || '',
            name: row.adjustCode
          }
        ],
        // 查看
        [
          'view',
          {
            component: PriceAdjustApplyDetail,
            params: {
              flag: type,
              row,
              tabName: row.adjustCode
            },
            title: row.adjustCode || '',
            name: row.adjustCode
          }
        ]
      ])
      this.$emit('tab-add', map.get(type))
    },

    /* 选中行 */
    handleCurrentChange (val) {
      console.log('val:::', val)
      this.selectedRows = val
    },

    async deleteRow (row) {
      const confirmResult = await this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => {})
      if (confirmResult !== 'confirm') return
      this.$http({
        url: `/api-sou/price/adjustment/apply/delete?adjustId=${row?.adjustId}`,
        method: 'DELETE',
        loading: true
      }).then(() => {
        this.$message.success(this.$t('common.successDelete'))
        this.getQueryData()
      })
    }
  }
}
</script>
