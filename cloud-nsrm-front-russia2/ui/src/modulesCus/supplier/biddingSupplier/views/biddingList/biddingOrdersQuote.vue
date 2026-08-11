<template>
  <el-container class="flex-container the_doBidingDetail_wrapper" direction="vertical">
    <el-main>
      <!--距离投标截止倒计时-->
      <div v-if="!readonly" class="cur-quote-deadline">
        <DynamicCutoffTime
          :label="$t('cusEntry.biddingSettings.orderEndTime')"
          :deadline-time="biddingData.orderEndTime"
        />
      </div>
      <!--技术投标-->
      <TechInfo
        v-if="['t','all'].includes(type)"
        :techFlag="type=='t'"
        :readonly="readonly"
        :vendor-file-list.sync="techOrderFileList"
        :merge-flag="mergeFlag"
        :pack-name-list="packNameList"
        :orderId="orderId"
        :contractVerification="contractVerification"
        @refresh="getOrderDetail('refresh')"
      />
      <!-- 商务标投标或投标 -->
      <div v-if="['b','all'].includes(type)" style="margin-bottom: 40px;">
        <div style="font-size:14px;font-weight:bold;margin-bottom:14px">
          {{ $t('bidMod.quoteInfo') }}
        </div>
        <el-table
          border
          :data="orderItemList"
          style="width: 100%"
          max-height="500"
        >
          <el-table-column
            align="center"
            type="index"
            fixed="left"
            :label="$t('common.sort')"
            width="50"
          />
          <!-- 包名 -->
          <!-- <el-table-column
            v-if="mergeFlag"
            align="center"
            prop="extPackageName"
            :label="$t('cusEntry.biddingSettings.bagName')"
            show-overflow-tooltip
            min-width="150px"
          /> -->
          <el-table-column
            v-for="(item,index) in templateData"
            :key="item.columnCode"
            :prop="item.columnCode"
            :label="item.columnName"
            align="center"
            min-width="160"
            :render-header="item.starFlag ? _addStarToColumn : null"
            show-overflow-tooltip
          >
            <template slot-scope="scope">
              <el-input
                v-if="templateData[index].inputFlag && templateData[index].columnType == 'STRING'"
                v-model="scope.row[item.columnCode]"
                :disabled="readonly"
              />
              <el-input-number
                v-else-if="templateData[index].columnSource=='VENDOR' && templateData[index].columnType == 'NUMBER' && templateData[index].amountFlag"
                v-model="scope.row[item.columnCode]"
                :precision="2"
                :disabled="readonly || !templateData[index].inputFlag"
                style="width:100%"
                :controls="false"
                :min="0"
                @change="templateData[index].changeEvent(scope.row)"
              />
              <el-input-number
                v-else-if="templateData[index].inputFlag && templateData[index].columnType == 'NUMBER'"
                v-model="scope.row[item.columnCode]"
                :disabled="readonly"
                style="width:100%"
                :controls="false"
                :min="0"
                @change="templateData[index].changeEvent(scope.row)"
              />
              <dict-select
                v-else-if="templateData[index].columnType == 'LIST'"
                v-model="scope.row[item.columnCode]"
                :code="templateData[index].code"
                :disabled="readonly || !templateData[index].inputFlag"
                @change-value="(val, dictItem) => templateData[index].changeEvent(val, scope.row, dictItem)"
              />
              <span v-else>{{ scope.row[item.columnCode] }}</span>
            </template>
          </el-table-column>
          <!-- <el-table-column
            align="center"
            prop="extExchangeRate"
            :label="$t('bidMod.appraisRate')"
            min-width="150"
            show-overflow-tooltip
          /> -->
          <!--投标状态 查看投标历史时出现-->
          <el-table-column
            v-if="readonly"
            align="center"
            prop="orderStatus"
            :label="$t('bidMod.orderStatus')"
            :formatter="(row, column, cellValue) => $getDictLabel('SOU_ORDER_STATUS', cellValue)"
            show-overflow-tooltip
            minWidth="150px"
          />
          <!--投标时间 查看投标历史时出现-->
          <el-table-column
            v-if="readonly"
            align="center"
            prop="submitTime"
            :label="$t('cusEntry.biddingSettings.bidTime')"
            width="150"
            show-overflow-tooltip
          />
        </el-table>

        <div style="margin-top: 14px">
          <div style="font-size:14px;font-weight:bold;margin-bottom:14px">
            <!-- 商务报价单 -->
            {{ $t('cusEntry.biddingSettings.businessFile') }}
          </div>
          <!-- <div>
            <el-button
              v-if="!readonly"
              type="primary"
              @click="addRow"
            >
              上传报价单
            </el-button>
            <el-button
              v-if="!readonly"
              type="primary"
              @click="addOtherRow"
            >
              上传其他文件
            </el-button>
            <el-button v-if="!readonly && contractVerification=='Y'" type="primary" @click="onlineSign('BID_BUSINESS')">
              {{ $t("cusEntry.biddingSettings.onlineSign") }}
            </el-button>
            <el-button
              v-if="!readonly && contractVerification=='Y'"
              type="primary"
              @click="getOrderDetail('refresh')"
            >
              刷新签署状态
            </el-button>
          </div> -->
        </div>
        <el-table
          border
          :data="busOrderFileList"
          style="width: 100%"
          max-height="200"
        >
          <el-table-column
            align="center"
            type="index"
            fixed="left"
            :label="$t('common.sort')"
            width="50"
          />
          <el-table-column
            align="center"
            prop="orderFileName"
            :label="$t('bidMod.fileName')"
            :render-header="_addStarToColumn"
            show-overflow-tooltip
            minWidth="200px"
          >
            <template slot-scope="scope">
              <SrmCommonFile
                :extra-data="fileInfo"
                :default-file="{
                  fileId: scope.row.orderDocId,
                  fileName: scope.row.orderFileName
                }"
                :readonly="readonly"
                @on-change="value => fileChange(value, scope.$index)"
              />
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="fileType"
            :label="$t('dataConfMod.fileExportType')"
            :formatter="(row, column, cellValue) => $getDictLabel('SOU_BUS_FILE_CONFIG_TYPE', cellValue)"
            show-overflow-tooltip
            minWidth="150px"
          />
          <!-- 标段 -->
          <el-table-column
            v-if="busOrderFileList.some(item => !!item.extBidSection)"
            align="center"
            prop="extBidSection"
            :label="$t('cusEntry.bidMod.extBidSection')"
            show-overflow-tooltip
            minWidth="150px"
          />
          <!--包名-->
          <!-- <el-table-column
            v-if="mergeFlag"
            align="center"
            prop="extPackageNameList"
            :label="$t('cusEntry.biddingSettings.bagName')"
            :render-header="_addStarToColumn"
            show-overflow-tooltip
            minWidth="150px"
          >
            <template slot-scope="scope">
              <el-select
                v-if="scope.row.fileType == 'BUS_BID'"
                v-model="scope.row.extPackageNameList"
                :disabled="readonly"
                multiple
              >
                <el-option
                  v-for="(item,index) in packNameList"
                  :key="index"
                  :label="item"
                  :value="item"
                />
              </el-select>
            </template>
          </el-table-column> -->
          <el-table-column
            align="center"
            prop="orderRemark"
            :label="$t('bidMod.remark')"
            minWidth="150px"
          >
            <template v-slot="scope">
              <el-input v-model="scope.row.orderRemark" :disabled="readonly" />
            </template>
          </el-table-column>
          <!-- <el-table-column
            align="center"
            prop="extSignStatus"
            label="签署状态"
            :formatter="(row, column, cellValue) => $getDictLabel('SOU_BID_SIGN_STATUS', cellValue)"
            show-overflow-tooltip
            minWidth="150px"
          /> -->
          <!--投标状态 查看投标历史时出现-->
          <el-table-column
            v-if="readonly"
            align="center"
            prop="extOrderStatus"
            :label="$t('bidMod.orderStatus')"
            :formatter="(row, column, cellValue) => $getDictLabel('SOU_ORDER_STATUS', cellValue)"
            show-overflow-tooltip
            minWidth="150px"
          />
          <!--投标时间 查看投标历史时出现-->
          <el-table-column
            v-if="readonly"
            align="center"
            prop="extSubmitTime"
            :label="$t('cusEntry.biddingSettings.bidTime')"
            show-overflow-tooltip
            minWidth="150px"
            :formatter="(row, column, cellValue) => $parseTime(cellValue)"
          />
          <!--操作-->
          <!-- <el-table-column
            v-if="!readonly"
            :label="$t('common.operation')"
            fixed="right"
            width="80"
          >
            <template v-slot="scope">
              <el-button
                type="text"
                @click="deleteRow(scope.$index)"
              >
                {{ $t("common.delete") }}
              </el-button>
            </template>
          </el-table-column> -->
        </el-table>
      </div>

      <CToolbar>
        <template slot="right">
          <el-button @click="backBill">
            {{ $t('common.close') }}
          </el-button>
          <el-button
            v-if="!readonly"
            type="primary"
            @click="saveOrSubmit('SAVE')"
          >
            {{ $t("common.staging") }}
          </el-button>
          <el-button
            v-if="!readonly"
            type="primary"
            @click="submitOrder('SUBMIT')"
          >
            {{ $t('common.submit') }}
          </el-button>
        </template>
      </CToolbar>

      <!--上传签章文件-->
      <EleSignDialog
        :visible.sync="eleSignDialogVisible"
        :orderId="orderId"
        :orderType="orderType"
      />
    </el-main>
  </el-container>
</template>

<script>
import { bidSupplierHttp } from 'modcs@/biddingSupplier/api'
import CToolbar from 'lib@/components/c-toolbar'
import DynamicCutoffTime from 'lib@/components/dynamic-cutoff-time'
import EleSignDialog from './composition/eleSignDialog'
import TechInfo from './techInfo'
import { transformMQL } from 'lib@/utils/util'
import { tabTodoMixin } from '@/utils/mixins'
import { isNull, parseTime } from '@/utils'
import Big from 'big.js'

export default {
  name: 'BiddingOrdersQuote',

  components: {
    CToolbar,
    DynamicCutoffTime,
    TechInfo,
    EleSignDialog
  },

  mixins: [tabTodoMixin],

  data () {
    return {
      type: 'all',
      orderType: '',
      readonly: false,
      projectId: null,
      orderId: null,
      mergeFlag: false,
      biddingData: {
        orderEndTime: null
      },
      fileInfo: {
        uploadType: 'DEF',
        sourceType: 'WEB_APP',
        fileModular: 'bid',
        fileFunction: 'quote',
        fileType: 'images'
      },
      packNameList: [],
      techOrderFileList: [],
      busOrderFileList: [],
      orderItemList: [],
      templateData: [],
      eleSignDialogVisible: false,
      contractVerification: 'N',
      allowBidWithoutSealFlag: 'N',
      allowQuotationWithoutSealFlag: 'N',
      publishDate: ''
    }
  },

  async created () {
    const { flag, row, type } = this.$attrs.params
    this.type = type
    this.readonly = flag == 'view'
    this.projectId = row.projectId
    this.orderId = row.orderId
    this.publishDate = parseTime(row.publishTime, '{y}-{m}-{d}', true)
    this.mergeFlag = row.mergeFlag
    // 有合并招标标识才查询包名下拉项
    this.mergeFlag && await this.getProjectPackName()
    this.type !== 't' && await this.getTemplateData()
    this.getOrderDetail()
    // this.getVendorInfo(row.vendorId)
  },

  methods: {
    // 查询供应商是否认证
    getVendorInfo (vendorId) {
      const searchData = transformMQL.save(
        'CompanyInfo',
        [vendorId],
        'vendorRead',
        {
          'contractVerification': {},
          'allowBidWithoutSealFlag': {},
          'allowQuotationWithoutSealFlag': {}
        }
      )
      this.$http({
        url: '/api-sup/api-ql/CompanyInfo/vendorRead',
        method: 'POST',
        data: searchData,
        loading: true
      }).then(res => {
        if (res && res.data && res.data.length) {
          const { contractVerification, allowBidWithoutSealFlag, allowQuotationWithoutSealFlag } = res.data[0]
          this.contractVerification = contractVerification
          this.allowBidWithoutSealFlag = allowBidWithoutSealFlag
          this.allowQuotationWithoutSealFlag = allowQuotationWithoutSealFlag
        }
      })
    },
    backBill () {
      this.$emit('tab-remove', `biddingOrdersQuote${this.$attrs.params.row.souNo}`)
      this.__setTabTodo('biddingOrdersList.getQueryData')
    },
    // 查询包名
    getProjectPackName () {
      this.$http({
        url: `/api-sou/ext/buyer/bid/init/getProjectPackName?projectId=${this.projectId}`,
        method: 'GET',
        loading: true
      }).then(res => {
        if (res && res.data) {
          this.packNameList = res.data
        }
      })
    },
    // 获取报价信息动态列
    getTemplateData () {
      this.$http({
        url: `/api-sou/ext/buyer/bid/init/listPriceTemplate?projectId=${this.projectId}`,
        method: 'GET',
        loading: true
      }).then(res => {
        if (res && res.data) {
          this.templateData = res.data.selectedList.map(item => {
            // 供应商输入
            if (item.columnSource == 'VENDOR' && item.colnmnInput == 'Y') {
              item.inputFlag = true
            } else {
              item.inputFlag = false
            }
            // 金额保留2位小数：含税单价、含税总价、未税单价、未税总价、固定未税单价（卢布）、暂定未税总价（卢布）、固定含税单价（卢布）、暂定含税总价（卢布）
            let amountList = ['extPriceTax', 'extPriceSumTax', 'extPriceNoTax', 'extPriceSumNoTax',
              'extFixedPriceNoTax', 'extProvPriceSumNoTax', 'extFixedPriceTax', 'extProvPriceSumTax']
            if (amountList.includes(item.columnCode)) {
              item.amountFlag = true
            } else {
              item.amountFlag = false
            }
            // 带星必填项: 固定未税单价（卢布）、未税单价、发票类型、税率、币种必填
            if (['extFixedPriceNoTax', 'extPriceNoTax', 'extInvoiceType', 'extTaxRate', 'extCurrency'].includes(item.columnCode)) {
              item.starFlag = true
            } else {
              item.starFlag = false
            }

            if (item.columnCode == 'extCurrency') {
              item.code = 'currency'
              item.changeEvent = this.currencyChange
            } else if (item.columnCode == 'extInvoiceType') {
              item.code = 'SOU_BIDPRICE_INVOICE_TYPE'
              item.changeEvent = this.invoiceTypeChange
            } else if (['extFixedPriceNoTax', 'extPriceNoTax', 'extTaxRate'].includes(item.columnCode)) {
              item.changeEvent = this.priceAndTaxChange
            } else {
              item.changeEvent = this.valueChange
            }
            return item
          })
        }
      })
    },
    // 查询汇率 基本货币：tradingCurrency，招标单发布日期：date，币种（报价选择的币种）：currenyCode
    // 基本货币（不传默认人民币）、对应货币（报价时选择的币种），汇率日期（取招标单的发布日期）。
    getRate (currenyCode, date, row) {
      this.$http({
        url: `/api-pj/pj-anon/exchangeRate/queryExchangeRate?currenyCode=${currenyCode}&date=${date}`,
        method: 'GET',
        loading: true
      }).then(res => {
        if (res && res.data && res.data.length > 0) {
          row.extExchangeRate = res.data[0].rate
        }
      })
    },
    // 币种改变-所有行币种一致
    async currencyChange (val, row) {
      // 切换币种 清空汇率 币种有值时 重新查询汇率
      // 0218逻辑修改：币种改变 不查汇率了
      // row.extExchangeRate = ''
      // val && this.getRate(val, this.publishDate, row)
      this.orderItemList = this.orderItemList.map(item => {
        return {
          ...item,
          extCurrency: val
        }
      })
    },
    // 发票类型改变--所有行发票类型一致
    invoiceTypeChange (val, row, dictItem) {
      this.orderItemList = this.orderItemList.map(item => {
        item.extInvoiceType = val
        item.extTaxRate = dictItem.description
        this.priceAndTaxChange(item)
        return item
      })
    },
    // 税率改变-所有行共用税率
    taxRateChange (val) {
      this.orderItemList = this.orderItemList.map(item => {
        return {
          ...item,
          extTaxRate: val
        }
      })
    },
    // 为了页面不报错给的空回调
    valueChange (val) {},
    // 输入未税单价和税率 计算未税总价、含税单价、含税总价
    // 输入固定未税单价（卢布）和税率 计算暂定未税总价（卢布）、固定含税单价（卢布）、暂定含税总价（卢布）
    priceAndTaxChange (row) {
      // 未税总价 = 未税单价 * 数量/工程量
      if (row.extPriceNoTax && row.extQuantity) {
        row.extPriceSumNoTax = Number(new Big(row.extPriceNoTax).times(new Big(row.extQuantity)))
      } else {
        row.extPriceSumNoTax = null
      }
      // 含税单价 = 未税单价 * (1+税率/100)
      if (row.extPriceNoTax && !isNull(row.extTaxRate)) {
        row.extPriceTax = Number(new Big(row.extPriceNoTax).times(new Big(row.extTaxRate).div(100).plus(1)))
      } else {
        row.extPriceTax = null
      }
      // 含税总价 = 含税单价 * 数量/工程量
      if (row.extPriceTax && row.extQuantity) {
        row.extPriceSumTax = Number(new Big(row.extPriceTax).times(row.extQuantity))
      } else {
        row.extPriceSumTax = null
      }
      // 暂定未税总价（卢布） = 固定未税单价（卢布） * 暂定数量/工程量
      if (row.extFixedPriceNoTax && row.requireQuantity) {
        row.extProvPriceSumNoTax = Number(new Big(row.extFixedPriceNoTax).times(row.requireQuantity))
      } else {
        row.extProvPriceSumNoTax = null
      }
      // 固定含税单价（卢布） = 固定未税单价（卢布） *（1+税率/100）
      if (row.extFixedPriceNoTax && !isNull(row.extTaxRate)) {
        row.extFixedPriceTax = Number(new Big(row.extFixedPriceNoTax).times(new Big(row.extTaxRate).div(100).plus(1)))
      } else {
        row.extFixedPriceTax = null
      }
      // 暂定含税总价（卢布） = 固定含税单价（卢布） * 暂定数量/工程量
      if (row.extFixedPriceTax && row.requireQuantity) {
        row.extProvPriceSumTax = Number(new Big(row.extFixedPriceTax).times(row.requireQuantity))
      } else {
        row.extProvPriceSumTax = null
      }
    },
    // 获取单据详情
    getOrderDetail (flag = '') {
      this.$http({
        url: `/api-sou/ext/vendor/bid/getTenderDetail?orderId=${this.orderId}`,
        method: 'GET',
        loading: true
      }).then(res => {
        if (res && res.data) {
          this.techOrderFileList = res.data.techOrderFileList
          this.busOrderFileList = res.data.busOrderFileList
          if(!flag) {
            this.orderItemList = res.data.orderItemList
            this.biddingData = res.data.project
          }
        }
      })
    },
    // 确认投标
    saveOrSubmit (type) { // tempSave区分暂存和提交，true为暂存 false提交
      const params = {
        tempSave: type == 'SAVE',
        projectId: this.projectId,
        orderId: this.orderId,
        techOrderFileList: this.techOrderFileList,
        busOrderFileList: this.busOrderFileList,
        orderItemList: this.orderItemList
      }
      this.$http({
        url: '/api-sou/ext/vendor/bid/editOrderItem',
        method: 'POST',
        data: params,
        loading: true
      }).then(res => {
        this.$message.success(res.message)
        type == 'SAVE' && this.getOrderDetail()
        type == 'SUBMIT' && this.backBill()
      })
    },
    async submitOrder (type) { // tempSave区分暂存和提交，true为暂存 false提交
      if (['t', 'all'].includes(this.type)) { // 投技术标或者投标
        if (this.techOrderFileList.length == 0) {
          // 至少上传一份投标文件
          this.$message.error(this.$t('cusEntry.supplement20250205.bidQuotaTips1'))
          return
        }
        let techFlag = this.techOrderFileList.some(item => !item.orderDocId)
        if (techFlag) {
          // 存在未上传的附件，请上传附件或者删除该行，否则无法提交
          this.$message.error(this.$t('cusEntry.supplement20250205.bidQuotaTips2'))
          return
        }
      }
      if (['b', 'all'].includes(this.type)) { // 投商务标或者投标
        if (this.busOrderFileList.length == 0) {
          // 至少上传一份商务报价单
          this.$message.error(this.$t('cusEntry.supplement20250205.bidQuotaTips5'))
          return
        }
        let busFlag = this.busOrderFileList.some(item => !item.orderDocId)
        if (busFlag) {
          // 请上传商务报价单附件
          this.$message.error(this.$t('cusEntry.supplement20250205.bidQuotaTips6'))
          return
        }

        let tipMsg = ''
        let valid = false
        this.orderItemList.some((it,index) => {
          this.templateData.some(v => {
            if (v.inputFlag && v.starFlag && isNull(it[v.columnCode])) {
              valid = true
              // `报价信息第${index + 1}行存在报价信息不完整，不可提交`
              tipMsg = this.$t('cusEntry.supplement20250205.bidQuotaTips13', { index: (index + 1) })
              return true
            }
          })
          if (valid) return true // 退出some遍历
        })

        if (valid) {
          this.$message.error(tipMsg)
          return
        }
      }
      this.saveOrSubmit(type)
    },
    // 上传投标文件
    addRow () {
      this.busOrderFileList.push({
        fileType: 'BUS_BID',
        orderFileName: '',
        orderDocId: '',
        extPackageName: '',
        extPackageNameList: [],
        orderRemark: '',
        extSignStatus: ''
      })
    },
    // 上传其他文件
    addOtherRow () {
      this.busOrderFileList.push({
        fileType: 'BUS_OTHER',
        orderFileName: '',
        orderDocId: '',
        extPackageName: '',
        extPackageNameList: [],
        orderRemark: '',
        extSignStatus: ''
      })
    },
    /* 文件变更 */
    fileChange ({ file }, index) {
      const { fileId = '', fileName = '' } = file || {}
      this.busOrderFileList[index].orderDocId = fileId
      this.busOrderFileList[index].orderFileName = fileName
    },
    /* 删除行 */
    deleteRow (index) {
      this.busOrderFileList.splice(index, 1)
    },
    // 电子签章
    onlineSign (orderType) {
      this.orderType = orderType
      this.$http({
        url: '/api-sou/ext/vendor/bid/pushSgin',
        method: 'POST',
        data: {
          orderId: this.orderId,
          orderType: orderType,
          signFileList: this.busOrderFileList
        },
        loading: true
      }).then(res => {
        this.$message.success(res.message)
        this.$emit('success')
        window.open(res.data)
      })
    }
  }
}
</script>

<style scoped lang="scss">
.the_doBidingDetail_wrapper ::v-deep {
  .cur-quote-deadline {
    padding-right: 16px;
    text-align: right;
    color: red;
  }
}
</style>
