import { createDictClass } from '@/library/utils/dict/dict-utils'
import StringUtils from '@/utils/string-utils'
import createTreeClass from '@/utils/tree-utils'
import { formPageAPI } from 'modb@/basicSetting/api/basicSetting'
import { createDynamicClass } from 'modb@/basicSetting/views/formPage/utils/dynamic-utils'
import { getDeleteFileKey } from 'lib@/utils/file'
import { fileDismiss } from '@/api/common'
import { geti18n } from '@/main'
const i18n = geti18n()


const PAGE_GUID = StringUtils.guid()
const dictClass = createDictClass()

const getTemplateFileName = (currentConfig, scope, paramValueStr) => {
  let paramNameTempArray = paramValueStr.split(',')
  if (!paramValueStr || paramNameTempArray.length === 0) {
    return scope.row[currentConfig.prop]
  }
  if (paramNameTempArray.length === 1) {
    const paramName = paramNameTempArray[0]
    return scope.row[paramName]
  }
  if (paramNameTempArray.length === 2) {
    const paramName = paramNameTempArray[0]
    const paramNameSuffix = scope.row[paramNameTempArray[1]]
    if (!paramNameSuffix) {
      return paramNameSuffix
    }
    const suffixIndex = paramNameSuffix.lastIndexOf('.')
    if (suffixIndex === -1) {
      return paramNameSuffix
    }
    const fileSuffix = paramNameSuffix.substring(suffixIndex + 1)
    return scope.row[paramName] + '.' + fileSuffix
  }
}

export default {
  components: {
  },
  data () {
    return {
      dictClass: dictClass,
      dictCodes: {},

      pageCode: null,

      typeNameArray: ['select', 'table', 'header'],

      globalInfo: {
        select: {},
        table: {},
        header: {}
      },

      globalAttributeId: {
        select: null,
        table: null,
        header: null
      },

      attributeList: {
        select: [],
        table: [],
        header: []
      },
      componentConfig: {
        select: {
          component: null,
          prop: 'selectComponentKey',
          componentProperty: null,
          listeners: null
        },
        table: {
          component: null,
          prop: 'tableComponentKey',
          componentProperty: null,
          listeners: null
        },
        header: {
          component: null,
          prop: 'headerComponentKey',
          componentProperty: null,
          listeners: null
        }
      },
      slots: {
        select: [],
        table: [],
        header: []
      },

      tableAttributeColumns: [],

      methodDataList: [],

      customMethods: {
        /**
         * @param currentConfig
         * @param scope
         * @param paramValueStr
         *    没有参数：使用当前属性
         *    一个参数：使用配置参数
         *    两个参数：第一个参数为实际名称
         *            第二个参数为原始附件名，取此附件名的后缀
         * @returns {*}
         */
        getTemplateFileName: (currentConfig, scope, paramValueStr) => {
          return getTemplateFileName(currentConfig, scope, paramValueStr)
        },
        // 获取模板附件文件，返回值是给SrmCommonFile使用
        getTemplateFile: (currentConfig, scope, paramValueStr) => {
          return {
            // templateFileId字段是写死的
            fileId: scope.row.templateFileId,
            fileName: getTemplateFileName(currentConfig, scope, paramValueStr)
          }
        },
        addInfo: () => {
          const propItem = Object.assign({}, { $index: this.generateTemplateId() }, this.defaultObj)
          this.$refs.tableAttribute.pushRow(propItem)
        },
        changeUploadFile: (argumentArray, scope) => {
          let needClear = true
          // 单选
          if (argumentArray.length === 2) {
            const fileObj = argumentArray[1]
            if (!fileObj) {
              needClear = true
            } else {
              needClear = false
              this.$set(scope.row, 'fileCustomName', fileObj.fileSourceName)
              if (!fileObj.fileUploadId) {
                this.$set(scope.row, 'fileuploadId', fileObj.fileuploadId)
              }

              this.$set(scope.row, 'fileName', fileObj.fileSourceName)
              this.$set(scope.row, 'expireTime', fileObj.expireTime)
            }
          } else if (argumentArray.length === 3) {
            let fileObj = argumentArray[1]
            if (!fileObj || !fileObj.file) {
              needClear = true
            } else {
              needClear = false
              fileObj = fileObj.file
              this.$set(scope.row, 'fileCustomName', fileObj.fileSourceName)
              if (!fileObj.fileUploadId) {
                this.$set(scope.row, 'fileuploadId', fileObj.fileuploadId)
              }

              this.$set(scope.row, 'fileName', fileObj.fileSourceName)
              this.$set(scope.row, 'expireTime', fileObj.expireTime)

              const keyOptions = argumentArray[2]
              for (const keyItem in keyOptions) {
                if (keyItem === 'idKey' || keyItem === 'nameKey') {
                  continue
                }
                this.$set(scope.row, keyOptions[keyItem], fileObj[keyItem])
              }
            }
          }

          // 多选，list
          if (argumentArray.length === 1 && Array.isArray(argumentArray[0])) {
            needClear = false
            this.$set(scope.row, 'sceneFileDetailList', argumentArray[0])
          }

          if (needClear) {
            this.$set(scope.row, 'fileCustomName', '')
            this.$set(scope.row, 'fileuploadId', '')
            this.$set(scope.row, 'fileName', '')
            this.$set(scope.row, 'fileUploadTime', null)
            this.$set(scope.row, 'sceneFileDetailList', [])
          }
        },
        changeUploadFileList: (argumentArray, scope) => {
          if (argumentArray.length === 1) {
            this.$set(scope.row, 'sceneFileDetailList', argumentArray[0])
          } else if (argumentArray.length === 2) {
            const keyOptions = argumentArray[1]

            const fileList = argumentArray[0] || []
            const multiData = fileList.map(item => {
              const curItem = {
                ...item,
                [keyOptions.idKey]: item.fileId,
                [keyOptions.nameKey]: item.fileName,
                fileSourceName: item.fileName
              }
              for (const keyItem in keyOptions) {
                if (keyItem === 'idKey' || keyItem === 'nameKey') {
                  continue
                }
                curItem[keyOptions[keyItem]] = curItem[keyItem]
              }
              return curItem
            })
            this.$set(scope.row, 'sceneFileDetailList', multiData)
          }
        },
        transferIn: (data, fileInfo) => {
          fileInfo.fileuploadId = data.fileuploadId
          fileInfo.fileSourceName = data.fileCustomName
          fileInfo.name = data.fileCustomName
          fileInfo.lastUpdateDate = data.expireTime
        },
        removeFile: (argumentArray, scope) => {
          // 审计日志添加
          // 一行多个附件的情况
          let fileList = scope.row.sceneFileDetailList
          if (fileList && fileList.length > 0) {
            let fileIds = fileList.map(i => (i.fileuploadId))
            fileIds.forEach(element => {
              fileDismiss(getDeleteFileKey(element))
            })
          }
          // 一行单个附件
          let fileuploadId = scope.row.fileuploadId
          if (fileuploadId) {
            fileDismiss(getDeleteFileKey(fileuploadId))
          }

          this.$emit('removeFile', scope.$index, this.dataList[scope.$index])

          this.dataList.splice(scope.$index, 1)
        },
        transferOut: (data, fileInfo) => {
          fileInfo.fileCustomName = data.fileSourceName
          fileInfo.name = data.fileSourceName
          fileInfo.expireTime = data.lastUpdateDate
        },
        selectableFunc: (row, index) => {
          return row.defaultFile !== 'Y'
        }
      }
    }
  },
  created () {
    window[PAGE_GUID] = this
  },
  methods: {
    initData () {
      this.globalInfo = {
        select: {},
        table: {},
        header: {}
      }

      this.globalAttributeId = {
        select: null,
        table: null,
        header: null
      }

      this.attributeList = {
        select: [],
        table: [],
        header: []
      }
      this.componentConfig = {
        select: {
          component: null,
          prop: 'selectComponentKey',
          componentProperty: null,
          listeners: null
        },
        table: {
          component: null,
          prop: 'tableComponentKey',
          componentProperty: null,
          listeners: null
        },
        header: {
          component: null,
          prop: 'headerComponentKey',
          componentProperty: null,
          listeners: null
        }
      }
      this.slots = {
        select: [],
        table: [],
        header: []
      }

      this.tableAttributeColumns = []

      this.methodDataList = []
    },
    getPropItem () {
      const propItem = {
        $index: this.generateTemplateId()
      }
      for (let i = 0; i < this.tableAttributeColumns.length; i++) {
        propItem[this.tableAttributeColumns[i].prop] = null
      }
      return propItem
    },
    queryDynamicConfig () {
      this.initData()
      this.pageCode = this.getPageCode()

      formPageAPI.getPageCode(this.useDefault ? 'SCENE_DEFAULT_ATTACHMENT' : this.pageCode).then(res => {
        let formTemp = res.data
        if (!formTemp.formAttributeList) {
          formTemp.formAttributeList = []
        }

        for (let i = 0; i < formTemp.formAttributeList.length; i++) {
          const attrItem = formTemp.formAttributeList[i]
          const typeMap = {
            'MAIN_HEADER': 'header',
            'FORM_WRAPPER': 'select',
            'TABLE_VIEW': 'table'
          }
          if (attrItem.parentAttributeId === -1) {
            const typeName = typeMap[attrItem.componentType]
            if (typeName) {
              this.globalInfo[typeName] = attrItem
              this.globalAttributeId[typeName] = attrItem.formAttributeId
            }
          }
        }
        let waitList = []
        for (let i = 0; i < formTemp.formAttributeList.length; i++) {
          const attrItem = formTemp.formAttributeList[i]
          let exist = false
          for (let j = 0; j < this.typeNameArray.length; j++) {
            const typeName = this.typeNameArray[j]
            if (attrItem.parentAttributeId === this.globalAttributeId[typeName]) {
              this.attributeList[typeName].push(attrItem)
              exist = true
              break
            }
          }
          if (!exist) {
            waitList.push(attrItem)
          }
        }
        while (waitList.length > 0) {
          const tempList = []
          for (let i = 0; i < waitList.length; i++) {
            const waitAttrItem = waitList[i]
            let exist = false
            for (let j = 0; j < this.typeNameArray.length; j++) {
              const typeName = this.typeNameArray[j]
              const typeAttributeList = this.attributeList[typeName]
              for (let k = 0; k < typeAttributeList.length; k++) {
                const attributeItem = typeAttributeList[k]
                if (attributeItem.formAttributeId === waitAttrItem.parentAttributeId) {
                  typeAttributeList.push(waitAttrItem)
                  exist = true
                  break
                }
              }
              if (exist) {
                break
              }
            }
            if (!exist) {
              tempList.push(waitAttrItem)
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
          // eslint-disable-next-line no-new-func
          this.customMethods[methodItem.methodName] = new Function(methodItem.methodParams, methodBody)
        }

        const formArray = []
        if (this.attributeList.select) {
          const tempAttributeDataList = this.attributeList.select
          const selectAttributeDataList = tempAttributeDataList.sort((a, b) => parseInt(!a.sort ? 100000 : a.sort) - parseInt(!b.sort ? 100000 : b.sort))
          for (let i = 0; i < selectAttributeDataList.length; i++) {
            const attribute = selectAttributeDataList[i]
            if (!attribute.viewable) {
              continue
            }
            let propertyDescribe = attribute.propertyDescribe
            let langCode = attribute.languageCode ? attribute.languageCode : 'fileDynamic.' + attribute.propertyName
            let label = i18n.t(langCode) || propertyDescribe
            formArray.push({
              prop: attribute.propertyName,
              label: label,
              rules: [
                { required: !!attribute.notNull, message: i18n.t('common.pleaseInput') + label, trigger: 'blur' }
              ],
              type: 'slot',
              slot: attribute.propertyName
            })
            const DynamicClass = createDynamicClass()
            DynamicClass.setDefaultProperty('dictClass', this.dictClass)
            DynamicClass.load(attribute, this.customMethods)
            this.slots.table.push(DynamicClass.dynamicInto)
          }
        }
        if (this.globalInfo.select) {
          const listeners = {}
          if (this.globalInfo.select.eventList) {
            for (let i = 0; i < this.globalInfo.select.eventList.length; i++) {
              const eventItem = this.globalInfo.select.eventList[i]
              listeners[eventItem.eventName] = this.customMethods[eventItem.methodName]
            }
          }

          const DynamicClass = createDynamicClass()
          DynamicClass.setDefaultProperty('formArray', formArray)
          DynamicClass.load(this.globalInfo.select, this.customMethods)
          this.componentConfig.select = DynamicClass.dynamicInto
        }
        // 头部
        if (this.globalInfo.header) {
          const DynamicClass = createDynamicClass()
          DynamicClass.setDefaultProperty('dictClass', this.dictClass)
          DynamicClass.load(this.globalInfo.header, this.customMethods)
          this.componentConfig.header = DynamicClass.dynamicInto
        }

        if (this.attributeList.header) {
          const tempAttributeDataList = this.attributeList.header
          const headerAttributeDataList = tempAttributeDataList.sort((a, b) => parseInt(!a.sort ? 100000 : a.sort) - parseInt(!b.sort ? 100000 : b.sort))

          let currentHeaderSlots = []
          for (let i = 0; i < headerAttributeDataList.length; i++) {
            const attribute = headerAttributeDataList[i]
            if (!attribute.viewable) {
              continue
            }

            const DynamicClass = createDynamicClass()
            DynamicClass.setDisabled(!this.editable)
            DynamicClass.setDefaultProperty('dictClass', this.dictClass)
            DynamicClass.load(attribute, this.customMethods)
            DynamicClass.transferDictCode(this.dictCodes)

            if (!DynamicClass.isHide()) {
              currentHeaderSlots.push(DynamicClass.dynamicInto)
            }
          }
          this.slots.header = currentHeaderSlots
        }
        // 表格
        if (this.attributeList.table) {
          const tempAttributeDataList = this.attributeList.table
          const tableAttributeDataList = tempAttributeDataList.sort((a, b) => parseInt(!a.sort ? 100000 : a.sort) - parseInt(!b.sort ? 100000 : b.sort))

          let currentTableHeader = []
          let currentTableSlots = []
          for (let i = 0; i < tableAttributeDataList.length; i++) {
            const attribute = tableAttributeDataList[i]
            if (!attribute.viewable) {
              continue
            }
            let propertyDescribe = attribute.propertyDescribe
            let i18Code = attribute.languageCode
            let langCode = i18Code || 'fileDynamic.' + attribute.propertyName
            let label = i18n.t(langCode) || propertyDescribe
            const columnProperty = {
              align: 'center',
              prop: attribute.propertyName,
              label: label,
              editable: true,
              minWidth: '180',
              formAttributeId: attribute.formAttributeId,
              parentAttributeId: attribute.parentAttributeId,
              slot: attribute.propertyName
            }

            const DynamicClass = createDynamicClass()
            DynamicClass.setDisabled(!this.editable)
            DynamicClass.setPageCode(this.pageCode)
            DynamicClass.setDefaultFileDisabled(this.defaultFileDisabled)
            DynamicClass.setDefaultProperty('dictClass', this.dictClass)
            DynamicClass.load(attribute, this.customMethods)
            DynamicClass.transferDictCode(this.dictCodes)

            if (!DynamicClass.isHide()) {
              currentTableSlots.push(DynamicClass.dynamicInto)
              currentTableHeader.push(columnProperty)
            }
          }
          this.slots.table = currentTableSlots

          const treeClass = createTreeClass('formAttributeId', 'parentAttributeId')
          const treeTableHeader = treeClass.buildTree(currentTableHeader)
          this.tableAttributeColumns = treeTableHeader
        }
        if (this.globalInfo.table) {
          const DynamicClass = createDynamicClass()
          DynamicClass.setPageCode(this.pageCode)
          DynamicClass.setDefaultFileDisabled(this.defaultFileDisabled)
          DynamicClass.setDefaultProperty('dictClass', this.dictClass)
          DynamicClass.setDefaultProperty('columns', this.tableAttributeColumns)
          DynamicClass.setDefaultProperty('initialize', false)
          // DynamicClass.setDefaultProperty('style', 'min-height:220px;max-height:300px;')

          DynamicClass.load(this.globalInfo.table, this.customMethods)

          this.componentConfig.table = DynamicClass.dynamicInto
        }

        const dictCodeArray = []
        for (let key in this.dictCodes) {
          dictCodeArray.push(this.dictCodes[key])
        }
        this.dictClass.loadDictionary(dictCodeArray)
      })
    }
  }
}
