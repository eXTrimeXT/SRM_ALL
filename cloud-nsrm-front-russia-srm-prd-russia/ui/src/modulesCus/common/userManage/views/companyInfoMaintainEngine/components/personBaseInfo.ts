import { 
  expression, 
  i18nExpression,
  generateCharExpressionByFunction
} from '@meicloud/render-engine'
import { requiredValidatorSegment } from 'lib@/components/render-engine/schema-segments'
export const personBaseInfo = {
  person: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: i18nExpression('cusEntry.vendorMod.baseInfo'),
    },
    'x-visible': generateCharExpressionByFunction(({ $form }) => {
      return $form.query('state').get('data').overseasRelation === 'PERSONAL'
    }),
    properties: {
      layout: {
        type: 'void',
        'x-decorator': 'FormLayout',
        'x-decorator-props': {
          layout: 'vertical'
        },
        'x-component': 'FormGrid',
        'x-component-props': {
          maxColumns: 3,
          columnGap: 32,
          rowGap: 0
        },
        properties: {
          personBaseInfo: {
            type: 'object',
            'x-query-engine-skip': true,
            properties: {
              businessLicenseFileId: {
                type: 'string',
                title: i18nExpression('cusEntry.vendorMod.frontOfIdCard'),
                'x-decorator': 'FormItem',
                'x-component': 'SrmCommonFile',
                'x-component-props': {
                  'extra-data': {
                    uploadType: 'DEF',
                    sourceType: 'WEB_APP',
                    fileModular: 'sup',
                    fileFunction: 'companyInfoMaintain',
                    fileType: 'images'
                  },
                  'default-file': {
                    fileId: expression(`$form.query('personBaseInfo').get('value').businessLicenseFileId`),
                    fileName: expression(`$form.query('personBaseInfo').get('value').businessLicense`)
                  },
                  '@on-change': expression(`({file}) => {
                     const { fileId = null, fileName = null } = file || {}
                     $form.query('personBaseInfo').get('value').businessLicenseFileId = fileId
                     $form.query('personBaseInfo').get('value').businessLicense = fileName
                     if (file) {
                      // 读取图片信息
                      app.$http({
                        url: '/api-pj/ocr/recognizeIDCardFront',
                        method: 'GET',
                        params: { fileuploadId: fileId },
                        loading: true
                      }).then(res => {
                        const {
                          birth,
                          idNum,
                          name,
                          sex
                        } = res.data
                        let form = $form.query('personBaseInfo').get('value')
                        form.companyName = name
                        form.lcCode = idNum
                        form.extSex = sex
                        // 校验供应商是否已注册
                        if (idNum) {
                          app.$http({
                            url: '/api-rbac/extUser/lcCodeVerify',
                            method: 'GET',
                            params: { lcCode: idNum, isPersonalAccount: 'Y' }
                          }).then(res => {
                            if (res.code + '' === '0') {
                              const data = res.data
                              if (data.isRemind === 'Y') {
                                let maskedName = ''
                                if(data.nickname.length==2){
                                  maskedName=data.nickname.substring(0,1)+'*'
                                }else if(data.nickname.length==3){
                                  maskedName=data.nickname.substring(0,1)+"*"+data.nickname.substring(2,3)
                                }else if(data.nickname.length>3){
                                  maskedName=data.nickname.substring(0,1)+"*"+'*'+data.nickname.substring(3,data.nickname.length)
                                }
                                const maskedPhone = data.phone.replace(data.phone.substring(3,7),'****')
                                const content = '该供应商已被注册，请联系' + maskedName + '，账号：' + maskedPhone + '，进行子账号维护操作。'
                                app.$alert(content, '', {
                                  confirmButtonText: '确定',
                                  callback: action => {}
                                });
                              }
                            }
                          })
                        }
                      })
                     }
                  }`),
                  readonly: expression(`$form.query('state').get('data').$disabled`)
                },
                ...requiredValidatorSegment
              },
              extIdCardOppositeFileName: {
                type: 'string',
                'x-hidden': true
              },
              extIdCardOppositeFileId: {
                type: 'string',
                title: i18nExpression('cusEntry.vendorMod.backOfIdCard'),
                'x-decorator': 'FormItem',
                'x-component': 'SrmCommonFile',
                'x-component-props': {
                  'extra-data': {
                    uploadType: 'DEF',
                    sourceType: 'WEB_APP',
                    fileModular: 'sup',
                    fileFunction: 'companyInfoMaintain',
                    fileType: 'images'
                  },
                  'default-file': {
                    fileId: expression(`$form.query('personBaseInfo').get('value').extIdCardOppositeFileId`),
                    fileName: expression(`$form.query('personBaseInfo').get('value').extIdCardOppositeFileName`)
                  },
                  '@on-change': expression(`({file}) => {
                    const { fileId = null, fileName = null } = file || {}
                    $form.query('personBaseInfo').get('value').extIdCardOppositeFileId = fileId
                    $form.query('personBaseInfo').get('value').extIdCardOppositeFileName = fileName
                    if (file) {
                      // 读取图片信息
                      app.$http({
                        url: '/api-pj/ocr/recognizeIDCardBack',
                        method: 'GET',
                        params: { fileuploadId: fileId },
                        loading: true
                      }).then(res => {
                        const {
                          businessEndDate,
                          businessStartDate
                        } = res.data
                        $form.query('personBaseInfo').get('value').validityPeriodOfCard = [businessStartDate, businessEndDate]
                      })
                     }
                  }`),
                  readonly: expression(`$form.query('state').get('data').$disabled`)
                },
                ...requiredValidatorSegment
              },
              companyName: {
                type: 'string',
                'x-decorator': 'FormItem',
                title: i18nExpression('cusEntry.vendorMod.companyNameOrPersonName'),
                ...requiredValidatorSegment,
                'x-component-props': {
                  disabled: expression(`$form.query('state').get('data').$disabled`)
                }
              },
              companyShortName: {
                type: 'string',
                'x-decorator': 'FormItem',
                title: i18nExpression('cusEntry.vendorMod.personalAbbreviation'),
                ...requiredValidatorSegment,
                'x-component-props': {
                  disabled: expression(`$form.query('state').get('data').$disabled`),
                  maxlength: 100,
                 'show-word-limit': true
                }
              },
              businessLicense: {
                type: 'string',
                'x-hidden': true
              },
              idNumber: {
                type: 'string',
                'x-decorator': 'FormItem',
                title: i18nExpression('cusEntry.vendorMod.idNo'),
                ...requiredValidatorSegment,
                'x-component-props': {
                  disabled: expression(`$form.query('state').get('data').$disabled`),
                  '@blur': expression(`(value) => {
                    if ($self.value) {
                      app.$http({
                        url: '/api-rbac/extUser/lcCodeVerify',
                        method: 'GET',
                        params: { lcCode: $self.value, isPersonalAccount: 'Y' }
                      }).then(res => {
                        if (res.code + '' === '0') {
                          const data = res.data
                          if (data.isRemind === 'Y') {
                            let maskedName = ''
                            if(data.nickname.length==2){
                              maskedName=data.nickname.substring(0,1)+'*'
                            }else if(data.nickname.length==3){
                              maskedName=data.nickname.substring(0,1)+"*"+data.nickname.substring(2,3)
                            }else if(data.nickname.length>3){
                              maskedName=data.nickname.substring(0,1)+"*"+'*'+data.nickname.substring(3,data.nickname.length)
                            }
                            const maskedPhone = data.phone.replace(data.phone.substring(3,7),'****')
                            const content = '该供应商已被注册，请联系' + maskedName + '，账号：' + maskedPhone + '，进行子账号维护操作。'
                            app.$alert(content, '', {
                              confirmButtonText: '确定',
                              callback: action => {}
                            });
                          }
                        }
                      })
                    }
                  }`)
                }
              },
              validityPeriodOfCard: {
                type: 'date',
                'x-decorator': 'FormItem',
                title: i18nExpression('cusEntry.vendorMod.validityPeriodOfCard'),
                'x-component-props': {
                  type: 'daterange',
                  disabled: expression(`$form.query('state').get('data').$disabled`)
                },
                ...requiredValidatorSegment
              },
              extSex: {
                type: 'string',
                'x-decorator': 'FormItem',
                title: i18nExpression('cusEntry.vendorMod.sex'),
                'x-component': 'DictSelect',
                'x-component-props': {
                  code: 'GENDER',
                  disabled: expression(`$form.query('state').get('data').$disabled`)
                },
                ...requiredValidatorSegment
              },
              businessScope: {
                type: 'string',
                title: i18nExpression('cusEntry.vendorMod.mainBusinessScope'),
                'x-decorator': 'FormItem',
                ...requiredValidatorSegment,
                'x-component-props': {
                  disabled: expression(`$form.query('state').get('data').$disabled`)
                }
              },
              // 国家
              companyCountry: {
                type: 'string',
                title: i18nExpression('components.address.country'),
                'x-decorator': 'FormItem',
                'x-component': 'DictSelect',
                'x-component-props': {
                  code: 'country',
                  disabled: expression(`$form.query('state').get('data').$disabled`),
                  placeholder: expression('$t(\'common.pleaseSelect\')'),
                  '@change': expression(`(val) => {
                    // 选择国外就清理省市区，并且禁用
                    if ($form.query('personBaseInfo.companyCountry').take().value !== 'CN') {
                      $form.query('personBaseInfo.companyProvince').take().value = ''
                      $form.query('personBaseInfo.companyCity').take().value = ''
                    }
                  }`)
                },
                ...requiredValidatorSegment
              },
              // 省
              companyProvince: {
                type: 'string',
                title: i18nExpression('components.address.area'),
                'x-decorator': 'FormItem',
                'x-component': 'DictSelect',
                'x-component-props': {
                  code: 'PROVINCE',
                  disabled: expression(`$form.query('state').get('data').$disabled`),
                  'custom-select-type': 'PROVINCE',
                  placeholder: expression('$t(\'common.pleaseSelect\')')
                },
                'x-visible': `{{$form.query('personBaseInfo.companyCountry').take().value == 'CN'}}`,
                ...requiredValidatorSegment
              },
              // 城市
              companyCity: {
                type: 'string',
                title: i18nExpression('components.address.city'),
                'x-decorator': 'FormItem',
                'x-component': 'DictSelect',
                'x-component-props': {
                  code: expression('$form.query(\'personBaseInfo.companyProvince\').take()?.value'),
                  'custom-select-type': 'CITY',
                  placeholder: expression('$t(\'common.pleaseSelect\')'),
                  disabled: expression(`$form.query('state').get('data').$disabled`)
                },
                'x-visible': `{{$form.query('personBaseInfo.companyCountry').take().value == 'CN'}}`,
                ...requiredValidatorSegment
              },
              companyAddress: {
                type: 'string',
                title: i18nExpression('cusEntry.vendorMod.detailAddress'),
                'x-decorator': 'FormItem',
                ...requiredValidatorSegment,
                'x-component-props': {
                  disabled: expression(`$form.query('state').get('data').$disabled`)
                }
              }
            }
          }
        }
      }
    }
  }
}
