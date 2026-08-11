import {
  useField,
  useFieldSchema,
  Schema,
  uid,
  h,
  FieldDisplayTypes,
  RecursionField,
  useExpressionScope,
  deepShallowCompile,
  watchEffect,
} from '@meicloud/render-engine'
import type { ArrayFieldModel, FieldModel, FormPath } from '@meicloud/render-engine'
import { ArrayBase } from '@meicloud/render-pix'
import { Tooltip } from '@meicloud/element-ui'
import { type Ref, shallowRef, computed } from 'vue-demi'
import { Colgroup, Column, type Table } from 'vxe-table'
import type { ColumnOption, ColumnCellRenderParams } from 'vxe-table'
import { EditMode } from './useTableEdit'
import { getRowLocalKey, setRowLocalKey } from '../helper'
import { useSetupContext } from '@/library/components/composables/useSetupContext'

export const COLUMN_FIELD_KEY = 'x-render-table-column'

const TABLE_GROUP = 'RenderTable.Group'
const TABLE_COLUMN = 'RenderTable.Column'

const isContainerField = (com: string) => [TABLE_COLUMN, TABLE_GROUP].includes(com)
const isColumnField = (schema: Schema) =>
  isContainerField(schema['x-component']) || schema[COLUMN_FIELD_KEY] !== undefined

const isGroupColumn = (com: string) => com === TABLE_GROUP

const resolveRequiredFromValidator = (validator: Schema['x-validator']) =>
  Array.isArray(validator) ? validator.some(v => v.required) : validator?.required

type ResolveColumn = {
  schema: Schema
  display: FieldDisplayTypes
  required?: boolean
  index?: number
  onlyRenderProperties: boolean
  columnType: 'group' | 'column'
  columnProps: ColumnOption
  children: ResolveColumn[]
}

const getBaseScope = (arrayField: ArrayFieldModel, scope = {}) => ({
  $form: arrayField.form,
  $self: arrayField,
  $values: arrayField.form.values,
  $props: (props: any) => arrayField.setComponentProps(props),
  ...scope,
})

export const useColumns = ({
  sortable,
  performanceMode,
  editModeRef,
  customTableHeaderConfigRef,
}: {
  sortable: boolean
  performanceMode?: boolean
  editModeRef: Ref<EditMode | undefined>
  customTableHeaderConfigRef: Ref<Record<string, any>>
}) => {
  const arrayFieldRef = useField<ArrayFieldModel>()
  const schemaRef = useFieldSchema() as unknown as Ref<Schema>
  const scopeRef = useExpressionScope()

  const rowKeyMap = new WeakMap()

  const cleanColumnsRef = shallowRef<ResolveColumn[]>([])
  let tempCleanColumns: ResolveColumn[] = []

  let cacheColumnsMap = new Map<string, ResolveColumn>()

  const { currentInstance } = useSetupContext()

  const vxeTableInstance = () =>
    (currentInstance.$refs?.vxeTable ?? {}) as unknown as Partial<typeof Table.prototype>

  const refreshColumns = useDebounceFn(() => {
    vxeTableInstance().refreshColumn?.()
  }, 116)

  const baseScope = () => getBaseScope(arrayFieldRef.value, scopeRef!.value)

  const shallowCompile = (str?: any) => Schema.shallowCompile(str, baseScope())

  const resolveColumns = (_schema: Schema, parentAddress: FormPath) =>
    _schema.reduceProperties((acc, schema, name) => {
      // 字段的显隐优先级，用户自定义的会比系统定义的高
      // @ts-ignore
      const customColumn = customTableHeaderConfigRef.value[schema.name]

      const onlyRenderProperties = isContainerField(schema['x-component'])

      const fieldPath = parentAddress.concat(name)
      const field = arrayFieldRef.value.form.query(fieldPath).take() as FieldModel

      const extraColumnProps = {
        ...(field?.componentProps ?? {}),
        ...((schema[COLUMN_FIELD_KEY] &&
          deepShallowCompile(schema[COLUMN_FIELD_KEY], baseScope())) ||
          {}),
      } as ColumnOption

      // 默认根据父节点的只读状态进行初始赋值 - 优先级更高
      // editMode=true 的时候就代表是自己接管渲染
      if (schemaRef.value['x-read-pretty'] || editModeRef.value !== true) {
        if (schema['x-read-pretty'] === undefined && !extraColumnProps.customRender) {
          if (onlyRenderProperties) {
            Schema.getOrderProperties(this).forEach(s => {
              if (s['x-read-pretty'] === undefined) {
                s['x-read-pretty'] = true
              }
            })
          }
          schema['x-read-pretty'] = true
        }
      }

      if (isColumnField(schema)) {
        const groupColumn = isGroupColumn(schema['x-component'])

        const display =
          shallowCompile(schema['x-display']) || shallowCompile(schema['x-hidden']) || 'visible'
        const col = {
          schema,
          display,
          index: customColumn?.['x-index'] ?? shallowCompile(schema?.['x-index']),
          onlyRenderProperties,
          columnType: groupColumn ? 'group' : 'column',
          columnProps: {
            visible: customColumn?.visible ?? display === 'visible',
            ...extraColumnProps,
            required:
              (extraColumnProps.asterisk || extraColumnProps?.required) ??
              field?.required ??
              shallowCompile(schema.required) ??
              resolveRequiredFromValidator(schema['x-validator']),
            title: extraColumnProps.title || field?.title,
            field: schema.name as unknown as string,
            width: customColumn?.width ?? extraColumnProps?.width,
            sortable: extraColumnProps.sortable ?? (schema.name === 'operation' ? false : sortable),
            fixed: customColumn?.fixed ?? extraColumnProps.fixed,
          },
          children: !groupColumn ? [] : resolveColumns(schema, fieldPath),
        } as ResolveColumn

        const tableColumn = vxeTableInstance().getColumnByField?.(col.columnProps.field!)
        if (tableColumn) {
          cacheColumnsMap.set(col.columnProps.field!, col)
          refreshColumns()
        }

        acc.push(col)

        if (!groupColumn) {
          tempCleanColumns.push(col)
        }
      }

      return acc
    }, [] as ResolveColumn[])

  const readyRef = ref(false)
  const columnsRef = shallowRef<ResolveColumn[]>(
    resolveColumns(schemaRef.value, arrayFieldRef.value.address),
  )

  const syncColumnsToFieldComponentPropsColumns = () => {
    arrayFieldRef.value.setData({
      columns: markRaw(
        tempCleanColumns
          .filter(column => column.columnProps.visible)
          .map(column => column.columnProps),
      ),
    })

    cleanColumnsRef.value = tempCleanColumns
  }

  syncColumnsToFieldComponentPropsColumns()

  onMounted(() => {
    nextTick(() => {
      readyRef.value = true
    })
  })

  watchEffect(() => {
    if (!readyRef.value) {
      return
    }

    tempCleanColumns = []
    // 找出 group/column 进行 state 渲染，用于执行 group/column 级别的显隐
    columnsRef.value = resolveColumns(schemaRef.value, arrayFieldRef.value.address).sort(
      (a, b) => (a.index ?? 9999) - (b.index ?? 9999),
    )

    nextTick(() => {
      syncColumnsToFieldComponentPropsColumns()
    })
  })

  const getRowKey = (record: any, index?: number) => {
    if (rowKeyMap instanceof WeakMap) {
      if (!getRowLocalKey(record)) {
        setRowLocalKey(record)
      }
      return `${rowKeyMap.get(record)}-${index}`
    }

    // @ts-ignore
    if (!rowKeyMap?.[index]) {
      // @ts-ignore
      rowKeyMap[index] = uid()
    }

    // @ts-ignore
    return `${rowKeyMap[index]}-${index}`
  }

  const renderColumnHeader = (column: ResolveColumn, cellParams: ColumnCellRenderParams) => {
    const innerColumn = (cacheColumnsMap.get(column.columnProps.field!) || column) as ResolveColumn

    return h(
      'div',
      {
        style: 'display: flex; align-items: center;',
        key: innerColumn.columnProps.field,
      },
      {
        default: () =>
          [
            innerColumn.columnProps.required &&
              h(
                'i',
                {
                  staticClass: 'vxe-cell--required-icon',
                  style: 'top: -2px; position: relative;',
                },
                {},
              ),
            innerColumn.columnProps.title,
            innerColumn.columnProps.description &&
              h(
                Tooltip,
                {
                  props: {
                    placement: 'top',
                  },
                },
                {
                  default: () =>
                    h(
                      'i',
                      {
                        staticClass:
                          (!!innerColumn.columnProps.icon
                            ? innerColumn.columnProps.icon
                            : 'el-icon-warning') + ' description-tips',
                        // attrs: {
                        //   class: innerColumn.columnProps.icon ?? 'el-icon-warning',
                        // },
                      },
                      {},
                    ),
                  content: () =>
                    h(
                      'div',
                      {},
                      {
                        default: () => innerColumn.columnProps.description,
                      },
                    ),
                },
              ),
          ].filter(Boolean),
      },
    )
  }

  const renderCell = (column: ResolveColumn, cellParams: ColumnCellRenderParams) => {
    // 内部自己维护的 key
    const currentRowKey = `${cellParams.column.field}_${getRowLocalKey(cellParams.row)}`

    if (performanceMode && column.columnProps.performanceMode !== false) {
      return h(
        'span',
        {
          key: currentRowKey,
        },
        {
          default: () => cellParams.row[cellParams.column.field],
        },
      )
    }

    return h(
      ArrayBase.Item,
      {
        props: { index: cellParams.$rowIndex, record: cellParams.row },
        key: currentRowKey,
      },
      {
        default: () =>
          h(
            RecursionField,
            {
              key: cellParams.rowIndex,
              props: {
                schema: column.schema,
                name: column.schema.name,
                basePath: arrayFieldRef.value.address.concat(cellParams.rowIndex),
              },
            },
            {},
          ),
      },
    )
  }

  const renderColumn = (column: ResolveColumn, index = 0): ReturnType<typeof h> => {
    const group = column.columnType === 'group'

    return h(
      group ? Colgroup : Column,
      { key: `${column.schema.name}_${column.index ?? index}`, props: column.columnProps },
      group
        ? {
            header: (props: ColumnCellRenderParams) => renderColumnHeader(column, props),
            default: () => column.children.map(col => renderColumn(col)),
          }
        : {
            header: (props: ColumnCellRenderParams) => renderColumnHeader(column, props),
            default: (props: ColumnCellRenderParams) => renderCell(column, props),
          },
    )
  }

  const renderColumns = computed(() => {
    return columnsRef.value
      .filter(column => column.columnProps.visible)
      .map((column, idx) => {
        return renderColumn(column, idx)
      })
  })

  const renderStateColumns = (
    columns: ResolveColumn[],
    basePath: FormPath,
  ): ReturnType<typeof h>[] => {
    return columns.reduce((acc, column, index) => {
      acc.push(
        h(
          RecursionField,
          {
            key: column.schema.name,
            props: {
              name: column.schema.name,
              basePath,
              schema: {
                ...column.schema,
                // 最小化
                type: 'void',
                'x-decorator': undefined,
                'x-decorator-props': undefined,
                'x-component': undefined,
                'x-component-props': undefined,
              },
              onlyRenderSelf: true,
            },
            index,
          },
          {},
        ),
      )

      if (column.children.length) {
        return acc.concat(
          renderStateColumns(
            column.children,
            basePath.concat(column.schema.name as unknown as string),
          ),
        )
      }

      return acc
    }, [] as ReturnType<typeof h>[])
  }

  const renderColumnsStateManager = computed(() => {
    return renderStateColumns(columnsRef.value, arrayFieldRef.value.address)
  })

  return {
    columnsRef,
    renderColumns,
    renderColumnsStateManager,
    cleanColumnsRef,
    rowKeyMap,
    getRowKey,
  }
}
