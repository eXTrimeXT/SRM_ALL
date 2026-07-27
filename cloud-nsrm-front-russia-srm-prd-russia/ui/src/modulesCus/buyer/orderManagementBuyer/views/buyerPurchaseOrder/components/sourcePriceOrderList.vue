<template>
  <SrmDialog
    title="选择协议"
    size="xLarge"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    append-to-body
    class="source-order-wrapper"
  >
    <FormWrapper
      :form-array="preArr"
      @getFormData="getQuerydata"
    />
    <TableView
      :ref="gridId"
      :bigData="true"
      :table-header="tableHeader"
      :page-size="pageSize"
      :pre-query-data="queryParam"
      :open-custom-table="false"
      :checkbox="true"
      :checkChange="checkChange"
      :comActive="$attrs['changeTab']"
      row-key="orderNumber"
      tableHeight="400px"
      url="api-sup-ce/purchaseRequirement/selectWithPriceAgree"
      @afterQuery="afterQueryPriceData"
    >
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
    </TableView>
    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t('common.cancel') }}
      </el-button>
      <el-button type="primary" @click="creatOrder">
        {{ $t('common.confirm') }}
      </el-button>
    </div>
  </SrmDialog>
</template>

<script>
import { transformMQL } from 'lib@/utils/util'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'

export default {
  name: 'SourcePriceOrderList',
  components: {
    TableView,
    FormWrapper
  },

  props: {
    visible: {
      type: Boolean,
      required: true
    }
  },

  data () {
    return {
      gridId: 'priceTable',
      pageSize: 15,
      queryParam: {},
      preArr: [
        {
          label: '采购申请单号',
          prop: 'requirementHeadNum'
        },
        {
          label: '物料编码',
          prop: 'materialCode'
        },
        {
          label: '物料名称',
          prop: 'materialName'
        },
        {
          label: '采购员',
          prop: 'ceeaPerformUserId',
          type: 'quicksearch',
          showKey: 'nickname',
          propKey: 'userId',
          name: 'scc_rbac_user_display'
        },
        {
          label: '使用部门',
          prop: 'extUseDepartmentName'
        },
        {
          label: '供应商名称',
          prop: 'vendorName'
        }
      ],
      tableHeader: [
        {
          label: '采购申请单号',
          prop: 'requirementHeadNum',
          width: 150
        },
        {
          label: '申请行号',
          prop: 'rowNum',
          width: 100
        },
        {
          label: '协议号',
          prop: 'agreementCode',
          width: 150
        },
        {
          label: '物料编码',
          prop: 'materialCode',
          width: 150
        },
        {
          label: '物料名称',
          prop: 'materialName',
          width: 150
        },
        {
          label: '规格型号',
          prop: 'extMaterialModel',
          width: 150
        },
        {
          label: '基本计量单位',
          prop: 'unit',
          width: 150
        },
        {
          label: '需求数量',
          prop: 'requirementQuantity',
          width: 150
        },
        {
          label: '本次需求日期',
          prop: 'requirementDate',
          width: 150
        },
        {
          label: '可下单数量',
          prop: 'orderQuantity',
          width: 150
        },
        {
          label: '本次下单数量',
          prop: 'orderQty',
          showType: 'inputNumber',
          editable: () => true,
          min: 0,
          max: row => row.orderQuantity,
          callback: row => this.orderQtyChange(row),
          width: 150
        },
        {
          label: '物料小类（品类）',
          prop: 'categoryName',
          width: 150
        },
        {
          label: '含税单价',
          prop: 'taxPrice',
          width: 120
        },
        {
          label: '未税单价',
          prop: 'unitPrice',
          width: 120
        },
        {
          label: '币种',
          prop: 'currencyName',
          width: 120
        },
        {
          label: '税率',
          prop: 'taxRate',
          width: 120
        },
        {
          label: '含税总额',
          prop: 'amountIncludingTax',
          width: 120
        },
        {
          label: '未税总额',
          prop: 'amountExcludingTax',
          width: 120
        },
        {
          label: '采购员',
          prop: 'ceeaPerformUserNickname',
          width: 120
        },
        {
          label: '使用人',
          prop: 'extUserName',
          width: 120
        },
        {
          label: '使用部门',
          prop: 'extUseDepartmentName',
          width: 120
        },
        {
          label: '订单行备注',
          prop: 'comments',
          minWidth: 150
        },
        {
          prop: 'extAttachId',
          label: '订单行附件',
          showType: 'slot',
          slot: 'extAttachId',
          minWidth: 200
        },
        {
          label: '供应商',
          prop: 'vendorName',
          minWidth: 150
        },
        {
          label: '价格来源',
          prop: 'priceSource',
          minWidth: 100
        }
      ],
      selection: []
    }
  },

  computed: {
    dialogVisible: {
      get: function () {
        return this.visible
      },
      set: function (val) {
        this.$emit('update:visible', val)
      }
    }
  },

  created () {
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    getQuerydata (params) {
      this.queryParam = params || {}
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    afterQueryPriceData (tableData) {
      this.tableData = tableData.map(item => {
        item.orderQty = item.orderQuantity
        item.priceSource = '价格库'
        // 计算含税总额
        item.amountIncludingTax = item.orderQty && item.taxPrice ? item.orderQty * item.taxPrice : null
        // 计算未税总额
        item.amountExcludingTax = item.orderQty && item.unitPrice ? item.orderQty * item.unitPrice : null
        return item
      })
    },
    checkChange (selection) {
      this.selection = selection
    },
    orderQtyChange (row) {
      if (row.orderQty && row.taxPrice) { // 计算含税总额
        row.amountIncludingTax = row.orderQty * row.taxPrice
      }
      if (row.orderQty && row.unitPrice) { // 计算未税总额
        row.amountExcludingTax = row.orderQty * row.unitPrice
      }
      if (row.orderQty == 0) {
        row.amountIncludingTax = null
        row.amountExcludingTax = null
      }
    },
    creatOrder () {
      if (this.selection.length == 0) {
        this.$message.warning('请勾选数据')
        return
      }
      // 依据供应商+业务实体+采购员+收货地址+币种+付款方式+付款条件 生成采购订单
      // 2024.1.2 放开校验
      // let validData = this.selection[0].vendorCode + this.selection[0].orgCode + this.selection[0].ceeaPerformUserName + this.selection[0].receiveAddress +
      //   this.selection[0].currencyCode + this.selection[0].paymentMethod + this.selection[0].paymentTerm
      // let validFlag = this.selection.some(item => validData != item.vendorCode + item.orgCode + item.ceeaPerformUserName +
      //   item.receiveAddress + item.currencyCode + item.paymentMethod + item.paymentTerm)
      // if (validFlag) {
      //   this.$message.warning('不同供应商，公司，采购员，收货地址，币种，付款条款，付款方式不能一起下单，请检查！')
      //   return
      // }
      const saveData = transformMQL.save('Order', this.selection, 'extCreateByRequirement')
      this.$http({
        url: '/api-sup-ce/api-ql/Order/extCreateByRequirement',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        this.dialogVisible = false
        this.$emit('afterCreatOrder')
      })
    }
  }
}
</script>
<style>
.source-order-wrapper .the_TableView .table-wrapper {
  display: block !important;
  flex: none;
}
</style>
