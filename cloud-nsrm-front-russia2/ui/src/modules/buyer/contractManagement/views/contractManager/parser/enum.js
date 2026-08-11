import { geti18n } from '@/main'
const i18n = geti18n()

const component = {
  INPUT: 'z-input',
  SELECT: 'z-select',
  TEXT: 'z-text',
  REFERENCE: 'z-reference',
  TIP: 'z-tip',
  DATE: 'z-date',
  TABLE: 'z-table',
  MATERIALTABLE: 'z-material-table',
  IMAGE: 'z-image',
  CHECKBOX: 'z-checkbox'
}

export const componentMap = {
  INPUT: i18n.t('contractMod.inputBox'), // 输入框
  SELECT: i18n.t('bidMod.selectBox'), // 下拉框
  TEXT: i18n.t('contractMod.fixedText'), // 固定文本
  // TIP: "提示文本",
  DATE: i18n.t('components.time'), // 时间
  CHECKBOX: i18n.t('components.checkbox'), // 复选框
  TABLE: i18n.t('contractMod.form'), // 表格
  MATERIALTABLE: i18n.t('contractMod.materialList'), // 物料清单
  IMAGE: i18n.t('contractMod.picture') // 图片
}

export const components = {
  INPUT: i18n.t('contractMod.inputBox'),
  SELECT: i18n.t('bidMod.selectBox'),
  TEXT: i18n.t('contractMod.fixedText'),
  TIP: i18n.t('contractMod.promptText'), // 提示文本
  REFERENCE: i18n.t('contractMod.referenceText'), // 引用文本
  DATE: i18n.t('components.time'),
  CHECKBOX: i18n.t('components.checkbox'), // 复选框
  TABLE: i18n.t('contractMod.form'),
  MATERIALTABLE: i18n.t('contractMod.materialList'),
  IMAGE: i18n.t('contractMod.picture')
}

export default component
