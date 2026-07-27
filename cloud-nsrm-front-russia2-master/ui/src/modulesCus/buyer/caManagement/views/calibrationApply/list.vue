<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main>
      <FormWrapper :form-array="searchFormConfig" @getFormData="getQueryData" />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <!-- <AuthorityButton code="ca:create" type="primary" @click="editTab('add',{})">
            {{ $t('common.add') }}
          </AuthorityButton> -->
          <AuthorityButton code="ca:abandonApply" type="primary" @click="abandonApply">
            <!-- 废弃定标申请 -->
            {{ $t("cusEntry.route.calibrationApplyAbandon") }}
          </AuthorityButton>
          <!-- 导出明细 -->
          <ExportDirect
            exprotUrl="/api-sou/ext/buyer/bid/init/downloadCalibrateExcel"
            code="ca:exprotDetail"
            requstType="POST"
            type="default"
            :btnText="$t('cusEntry.common.exprotDetail')"
            :filename="$t('cusEntry.common.exprotDetail') + '.xls'"
            :filterParams="filterParams"
            :validateExport="beforeExport"
          />
        </template>
      </MainHeader>

      <TableView
        ref="list"
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :com-active="$attrs['changeTab']"
        :checkbox="true"
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
import ExportDirect from 'lib@/components/export-direct'
import CalibrationApplyDetail from './edit'
import caHttp from './api'
import { transformMQL, transformTimeQuery } from 'lib@/utils/util'
import DiscardDialog from './components/dialog/discardDialog'

export default {
  name: 'CalibrationApplyList',

  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportDirect,
    DiscardDialog
  },

  mixins: [tabTodoWatch],

  data () {
    return {
      filterParams: {},
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
          prop: 'extProjectNo',
          // label: '招标项目编号'
          label: () => this.$t('bidMod.bidingNumCla')
        },
        {
          prop: 'status',
          // label: '单据状态',
          label: () => this.$t('vendorMod.relegation.documentStatus'),
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
        if (['calibrationApplyAbandon', 'bidNotice'].includes(from)) {
          this.editTab('view', row)
        } else if (from === 'biddingManagementNew') {
          this.editTab('edit', row)
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
        // label: '定标申请单',
        label: () => this.$t('cusEntry.supplement20250205.bidAwardApplicationForm'),
        minWidth: 130,
        showType: 'button',
        btnStyle: 'text',
        callback: (row) => {
          this.editTab('view', row)
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
            code: 'ca:update',
            callback: row => {
              this.editTab('edit', row)
            }
          },
          {
            show: row => ['DRAFT'].includes(row.status) && this.curUserId === row.createdId,
            formattor: () => this.$t('common.delete'),
            code: 'ca:delete',
            callback: row => {
              this.deleteRows(row)
            }
          },
          // 审批
          {
            callback: row => this.editTab('approval', row),
            code: 'ca:approval',
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
        type: 'Ca',
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
            component: CalibrationApplyDetail,
            params: {
              flag: type,
              row,
              tabName: 'calibrationApply'
            },
            // title: '定标审批单',
            title: () => this.$t('cusEntry.route.calibrationApply'),
            name: 'calibrationApply'
          }
        ],
        // 编辑
        [
          'edit',
          {
            component: CalibrationApplyDetail,
            params: {
              flag: type,
              row,
              tabName: 'calibrationApply' + row.caNo
            },
            // title: '定标审批单' + (row.caNo || ''),
            title: () => this.$t('cusEntry.route.calibrationApply') + (row.caNo || ''),
            name: 'calibrationApply' + row.caNo
          }
        ],
        // 查看
        [
          'view',
          {
            component: CalibrationApplyDetail,
            params: {
              flag: type,
              row,
              tabName: 'calibrationApply' + row.caNo
            },
            // title: '定标审批单' + (row.caNo || ''),
            title: () => this.$t('cusEntry.route.calibrationApply') + (row.caNo || ''),
            name: 'calibrationApply' + row.caNo
          }
        ],
        // 审批
        [
          'approval',
          {
            component: CalibrationApplyDetail,
            params: {
              flag: type,
              row,
              tabName: 'calibrationApply' + row.caNo,
              activeWorkflowTab: true // 跳转到审批流
            },
            // title: '定标审批单' + (row.caNo || ''),
            title: () => this.$t('cusEntry.route.calibrationApply') + (row.caNo || ''),
            name: 'calibrationApply' + row.caNo
          }
        ]
      ])
      this.$emit('tab-add', map.get(type))
    },

    /** 废弃定标申请 */
    async abandonApply () {
      // if (!this.selectedRows || !this.selectedRows.length) return this.$message.warning('请勾选列表')
      if (!this.selectedRows || !this.selectedRows.length) return this.$message.warning(this.$t('outsource.pleaseCheckList'))
      // if (this.selectedRows.length > 1) return this.$message.warning('仅可勾选一条')
      if (this.selectedRows.length > 1) return this.$message.warning(this.$t('cusEntry.supplement20250205.onlyOneSelectable'))
      // if (this.selectedRows[0].status !== 'APPROVED') return this.$message.warning('仅【已审批】状态可发起废弃定标申请')
      if (this.selectedRows[0].status !== 'APPROVED') return this.$message.warning(this.$t('cusEntry.supplement20250205.onlyApprovedStatusCanInitiateAbandonment'))
      const confirmResult = await this.$confirm(this.$t('cusEntry.supplement20250205.confirmInitiateAbandonmentApplication'), { //确定发起废弃定标申请？
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => {})
      if (confirmResult !== 'confirm') return
      this.$router.push({
        name: 'calibrationApplyAbandon',
        params: {
          from: 'calibrationApply',
          row: this.selectedRows[0]
        }
      })
    },

    beforeExport () {
      return new Promise((resolve, reject) => {
        if (!this.selectedRows || !this.selectedRows.length || this.selectedRows.length > 1) {
          reject()
          // 请勾选一条数据进行导出
          return this.$message.warning(this.$t('cusEntry.supplement20250205.selectAndExportData'))
        }
        this.filterParams = { caId: this.selectedRows[0].caId }
        resolve()
      })
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
      let transformParams = transformMQL.save('Ca', [row.caId], 'delete')
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
      // let transformParams = transformMQL.save('Ca', [row.caId], 'abandon')
      // await caHttp.abandon(transformParams)
      // this.$message.success(this.$t('common.successAbandon'))
      // this.getQueryData()
    },
    async discardDialogConfirm (form) {
      let transformParams = transformMQL.save('Ca', [{
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
