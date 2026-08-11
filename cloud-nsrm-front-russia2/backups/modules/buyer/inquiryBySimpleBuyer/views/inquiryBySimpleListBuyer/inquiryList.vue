<template>
  <el-container class="flex-container the_inquiryList_wrapper" direction="vertical">
    <el-main>
      <FormWrapper
        :form-array="formWrapperArray"
        form-label-width="120px"
        @getFormData="getQueryData"
      />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <!--b 创建询价单-->
          <el-button type="primary" @click="editTab('add')">
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
        :com-active="$attrs['changeTab']"
        url="/api-inq/inquiry/header/listPage"
      />
    </el-main>
  </el-container>
</template>

<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import { parseTimeYMD } from 'lib@/composition/origin/composition'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import inquiryDetail from './inquiryBySimpleListBuyer/inquiryDetail'
import inquiryTrackingDetail from './inquiryBySimpleListBuyer/inquiryTrackingDetail'

export default {
  name: 'InquiryList',

  components: {
    TableView,
    MainHeader,
    FormWrapper
  },

  mixins: [tabTodoWatch, tabTodoMixin],

  data () {
    return {
      tableHeader: [],
      tableData: [],
      formWrapperArray: [
        // 询价标题
        {
          prop: 'inquiryTitle',
          label: this.$t('bidMod.inquiryTitle')
        },
        // 询价单号
        {
          prop: 'inquiryNo',
          label: this.$t('bidMod.inquiryNo')
        },
        // 询价状态
        {
          prop: 'status',
          label: this.$t('bidMod.inquiryStatus'),
          type: 'dict',
          code: 'RFQ_STATUS'
        },
        // 评分规则
        {
          prop: 'inquiryRule',
          label: this.$t('bidMod.inquiryRule'),
          type: 'dict',
          code: 'RFQ_SCORE_RULE'
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
          prop: 'auditStatus',
          label: this.$t('bidMod.auditStatus'),
          type: 'dict',
          code: 'APPROVE_STATUS'
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
        if (this.$route.params.from === 'fromFun' && this.$route.params.funName === 'inquiryBySimpleListBuyer') {
          const inquiryId = Number(this.$route.params.formId)
          const title = this.$route.params.formNo
          const row = {
            ...this.$route.params,
            inquiryId,
            inquiryNo: title // tab 标题显示
          }
          this.editTab('view', row)
        }
        if (this.$route.params.type === 'INQUIRY') {
          this.showInquiryListDetail(this.$route.params.inquiryId, this.$route.params.inquiryNumber)
        }
        if (this.$route.params.from === 'demandPoolManagement' && this.$route.params.funName === 'inquiryBySimpleListBuyer') {
          // 需求池跳转编辑
          this.editTab('edit', {
            inquiryId: this.$route.params.formId,
            inquiryNo: this.$route.params.formNo
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
        prop: 'inquiryNo',
        label: this.$t('bidMod.inquiryNo'),
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.editTab('view', row)
      },
      // t 询价标题
      {
        prop: 'inquiryTitle',
        label: this.$t('bidMod.inquiryTitle'),
        minWidth: 120
      },
      // t 评分规则
      {
        prop: 'inquiryRule',
        label: this.$t('bidMod.inquiryRule'),
        minWidth: 120,
        dataType: 'dict',
        code: 'RFQ_SCORE_RULE'
      },
      // t 单据状态
      {
        prop: 'status',
        label: this.$t('bidMod.inquiryStatus'),
        minWidth: 120,
        dataType: 'dict',
        code: 'RFQ_STATUS'
      },
      // 审核状态
      {
        prop: 'auditStatus',
        label: this.$t('bidMod.auditStatus'),
        minWidth: 120,
        dataType: 'dict',
        code: 'APPROVE_STATUS'
      },
      // t 报价方式
      {
        prop: 'quoteRule',
        label: this.$t('bidMod.quoteRule'),
        minWidth: 120,
        dataType: 'dict',
        code: 'RFQ_QUOTE_TYPE'
      },
      // t 轮次
      {
        prop: 'round',
        label: this.$t('bidMod.bidingRound'),
        minWidth: 80
      },
      // t 报价回应
      {
        prop: 'quoteCnt',
        label: this.$t('bidMod.quoteCnt'),
        minWidth: 120,
        formattor: (val, row) => (row.quoteCnt || 0) + ' / ' + (row.inviteCnt || 0)
      },
      // t 发起人
      {
        prop: 'createdUserName',
        label: this.$t('bidMod.createdBy'),
        minWidth: 120
      },
      // t 创建时间
      {
        prop: 'creationDate',
        label: this.$t('bidMod.creationDate'),
        minWidth: 140,
        formattor: val => parseTimeYMD(val)
      },
      // t 发布日期
      {
        prop: 'publishDate',
        label: this.$t('bidMod.publishDate'),
        minWidth: 100,
        formattor: val => parseTimeYMD(val)
      },
      // t 报价截止时间
      {
        prop: 'deadline',
        label: this.$t('bidMod.quotedeadline'),
        minWidth: 120,
        formattor: val => parseTimeYMD(val)
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
            show: row => row.status === 'DRAFT',
            formattor: () => this.$t('common.edit'),
            callback: row => this.editTab('edit', row)
          },
          // b 删除
          {
            show: row => row.status === 'DRAFT',
            formattor: () => this.$t('common.delete'),
            callback: row => this.delRowData(row)
          },
          // b 询价管理
          {
            show: row => !(['DRAFT', 'UNPUBLISH', 'CANCEL'].includes(row.status)),
            formattor: () => '询价管理',
            callback: row => this.inquiryManage(row)
          },
          // b 审批
          {
            // 未发布 && 非驳回 && 非已审批
            show: row => row.status === 'UNPUBLISH' && !['REJECTED', 'APPROVED'].includes(row.auditStatus),
            formattor: () => this.$t('common.approve'),
            callback: row => this.editTab('view', row)
          },
          // b 取消
          {
            // 拟定、已取消、已定价
            show: row => !(['DRAFT', 'UNPUBLISH', 'CANCEL', 'FIXED_PRICE'].includes(row.status)),
            formattor: () => this.$t('common.cancel'),
            callback: row => this.cancelProject(row)
          },
          // b 查看
          {
            show: row => row.status === 'CANCEL',
            formattor: () => this.$t('common.view'),
            callback: row => this.editTab('view', row)
          },
          // b 复制询价单
          {
            // 除了已取消、拟定状态
            show: row => !['CANCEL', 'DRAFT'].includes(row.status),
            formattor: () => '复制询价单',
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
      this.showInquiryListDetail(this.$route.params.inquiryId, this.$route.params.inquiryNumber)
    }
  },

  methods: {
    getQueryData (v) {
      this.queryParam = v || this.queryParam
      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },

    editTab (type, row) {
      let tab = {}
      switch (type) {
        case 'add':
          // 新增
          tab = {
            component: inquiryDetail,
            params: {
              flag: 'add',
              readOnly: false,
              tabName: 'inquiryDetail'
            },
            title: this.$t('inquiryBySimple.addInquiryBySimple'),
            name: 'inquiryDetail'
          }
          break
        case 'edit':
          // 修改 只有拟定状态的单据可以修改，否者只读模式
          tab = {
            component: inquiryDetail,
            params: {
              flag: 'edit',
              readOnly: false,
              row: row,
              tabName: `inquiryDetail${row.inquiryNo}`
            },
            title: row.inquiryNo,
            name: `inquiryDetail${row.inquiryNo}`
          }
          break
        case 'view':
          // 查看 审批
          tab = {
            component: inquiryDetail,
            params: {
              flag: 'view',
              readOnly: true,
              row: row,
              tabName: `inquiryDetail${row.inquiryNo}`
            },
            title: row.inquiryNo,
            name: `inquiryDetail${row.inquiryNo}`
          }
          break
        default:
      }
      this.$emit('tab-add', tab)
    },

    /* 删除 */
    delRowData (row) {
      this.$confirm(this.$t('common.delRow'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$api.inq.inquiryBySimple.inquiryDelete({ inquiryId: row.inquiryId }).then((data) => {
          if (data) {
            this.$message({ message: this.$t('common.successDelete'), type: 'success' })
            this.getQueryData()
          }
        })
      })
    },

    /* 询价管理 */
    inquiryManage (row) {
      const tab = {
        component: inquiryTrackingDetail,
        params: {
          flag: 'edit',
          row: row,
          tabName: `inquiryTrackingDetail${row.inquiryNo}`
        },
        title: row.inquiryNo,
        name: `inquiryTrackingDetail${row.inquiryNo}`
      }
      this.$emit('tab-add', tab)
    },

    /* 取消询价单 */
    cancelProject (row) {
      this.$confirm('确认取消项目吗？', {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$api.inq.inquiryBySimple.inquiryCancel({ inquiryId: row.inquiryId }).then((data) => {
          if (data) {
            this.$message({ message: '取消项目成功', type: 'success' })
            this.getQueryData()
          }
        })
      })
    },

    showInquiryListDetail (inquiryId, inquiryNumber) {
      const row = { inquiryId: inquiryId, inquiryNo: inquiryNumber }
      this.editTab('view', row)
    },

    /* 复制询价单 */
    copyInquiry (row) {
      this.$confirm('确认复制该询价单吗？', {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$api.inq.inquiryBySimple.inquiryCopy(row.inquiryId).then((data) => {
          const header = ((data || {}).data || {}).header
          if (header.inquiryId && header.inquiryNo) {
            this.$message.success(this.$t('logisticsMod.copySuccess'))
            // 更新列表
            this.getQueryData()
            // 进入详情页面，带上询价单号和ID
            this.editTab('edit', { inquiryNo: header.inquiryNo, inquiryId: header.inquiryId })
          } else {
            this.$message.error('无法获取复制的询价单')
          }
        })
      })
    }
  }
}
</script>
