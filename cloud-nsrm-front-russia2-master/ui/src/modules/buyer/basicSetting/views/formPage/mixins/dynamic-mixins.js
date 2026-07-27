import { formPageAPI } from 'modb@/basicSetting/api/basicSetting'
import createTreeClass from '@/utils/tree-utils'

function guid () {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
    // let r = Math.random() * 16 | 0
    let r = window.crypto.getRandomValues(new Uint8Array(1)) * 0.001 * 16 | 0
    let v = c == 'x' ? r : (r & 0x3 | 0x8)
    return v.toString(16)
  })
}
const PAGE_GUID = 'U' + guid().replace(/-/g, '')

export default {
  components: {
  },
  data () {
    return {
      pageCode: null,

      selectGlobalInfo: {},
      selectAttributeDataList: [],
      selectAttributeColumns: [],

      tableGlobalInfo: {},
      tableAttributeDataList: [],
      tableAttributeColumns: [],
      tableFormGlobal: {},

      methodDataList: [],

      customMethods: {},

      formWrapperConfig: {},

      formSlots: []
    }
  },
  created () {
    window[PAGE_GUID] = this
  },
  methods: {
    queryDynamicConfig () {
      this.pageCode = this.getPageCode()
      formPageAPI.getPageCode(this.pageCode).then(res => {
        let formTemp = res.data
        if (!formTemp.formAttributeList) {
          formTemp.formAttributeList = []
        }

        for (let i = 0; i < formTemp.formAttributeList.length; i++) {
          const attrItem = formTemp.formAttributeList[i]
          if (attrItem.componentType === 'FORM_WRAPPER' && attrItem.parentAttributeId === -1) {
            this.selectGlobalInfo = attrItem
          } else if (attrItem.componentType === 'TABLE_VIEW' && attrItem.parentAttributeId === -1) {
            this.tableGlobalInfo = attrItem
          }
        }
        const selectFormAttributeId = this.selectGlobalInfo ? this.selectGlobalInfo.formAttributeId : null
        const tableFormAttributeId = this.tableGlobalInfo ? this.tableGlobalInfo.formAttributeId : null
        let waitList = []
        for (let i = 0; i < formTemp.formAttributeList.length; i++) {
          const attrItem = formTemp.formAttributeList[i]
          if (attrItem.parentAttributeId === selectFormAttributeId) {
            this.selectAttributeDataList.push(attrItem)
          } else if (attrItem.parentAttributeId === tableFormAttributeId) {
            this.tableAttributeDataList.push(attrItem)
          } else {
            waitList.push(attrItem)
          }
        }
        while (waitList.length > 0) {
          const tempList = []
          for (let i = 0; i < waitList.length; i++) {
            const attrItem = waitList[i]
            let existSelect = false
            for (let j = 0; j < this.selectAttributeDataList.length; j++) {
              const selectItem = this.selectAttributeDataList[j]
              if (selectItem.formAttributeId === attrItem.parentAttributeId) {
                existSelect = true
                break
              }
            }
            if (existSelect) {
              this.selectAttributeDataList.push(attrItem)
            } else {
              let existTable = false
              for (let j = 0; j < this.tableAttributeDataList.length; j++) {
                const tableItem = this.tableAttributeDataList[j]
                if (tableItem.formAttributeId === attrItem.parentAttributeId) {
                  existTable = true
                  break
                }
              }
              if (existTable) {
                this.tableAttributeDataList.push(attrItem)
              } else {
                tempList.push(attrItem)
              }
            }
          }
          if (tempList.length === waitList.length) {
            break
          }
          waitList = tempList
        }

        this.methodDataList = formTemp.methodList

        for (let i = 0; i < this.methodDataList.length; i++) {
          const methodItem = this.methodDataList[i]
          let methodBody = methodItem.methodBody
          methodBody = methodBody.replace(/this/g, 'window.' + PAGE_GUID)
          this.customMethods[methodItem.methodName] = new Function(methodItem.methodParams, methodBody)
        }

        this.formConfig = []
        if (this.selectAttributeDataList) {
          const tempAttributeDataList = this.selectAttributeDataList
          const selectAttributeDataList = tempAttributeDataList.sort((a, b) => parseInt(!a.sort ? 100000 : a.sort) - parseInt(!b.sort ? 100000 : b.sort))
          for (let i = 0; i < selectAttributeDataList.length; i++) {
            const attribute = selectAttributeDataList[i]
            if (!attribute.viewable) {
              continue
            }
            this.formConfig.push({
              prop: attribute.propertyName,
              label: attribute.propertyDescribe,
              rules: [
                { required: !!attribute.notNull, message: this.$t('common.pleaseInput') + attribute.propertyDescribe, trigger: 'blur' }
              ],
              type: 'slot',
              slot: attribute.propertyName
            })
            var componentSlotItem = {
              component: attribute.elementTag,
              componentProperty: {},
              prop: attribute.propertyName,
              listeners: {}
            }
            if (attribute.dictCode) {
              componentSlotItem.componentProperty['code'] = attribute.dictCode
            }
            if (attribute.dictCode) {
              componentSlotItem.componentProperty['dictCode'] = attribute.dictCode

              if (attribute.componentType === 'DICT_SELECT') {
                componentSlotItem.componentProperty['code'] = attribute.dictCode
                let changeFunction = new Function('dictCode', 'console.log(\'dictValue change：\' + dictCode, window.' + PAGE_GUID + '.testValue)')
                componentSlotItem.listeners['change'] = changeFunction
              }
            }
            if (attribute.maxLength) {
              componentSlotItem.componentProperty['max-length'] = attribute.maxLength
            }
            if (attribute.minLength) {
              componentSlotItem.componentProperty['min-length'] = attribute.minLength
            }
            if (attribute.placeholder) {
              componentSlotItem.componentProperty['placeholder'] = attribute.placeholder
            }
            this.formSlots.push(componentSlotItem)
          }
        }
        if (this.selectGlobalInfo) {
          const listeners = {}
          if (this.selectGlobalInfo.eventList) {
            for (let i = 0; i < this.selectGlobalInfo.eventList.length; i++) {
              const eventItem = this.selectGlobalInfo.eventList[i]
              listeners[eventItem.eventName] = this.customMethods[eventItem.methodName]
            }
          }

          this.formWrapperConfig = {
            component: this.selectGlobalInfo.elementTag,
            componentProperty: {
              formArray: this.formConfig
            },
            prop: this.selectGlobalInfo.propertyName,
            listeners: listeners
          }
        }
        if (this.tableAttributeDataList) {
          const tempAttributeDataList = this.tableAttributeDataList
          const tableAttributeDataList = tempAttributeDataList.sort((a, b) => parseInt(!a.sort ? 100000 : a.sort) - parseInt(!b.sort ? 100000 : b.sort))

          let currentTableHeader = []
          for (let i = 0; i < tableAttributeDataList.length; i++) {
            const attribute = tableAttributeDataList[i]
            if (!attribute.viewable) {
              continue
            }
            const columnProperty = {
              align: 'center',
              prop: attribute.propertyName,
              label: attribute.propertyDescribe,
              editable: false,
              minWidth: '180',
              formAttributeId: attribute.formAttributeId,
              parentAttributeId: attribute.parentAttributeId
            }
            if (attribute.dataType) {
              columnProperty['dataType'] = attribute.dataType
            }
            if (attribute.dictCode) {
              columnProperty['dictCode'] = attribute.dictCode

              if (attribute.dataType === 'dict') {
                columnProperty['code'] = attribute.dictCode
                this.dictCodes[attribute.propertyName] = attribute.dictCode
              }
            }
            currentTableHeader.push(columnProperty)
          }
          const treeClass = createTreeClass('formAttributeId', 'parentAttributeId')
          const treeTableHeader = treeClass.buildTree(currentTableHeader)
          this.tableHeader = treeTableHeader
        }
        if (this.tableGlobalInfo) {
          this.tableFormGlobal = this.tableGlobalInfo
        }
      })
    }
  }
}
