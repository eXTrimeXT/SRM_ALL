<template>
  <el-container
    class="flex-container demoorder_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="filterConfig"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      >
        <template #orderStatus="{ scope }">
          <DictSelect
            v-model="scope.orderStatus"
            code="ORDER_STATUS"
          />
        </template>
      </FormWrapper>
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            type="primary"
            @click="addHandlePop"
          >
            {{
              $t("common.add")
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
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :checkbox="true"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :source="demoOrderApi.list"
      />
    </el-main>
    <srm-dialog
      :title="dialogTitle"
      size="large"
      :visible.sync="visible"
    >
      <div class="demoorderEdit">
        <el-form
          ref="form"
          :model="form"
          :rules="rules"
        >
          <el-row :gutter="32">
            <el-col :span="6">
              <el-form-item
                :label="$t('components.organization.organizationCode')"
                prop="organizationId"
              >
                <OrganizationSelector
                  ref="organizationSelector"
                  v-model="form.organizationId"
                  :parent-id="-1"
                  node-type="OU"
                  :placeholder="$t('common.pleaseSelect')"
                  :scope="form"
                  @select="selectHandler"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="vendorId"
                :label="$t('common.vendor')"
              >
                <QuickSearch
                  :show-input="form.vendorName"
                  show-key="companyName"
                  :scope-data="form"
                  name="scc_sup_company_info5"
                  @close-quicksearch="getVendorObj"
                />
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
                  disabled
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="orderNumber"
                :label="$t('orderMod.buyerOrderSynergy.orderNumber2')"
              >
                <el-input
                  v-model="form.orderNumber"
                  disabled
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="orderAmount"
                :label="$t('dataConfMod.orderAmount')"
              >
                <el-input
                  v-model="form.orderAmount"
                  v-input-format="{ type: 'float' }"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="rfqSettlementCurrency"
                :label="$t('dataConfMod.settingGuide.step3.2')"
              >
                <DictSelect
                  v-model="form.rfqSettlementCurrency"
                  code="currency"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="termOfPayment"
                :label="$t('route.contractPaymentType')"
              >
                <DictSelect
                  v-model="form.termOfPayment"
                  code="PAYMENT_TERMS"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="paymentMethod"
                :label="$t('bidMod.category_paymentMethod')"
              >
                <DictSelect
                  v-model="form.paymentMethod"
                  code="PAYMENT_WAY"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="deliveryLevel"
                :label="$t('key9')"
              >
                <DictSelect
                  v-model="form.deliveryLevel"
                  code="DELIVERY_LEVEL"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="buyerName"
                :label="$t('purchaseDemand.buyerName')"
              >
                <QuickSearch
                  :show-input="form.buyerName"
                  show-key="nickname"
                  :scope-data="form"
                  name="scc_rbac_user_display"
                  @close-quicksearch="getBuyerObj"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="tel"
                :label="$t('orderMod.buyerOrderSynergy.tel')"
              >
                <el-input
                  v-model="form.tel"
                  maxlength="11"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="taxRate"
                :label="$t('dataConfMod.settingGuide.step3.4')"
              >
                <DictSelect
                  v-model="form.taxRate"
                  code="tax"
                  @change="getRaxRateObj"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="taxKey"
                :label="$t('dataConfMod.taxKey')"
              >
                <el-input
                  v-model="form.taxKey"
                  disabled
                />
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
                  type="datetime"
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
                :label="$t('key6')"
              >
                <el-date-picker v-model="form.refuseTime" />
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
                prop="vendorCode"
                :label="$t('supplierRating.supplierCode')"
              >
                <el-input
                  v-model="form.vendorCode"
                  disabled
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="responseStatus"
                :label="$t('key5')"
              >
                <DictSelect
                  v-model="form.responseStatus"
                  code="RESPONSE_STATUS"
                  disabled
                />
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
                <DictSelect
                  v-model="form.orderType"
                  code="ORDER_TYPE"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="jitOrder"
                :label="$t('jit')"
              >
                <DictSelect
                  v-model="form.jitOrder"
                  code="JIT_ORDER"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="approveStatus"
                :label="$t('common.approvalStatus')"
              >
                <DictSelect
                  v-model="form.approveStatus"
                  code="APPROVE_STATUS"
                  disabled
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item
                prop="comments"
                :label="$t('bidMod.appraisRemark')"
              >
                <el-input
                  v-model="form.comments"
                  type="textarea"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
      <template
        #footer
        class="dialog-footer"
      >
        <el-button
          type="primary"
          @click="confirm"
        >
          {{ $t("common.confirm") }}
        </el-button>
        <el-button @click="cancel">
          {{ $t("common.cancel") }}
        </el-button>
      </template>
    </srm-dialog>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import ExportExcel from 'lib@/components/export-excel'
import QuickSearch from 'lib@/components/QuickSearch' // 快速查询组件
import OrganizationSelector from 'lib@/components/organization-selector'
import { adaptDictData } from '@/utils'
import { validatePhone } from '@/utils/validate'
import { demoOrderApi } from 'modb@/demo/api'
export default {
  name: 'DemoorderList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    MImport,
    ExportExcel,
    QuickSearch,
    OrganizationSelector
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      demoOrderApi:demoOrderApi,
      name: 'demoorderList',
      tableName: 'demoorderTable',
      pageSize: 15,
      gridId: 'list',
      dialogTitle: '单据详情',
      currentRows: [],
      visible: false,
      form: {
        demoOrderId: '主键ID',
        organizationId: '采购组织ID',
        fullPathId: '组织全路径虚拟ID',
        vendorId: '供应商ID',
        orderStatus: '订单状态',
        orderNumber: '订单号',
        orderAmount: '订单金额',
        rfqSettlementCurrency: '币种',
        termOfPayment: '付款条件',
        paymentMethod: '付款方式',
        deliveryLevel: '交期等级',
        buyerName: '采购员名称',
        tel: 'TEL',
        taxRate: '税率',
        taxKey: '税率编码',
        comments: '备注',
        submittedId: '订单提交人ID',
        submittedBy: '订单提交人',
        submittedTime: '提交时间',
        comfirmId: '订单确认人ID',
        comfirmBy: '订单确认人',
        comfirmTime: '确认时间',
        refuseId: '订单拒绝人ID',
        refuseBy: '订单拒绝人',
        refuseTime: '拒绝时间',
        refuseReason: '拒绝原因',
        createdId: '创建人ID',
        createdBy: '创建人',
        creationDate: '创建时间',
        createdByIp: '创建人IP',
        lastUpdateDate: '最后更新时间',
        lastUpdatedId: '最后更新人ID',
        lastUpdatedBy: '最后更新人',
        lastUpdatedByIp: '最后更新人IP',
        tenantId: '租户',
        version: '版本号',
        organizationCode: '组织编号',
        organizationName: '组织名称',
        vendorCode: '供应商编号',
        vendorName: '供应商名称',
        companyCode: '公司代码',
        responseStatus: '供应商反馈状态',
        sourceSystem: '来源系统',
        purchaseResponse: '采购商回复',
        orderType: '订单类型',
        jitOrder: '是否JIT订单',
        cbpmInstanceId: '外部CBPM实例ID',
        approveStatus: '审批状态'
      },
      rules: {
        tel: [
          {
            required: true,
            message: '手机号码不能为空！'
          },
          {
            trigger: 'blur',
            validator: (rule, value, callback) => {
              const telValidate = validatePhone(value)
              if (!telValidate) {
                callback(
                  this.$message({ type: 'warning', message: '手机号码不正确' })
                )
              } else {
                callback()
              }
            }
          }
        ]
      },
      extraData: {
        fileModular: 'base',
        fileFunction: 'demoorder',
        fileType: 'excel'
      },
      dictCodes: {
        orderStatus: 'ORDER_STATUS'
      },
      filterParams: {},
      tableHeader: [
        {
          prop: 'orderStatus',
          label: '订单状态',
          width: 100,
          formattor: (val) => this.$getDictLabel('ORDER_STATUS', val)
        },
        {
          prop: 'orderNumber',
          label: '订单号',
          width: 100
        },
        {
          prop: 'orderAmount',
          label: '订单金额',
          width: 100
        },
        {
          prop: 'rfqSettlementCurrency',
          label: '币种',
          width: 100
        },
        {
          prop: 'termOfPayment',
          label: '付款条件',
          width: 100
        },
        {
          prop: 'paymentMethod',
          label: '付款方式',
          width: 100
        },
        {
          prop: 'deliveryLevel',
          label: '交期等级',
          width: 100
        },
        {
          prop: 'buyerName',
          label: '采购员名称',
          width: 100
        },
        {
          prop: 'tel',
          label: 'TEL',
          width: 100
        },
        {
          prop: 'taxRate',
          label: '税率',
          width: 100
        },
        {
          prop: 'taxKey',
          label: '税率编码 ',
          width: 100
        },
        {
          prop: 'comments',
          label: '备注',
          width: 100
        },
        {
          prop: 'submittedBy',
          label: '订单提交人',
          width: 100
        },
        {
          prop: 'submittedTime',
          label: '提交时间',
          width: 100
        },
        {
          prop: 'comfirmBy',
          label: '订单确认人',
          width: 100
        },
        {
          prop: 'comfirmTime',
          label: '确认时间',
          width: 100
        },
        {
          prop: 'refuseBy',
          label: '订单拒绝人',
          width: 100
        },
        {
          prop: 'refuseTime',
          label: '拒绝时间',
          width: 100
        },
        {
          prop: 'refuseReason',
          label: '拒绝原因',
          width: 100
        },
        {
          prop: 'createdId',
          label: '创建人ID',
          width: 100
        },
        {
          prop: 'creationDate',
          label: '创建时间',
          width: 100
        },
        {
          prop: 'lastUpdateDate',
          label: '最后更新时间',
          width: 100
        },
        {
          prop: 'lastUpdatedBy',
          label: '最后更新人',
          width: 100
        },
        {
          prop: 'organizationCode',
          label: '组织编号',
          width: 100
        },
        {
          prop: 'organizationName',
          label: '组织名称',
          width: 100
        },
        {
          prop: 'vendorCode',
          label: '供应商编号',
          width: 100
        },
        {
          prop: 'vendorName',
          label: '供应商名称',
          width: 100
        },
        {
          prop: 'companyCode',
          label: '公司代码',
          width: 100
        },
        {
          prop: 'responseStatus',
          label: '供应商反馈状态',
          width: 100
        },
        {
          prop: 'sourceSystem',
          label: '来源系统',
          width: 100
        },
        {
          prop: 'purchaseResponse',
          label: '采购商回复',
          width: 100
        },
        {
          prop: 'orderType',
          label: '订单类型',
          width: 100
        },
        {
          prop: 'jitOrder',
          label: '是否JIT订单',
          width: 100
        },
        {
          prop: 'approveStatus',
          label: '审批状态',
          width: 100
        },
        {
          prop: 'operation',
          label: '操作',
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          width: 130,
          buttons: [
            {
              callback: (row) => this.editHandlePop(row),
              // code: "pr:requirementApply:edit",
              // show: row => row.status === "DRAFT",
              formattor: () => {
                return this.$t('common.edit')
              }
            },
            {
              callback: (row) => this.deleteHandle(row),
              // code: "pr:requirementApply:edit",
              // show: row => row.status === "DRAFT",
              formattor: () => {
                return this.$t('common.delete')
              }
            }
          ]
        }
      ],
      filterConfig: [
        {
          prop: 'orderStatus',
          label: '订单状态',
          type: 'slot',
          slot: 'orderStatus'
        },
        { prop: 'orderNumber', label: '订单号' }
      ],
      queryParam: {}
    }
  },
  created () {
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    // 弹框新增
    addHandlePop () {
      this.mode = 'add'
      for (let i in this.form) {
        this.form[i] = ''
      }
      this.dialogTitle = '新增单据'
      this.visible = true
    },
    // 弹框编辑
    editHandlePop (row) {
      this.mode = 'edit'
      this.form = row
      this.dialogTitle = '编辑单据'
      this.visible = true
    },
    // 新增编辑弹窗--业务实体选择回调
    selectHandler (node, value, scope) {
      this.form.organizationId = node ? node.organizationId : null
      this.form.organizationCode = node ? node.organizationCode : null
      this.form.organizationName = node ? node.organizationName : null
    },
    // 新增编辑弹窗--关闭供应商快查回调
    getVendorObj (val, scope) {
      this.form.vendorId = val ? val.companyId : ''
      this.form.vendorCode = val ? val.companyCode : ''
      this.form.vendorName = val ? val.companyName : ''
    },
    handleSuccess () {
      this.getQuerydata()
    },
    downloadTemplate () {
      downloadFileLink(
        '/api-base/base/demoorder/exportExcelTemplate',
        '导入模板.xlsx'
      ).catch(() => {
        this.$message.error('下载失败')
      })
    },
    cancel () {
      this.visible = false
    },
    confirm () {
      this.$refs.form.validate((result) => {
        if (result) {
          const flag = this.mode
          // 新增时不用提交主键值
          const { demoOrderId, ...rest } = this.form
          if (flag === 'add') {
            demoOrderApi.add(rest).then((res) => {
              this.$message({
                type: 'success',
                message: res.message
              })
              this.visible = false
            })
          } else if (flag === 'edit') {
            demoOrderApi.update(this.form).then((res) => {
              this.$message({
                type: 'success',
                message: res.message
              })
              this.visible = false
            })
          }
          this.$nextTick(() => {
            this.getQuerydata()
          })
        }
      })
    },
    // 删除行数据
    deleteHandle (row) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          demoOrderApi.delete(row.demoOrderId).then((res) => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
        .catch(() => {})
    },
    syncFilterParams (values) {
      this.filterParams = values
    },
    // 查询列表页数据
    getQuerydata (params) {
      this.queryParam = params
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      this.currentRows = val
    }
  }
}
</script>
