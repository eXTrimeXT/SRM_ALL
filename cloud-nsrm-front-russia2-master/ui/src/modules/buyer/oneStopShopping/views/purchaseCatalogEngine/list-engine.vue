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
// @ts-ignore
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import listShowCard from './components/listShowCard'
// @ts-ignore
import CPagination from 'lib@/components/c-pagination'

const { emitTabAdd, t, app } = usePageHelper()

const { globalNickname } = app.$store.getters.userInfo

const itemDetail = (row: any) => {
  emitTabAdd({
    component: editEngine,
    params: {
      flag: 'view',
      row,
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
const addShoppingCart = (data: any, $queryEngine: any) => {
  if (!data.unitCode || !data.categoryCode) {
    return app.$message.warning(t('oneStopShopping.addShoppingCartMsg1'))
  }
  if (!data.orgId || !data.organizationId) {
    return app.$message.warning(t('oneStopShopping.addShoppingCartMsg2'))
  }
  $queryEngine.request.baseRequest({
    action: 'addToShoppingCart',
    payload: [data.catalogOnShelvesId],
    query: { '*': {}, catalogOnShelvesAttaches: { '*': {} } }
  }).then(() => {
    app.$message.success(t('common.success'))
    $queryEngine.request.paginationQuery()
  })
}

// 获取图片
const formatImgUrl = (id: string, data: any) => {
  if (id) {
    return getImgSrc(id)
  } else {
    return data.defaultLogo
  }
}

// 数据处理
const adaptData = (data: any) => {
  return data.map((item: any) => {
    let remainingTime = getCountDown(item.expirationDate) // 获取倒计时时间
    let imgUrl = formatImgUrl(item.fileuploadId, data) // 获取图片信息链接
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
  $queryEngine.request.paginationQuery()
}

// 选择每页多少条
const handleSizeChange = (size: number, $form: any, $queryEngine: any) => {
  $form.query('CPagination').get('componentProps').pageSize = size
  $queryEngine.request.paginationQuery()
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
                    click: () => itemDetail(row)
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

const scope = {
  $t: t,
  app,
  parseTime,
  toggleSlot,
  globalNickname,
  emitTabAdd,
  itemDetail,
  addShoppingCart,
  smallImgSlot,
  adaptData,
  getCountDown,
  handlePageNumChange,
  handleSizeChange
}

const components = {
  editEngine,
  listShowCard: connect(listShowCard),
  CPagination: connect(CPagination)
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
          transformRequest: expression(`(data, headers) => {
            console.log('transformRequest=>', data, headers)
            data.query = {'*': {}}
            const pageInfoProps = $form.query('CPagination').get('componentProps')
            data.payload.page.pageNum = pageInfoProps.pageNum
            data.payload.page.pageSize = pageInfoProps.pageSize

            return data
          }`),
          onSuccess: expression(`(res) => {
            console.log('onSuccess=>', res)
            const thatData = $form.query('CatalogOnShelves').get('data')
            let list = adaptData(res.data, $form)
            thatData.itemResData = list
            $form.values.table = list

            $form.query('CPagination').get('componentProps').total = res.originalData.payload.total
          }`)
        }
      }
    },
    'x-component': 'QueryEngine',
    'x-decorator': 'el-container',
    'x-decorator-props': {
      class: 'flex-container purchase-catalog-engine',
      direction: 'vertical'
    },
    'x-data': {
      listShowType: 'card',
      smallLogo: '@/assets/images/catalogLogoSmall.png',
      bigLogo: '@/assets/images/catalogLogoBig.png',
      defaultLogo: '@/assets/images/catalogLogoBig.png',
      itemResData: []
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
      query: {
        type: 'object',
        'x-query-engine-skip': true,
        'x-component': 'QueryFormByQueryEngine',
        properties: generateXindexInOrder({
          // 业务实体
          orgId: {
            type: 'string',
            title: i18nExpression('dataConfMod.orgId'),
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'node-type': 'OU',
              'parent-id': -1,
              '@select': expression(`(node) => {
                if ($values.query.organizationId) {
                  $values.query.organizationId = null
                  $values.query.organizationCode = null
                  $values.query.organizationName = null
                }
              }`)
            }
          },
          // 库存组织
          organizationId: {
            type: 'string',
            title: i18nExpression('dataConfMod.organizationId'),
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'parent-id': '{{$form.values.query.orgId}}',
              'node-type': 'INV',
              'scope': '{{ $form.values.query }}'
            }
          },
          // 品类名称
          categoryId: {
            type: 'string',
            title: i18nExpression('common.categoryName'),
            'x-component': 'CCategorySelect',
            'x-component-props': {
              showKey: 'categoryId'
            }
          },
          // 物料名称
          materialId: {
            type: 'string',
            title: i18nExpression('common.materialName'),
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'materialName',
              propKey: 'materialId',
              name: 'scc_base_material_item'
            }
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
          '@checkbox-change': expression('(val) => handleCurrentChange(val, $form)'),
          pagination: false
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
                itemDetail(row)
              }`)
            },
            'x-render-table-column': {
              minWidth: 100
            }
          },
          // 物料名称
          materialName: {
            type: 'string',
            title: i18nExpression('common.materialName'),
            'x-render-table-column': {
              minWidth: 100
            }
          },
          // 型号规格
          specification: {
            type: 'string',
            title: i18nExpression('materialMainData.specification'),
            'x-render-table-column': {
              minWidth: 100
            }
          },
          // 单位
          unit: {
            type: 'string',
            title: i18nExpression('materialMainData.unit'),
            'x-render-table-column': {
              minWidth: 100
            }
          },
          // 供应商
          vendorName: {
            type: 'string',
            title: i18nExpression('common.vendor'),
            'x-render-table-column': {
              minWidth: 100
            }
          },
          // 含税单价
          taxPrice: {
            type: 'string',
            'x-component': 'p',
            'x-content': '{{"￥" + $self.value}}',
            title: i18nExpression('purchaseDemand.taxPrice'),
            'x-component-props': {
              style: 'color: #FF4A4D;'
            },
            'x-render-table-column': {
              minWidth: 100
            }
          },
          // 距离下架
          remainingTime: {
            type: 'string',
            title: i18nExpression('oneStopShopping.distanceShelf'),
            'x-reactions': expression(`() => {
              let row = $table.getRowByIndex($self.index)
              if (row && row.expirationDate) {
                $self.value = getCountDown(row.expirationDate)
              }
            }`),
            'x-component-props': {
              style: 'color: #FF4A4D;'
            },
            'x-render-table-column': {
              minWidth: 100
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
                    addShoppingCart($table.getRowByIndex($self.index), $queryEngine)
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
          '@itemDetail': '{{(row) => itemDetail(row)}}',
          '@addShoppingCart': '{{(data) => addShoppingCart(data, $queryEngine)}}'
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
})
</script>

<template>
  <RenderEngine :scope="scope" :components="components" :schema="schema" schemaKey="CatalogOnShelves" />
</template>

<style lang="scss">
.purchase-catalog-engine {
  .header-btn {
    padding: 0 0 12px 0;
    display: flex;
    justify-content: flex-end;
    align-items: center;
  }
  .toggle-list {
    font-size: 20px;
    width: 30px;
    height: 30px;
    line-height: 30px;
    text-align: center;
    border-radius: 4px;
    color: #979A9D;
    border: 1px solid #979A9D;
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
}
</style>
