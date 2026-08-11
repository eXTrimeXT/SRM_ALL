<template>
  <el-container
    class="flex-container the_doBidingDetail_wrapper"
    direction="vertical"
  >
    <el-main>
      <div class="the_progress">
        <el-steps :active="2">
          <el-step :title="$t('bidMod.openBidFollow')" />
          <el-step :title="$t('bidMod.confirmFollowBid')" />
          <el-step :title="$t('common.finish')" />
        </el-steps>
        <div class="the_footer_row">
          {{ $t("bidMod.curBidDeadline")
          }}<span style="color:#f44">{{ showTimeText }}</span>
        </div>
      </div>
      <div class="the_display_content">
        <el-row type="flex">
          <el-col>
            <span>{{ $t("bidMod.currentRound") }}</span>{{ displayForm.round }}
          </el-col>
          <el-col>
            <span>{{ $t("bidMod.decimalAccuracy1") }}</span>{{ displayForm.decimalAccuracy }}
          </el-col>
          <el-col>
            <span>{{ $t("bidMod.bidingCurrency3") }}</span>{{
              currencyList2Obj[displayForm.bidingCurrency] ||
                displayForm.bidingCurrency
            }}
          </el-col>
          <el-col>
            <span>{{ $t("bidMod.taxInclusivePrice2") }}</span>{{
              displayForm.taxInclusivePrice == "Y"
                ? this.$t("common.yes")
                : this.$t("common.no")
            }}
          </el-col>
        </el-row>
        <el-row type="flex">
          <!--            <el-col><span>税率</span>{{displayForm.taxRate}}</el-col>-->
          <el-col>
            <span>{{ $t("bidMod.bidingType") }}</span>{{ displayForm.bidingType }}
          </el-col>
          <el-col>
            <span>{{ $t("bidMod.inquiryRule") }}</span>{{
              quoteTypeList2Obj[displayForm.evaluateMethod] ||
                displayForm.evaluateMethod
            }}
          </el-col>
          <el-col>
            <span>{{ $t("bidMod.bidingAwardWay") }}</span>{{
              bidDecideList2Obj[displayForm.bidingAwardWay] ||
                displayForm.bidingAwardWay
            }}
          </el-col>
          <el-col />
        </el-row>
      </div>

      <div class="table1">
        <p>
          <span style="padding-right: 11px">{{
            $t("bidMod.businessInfo")
          }}</span>
          <el-button
            type="primary"
            @click="confirmBidFollow"
          >
            {{
              $t("bidMod.confirmFollowBid")
            }}
          </el-button>
          <el-button
            type="primary"
            @click="cancelBidFollow"
          >
            {{
              $t("bidMod.cancelBidFollow")
            }}
          </el-button>
        </p>
        <el-table
          :data="targetList"
          style="width: 100%"
          border
          height="200px"
          @selection-change="handleSelectionChange"
        >
          <el-table-column
            align="center"
            type="index"
            width="40"
          />
          <el-table-column
            type="selection"
            width="50"
          />
          <el-table-column
            align="center"
            prop="targetNum"
            :label="$t('bidMod.bidCode')"
            width="120"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            align="center"
            prop="targetDesc"
            :label="$t('bidMod.bidDesc')"
            min-width="150"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            align="center"
            prop="itemGroup"
            :label="$t('bidMod.itemGroup')"
            width="100"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            align="center"
            prop="orgName"
            :label="$t('bidMod.orgName')"
            width="150"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            align="right"
            prop="quantity"
            :label="$t('bid_mod.purQuantity')"
            width="100"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            align="center"
            prop="uomDesc"
            :label="$t('bid_mod.unit')"
            width="60"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            align="center"
            prop="finalPrice"
            :label="$t('bidMod.bidPriceIncludingTax')"
            width="100"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            align="center"
            prop="taxRate"
            :label="$t('bidMod.taxRate2')"
            width="100"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            align="center"
            prop="withStandard"
            :label="$t('bidMod.followBidSituation')"
            width="100"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            align="center"
            prop="comments"
            :label="$t('bid_mod.remark')"
            width="100"
            :show-overflow-tooltip="true"
          />
        </el-table>
      </div>
    </el-main>
  </el-container>
</template>
<script>
import { getDictItem, getAllPurCurrency } from '@/api/common'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import { tabTodoMixin } from '@/utils/mixins'
import { adaptDictData, parseTime } from '@/utils'

export default {
  name: 'BidingFollowingDetail',
  components: {
    TableView,
    MainHeader
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      showTimeText: this.$t('time.initTime'),
      multipleSelection: [],
      targetList: [],
      timer: '',
      displayForm: {
        round: '',
        bidingCurrency: '',
        taxInclusivePrice: '',
        taxRate: '',
        decimalAccuracy: '',
        bidingType: '',
        evaluateMethod: '',
        bidingAwardWay: '',
        orderLines: []
      },
      formDeadline: '',
      scopecompanyCode: '',
      scopeOrganizationCode: '',
      currencyList: [],
      currencyList2Obj: {},
      quoteTypeList2: [],
      quoteTypeList2Obj: {},
      bidDecideList: [],
      bidDecideList2Obj: {}
    }
  },
  created () {
    // 获取所有币种
    getAllPurCurrency().then(res => {
      this.currencyList = adaptDictData(res.data, 'currency')
      for (let i of this.currencyList) {
        this.currencyList2Obj[i.value] = i.label
      }
    })
    // 报价方式就是评选方式
    getDictItem('BIDDING_GRADING').then(res => {
      this.quoteTypeList2 = adaptDictData(res.data, 'dict')
      for (let i of this.quoteTypeList2) {
        this.quoteTypeList2Obj[i.value] = i.label
      }
    })
    // 决标方式
    getDictItem('BID_DECIDE_METHOD').then(res => {
      this.bidDecideList = adaptDictData(res.data, 'dict')
      for (let i of this.bidDecideList) {
        this.bidDecideList2Obj[i.value] = i.label
      }
    })
    this.getListDetail(
      this.$attrs.params.row.bidingId,
      this.$attrs.params.row.bidVendorId
    )
  },
  methods: {
    getListDetail () {
      this.$http({
        url:
          '/api-pd/supplierCooperate/orderHead/withStandardOrderInfo',
        method: 'POST',
        data: this.$attrs.params.row,
        loading: true
      })
        .then(data => {
          if (data && data.data) {
            console.log(data.data)
            this.displayForm = data.data
            this.targetList = data.data.orderLines
            this.formDeadline = data.data.endTime
            let date3 =
              new Date(this.formDeadline).getTime() - new Date().getTime() // 时间差的毫秒数
            if (date3 > 0) {
              this.timer = setInterval(() => {
                this.refreshDate()
              }, 1000)
            } else {
              clearInterval(this.timer)
            }
          }
        })
        .catch(err => {
          console.log(err)
          this.$emit(
            'tab-remove',
            'bidingFollowingDetail' + this.$attrs.params.row.bidingName
          )
          this.__setTabTodo('vendorBiddingList.getQuerydata')
        })
    },
    refreshDate () {
      let date3 = new Date(this.formDeadline).getTime() - new Date().getTime() // 时间差的毫秒数
      if (date3 < 0) return
      // 计算出相差天数
      var days = Math.floor(date3 / (24 * 3600 * 1000)) // 计算出小时数
      var leave1 = date3 % (24 * 3600 * 1000) // 计算天数后剩余的毫秒数
      var hours = Math.floor(leave1 / (3600 * 1000))
      // 计算相差分钟数
      var leave2 = leave1 % (3600 * 1000) // 计算小时数后剩余的毫秒数
      var minutes = Math.floor(leave2 / (60 * 1000))
      // 计算相差秒数
      var leave3 = leave2 % (60 * 1000) // 计算分钟数后剩余的毫秒数
      var seconds = Math.round(leave3 / 1000)
      this.showTimeText =
        days +
        this.$t('time.day') +
        hours +
        this.$t('time.hour') +
        minutes +
        this.$t('time.minute') +
        seconds +
        this.$t('time.second')
    },
    handleSelectionChange (val) {
      this.multipleSelection = val
    },
    cancelBidFollow () {
      if (this.multipleSelection.length == 0) {
        this.$message.info(this.$t('bidMod.selRowData'))
        return
      }
      this.displayForm.orderLines = this.multipleSelection
      this.$http({
        url:
          '/api-pd/supplierCooperate/orderHead/cancleWithStandardOrder',
        method: 'POST',
        data: this.displayForm,
        loading: true
      })
        .then(data => {
          this.$message.success(this.$t('bidMod.cancelFollowSucc'))
          this.getListDetail()
        })
        .catch(err => {
          console.log(err)
        })
    },
    confirmBidFollow () {
      if (this.multipleSelection.length == 0) {
        this.$message.info(this.$t('bidMod.selRowData'))
        return
      }
      this.displayForm.orderLines = this.multipleSelection
      // 新增或者保存，不同接口
      this.$http({
        url: '/api-pd/supplierCooperate/orderHead/withStandardOrder',
        method: 'POST',
        data: this.displayForm,
        loading: true
      })
        .then(data => {
          this.$message.success(this.$t('bidMod.followBidSuccess'))
          this.getListDetail()
        })
        .catch(err => {
          console.log(err)
        })
    },
    backTo () {
      this.$emit(
        'tab-remove',
        'bidingFollowingDetail' + this.$attrs.params.row.bidingName
      )
      this.__setTabTodo('vendorBiddingList.getQuerydata')
    }
  }
}
</script>
<style scoped lang="scss">
.the_doBidingDetail_wrapper /deep/ {
  .the_footer_row {
    float: right;
    font-size: 20px;
  }
  .the_display_content {
    padding: 11px;
    .el-row {
      margin-bottom: 11px;
      font-size: 16px;
      span {
        padding-right: 11px;
        display: inline-block;
        color: #999;
      }
    }
    .the_display_footer {
      text-align: center !important;
    }
  }
  .the_progress {
    height: 120px;
    padding: 11px;
    margin: 20px;
    background: #eee;
    .el-steps {
      padding-bottom: 0;
    }
    .the_footer_row {
      float: right;
      font-size: 20px;
    }
  }
  .table1 {
    height: 250px;
    padding: 5px 11px;
  }
}
</style>
