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

      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <!--b 新增-->
          <el-button
            type="primary"
            @click="openDetailTab('add')"
          >
            {{ $t("common.add") }}
          </el-button>
        </template>
      </MainHeader>

      <TableView
        ref="techExchangeManagementTable"
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :com-active="$attrs['changeTab']"
        open-custom-table
        custom-table-key="techExchangeManagementTable"
        :url="techExchangeBuyerApi.listPageUrl"
      />
    </el-main>
  </el-container>
</template>

<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import techExchangeManagementDetail from './techExchangeManagementDetail'
import { techExchangeBuyerApi } from 'modb@/techExchange/api'
export default {
  name: 'TechExchangeManagementList',

  components: {
    TableView,
    MainHeader,
    FormWrapper
  },

  mixins: [tabTodoWatch, tabTodoMixin],

  data () {
    return {
      techExchangeBuyerApi: techExchangeBuyerApi,
      tableData: [],
      tableHeader: [
        // 交流单号
        {
          prop: 'technicalExchangeFormCode',
          label: this.$t('techExchange.technicalExchangeFormCode'),
          minWidth: 130,
          showType: 'button',
          btnStyle: 'text',
          callback: row => this.openDetailTab('view', row)
        },
        // 交流标题
        {
          prop: 'technicalExchangeTitle',
          label: this.$t('bidMod.communicateTitle'),  // '交流标题'
          minWidth: 130
        },
        // 业务实体
        {
          prop: 'orgOuName',
          label: this.$t('bidMod.businessEntity'),
          minWidth: 130
        },
        // 交流类型
        {
          prop: 'technicalExchangeType',
          label: this.$t('bidMod.communicateType'),  // '交流类型'
          minWidth: 130,
          formattor: (val) => this.$getDictLabel('TECHNICAL_EXCHANGE_TYPE', val)
        },
        // 单据状态
        {
          prop: 'technicalExchangeFormStatus',
          label: this.$t('techExchange.orderStatus'),
          minWidth: 130,
          formattor: (val) => this.$getDictLabel('TECHNICAL_EXCHANGE_FORM_STATUS', val)
        },
        // 交流反馈
        {
          prop: 'feedbackRatio',
          label: this.$t('bidMod.exchangeFeedback'),  // '交流反馈'
          minWidth: 90
        },
        // 创建人
        {
          prop: 'createdUserName',
          label: this.$t('common.creator'),
          minWidth: 130
        },
        // 创建时间
        {
          prop: 'creationDate',
          label: this.$t('common.creationTime'),
          minWidth: 140,
          dataType: 'dateTime'
        },
        // 发布时间
        {
          prop: 'technicalExchangeReleaseTime',
          label: this.$t('bidMod.releaseDatetime'),
          minWidth: 140,
          dataType: 'dateTime'
        },
        // 预计结束时间
        {
          prop: 'technicalExchangeEndTime',
          label: this.$t('bidMod.estimatedEndTime'),  // '预计结束时间'
          minWidth: 140,
          dataType: 'dateTime'
        },
        {
          prop: 'operation',
          label: this.$t('bidMod.operation'),
          minWidth: 170,
          showType: 'buttons',
          fixed: 'right',
          btnStyle: 'text',
          buttons: [
            // 编辑
            {
              // 拟定
              show: row => row.technicalExchangeFormStatus === 'DRAFT',
              formattor: () => this.$t('common.edit'),
              callback: row => this.openDetailTab('edit', row)
            },
            // 删除
            {
              // 拟定
              show: row => row.technicalExchangeFormStatus === 'DRAFT',
              formattor: () => this.$t('common.delete'),
              callback: row => this.deleteItem(row)
            },
            // 查看反馈
            {
              // 已发布，已结束，已取消
              show: row => row.technicalExchangeFormStatus !== 'DRAFT',
              // '查看反馈'
              formattor: () => this.$t('bidMod.viewFeedback'),
              callback: row => this.openDetailTab('feedbackView', row)
            },
            // 结束反馈
            {
              // 已发布
              show: row => row.technicalExchangeFormStatus === 'PUBLISHED',
              // '结束反馈'
              formattor: () => this.$t('bidMod.endFeedback'),
              callback: row => this.closureItem(row)
            },
            // 取消
            {
              // 已发布
              show: row => row.technicalExchangeFormStatus === 'PUBLISHED',
              formattor: () => this.$t('common.cancel'),
              callback: row => this.cancelItem(row)
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
          code: 'TECHNICAL_EXCHANGE_FORM_STATUS'
        },
        // 交流类型
        {
          prop: 'technicalExchangeType',
          // '交流类型'
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
        this.$refs.techExchangeManagementTable.query()
      })
    },

    /* 新增add，查看view，编辑edit */
    openDetailTab (type, row) {
      let tab = {
        component: techExchangeManagementDetail,
        params: {
          flag: type,
          tabName: 'techExchangeManagementDetail'
        },
        title: '',
        name: 'techExchangeManagementDetail'
      }
      if (type === 'add') {
        // 新增
        tab.title = this.$t('common.add')
      } else {
        // 编辑 查看
        tab.params = {
          ...tab.params,
          row,
          tabName: `techExchangeManagementDetail${row.technicalExchangeFormCode}`
        }
        tab.title = row.technicalExchangeFormCode
        tab.name = tab.params.tabName
      }
      this.$emit('tab-add', tab)
    },

    /* 删除 */
    async deleteItem (row) {
      // '确定删除该单据？'
      const confirm = await this.$confirm(this.$t('bidMod.sureDeleteDocument'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
      if (confirm !== 'confirm') {
        return
      }

      const response = await techExchangeBuyerApi.delete(row.technicalExchangeId)
      if (response) {
        this.$message.success(this.$t('common.successDelete'))
        this.getQueryData()
      }
    },

    /* 取消 */
    async cancelItem (row) {
      // '确定取消该单据？'
      const confirm = await this.$confirm(this.$t('bidMod.sureCancelDocument'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
      if (confirm !== 'confirm') {
        return
      }

      const response = await techExchangeBuyerApi.cancel(row.technicalExchangeId)
      if (response) {
        // '取消成功！'
        this.$message.success(this.$t('bidMod.cancelSuccessfully'))
        this.getQueryData()
      }
    },

    /* 结束反馈 */
    async closureItem (row) {
      // '请确认是否立即结束供应商反馈！'
      const confirm = await this.$confirm(this.$t('bidMod.confirmEndSupplierFeedback'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
      if (confirm !== 'confirm') {
        return
      }

      const response = await techExchangeBuyerApi.closure(row.technicalExchangeId)
      if (response) {
        this.$message.success(this.$t('common.successUpdate'))
        this.getQueryData()
      }
    }
  }
}
</script>
