import { i18nExpression, expression } from '@meicloud/render-engine'
export * from './schema-segments/date'

/**
 * 利用 button-list 组件中的 visible 特性来做按钮权限的显隐
 */
export const buttonListItemVisibleByPermission = (code: string) => ({
  visible: expression(`$authorityVisible('${code}')`)
})

export const feedbackLayoutIsPopover = {
  'x-decorator': 'FormItem',
  'x-decorator-props': {
    feedbackLayout: 'popover'
  }
}

/**
 * 表格编辑行必填验证器
 */
export const editTableFormItemValid = {
  ...feedbackLayoutIsPopover,
  'x-validator': {
    required: true,
    message: i18nExpression('common.requiredField')
  }
}

/**
 * 必填验证器
 */
export const requiredValidatorSegment = {
  'x-validator': {
    required: true,
    message: i18nExpression('common.requiredField')
  }
}

/**
 * 必填验证器 - function
 * @param message 必填提示信息
 * @return {Record<any, any>}
 */
export const requiredFunctionValidatorSegment = (message: string): Record<any, any> => {
  return {
    'x-validator': {
      ...requiredValidatorSegment['x-validator'],
      message: message || i18nExpression('common.requiredField')
    }
  }
}

/**
 * 年月日日期选择器
 */
export const yearMonthDayStartSelectorSegment = {
  type: 'date',
  default: null,
  'x-component-props': {
    placeholder: i18nExpression('common.pleaseSelectDate'),
    format: 'yyyy-MM-dd',
    'value-format': 'yyyy-MM-dd',
    '@change': expression(`(date) => {
      if (date && date.indexOf('00:00:00') < 0) {
        $self.value = date + ' 00:00:00'
      }
    }`)
  }
}

export const yearMonthDayEndSelectorSegment = {
  type: 'date',
  default: null,
  'x-component-props': {
    placeholder: i18nExpression('common.pleaseSelectDate'),
    format: 'yyyy-MM-dd',
    'value-format': 'yyyy-MM-dd',
    '@change': expression(`(date) => {
      if (date && date.indexOf('23:59:59') < 0) {
        $self.value = date + ' 23:59:59'
      }
    }`)
  }
}

/**
 * 年月日日期选择器
 */
export const yearMonthDaySelectorSegment = {
  type: 'date',
  default: null,
  'x-component-props': {
    placeholder: i18nExpression('common.pleaseSelectDate'),
    format: 'yyyy-MM-dd',
    'value-format': 'yyyy-MM-dd'
  }
}

/**
 * 年月日时分秒日期选择器
 */
export const yearMonthDayHourMinuteSecondSelectorSegment = {
  type: 'date',
  default: null,
  'x-component-props': {
    placeholder: yearMonthDaySelectorSegment['x-component-props'].placeholder,
    type: 'datetime',
    format: 'yyyy-MM-dd HH:mm:ss',
    'value-format': 'yyyy-MM-dd HH:mm:ss'
  }
}

export const dataTimeSelectorSegment = {
  ...yearMonthDaySelectorSegment,
  'x-component-props': {
    ...yearMonthDaySelectorSegment['x-component-props'],
    type: 'datetimerange',
    format: 'yyyy-MM-dd HH:mm:ss',
    'default-time': ['00:00:00', '23:59:59']
  }
}

/**
 * 一行 4 栏的 FormGrid 配置
 */
export const formGridSegment = {
  'x-component': 'FormGrid',
  'x-component-props': {
    maxColumns: 4,
    columnGap: 32,
    rowGap: 0
  }
}

/**
 * 下拉框
 */
export const selectByYOrNAndNoFormItemSegment = {
  type: 'string',
  default: 'Y',
  enum: [
    {
      label: i18nExpression('common.yes'),
      value: 'Y'
    },
    {
      label: i18nExpression('common.no'),
      value: 'N'
    }
  ],
  'x-component': 'Select'
}

export const selectByYOrNSegment = {
  ...selectByYOrNAndNoFormItemSegment,
  'x-decorator': 'FormItem'
}

/**
 * 单选框
 */
export const radioGroupByYOrNSegment = {
  type: 'string',
  default: 'Y',
  enum: [
    {
      label: i18nExpression('common.yes'),
      value: 'Y'
    },
    {
      label: i18nExpression('common.no'),
      value: 'N'
    }
  ],
  'x-decorator': 'FormItem',
  'x-component': 'Radio.Group'
}

export const checkboxByYOrNSegment = {
  type: 'string',
  default: 'N',
  'x-decorator': 'FormItem',
  'x-component': 'Checkbox',
  'x-component-props': {
    trueLabel: 'Y',
    falseLabel: 'N'
  }
}

/**
 * 字段要使用但不需要显示字段
 */
export const hiddenField = {
  type: 'string',
  'x-hidden': true
}

/**
 * 按钮组件
 */
export const buttonSegment = {
  type: 'void',
  'x-component': 'Button'
}

/**
 * 输入框限制字段长度
 */

export const inputLimitSegment = (type: string, length: string) => {
  if (type == 'textarea') {
    return {
      type: 'textarea',
      rows: 2,
      maxlength: length || '500',
      'show-word-limit': true
    }
  } else {
    return {
      maxlength: length || '50',
      'show-word-limit': true
    }
  }
}
/**
 * MEIQL导出
 */
export const exportExcelSegment = {
  exportMode: 'front',
  exportType: 'meiqlApi',
  generateMeiQLExportRequest: `{{
    () =>{
      return $queryEngine.request.getQueryRequestInfo({
        action: 'paginationQuery',
        ...$form.query('query').take().invoke('getQueryParamsByQueryFrom')
      })
    }
  }}`
}
