<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        @getFormData="getQueryData"
      />
      <MainHeader>
        <template slot="left">
          <AuthorityButton
            type="primary"
            code="inq:centralizedPurchaseList:add"
            @click="edit('add')"
          >
            {{ $t("common.add") }}
          </AuthorityButton>
        </template>
      </MainHeader>
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
  </el-container>
</template>

<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import InquiryDetail from './inquiry-detail'
import purInqApi from 'modcb@/centralizedPurchase/api'
import { tabTodoWatch } from '@/utils/mixins'
import {
  judgeApproval,
  judgeCancel,
  judgeCopy,
  judgeDelete,
  judgeEdit,
  judgeManage,
  judgeView
} from 'lib@/composition/inquiry/utils'
import inquiryTrackingDetail from './inquiryTrackingDetail.vue'
export default {
  name: 'InquiryList',
  components: {
    TableView,
    FormWrapper,
    MainHeader
  },
  mixins: [tabTodoWatch],
  data () {
    return {
      preArr: [],
      tableViewUrl: purInqApi.list.pageList,
      tableHeader: [],
      queryParams: {}
    }
  },
  created () {
    this.preArr = [
      {
        prop: 'designProjectCode',
        label: this.$t('cusEntry.centralizedPurchase.projectNo')
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
      },
      {
        prop: 'creationDate',
        label: this.$t('bidMod.creationDate'),
        type: 'date'
      },
      // 询价状态
      {
        prop: 'extProjectStatus',
        label: this.$t('bidMod.inquiryStatus'),
        type: 'dict',
        code: 'EXT_INQ_SOU_PROJECT_STATUS'
      }
    ]
    this.tableHeader = [
      // t 询价单号
      {
        prop: 'souNo',
        label: this.$t('bidMod.inquiryNo'),
        minWidth: 120,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.edit('view', row)
      },
      {
        prop: 'designProjectCode',
        label: this.$t('cusEntry.centralizedPurchase.projectNo'),
        minWidth: 120
      },
      {
        prop: 'designProjectName',
        label: this.$t('cusEntry.centralizedPurchase.projectName'),
        minWidth: 120
      },
      // t 轮次
      {
        prop: 'currentRound',
        label: this.$t('bidMod.bidingRound'),
        minWidth: 80
      },
      {
        prop: 'adjustCode',
        label: this.$t('cusEntry.centralizedPurchase.adjustPriceApplyNo'),
        minWidth: 120
      },
      {
        prop: 'adjustName',
        label: this.$t('cusEntry.centralizedPurchase.adjustPriceApplyName'),
        minWidth: 120
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
        minWidth: 150
      },
      {
        prop: 'extProjectStatus',
        label: this.$t('bidMod.inquiryStatus'),
        minWidth: 120,
        dataType: 'dict',
        code: 'EXT_INQ_SOU_PROJECT_STATUS'
      },
      {
        prop: 'designProjMoney',
        label: this.$t('cusEntry.centralizedPurchase.projectAmount'),
        minWidth: 130
      },
      {
        prop: 'orgDeptName',
        label: this.$t('cusEntry.centralizedPurchase.createCompany'),
        minWidth: 120
      },
      {
        prop: 'operation',
        label: this.$t('common.operation'),
        width: 100,
        fixed: 'right',
        showType: 'buttons',
        btnStyle: 'text',
        buttons: [
          {
            // 单据状态拟定，审核状态拟定
            show: row => judgeEdit(row),
            callback: row => this.edit('edit', row),
            formattor: () => this.$t('common.edit'),
            code: 'inq:centralizedPurchaseList:edit'
          },
          {
            show: row => judgeDelete(row),
            callback: row => this.delete(row),
            formattor: () => this.$t('common.delete'),
            code: 'inq:centralizedPurchaseList:delete'
          },
          {
            show: row => judgeManage(row),
            formattor: () => this.$t('bidMod.inquiryManage'),
            callback: row => this.openDetailTab('manage', row)
          }
        ]
      }
    ]
  },
  methods: {
    /* 列表查询 */
    getQueryData (params) {
      this.queryParams = params
      this.$nextTick(() => {
        this.$refs.tableList.query()
      })
    },
    /* 编辑 */
    edit (type, row = {}) {
      const name = type === 'add' ? 'inquiryDetail' : `inquiryDetail${row.souNo}`
      const title = type === 'add' ? this.$t('cusEntry.centralizedPurchase.addInquiry') : row.souNo
      this.$emit('tab-add', {
        name,
        component: InquiryDetail,
        params: {
          type,
          tabName: name,
          row,
          readOnly: !['add', 'edit'].includes(type)
        },
        title
      })
    },
    /* 删除 */
    delete (row) {
      purInqApi.list.delete(row.projectId).then(res => {
        this.$message.success(this.$t('common.successDelete'))
        this.getQueryData(this.queryParams)
      })
    },
    /* 打开详情页签 */
    openDetailTab (type, row = {}) {
      const map = new Map([
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
    }
  }
}
</script>
