import {
  expression,
  i18nExpression,
  generateCharExpressionByFunction,
  $watchFormEffect
} from '@meicloud/render-engine'
import { yearMonthDaySelectorSegment } from 'lib@/components/render-engine'

export const companyInfo = {
  companyInfo: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: `{{observer(
        {
          render(h) {
            return h($$components.Note, {
              props: {
                title: t('vendorMod.enterpriseThreeCertificates'),
                value: $form.values.extRejectAttribute2,
                readonly: true
              }
            })
          }
        }
      )}}`
    },
    'x-visible': generateCharExpressionByFunction(({ $form }) => {
      return $form.query('state').get('data').overseasRelation !== 'PERSONAL'
    }),
    'x-query-engine-skip': true,
    properties: {
      div: {
        type: 'void',
        'x-component': 'div',
        'x-component-props': {
          class: 'companyInfo'
        },
        properties: {
          // 营业执照上传
          businessLicense: {
            type: 'string',
            'x-hidden': true
          },
          'businessLicenseFileId': {
            type: 'string',
            'x-component': 'SrmCommonFile',
            'x-component-props': {
              readonly: expression('$form.query(\'state\').get(\'data\').$disabled'),
              'list-type': "picture-card",
              style: {
                'width': '33%',
                'padding-right': '25px'
              },
              'defaultFile': {
                fileId: expression(`$self.value`),
                fileName: expression(`$form.query('businessLicense').get('value')`)
              },
              'dragger-options': {
                width: '100%',
                height: '345px'
              },
              'limit': 1,
              'drag': 'drag',
              '@on-change': expression(`({ file }) => {
                if (!file) {
                  $form.query('.businessLicenseFileId').take().value = null
                  $form.query('.businessLicense').take().value = null
                  return false
                }
                const { fileId, fileName } = file || {}
                // 判断是否需要OCR识别 境内供应商 && 开启OCR
                // if ($form.query('state').get('data').overseasRelation === 'INSIDE') {
                //   // 读取图片信息
                //   app.$http({
                //     url: '/api-pj/ocr/recognizeLcImage',
                //     method: 'GET',
                //     params: { fileuploadId: fileId },
                //     loading: true
                //   }).then(res => {
                //     const {
                //       regNum,
                //       person,
                //       name,
                //       address,
                //       business,
                //       businessEndDate,
                //       businessStartDate,
                //       capital,
                //       period,
                //       setDate,
                //       type
                //     } = res.data
                //     $form.query('.companyType').take().value = type
                //     $form.query('.companyName').take().value = name
                //     $form.query('.legalPerson').take().value = person
                //     $form.query('.lcCode').take().value = regNum
                //     // $form.query('.registeredCapital').take().value = licenseData.registeredCapital
                //     // $form.query('.registCurrency').take().value = licenseData.registCurrency
                //     $form.query('.companyAddress').take().value = address
                //     $form.query('.businessScope').take().value = business
                //     const [year, month, day] = setDate.replace(\/[^\\d]\/g, '-').split('-')
                //     const createDate = year + '-' + month + '-' + day
                //     $form.query('.companyCreationDate').take().value = app.$dayjs(createDate).format('YYYY-MM-DD')
                //     // 校验供应商是否已注册
                //     if (regNum) {
                //       app.$http({
                //         url: '/api-rbac/extUser/lcCodeVerify',
                //         method: 'GET',
                //         params: { lcCode: regNum, isPersonalAccount: 'N' }
                //       }).then(res => {
                //         if (res.code + '' === '0') {
                //           const data = res.data
                //           if (data.isRemind === 'Y') {
                //             let maskedName = ''
                //             if(data.nickname.length==2){
                //               maskedName=data.nickname.substring(0,1)+'*'
                //             }else if(data.nickname.length==3){
                //               maskedName=data.nickname.substring(0,1)+"*"+data.nickname.substring(2,3)
                //             }else if(data.nickname.length>3){
                //               maskedName=data.nickname.substring(0,1)+"*"+'*'+data.nickname.substring(3,data.nickname.length)
                //             }
                //             const maskedPhone = data.phone.replace(data.phone.substring(3,7),'****')
                //             // '该供应商已被注册，请联系' + maskedName + '，账号：' + maskedPhone + '，进行子账号维护操作。'
                //             app.$alert($t('cusEntry.vendorMod.registerTips', { maskedName, maskedPhone }), '', {
                //               confirmButtonText: $t('common.confirm'),
                //               callback: action => {}
                //             });
                //           }
                //         }
                //       })
                //     }
                //   })
                //   .catch(err => {
                //     console.log(err)
                //   })
                // }
                $form.query('.businessLicenseFileId').take().value = fileId.toString()
                $form.query('.businessLicense').take().value = fileName
              }`)
            },
            title: ''
          },
          layout: {
            type: 'void',
            'x-decorator': 'FormLayout',
            'x-decorator-props': {
              layout: 'vertical',
              style: {
                'width': '67%',
                'padding-left': '20px'
              }
            },
            'x-component': 'FormGrid',
            'x-component-props': {
              maxColumns: 2,
              columnGap: 32,
              rowGap: 0
            },
            properties: {
              // 企业名称
              'companyName': {
                type: 'string',
                'x-decorator': 'FormItem',
                'x-component': 'Input',
                title: expression(`$t('vendorMod.companyName')`),
                'x-component-props': {
                  disabled: expression('$form.query(\'state\').get(\'data\').$disabled')
                },
                'x-validator': {
                  required: true,
                  message: i18nExpression('vendorMod.msgCompanyName')
                }
              },
              // 企业简称
              companyShortName: {
                type: 'string',
                'x-decorator': 'FormItem',
                'x-component': 'Input',
                title: expression(`$t('vendorMod.companyShortName')`),
                'x-component-props': {
                  disabled: expression('$form.query(\'state\').get(\'data\').$disabled')
                },
                'x-validator': {
                  required: true,
                  message: i18nExpression('vendorMod.msgCompanyShortName')
                }
              },
              companyEnName: {
                type: 'string',
                'x-decorator': 'FormItem',
                'x-component': 'Input',
                title: expression(`$t('cusEntry.vendorMod.companyEnName')`),
                'x-component-props': {
                  disabled: expression('$form.query(\'state\').get(\'data\').$disabled')
                }
              },
              enterpriseNo: {
                type: 'string',
                title: i18nExpression('cusEntry.vendorMod.enterpriseNo'),
                'x-decorator': 'FormItem',
                'x-component-props': {
                  disabled: expression(`$form.query('state').get('data').$disabled`)
                },
                'x-validator': {
                  required: true,
                  message: i18nExpression('vendorMod.msgLegalPerson')
                }
              },
              // KPP编码
              extKpp: {
                type: 'string',
                title: i18nExpression('cusEntry.vendorMod.extKpp'),
                'x-decorator': 'FormItem',
                'x-component': 'Input',
                'x-component-props': {
                  disabled: expression('$form.query(\'state\').get(\'data\').$disabled')
                }
              },
              // 法人代表
              legalPerson: {
                type: 'string',
                'x-decorator': 'FormItem',
                'x-component': 'Input',
                title: expression(`$t('vendorMod.legalPerson')`),
                'x-component-props': {
                  disabled: expression('$form.query(\'state\').get(\'data\').$disabled')
                },
                'x-validator': {
                  required: true,
                  message: i18nExpression('vendorMod.msgLegalPerson')
                }
              },
              // 名称
              'nickname': {
                type: 'string',
                'x-decorator': 'FormItem',
                'x-component': 'Input',
                title: expression(`$t('dataConfMod.userName')`),
                'x-component-props': {
                  'disabled': expression('$form.query(\'state\').get(\'data\').$disabled'),
                },
                'x-validator': {
                  required: true,
                  message: i18nExpression('vendorMod.msgInputNickname')
                }
              },
              // 岗位
              'position': {
                type: 'string',
                'x-decorator': 'FormItem',
                'x-component': 'Input',
                title: expression(`$t('components.orgPositionSel.position')`),
                'x-component-props': {
                  'disabled': expression('$form.query(\'state\').get(\'data\').$disabled')
                }
              },
              // 币种
              registCurrency: {
                type: 'string',
                'x-hidden': true
              },
              // 注册资本(万)
              registeredCapital: {
                type: 'number',
                'x-visible': expression(`$form.query('.companyType').take().value != 'GETI'`),
                'x-decorator': 'FormItem',
                'x-component': 'Input',
                title: expression(`$t('vendorMod.registeredCapital')`),
                'x-component-props': { 
                  disabled: expression('$form.query(\'state\').get(\'data\').$disabled'),
                  class: "input-with-select",
                  '@change': expression(`(value) => {
                    $self.value = value.replace(\/[^\\d.]\/g, '')
                  }`)
                },
                'x-content': {
                  append: expression(`observer(
                    {
                      render(h) {
                        const targetField = $self.query('.registCurrency').take()
                        return h("div", {class: "bzBox"}, [
                          h("label", {class: "bzTitle"}, $t('vendorMod.currencyCode')),
                            h(DictSelect, {
                              props: {
                                value: targetField.value,
                                code: 'currency',
                              },
                              attrs: {
                                disabled: $form.query('state').get('data').$disabled,
                              },
                              on: {
                                'change-value': (value) => {
                                  targetField.value = value
                                }
                              }
                            }),
                          ])
                        }
                      }
                    )
                  `)
                },
                'x-validator': {
                  required: expression(`!['GETI','FEIYINGLI'].includes($form.query('.companyType').take().value)`),
                  message: i18nExpression('vendorMod.msgRegisteredCapital')
                }
              },
              // 是否长期供应商
              ifLongPeriod: {
                type: 'string',
                title: i18nExpression('cusEntry.vendorMod.ifLongTermSupplier'),
                'x-component': 'DictSelect',
                'x-component-props': {
                  code: 'YES_OR_NO',
                  disabled: expression('$form.query(\'state\').get(\'data\').$disabled')
                },
                default: 'Y',
                'x-decorator': 'FormItem',
                'x-validator': {
                  required: true,
                  message: i18nExpression('cusEntry.tipMessage.ifLongPeriodMsg')
                }
              },
              businessEndDate: {
                type: 'date',
                'x-hidden': true
              },
              // 营业期限
              businessStartDate: {
                ...yearMonthDaySelectorSegment,
                'x-decorator': 'FormItem',
                'x-component-props': {
                  ...yearMonthDaySelectorSegment['x-component-props'],
                  disabled: expression('$form.query(\'state\').get(\'data\').$disabled')
                },
                title: expression(`$t('vendorMod.dateBusiness')`),
                'x-validator': {
                  required: expression(`$form.query('ifLongPeriod').take().value === 'N'`),
                  message: i18nExpression('vendorMod.msgCreationDate')
                }
              },
              // 成立日期
              companyCreationDate: {
                ...yearMonthDaySelectorSegment,
                'x-decorator': 'FormItem',
                'x-component-props': {
                  ...yearMonthDaySelectorSegment['x-component-props'],
                  disabled: expression('$form.query(\'state\').get(\'data\').$disabled')
                },
                title: expression(`$t('vendorMod.creationDate')`),
                'x-validator': {
                  required: expression(`!['FEIYINGLI'].includes($form.query('.companyType').take().value)`),
                  message: i18nExpression('vendorMod.msgCreationDate')
                }
              },
              // 营业范围
              businessScope: {
                type: 'string',
                'x-decorator': 'FormItem',
                'x-component': 'Input',
                title: expression(`$t('vendorMod.businessScope')`),
                'x-component-props': {
                  disabled: expression('$form.query(\'state\').get(\'data\').$disabled'),
                  type: "textarea",
                  maxlength: 2000
                },
                'x-decorator-props': { gridSpan: 3 }
              }
            }
          }
        }
      }
    }
  }
}
