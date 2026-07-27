import StringUtils from '@/utils/string-utils'

export const createDynamicClass = () => {
  const DynamicClass = {
    dynamicInto: {},
    dictCodes: {},
    defaultProperty: {},
    attribute: {},
    customMethods: {},
    disabled: false,
    disabledHide: false,
    pageCode: '',
    defaultFileDisabled: false,
    load: (attribute, customMethods) => {
      DynamicClass.attribute = attribute
      DynamicClass.customMethods = customMethods

      const listeners = {}
      if (attribute.eventList) {
        for (let i = 0; i < attribute.eventList.length; i++) {
          const eventItem = attribute.eventList[i]
          listeners[eventItem.eventName] = customMethods[eventItem.methodName]
        }
      }

      let componentProperty = {}
      let slots = {} // type(text, other): {slotName: }
      let dictCodes = {}
      let ref = null
      if (attribute.propertyName === 'attachmentType') {
        if (!attribute.dictCode) {
          attribute.dictCode = DynamicClass.pageCode
        }
      }
      if (attribute.componentType === 'DICT_SELECT') {
        if (attribute.dictCode) {
          componentProperty['code'] = attribute.dictCode
        }
      }
      if (attribute.dataType) {
        componentProperty['dataType'] = attribute.dataType
      }
      if (attribute.dataType === 'dict' && attribute.dictCode) {
        dictCodes[attribute.propertyName] = attribute.dictCode
      }
      if (attribute.maxLength) {
        componentProperty['max-length'] = attribute.maxLength
      }
      if (attribute.minLength) {
        componentProperty['min-length'] = attribute.minLength
      }
      if (attribute.placeholder) {
        componentProperty['placeholder'] = attribute.placeholder
      }
      if (attribute.lSpan) {
        componentProperty['lSpan'] = attribute.lSpan
      }
      if (attribute.rSpan) {
        componentProperty['rSpan'] = attribute.rSpan
      }
      if (attribute.ref) {
        ref = attribute.ref
      }
      if (attribute.initialize) {
        componentProperty['initialize'] = attribute.initialize
      }

      let modelBind = attribute.modelBind !== 'N'
      let modelType = attribute.modelType ? attribute.modelType : 'default'
      let modelParam = []
      if (attribute.propList) {
        for (let i = 0; i < attribute.propList.length; i++) {
          let propItem = attribute.propList[i]

          if (attribute.componentType === 'EL_BUTTON' && propItem.propName === 'buttonNameText') {
            slots.text = {
              default: []
            }
            slots.text.default.push({ text: propItem.propValue })
          }

          if (propItem.propName === 'MODEL_PARAM') {
            let paramNameTempArray = propItem.propValue.split(',')
            let paramNameArray = []
            for (let i = 0; i < paramNameTempArray.length; i++) {
              let paramItem = StringUtils.trim(paramNameTempArray[i])
              if (paramItem.length > 0) {
                paramNameArray.push(paramItem)
              }
            }
            modelParam = paramNameArray
          } else if (propItem.propName === 'disabledHide') {
            DynamicClass.disabledHide = propItem.propValue === 'true'
          } else if (propItem.propValueType === 'SCOPE') {
            componentProperty[propItem.propName] = propItem.propValue
          } else if (propItem.propValueType === 'BOOLEAN') {
            componentProperty[propItem.propName] = propItem.propValue !== 'false'
          } else if (propItem.propValueType === 'FUNCTION') {
            componentProperty[propItem.propName] = customMethods[propItem.methodName]
          } else if (propItem.propValueType === 'FUNCTION_VALUE') {
            componentProperty[propItem.propName] = customMethods[propItem.methodName]
          } else {
            componentProperty[propItem.propName] = propItem.propValue
          }
        }
      }

      DynamicClass.dynamicInto = {
        component: attribute.elementTag,
        componentProperty: Object.assign({}, DynamicClass.defaultProperty, componentProperty, { disabled: DynamicClass.disabled }),
        prop: attribute.propertyName,
        listeners: listeners,
        slots: slots,
        ref: ref,
        dynamicClass: DynamicClass,
        modelConfig: {
          modelBind: modelBind,
          modelType: modelType,
          modelParam: modelParam
        }
      }
      DynamicClass.dictCodes = dictCodes
    },
    setDisabled (disabled) {
      DynamicClass.disabled = disabled
    },
    setDefaultProperty (key, value) {
      DynamicClass.defaultProperty[key] = value
    },
    setProperty (key, value) {
      if (value) {
        DynamicClass.dynamicInto.componentProperty[key] = value
      }
    },
    setPageCode (pageCode) {
      DynamicClass.pageCode = pageCode
    },
    transferDictCode (destDictCodes) {
      for (let dictKey in DynamicClass.dictCodes) {
        destDictCodes[dictKey] = DynamicClass.dictCodes[dictKey]
      }
    },
    getScopeProperty (scope, currentConfig) {
      const property = Object.assign({}, {}, DynamicClass.dynamicInto.componentProperty)

      const attribute = DynamicClass.attribute
      if (attribute.propList) {
        for (let i = 0; i < attribute.propList.length; i++) {
          let propItem = attribute.propList[i]

          if (propItem.propValueType === 'SCOPE') {
            let paramNameTempArray = propItem.propValue.split(',')
            let paramNameArray = []
            for (let i = 0; i < paramNameTempArray.length; i++) {
              let paramItem = StringUtils.trim(paramNameTempArray[i])
              if (paramItem.length > 0) {
                paramNameArray.push(paramItem)
              }
            }

            property[propItem.propName] = this.getChildValue(scope, paramNameArray, 0, paramNameArray.length - 1)
          } else if (propItem.propValueType === 'BOOLEAN') {
            property[propItem.propName] = propItem.propValue !== 'false'
          } else if (propItem.propValueType === 'FUNCTION') {
            property[propItem.propName] = DynamicClass.customMethods[propItem.methodName]
          } else if (propItem.propValueType === 'FUNCTION_VALUE') {
            property[propItem.propName] = DynamicClass.customMethods[propItem.methodName](currentConfig, scope, propItem.propValue)
          }
        }
      }
      if (DynamicClass.defaultFileDisabled) {
        if (scope.row.defaultFile === 'Y') {
          if (attribute.columnName === 'DELETE_ITEM') {
            property['style'] = 'display:none'
          } else if (attribute.columnName === 'ATTACHMENT_TYPE') {
            property['disabled'] = true
          }
        }
      }
      return property
    },
    getChildValue (json, paramArray, current, deep) {
      if (!json) {
        return null
      }
      if (current >= deep) {
        return json[paramArray[current]]
      } else {
        return this.getChildValue(json[paramArray[current]], paramArray, current + 1, deep)
      }
    },
    getScopeEvent (scope) {
      const listeners = {}
      const attribute = DynamicClass.attribute
      if (attribute.eventList) {
        for (let i = 0; i < attribute.eventList.length; i++) {
          const eventItem = attribute.eventList[i]
          listeners[eventItem.eventName] = function () {
            return DynamicClass.customMethods[eventItem.methodName](arguments, scope)
          }
        }
      }

      return listeners
    },
    isHide () {
      return DynamicClass.disabled && DynamicClass.disabledHide
    },
    setDefaultFileDisabled (defaultFileDisabled) {
      DynamicClass.defaultFileDisabled = defaultFileDisabled
    }
  }

  return DynamicClass
}
