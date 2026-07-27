/**
 * 报价模板配置共用变量和方法
 */
import i18n from '@/lang'

// 公式类型魔法值
export const FORMULA = 'FORMULA'

// 数字类型魔法值
export const DECIMAL = 'DECIMAL'

// 字典类型魔法值
export const DICT = 'DICT'

// 日期类型魔法值
export const DATE = 'DATE'

// 左括号字典
export const leftBracket = [{ id: 1, label: '(', value: '(' }]

// 右括号字典
export const rightBracket = [{ id: 1, label: ')', value: ')' }]

// 连接符
export const connector = [
  // 或者
  {
    id: 1,
    label: i18n.t('components.condition.or'),
    value: i18n.t('components.condition.or')
  },
  // 并且
  {
    id: 2,
    label: i18n.t('components.condition.and'),
    value: i18n.t('components.condition.and')
  }
]

// 字符串和枚举类型限制使用的比较符 只有等于和不等于
export const stringOrEnumComparator = [
  // 等于
  {
    id: 3,
    value: i18n.t('components.condition.eq'),
    label: i18n.t('components.condition.eq')
  },
  // 不等于
  {
    id: 4,
    value: i18n.t('components.condition.ne'),
    label: i18n.t('components.condition.ne')
  }
]

// 全部比较符字典
export const conditionComparator = [
  // 大于
  {
    id: 1,
    value: i18n.t('components.condition.gt'),
    label: i18n.t('components.condition.gt')
  },
  // 小于
  {
    id: 2,
    value: i18n.t('components.condition.lt'),
    label: i18n.t('components.condition.lt')
  },
  ...stringOrEnumComparator,
  // 大于等于
  {
    id: 5,
    value: i18n.t('components.condition.ge'),
    label: i18n.t('components.condition.ge')
  },
  // 小于等于
  {
    id: 6,
    value: i18n.t('components.condition.le'),
    label: i18n.t('components.condition.le')
  },
  // 未填写
  {
    id: 7,
    value: i18n.t('components.condition.null'),
    label: i18n.t('components.condition.null')
  },
  // 已填写
  {
    id: 8,
    value: i18n.t('components.condition.fill'),
    label: i18n.t('components.condition.fill')
  }
]

// 运算符
export const operator = [
  {
    id: 1,
    value: '+',
    label: '+'
  },
  {
    id: 2,
    value: '-',
    label: '-'
  },
  {
    id: 3,
    value: '*',
    label: '*'
  },
  {
    id: 4,
    value: '/',
    label: '/'
  }
  // {
  //   id: 5,
  //   value: '%',
  //   label: '%'
  // }
]
/**
 * @description 在属性设置公式定义中的应用条件中，根据当前行数据，判断属性值是否可编辑
 * @author donghf3
 * @param row
 * @returns {boolean}
 */
export const conditionAttributeValueDisabled = row => {
  // 比较符为已填写 未填写
  return !!([i18n.t('components.condition.null'), i18n.t('components.condition.fill')].includes(row.comparisonOperators))
}

/**
 * @description 判断字段类型是否是公式类型
 * @author donghf3
 * @param type 字段类型
 * @returns {boolean}
 */
export const fieldTypeIsFormula = type => !!(type && type === FORMULA)

/**
 * @description 判断字段类型是日期类型
 * @author donghf3
 * @param type 字段类型
 * @returns {boolean}
 */
export const fieldTypeIsDate = type => !!(type && type === DATE)

/**
 * @description 判断字段类型是枚举
 * @author donghf3
 * @param type 字段类型
 * @returns {boolean}
 */
export const fieldTypeIsEnum = type => !!(type && type.indexOf('ENUM_') === 0)

/**
 * @description 判断字段类型是字典类型
 * @author donghf3
 * @param type 字段类型
 * @returns {boolean}
 */
export const fieldTypeIsDict = type => !!(type && type === DICT)

/**
 * @description 判断字段类型是否是数字，包含数字和数字枚举
 * @author donghf3
 * @param type
 * @returns {boolean}
 */
export const fieldTypeIsNumber = type => !!(type && [DECIMAL, 'ENUM_DECIMAL'].includes(type))

/**
 * @description 判断应用条件可选那些操作符
 * @author donghf3
 * @param type
 * @returns {*[]|[{id: number, label: VueI18n.TranslateResult, value: VueI18n.TranslateResult}]}
 */
export const judgementOperator = type => {
  if (!type) {
    return []
  }
  // 字符串、枚举、字典类型只需要等于、不等于
  if (type === 'TEXT' || fieldTypeIsEnum(type) || fieldTypeIsDict(type)) {
    return stringOrEnumComparator
  }
  return conditionComparator
}

// baseTable动态表格列配置
export const baseTableColumnOptions = {
  // 序号列
  indexColumn: {
    attrs: {
      align: 'center',
      type: 'index',
      width: 70,
      label: i18n.t('common.sort')
    }
  },
  // 操作列
  operationsColumn: {
    attrs: {
      prop: 'operation',
      align: 'center',
      label: i18n.t('common.operation'),
      fixed: 'right',
      width: 80
    },
    operations: [
      {
        key: 'deleteRow',
        event: 'deleteRow',
        name: i18n.t('common.delete'),
        attrs: { type: 'text' }
      }
    ]
  }
}

/**
 * @description 字段类型 与 组件类型映射关系
 * [枚举]: 下拉框 select
 * [日期]: 日期选择 date
 * [数字]: 数字格式输入框 inputNumber
 * [字典]: 字典选择框 dictSelect
 * [其他]: 输入框 input
 * @author donghf3
 * @param type
 * @returns {string}
 */
export const fieldTypeToComponentKeyMap = type => {
  // 枚举
  if (fieldTypeIsEnum(type)) {
    return 'select'
  }
  // 日期
  if (fieldTypeIsDate(type)) {
    return 'date'
  }
  // 数字
  if (type === DECIMAL) {
    return 'inputNumber'
  }
  // 字典
  if (fieldTypeIsDict(type)) {
    return 'dictSelect'
  }
  return 'input'
}

// 字段值格式化配置，不允许输入比较符、连接符、运算符、左右符号  []'()或者并且
export const attributeValueFormat = {
  type: 'filterString',
  filterOptions: [
    '\\[', '\\]', '\\\'', ' ',
    // 左右括号
    ...leftBracket.map(item => item.value),
    ...rightBracket.map(item => item.value)
    // 连接符
    // ...connector.map(item => item.value),
    // 比较符
    // ...conditionComparator.map(item => item.value),
    // 运算符 需要转义
    // ...operator.map(item => `\\${item.value}`)
  ]
}
