<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="searchFormConfig"
        form-label-width="120px"
        @getFormData="getQueryData"
      />

      <TableView
        ref="list"
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :com-active="$attrs['changeTab']"
        open-custom-table
        :url="techExchangeSupApi.listPageUrl"
      />
    </el-main>
  </el-container>
</template>

<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import techExchangeManagementSupplierDetail from './techExchangeManagementSupplierDetail'
import techExchangeManagementSupplierFeedback from './techExchangeManagementSupplierFeedback'
import { techExchangeSupApi } from 'mods@/techExchangeSupplier/api'
export default {
  name: 'TechExchangeManagementSupplierList',

  components: {
    TableView,
    FormWrapper
  },

  mixins: [tabTodoWatch, tabTodoMixin],

  data () {
    return {
      techExchangeSupApi: techExchangeSupApi,
      tableData: [],
      tableHeader: [
        // 交流单号
        {
          prop: 'technicalExchangeFormCode',
          label: this.$t('techExchange.technicalExchangeFormCode'),
          width: 130,
          showType: 'button',
          btnStyle: 'text',
          callback: row => this.openDetailTab(row)
        },
        // 交流标题
        {
          prop: 'technicalExchangeTitle',
          label: this.$t('bidMod.communicateTitle'),
          minWidth: 130
        },
        // 业务实体
        {
          prop: 'orgOuName',
          label: this.$t('components.organization.ORG'),
          width: 130
        },
        // 交流类型
        {
          prop: 'technicalExchangeType',
          label: this.$t('bidMod.communicateType'),
          width: 130,
          formattor: val => this.$getDictLabel('TECHNICAL_EXCHANGE_TYPE', val)
        },
        // 单据状态
        {
          prop: 'technicalExchangeFormStatus',
          label: this.$t('vendorMod.relegation.documentStatus'),
          width: 130,
          formattor: val => this.$getDictLabel('TECHNICAL_EXCHANGE_FORM_STATUS', val)
        },
        // 反馈状态
        {
          prop: 'feedbackStatus',
          label: this.$t('dashboard.loopMode'),
          width: 130,
          formattor: val => this.$getDictLabel('TECHNICAL_EXCHANGE_FEEDBACK_STATUS', val)
        },
        // 创建人
        {
          prop: 'createdUserName',
          label: this.$t('common.creator'),
          width: 130
        },
        // 创建时间
        {
          prop: 'creationDate',
          label: this.$t('common.creationTime'),
          width: 140,
          dataType: 'dateTime'
        },
        // 发布时间
        {
          prop: 'technicalExchangeReleaseTime',
          label: this.$t('components.notice.publishTime'),
          width: 140,
          dataType: 'dateTime'
        },
        // 预计结束时间
        {
          prop: 'technicalExchangeEndTime',
          label: this.$t('bidMod.estimatedEndTime'),
          width: 140,
          dataType: 'dateTime'
        },
        {
          prop: 'operation',
          label: this.$t('bidMod.operation'),
          width: 90,
          showType: 'buttons',
          fixed: 'right',
          btnStyle: 'text',
          buttons: [
            // 反馈
            {
              // 已发布 && 非未反馈 && 未截止 && 开始时间小于当前时间
              show: row => row.technicalExchangeFormStatus === 'PUBLISHED' &&
                row.feedbackStatus !== 'FEEDBACK_ALREADY' &&
                new Date(row.technicalExchangeStartTime).getTime() < new Date().getTime() &&
                new Date(row.technicalExchangeEndTime).getTime() > new Date().getTime(),
              formattor: () => this.$t('vendorMod.retroaction'),
              callback: row => this.feedback(row)
            },
            // 撤回
            {
              // 已发布 && 已反馈 && 未截止
              show: row => row.technicalExchangeFormStatus === 'PUBLISHED' &&
                row.feedbackStatus === 'FEEDBACK_ALREADY' &&
                new Date(row.technicalExchangeEndTime).getTime() > new Date().getTime(),
              formattor: () => this.$t('components.approvalHead.headers.return'),
              callback: row => this.withdrawItem(row)
            }
          ]
        }
      ],
      queryParam: {},
      searchFormConfig: [
        // 交流标题
        { prop: 'technicalExchangeTitle', label: this.$t('bidMod.communicateTitle') },
        // 交流单号
        { prop: 'technicalExchangeFormCode', label: this.$t('techExchange.technicalExchangeFormCode') },
        // 单据状态
        {
          prop: 'technicalExchangeFormStatus',
          label: this.$t('techExchange.orderStatus'),
          type: 'dict',
          code: 'TECHNICAL_EXCHANGE_FORM_STATUS',
          transformOptions: (options) => options.filter(item => item.value !== 'DRAFT')
        },
        // 交流类型
        {
          prop: 'technicalExchangeType',
          label: this.$t('bidMod.communicateType'),
          type: 'dict',
          code: 'TECHNICAL_EXCHANGE_TYPE'
        },
        // 创建人
        {
          prop: 'createdBy',
          label: this.$t('common.creator'),
          type: 'quicksearch',
          showKey: 'username',
          name: 'scc_rbac_user_display'
        },
        // 业务实体
        {
          prop: 'orgOuId',
          label: () => this.$t('bid_mod.businessEntity'),
          type: 'OUorganizationSelector'
        }
      ]
    }
  },

  mounted () {
    this.$nextTick(() => {
      this.getQueryData()
    })
  },

  methods: {
    /* 查询 */
    getQueryData (payload) {
      this.queryParam = payload || this.queryParam
      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },

    /* 查看详情 */
    openDetailTab (row) {
      this.$emit('tab-add',
        {
          component: techExchangeManagementSupplierDetail,
          params: {
            flag: 'view',
            row,
            tabName: `techExchangeManagementSupplierDetail${row.technicalExchangeId}`
          },
          title: row.technicalExchangeFormCode,
          name: `techExchangeManagementSupplierDetail${row.technicalExchangeId}`
        }
      )
    },

    /* 反馈 */
    feedback (row) {
      this.$emit('tab-add',
        {
          component: techExchangeManagementSupplierFeedback,
          params: {
            flag: 'edit',
            row,
            tabName: `techExchangeManagementSupplierFeedback${row.technicalExchangeId}`
          },
          title: row.technicalExchangeFormCode,
          name: `techExchangeManagementSupplierFeedback${row.technicalExchangeId}`
        }
      )
    },

    /* 撤回 */
    async withdrawItem (row) {
      const confirm = await this.$confirm(this.$t('bidMod.confirmWithdrawDocument'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
      if (confirm !== 'confirm') {
        return
      }

      const response = await techExchangeSupApi.cancel(row.technicalExchangeId)
      if (response) {
        this.$message.success(this.$t('common.successSubmit'))
        this.getQueryData()
      }
    }
  }
}
</script>
