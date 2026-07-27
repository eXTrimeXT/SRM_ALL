<template>
  <el-container
    class="flex-container the_vendorPurchaseOrderList_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        form-label-width="100px"
        @getFormData="getQuerydata"
      />
      <TableView
        ref="tableList"
        big-data
        :table-header="tableHeader"
        :pre-query-data="queryParams"
        open-custom-table
        :com-active="$attrs['changeTab']"
        :auto-query="true"
        :url="tableViewUrl"
      />
    </el-main>
    <SrmDialog
      title="集采需知"
      :visible.sync="dialogVisible"
      :close-on-click-modal="false"
      append-to-body
      size="middle"
    >
      <div>
        <el-button
          style="margin-right:20px"
          @click="downloadTemplate"
        >
          附件
        </el-button>
        <el-checkbox v-model="isAgree">
          是否同意
        </el-checkbox>
      </div>
      <div
        slot="footer"
        class="dialog-footer"
      >
        <el-button @click="dialogVisible = false">
          {{ $t('common.cancel') }}
        </el-button>
        <el-button
          type="primary"
          :disabled="!isAgree"
          @click="handelEdit"
        >
          {{ $t('common.confirm') }}
        </el-button>
      </div>
    </SrmDialog>
  </el-container>
</template>
<script>
import { parseTime } from '@/utils'
import { tabTodoMixin, tabTodoWatch } from '@/utils/mixins'
import ExportExcel from 'lib@/components/export-excel'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import TableView from 'lib@/components/Table/TableView'
import { transformMQL } from 'lib@/utils/util'
import detail from './detail'
import quotationApi from 'modcb@/jcManagement/api'
import { judgeQuote, judgeRollback } from 'lib@/composition/inquiry/utils'
import { downloadFileLink } from 'lib@/utils/file'
export default {
  name: 'JcQuotationList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      isAgree: null,
      dialogVisible: false,
      integrationMode: '',
      gridId: 'list',
      currentRows: null,
      tableViewUrl: quotationApi.quotation.pageList,
      pageSize: 15,
      preArr: [],
      tableHeader: [
        {
          prop: 'souNo',
          label: '询价单号',
          width: 120,
          showType: 'button',
          btnStyle: 'text',
          callback: row => this.handelView(row)
        },
        {
          prop: 'projectStatus',
          label: '询价状态',
          width: 120,
          dataType: 'dict',
          code: 'EXT_INQ_SOU_PROJECT_STATUS'
        },
        {
          prop: 'orderNo',
          label: '报价单号',
          width: 150
        },
        {
          prop: 'orderStatus',
          label: '报价状态',
          dataType: 'dict',
          code: 'INQ_SOU_ORDER_STATUS',
          width: 120
        },
        {
          prop: 'orderEndTime',
          label: '报价截止时间',
          width: 150,
          formattor: val => (val ? parseTime(val, '{y}-{m}-{d} {h}:{i}:{s}') : '')
        },
        {
          prop: 'currentRound',
          label: '轮次',
          width: 120
        },
        {
          prop: 'createdBy',
          label: '创建人',
          width: 120
        },
        {
          prop: 'publishTime',
          label: '发布时间',
          width: 120,
          formattor: val => (val ? parseTime(val, '{y}-{m}-{d} {h}:{i}:{s}') : '')
        },
        {
          prop: 'operation',
          label: () => this.$t('common.operation'),
          width: 150,
          btnStyle: 'text',
          fixed: 'right',
          showType: 'buttons',
          buttons: [
            {
              show: row => judgeQuote(row),
              callback: row => this.openModal(row),
              formattor: () => '报价'
            },
            {
              // 已报价 && 接受报价中 && 允许撤回
              show: row => judgeRollback(row),
              callback: row => this.handelBack(row),
              formattor: () => this.$t('bidMod.withdraw')
            }
          ]
        }
      ],
      queryParams: {},
      selectRow: null
    }
  },
  created () {
    this.preArr = [
      {
        prop: 'souNo',
        label: '询价单号'
      },
      {
        prop: 'extProjectStatus',
        label: '询价阶段',
        type: 'dict',
        code: 'EXT_INQ_SOU_PROJECT_STATUS'
      },
      {
        prop: 'orderStatus',
        label: '报价状态',
        type: 'dict',
        code: 'INQ_SOU_ORDER_STATUS'
      },
      {
        prop: 'orderNo',
        label: '报价单号'
      }
    ]
  },
  mounted () {
  },
  methods: {
    downloadTemplate () {
      // this.$message.error('暂无附件')
      downloadFileLink(
        '/api-sou/npm/vendor/ext_pur_inq/order/getPurOrderNoticeFile',
        '集采需知.docx'
      ).catch(() => {
        this.$message.error(this.$t('purchaseDemand.downloadFail'))
      })
    },
    getQuerydata (params) {
      this.queryParams = params
      this.$nextTick(() => {
        this.$refs.tableList.query()
      })
    },
    async getFlowIntegrationMode () {
      let res = await this.$api.base.flowAPI.getFlowIntegrationMode({ businessType: 'ORDER' })
      if (res.data) {
        this.integrationMode = res.data
      }
    },
    /* 撤回 */
    async handelBack (row) {
      const promptResult = await this.$prompt(
        this.$t('bidMod.withdrawReason'),
        this.$t('bidMod.withdrawDesc'),
        {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel')
        }
      )

      if (!promptResult) {
        return
      }

      if (!promptResult.value) {
        this.$message.warning(this.$t('bidMod.withdrawTips1'))
        return
      }
      if (promptResult.value.length > 250) {
        this.$message.warning(this.$t('bidMod.withdrawTips2'))
        return
      }
      let userInfo = this.$store.getters.userInfo
      const response = await quotationApi.quotation.rollback({
        projectId: row.projectId,
        withdrawReason: promptResult.value,
        vendorId: userInfo.companyId
      })

      if (response) {
        this.$message.success(this.$t('common.successWithdraw'))
        this.getQuerydata()
      }
    },
    openModal (item) {
      this.selectRow = item
      this.dialogVisible = true
    },
    handelEdit () {
      this.dialogVisible = false
      this.isAgree = null
      this.$emit('tab-add', {
        component: detail,
        params: {
          flag: 'edit',
          tabName: 'detail',
          projectId: this.selectRow.projectId
        },
        title: `${this.selectRow.souNo}`,
        name: 'detail'
      })
    },
    handelView (item) {
      this.$emit('tab-add', {
        component: detail,
        params: {
          flag: 'view',
          tabName: 'detail',
          projectId: item.projectId
        },
        title: `${item.souNo}`,
        name: 'detail'
      })
    }
  }
}
</script>
<style scoped lang="scss">
.el-input-group__append >div{
    display: flex;
    justify-content: center;
}
</style>
