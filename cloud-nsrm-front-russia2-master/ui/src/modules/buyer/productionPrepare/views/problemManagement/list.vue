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
          <AuthorityButton v-if="userType === 'BUYER'" type="primary" @click="addOne">
            {{ $t("common.add") }}
          </AuthorityButton>
          <!-- 导出数据 -->
          <ExportExcel
            v-loading
            page-url="/api-ppap/problem/exchange/listPage"
            :filter-params="queryParam"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            :title="$t('qualitySynergy.exportData')"
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
        url="/api-ppap/problem/exchange/listPage"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import TableView from 'lib@/components/Table/TableView'
import ExportExcel from 'lib@/components/export-excel'
import OrganizationSelector from 'lib@/components/organization-selector'
import problemDetail from './edit'
import { problemManagement } from 'modb@/productionPrepare/api'
import { daterangePayloadFormat } from 'lib@/composition/commonComposition'

export default {
  name: 'ProblemList',
  components: {
    FormWrapper,
    MainHeader,
    TableView,
    OrganizationSelector,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      dictCodes: {
        orderStatus: 'PROBLEM_EXCHANGE_STATUS',
        status: 'PROBLEM_EXCHANGE_STAGE',
        problemExchangeStatus: 'PROBLEM_EXCHANGE_STATUS',
        problemExchangeRiskLevel: 'PROBLEM_EXCHANG_ERISK_LEVEL',
        problemExchangeStage: 'PROBLEM_EXCHANGE_STAGE'
      },
      pageSize: 15,
      gridId: 'list',
      tableHeader: [],
      tableData: [],
      preArrList: [
        // 问题单号
        {
          prop: 'problemExchangeCode',
          label: this.$t('problemManagement.problemExchangeCode')
        },
        // 物料名称
        {
          prop: 'itemName',
          label: this.$t('problemManagement.itemName'),
          type: 'quicksearch',
          showKey: 'materialName',
          name: 'scc_base_material_item'
        },
        // 车型名称
        {
          prop: 'motorcycleTypeName',
          label: this.$t('problemManagement.motorcycleTypeName')
        },
        // 问题状态
        {
          prop: 'problemExchangeStatus',
          label: this.$t('problemManagement.problemExchangeStatus'),
          type: 'dict',
          code: 'PROBLEM_EXCHANGE_STATUS'
        },
        // 创建人
        {
          prop: 'createdBy',
          label: this.$t('problemManagement.createdBy'),
          type: 'quicksearch',
          propKey: 'username',
          showKey: 'nickname',
          name: 'scc_rbac_user_display'
        },
        // 创建时间
        {
          prop: 'dateList',
          label: this.$t('problemManagement.createTime'),
          type: 'daterange'
        },
        // 发生阶段
        {
          prop: 'problemExchangeStage',
          label: this.$t('problemManagement.problemExchangeStage'),
          type: 'dict',
          code: 'PROBLEM_EXCHANGE_STAGE'
        }
      ],
      queryParam: {}
    }
  },
  computed: {
    userType () {
      return this.$store.getters.userType
    },
    preArr () {
      if (this.userType === 'BUYER') {
        return [
          ...this.preArrList,
          {
            prop: 'vendorName',
            label: () => this.$t('common.vendorName'),
            type: 'quicksearch',
            showKey: 'companyName',
            name: 'scc_sup_company_info_display_buyer'
          }
        ]
      } else {
        return this.preArrList
      }
    }
  },
  created () {
    this.tableHeader = [
      // 问题单号
      {
        prop: 'problemExchangeCode',
        label: this.$t('problemManagement.problemExchangeCode'),
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.readOne(row)
      },
      // 车型编码
      {
        prop: 'motorcycleTypeCode',
        label: this.$t('problemManagement.motorcycleTypeCode'),
        width: 120
      },
      // 车型名称
      {
        prop: 'motorcycleTypeName',
        label: this.$t('problemManagement.motorcycleTypeName'),
        width: 120
      },
      // 物料编码
      {
        prop: 'itemCode',
        label: this.$t('problemManagement.itemCode'),
        width: 120
      },
      // 物料名称
      {
        prop: 'itemName',
        label: this.$t('problemManagement.itemName'),
        width: 120
      },
      // 供应商名称
      {
        prop: 'vendorName',
        label: this.$t('common.vendorName'),
        width: 120
      },
      // 问题状态
      {
        prop: 'problemExchangeStatus',
        label: this.$t('problemManagement.problemExchangeStatus'),
        width: 120,
        dataType: 'dict',
        code: 'PROBLEM_EXCHANGE_STATUS'
      },
      // 风险等级
      {
        prop: 'problemExchangeRiskLevel',
        label: this.$t('problemManagement.problemExchangeRiskLevel'),
        width: 120,
        dataType: 'dict',
        code: 'PROBLEM_EXCHANG_ERISK_LEVEL'
      },
      // 发生阶段
      {
        prop: 'problemExchangeStage',
        label: this.$t('problemManagement.problemExchangeStage'),
        width: 120,
        dataType: 'dict',
        code: 'PROBLEM_EXCHANGE_STAGE'
      },
      // 创建人
      {
        prop: 'createdFullName',
        label: this.$t('problemManagement.createdBy'),
        width: 100
      },
      // 创建时间
      {
        prop: 'creationDate',
        label: this.$t('problemManagement.createTime'),
        width: 160,
        dataType: 'dateTime'
      },
      {
        prop: 'operation',
        label: this.$t('common.operation'),
        width: 120,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          // 编辑
          {
            callback: row => this.editOne(row),
            formattor: () => this.$t('common.edit'),
            show: row => row.problemExchangeStatus === 'DRAFT'
          },
          // 删除
          {
            callback: row => this.deleteOne(row),
            formattor: () => this.$t('common.delete'),
            show: row => row.problemExchangeStatus === 'DRAFT'
          },
          // 跟进处理
          {
            callback: row => this.editOne(row),
            formattor: () => this.$t('common.handle'),
            show: row =>
              !['DRAFT', 'CLOSE'].includes(row.problemExchangeStatus) &&
              ((this.userType === 'BUYER' &&
                ['PJ_TO_BE_CONFIRMED', 'TO_BE_ACCEPTED'].includes(row.problemExchangeStatus)) ||
                (this.userType === 'VENDOR' &&
                  [
                    'PJ_REJECT',
                    'PJ_TO_BE_SUBMITTED',
                    'RECTIFICATION_REJECTION',
                    'UNDER_RECTIFICATION'
                  ].includes(row.problemExchangeStatus)))
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
          { prop: 'dateList', fromProp: 'startDate', toProp: 'endDate' }
        ])
      }

      this.queryParam = payload || this.queryParam
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    addOne () {
      this.$emit('tab-add', {
        component: problemDetail,
        params: {
          flag: 'add',
          tabName: 'problemDetail'
        },
        // 新增问题点
        title: this.$t('problemManagement.addProblem'),
        name: 'problemDetail'
      })
    },
    editOne (row) {
      this.$emit('tab-add', {
        component: problemDetail,
        params: {
          flag: 'edit',
          row: row,
          tabName: 'problemDetail' + row.problemExchangeCode
        },
        title: row.problemExchangeCode,
        name: 'problemDetail' + row.problemExchangeCode
      })
    },
    readOne (row) {
      this.$emit('tab-add', {
        component: problemDetail,
        params: {
          flag: 'view',
          row: row,
          tabName: 'problemDetail' + row.problemExchangeCode
        },
        title: row.problemExchangeCode,
        name: 'problemDetail' + row.problemExchangeCode
      })
    },
    deleteOne ({ problemExchangeId }) {
      // 当前操将永久删除此数据，确认删除此数据
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(async () => {
          let res = await problemManagement.delete({ problemExchangeId })
          // 删除成功
          this.$message.success(this.$t('common.successDelete'))
          this.$refs[this.gridId].query()
        })
        .catch(() => {})
    }
  }
}
</script>

<style lang="scss" scoped></style>
