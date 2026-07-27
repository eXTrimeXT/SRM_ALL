<template>
  <el-container class="flex-container black_list_wrapper" direction="vertical">
    <el-main>
      <FormWrapper
        :form-array="filterConfig"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />
      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <AuthorityButton type="primary" @click="orderEditHandle({},'add')">
            {{ $t('common.add') }}
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :checkbox="true"
        :open-custom-table="true"
        :adept-mei-ql="true"
        :com-active="$attrs['changeTab']"
        :source="biddingDocuments.createVendorRecommend"
        @afterQuery="afterQuery"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import ListFlowBtn from 'lib@/components/c-workflow-button/ListFlowBtn'
import blackEdit from './edit.vue'
import listApprove from './listApprove.vue'
import { downloadFileLink } from 'lib@/utils/file'
import ExportExcel from 'lib@/components/export-excel'
import { biddingDocuments } from 'modc@/buyer/purchasingDemand/api'
import { transformMQL } from '@/library/utils/util'

export default {
  name: 'BlackList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel,
    ListFlowBtn
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      biddingDocuments: biddingDocuments,
      name: 'blackList',
      tableName: 'blackTable',
      pageSize: 15,
      gridId: 'list',
      currentRows: [],
      flowOrderData: {},
      dictCodes: {

      },
      filterParams: {},
      tableHeader: [],

      filterConfig: [
        { prop: 'projectName', label: this.$t('bidMod.bidingName') },
        // 招标资料递交单号
        { prop: 'dataSubmitNo', label: this.$t('cusEntry.supplement20250121.bidSubmitNo') },
        {
          prop: 'status',
          label: this.$t('vendorMod.relegation.documentStatus'),
          width: 180,
          type: 'dict',
          code: 'BID_DATA_SUBMIT_STATUS'
        },
        {
          prop: 'createdFullName',
          label: this.$t('common.creator')
        },
        {
          prop: 'dateList2',
          width: 180,
          label: this.$t('bidMod.publishDate'),
          type: 'daterange'
        },
        {
          prop: 'dateList',
          width: 180,
          label: this.$t('common.creationDate'),
          type: 'daterange'
        }
      ],
      queryParam: {},
      curUserId: this.$store.getters.userInfo.userId,
      integrationMode: '' // 工作流模式
    }
  },
  computed: {
    username () {
      return this.$store.getters.userInfo.username || ''
    }
  },
  created () {
    let _this = this
    this.tableHeader = [
      // 招标资料递交单号
      {
        prop: 'dataSubmitNo',
        label: this.$t('cusEntry.supplement20250121.bidSubmitNo'),
        width: 140,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.orderEditHandle(row, 'view')
        }.bind(this)
      },
      // 板块
      {
        prop: 'orgBuName',
        label: this.$t('cusEntry.bidSuperviseReport.extOrgBuName'),
        width: 150
      },
      // 公司
      {
        prop: 'orgName',
        label: this.$t('cusEntry.bidMod.companyName'),
        width: 150
      },
      {
        prop: 'projectName',
        label: this.$t('bidMod.bidsProjectName'),
        width: 100
      },
      // 需求来源
      {
        prop: 'sourceFrom',
        label: this.$t('cusEntry.supplement20250121.sourceFrom'),
        dataType: 'dict',
        code: 'PR_SOU_REQUIREMENT_FROM',
        minWidth: 120
      },
      // 规模数量
      {
        prop: 'requireQuantity',
        label: this.$t('cusEntry.bidMod.scaleQuantity'),
        width: 100
      },
      {
        prop: 'status',
        label: this.$t('orderMod.deliveryNoteStatus'),
        dataType: 'dict',
        code: 'BID_DATA_SUBMIT_STATUS',
        minWidth: 120
      },
      {
        prop: 'createdFullName',
        label: this.$t('common.creator')
      },
      {
        prop: 'creationDate',
        label: this.$t('common.creationDate'),
        width: 100,
        dataType: 'dateTime'
      },
      {
        prop: 'publishTime',
        label: this.$t('dataConfMod.publishTime'),
        width: 100,
        dataType: 'dateTime'
      },
      {
        prop: 'operation',
        label: this.$t('components.headers.operation'),
        showType: 'buttons',
        btnStyle: 'text',
        fixed: 'right',
        width: 100,
        buttons: [
          {
            callback: row => this.orderEditHandle(row, 'edit'),
            // 拟定、驳回、撤回 显示编辑按钮
            show: row => ['DRAFT', 'REJECTED', 'WITHDRAW'].includes(row.status) && this.curUserId == row.createdId,
            formattor: () => {
              return this.$t('common.edit')
            }
          },
          {
            callback: row => this.deleteHandle(row),
            show: row => row.status === 'DRAFT' && this.curUserId == row.createdId,
            formattor: () => {
              return this.$t('common.delete')
            }
          },
          // 审批
          {
            callback: row => this.approvalOne(row),
            show: row => ['APPROVING'].includes(row.status) && (this.curUserId == row.createdId || row.isApprover == 'Y'),
            formattor: () => this.$t('common.approve')
          }
        ]
      }
    ]
    this.defaultTableHeader = this.tableHeader
    this.getFlowIntegrationMode()
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },

  methods: {
    syncFilterParams (values) {
      this.filterParams = values
    },

    getQuerydata (obj) {
      const { dateList, dateList2, ...rest } = obj || this.queryParam
      const params = {}
      if (dateList) {
        params.creationDate = [dateList[0], dateList[1]]
      }
      if (dateList2) {
        params.publishTime = [dateList2[0], dateList2[1]]
      }
      this.queryParam = transformMQL.listPageData({
        type: 'SubmitBuyer',
        action: 'query',
        params: { ...rest, ...params },
        filterOperator: {
          creationDate: 'between',
          publishTime: 'between'
        }
      })
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },

    deleteHandle (row) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          const saveData = transformMQL.save('SubmitBuyer', [{ dataSubmitId: row.dataSubmitId }], 'delete')
          biddingDocuments.delete(saveData).then(res => {
            this.$message.success(this.$t('common.successDelete'))
            this.getQuerydata()
          })
        })
    },

    orderEditHandle (row, type) {
      let tabName = type == 'add' ? 'blackEdit' : 'blackEdit' + row.dataSubmitId
      const tab = {
        component: blackEdit,
        params: {
          row,
          flag: type,
          tabName: tabName,
          activeWorkflowTab: false,
          readOnly: type == 'view'
        },
        title: type == 'add' ? this.$t('vendorMod.addDocument') : row.dataSubmitNo,
        name: tabName
      }
      this.$emit('tab-add', tab)
    },

    async approvalPassOne (row) {
      this.$http({
        url: '/api-pj/external/bpm/callback',
        method: 'POST',
        data: {
          ControlState: 'end',
          businessId: row.dataSubmitId
        },
        loading: true
      }).then(res => {
        this.getQuerydata()
      })
    },

    async approvalOne (row) {
      this.$emit('tab-add', {
        component: blackEdit,
        params: {
          flag: 'edit',
          row: row,
          tabName: 'blackEdit' + row.dataSubmitNo,
          activeWorkflowTab: true,
          readOnly: true
        },
        title: row.dataSubmitNo,
        name: 'blackEdit' + row.dataSubmitNo
      })
      // }
    },

    handleCurrentChange (val) {
      this.currentRows = val
    },

    afterQuery () {
      this.$refs[this.gridId].setTableData(async tableData => {
        const res = await this.$api.base.flowAPI.queryTodo()
        let queryTodoList = res.data || []
        tableData.forEach(tableItem => {
          let obj = queryTodoList.find(todoItem => tableItem.dataSubmitId + '' === todoItem.businessId + '')
          if (obj) {
            this.$set(tableItem, 'isApprover', 'Y')
          } else {
            this.$set(tableItem, 'isApprover', 'N')
          }
        })
      })
    },

    async getFlowIntegrationMode () {
      const res = await this.$api.base.flowAPI.getFlowIntegrationMode({ businessType: 'bidDataSubmit' })
      if (res.data) {
        this.integrationMode = res.data
      }
    },
    beforeOpenHandle () {
      return true
    },
    // 获取提交单据信息
    getFlowOrderData () {
      // 数据选 拟定 驳回 撤回 的单据
      let selectData = this.currentRows.filter(item => ['REJECTED', 'DRAFT', 'WITHDRAW'].includes(item.status))
      let orderId = selectData.map(i => (i.dataSubmitId))
      if (selectData.length > 0) {
        return {
          businessData: selectData, // 选中的多个单据集合
          fileuploadIds: [], // 附件信息
          businessType: 'black',
          businessIds: orderId
        }
      } else {
        // 拟定、驳回、撤回的单据可提交，请选择可提交的单据！
        return this.$message.warning(this.$t('cusEntry.supplement20250121.promptTips16'))
      }
    },
    // 提交审批
    submitApprove (businessId) {
      console.log('businessId', businessId)
      if (this.srmFlowMode.includes(this.integrationMode)) {
        this.$message.success(this.$t('common.successSubmit'))
        this.getQuerydata()
      } else {
        this.$emit('tab-add', {
          component: listApprove,
          params: {
            flag: 'flowEdit',
            tabName: 'blackEdit' + businessId,
            activeWorkflowTab: true,
            businessId: businessId,
            readOnly: true
          },
          title: this.$t('returnGoodsBill.key13', { businessId: businessId }),
          name: 'blackEdit' + businessId
        })
      }
    }
  }
}
</script>
