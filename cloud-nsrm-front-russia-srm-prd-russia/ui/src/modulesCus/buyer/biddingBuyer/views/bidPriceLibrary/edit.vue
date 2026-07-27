<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <el-collapse v-model="colValue">
        <el-collapse-item title="明细信息" name="1">
          <div
            v-if="!disabledFlag"
            class="btns"
            style="margin-bottom:10px;"
          >
            <el-button type="primary" @click="addNew">
              新增
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
              删除
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
          label: '序号'
        }
      },
      {
        attrs: {
          prop: 'souName',
          label: '项目名称',
          showOverflowTooltip: true,
          minWidth: 150
        },
        slot: 'souName'
      },
      {
        attrs: {
          prop: 'projectNo',
          label: '招标项目编号',
          showOverflowTooltip: true,
          minWidth: 150
        },
        slot: 'projectNo'
      },
      {
        attrs: {
          prop: 'categoryName',
          label: '品类',
          showOverflowTooltip: true,
          minWidth: 130
        },
        slot: 'categoryName'
      },
      {
        attrs: {
          prop: 'itemDesc',
          label: '名称',
          showOverflowTooltip: true,
          minWidth: 150
        },
        slot: 'itemDesc'
      },
      {
        attrs: {
          prop: 'productType',
          label: '产品类型',
          showOverflowTooltip: true,
          minWidth: 130
        },
        slot: 'productType'
      },
      {
        attrs: {
          prop: 'productConfig',
          label: '产品配置',
          showOverflowTooltip: true,
          minWidth: 130
        },
        slot: 'productConfig'
      },
      {
        attrs: {
          prop: 'specification',
          label: '规格/型号',
          showOverflowTooltip: true,
          minWidth: 130
        },
        slot: 'specification'
      },
      {
        attrs: {
          prop: 'brand',
          label: '品牌',
          showOverflowTooltip: true,
          minWidth: 130
        },
        slot: 'brand'
      },
      {
        attrs: {
          prop: 'feature',
          label: '项目特征',
          showOverflowTooltip: true,
          minWidth: 130
        },
        slot: 'feature'
      },
      {
        attrs: {
          prop: 'constructionItem',
          label: '施工内容',
          showOverflowTooltip: true,
          minWidth: 130
        },
        slot: 'constructionItem'
      },
      {
        attrs: {
          prop: 'quantity',
          label: '数量/工程量',
          showOverflowTooltip: true,
          minWidth: 130
        },
        slot: 'quantity'
      },
      {
        attrs: {
          prop: 'priceNoTax',
          label: '未税单价（万元）',
          showOverflowTooltip: true,
          minWidth: 130
        },
        slot: 'priceNoTax'
      },
      {
        attrs: {
          prop: 'priceSumNoTax',
          label: '未税总价（万元）',
          showOverflowTooltip: true,
          minWidth: 130
        },
        slot: 'priceSumNoTax'
      },
      {
        attrs: {
          prop: 'priceTax',
          label: '含税单价（万元）',
          showOverflowTooltip: true,
          minWidth: 130
        },
        slot: 'priceTax'
      },
      {
        attrs: {
          prop: 'priceSumTax',
          label: '含税总价（万元）',
          showOverflowTooltip: true,
          minWidth: 130
        },
        slot: 'priceSumTax'
      },
      {
        attrs: {
          prop: 'invoiceType',
          label: '发票类型',
          showOverflowTooltip: true,
          minWidth: 130
        },
        slot: 'invoiceType'
      },
      {
        attrs: {
          prop: 'taxRate',
          label: '税率（%）',
          showOverflowTooltip: true,
          minWidth: 130
        },
        slot: 'taxRate'
      },
      {
        attrs: {
          prop: 'currency',
          label: '币种',
          showOverflowTooltip: true,
          minWidth: 130
        },
        slot: 'currency'
      },
      {
        attrs: {
          prop: 'bidSection',
          label: '标段',
          showOverflowTooltip: true,
          minWidth: 130
        },
        slot: 'bidSection'
      },
      {
        attrs: {
          prop: 'region',
          label: '区域',
          showOverflowTooltip: true,
          minWidth: 130
        },
        slot: 'region'
      },
      {
        attrs: {
          prop: 'unit',
          label: '单位',
          showOverflowTooltip: true,
          minWidth: 130
        },
        slot: 'unit'
      },
      {
        attrs: {
          prop: 'requireQuantity',
          label: '暂定数量/工程量',
          showOverflowTooltip: true,
          minWidth: 130
        },
        slot: 'requireQuantity'
      },
      {
        attrs: {
          prop: 'subitem',
          label: '分项',
          showOverflowTooltip: true,
          minWidth: 130
        },
        slot: 'subitem'
      },
      {
        attrs: {
          prop: 'fixedPriceNoTax',
          label: '固定未税单价（万元）',
          showOverflowTooltip: true,
          minWidth: 150
        },
        slot: 'fixedPriceNoTax'
      },
      {
        attrs: {
          prop: 'provPriceSumNoTax',
          label: '暂定未税总价（万元）',
          showOverflowTooltip: true,
          minWidth: 150
        },
        slot: 'provPriceSumNoTax'
      },
      {
        attrs: {
          prop: 'fixedPriceTax',
          label: '固定含税单价（万元）',
          showOverflowTooltip: true,
          minWidth: 150
        },
        slot: 'fixedPriceTax'
      },
      {
        attrs: {
          prop: 'provPriceSumTax',
          label: '暂定含税总价（万元）',
          showOverflowTooltip: true,
          minWidth: 150
        },
        slot: 'provPriceSumTax'
      },
      {
        attrs: {
          prop: 'remark',
          label: '备注',
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
        return this.$message.warning('请选择要删除的列表')
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
        return this.$message.waring('请新增明细行数据')
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
