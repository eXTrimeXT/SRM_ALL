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
      <div style="padding:11px 33px 30px">
        <el-steps :active="activeNum">
          <el-step title="待报价" />
          <el-step :title="$t('bidMod.inquiryStatus3')" />
          <el-step :title="$t('bidMod.inquiryStatus4')" />
        </el-steps>
      </div>

      <!--报价截止倒计时-->
      <div class="cur-quote-deadline">
        <dynamic-cutoff-time
          :label="$t('bidMod.curQuoteDeadline')"
          :deadline-time="detailData.header.deadline"
        />
      </div>

      <!--代理报价-->
      <div v-if="proxyQuoteParams.visible" class="flex-col">
        <span class="label">
          <em style="color: red; margin-right: 10px">*</em>
          代理报价授权证明
        </span>

        <SrmCommonFile
          :default-file="{
            fileId: proxyData.proxyDocId,
            fileName: proxyData.proxyFileName
          }"
          :readonly="false"
          style="flex: 1; max-width: 250px;"
          @on-change="({file}) => proxyFileUploadSuccess(file)"
        />
      </div>

      <!--内容区-->
      <el-collapse v-model="activeDims" class="tab-form-style">
        <el-form
          v-if="isReadTabFlag"
          :model="detailData"
          disabled
          label-width="120px"
          label-position="top"
          class="form-incontainer"
        >
          <!--报价信息-->
          <el-collapse-item :title="$t('bidMod.quoteInfo')" name="1">
            <quote-info :header="detailData.header" :quote-no="detailData.quoteNo" />
          </el-collapse-item>

          <!--商务信息-->
          <el-collapse-item :title="$t('bidMod.businessInfo')" name="2">
            <business-info :header="detailData.header" :currency-list="detailData.currencyList" />
          </el-collapse-item>

          <!--查看附件-->
          <el-collapse-item :title="$t('bidMod.fileList')" name="3">
            <file-list :outer-file-list="detailData.outerFileList" />
          </el-collapse-item>

          <!--联系方式-->
          <el-collapse-item :title="$t('bidMod.contactInfo')" name="4">
            <srm-row>
              <srm-col :init-col="3">
                <el-form-item :label="$t('bidMod.linkman')">
                  <el-input v-model="detailData.header.linkman" disabled />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="3">
                <el-form-item :label="$t('bidMod.tel')">
                  <el-input v-model="detailData.header.tel" disabled />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="3">
                <el-form-item :label="$t('bidMod.email')">
                  <el-input v-model="detailData.header.email" disabled />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-collapse-item>
        </el-form>

        <!--物料信息-->
        <el-collapse-item :title="$t('bidMod.itemInfo')" name="5">
          <item-info
            ref="itemInfo"
            :item-list="detailData.itemList"
            :header="detailData.header"
            :currency-list="detailData.currencyList"
            :readonly="isReadOnly"
            :proxy-quote-params="proxyQuoteParams"
          />
        </el-collapse-item>
      </el-collapse>

      <CToolbar :style="{ position: proxyQuoteParams.visible ? 'absolute' : 'fixed' }">
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
import { tabTodoMixin, tabTodoWatch } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import MainHeader from 'lib@/components/Table/MainHeader'
import dynamicCutoffTime from 'lib@/components/dynamic-cutoff-time'
import quoteInfo from './inquiryOrdersDetail/quoteInfo'
import businessInfo from './inquiryOrdersDetail/businessInfo'
import fileList from './inquiryOrdersDetail/fileList'
import itemInfo from './inquiryOrdersDetail/itemInfo'

export default {
  name: 'InquiryOrdersDetail',

  components: {
    MainHeader,
    CToolbar,
    dynamicCutoffTime,
    quoteInfo,
    businessInfo,
    fileList,
    itemInfo
  },

  mixins: [tabTodoMixin, tabTodoWatch],

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
      detailData: {
        inquiryId: '',
        quoteNo: '',
        quoteStatus: '',
        header: {},
        itemList: [],
        currencyList: [],
        outerFileList: []
      },
      activeDims: ['1', '2', '3', '4', '5'],
      tabFlag: '',
      paramsRow: {},
      // 代理报价提交数据
      proxyData: {
        proxyDocId: '',
        proxyFileName: ''
      }
    }
  },

  computed: {
    // 不允许报价或者只读页面
    isReadOnly () {
      // 已作废，已报价
      return this.tabFlag === 'read' || (this.tabFlag === 'edit' && ['CANCEL', 'SUBMIT'].includes(this.detailData.quoteStatus))
    },
    // 只读模式，查看
    isReadTabFlag () {
      return this.tabFlag === 'read'
    },
    // 进度条状态
    activeNum () {
      let active = 0
      if (['FIXING_PRICE', 'FIXED_PRICE'].includes(this.detailData.header.status)) {
        active = 2
      } else if (this.detailData.quoteStatus === 'SUBMIT') {
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
      try {
        let params = {
          inquiryId: this.paramsRow.inquiryId
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

        const data = await this.$api.inq.inquiryBySimple.getInqQuoteInfo(params)

        if (data && data.data) {
          const detailData = {
            inquiryId: this.paramsRow.inquiryId,
            header: data.data.header || {},
            itemList: data.data.itemList || [],
            currencyList: data.data.currencyList || [],
            outerFileList: data.data.outerFileList || [],
            quoteNo: data.data.quoteNo || '',
            quoteStatus: data.data.quoteStatus || ''
          }
          // 是否存在阶梯报价行 并且已经填写过 批量查询阶梯报价回填
          const isLadderList = detailData.itemList.filter(item => item.isLadder === 'Y' && item.notaxPrice)
          if (isLadderList.length > 0) {
            // 批量查询阶梯价数据
            const ladderPricesData = await this.batchGetQuoteLadderPrices(isLadderList.map(item => {
              return {
                inquiryItemId: item.inquiryItemId,
                quoteItemId: item.quoteItemId
              }
            }))
            if (ladderPricesData) {
              Object.keys(ladderPricesData).forEach(key => {
                // 找到该行更新
                const findIndex = detailData.itemList.findIndex(item => item.inquiryItemId.toString() === key)
                if (findIndex >= 0) {
                  detailData.itemList.splice(findIndex, 1, {
                    ...detailData.itemList[findIndex],
                    ladderPriceTable: ladderPricesData[key]
                  })
                }
              })
            }
          }

          // 批量查询付款账期
          const paymentsList = detailData.itemList.filter(item => item.quoteItemId && item.inquiryItemId)
          if (paymentsList.length > 0) {
            // 批量查询付款账期
            const paymentsData = await this.batchGetQuoteItemPayments(paymentsList.map(item => {
              return {
                inquiryItemId: item.inquiryItemId,
                quoteItemId: item.quoteItemId
              }
            }))
            if (paymentsData) {
              Object.keys(paymentsData).forEach(key => {
                // 找到该行更新
                const findIndex = detailData.itemList.findIndex(item => item.inquiryItemId.toString() === key)
                if (findIndex >= 0) {
                  detailData.itemList.splice(findIndex, 1, {
                    ...detailData.itemList[findIndex],
                    paymentList: paymentsData[key]
                  })
                }
              })
            }
          }

          this.detailData = detailData
        }
      } catch (err) {
        console.log(err)
      }
    },

    /* 批量查询阶梯价报价值 */
    batchGetQuoteLadderPrices (data) {
      return new Promise(resolve => {
        this.$api.inq.inquiryBySimple.batchGetQuoteLadderPrices(data).then(res => {
          if (res && res.data) {
            resolve(res.data)
          } else {
            resolve(null)
          }
        })
      })
    },

    /* 批量查询付款账期 */
    batchGetQuoteItemPayments (data) {
      return new Promise(resolve => {
        this.$api.inq.inquiryBySimple.batchGetQuoteItemPayments(data).then(res => {
          if (res && res.data) {
            resolve(res.data)
          } else {
            resolve(null)
          }
        })
      })
    },

    /* 编排数据 */
    async arrangeInquiryOrdersData (isValidate = true) {
      const params = {
        // 询价单ID
        inquiryId: this.detailData.inquiryId,
        // 报价信息
        quoteItemList: await this.$refs.itemInfo.validateForm(isValidate)
      }
      if (!params.quoteItemList) {
        // 校验不通过
        return null
      }
      return params
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

    /*  保存 */
    async requestInquiryOrders () {
      const paramData = await this.arrangeInquiryOrdersData(true)

      if (paramData) {
        this.$api.inq.inquiryBySimple.saveQuote(paramData).then(() => {
          this.$message.success(this.$t('common.successSave'))
          // 重新查询
          this.getFormDetail()
        })
      }
    },

    /* 提交报价 */
    async submitInquiryOrders () {
      let paramData = await this.arrangeInquiryOrdersData()

      if (this.proxyQuoteParams.visible) {
        // 代理报价
        paramData = {
          ...paramData,
          proxyVendorId: this.proxyQuoteParams.vendorId
        }
        if (this.proxyData.proxyDocId) {
          paramData = {
            ...paramData,
            proxyDocId: this.proxyData.proxyDocId,
            proxyFileName: this.proxyData.proxyFileName
          }
        }
      }

      if (paramData) {
        this.$confirm('请确认是否要提交报价？', {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        }).then(async () => {
          // 确定
          this.$api.inq.inquiryBySimple.submitQuote(paramData).then(() => {
            this.$message.success(this.$t('common.successSave'))
            // 提交
            if (this.proxyQuoteParams.visible) {
              // 代理报价成功回调
              this.$emit('proxy-quote-success')
            } else {
              // 返回上一页
              this.backTo()
            }
          })
        })
      }
    },
    backTo () {
      if (this.proxyQuoteParams.visible) {
        this.$emit('proxy-quote-close')
      } else {
        this.$emit('tab-remove', `inquiryOrders${this.tabFlag}${this.paramsRow.inquiryNo}`)
        this.__setTabTodo('InquiryOrdersList.getQueryData')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.the-inquiryOrdersDetail-detail {
  padding-bottom: 60px;
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
