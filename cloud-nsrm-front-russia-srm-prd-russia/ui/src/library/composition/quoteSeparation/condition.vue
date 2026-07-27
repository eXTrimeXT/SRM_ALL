<template>
  <div class="condition-wrap">
    <div v-if="!readonly" class="table-tool">
      <el-button type="primary" @click="addRow">
        {{ $t('common.add') }}
      </el-button>
    </div>

    <el-table
      ref="conditionTable"
      :data="conditionData"
      style="margin-top: 10px;"
      border
    >
      <!--左括号-->
      <el-table-column
        align="center"
        prop="leftBracket"
        min-width="90"
        :label="$t('quoteTemplate.condition.leftBracket')"
      >
        <template v-slot="scope">
          <DictSelect
            v-model="scope.row.leftBracket"
            code="leftBracket"
            clearable
            :dict-class="dictClass"
            :disabled="readonly"
          />
        </template>
      </el-table-column>

      <!--字段名称-->
      <el-table-column
        align="center"
        prop="attributeName"
        min-width="130"
        :label="$t('quoteTemplate.fieldName')"
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <el-select
            v-model="scope.row.attributeName"
            :disabled="readonly"
            @change="value => attributeNameChange(value, scope)"
          >
            <el-option
              v-for="(item, index) in attributeList"
              :key="`fieldName-${index}`"
              :value="item.fieldName"
              :label="item.fieldName"
            />
          </el-select>
        </template>
      </el-table-column>

      <!--比较符-->
      <el-table-column
        v-if="symbolType === 'COMPARATOR'"
        align="center"
        prop="comparisonOperators"
        min-width="100"
        :label="$t('quoteTemplate.condition.comparisonOperators')"
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <el-select
            v-model="scope.row.comparisonOperators"
            clearable
            :disabled="readonly"
          >
            <el-option
              v-for="item in judgementOperator(scope.row.fieldType)"
              :key="item.id"
              :value="item.value"
              :label="item.label"
            />
          </el-select>
        </template>
      </el-table-column>

      <!--运算符-->
      <el-table-column
        v-if="symbolType === 'OPERATOR'"
        align="center"
        prop="comparisonOperators"
        min-width="100"
        :label="$t('quoteTemplate.condition.operator')"
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <DictSelect
            v-model="scope.row.comparisonOperators"
            code="operator"
            clearable
            :dict-class="dictClass"
            :disabled="readonly"
          />
        </template>
      </el-table-column>

      <!--字段值-->
      <el-table-column
        align="center"
        prop="attributeValue"
        min-width="130"
        :label="$t('quoteTemplate.condition.attributeValue')"
      >
        <template #header="{ column, $index }">
          <div data-property="attributeValue" class="required-tag">
            <span style="color: red; font-size: 12px;">*</span>
            <span>{{ column.label }}</span>
            <el-tooltip
              effect="dark"
              :content="$t('quoteTemplate.condition.attributeValueTooltip')"
              placement="top"
              class="table-column-tooltip"
            >
              <em class="el-icon-warning tip-icon" />
            </el-tooltip>
          </div>
        </template>

        <template v-slot="scope">
          <!--日期类型，选择日期-->
          <el-date-picker
            v-if="fieldTypeToComponentKeyMap(scope.row.fieldType) === 'date'"
            v-model="scope.row.attributeValue"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            :placeholder="$t('common.pleaseSelectDate')"
            :disabled="readonly || conditionAttributeValueDisabled(scope.row)"
            style="width: 100%"
          />

          <!--枚举类型，下拉选择-->
          <el-select
            v-else-if="fieldTypeToComponentKeyMap(scope.row.fieldType) === 'select'"
            v-model="scope.row.attributeValue"
            :disabled="readonly || conditionAttributeValueDisabled(scope.row)"
            style="width: 100%"
          >
            <el-option
              v-for="itemOption in availableEnumMap.get(scope.row.attributeName) || ''"
              :key="itemOption.key"
              :label="itemOption.label"
              :value="itemOption.value"
            />
          </el-select>

          <!--字典类型，字典选择-->
          <DictSelect
            v-else-if="fieldTypeToComponentKeyMap(scope.row.fieldType) === 'dictSelect'"
            v-model="scope.row.attributeValue"
            :code="availableDictMap.get(scope.row.attributeName) || ''"
            :disabled="readonly"
          />

          <!--剩下就是输入框 涉及到自定义指令绑定，需要用v-show指令，不能用v-if [数字，普通]-->
          <template v-else>
            <!--数字类型 限制输入数字-->
            <el-input
              v-show="fieldTypeToComponentKeyMap(scope.row.fieldType) === 'inputNumber'"
              v-model="scope.row.attributeValue"
              v-input-format="{ type: 'float' }"
              :disabled="readonly || conditionAttributeValueDisabled(scope.row)"
            />

            <!--文本输入框 非数字类型 数字类型 过滤限制字符-->
            <el-input
              v-show="fieldTypeToComponentKeyMap(scope.row.fieldType) === 'input'"
              v-model="scope.row.attributeValue"
              v-input-format="attributeValueFormat"
              :disabled="readonly || conditionAttributeValueDisabled(scope.row)"
            />
          </template>
        </template>
      </el-table-column>

      <!--右括号-->
      <el-table-column
        align="center"
        prop="rightBracket"
        min-width="90"
        :label="$t('quoteTemplate.condition.rightBracket')"
      >
        <template v-slot="scope">
          <DictSelect
            v-model="scope.row.rightBracket"
            code="rightBracket"
            clearable
            :dict-class="dictClass"
            :disabled="readonly"
          />
        </template>
      </el-table-column>

      <!--连接符-->
      <el-table-column
        align="center"
        prop="connector"
        min-width="100"
        :label="$t('quoteTemplate.condition.connector')"
      >
        <template v-slot="scope">
          <DictSelect
            v-model="scope.row.connector"
            code="connector"
            clearable
            :dict-class="dictClass"
            :disabled="readonly"
          />
        </template>
      </el-table-column>

      <!--操作-->
      <el-table-column
        v-if="!readonly"
        align="center"
        :label="$t('common.operation')"
        min-width="80"
      >
        <template v-slot="{ row, $index }">
          <!--删除-->
          <el-button type="text" @click="deleteRow($index)">
            {{ $t('common.delete') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!--条件描述-->
    <p class="condition-desc">
      {{ conditionTitle + $t('quoteTemplate.condition.desc') }}
    </p>
    <el-input
      v-model="conditionDesc.showText"
      type="textarea"
      disabled
    />
  </div>
</template>

<script>
/**
 * 条件编辑
 */
import {
  connector,
  leftBracket,
  rightBracket,
  conditionComparator,
  operator,
  attributeValueFormat,
  conditionAttributeValueDisabled,
  fieldTypeToComponentKeyMap,
  judgementOperator,
  fieldTypeIsEnum,
  fieldTypeIsNumber,
  fieldTypeIsDict
} from './utils'
import { createDictClass } from '@/library/utils/dict/dict-utils'
import { validateRequiredColumn } from '@/library/mixins/addStarToColumn'

export default {
  name: 'Condition',

  props: {
    readonly: {
      type: Boolean,
      default: false
    },
    // 可选的属性字段列表
    attributeList: {
      type: Array,
      default: () => []
    },
    // 详情数据
    detailData: {
      type: Object,
      default: () => {
        return {
          // 描述文本
          conditionName: null,
          // JSON化的表格数据
          conditionNameJson: null
        }
      }
    },
    // 标题
    conditionTitle: {
      type: String,
      default () {
        return this.$t('bidMod.common.condition')
      }
    },
    // 当前数据是否必填
    tableRequired: {
      type: Boolean,
      default: false
    },
    // 符号类型，用于识别是用运算符还是比较符，默认比较符
    symbolType: {
      type: String,
      default: 'COMPARATOR'
    }

  },

  data () {
    return {
      dictClass: createDictClass({
        leftBracket,
        rightBracket,
        connector,
        operator
      }, false),
      conditionData: [],
      judgementOperator,
      fieldTypeToComponentKeyMap,
      conditionAttributeValueDisabled,
      attributeValueFormat
    }
  },

  computed: {
    // 可选的枚举类型Map select option
    availableEnumMap () {
      const map = new Map()
      this.attributeList
        // 过滤枚举类型
        .filter(item => fieldTypeIsEnum(item.fieldType) && item.fieldName)
        .forEach(item => {
          // 以字段名为key
          map.set(
            item.fieldName,
            item.fieldValue
              .split(',')
              .map((itemValue, indexValue) => {
                return {
                  key: `enum-${indexValue}`,
                  label: itemValue,
                  value: itemValue
                }
              }))
        })
      return map
    },

    // 可选的字典类型Map
    availableDictMap () {
      const map = new Map()
      this.attributeList
        // 过滤字典类型
        .filter(item => fieldTypeIsDict(item.fieldType) && item.fieldName)
        // 以字段名为key
        .forEach(item => map.set(item.fieldName, item.fieldValue))
      return map
    },

    // 把应用条件列表转化为文本，分为展示文本和运算文本
    conditionDesc () {
      let [valueText, showText] = ['', '']
      this.conditionData.forEach(item => {
        let attrValue = item.attributeValue || item.attributeValue === 0 ? String(item.attributeValue).trim() : ''
        // 用于展示的值，一般用于字典值得格式化等
        let attrValueShow = attrValue

        // 判断处理属性值，文本加单引号，数字不加
        if (attrValue.length > 0) {
          // 处理需要格式化展示的字段
          if (fieldTypeIsDict(item.fieldType)) {
            // 字典类型
            attrValueShow = this.$getDictLabel(this.availableDictMap.get(item.attributeName), attrValueShow)
          }

          // 根据类型判断，数字类型和数字枚举类型格式化不加双引号
          if (!fieldTypeIsNumber(item.fieldType)) {
            attrValue = '\'' + attrValue + '\''
            attrValueShow = '\'' + attrValueShow + '\''
          }
        }

        // isShow 标识是否用于展示
        const getText = (isShow = false) => {
          return `${item.leftBracket || ' '} ` +
            `${item.attributeName ? '[' + item.attributeName + ']' : ' '} ` +
            `${item.comparisonOperators || ' '} ` +
            `${isShow ? attrValueShow : (attrValue || ' ')} ` +
            `${item.rightBracket || ' '} ` +
            `${item.connector || ' '}`
        }

        showText += getText(true) + ' '
        valueText += getText() + ' '
      })

      return {
        showText,
        valueText
      }
    }
  },

  watch: {
    detailData: {
      handler (val) {
        if (!val || (!val.conditionName && !val.conditionNameJson)) {
          this.conditionData = []
          return
        }
        if (val.conditionNameJson) {
          // JSON化的表格数据
          this.arrangeConditionDataFromJson()
        }
        if (val.conditionName && !val.conditionNameJson) {
          // 根据描述转化，如果存在JSON数据就不用
          this.arrangeConditionData()
        }
      },
      immediate: true,
      deep: true
    }
  },

  methods: {
    /* 解析文本的应用公式为应用表格列表数据 */
    arrangeConditionData () {
      // 解析应用公式为表格数据
      const conditionName = this.detailData.conditionName
      if (!conditionName || conditionName.length === 0) {
        this.conditionData = []
        return
      }

      // 以连接符分割成数组，数组中偶数是行值，行值后是连接符
      const conditionNameArr = conditionName.split(/(或者|并且)/g)
      const listData = []
      conditionNameArr.forEach((item, index) => {
        if (index % 2 !== 0 || item.trim().length <= 0) {
          // 判断非偶数下标，或者不为空 就不用执行
          return
        }

        // 存储行对象
        const itemObj = {}
        // 以空格分隔字符串成数组
        const spaceArr = item.trim().split(/\s/g)
        spaceArr.forEach((j, _i) => {
          // 判断中括号拿到属性名称
          if (/\[(.*?)\]/g.exec(j)) {
            // 移除中括号
            itemObj.attributeName = j.trim().slice(1, j.trim().length - 1)
          }

          // 根据运算符和比较符作不同处理
          const comp = (this.symbolType === 'COMPARATOR' ? conditionComparator : operator).map(i => i.label).includes(j)
          if (comp) {
            itemObj.attributeValue = spaceArr[_i + 1] || null
            if (/'(.*?)'/g.exec(itemObj.attributeValue)) {
              itemObj.attributeValue = itemObj.attributeValue.substring(1, itemObj.attributeValue.length - 1)
            }
            itemObj.comparisonOperators = j
          }
        })

        itemObj.leftBracket = item.indexOf('(') > -1 ? '(' : ''
        itemObj.rightBracket = item.indexOf(')') > -1 ? ')' : ''
        itemObj.connector = conditionNameArr[index + 1] || ''
        // 找到字段类型冗余
        const attributeObj = this.attributeList.find(itemI => itemI.fieldName === itemObj.attributeName)
        if (attributeObj) {
          itemObj.fieldType = attributeObj.fieldType
        }
        listData.push(itemObj)
      })
      this.conditionData = listData
    },

    /* 解析JSON的应用公式应用表格列表数据 */
    arrangeConditionDataFromJson () {
      const jsonText = this.detailData.conditionNameJson
      if (!jsonText || typeof jsonText !== 'string' || jsonText.length === 0) {
        this.conditionData = []
        return
      }

      try {
        this.conditionData = JSON.parse(jsonText)
      } catch (e) {
        this.conditionData = []
      }
    },

    /* 新增行 */
    addRow () {
      this.conditionData.push({
        leftBracket: '',
        attributeName: '',
        comparisonOperators: '',
        attributeValue: '',
        rightBracket: '',
        connector: ''
      })
    },

    /* 删除行 */
    deleteRow ($index) {
      this.conditionData.splice($index, 1)
    },

    /* 字段名称变更 */
    attributeNameChange (value, scope) {
      // 冗余类型
      const attributeObj = this.attributeList.find(item => item.fieldName === value)
      if (attributeObj) {
        if (scope.row.fieldType !== attributeObj.fieldType) {
          // 字段类型改变了，清空比较符和属性值
          scope.row.comparisonOperators = ''
          scope.row.attributeValue = ''
        }
        scope.row.fieldType = attributeObj.fieldType
      }
    },

    /* 校验数据 */
    validateData () {
      return new Promise(resolve => {
        const tableLength = this.conditionData.length
        // 表格默认可以为空
        // 字段值、比较符 必填
        if (
          !validateRequiredColumn(
            this.$refs.conditionTable,
            this.conditionData,
            {
              validateScope: true,
              tableRequired: this.tableRequired,
              tableTitle: this.conditionTitle,
              excludeProperty: ['attributeValue']
            }
          )
        ) {
          resolve({ status: false })
          return
        }

        // 额外校验字段值必填
        for (let [index, item] of this.conditionData.entries()) {
          if (!conditionAttributeValueDisabled(item)) {
            // 比较符不为为已填写未填写 允许为0
            if (!item.attributeValue && item.attributeValue !== 0) {
              // 第${index + 1}行 请维护字段值！
              this.$message.warning(this.$t('bidMod.common.quoteTempMsg1', { index: index + 1 }))
              return
            }
          }
        }

        // 超过两行时，除了最后一行其他行都要有连接符
        if (tableLength >= 2) {
          const errorIndex = this.conditionData.findIndex((item, index) => !item.connector && index !== tableLength - 1)
          if (errorIndex >= 0) {
            // ${this.conditionTitle}表格 第${errorIndex + 1}行请选择连接符
            this.$message.warning(this.$t('bidMod.common.quoteTempMsg2', { title: this.conditionTitle, index: errorIndex + 1 }))
            resolve({ status: false })
            return
          }
        }
        // 4. 最后一行不能有连接符
        if (tableLength && this.conditionData.find((item, index) => item.connector && index === tableLength - 1)) {
          // ${this.conditionTitle}表格最后一行禁止选择连接符
          this.$message.warning(this.$t('bidMod.common.quoteTempMsg3', { title: this.conditionTitle }))
          resolve({ status: false })
          return
        }

        resolve({
          status: true,
          data: {
            conditionName: this.conditionDesc.valueText.trim(),
            conditionNameShow: this.conditionDesc.showText.trim(),
            conditionNameJson: JSON.stringify(this.conditionData)
          }
        })
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.tip-icon {
  vertical-align: inherit;
  font-size: 12px;
  color: #96999C;
  margin-left: 5px;
}

.condition-desc {
  font-size: 12px;
}
</style>
