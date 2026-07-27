<template>
  <el-container class="flex-container the-inquiryOrdersDetail-detail" direction="vertical">
    <el-main style="padding: 10px 10px 40px 10px">
      <MainHeader>
        <template slot="left">
          <h2 style="margin-top: 0">
            {{ $t('bidMod.inquiryDetail') }}
          </h2>
        </template>
      </MainHeader>

      <!--进度条-->
      <div>
        <el-steps :active="activeNum">
          <el-step :title="$t('bidMod.inquiryStatus2')" />
          <el-step :title="$t('bidMod.inquiryStatus3')" />
          <el-step :title="$t('bidMod.inquiryStatus4')" />
        </el-steps>
      </div>

      <!--报价截止倒计时-->
      <div class="cur-quote-deadline">
        <DynamicCutoffTime
          :label="$t('bidMod.curQuoteDeadline')"
          :deadline-time="header.orderEndTime"
        />
      </div>

      <!--代理报价-->
      <div v-if="proxyQuoteParams.visible" class="flex-col">
        <span class="label">
          <em style="color: red; margin-right: 10px">*</em>
          {{ $t('bidMod.proxyQuoteCert') }}
        </span>

        <SrmCommonFile
          :default-file="{
            fileId: proxyData.proxyDocId,
            fileName: proxyData.proxyFileName
          }"
          :readonly="false"
          style="flex: 1; max-width: 250px;"
          @on-change="({ file }) => proxyFileUploadSuccess(file)"
        />
      </div>

      <!--内容区-->
      <el-collapse v-model="activeDims" class="tab-form-style">
        <el-form
          :model="header"
          disabled
          label-width="120px"
          label-position="top"
          class="form-incontainer"
        >
          <!--报价信息-->
          <el-collapse-item :title="$t('bidMod.quoteInfo')" name="1">
            <InquiryInfo :header="header" :order-no="orderNo" />
          </el-collapse-item>
          <!--查看附件-->
          <el-collapse-item :title="$t('bidMod.fileList')" name="3">
            <FileList :outer-file-list="outerFileList" />
          </el-collapse-item>

          <!--联系方式-->
          <el-collapse-item :title="$t('cusEntry.bidMod.contactInfo')" name="4">
            <OriginContactInfo
              :business-type="BUSINESS_TYPE_ENUM.INQUIRY_LTS"
              :info-data.sync="header"
              :set-default="false"
            />
          </el-collapse-item>
          <el-collapse-item :title="$t('bidMod.quoteInfo')" name="5">
            <QuoteInfo
              ref="quoteInfo"
              :header.sync="header"
              :order-no="orderNo"
              :readonly="isReadOnly"
            />
          </el-collapse-item>
        </el-form>

        <!--物料信息-->
        <el-collapse-item :title="$t('cusEntry.bidMod.itemInfo')" name="5">
          <ItemInfo
            ref="itemInfo"
            :item-list="itemList"
            :header="header"
            :currency-list="currencyList"
            :readonly="isReadOnly"
            :proxy-quote-params="proxyQuoteParams"
            @refresh="getFormDetail"
            @getMaterialLang="getMaterialLang"
          />
        </el-collapse-item>
      </el-collapse>

      <CToolbar>
        <template slot="right">
          <!--b 保存-->
          <el-button
            v-if="!isReadOnly && !proxyQuoteParams.visible"
            type="primary"
            @click="requestInquiryOrders"
          >
            {{ $t('common.save') }}
          </el-button>
          <!--b 提交-->
          <el-button v-if="!isReadOnly" type="primary" @click="submitInquiryOrders">
            {{ $t('common.submit') }}
          </el-button>
          <!--b 返回-->
          <el-button @click="backTo">
            {{ $t('common.backTo') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>

<script>
import { inqSupplierHttp } from 'modcs@/inquirySupplier/api'
import { inqBuyerHttp } from 'modb@/inquiry/api'
import { tabTodoMixin } from '@/utils/mixins'
import { BUSINESS_TYPE_ENUM, SOU_ORDER_STATUS_ENUM } from 'lib@/composition/origin/enum'
import CToolbar from 'lib@/components/c-toolbar'
import MainHeader from 'lib@/components/Table/MainHeader'
import DynamicCutoffTime from 'lib@/components/dynamic-cutoff-time'
import QuoteInfo from './inquiryOrdersList/inquiryOrdersDetail/quoteInfo'
import InquiryInfo from './inquiryOrdersList/inquiryOrdersDetail/inquiryInfo'
import BusinessInfo from './inquiryOrdersList/inquiryOrdersDetail/businessInfo'
import FileList from './inquiryOrdersList/inquiryOrdersDetail/fileList'
import ItemInfo from './inquiryOrdersList/inquiryOrdersDetail/itemInfo'
import OriginContactInfo from 'lib@/composition/origin/contactInfo'
import { validatePhone } from '@/utils/validate'
export default {
  name: 'InquiryOrdersDetail',

  components: {
    MainHeader,
    CToolbar,
    DynamicCutoffTime,
    QuoteInfo,
    BusinessInfo,
    FileList,
    ItemInfo,
    OriginContactInfo,
    InquiryInfo
  },

  mixins: [tabTodoMixin],

  props: {
    // 代理报价参数
    proxyQuoteParams: {
      type: Object,
      default: () => {
        return {
          visible: false
        }
      }
    }
  },

  data () {
    return {
      projectId: '',
      orderNo: '',
      orderStatus: '',
      header: {
        orderDocId: null,
        orderFileName: '',
        priceActiveDay: null,
        extOrderByNickname: null,
        extIsPrepaid: null,
        extPrepaidRatio: null,
        extOrderPhone: null
      },
      itemList: [],
      currencyList: [],
      outerFileList: [],
      activeDims: ['1', '2', '3', '4', '5'],
      tabFlag: '',
      paramsRow: {},
      // 代理报价提交数据
      proxyData: {
        proxyDocId: '',
        proxyFileName: ''
      },
      BUSINESS_TYPE_ENUM
    }
  },

  computed: {
    // 不允许报价或者只读页面
    isReadOnly () {
      // 已作废，已报价
      return this.tabFlag === 'read' ||
        (this.tabFlag === 'edit' && [SOU_ORDER_STATUS_ENUM.CANCEL, SOU_ORDER_STATUS_ENUM.SUBMISSION].includes(this.orderStatus))
    },
    // 只读模式，查看
    isReadTabFlag () {
      return this.tabFlag === 'read'
    },
    // 进度条状态
    activeNum () {
      let active = 0
      // 已定价 定价中
      if (['PRICING', 'PRICE_END'].includes(this.header.extProjectStatus)) {
        active = 2
      } else if (this.orderStatus === SOU_ORDER_STATUS_ENUM.SUBMISSION) {
        // 已报价
        active = 1
      }
      return active
    }
  },

  created () {
    this.paramsRow = this.proxyQuoteParams.visible ? this.proxyQuoteParams : this.$attrs.params.row
    this.tabFlag = this.proxyQuoteParams.visible ? 'edit' : this.$attrs.params.flag
    this.getFormDetail()
  },

  methods: {
    /* 查询详情信息 */
    async getFormDetail () {
      let params = {
        projectId: this.paramsRow.projectId
      }
      if (this.proxyQuoteParams.visible) {
        if (!this.proxyQuoteParams.vendorId) {
          return
        }
        // 代理报价
        params = {
          ...params,
          vendorId: this.proxyQuoteParams.vendorId
        }
      }

      let response

      if (this.proxyQuoteParams.visible) {
        // 代理报价 调采购商接口
        response = await inqBuyerHttp.order.getInqOrderInfo(params)
      } else {
        response = await inqSupplierHttp.order.getInqOrderInfo(params)
      }

      if (!response || !response.data) {
        return
      }

      const {
        initInfo = {},
        itemList = [],
        order = {},
        orderFileList = []
      } = response.data

      const {
        souFileList = [],
        currencyList = [],
        ...project
      } = (initInfo.projectInfo || {}) || {}

      const {
        orderNo = '',
        orderStatus = ''
      } = order || {}

      this.projectId = this.paramsRow.projectId
      const {
        orderDocId,
        orderFileName
      } = orderFileList.length ? orderFileList[0] : {}
      this.header = { ...project, ...order, orderDocId, orderFileName }
      this.currencyList = currencyList
      this.outerFileList = souFileList.filter(item => item.fileType === 'OUTER')
      this.orderNo = orderNo
      this.orderStatus = orderStatus

      await this.handleItemListData(itemList)
    },

    /* 处理物料行数据 */
    async handleItemListData (itemList) {
      // 是否存在阶梯报价行 并且已经填写过 批量查询阶梯报价回填
      const isLadderList = itemList.filter(item => item.isLadder === 'Y' && item.orderNotaxPrice)
      let ladderPricesResponse = null
      if (isLadderList.length > 0) {
        let data = isLadderList.map(item => {
          return {
            souItemId: item.souItemId,
            orderItemId: item.orderItemId
          }
        })
        // 批量查询阶梯价数据
        if (this.proxyQuoteParams.visible) {
          // 采购商
          ladderPricesResponse = await this.$http({
            url: `/api-sou/buyer/inq/order/batchGetOrderItemPayments/${this.proxyQuoteParams.vendorId}`,
            data,
            method: 'post',
            loading: true
          })
        } else {
          // 供应商
          ladderPricesResponse = await inqSupplierHttp.order.batchGetOrderLadderPrices(data)
        }

        if (ladderPricesResponse && ladderPricesResponse.data) {
          const ladderPricesData = ladderPricesResponse.data
          Object.keys(ladderPricesData).forEach(key => {
            // 找到该行更新
            const findIndex = itemList.findIndex(item => item.souItemId.toString() === key)
            if (findIndex >= 0) {
              itemList.splice(findIndex, 1, {
                ...itemList[findIndex],
                ladderPriceTable: ladderPricesData[key]
              })
            }
          })
        }
      }

      // 批量查询付款账期
      const paymentsList = itemList.filter(item => item.orderItemId && item.souItemId)
      if (paymentsList.length > 0) {
        // 批量查询付款账期
        const paymentsResponse = await inqSupplierHttp.order.batchGetOrderItemPayments(paymentsList.map(item => {
          return {
            souItemId: item.souItemId,
            orderItemId: item.orderItemId
          }
        }))

        if (paymentsResponse && paymentsResponse.data) {
          const paymentsData = paymentsResponse.data
          Object.keys(paymentsData).forEach(key => {
            // 找到该行更新
            const findIndex = itemList.findIndex(item => item.souItemId.toString() === key)
            if (findIndex >= 0) {
              itemList.splice(findIndex, 1, {
                ...itemList[findIndex],
                paymentList: paymentsData[key]
              })
            }
          })
        }
      }

      // 查询物料多语言
      this.getMaterialLang(itemList)
    },

    // 查询多语言物料信息
    async getMaterialLang (itemList = []) {
      const materialIds = itemList.map(item => item.itemId)
      if (materialIds.length === 0) {
        return []
      }
      const response = await this.$http({
        url: '/api-base/material/materialItem/ext/multilingual',
        method: 'POST',
        data: { materialIds, language: this.$i18n.locale },
        loading: true
      })
      this.itemList = itemList.map(item => {
        const data = response.data.find(it => it.materialId === item.itemId)
        return {
          ...item,
          materialNameShow: data?.materialName,
          extMaterialModelShow: data?.extMaterialModel
        }
      })
    },
    /* 代理报价上传成功 */
    proxyFileUploadSuccess (file) {
      const { fileId = '', fileName = '' } = file || {}
      this.proxyData = {
        ...this.proxyData,
        proxyDocId: fileId,
        proxyFileName: fileName
      }
    },

    /* 编排数据 */
    async arrangeInquiryOrdersData (tempSave) {
      const {
        orderDocId,
        orderFileName,
        priceActiveDay,
        extOrderByNickname,
        extIsPrepaid,
        extPrepaidRatio,
        extOrderPhone
      } = this.header
      const orderFileList = orderDocId ? [{ orderDocId, orderFileName, fileType: 'BUSINESS_FILE' }] : []
      let params = {
        projectId: this.projectId,
        priceActiveDay,
        extOrderByNickname,
        extIsPrepaid,
        extPrepaidRatio,
        extOrderPhone,
        // 是否代理报价
        isProxy: 'N',
        // 报价信息
        orderItemList: await this.$refs.itemInfo.validateForm(true),
        // 提交还是暂存
        isTempSave: tempSave,
        tempSave: tempSave,
        // 报价附件
        orderFileList
      }

      if (!params.orderItemList) {
        // 校验不通过
        return null
      }

      if (this.proxyQuoteParams.visible) {
        // 代理报价
        params.vendorId = this.proxyQuoteParams.vendorId
        params.souOrder = {
          ...params.souOrder,
          isProxy: 'Y',
          proxyVendorId: this.proxyQuoteParams.vendorId,
          // 文件非必填
          ...(this.proxyData.proxyDocId ? this.proxyData : {})
        }
      }

      return params
    },

    /* 保存 */
    async requestInquiryOrders () {
      const paramData = await this.arrangeInquiryOrdersData(true)
      if (paramData) {
        const response = await inqSupplierHttp.order.editOrder(paramData)

        if (response) {
          this.$message.success(this.$t('common.successSave'))
          // 重新查询
          await this.getFormDetail()
        }
      }
    },

    /* 提交报价 */
    async submitInquiryOrders () {
      const validResult = await this.$refs.quoteInfo.validateForm()
      if (!validResult) {
        this.$message.warning(this.$t('cusEntry.tipMessage.required'))
        return false
      }
      let paramData = await this.arrangeInquiryOrdersData(false)
      if (paramData) {
        const confirmResult = await this.$confirm(this.$t('bidMod.confirmSubmitQuote'), {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        }).catch(() => { /* nothing */ })

        if (confirmResult !== 'confirm') {
          return
        }

        let response

        if (this.proxyQuoteParams.visible) {
          // 采购商代理报价
          response = await this.$http({
            url: '/api-sou/buyer/inq/order/editOrder',
            method: 'POST',
            data: paramData,
            loading: true
          })
        } else {
          // 供应商报价
          response = await inqSupplierHttp.order.editOrder(paramData)
        }

        if (response) {
          this.$message.success(this.$t('common.successSubmit'))
          // 提交
          if (this.proxyQuoteParams.visible) {
            // 代理报价成功回调
            this.$emit('proxy-quote-success')
          } else {
            // 返回上一页
            this.backTo()
          }
        }
      }
    },

    /* 返回 / 取消 */
    backTo () {
      if (this.proxyQuoteParams.visible) {
        this.$emit('proxy-quote-close')
      } else {
        this.$emit('tab-remove', this.$attrs.params.tabName)
        this.__setTabTodo('InquiryOrdersList.getQueryData')
      }
    }
  }
}
</script>

<style scoped lang="scss">
.the-inquiryOrdersDetail-detail {
  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }
  .el-table .el-date-editor {
    width: 135px;
  }
  .flex-col {
    display: flex;
    margin: 10px 0;
    .label {
      line-height: 30px;
      margin-right: 10px;
    }
  }
}
</style>
