<script setup lang="ts">
import { RenderEngine } from 'lib@/components/render-engine'
import {
  defineSchemas,
  expression,
  i18nExpression,
  ViewModel
} from '@meicloud/render-engine'
import { FormCollapse } from '@meicloud/render-pix'
import OrganizationSelector from 'lib@/components/organization-selector'
import QuickSearch from 'lib@/components/QuickSearch'
import { DictClass } from '@/library/utils/dict/dict-utils'
import InvoiceInfo from './components/invoiceInfo.ts'
import invoiceDetail from './invoiceDetail'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import { FileDynamic } from '@/library/components/srm-components/file-dynamic'
import CInvoiceUpload from 'lib@/components/c-ocr/c-invoice-upload'
import UploadInvoice from 'lib@/components/c-ocr/c-invoice-upload/uploadInvoice'
import { downloadWithParam, getImgSrc } from 'lib@/utils/file'
import FilePreview from './components/filePreview'

const $getDictLabel = DictClass.getDictLabel // svg组件
const { app, t: $t, emitTabRemove, buyer, vendor, getCurrentUserRole } = usePageHelper()

const getValueFromFormValues = (fieldName: string) => expression(`$form.values.${fieldName}`)

const formatYMDHMS = (date: string) => date && app.$dayjs(date).format('YYYY-MM-DD HH:mm:ss')

const schema = defineSchemas({
  PerInvoice: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-query-engine': {
      service: 'cm',
      actions: {
        read: {
          immediate: true,
          ready: expression(`() => {
            const edit = !!$attrs.params.row.perInvoiceId

            if ($attrs.params.flag === 'add') {
              const { userInfo } = app.$store.getters.user
              $form.setValues({
                vendorId: userInfo.companyId,
                vendorCode: userInfo.companyCode,
                vendorName: userInfo.companyName
              })
            }

            return edit
          }`),
          transformRequest: expression(`(data, headers) => {
            data.payload = [$attrs.params.row.perInvoiceId]
            return data
          }`),
          onSuccess: expression(`(res) => {
            $form.setValues({
              ...res.data[0],
              perInvoiceId: $attrs.params.row.perInvoiceId,
              taxId:res.data[0].taxId?.toString()
            })
            $calcTaxAmountAndSetFieldValue($form, res.data[0].perInvoiceDetailList)
            if (["SUPPLIER_SUBMITTED"].includes(res.data[0].status)) {
              $form.readPretty = true
            }
            $form.query('fileUploads').take(field => {
              field.componentProps.componentInstance.reLoadFileInfo()
            })
          }`)
        },
        save: {
          // 标记当前 action 需要消费底层储存的级联删除数据
          cascadeDeletion: true
        }
      }
    },
    'x-component': 'FormContainer',
    'x-data': {
      fileList: [],
      fileuploadId: null,
      fileSourceName: null,
      filePreviewInit: false,
      extraData: {
        sourceType: 'WEB_APP',
        uploadType: 'DEF',
        fileModular: 'cm',
        fileFunction: expression('$buyer() ? \'BUYER_INVOICE_CONTRACT_PERFORMANCE\' : \'SUPPLIER_INVOICE_CONTRACT_PERFORMANCE\''),
        fileType: 'images'
      }
    },
    items: {
      type: 'object',
      properties: {
        goBack: {
          type: 'void',
          'x-content': i18nExpression('vendorMod.goBack'),
          'x-component': 'Button',
          'x-component-props': {
            type: 'default',
            '@submit': expression(`async (values) => {
              emitTabRemove($attrs.tabName)
            }`)
          }
        },
        staging: {
          type: 'void',
          'x-content': i18nExpression('common.staging'),
          'x-component': 'Button',
          'x-hidden': '{{$attrs.params.flag == \'manage\'}}',
          'x-component-props': {
            type: 'default',
            plain: 'plain',
            '@submit': expression(`async (values) => {
              setTimeout(() => {
                const flag = $attrs.params.flag
                let data = values
                if (!data.status) {
                  data.status = 'DRAFT'
                }

                if ($invoicingVerification($form)) {
                  $queryEngine.request.save($formatRequestPayloadData(data)).then(() => {
                    $success($bus)
                  })
                }
              })
           }`)
          }
        },
        submit: {
          type: 'void',
          'x-content': i18nExpression('common.submit'),
          'x-component': 'Button',
          'x-hidden': '{{$attrs.params.flag == \'manage\'}}',
          'x-component-props': {
            type: 'primary',
            '@submit': expression(`async (values) => {
              setTimeout(() => {
                const flag = $attrs.params.flag
                let data = values
                if (!data.status) {
                  data.status = 'SUPPLIER_SUBMITTED'
                }
                if ($invoicingVerification($form)) {
                  let datas = $formatRequestPayloadData(data)
                  datas.creationDate = $formatYMDHMS(datas.creationDate)

                  if (datas.perInvoiceDetailList.length > 0) {
                    datas.perInvoiceDetailList.forEach(e => {
                      e.creationDate = $formatYMDHMS(e.creationDate)
                      e.lastUpdateDate = $formatYMDHMS(e.lastUpdateDate)
                    })
                  }

                  $queryEngine.request.save(datas, { customizeAction: $vendor()  ? 'vendorSubmit' : undefined }).then(() => {
                    $success($bus)
                  })
                }
              })
           }`)
          }
        },
        toApprove: {
          type: 'void',
          'x-content': i18nExpression('common.toApprove'),
          'x-hidden': '{{!($attrs.params.flag == \'manage\' && $buyer())}}',
          'x-component': 'Button',
          'x-component-props': {
            type: 'primary',
            plain: 'plain',
            '@submit': expression(`async (values) => {
              setTimeout(() => {
                const flag = $attrs.params.flag
                let data = values
                data.status = 'APPROVED'

                if ($invoicingVerification($form)) {
                  let datas = $formatRequestPayloadData(data)
                  datas.perInvoiceId = $attrs.params.row.perInvoiceId
                  datas.creationDate = $formatYMDHMS(datas.creationDate)

                  if (datas.perInvoiceDetailList.length > 0) {
                    datas.perInvoiceDetailList.forEach(e => {
                      e.creationDate = $formatYMDHMS(e.creationDate)
                      e.lastUpdateDate = $formatYMDHMS(e.lastUpdateDate)
                    })
                  }

                  $queryEngine.request.save(datas, { customizeAction: 'approved' }).then(() => {
                    $success($bus)
                  })
                }
              })
           }`)
          }
        },
        sureRefuse: {
          type: 'void',
          'x-content': i18nExpression('orderMod.buyerOrderSynergy.sureRefuse'),
          'x-hidden': '{{!($attrs.params.flag == \'manage\' && $buyer())}}',
          'x-component': 'Button',
          'x-component-props': {
            type: 'primary',
            plain: 'plain',
            '@submit': expression(`async (values) => {
              setTimeout(() => {
                const flag = $attrs.params.flag
                let data = values
                if (!data.status) {
                  data.status = 'SUPPLIER_SUBMITTED'
                }
                if ($invoicingVerification($form)) {
                let datas = $formatRequestPayloadData(data)
                datas.perInvoiceId = $attrs.params.row.perInvoiceId
                  datas.creationDate = $formatYMDHMS(datas.creationDate)
                  if (datas.perInvoiceDetailList.length > 0) {
                    datas.perInvoiceDetailList.forEach(e => {
                      e.creationDate = $formatYMDHMS(e.creationDate)
                      e.lastUpdateDate = $formatYMDHMS(e.lastUpdateDate)
                    })
                  }

                  $queryEngine.request.save(datas, { customizeAction: 'rejected' }).then(() => {
                    $success($bus)
                  })
                }
              })
           }`)
          }
        }
      }
    },
    properties: {
      collapse: {
        type: 'void',
        'x-component': 'FormCollapse',
        'x-component-props': {
          defaultOpenPanelCount: 1
        },
        properties: {
          // 基础信息
          baseInfo: {
            type: 'void',
            'x-component': 'FormCollapse.Item',
            'x-component-props': {
              title: i18nExpression('supRisk.baseInfo')
            },
            properties: {
              formAll: {
                type: 'void',
                'x-component': 'FormGrid',
                'x-component-props': {
                  maxColumns: 4,
                  columnGap: 32,
                  rowGap: 0
                },
                'x-disabled': expression('$disabledFlag($attrs)'),
                properties: {
                  // 供应商编码
                  vendorCode: {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    title: i18nExpression('common.vendorCode'),
                    'x-component-props': {
                      disabled: true
                    }
                  },
                  // 供应商名称
                  vendorName: {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    title: i18nExpression('common.vendorName'),
                    'x-component-props': {
                      'disabled': true
                    }
                  },
                  buName: {
                    type: 'string',
                    'x-hidden': true,
                    'x-query-engine-primary-key': true
                  },
                  // 业务实体
                  buId: {
                    type: 'number',
                    'x-decorator': 'FormItem',
                    title: i18nExpression('bidMod.businessEntity'),
                    'x-component': 'OrganizationSelector',
                    'x-component-props': {
                      ref: 'orgSelector',
                      placeholder: '{{$t(\'common.pleaseSelect\')}}',
                      disabled: '{{$disabledControl($form)}}',
                      'node-type': 'OU',
                      '@select': `{{(node) => {
                        $self.value = node.organizationId && String(node.organizationId)
                        $values.buName = node.organizationName
                        $values.buCode = node.organizationCode
                      }}}`
                    },
                    'x-validator': {
                      required: true
                    }
                  },
                  // 币种
                  currencyCode: {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    title: i18nExpression('dataConfMod.settingGuide.step3.2'),
                    'x-component': 'DictSelect',
                    'x-component-props': {
                      code: 'currency',
                      disabled: '{{$disabledControl($form)}}',
                      '@change-value': `{{(value, dictItem) => {
                        $form.query(".currencyId").take().setValue(dictItem.id)
                        $form.query(".currencyCode").take().setValue(dictItem.value)
                        $form.query(".currencyName").take().setValue(dictItem.currencyName)
                      }}}`
                    },
                    'x-validator': {
                      required: true
                    }
                  },
                  currencyId: {
                    type: 'number',
                    'x-hidden': true,
                    'x-query-engine-primary-key': true
                  },
                  currencyName: {
                    type: 'string',
                    'x-hidden': true,
                    'x-query-engine-primary-key': true
                  },
                  // 税率
                  taxKey: {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    title: i18nExpression('dataConfMod.settingGuide.step3.4'),
                    'x-component': 'DictSelect',
                    'x-component-props': {
                      code: 'tax',
                      disabled: '{{$disabledControl($form)}}',
                      '@change-value': `{{(value, dictItem) => {
                        $self.value = dictItem.value
                        $self.query('taxId').take(field => {
                          field.value = dictItem.id
                        })
                      }}}`
                    },
                    'x-validator': {
                      required: true
                    }
                  },
                  taxId: {
                    type: 'number',
                    'x-hidden': true,
                    'x-query-engine-primary-key': true
                  },
                  // 开票含税总金额
                  taxAmount: {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    title: i18nExpression('other.key25'),
                    'x-component-props': {
                      'disabled': true
                    }
                  },
                  // 开票未税总金额
                  notTaxAmount: {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    title: i18nExpression('other.key24'),
                    'x-component-props': {
                      'disabled': true
                    }
                  },
                  // 状态
                  status: {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    title: '状态',
                    'x-component': 'DictSelect',
                    'x-component-props': {
                      code: 'CONTRACT_INVOICE_STATUS',
                      'disabled': true
                    }
                  },
                  // 创建人
                  createdFullName: {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    title: i18nExpression('common.creator'),
                    'x-component-props': {
                      'disabled': true
                    }
                  },
                  // 创建时间
                  creationDate: {
                    type: 'date',
                    'x-decorator': 'FormItem',
                    title: i18nExpression('common.creationTime'),
                    'x-component-props': {
                      'disabled': true
                    }
                  }
                }
              }
            }
          },
          // 开票明细
          invocie: {
            type: 'void',
            'x-component': 'FormCollapse.Item',
            'x-component-props': {
              title: i18nExpression('purSettlementMod.invoiceDetail')
            },
            properties: {
              'perInvoiceDetailList': {
                'x-component': 'invoiceDetail',
                'x-query-engine-relation': 'perInvoiceDetailList:*',
                'x-component-props': {
                  disabledFlag: '{{$disabledFlag($attrs) || $form.readPretty}}',
                  buId: getValueFromFormValues('buId'),
                  buCode: expression('$values.buCode'),
                  currencyId: getValueFromFormValues('currencyId'),
                  currencyCode: getValueFromFormValues('currencyCode'),
                  taxId: getValueFromFormValues('taxId'),
                  taxKey: getValueFromFormValues('taxKey'),
                  validate: `{{
                    (scope) => {
                      return $form.validate('PerInvoice.collapse.baseInfo.formAll').then(() => {

                      })
                    }
                  }}`,
                  '@change': expression(`(values) => {
                    $calcTaxAmountAndSetFieldValue($form, values)
                  }`)
                }
              }
            }
          },
          // 发票信息
          invoiceInfo: {
            ...InvoiceInfo
          },
          // 相关附件
          relevantAttachment: {
            type: 'void',
            'x-component': 'FormCollapse.Item',
            'x-component-props': {
              title: i18nExpression('accountMod.relevantAttachment')
            },
            properties: {
              fileUploads: {
                type: 'array',
                'x-query-engine-relation': 'fileUploads:*',
                'x-component': 'FileDynamic',
                'x-component-props': {
                  'scene-module-code': 'SCENE_PERFORM_INVOICE_ATTACHMENT',
                  'business-id': `{{
                    $attrs.params.row.perInvoiceId
                  }}`,
                  editable: '{{!$disabledFlag($attrs) && !$form.readPretty}}',
                  'need-init': false
                }
              }
            }
          }
        }
      }
    }
  },
  // 发票预览
  filePreview: {
    type: 'void',
    title: expression('$form.query(\'PerInvoice\').get(\'data\').fileSourceName + \'- 预览\''),
    'x-component': 'RDialog',
    'x-component-props': {
      size: 'xLarge',
      footer: true,
      okButtonProps: false,
      beforeClose: expression(`(done, type) => {
        $form.query('PerInvoice').get('data').filePreviewInit = false
        done()
      }`)
    },
    properties: {
      purchaseOrder: {
        type: 'void',
        'x-component': 'FilePreview',
        'x-component-props': {
          init: expression('$form.query(\'PerInvoice\').get(\'data\').filePreviewInit'),
          fileuploadId: expression('$form.query(\'PerInvoice\').get(\'data\').fileuploadId')
        }
      }

    }
  },
  // 编辑
  uploadInvoice: {
    type: 'void',
    'x-component': 'UploadInvoice',
    'x-component-props': {
      visible: false,
      headerText: {
        title: '编辑发票',
        content: $t('purSettlementMod.voiceTitle')
      },
      invoiceFiles: expression('$form.query(\'PerInvoice\').get(\'data\').fileList'),
      extraData: expression('$form.query(\'PerInvoice\').get(\'data\').extraData'),
      hasRepeat: false,
      editMode: true,
      invoiceInformation: expression('$form.values.perInvoiceInformationList'),
      '@editInvoiceRow': expression(`(row) => {
          $form.query('uploadInvoice').get('componentProps').visible = false
          $editInvoiceRow($form,row)
        }`),
      '@saveInvoice': expression(`(fileList) => {
          $form.query('uploadInvoice').get('componentProps').visible = false
          $saveInvoice($form,fileList)
       }`),
      '@closeInvoice': expression(`(fileList) => {
         $form.query('uploadInvoice').get('componentProps').visible = false
       }`)
    }
  }
})
const attrs = useAttrs()

const $invoicingVerification = ($form) => { // 校验是否一个开票单只能有一种里程碑
  let perInvoiceDetailList = $form.query('perInvoiceDetailList').take().value
  if (perInvoiceDetailList?.length > 0) {
    const milestoneType = perInvoiceDetailList[0].milestoneType
    let bol = true
    let bol2 = true
    perInvoiceDetailList.forEach((e) => {
      if (!e.milestoneType) {
        bol2 = false
        return false
      }
      if (e.milestoneType != milestoneType) {
        bol = false
      }
    })
    if (!bol2) {
      app.$message.warning('请输入里程碑节点')
      return false
    }
    if (!bol) {
      app.$message.warning('一个开票单只能选择一种里程碑')
      return false
    }
    return bol
  } else {
    app.$message.warning(t('contractMod.billingDetailsMgs'))
    return false
  }
}

const $disabledControl = ($form) => {
  let bol = false
  try {
    bol = !!$form.query('perInvoiceDetailList').take().value.filter(item => item.contractNo).length
  } catch (err) {}
  return bol
}

const inputFormat = { type: 'float', digits: 2, negative: false, zero: false }

const $disabledFlag = ($attrs) => {
  return !!['view', 'manage'].includes($attrs.flag)
}

const formatPerInvoiceDetailList = (arr) => {
  arr.forEach(item => {
    let milestoneTypeList = item.milestoneTypeList
    item.currentUntaxedAmount = parseFloat(item.currentUntaxedAmount)
    if (milestoneTypeList && typeof (milestoneTypeList) !== 'string') item.milestoneTypeList = JSON.stringify(milestoneTypeList)
  })
  return arr
}

const formatRequestPayloadData = (data) => {
  return {
    ...data,
    perInvoiceDetailList: formatPerInvoiceDetailList(data.perInvoiceDetailList)
  }
}

const $calcTaxAmountAndSetFieldValue = ($form: ViewModel, values: any[] = []) => {
  let total = 0; let unTotal = 0
  if (values) {
    values.forEach(item => {
      total += parseFloat(item.currentTaxedAmount || 0)
      unTotal += parseFloat(item.currentUntaxedAmount || 0)
    })
  }

  $form.query('taxAmount').take(field => {
    field.value = total
  })

  $form.query('notTaxAmount').take(field => {
    field.value = unTotal
  })
  if (values[0]?.taxKey) {
    $form.query('.taxKey').take(field => {
      field.value = values[0]?.taxKey
    })
    $form.query('.taxId').take(field => {
      field.value = values[0]?.taxId
    })
  }
  if (values[0]?.buId) {
    $form.values.buId = values[0].buId || null
    $form.values.buCode = values[0].buCode || null
    $form.values.buName = values[0].buName || null
  }
  if (values[0]?.currencyId) {
    $form.values.currencyCode = values[0].currencyCode || null
    $form.values.currencyId = values[0].currencyId || null
    $form.values.currencyName = values[0].currencyName || null
  }
}
/** 发票信息---------start */
// 编辑发票行
const $editInvoiceRow = ($form: any, row: any) => {
  for (let item of $form.values.perInvoiceInformationList) {
    if (item.fileuploadId === row.fileuploadId) {
      Object.assign(item, row)
      break
    }
  }
}

// 新增发票 - 保存
const $saveInvoice = ($form: any, fileList: any) => {
  const ids = $form.values.perInvoiceInformationList.map((item: any) => item.fileuploadId)
  fileList.forEach((item: any) => {
    if (!ids.includes(item.fileuploadId)) {
      $form.query('perInvoiceInformationList').take((field: any) => field.value.push(item))
    }
  })
}

// 批量删除发票信息
const $batchDeleteInvoice = ($form: any, $message: any) => {
  const rows = $form.query('perInvoiceInformationList').take()
    .componentProps
    .componentInstance
    .getCheckboxRecords()

  if (rows.length === 0) {
    return $message.warning($t('common.msgSelectData'))
  }
  const ids = rows.map((select: any) => select.fileuploadId)
  $form.values.perInvoiceInformationList = $form.values.perInvoiceInformationList.filter((item: any) => !ids.includes(item.fileuploadId))
}

// 发票预览
const $invoicePreview = ($form: any, row: any) => {
  $form.query('filePreview').take().setComponentProps({ visible: true })

  setTimeout(() => {
    $form.query('PerInvoice').get('data').fileSourceName = row.fileSourceName.split('.')[0]
    $form.query('PerInvoice').get('data').fileuploadId = row.fileuploadId
    $form.query('PerInvoice').get('data').filePreviewInit = true
  })
}

// 下载发票
const $downloadInvoice = (row: any, $message: any) => {
  if (row.fileuploadId) {
    downloadWithParam(
      row.fileuploadId,
      row.fileSourceName,
    ).catch(() => {
      $message.error($t('components.eio.downloadFail')) // 下载失败
    })
  } else {
    throw new Error('AttachId is null.')
  }
}

/** 发票信息---------end */

const scope = {
  $formatYMDHMS: formatYMDHMS,
  app,
  $success: ($bus: any) => {
    app.$message.success(app.$t('common.successSave'))
    emitTabRemove(attrs.tabName)
    $bus.$emit('ListPerInvoice')
  },
  $calcTaxAmountAndSetFieldValue,
  $attrs: attrs,
  $disabledControl,
  $disabledFlag,
  $getDictLabel,
  inputFormat,
  $formatRequestPayloadData: formatRequestPayloadData,
  $invoicingVerification,
  emitTabRemove,
  $buyer: buyer,
  $vendor: vendor,
  getCurrentUserRole,
  $editInvoiceRow,
  $saveInvoice,
  $batchDeleteInvoice,
  $invoicePreview,
  $downloadInvoice,
  getImgSrc
}

const components = {
  FormCollapse,
  OrganizationSelector,
  QuickSearch,
  InvoiceInfo,
  invoiceDetail,
  FileDynamic,
  CInvoiceUpload,
  UploadInvoice,
  FilePreview
}
</script>

<template>
  <RenderEngine
    schemaKey="contractPerformanceInvoiceVendorDetail"
    :pageAttrs="$attrs"
    :schema="schema"
    :components="components"
    :scope="scope"
  />
</template>
