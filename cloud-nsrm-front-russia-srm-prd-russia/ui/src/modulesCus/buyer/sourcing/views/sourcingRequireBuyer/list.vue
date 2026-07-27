<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main>
      <FormWrapper :form-array="searchFormConfig" @getFormData="getQueryData" />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <!-- <AuthorityButton code="sourcingRequireBuyer:add" type="primary" @click="editTab('add',{})">
            新增
          </AuthorityButton> -->
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
        :adeptMeiQl="true"
      />
    </el-main>
  </el-container>
</template>

<script>
import souHttp from '../../api'
import { tabTodoWatch } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import SourcingRequireDetail from './edit'
import SourcingRequireQuote from './quote'
import { transformMQL, transformTimeQuery } from 'lib@/utils/util'
import sourcingApplicationDetailNew from './sourcingApplicationDetailNew'

export default {
  name: 'SourcingRequireList',

  components: {
    TableView,
    MainHeader,
    FormWrapper
  },

  mixins: [tabTodoWatch],

  data () {
    return {
      tableViewUrl: souHttp.listPageUrl,
      tableHeader: [],
      tableData: [],
      searchFormConfig: [
        {
          prop: 'projectName',
          label: '项目名称'
        },
        {
          prop: 'reqHeadNo',
          label: '寻源单号'
        },
        {
          prop: 'status',
          label: '单据状态',
          type: 'dict',
          code: 'SOU_REQ_HEAD_STATUS'
        },
        {
          prop: 'createdFullName',
          label: '创建人'
        },
        {
          prop: 'releaseDate',
          label: '发布日期',
          type: 'daterange'
        },
        {
          prop: 'creationDate',
          label: '创建日期',
          type: 'daterange'
        },
        {
          prop: 'souPersonUserName',
          label: '招标负责人'
        },
        {
          prop: 'responsibilityUserName',
          label: '供应商负责人'
        },
        {
          prop: 'isPreComm',
          label: '是否前置交流',
          type: 'dict',
          code: 'YES_OR_NO'
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
        console.log('nVal', nVal)
        const { from, funName, formId, formNo, row = {} } = nVal
        if (from === 'fromFun' && funName === 'sourcingRequireBuyer') {
          this.editTab('view', {
            reqHeadId: formId,
            reqHeadNo: formNo
          })
        } else if (from === 'recommendVendor') { // 从推荐供应商跳转
          this.editTab('quote', row)
        }
      },
      immediate: true,
      deep: true
    }
  },

  mounted () {
    this.tableHeader = [
      {
        prop: 'reqHeadNo',
        label: '寻源单号',
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: (row) => {
          console.log('row:::', row)
          this.editTab('view', row)
        }
      },
      {
        prop: 'orgBuName',
        label: '板块',
        minWidth: 120
      },
      {
        prop: 'orgName',
        label: '公司',
        minWidth: 150
      },
      {
        prop: 'projectName',
        label: '项目名称',
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: (row) => {
          this.projectFun(row)
        }
      },
      {
        prop: 'publicEndTime',
        label: '公示截止时间',
        minWidth: 150
      },
      {
        prop: 'isPreComm',
        label: '是否前置交流',
        dataType: 'dict',
        code: 'YES_OR_NO',
        minWidth: 120
      },
      {
        prop: 'depositAmount',
        label: '意向金',
        minWidth: 100,
        formattor: (val, row) => row.isNeedDeposit === 'Y' ? row.depositAmount : '--'
      },
      {
        prop: 'responsibilityUserName',
        label: '供应商负责人',
        minWidth: 120
      },
      {
        prop: 'souPersonUserName',
        label: '招标负责人',
        minWidth: 120
      },
      {
        prop: 'technicalUserName',
        label: '技术负责人',
        minWidth: 120
      },
      {
        prop: 'techPhone',
        label: '技术负责人联系方式',
        minWidth: 150
      },
      {
        prop: 'createdFullName',
        label: '创建人',
        minWidth: 150,
        formattor: (val, row) => `${row.createdFullName}（${row.createdBy}）`
      },
      {
        prop: 'status',
        label: '单据状态',
        dataType: 'dict',
        code: 'SOU_REQ_HEAD_STATUS',
        minWidth: 120
      },
      {
        prop: 'creationDate',
        label: '创建日期',
        minWidth: 120,
        formattor: (val) => this.$dayjsParse(val)
      },
      {
        prop: 'releaseDate',
        label: '发布日期',
        minWidth: 120,
        formattor: (val) => this.$dayjsParse(val)
      },
      {
        prop: 'partCancle',
        label: '是否部分取消',
        dataType: 'dict',
        code: 'YES_OR_NO',
        minWidth: 120
      },
      {
        prop: 'operation',
        label: this.$t('bidMod.operation'),
        minWidth: 130,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            show: row => ['DRAFT', 'WITHDRAW', 'REJECTED'].includes(row.status),
            formattor: () => this.$t('common.edit'),
            code: 'sourcingRequireBuyer:edit',
            callback: row => {
              this.editTab('edit', row)
            }
          },
          {
            show: row => ['DRAFT', 'WITHDRAW', 'REJECTED'].includes(row.status),
            formattor: () => this.$t('common.delete'),
            code: 'sourcingRequireBuyer:delete',
            callback: row => {
              this.deleteRows(row)
            }
          },
          // 审批中 需要审批操作
          {
            show: row => ['APPROVING'].includes(row.status),
            formattor: () => this.$t('common.approve'),
            code: 'sourcingRequireBuyer:approval',
            callback: row => {
              this.editTab('approval', row)
            }
          },
          // 接收报名中、报名已截止有管理操作
          {
            show: row => ['APPROVED', 'SIGNUP_DONE', 'ABANDON'].includes(row.status),
            formattor: () => this.$t('purchaseDemand.manage'),
            code: 'sourcingRequireBuyer:manage',
            callback: row => {
              this.editTab('quote', row)
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
    projectFun (row) {
      console.log(row, 'row')
      this.$emit('tab-add', {
        component: sourcingApplicationDetailNew,
        params: {
          flag: 'view',
          row: row,
          tabName: 'purchaseApplicationDetail' + row.projectName
        },
        title: row.projectName,
        name: 'purchaseApplicationDetail' + row.projectName
      })
    },
    /* 查询列表数据 */
    getQueryData (params = {}) {
      transformTimeQuery(['releaseDate', 'creationDate'], params)
      this.queryParam = transformMQL.listPageData({
        type: 'SouReqHeadBuyer',
        action: 'query',
        sort: 'creationDate',
        params,
        filterOperator: {
          releaseDate: 'between',
          creationDate: 'between'
        }
      })

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
            component: SourcingRequireDetail,
            params: {
              flag: type,
              row,
              tabName: 'sourcingRequireBuyer'
            },
            title: '寻源需求新增',
            name: 'sourcingRequireBuyer'
          }
        ],
        // 编辑
        [
          'edit',
          {
            component: SourcingRequireDetail,
            params: {
              flag: type,
              row,
              tabName: '寻源需求编辑' + row.reqHeadNo
            },
            title: '寻源需求编辑' + (row.reqHeadNo || ''),
            name: '寻源需求编辑' + row.reqHeadNo
          }
        ],
        // 查看
        [
          'view',
          {
            component: SourcingRequireDetail,
            params: {
              flag: type,
              row,
              tabName: '寻源需求查看' + row.reqHeadNo
            },
            title: '寻源需求查看' + (row.reqHeadNo || ''),
            name: '寻源需求查看' + row.reqHeadNo
          }
        ],
        // 审批
        [
          'approval',
          {
            component: SourcingRequireDetail,
            params: {
              flag: type,
              row,
              tabName: row.reqHeadNo,
              activeWorkflowTab: true // 跳转到审批流
            },
            title: '寻源需求' + (row.reqHeadNo || ''),
            name: row.reqHeadNo
          }
        ],
        // 报名
        [
          'quote',
          {
            component: SourcingRequireQuote,
            params: {
              flag: type,
              row,
              tabName: '寻源报名管理' + row.reqHeadNo
            },
            title: '寻源报名管理' + (row.reqHeadNo || ''),
            name: '寻源报名管理' + row.reqHeadNo
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

    /** 删除 */
    async deleteRows (row) {
      const confirmResult = await this.$confirm(this.$t('common.confirmDeleteRow'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => {})
      if (confirmResult !== 'confirm') return
      let transformParams = transformMQL.save('SouReqHeadBuyer', [row.reqHeadId], 'delete')
      await souHttp.delete(transformParams)
      this.$message.success(this.$t('common.successDelete'))
      this.getQueryData()
    },

    /** 废弃 */
    abandonRows () {

    }
  }
}
</script>
