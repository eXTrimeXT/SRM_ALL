<script setup lang="ts">
// @ts-ignore
import {
  defineSchemas,
  expression,
  i18nExpression,
  generateXindexInOrder,
  connect
} from '@meicloud/render-engine'
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
// @ts-ignore
import editEngine from './edit-engine'
// @ts-ignore
import { parseTime } from '@/utils'
// @ts-ignore
import { getCountDown } from 'lib@/utils/date-format'
// @ts-ignore
import { getImgSrc } from 'lib@/utils/file'
import { getFileUrl } from '@/library/utils/file'
// @ts-ignore
import leftNaviBar from 'lib@/components/leftNaviBar'
// @ts-ignore
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import { shoppingCartApi } from 'modcb@/oneStopShopping/api'
// @ts-ignore
import listShowCard from './components/listShowCard'
// @ts-ignore
import CPagination from 'lib@/components/c-pagination'
const { emitTabAdd, t, app } = usePageHelper()

const { globalNickname } = app.$store.getters.userInfo

const itemDetail = (row: any, addressList: Array, queryForm: any) => {
  emitTabAdd({
    component: editEngine,
    params: {
      flag: 'view',
      row,
      addressList,
      queryForm,
      tabName: 'purchaseCatalogDetail' + row.catalogOnShelvesId
    },
    title: t('oneStopShopping.purchaseCatalogDetail') + row.materialName,
    name: 'purchaseCatalogDetail' + row.catalogOnShelvesId
  })
}

// 切换列表样式或者卡片样式
const toggleList = async ($form: any, $queryEngine: any) => {
  const _data = $form.query('CatalogOnShelves').get('data')
  // 切换图片、切换每页多少条数据
  _data.defaultLogo = _data.listShowType === 'card' ? _data.smallLogo : _data.bigLogo
  // $form.query('CPagination').get('componentProps').pageSize = _data.listShowType === 'card' ? 15 : 3

  let pageProps = $form.query('CPagination').get('componentProps')
  if (_data.listShowType === 'card') {
    pageProps.pageSize = 15
    pageProps.pageSizes = [15, 30, 60, 120, 300, 600, 1000, 1500]
  } else {
    pageProps.pageSize = 4
    pageProps.pageSizes = [4, 8, 16, 32, 64, 128, 256, 512, 1024]
  }

  await $queryEngine.request.paginationQuery()

  // 重新请求数据之后再切换卡片，避免大图小路切换闪烁
  if (_data.listShowType === 'card') {
    _data.listShowType = 'list'
  } else {
    _data.listShowType = 'card'
  }

  $form.query('toggleIcon').take((field: any) => {
    field.visible = false
    setTimeout(() => {
      field.visible = true
    })
  })
}

// 加入购物车
const addShoppingCart = (data: any, $queryEngine: any, $form: any) => {
  const queryForm = $form.query('query').take().value
  const {
    extAddressId,
    extAddressName,
    extAreaCode
  } = queryForm
  /* 获取收货地址的收货人以及收货人联系方式 */
  const addressList = $form.query('CatalogOnShelves').get('data').addressList
  const { receiverPhone, receiver } = addressList.find(item => item.value === extAddressId)
  const {
    catalogOnShelvesId,
    requirementNum,
    orderQuantityMinimum
  } = data
  if (Number(orderQuantityMinimum) > Number(requirementNum)) {
    app.$message.warning(t('cusEntry.tipMessage.requirementNumLessThanOrderQuantityMinimum'))
    return false
  }
  $queryEngine.request.baseRequest({
    action: 'addToShoppingCart',
    payload: [{
      catalogOnShelvesId: catalogOnShelvesId,
      requirementNum: requirementNum,
      extAddressId,
      extAddressName,
      extAreaCode,
      extReceiver: receiver,
      extReceiverContact: receiverPhone
    }],
    query: { '*': {}, catalogOnShelvesAttaches: { '*': {} } }
  }).then(() => {
    app.$message.success(t('common.success'))
    $form.query('.query').take().invoke('query')
  })
}

// 获取图片
const formatImgUrl = (id: string, $form: any) => {
  if (id) {
    return getImgSrc(id)
  } else {
    return getFileUrl($form.query('CatalogOnShelves').get('data').defaultLogo)
  }
}

// 数据处理
const adaptData = (data: any, $form: any) => {
  return data.map((item: any) => {
    let remainingTime = getCountDown(item.extExpirationDate) // 获取倒计时时间
    let imgUrl = formatImgUrl(item.fileuploadId, $form) // 获取图片信息链接
    return {
      ...item,
      remainingTime: remainingTime, // 剩余时间
      imgUrl: imgUrl // 图片链接
    }
  })
}

// 选择页码
const handlePageNumChange = (num: number, $form: any, $queryEngine: any) => {
  $form.query('CPagination').get('componentProps').pageNum = num
  $form.query('.query').take().invoke('query')
}

// 选择每页多少条
const handleSizeChange = (size: number, $form: any, $queryEngine: any) => {
  $form.query('CPagination').get('componentProps').pageSize = size
  $form.query('.query').take().invoke('query')
}

// 切换列表图标
const toggleSlot = ($form: any, $queryEngine: any) => {
  const data = $form.query('CatalogOnShelves').get('data')
  const classAttrs = [
    'toggle-list',
    'iconfont',
    data.listShowType === 'card' ? 'iconunorderedList' : 'icontupianliebiao'
  ]
  return {
    functional: true,
    render: (h: any) => {
      return h('div',
        {
          attrs: {
            class: 'header-btn'
          }
        },
        [
          h('em', {
            attrs: {
              class: 'iconfont icongouwuche shopping-cart'
            },
            on: {
              click: () => {
                app.$router.push('shoppingCart')
              }
            }
          }),
          h('em', {
            attrs: {
              class: classAttrs.join(' ')
            },
            on: {
              click: () => {
                toggleList($form, $queryEngine)
              }
            }
          })
        ]
      )
    }
  }
}
// 路径可点击
const routeClick = ($form: any, $queryEngine: any) => {
  const data = $form.query('CatalogOnShelves').get('data')
  return {
    functional: true,
    render: (h: any) => {
      let temp = []
      let navText = data.navText.split('/')
      data.navText.split('/').forEach((e, i) => {
        if (i == 0) {
          temp.push(h('div', {
            on: {
              click: () => {
                data.navText.split('/').forEach((v, i) => {
                  if (v == e) {
                    navText.splice(i + 1)
                  }
                })
                console.log(navText)
                $form.query('CatalogOnShelves').get('data').navText = navText.join('/')
                $form.query('CatalogOnShelves').get('data').categoryId = ''
                $form.query('CatalogOnShelves.rightContainer.query').take(field => {
                  field.invoke('query')
                })
              }
            }
          }, e))
        } else {
          temp.push(h('div', {
            on: {
              click: () => {
                data.navText.split('/').forEach((v, i) => {
                  if (v == e) {
                    navText.splice(i + 1)
                  }
                })
                console.log(navText)
                $form.query('CatalogOnShelves').get('data').navText = navText.join('/')
                $form.query('CatalogOnShelves').get('data').categoryId = data.struct.split('-')[i - 1]
                $form.query('CatalogOnShelves.rightContainer.query').take(field => {
                  field.invoke('query')
                })
              }
            }
          }, `/ ${e}`))
        }
      })
      return h('div',
        {
          attrs: {
            class: 'nav-text'
          }
        },
        temp
      )
    }
  }
}
// 列表模式图片展示插槽
const smallImgSlot = ($form: any, row: any) => {
  const data = $form.query('CatalogOnShelves').get('data')
  return {
    functional: true,
    render: (h: any) => {
      return h('div',
        {
          attrs: {
            class: 'table-img'
          }
        },
        [
          h('div',
            {
              attrs: {
                class: 'logo-small'
              },
              style: {
                padding: row.fileuploadId ? 0 : '0 4px',
                backgroundColor: row.fileuploadId ? 'none' : '#75C8FF',
                display: data.listShowType === 'list' ? 'block' : 'none'
              }
            },
            [
              h('img',
                {
                  attrs: {
                    width: '100%',
                    src: row.imgUrl,
                    alt: row.materialName
                  },
                  on: {
                    click: () => itemDetail(row, $form.query('CatalogOnShelves').get('data').addressList, $form.query('query').take().value)
                  }
                }
              )
            ]
          )
        ]
      )
    }
  }
}

const queryCategory = (val, $queryEngine, $form) => {
  $form.query('CatalogOnShelves').get('data').navText = i18nExpression('common.all')   // '全部'
  $form.query('CatalogOnShelves').get('data').struct = val.struct
  $form.query('CatalogOnShelves').get('data').categoryId = val.categoryId + ''
  $form.query('CatalogOnShelves').get('data').navText += `/${val.categoryFullName.split('-').join('/')}`
  $form.query('CatalogOnShelves.rightContainer.query').take(field => {
    field.invoke('query')
  })
}
const scope = {
  $t: t,
  app,
  parseTime,
  toggleSlot,
  routeClick,
  globalNickname,
  emitTabAdd,
  itemDetail,
  addShoppingCart,
  smallImgSlot,
  adaptData,
  getCountDown,
  handlePageNumChange,
  handleSizeChange,
  queryCategory,
  getFileUrl,
  getImgSrc,
  shoppingCartApi
}

const components = {
  editEngine,
  listShowCard: connect(listShowCard),
  CPagination: connect(CPagination),
  leftNaviBar: connect(leftNaviBar)
}

const schema = defineSchemas({
  CatalogOnShelves: {
    type: 'void',
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        paginationQuery: {
          action: 'listPageForPurchaseCatalog',
          loading: true,
          immediate: true,
          ready: expression(`async () => {
            /* 获取业务实体和默认部门 */
            const userId = app.$store.getters.userInfo.userId
            const respone = await shoppingCartApi.getUserOrgAndDept(userId)
            if (respone) {
              const {
                ouOrganization,
                departmentOrganization
              } = respone.data
              /* 获取收货地址 */
              if (departmentOrganization?.organizationId || ouOrganization?.organizationId) {
                const res = await shoppingCartApi.getDeptAddress(departmentOrganization?.organizationId || ouOrganization?.organizationId)
                /* 获取默认收货地址编码 */
                $form.query('CatalogOnShelves').get('data').defaultAddress= res.data.find(item => item.isDefault === 'Y') || {}
                $form.query('CatalogOnShelves').get('data').addressList = res.data.map(item => ({
                  id: item.siteId,
                  label: item.siteName,
                  code: item.siteDesc,
                  value: item.siteId,
                  areaCode: item.addressRegion,
                  receiver: item.receiver,
                  receiverPhone: item.receiverPhone
                }))
              }
            }
            return true
          }`),
          transformRequest: expression(`(data, headers) => {
            let schemaData = $form.query("CatalogOnShelves").get("data")
            data.query = {'*': {}}
            const pageInfoProps = $form.query('CPagination').get('componentProps')
            data.payload.page.pageNum = pageInfoProps.pageNum
            data.payload.page.pageSize = pageInfoProps.pageSize
            const categoryId = schemaData.categoryId
            const {
              siteId,
              siteName,
              addressRegion
            } = schemaData.defaultAddress || {}
            if (schemaData.initQuery) {
              data.payload.filter = {
                extAddressId: {
                  eq: siteId
                },
                extAddressName: {
                  eq: siteName
                },
                extAreaCode: {
                  contains: addressRegion
                }
              }
              schemaData.initQuery = false
            }
            if (categoryId) {
              if (data.payload.filter) {
                data.payload.filter.struct = {contains: categoryId}
              } else {
                data.payload.filter = {struct: {contains: categoryId}}
              }
            }
            return data
          }`),
          onSuccess: expression(`async (res) => {
            let response = []
            let materialIds = res.data.map(item => item.materialId)
            if (materialIds.length > 0) {
              response = await app.$http({
                url: '/api-base/material/materialItem/ext/multilingual',
                method: 'POST',
                data: { materialIds, language: app.$i18n.locale },
                loading: true
              })
            }
            const resData = res.data.map(item => {
              const data = response.data.find(it => it.materialId === item.materialId)
              item.materialNameShow = data?.materialName
              item.specificationShow = data?.extMaterialModel
              return item
            })

            const thatData = $form.query('CatalogOnShelves').get('data')
            const list = adaptData(resData, $form)
            thatData.itemResData = list
            setTimeout(() => {
              $form.values.table = list
            })
            $form.query('CPagination').get('componentProps').total = res.originalData.payload.total
          }`)
        }
      }
    },
    'x-component': 'QueryEngine',
    'x-decorator': 'el-container',
    'x-decorator-props': {
      class: 'purchase-catalog-list-page',
      style: {
        'padding-left': '0 !important'
      }
    },
    'x-data': {
      listShowType: 'card',
      smallLogo: 'images/gwn.png',
      bigLogo: 'images/gwn.png',
      defaultLogo: 'images/gwn.png',
      itemResData: [],
      addressList: [],
      defaultAddress: {},
      navText: i18nExpression('common.all'),  // '全部'
      categoryId: '',
      struct: '',
      initQuery: true
    },
    properties: {
      // 事件总线
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'purchaseCatalog',
          '@listener': expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)
        }
      },
      // leftContainer: {
      //   type: 'void',
      //   'x-decorator': 'el-aside',
      //   'x-decorator-props': {
      //     width: '195px',
      //     class: 'purchaseCatalogAside',
      //     style: {
      //       'background-color': '#fff',
      //       'padding': '0',
      //       'margin': '0px',
      //       'overflow': 'visible',
      //       'position': 'relative'
      //     }
      //   },
      //   properties: {
      //     leftNaviBar: {
      //       type: 'void',
      //       'x-component': 'leftNaviBar',
      //       'x-component-props': {
      //         '@send-id': '{{(val) => queryCategory(val, $queryEngine, $form)}}'
      //       }
      //     }
      //   }
      // },
      rightContainer: {
        type: 'void',
        'x-component': 'el-container',
        'x-component-props': {
          class: 'flex-container purchase-catalog-engine',
          direction: 'vertical'
        },
        properties: {
          // status: {
          //   type: 'void',
          //   'x-query-engine-skip': true,
          //   'x-content': {
          //     default: '{{routeClick($form, $queryEngine)}}'
          //   }
          // },
          query: {
            type: 'object',
            'x-query-engine-skip': true,
            'x-component': 'QueryFormByQueryEngine',
            // 'x-component-props': {
            //   '@reset': expression(`() => {
            //     $form.query("CatalogOnShelves").get("data").navText = '全部'
            //     $form.query("CatalogOnShelves").get("data").categoryId = ''
            //     $form.query("CatalogOnShelves").get("data").struct = ''
            //   }`)
            // },
            properties: generateXindexInOrder({
              extAddressId: {
                type: 'string',
                title: i18nExpression('cusEntry.sup.extAddress'),
                'x-component': 'Select',
                'x-component-props': {
                  '@change': expression(`value => {
                    /* 获取收货地址 */
                    const addressList = $form.query('CatalogOnShelves').get('data').addressList
                    const addressItem = addressList.find(item => item.value === value)
                    const queryForm = $self.query($self.parent.address).take().value
                    queryForm.extAreaCode = addressItem.areaCode
                    queryForm.extAddressCode = addressItem.code
                    queryForm.extAddressName = addressItem.label
                    $self.query($self.parent.address).take().invoke('query')
                  }`)
                },
                default: '{{$form.query(\'CatalogOnShelves\').get(\'data\').defaultAddress?.siteId}}',
                enum: expression('$form.query(\'CatalogOnShelves\').get(\'data\').addressList')
              },
              extAddressCode: {
                type: 'string',
                'x-hidden': true,
                default: '{{$form.query(\'CatalogOnShelves\').get(\'data\').defaultAddress?.siteDesc}}'
              },
              extAddressName: {
                type: 'string',
                'x-hidden': true,
                default: '{{$form.query(\'CatalogOnShelves\').get(\'data\').defaultAddress?.siteName}}'
              },
              // 物料名称
              // materialId: {
              //   type: 'string',
              //   title: i18nExpression('common.materialName'),
              //   'x-component': 'QuickSearchWrapper',
              //   'x-component-props': {
              //     showKey: 'materialName',
              //     propKey: 'materialId',
              //     name: 'scc_base_material_item_contract'
              //   }
              // },
              materialName: {
                type: 'string',
                title: i18nExpression('common.materialName'),
                'x-query-engine-query-operator': 'contains'
              },
              materialCode: {
                type: 'string',
                title: i18nExpression('common.materialCode'),
                'x-query-engine-query-operator': 'contains'
              },
              extAreaCode: {
                type: 'string',
                title: i18nExpression('cusEntry.sup.area'),
                'x-component': 'DictSelect',
                'x-component-props': {
                  code: 'REGION',
                  disabled: true
                },
                default: '{{$form.query(\'CatalogOnShelves\').get(\'data\').defaultAddress?.addressRegion}}',
                'x-query-engine-query-operator': 'contains'
              },
              // 品类名称
              categoryName: {
                type: 'string',
                title: i18nExpression('cusEntry.common.categoryName'),
                'x-component': 'CCategorySelect',
                'x-component-props': {
                  showKey: 'categoryName'
                }
              },
              specification: {
                type: 'string',
                title: i18nExpression('cusEntry.common.specification'),
                'x-query-engine-query-operator': 'contains'
              }
            })
          },
          // 切换卡片或者列表icon
          toggleIcon: {
            type: 'object',
            'x-visible': true,
            'x-content': {
              default: '{{toggleSlot($form, $queryEngine)}}'
            }
          },
          table: {
            type: 'array',
            'x-hidden': '{{$form.query("CatalogOnShelves").get("data").listShowType !== "list"}}',
            'x-component': 'NormalRenderTable',
            'x-component-props': {
              openCustomTable: false,
              pagination: false,
              editMode: true
            },
            properties: generateXindexInOrder({
              catalogOnShelvesId: {
                // 主键ID
                type: 'string',
                'x-hidden': true
              },
              lastUpdateDate: {
                type: 'string',
                'x-hidden': true,
                'x-query-engine-sort': 'desc'
              },
              // 图片
              imgSmall: {
                type: 'object',
                'x-query-engine-skip': true,
                'x-content': '{{smallImgSlot($form, $table.getRowByIndex($self.index))}}',
                'x-read-pretty': false,
                'x-render-table-column': {
                  minWidth: 90,
                  sortable: false,
                  'show-overflow': false
                }
              },
              // 物料编码
              materialCode: {
                type: 'string',
                title: i18nExpression('common.materialCode'),
                'x-component-props': {
                  '@click': expression(`({ row }) => {
                    itemDetail(row, $form.query('CatalogOnShelves').get('data').addressList, $form.query('query').take().value)
                  }`)
                },
                'x-render-table-column': {
                  minWidth: 100
                },
                'x-read-pretty': true
              },
              // 物料名称
              materialName: {
                type: 'string',
                title: i18nExpression('common.materialName'),
                'x-render-table-column': {
                  minWidth: 100
                },
                'x-hidden': true
              },
              // 物料名称
              materialNameShow: {
                type: 'string',
                title: i18nExpression('common.materialName'),
                'x-render-table-column': {
                  minWidth: 100
                },
                'x-read-pretty': true
              },
              // 型号规格
              specification: {
                type: 'string',
                title: i18nExpression('materialMainData.specification'),
                'x-render-table-column': {
                  minWidth: 100
                },
                'x-hidden': true
              },
              // 型号规格
              specificationShow: {
                type: 'string',
                title: i18nExpression('materialMainData.specification'),
                'x-render-table-column': {
                  minWidth: 100
                },
                'x-read-pretty': true
              },
              // 单位
              unit: {
                type: 'string',
                title: i18nExpression('materialMainData.unit'),
                'x-render-table-column': {
                  minWidth: 100
                },
                'x-read-pretty': true
              },
              // 起订量
              orderQuantityMinimum: {
                type: 'string',
                title: i18nExpression('cusEntry.sup.orderQuantityMinimum'),
                'x-render-table-column': {
                  minWidth: 100
                },
                'x-read-pretty': true
              },
              // 供应商
              // vendorName: {
              //   type: 'string',
              //   title: i18nExpression('common.vendor'),
              //   'x-render-table-column': {
              //     minWidth: 100
              //   }
              // },
              // 含税单价
              extReferencePrice: {
                type: 'string',
                'x-component': 'p',
                'x-content': '{{"₽ " + $self.value}}',
                title: i18nExpression('purchaseDemand.taxPrice'),
                'x-component-props': {
                  style: 'color: #FF4A4D;'
                },
                'x-render-table-column': {
                  minWidth: 100
                },
                'x-read-pretty': true
              },
              // 距离下架
              remainingTime: {
                type: 'string',
                title: i18nExpression('oneStopShopping.distanceShelf'),
                'x-reactions': expression(`() => {
                  let row = $table.getRowByIndex($self.index)
                  if (row && row.extExpirationDate) {
                    $self.value = getCountDown(row.extExpirationDate)
                  }
                }`),
                'x-component-props': {
                  style: 'color: #FF4A4D;'
                },
                'x-render-table-column': {
                  minWidth: 100
                },
                'x-read-pretty': true
              },
              // 数量
              requirementNum: {
                type: 'number',
                title: i18nExpression('cusEntry.sup.requirementNum'),
                'x-render-table-column': {
                  minWidth: 100
                },
                'x-component-props': {
                  min: 0,
                  '@change': expression(`(value) => {
                      setTimeout(() => {
                        const ifPrecision = value?.toString().includes('.')
                        if (ifPrecision) {
                          /* 获取小数点 */
                          const [integer, precision] = value?.toString().split('.')
                          $self.value = Number(integer + '.' +  precision.toString().slice(0, 4))
                        }
                      })
                  }`)
                }
              },
              operation: {
                type: 'void',
                title: i18nExpression('common.operation'),
                'x-component': 'Space',
                'x-render-table-column': {
                  minWidth: 130,
                  fixed: 'right'
                },
                properties: {
                  // 加如购物车
                  addShoppingCart: {
                    type: 'void',
                    'x-component': 'AuthorityButton',
                    'x-content': i18nExpression('common.addShoppingCart'),
                    'x-component-props': {
                      type: 'text',
                      '@click': expression(`() => {
                        addShoppingCart($table.getRowByIndex($self.index), $queryEngine, $form)
                      }`)
                    }
                  }
                }
              }
            })
          },
          listShowCard: {
            type: 'void',
            'x-component': 'listShowCard',
            'x-hidden': '{{$form.query("CatalogOnShelves").get("data").listShowType !== "card"}}',
            'x-component-props': {
              class: 'purchaseCatalogListMain',
              itemResData: '{{$form.query("CatalogOnShelves").get("data").itemResData}}',
              '@itemDetail': '{{(row) => itemDetail(row, $form.query(\'CatalogOnShelves\').get(\'data\').addressList, $form.query(\'query\').take().value)}}',
              '@addShoppingCart': '{{(data) => addShoppingCart(data, $queryEngine, $form)}}'
            }
          },
          CPagination: {
            type: 'object',
            'x-component': 'CPagination',
            'x-component-props': {
              style: 'padding:16px 0 4px 0;',
              total: 0,
              pageNum: 1,
              pageSize: 4,
              pageSizes: [4, 8, 16, 32, 64, 128, 256, 512, 1024],
              '@current-change': '{{(pageNum) => handlePageNumChange(pageNum, $form, $queryEngine)}}',
              '@size-change': '{{(pageSize) => handleSizeChange(pageSize, $form, $queryEngine)}}'
            }
          }
        }
      }
    }
  }
})
</script>

<template>
  <RenderEngine :scope="scope" :components="components" :schema="schema" schemaKey="CatalogOnShelves" />
</template>

<style lang="scss">
.purchase-catalog-list-page{
  position: relative;
}
.purchase-catalog-engine {
  position: relative;
  // padding-left: 16px;
  &:before{
    content: " ";
    position: absolute;
    left: 0px;
    top: -16px;
    bottom: -12px;
    // border-left: 1px solid #DCDDDE;
    background: #DCDDDE;
  }
  .header-btn {
    padding: 0 0 12px 0;
    display: flex;
    justify-content: flex-end;
    align-items: center;
  }
  .toggle-list {
    font-size: 20px;
    width: 28px;
    height: 28px;
    line-height: 28px;
    text-align: center;
    border-radius: 2px;
    color: #979A9D;
    border: 1px solid #979A9D;
  }
  .shopping-cart {
    font-size: 20px;
    width: 30px;
    height: 30px;
    line-height: 30px;
    text-align: center;
    border-radius: 4px;
    color: #979A9D;
    border: 1px solid #979A9D;
    margin-right: 5px;
  }
  .shopping-cart:hover, .toggle-list:hover {
    color: blue;
    cursor: pointer;
  }
  .table-img {
    height: 104px;
    display: flex;
    align-items: center;
    justify-content: center;
    .logo-small {
      height: 94px;
      width: 94px;
      display: flex;
      justify-content: center;
      align-items: center;
      overflow: hidden;
      background-image: linear-gradient(to right, #95A5C9 , #8295BF);
    }
  }
  .purchaseCatalogListMain {
    overflow: hidden;
    border: 1px solid #dfe6ec;
  }
  .vxe-cell {
    max-height: fit-content !important;
  }
  .nav-text {
    font-family: PingFangSC-Regular;
    font-size: 12px;
    color: #96999C;
    line-height: 20px;
    font-weight: 400;
    margin-bottom: 16px;
    display: flex;
    &:hover {
      cursor: pointer;
    }
    &>div:after {
      content: ' ';
      white-space: pre;
    }
    &>div:last-child {
      color: #161C24;
    }
    &>div:hover {
      color: #0077FF;
    }
  }
}
</style>
