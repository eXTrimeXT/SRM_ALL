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
      />
    </el-main>
  </el-container>
</template>

<script>
/**
 * 比价
 */
import { tabTodoWatch } from '@/utils/mixins'
import { targetNumReveal } from 'lib@/composition/origin/composition'
import { BUSINESS_TYPE_ENUM } from 'lib@/composition/origin/enum'
import { mappingPropByBusinessTypeAndKey, getApiByBusinessTypeAndPricingType } from './utils'
import { maxNumberOption } from 'lib@/composition/commonComposition'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import BaseTable from 'lib@/components/BaseTable'

export default {
  name: 'PriceComparison',

  components: {
    FormWrapper,
    BaseTable
  },

  mixins: [tabTodoWatch],

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
      priceComparisonTableColumns: []
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
        isBiding: [BUSINESS_TYPE_ENUM.BIDING, BUSINESS_TYPE_ENUM.BIDDING_LTS].includes(type),
        // 项目式询价[LTS]
        isBargain: [BUSINESS_TYPE_ENUM.BARGAIN, BUSINESS_TYPE_ENUM.BARGAIN_LTS].includes(type)
      }
    },

    // 报价类型
    pricingType () {
      // 普通报价、公式报价、模型报价、模板报价 ['SIMPLE_PRICING', 'FORMULA_PRICING', 'MODEL_PRICING', 'TEMPLATE_PRICING']
      const type = this.paramsPricingType
      return {
        isSimplePricing: type === mappingPropByBusinessTypeAndKey(this.paramsBusinessType, 'SIMPLE_PRICING'),
        isFormulaPricing: type === mappingPropByBusinessTypeAndKey(this.paramsBusinessType, 'FORMULA_PRICING'),
        isModelPricing: type === mappingPropByBusinessTypeAndKey(this.paramsBusinessType, 'MODEL_PRICING'),
        isTemplatePricing: type === mappingPropByBusinessTypeAndKey(this.paramsBusinessType, 'TEMPLATE_PRICING')
      }
    }
  },

  created () {
    this.arrangeFormWrapperArray(false)
    this.getPriceComparisonData()
  },

  methods: {
    /* 切换当前页签，判断单号是否当前单据，执行表格doLayout修复错位 */
    doLayout (number) {
      if (
        this.priceComparisonTableData.length > 0 &&
        this.$refs.priceComparisonTable &&
        (
          (number && this.navTabsParamsRow.number && number === this.navTabsParamsRow.number) ||
          !number ||
          !this.navTabsParamsRow.number
        )
      ) {
        this.$refs.priceComparisonTable.$children[0].doLayout()
      }
    },

    /* 编排查询条件 */
    arrangeFormWrapperArray (data) {
      if (data && !this.formWrapperObj.round) {
        // 轮次信息被清空，以后端返回轮次稳准，否则默认
        this.formWrapperObj.round = data.round || this.navTabsParamsRow.round
        this.$refs.formWrapper.setValue('round', this.formWrapperObj.round)
      }

      // 物料需求信息
      const { itemList } = data

      if (itemList && this.formWrapperArray.length > 0) {
        // 编排物料编码options
        this.formWrapperArray[0].options = itemList
          // 过滤空的编码
          .filter(item => targetNumReveal(item.itemCode))
          // 编排下拉框所需数据
          .map((item, index) => {
            return {
              id: `itemCode-${index}`,
              label: item.itemCode,
              value: item.itemCode
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

    /* 点击查询 */
    searchForm (val) {
      this.formWrapperObj = { ...(val || {}) }
      this.$nextTick(() => {
        this.getPriceComparisonData()
      })
    },

    /* 查询比价数据 */
    async getPriceComparisonData () {
      const url = getApiByBusinessTypeAndPricingType(this.paramsBusinessType, this.paramsPricingType)
      if (!url) {
        console.error(this.$t('bidMod.unableGet'))
        return
      }

      const data = await this.$http({
        url,
        method: 'POST',
        data: {
          // 业务ID
          projectId: this.navTabsParamsRow.id,
          ...this.formWrapperObj
        },
        loading: true
      })
      if (data && data.data) {
        this.arrangeFormWrapperArray(data.data)
        this.arrangeTableColumns(data.data)
        this.arrangeTableData(data.data)
        this.$nextTick(() => {
          this.$refs.priceComparisonTable.$children[0].doLayout()
        })
      }
    },

    /* 根据业务类型 和 报价类型 编排表头 */
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
            prop: 'quantity',
            minWidth: '90',
            label: this.$t('bidMod.demandQuantity2')
          }
        }
      ]

      // 询价
      if (this.businessType.isInquiry) {
        // 目标价
        columns.push({
          attrs: {
            prop: 'notaxTargetPrice',
            minWidth: '90',
            label: this.$t('bidMod.common.notaxTargetPrice'),
            showOverflowTooltip: true
          }
        })
        if (this.pricingType.isSimplePricing) {
          // 普通报价
          columns.push({
            attrs: {
              prop: 'quantityExtent',
              minWidth: '110',
              label: this.$t('bidMod.common.quantityExtent'),
              showOverflowTooltip: true
            }
          })
        }
      }

      // 招标
      if (this.businessType.isBiding && this.pricingType.isSimplePricing) {
        // 普通报价
        columns.push({
          attrs: {
            prop: 'quantityExtent',
            minWidth: '110',
            label: this.$t('bidMod.common.quantityExtent'),
            showOverflowTooltip: true
          }
        })
      }

      // 公式报价
      if (this.pricingType.isFormulaPricing) {
        // 价格明细
        columns.push({
          attrs: {
            minWidth: '110',
            label: this.$t('bidMod.common.factorName'),
            prop: 'factorName',
            showOverflowTooltip: true
          }
        })
      }

      // 模型报价
      if (this.pricingType.isModelPricing) {
        columns = columns.concat([
          // 费用大类
          {
            attrs: {
              minWidth: '100',
              label: this.$t('bidMod.common.costItem'),
              prop: 'description',
              showOverflowTooltip: true
            }
          },
          // 费用项
          {
            attrs: {
              minWidth: '100',
              label: this.$t('bidMod.common.costItemDesc'),
              prop: 'costDescription',
              showOverflowTooltip: true
            }
          }
        ])
      }

      // 编排企业列
      data.vendorList.forEach(item => {
        columns.push({
          attrs: {
            minWidth: '120',
            label: item.vendorName,
            // 加一个vendor标识
            prop: `vendor-${item.vendorId.toString()}`,
            showOverflowTooltip: true
          }
        })
      })

      this.priceComparisonTableColumns = columns
    },

    /**
     * 编排数据
     * - 普通报价
     *  - 简易询价：先以物料为维度，再判断是否存在阶梯价，如果有就以数量范围维度
     *  - 其他：物料维度
     * - 公式报价：价格明细维度
     * - 模型报价：费用项维度
     * @param data
     */
    arrangeTableData (data) {
      const {
        // 物料需求信息
        itemList,
        // 供应商报价
        orderItemList,
        // 公式报价map
        orderItemFormulaMap,
        // 模型报价Map
        reqLineModelMap,
        // 简易询价 - 阶梯价
        itemLadderPriceMap
      } = data

      let tableData = []

      // 检查必须的信息
      if (!itemList || !Array.isArray(itemList) || !orderItemList || !Array.isArray(orderItemList)) {
        return this.$message.warning(this.$t('bidMod.common.interfaceWrong'))
      }

      // 普通报价 模板报价
      if (this.pricingType.isSimplePricing || this.pricingType.isTemplatePricing) {
        if (this.businessType.isInquiry || this.businessType.isBiding) {
          // 简易询价
          // 因为可能存在阶梯价 先以物料为维度，再判断是否存在阶梯价
          let list = []
          itemList.forEach(item => {
            // 当前物料信息
            let itemResult = {
              ...this.arrangeReqLineObj(item.souItemId, itemList)
            }

            // 当前物料的阶梯价
            const itemLadderPrice = itemLadderPriceMap[item.souItemId]

            if (itemLadderPrice && Array.isArray(itemLadderPrice)) {
              // 当前物料存在阶梯价
              list = list.concat(itemLadderPrice.map((ladderItem, ladderIndex) => {
                const {
                  // 开始数量
                  beginQuantity,
                  // 截止数量
                  endQuantity,
                  // 阶梯价模板ID
                  souItemLadderId
                } = ladderItem
                // 数值范围 截止数量可能不存在
                // 存在截止数量： 0 ≤ Q < 100
                // 不存在截止数量：Q > 1000
                const quantityExtent = endQuantity ? `${beginQuantity} ≤ Q < ${endQuantity}` : `Q > ${beginQuantity}`

                // 找到当前物料当前阶梯范围的所有供应商报价
                const vendorPriceObj = this.arrangeVendorPriceByPriceType({
                  souItemId: item.souItemId,
                  isLadder: true,
                  // 阶梯价ID
                  souItemLadderId
                }, orderItemList)

                return {
                  ...itemResult,
                  quantityExtent,
                  // 合并单元格
                  rowspan: ladderIndex === 0 ? itemLadderPrice.length : 0,
                  isLadder: true,
                  ...vendorPriceObj
                }
              }))
            } else {
              // 不存在阶梯价 直接push当前物料
              list.push({
                ...itemResult,
                // 数量范围
                quantityExtent: '/',
                rowspan: 1,
                // 供应商报价数据
                ...this.arrangeVendorPriceByPriceType({ souItemId: item.souItemId }, orderItemList)
              })
            }
          })

          tableData = list
        } else {
          tableData = itemList.map(item => {
            return {
              // 只取部分字段，并根据不同类型进行映射
              ...this.arrangeReqLineObj(item.souItemId, itemList),
              rowspan: 1,
              // 供应商报价数据 { '292111090614272': 100 }
              ...this.arrangeVendorPriceByPriceType({ souItemId: item.souItemId }, orderItemList)
            }
          })
        }
      }

      // 公式报价
      if (this.pricingType.isFormulaPricing) {
        // 1.orderItemFormulaMap编排成数组，把key放到数组中的对象项
        // 2.根据souItemId，找到物料信息
        // 3.根据souItemId，找出当前物料所有供应商报价，并格式化供应商报价对象
        let list = []
        // 得到以公式项为维度的list
        Object.keys(orderItemFormulaMap).forEach((item, index) => {
          list = list
            // 总价
            .concat([
              {
                souItemId: item,
                factorId: `formulaTotal-${index}`,
                factorName: this.$t('bidMod.common.totalPrice')
              }
            ])
            // 公式项
            .concat(orderItemFormulaMap[item].map(factorItem => {
              return {
                souItemId: item,
                factorId: factorItem.factorId.toString(),
                factorName: factorItem.factorName
              }
            }))
            // 公式值
            .concat([
              {
                souItemId: item,
                factorId: `formulaValueText-${index}`,
                factorName: this.$t('bidMod.common.formulaValueText'),
                formulaValue: orderItemFormulaMap[item][0] ? orderItemFormulaMap[item][0].formulaValue : ''
              }
            ])
        })

        // 数据中需求物料与行数对应关系 map { [souItemId]: 3 }
        const requirementLineLengthMap = list.reduce((obj, cur) => {
          if (!obj[cur.souItemId]) {
            obj[cur.souItemId] = 1
          } else {
            obj[cur.souItemId]++
          }
          return obj
        }, {})

        // 写入物料信息 和 项报价
        list = list.map((item, index) => {
          return {
            ...item,
            // 报价
            ...this.arrangeVendorPriceByPriceType(item, orderItemList),
            // 用于需求合并行
            rowspan: index === 0 || list[index - 1].souItemId !== item.souItemId ? requirementLineLengthMap[item.souItemId] : 0,
            // 物料和轮次相关信息
            ...this.arrangeReqLineObj(item.souItemId, itemList)
          }
        })

        tableData = tableData.concat(list)
      }

      // 模型报价
      if (this.pricingType.isModelPricing) {
        // 1.reqLineModelMap编排成数组，把key放到数组中的对象项
        // 2.根据souItemId，找到物料信息
        // 3.根据souItemId，找出当前物料所有供应商报价，并格式化供应商报价对象
        let list = []
        // 得到以模型项为维度的list
        Object.keys(reqLineModelMap).forEach((item, index) => {
          list = list
            // 总价
            .concat([
              {
                souItemId: item,
                modelId: `modelTotal-${index}`,
                description: this.$t('bidMod.common.totalPrice'),
                // 总价行的费用项用/代替
                costDescription: '/'
              }
            ])
            // 费用
            .concat(reqLineModelMap[item].map(descItem => {
              return {
                souItemId: item,
                modelId: descItem.modelPriceLineTemplateId.toString(),
                description: descItem.description,
                costDescription: descItem.costDescription
              }
            }))
        })

        // 数据中需求物料与行数对应关系 map { [souItemId]: 3 }
        const requirementLineLengthMap = list.reduce((obj, cur) => {
          if (!obj[cur.souItemId]) {
            obj[cur.souItemId] = 1
          } else {
            obj[cur.souItemId]++
          }
          return obj
        }, {})

        // 写入物料信息 和 模型项报价
        list = list.map((item, index) => {
          let descriptionRowspan = 1
          // 前一个费用大类不等于当前费用大类，记录出现的长度，默认是按顺序的
          if (item.modelId.indexOf('modelTotal-') !== 0) {
            if (list[index - 1].description !== item.description) {
              descriptionRowspan = reqLineModelMap[item.souItemId].filter(mapItem => mapItem.description === item.description).length
            } else {
              descriptionRowspan = 0
            }
          }

          return {
            ...item,
            // 报价
            ...this.arrangeVendorPriceByPriceType(item, orderItemList),
            // 用于合并物料信息行
            rowspan: index === 0 || list[index - 1].souItemId !== item.souItemId ? requirementLineLengthMap[item.souItemId] : 0,
            // 用于合并费用大类
            descriptionRowspan,
            // 物料和轮次相关信息
            ...this.arrangeReqLineObj(item.souItemId, itemList)
          }
        })

        tableData = tableData.concat(list)
      }

      this.priceComparisonTableData = tableData
    },

    /* 根据物料需求信息返回物料需求相关信息和轮次 */
    arrangeReqLineObj (souItemId, itemList) {
      if (!souItemId || !itemList) {
        console.error('arrangeReqLineObj error')
        return {}
      }

      // 物料信息
      const reqLine = itemList.find(reqLineItem => {
        return reqLineItem.souItemId.toString() === souItemId.toString()
      })

      if (reqLine) {
        // 只取部分字段，并根据不同类型进行映射
        return {
          itemId: reqLine.itemId,
          // 物料编码
          itemCode: reqLine.itemCode,
          // 物料名称描述
          itemDesc: reqLine.itemDesc,
          // 需求数量
          quantity: reqLine.requireQuantity,
          // 轮次
          round: this.formWrapperObj.round,
          // 目标价 目前就简易询价有
          notaxTargetPrice: reqLine.notaxTargetPrice || ''
        }
      }
      return {
        // 轮次
        round: this.formWrapperObj.round
      }
    },

    /* 根据报价类型编排供应商报价数据 */
    arrangeVendorPriceByPriceType (itemObj, orderItemList) {
      const {
        souItemId = '',
        factorId = '',
        formulaValue = '',
        modelId = '',
        // 是否阶梯报价
        isLadder = false,
        // 阶梯项ID
        souItemLadderId = ''
      } = itemObj

      if (!souItemId) {
        return {}
      }

      // 先找出当前物料所有供应商报价，一个物料会有多个
      const reqOrderLineList = orderItemList
        .filter(orderItem => (orderItem.souItemId || '').toString() === souItemId.toString())

      if (reqOrderLineList.length === 0) {
        return {}
      }

      // 普通报价 模板报价
      if (this.pricingType.isSimplePricing || this.pricingType.isTemplatePricing) {
        if (!isLadder) {
          // 非阶梯价 编排报价对象
          return reqOrderLineList.reduce((obj, cur) => {
            if (cur.vendorId) {
              // 未税报价
              obj[`vendor-${cur.vendorId.toString()}`] = cur.standardNotaxPrice
            }
            return obj
          }, {})
        } else {
          // 阶梯价 目前就简易询价有
          return reqOrderLineList.reduce((obj, cur) => {
            if (cur.vendorId) {
              // 计算报价项
              if (cur.souOrderItemLadderList && this.propertyCanToNumberFromString(cur.standardNotaxPrice)) {
                // 根据阶梯项ID找到报价
                const value = cur.souOrderItemLadderList.find(ladderPriceItem => {
                  return ladderPriceItem.souItemLadderId.toString() === souItemLadderId.toString()
                })
                obj[`vendor-${cur.vendorId.toString()}`] = value && typeof value === 'object' ? value.orderNotaxPrice || '' : ''
              } else {
                obj[`vendor-${cur.vendorId.toString()}`] = cur.standardNotaxPrice
              }
            }
            return obj
          }, {})
        }
      }

      // 公式报价
      if (this.pricingType.isFormulaPricing) {
        return reqOrderLineList.reduce((obj, cur) => {
          if (cur.vendorId) {
            // 计算总价
            if (factorId.indexOf('formulaTotal-') === 0) {
              obj[`vendor-${cur.vendorId.toString()}`] = cur.standardNotaxPrice
            } else if (factorId.indexOf('formulaValueText-') === 0) {
              // 计算公式值
              obj[`vendor-${cur.vendorId.toString()}`] = formulaValue
            } else {
              // 计算报价项 总价需要判断空值
              if (cur.formulaResult && this.propertyCanToNumberFromString(cur.standardNotaxPrice)) {
                try {
                  const value = JSON.parse(cur.formulaResult)
                  obj[`vendor-${cur.vendorId.toString()}`] = value && typeof value === 'object' ? value[factorId] || '' : ''
                } catch (e) {
                  console.error(e)
                }
              } else {
                obj[`vendor-${cur.vendorId.toString()}`] = cur.standardNotaxPrice
              }
            }
          }
          return obj
        }, {})
      }

      // 模型报价
      if (this.pricingType.isModelPricing) {
        return reqOrderLineList.reduce((obj, cur) => {
          if (cur.vendorId) {
            // 计算总价 modelTotal-
            if (modelId.indexOf('modelTotal-') === 0) {
              obj[`vendor-${cur.vendorId.toString()}`] = cur.standardNotaxPrice
            } else {
              // 计算报价项
              if (cur.modelPriceLineList && this.propertyCanToNumberFromString(cur.standardNotaxPrice)) {
                const value = cur.modelPriceLineList.find(modelPriceLineItem => {
                  return modelPriceLineItem.modelPriceLineTemplateId.toString() === modelId
                })
                obj[`vendor-${cur.vendorId.toString()}`] = value && typeof value === 'object' ? value.taxTotalPrice || '' : ''
              } else {
                obj[`vendor-${cur.vendorId.toString()}`] = cur.standardNotaxPrice
              }
            }
          }
          return obj
        }, {})
      }
    },

    /* 根据报价类型合并单元格 */
    tableSpanMethod ({ row, columnIndex }) {
      // 默认合并列
      if ([0, 1, 2, 3].includes(columnIndex)) {
        return {
          rowspan: row.rowspan || 0,
          colspan: row.rowspan && row.rowspan > 0 ? 1 : 0
        }
      }

      // 简易询价
      if (this.businessType.isInquiry) {
        // 阶梯价 合并目标价
        if (columnIndex === 4) {
          return {
            rowspan: row.rowspan || 0,
            colspan: row.rowspan && row.rowspan > 0 ? 1 : 0
          }
        }
      }

      // 公式报价
      if (this.pricingType.isFormulaPricing) {
        if (row.factorId.indexOf('formulaValueText-') === 0) {
          // 简易询价多一列
          const index = this.businessType.isInquiry ? 6 : 5
          // 公式值 合并为一个单元格
          if (columnIndex === index) {
            // 开始合并行单元格
            return {
              rowspan: 1,
              // 表格列长度 - 公式报价表格除了供应商报价列长度 + 补位1
              colspan: this.priceComparisonTableColumns.length - index + 1
            }
          } else if (columnIndex > index) {
            // 被合并的单元格
            return {
              rowspan: 0,
              colspan: 0
            }
          }
        }
      }

      // 模型报价
      if (this.pricingType.isModelPricing) {
        // 费用大类列合并
        if (columnIndex === 4) {
          return {
            rowspan: row.descriptionRowspan,
            colspan: 1
          }
        }
      }
    },

    /* 判断返回单元格样式 */
    cellClassName ({ row, column }) {
      // 未报价，禁止报价的单元格值标红，直接判断值能不能转数字
      if (
        column.property.indexOf('vendor-') === 0 &&
        !this.propertyCanToNumberFromString(row[column.property])
      ) {
        if (row.factorId && row.factorId.indexOf('formulaValueText-') === 0) {
          // 公式值列排除
          return ''
        }
        return 'cell-warning'
      }
    },

    /* 判断数值是否可以正常转数字 */
    propertyCanToNumberFromString (value) {
      return value &&
        (typeof value === 'string' || typeof value === 'number') &&
        !isNaN(Number(value))
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@/styles/variables.scss";

.price-comparison-table {
  :deep(.cell-warning ){
    color: $danger-color;
  }
}
</style>
