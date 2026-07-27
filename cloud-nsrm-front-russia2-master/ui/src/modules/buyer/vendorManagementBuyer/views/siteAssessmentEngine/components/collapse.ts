import {
  expression,
  generateXindexInOrder,
  i18nExpression
} from "@meicloud/render-engine";

import {
  formGridSegment
} from 'lib@/components/render-engine/schema-segments'
import { siteAssessmentFormList } from './siteAssessmentFormList'
import { siteFormHistoryList } from './siteFormHistoryList'
import { authBaseInfo } from './authBaseInfo'
import { authOrganization } from './authOrganization'
import { siteFormPersonList } from './siteFormPersonList'
import { results } from './results'

export const collapseMain = {
  type: 'void',
  'x-component': 'Collapse',
    properties: generateXindexInOrder({
      // 现场评审单form
      siteAssessmentForm: {
        type: 'void',
        'x-component': 'CollapseItem',
        'x-component-props': {
          title: i18nExpression('route.siteAssessment2')
        },
        'x-query-engine-skip': true,
        properties: {
          siteAssessmentFormList:{
            type: 'void',
            ...formGridSegment,
            'x-reactions': expression(`() => {
              setTimeout(() => {
                const { row } = $attrs.params
                console.log($attrs,'datas')
                $self.form.setValues(row)
              })
            }`),
            properties: {
              // 现场评审表单
              ...siteAssessmentFormList
            }
          }
        }
      },
      // 历史现场评审
      siteFormHistoryList: {
        type: 'void',
        'x-component': 'CollapseItem',
        'x-component-props': {
          title: i18nExpression('vendorMod.historySiteAssessment')
        },
        'x-query-engine-skip': true,
        properties: {
          ...siteFormHistoryList
        }
      },
      // 认证基本信息
      authBaseInfo: {
        type: 'void',
        'x-component': 'CollapseItem',
        'x-component-props': {
          title: i18nExpression('vendorMod.authBaseInfo')
        },
        'x-query-engine-skip': true,
        properties: {
          ...authBaseInfo
        }
      },
      // 认证组织和品类
      authOrganization: {
        type: 'void',
        'x-component': 'CollapseItem',
        'x-component-props': {
          title: i18nExpression('vendorMod.authOrganization')
        },
        'x-query-engine-skip': true,
        properties: {
          ...authOrganization
        }
      },
      // 工作小组人员
      workingGroupStaff: {
        type: 'void',
        'x-component': 'CollapseItem',
        'x-component-props': {
          title: i18nExpression('vendorMod.workingGroupStaff')
        },
        'x-query-engine-skip': true,
        properties: {
          ...siteFormPersonList
        }
      },
      // 附件
      attachment: {
        type: 'void',
        'x-component': 'CollapseItem',
        'x-component-props': {
          title: i18nExpression('vendorMod.attachment')
        },
        'x-query-engine-skip': true,
        properties: {
          fileList: {
            type: 'array',
            'x-component': 'vendorAccessAttachment',
            'x-component-props': {
              'sence-code': 'QUA',
              'attOpt': expression(`$form.readPretty || $attrs.params.flag == 'appraisal' ? 'view' : 'add'`),
              'up-file-info': {
                uploadType: 'DEF',
                sourceType: 'WEB_APP',
                fileModular: 'sup',
                fileFunction: 'siteAssessment',
                fileType: 'images'
              }
            }
          }
        }
      },
      // 认证结果
      certificationResult: {
        type: 'void',
        'x-component': 'CollapseItem',
        'x-component-props': {
          title: i18nExpression('vendorMod.certificationResult')
        },
        'x-query-engine-skip': true,
        properties: {
          ...results
        }
      },
    })
}
