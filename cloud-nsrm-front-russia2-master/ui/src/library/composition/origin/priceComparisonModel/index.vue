<template>
  <el-container
    class="flex-container price-comparison"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        ref="formWrapper"
        :form-array="formWrapperArray"
        :pre-form-obj="{ round: $attrs.params.row.round }"
        form-label-width="120px"
        @getFormData="searchForm"
      />

      <BaseTable
        ref="priceComparisonTable"
        stripe
        :data="priceComparisonTableData"
        :columns="priceComparisonTableColumns"
        :empty-text="$t('components.noData')"
        border
        height="100%"
        class="price-comparison-table"
        :cell-class-name="cellClassName"
        :span-method="tableSpanMethod"
      >
        <template #subItemName="{row}">
          <el-button type="text" @click="openSubItemDialog(row)">
            {{ row.subItemName }}
          </el-button>
        </template>
      </BaseTable>
    </el-main>

    <!-- 分项明细弹窗 -->
    <PriceComparisonItemDialog
      :visible.sync="priceComparisonItemDialogVisible"
      :data="editRow"
    />
  </el-container>
</template>
<script>
import { maxNumberOption } from 'lib@/composition/commonComposition'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import BaseTable from 'lib@/components/BaseTable'
import { targetNumReveal } from 'lib@/composition/origin/composition'
import { getApiByBusinessType, getFeildByBuinessTypeAndField } from './utils'
import PriceComparisonItemDialog from './priceComparisonItemDialog'

export default {
  name: 'PriceComparisonModel',
  components: {
    FormWrapper,
    BaseTable,
    PriceComparisonItemDialog
  },
  data () {
    return {
      formWrapperArray: [],
      formWrapperObj: {
        round: this.$attrs.params.row.round
      },
      navTabsParamsRow: this.$attrs.params.row || {},
      paramsBusinessType: this.$attrs.params.businessType,
      paramsPricingType: this.$attrs.params.pricingType,
      priceComparisonTableData: [],
      priceComparisonTableColumns: [],
      rowspan: 1, // 合并行的个数
      originalDataList: [], // 原始数据
      priceComparisonItemDialogVisible: false,
      editRow: null
    }
  },
  computed: {
    // 业务类型
    businessType () {
      // 业务类型、简易询价、招标、项目式询价 ['INQUIRY', 'BIDING', 'BARGAIN']
      const type = this.paramsBusinessType
      return {
        // 简易询价[LTS]
        isInquiry: [BUSINESS_TYPE_ENUM.INQUIRY, BUSINESS_TYPE_ENUM.INQUIRY_LTS].includes(type),
        // 招标
        isBiding: [BUSINESS_TYPE_ENUM.BIDING, BUSINESS_TYPE_ENUM.BIDDING_LTS].includes(type),
        // 项目式询价[LTS]
        isBargain: [BUSINESS_TYPE_ENUM.BARGAIN, BUSINESS_TYPE_ENUM.BARGAIN_LTS].includes(type)
      }
    }
  },
  created () {
    this.arrangeFormWrapperArray(false)
    this.getPriceComparisonData()
  },
  methods: {
    /* 点击查询 */
    searchForm (val) {
      // 没有轮次默认查询当前轮次
      if (!val.round) {
        this.$refs.formWrapper.setValue('round', this.navTabsParamsRow.round)
      }
      this.formWrapperObj = { ...(val || {}) }
      this.$nextTick(() => {
        this.serachDataList()
      })
    },

    /** 编排查询条件 */
    arrangeFormWrapperArray (data) {
      if (data && !this.formWrapperObj.round) {
        // 轮次信息被清空，以后端返回轮次稳准，否则默认
        this.formWrapperObj.round = data.round || this.navTabsParamsRow.round
        this.$refs.formWrapper.setValue('round', this.formWrapperObj.round)
      }

      // 物料需求信息
      const itemList = data

      if (itemList && this.formWrapperArray.length > 0) {
        // 编排物料编码options
        this.formWrapperArray[0].options = itemList
          // 过滤空的编码
          .filter(itemCode => targetNumReveal(itemCode))
          // 编排下拉框所需数据
          .map((itemCode, index) => {
            return {
              id: `itemCode-${index}`,
              label: itemCode,
              value: itemCode
            }
          })
        return
      }

      // 只格式化一次
      this.formWrapperArray = [
        // 物料编码
        {
          prop: 'itemCode',
          label: () => this.$t('bidMod.itemCode'),
          type: 'select',
          options: []
        },
        {
          prop: 'itemDesc',
          label: () => this.$t('bidMod.itemName')
        },
        {
          prop: 'round',
          label: () => this.$t('bidMod.bidingRound'),
          type: 'select',
          options: () => maxNumberOption(this.navTabsParamsRow.round || 1)
        }
      ]
    },

    /** 编排表格 */
    arrangeTableColumns (data) {
      let columns = [
        // 物料编码
        {
          attrs: {
            prop: 'itemCode',
            minWidth: '100',
            fixed: 'left',
            label: this.$t('bidMod.itemCode'),
            formatter: (_row, _column, value) => targetNumReveal(value)
          }
        },
        // 物料名称
        {
          attrs: {
            prop: 'itemDesc',
            minWidth: '110',
            fixed: 'left',
            label: this.$t('bidMod.itemName'),
            showOverflowTooltip: true
          }
        },
        // 轮次
        {
          attrs: {
            prop: 'round',
            minWidth: '80',
            label: this.$t('bidMod.bidingRound')
          }
        },
        // 需求数量
        {
          attrs: {
            prop: 'requireQuantity',
            minWidth: '90',
            label: this.$t('bidMod.demandQuantity2')
          }
        },
        // 目标价
        {
          attrs: {
            prop: getFeildByBuinessTypeAndField(this.paramsBusinessType, 'notaxTargetPrice'),
            align: 'right',
            minWidth: '90',
            label: this.$t('bidMod.common.notaxTargetPrice'),
            showOverflowTooltip: true
          }
        }
      ]

      columns.push({
        attrs: {
          prop: 'subItemName',
          minWidth: '120',
          label: this.$t('bidMod.common.subItemName'),
          showOverflowTooltip: true
        },
        slot: 'subItemName'
      })

      // 供应商动态列
      if (data.length) {
        for (let item of data) {
          columns.push({
            attrs: {
              prop: item.prop,
              minWidth: '120',
              label: item.label,
              showOverflowTooltip: true
            }
          })
        }
      }

      this.priceComparisonTableColumns = columns
    },

    /** 编排数据 */
    arrangeTableData (data) {
      if (!data || !data.length) return
      this.priceComparisonTableData = data.map(item => {
        return {
          ...item,
          ...item.dynamicColMap,
          subItemName: item.quoteFieldName
        }
      })
    },

    /* 判断返回单元格样式 */
    cellClassName ({ row, column }) {

    },

    /* 根据报价类型合并单元格 */
    tableSpanMethod ({ row, column, rowIndex, columnIndex }) {
      let rowspan = this.rowspan ?? 1
      if ([0, 1, 2, 3, 4].includes(columnIndex)) {
        if (rowIndex % rowspan === 0) {
          return {
            rowspan: rowspan,
            colspan: 1
          }
        } else {
          return {
            rowspan: 0,
            colspan: 0
          }
        }
      }
    },

    /* 查询比价数据 */
    async getPriceComparisonData () {
      const url = getApiByBusinessType(this.paramsBusinessType)
      if (!url) {
        console.error(this.$t('bidMod.unableGet'))
        return
      }
      const response = await this.$http({
        url,
        method: 'POST',
        data: {
          // 业务ID
          projectId: this.navTabsParamsRow.id
          // round:this.formWrapperObj.round  // 前端要查询所有数据所以不传轮次便于做前端搜索
        },
        loading: true
      })
      if (response && response.data) {
        const { dataList = [], dynamicColList = [] } = response.data
        let itemCodeList = []
        for (let item of dataList) {
          if (!itemCodeList.includes(item.itemCode)) itemCodeList.push(item.itemCode)
        }
        if (itemCodeList.length) this.rowspan = dataList.length / itemCodeList.length
        this.arrangeFormWrapperArray(itemCodeList)
        this.arrangeTableColumns(dynamicColList)
        this.originalDataList = dataList
        this.serachDataList()
      }
    },

    /** 查询搜索处理 */
    serachDataList () {
      let dataList = this.originalDataList.concat()
      Object.keys(this.formWrapperObj).forEach(key => {
        if (this.formWrapperObj[key]) {
          if (key === 'itemDesc') {
            dataList = dataList.filter(item => item[key].includes(this.formWrapperObj[key]))
          } else {
            dataList = dataList.filter(item => item[key] === this.formWrapperObj[key])
          }
        }
      })
      this.arrangeTableData(dataList)
      this.doLayout()
    },

    /** 打开分项明细弹窗 */
    openSubItemDialog (row) {
      if (!row.souTempSelectDetailDataVOList?.length) {
        this.$message.warning(row.errorMessage)
        return
      }
      this.priceComparisonItemDialogVisible = true
      this.editRow = row
    },

    /** 表格错位 */
    doLayout () {
      this.$nextTick(() => {
        this.$refs.priceComparisonTable.$children[0].doLayout()
      })
    }
  }
}

</script>
