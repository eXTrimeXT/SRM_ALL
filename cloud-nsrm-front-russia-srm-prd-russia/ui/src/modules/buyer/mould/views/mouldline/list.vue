<template>
  <el-container
    class="flex-container mouldline_list_wrapper"
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
            page-url="/api-sup-ce/mould/mouldline/listPage"
            :filter-params="queryParam"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            :timeout="1000000"
            export-mode="front"
          />
        </template>
      </MainHeader>

      <TableView
        :ref="gridId"
        customTableKey="mouldlineText"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        :source="mouldline.list"
      />
    </el-main>
  </el-container>
</template>

<script>
import { tabTodoMixin, tabTodoWatch } from '@/utils/mixins'
import { getDictItemList } from '@/api/common'
import { adaptDictData } from '@/utils'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import ExportExcel from 'lib@/components/export-excel'
import { mouldline } from 'modb@/mould/api'
import mouldheaderEdit from 'modb@/mould/views/mouldheader/edit.vue'
export default {
  name: 'MouldlineList',
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
      mouldline: mouldline,
      name: 'mouldlineList',
      tableName: 'mouldlineTable',
      pageSize: 15,
      gridId: 'list',
      currentRows: [],
      dictCodes: {
        itemProductType: 'ITEM_PRODUCT_TYPE',
        mouldItemType: 'MOULD_ITEM_TYPE'
      },
      filterParams: {},
      mouldItemType: [],
      mouldItemTypeList: [],
      itemProductTypeOpts: [],
      tableHeader: [],
      filterConfig: [
        {
          prop: 'mouldName',
          label: this.$t('mould.mouldName'),
          type: 'quicksearch',
          showKey: 'mouldName',
          name: 'scc_sc_mould_header'
        },
        { prop: 'itemNumber', label: this.$t('supplierCapacityReport.materialCode') },
        {
          prop: 'mouldItemType',
          label: this.$t('mould.mouldItemType'),
          type: 'select',
          options: []
        },
        { prop: 'sameShareId', label: this.$t('mould.sameShareId') }
      ],
      queryParam: {}
    }
  },
  created () {
    this.fatchDictData()
    const _this = this
    this.tableHeader = [
      {
        prop: 'mouldCode',
        label: this.$t('mould.mouldCode'),
        width: 140,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.viewDetail(row)
      },

      {
        prop: 'mouldName',
        label: this.$t('mould.mouldName'),
        width: 100
      },

      {
        prop: 'mouldItemType',
        label: this.$t('mould.mouldItemType'),
        width: 100,
        formattor (val) {
          return _this.$getDictLabelByValue(_this.mouldItemTypeList, val)
        }
      },

      {
        prop: 'sameShareId',
        label: this.$t('mould.sameShareId'),
        width: 100
      },
      {
        prop: 'itemProductType',
        label: this.$t('mould.itemProductSort'),
        width: 100,
        formattor (val) {
          return _this.$getDictLabelByValue(_this.itemProductTypeOpts, val)
        }
      },
      {
        prop: 'itemNumber',
        label: this.$t('supplierCapacityReport.materialCode'),
        width: 100
      },
      {
        prop: 'itemDescZhs',
        label: this.$t('supplierCapacityReport.materialName'),
        width: 100
      },
      {
        prop: 'productNum',
        label: this.$t('mould.productNum'),
        width: 100
      },
      {
        prop: 'shareUnitPrice',
        label: this.$t('mould.shareUnitPrice'),
        width: 100
      },
      {
        prop: 'predictDailyCapacity',
        label: this.$t('mould.predictDailyCapacity'),
        width: 100
      },
      {
        prop: 'shareStartTime',
        label: this.$t('mould.shareStartTime'),
        width: 100
      },
      {
        prop: 'shareEndTime',
        label: this.$t('mould.shareEndTime'),
        width: 100
      },
      {
        prop: 'orderNum',
        label: this.$t('mould.orderNum'),
        width: 100
      },
      {
        prop: 'warehousingNum',
        label: this.$t('purSettlementMod.warehouseReceiptQuantity'),
        width: 100
      },
      {
        prop: 'creationDate',
        label: this.$t('elementDefinition.creationDate'),
        width: 100
      },
      {
        prop: 'lastUpdateDate',
        label: this.$t('dataConfMod.lastUpdateDate'),
        width: 100
      }
    ]
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    viewDetail (row) {
      const tab = {
        component: mouldheaderEdit,
        params: {
          row,
          flag: 'view',
          readOnly: true
        },
        title: this.$t('mould.checkMoldledger'),
        name: 'mouldheaderEdit' + row.mouldHeaderId
      }
      this.$emit('tab-add', tab)
    },
    // 获取数据字典
    fatchDictData () {
      const codes = ['MOULD_ITEM_TYPE', 'ITEM_PRODUCT_TYPE'].map(i => ({
        dictCode: i
      }))
      getDictItemList(codes).then(res => {
        const [MOULD_ITEM_TYPE, ITEM_PRODUCT_TYPE] = res.data
        this.mouldItemTypeList = adaptDictData(MOULD_ITEM_TYPE.MOULD_ITEM_TYPE)
        this.itemProductTypeOpts = adaptDictData(ITEM_PRODUCT_TYPE.ITEM_PRODUCT_TYPE)
        this.filterConfig[2].options = this.mouldItemTypeList
      })
    },

    getQuerydata (params) {
      this.queryParam = params
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      this.currentRows = val
    },
    // 上传附件成功
    handleUploadSuccess (file, row, key) {
      const { id } = file
      row[key] = id.toString()
    },
    // 删除文件
    handleAttachmentRemove (row, key) {
      row[key] = ''
    },
    // TODO 没有这个方法, 报错，补充一个空
    syncFilterParams () {}
  }
}
</script>
