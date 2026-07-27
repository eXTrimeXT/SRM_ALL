<template>
  <!-- <SrmDialog
    title="选择协议"
    size="xLarge"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    append-to-body
    class="source-order-wrapper"
  > -->
  <SrmDialog
    :title="$t('cusEntry.supplement20250121.selectAgreement')"
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
          // label: '采购申请单号',
          label: () => this.$t('purchaseDemand.purRequisitionNum'),
          prop: 'requirementHeadNum'
        },
        {
          // label: '物料编码',
          label: () => this.$t('common.materialCode'),
          prop: 'materialCode'
        },
        {
          // label: '物料名称',
          label: () => this.$t('common.materialName'),
          prop: 'materialName'
        },
        {
          // label: '采购员',
          label: () => this.$t('bidMod.quotePurchasor'),
          prop: 'ceeaPerformUserId',
          type: 'quicksearch',
          showKey: 'nickname',
          propKey: 'userId',
          name: 'scc_rbac_user_display'
        },
        {
          // label: '使用部门',
          label: () => this.$t('cusEntry.orderMod.extUseDepartmentName'),
          prop: 'extUseDepartmentName'
        },
        {
          // label: '供应商名称',
          label: () => this.$t('common.companyName'),
          prop: 'vendorName'
        }
      ],
      tableHeader: [
        {
          // label: '采购申请单号',
          label: () => this.$t('purchaseDemand.purRequisitionNum'),
          prop: 'requirementHeadNum',
          width: 150
        },
        {
          // label: '申请行号',
          label: () => this.$t('purchaseDemand.rowNum'),
          prop: 'rowNum',
          width: 100
        },
        {
          // label: '协议号',
          label: () => this.$t('cusEntry.supplement20250121.agreementNumber'),
          prop: 'agreementCode',
          width: 150
        },
        {
          // label: '物料编码',
          label: () => this.$t('common.materialCode'),
          prop: 'materialCode',
          width: 150
        },
        {
          // label: '物料名称',
          label: () => this.$t('common.materialName'),
          prop: 'materialNameShow',
          width: 150
        },
        {
          // label: '规格型号',
          label: () => this.$t('vendorMod.specification'),
          prop: 'extMaterialModelShow',
          width: 150
        },
        {
          // label: '基本计量单位',
          label: () => this.$t('cusEntry.inq.baseMeasurmentUnit'),
          prop: 'unit',
          width: 150
        },
        {
          // label: '需求数量',
          label: () => this.$t('bidMod.demandQuantity2'),
          prop: 'requirementQuantity',
          width: 150
        },
        {
          // label: '本次需求日期',
          label: () => this.$t('cusEntry.supplement20250121.requirementDate'),
          prop: 'requirementDate',
          width: 150,
          dataType: 'dateTime'
        },
        {
          // label: '可下单数量',
          label: () => this.$t('purchaseDemand.orderQuantity'),
          prop: 'orderQuantity',
          width: 150
        },
        {
          // label: '本次下单数量',
          label: () => this.$t('purchaseDemand.thisOrderQuantity'),
          prop: 'orderQty',
          showType: 'inputNumber',
          editable: () => true,
          min: 0,
          max: row => row.orderQuantity,
          callback: row => this.orderQtyChange(row),
          width: 150
        },
        {
          // label: '物料小类（品类）',
          label: () => this.$t('cusEntry.supplement20250121.materialSubcategory'),
          prop: 'categoryName',
          width: 150
        },
        {
         // label: '含税单价',
          label: () => this.$t('bid_mod.taxUnitPrice'),
          prop: 'taxPrice',
          width: 120
        },
        {
          // label: '未税单价',
          label: () => this.$t('bid_mod.untaxedPrice'),
          prop: 'unitPrice',
          width: 120
        },
        {
          // label: '币种',
          label: () => this.$t('vendorMod.currencyCode'),
          prop: 'currencyName',
          width: 120
        },
        {
          // label: '汇率',
          label: () => this.$t('bid_mod.priceTax'),
          prop: 'taxRate',
          width: 120
        },
        {
          // label: '含税总额',
          label: () => this.$t('cusEntry.supplement20250121.totalAmountIncludingTax'),
          prop: 'amountIncludingTax',
          width: 120
        },
        {
          // label: '未税总额',
          label: () => this.$t('bidMod.quotetotalAmount'),
          prop: 'amountExcludingTax',
          width: 120
        },
        {
          // label: '采购员',
          label: () => this.$t('bidMod.quotePurchasor'),
          prop: 'ceeaPerformUserNickname',
          width: 120
        },
        {
          // label: '使用人',
          label: () => this.$t('cusEntry.orderMod.extUserName'),
          prop: 'extUserName',
          width: 120
        },
        {
          // label: '使用部门',
          label: () => this.$t('cusEntry.orderMod.extUseDepartmentName'),
          prop: 'extUseDepartmentName',
          width: 120
        },
        {
          // label: '订单行备注',
          label: () => this.$t('cusEntry.supplement20250121.orderLineRemark'),
          prop: 'comments',
          minWidth: 150
        },
        {
          prop: 'extAttachId',
          // label: '订单行附件',
          label: () => this.$t('cusEntry.supplement20250121.orderLineAttachment'),
          showType: 'slot',
          slot: 'extAttachId',
          minWidth: 200
        },
        {
          // label: '供应商',
          label: () => this.$t('common.vendor'),
          prop: 'vendorName',
          minWidth: 150
        },
        {
          // label: '价格来源',
          label: () => this.$t('purchaseDemand.ceeaPriceSourceType'),
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
    async afterQueryPriceData (tableData = []) {
      // 查询多语言物料信息
      const materialIds = tableData.map(item => item.materialId)
      const response = materialIds.length ? await this.$http({
        url: '/api-base/material/materialItem/ext/multilingual',
        method: 'POST',
        data: { materialIds, language: this.$i18n.locale },
        loading: true
      }) : []

      this.tableData = tableData.map(item => {
        const data = response.data.find(it => it.materialId === item.materialId)
        item.materialNameShow = data?.materialName
        item.extMaterialModelShow = data?.extMaterialModel
        item.orderQty = item.orderQuantity
        // item.priceSource = '价格库'
        item.priceSource = this.$t("cusEntry.supplement20250121.priceLibrary")
        item.taxPrice = Number(item.taxPrice || 0).toFixed(2)
        item.unitPrice = Number(item.unitPrice || 0).toFixed(2)
        // 计算含税总额
        item.amountIncludingTax = item.orderQty && item.taxPrice
          ? Number(Number(item.orderQty || 0) * Number(item.taxPrice || 0)).toFixed(2)
          : null
        // 计算未税总额
        item.amountExcludingTax = item.orderQty && item.unitPrice
          ? Number(Number(item.orderQty || 0) * Number(item.unitPrice || 0)).toFixed(2)
          : null
        return item
      })
    },
    checkChange (selection) {
      this.selection = selection
    },
    orderQtyChange (row) {
      if (row.orderQty && row.taxPrice) { // 计算含税总额
        row.amountIncludingTax = Number(Number(row.orderQty || 0) * Number(row.taxPrice || 0)).toFixed(2)
      }
      if (row.orderQty && row.unitPrice) { // 计算未税总额
        row.amountExcludingTax = Number(Number(row.orderQty || 0) * Number(row.unitPrice || 0)).toFixed(2)
      }
      if (row.orderQty == 0) {
        row.amountIncludingTax = null
        row.amountExcludingTax = null
      }
    },
    creatOrder () {
      if (this.selection.length == 0) {
        // this.$message.warning('请勾选数据')
        this.$message.warning(this.$t("cusEntry.tipMessage.selectRowsMsg"))
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
