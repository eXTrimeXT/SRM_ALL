<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <FormWrapper
        :form-array="formWrapperArray"
        form-label-width="120px"
        @getFormData="getQueryData"
      />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <ExportExcel
            :page-url="tableViewUrl"
            export-mode="front"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            :filter-params="queryParam"
            :title="$t('components.eio.customExport')"
            type="default"
          />
        </template>
      </MainHeader>

      <TableView
        ref="list"
        big-data
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        open-custom-table
        :com-active="$attrs['changeTab']"
        :url="tableViewUrl"
      />
    </el-main>
  </el-container>
</template>

<script>
import { inqBuyerHttp } from 'modb@/inquiry/api'
import {
  judgeApproval,
  judgeCancel,
  judgeCopy,
  judgeDelete,
  judgeEdit,
  judgeManage,
  judgeView
} from 'lib@/composition/inquiry/utils'
import { tabTodoWatch } from '@/utils/mixins'
import { EXT_INQ_SOU_PROJECT_STATUS_ENMU, SOU_SCORE_RULE_TYPE_ENUM } from 'lib@/composition/origin/extEnum'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import inquiryDetail from './inquiryDetail.vue'
import inquiryTrackingDetail from './inquiryTrackingDetail.vue'
import ExportExcel from 'lib@/components/export-excel'
export default {
  name: 'InquiryList',

  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel
  },

  mixins: [tabTodoWatch],

  data () {
    return {
      tableViewUrl: inqBuyerHttp.init.listPageUrl,
      tableHeader: [],
      tableData: [],
      formWrapperArray: [
        // 询价单号
        { prop: 'souNo', label: this.$t('bidMod.inquiryNo') },
        // 询价状态
        {
          prop: 'extProjectStatus',
          label: this.$t('bidMod.inquiryStatus'),
          type: 'dict',
          code: 'EXT_INQ_SOU_PROJECT_STATUS'
        },
        // 采购员
        {
          prop: 'createdId',
          label: this.$t('cusEntry.inq.purchaser'),
          type: 'quicksearch',
          propKey: 'userId',
          showKey: 'nickname',
          name: 'scc_rbac_user_display'
        },
        // 审批状态
        {
          prop: 'createApprovalStatus',
          label: this.$t('bidMod.auditStatus'),
          type: 'dict',
          code: 'SOU_APPROVAL_STATUS'
        },
        {
          prop: 'creationDate',
          label: this.$t('bidMod.creationDate'),
          type: 'date'
        },
        {
          prop: 'itemCode',
          label: this.$t('bidMod.itemCode')
        },
        {
          prop: 'itemDesc',
          label: this.$t('bidMod.itemDesc')
        },
        {
          prop: 'extMaterialModel',
          label: this.$t('cusEntry.bidMod.specification')
        }
      ],
      queryParam: {},
      dictCodes: {
        extProjectStatus: 'EXT_INQ_SOU_PROJECT_STATUS',
        createApprovalStatus: 'SOU_APPROVAL_STATUS'
      }
    }
  },

  watch: {
    $route: {
      deep: true,
      immediate: true,
      handler () {
        if (this.$route.params.from === 'fromFun' && this.$route.params.funName === 'inquiryManagement') {
          const projectId = Number(this.$route.params.formId)
          const title = this.$route.params.formNo
          const row = {
            ...this.$route.params,
            projectId,
            souNo: title
          }
          this.openDetailTab('view', row)
        }
        if (this.$route.params.type === 'INQUIRY') {
          this.showInquiryListDetail(this.$route.params.projectId, this.$route.params.inquiryNumber)
        }
        if (this.$route.params.from === 'demandPoolManagement' && this.$route.params.funName === 'inquiryManagement') {
          // 需求池跳转编辑
          this.openDetailTab('edit', {
            projectId: this.$route.params.formId,
            souNo: this.$route.params.formNo
          })
        }
      }
    }
  },

  created () {
    // 设置表头
    this.tableHeader = [
      // t 询价单号
      {
        prop: 'souNo',
        label: this.$t('bidMod.inquiryNo'),
        minWidth: 140,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.openDetailTab('view', row)
      },
      // t 单据状态
      {
        prop: 'extProjectStatus',
        label: this.$t('bidMod.inquiryStatus'),
        minWidth: 120,
        dataType: 'dict',
        code: 'EXT_INQ_SOU_PROJECT_STATUS'
      },
      // 审核状态
      {
        prop: 'createApprovalStatus',
        label: this.$t('bidMod.auditStatus'),
        minWidth: 120,
        dataType: 'dict',
        code: 'SOU_APPROVAL_STATUS'
      },
      // t 轮次
      {
        prop: 'currentRound',
        label: this.$t('bidMod.bidingRound'),
        minWidth: 80
      },
      // t 创建人
      {
        prop: 'createdUserName',
        label: this.$t('cusEntry.inq.purchaser'),
        minWidth: 120
      },
      // t 创建时间
      {
        prop: 'creationDate',
        label: this.$t('bidMod.creationDate'),
        minWidth: 150,
        dataType: 'dateTime'
      },
      // t 发布时间
      {
        prop: 'publishTime',
        label: this.$t('bidMod.releaseDatetime'),
        minWidth: 150,
        dataType: 'dateTime'
      },
      // t 报价截止时间
      {
        prop: 'orderEndTime',
        label: this.$t('bidMod.quotedeadline'),
        minWidth: 150,
        dataType: 'dateTime'
      },
      // t 操作
      {
        prop: 'operation',
        label: this.$t('common.operation'),
        minWidth: 220,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          // b 编辑
          {
            // 单据状态拟定，审核状态拟定
            show: row => judgeEdit(row),
            formattor: () => this.$t('common.edit'),
            callback: row => this.openDetailTab('edit', row)
          },
          // b 删除
          {
            show: row => judgeDelete(row),
            formattor: () => this.$t('common.delete'),
            callback: row => this.deleteRow(row)
          },
          // b 询价管理
          {
            show: row => judgeManage(row),
            formattor: () => this.$t('bidMod.inquiryManage'),
            callback: row => this.openDetailTab('manage', row)
          },
          // b 审批
          {
            // 审核状态：已提交
            show: row => judgeApproval(row),
            formattor: () => this.$t('common.approve'),
            callback: row => this.openDetailTab('approve', row)
          },
          // b 取消
          {
            // 拟定、已取消、已定价
            show: row => judgeCancel(row),
            formattor: () => this.$t('common.cancel'),
            callback: row => this.cancelProject(row)
          },
          // b 查看
          {
            // 已取消
            show: row => judgeView(row),
            formattor: () => this.$t('common.view'),
            callback: row => this.openDetailTab('view', row)
          }
        ]
      }
    ]

    this.$nextTick(() => {
      this.getQueryData()
    })
  },

  mounted () {
    // if (this.$route.params.status) {
    //   this.showInquiryListDetail(this.$route.params.projectId, this.$route.params.inquiryNumber)
    // }
  },

  methods: {
    /* 查询列表数据 */
    getQueryData (val) {
      this.queryParam = Object.assign({}, val || this.queryParam)
      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },

    /* 打开详情页签 */
    openDetailTab (type, row = {}) {
      const map = new Map([
        // 新增
        [
          'add',
          {
            component: inquiryDetail,
            params: {
              flag: 'add',
              readOnly: false,
              tabName: 'inquiryDetail'
            },
            title: this.$t('inquiryBySimple.addInquiryBySimple'),
            name: 'inquiryDetail'
          }
        ],
        // 编辑
        [
          'edit',
          {
            component: inquiryDetail,
            params: {
              flag: 'edit',
              readOnly: false,
              row: row,
              tabName: row.souNo || ''
            },
            title: row.souNo || '',
            name: row.souNo || ''
          }
        ],
        // 查看 审批
        [
          'view',
          {
            component: inquiryDetail,
            params: {
              flag: 'view',
              readOnly: true,
              row: row,
              tabName: `inquiryDetail${(row.souNo || '')}`
            },
            title: row.souNo,
            name: `inquiryDetail${row.souNo || ''}`
          }
        ],
        [
          'approve',
          {
            component: inquiryDetail,
            params: {
              flag: 'approve',
              readOnly: true,
              row: row,
              tabName: `inquiryDetail${(row.souNo || '')}`
            },
            title: row.souNo,
            name: `inquiryDetail${row.souNo || ''}`
          }
        ],
        // 询价管理
        [
          'manage',
          {
            component: inquiryTrackingDetail,
            params: {
              flag: 'edit',
              row: row,
              tabName: row.souNo || ''
            },
            title: row.souNo || '',
            name: row.souNo || ''
          }
        ]
      ])

      this.$emit('tab-add', map.get(type))
    },

    /* 删除 */
    async deleteRow (row) {
      const confirmResult = await this.$confirm(this.$t('common.delRow'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => { /* nothing */ })

      if (confirmResult !== 'confirm') {
        return
      }

      const response = await inqBuyerHttp.init.delete(row.projectId)
      if (response) {
        this.$message.success(this.$t('common.successDelete'))
        this.getQueryData()
      }
    },

    /* 取消询价单 */
    async cancelProject (row) {
      const confirmResult = await this.$confirm(this.$t('bidMod.cancelProject'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => { /* nothing */ })

      if (confirmResult !== 'confirm') {
        return
      }

      const response = await inqBuyerHttp.init.cancel({
        projectId: row.projectId,
        cancelReason: ''
      })
      if (response) {
        this.$message.success(this.$t('bidMod.cancelSuccess'))
        this.getQueryData()
      }
    },

    /* 展示询价单详情 */
    showInquiryListDetail (projectId, souNo) {
      this.openDetailTab('view', { projectId, souNo })
    },

    /* 复制询价单 */
    async copyInquiry (row) {
      const confirmResult = await this.$confirm(this.$t('bidMod.confirmCopy'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => { /* nothing */ })

      if (confirmResult !== 'confirm') {
        return
      }

      const response = await inqBuyerHttp.init.copy(row.projectId)
      if (response) {
        const { projectId, souNo } = response.data?.projectInfo
        if (projectId && souNo) {
          this.$message.success(this.$t('logisticsMod.copySuccess'))
          // 更新列表
          this.getQueryData()
          // 进入详情页面，带上询价单号和ID
          this.openDetailTab('edit', { souNo, projectId })
        } else {
          this.$message.error(this.$t('bidMod.cannotGetInquiry'))
        }
      }
    }
  }
}
</script>
