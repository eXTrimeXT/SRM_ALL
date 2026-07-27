<template>
  <el-container class="flex-container mouldheader_list_wrapper" direction="vertical">
    <el-main>
      <FormWrapper
        :form-array="filterConfig"
        @getFormData="getQueryData"
        @synchronous-value="syncFilterParams"
      />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <AuthorityButton type="primary" code="marketLink:partsPrice:add" @click="editHandle({},'add')">
            {{ $t('common.add') }}
          </AuthorityButton>
          <AuthorityButton type="ghost" code="marketLink:partsPrice:linkPrice" @click="showLinkPrice">
            {{ $t('marketBudget.showLinkPrice') }}
          </AuthorityButton>
          <!-- <ListFlowBtn
            ref="workflowButtonSUBMIT"
            type="default"
            button-name="提交审批"
            businessType="MARKET_LINK_CALCULATION"
            :getOrderData="getFlowOrderData"
            @click-handler="submitApprove"
          /> -->
        </template>
      </MainHeader>

      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        url="/api-cost/api-ql/CostLinkMaterialPrice/query"
        :checkChange="handleCurrentChange"
        :open-custom-table="true"
        :adeptMeiQl="true"
        :checkbox="true"
        :comActive="$attrs['changeTab']"
      />
      <srm-dialog
        :title="$t('计算任务查看')"
        :visible.sync="dialogVisible"
        :close-on-click-modal="false"
        size="large"
      >
        <TableView
          :ref="'linkPriceTable'"
          style="height: 400px;"
          :table-header="tableHeader2"
          :pre-query-data="queryParam2"
          :adeptMeiQl="true"
          url="/api-cost/api-ql/CostLinkPrice/query"
          :open-custom-table="false"
          :checkbox="false"
          :comActive="$attrs['changeTab']"
        />

        <template #footer>
          <el-button @click="dialogVisible = false">
            {{ $t('common.close') }}
          </el-button>
        </template>
      </srm-dialog>
    </el-main>
  </el-container>
</template>

<script>
// import ListFlowBtn from 'lib@/components/c-workflow-button/ListFlowBtn'
import { tabTodoMixin, tabTodoWatch } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MImport from 'lib@/components/import'
// import ExportExcel from 'lib@/components/export-excel'
// import { parseTime } from '@/utils'
// import { downloadFileLink } from 'lib@/utils/file'
import marketPartsPriceDetail from './edit'
import marketPartsPriceReport from './report'
import { transformMQL } from '@/library/utils/util'

export default {
  name: 'MarketPartsPriceList',

  components: {
    TableView,
    MainHeader,
    MImport,
    // ExportExcel,
    FormWrapper
    // ListFlowBtn,
  },

  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    const _this = this
    return {
      dialogVisible: false,
      queryParam2: {
        'type': 'CostLinkPrice',
        'lang': 'zh-cn',
        'query': {
          '*': {}
        },
        'payload': {
          'filter': {},
          'page': {
            'sort': 'lastUpdateDate desc'
          }
        },
        'action': 'query',
        tree: true
      },
      tableHeader2: [
        {
          prop: 'linkPriceName',
          label: '任务名称',
          minWidth: 150,
          showType: 'button',
          btnStyle: 'text',
          callback: row => this.editHandle(row, row.executeStatus === 'DRAFT' ? 'edit' : 'view')
        },
        {
          prop: 'computeStartTime',
          label: '执行时间',
          width: 120
        },
        {
          prop: 'createdFullName',
          label: '执行人',
          minWidth: 150
        },
        {
          prop: 'executeStatus',
          label: '状态',
          minWidth: 150,
          formattor: val => this.$getDictLabel('COST_LINK_PRICE_EXECUTE_STATUS', val)
        },
        {
          prop: 'executeMessage',
          label: '执行结果',
          minWidth: 150
        },
        {
          prop: 'operation',
          label: this.$t('common.operation'),
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          width: 130,
          buttons: [
            {
              callback: row => this.computeLinkPrice(row),
              show: row => row.executeStatus === 'FAILURE',
              formattor: () => {
                return this.$t('marketBudget.retry')
              }
            }
          ]
        }

      ],
      dictCodes: {
        marketType: 'COST_LINK_MARKET_TYPE',
        scene: 'BUDGET_BNS_TYPE',
        countType: 'BID_MARKET_COUNT_TYPE',
        priceType: 'BID_MARKET_BUDGET_PRICE_TYPE',
        status: 'BID_MARKET_BUDGET_STATUS'
      },
      filterParams: {},
      extraData: {
        sourceType: 'WEB_APP',
        uploadType: 'FASTDFS',
        fileModular: 'base',
        fileFunction: 'demoorder',
        fileType: 'excel'
      },
      currentRows: [],
      gridId: 'list',
      tableHeader: [],
      filterConfig: [
        // 物料编码
        { prop: 'materialCode', label: '物料编码' },
        // 生效时间
        { prop: 'effectiveTime', label: '生效时间', type: 'daterange' },
        // 截止时间
        { prop: 'expiredTime', label: '截止时间', type: 'daterange' },
        // 联动行情单号
        { prop: 'linkPriceNo', label: '联动行情单号' },
        // 生效状态
        {
          prop: 'status',
          label: '状态',
          type: 'dict',
          code: 'COST_LINK_MATERIAL_PRICE_STATUS'
        }
        // 审批状态
        // { prop: 'status', label: '审批状态', type: 'dict', code: 'COST_LINK_MARKET_TYPE' },
      ],
      queryParam: {},
      userInfo: this.$store.getters.userInfo
    }
  },

  watch: {
    $route: {
      handler (val) {
        if (val) {
          let { from, row } = val.params || {}
          if (from === 'fromMeetManage') {
            this.operationFn(row)
          }
        }
      },
      immediate: true,
      deep: true
    }
  },

  created () {
    this.tableHeader = [
      // 联动行情单号
      {
        prop: 'linkPriceNo',
        label: '联动行情单号',
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.editHandle(row, 'view')
      },
      // 库存组织
      {
        prop: 'orgInvName',
        label: '库存组织名称',
        minWidth: 150
      },
      // 物料编码
      {
        prop: 'materialCode',
        label: '物料编码',
        minWidth: 150
      },
      // 物料名称
      {
        prop: 'materialName',
        label: '物料名称',
        minWidth: 150
      },

      {
        prop: 'vendorCode',
        label: '供应商编码',
        minWidth: 150
      },
      // 供应商名称
      {
        prop: 'vendorName',
        label: '供应商名称',
        minWidth: 150
      },
      // 价格目录最新价
      {
        prop: 'notaxLatestPlPrice',
        label: '价格目录最新价(未税)',
        minWidth: 150
      },
      // 币种
      {
        prop: 'currencyCode',
        label: '币种',
        minWidth: 150,
        formattor: val => this.$getDictLabel('currency', val)
      },
      // 系统计算变化价
      {
        prop: 'notaxSpreadPrice',
        label: '系统计算变化价',
        minWidth: 150
      },
      {
        prop: 'notaxLinkPrice',
        label: '联动后价格',
        minWidth: 150
      },
      {
        prop: 'linkDetail',
        label: '联动详情',
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.operationFn(row),
        formattor: (val) => {
          return this.$t('common.view') // "查看"
        }
      },
      // 单位
      {
        prop: 'unit',
        label: '单位',
        minWidth: 150,
        formattor: val => this.$getDictLabel('unit', val)
      },
      // 状态
      {
        prop: 'status',
        label: '状态',
        minWidth: 150,
        formattor: val => this.$getDictLabel('COST_LINK_MATERIAL_PRICE_STATUS', val)
      },

      // 生效时间
      {
        prop: 'effectiveTime',
        label: '生效时间',
        minWidth: 150
      },
      // 截止时间
      {
        prop: 'expiredTime',
        label: '截止时间',
        minWidth: 150
      },

      {
        prop: 'operation',
        label: this.$t('common.operation'),
        showType: 'buttons',
        btnStyle: 'text',
        fixed: 'right',
        width: 130,
        buttons: [
          // 编辑
          {
            callback: row => this.editHandle(row, 'edit'),
            show: row => row.status === 'DRAFT',
            formattor: () => {
              return this.$t('common.edit')
            }
          },
          {
            callback: row => this.tabAddApproval(row),
            show: row => row.status === 'DRAFT',
            formattor: () => {
              return this.$t('提交审批')
            }
          }
        ]
      }
    ]

    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQueryData()
    })
  },

  methods: {
    showLinkPrice () {
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs['linkPriceTable'].query()
      })
    },
    syncFilterParams (values) {
      this.filterParams = values
    },
    handleCurrentChange (val) {
      this.currentRows = val
    },

    editHandle (row, type) {
      let name =
        type === 'add'
          ? this.$t('新增市况联动计算')
          : this.$t('市况联动计算') + row.linkPriceNo
      const tab = {
        component: marketPartsPriceDetail,
        params: {
          row,
          flag: type,
          tabName: name,
          getQueryData: this.getQueryData
        },
        title: name,
        name
      }
      this.$emit('tab-add', tab)
      this.dialogVisible = false
    },

    operationFn (row, type) {
      const name = '联动详情' + row.linkPriceNo
      const tab = {
        component: marketPartsPriceReport,
        params: {
          row,
          flag: type,
          tabName: name,
          getQuerydata: this.getQuerydata
        },
        title: name,
        name: name + Date.now()
      }
      this.$emit('tab-add', tab)
    },

    deleteHandle (row) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        //
      })
    },

    getQueryData (params = {}) {
      // this.queryParam = JSON.parse(JSON.stringify(params || {}))
      let { effectiveTime = [], expiredTime = [] } = JSON.parse(JSON.stringify(params || {}))
      // if (effectiveTime && effectiveTime.length) {
      //   this.queryParam.effectiveTimeFrom = effectiveTime[0]
      //   this.queryParam.effectiveTimeTo = effectiveTime[1]
      // }
      // delete this.queryParam.effectiveTime
      // if (expiredTime && expiredTime.length) {
      //   this.queryParam.expiredTimeFrom = expiredTime[0]
      //   this.queryParam.expiredTimeTo = expiredTime[1]
      // }
      // delete this.queryParam.expiredTime
      let filter = {
        materialCode: { 'eq': params.materialCode },
        linkPriceNo: { 'eq': params.linkPriceNo },
        status: { 'eq': params.status }
      }
      if (effectiveTime && effectiveTime.length) {
        filter.effectiveTime = {
          'between': [effectiveTime[0], effectiveTime[1]]
        }
      }
      if (expiredTime && expiredTime.length) {
        filter.expiredTime = {
          'between': [expiredTime[0], expiredTime[1]]
        }
      }
      this.queryParam = transformMQL.listGetData('CostLinkMaterialPrice', params, 'lastUpdateDate', undefined, 'query', filter)

      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    getFlowOrderData () {
      //
      let selectData = this.currentRows.filter(item =>
        ['REJECTED', 'DRAFT', 'WITHDRAW'].includes(item.status),
      )
      let orderId = selectData.map(i => i.resultId)
      if (selectData.length > 0) {
        return {
          businessData: selectData, //
          fileuploadIds: [], //
          businessType: 'MARKET_LINK_CALCULATION',
          businessIds: orderId
        }
      } else {
        return this.$message.warning('请选择单据！')
      }
    },

    computeLinkPrice ({ linkPriceId }) {
      let payload = [
        { linkPriceId: linkPriceId }
      ]
      let formData = transformMQL.save('CostLinkPrice', payload, 'computeLinkPrice')
      linkPriceId &&
        this.$http({
          url: '/api-cost/api-ql/CostLinkPrice/computeLinkPrice',
          method: 'POST',
          data: formData,
          loading: true
        }).then(res => {
          if (res.code === '0') {
            this.$message.success(this.$t('common.success'))
            this.$refs['linkPriceTable'].query()
          }
        })
    }
  }
}
</script>
