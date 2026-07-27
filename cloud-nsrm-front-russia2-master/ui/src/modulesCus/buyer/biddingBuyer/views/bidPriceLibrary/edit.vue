<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <el-collapse v-model="colValue">
        <el-collapse-item :title="$t('productionPrepare.supplierInfo')" name="1">
          <div
            v-if="!disabledFlag"
            class="btns"
            style="margin-bottom:10px;"
          >
            <el-button type="primary" @click="addNew">
              {{ $t('common.add') }}
            </el-button>
            <MImport
              ref="import"
              :title="$t('common.import')"
              up-load-url="/api-sou/npm/bidPrice/import"
              type="default"
              :extra-data="extraData"
              @downloadTemplate="downloadTemplate"
              @handleSuccess="handleSuccess"
            />
            <el-button @click="deleteRows">
              {{ $t('common.delete') }}
            </el-button>
          </div>
          <BaseTable
            stripe
            :data="tableData"
            :columns="tableColumns"
            :empty-text="$t('components.noData')"
            border
            @selection-change="selectionChangeHandle"
          >
            <template #souName="scope">
              <el-input v-model="scope.row.souName" :disabled="disabledFlag" />
            </template>
            <template #projectNo="scope">
              <el-input v-model="scope.row.projectNo" :disabled="disabledFlag" />
            </template>
            <template #categoryName="scope">
              <CCategorySelect
                v-model="scope.row.categoryName"
                :disabled="disabledFlag"
                :scope="scope.row"
                showKey="categoryName"
                :placeholder="$t('vendorMod.msgCategoryNormalizer')"
                @select="comfirmSelect"
              />
            </template>
            <template #itemDesc="scope">
              <el-input v-model="scope.row.itemDesc" :disabled="disabledFlag" />
            </template>
            <template #productType="scope">
              <el-input v-model="scope.row.productType" :disabled="disabledFlag" />
            </template>
            <template #productConfig="scope">
              <el-input v-model="scope.row.productConfig" :disabled="disabledFlag" />
            </template>
            <template #specification="scope">
              <el-input v-model="scope.row.specification" :disabled="disabledFlag" />
            </template>
            <template #brand="scope">
              <el-input v-model="scope.row.brand" :disabled="disabledFlag" />
            </template>
            <template #feature="scope">
              <el-input v-model="scope.row.feature" :disabled="disabledFlag" />
            </template>
            <template #constructionItem="scope">
              <el-input v-model="scope.row.constructionItem" :disabled="disabledFlag" />
            </template>
            <template #quantity="scope">
              <el-input v-model="scope.row.quantity" :disabled="disabledFlag" />
            </template>
            <template #priceNoTax="scope">
              <el-input-number v-model="scope.row.priceNoTax" :min="0" style="width:100%;" :disabled="disabledFlag" />
            </template>
            <template #priceSumNoTax="scope">
              <el-input-number v-model="scope.row.priceSumNoTax" :min="0" style="width:100%;" :disabled="disabledFlag" />
            </template>
            <template #priceTax="scope">
              <el-input-number v-model="scope.row.priceTax" :min="0" style="width:100%;" :disabled="disabledFlag" />
            </template>
            <template #priceSumTax="scope">
              <el-input-number v-model="scope.row.priceSumTax" :min="0" style="width:100%;" :disabled="disabledFlag" />
            </template>
            <template #invoiceType="scope">
              <DictSelect v-model="scope.row.invoiceType" code="SOU_BIDPRICE_INVOICE_TYPE" :disabled="disabledFlag" />
            </template>
            <template #taxRate="scope">
              <el-input-number v-model="scope.row.taxRate" :min="0" style="width:100%;" :disabled="disabledFlag" />
            </template>
            <template #currency="scope">
              <DictSelect v-model="scope.row.currency" code="currency" :disabled="disabledFlag" />
            </template>
            <template #bidSection="scope">
              <el-input v-model="scope.row.bidSection" :disabled="disabledFlag" />
            </template>
            <template #region="scope">
              <DictSelect v-model="scope.row.region" code="REGION" :disabled="disabledFlag" />
            </template>
            <template #unit="scope">
              <DictSelect v-model="scope.row.unit" code="unit" :disabled="disabledFlag" />
            </template>
            <template #requireQuantity="scope">
              <el-input v-model="scope.row.requireQuantity" :disabled="disabledFlag" />
            </template>
            <template #subitem="scope">
              <el-input v-model="scope.row.subitem" :disabled="disabledFlag" />
            </template>
            <template #fixedPriceNoTax="scope">
              <el-input-number v-model="scope.row.fixedPriceNoTax" :min="0" style="width:100%;" :disabled="disabledFlag" />
            </template>
            <template #provPriceSumNoTax="scope">
              <el-input-number v-model="scope.row.provPriceSumNoTax" :min="0" style="width:100%;" :disabled="disabledFlag" />
            </template>
            <template #fixedPriceTax="scope">
              <el-input-number v-model="scope.row.fixedPriceTax" :min="0" style="width:100%;" :disabled="disabledFlag" />
            </template>
            <template #provPriceSumTax="scope">
              <el-input-number v-model="scope.row.provPriceSumTax" :min="0" style="width:100%;" :disabled="disabledFlag" />
            </template>
            <template #remark="scope">
              <el-input v-model="scope.row.remark" :disabled="disabledFlag" />
            </template>
          </BaseTable>
        </el-collapse-item>
      </el-collapse>
    </el-main>
    <CToolbar>
      <template slot="right">
        <el-button type="ghost" @click="back">
          {{ $t('bidMod.cancel') }}
        </el-button>
        <el-button v-if="!disabledFlag" type="primary" @click="saveBill">
          {{ $t('common.save') }}
        </el-button>
      </template>
    </CToolbar>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import BaseTable from 'lib@/components/BaseTable'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import { transformMQL } from 'lib@/utils/util'
import bidPriceHttp from './api'
import CCategorySelect from 'lib@/components/c-category-select'
import CToolbar from 'lib@/components/c-toolbar'

export default {
  name: 'BidPriceLibraryDetail',
  components: {
    BaseTable,
    MImport,
    CCategorySelect,
    CToolbar
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      colValue: ['1'],
      tableData: [],
      tableColumns: [],
      selectedRows: [],
      extraData: {
        fileModular: 'sou',
        fileFunction: 'bidPriceLibrary',
        fileType: 'excel'
      },
      bidPriceId: null,
      inputFormat: {
        type: 'float'
      }
    }
  },
  computed: {
    urlParams () {
      return this.$attrs.params || {}
    },
    disabledFlag () {
      return !!['view', 'approval', 'manage'].includes(this.urlParams.flag)
    }
  },
  mounted () {
    this.tableColumns = [
      {
        attrs: {
          type: 'selection',
          width: 60
        }
      },
      {
        attrs: {
          type: 'index',
          width: 60,
          label: this.$t('common.sort')
        }
      },
      {
        attrs: {
          prop: 'souName',
          label: this.$t('bidMod.bidingName'),
          showOverflowTooltip: true,
          minWidth: 150
        },
        slot: 'souName'
      },
      {
        attrs: {
          prop: 'projectNo',
          label: this.$t('bidMod.bidingNumCla'),
          showOverflowTooltip: true,
          minWidth: 150
        },
        slot: 'projectNo'
      },
      {
        attrs: {
          prop: 'categoryName',
          label: this.$t('dataConfMod.category'),
          showOverflowTooltip: true,
          minWidth: 130
        },
        slot: 'categoryName'
      },
      {
        attrs: {
          prop: 'itemDesc',
          label: this.$t('bidMod.designation'),
          showOverflowTooltip: true,
          minWidth: 150
        },
        slot: 'itemDesc'
      },
      // 产品类型
      {
        attrs: {
          prop: 'productType',
          label: this.$t('cusEntry.supplement20250205.productType'),
          showOverflowTooltip: true,
          minWidth: 130
        },
        slot: 'productType'
      },
      // 产品配置
      {
        attrs: {
          prop: 'productConfig',
          label: this.$t('cusEntry.supplement20250205.productConfig'),
          showOverflowTooltip: true,
          minWidth: 130
        },
        slot: 'productConfig'
      },
      {
        attrs: {
          prop: 'specification',
          label: this.$t('common.specification'),
          showOverflowTooltip: true,
          minWidth: 130
        },
        slot: 'specification'
      },
      {
        attrs: {
          prop: 'brand',
          label: this.$t('dataConfMod.band'),
          showOverflowTooltip: true,
          minWidth: 130
        },
        slot: 'brand'
      },
      // 项目特征
      {
        attrs: {
          prop: 'feature',
          label: this.$t('cusEntry.supplement20250205.feature'),
          showOverflowTooltip: true,
          minWidth: 130
        },
        slot: 'feature'
      },
      // 施工内容
      {
        attrs: {
          prop: 'constructionItem',
          label: this.$t('cusEntry.supplement20250205.constructionItem'),
          showOverflowTooltip: true,
          minWidth: 130
        },
        slot: 'constructionItem'
      },
      // 数量/工程量
      {
        attrs: {
          prop: 'quantity',
          label: this.$t('cusEntry.supplement20250205.quantity'),
          showOverflowTooltip: true,
          minWidth: 130
        },
        slot: 'quantity'
      },
      // 未税单价（卢布）
      {
        attrs: {
          prop: 'priceNoTax',
          label: this.$t('cusEntry.supplement20250205.unitPriceExcludingTaxRUB'),
          showOverflowTooltip: true,
          minWidth: 130
        },
        slot: 'priceNoTax'
      },
      // 未税总价（卢布）
      {
        attrs: {
          prop: 'priceSumNoTax',
          label: this.$t('cusEntry.supplement20250205.totalPriceExcludingTaxRUB'),
          showOverflowTooltip: true,
          minWidth: 130
        },
        slot: 'priceSumNoTax'
      },
      // 含税单价（卢布）
      {
        attrs: {
          prop: 'priceTax',
          label: this.$t('cusEntry.supplement20250205.priceTax'),
          showOverflowTooltip: true,
          minWidth: 130
        },
        slot: 'priceTax'
      },
      // 含税总价（卢布）
      {
        attrs: {
          prop: 'priceSumTax',
          label: this.$t('cusEntry.supplement20250205.priceSumTax'),
          showOverflowTooltip: true,
          minWidth: 130
        },
        slot: 'priceSumTax'
      },
      {
        attrs: {
          prop: 'invoiceType',
          label: this.$t('accountMod.invoiceType'),
          showOverflowTooltip: true,
          minWidth: 130
        },
        slot: 'invoiceType'
      },
      {
        attrs: {
          prop: 'taxRate',
          label: this.$t('bidMod.taxRate2'),
          showOverflowTooltip: true,
          minWidth: 130
        },
        slot: 'taxRate'
      },
      {
        attrs: {
          prop: 'currency',
          label: this.$t('bidMod.currency_price'),
          showOverflowTooltip: true,
          minWidth: 130
        },
        slot: 'currency'
      },
      // 标段
      {
        attrs: {
          prop: 'bidSection',
          label: this.$t('cusEntry.bidMod.extBidSection'),
          showOverflowTooltip: true,
          minWidth: 130
        },
        slot: 'bidSection'
      },
      {
        attrs: {
          prop: 'region',
          label: this.$t('vendorMod.area1'),
          showOverflowTooltip: true,
          minWidth: 130
        },
        slot: 'region'
      },
      {
        attrs: {
          prop: 'unit',
          label: this.$t('dataConfMod.unit'),
          showOverflowTooltip: true,
          minWidth: 130
        },
        slot: 'unit'
      },
      // 暂定数量/工程量
      {
        attrs: {
          prop: 'requireQuantity',
          label: this.$t('cusEntry.supplement20250205.requireQuantity'),
          showOverflowTooltip: true,
          minWidth: 130
        },
        slot: 'requireQuantity'
      },
      // 分项
      {
        attrs: {
          prop: 'subitem',
          label: this.$t('cusEntry.supplement20250205.subitem'),
          showOverflowTooltip: true,
          minWidth: 130
        },
        slot: 'subitem'
      },
      // 固定未税单价（卢布）
      {
        attrs: {
          prop: 'fixedPriceNoTax',
          label: this.$t('cusEntry.supplement20250205.fixedPriceNoTax'),
          showOverflowTooltip: true,
          minWidth: 150
        },
        slot: 'fixedPriceNoTax'
      },
      // 暂定未税总价（卢布）
      {
        attrs: {
          prop: 'provPriceSumNoTax',
          label: this.$t('cusEntry.supplement20250205.provPriceSumNoTax'),
          showOverflowTooltip: true,
          minWidth: 150
        },
        slot: 'provPriceSumNoTax'
      },
      // 固定含税单价（卢布）
      {
        attrs: {
          prop: 'fixedPriceTax',
          label: this.$t('cusEntry.supplement20250205.fixedPriceTax'),
          showOverflowTooltip: true,
          minWidth: 150
        },
        slot: 'fixedPriceTax'
      },
      // 暂定含税总价（卢布）
      {
        attrs: {
          prop: 'provPriceSumTax',
          label: this.$t('cusEntry.supplement20250205.provPriceSumTax'),
          showOverflowTooltip: true,
          minWidth: 150
        },
        slot: 'provPriceSumTax'
      },
      {
        attrs: {
          prop: 'remark',
          label: this.$t('common.remark'),
          showOverflowTooltip: true,
          minWidth: 130
        },
        slot: 'remark'
      }
    ]
    this.bidPriceId = this.urlParams.row.bidPriceId
    if (this.bidPriceId) {
      this.getFormDetail()
    }
  },
  methods: {
    selectionChangeHandle (val) {
      console.log('val::', val)
      this.selectedRows = val
    },
    addNew () {
      this.tableData.push({
        souName: null,
        projectNo: null,
        categoryName: null,
        categoryId: null,
        categoryCode: null,
        itemDesc: null,
        productType: null,
        productConfig: null,
        specification: null,
        brand: null,
        feature: null,
        constructionItem: null,
        quantity: null,
        priceNoTax: null,
        priceSumNoTax: null,
        priceTax: null,
        priceSumTax: null,
        invoiceType: null,
        taxRate: null,
        currency: null,
        bidSection: null,
        region: null,
        unit: null,
        requireQuantity: null,
        subitem: null,
        fixedPriceNoTax: null,
        provPriceSumNoTax: null,
        fixedPriceTax: null,
        provPriceSumTax: null,
        remark: null
      })
      this.refreshTable()
    },
    refreshTable () {
      this.tableData.forEach((item, index) => {
        item.$index = index + 1
      })
    },
    async deleteRows () {
      if (!this.selectedRows || !this.selectedRows.length) {
        // 请选择要删除的列表
        return this.$message.warning(this.$t('cusEntry.supplement20250205.bidEvaTip18'))
      }
      const confirmResult = await this.$confirm(this.$t('common.confirmDeleteRow'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => {})
      if (confirmResult !== 'confirm') return
      let indexList = this.selectedRows.map(item => item.$index)
      this.tableData = this.tableData.filter(item => !indexList.includes(item.$index))
      this.refreshTable()
      let idList = this.selectedRows.filter(item => item.bidPriceId).map(item => item.bidPriceId)
      if (idList.length) {
        let transformParams = transformMQL.save('BidPrice', idList, 'delete')
        const response = await bidPriceHttp.delete(transformParams)
      }
    },
    // 确认选择品类
    comfirmSelect (node, scope) {
      scope.categoryId = node ? node.categoryId : ''
      scope.categoryName = node ? node.categoryName : ''
      scope.categoryCode = node ? node.categoryCode : ''
    },
    async getFormDetail () {
      let transformParams = transformMQL.save('BidPrice', [{ bidPriceId: this.bidPriceId }], 'edit')
      const response = await bidPriceHttp.get(transformParams)
      if (response) {
        this.tableData = response.data || []
        this.refreshTable()
      }
    },
    handleSuccess (data) {
      console.log('data', data)
      if (data && data.data && data.data.length) {
        for (let item of data.data) {
          this.tableData.push(item)
        }
      }
    },
    downloadTemplate () {
      downloadFileLink(
        '/api-sou/npm/bidPrice/downloadTemplate'
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
      })
    },
    async saveBill () {
      if (!this.tableData.length) {
        return this.$message.waring(this.$t('cusEntry.supplement20250205.bidEvaTip19')) // 请新增明细行数据
      }
      this.tableData.forEach(item => {
        item.parentBidPriceId = this.bidPriceId
      })
      let transformParams = transformMQL.save('BidPrice', this.tableData, 'save')
      const response = await bidPriceHttp.save(transformParams)
      if (response) {
        this.$message.success(this.$t('common.success'))
        this.back()
      }
    },
    back () {
      let { tabName } = this.$attrs.params
      this.$emit('tab-remove', tabName)
      this.__setTabTodo('BidPriceLibraryList.getQueryData')
    }
  }
}
</script>
