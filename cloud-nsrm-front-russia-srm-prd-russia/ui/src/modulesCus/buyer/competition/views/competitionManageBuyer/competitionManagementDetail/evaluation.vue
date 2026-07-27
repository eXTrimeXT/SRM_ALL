<!--suppress AllyHtmlVueInspection, AllyHtmlVueInspection -->
<template>
  <el-container
    class="flex-container"
    direction="vertical"
    style="padding-top: 10px;"
  >
    <el-tabs
      v-model="activeName"
      type="card"
      @tab-click="tabClick"
    >
      <el-tab-pane
        v-if="!approvalFlag"
        :label="$t('cusEntry.competition.historyQuote')"
        name="historyQuote"
      >
        <SearchForm
          :require-info-data="requireInfoData"
          :vendor-info-data="vendorInfoData"
          @search="getQueryData"
        />
        <AuthorityButton
          type="primary"
          code="inq:competition:export"
          @click="exportHandle('历史报价', 'hisDownloadExcel')"
        >
          {{ $t('common.export') }}
        </AuthorityButton>
        <el-table
          border
          :data="historyQuoteList"
          max-height="250"
          style="margin-top: 8px;"
        >
          <el-table-column
            type="index"
            align="center"
            width="50"
            fixed="left"
          />
          <!--物资名称-->
          <el-table-column
            align="center"
            :label="$t('cusEntry.competition.materialName')"
            prop="itemDesc"
            min-width="120"
            show-overflow-tooltip
          />
          <!--供应商编码-->
          <el-table-column
            align="center"
            :label="$t('bidMod.vendorCode')"
            prop="vendorCode"
            min-width="120"
            show-overflow-tooltip
          />
          <!--供应商名称-->
          <el-table-column
            align="center"
            prop="vendorName"
            :label="$t('bidMod.vendorName')"
            min-width="120"
            show-overflow-tooltip
          />
          <!--报价Ip-->
          <el-table-column
            align="center"
            :label="$t('cusEntry.competition.quoteIp')"
            prop="lastUpdatedByIp"
            min-width="120"
            show-overflow-tooltip
          />
          <!--报价时间-->
          <el-table-column
            align="center"
            :label="$t('cusEntry.competition.quoteTime')"
            prop="lastUpdateDate"
            min-width="120"
            show-overflow-tooltip
          />
          <!--报价单价-->
          <el-table-column
            align="center"
            :label="$t('cusEntry.competition.quotePrice')"
            prop="orderTaxPrice"
            min-width="120"
            show-overflow-tooltip
          />
        </el-table>
      </el-tab-pane>
      <el-tab-pane
        v-if="!approvalFlag"
        :label="$t('cusEntry.competition.quoteResult')"
        name="quoteResult"
      >
        <AuthorityButton
          type="primary"
          code="inq:competition:export"
          @click="exportHandle('报价结果', 'qouteDownloadExcel')"
        >
          {{ $t('common.export') }}
        </AuthorityButton>
        <el-table
          border
          :data="quoteResultList"
          max-height="250"
          style="margin-top: 8px;"
        >
          <el-table-column
            type="index"
            align="center"
            width="50"
            fixed="left"
          />
          <!--物资名称-->
          <el-table-column
            align="center"
            :label="$t('cusEntry.competition.materialName')"
            prop="itemDesc"
            min-width="120"
            show-overflow-tooltip
          />
          <!-- 所属单位 -->
          <el-table-column
            align="center"
            :label="$t('cusEntry.competition.belongCompany')"
            prop="affiliatedUnit"
            min-width="120"
            show-overflow-tooltip
          />
          <!-- 月约产量 -->
          <el-table-column
            align="center"
            :label="$t('cusEntry.competition.monthProduct')"
            prop="monthlyProduction"
            min-width="120"
            show-overflow-tooltip
          />
          <!--最高价-->
          <el-table-column :label="$t('cusEntry.competition.topPrice')" align="center">
            <el-table-column
              prop="maxVendorName"
              :label="$t('cusEntry.competition.name')"
              width="100"
              align="center"
              show-overflow-tooltip
            />
            <el-table-column
              prop="maxPrice"
              align="center"
              :label="$t('cusEntry.competition.price')"
              width="100"
              show-overflow-tooltip
            />
          </el-table-column>
          <!--次高价-->
          <el-table-column :label="$t('cusEntry.competition.secondPrice')" align="center">
            <el-table-column
              prop="secondVendorName"
              :label="$t('cusEntry.competition.name')"
              width="100"
              align="center"
              show-overflow-tooltip
            />
            <el-table-column
              prop="secondPrice"
              align="center"
              :label="$t('cusEntry.competition.price')"
              width="100"
              show-overflow-tooltip
            />
          </el-table-column>
          <!--第三高-->
          <el-table-column :label="$t('cusEntry.competition.thirdPrice')" align="center">
            <el-table-column
              prop="thirdVendorName"
              :label="$t('cusEntry.competition.name')"
              width="100"
              align="center"
              show-overflow-tooltip
            />
            <el-table-column
              prop="thirdPrice"
              align="center"
              :label="$t('cusEntry.competition.price')"
              width="100"
              show-overflow-tooltip
            />
          </el-table-column>
          <!--上期中标供应商-->
          <el-table-column :label="$t('cusEntry.competition.lastWinSupplier')" align="center">
            <el-table-column
              prop="periodVendorName"
              :label="$t('cusEntry.competition.name')"
              width="100"
              align="center"
              show-overflow-tooltip
            />
            <el-table-column
              prop="periodPrice"
              align="center"
              :label="$t('cusEntry.competition.price')"
              width="100"
              show-overflow-tooltip
            />
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane :label="$t('cusEntry.competition.priceApproval')" name="priceApproval">
        <AuthorityButton
          type="primary"
          code="inq:competition:export"
          @click="exportHandle('定价审批', 'downloadExcel')"
        >
          {{ $t('common.export') }}
        </AuthorityButton>
        <el-form :model="priceApprovalForm">
          <srm-row :gutter="50">
            <srm-col :init-col="3">
              <el-form-item :label="$t('cusEntry.competition.file')">
                <SrmCommonFile
                  :extra-data="fileInfo"
                  :default-file="{
                    fileId: priceApprovalForm.selectDocId,
                    fileName: priceApprovalForm.selectFileName
                  }"
                  :readonly="isPageView || readonly"
                  @on-change="({file}) => handleUploadSuccess(file)"
                />
              </el-form-item>
            </srm-col>
          </srm-row>
        </el-form>
        <el-table
          border
          :data="priceApprovalList"
          max-height="250"
        >
          <el-table-column
            type="index"
            align="center"
            width="50"
            fixed="left"
          />
          <!--物资名称-->
          <el-table-column
            align="center"
            :label="$t('cusEntry.competition.materialName')"
            prop="itemDesc"
            min-width="120"
            show-overflow-tooltip
          />
          <!-- 所属单位 -->
          <el-table-column
            align="center"
            :label="$t('cusEntry.competition.belongCompany')"
            prop="affiliatedUnit"
            min-width="120"
            show-overflow-tooltip
          />
          <!--状态-->
          <el-table-column
            align="center"
            :label="$t('cusEntry.competition.approvalStatus')"
            prop="resultStatus"
            min-width="100"
            :formatter="row => {
              return row.resultStatus ? $getDictLabel('SOU_APPROVAL_STATUS', row.resultStatus) : ''
            }"
          />
          <!-- 月约产量 -->
          <el-table-column
            align="center"
            :label="$t('cusEntry.competition.monthProduct')"
            prop="monthlyProduction"
            min-width="120"
            show-overflow-tooltip
          />
          <!-- 计量单位 -->
          <el-table-column
            align="center"
            :label="$t('cusEntry.competition.measurementUnit')"
            prop="meteringUnit"
            min-width="120"
            show-overflow-tooltip
          />
          <!--最高价-->
          <el-table-column :label="$t('cusEntry.competition.topPrice')" align="center">
            <el-table-column
              prop="maxVendorName"
              :label="$t('cusEntry.competition.name')"
              width="100"
              align="center"
              show-overflow-tooltip
            />
            <el-table-column
              prop="maxPrice"
              align="center"
              :label="$t('cusEntry.competition.price')"
              width="100"
              show-overflow-tooltip
            />
          </el-table-column>
          <!--次高价-->
          <el-table-column :label="$t('cusEntry.competition.secondPrice')" align="center">
            <el-table-column
              prop="secondVendorName"
              :label="$t('cusEntry.competition.name')"
              width="100"
              align="center"
              show-overflow-tooltip
            />
            <el-table-column
              prop="secondPrice"
              align="center"
              :label="$t('cusEntry.competition.price')"
              width="100"
              show-overflow-tooltip
            />
          </el-table-column>
          <!--第三高-->
          <el-table-column :label="$t('cusEntry.competition.thirdPrice')" align="center">
            <el-table-column
              prop="thirdVendorName"
              :label="$t('cusEntry.competition.name')"
              width="100"
              align="center"
              show-overflow-tooltip
            />
            <el-table-column
              prop="thirdPrice"
              align="center"
              :label="$t('cusEntry.competition.price')"
              width="100"
              show-overflow-tooltip
            />
          </el-table-column>
          <!--上期中标供应商-->
          <el-table-column :label="$t('cusEntry.competition.lastWinSupplier')" align="center">
            <el-table-column
              prop="periodVendorName"
              :label="$t('cusEntry.competition.name')"
              width="100"
              align="center"
              show-overflow-tooltip
            />
            <el-table-column
              prop="periodPrice"
              align="center"
              :label="$t('cusEntry.competition.price')"
              width="100"
              show-overflow-tooltip
            />
          </el-table-column>
          <el-table-column
            :label="$t('cusEntry.competition.priceDifferenceRate')"
            prop="differenceRate"
            align="center"
            min-width="120"
            show-overflow-tooltip
          />
          <el-table-column
            align="center"
            prop="orderRemark"
            :label="$t('cusEntry.competition.remark')"
            min-width="120"
          >
            <template slot-scope="scope">
              <el-input
                v-if="!readonly && !isPageView"
                v-model="scope.row.orderRemark"
              />
              <span v-else>{{ scope.row.orderRemark }}</span>
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="winVendorId"
            label="中标供应商"
            min-width="120"
          >
            <template slot-scope="scope">
              <el-select
                v-if="approvalFlag && ableSelect"
                v-model="scope.row.winVendorId"
                @change="val => winVendorChange(val,scope.row)"
              >
                <el-option
                  v-for="item in scope.row.vendorList"
                  :key="item.vendorId"
                  :label="item.vendorName"
                  :value="item.vendorId"
                />
              </el-select>
              <span v-else>{{ scope.row.winVendorName }}</span>
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="winReason"
            label="中标原因"
            min-width="120"
          >
            <template slot-scope="scope">
              <el-input
                v-if="approvalFlag && ableSelect"
                v-model="scope.row.winReason"
              />
              <span v-else>{{ scope.row.winReason }}</span>
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="failureBidFlag"
            label="是否流标"
            min-width="120"
          >
            <template slot-scope="scope">
              <dict-select
                v-if="approvalFlag && ableSelect"
                v-model="scope.row.failureBidFlag"
                code="YES_OR_NO"
              />
              <span v-else>{{ $getDictLabel('YES_OR_NO', scope.row.failureBidFlag) }}</span>
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="failureReason"
            label="流标原因"
            min-width="120"
          >
            <template slot-scope="scope">
              <el-input
                v-if="approvalFlag && ableSelect"
                v-model="scope.row.failureReason"
              />
              <span v-else>{{ scope.row.failureReason }}</span>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </el-container>
</template>

<script>
import { carBuyerHttp } from 'modcb@/competition/api'
import { BUSINESS_TYPE_ENUM } from 'lib@/composition/origin/enum'
import TableView from 'lib@/components/Table/TableView'
import SearchForm from './evaluation/searchForm'
import StartNewRound from 'lib@/composition/competition/startNewRound'
import ExpenseVerification from './evaluation/expenseVerification'
import SetNewRoundStartingPrice from './evaluation/setNewRoundStartingPrice.vue'
import { transformMQL } from 'lib@/utils/util'
import { downloadFileLink } from 'lib@/utils/file'
export default {
  name: 'Evaluation',

  components: {
    SearchForm,
    TableView,
    StartNewRound,
    ExpenseVerification,
    SetNewRoundStartingPrice
  },

  props: {
    baseInfo: {
      type: Object,
      default: () => ({})
    },
    isActiveMenu: {
      type: Boolean,
      required: true
    },
    // 当前启用的节点
    enabledNodeMenu: {
      type: Array,
      default: () => []
    },
    // 物料需求数据
    requireInfoData: {
      type: Array,
      default: () => []
    },
    // 邀请供应商数据
    vendorInfoData: {
      type: Array,
      default: () => []
    },
    isPageView: {
      type: Boolean,
      default: false
    },
    approvalFlag: {
      type: Boolean,
      default: false
    },
    ableSelect: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      readonly: false,
      // 文件上传配置信息
      fileInfo: {
        fileModular: 'inq', // 文件所属模块 -》基础模块
        fileFunction: 'competition', // 文件所属功能
        fileType: 'images' // 文件所属类型
      },
      priceApprovalForm: {
        fileType: 'SELECT',
        selectFileName: '',
        selectDocId: null
      },
      historyQuoteList: [], // 历史报价
      quoteResultList: [], // 报价结果
      priceApprovalList: [], // 定价审批
      activeName: 'historyQuote',
      multipleSelection: [],
      queryParam: {},
      startNewRoundVisible: false,
      expenseVerificationVisible: false,
      setNewRoundStartingPriceVisible: false,
      fromStartNewRound: {},
      BUSINESS_TYPE_ENUM,
      prevRoundData: {}// 发起新一轮数据
    }
  },

  watch: {
    isActiveMenu: {
      handler (val) {
        if (val) {
          this.getQueryData()
        }
      },
      immediate: true
    }
  },

  created () {
    // 从BPM审批链接过来
    if (this.approvalFlag) {
      this.activeName = 'priceApproval'
      this.getPriceApprovalData()
    }
  },

  methods: {
    // 导出
    exportHandle (name, url) {
      downloadFileLink(
        `/api-pj/buyer/comp/select/${url}/${this.baseInfo.projectId}`,
        `${name}.xlsx`,
      ).catch(() => {
        this.$message.error(this.$t('purchaseDemand.downloadFail'))
      })
    },
    winVendorChange (val, row) {
      row.winVendorId = val
      let vendorObj = val ? row.vendorList.find(it => it.vendorId == val) || {} : {}
      row.winVendorName = vendorObj.vendorName || ''
    },
    getPriceApprovalData () {
      const data = {
        projectId: this.baseInfo.projectId
      }
      carBuyerHttp.select.getPriceApproval(data).then(res => {
        if (res.data) {
          let priceApprovalList = res.data.list || []
          this.priceApprovalList = priceApprovalList.map(item => {
            item.vendorList = [
              { vendorId: item.maxVendorId, vendorName: item.maxVendorName },
              { vendorId: item.secondVendorId, vendorName: item.secondVendorName },
              { vendorId: item.thirdVendorId, vendorName: item.thirdVendorName }
            ]
            if (!item.winVendorId) {
              item.winVendorId = item.maxVendorId
              item.winVendorName = item.maxVendorName
            }
            return item
          })
          this.$emit('getData', this.priceApprovalList)
          this.readonly = res.data.list?.[0]?.resultStatus === 'SUBMITTED'
          if (res.data.list.length > 0) {
            const {
              selectFileList
            } = res.data.list[0]
            if (selectFileList[0]) {
              this.priceApprovalForm = selectFileList[0]
            }
          }
        }
      })
    },
    /* 切换页签 */
    tabClick (tabName) {
      const { name } = tabName
      this.$emit('setFooterBtn', name)
      if (name === 'quoteResult') {
        const data = {
          projectId: this.baseInfo.projectId
        }
        carBuyerHttp.select.getQuoteResult(data).then(res => {
          if (res.data) {
            this.quoteResultList = res.data.list || []
          }
        })
      } else if (name === 'priceApproval') {
        this.getPriceApprovalData()
      }
    },
    /* 附件上传 */
    handleUploadSuccess (file) {
      const { fileId = null, fileName = null } = file || {}
      this.priceApprovalForm.selectDocId = fileId
      this.priceApprovalForm.selectFileName = fileName
    },
    /* 搜索 */
    getQueryData (params = {}) {
      const data = {
        projectId: this.baseInfo.projectId,
        ...params
      }
      carBuyerHttp.select.getHistoryPrice(data).then(res => {
        if (res.data) {
          this.historyQuoteList = res.data.list || []
        }
      })
    },

    transformTableData (data) {
      const records = data.data.records
      data.data.records = records.map(item => ({
        ...item,
        ...item.auctSouOrderItem,
        ...item.souItem,
        ...item.souItem.auctSouItem,
        ...item.souVendor
      }))
      console.log('reocrds::', data.data.records)
      return data
    },

    /* 记录表格选中行 */
    handleSelectionChange (val) {
      this.multipleSelection = val
    },

    /* 中标 */
    async changeSelectStatus (type) {
      if (this.multipleSelection.length === 0) {
        this.$message.warning(this.$t('bidMod.msgSelData'))
        return false
      }

      const selects = this.multipleSelection.map(item => {
        return {
          orderItemId: item.orderItemId,
          winAmount: item.winAmount
        }
      })

      if (!selects || selects.length === 0) {
        return
      }

      const toWin = type === 'win'
      // if (toWin) {
      //   // 允许为0
      //   if (selects.find(item => !item.winAmount && item.winAmount !== 0)) {
      //     // 请先填写中标数量
      //     return this.$message.warning(this.$t('vendorMod.msgSelBidder'))
      //   }
      // }

      let transformParams = transformMQL.save('AuctSouProjectForBuyer', [{
        projectId: this.baseInfo.projectId,
        selects,
        toWin: type === 'win'
      }], 'changeSelectStatus')
      // 发起中标
      const response = await carBuyerHttp.select.changeSelectStatus(transformParams)

      if (response) {
        this.$message.success(this.$t('common.successSubmit'))

        this.getQueryData()
      }
    },

    /* 生成价格审批单 */
    async signCreate () {
      let transformParams = transformMQL.save('AuctSouProjectForBuyer', [{
        projectId: this.baseInfo.projectId
      }], 'createPricingApproval')
      const response = await carBuyerHttp.control.createPricingApproval(transformParams)

      if (response) {
        this.getQueryData()

        if (response.data) {
          // 定点会签单创建成功，是否跳转到单据页面？
          const confirmResult = await this.$confirm('价格审批单创建成功，是否跳转到单据页面？', {
            confirmButtonText: this.$t('common.confirm'),
            cancelButtonText: this.$t('common.cancel'),
            type: 'success'
          })

          if (confirmResult !== 'confirm') {
            return
          }

          const { approvalHeaderId, approvalNo } = response.data.records[0] || {}
          // 传递参数到定点会签单页
          this.$router.push({
            name: 'priceApproval',
            params: {
              from: 'fromFun',
              funName: 'priceApproval',
              formId: approvalHeaderId,
              formNo: approvalNo
            }
          })
        }
      }
    },

    /* 生成定点通知单 */
    async noticeCreate () {
      let transformParams = transformMQL.save('AuctSouProjectForBuyer', [{
        projectId: this.baseInfo.projectId
      }], 'openResult')
      const response = await carBuyerHttp.select.openResult(transformParams)

      if (response) {
        this.getQueryData()
        this.$message.success(this.$t('common.successSubmit'))
      }
    },

    /* 打开设置新一轮起拍价 */
    async openSetNewRoundStartingPrice (data) {
      console.log('data:::', data)
      this.prevRoundData = data
      this.setNewRoundStartingPriceVisible = true
    },

    /* 提交发起新一轮 */
    async submitStartNewRound (data, flag) {
      let params = {}
      if (flag === 'Y') {
        const { formData, vendorData } = this.prevRoundData
        params = {
          projectId: this.baseInfo.projectId,
          orderStartTime: formData.orderStartTime,
          orderEndTime: formData.orderEndTime,
          startNow: true,
          newVendors: vendorData,
          auctSouItemList: data.souItemList
        }
      } else {
        const { formData, vendorData } = data
        params = {
          projectId: this.baseInfo.projectId,
          orderStartTime: formData.orderStartTime,
          orderEndTime: formData.orderEndTime,
          startNow: true,
          newVendors: vendorData,
          auctSouItemList: []
        }
      }
      console.log('params:::', params)
      let transformParams = transformMQL.save('AuctSouProjectForBuyer', [params], 'startNewRound')
      const response = await carBuyerHttp.control.startNewRound(transformParams)

      if (response) {
        this.$message.success(this.$t('common.successSubmit'))
        this.startNewRoundVisible = false
        this.setNewRoundStartingPriceVisible = false
        this.$emit('refresh')
      }
    }
  }
}
</script>
