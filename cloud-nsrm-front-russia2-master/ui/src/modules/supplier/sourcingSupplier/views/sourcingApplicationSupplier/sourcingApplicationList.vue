<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <FormWrapper
        ref="formRef"
        :form-array="preArr"
        :init-active="true"
        @getFormData="getQuerydata"
      />
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-inq/inq/reqapply/listPage"
      />
      <!-- 评选结果 -->
      <srm-dialog
        :title="$t('sourcingBuyer.result')"
        size="large"
        :visible.sync="dialogVisible"
        :close-on-click-modal="false"
      >
        <div>
          <el-form :model="resultQuery">
            <el-row :gutter="32">
              <el-col :span="6">
                <el-form-item :label="$t('sourcingBuyer.resultStatus')">
                  <dict-select
                    v-model="resultQuery.selectStatus"
                    code="APPLY_HEAD_STATUS"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-table
              :data="resultData"
              style="width: 100%"
              border
              height="345px"
              highlight-current-row
            >
              <el-table-column align="center" type="index" :label="$t('common.sort')" width="60" />
              <!-- 是否发起询价 -->
              <el-table-column
                align="center"
                prop="projectName"
                :label="$t('sourcingBuyer.isInquiry')"
                min-width="120"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="{row}">
                  {{ row.isInquiry === 'Y' ? $t('common.yes'):$t('common.no') }}
                </template>
              </el-table-column>
              <!-- 询价单号 -->
              <el-table-column
                align="center"
                prop="inquiryNo"
                :label="$t('sourcingBuyer.inquiryNo')"
                min-width="120"
                :show-overflow-tooltip="true"
              />
              <!-- 是否发起资质审查 -->
              <el-table-column
                align="center"
                prop="isReview"
                :label="$t('sourcingBuyer.isReview')"
                min-width="120"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="{row}">
                  {{ row.isReview === 'Y' ? $t('common.yes'):$t('common.no') }}
                </template>
              </el-table-column>
              <!-- 资质审查单号 -->
              <el-table-column
                align="center"
                prop="reviewFormNumber"
                :label="$t('sourcingBuyer.reviewFormNumber')"
                min-width="120"
                :show-overflow-tooltip="true"
              />
            </el-table>
          </el-form>
        </div>
        <div slot="footer" class="dialog-footer">
          <el-button @click="handleDialogClose">
            <!-- 关 闭 -->
            {{ $t('common.close') }}
          </el-button>
        </div>
      </srm-dialog>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import sourcingApplicationDetail from './sourcingApplicationDetail'
import OrganizationSelector from 'lib@/components/organization-selector'
import { daterangePayloadFormat } from 'lib@/composition/commonComposition'

export default {
  name: 'SourcingApplicationList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
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
        // 报名状态
        {
          prop: 'applyStatus',
          label: this.$t('sourcingBuyer.applyStatus'),
          type: 'dict',
          code: 'APPLY_STATUS'
        }
      ],
      queryParam: {},
      dialogVisible: false,
      resultData: [],
      resultQuery: {
        selectStatus: ''
      }
    }
  },
  created () {
    this.tableHeader = [
      // 寻源单号
      {
        prop: 'reqHeadNo',
        label: this.$t('sourcingBuyer.reqHeadNo'),
        width: 130,
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
      // 报名状态
      {
        prop: 'applyStatus',
        label: this.$t('sourcingBuyer.applyStatus'),
        width: 120,
        dataType: 'dict',
        code: 'APPLY_STATUS'
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
          // 报名
          {
            callback: row => this.addOne(row),
            formattor: () => this.$t('sourcingBuyer.signUp'),
            code: 'su:sourcingApplicationList:signUp',
            show: row => row.status === 'PUBLISHED' && row.applyStatus === 'DRAFT'
          },
          // 拒绝
          {
            callback: row => this.refuseOne(row),
            formattor: () => this.$t('sourcingBuyer.refuse'),
            code: 'su:sourcingApplicationList:refuse',
            show: row => row.status === 'PUBLISHED' && row.applyStatus === 'DRAFT'
          },
          // 查看评选结果
          {
            callback: row => this.viewResult(row),
            formattor: () => this.$t('sourcingBuyer.viewResult'),
            code: 'su:sourcingApplicationList:viewResult',
            show: row => ['SIGN'].includes(row.applyStatus)
          },
          // 查看报名
          {
            callback: row => this.readOne(row),
            formattor: () => this.$t('sourcingBuyer.viewSignUp'),
            code: 'su:sourcingApplicationList:viewSignUp',
            show: row => ['SIGN'].includes(row.applyStatus)
          },
          // 查看拒绝原因
          {
            callback: row => this.viewRefuseReason(row),
            formattor: () => this.$t('sourcingBuyer.viewRefuseReason'),
            code: 'su:sourcingApplicationList:viewRefuseReason',
            show: row => ['REFUSE_SIGN'].includes(row.applyStatus)
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
    addOne (row) {
      this.$emit('tab-add', {
        component: sourcingApplicationDetail,
        params: {
          flag: 'add',
          row: row,
          tabName: 'sourcingApplicationDetail'
        },
        title: this.$t('sourcingBuyer.signUp'),
        name: 'sourcingApplicationDetail'
      })
    },
    refuseOne ({ reqHeadId }) {
      this.$prompt(this.$t('sourcingBuyer.refuseSignUp'), this.$t('sourcingBuyer.refuseReason'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        inputValidator: value => !!value,
        inputErrorMessage: this.$t('sourcingBuyer.refuseReasonIsRequired')
      }).then(async ({ value }) => {
        await this.$api.inq.sourcing.refuseSignUp({
            refuseReason: value,
            reqHeadId
          })
        this.$message.success(this.$t('sourcingBuyer.refused'))
        this.$refs[this.gridId].query()
      })
    },
    viewRefuseReason ({ refuseReason }) {
      this.$alert(refuseReason, this.$t('sourcingBuyer.refuseReason'), {
        confirmButtonText: this.$t('common.confirm')
      })
    },
    async viewResult ({ applyId }) {
      let res = await this.$api.inq.sourcing.getResult({ applyId })
      if (res.data) {
        this.resultQuery.selectStatus = res.data.selectStatus
        this.resultData = [res.data]
        this.dialogVisible = true
      }
    },
    handleDialogClose () {
      this.dialogVisible = false
    },
    readOne (row) {
      this.$emit('tab-add', {
        component: sourcingApplicationDetail,
        params: {
          flag: 'supplierView',
          row: row,
          showType: 'readOnly',
          tabName: 'sourcingApplicationDetail' + row.applyId
        },
        title: row.applyId,
        name: 'sourcingApplicationDetail' + row.applyId
      })
    }
  }
}
</script>

<style lang="scss" scoped></style>
