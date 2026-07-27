<template>
  <el-container
    class="flex-container the_vendorBiddingList_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="searchFormConfig"
        :pre-form-obj="preFormObj"
        form-label-width="120px"
        @getFormData="getQueryData"
      />

      <TableView
        ref="list"
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :com-active="$attrs['changeTab']"
        open-custom-table
        :url="tableViewUrl"
      />
    </el-main>
  </el-container>
</template>

<script>
import { SOU_SIGN_UP_STATUS_ENUM, SOU_PROJECT_STATUS_ENUM, SOU_ORDER_STATUS_ENUM } from 'lib@/composition/origin/enum'
import { compVendorHttp } from 'mods@/competitionSupplier/api'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import competitionProjectQuote from './competitionProjectQuote'
import competitionProjectSignUp from './competitionProjectSignUp'
import competitionProjectDetail from './competitionProjectDetail'

export default {
  name: 'CompetitionProjectList',

  components: {
    TableView,
    FormWrapper
  },

  mixins: [tabTodoWatch, tabTodoMixin],

  data () {
    return {
      tableViewUrl: compVendorHttp.order.listPageUrl,
      gridData: [],
      tableHeader: [
        // 项目编号
        {
          prop: 'souNo',
          label: this.$t('bidMod.bidingNum'),
          minWidth: 150,
          showType: 'button',
          btnStyle: 'text',
          callback: row => this.openDetailTab(row, 'view')
        },
        // 项目名称
        {
          prop: 'souName',
          label: this.$t('bidMod.bidingName'),
          minWidth: 150
        },
        // 项目状态
        {
          prop: 'projectStatus',
          label: this.$t('bidMod.bidingStatus'),
          minWidth: 100,
          dataType: 'dict',
          code: 'SOU_PROJECT_STATUS'
        },
        // 报名状态
        {
          prop: 'signUpStatus',
          label: this.$t('bidMod.signUpStatus'),
          minWidth: 100,
          dataType: 'dict',
          code: 'SOU_SIGN_UP_STATUS'
        },
        // 投标状态
        {
          prop: 'orderStatus',
          label: this.$t('bidMod.orderStatus'),
          minWidth: 100,
          dataType: 'dict',
          code: 'SOU_ORDER_STATUS'
        },
        // 报价开始时间
        {
          prop: 'orderStartTime',
          label: this.$t('bidMod.bidingStartDatetime1'),
          minWidth: 140
        },
        // 报价截止时间
        {
          prop: 'orderEndTime',
          label: this.$t('bidMod.quotedeadline'),
          minWidth: 140
        },
        // 发布时间
        {
          prop: 'publishTime',
          label: this.$t('bidMod.releaseDatetime'),
          minWidth: 140
        },
        {
          prop: 'operation',
          label: this.$t('bidMod.operation'),
          minWidth: 140,
          showType: 'buttons',
          fixed: 'right',
          btnStyle: 'text',
          buttons: [
            // 报名
            {
              // 未报名 && 接受报名中
              show: row => row.signUpStatus === SOU_SIGN_UP_STATUS_ENUM.NO_SIGN_UP && row.projectStatus === SOU_PROJECT_STATUS_ENUM.ACCEPT_SIGN_UP,
              callback: row => this.signUp(row),
              formattor: () => this.$t('bidMod.signUpBiding')
            },
            // 报价
            {
              // 接受投标中
              show: row => row.projectStatus === SOU_PROJECT_STATUS_ENUM.ACCEPT_ORDER,
              callback: row => this.quote(row),
              formattor: () => this.$t('bidMod.doQuote')
            },
            // 查看结果
            {
              // 已投标
              show: row => row.orderStatus === SOU_ORDER_STATUS_ENUM.SUBMISSION,
              callback: row => this.openDetailTab(row, 'viewResults'),
              formattor: () => this.$t('bidMod.viewResults')
            }
          ]
        }
      ],
      tableData: [],
      preFormObj: {},
      searchFormConfig: [
        // 项目编号
        { prop: 'souNo', label: () => this.$t('bidMod.enquiry_projectNum') },
        // 项目名称
        { prop: 'souName', label: () => this.$t('bidMod.enquiry_projectName') },
        // 项目状态
        {
          prop: 'projectStatus',
          label: () => this.$t('bidMod.enquiry_bidingStatus'),
          type: 'dict',
          code: 'SOU_PROJECT_STATUS'
        },
        // 报名状态
        { prop: 'signUpStatus',
          label: () => this.$t('bidMod.signUpStatus'),
          type: 'dict',
          code: 'SOU_SIGN_UP_STATUS'
        },
        // 报价状态
        {
          prop: 'orderStatus',
          label: () => this.$t('bidMod.quoteStatus'),
          type: 'dict', // 字典类型
          code: 'SOU_ORDER_STATUS'
        }
      ],
      queryParam: {}
    }
  },

  watch: {
    $route: {
      deep: true,
      immediate: true,
      handler () {
        const routeParams = this.$route.params
        if (routeParams.from === 'workCount' && routeParams.funName === 'vendorBiddingList_new') {
          // 供应商 工作台跳转
          this.queryParam.projectStatus = routeParams.projectStatus
          this.preFormObj = {
            projectStatus: routeParams.projectStatus
          }
        }
      }
    }
  },

  mounted () {
    this.$nextTick(() => {
      this.getQueryData()
    })
  },

  methods: {
    /* 查询 */
    getQueryData (v) {
      let query = v || this.preFormObj
      this.queryParam = Object.assign(
        { vendorId: this.$store.state.user.userInfo.companyId || 1 },
        query
      )
      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },

    /* 报名 */
    signUp (row) {
      this.$emit('tab-add', {
        component: competitionProjectSignUp,
        params: {
          flag: 'edit',
          row: row,
          tabName: `competitionProjectSignUp${row.souNo}`
        },
        title: row.souNo,
        name: `competitionProjectSignUp${row.souNo}`
      })
    },

    /* 报价 */
    quote (row) {
      this.$emit('tab-add', {
        component: competitionProjectQuote,
        params: {
          flag: 'edit',
          row: row,
          tabName: `competitionProjectQuote${row.souNo}`
        },
        title: row.souNo,
        name: `competitionProjectQuote${row.souNo}`
      })
    },

    /* 打开详情 / 查看结果 */
    openDetailTab (row, type) {
      this.$emit('tab-add', {
        component: competitionProjectDetail,
        params: {
          flag: 'view',
          row: row,
          type,
          tabName: `competitionProjectDetail${row.souNo}`
        },
        title: row.souNo,
        name: `competitionProjectDetail${row.souNo}`
      })
    }
  }
}
</script>

<style scoped lang="scss">
:deep(.the_vendorBiddingList_wrapper) {
  .el-button-group .el-button {
    margin-left: 5px !important;
  }
}
</style>
