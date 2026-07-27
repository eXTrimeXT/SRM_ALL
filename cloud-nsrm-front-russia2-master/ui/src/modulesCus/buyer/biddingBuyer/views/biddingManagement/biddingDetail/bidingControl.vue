<template>
  <div class="bidding-control">
    <el-form label-position="left" disabled>
      <SrmRow>
        <!--本轮需投标的供应商数量-->
        <SrmCol :init-col="3">
          <el-form-item :label="$t('cusEntry.bidMod.vendorCountToBid')">
            <el-input v-model="biddingConForm.needTenderNum" />
          </el-form-item>
        </SrmCol>

        <!--已提交投标供应商-->
        <SrmCol :init-col="3">
          <el-form-item :label="$t('cusEntry.bidMod.vendorCountDone')">
            <el-input v-model="biddingConForm.haveTenderNum" />
          </el-form-item>
        </SrmCol>

        <!--本轮投标截止时间-->
        <SrmCol :init-col="3">
          <el-form-item :label="$t('cusEntry.bidMod.bidDeadline')">
            <el-date-picker
              v-model="biddingConForm.orderEndTime"
              :format="$formatDatePickerTime"
              value-format="yyyy-MM-dd HH:mm:ss"
            />
          </el-form-item>
        </SrmCol>
      </SrmRow>
    </el-form>

    <div style="margin-bottom: 16px">
      <el-button
        type="primary"
        :disabled="orderInfos.length==0"
        @click="saveWithdrawReason"
      >
        {{ $t('common.save') }}
      </el-button>
      <!-- 调整投标时间 -->
      <el-button
        type="primary"
        @click="adjustDeadlineDialogVisible=true"
      >
        {{ $t('cusEntry.bidMod.adjustDeadline1') }}
      </el-button>
      <!-- 查看IP地址 -->
      <el-button
        :class="ipSameNum > 0 ? 'view-ip-address-style' : null"
        @click="ipAddressDialogVisible=true"
      >
        {{ $t('cusEntry.bidMod.viewIpAddress', { h: ipSameNum }) }}
      </el-button>
      <!-- 查看调整投标时间历史 -->
      <el-button @click="deadlineHistoryVisible=true">
        {{ $t('cusEntry.supplement20250121.bidTimeHistory') }}
      </el-button>
      <!-- 联系人查重 -->
      <el-button @click="findDuplicateVisible=true">
        {{ $t('cusEntry.supplement20250121.linkManDuplicate') }}
      </el-button>
    </div>

    <el-table
      :data="orderInfos"
      style="width: 100%"
      border
      highlight-current-row
    >
      <!--轮次-->
      <el-table-column
        align="center"
        prop="round"
        fixed="left"
        :label="$t('bidMod.bidingRound')"
        width="80"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        fixed="left"
        prop="sequentialNumber"
        :label="$t('common.sort')"
        width="50"
      />
      <!--供应商编码-->
      <el-table-column
        align="center"
        prop="vendorCode"
        :label="$t('bidMod.vendorCode')"
        min-width="120"
        show-overflow-tooltip
      />
      <!--供应商名称-->
      <el-table-column
        align="center"
        prop="vendorName"
        :label="$t('bidMod.vendorName')"
        min-width="150"
        show-overflow-tooltip
      />
      <!-- 投标联系人 -->
      <el-table-column
        align="center"
        prop="extTenderName"
        :label="$t('cusEntry.reportManagement.bidContactName')"
        width="120"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="extTenderPhone"
        :label="$t('vendorMod.contactMethod')"
        width="120"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="extOrderType"
        :label="$t('bidMod.typeOfTender')"
        width="100"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $getDictLabel('SOU_BID_TYPE', cellValue)"
      />
      <!--投标状态-->
      <el-table-column
        align="center"
        prop="orderStatus"
        :label="$t('bidMod.orderStatus')"
        width="100"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $getDictLabel('SOU_ORDER_STATUS', cellValue)"
      />
      <!--投标时间-->
      <el-table-column
        align="center"
        prop="submitTime"
        :label="$t('cusEntry.biddingSettings.bidTime')"
        width="150"
        :formatter="(row, column, cellValue) => $parseTime(cellValue)"
        show-overflow-tooltip
      />
      <!-- 是否查阅标书 -->
      <el-table-column
        align="center"
        prop="readBidFileFlag"
        :label="$t('cusEntry.bidMod.readBidFileFlag')"
        width="120"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $getDictLabel('YES_OR_NO', cellValue)"
      />
      <!-- 下载标书时间 -->
      <el-table-column
        align="center"
        prop="extDownBidFileTime"
        :label="$t('cusEntry.bidMod.extDownBidFileTime')"
        width="150"
        :formatter="(row, column, cellValue) => $parseTime(cellValue)"
        show-overflow-tooltip
      />
      <!-- 契约认证 -->
      <!-- <el-table-column
        align="center"
        prop="contractVerification"
        :label="$t('cusEntry.supplement20250121.contractVerification')"
        width="120"
        :formatter="(row, column, cellValue) => $getDictLabel('YES_OR_NO', cellValue)"
        show-overflow-tooltip
      /> -->
      <!-- 已投标包名 -->
      <!-- <el-table-column
        v-if="biddingBase.mergeFlag"
        align="center"
        prop="tenderPackageName"
        :label="$t('cusEntry.bidMod.tenderPackageName')"
        width="100"
        show-overflow-tooltip
      /> -->
      <!-- 不参与原因 -->
      <el-table-column
        align="center"
        prop="extNotjoinReason"
        :label="$t('cusEntry.bidMod.withdrawReason')"
        min-width="150"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <el-input v-model="scope.row.extNotjoinReason" :disabled="readonly" />
        </template>
      </el-table-column>
      <!-- 废标原因 -->
      <el-table-column
        align="center"
        prop="rejectReason"
        :label="$t('cusEntry.bidMod.rejectReason')"
        min-width="150"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="withdrawReason"
        :label="$t('bidMod.withdrawReason')"
        min-width="150"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        fixed="right"
        :label="$t('bidMod.operation')"
        width="100"
      >
        <template v-slot="{ row }">
          <!--代理投标-->
          <!-- <el-button
            v-if="judgeProxyQuote(row.orderStatus, projectStatus)"
            type="text"
            :disabled="readonly"
            @click="openProxyQuoteDialog(row)"
          >
            {{ $t("bid_mod.proxyQuoteHandle") }}
          </el-button> -->
          <!-- 废标 -->
          <el-button
            type="text"
            :disabled="readonly"
            @click="confirmDiscard(row)"
          >
            {{ $t('cusEntry.bidMod.rejectBid') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 调整截止时间 -->
    <AdjustDeadlineDialog
      v-if="adjustDeadlineDialogVisible"
      :visible.sync="adjustDeadlineDialogVisible"
      :project-id="biddingBase.projectId"
      :current-end-time="biddingConForm.orderEndTime"
      @success="adjustDeadlineSuccess"
    />
    <!-- 查看IP地址 -->
    <IpAddressDialog
      v-if="ipAddressDialogVisible"
      :visible.sync="ipAddressDialogVisible"
      :ip-address-list="ipAddressList"
    />
    <!-- 查看调整时间历史 -->
    <AdjustDeadlineHistory
      v-if="deadlineHistoryVisible"
      :visible.sync="deadlineHistoryVisible"
      :project-id="biddingBase.projectId"
    />
    <!-- 联系人查重-->
    <findDuplicateDialog
      v-if="findDuplicateVisible"
      :visible.sync="findDuplicateVisible"
      :projectId="biddingBase.projectId"
    />
  </div>
</template>

<script>
/**
 * 投标控制
 */
import { bidBuyerHttp } from 'modcb@/biddingBuyer/api'
import { SOU_PROJECT_STATUS_ENUM } from 'lib@/composition/origin/enum'
import { judgeManagement, judgeProxyQuote } from 'lib@/composition/biddingLts/utils'
import AdjustDeadlineDialog from './bidingControl/adjustDeadlineDialog'
import AdjustDeadlineHistory from './bidingControl/adjustDeadlineHistory'
import IpAddressDialog from './bidingControl/ipAddressDialog'
import findDuplicateDialog from './bidingControl/findDuplicate';
import { transformMQL } from 'lib@/utils/util'

export default {
  name: 'BidingControl',

  components: {
    AdjustDeadlineDialog,
    AdjustDeadlineHistory,
    IpAddressDialog,
    findDuplicateDialog
  },

  props: {
    biddingBase: {
      type: Object,
      default: () => ({})
    },
    projectStatus: {
      // 招标状态
      type: String,
      default: ''
    },
    // 审批状态
    createApprovalStatus: {
      type: String,
      default: ''
    },
    isActiveMenu: {
      type: Boolean,
      required: true
    }
  },

  data () {
    return {
      ipSameNum: 0,
      ipAddressList: [],
      biddingConForm: {
        needTenderNum: '',
        haveTenderNum: '',
        orderEndTime: ''
      },
      orderInfos: [],
      adjustDeadlineDialogVisible: false,
      ipAddressDialogVisible: false,
      deadlineHistoryVisible: false,
      findDuplicateVisible: false,
      vendorInfoList: [],
      judgeProxyQuote
    }
  },

  computed: {
    readonly () {
      // 项目状态=='拟定' && 审批状态=='草稿、审批中'
      return judgeManagement(this.projectStatus, this.createApprovalStatus)
    },
    isShowStartBiddingButton () {
      // 报名截止 投标未开始
      return [SOU_PROJECT_STATUS_ENUM.SIGN_UP_END, SOU_PROJECT_STATUS_ENUM.ORDER_NOT_START].includes(this.projectStatus)
    },
    isShowHandleEndTimeButton () {
      //  接受投标中
      return this.projectStatus === SOU_PROJECT_STATUS_ENUM.ACCEPT_ORDER
    },
    isShowStartNewRound () {
      return this.biddingBase.projectStatus === SOU_PROJECT_STATUS_ENUM.EVALUATING
    }
  },

  watch: {
    isActiveMenu: {
      handler (val) {
        if (val) {
          this.getOrders()
          this.getIpAdress()
        }
      },
      immediate: true
    }
  },

  methods: {
    groupAndSortWithSequentialNumbers (array, groupByField, sortByField) {
      const grouped = array.reduce((acc, obj) => {
        const key = obj[groupByField]
        if (!acc[key]) acc[key] = []
        obj.sequentialNumber = (acc[key].length + 1)
        acc[key].push(obj)
        return acc
      }, {})
      const result = Object.keys(grouped).map(key => {
        const group = grouped[key]
        group.sort((a, b) => a[sortByField] - b[sortByField]);
        group.forEach((item, index) => {
          item.sequentialNumber = index + 1
        })
        return { [groupByField]: key, items: group }
      })
      // 反转分组数组以实现倒序
      result.reverse()
      return result
    },
    // 查询供应商是否认证
    getVendorInfo (vendorIdList, orderInfos) {
      const searchData = transformMQL.save(
        'CompanyInfo',
        vendorIdList,
        'read',
        {
          'companyId': {},
          'contractVerification': {},
          'allowBidWithoutSealFlag': {},
          'allowQuotationWithoutSealFlag': {}
        }
      )
      this.$http({
        url: '/api-sup/api-ql/CompanyInfo/read',
        method: 'POST',
        data: searchData,
        loading: true
      }).then(res => {
        if (res && res.data) {
          this.vendorInfoList = res.data
          this.orderInfos = orderInfos.map(resItem => {
            const { contractVerification } = this.vendorInfoList.find(it => it.companyId == resItem.vendorId) || {}
            return { ...resItem, contractVerification }
          })
          const sortList = this.groupAndSortWithSequentialNumbers(this.orderInfos, 'round', 'round')
          let tempArr = []
          sortList.forEach(item => {
            item.items.forEach(arr => {
              tempArr.push(arr)
            })
          })
          this.orderInfos = tempArr
        }
      })
    },
    // 查看ip地址
    getIpAdress () {
      this.$http({
        url: '/api-sou/bids/ip/address/ipAddress/zbList',
        method: 'POST',
        data: { projectId: this.biddingBase.projectId },
        loading: true
      }).then(res => {
        if (res && res.data) {
          this.ipSameNum = res.data.num || 0
          this.ipAddressList = res.data.ipAddressList
        }
      })
    },
    // 保存不参与原因
    saveWithdrawReason () {
      bidBuyerHttp.control.saveWithdrawReason(this.orderInfos).then(res => {
        this.$message.success(res.message)
        this.getOrders()
      })
    },
    // 确认废标
    confirmDiscard (row) {
      this.$prompt(this.$t('cusEntry.bidMod.rejectReason'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        closeOnClickModal: false,
        inputPlaceholder: this.$t('cusEntry.common.pleaseFill'),
        inputValidator: (value) => {
          if (!value) {
            return this.$t('cusEntry.bidMod.inputRejectReason')
          }
        } }).then(({ value }) => {
        const params = {
          orderId: row.orderId,
          rejectReason: value
        }
        bidBuyerHttp.control.confirmDiscard(params).then(res => {
          this.$message.success(res.message)
          this.getOrders()
        })
      })
    },
    /* 查询数据 */
    getOrders () {
      // 查询投标信息
      bidBuyerHttp.control.orders(this.biddingBase.projectId).then(res => {
        if (res && res.data) {
          this.biddingConForm = res.data
        }
      })
      // 查询表格明细数据
      bidBuyerHttp.control.orderInfos(this.biddingBase.projectId).then(res => {
        if (res && res.data) {
          let vendorIdList = res.data.map(item => item.vendorId)
          if (this.vendorInfoList.length !== 0) {
            this.orderInfos = res.data.map(resItem => {
              const { contractVerification } = this.vendorInfoList.find(it => it.companyId == resItem.vendorId) || {}
              return { ...resItem, contractVerification }
            })
            const sortList = this.groupAndSortWithSequentialNumbers(this.orderInfos, 'round', 'round')
            let tempArr = []
            sortList.forEach(item => {
              item.items.forEach(arr => {
                tempArr.push(arr)
              })
            })
            this.orderInfos = tempArr
          } else this.getVendorInfo(vendorIdList, res.data)
        }
      })
    },

    /* 调整时间成功 */
    adjustDeadlineSuccess () {
      this.$emit('refresh')
      this.$emit('refresh-process')
      this.getOrders()
    },

    /* 立即开始投标 */
    async startBidding () {
      const confirmResult = await this.$confirm(this.$t('bidMod.biddingManagementBuyer.confirmStartBiddingTitle'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => { /* nothing */ })

      if (confirmResult !== 'confirm') {
        return
      }

      const response = await bidBuyerHttp.control.changeOrderStartTime({
        projectId: this.biddingBase.projectId,
        startNow: true
      })
      if (response) {
        this.$message.success(this.$t('common.successSubmit'))
        // 更新本页签数据
        await this.getOrders()
        // 更新基础数据
        this.$emit('refresh')
      }
    },

    /* 发起新一轮成功 */
    async startNewRoundSuccess () {
      // 更新基础数据
      this.$emit('refresh')
      // 更新本页签数据
      await this.getOrders()
    },

    /* 立即结束投标 */
    async endImmediatelyDoBidding () {
      const confirmResult = await this.$confirm(this.$t('bidMod.biddingManagementBuyer.confirmEndBiddingTitle'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => { /* nothing */ })

      if (confirmResult !== 'confirm') {
        return
      }

      const response = await bidBuyerHttp.control.changeOrderEndTime({
        projectId: this.biddingBase.projectId,
        endNow: true
      })
      if (response) {
        // 立即结束投标成功！
        this.$message.success(this.$t('bidMod.endBiddingImmediate'))
        await this.getOrders()
        this.$emit('refresh-process')
        this.$emit('refresh')
      }
    },

    /* 打开代理报价弹窗 */
    openProxyQuoteDialog (row) {
      this.proxyQuoteParams = {
        visible: true,
        projectId: this.biddingBase.projectId,
        souName: this.biddingBase.souName,
        vendorId: row.vendorId
      }
      this.proxyQuoteDialogVisible = true
    }
  }
}
</script>

<style lang="scss" scoped>
.bidding-control {
  padding-top: 8px;
  .view-ip-address-style {
    background-color: red;
    color: white;
  }
}
</style>
