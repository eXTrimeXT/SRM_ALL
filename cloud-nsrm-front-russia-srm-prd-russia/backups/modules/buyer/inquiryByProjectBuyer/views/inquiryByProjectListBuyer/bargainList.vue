<template>
  <el-container
    class="flex-container the_biddingProject_wrapper"
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
            @click="editTab('add')"
          >
            {{ $t("common.add") }}
          </el-button>
        </template>
      </MainHeader>

      <TableView
        ref="bargainProjectTable"
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :com-active="$attrs['changeTab']"
        url="/api-brg/brgInitiating/bargain/listPage"
      />
    </el-main>
  </el-container>
</template>

<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import { parseTimeYMD } from 'lib@/composition/origin/composition'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import bargainDetail from './bargainDetail'

export default {
  name: 'BargainList',

  components: {
    TableView,
    MainHeader,
    FormWrapper
  },

  mixins: [tabTodoWatch, tabTodoMixin],

  data () {
    return {
      tableHeader: [
        // 项目编号
        {
          prop: 'bargainNum',
          label: this.$t('bidMod.bidingNum'),
          minWidth: 150,
          showType: 'button',
          btnStyle: 'text',
          callback: row => this.editTab('view', row)
        },
        // 项目名称
        {
          prop: 'bargainName',
          label: this.$t('bidMod.bidingName'),
          minWidth: 150,
          formattor: val => val || '--'
        },
        // 项目状态
        {
          prop: 'bargainStatus',
          label: this.$t('bidMod.bidingStatus'),
          minWidth: 100,
          dataType: 'dict',
          code: 'BRG_PROJECT_STATUS'
        },
        // 审批状态
        {
          prop: 'auditStatus',
          label: this.$t('bidMod.auditStatus'),
          minWidth: 100,
          dataType: 'dict',
          code: 'BIDDING_APPROVAL_STATUS'
        },
        // 评分规则
        {
          prop: 'evaluateMethod',
          label: this.$t('bidMod.evaluateMethod'),
          minWidth: 100,
          dataType: 'dict',
          code: 'BRG_EVALUATE_METHOD'
        },
        // 当前轮次
        {
          prop: 'currentRound',
          label: this.$t('bidMod.currentRound'),
          minWidth: 100
        },
        // 发布人
        {
          prop: 'createdUserName',
          label: this.$t('bidMod.publishBy'),
          minWidth: 100
        },
        // 创建日期
        {
          prop: 'creationDate',
          label: this.$t('bidMod.creationDate'),
          minWidth: 100,
          formattor: val => parseTimeYMD(val)
        },
        // 发布时间
        {
          prop: 'releaseDatetime',
          label: this.$t('bidMod.releaseDatetime'),
          minWidth: 100,
          formattor: val => parseTimeYMD(val)
        },
        // 报价截止时间
        {
          prop: 'enrollEndDatetime',
          label: this.$t('bidMod.stopTime'),
          minWidth: 100,
          formattor: val => parseTimeYMD(val)
        },
        {
          prop: 'operation',
          label: this.$t('bidMod.operation'),
          minWidth: 150,
          showType: 'buttons',
          fixed: 'right',
          btnStyle: 'text',
          buttons: [
            // 管理
            {
              callback: row => this.editTab('edit', row),
              formattor: () => this.$t('bidMod.management')
            },
            // 废弃
            {
              // 审批状态为 已审批
              show: row => !['DRAW_UP', 'FIXING_PRICE', 'PRICED', 'PROJECT_END', 'ABANDON'].includes(row.bargainStatus),
              callback: row => this.abandonRow(row),
              formattor: () => this.$t('common.abandon')
            },
            // 删除
            {
              show: row => row.bargainStatus === 'DRAW_UP',
              callback: row => this.deleteItem(row),
              formattor: () => this.$t('common.delete')
            }
          ]
        }
      ],
      searchFormConfig: [
        // 项目编号
        { prop: 'bargainNum', label: this.$t('bidMod.bidingNum') },
        // 项目名称
        { prop: 'bargainName', label: this.$t('bidMod.bidingName') },
        // 项目状态
        {
          prop: 'bargainStatus',
          label: this.$t('bidMod.bidingStatus'),
          type: 'dict',
          code: 'BRG_PROJECT_STATUS'
        },
        // 评分规则
        {
          prop: 'evaluateMethod',
          label: this.$t('bidMod.evaluateMethod'),
          type: 'dict',
          code: 'BRG_EVALUATE_METHOD'
        },
        // 发布人
        {
          prop: 'createdBy',
          label: this.$t('bidMod.publishBy'),
          type: 'quicksearch',
          showKey: 'username',
          name: 'scc_rbac_user_display'
        },
        // 立项审核状态
        {
          prop: 'auditStatus',
          label: this.$t('bidMod.biddingManagementBuyer.auditStatus'),
          type: 'dict',
          code: 'BIDDING_APPROVAL_STATUS'
        },
        // 创建时间
        {
          prop: 'creationDate',
          label: this.$t('bidMod.creationDate'),
          type: 'daterange'
        },
        // 发布时间
        {
          prop: 'releaseDatetime',
          label: this.$t('bidMod.releaseDatetime'),
          type: 'daterange'
        }
      ],
      status: false,
      tableData: [],
      queryParam: {}
    }
  },

  watch: {
    $route: {
      immediate: true,
      deep: true,
      handler () {
        if (
          this.$route.params.from === 'fromFun' &&
          this.$route.params.funName === 'inquiryByProjectListBuyer'
        ) {
          // 工作流 或者其他地方 跳转过来
          let bargainId = null
          let title = null
          if (this.$route.params.formId) {
            bargainId = Number(this.$route.params.formId)
            title = this.$route.params.formNo
          }
          let row = {
            ...this.$route.params,
            bargainId,
            bargainNum: title
          }
          this.editTab('edit', row)
        }
        if (this.$route.params.type === 'BIDDINGBi') {
          this.editTab('edit', {
            bargainId: this.$route.params.bargainId,
            bargainNum: this.$route.params.inquiryNumber
          })
        }
        if (this.$route.params.from === 'demandPoolManagement' && this.$route.params.funName === 'inquiryByProjectListBuyer') {
          // 需求池跳转
          this.editTab(this.$route.params.type, {
            bargainId: this.$route.params.formId,
            bargainNum: this.$route.params.formNo
          })
        }
      }
    }
  },

  mounted () {
    this.$nextTick(() => {
      this.getQueryData()
    })
    this.status = this.$route.params.status ? this.$route.params.status : this.status
    if (this.status) {
      this.editTab('edit', {
        bargainId: this.$route.params.bargainId,
        bargainNum: this.$route.params.inquiryNumber
      })
    }
  },

  methods: {
    /* 查询 */
    getQueryData (payload) {
      if (payload) {
        // 创建时间
        if (payload.creationDate && Array.isArray(payload.creationDate) && payload.creationDate.length === 2) {
          payload = {
            ...payload,
            creationDateFrom: payload.creationDate[0],
            creationDateTo: payload.creationDate[1]
          }
          delete payload.creationDate
        }
        // 发布时间
        if (payload.releaseDatetime && Array.isArray(payload.releaseDatetime) && payload.releaseDatetime.length === 2) {
          payload = {
            ...payload,
            releaseDatetimeFrom: payload.releaseDatetime[0],
            releaseDatetimeTo: payload.releaseDatetime[1]
          }
          delete payload.releaseDatetime
        }
      }
      this.queryParam = payload || this.queryParam
      this.$nextTick(() => {
        this.$refs.bargainProjectTable.query()
      })
    },

    /* 废弃招标行 */
    abandonRow (row) {
      this.$prompt(this.$t('common.abandonA'), '', {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        inputValidator: value => !(!value || value.length > 200),
        inputErrorMessage: '请输入废弃原因并且字符数不能超过200'
      }).then(({ value }) => {
        this.$api.brg.inquiryByProject.bargainAbandon({
            bargainId: row.bargainId,
            abandonReason: value
          }).then(data => {
          if (data) {
            this.$message({
              message: this.$t('common.successAbandon'), // 废弃成功
              type: 'success'
            })
            this.getQueryData()
          }
        })
      })
    },

    /* 删除招标行 */
    deleteItem (row) {
      // 删除该行?
      this.$confirm(this.$t('common.delRow'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$api.brg.inquiryByProject.bargainDelete(row.bargainId).then(data => {
          if (data) {
            this.$message({
              message: this.$t('common.successDelete'), // 删除成功
              type: 'success'
            })
            this.getQueryData()
          }
        })
      })
    },

    /* 打开新增 or 编辑tab */
    editTab (type, row) {
      let tab = {}
      if (type === 'add') {
        // 新增
        tab = {
          component: bargainDetail,
          params: {
            flag: type,
            tabName: 'bargainDetail'
          },
          title: this.$t('bidMod.createProject'), // 创建项目
          name: 'bargainDetail'
        }
      } else {
        // 修改 只读
        tab = {
          component: bargainDetail,
          params: {
            flag: type,
            row: row,
            readOnly: type === 'view',
            tabName: 'bargainDetail' + row.bargainNum
          },
          title: row.bargainNum,
          name: 'bargainDetail' + row.bargainNum
        }
      }
      this.$emit('tab-add', tab)
    }
  }
}
</script>

<style scoped lang="scss">
.the_biddingProject_wrapper {
  :deep(.el-button-group .el-button) {
    margin-left: 5px !important;
  }
}
</style>
