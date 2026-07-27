import {
  generateXindexInOrder,
  i18nExpression,
  expression
} from '@meicloud/render-engine'
import {
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'
// @ts-ignore
import { deepClone } from '@/utils'

const createOrderFetch = ($form: any, $queryEngine: any, done: any, $message: any, $t: any, $http: any, app: any, closeLoading:any) => {
  const rows = $form.query('*.createOrderDialog.*.purOrderList').take()
    .componentProps
    .componentInstance
    .getCheckboxRecords()

  if (rows.length === 0) {
    $message.warning($t('common.msgSelectData'))
    closeLoading()
    return false
  }
  const params = deepClone(rows)
  for (const item of params) {
    const requirementDateTime = new Date(item.requirementDateBuff).getTime()
    const nowTime = new Date(new Date().toLocaleDateString()).getTime() // 获取当天0点时间戳
    if (requirementDateTime < nowTime) {
      closeLoading()
      return $message.warning($t('demandPoolManagement.prompt5')) // 要求到货日期,应晚于当前日期！
    }

    if (item.ladderPriceFlag === 'Y') {
      const ladderPrice = item.ladderPrices.find((obj:any) => {
        if (obj.endQuantity) {
          return item.thisOrderQuantity >= obj.beginQuantity && item.thisOrderQuantity < obj.endQuantity
        } else {
          return item.thisOrderQuantity >= obj.beginQuantity
        }
      })
      if (!ladderPrice) {
        closeLoading()
        return $message.warning($t('demandPoolManagement.prompt6')) // 本次下单数量无对应区间阶梯价，请修改！
      }
    }

    delete item.isladderPriceFlag
  }

  // $http({
  //   url: '/api-sup-ce/pr/requirementManage/submitPurchaseOrder',
  //   method: 'POST',
  //   data: params,
  //   loading: true
  // }).then((res: any) => {
  //   done()
  //   $queryEngine.state.paginationManagement.refresh()
  //   app.$router.push({
  //     name: 'buyerPurchaseOrder',
  //     params: { from: 'demandPoolManagement' }
  //   })
  // }).catch(() => {
  //   closeLoading()
  // })

  $queryEngine.request.baseRequest({
    'type': 'PrRequirementPoolForBuyer',
    'lang': 'zh-cn',
    'query': {
      '*': {}
    },
    'payload': params,
    'action': 'createPurchaseOrder'
  }).then((res: any) => {
    done()
    $queryEngine.state.paginationManagement.refresh()
    app.$router.push({
      name: 'buyerPurchaseOrder',
      params: { from: 'demandPoolManagement' }
    })
  }).catch(() => {
    closeLoading()
  })
}
const setQuantity = ($form: any, row: any, $message: any, $t: any) => {
  if (row.quotaProportion > 0) {
    row.thisOrderQuantity = (row.quotaProportion / 100) * row.orderQuantity
    getLadderPrice($form, row, $message, $t)
    $form.query('*.createOrderDialog.*.purOrderList').take((field: any) => {
      const vxeTable = field.invoke('getVxeTableInstance')
      const { tableData } = vxeTable.getTableData()
      vxeTable.setCheckboxRow([tableData[row.sortIndex]], true)
    })
  } else {
    row.thisOrderQuantity = 0
  }
}
const setPortation = ($form: any, row: any, $message: any, $t: any) => {
  if (row.thisOrderQuantity > 0) {
    row.quotaProportion = Number((row.thisOrderQuantity / row.orderQuantity) * 100).toFixed(4)
    getLadderPrice($form, row, $message, $t)
    $form.query('*.createOrderDialog.*.purOrderList').take((field: any) => {
      const vxeTable = field.invoke('getVxeTableInstance')
      const { tableData } = vxeTable.getTableData()
      vxeTable.setCheckboxRow([tableData[row.sortIndex]], true)
    })
  } else {
    row.quotaProportion = 0
  }
}

const getLadderPrice = ($form: any, row: any, $message: any, $t: any) => {
  if (row.ladderPriceFlag === 'Y') {
    const ladderPrice = row.ladderPrices.find((item:any) => {
      if (item.endQuantity) {
        return row.thisOrderQuantity >= item.beginQuantity && row.thisOrderQuantity < item.endQuantity
      } else {
        return row.thisOrderQuantity >= item.beginQuantity
      }
    })
    if (ladderPrice) {
      row.taxPrice = parseFloat((ladderPrice.price * (1 + row.taxRate / 100)).toFixed(8))
    } else {
      $message.warning($t('demandPoolManagement.prompt6')) // 本次下单数量无对应区间阶梯价，请修改！
    }
  }
}

const batchDoSupplierConfirm = ($form: any) => {
  $form.query('*.createOrderDialog.*.purOrderList').take((field: any) => {
    field.value.forEach((item:any) => {
      item.ceeaIfSupplierConfirm = 'Y'
    })
  })
}

const batchCancelSupplierConfirm = ($form: any) => {
  $form.query('*.createOrderDialog.*.purOrderList').take((field: any) => {
    field.value.forEach((item:any) => {
      item.ceeaIfSupplierConfirm = 'N'
    })
  })
}

const openLadderPriceDialog = ($form: any, row: any) => {
  $form.query('*.ladderPriceDialog').take().setComponentProps({ visible: true })
  const ladderPrices = deepClone(row.ladderPrices)
  ladderPrices.forEach((item:any) => { item.unit = row.unit })
  setTimeout(() => {
    $form.query('PrRequirementPoolForBuyer').get('data').ladderPriceDescribeRow = row
    $form.query('*.ladderPriceDialog.*.ladderPrices').take((field: any) => {
      field.value = ladderPrices
    })
  })
}

const supplierConfirmSlot = ($form:any, $t:any) => {
  return {
    functional: true,
    render: (h: any) => {
      return (
        <el-dropdown>
          <el-button>
            { $t('oneStopShopping.ifSupplierConfirm')}
            <i class="el-icon-arrow-down el-icon--right" />
          </el-button>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item>
              <div style="padding-bottom: 5px;">
                <el-button v-on:click={() => batchDoSupplierConfirm($form)}>
                  { $t('demandPoolManagement.batchSelect')}
                </el-button>
              </div>
            </el-dropdown-item>
            <el-dropdown-item>
              <el-button v-on:click={() => batchCancelSupplierConfirm($form)}>
                { $t('demandPoolManagement.batchCancelSelect')}
              </el-button>
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      )
    }
  }
}

export default {
  type: 'void',
  'x-decorator': 'QueryEngine',
  title: i18nExpression('purchaseDemand.createOrder'), // 创建采购订单
  'x-component': 'RDialog',
  'x-component-props': {
    size: 'xLarge',
    'close-on-click-modal': false,
    footerButtonList: expression(`(_, { cancelButton,okButton }) => {
      return [
        cancelButton,
        {...okButton, text: $t('purchaseDemand.createOrdersBulk')} // 批量创建订单
      ]
    }`),
    beforeClose: `{{(done, type, closeLoading) => { 
        if ( type === 'ok') { 
            $createOrderFetch($form, $queryEngine, done, $message, $t, $http, app, closeLoading)
        } else {
            done()
        }
    }}}`
  },
  properties: {
    toolbar: {
      type: 'void',
      'x-component': 'Space',
      'x-component-props': {
        style: 'margin-bottom: 16px'
      },
      properties: {
        confirm: {
          type: 'void',
          'x-content': '{{$supplierConfirmSlot($form,$t)}}'
        }
      }
    },
    layout: {
      type: 'void',
      properties: {
        purOrderList: {
          type: 'array',
          'x-query-engine-skip': true,
          'x-component': 'RenderTable',
          'x-component-props': {
            class: 'table-view-vxe-table',
            editMode: true,
            preColumns: 'checkbox,seq',
            pagination: false,
            sortable: false
          },
          'x-read-pretty': true,
          properties: generateXindexInOrder({
            organizationName: {
              type: 'string',
              title: i18nExpression('purchaseDemand.invOrg'), // 库存组织
              'x-render-table-column': {
                minWidth: 100,
                fixed: 'left'
              }
            },
            materialCode: {
              type: 'string',
              title: i18nExpression('purchaseDemand.itemCode'), // 物料编码
              'x-render-table-column': {
                minWidth: 100,
                fixed: 'left'
              }
            },
            materialName: {
              type: 'string',
              title: i18nExpression('purchaseDemand.itemName'), // 物料名称
              'x-render-table-column': {
                minWidth: 100,
                fixed: 'left'
              }
            },
            unit: {
              type: 'string',
              title: i18nExpression('bid_mod.unit'), // 单位
              'x-render-table-column': {
                minWidth: 100,
                fixed: 'left'
              }
            },
            requirementQuantity: {
              type: 'string',
              title: i18nExpression('purchaseDemand.requirementQuantity'), // 需求数量
              'x-render-table-column': {
                minWidth: 100
              }
            },
            requirementDate: {
              type: 'string',
              title: i18nExpression('purchaseDemand.requirementDate'), // 需求日期
              'x-render-table-column': {
                minWidth: 100
              }
            },
            orderQuantity: {
              type: 'string',
              title: i18nExpression('purchaseDemand.orderQuantity'), // 可下单数量
              'x-render-table-column': {
                minWidth: 100
              }
            },
            quotaProportion: {
              type: 'string',
              title: i18nExpression('purchaseDemand.quota'), // 配额比例(%)
              'x-render-table-column': {
                minWidth: 150
              },
              'x-component-props': {
                '@blur': expression('({row}) => $setQuantity($form,$table.getRowByIndex($self.index), $message, $t)')
              },
              'x-read-pretty': '{{$form.readPretty}}'
            },
            thisOrderQuantity: {
              type: 'string',
              title: i18nExpression('purchaseDemand.thisOrderQuantity'), // 本次下单数量
              'x-render-table-column': {
                minWidth: 150
              },
              'x-component-props': {
                '@blur': expression('({row}) => $setPortation($form,$table.getRowByIndex($self.index), $message, $t)')
              },
              'x-read-pretty': '{{$form.readPretty}}'
            },
            requirementDateBuff: {
              title: i18nExpression('purchaseDemand.requirementDate1'), // 要求到货日期
              'x-render-table-column': {
                minWidth: 230
              },
              ...yearMonthDaySelectorSegment,
              'x-read-pretty': '{{$form.readPretty}}'
            },
            ceeaIfSupplierConfirm: {
              type: 'string',
              title: i18nExpression('purchaseDemand.ceeaIfSupplierConfirm'), // 供方确认
              'x-render-table-column': {
                minWidth: 100
              },
              'x-component': 'Checkbox',
              'x-component-props': {
                'true-label': 'Y',
                'false-label': 'N'
              },
              'x-read-pretty': '{{$form.readPretty}}'
            },
            vendorName: {
              type: 'string',
              title: i18nExpression('purchaseDemand.vendorName'), // 供应商名称
              'x-render-table-column': {
                minWidth: 100
              }
            },
            ceeaPurchaseType: {
              type: 'string',
              title: i18nExpression('purchaseDemand.purchaseType'), // 订单类型
              'x-render-table-column': {
                minWidth: 100
              },
              'x-component': 'DictSelect',
              'x-component-props': {
                code: 'ORDER_TYPE'
              }
            },
            ceeaExecutedQuantity: {
              type: 'string',
              title: i18nExpression('purchaseDemand.ceeaExecutedQuantity'), // 已下单数量
              'x-render-table-column': {
                minWidth: 100
              }
            },
            taxPrice: {
              type: 'string',
              title: i18nExpression('purchaseDemand.taxPrice'), // 含税单价
              'x-render-table-column': {
                minWidth: 100
              }
            },
            isladderPriceFlag: {
              type: 'string',
              'x-component': 'TableButton',
              'x-component-props': {
                type: 'text',
                disabled: expression('$self.value !== \'是\''),
                '@click': expression('({row}) => $openLadderPriceDialog($form,$table.getRowByIndex($self.index))')
              },
              'x-render-table-column': {
                title: i18nExpression('bidMod.isLadders'), // 是否阶梯价
                minWidth: 120,
                customRender: true
              },
              'x-reactions': expression(`(field) => {
                $self.value = $table.getRowByIndex($self.index)?.ladderPriceFlag === 'Y' ? '是'  : '否'
              }`),
              'x-read-pretty': '{{$form.readPretty}}'
            },
            requirementHeadNum: {
              type: 'string',
              title: i18nExpression('purchaseDemand.requirementHeadNum'), // 申请编号
              'x-render-table-column': {
                minWidth: 100
              }
            },
            orgName: {
              type: 'string',
              title: i18nExpression('purchaseDemand.businessEntity'), // 业务实体
              'x-render-table-column': {
                minWidth: 100
              }
            },
            receiveAddress: {
              type: 'string',
              title: i18nExpression('purchaseDemand.ceeaDeliveryPlaceOut'), // 收货地址
              'x-render-table-column': {
                minWidth: 100
              }
            },
            // ceeaProjectName: {
            //   type: 'string',
            //   title: i18nExpression('purchaseDemand.purchaseItem'), // 采购项目
            //   'x-render-table-column': {
            //     minWidth: 100
            //   }
            // },
            // alreadyQuota: {
            //   type: 'string',
            //   title: i18nExpression('purchaseDemand.alreadyQuota'), // 已分配配额比
            //   'x-render-table-column': {
            //     minWidth: 100
            //   }
            // },
            vendorCode: {
              type: 'string',
              title: i18nExpression('purchaseDemand.vendorCode'), // 供应商编码
              'x-render-table-column': {
                minWidth: 100
              }
            },
            categoryName: {
              type: 'string',
              title: i18nExpression('purchaseDemand.materialCateSub'), // 物料小类
              'x-render-table-column': {
                minWidth: 100
              }
            },
            rowNum: {
              type: 'string',
              title: i18nExpression('purchaseDemand.rowNum'), // 申请行号
              'x-render-table-column': {
                minWidth: 100
              }
            },
            taxRate: {
              type: 'string',
              title: i18nExpression('purchaseDemand.taxRate'), // 税率
              'x-render-table-column': {
                minWidth: 100
              }
            },
            ceeaPriceSourceType: {
              type: 'string',
              title: i18nExpression('purchaseDemand.ceeaPriceSourceType'), // 价格来源
              'x-render-table-column': {
                minWidth: 100
              },
              'x-component': 'DictSelect',
              'x-component-props': {
                code: 'PRICE_SOURCE'
              }
            },
            contractNo: {
              type: 'string',
              title: i18nExpression('demandPoolManagement.contractNo'), // 合同编号
              'x-render-table-column': {
                minWidth: 100
              }
            },
            currencyName: {
              type: 'string',
              title: i18nExpression('purchaseDemand.currency'), // 币种
              'x-render-table-column': {
                minWidth: 100
              }
            },
            // alreadyNum: {
            //   type: 'string',
            //   title: i18nExpression('purchaseDemand.alreadyNum'), // 已分配数量
            //   'x-render-table-column': {
            //     minWidth: 100
            //   }
            // },
            // totalDistribution: {
            //   type: 'string',
            //   title: i18nExpression('purchaseDemand.totalDistribution'), // 本次分配总量
            //   'x-render-table-column': {
            //     minWidth: 100
            //   }
            // },
            // afterQuota: {
            //   type: 'string',
            //   title: i18nExpression('purchaseDemand.afterQuota'), // 分配后配额
            //   'x-render-table-column': {
            //     minWidth: 100
            //   }
            // },
            comments: {
              type: 'string',
              title: i18nExpression('purchaseDemand.purchaseRequisitionLineRemarks'), // 采购申请行备注
              'x-render-table-column': {
                minWidth: 100
              },
              'x-read-pretty': '{{$form.readPretty}}'
            }

          })
        }
      }
    }

  }
}
export {
  createOrderFetch,
  setQuantity,
  setPortation,
  openLadderPriceDialog,
  supplierConfirmSlot
}
