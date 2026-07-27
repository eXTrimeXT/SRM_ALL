<template>
  <el-container
    class="flex-container demoorder_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        ref="parentFormWrapper"
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
        <template #demoOrderId="{ scope }">
          <el-input
            v-model="scope.demoOrderId"
            placeholder="插槽弹窗查询"
            clearable
            suffix-icon="el-icon-search"
            @focus="openQueryDialog"
          />
        </template>
        <template #province="{ scope }">
          <DictSelect
            v-model="scope.province"
            custom-select-type="PROVINCE"
            code="PROVINCE"
            filterable
            clearable
            @change="provinceChangeHandle"
          />
        </template>
        <template #city="{ scope }">
          <DictSelect
            v-model="scope.city"
            :code="scope.province"
            custom-select-type="CITY"
            filterable
            clearable
            placeholder="请先选择一级联动"
            :disabled="!scope.province"
          />
        </template>
        <template #categoryIdGroup="{scope}">
          <CategoryCascader
            v-model="scope.categoryIdGroup"
            :multiple="true"
            @select="categoryTreeSelectChange"
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
            @click="addHandle"
          >
            {{
              $t("common.add")
            }}
          </AuthorityButton>
          <el-button
            :disabled="!currentRows.length"
            type="primary"
            @click="deleteHandle(currentRows)"
          >
            {{ $t("common.delete") }}
          </el-button>
          <MImport
            title="导入"
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
        :source="demoOrderMulApi.list"
      />
      <!-- 自定义查询弹框 -->
      <srm-dialog
        title="自定义查询弹框"
        size="large"
        :visible.sync="queryDialog.visible"
        :close-on-click-modal="false"
      >
        <FormWrapper
          :form-array="filterConfig"
          @getFormData="getQuerydata1"
        >
          <template #orderStatus="{ scope }">
            <DictSelect
              v-model="scope.orderStatus"
              code="ORDER_STATUS"
            />
          </template>
          <template #demoOrderId="{ scope }">
            <el-input
              v-model="scope.demoOrderId"
              placeholder="插槽弹窗查询"
              suffix-icon="el-icon-search"
              @focus="queryDialog.visible = true"
            />
          </template>
        </FormWrapper>
        <MainHeader
          :l-span="22"
          :r-span="2"
        >
          <template slot="left">
            <el-button
              type="primary"
              :disabled="!queryDialog.currentRows.length"
              @click="selectConfirm"
            >
              {{ $t("common.affirm") }}
            </el-button>
            <el-button
              type="primary"
              @click="queryDialog.visible = false"
            >
              {{
                $t("common.cancel")
              }}
            </el-button>
          </template>
        </MainHeader>
        <TableView
          :ref="queryDialog.gridId"
          :table-header="tableHeader"
          :check-change="handleCurrentChange1"
          :row-dblclick="queryDialogDblclick"
          :page-size="queryDialog.pageSize"
          :checkbox="true"
          :pre-query-data="queryDialog.queryParam"
          :source="demoOrderMulApi.list"
          :reserve-selection="true"
          row-key="demoOrderId"
        />
      </srm-dialog>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import demoorderEdit from './edit.vue'
import MImport from 'lib@/components/import'
import ExportExcel from 'lib@/components/export-excel'
import CategoryCascader from 'lib@/components/category-cascader'
import { downloadFileLink } from 'lib@/utils/file'
import { getRegion } from '@/api/common'
import { adaptDictData } from '@/utils'
import { demoOrderMulApi } from 'modb@/demo/api'
export default {
  name: 'DemoorderList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    MImport,
    ExportExcel,
    CategoryCascader
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      demoOrderMulApi:demoOrderMulApi,
      name: 'demoorderList',
      tableName: 'demoorderTable',
      pageSize: 15,
      gridId: 'list',
      currentRows: [],
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
          width: 100,
          formattor: (val) => this.$getDictLabel('PAYMENT_TERMS', val)
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
              callback: (row) => this.editHandle(row),
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
          label: '插槽下拉',
          type: 'slot',
          slot: 'orderStatus'
        },
        {
          prop: 'paymentMethod',
          label: '字典下拉',
          width: 180,
          type: 'dict',
          code: 'PAYMENT_WAY'
        },
        {
          prop: 'date',
          label: '单个日期',
          type: 'date'
        }, // 【单个日期时间】type值为datetime即可
        {
          prop: 'daterange',
          label: '日期周期',
          type: 'daterange'
        },
        {
          prop: 'companyName',
          label: '快查',
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info_display_buyer'
        },
        {
          type: 'switch',
          prop: 'isNew',
          label: '是否单选'
        },
        {
          prop: 'organizationId',
          label: '采购组织',
          type: 'OUorganizationSelector'
        },
        {
          prop: 'demoOrderId',
          label: '弹窗查询',
          type: 'slot',
          slot: 'demoOrderId'
        },
        { prop: 'orderNumber', label: '纯输入框' },
        {
          prop: 'province',
          label: '一级联动',
          type: 'slot',
          slot: 'province'
        },
        {
          prop: 'city',
          label: '二级联动',
          type: 'slot',
          slot: 'city'
        },
        {
          prop: 'categoryIdGroup',
          label: '品类',
          type: 'slot',
          slot: 'categoryIdGroup'
        }
      ],
      queryDialog: {
        visible: false, // 自定义弹窗查询--弹窗是否显示
        gridId: 'queryDialogList',
        pageSize: 15,
        currentRows: [],
        queryParam: {}
      },
      queryParam: {},
      currencyList: []
    }
  },
  created () {
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    // 选择一级联动回调触发二级联动的数据请求
    provinceChangeHandle (val) {
      this.$refs.parentFormWrapper.setValue('city', null)
    },
    // 品类选择回调
    categoryTreeSelectChange (nodes, value, scope) {
      // this.categoryIds = value
    },
    // 导入成功回调
    handleSuccess () {
      this.getQuerydata()
    },
    // 模板下载回调
    downloadTemplate () {
      downloadFileLink(
        '/api-base/base/demoorder/exportExcelTemplate',
        '导入模板.xlsx'
      ).catch(() => {
        this.$message.error('下载失败')
      })
    },
    // 当formwrapper组件中的表单数据有变化时，将数据同步给filterParams。
    // values格式：{ orderStatus: "SUBMIT", orderNumber: "1111"}
    syncFilterParams (values) {
      this.filterParams = values
    },
    // 查询按钮点击回调，params为查询头数据，params格式：{ orderStatus: "SUBMIT", orderNumber: "1111"}
    getQuerydata (params) {
      this.queryParam = params
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    // 删除操作
    // 批量删除：rows就是选择的行数据集合currentRows，由于点击事件也会有默认返回值，此参数必传
    // 行数据删除：rows为当前操作行的数据
    deleteHandle (rows) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          let delParams
          if (Array.isArray(rows)) {
            delParams = rows.map(i => i.demoOrderId)
            this.$message({ type: 'warning', message: '接口暂不支持批量删除，批量选中的数据id为' + delParams })
            return
          } else {
            delParams = rows.demoOrderId
          }
          this.$nextTick(() => {
            demoOrderMulApi.delete(delParams).then((res) => {
              this.$message.success(res.message)
              this.getQuerydata()
            })
          })
        })
        .catch(() => {})
    },
    // 新增
    addHandle (row) {
      this.mode = 'add'
      const tab = {
        component: demoorderEdit,
        params: {
          row,
          flag: this.mode,
          tabName: 'demoorderEdit'
        },
        title: '订单头行结构新增',
        name: 'demoorderEdit'
      }
      this.$emit('tab-add', tab)
    },
    // 编辑
    editHandle (row) {
      this.mode = 'edit'
      const tab = {
        component: demoorderEdit,
        params: {
          row,
          flag: this.mode,
          tabName: 'demoorderEdit' + row.demoOrderId
        },
        title: '订单头行结构编辑',
        name: 'demoorderEdit' + row.demoOrderId
      }
      this.$emit('tab-add', tab)
    },
    // 选择数据：val为当前选中的所有行数据数组
    handleCurrentChange (val) {
      this.currentRows = val
    },
    // 自定义查询弹窗--显示弹窗
    openQueryDialog () {
      this.queryDialog.visible = true
      this.getQuerydata1()
    },
    // 自定义查询弹窗--查询数据
    getQuerydata1 (params) {
      this.queryDialog.queryParam = params
      this.$nextTick(() => {
        this.$refs[this.queryDialog.gridId].query()
      })
    },
    // 自定义查询弹窗--选择数据
    handleCurrentChange1 (val) {
      this.queryDialog.currentRows = val
    },
    // 自定义查询弹窗--确认选择
    selectConfirm () {
      this.queryDialog.visible = false
      let ids = this.queryDialog.currentRows.map(i => i.demoOrderId)
      // 使用formWrapper组件中的方法设置表单值，才能在查询回调中获取对应数据
      this.$refs.parentFormWrapper.setValue('demoOrderId', ids.join(','))
    },
    // 自定义查询弹窗--双击单行选择
    queryDialogDblclick (row) {
      this.queryDialog.visible = false
      // 使用formWrapper组件中的方法设置表单值，才能在查询回调中获取对应数据
      this.$refs.parentFormWrapper.setValue('demoOrderId', row.demoOrderId)
    }
  }
}
</script>
