import {
  expression,
  generateXindexInOrder,
  i18nExpression, methodExpression,
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
  fileRecords: {
    type: 'array',
    'x-component': 'RenderTable',
    'x-component-props': {
      preColumns: 'seq',
      editMode: true,
      maxHeight: 400,
      pagination: false,
      sortable: false
    },
    'x-query-engine-skip': true,
    properties: generateXindexInOrder({
      reviewModelId: {
        type: 'string',
        'x-hidden': true,
        'x-render-table-column': {

        }
      },
      reviewModelName: {
        type: 'string',
        'x-component': 'TableButton',
        'x-component-props': {
          type: 'text',
          '@click': expression(`({row}) => {
              const id = $form.query('.reviewModelId').take().value
              siteReviewModel.getDetail(id).then(res => {
                  $form.query('.Dialog').take().title = res.data.reviewModelName
                  const modelResultList = $form.query('.modelResultList').take().value
                  const state = $form.query('state').get('data')
                  console.log(modelResultList)

                  let editableTabs = []
                  res.data.dimList.forEach((datas, indexs) => {
                    const index = String(indexs + '1')
                    console.log(index,'index')
                    let d1 = {
                      dimId: datas.dimId,
                      siteReviewModelId: datas.siteReviewModelId,
                      title: datas.dimName,
                      name: index,
                      type: datas.dimType,
                      content: []
                    }
                    let attr = []
                    if (datas.dimType == 'TABLE') {
                      let tableIndex = 0
                      datas.fieldList.forEach(datas2 => {
                        let d2 = {
                          fieldCode: datas2.fieldCode,
                          fieldName: datas2.fieldName,
                          dictCode: datas2.dictCode,
                          fieldType: datas2.fieldType,
                          assemblyType: datas2.assemblyType,
                          necessaryFlag: datas2.necessaryFlag,
                          dimFieldId: datas2.dimFieldId,
                          siteReviewModelId: datas2.siteReviewModelId,
                          dimId: datas2.dimId
                        }
                        attr.push(d2)
                        for (let i = 0;i < modelResultList.length; i++) {
                          if (modelResultList[i].fieldCode == datas2.fieldCode && modelResultList[i].fileRecordId == row.fileRecordId) {
                            tableIndex = i
                          }
                        }
                      })
                      try {
                        state.tableData = JSON.parse(modelResultList[tableIndex].fieldValue)
                      } catch (error) {
                        console.log(error)
                      }
                    } else {
                      datas.fieldList.forEach(datas2 => {
                        for (let i = 0;i < modelResultList.length; i++) {
                          if (modelResultList[i].fieldCode == datas2.fieldCode && modelResultList[i].fileRecordId == row.fileRecordId) {
                            let d2 = {
                              fieldCode: datas2.fieldCode,
                              fieldName: datas2.fieldName,
                              dictCode: datas2.dictCode,
                              fieldContent: modelResultList[i].fieldValue,
                              fieldType: datas2.fieldType,
                              assemblyType: datas2.assemblyType,
                              necessaryFlag: datas2.necessaryFlag
                            }
                            attr.push(d2)
                          }
                        }
                      })
                    }
                    d1.content = attr
                    editableTabs.push(d1)
                  })
                  state.editableTabs = editableTabs
                  state.fileRecordId = row.fileRecordId
                  $form.query('.Dialog').take().setComponentProps({ visible: true })
              })
          }`)
        },
        'x-reactions': expression(`() => {
            setTimeout(() => {
                const modelList = $form.query('state').get('data').modelList
                const id = $form.query('.reviewModelId').take().value
                let obj = ''
                modelList.forEach(datas => {
                  if (datas.value == id) {
                    obj = datas.label
                  }
                })
                $self.value = obj
            })
        }`),
        'x-render-table-column': {
          title: "{{$t('vendorMod.reviewModelId')}}", // 评审与结论阶段显示该模板
          minWidth: 140,
          customRender: true
        }
      },
      fileName: {
        type: 'string',
        'x-hidden': true,
        'x-render-table-column': {

        }
      },
      fileId: {
        type: 'string',
        title: "{{$t('vendorMod.attachmentUpload')}}", // 评审与结论阶段显示该模板
        'x-component': 'SrmCommonFile',
        'x-component-props': {
          'extra-data': {
            uploadType: 'DEF',
            sourceType: 'WEB_APP',
            fileModular: 'sup',
            fileFunction: 'siteAssessment',
            fileType: 'images'
          },
          'default-file': expression(`{
            fileId: $table.getRowByIndex($self.index).fileId,
            fileName: $table.getRowByIndex($self.index).fileName
          }`),
          'readonly': expression(`$attrs.params.flag == 'view' || $form.query('state').get('data').bolType == 3 `),
          '@on-change': expression(`({file}) => {
            const { fileId = null, fileName = '' } = file || {}
            let row = $table.getRowByIndex($self.index)
            row.fileId = fileId.toString()
            row.fileName = fileName
          }`)
        },
        'x-render-table-column': {
          minWidth: 140
        }
      },
      userName: {
        type: 'string',
        'x-hidden': true
      },
      reviewPeopleName: {
        type: 'string',
        title: i18nExpression('vendorMod.reviewPeopleName'), // 评审人员
        'x-component-props': {
          disabled: true
        },
        'x-render-table-column': {
          minWidth: 100
        },
        'x-reactions': expression(`() => {
            const userName = $table.getRowByIndex($self.index)?.userName
            $self.value = userName
          }`)
      },
      userPost: {
        type: 'string',
        title: i18nExpression('vendorMod.userPost'), // 岗位
        'x-component-props': {
          disabled: true
        },
        'x-render-table-column': {
          minWidth: 100
        }
      },
      reviewDate: {
        type: 'string',
        title: i18nExpression('vendorMod.reviewTime'), // 评审时间
        'x-component-props': {
          disabled: true
        },
        'x-render-table-column': {
          minWidth: 100
        }
      },
      score: {
        type: 'string',
        title: i18nExpression('vendorMod.score'), // 得分
        'x-component-props': {
          disabled: expression(`$form.query('state').get('data').bolType == 3 || $table.getRowByIndex($self.index)?.disable == true`)
        },
        'x-render-table-column': {
          minWidth: 100
        }
      },
      authResult: {
        type: 'string',
        title: i18nExpression('vendorMod.result'), // 结果
        'x-component-props': {
          disabled: expression(`$form.query('state').get('data').bolType == 3 || $table.getRowByIndex($self.index)?.disable == true`)
        },
        'x-render-table-column': {
          minWidth: 100
        }
      },
      remark: {
        type: 'string',
        title: i18nExpression('common.remark'), // 备注
        'x-component-props': {
          disabled: expression(`$form.query('state').get('data').bolType == 3 || $table.getRowByIndex($self.index)?.disable == true`)
        },
        'x-render-table-column': {
          minWidth: 100
        }
      }
    })
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
            if ((state.bolType == 2 && accountNameId != createdId) || approveStatus == 'APPROVED' || $form.readPretty){
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
            if ((state.bolType == 2 && accountNameId != createdId) || approveStatus == 'APPROVED' || $form.readPretty){
              $self.disabled = true
            } else {
              $self.disabled = false
            }
          }`)
        }
      }
    }
}
