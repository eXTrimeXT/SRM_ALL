import './index.scss'

import { defineComponent, toRef, computed, onMounted } from 'vue-demi'
import {
  useField,
  useFieldSchema,
  ArrayFieldModel,
  h,
  useQueryEngine,
  useAutoMountInstanceToField,
  isObj,
  isStr,
  toJS,
  useCascadeDeletionForArray,
  computed as RnComputed,
  untracked,
} from '@meicloud/render-engine'
import { ArrayBase } from '@meicloud/render-pix'
import XEUtils from 'xe-utils'
import { Table } from 'vxe-table'
// @ts-ignore
import CustomTableHeader from './custom-table-header.vue'
import { RenderTablePagination } from './pagination'
import { setRowLocalKey, getRowLocalKey } from './helper'
import { usePreColumns } from './pre-columns'
import { useTableExpressionScopeProvide } from './composables/useExpressionScope'
import { useTableEdit, type EditMode } from './composables/useTableEdit'
import { useSetupContext } from '../../../composables/useSetupContext'
import emptyIcon from '@/assets/table/empty.svg'
import { useAutoRun } from '@/library/components/composables/useAutoRun'
import { useColumns } from './composables/useColumns'
import { Loading } from './loading'

type RenderTableProps = {
  editMode?: EditMode
  // checkbox, radio, seq 类似 element Pagination 的 layout 用法
  preColumns?: string
  sortable?: boolean
  performanceMode?: boolean
  openCustomTable?: boolean
  // 自定义参数
  pagination?: boolean | Record<string, any>
  dblclickEditable?: boolean
}

export const RenderTable = defineComponent<RenderTableProps>({
  name: 'RenderTable',
  inheritAttrs: false,
  props: {
    preColumns: String,
    // 性能模式，主要是关掉部分 field 的渲染
    performanceMode: {
      type: Boolean,
      default: false,
    },
    sortable: {
      type: Boolean,
      default: true,
    },
    editMode: {
      type: [Boolean, String],
      default: false,
    },
    openCustomTable: {
      type: Boolean,
      default: false,
    },
    pagination: {
      type: [Boolean, Object],
      default: true,
    },
    dblclickEditable: {
      type: Boolean,
      default: false,
    },
  },
  setup(props, { attrs, listeners, expose }) {
    const field = useField<ArrayFieldModel>()
    const fieldSchema = useFieldSchema()
    const tableName = fieldSchema.value.name

    const preColumnsRef = toRef<RenderTableProps, 'preColumns'>(props, 'preColumns')
    const preColumns = usePreColumns(preColumnsRef)

    const { currentInstance } = useSetupContext()

    useAutoMountInstanceToField()

    // @ts-ignore
    useCascadeDeletionForArray(attrs)

    // TODO 分离数据处理, 提到上层
    const queryEngine = useQueryEngine()

    const editModeRef = ref(props.editMode)

    const customTableHeaderConfigRef = shallowRef({} as Record<string, any>)

    const { renderColumns, renderColumnsStateManager, rowKeyMap, cleanColumnsRef } = useColumns({
      // @ts-ignore
      customTableHeaderConfigRef,
      sortable: props.sortable!,
      performanceMode: props.performanceMode,
      editModeRef,
    })

    const tableEdit = useTableEdit({
      columnsRef: cleanColumnsRef,
      editModeRef,
      refreshVxeTableData: () => {
        // const xTable = currentInstance?.$refs.vxeTable as Table | undefined
        // if (xTable) {
        //   // xTable.reloadData(field.value.value)
        // }
      },
    })

    const dataSource = shallowRef([] as any[])
    useAutoRun(() => {
      dataSource.value = field.value.value?.length ? field.value.value.slice() : []
    })

    onMounted(() => {
      if (field.value.initialValue?.length) {
        // loadDataToVxeTable(field.value.initialValue)
        field.value.setValue(toJS(field.value.initialValue))
      }
    })

    const loadDataToVxeTable = (dataSource = []) => {
      const xTable = currentInstance?.$refs.vxeTable as Table | undefined
      // TODO 优化数据同步，减少渲染引擎跟 vxe-table 的同步开销
      xTable?.loadData(Object.freeze(toJS(dataSource)))
    }

    const setTableDataSource = (dataSource: any[]) =>
      field.value.setValue(dataSource.map(row => (getRowLocalKey(row) ? row : setRowLocalKey(row))))

    useAutoRun(() => {
      const paginationData = queryEngine.state.paginationManagement.dataSource.value

      untracked(() => {
        if (queryEngine.state.paginationManagement.requestExecuted.value) {
          setTableDataSource(toJS(paginationData))
        }
      })
    })

    const getCheckboxRecords = (isFull = false) =>
      // @ts-ignore
      currentInstance.$refs.vxeTable.getCheckboxRecords(isFull)

    useTableExpressionScopeProvide({
      $table: {
        ...tableEdit,
        getCheckboxRecords,
        tableName,
        getRowByIndex: (index: number) => field.value.value[index],
        remove: (index: number) => field.value.remove(index),
        getComponentInstance: () => currentInstance,
        getVxeTableInstance: () => currentInstance?.$refs.vxeTable,
      },
    })

    const setCurrentRow = (row: Object) => {
      // @ts-ignore
      currentInstance.$refs.vxeTable.setCurrentRow(row)
    }

    expose({
      ...tableEdit,
      getCheckboxRecords,
      setTableDataSource,
      setCurrentRow,
    })

    const handleCustomTableHeaderChange = useDebounceFn((customTableHeaderConfig: any[]) => {
      customTableHeaderConfigRef.value = customTableHeaderConfig.reduce(
        (acc, item) => ({ ...acc, [item.field]: item }),
        {},
      )
    }, 116)

    // @ref https://github.com/x-extends/vxe-table/blob/cdbb059bb2070df1d585633d91b4ee696d760069/packages/table/src/table.ts#L1137
    const getOrderField = (column: VxeTableDefines.ColumnInfo) => {
      const { sortBy, sortType } = column
      return (row: any) => {
        let cellValue
        if (sortBy) {
          cellValue = XEUtils.isFunction(sortBy)
            ? sortBy({ row, column })
            : XEUtils.get(row, sortBy)
        } else {
          cellValue = row[column.field]
        }
        if (!sortType || sortType === 'auto') {
          return isNaN(cellValue) ? cellValue : XEUtils.toNumber(cellValue)
        } else if (sortType === 'number') {
          return XEUtils.toNumber(cellValue)
        } else if (sortType === 'string') {
          return XEUtils.toValueString(cellValue)
        }
        return cellValue
      }
    }

    // TODO 拓展远程排序
    const sortConfig = {
      trigger: 'cell',
      sortMethod: ({ sortList }) => {
        // @ref https://github.com/x-extends/vxe-table/blob/cdbb059bb2070df1d585633d91b4ee696d760069/packages/table/src/table.ts#L1326
        const orderTableData = XEUtils.orderBy(
          field.value.value,
          sortList.map(({ column, order }) => [getOrderField(column), order]),
        )

        field.value.value.forEach((_, index) => {
          field.value.value[index] = orderTableData[index]
        })
      },
    }

    const handleChangePageSize = (v: number) => {
      // 分页自定义，不走query接口
      if (isObj(props.pagination) && props.pagination?.customPage) {
        return listeners.handleChangePageSize(v)
      }
      queryEngine.state.paginationManagement.changePageSize(v)

      tableEdit.cancelAllEditRow()
    }

    const handleCurrentChange = (v: number) => {
      // 分页自定义，不走query接口
      if (isObj(props.pagination) && props.pagination?.customPage) {
        return listeners.handleCurrentChange(v)
      }
      queryEngine.state.paginationManagement.jumpPage(v)

      tableEdit.cancelAllEditRow()
    }

    // vxe-table 的 maxHeight 只支持 %、px 单位，这里我们支持一下 vh/vw
    const innerMaxHeight = computed(
      () =>
        (!attrs.maxHeight || !isStr(attrs.maxHeight)
          ? attrs.maxHeight
          : /vh$/.test(attrs.maxHeight)
          ? (window.innerHeight * parseFloat(attrs.maxHeight)) / 100
          : /vw$/.test(attrs.maxHeight)
          ? (window.innerWidth * parseFloat(attrs.maxHeight)) / 100
          : attrs.maxHeight) as any,
    )

    const loadingRef = RnComputed(() => queryEngine.state.paginationManagement.loading.value)

    return () => {
      const renderTable = (): ReturnType<typeof h> =>
        h(
          Table,
          {
            ref: 'vxeTable',
            props: {
              stripe: true,
              height: '100%',
              border: true,
              rowConfig: { isCurrent: true, isHover: true },
              columnConfig: { resizable: true },
              autoResize: true,
              syncResize: true,
              showOverflow: true,
              sortConfig,
              data: dataSource.value,
              loading: loadingRef.value,
              ...attrs,
              scrollY: {
                // 大数据模式下并且浏览器版本较低的话，可以设置大一点
                // TODO 实现内置，跟 render-engine 解耦，比如两边都维护一套数据
                oSize: 48,
                ...(attrs.scrollY ?? {}),
              },
              maxHeight: innerMaxHeight.value,
            },
            on: {
              ...listeners,
              'cell-dblclick': (row: any) => {
                if (props.dblclickEditable) {
                  tableEdit.editRowByIndex(row.rowIndex)
                }

                listeners['cell-dblclick']?.(row)
              },
            },
          },
          {
            default: () => preColumns.value.concat(renderColumns.value),
            loading: () => h(Loading, {}, {}),
            empty: () =>
              h(
                'span',
                {},
                {
                  default: () => [
                    h('img', { style: { 'margin-top': '16px' }, attrs: { src: emptyIcon } }, {}),
                    h(
                      'p',
                      { style: { color: '#96999C', margin: '0 0 16px 0' } },
                      { default: () => currentInstance.$t('common.noData') },
                    ),
                  ],
                },
              ),
          },
        )

      return h(
        'div',
        {
          staticClass: 'render-table',
        },
        {
          default: () =>
            h(
              ArrayBase,
              {
                props: {
                  keyMap: rowKeyMap,
                },
              },
              {
                default: () =>
                  [
                    props.openCustomTable &&
                      h(
                        CustomTableHeader,
                        {
                          staticClass: 'custom-table-header__button',
                          props: {
                            columns: cleanColumnsRef.value,
                            tableKey: fieldSchema.value.name,
                          },
                          on: {
                            change: handleCustomTableHeaderChange,
                          },
                        },
                        {},
                      ),
                    h(
                      'div',
                      {
                        staticClass: 'render-table__content',
                      },
                      {
                        default: () => [
                          h(
                            'div',
                            {
                              staticClass: 'render-table__table',
                            },
                            { default: renderTable },
                          ),
                          props.pagination &&
                            h(
                              RenderTablePagination,
                              {
                                props: {
                                  total:
                                    queryEngine.state.paginationManagement.configState.value.total,
                                  currentPage:
                                    queryEngine.state.paginationManagement.configState.value
                                      .pageNum,
                                  ...(isObj(props.pagination) ? props.pagination : {}),
                                },
                                on: {
                                  'current-change': handleCurrentChange,
                                  'size-change': handleChangePageSize,
                                },
                              },
                              {},
                            ),
                          renderColumnsStateManager.value,
                        ],
                      },
                    ),
                  ].filter(Boolean),
              },
            ),
        },
      )
    }
  },
})
