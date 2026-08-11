import {
  expression,
  i18nExpression
} from "@meicloud/render-engine";
import {formGridSegment} from "lib@/components/render-engine";



export const results = {
  modelResultList: {
    type: 'array',
    'x-hidden': true
  },
  Dialog: {
    type: 'void',
    title: '132',
    'x-component': 'RDialog',
    'x-component-props': {
      'beforeClose': expression(`(done, type) => {
            if ( type === 'ok') {
              const state = $form.query('state').get('data')
              const fileRecordId = state.fileRecordId
              let reviewFormStandardDimList = []
              console.log($form.query('.printer').take())
              const tableDatas = $form.query('.printer').take().componentProps.tableData
              const editableTabs = $form.query('.printer').take().componentProps.editableTabs
              let modelResultList = $form.query('.modelResultList').take().value
              let tableData = JSON.stringify(tableDatas)
              editableTabs.forEach(datas1 => {
                let d1 = {
                  dimName: datas1.title,
                  dimType: datas1.type,
                  dimId: datas1.dimId,
                  siteReviewModelId: datas1.siteReviewModelId
                }
                let attr = []
                datas1.content.forEach(datas2 => {
                  let d2
                  if (datas1.type == 'TABLE') {
                    for (let i = 0;i<modelResultList.length;i++){
                        if (modelResultList[i].fieldCode == datas2.fieldCode && fileRecordId == modelResultList[i].fileRecordId){
                            modelResultList[i] = {
                              fieldName: datas2.fieldName,
                              fieldCode: datas2.fieldCode,
                              fieldValue: tableData,
                              dictCode: datas2.dictCode,
                              fieldType: datas2.fieldType,
                              assemblyType: datas2.assemblyType,
                              necessaryFlag: datas2.necessaryFlag,
                              dimFieldId: datas2.dimFieldId,
                              siteReviewModelId: datas2.siteReviewModelId,
                              dimId: datas2.dimId
                            }
                        }
                    }
                  } else {
                      for (let i = 0;i<modelResultList.length;i++){
                        if (modelResultList[i].fieldCode == datas2.fieldCode && fileRecordId == modelResultList[i].fileRecordId){
                            modelResultList[i] = {
                              fieldName: datas2.fieldName,
                              fieldValue: datas2.fieldContent,
                              fieldCode: datas2.fieldCode,
                              dictCode: datas2.dictCode,
                              fieldType: datas2.fieldType,
                              assemblyType: datas2.assemblyType,
                              necessaryFlag: datas2.necessaryFlag,
                              dimFieldId: datas2.dimFieldId,
                              siteReviewModelId: datas2.siteReviewModelId,
                              dimId: datas2.dimId
                            }
                        }
                    }
                  }
                })
              })
              $form.query('.modelResultList').take().value = modelResultList
              done()
            } else {
              done()
            }
      }`)
    },
    properties: {
      printer: {
        type: 'array',
        'x-component': 'Printer',
        'x-component-props': {
          editableTabs: expression(`$form.query('state').get('data').editableTabs`),
          'read-only':"false",
          tableData: expression(`$form.query('state').get('data').tableData`),
        },
        'x-query-engine-skip': true
      }
    }
  },
  certificationResultForm: {
      type: 'void',
      ...formGridSegment,
      properties: {
        reviewResult: {
          type: 'string',
          title: i18nExpression('vendorMod.finalResult'), // 最终结论
          'x-decorator': 'FormItem',
          'x-component': 'DictSelect',
          'x-decorator-props': { gridSpan: 1 },
          'x-component-props': {
            code: 'CEEA_RESULT_TYPE'
          },
          'x-reactions': expression(`() => {
            const state = $form.query('state').get('data')
            const approveStatus = $form.query('.approveStatus').get('value')
            const accountNameId = app.$store.getters.userId
            const createdId = $form.query('.createdId').take().value
            if ((state.bolType == 2 && accountNameId != createdId) || state.bolType == 4 || approveStatus == 'APPROVED'){
              $self.disabled = true
            } else {
              $self.disabled = false
            }
          }`)
        },
        ceeaResultExplain: {
          type: 'string',
          title: i18nExpression('vendorMod.resultExplain'), // 结论说明
          'x-decorator': 'FormItem',
          'x-decorator-props': { gridSpan: 2 },
          'x-component-props': {
            'type': 'textarea',
            'rows': '2'
          },
          'x-reactions': expression(`() => {
            const state = $form.query('state').get('data')
            const approveStatus = $form.query('.approveStatus').get('value')
            const accountNameId = app.$store.getters.userId
            const createdId = $form.query('.createdId').take().value
            if ((state.bolType == 2 && accountNameId != createdId) || state.bolType == 4 || approveStatus == 'APPROVED'){
              $self.disabled = true
            } else {
              $self.disabled = false
            }
          }`)
        }
      }
    }
}
