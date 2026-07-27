import { nextTick } from 'vue'
import { expression, i18nExpression } from '@meicloud/render-engine'
import { defineInterceptFormat, RenderEngineScope, schemaMapReplacements } from 'lib@/components/render-engine/ide-mini-sdk'

// 资质审查
// @ts-ignore
import $QuaOfReview from 'modb@/vendorManagementBuyer/views/quaOfReview/quaOfReviewDetail'
// 现场评审
// @ts-ignore
import $SiteAssessment from 'modb@/vendorManagementBuyer/views/siteAssessmentEngine/siteAssessmentDetailEngine'
// 样品确认
// @ts-ignore
import $SampleConfirmed from 'modb@/vendorManagementBuyer/views/sampleConfirmedEngine/edit_engine.vue'

import $MaterialTrial from 'modb@/vendorManagementBuyer/views/materialTrialEngine/edit_engine.vue'

const updateSchemaWorkflow = (scope: RenderEngineScope) => {
  nextTick(() => {
    const schemaWorkflowComponentInstance = scope.$self.componentProps.componentInstance

    const tabDisabled = [null, undefined, 'DRAFT', 'WITHDRAW'].includes(
      scope.$values.approveStatus,
    )

    const buttonViewStatus = !scope.$readOnly && tabDisabled

    schemaWorkflowComponentInstance.buttonConfigInfo.save.view = buttonViewStatus
    schemaWorkflowComponentInstance.buttonConfigInfo.submit.view = buttonViewStatus
    schemaWorkflowComponentInstance.buttonConfigInfo.cancel.view = true
    schemaWorkflowComponentInstance.buttonConfigInfo.close.view = false
    schemaWorkflowComponentInstance.setWorkflowBusinessId(scope.$values.effectFormId)
    schemaWorkflowComponentInstance.setWorkflowTabDisabled(tabDisabled)
    schemaWorkflowComponentInstance.setWorkflowBusinessVariables({})
  })
}

export const accessProcessProcessing = defineInterceptFormat(options => {
  options.scope = {
    ...options.scope,
    $QuaOfReview,
    $SiteAssessment,
    $SampleConfirmed,
    $MaterialTrial
  }

  // 页面级直接劫持篡改即可，无需隔离
  if (options.scope?.$designPages?.FORM?.schema?.effectForm) {
    const properties = options.scope.$designPages.FORM.schema.effectForm.properties

    const formBottomFixedKey = Object.keys(properties).find(
      key => properties[key]['x-component'] === 'FormBottomFixed',
    )

    const formBottomFixed = !formBottomFixedKey ? {} : properties[formBottomFixedKey]
    if (formBottomFixedKey) {
      delete properties[formBottomFixedKey]
    }

    options.scope.$updateSchemaWorkflow = updateSchemaWorkflow

    options.scope.$designPages.FORM.schema.effectForm.properties = {
      SchemaWorkflow: {
        type: 'void',
        'x-component': 'SchemaWorkflow',
        'x-reactions': expression(`(field) => {
          // 用于 watch
          $values.effectFormId

          $updateSchemaWorkflow($root)
        }`),
        'x-component-props': {
          'business-id': expression('$values.effectFormId'),
          'business-type': 'EffectForm',
          '@click-handler': expression(`async(type) => {
            const query = {
              "*":{},
              "effectFormRelationForms": {'*': {}},
              "effectFormImportScenes": {'*': {}},
              "effectFormBankInfos": {'*': {}},
              "effectFormFinanceInfos": {'*': {}},
              "fileRecordIdes": {'*': {}},
              "effectFormReqHeads": {'*': {}},
              "effectFormOrgCategorys": {'*': {}}
            }

            if ([null, undefined, 'DRAFT'].includes($values.approveStatus)) {
              // 新增或者编辑的时候
              $values.approveStatus = 'DRAFT'
            }
            if (type != 'SAVE') {
              let validate = 0
              await $form.validate().then().catch(eq => {
                  validate = 1
                  console.log(validate, 'validate')
                })
                if (validate) {
                  return false
                }
            }

            if (type == 'SAVE') {
              // 暂存
              return $queryEngine.request.save($values, { query: query, loading: true })
                .then((res) => {
                  $message.success($t('common.successSave'))
                  console.log(res.data[0],'data')
                  $form.setValues(res.data[0])

                  const refreshListPageData = $$safeGetScope('$pageParams.refresh', $root);
                  if (refreshListPageData) {
                    refreshListPageData()
                  }
                }).catch( err => {
                  console.log(err)
                })
            }

            // 提交
            return $queryEngine.request.save($values, { query: query, tree: true, loading: true })
              .then((res) => {
                // 上面会自动 watch 变更的
                $values.effectFormId = res.data[0]?.effectFormId

                $self.componentProps.componentInstance.handlerAfter(type.toUpperCase(), () => {
                  $self.componentProps['@close-tab']()
                })
              }).catch( err => {
                console.log(err)
              })
          }`),
          '@submit-direct': expression(`(type) => {
            $self.componentProps['@click-handler'](type)
          }`),
          '@confirm': expression(`(type) => {
            $self.componentProps['@click-handler'](type)
          }`),
          '@close-tab': expression(`() => {
            const closeTabPane = $$safeGetScope('$closeTabPane', $root)
            if (closeTabPane) {
              return closeTabPane()
            }
          }`),
          '@update-integration-mode': expression(`() => {
            $updateSchemaWorkflow($root)
          }`),
        },
        items: {
          type: 'object',
          'x-query-engine-skip': true,
          properties: formBottomFixed?.properties,
        },
        properties,
      },
    }

    schemaMapReplacements(options.scope.$designPages.FORM.schema.effectForm, {
      // 'effectFormFinanceInfos.orgName': {
      //   'x-component': 'OrganizationSelector',
      //   'x-component-props': {
      //     readPretty: '{{$form.readPretty}}',
      //     'parent-id': -1,
      //     'node-type': 'OU',
      //     'select-type': 'input',
      //     placeholder: i18nExpression('common.pleaseSelect'),
      //     '@select': expression(`(node) => {
      //       $values.orgId = node ? node.organizationId : null
      //       $values.orgCode = node ? node.organizationCode : null
      //       $values.orgName = node ? node.organizationName : null
      //     }`),
      //   },
      // },
      // 'effectFormFinanceInfos.invName': {
      //   'x-component': 'OrganizationSelector',
      //   'x-component-props': {
      //     readPretty: '{{$form.readPretty}}',
      //     'parent-id': '{{$values.orgId}}',
      //     'node-type': 'INV',
      //     'select-type': 'input',
      //     placeholder: i18nExpression('common.pleaseSelect'),
      //   },
      // },
      // 'sup_effect_form_finance_info_ides.clearCurrency': {
      //   'x-component': 'DictSelect',
      //   'x-component-props': {
      //     readPretty: '{{$form.readPretty}}',
      //     code: "BID_TENDER_CURRENCY"
      //   }
      // }
    })
  }

  return options
})
