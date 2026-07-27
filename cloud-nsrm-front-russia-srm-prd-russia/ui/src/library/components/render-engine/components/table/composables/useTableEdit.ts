// @ts-ignore
import { clone } from 'xe-utils'
import { type Ref, nextTick } from 'vue-demi'
import {
  observable,
  Schema,
  ArrayFieldModel,
  FieldModel,
  isNum,
  action,
  useField,
} from '@meicloud/render-engine'
import {
  parseTableColumnsFromSchema,
  COLUMN_FIELD_KEY,
  getRowLocalKey,
  setRowLocalKey,
} from '../helper'

export const SKIP_EDITABLE = 'skipEditable'

const TEMP_ADD_DATA_KEY = Symbol('__RENDER_TABLE_TEMP_ADD_DATA_KEY__')

const getDefaultValue = (schema: Schema): any => {
  if (schema.default !== undefined) {
    return schema.default
  }

  if (schema['x-component-props']?.defaultValue !== undefined) {
    return schema['x-component-props'].defaultValue
  }

  if (Array.isArray(schema?.items)) return getDefaultValue(schema.items[0])

  return (
    // @ts-ignore
    {
      array: [],
      boolean: true,
      date: '',
      datetime: '',
      number: 0,
      object: {},
      string: '',
      // @ts-ignore
    }[schema.type] ?? undefined
  )
}

/**
 * false 不编辑，默认不编辑
 * row 单行
 * cell 单元格
 * multi-row 多行
 */
export type EditMode = boolean | 'row' | 'cell' | 'multi-row'

type Params = {
  // eslint-disable-next-line no-undef
  columnsRef: Ref<ReturnType<typeof parseTableColumnsFromSchema>>
  editModeRef: Ref<EditMode>
  refreshVxeTableData: () => void
}

const getStoreTempEditDataMapKeyByField = (field: FieldModel) =>
  field.address.toArr().slice(-1)[0] ?? ''

// TODO 优化批量设置
// TODO 优化逻辑、复杂度
export const useTableEdit = ({ columnsRef, editModeRef, refreshVxeTableData }: Params) => {
  const tempEditDataMap = new Map<string, Map<string | number, any>>()
  const editRowLocalKeys = observable.ref<string[]>([])
  const arrayField = useField<ArrayFieldModel>()

  const parseColumnsFields = observable.computed(
    () =>
      columnsRef.value
        .filter(
          (col: any) =>
            !(
              col.schema['x-hidden'] === true ||
              !!col.schema[COLUMN_FIELD_KEY]?.customRender ||
              !!col.columnProps[SKIP_EDITABLE] ||
              col.schema.type === 'void'
            ),
        )
        .map((col: any) => col.columnProps.field) ?? [],
  )

  const queryFieldModelInstance = (index: number) => {
    return arrayField.value.form.query(
      arrayField.value.address
        // @ts-ignore
        .concat(`${index}.*(${parseColumnsFields.value.join(',')})`)
        .toString(),
    )
  }

  const fieldsEditableToggle = (
    index: number,
    editable: boolean,
    cb?: (field: FieldModel) => void,
  ) => {
    if (!parseColumnsFields.value?.length) {
      throw new Error('没有可供编辑的字段')
    }

    queryFieldModelInstance(index).forEach((field: FieldModel) => {
      field.editable = editable
      // field.setSelfErrors([])

      // @ts-ignore
      // eslint-disable-next-line no-unused-expressions
      cb?.(field)
    })
  }

  const isEditByRowIndex = (index: number) =>
    editRowLocalKeys.value.includes(getRowLocalKey(arrayField.value.value[index]))

  const changeTableEditable = () => {
    arrayField.value.editable = !!editRowLocalKeys.value.length
  }

  const editableRowDataByIndex = (index: number, editable = true) => {
    const dataMap = new Map()

    fieldsEditableToggle(index, editable, field => {
      // TODO 最小化的值收集
      // @ts-ignore
      dataMap.set(getStoreTempEditDataMapKeyByField(field), clone(field.value, true))
    })

    tempEditDataMap.set(getRowLocalKey(arrayField.value.value[index]), dataMap)

    changeTableEditable()
  }

  const isRowEdit = () => editModeRef.value === 'row'
  const isMultiRowEdit = () => editModeRef.value === 'multi-row'

  const canAddOrEditRow = () => isRowEdit() && editRowLocalKeys.value.length <= 0

  // TODO 最优的处理还是到 formily ArrayField 底层做
  let tempCacheErrorByRowLocalKeys = {} as Record<string, Record<string, any>>
  const cacheRowErrors = () => {
    if (!arrayField.value.errors.length) {
      return
    }

    for (let i = 0; i < arrayField.value.errors.length; i += 1) {
      const errorItem = arrayField.value.errors[i]
      const [index] = errorItem.path!.match(/\d+/) ?? []

      if (!index) {
        continue
      }

      const rowLocalKey = getRowLocalKey(arrayField.value.value[Number(index!)])
      const editable = editRowLocalKeys.value.includes(rowLocalKey)

      if (!editable) {
        continue
      }

      if (!tempCacheErrorByRowLocalKeys[rowLocalKey]) {
        tempCacheErrorByRowLocalKeys[rowLocalKey] = {}
      }

      tempCacheErrorByRowLocalKeys[rowLocalKey][String(errorItem.path!.split('.').slice(-1)[0])] =
        errorItem
    }
  }

  const refreshAllFieldEditableStatus = (
    cb?: (localRowKey: string, field: FieldModel) => void,
    allEditable?: boolean,
  ) => {
    // 虽然是精准更新，但是还是需要优化这里的粗粒度查找变更状态
    // todo 减少计算次数
    action(() => {
      for (let i = 0; i < arrayField.value.value.length; i += 1) {
        const rowLocalKey = getRowLocalKey(arrayField.value.value[i])
        const editable = allEditable ?? editRowLocalKeys.value.includes(rowLocalKey)

        const isError = !!Object.keys(tempCacheErrorByRowLocalKeys).length

        fieldsEditableToggle(i, editable, (field: FieldModel) => {
          // eslint-disable-next-line no-unused-expressions
          cb?.(rowLocalKey, field)

          if (isError && tempCacheErrorByRowLocalKeys[rowLocalKey]) {
            const errorItem =
              tempCacheErrorByRowLocalKeys[rowLocalKey][getStoreTempEditDataMapKeyByField(field)]
            if (errorItem) {
              field.setFeedback({
                type: 'error',
                // TODO 考虑多校验异常的情况
                messages: errorItem.messages,
              })

              return
            }
          }

          field.setFeedback({
            type: 'error',
            messages: [],
          })
        })

        // 在空闲的时候重置
        requestAnimationFrame(() => {
          tempCacheErrorByRowLocalKeys = {}
        })
      }
    })
  }

  const clearAllEditStatus = () => {
    action(() => {
      for (let i = 0; i < arrayField.value.value.length; i += 1) {
        fieldsEditableToggle(i, false)
      }

      editRowLocalKeys.value = []
      tempEditDataMap.clear()
    })
  }

  const getRowDefaultValue = () => {
    return columnsRef.value.reduce(
      (acc: any, col: any) =>
        // void 模型的字段不用给默认参数
        col.schema.type === 'void'
          ? acc
          : { ...acc, [col.columnProps.field]: getDefaultValue(col.schema) },
      setRowLocalKey({
        [TEMP_ADD_DATA_KEY]: true,
      }),
    )
  }

  // 全 table 全字段都是编辑状态
  const addRowByFullEditing = (method: 'unshift' | 'push' = 'push', defaultRowValue = {}) => {
    const defaultValue = {
      ...getRowDefaultValue(),
      ...defaultRowValue
    }

    if (method === 'unshift') {
      arrayField.value.unshift(defaultValue)
    } else {
      arrayField.value.push(defaultValue)
    }

    refreshVxeTableData()
  }

  const addRow = (method: 'unshift' | 'push' = 'push', defaultRowValue = {}) => {
    if (!editModeRef.value) {
      return
    }

    if (editModeRef.value === true) {
      addRowByFullEditing(method, defaultRowValue)

      return
    }

    // 单行编辑的状态下，已经存在有编辑行
    if (isRowEdit() && editRowLocalKeys.value.length > 0) {
      return
    }

    const defaultValue = {
      ...getRowDefaultValue(),
      ...defaultRowValue
    }

    action(() => {
      if (method === 'unshift') {
        if (!isRowEdit()) {
          cacheRowErrors()
        }

        arrayField.value.value.unshift(defaultValue)

        if (isRowEdit()) {
          editRowByIndex(0)
        } else {
          editRowLocalKeys.value.push(getRowLocalKey(defaultValue))

          // TODO 优化下，因为 push 的时候 field 还没创建
          setTimeout(() => {
            // 多行编辑
            refreshAllFieldEditableStatus()
          })
        }
      } else {
        console.log('!----')
        arrayField.value.value.push(defaultValue)

        editRowByIndex(arrayField.value.value.length - 1)
      }

      refreshVxeTableData()
    })
  }

  /**
   * 单行编辑
   */
  const editRowByIndex = (index: number) => {
    if (!editModeRef.value) {
      return
    }

    if (index === undefined) {
      throw new Error('请传入 index 参数')
    }

    const curr = arrayField.value.value?.[index]
    if (!curr) {
      throw new Error('不存在该行数据')
    }

    // 多行编辑
    if (isMultiRowEdit()) {
      editRowLocalKeys.value.push(getRowLocalKey(curr))
      editableRowDataByIndex(index)
      return
    }

    if (!canAddOrEditRow()) {
      return
    }

    action(() => {
      editRowLocalKeys.value = [getRowLocalKey(curr)]
      editableRowDataByIndex(index)
    })
  }

  const resetByCancel = (index: number) => {
    action(() => {
      const curLocalKey = getRowLocalKey(arrayField.value.value[index])
      tempEditDataMap.delete(curLocalKey)
      editRowLocalKeys.value = editRowLocalKeys.value.filter(key => key !== curLocalKey)

      changeTableEditable()
    })
  }

  const cancelAllEditRow = () => {
    if (!editRowLocalKeys.value.length || !arrayField.value.value.length) {
      return
    }

    refreshAllFieldEditableStatus((localRowKey, field) => {
      let tempValue = tempEditDataMap
        .get(localRowKey)
        ?.get(getStoreTempEditDataMapKeyByField(field))

      // TODO 优化查找值
      // @ts-ignore
      if (tempValue !== field.value) {
        field.setValue(tempValue)
      }
    }, false)
  }

  /**
   * 取消行编辑，并回滚编辑前的数据
   */
  const cancelEditRow = (index: number) => {
    if (!editRowLocalKeys.value.length || !isNum(index) || !arrayField.value.value[index]) {
      return
    }

    const localRowKey = getRowLocalKey(arrayField.value.value[index])
    if (!localRowKey) {
      return
    }

    action(() => {
      if (arrayField.value.value[index]?.[TEMP_ADD_DATA_KEY]) {
        resetByCancel(index)

        arrayField.value.value.splice(index, 1)
        refreshAllFieldEditableStatus()

        nextTick(() => {
          refreshVxeTableData()
        })

        return
      }

      const currTempData = tempEditDataMap.get(localRowKey)

      fieldsEditableToggle(index, false, field => {
        if (currTempData) {
          // @ts-ignore
          field.setValue(currTempData.get(getStoreTempEditDataMapKeyByField(field)))
        }
      })

      resetByCancel(index)
    })
  }

  /**
   * 关闭行编辑，保留现有编辑值
   */
  const saveEditRow = (index?: number) => {
    // if (!editRowLocalKeys.value.length) {
    //   return
    // }
    // fieldsEditableToggle(
    //   index === undefined || !isNum(index) ? editRowIndexArray.value[0] : index,
    //   false,
    // )
    // resetByCancel(index)
  }

  /**
   * 单元格编辑
   */
  const editCell = () => {
    //
  }

  const cancelEditCell = () => {
    //
  }

  const saveEditCell = () => {
    //
  }

  const validateRow = (index: number) => {
    return arrayField.value.form.validate(
      arrayField.value.address
        // @ts-ignore
        .concat(`${index}.*(${parseColumnsFields.value.join(',')})`)
        .toString(),
    )
  }

  const getUpdateRecords = () => {
    return arrayField.value.value.filter(row => {
      return editRowLocalKeys.value.includes(getRowLocalKey(row))
    })
  }

  return {
    validateRow,
    isEditByRowIndex,
    addRow,
    editRowByIndex,
    cancelEditRow,
    cancelAllEditRow,
    saveEditRow,
    editCell,
    cancelEditCell,
    saveEditCell,
    clearAllEditStatus,
    getUpdateRecords,
  }
}
