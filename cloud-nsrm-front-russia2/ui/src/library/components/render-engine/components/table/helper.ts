import { Schema, isObj, uid, FieldModel } from '@meicloud/render-engine'
import { Ref } from 'vue-demi'
import type { Column } from 'vxe-table'

export const COLUMN_FIELD_KEY = 'x-render-table-column'

const isColumnField = (schema: Schema) => schema[COLUMN_FIELD_KEY] !== undefined

const resolveRequiredFromValidator = (validator: Schema['x-validator']) =>
  Array.isArray(validator) ? validator.some(v => v.required) : validator?.required

type Value = { columnProps: Partial<Column>; index: number; schema: Schema }
export const parseTableColumnsFromSchema = ({
  arrayField,
  schema,
  scopeRef,
  customTableHeaderConfigRef,
  sortable,
  editModeRef,
}: {
  arrayField: FieldModel;
  schema: Schema;
  scopeRef: Ref<Record<string, any>>;
  customTableHeaderConfigRef: Ref<Record<string, any>>;
  sortable: boolean;
  editModeRef: Ref<any>;
}) => {
  return schema
    .reduceProperties<Value[], Value[]>((acc, currentFieldSchema) => {
      if (!isColumnField(currentFieldSchema)) {
        return acc
      }

      // 字段的显隐优先级，用户自定义的会比系统定义的低
      // @ts-ignore
      const customCurrentColumn = customTableHeaderConfigRef.value[currentFieldSchema.name]
      if (customCurrentColumn?.['x-visible'] === false) {
        return acc
      }

      // 动态切换
      // TODO 按需开启
      const field = arrayField.query(arrayField.address.concat(currentFieldSchema.name!)).take()
      const display = field?.display || schema['x-display'] || 'visible'

      const extraColumnProps = isObj(currentFieldSchema[COLUMN_FIELD_KEY])
        ? currentFieldSchema[COLUMN_FIELD_KEY]
        : {}

      // 默认根据父节点的只读状态进行初始赋值 - 优先级更高
      // editMode=true 的时候就代表是自己接管渲染
      if (schema['x-read-pretty'] || editModeRef.value !== true) {
        if (
          currentFieldSchema['x-read-pretty'] === undefined &&
          !currentFieldSchema[COLUMN_FIELD_KEY]?.customRender
        ) {
          currentFieldSchema['x-read-pretty'] = true
        }
      }

      return acc.concat({
        schema: currentFieldSchema,
        index: customCurrentColumn?.['x-index'] ?? currentFieldSchema?.['x-index'],
        display,
        required:
          extraColumnProps.asterisk ??
          currentFieldSchema.required ??
          resolveRequiredFromValidator(currentFieldSchema['x-validator']),
        columnProps: {
          field: currentFieldSchema.name as unknown as string,
          width: customCurrentColumn?.width ?? extraColumnProps?.width,

          lockLeft:customCurrentColumn?.lockLeft,
          lockRight:customCurrentColumn?.lockRight,
          fixed: customCurrentColumn?.lockLeft === 'Y' ? 'left' : customCurrentColumn?.lockRight === 'Y' ? 'right' : undefined,

          sortable: extraColumnProps.sortable ?? (
            currentFieldSchema.name === 'operation' ? false : sortable
          ),
          ...extraColumnProps,
          title: Schema.shallowCompile(
            extraColumnProps?.title ?? currentFieldSchema.title,
            scopeRef.value,
          ),
        },
      } as any)
    }, [])
    .sort((a, b) => a.index - b.index)
}

const rowLocalKeySymbol = Symbol('RenderTable#RowLocalKey')
export const setRowLocalKey = (obj: Record<string, any>) => ({
  ...obj,
  [rowLocalKeySymbol]: `render-table-${uid()}`,
})
export const getRowLocalKey = (obj: Record<string, any>) => obj?.[rowLocalKeySymbol]
