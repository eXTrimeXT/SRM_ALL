<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main>
      <FormWrapper :form-array="searchFormConfig" @getFormData="getQueryData" />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <AuthorityButton code="chDesignPlan:add" type="primary" @click="editTab('add',{})">
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
import ChDesignPlanDetail from './edit'
import InquiryDetail from 'modcb@/centralizedPurchase/views/inquiry/inquiry-detail'

export default {
  name: 'ChDesignPlanList',

  components: {
    TableView,
    MainHeader,
    FormWrapper,
    MImport
  },

  mixins: [tabTodoWatch],

  data () {
    return {
      tableViewUrl: designPlanHttp.listPageUrl,
      tableHeader: [],
      tableData: [],
      searchFormConfig: [
        {
          prop: 'projectCode',
          label: this.$t('bidMod.bidingNum'), // '项目编号'
        },
        {
          prop: 'createdBy',
          label: this.$t('common.createdFullName'), // '创建人'
        },
        {
          prop: 'status',
          label: this.$t('vendorMod.approvalStatus'), // '审核状态',
          type: 'dict',
          code: 'DESIGN_PLAN_STATUS'
        },
        {
          prop: 'creationDateAlias',
          label: this.$t('common.creationTime'), // '创建时间',
          type: 'daterange'
        }
      ],
      queryParam: {},
      selectedRows: [] // 标记勾选行
    }
  },

  computed: {
    username () {
      return this.$store.getters.userInfo.username || ''
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
        prop: 'projectCode',
        label: this.$t('bidMod.bidingNum'), // '项目编号'
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: (row) => {
          console.log('row:::', row)
          this.editTab('view', row)
        }
      },
      {
        prop: 'projectName',
        label: this.$t('bidMod.bidingName'), // 项目名称
        minWidth: 150
      },
      {
        prop: 'num',
        label: this.$t('bidMod.bidingRound'), // '轮数',
        minWidth: 100
      },
      {
        prop: 'createdFullName',
        label: this.$t('common.createdFullName'), // '创建人',
        minWidth: 120
      },
      {
        prop: 'creationDate',
        label: this.$t('common.creationTime'), // '创建时间',
        minWidth: 120,
        dataType: 'dateTime'
      },
      {
        prop: 'status',
        label: this.$t('vendorMod.approvalStatus'), // '审核状态',
        dataType: 'dict',
        code: 'DESIGN_PLAN_STATUS',
        minWidth: 120
      },
      {
        prop: 'hasCreatePurInq',
        label: this.$t('sourcingBuyer.isInquiry'), // '是否发起询价',
        dataType: 'dict',
        code: 'YES_OR_NO',
        minWidth: 120
      },
      {
        prop: 'projMoney',
        label: this.$t('cusEntry.centralizedPurchase.projectAmount'), // '项目金额（卢布）',
        minWidth: 150
      },
      {
        prop: 'depName',
        label: this.$t('cusEntry.centralizedPurchase.createCompany'), // '创建单位',
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
            show: row => ['DRAFT', 'WITHDRAW', 'REJECTED'].includes(row.status),
            formattor: () => this.$t('common.edit'),
            code: 'chDesignPlan:edit',
            callback: row => {
              this.editTab('edit', row)
            }
          },
          // 拟定可以删除
          {
            show: row => ['DRAFT'].includes(row.status),
            formattor: () => this.$t('common.delete'),
            code: 'chDesignPlan:delete',
            callback: row => {
              this.deleteRow(row)
            }
          },
          {
            show: row => ['APPROVED'].includes(row.status) && row.hasCreatePurInq !== 'Y',
            formattor: () => this.$t('sourcingBuyer.inquiryOne'), // '发起询价'
            code: 'chDesignPlan:createInq',
            callback: row => {
              this.createInquiry(row)
            }
          },
          {
            show: row => ['APPROVING'].includes(row.status) && row.startBpmUsername === this.username,
            formattor: () => this.$t('common.recall'), // '撤回'
            code: 'chDesignPlan:withdraw',
            callback: row => {
              this.editTab('view', row)
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
    /* 查询列表数据 */
    getQueryData (params = {}) {
      let transformParams = {}
      const { creationDateAlias, ...rest } = params
      if (creationDateAlias && creationDateAlias.length) {
        transformParams.creationDate = creationDateAlias[0]
        transformParams.createDateEnd = creationDateAlias[1]
      }
      this.queryParam = {
        ...rest,
        ...transformParams
      }

      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },

    // 发起询价
    async createInquiry (row) {
      if (!row.designId) return
      const response = await this.$http({
        url: `/api-sou/design/plan/createPurInq/${row.designId}`,
        method: 'POST',
        data: {},
        loading: true
      })
      if (response) {
        let result = response.data || {}
        if (!result.projectId) return
        this.getQueryData(this.queryParam)
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
      }
    },

    editTab (type, row) {
      const map = new Map([
        // 新增
        [
          'add',
          {
            component: ChDesignPlanDetail,
            params: {
              flag: type,
              row,
              tabName: 'chDesignPlan'
            },
            title: this.$t('common.add'),
            name: 'chDesignPlan'
          }
        ],
        // 编辑
        [
          'edit',
          {
            component: ChDesignPlanDetail,
            params: {
              flag: type,
              row,
              tabName: row.projectCode
            },
            title: row.projectCode || '',
            name: row.projectCode
          }
        ],
        // 查看
        [
          'view',
          {
            component: ChDesignPlanDetail,
            params: {
              flag: type,
              row,
              tabName: row.projectCode
            },
            title: row.projectCode || '',
            name: row.projectCode
          }
        ],
        // 审批
        [
          'approval',
          {
            component: ChDesignPlanDetail,
            params: {
              flag: type,
              row,
              tabName: row.projectCode,
              activeWorkflowTab: true // 跳转到审批流
            },
            title: this.$t('cusEntry.route.calibrationApply') + (row.projectCode || ''), // 定标审批单
            name: row.projectCode
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
      const response = await designPlanHttp.delete({
        designId: row.designId
      })
      if (response) {
        this.$message.success(this.$t('common.success'))
        this.getQueryData()
      }
    }
  }
}
</script>
