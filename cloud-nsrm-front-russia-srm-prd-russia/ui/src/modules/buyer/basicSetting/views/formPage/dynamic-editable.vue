<!-- 附件管理组件 -->
<template>
  <div>
    <component
      :is="componentConfig.select.component"
      :key="componentConfig.select.prop"
      v-bind="componentConfig.select.componentProperty"
      v-on="componentConfig.select.listeners"
    >
      <template
        v-for="item in slots.select"
        #[item.prop]="{ scope }"
      >
        <component
          :is="item.component"
          :key="item.prop"
          v-model="scope[item.prop]"
          v-bind="item.componentProperty"
          v-on="item.listeners"
        />
      </template>
    </component>

    <component
      :is="componentConfig.header.component"
      :key="componentConfig.header.prop"
      v-bind="componentConfig.header.componentProperty"
      v-on="componentConfig.header.listeners"
    >
      <template slot="left">
        <template v-for="slotItem in slots.header">
          <component
            :is="slotItem.component"
            :key="slotItem.prop"
            v-bind="slotItem.componentProperty"
            v-on="slotItem.listeners"
          >
            <template
              v-for="(slotItem3, slotName) in slotItem.slots.component"
              #[slotName]="{ scope }"
            >
              <template v-for="subItem3 in slotItem3">
                <component
                  :is="subItem3.component"
                  :key="subItem3.prop"
                  v-bind="subItem3.componentProperty"
                  v-on="subItem3.listeners"
                />
              </template>
            </template>
            <template v-for="(slotItem3, slotName) in slotItem.slots.text">
              <template :slot="slotName">
                <template v-for="subItem3 in slotItem3">
                  <div>{{ subItem3.text }}</div>
                </template>
              </template>
            </template>
          </component>
        </template>
      </template>
    </component>

    <component
      :is="componentConfig.table.component"
      :key="componentConfig.table.prop"
      ref="tableAttribute"
      v-model="value"
      v-bind="componentConfig.table.componentProperty"
      v-on="componentConfig.table.listeners"
    >
      <template
        v-for="item in slots.table"
        #[item.prop]="{ scope }"
      >
        <component
          :is="item.component"
          v-if="item.modelConfig.modelBind && item.modelConfig.modelType === 'default'"
          :key="item.prop"
          v-model="scope.row[item.prop]"
          v-bind="item.dynamicClass.getScopeProperty(scope)"
          v-on="item.dynamicClass.getScopeEvent(scope)"
        />

        <component
          :is="item.component"
          v-else-if="item.modelConfig.modelBind && item.modelConfig.modelType === 'global'"
          :key="item.prop"
          v-model="scope"
          v-bind="item.dynamicClass.getScopeProperty(scope)"
          v-on="item.dynamicClass.getScopeEvent(scope)"
        />
        <component
          :is="item.component"
          v-else-if="item.modelConfig.modelBind && item.modelConfig.modelType === 'one'"
          :key="item.prop"
          v-model="scope[item.modelConfig.modelParam[0]]"
          v-bind="item.dynamicClass.getScopeProperty(scope)"
          v-on="item.dynamicClass.getScopeEvent(scope)"
        />
        <component
          :is="item.component"
          v-else-if="item.modelConfig.modelBind && item.modelConfig.modelType === 'two'"
          :key="item.prop"
          v-model="scope[item.modelConfig.modelParam[0]][item.modelConfig.modelParam[1]]"
          v-bind="item.dynamicClass.getScopeProperty(scope)"
          v-on="item.dynamicClass.getScopeEvent(scope)"
        />
        <component
          :is="item.component"
          v-else-if="item.modelConfig.modelBind && item.modelConfig.modelType === 'three'"
          :key="item.prop"
          v-model="scope[item.modelConfig.modelParam[0]][item.modelConfig.modelParam[1]][item.modelConfig.modelParam[2]]"
          v-bind="item.dynamicClass.getScopeProperty(scope)"
          v-on="item.dynamicClass.getScopeEvent(scope)"
        />
        <component
          :is="item.component"
          v-else
          :key="item.prop"
          v-bind="item.dynamicClass.getScopeProperty(scope)"
          v-on="item.dynamicClass.getScopeEvent(scope)"
        >
          <template
            v-for="(slotItem3, slotName) in item.slots.component"
            #[slotName]="{ scope }"
          >
            <template v-for="subItem3 in slotItem3">
              <component
                :is="subItem3.component"
                :key="subItem3.prop"
                v-bind="subItem3.componentProperty"
                v-on="subItem3.listeners"
              />
            </template>
          </template>
          <template v-for="(slotItem3, slotName) in item.slots.text">
            <template :slot="slotName">
              <template v-for="(subItem3, subItem3Index) in slotItem3">
                <div :key="`subItem3-${subItem3Index}`">{{ subItem3.text }}</div>
              </template>
            </template>
          </template>
        </component>
      </template>
    </component>
  </div>
</template>

<script>
import ExportExcel from 'lib@/components/export-excel'
import MainHeader from 'lib@/components/Table/MainHeader'
import FileManager from './mixins/file-manager'
import BaseTableBind from '@/library/components/BaseTable/BaseTableBind'
import { sceneFileApi } from 'modb@/basicSetting/api/basicSetting'

let seed = 0

function generateTemplateId () {
  const index = ++seed
  return `custom_file_id_${index}`
}

export default {
  name: 'CFileManagement',
  components: {
    ExportExcel,
    MainHeader,
    BaseTableBind
  },
  mixins: [FileManager],
  model: {
    event: 'input',
    prop: 'value'
  },
  props: {
    // 单据新建的时候没有businessId
    businessId: {
      type: [Number, String]
    },
    height: {
      type: [Number, String],
      default: 300
    },
    maxHeight: {
      type: [Number, String],
      default: 300
    },
    sceneModuleCode: {
      type: String,
      required: true
    },
    value: {
      type: Array
    },
    showAddition: {
      type: Boolean,
      default: true
    },
    showMultiDeletion: {
      type: Boolean,
      default: true
    },
    fileInfo: {
      type: Object,
      default: () => {
        return {
          uploadType: 'DEF',
          sourceType: 'WEB_APP',
          fileModular: 'base', // 文件所属模块 -》基础模块
          fileFunction: 'CFileManagement', // 文件所属功能
          fileType: 'images' // 文件所属类型
        }
      }
    },
    editable: {
      type: Boolean,
      default: () => {
        return false
      }
    }
  },
  data () {
    return {
      requestUrl: null,
      defaultObj: {},
      sceneFileTypeMap: {}, // fileType, sceneTemplate

      dataList: [],
      selection: []
    }
  },
  computed: {
    addition () {
      if (this.businessId) {
        return this.showAddition
      }
      return false || true
    },
    deletion () {
      if (this.businessId) {
        return this.showMultiDeletion
      }
      return false || true
    }
  },
  watch: {
    value: {
      handler () {
        this.dataList = this.value
      },
      deep: true,
      immediate: true
    },
    dataList: {
      handler () {
        this.$emit('input', this.dataList)
      },
      deep: true,
      immediate: true
    }
  },
  created () {
    this.customMethods['selectionChange'] = this.selectionChange
    this.customMethods['multiDeleteFiles'] = this.multiDeleteFiles
    this.customMethods['changeAttachmentType'] = this.changeAttachmentType

    this.initDataList()
    this.initComponent()
  },
  methods: {
    generateTemplateId () {
      return generateTemplateId()
    },
    getPageCode () {
      return this.sceneModuleCode
    },
    initComponent () {
      this.queryDynamicConfig()
    },
    selectionChange (value) {
      this.selection = value
    },
    async initDataList () {
      const res = await sceneFileApi.listAll({ sceneModuleCode: this.getPageCode() })
      const currentList = res.data
      let isDefault = currentList.length === 1 && currentList[0].attachmentType === 'SCENE_FILE_DEFAULT'
      if (isDefault) {
        const templateItem = currentList[0]
        this.defaultObj = {
          sceneTemplateId: templateItem.sceneTemplateId,
          businessId: this.businessId,
          sceneCode: templateItem.sceneCode,
          templateFileId: templateItem.templateFileId,
          sceneUniqueCode: templateItem.sceneUniqueCode,
          sceneModuleCode: templateItem.sceneModuleCode,
          attachmentType: templateItem.attachmentType,
          attachmentSourceName: templateItem.attachmentSourceName,
          attachmentName: templateItem.attachmentName,
          required: templateItem.required
        }
      }
      const dataList = []
      for (let i = 0; i < currentList.length; i++) {
        const templateItem = currentList[i]
        this.sceneFileTypeMap[templateItem.attachmentType] = templateItem
        if (templateItem.enabled !== 'Y') { // 未启用
          continue
        }

        const dataItem = {
          $index: this.generateTemplateId(),
          sceneTemplateId: templateItem.sceneTemplateId,
          businessId: this.businessId,
          sceneCode: templateItem.sceneCode,
          templateFileId: templateItem.templateFileId,
          sceneUniqueCode: templateItem.sceneUniqueCode,
          sceneModuleCode: templateItem.sceneModuleCode,
          attachmentType: templateItem.attachmentType,
          attachmentSourceName: templateItem.attachmentSourceName,
          attachmentName: templateItem.attachmentName,
          required: templateItem.required
        }
        this.sceneFileTypeMap[templateItem.attachmentType] = templateItem
        dataList.push(dataItem)
      }

      if (!this.businessId) {
        this.dataList = dataList
        return
      }
      const { list } = await this.$api.base.fileManagement({
        path: 'listAll',
        parameter: { businessId: this.businessId }
      })
      for (let i = 0; i < list.length; i++) {
        list[i]['$index'] = this.generateTemplateId()
      }
      this.dataList = list
    },
    multiDeleteFiles () {
      if (this.selection.length) {
        const list = []
        for (let i = 0; i < this.dataList.length; i++) {
          let exist = false
          for (let j = 0; j < this.selection.length; j++) {
            if (this.dataList[i].$index === this.selection[j].$index) {
              exist = true
              break
            }
          }
          if (!exist) {
            list.push(this.dataList[i])
          }
        }
        this.dataList = list
      } else {
        this.$message.warning(this.$t('common.deleteData'))
      }
    },
    changeAttachmentType (params, scope) {
      const attachmentType = params[0]
      const templateItem = this.sceneFileTypeMap[attachmentType]
      if (!templateItem) {
        this.$set(scope.row, 'sceneTemplateId', '')
        this.$set(scope.row, 'businessId', this.businessId)
        this.$set(scope.row, 'sceneCode', '')
        this.$set(scope.row, 'templateFileId', '')
        this.$set(scope.row, 'sceneUniqueCode', '')
        this.$set(scope.row, 'sceneModuleCode', '')
        this.$set(scope.row, 'attachmentType', '')
        this.$set(scope.row, 'attachmentSourceName', '')
        this.$set(scope.row, 'attachmentName', '')
        this.$set(scope.row, 'required', '')
      } else {
        this.$set(scope.row, 'sceneTemplateId', templateItem.sceneTemplateId)
        this.$set(scope.row, 'businessId', this.businessId)
        this.$set(scope.row, 'sceneCode', templateItem.sceneCode)
        this.$set(scope.row, 'templateFileId', templateItem.templateFileId)
        this.$set(scope.row, 'sceneUniqueCode', templateItem.sceneUniqueCode)
        this.$set(scope.row, 'sceneModuleCode', templateItem.sceneModuleCode)
        this.$set(scope.row, 'attachmentType', templateItem.attachmentType)
        this.$set(scope.row, 'attachmentSourceName', templateItem.attachmentSourceName)
        this.$set(scope.row, 'attachmentName', templateItem.attachmentName)
        this.$set(scope.row, 'required', templateItem.required)
      }
    }
  }
}
</script>
<style scoped lang="scss">
.btn-group {
  margin-bottom: 10px;
}
</style>
