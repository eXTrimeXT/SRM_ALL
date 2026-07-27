<template>
  <el-container
    class="flex-container the_vendorBiddingList_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :form-array="preArr"
        :pre-form-obj="preFormObj"
        @getFormData="getQuerydata"
      >
        <!-- 业务模式 -->
        <template #businessModeCode="{ scope }">
          <DictSelect
            v-model="scope.businessModeCode"
            code="BUSINESS_MODE"
          />
        </template>
        <!-- 运输方式 -->
        <template #transportModeCode="{ scope }">
          <DictSelect
            v-model="scope.transportModeCode"
            code="TRANSPORT_MODE"
          />
        </template>
        <!-- 业务类型 -->
        <template #businessType="{ scope }">
          <DictSelect
            v-model="scope.businessType"
            code="LOGISTICS_BUSINESS_TYPE"
          />
        </template>
        <!-- 项目状态 -->
        <template #bidingStatus="{ scope }">
          <DictSelect
            v-model="scope.bidingStatus"
            code="LOGISTICS_PROJECT_STATUS"
          />
        </template>
        <!-- 项目状态 -->
        <template #status="{ scope }">
          <DictSelect
            v-model="scope.status"
            code="BIDDING_ORDER_STATES"
          />
        </template>
      </form-wrapper>
      <main-header
        :l-span="22"
        :r-span="2"
      >
        <template slot="left" />
      </main-header>
      <table-view
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :comActive="$attrs['changeTab']"
        url="/api-pd/logistics/biding/vendor/listPage"
      />
      <!-- 查看回复 -->
      <srm-dialog
        :title="$t('bidMod.replyDialog')"
        size="large"
        :visible.sync="dialogFormVisible"
        :close-on-click-modal="false"
      >
        <el-table
          :data="tableData2"
          style="width: 100%"
          border
          height="222px"
        >
          <el-table-column
            align="center"
            type="index"
            width="50"
          />
          <el-table-column
            align="center"
            prop="vendorCode"
            :label="$t('common.vendorCode')"
            width="100"
          />
          <el-table-column
            align="center"
            prop="vendorName"
            :label="$t('common.vendorName')"
            width="100"
          />
          <el-table-column
            align="center"
            prop="contactMan"
            :label="$t('bidMod.contactMan')"
            width="100"
          />
          <el-table-column
            align="center"
            prop="telephone"
            :label="$t('bid_mod.phone')"
            width="100"
          />
          <el-table-column
            align="center"
            prop="email"
            :label="$t('common.email')"
            width="180"
          />
          <el-table-column
            align="center"
            prop="status"
            :label="$t('common.status')"
            width="100"
          />
          <!-- 技术建议 -->
          <el-table-column
            align="center"
            prop="technicalAdvise"
            :label="$t('bidMod.suggestionFileId')"
            width="100"
          />
          <!-- 参考价格 -->
          <el-table-column
            align="center"
            prop="price1"
            :label="$t('bidMod.referencePrice')"
            width="100"
          />
          <el-table-column
            align="center"
            prop="remark"
            :label="$t('common.remark')"
            width="100"
          />
        </el-table>
      </srm-dialog>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import vendorBiddingDetail from './vendorBiddingDetail'
import vendorBiddingSignUp from './vendorBiddingSignUp'
import doBidingDetail from './doBidingDetail'
import bidingFollowingDetail from './bidingFollowingDetail'

export default {
  name: 'VendorBiddingList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    vendorBiddingDetail,
    vendorBiddingSignUp,
    doBidingDetail,
    bidingFollowingDetail
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      name: 'biddingProjectTable',
      reviewFormNumber: '',
      gridData: [],
      pageSize: 15,
      gridId: 'list',
      selectList: [],
      currentRow: null,
      vendorId: null,
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      tableData2: [],
      statusList: [],
      form: {
        id: '',
        vendorCode: '',
        vendorCompanyName: '',
        reviewFormNumber: '',
        enabled: ''
      },
      rules: {
        vendorCode: [
          { required: true, message: this.$t('bidMod.msgDictCode') }
        ], // 请输入字典编码
        vendorCompanyName: [
          { required: true, message: this.$t('bidMod.msgDictName') }
        ] // 请输入字典名称
      },
      isModify: false,
      dialogFormVisible: false,
      formLabelWidth: '100px',
      curRole: this.$store.getters.userType, // 用户类型 BUYER || VENDOR
      firstLoad: true,
      preFormObj: {},
      preArr: [
        { prop: 'bidingNum', label: this.$t('bidMod.bidingNum') }, // 项目编号
        { prop: 'bidingName', label: this.$t('bidMod.bidingName') }, // 项目名称
        {
          prop: 'bidingStatus',
          label: this.$t('bidMod.bidingStatus'), // 项目状态
          slot: 'bidingStatus',
          type: 'slot'
        },
        {
          prop: 'businessModeCode',
          label: this.$t('logisticsMod.businessMode'), // 业务模式
          slot: 'businessModeCode',
          type: 'slot'
        },
        {
          prop: 'transportModeCode',
          label: this.$t('bid_mod.transportType'), // 运输方式
          slot: 'transportModeCode',
          type: 'slot'
        },
        {
          prop: 'businessType',
          label: this.$t('bidMod.businessType'), // 业务类型
          slot: 'businessType',
          type: 'slot'
        },
        {
          prop: 'status',
          label: this.$t('bidMod.orderStatus'),
          slot: 'status',
          type: 'slot'
        }
      ],
      queryParam: {},
      evaluateMethodList: [],
      bidingProStatusList: []
    }
  },
  watch: {
    $route: {
      // 当前功能已经打开的时候监听是否是从其他功能跳转过来的
      deep: true,
      immediate: true,
      handler () {
        if (
          this.$route.params.from === 'workCount' &&
          this.$route.params.funName === 'vendorBiddingList'
        ) {
          // 供应商 工作台跳转
          this.queryParam.bidingStatus = this.$route.params.bidingStatus
          // this.firstLoad = false;
          this.preFormObj = Object.assign(
            {},
            { bidingStatus: this.$route.params.bidingStatus }
          )
        }
      }
    }
  },
  created () {
    this.vendorId = this.$store.getters.userInfo.companyId
    let _this = this
    this.tableHeader = [
      { prop: 'bidingNum', label: _this.$t('bidMod.bidingNum'), width: 150 },
      {
        prop: 'bidingName',
        label: _this.$t('bidMod.bidingName'),
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.editTab(row)
        }.bind(this),
        formattor (val) {
          return val || '--'
        }
      },
      {
        prop: 'bidingStatus',
        label: _this.$t('bidMod.bidingStatus'),
        width: 100,
        dataType: 'dict',
        code: 'LOGISTICS_PROJECT_STATUS'
      },
      {
        prop: 'status',
        label: this.$t('bidMod.orderStatus'),
        width: 100,
        dataType: 'dict',
        code: 'BIDDING_ORDER_STATES'
      },
      {
        prop: 'currentRound',
        label: this.$t('bidMod.currentRound'), // 当前轮次
        width: 100
      },
      {
        prop: 'businessModeCode',
        label: this.$t('logisticsMod.businessMode'), // 业务模式
        width: 120,
        dataType: 'dict',
        code: 'BUSINESS_MODE'
      },
      {
        prop: 'transportModeCode',
        label: this.$t('bid_mod.transportType'), // 运输方式
        width: 120,
        dataType: 'dict',
        code: 'TRANSPORT_MODE'
      },
      {
        prop: 'businessType',
        label: this.$t('bidMod.businessType'), // 业务类型
        width: 120,
        dataType: 'dict',
        code: 'LOGISTICS_BUSINESS_TYPE'
      },
      {
        prop: 'enrollEndDatetime',
        label: _this.$t('bidMod.enrollEndDatetime'),
        width: 150
      },
      {
        prop: 'withdrawReason',
        label: this.$t('bidMod.withdrawReason'), // 撤回原因
        width: 120
      },
      {
        prop: 'releaseDatetime',
        label: _this.$t('bidMod.releaseDatetime'),
        width: 150
      },
      {
        prop: 'operation',
        label: _this.$t('bidMod.operation'),
        width: 250,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            callback: function (row) {
              this.doBiding(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('bidMod.doBiding')
            },
            show: function (row) {
              // 项目状态为【接受投标中】,投标状态为【未投标】才显示
              if (
                row.bidingStatus === 'ACCEPT_BID' &&
                ['DRAFT', 'WITHDRAW'].includes(row.status)
              ) {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              this.doBiding(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('bidMod.management')
            }
            // show: function(row) {
            //   //项目状态为【接受投标中】,投标状态为【未投标】才显示
            //   if (
            //     row.bidingStatus === 'ACCEPT_BID' &&
            //     ["DRAFT", 'WITHDRAW'].includes(row.status)
            //   ) {
            //     return true;
            //   } else {
            //     return false;
            //   }
            // }
          },
          {
            callback: function (row) {
              this.rebackBiding(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('bidMod.rebackBiding')
            },
            show: function (row) {
              if (
                row.bidingStatus === 'ACCEPT_BID' &&
                row.withdrawBiding == 'Y' &&
                ['SUBMISSION'].includes(row.status)
              ) {
                return true
              } else {
                return false
              }
            }
          }
        ]
      }
    ]

    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    rebackBiding (row) {
      this.$prompt(
        this.$t('bidMod.withdrawReason'),
        this.$t('bidMod.rebackBiding'),
        {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel')
        }
      )
        .then(({ value }) => {
          this.$http({
            url:
              '/api-pd/logistics/biding/vendor/withdrawQuotedPrice',
            method: 'post',
            params: {
              bidingId: row.bidingId,
              vendorId: this.vendorId,
              withdrawReason: value
            },
            loading: true
          })
            .then(data => {
              this.$message.success(this.$t('bidMod.withdrawBidSuccess')) // 投标撤回成功！
              this.getQuerydata()
            })
            .catch(err => {
              console.log(err)
            })
        })
        .catch(() => {
          this.$message.info(this.$t('bidMod.cancelWithdraw')) // 取消撤回！
        })
    },
    doBiding (row) {
      this.$emit('tab-add', {
        component: doBidingDetail,
        params: {
          flag: 'edit',
          row: row
        },
        title: row.bidingName,
        name: 'doBidingDetail' + row.bidingName
      })
    },
    getQuerydata (v) {
      let query = v || this.preFormObj
      this.queryParam = Object.assign(
        { vendorId: this.$store.state.user.userInfo.companyId || 1 },
        query
      )
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      this.currentRow = val
    },
    editTab (row) {
      // 修改
      let tab = {
        component: vendorBiddingDetail,
        params: {
          flag: 'edit',
          row: row
        },
        title: row.bidingName,
        name: 'vendorBiddingDetail' + row.bidingName
      }
      this.$emit('tab-add', tab)
    }
  }
}
</script>
<style scoped lang="scss">
.the_vendorBiddingList_wrapper /deep/ {
  .el-button-group .el-button {
    margin-left: 5px !important;
  }
}
</style>
