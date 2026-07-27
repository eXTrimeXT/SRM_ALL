/**
 * @description 推荐供应商配置
 */
import { checkboxByYOrNSegment } from 'lib@/components/render-engine'
import { methodExpression,i18nExpression } from '@meicloud/render-engine'

const RecommendedVendorControlSegment: Record<any, any> = {
  recommendedSupplierControlForm: {
    type: 'void',
    'x-component': 'FormGrid',
    'x-component-props': {
      maxColumns: 3,
      columnGap: 32,
      rowGap: 0
    },
    properties: {
      // 排除黑名单供应商
      'inqSouProject.excludeBlackVendors': {
        ...checkboxByYOrNSegment,
        default: 'Y',
        'x-component-props': {
          ...checkboxByYOrNSegment['x-component-props'],
          label: i18nExpression('bidMod.excludeBlackVendors')  // '排除黑名单供应商'
        }
      },
      // 排除非本业务实体供应商
      'inqSouProject.excludeNoCurrentOrgVendors': {
        ...checkboxByYOrNSegment,
        'x-component-props': {
          ...checkboxByYOrNSegment['x-component-props'],
          label: i18nExpression('bidMod.excludeNoCurrentOrgVendors')  // '排除非本业务实体供应商'
        }
      },
      // 排除业务实体退出/冻结供应商
      'inqSouProject.excludeOrgQuitVendors': {
        ...checkboxByYOrNSegment,
        'x-component-props': {
          ...checkboxByYOrNSegment['x-component-props'],
          label: i18nExpression('bidMod.excludeOrgQuitVendors')  // '排除业务实体退出/冻结供应商'
        }
      },
      // 排除 XX 状态品类供应商
      categoryStatusCheckWrap: {
        type: 'void',
        'x-decorator': 'FormItem',
        'x-decorator-props': {
          gridSpan: 3
        },
        properties: {
          // 勾选框
          categoryStatusCheck: {
            ...checkboxByYOrNSegment,
            'x-query-engine-skip': true,
            'x-decorator': '',
            'x-disabled': true,
            'x-component-props': {
              ...checkboxByYOrNSegment['x-component-props'],
              label: i18nExpression('cusEntry.supplement20250211.exclude')  // '排除'
            },
            'x-reactions': {
              dependencies: ['excludeOrgCategoryStatus'],
              fulfill: {
                state: {
                  // 排除状态的勾选
                  value: methodExpression('!!($deps[0] && $deps[0].length > 0)')
                }
              }
            }
          },
          'inqSouProject.excludeOrgCategoryStatus': {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CATEGORY_STATUS',
              multiple: true,
              valueIsString: true,
              filterItem: ['QUALIFIED'],
              style: 'width: 250px'
            }
          },
          categoryStatusCheckLabel: {
            type: 'void',
            'x-component': 'span',
            'x-content': i18nExpression('cusEntry.supplement20250211.stateCategorySupplier')  // '状态品类供应商's
          } 
        }
      }
    }
  }
}

export default RecommendedVendorControlSegment
