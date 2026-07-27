<template>
  <srm-dialog
    :title="$t('orderMod.selDeliveryOrderDetail')"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    append-to-body
  >
    <FormWrapper
      ref="formRef"
      :form-array="preArr"
      init-active
      @getFormData="getQuerydata"
      @synchronous-value="syncFilterParams"
    />
    <TableView
      :ref="gridId"
      :table-data="tableData"
      :table-header="tableHeader"
      :page-size="pageSize"
      :pre-query-data="queryParam"
      :row-index="false"
      url="/api-sup-ce/po/deliveryNoteDetail/listInReturnOrder"
      :current-change="handleRadioChange"
      :row-dblclick="rowDblclick"
    >
      <template #radio="{ scope }">
        <el-radio v-model="selectedRow" :label="scope.row">
          <i />
        </el-radio>
      </template>
    </TableView>
    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t("common.cancel") }}
      </el-button>
      <el-button type="primary" @click="save">
        {{ $t("common.confirm") }}
      </el-button>
    </div>
  </srm-dialog>
</template>
<script>
import FormWrapper from 'lib@/components/Table/FormWrapper'
import TableView from 'lib@/components/Table/TableView'

export default {
  name: 'InspectionItemDialog',
  components: {
    FormWrapper,
    TableView
  },

  props: {
    visible: {
      type: Boolean
    }
  },

  data () {
    return {
      selectedRow: '',
      orgId: null,
      vendorName: '',
      pageSize: 15,
      gridId: 'list',
      tableHeader: [],
      tableData: [],
      preArr: [
        // 物料编码
        {
          prop: 'materialCode',
          label: () => this.$t('common.materialCode'), // 物料编码,
          type: 'quicksearch',
          showKey: 'materialCode',
          name: 'scc_base_material_item'
        },
        // 送货单号
        {
          prop: 'deliveryNumber',
          label: () => this.$t('qualitySynergy.deliveryNumber')
        },
        // 送货日期
        {
          prop: 'deliveryDate',
          label: () => this.$t('qualitySynergy.deliveryDate'),
          type: 'date'
        }
      ],
      queryParam: {}
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
    this.tableHeader = [
      {
        label: () => this.$t('common.select'),
        width: 60,
        showType: 'slot',
        slot: 'radio'
      },
      // 供应商编码
      {
        label: () => this.$t('common.vendorCode'),
        prop: 'vendorCode',
        width: 150
      },
      // 供应商名称
      {
        label: () => this.$t('orderMod.buyerOrderSynergy.vendorName'),
        prop: 'vendorName',
        width: 150
      },
      // 送货日期
      {
        label: () => this.$t('qualitySynergy.deliveryDate'),
        prop: 'deliveryDate',
        width: 100,
        dataType: 'dateTime'
      },
      // 送货单号
      {
        label: () => this.$t('qualitySynergy.deliveryNumber'),
        prop: 'deliveryNumber',
        width: 100
      },
      // 送货单行号
      {
        label: () => this.$t('qualitySynergy.lineNum'),
        prop: 'lineNum',
        width: 120
      },
      // 采购订单号
      {
        label: () => this.$t('qualitySynergy.orderNumber'),
        prop: 'orderNumber',
        width: 120
      },
      // 物料编码
      {
        label: () => this.$t('common.materialCode'),
        prop: 'materialCode',
        width: 100
      },
      // 物料名称
      {
        label: () => this.$t('common.materialName'),
        prop: 'materialName',
        width: 100
      },
      // 送货数量
      {
        label: () => this.$t('qualitySynergy.deliveryQuantity'),
        prop: 'deliveryQuantity',
        width: 100
      }
    ]
  },

  methods: {
    init (orgId = null, vendorName = null) {
      this.orgId = orgId
      this.vendorName = vendorName
      this.getQuerydata()
    },
    getQuerydata (obj) {
      let objs = obj || this.queryParam
      this.queryParam = { ...objs }

      this.queryParam.orgId = this.orgId
      this.queryParam.vendorName = this.vendorName
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    syncFilterParams (values) {
      this.queryParam = values
    },
    handleRadioChange (item) {
      this.selectedRow = item
    },
    rowDblclick (item) {
      this.selectedRow = item
      this.save()
    },
    save () {
      this.dialogVisible = false
      this.$emit('getSelectedRow', this.selectedRow)
    }
  }
}
</script>
