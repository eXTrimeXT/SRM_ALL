<template>
  <el-container
    direction="vertical"
    class="flex-container demoorder_list_wrapper"
  >
    <FormWrapper
      :form-array="filterConfig"
      @getFormData="search"
      @synchronous-value="syncFilterParams"
    >
      <template #orderStatus="{ scope }">
        <DictSelect
          v-model="scope.orderStatus"
          code="ORDER_STATUS"
        />
      </template>
    </FormWrapper>
    <el-main>
      <EasyTable
        ref="table"
        :selection="true"
        :methods="methods"
        :columns="columns"
        row-key="demoOrderId"
        table-name="demoorder_table"
        :query-params.sync="queryParams"
      >
        <template #btns>
          <AuthorityButton
            type="primary"
            @click="add"
          >
            {{
              $t("common.add")
            }}
          </AuthorityButton>
          <AuthorityButton
            type="primary"
            @click="save"
          >
            {{
              $t("common.submit")
            }}
          </AuthorityButton>
          <MImport
            :title="$t('common.import')"
            up-load-url="/api-base/base/demoorder/importExcel"
            :extra-data="extraData"
            @downloadTemplate="downloadTemplate"
            @handleSuccess="handleSuccess"
          />
          <ExportExcel
            page-url="/api-base/base/demoorder/listPage"
            :filter-params="filterParams"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            timeout="1000000"
            export-mode="front"
          />
        </template>
        <template #organizationName="{ scope }">
          <OrganizationSelector
            ref="organizationSelector"
            v-model="scope.row.organizationName"
            :parent-id="-1"
            :placeholder="$t('common.pleaseSelect')"
            :scope="scope.row"
            clearable
            @select="(val) => getOrgObj(val,scope)"
          />
        </template>
        <template #vendorName="{ scope }">
          <QuickSearch
            clearable
            :show-input="scope.row.vendorName"
            show-key="companyName"
            :scope-data="scope.row"
            name="scc_sup_company_info5"
            @close-quicksearch="(val) => getVendorObj(val, scope)"
          />
        </template>
        <template #responseStatus="{ scope }">
          <DictSelect
            v-model="scope.row.responseStatus"
            clearable
            code="RESPONSE_STATUS"
          />
        </template>
        <template #orderStatus="{ scope }">
          <DictSelect
            v-model="scope.row.orderStatus"
            clearable
            code="ORDER_STATUS"
          />
        </template>
        <template #orderType="{ scope }">
          <DictSelect
            v-model="scope.row.orderType"
            clearable
            code="ORDER_TYPE"
          />
        </template>
        <template #orderAmount="{ scope }">
          <el-input
            v-model="scope.row.orderAmount"
            v-input-format="{ type: 'float' }"
          />
        </template>
        <template #rfqSettlementCurrency="{ scope }">
          <DictSelect
            v-model="scope.row.rfqSettlementCurrency"
            clearable
            code="currency"
          />
        </template>
        <template #termOfPayment="{ scope }">
          <DictSelect
            v-model="scope.row.termOfPayment"
            clearable
            code="PAYMENT_TERMS"
          />
        </template>
        <template #paymentMethod="{ scope }">
          <DictSelect
            v-model="scope.row.paymentMethod"
            clearable
            code="PAYMENT_WAY"
          />
        </template>
        <template #province="{ scope }">
          <DictSelect
            v-model="scope.row.province"
            code="PROVINCE"
            custom-select-type="PROVINCE"
            filterable
            clearable
            @change="provinceChangeHandle(scope)"
          />
        </template>
        <template #city="{ scope }">
          <DictSelect
            v-model="scope.row.city"
            :code="scope.row.province"
            custom-select-type="CITY"
            filterable
            clearable
            :placeholder="$t('key12')"
            :disabled="!scope.row.province"
          />
        </template>
        <template #tel="{ scope }">
          <el-input
            v-model="scope.row.tel"
            maxlength="11"
          />
        </template>
        <template #refuseTime="{ scope }">
          <el-date-picker v-model="scope.row.refuseTime" :format="$formatDatePickerTime" />
        </template>
        <template #refuseReason="{ scope }">
          <el-input v-model="scope.row.refuseReason" />
        </template>
        <template #attachName="{ scope }">
          <SrmCommonFile
            :extra-data="fileInfo"
            :default-file="{
              fileId: scope.row.fileuploadId,
              fileName: scope.row.attachName
            }"
            :readonly="false"
            @on-change="({file}) => handleUploadSuccess(file,scope.row)"
          />
        </template>
      </EasyTable>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import EasyTable from 'lib@/components/BaseTable/EasyTable'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MImport from 'lib@/components/import'
import ExportExcel from 'lib@/components/export-excel'
import QuickSearch from 'lib@/components/QuickSearch' // 快速查询组件
import OrganizationSelector from 'lib@/components/organization-selector'
import { downloadFileLink } from 'lib@/utils/file'
import { validatePhone } from '@/utils/validate'
import { demoOrderApi } from 'modb@/demo/api'

export default {
  name: 'Demoorder',
  components: {
    EasyTable,
    FormWrapper,
    MImport,
    ExportExcel,
    QuickSearch,
    OrganizationSelector
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      fileInfo: {
        fileModular: 'workFlow',
        fileFunction: 'workflowReport',
        fileType: 'images'
      },
      filterConfig: [
        {
          prop: 'orderStatus',
          label: this.$t('orderMod.buyerOrderSynergy.orderStatus'),  // '订单状态'
          type: 'slot',
          slot: 'orderStatus'
        },
        { prop: 'orderNumber', label: this.$t('orderMod.orderNumber2') } // '订单号'
      ],
      extraData: {
        fileModular: 'base',
        fileFunction: 'demoorder',
        fileType: 'excel'
      },
      tableHeader: [],
      dictCodes: {
        orderStatus: 'ORDER_STATUS',
        responseStatus: 'RESPONSE_STATUS',
        orderType: 'ORDER_TYPE',
        termOfPayment: 'PAYMENT_TERMS',
        paymentMethod: 'PAYMENT_WAY'
      },
      filterParams: {},
      queryParams: {},
      methods: {
        listPage: async (params) => {
          const res = await demoOrderApi.list(params)
          return res
        }
      },
      columns: [
        {
          attrs: {
            label: this.$t('components.organization.organizationName'),  // '组织名称'
            prop: 'organizationName'
          },
          slot: 'organizationName'
        },
        {
          attrs: {
            label: this.$t('common.companyName'),  // '供应商名称'
            prop: 'vendorName',
            width: 150
          },
          slot: 'vendorName',
          rules: { required: 1, message: this.$t('vendorMod.required') }  //'必填'
        },
        {
          attrs: {
            label: this.$t('key5'),  // '供应商反馈状态'
            prop: 'responseStatus',
            width: 120,
            formatter: (value) => this.$getDictLabel('RESPONSE_STATUS', value)
          },
          slot: 'responseStatus'
        },
        {
          attrs: {
            label: this.$t('orderMod.buyerOrderSynergy.orderStatus'),  // '订单状态'
            prop: 'orderStatus',
            formatter: (value) => this.$getDictLabel('ORDER_STATUS', value)
          },
          slot: 'orderStatus'
        },
        {
          attrs: {
            label: this.$t('orderMod.buyerOrderSynergy.orderType'), // '订单类型'
            prop: 'orderType',
            formatter: (value) => this.$getDictLabel('ORDER_TYPE', value)
          },
          slot: 'orderType'
        },
        {
          attrs: {
            label: this.$t('dataConfMod.orderAmount'),  // '订单金额'
            prop: 'orderAmount'
          },
          slot: 'orderAmount'
        },
        {
          attrs: {
            label: this.$t('vendorMod.currencyCode'),  // '币种'
            prop: 'rfqSettlementCurrency'
          },
          slot: 'rfqSettlementCurrency'
        },
        {
          attrs: {
            label: this.$t('orderMod.orderNumber2'),  // '订单号'
            prop: 'orderNumber'
          }
        },
        {
          attrs: {
            label: this.$t('route.contractPaymentType'),  // '付款条件'
            prop: 'termOfPayment',
            formatter: (value) => this.$getDictLabel('PAYMENT_TERMS', value)
          },
          slot: 'termOfPayment'
        },
        {
          attrs: {
            label: this.$t('vendorMod.paymentMethod'),  // '付款方式'
            prop: 'paymentMethod',
            formatter: (value) => this.$getDictLabel('PAYMENT_WAY', value)
          },
          slot: 'paymentMethod'
        },
        {
          attrs: {
            label: this.$t('cusEntry.supplement20250211.provinceLevelCascade'),  // '一级联动（省）'
            prop: 'province',
            width: 120
          },
          slot: 'province'
        },
        {
          attrs: {
            label: this.$t('cusEntry.supplement20250211.twoLevelCityLinkage'),  // '二级联动（市）'
            prop: 'city',
            width: 120
          },
          slot: 'city'
        },
        {
          attrs: {
            label: 'TEL',
            prop: 'tel'
          },
          slot: 'tel',
          rules: {
            required: 1, // 有validator时，required参数仅设置表头红色*（不需要可删，不影响判断效果），判断规则以validator为主。
            validator: (rule, value, callback) => {
              const telValidate = validatePhone(value)
              if (!telValidate) {
                // '请输入正确格式的手机号码'
                callback(this.$t('cusEntry.supplement20250211.inputCorrectPhoneNumber'))
              } else {
                callback()
              }
            }
          }
        },
        {
          attrs: {
            label: this.$t('key10'),  // '拒绝时间'
            prop: 'refuseTime',
            width: 150
          },
          slot: 'refuseTime'
        },
        {
          attrs: {
            label: this.$t('orderMod.refuseReason'),  // '拒绝原因'
            prop: 'refuseReason'
          },
          slot: 'refuseReason'
        },
        {
          attrs: {
            label: this.$t('cusEntry.supplement20250211.singleAttachmentUpload'),  // '单个附件上传'
            prop: 'attachName',
            width: 120
          },
          slot: 'attachName',
          rules: { required: 1, message: this.$t('vendorMod.required') }  // '必填'
        },
        {
          attrs: {
            prop: 'operation',
            label: this.$t('components.headers.operation'),  // '操作'
            width: 100,
            fixed: 'right'
          },
          operations: [
            {
              event: 'deleteItem',
              name: this.$t('common.delete'),
              func: this.deleteItem
            }
          ]
        }
      ]
    }
  },
  created () {
    // 自定义导出入参列头信息 label, prop
    let columns = this.columns
    this.tableHeader = columns.map((i) => ({
      ...i.attrs
    }))
  },
  methods: {
    // 当formwrapper组件中的表单数据有变化时，将数据同步给filterParams，导出时使用。
    // values格式：{ orderStatus: "SUBMIT", orderNumber: "1111"}
    syncFilterParams (values) {
      this.filterParams = values
    },
    // 导入成功回调
    handleSuccess () {
      this.getQuerydata()
    },
    // 模板下载回调
    downloadTemplate () {
      downloadFileLink(
        '/api-base/base/demoorder/exportExcelTemplate',
        // '导入模板.xlsx'
        `${this.$t('supplierRating.importTemplate')}.xlsx`
      ).catch(() => {
        // '下载失败'
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    },
    // 新增行数据
    add () {
      this.$refs.table.add({})
    },
    // 行编辑--选择组织名称
    getOrgObj (node, scope) {
      if (node) {
        this.$set(this.$refs.table.realDataSource[scope.$index], 'organizationId', node.organizationId)
        this.$set(this.$refs.table.realDataSource[scope.$index], 'organizationCode', node.organizationCode)
        this.$set(this.$refs.table.realDataSource[scope.$index], 'organizationName', node.organizationName)
      }
    },
    // 行编辑--供应商快查
    getVendorObj (val, scope) {
      if (val) {
        this.$set(this.$refs.table.realDataSource[scope.$index], 'vendorId', val.companyId)
        this.$set(this.$refs.table.realDataSource[scope.$index], 'vendorName', val.companyName)
        this.$set(this.$refs.table.realDataSource[scope.$index], 'vendorCode', val.companyCode)
      }
    },
    // 行编辑--选择一级联动（省）回调
    provinceChangeHandle (scope) {
      this.$set(this.$refs.table.realDataSource[scope.$index], 'city', null)
    },
    // 行编辑--附件上传成功回调
    handleUploadSuccess (file, row) {
      const { fileId = '', fileName = '' } = file || {}
      row.fileuploadId = fileId.toString()
      row.attachName = fileName
    },
    // 删除行数据
    deleteItem (scope, data) {
      if (scope.row.demoOrderId) {
        // 有主键ID
        this.$confirm(this.$t('common.deleteViews'), {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        })
          .then(() => {
            demoOrderApi.delete(scope.row.demoOrderId)
              .then((res) => {
                this.$message.success(res.message)
                this.$refs.table.search(this.queryParams, true)
              })
          })
          .catch(() => {})
      } else {
        // 无主键ID
        data.splice(scope.$index, 1)
      }
    },
    // 查询列表数据
    search (params) {
      const { pageSize, pageNum } = this.queryParams
      this.$refs.table.search({ pageSize, pageNum, ...params }, true)
    },
    // 提交保存新增编辑的数据
    save () {
      const list = this.$refs.table.getUpdatedRows()
      this.$refs.table.validate((f) => {
        if (f) {
          demoOrderApi.batchSaveOrUpdate(
              list.map(({ demoOrderId, ...rest }) => {
                if (!demoOrderId) {
                  return rest
                } else {
                  return { demoOrderId, ...rest }
                }
              })
            )
            .then((res) => {
              this.$message.success(res.message)
              this.$refs.table.search(this.queryParams, true)
            })
        } else {
          this.$message({
            message: this.$t('cusEntry.supplement20250211.inputCorrectFormatMandatoryDocumentInfo'),  // '请输入正确格式的必填单据信息'
            type: 'error'
          })
        }
      })
    }
  }
}
</script>
<style scoped lang="scss">
</style>
