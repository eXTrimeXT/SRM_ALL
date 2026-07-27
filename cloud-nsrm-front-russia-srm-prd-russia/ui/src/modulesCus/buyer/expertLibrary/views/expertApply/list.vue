<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main>
      <FormWrapper :form-array="searchFormConfig" @getFormData="getQueryData" />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <AuthorityButton code="expertApply:add" type="primary" @click="editTab('add',{})">
            绿色通道
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
        :adeptMeiQl="true"
      />
    </el-main>
  </el-container>
</template>

<script>
import { expApplyHttp, commonType } from 'modcb@/expertLibrary/api'
import { tabTodoWatch } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import ExpertApplyDetail from './edit'
import { transformMQL } from 'lib@/utils/util'

export default {
  name: 'ExpertApplyList',

  components: {
    TableView,
    MainHeader,
    FormWrapper
  },

  mixins: [tabTodoWatch],

  data () {
    return {
      tableViewUrl: expApplyHttp.listPageUrl,
      tableHeader: [],
      tableData: [],
      searchFormConfig: [
        {
          prop: 'expertApplyNo',
          label: '申请编号'
        },
        {
          prop: 'applyByNickname',
          label: '申请人'
        },
        {
          prop: 'applyStatus',
          label: '单据状态',
          type: 'dict',
          code: 'EXT_SOU_EXPERT_APPLY_STATUS'
        },
        {
          prop: 'creationDate',
          label: '申请时间',
          type: 'daterange'
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
        prop: 'expertApplyNo',
        label: '申请编号',
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: (row) => {
          console.log('row:::', row)
          this.editTab('view', row)
        }
      },
      {
        prop: 'applyStatus',
        label: '单据状态',
        minWidth: 120,
        dataType: 'dict',
        code: 'EXT_SOU_EXPERT_APPLY_STATUS'
      },
      {
        prop: 'creationDate',
        label: '申请日期',
        minWidth: 150
      },
      {
        prop: 'applyByNickname',
        label: '申请人',
        minWidth: 150
      },
      {
        prop: 'applyFromType',
        label: '数据来源',
        minWidth: 120,
        dataType: 'dict',
        code: 'EXT_SOU_EXPERT_APPLY_FROM_TYPE'
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
            show: row => ['DRAFT', 'WITHDRAW', 'REJECTED'].includes(row.applyStatus),
            formattor: () => this.$t('common.edit'),
            code: 'expertApply:edit',
            callback: row => {
              this.editTab('edit', row)
            }
          },
          {
            show: row => ['DRAFT'].includes(row.applyStatus),
            formattor: () => this.$t('common.delete'),
            code: 'expertApply:delete',
            callback: row => {
              this.deleteRows(row)
            }
          },
          // 审批中 需要审批操作
          {
            show: row => ['APPROVING'].includes(row.applyStatus),
            formattor: () => this.$t('common.approve'),
            code: 'expertApply:approval',
            callback: row => {
              this.editTab('approval', row)
            }
          }
          // {
          //   show: row => ['WITHDRAW', 'REJECTED'].includes(row.applyStatus),
          //   formattor: () => this.$t('common.abandon'),
          //   callback: row => {
          //     this.abandonRows(row)
          //   }
          // },
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
      const { creationDate } = params
      if (creationDate && creationDate.length) {
        creationDate[0] = creationDate[0] + ' 00:00:00'
        creationDate[1] = creationDate[1] + ' 23:59:59'
      }
      let filter = {}
      for (let key in params) {
        if (['creationDate'].includes(key)) {
          filter[key] = {
            'between': params[key]
          }
        } else {
          filter[key] = {
            'contains': params[key]
          }
        }
      }
      this.queryParam = transformMQL.listPageData({
        type: commonType,
        action: 'query',
        filter: {
          ...filter,
          $not: { // 排除数据来源 自主申请 并且 单据状态拟定的单据
            applyFromType: {
              'in': ['INDEPENDENT']
            },
            applyStatus: {
              'eq': 'DRAFT'
            }
          }
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
            component: ExpertApplyDetail,
            params: {
              flag: type,
              row,
              tabName: 'expertApply'
            },
            title: '专家申请绿色通道',
            name: 'expertApply'
          }
        ],
        // 编辑
        [
          'edit',
          {
            component: ExpertApplyDetail,
            params: {
              flag: type,
              row,
              tabName: row.expertApplyNo
            },
            title: '专家申请' + (row.expertApplyNo || ''),
            name: row.expertApplyNo
          }
        ],
        // 查看
        [
          'view',
          {
            component: ExpertApplyDetail,
            params: {
              flag: type,
              row,
              tabName: row.expertApplyNo
            },
            title: '专家申请' + (row.expertApplyNo || ''),
            name: row.expertApplyNo
          }
        ],
        // 审批
        [
          'approval',
          {
            component: ExpertApplyDetail,
            params: {
              flag: type,
              row,
              tabName: row.expertApplyNo,
              activeWorkflowTab: true // 跳转到审批流
            },
            title: '专家申请' + (row.expertApplyNo || ''),
            name: row.expertApplyNo
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
      let transformParams = transformMQL.save(commonType, [row.expertApplyId], 'removeApply')
      await expApplyHttp.delete(transformParams)
      this.$message.success(this.$t('common.successDelete'))
      this.getQueryData()
    },

    /** 废弃 */
    abandonRows () {

    }
  }
}
</script>
