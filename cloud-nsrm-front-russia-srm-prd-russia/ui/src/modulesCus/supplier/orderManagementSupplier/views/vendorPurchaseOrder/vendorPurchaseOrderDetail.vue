<template>
  <el-container class="flex-container vendorPurchaseOrderDetail" direction="vertical">
    <el-main>
      <el-form
        ref="detailRef"
        :rules="rules"
        :model="form"
        label-width="80px"
        label-position="top"
        class="form-container2"
      >
        <el-collapse v-model="activeDims" class="tab-form-style">
          <!-- 采购订单单据 -->
          <el-collapse-item :title="$t('orderMod.buyerOrderSynergy.orderDetailsForm')" name="1">
            <BaseForm
              ref="formRef"
              :form-items="formItems"
              :merge-form.sync="form"
              form-name="form"
              :wrapper-col="{ span: 6, gutter: 27 }"
            />
          </el-collapse-item>
          <!-- 订单明细 -->
          <el-collapse-item :title="$t('orderMod.buyerOrderSynergy.orderDetailsList')" name="2">
            <BaseInfor
              ref="detailInforRef"
              style="height: 300px;"
              border
              row-key="objectKey"
              :data-source="detailModel.slice((pageInfo.pageNum-1)*pageInfo.pageSize,pageInfo.pageNum*pageInfo.pageSize)"
              :columns="detailColumn"
              columns-name="detailColumn"
              :initialize="false"
              :editable="false"
              @asyncGetRealDataSource="asyncGetRealDataSource"
            >
              <!-- 实际送货日期 -->
              <template #ceeaPromiseReceiveDate="{scope}">
                <el-date-picker
                  v-model="scope.row.ceeaPromiseReceiveDate"
                  type="date"
                  format="yyyy-MM-dd"
                  value-format="yyyy-MM-dd HH:mm:ss"
                  :disabled="isReadOnly"
                  :picker-options="{
                    disabledDate: time => {
                      return time.getTime() > new Date(scope.row.deliveryDate).getTime()
                    }
                  }"
                />
              </template>
              <!-- 订单行附件 -->
              <template #extAttachId="{ scope }">
                <SrmCommonFile
                  :default-file="{
                    fileId: scope.row.extAttachId,
                    fileName: scope.row.extAttachName
                  }"
                  readonly
                />
              </template>
            </BaseInfor>
            <CPagination
              ref="queryPagination"
              class="c-query-table-pagination"
              :total="detailModel.length"
              :page-num="pageInfo.pageNum"
              :page-size="pageInfo.pageSize"
              @current-change="handleCurrentChange"
              @size-change="handleSizeChange"
            />
          </el-collapse-item>
        </el-collapse>
      </el-form>
    </el-main>
    <CToolbar>
      <template slot="right">
        <el-button
          type="primary"
          @click="printBill"
        >
          {{ $t('common.pdfPrint') }}
        </el-button>
        <el-button @click="cancelBill">
          {{ $t('common.cancel') }}
        </el-button>
        <el-button
          v-if="!isReadOnly"
          type="primary"
          @click="confirmOrder"
        >
          {{ $t('components.common.confirm') }}
        </el-button>
        <el-button
          v-if="!isReadOnly"
          type="primary"
          @click="refuseOrder"
        >
          {{ $t('common.refused') }}
        </el-button>
      </template>
    </CToolbar>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import BaseInfor from 'lib@/components/BaseTable/baseTable'
import QuickSearch from 'lib@/components/QuickSearch' // 快速查询组件
import CToolbar from 'lib@/components/c-toolbar'
import TableView from 'lib@/components/Table/TableView'
import BaseForm from 'lib@/components/BaseForm'
import CPagination from 'lib@/components/c-pagination'
import { formItems, detailColumn } from './data/detail'
import { transformMQL } from 'lib@/utils/util'
import { parseTime } from '@/utils'

export default {
  name: 'VendorPurchaseOrderDetail',
  components: {
    QuickSearch,
    TableView,
    CPagination,
    BaseForm,
    BaseInfor,
    CToolbar
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      pageInfo: {
        pageNum: 1,
        pageSize: 15,
        total: 0
      },
      activeDims: ['1', '2'],
      detailColumn: detailColumn(this),
      formItems: formItems(this),
      detailModel: [],
      form: {
        ceeaOrgId: null,
        ceeaOrgCode: null,
        ceeaOrgName: null,
        orderId: null,
        orderNumber: null,
        orderStatus: 'DRAFT',
        organizationId: null,
        organizationCode: null,
        organizationName: null,
        orderType: null,
        ceeaPurchaseOrderDate: null,
        receiveContact: null,
        receiveTelephone: null,
        receiveAddress: null,
        ceeaEmpUsername: null,
        ceeaDepartmentName: null,
        extPurchaserPhone: null,
        extPurchaserEmail: null,
        extPurchaserOrgName: null,
        ceeaIfSupplierConfirm: 'Y',
        vendorId: null,
        vendorCode: null,
        vendorName: null,
        extVendorContacts: null,
        extVendorPhone: null,
        paymentMethod: null,
        termOfPayment: null,
        rfqSettlementCurrency: 'RMB',
        extOrderProperty: null,
        ceeaTotalNum: 0, // 合计数量
        ceeaTaxAmount: 0, // 合计金额含税
        ceeaNoTaxAmount: 0, // 合计金额不含税
        extAreaCode: null, // 区域
        comments: null
      },
      rules: {
        ceeaUnitTaxPrice: [{ required: true }],
        ceeaPromiseReceiveDate: [{ required: true }],
        currencyName: [{ required: true }],
        ceeaTaxKey: [{ required: true }],
        ceeaOrgId: [{ required: true, message: this.$t('purchaseDemand.orgIdTips') }],
        vendorName: [{ required: true, message: this.$t('vendorMod.msgVendor') }],
        receiveAddress: [
          {
            required: true,
            message: this.$t('orderMod.buyerOrderSynergy.msgReceiveAddress')
          }
        ]
      }
    }
  },
  computed: {
    // 手工订单
    isManual () {
      return this.form.orderType === 'MANUAL'
    },
    isReadOnly () {
      return this.$attrs.params.flag === 'view'
    }
  },
  created () {
    this.initDetail()
  },
  methods: {
    // 打印
    printBill () {
      const xml = encodeURIComponent('database:采购订单打印模板.ureport.xml')
      const params = encodeURIComponent(`orderId=${this.form.orderId}`)
      const url = `${this.$systemUrl}/#/pdfPrint?xml=${xml}&params=${params}`
      window.open(url, '_blank', 'noopener,noreferrer')
    },
    asyncGetRealDataSource (data) {
      const { pageNum, pageSize } = this.pageInfo
      this.detailModel = [...this.detailModel.slice(0, (pageNum - 1) * pageSize), ...data, ...this.detailModel.slice(pageNum * pageSize)]
    },
    handleCurrentChange (pageNum) {
      this.pageInfo.pageNum = pageNum
    },
    handleSizeChange (pageSize) {
      this.pageInfo.pageSize = pageSize
      /* 判断数组长度是否达到分页要求 */
      const sourceLength = this.detailModel.length
      const targetLength = (this.pageInfo.pageNum - 1) * pageSize
      if (sourceLength <= targetLength) {
        this.pageInfo.pageNum = Math.ceil(sourceLength / pageSize)
      }
    },
    initDetail () {
      const { row, flag } = this.$attrs.params
      if (flag === 'add') {
        this.form.orderType = 'MANUAL'
        this.form.ceeaPurchaseOrderDate = parseTime(new Date(), '{y}-{m}-{d}')
      } else {
        const { orderId } = this.$attrs.params.row
        this.queryDetails(orderId)
      }
    },
    // 去除响应对象返回空值问题
    getResponseRemoveEmpty (target, obj) {
      const fillInfo = {}
      Object.keys(obj).forEach(item => {
        if (obj[item]) {
          fillInfo[item] = obj[item]
        }
      })
      Object.assign(target, fillInfo)
    },
    async queryDetails (orderId) {
      const searchData = transformMQL.save(
        'OrderVendor',
        [orderId],
        'read',
        {
          '*': {},
          'detailList': { '*': {} }
        }
      )
      this.$http({
        url: '/api-sup-ce/api-ql/OrderVendor/read',
        method: 'POST',
        data: searchData,
        loading: true
      }).then(res => {
        if (res && res.data) {
          this.getResponseRemoveEmpty(this.form, res.data[0])
          // 订单行明细状态 优先取扩展字段 extDetailStatus
          let detailModel = res.data[0].detailList?.map(item => {
            item.orderDetailStatus = item.extDetailStatus || item.orderDetailStatus
            return item
          })
          this.detailModel = detailModel
        }
      })
    },
    async getCheckForm () {
      const formFiled = await this.$refs.formRef.validate()
      const formFiled1 = await this.$refs.detailInforRef.validate()

      if (!formFiled.flag && Object.keys(formFiled.obj).length > 0) {
        const warnObj = Object.keys(formFiled.obj)[0]
        return {
          flag: formFiled.flag,
          message: formFiled.obj[warnObj][0].message
        }
      }

      if (!formFiled1.flag && Object.keys(formFiled1.obj).length > 0) {
        const warnObj1 = Object.keys(formFiled1.obj)[0]
        return {
          flag: formFiled1.flag,
          message: formFiled1.obj[warnObj1][0].message
        }
      }
      return { flag: true }
    },
    // 确认订单
    async confirmOrder () {
      const { flag, message } = await this.getCheckForm()
      this.$confirm('请注意，确认订单后应当如约送货，否则按违约处理!', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 验证form表单
        if (flag) {
          const params = {
            ...this.form,
            detailList: this.detailModel
          }
          const saveData = transformMQL.save('OrderVendor', [params], 'extSupplierConfirm')
          this.$http({
            url: '/api-sup-ce/api-ql/OrderVendor/extSupplierConfirm',
            method: 'POST',
            data: saveData,
            loading: true
          }).then(res => {
            this.$message.success(this.$t('common.success'))
            this.cancelBill()
          })
        } else {
          this.__focus_error__(message)
        }
      }).catch(() => {})
    },
    // 拒绝订单
    async refuseOrder () {
      const prompt = await this.$prompt(this.$t('orderMod.msgRufuseReason'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        inputPattern: /\S{1,}/,
        inputErrorMessage: this.$t('orderMod.refuseReasonRequire')
      })
      if (!prompt) return
      const params = {
        orderId: this.form.orderId,
        refuseReason: prompt.value
      }
      const saveData = transformMQL.save('OrderVendor', [params], 'extSupplierRefuse')
      this.$http({
        url: '/api-sup-ce/api-ql/OrderVendor/extSupplierRefuse',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        this.cancelBill()
      })
    },
    cancelBill () {
      this.$emit('tab-remove', this.$attrs.tabName)
      this.__setTabTodo('VendorPurchaseOrderList.getQuerydata')
    }
  }
}
</script>
<style scoped lang="scss"></style>
