import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
import CCategorySelect from 'lib@/components/c-category-select'
import CInputMultiValue from 'lib@/components/c-input-multi-value'
// 导入 可配置模板下载接口 导入接口 额外条件参数  延迟等
import MImport from 'lib@/components/import'
// 自定义导出 额外条件参数  延迟等
import { ExportExcel } from './export-excel'
import { DictSelect, DictSelectPreview } from './dict-select'
import SrmCommonFile from 'lib@/components/srm-ui/packages/srm-common-file'
import DynamicCutoffTime from 'lib@/components/dynamic-cutoff-time'
import { RButton } from './button'
import { TableButton } from './table-button'
import { RFormWrapper } from './form-wrapper'

import { Steps } from './steps'
import { Collapse, CollapseItem } from './collapse'
import { QuickSearchWrapper } from './quick-search'

// 这里只是做下示例，真实场景可以随意组织
// 这里的组件也可以是异步组件，按需加载
export const srmComponents = {
  DictSelect,
  DictSelectPreview,
  OrganizationSelector,
  QuickSearch,
  QuickSearchWrapper,
  CCategorySelect,
  CInputMultiValue,
  RButton,
  TableButton,
  ImportExcel: MImport,
  ExportExcel,
  SrmCommonFile,
  DynamicCutoffTime,
  Steps,
  Collapse,
  CollapseItem,
  RFormWrapper,
}
