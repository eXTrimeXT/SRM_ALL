<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main>
      <FormWrapper :form-array="searchFormConfig" @getFormData="getQueryData" />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <!-- <el-button type="primary" @click="editTab('add',{})">
            {{ $t('common.add') }}
          </el-button> -->
        </template>
      </MainHeader>

      <TableView
        ref="list"
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :com-active="$attrs['changeTab']"
        :checkbox="false"
        :checkChange="handleCurrentChange"
        open-custom-table
        :url="tableViewUrl"
        :adeptMeiQl="true"
      />
    </el-main>
  </el-container>
</template>

<script>
import bidPriceHttp from './api'
import { tabTodoWatch } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import BidPriceLibraryDetail from './edit'
import { transformMQL } from 'lib@/utils/util'

export default {
  name: 'BidPriceLibraryList',

  components: {
    TableView,
    MainHeader,
    FormWrapper
  },

  mixins: [tabTodoWatch],

  data () {
    return {
      tableViewUrl: bidPriceHttp.listPageUrl,
      tableHeader: [],
      tableData: [],
      searchFormConfig: [
        {
          prop: 'categoryName',
          label: this.$t('dataConfMod.category'),
          type: 'catSelect',
          showKey: 'categoryName'
        },
        {
          prop: 'vendorName',
          label: this.$t('common.vendorName'),
          type: 'quicksearch',
          showKey: 'companyName',
          propKey: 'companyName',
          name: 'scc_sup_company_info_display'
        },
        {
          prop: 'souName',
          label: this.$t('bidMod.bidingName')
        },
        {
          prop: 'projectNo',
          label: this.$t('bidMod.bidingNumCla')
        },
        {
          prop: 'brand',
          label: this.$t('dataConfMod.band')
        },
        {
          prop: 'itemDesc',
          label: this.$t('bidMod.designation')
        },
        {
          prop: 'specification',
          label: this.$t('common.specification')
        }
      ],
      queryParam: {},
      selectedRows: [] // 标记勾选行
    }
  },

  watch: {
    '$route.params': {
      // 寻源需求等其它地方跳转过来
      handler (nVal) {
        const { from, row } = nVal
        if (from) {
          this.editTab('view', row)
        }
      },
      immediate: true,
      deep: true
    }
  },

  mounted () {
    this.tableHeader = [
      {
        prop: 'souName',
        label: this.$t('bidMod.bidingName'),
        minWidth: 150
      },
      {
        prop: 'projectNo',
        label: this.$t('bidMod.bidingNumCla'),
        minWidth: 150
      },
      // 招标专家
      {
        prop: 'souPrincipal',
        label: this.$t('cusEntry.bidMod.extBidNickname'),
        minWidth: 130
      },
      {
        prop: 'categoryName',
        label: this.$t('dataConfMod.category'),
        minWidth: 130
      },
      {
        prop: 'itemDesc',
        label: this.$t('bidMod.designation'),
        minWidth: 120
      },
      {
        prop: 'vendorCode',
        label: this.$t('common.vendorCode'),
        minWidth: 120
      },
      {
        prop: 'vendorName',
        label: this.$t('common.vendorName'),
        minWidth: 180
      },
      {
        prop: 'specification',
        label: this.$t('common.specification'),
        minWidth: 120
      },
      {
        prop: 'brand',
        label: this.$t('dataConfMod.band'),
        minWidth: 120
      },
      // 项目特征
      {
        prop: 'feature',
        label: this.$t('cusEntry.supplement20250205.feature'),
        minWidth: 120
      },
      // 施工内容
      {
        prop: 'constructionItem',
        label: this.$t('cusEntry.supplement20250205.constructionItem'),
        minWidth: 120
      },
      // 数量/工程量
      {
        prop: 'quantity',
        label: this.$t('cusEntry.supplement20250205.quantity'),
        minWidth: 120
      },
      // 未税单价（卢布）
      {
        prop: 'priceNoTax',
        label: this.$t('cusEntry.supplement20250205.unitPriceExcludingTaxRUB'),
        minWidth: 150
      },
      // 未税总价（卢布）
      {
        prop: 'priceSumNoTax',
        label: this.$t('cusEntry.supplement20250205.totalPriceExcludingTaxRUB'),
        minWidth: 150
      },
      // 含税单价（卢布）
      {
        prop: 'priceTax',
        label: this.$t('cusEntry.supplement20250205.priceTax'),
        minWidth: 150
      },
      // 含税总价（卢布）
      {
        prop: 'priceSumTax',
        label: this.$t('cusEntry.supplement20250205.priceSumTax'),
        minWidth: 150
      },
      {
        prop: 'invoiceType',
        label: this.$t('accountMod.invoiceType'),
        dataType: 'dict',
        code: 'SOU_BIDPRICE_INVOICE_TYPE',
        minWidth: 120
      },
      {
        prop: 'taxRate',
        label: this.$t('bidMod.taxRate2'),
        minWidth: 120
      },
      {
        prop: 'currency',
        label: this.$t('bidMod.currency_price'),
        dataType: 'dict',
        code: 'currency',
        minWidth: 120
      },
      // 标段
      {
        prop: 'bidSection',
        label: this.$t('cusEntry.bidMod.extBidSection'),
        minWidth: 120
      },
      {
        prop: 'region',
        label: this.$t('vendorMod.area1'),
        minWidth: 120
      },
      {
        prop: 'unit',
        label: this.$t('dataConfMod.unit'),
        dataType: 'dict',
        code: 'unti',
        minWidth: 120
      },
      // 暂定数量/工程量
      {
        prop: 'requireQuantity',
        label: this.$t('cusEntry.supplement20250205.requireQuantity'),
        minWidth: 150
      },
      // 分项
      {
        prop: 'subitem',
        label: this.$t('cusEntry.supplement20250205.subitem'),
        minWidth: 120
      },
      // 固定未税单价（卢布）
      {
        prop: 'fixedPriceNoTax',
        label: this.$t('cusEntry.supplement20250205.fixedPriceNoTax'),
        minWidth: 160
      },
      // 暂定未税总价（卢布）
      {
        prop: 'provPriceSumNoTax',
        label: this.$t('cusEntry.supplement20250205.provPriceSumNoTax'),
        minWidth: 160
      },
      // 固定含税单价（卢布）
      {
        prop: 'fixedPriceTax',
        label: this.$t('cusEntry.supplement20250205.fixedPriceTax'),
        minWidth: 160
      },
      // 暂定含税总价（卢布）
      {
        prop: 'provPriceSumTax',
        label: this.$t('cusEntry.supplement20250205.provPriceSumTax'),
        minWidth: 160
      },
      {
        prop: 'remark',
        label: this.$t('common.remark'),
        minWidth: 120
      },
      {
        prop: 'operation',
        label: this.$t('bidMod.operation'),
        minWidth: 130,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            show: row => true,
            formattor: () => this.$t('common.edit'),
            code: 'bidPriceLibrary:edit',
            callback: row => {
              this.editTab('edit', row)
            }
          }
        ]
      }
    ]
    this.$nextTick(() => {
      this.getQueryData()
    })
  },

  methods: {
    /* 查询列表数据 */
    getQueryData (params = {}) {
      this.queryParam = transformMQL.listPageData({
        type: 'BidPrice',
        action: 'query',
        params: {
          ...params,
          parentBidPriceId: -1
        }
      })

      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },

    editTab (type, row) {
      const map = new Map([
        // 新增
        [
          'add',
          {
            component: BidPriceLibraryDetail,
            params: {
              flag: type,
              row,
              tabName: 'bidPriceLibrary'
            },
            title: this.$t('cusEntry.supplement20250121.bidPriceLibrary'), // 招标价格库
            name: 'bidPriceLibrary'
          }
        ],
        // 编辑
        [
          'edit',
          {
            component: BidPriceLibraryDetail,
            params: {
              flag: type,
              row,
              tabName: row.projectNo
            },
            title: this.$t('cusEntry.supplement20250121.bidPriceLibrary') + (row.projectNo || ''),
            name: row.projectNo
          }
        ],
        // 查看
        [
          'view',
          {
            component: BidPriceLibraryDetail,
            params: {
              flag: type,
              row,
              tabName: row.projectNo
            },
            title: this.$t('cusEntry.supplement20250121.bidPriceLibrary') + (row.projectNo || ''),
            name: row.projectNo
          }
        ]
      ])
      this.$emit('tab-add', map.get(type))
    },

    /* 选中行 */
    handleCurrentChange (val) {
      console.log('val:::', val)
      this.selectedRows = val
    },

    /** 删除 */
    async deleteRows (row) {
      const confirmResult = await this.$confirm(this.$t('common.confirmDeleteRow'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => {})
      if (confirmResult !== 'confirm') return
      await bidPriceHttp.delete(row)
      this.$message.success(this.$t('common.successDelete'))
      this.getQueryData()
    }
  }
}
</script>
