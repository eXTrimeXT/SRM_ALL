<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main>
      <FormWrapper :form-array="searchFormConfig" @getFormData="getQueryData" />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left" />
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
        @afterQuery="afterQuery"
      />
    </el-main>

    <!-- 废弃弹窗 -->
    <DiscardDialog
      ref="discardDialog"
      :visible.sync="discardDialogVisible"
      @confirm="discardDialogConfirm"
    />
  </el-container>
</template>

<script>
import { tabTodoWatch } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import CalibrationApplyAbandonDetail from './edit'
import caHttp from './api'
import { transformMQL, transformTimeQuery } from 'lib@/utils/util'
import DiscardDialog from './components/dialog/discardDialog'

export default {
  name: 'CalibrationApplyAbandonList',

  components: {
    TableView,
    MainHeader,
    FormWrapper,
    DiscardDialog
  },

  mixins: [tabTodoWatch],

  data () {
    return {
      tableViewUrl: caHttp.listPageUrl,
      tableHeader: [],
      tableData: [],
      searchFormConfig: [
        {
          prop: 'souName',
          // label: '项目名称'
          label: () => this.$t('bidMod.bidingName')
        },
        {
          prop: 'originalCaNo',
          // label: '定标申请单'
          label: () => this.$t('cusEntry.supplement20250205.bidAwardApplicationForm')
        },
        {
          prop: 'status',
          // label: '单据状态',
          label: () => this.$t('cusEntry.reportManagement.auditStatus'),
          type: 'dict',
          code: 'SOU_CA_STATUS'
        },
        {
          prop: 'createdFullName',
          // label: '创建人'
          label: () => this.$t('common.creator')
        },
        {
          prop: 'releaseTime',
          // label: '发布日期',
          label: () => this.$t('dataConfMod.publishTime'),
          type: 'daterange'
        },
        {
          prop: 'creationDate',
          // label: '创建日期',
          label: () => this.$t('common.creationDate'),
          type: 'daterange'
        }
      ],
      queryParam: {},
      selectedRows: [], // 标记勾选行
      discardDialogVisible: false,
      discardRow: [],
      curUserId: this.$store.getters.userInfo.userId
    }
  },

  watch: {
    '$route.params': {
      // 寻源需求等其它地方跳转过来
      handler (nVal) {
        const { from, row } = nVal
        if (from === 'calibrationApply') {
          this.editTab('add', {
            originalCaId: row.caId
          })
        }
      },
      immediate: true,
      deep: true
    }
  },

  mounted () {
    this.tableHeader = [
      {
        prop: 'caNo',
        // label: '定标废弃申请单',
        label: () => this.$t('cusEntry.supplement20250205.bidAwardCancellationApplicationForm'),
        minWidth: 130,
        showType: 'button',
        btnStyle: 'text',
        callback: (row) => {
          this.editTab('view', row)
        }
      },
      {
        prop: 'originalCaNo',
        // label: '定标申请单',
        label: () => this.$t('cusEntry.supplement20250205.bidAwardApplicationForm'),
        minWidth: 130,
        showType: 'button',
        btnStyle: 'text',
        callback: (row) => {
          this.$router.push({
            name: 'calibrationApply',
            params: {
              from: 'calibrationApplyAbandon',
              row: {
                caId: row.originalCaId,
                caNo: row.originalCaNo
              }
            }
          })
        }
      },
      {
        prop: 'extOrgBuName',
        // label: '板块',
        label: () => this.$t('cusEntry.bidSuperviseReport.extOrgBuName'),
        minWidth: 120
      },
      {
        prop: 'extOrgOuName',
        // label: '公司',
        label: () => this.$t('components.organization.COMPANY'),
        minWidth: 150
      },
      {
        prop: 'souName',
        // label: '项目名称',
        label: () => this.$t('bidMod.bidingName'),
        minWidth: 150
      },
      {
        prop: 'extProjectNo',
        // label: '招标项目编号',
        label: () => this.$t('bidMod.bidingNumCla'),
        minWidth: 120
      },
      {
        prop: 'status',
        // label: '单据状态',
        label: () => this.$t('vendorMod.relegation.documentStatus'),
        dataType: 'dict',
        code: 'SOU_CA_STATUS',
        minWidth: 120
      },
      {
        prop: 'createdFullName',
        // label: '创建人',
        label: () => this.$t('common.creator'),
        minWidth: 120
      },
      {
        prop: 'creationDate',
        // label: '创建日期',
        label: () => this.$t('common.creationDate'),
        minWidth: 130,
        dataType: 'dateTime'
      },
      {
        prop: 'discardDescription',
        // label: '废弃说明',
        label: () => this.$t('cusEntry.supplement20250121.reasonDesc'),
        minWidth: 130
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
            show: row => ['DRAFT', 'WITHDRAW', 'REJECTED'].includes(row.status) && this.curUserId === row.createdId,
            formattor: () => this.$t('common.edit'),
            code: 'calibrationApplyAbandon:update',
            callback: row => {
              this.editTab('edit', row)
            }
          },
          {
            show: row => ['DRAFT'].includes(row.status) && this.curUserId === row.createdId,
            formattor: () => this.$t('common.delete'),
            code: 'calibrationApplyAbandon:delete',
            callback: row => {
              this.deleteRows(row)
            }
          },
          // 审批
          {
            callback: row => this.editTab('approval', row),
            code: 'calibrationApplyAbandon:approval',
            show: row => ['APPROVING'].includes(row.status) && (this.curUserId === row.createdId || row.isApprover == 'Y'),
            formattor: () => this.$t('common.approve')
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
      transformTimeQuery(['releaseTime', 'creationDate'], params)
      this.queryParam = transformMQL.listPageData({
        type: 'Dca',
        action: 'query',
        params,
        filterOperator: {
          releaseTime: 'between',
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
            component: CalibrationApplyAbandonDetail,
            params: {
              flag: type,
              row,
              tabName: 'calibrationApplyAbandon'
            },
            // title: '定标废弃申请',
            title: () => this.$t('cusEntry.supplement20250205.bidAwardCancellationRequest'),
            name: 'calibrationApplyAbandon'
          }
        ],
        // 编辑
        [
          'edit',
          {
            component: CalibrationApplyAbandonDetail,
            params: {
              flag: type,
              row,
              tabName: 'calibrationApplyAbandon' + row.caNo
            },
            // title: '定标废弃申请' + (row.caNo || ''),
            title: this.$t('cusEntry.supplement20250205.bidAwardCancellationRequest') + (row.caNo || ''),
            name: 'calibrationApplyAbandon' + row.caNo
          }
        ],
        // 查看
        [
          'view',
          {
            component: CalibrationApplyAbandonDetail,
            params: {
              flag: type,
              row,
              tabName: 'calibrationApplyAbandon' + row.caNo
            },
            // title: '定标废弃申请' + (row.caNo || ''),
            title: this.$t('cusEntry.supplement20250205.bidAwardCancellationRequest') + (row.caNo || ''),
            name: 'calibrationApplyAbandon' + row.caNo
          }
        ],
        // 审批
        [
          'approval',
          {
            component: CalibrationApplyAbandonDetail,
            params: {
              flag: type,
              row,
              tabName: 'calibrationApplyAbandon' + row.caNo,
              activeWorkflowTab: true // 跳转到审批流
            },
            // title: '定标废弃申请' + (row.caNo || ''),
            title: this.$t('cusEntry.supplement20250205.bidAwardCancellationRequest') + (row.caNo || ''),
            name: 'calibrationApplyAbandon' + row.caNo
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
    
    afterQuery () {
      this.$refs['list'].setTableData(async tableData => {
        const res = await this.$api.base.flowAPI.queryTodo()
        let queryTodoList = res.data || []
        tableData.forEach(tableItem => {
          let obj = queryTodoList.find(todoItem => tableItem.caId + '' === todoItem.businessId + '')
          if (obj) {
            this.$set(tableItem, 'isApprover', 'Y')
          } else {
            this.$set(tableItem, 'isApprover', 'N')
          }
        })
      })
    },

    /** 删除 */
    async deleteRows (row) {
      const confirmResult = await this.$confirm(this.$t('common.confirmDeleteRow'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => {})
      if (confirmResult !== 'confirm') return
      let transformParams = transformMQL.save('Dca', [row.caId], 'delete')
      await caHttp.delete(transformParams)
      this.$message.success(this.$t('common.successDelete'))
      this.getQueryData()
    },

    /** 废弃 */
    async abandonRows (row) {
      // const confirmResult = await this.$confirm(this.$t('common.confirmAbandonRow'), {
      //   confirmButtonText: this.$t('common.confirm'),
      //   cancelButtonText: this.$t('common.cancel'),
      //   type: 'warning'
      // }).catch(() => {})
      // if (confirmResult !== 'confirm') return
      this.discardDialogVisible = true
      this.$nextTick(() => {
        this.$refs.discardDialog.resetFields()
      })
      this.discardRow = row
      // let transformParams = transformMQL.save('Dca', [row.caId], 'abandon')
      // await caHttp.abandon(transformParams)
      // this.$message.success(this.$t('common.successAbandon'))
      // this.getQueryData()
    },
    async discardDialogConfirm (form) {
      let transformParams = transformMQL.save('Dca', [{
        ...this.discardRow,
        ...form
      }], 'abandon')
      await caHttp.abandon(transformParams)
      this.$message.success(this.$t('common.successAbandon'))
      this.getQueryData()
    }
  }
}
</script>
