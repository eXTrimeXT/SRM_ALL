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
            新增
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
          label: '品类',
          type: 'catSelect',
          showKey: 'categoryName'
        },
        {
          prop: 'vendorName',
          label: '供应商名称',
          type: 'quicksearch',
          showKey: 'companyName',
          propKey: 'companyName',
          name: 'scc_sup_company_info_display'
        },
        {
          prop: 'souName',
          label: '项目名称'
        },
        {
          prop: 'projectNo',
          label: '招标项目编号'
        },
        {
          prop: 'brand',
          label: '品牌'
        },
        {
          prop: 'itemDesc',
          label: '名称'
        },
        {
          prop: 'specification',
          label: '规格/型号'
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
        label: '项目名称',
        minWidth: 150
      },
      {
        prop: 'projectNo',
        label: '招标项目编号',
        minWidth: 150
      },
      {
        prop: 'souPrincipal',
        label: '招标负责人',
        minWidth: 130
      },
      {
        prop: 'categoryName',
        label: '品类',
        minWidth: 130
      },
      {
        prop: 'vendorName',
        label: '供应商名称',
        minWidth: 180
      },
      {
        prop: 'itemDesc',
        label: '名称',
        minWidth: 120
      },
      {
        prop: 'specification',
        label: '规格/型号',
        minWidth: 120
      },
      {
        prop: 'brand',
        label: '品牌',
        minWidth: 120
      },
      {
        prop: 'feature',
        label: '项目特征',
        minWidth: 120
      },
      {
        prop: 'constructionItem',
        label: '施工内容',
        minWidth: 120
      },
      {
        prop: 'quantity',
        label: '数量/工程量',
        minWidth: 120
      },
      {
        prop: 'priceNoTax',
        label: '未税单价（万元）',
        minWidth: 150
      },
      {
        prop: 'priceSumNoTax',
        label: '未税总价（万元）',
        minWidth: 150
      },
      {
        prop: 'priceTax',
        label: '含税单价（万元）',
        minWidth: 150
      },
      {
        prop: 'priceSumTax',
        label: '含税总价（万元）',
        minWidth: 150
      },
      {
        prop: 'invoiceType',
        label: '发票类型',
        dataType: 'dict',
        code: 'SOU_BIDPRICE_INVOICE_TYPE',
        minWidth: 120
      },
      {
        prop: 'taxRate',
        label: '税率（%）',
        minWidth: 120
      },
      {
        prop: 'currency',
        label: '币种',
        dataType: 'dict',
        code: 'currency',
        minWidth: 120
      },
      {
        prop: 'bidSection',
        label: '标段',
        minWidth: 120
      },
      {
        prop: 'region',
        label: '区域',
        minWidth: 120
      },
      {
        prop: 'unit',
        label: '单位',
        dataType: 'dict',
        code: 'unti',
        minWidth: 120
      },
      {
        prop: 'requireQuantity',
        label: '暂定数量/工程量',
        minWidth: 150
      },
      {
        prop: 'subitem',
        label: '分项',
        minWidth: 120
      },
      {
        prop: 'fixedPriceNoTax',
        label: '固定未税单价（万元）',
        minWidth: 160
      },
      {
        prop: 'provPriceSumNoTax',
        label: '暂定未税总价（万元）',
        minWidth: 160
      },
      {
        prop: 'fixedPriceTax',
        label: '固定含税单价（万元）',
        minWidth: 160
      },
      {
        prop: 'provPriceSumTax',
        label: '暂定含税总价（万元）',
        minWidth: 160
      },
      {
        prop: 'remark',
        label: '备注',
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
            title: '招标价格库',
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
            title: '招标价格库编辑' + (row.projectNo || ''),
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
            title: '招标价格库查看' + (row.projectNo || ''),
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
