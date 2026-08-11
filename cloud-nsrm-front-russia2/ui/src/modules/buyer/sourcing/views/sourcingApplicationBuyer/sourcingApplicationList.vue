<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <FormWrapper
        ref="formRef"
        :form-array="preArr"
        :init-active="true"
        @getFormData="getQuerydata"
      />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <AuthorityButton
            code="bu:sourcingApplicationList:add"
            type="primary"
            @click="addOne"
          >
            {{ $t("common.add") }}
          </AuthorityButton>
        </template>
      </MainHeader>

      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-inq/inq/reqhead/listPage"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import TableView from 'lib@/components/Table/TableView'
import OrganizationSelector from 'lib@/components/organization-selector'
import sourcingApplicationDetail from './sourcingApplicationDetail'
import vendorSignUp from './vendorSignUp'
import { sourcing } from 'modb@/sourcing/api'
import { daterangePayloadFormat } from 'lib@/composition/commonComposition'

export default {
  name: 'SourcingApplicationList',
  components: {
    FormWrapper,
    MainHeader,
    TableView,
    OrganizationSelector
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      pageSize: 15,
      gridId: 'list',
      tableHeader: [],
      tableData: [],
      preArr: [
        // 需求标题
        {
          prop: 'souReqTitile',
          label: this.$t('sourcingBuyer.souReqTitile')
        },
        // 寻源单号
        {
          prop: 'reqHeadNo',
          label: this.$t('sourcingBuyer.reqHeadNo')
        },
        // 状态
        {
          prop: 'status',
          label: this.$t('sourcingBuyer.status'),
          type: 'dict',
          code: 'REQ_HEAD_STATUS'
        },
        // 创建人
        {
          prop: 'createdBy',
          label: this.$t('sourcingBuyer.createdFullName'),
          type: 'quicksearch',
          propKey: 'username',
          showKey: 'nickname',
          name: 'scc_rbac_user_display'
        },
        // 创建时间
        {
          prop: 'dateList',
          label: this.$t('sourcingBuyer.creationDate'),
          type: 'daterange'
        },
        // 审批状态
        {
          prop: 'auditStatus',
          label: this.$t('sourcingBuyer.auditStatus'),
          type: 'dict',
          code: 'APPROVE_STATUS'
        }
      ],
      queryParam: {}
    }
  },
  created () {
    this.tableHeader = [
      // 寻源单号
      {
        prop: 'reqHeadNo',
        label: this.$t('sourcingBuyer.reqHeadNo'),
        width: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.readOne(row)
      },
      // 需求标题
      {
        prop: 'souReqTitile',
        label: this.$t('sourcingBuyer.souReqTitile')
      },
      // 状态
      {
        prop: 'status',
        label: this.$t('sourcingBuyer.status'),
        width: 120,
        dataType: 'dict',
        code: 'REQ_HEAD_STATUS'
      },
      // 审批状态
      {
        prop: 'auditStatus',
        label: this.$t('sourcingBuyer.auditStatus'),
        width: 120,
        dataType: 'dict',
        code: 'APPROVE_STATUS'
      },
      // 截止时间
      {
        prop: 'expirationTime',
        label: this.$t('sourcingBuyer.expirationTime'),
        width: 160,
        dataType: 'dateTime'
      },
      // 创建人
      {
        prop: 'createdFullName',
        label: this.$t('sourcingBuyer.createdFullName'),
        width: 100
      },
      // 创建时间
      {
        prop: 'creationDate',
        label: this.$t('sourcingBuyer.creationDate'),
        width: 160,
        dataType: 'dateTime'
      },
      {
        prop: 'operation',
        label: this.$t('common.operation'),
        width: 180,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          // 编辑
          {
            callback: row => this.editOne(row),
            formattor: () => this.$t('common.edit'),
            code: 'bu:sourcingApplicationList:edit',
            show: row => row.status === 'DRAFT'
          },
          // 删除
          {
            callback: row => this.deleteOne(row),
            formattor: () => this.$t('common.delete'),
            code: 'bu:sourcingApplicationList:delete',
            show: row => row.status === 'DRAFT'
          },
          // 查看报名
          {
            callback: row => this.viewSignUp(row),
            formattor: () => this.$t('sourcingBuyer.viewApply'),
            code: 'bu:sourcingApplicationList:viewApply',
            show: row =>
              ['PUBLISHED', 'SELECTED', 'SCORING', 'CLOSED', 'CANCEL'].includes(row.status)
          },
          // 截止报名
          {
            callback: row => this.stopOne(row),
            formattor: () => this.$t('sourcingBuyer.closeApply'),
            code: 'bu:sourcingApplicationList:closeApply',
            show: row => row.status === 'PUBLISHED'
          },
          // 作废
          {
            callback: row => this.abandonOne(row),
            formattor: () => this.$t('common.cancelled'),
            code: 'bu:sourcingApplicationList:cancelled',
            show: row => ['PUBLISHED', 'SELECTED', 'SCORING'].includes(row.status)
          },
          // 审批
          {
            callback: row => this.viewSignUp(row),
            formattor: () => this.$t('common.approve'),
            code: 'bu:sourcingApplicationList:approve',
            show: row => row.status === 'SUBMITTED'
          }
        ]
      }
    ]
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    getQuerydata (payload) {
      if (payload) {
        // 格式化时间范围
        payload = daterangePayloadFormat(payload, [
          // 创建时间
          { prop: 'dateList', fromProp: 'beginCreationDate', toProp: 'endCreationDate' }
        ])
      }

      this.queryParam = payload || this.queryParam
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    addOne () {
      this.$emit('tab-add', {
        component: sourcingApplicationDetail,
        params: {
          flag: 'add',
          tabName: 'sourcingApplicationDetail'
        },
        // 新增寻源需求
        title: this.$t('sourcingBuyer.addSourcing'),
        name: 'sourcingApplicationDetail'
      })
    },
    editOne (row) {
      this.$emit('tab-add', {
        component: sourcingApplicationDetail,
        params: {
          flag: 'edit',
          row: row,
          tabName: 'sourcingApplicationDetail' + row.reqHeadNo
        },
        title: row.reqHeadNo,
        name: 'sourcingApplicationDetail' + row.reqHeadNo
      })
    },
    readOne (row) {
      this.$emit('tab-add', {
        component: sourcingApplicationDetail,
        params: {
          flag: 'view',
          row: row,
          showType: 'readOnly',
          tabName: 'sourcingApplicationDetail' + row.reqHeadNo
        },
        title: row.reqHeadNo,
        name: 'sourcingApplicationDetail' + row.reqHeadNo
      })
    },
    deleteOne (row) {
      // 当前操将永久删除此数据，确认删除此数据
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(async () => {
          await sourcing.deleteSourcing(row.reqHeadId)
          // 删除成功
          this.$message.success(this.$t('common.successDelete'))
          this.$refs[this.gridId].query()
        })
        .catch(() => {})
    },
    viewSignUp (row) {
      this.$emit('tab-add', {
        component: vendorSignUp,
        params: {
          row: row,
          tabName: 'vendorSignUp' + row.reqHeadNo
        },
        title: row.reqHeadNo,
        name: 'vendorSignUp' + row.reqHeadNo
      })
    },
    stopOne ({ reqHeadId }) {
      // 确定要提前截止此单据信息？
      this.$confirm(this.$t('sourcingBuyer.confirmStop'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(async () => {
        await sourcing.stopSignUp(reqHeadId)
        this.$message.success(this.$t('sourcingBuyer.successStop'))
        this.$refs[this.gridId].query()
      })
    },
    abandonOne ({ reqHeadId }) {
      // 确定要作废此单据信息？
      this.$confirm(this.$t('sourcingBuyer.confirmAbandon'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(async () => {
         await sourcing.abandonSourcing(reqHeadId)
        this.$message.success(this.$t('sourcingBuyer.successAbandon'))
        this.$refs[this.gridId].query()
      })
    }
  }
}
</script>

<style lang="scss" scoped></style>
