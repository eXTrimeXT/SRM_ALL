<template>
  <el-container
    class="demoorderEdit"
    direction="vertical"
  >
    <el-main>
      <el-tabs
        v-model="curTabName"
        type="border-card"
      >
        <el-tab-pane
          :label="$t('1')"
          name="tab1"
        >
          <div class="form-container">
            <el-form
              ref="form"
              :model="form"
              :rules="rules"
            >
              <el-row :gutter="32">
                <el-col :span="6">
                  <el-form-item
                    prop="vendorId"
                    :label="$t('common.vendor')"
                  >
                    <el-input v-model="form.vendorId" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    prop="orderStatus"
                    :label="$t('orderMod.buyerOrderSynergy.orderStatus')"
                  >
                    <DictSelect
                      v-model="form.orderStatus"
                      code="ORDER_STATUS"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    prop="orderNumber"
                    :label="$t('orderMod.buyerOrderSynergy.orderNumber2')"
                  >
                    <el-input v-model="form.orderNumber" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    prop="orderAmount"
                    :label="$t('dataConfMod.orderAmount')"
                  >
                    <el-input v-model="form.orderAmount" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    prop="rfqSettlementCurrency"
                    :label="$t('dataConfMod.settingGuide.step3.2')"
                  >
                    <el-input v-model="form.rfqSettlementCurrency" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    prop="termOfPayment"
                    :label="$t('route.contractPaymentType')"
                  >
                    <el-input v-model="form.termOfPayment" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    prop="paymentMethod"
                    :label="$t('bidMod.category_paymentMethod')"
                  >
                    <el-input v-model="form.paymentMethod" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    prop="deliveryLevel"
                    :label="$t('key9')"
                  >
                    <el-input v-model="form.deliveryLevel" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    prop="buyerName"
                    :label="$t('purchaseDemand.buyerName')"
                  >
                    <el-input v-model="form.buyerName" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    prop="tel"
                    :label="$t('orderMod.buyerOrderSynergy.tel')"
                  >
                    <el-input v-model="form.tel" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    prop="taxRate"
                    :label="$t('dataConfMod.settingGuide.step3.4')"
                  >
                    <el-input v-model="form.taxRate" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    prop="taxKey"
                    :label="$t('key11')"
                  >
                    <el-input v-model="form.taxKey" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    prop="comments"
                    :label="$t('bidMod.appraisRemark')"
                  >
                    <el-input v-model="form.comments" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    prop="comfirmBy"
                    :label="$t('key7')"
                  >
                    <el-input v-model="form.comfirmBy" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    prop="comfirmTime"
                    :label="$t('bidMod.confirmeDatetime')"
                  >
                    <el-date-picker
                      v-model="form.comfirmTime"
                      :format="$formatDatePickerTime"
                      value-format="yyyy-MM-dd HH:mm:ss"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    prop="refuseId"
                    :label="$t('orderMod.refuseId')"
                  >
                    <el-input v-model="form.refuseId" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    prop="refuseBy"
                    :label="$t('orderMod.refuseBy')"
                  >
                    <el-input v-model="form.refuseBy" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    prop="refuseTime"
                    :label="$t('key10')"
                  >
                    <el-date-picker
                      v-model="form.refuseTime"
                      :format="$formatDatePickerTime"
                      value-format="yyyy-MM-dd HH:mm:ss"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    prop="refuseReason"
                    :label="$t('contractMod.refusedReason')"
                  >
                    <el-input v-model="form.refuseReason" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    prop="organizationName"
                    :label="$t('components.organization.organizationCode')"
                  >
                    <el-input v-model="form.organizationName" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    prop="vendorCode"
                    :label="$t('supplierRating.supplierCode')"
                  >
                    <el-input v-model="form.vendorCode" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    prop="responseStatus"
                    :label="$t('key5')"
                  >
                    <el-input v-model="form.responseStatus" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    prop="purchaseResponse"
                    :label="$t('key8')"
                  >
                    <el-input v-model="form.purchaseResponse" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    prop="orderType"
                    :label="$t('purchaseDemand.orderType')"
                  >
                    <el-input v-model="form.orderType" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    prop="jitOrder"
                    :label="$t('jit')"
                  >
                    <el-input v-model="form.jitOrder" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    prop="approveStatus"
                    :label="$t('common.approvalStatus')"
                  >
                    <el-input v-model="form.approveStatus" />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form>
          </div>
          <el-collapse v-model="activeLine">
            <el-collapse-item
              :title="$t('dataConfMod.detail')"
              name="1"
            >
              <el-container
                class="flex-container"
                style="height: 300px"
              >
                <el-main>
                  <div style="padding: 12px 0">
                    <el-button
                      class="detail-pbtn"
                      type="primary"
                      @click="addLine"
                    >
                      {{ $t('bidMod.affairsIncreased') }}
                    </el-button>
                    <el-button
                      class="detail-pbtn"
                      type="primary"
                      @click="exportExcel"
                    >
                      {{ $t('common.export') }}
                    </el-button>
                    <MImport
                      btn-class-name="detail-pbtn"
                      :title="$t('common.import')"
                      up-load-url="/api-base/base/demoorder/importDemoOrderDetailExcel"
                      :extra-data="extraData"
                      @downloadTemplate="downloadTemplate"
                      @handleSuccess="handleSuccess"
                    />
                  </div>
                  <BaseTable
                    ref="table"
                    :columns="columns"
                    :data-source="dataSource"
                    :initialize="false"
                    row-key="quotaLineId"
                    border
                    @asyncGetRealDataSource="asyncGetRealDataSource"
                  >
                    <template #materialCode="{ scope }">
                      <QuickSearch
                        :show-input="scope.row.materialCode"
                        show-key="materialCode"
                        :scope-data="scope.row"
                        name="scc_base_material_item_display"
                        @close-quicksearch="(val) => getItemObj(val,scope)"
                      />
                    </template>
                    <template #orderNum="{ scope }">
                      <el-input
                        v-model="scope.row.orderNum"
                        v-input-format="{ type: 'number' }"
                        @change="calcTaxTotalPrice(scope)"
                      />
                    </template>
                    <template #unitPriceContainingTax="{ scope }">
                      <el-input
                        v-model="scope.row.unitPriceContainingTax"
                        v-input-format="{ type: 'float' }"
                        @change="calcTaxTotalPrice(scope)"
                      />
                    </template>
                    <template #deliveryDate="{ scope }">
                      <el-date-picker
                        v-model="scope.row.deliveryDate"
                        type="date"
                        :format="$formatDatePicker"
                        value-format="yyyy-MM-dd"
                      />
                    </template>
                    <template #receiptPlace="{ scope }">
                      <RenderAsyncText :cell-value="scope.row.receiptPlace" />
                    </template>
                    <template #costType="{ scope }">
                      <DictSelect
                        v-model="scope.row.costType"
                        code="COST_TYPE"
                      />
                    </template>
                    <template #currency="{ scope }">
                      <DictSelect
                        v-model="scope.row.currency"
                        code="currency"
                      />
                    </template>
                  </BaseTable>
                </el-main>
              </el-container>
            </el-collapse-item>
            <el-collapse-item
              :title="$t('perfMod.accessory')"
              name="2"
            >
              <p class="btn_line">
                <el-button
                  type="primary"
                  class="detail-pbtn"
                  @click="addFile"
                >
                  {{ $t("common.add") }}
                </el-button>
              </p>
              <el-table
                :data="fileList"
                style="width: 100%"
                border
                max-height="250px"
              >
                <el-table-column
                  align="center"
                  type="index"
                  :label="$t('common.sort')"
                  width="50"
                />
                <el-table-column
                  align="center"
                  prop="attachName"
                  :label="$t('bidMod.fileName')"
                >
                  <template slot-scope="scope">
                    <SrmCommonFile
                      :extra-data="fileInfo"
                      :default-file="{
                        fileId: scope.row.fileuploadId,
                        fileName: scope.row.attachName
                      }"
                      :readonly="false"
                      @on-change="({file}) => outerHandleUploadSuccess(file,scope.row)"
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="createdBy"
                  :label="$t('purchaseDemand.attachmentCreatedBy')"
                />
                <el-table-column
                  align="center"
                  prop="creationDate"
                  :label="$t('purchaseDemand.attachmentCreatedDate')"
                  :formatter="(row, column, cellValue) => $parseTime(cellValue)"
                />
                <el-table-column
                  :label="$t('common.operation')"
                  width="60"
                >
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      @click="handleDelClick(scope.$index, scope.row)"
                    >
                      {{ $t("common.delete") }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
          </el-collapse>
        </el-tab-pane>
        <el-tab-pane
          :label="$t('22')"
          name="tab2"
        >
          {{ $t('2') }}
        </el-tab-pane>
      </el-tabs>
      <CToolbar>
        <template #right>
          <el-button
            @click="cancelBill"
          >
            {{ $t('bidMod.cancel') }}
          </el-button>
          <el-button
            type="primary"
            :disabled="readOnly"
            @click="save"
          >
            {{ $t('orderMod.buyerOrderSynergy.confirm') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'
import CToolbar from 'lib@/components/c-toolbar'
import BaseTable from 'lib@/components/BaseTable/baseTable'
import MImport from 'lib@/components/import'
import QuickSearch from 'lib@/components/QuickSearch' // 快速查询组件
import RenderAsyncText from '@/library/components/provice-city/renderAsyncText'
import { downloadFileLink } from 'lib@/utils/file'
import axios from 'axios'
import { sysPrefix } from '@/config/ipConfig'
import { getToken } from '@/utils/auth'
import { demoOrderMulApi } from 'modb@/demo/api'
import { getMenuInfo } from '@/utils/menu-auth'

export default {
  name: 'DemoorderEdit',
  components: {
    MainHeader,
    CToolbar,
    BaseTable,
    MImport,
    QuickSearch,
    RenderAsyncText
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      curTabName: 'tab1',
      activeLine: ['1', '2'],
      realDataSource: [],
      dataSource: [],
      form: {
        demoOrderId: null,
        organizationId: null,
        fullPathId: null,
        vendorId: null,
        orderStatus: null,
        orderNumber: null,
        orderAmount: null,
        rfqSettlementCurrency: null,
        termOfPayment: null,
        paymentMethod: null,
        deliveryLevel: null,
        buyerName: null,
        tel: null,
        taxRate: null,
        taxKey: null,
        comments: null,
        submittedId: null,
        submittedBy: null,
        submittedTime: null,
        comfirmId: null,
        comfirmBy: null,
        comfirmTime: null,
        refuseId: null,
        refuseBy: null,
        refuseTime: null,
        refuseReason: null,
        createdId: null,
        createdBy: null,
        creationDate: null,
        createdByIp: null,
        lastUpdateDate: null,
        lastUpdatedId: null,
        lastUpdatedBy: null,
        lastUpdatedByIp: null,
        tenantId: null,
        version: null,
        organizationCode: null,
        organizationName: null,
        vendorCode: null,
        vendorName: null,
        companyCode: null,
        responseStatus: null,
        sourceSystem: null,
        purchaseResponse: null,
        orderType: null,
        jitOrder: null,
        cbpmInstanceId: null,
        approveStatus: null
      },
      columns: [
        {
          attrs: {
            prop: 'materialCode',
            label: this.$t('common.materialCode')  // '物料编码'
          },
          slot: 'materialCode',
          rules: { required: true, message: this.$t('vendorMod.required') }  // '必填'
        },
        {
          attrs: {
            prop: 'materialName',
            label: this.$t('common.materialName')  // '物料名称'
          }
        },
        {
          attrs: {
            prop: 'specification',
            label: this.$t('vendorMod.specification')  // '规格型号'
          }
        },
        {
          attrs: {
            prop: 'unit',
            label: this.$t('dataConfMod.settingGuide.step3.3')  // '单位'
          }
        },
        {
          attrs: {
            prop: 'categoryName',
            label: this.$t('vendorMod.classify')  // '分类'
          }
        },
        {
          attrs: {
            prop: 'orderNum',
            label: this.$t('orderMod.buyerOrderSynergy.orderNum')  // '订单数量'
          },
          slot: 'orderNum'
        },
        {
          attrs: {
            prop: 'unitPriceContainingTax',
            label: this.$t('bidMod.taxPrice')  // '单价（含税）'
          },
          slot: 'unitPriceContainingTax'
        },
        {
          attrs: {
            prop: 'totalPriceContainingTax',
            label: this.$t('cusEntry.supplement20250211.totalPriceTaxIncluded')  // '总价（含税）'
          }
        },
        {
          attrs: {
            prop: 'deliveryDate',
            label: this.$t('contractMod.deliveryDate'),  // '交货日期'
            width: 150
          },
          slot: 'deliveryDate'
        },
        {
          attrs: {
            prop: 'receiptPlace',
            label: this.$t('dataConfMod.organizationSite')  // '收货地点'
          },
          slot: 'receiptPlace'
        },
        {
          attrs: {
            prop: 'costType',
            label: this.$t('cusEntry.supplement20250211.costTypeDropdown')  // '成本类型（字典下拉）'
          },
          slot: 'costType'
        },
        {
          attrs: {
            prop: 'currency',
            label: this.$t('cusEntry.supplement20250211.currencyDropdownData') // '币种（接口数据下拉）'
          },
          slot: 'currency'
        },
        {
          attrs: {
            prop: 'operation',
            label: this.$t('components.headers.operation'),  // '操作'
            width: 150,
            fixed: 'right'
          },
          operations: [
            {
              event: 'deleteItem',
              name: this.$t('common.delete'),
              func: this.deleteLine
            }
          ]
        }
      ],
      extraData: {
        fileModular: 'base',
        fileFunction: 'quotalinetest',
        fileType: 'excel'
      },
      fileInfo: {
        fileModular: 'workFlow',
        fileFunction: 'workflowReport',
        fileType: 'images'
      },
      fileList: [],
      rules: {},
      readOnly: false
    }
  },
  computed: {},
  watch: {},
  created () {
  },
  mounted () {
    const { flag, row, readOnly = false } = this.$attrs.params
    this.readOnly = readOnly
    if (flag === 'edit') {
      this.getDetail()
    }
  },
  methods: {
    // 编辑--获取单据详情
    getDetail () {
      demoOrderMulApi.getById(this.$attrs.params.row.demoOrderId)
        .then((res) => {
          const { demoOrderDetailList, ...rest } = res.data
          this.form = rest
          this.dataSource = demoOrderDetailList
        })
    },
    // 下载模板
    downloadTemplate () {
      downloadFileLink(
        '/api-base/base/demoorder/exportDemoOrderDetailExcelTemplate',
        // '导入模板.xlsx'
        `${this.$t('supplierRating.importTemplate')}.xlsx`
      ).catch(() => {
        this.$message.error('下载失败')
      })
    },
    // 导入成功回调
    handleSuccess () {
      this.getDetail()
    },
    // 导出
    exportExcel () {
      let menuInfo = getMenuInfo()
      axios({
        method: 'POST',
        url: `${sysPrefix()}/api-base/base/demoorder/exportDemoOrderDetailExcel`,
        timeout: this.timeout,
        headers: {
          Authorization: 'Bearer ' + getToken(),
          'X-Fun-Info': menuInfo.secretKey
        },
        data: { id: this.$attrs.params.row.quotaHeadId },
        responseType: 'arraybuffer'
      })
        .then((response) => {
          console.log(response)
          const { data } = response
          if (response.headers['content-type'].startsWith('application/json')) {
            let enc = new TextDecoder('utf-8')
            let res = JSON.parse(enc.decode(new Uint8Array(data))) // 转化成json对象
            throw new Error(res.message)
          }
          const blob = new Blob([data])
          const disposition = response.headers['content-disposition'] || ''
          const filename = decodeURIComponent(disposition.split('=')[1])
          const url = window.URL.createObjectURL(blob) // URL.createObjectURL(object)表示生成一个File对象或Blob对象
          let dom = document.createElement('a') // 设置一个隐藏的a标签，href为输出流，设置download
          dom.style.display = 'none'
          dom.href = url
          dom.rel = 'noopener'
          dom.setAttribute('download', filename || `${this.fileName}.xlsx`) // 指示浏览器下载url,而不是导航到它；因此将提示用户将其保存为本地文件
          document.body.appendChild(dom)
          dom.click()
        })
        .catch((error) => {
          console.log(error)
          this.$message({ type: 'error', message: error.message })
        })
    },
    // 明细行--新增
    addLine () {
      this.$refs.table.add({})
    },
    // 明细行--删除
    deleteLine (scope, data) {
      data.splice(scope.$index, 1)
    },
    // 明细行--物料快查回调
    getItemObj (val, scope) {
      if (val) {
        this.$set(this.$refs.table.form.dataSource[scope.$index], 'materialId', val.materialId)
        this.$set(this.$refs.table.form.dataSource[scope.$index], 'materialCode', val.materialCode)
        this.$set(this.$refs.table.form.dataSource[scope.$index], 'materialName', val.materialName)
        this.$set(this.$refs.table.form.dataSource[scope.$index], 'categoryName', val.categoryName)
        this.$set(this.$refs.table.form.dataSource[scope.$index], 'categoryCode', val.categoryCode)
        this.$set(this.$refs.table.form.dataSource[scope.$index], 'categoryId', val.categoryId)
        this.$set(this.$refs.table.form.dataSource[scope.$index], 'specification', val.specification || '-')
        this.$set(this.$refs.table.form.dataSource[scope.$index], 'unit', val.unit || '-')
        this.$set(this.$refs.table.form.dataSource[scope.$index], 'unitName', val.unitName)
      }
    },
    // 明细行--计算总价
    calcTaxTotalPrice (scope) {
      const { orderNum, unitPriceContainingTax } = scope.row
      if (orderNum && unitPriceContainingTax) {
        scope.row.totalPriceContainingTax = unitPriceContainingTax * orderNum
      }
    },
    // 明细行--将表格中的数据实时同步到realDataSource字段中
    asyncGetRealDataSource (data) {
      this.realDataSource = data
    },
    // 附件--新增附件
    addFile () {
      this.fileList.push({
        fileuploadId: null,
        attachName: '',
        createdBy: '',
        creationDate: ''
      })
    },
    // 附件--上传成功回调
    outerHandleUploadSuccess (file, row) {
      const { fileId = '', fileName = '' } = file || {}
      row.fileuploadId = fileId.toString()
      row.attachName = fileName
    },
    // 附件--删除文件表格行数据
    handleDelClick (index, row) {
      this.fileList.splice(index, 1)
    },
    // 关闭tab页
    cancelBill () {
      const { tabName } = this.$attrs.params
      this.$emit('tab-remove', tabName)
      this.__setTabTodo('demoorderList.getQuerydata')
    },
    // 保存
    save () {
      this.$refs.form.validate((result) => {
        this.$refs.table.validate((res) => {
          if (result && res) {
            const { flag } = this.$attrs.params
            const data = {
              ...this.form,
              demoOrderDetailList: this.realDataSource
            }
            demoOrderMulApi.addOrUpdate(data).then((res) => {
              this.$message({
                type: 'success',
                message: res.message
              })
              this.cancelBill()
            })
          } else {
            this.__focus_error__()
          }
        })
      })
    }
  }
}
</script>
<style scoped lang="scss">
.demoorderEdit {
  height: 100%;
  padding-bottom: 50px;
  :deep(.table-wrapper) {
    padding-left: 0;
    padding-right: 0;
  }
  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }
  .el-table .el-date-editor {
    width: 135px;
  }
  .base-form {
    padding: 15px 30px 0;
  }
  .toRequired {
    color: #ff4949;
    padding-right: 2px;
  }
  .edit_cond {
    color: #23adf4;
    cursor: pointer;
  }
}
.demoorderEdit .form-container {
  padding-bottom: 40px !important;
}
</style>
