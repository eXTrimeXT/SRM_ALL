<template>
  <div class="template-compile-wrap">
    <el-collapse v-model="activeCollapseItem">
      <!--报价属性项-->
      <el-collapse-item
        v-for="item in attrTableList"
        :key="item.attrId"
        :title="item.attrName"
        :name="item.attrId"
        class="attr-table-collapse-item"
      >
        <!--为总价计算的属性，不能新增-->
        <div v-if="!item.isTotalBoolean && !readonly" class="attr-table-tools">
          <!--新增-->
          <el-button type="primary" @click="attrTableAddRow(item)">
            {{ $t('common.add') }}
          </el-button>
        </div>

        <BaseTable
          :ref="`attrTable-${item.attrId}`"
          stripe
          :data="templateData[item.attrId]"
          :columns="item.tableColumns"
          :empty-text="$t('components.noData')"
          border
          :show-summary="!item.isTotalBoolean"
          :summary-method="getSummaries"
          @deleteRow="value => deleteRow(value, item)"
        >
          <!--表头 公式提示-->
          <template #formulaValueTooltip="{ column, row }">
            <span>{{ column.label }}</span>
            <el-popover
              placement="top"
              width="430"
              trigger="hover"
            >
              <el-table :data="fieldTypeIsFormulaPropertyMap.get(column.property) || []" style="width: 100%">
                <!-- 应用条件 -->
                <el-table-column
                  prop="conditionNameShow"
                  :label="$t('quoteTemplate.condition.label')"
                  show-overflow-tooltip
                />
                <!-- 价格公式 -->
                <el-table-column
                  prop="formulaValue"
                  :label="$t('quoteTemplate.priceFormula')"
                  min-width="200"
                  show-overflow-tooltip
                />
              </el-table>
              <em slot="reference" class="el-icon-warning tip-icon" />
            </el-popover>
          </template>

          <!--根据字段类型，渲染不同的组件，配置类型于组件的对于关系-->
          <!--输入框-->
          <template #input="{ column, row }">
            <el-input v-model="row[column.property]" :disabled="readonly" />
          </template>
          <!--数字输入框-->
          <template #inputNumber="{ column, row }">
            <el-input
              v-model="row[column.property]"
              v-input-format="{ type: 'float' }"
              :disabled="readonly"
            />
          </template>
          <!--下拉框-->
          <template #select="{ column, row }">
            <el-select
              v-model="row[column.property]"
              :disabled="readonly"
              style="width: 100%"
            >
              <!--下拉取值来自item.cellOptions-->
              <el-option
                v-for="itemOption in item.cellOptions[column.property]"
                :key="itemOption.key"
                :label="itemOption.label"
                :value="itemOption.value"
              />
            </el-select>
          </template>
          <!--字典-->
          <template #dictSelect="{ column, row }">
            <DictSelect
              v-model="row[column.property]"
              :code="item.dictCodes[column.property] || ''"
              :disabled="readonly"
            />
          </template>
          <!--日期选择-->
          <template #date="{ column, row }">
            <el-date-picker
              v-model="row[column.property]"
              type="datetime"
              :format="$formatDatePickerTime"
              value-format="yyyy-MM-dd HH:mm:ss"
              :placeholder="$t('common.pleaseSelectDate')"
              :disabled="readonly"
              style="width: 100%"
            />
          </template>
        </BaseTable>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<script>
/**
 * 报价模板编译
 */
import {
  baseTableColumnOptions,
  fieldTypeIsDict,
  fieldTypeIsEnum,
  fieldTypeIsFormula,
  fieldTypeToComponentKeyMap
} from './utils'
import { validateRequiredColumn } from '@/library/mixins/addStarToColumn'
import { FLOAT_FORMAT_MAGIC } from '@/config/sysConfig'
import BaseTable from 'lib@/components/BaseTable'
import Big from 'big.js'

export default {
  name: 'TemplateCompile',

  components: { BaseTable },

  props: {
    readonly: {
      type: Boolean,
      default: false
    },
    //  模板数据 先变更这个
    detailData: {
      type: Object,
      default: () => {
        return {
          attrMap: null,
          tempVO: null
        }
      }
    },
    // 报价属性表格数据
    priceData: {
      type: Object,
      default: () => { /* nothing */ }
    }
  },

  data () {
    return {
      activeCollapseItem: [],
      attrTableList: [],
      templateData: {},
      // 全部的公式总价字段
      fieldTypeIsFormulaTotalPropertyList: [],
      // 公式类型的字段ID与公式值列表
      fieldTypeIsFormulaPropertyMap: new Map()
    }
  },

  computed: {
    // 平铺所有的字段列表 map
    fieldMaps () {
      const maps = new Map()
      // 拿到所有字段的平铺数组列表
      const attrList = Object.values(this.detailData.attrMap || {})
        .map(item => item.fieldList)
        .flat(1)

      attrList.forEach(item => {
        // 以fieldId为key，记录字段
        maps.set(item.fieldId.toString(), item)
      })
      return maps
    }
  },

  watch: {
    detailData: {
      handler (val) {
        if (val && val.attrMap && val.tempVO) {
          this.arrangeTableColumns()
        }
      },
      immediate: true,
      deep: true
    },
    priceData: {
      handler (val) {
        console.log(val)
        if (val) {
          this.arrangeTableData()
        }
      },
      immediate: true,
      deep: true
    }
  },

  methods: {
    /* 报价属性表格 新增行 */
    attrTableAddRow (attrItem) {
      // 遍历当前表格行的配置，找到所有的prop
      this.templateData[attrItem.attrId].push({
        ...attrItem.tableColumns.reduce((obj, cur) => {
          if (cur.attrs.prop && cur.attrs.prop !== 'operation') {
            // 根据fieldId找到默认值
            const fieldItem = this.fieldMaps.get(cur.attrs.prop)
            obj[cur.attrs.prop] = (fieldItem || {}).defaultValue || ''
          }
          return obj
        }, {})
      })
    },

    /* 报价属性表格 删除行 */
    deleteRow ({ $index }, attrItem) {
      this.templateData[attrItem.attrId].splice($index, 1)
    },

    /* 编排表格头数据 */
    arrangeTableColumns () {
      const { attrMap, tempVO } = this.detailData
      if (!attrMap || !tempVO || !tempVO.tempLineList.length) {
        return
      }

      let resultList = []
      tempVO.tempLineList.forEach(item => {
        const itemColumns = {
          attrId: item.attrId,
          attrNo: item.attrNo,
          attrName: item.attrName,
          isTotalBoolean: item.isTotal === 'Y',
          required: item.required,
          tableColumns: [
            { ...baseTableColumnOptions.indexColumn }
          ],
          // 配置[下拉框]选择可用数据 以property为key
          cellOptions: {},
          // 配置[字典]选择的字典code 以property为key
          dictCodes: {}
        }

        // 从attrMap根据id找到属性详细数据
        const fieldList = (attrMap[item.attrId] || {}).fieldList || []
        fieldList.forEach(itemField => {
          // 根据字段列表，转化为表格列配置
          itemColumns.tableColumns.push({ ...this.fieldItemToColumnItem(itemField) })

          // 枚举类型，写入下拉选择列
          if (fieldTypeIsEnum(itemField.fieldType)) {
            itemColumns.cellOptions[itemField.fieldId.toString()] = this.enumFieldValueToOptions(itemField.fieldValue)
          }

          // 字典类型，写入字典code
          if (fieldTypeIsDict(itemField.fieldType)) {
            itemColumns.dictCodes[itemField.fieldId.toString()] = itemField.fieldValue
          }
        })

        // 操作列
        if (!itemColumns.isTotalBoolean && !this.readonly) {
          // 为总价计算的属性，不能操作
          itemColumns.tableColumns.push({ ...baseTableColumnOptions.operationsColumn })
        }

        resultList.push(itemColumns)
      })

      this.attrTableList = resultList

      this.afterArrangeTableColumns(resultList)
    },

    /* 表格头数据编排完成后处理 */
    afterArrangeTableColumns (resultList) {
      const attrList = Object.values(this.detailData.attrMap)

      // 默认打开全部折叠面板
      this.activeCollapseItem = (resultList || []).map(item => item.attrId).concat()

      // 记录总价公式字段类型的字段
      this.fieldTypeIsFormulaTotalPropertyList = attrList
        .map(item => {
          // 只拿总价公式类型 id转字符串方便判断
          return item.fieldList
            .filter(itemField => itemField.isTotal === 'Y')
            .map(itemField => itemField.fieldId.toString())
        })
        .flat()

      // 以字段ID为key，记录所有公式列表Map
      attrList.forEach(item => {
        item.formulaList.forEach(itemField => {
          this.fieldTypeIsFormulaPropertyMap.set(itemField.fieldId.toString(), [
            ...(this.fieldTypeIsFormulaPropertyMap.get(itemField.fieldId.toString()) || []),
            {
              conditionNameShow: itemField.conditionNameShow,
              formulaValue: itemField.formulaValue
            }
          ])
        })
      })

      // 模板数据，用对象存储数据，key是报价属性ID
      this.templateData = (resultList || []).reduce((obj, cur) => {
        obj[cur.attrId] = []
        return obj
      }, {})
      // 编排总价类型的表格，默认添加一行
      const totalAttrTableItem = this.attrTableList.find(item => item.isTotalBoolean)
      if (totalAttrTableItem) {
        // 新增一行
        this.attrTableAddRow(totalAttrTableItem)
      }
    },

    /* 根据字段数据信息，转化为表格列配置 */
    fieldItemToColumnItem (fieldData) {
      let obj = {
        attrs: {
          // 以字段id作为key
          prop: fieldData.fieldId.toString(),
          align: 'center',
          minWidth: '110',
          label: fieldData.fieldDesc
        }
      }
      // 必填，添加红色*
      if (fieldData.required === 'Y') {
        obj.attrs.renderHeader = this._addStarToColumn
      }
      // 禁止编辑的字段类型，无法输入，只能显示纯文本
      if (fieldData.forbidEdit === 'N') {
        // 组件类型为插槽名称
        obj.slot = fieldTypeToComponentKeyMap(fieldData.fieldType)
      }
      // 公式类型，存在公式，表头使用提示插槽
      if (fieldTypeIsFormula(fieldData.fieldType) && fieldData.formulaValue !== '') {
        obj.headerSlot = 'formulaValueTooltip'
      }

      return obj
    },

    /* 枚举类型字段值转化为下拉框列表 */
    enumFieldValueToOptions (value = '') {
      return value.trim().split(',').map((item, index) => {
        return {
          id: index + 1,
          key: `enum-${index}`,
          label: item,
          value: item
        }
      })
    },

    /* 编排表格数据 */
    arrangeTableData () {
      // 判断总价行是否有值，有值才允许覆盖，这里暂时设定必须先传detailData
      if (!this.priceData || !this.detailData) {
        return
      }

      const { data = {} } = JSON.parse(JSON.stringify(this.priceData))

      const { tempVO = {} } = this.detailData
      // 拿到总价行
      const isTotalItem = tempVO.tempLineList.find(item => item.isTotal === 'Y')

      // 不存在总价行 or 只读
      if (!isTotalItem || this.readonly || (isTotalItem && data[isTotalItem.attrId].length > 0)) {
        // 直接覆盖
        this.templateData = data
      } else {
        // 不能覆盖
        this.templateData = {
          ...data,
          // 使用当前数据
          [isTotalItem.attrId]: this.templateData[isTotalItem.attrId]
        }
      }
    },

    /* 合计行计算方法 */
    getSummaries (param) {
      const { columns, data } = param
      const sums = []

      columns.forEach((column, index) => {
        // 判断当前的property是否是公式总价类型 才去计算合计
        if (this.fieldTypeIsFormulaTotalPropertyList.includes(column.property)) {
          sums[index] = `${this.$t('other.key13')}：`
          const values = data.map(item => Number(item[column.property]))
          if (!values.every(value => isNaN(value))) {
            sums[index] += values.reduce((prev, curr) => {
              const value = Number(curr)
              if (!isNaN(value)) {
                return Big(prev).plus(Big(curr)).round(FLOAT_FORMAT_MAGIC.DIGITS).toString()
              } else {
                return Big(prev).round(FLOAT_FORMAT_MAGIC.DIGITS).toString()
              }
            }, 0)
          } else {
            sums[index] += 'N/A'
          }
        }
      })

      return sums
    },

    /* 校验并返回数据 */
    validateAndResolveData () {
      return new Promise(resolve => {
        // 校验表格
        for (let item of this.attrTableList) {
          // 非必填表格存在行数据才去校验
          if (
            item.required ||
            (!item.required && this.templateData[item.attrId].length > 0)
          ) {
            const elTable = this.$refs[`attrTable-${item.attrId}`][0].$children[0]
            if (!elTable) {
              return
            }

            if (
              !validateRequiredColumn(
                elTable,
                this.templateData[item.attrId],
                {
                  validateScope: false,
                  tableTitle: item.attrName
                }
              )
            ) {
              resolve({ status: false })
              return
            }
          }
        }

        resolve({ status: true, data: this.templateData })
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.template-compile-wrap {
  .attr-table-tools {
    margin-bottom: 10px;
  }
}

// 表格列头
.table-column-tooltip {
  color: #96999C;
  margin-left: 4px;
}

.tip-icon {
  vertical-align: inherit;
  font-size: 12px;
  color: #96999C;
  margin-left: 5px;
}

.attr-table-collapse-item {
  margin-bottom: 15px;
}
</style>
