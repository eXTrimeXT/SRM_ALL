// 采购目录上下架页面
import { expression, i18nExpression } from '@meicloud/render-engine'
import pictureCard from 'lib@/compositionEngine/oneStopShopping/components/pictureCard'
import priceSearch from 'lib@/compositionEngine/oneStopShopping/components/priceSearch'
import {
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

// 价格库编码插槽
const priceSlot = ($form: any, statusJudge: string) => {
  const data = $form.query('CatalogOnShelvesVendor').get('data')
  const readStatus = data.isReadOnly || $form.values?.status === statusJudge
  const allClass = [
    readStatus ? 'el-input__icon' : 'off-cursor',
    'iconfont',
    'iconselect'
  ]
  return {
    functional: true,
    render: (h: any) => {
      return h('em', {
        attrs: {
          class: allClass.join(' '),
          style: 'line-height: 28px;'
        },
        on: {
          click: () => {
            if (readStatus) return
            console.log($form.query('*.priceSearch').take(), 'priceSearch')
            $form.query('*.priceSearch').take((field: any) => {
              field.visible = true
              field.setComponentProps({
                visible: true
              })
            })
          }
        }
      })
    }
  }
}

// 图片上传成功赋值
const handleSuccess = (fileList: any, $form: any) => {
  $form.query('CatalogOnShelvesVendor').get('data').catalogOnShelvesAttaches = fileList
}

const setDefaultPic = (file: any, catalogOnShelvesAttaches: any) => {
  catalogOnShelvesAttaches.forEach((item: any) => {
    if (!item.response.data.ifDefaultPicture) item.response.data.ifDefaultPicture = 'N'
    if (file.fileuploadId === item.response.data.fileuploadId) {
      if (item.response.data.ifDefaultPicture === 'Y') {
        item.ifDefaultPicture = 'N'
        item.response.data.ifDefaultPicture = 'N'
      } else {
        item.ifDefaultPicture = 'Y'
        item.response.data.ifDefaultPicture = 'Y'
      }
    } else {
      item.ifDefaultPicture = 'N'
      item.response.data.ifDefaultPicture = 'N'
    }
  })
}

// 价格编号确认
const priceConfirm = (select: any, $form: any) => {
  $form.setValues(select)
}

/**
 * 采购目录上下架页面函数
 * @param scope 页面注册变量方法
 * @param components 组件注册
 * @param statusJudge 价格库编号只读态判断
 * @param priceSearchUrl 价格库编号查询url
 * @param handleSuccess 图片上传成功回调
 * @param setDefaultPic 设置默认图片
 */
export default (scope: any, components: any, pageParams: any) => {
  Object.assign(scope, {
    priceSlot,
    pageParams,
    handleSuccess,
    setDefaultPic,
    priceConfirm
  })
  Object.assign(components, {
    priceSearch,
    pictureCard
  })
  return {
    priceSearch: {
      type: 'void',
      'x-visible': false,
      'x-component': 'priceSearch',
      'x-reactions': expression(`() => {
        if (!pageParams.priceSearchUrl) return
        $self.setComponentProps({
          url: pageParams.priceSearchUrl
        })
      }`),
      'x-component-props': {
        '@confirm': '{{(select) => priceConfirm(select, $form)}}',
        '@close': expression(`() => {
          console.log('close')
          $self.visible = false
          $form.query('.priceSearch').take().setComponentProps({
            visible: false
          })
        }`)
      }
    },
    collapse: {
      type: 'void',
      'x-component': 'Collapse',
      'x-component-props': {
        defaultOpenPanelCount: 1
      },
      properties: {
        // 物料信息
        form: {
          type: 'void',
          'x-component': 'CollapseItem',
          'x-component-props': {
            title: i18nExpression('bidMod.itemInfo')
          },
          properties: {
            materialInfor: {
              type: 'void',
              'x-decorator': 'FormLayout',
              'x-decorator-props': {
                layout: 'vertical'
              },
              'x-component': 'FormGrid',
              'x-component-props': {
                maxColumns: 4,
                columnGap: 32,
                rowGap: 0
              },
              properties: {
                // 价格库编号
                priceLibraryNo: {
                  type: 'string',
                  title: i18nExpression('bidMod.priceLibraryNo'),
                  'x-reactions': expression(`() => {
                    if (!$form.values.status) return
                    setTimeout(() => {
                      $self.setComponentProps({
                        disabled: $form.query("CatalogOnShelvesVendor").get("data").isReadOnly || $form.values?.status === pageParams.statusJudge
                      })
                    })
                  }`),
                  readOnly: true,
                  'x-decorator': 'FormItem',
                  'x-validator': {
                    required: true,
                    message: i18nExpression('bidMod.selectPriceLibraryNo')
                  },
                  'x-content': {
                    suffix: '{{priceSlot($form, pageParams.statusJudge)}}'
                  }
                },
                // 业务实体
                orgName: {
                  type: 'string',
                  'x-decorator': 'FormItem',
                  title: i18nExpression('dataConfMod.orgId'),
                  'x-component-props': {
                    disabled: true
                  }
                },
                // 库存组织
                organizationName: {
                  type: 'string',
                  title: i18nExpression('purchaseDemand.invOrg'),
                  'x-decorator': 'FormItem',
                  'x-component-props': {
                    disabled: true
                  }
                },
                // 供应商编码
                vendorCode: {
                  type: 'string',
                  title: i18nExpression('common.vendorCode'),
                  'x-decorator': 'FormItem',
                  'x-component-props': {
                    disabled: true
                  }
                },
                // 供应商名称
                vendorName: {
                  type: 'string',
                  title: i18nExpression('common.vendorName'),
                  'x-decorator': 'FormItem',
                  'x-component-props': {
                    disabled: true
                  }
                },
                // 物料编码
                materialCode: {
                  type: 'string',
                  title: i18nExpression('common.materialCode'),
                  'x-decorator': 'FormItem',
                  'x-component-props': {
                    disabled: true
                  }
                },
                // 物料名称
                materialName: {
                  type: 'string',
                  title: i18nExpression('common.materialName'),
                  'x-decorator': 'FormItem',
                  'x-component-props': {
                    disabled: true
                  }
                },
                // 品类名称
                categoryName: {
                  type: 'string',
                  title: i18nExpression('common.categoryName'),
                  'x-decorator': 'FormItem',
                  'x-component-props': {
                    disabled: true
                  }
                },
                // 单位
                unit: {
                  type: 'string',
                  title: i18nExpression('purchaseDemand.unitCode'),
                  'x-decorator': 'FormItem',
                  'x-component-props': {
                    disabled: true
                  }
                },
                // 状态
                status: {
                  type: 'string',
                  title: i18nExpression('common.status'),
                  'x-decorator': 'FormItem',
                  'x-component': 'DictSelect',
                  'x-component-props': {
                    code: 'CATALOG_ON_SHELVES_STATUS',
                    disabled: true
                  }
                },
                // 创建人
                createdUserName: {
                  type: 'string',
                  'x-decorator': 'FormItem',
                  title: i18nExpression('orderMod.buyerOrderSynergy.createdBy'),
                  'x-component-props': {
                    disabled: true
                  }
                },
                // 创建时间
                creationDate: {
                  'x-decorator': 'FormItem',
                  title: i18nExpression('orderMod.buyerOrderSynergy.creationDate'),
                  ...yearMonthDaySelectorSegment,
                  'x-component-props': {
                    ...yearMonthDaySelectorSegment['x-component-props'],
                    disabled: true
                  }
                }
              }
            }
          }
        },
        // 价格信息
        priceItem: {
          type: 'void',
          'x-component': 'CollapseItem',
          'x-component-props': {
            title: i18nExpression('dataConfMod.priceInfor')
          },
          'x-query-engine-skip': true,
          'x-read-pretty': true,
          properties: {
            priceInfor: {
              type: 'void',
              'x-decorator': 'FormLayout',
              'x-decorator-props': {
                layout: 'vertical'
              },
              'x-component': 'FormGrid',
              'x-component-props': {
                maxColumns: 4,
                columnGap: 32,
                rowGap: 0
              },
              properties: {
                // 含税单价
                taxPrice: {
                  type: 'string',
                  'x-decorator': 'FormItem',
                  title: i18nExpression('purchaseDemand.taxPrice')
                },
                // 未税单价
                notaxPrice: {
                  type: 'string',
                  title: i18nExpression('contractMod.untaxedPrice'),
                  'x-decorator': 'FormItem'
                },
                // 税率
                taxRate: {
                  type: 'string',
                  'x-decorator': 'FormItem',
                  title: i18nExpression('purchaseDemand.taxRate')
                },
                // 币种
                currencyCode: {
                  type: 'string',
                  'x-decorator': 'FormItem',
                  title: i18nExpression('purchaseDemand.currency'),
                  'x-component': 'DictSelect',
                  'x-component-props': {
                    code: 'currency'
                  }
                },
                // 价格有效期自
                effectiveDate: {
                  ...yearMonthDaySelectorSegment,
                  'x-component-props': {
                    ...yearMonthDaySelectorSegment['x-component-props']
                  },
                  'x-decorator': 'FormItem',
                  title: i18nExpression('quota.priceStartTime')
                },
                // 价格有效期至
                expirationDate: {
                  ...yearMonthDaySelectorSegment,
                  'x-component-props': {
                    ...yearMonthDaySelectorSegment['x-component-props']
                  },
                  'x-decorator': 'FormItem',
                  title: i18nExpression('quota.priceEndTime')
                }
              }
            }
          }
        },
        // 物料参数
        materialItem: {
          type: 'void',
          'x-component': 'CollapseItem',
          'x-component-props': {
            title: i18nExpression('dataConfMod.materialParams')
          },
          'x-reactions': expression(`() => {
            if (!$values.status) return
            $self.readPretty = $form.query("CatalogOnShelvesVendor").get("data")?.isReadOnly
          }`),
          properties: {
            materialParams: {
              type: 'void',
              'x-decorator': 'FormLayout',
              'x-decorator-props': {
                layout: 'vertical'
              },
              'x-component': 'FormGrid',
              'x-component-props': {
                maxColumns: 4,
                columnGap: 32,
                rowGap: 0
              },
              properties: {
                // 送货周期
                deliveryCycle: {
                  type: 'string',
                  'x-decorator': 'FormItem',
                  title: i18nExpression('dataConfMod.deliveryCycle'),
                  'x-component-props': {
                    maxlength: 25
                  }
                },
                // 品牌
                brand: {
                  type: 'string',
                  title: i18nExpression('dataConfMod.band'),
                  'x-decorator': 'FormItem',
                  'x-component-props': {
                    maxlength: 25
                  }
                },
                // 最小起订量
                orderQuantityMinimum: {
                  type: 'string',
                  title: i18nExpression('dataConfMod.orderQuantityMinimum'),
                  'x-decorator': 'FormItem',
                  'x-reactions': expression(`() => {
                    if (!$values.status && attrs.params.flag !== 'add') return
                    const sign = $form.query("CatalogOnShelvesVendor").get("data")?.isReadOnly
                    if (sign) return 
                    setTimeout(() => {
                      $self.setComponentProps({
                        'v-input-format': { type: 'float', digits: 8, negative: false }
                      })
                    },2000)
                  }`),
                  'x-component-props': {
                    maxlength: 30
                  }
                },
                // 规格/型号
                specification: {
                  type: 'string',
                  title: i18nExpression('common.specification'),
                  'x-decorator': 'FormItem',
                  'x-component-props': {
                    maxlength: 25
                  }
                },
                // 重量
                weight: {
                  type: 'string',
                  title: i18nExpression('common.weight'),
                  'x-decorator': 'FormItem',
                  'x-component-props': {
                    maxlength: 25
                  }
                },
                // 尺寸
                size: {
                  type: 'string',
                  title: i18nExpression('common.size'),
                  'x-decorator': 'FormItem',
                  'x-component-props': {
                    maxlength: 25
                  }
                },
                // 颜色
                color: {
                  type: 'string',
                  title: i18nExpression('dataConfMod.ceeaColor'),
                  'x-decorator': 'FormItem',
                  'x-component-props': {
                    maxlength: 25
                  }
                }
              }
            }
          }
        },
        // 物料图片
        pictureItem: {
          type: 'void',
          'x-component': 'CollapseItem',
          'x-component-props': {
            title: i18nExpression('dataConfMod.materialPicture')
          },
          properties: {
            catalogOnShelvesAttaches: {
              type: 'array',
              'x-component': 'pictureCard',
              'x-query-engine-skip': true,
              'x-query-engine-relation': 'catalogOnShelvesAttaches:*',
              'x-component-props': {
                fileList: '{{$form.query("CatalogOnShelvesVendor").get("data").catalogOnShelvesAttaches}}',
                isReadOnly: '{{$form.query("CatalogOnShelvesVendor").get("data").isReadOnly}}',
                '@handleSuccess': '{{(res, file, fileList) => handleSuccess(fileList, $form)}}',
                '@setDefaultPic': '{{(file) => setDefaultPic(file, $form.query("CatalogOnShelvesVendor").get("data").catalogOnShelvesAttaches)}}',
                '@handleRemove': expression(`(newList, file) => {
                  if ($values.status) {
                    $form.query("CatalogOnShelvesVendor").get("data").attachesDeleteList.push(file)
                  }
                  $form.query("CatalogOnShelvesVendor").get("data").catalogOnShelvesAttaches = newList
                }`)
              }
            }
          }
        }
      }
    }
  }
}
