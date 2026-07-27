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
        <div style="margin-bottom: 14px">
          <div style="font-size:14px;font-weight:bold;margin-bottom:14px">
            商务报价单
          </div>
          <div>
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
            <!--线上签署-->
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
          </div>
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
            minWidth="180px"
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
            label="文件类型"
            :formatter="(row, column, cellValue) => $getDictLabel('SOU_BUS_FILE_CONFIG_TYPE', cellValue)"
            show-overflow-tooltip
            minWidth="150px"
          />
          <!--包名-->
          <el-table-column
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
          </el-table-column>
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
          <el-table-column
            align="center"
            prop="extSignStatus"
            label="签署状态"
            :formatter="(row, column, cellValue) => $getDictLabel('SOU_BID_SIGN_STATUS', cellValue)"
            show-overflow-tooltip
            minWidth="150px"
          />
          <!--投标状态 查看投标历史时出现-->
          <el-table-column
            v-if="readonly"
            align="center"
            prop="extOrderStatus"
            label="投标状态"
            :formatter="(row, column, cellValue) => $getDictLabel('SOU_ORDER_STATUS', cellValue)"
            show-overflow-tooltip
            minWidth="150px"
          />
          <!--投标时间 查看投标历史时出现-->
          <el-table-column
            v-if="readonly"
            align="center"
            prop="extSubmitTime"
            label="投标时间"
            show-overflow-tooltip
            minWidth="150px"
          />
          <!--操作-->
          <el-table-column
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
          </el-table-column>
        </el-table>
        <div style="margin:14px 0; font-size:14px; font-weight:bold">
          报价信息
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
          <el-table-column
            v-if="mergeFlag"
            align="center"
            prop="extPackageName"
            :label="$t('cusEntry.biddingSettings.bagName')"
            show-overflow-tooltip
            min-width="150px"
          />
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
                :precision="6"
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
              <!-- 选中增值税普通、增值税专用发票类型时，币种仅可选择人民币 -->
              <dict-select
                v-else-if="templateData[index].columnType == 'LIST' && item.columnCode == 'extCurrency'"
                :key="scope.row.extInvoiceType"
                v-model="scope.row[item.columnCode]"
                :code="templateData[index].code"
                :transformOptions="options => {
                  let vatFlag = item.columnCode == 'extCurrency' && ['VAT_SPECIAL', 'VAT_ORDINARY'].includes(scope.row.extInvoiceType)
                  let res = options.filter(item => ['RMB'].includes(item.value))
                  return vatFlag ? res : options           
                }"
                :disabled="readonly || !templateData[index].inputFlag"
                @change-value="val => templateData[index].changeEvent(val, scope.row)"
              />
              <dict-select
                v-else-if="templateData[index].columnType == 'LIST'"
                v-model="scope.row[item.columnCode]"
                :code="templateData[index].code"
                :disabled="readonly || !templateData[index].inputFlag"
                @change-value="val => templateData[index].changeEvent(val, scope.row)"
              />
              <span v-else>{{ scope.row[item.columnCode] }}</span>
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="extExchangeRate"
            label="汇率"
            min-width="150"
            show-overflow-tooltip
          />
          <!--投标状态 查看投标历史时出现-->
          <el-table-column
            v-if="readonly"
            align="center"
            prop="orderStatus"
            label="投标状态"
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
import { parseTime } from '@/utils'

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
    this.publishDate = parseTime(row.publishTime, '{y}-{m}-{d}')
    this.mergeFlag = row.mergeFlag
    // 有合并招标标识才查询包名下拉项
    this.mergeFlag && await this.getProjectPackName()
    this.type !== 't' && await this.getTemplateData()
    this.getOrderDetail()
    this.getVendorInfo(row.vendorId)
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
            // 金额保留6位小数：含税单价、含税总价、未税单价、未税总价、固定未税单价（万元）、暂定未税总价（万元）、固定含税单价（万元）、暂定含税总价（万元）
            let amountList = ['extPriceTax', 'extPriceSumTax', 'extPriceNoTax', 'extPriceSumNoTax',
              'extFixedPriceNoTax', 'extProvPriceSumNoTax', 'extFixedPriceTax', 'extProvPriceSumTax']
            if (amountList.includes(item.columnCode)) {
              item.amountFlag = true
            } else {
              item.amountFlag = false
            }
            // 带星必填项: 固定含税单价（万元）、含税单价、发票类型、税率、币种必填
            if (['extFixedPriceTax', 'extPriceTax', 'extInvoiceType', 'extTaxRate', 'extCurrency'].includes(item.columnCode)) {
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
            } else if (['extFixedPriceTax', 'extPriceTax', 'extTaxRate'].includes(item.columnCode)) {
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
    // 币种改变
    async currencyChange (val, row) {
      // 切换币种 清空汇率 币种有值时 重新查询汇率
      row.extExchangeRate = ''
      val && this.getRate(val, this.publishDate, row)
    },
    // 发票类型改变
    invoiceTypeChange (val, row) {
      // 发票类型为增值税专票不能填0
      if (val === 'VAT_SPECIAL' && row.extTaxRate == 0) {
        row.extTaxRate = null
      }
      row.extCurrency = ''
      row.extExchangeRate = ''
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
    // 输入含税单价和税率计算含税总价、未税单价、未税总价
    // 输入固定含税单价（万元）和税率计算 暂定含税总价（万元）、固定未税单价（万元）、暂定未税总价（万元）
    priceAndTaxChange (row) {
      // 发票类型为增值税专票税率不能填0
      if (row.extInvoiceType === 'VAT_SPECIAL' && row.extTaxRate == 0) {
        row.extTaxRate = null
        return
      }
      // 含税总价 = 含税单价 * 数量/工程量
      if (row.extPriceTax && row.extQuantity) {
        row.extPriceSumTax = Number(row.extPriceTax || 0) * Number(row.extQuantity || 0)
      } else {
        row.extPriceSumTax = null
      }
      // 未税单价 = 含税单价/（1+税率/100）
      if (row.extPriceTax && row.extTaxRate) {
        row.extPriceNoTax = Number(row.extPriceTax || 0) / (1 + Number(row.extTaxRate || 0) / 100)
      } else {
        row.extPriceNoTax = null
      }
      // 未税总价 = 未税单价 * 数量/工程量
      if (row.extPriceNoTax && row.extQuantity) {
        row.extPriceSumNoTax = Number(row.extPriceNoTax || 0) * Number(row.extQuantity || 0)
      } else {
        row.extPriceSumNoTax = null
      }
      // 暂定含税总价（万元） = 固定含税单价（万元） * 暂定数量/工程量
      if (row.extFixedPriceTax && row.requireQuantity) {
        row.extProvPriceSumTax = Number(row.extFixedPriceTax || 0) * Number(row.requireQuantity || 0)
      } else {
        row.extProvPriceSumTax = null
      }
      // 固定未税单价（万元） = 固定含税单价（万元） /（1+税率/100）
      if (row.extFixedPriceTax && row.extTaxRate) {
        row.extFixedPriceNoTax = Number(row.extFixedPriceTax || 0) / (1 + Number(row.extTaxRate || 0) / 100)
      } else {
        row.extFixedPriceNoTax = null
      }
      // 暂定未税总价（万元） = 固定未税单价（万元） * 暂定数量/工程量
      if (row.extFixedPriceNoTax && row.requireQuantity) {
        row.extProvPriceSumNoTax = Number(row.extFixedPriceNoTax || 0) * Number(row.requireQuantity || 0)
      } else {
        row.extProvPriceSumNoTax = null
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
      let quoteTipFlag = false
      // 获取去重后的技术标、商务标包名
      let techBagList = this.techOrderFileList.map(item => item.extPackageNameList)
      let techBag = Array.from(new Set(techBagList.flat(2)))
      let busBagList = this.busOrderFileList.map(item => item.extPackageNameList)
      let busBag = Array.from(new Set(busBagList.flat(2)))

      if (['t', 'all'].includes(this.type)) { // 投技术标或者投标
        if (this.techOrderFileList.length == 0) {
          this.$message.error('至少上传一份投标文件')
          return
        }
        let techFlag = this.techOrderFileList.some(item => !item.orderDocId)
        if (techFlag) {
          this.$message.error('存在未上传的附件，请上传附件或者删除该行，否则无法提交')
          return
        }
        if (this.mergeFlag) {
          let bagFlag = this.techOrderFileList.some(item => item.fileType == 'TECH_BID' && item.extPackageNameList.length == 0)
          if (bagFlag) {
            this.$message.error('投标文件包名必填')
            return
          }
        }
        // 【契约认证】为是 && 【是否允许投标免签章】为否，提交时，校验技术文件为已签署状态
        if (this.contractVerification == 'Y' && this.allowBidWithoutSealFlag != 'Y') {
          let flag = this.techOrderFileList.some(item => item.fileType == 'TECH_BID' && item.extSignStatus != 'SIGN')
          if (flag) {
            this.$message.error('投标文件未签署')
            return
          }
        }
      }
      if (['b', 'all'].includes(this.type)) { // 投商务标或者投标
        if (this.busOrderFileList.length == 0) {
          this.$message.error('至少上传一份商务报价单')
          return
        }
        let busFlag = this.busOrderFileList.some(item => !item.orderDocId)
        if (busFlag) {
          this.$message.error(' 请上传商务报价单附件')
          return
        }
        if (this.mergeFlag) {
          let bagFlag = this.busOrderFileList.some(item => item.fileType == 'BUS_BID' && item.extPackageNameList.length == 0)
          if (bagFlag) {
            this.$message.error('报价单文件包名必填')
            return
          }
          // 有技术标，包名需保持一致
          if (techBag.length > 0) {
            // console.log('=====', techBag, busBag)
            if (techBag.length != busBag.length || busBag.some(it => !techBag.includes(it))) {
              this.$message.error('商务报价单所选包名与技术标不一致')
              return
            }
          }
        }
        // 【契约认证】为是 && 【是否允许报价免签章】为否，提交时，校验商务报价单为已签署状态
        if (this.contractVerification == 'Y' && this.allowQuotationWithoutSealFlag != 'Y') {
          let flag = this.busOrderFileList.some(item => item.fileType == 'BUS_BID' && item.extSignStatus != 'SIGN')
          if (flag) {
            this.$message.error('报价单文件未签署')
            return
          }
        }

        let tipMsg = ''
        let valid = false
        this.templateData.map(item => {
          if (item.starFlag) {
            // 弹框提示标识：存在部分行未报价，请确认是否继续提交
            this.orderItemList.some(it => {
              if (!it[item.columnCode]) {
                quoteTipFlag = true
                return true
              }
            })
            // 合并招标 提交完整的商务标包名数据
            // 非合并招标 至少提交一条完整数据
            if (this.mergeFlag) {
              busBag.map(bag => {
                this.orderItemList.some(it => {
                  if (it.extPackageName == bag) {
                    if (!it[item.columnCode]) {
                      valid = true
                      tipMsg = `报价信息【${bag}】数据未提交完整`
                      return true
                    }
                  }
                })
              })
            } else {
              this.orderItemList.some(it => {
                if (!it[item.columnCode]) {
                  valid = true
                  tipMsg = '至少提交一条完整的报价信息'
                } else {
                  valid = false
                  return true
                }
              })
            }
          }          
        })

        let warnValid = false
        let warnValidTip = ''
        this.orderItemList.some((it, index) => {
          // 含税单价小数点前超过5位，提示：存在报价金额较大，请确认检查报价是否正常
          if (it.extPriceTax > 9999) {
            warnValid = true
            warnValidTip = `请注意报价单位为“万元”，确认无误后提交报价`
            return true
          }
        })

        if (warnValid) {
          const promptResult = await this.$confirm(warnValidTip, '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消'
          })
          if (!promptResult) return
        }

        // 12.16新加校验：若存在报价行填写了部分信息，但必填项未填写完整，则提示：存在部分行报价信息不完整，不可提交
        this.orderItemList.some((it,index) => {
          let fillFlag = this.templateData.some(v => v.inputFlag && !!it[v.columnCode])
          this.templateData.some(v => {
            if (fillFlag && v.starFlag && !it[v.columnCode] && (it[v.columnCode] === 0 && v.columnCode === 'extTaxRate' && it.extInvoiceType === 'VAT_SPECIAL')) {
              valid = true
              tipMsg = `报价信息第${index + 1}行存在报价信息不完整，不可提交`
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
      if (quoteTipFlag) {
        const confirmResult = await this.$confirm('存在部分行未报价，请确认是否继续提交', {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        }).catch(() => { /* nothing */ })
        if (confirmResult !== 'confirm') {
          return
        }
        this.saveOrSubmit(type)
      } else {
        this.saveOrSubmit(type)
      }
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
