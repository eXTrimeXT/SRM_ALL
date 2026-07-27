<template>
  <el-container class="flex-container the_requirementMonitorReport_wrapper" direction="vertical">
    <el-main>
      <div class="status-list-block">
        <ul class="status-list">
          <li v-for="i in statusList" :key="i.value" :class="currentStatus === i.value ? 'status-active' : null" @click="getList(i.value)">
            {{ `${i.label}（${i.count}）` }}<i />
          </li>
        </ul>
      </div>
      <FormWrapper ref="formRef" :form-array="preArr" :init-active="true" @getFormData="getQuerydata" />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <ExportExcel
            v-loading
            page-url="/api-inq/inq/reqhead/souReqReportPage"
            :filter-params="queryParam"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            :title="$t('sourcingBuyer.exportData')"
            timeout="1000000"
            export-mode="front"
          />
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
        url="/api-inq/inq/reqhead/souReqReportPage"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import QuickSearch from 'lib@/components/QuickSearch'
import ExportExcel from 'lib@/components/export-excel'
import OrganizationSelector from 'lib@/components/organization-selector'
import CCategorySelect from 'lib@/components/c-category-select'
import sourcingApplicationDetail from '../sourcingApplicationBuyer/sourcingApplicationDetail'
import { sourcing } from 'modb@/sourcing/api'
import { daterangePayloadFormat } from 'lib@/composition/commonComposition'

export default {
  name: 'RequirementMonitorReportList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel,
    OrganizationSelector,
    CCategorySelect,
    QuickSearch
  },
  provide () {
    return { context: this }
  },
  data () {
    return {
      statusList: [{
        value: 'DRAFT',
        label: this.$t('sourcingBuyer.draft'),
        count: 0
      }, {
        value: 'PUBLISHED',
        label: this.$t('sourcingBuyer.published'),
        count: 0
      }, {
        value: 'SELECTED',
        label: this.$t('sourcingBuyer.selected'),
        count: 0
      }, {
        value: 'SCORING',
        label: this.$t('sourcingBuyer.scoring'),
        count: 0
      }, {
        value: 'SUBMITTED',
        label: this.$t('sourcingBuyer.submitted'),
        count: 0
      }, {
        value: 'CLOSED',
        label: this.$t('sourcingBuyer.closed'),
        count: 0
      }],
      currentStatus: 'DRAFT',
      dictCodes: {
        status: 'REQ_HEAD_STATUS',
        draft: 'REQ_HEAD_REPORT_STATUS',
        published: 'REQ_HEAD_REPORT_STATUS',
        selected: 'REQ_HEAD_REPORT_STATUS',
        scoring: 'REQ_HEAD_REPORT_STATUS',
        submitted: 'REQ_HEAD_REPORT_STATUS',
        closed: 'REQ_HEAD_REPORT_STATUS'
      },
      pageSize: 15,
      gridId: 'list',
      tableHeader: [],
      tableData: [],
      preArr: [
        // 业务实体
        {
          prop: 'orgId',
          label: this.$t('sourcingBuyer.orgName'),
          type: 'OUorganizationSelector'
        },
        // 库存组织
        {
          prop: 'organizationId',
          label: this.$t('sourcingBuyer.organizationName'),
          type: 'INVorganizationSelector',
          parentId: 'orgId'
        },
        // 寻源单号
        {
          prop: 'reqHeadNo',
          label: this.$t('sourcingBuyer.reqHeadNo')
        },
        // 物料品类
        {
          prop: 'categoryId',
          label: this.$t('sourcingBuyer.categoryType'),
          type: 'catSelect',
          showKey: 'categoryId'
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
        }
      ],
      queryParam: {}
    }
  },
  created () {
    this.tableHeader = [
      // 业务实体
      {
        prop: 'orgName',
        label: this.$t('sourcingBuyer.orgName'),
        width: 120
      },
      // 库存组织
      {
        prop: 'organizationName',
        label: this.$t('sourcingBuyer.organizationName'),
        width: 120
      },
      // 寻源单号
      {
        prop: 'reqHeadNo',
        label: this.$t('sourcingBuyer.reqHeadNo'),
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.readOne(row)
      },
      // 物料品类
      {
        prop: 'categoryName',
        label: this.$t('sourcingBuyer.categoryType'),
        width: 120
      },
      // 创建人
      {
        prop: 'createdFullName',
        label: this.$t('sourcingBuyer.createdFullName'),
        width: 120
      },
      // 创建时间
      {
        prop: 'creationDate',
        label: this.$t('sourcingBuyer.creationDate'),
        width: 120,
        dataType: 'dateTime'
      },
      // 单据状态
      {
        prop: 'status',
        label: this.$t('sourcingBuyer.sourcingStatus'),
        width: 120,
        dataType: 'dict',
        code: 'REQ_HEAD_STATUS'
      },
      // 拟定
      {
        prop: 'draft',
        label: this.$t('sourcingBuyer.draft'),
        width: 120,
        dataType: 'dict',
        code: 'REQ_HEAD_REPORT_STATUS'
      },
      // 已发布
      {
        prop: 'published',
        label: this.$t('sourcingBuyer.published'),
        width: 120,
        dataType: 'dict',
        code: 'REQ_HEAD_REPORT_STATUS'
      },
      // 入围筛选中
      {
        prop: 'selected',
        label: this.$t('sourcingBuyer.selected'),
        width: 120,
        dataType: 'dict',
        code: 'REQ_HEAD_REPORT_STATUS'
      },
      // 评分中
      {
        prop: 'scoring',
        label: this.$t('sourcingBuyer.scoring'),
        width: 120,
        dataType: 'dict',
        code: 'REQ_HEAD_REPORT_STATUS'
      },
      // 结果审批中
      {
        prop: 'submitted',
        label: this.$t('sourcingBuyer.submitted'),
        width: 120,
        dataType: 'dict',
        code: 'REQ_HEAD_REPORT_STATUS'
      },
      // 已关闭
      {
        prop: 'closed',
        label: this.$t('sourcingBuyer.closed'),
        width: 120,
        dataType: 'dict',
        code: 'REQ_HEAD_REPORT_STATUS'
      },
      // 总耗时
      {
        prop: 'cusDate',
        label: this.$t('sourcingBuyer.totalTime'),
        width: 120
      }
    ]
    this.$nextTick(() => {
      this.getReportCount()
      this.getQuerydata()
    })
  },
  methods: {
    async getReportCount () {
      let res = await sourcing.getReportCount()
      if (res.data) {
        this.statusList.forEach(item => {
          item.count = res.data[item.value]
        })
      }
    },
    getQuerydata (payload) {
      if (payload) {
        // 格式化时间范围
        payload = daterangePayloadFormat(payload, [
          // 创建时间
          { prop: 'dateList', fromProp: 'beginCreationDate', toProp: 'endCreationDate' }
        ])
      }

      this.queryParam = payload || this.queryParam
      this.queryParam.status = this.currentStatus
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    getList (status) {
      this.currentStatus = status
      this.queryParam.status = this.currentStatus
      this.$refs[this.gridId].query()
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
    }
  }
}
</script>

<style lang="scss" scoped>
@import "./style/mixin.scss";
.the_requirementMonitorReport_wrapper {
  @include arrowArocess();

}
</style>
