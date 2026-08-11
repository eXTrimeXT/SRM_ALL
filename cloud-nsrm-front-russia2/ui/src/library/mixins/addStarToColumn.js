import { MessageBox, Message } from '@meicloud/element-ui' // 'element-ui'
import { filter } from 'lodash'
import { messageConfig } from '@/utils/message'

/**
 * 校验表格必填，前提是在表格使用了_addStarToColumn重写表头添加红色星号，或自定义添加required-tag样式
 * 后续考虑支持自定义校验规则
 * @param tableRef {ref} el-table 表格实例
 * @param tableData {array} 表格数据
 * @param options 额外配置
 * @returns {boolean}
 */
export const validateRequiredColumn = (tableRef, tableData, options = {}) => {
  const {
    // 校验范围 为true是校验全部结果返回，false就是校验一条错误就返回
    validateScope = false,
    // 校验的表格标题
    tableTitle = this.$t('dataConfMod.table'),
    // 表格是否必填 默认必填
    tableRequired = true,
    // 排除字段，排除的字段即使标记必填，也不做校验
    excludeProperty = []
  } = options

  if (!tableRef) {
    return false
  }

  if (tableRequired && (!Array.isArray(tableData) || tableData.length === 0)) {
    Message({
      ...messageConfig,
      // 请新增${tableTitle}数据！
      message: this.$t('dataConfMod.tableTitleTips', { tableTitle }),
      type: 'warning'
    })
    return false
  }

  // 必填key
  const requiredProperty = []
  const children = tableRef.$el.children
  Object.keys(children).forEach(key => {
    // 找到表头
    if (children[key].className === 'el-table__header-wrapper') {
      // 找到表头下必填标签
      const requiredTagList = children[key].getElementsByClassName('required-tag')
      Object.keys(requiredTagList).forEach(key => {
        // 排除配置的排除字段
        if (!excludeProperty.includes(requiredTagList[key].dataset.property)) {
          requiredProperty.push({
            innerText: requiredTagList[key].innerText,
            property: requiredTagList[key].dataset.property
          })
        }
      })
    }
  })

  if (requiredProperty.length === 0) {
    return true
  }

  // 根据必填key校验数据
  let validateResult = {}
  for (let index = 0; index < tableData.length; index++) {
    // 返回校验不通过的key list
    const result = filter(requiredProperty, item => {
      const value = tableData[index][item.property]
      if (Array.isArray(value)) {
        // 数组
        return value.length === 0
      }
      // 0允许通过
      return !value && value !== 0
    })

    if (result.length > 0) {
      validateResult = {
        ...validateResult,
        // 以行号为key
        [index + 1]: result.map(item => ({ $index: index + 1, ...item }))
      }
      if (!validateScope) {
        // 只校验一行 终止
        break
      }
    }
  }

  const validateResultKeys = Object.keys(validateResult)
  if (validateResultKeys.length > 0) {
    if (!validateScope) {
      const firstKey = validateResultKeys[0]
      // 只校验一行
      Message({
        ...messageConfig,
        message: `${tableTitle} ${this.$t('bidMod.warningMessage', { index: firstKey, message: validateResult[firstKey][0].innerText })}${this.$t('vendorMod.required')}`,
        type: 'warning'
      })
    } else {
      // 校验全部
      let message = ''
      validateResultKeys.forEach(key => {
        message += validateResult[key].map(item => {
          return `<p>${this.$t('bidMod.warningMessage', { index: key, message: item.innerText })}${this.$t('vendorMod.required')}</p>`
        }).join('')
      })

      MessageBox.alert(message, `${tableTitle}${this.$t('templatePrice.verificationPrompt')}`, {
        dangerouslyUseHTMLString: true
      })
    }
    // 返回校验不通过
    return false
  }
  // 默认返回通过
  return true
}

export default {
  // 表格必填列的加入* 添加required-tag和data-property用于判断必填
  methods: {
    _addStarToColumn (h, { column }) {
      return h(
        'div',
        {
          class: 'required-tag',
          attrs: {
            'data-property': column.property
          }
        },
        [
          h(
            'span',
            {
              style: {
                color: 'red',
                fontSize: '12px'
              }
            },
            '*'
          ),
          h(
            'span',
            null,
            column.label
          )
        ]
      )
    }
  }
}
