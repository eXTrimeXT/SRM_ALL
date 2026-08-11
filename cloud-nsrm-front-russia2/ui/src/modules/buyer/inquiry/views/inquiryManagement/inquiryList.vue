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
          <!--b 创建询价单-->
          <el-button type="primary" @click="openDetailTab('add')">
            {{ $t('inquiryBySimple.addInquiryBySimple') }}
          </el-button>
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
import { SOU_PROJECT_STATUS_ENUM, SOU_SCORE_RULE_TYPE_ENUM } from 'lib@/composition/origin/enum'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import inquiryDetail from './inquiryDetail.vue'
import inquiryTrackingDetail from './inquiryTrackingDetail.vue'

export default {
  name: 'InquiryList',

  components: {
    TableView,
    MainHeader,
    FormWrapper
  },

  mixins: [tabTodoWatch],

  data () {
    return {
      tableViewUrl: inqBuyerHttp.init.listPageUrl,
      tableHeader: [],
      tableData: [],
      formWrapperArray: [
        // 询价标题
        { prop: 'souName', label: this.$t('bidMod.inquiryTitle') },
        // 询价单号
        { prop: 'souNo', label: this.$t('bidMod.inquiryNo') },
        // 询价状态
        {
          prop: 'extProjectStatus',
          label: this.$t('bidMod.inquiryStatus'),
          type: 'dict',
          code: 'SOU_PROJECT_STATUS',
          filterItem: () => [
            SOU_PROJECT_STATUS_ENUM.ACCEPT_SIGN_UP,
            SOU_PROJECT_STATUS_ENUM.SIGN_UP_END,
            SOU_PROJECT_STATUS_ENUM.TECH_EVAL,
            SOU_PROJECT_STATUS_ENUM.BUSINESS_EVAL
          ]
        },
        // 评分规则
        {
          prop: 'scoreRuleType',
          label: this.$t('bidMod.inquiryRule'),
          type: 'dict',
          code: 'SOU_SCORE_RULE_TYPE',
          filterItem: () => [SOU_SCORE_RULE_TYPE_ENUM.COMPOSITE_PRICE]
        },
        // 创建人
        {
          prop: 'createdId',
          label: this.$t('bidMod.creator'),
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
        }
      ],
      queryParam: {}
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
      // t 询价标题
      {
        prop: 'souName',
        label: this.$t('bidMod.inquiryTitle'),
        minWidth: 140
      },
      // t 评分规则
      {
        prop: 'scoreRuleType',
        label: this.$t('bidMod.inquiryRule'),
        minWidth: 120,
        dataType: 'dict',
        code: 'SOU_SCORE_RULE_TYPE'
      },
      // t 单据状态
      {
        prop: 'extProjectStatus',
        label: this.$t('bidMod.inquiryStatus'),
        minWidth: 120,
        dataType: 'dict',
        code: 'SOU_PROJECT_STATUS'
      },
      // 审核状态
      {
        prop: 'createApprovalStatus',
        label: this.$t('bidMod.auditStatus'),
        minWidth: 120,
        dataType: 'dict',
        code: 'SOU_APPROVAL_STATUS'
      },
      // t 报价方式
      {
        prop: 'orderWay',
        label: this.$t('bidMod.quoteRule'),
        minWidth: 120,
        dataType: 'dict',
        code: 'SOU_ORDER_WAY'
      },
      // t 轮次
      {
        prop: 'currentRound',
        label: this.$t('bidMod.bidingRound'),
        minWidth: 80
      },
      // t 报价回应
      {
        prop: 'quoteCnt',
        label: this.$t('bidMod.quoteCnt'),
        minWidth: 120,
        formattor: (_val, row) => (row.orderCount || 0) + ' / ' + (row.inviteCount || 0)
      },
      // t 创建人
      {
        prop: 'createdUserName',
        label: this.$t('bidMod.creator'),
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
          },
          // b 复制询价单
          {
            // 除了已取消、拟定状态
            show: row => judgeCopy(row),
            formattor: () => this.$t('bidMod.copyInquiry'),
            callback: row => this.copyInquiry(row)
          }
        ]
      }
    ]

    this.$nextTick(() => {
      this.getQueryData()
    })
  },

  mounted () {
    if (this.$route.params.status) {
      this.showInquiryListDetail(this.$route.params.projectId, this.$route.params.inquiryNumber)
    }
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
