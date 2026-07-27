<template>
  <el-container
    class="flex-container supplierinventorylog_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="filterConfig"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <ExportExcel
            page-url="/api-sup-ce/sup/inventoryLog/listPage"
            :filter-params="queryParam"
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
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        :source="supplierInventoryLogApi.list"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import ExportExcel from 'lib@/components/export-excel'
import { supplierInventoryLogApi } from 'modb@/planManagementBuyer/api/inventory'

export default {
  name: 'SupplierinventorylogList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      supplierInventoryLogApi: supplierInventoryLogApi,
      name: 'supplierinventorylogList',
      tableName: 'supplierinventorylogTable',
      pageSize: 15,
      gridId: 'list',
      currentRows: [],
      dictCodes: {},
      filterParams: {},
      tableHeader: [
        {
          prop: 'itemCode',
          label: this.$t('common.materialCode'), // 物料编码
          width: 100
        },
        {
          prop: 'itemDesc',
          label: this.$t('common.materialName'), // 物料名称
          width: 100
        },
        {
          prop: 'categoryName',
          label: this.$t('supplierInventory.categoryName'), // 品类名称(物料小类)
          width: 160
        },
        {
          prop: 'categoryCode',
          label: this.$t('supplierInventory.categoryCode'), // 品类编码(物料小类)
          width: 160
        },
        {
          prop: 'vendorCode',
          label: this.$t('supplierRating.vendorCode'), // 供应商编码
          width: 120
        },
        {
          prop: 'vendorName',
          label: this.$t('supplierRating.supplierName'), // 供应商名称
          width: 120
        },
        {
          prop: 'ceeaOrgName',
          label: this.$t('supplierRating.entity'), // 业务实体"
          width: 100
        },
        {
          prop: 'inventoryNumber',
          label: this.$t('supplierInventory.inventoryNumber'), // 供方库存现有数量
          width: 160
        },
        {
          prop: 'inAndOutNumber',
          label: this.$t('supplierInventory.inAndOutNumber'), // 出入库数量
          width: 120
        },
        {
          prop: 'unit',
          label: this.$t('materialPrice.unit'), // 单位
          width: 100
        },
        // {
        //   prop: "createdBy",
        //   label: "创建人",
        //   width: 100,
        // },
        // {
        //   prop: "creationDate",
        //   label: "创建时间",
        //   width: 150,
        // },
          {
        prop: 'lastUpdatedBy',
        label: this.$t('common.updatePeople'), // 更新人
        width: 100
      },
      {
        prop: 'lastUpdateDate',
        label: this.$t('common.updateTime'), // 更新时间
        width: 150
      }
      ],

      filterConfig: [],
      queryParam: {}
    }
  },
  created () {
    let _this = this
    _this.filterConfig = [
      {
        prop: 'itemCode',
        label: () => this.$t('common.materialCode'), // 物料编码,
        type: 'quicksearch',
        showKey: 'materialCode',
        name: 'scc_base_material_item'
      },
      { prop: 'itemDesc', label: () => this.$t('common.materialName') }, // 物料名称
      {
        prop: 'vendorName',
        label: () => this.$t('common.vendorName'), // 供应商名称
        type: 'quicksearch',
        showKey: 'companyName',
        name: 'scc_sup_company_info_display_buyer'
      },
      {
        prop: 'ceeaOrgId',
        label: () => this.$t('contractMod.buId'),
        type: 'OUorganizationSelector',
        multiple: false
      },
      {
        prop: 'updateStarTime',
        label: () => this.$t('supplierInventory.updateStarTime'),
        type: 'datetime'
      },
      {
        prop: 'updateEndTime',
        label: () => this.$t('supplierInventory.updateEndTime'),
        type: 'datetime'
      }
    ]
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    syncFilterParams (values) {
      this.filterParams = values
    },
    getQuerydata (params) {
      this.queryParam = params || this.queryParam
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    deleteHandle (row) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          supplierInventoryLogApi.delete(row.supplierInventoryLogId).then(res => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
        .catch(() => {})
    },
    handleCurrentChange (val) {
      this.currentRows = val
    },
    // 上传附件成功
    handleUploadSuccess (file, row, key) {
      const { id, name } = file
      row[key] = id.toString()
    },
    // 删除文件
    handleAttachmentRemove (row, key) {
      row[key] = ''
    }
  }
}
</script>
