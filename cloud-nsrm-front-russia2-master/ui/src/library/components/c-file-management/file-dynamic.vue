<!-- 附件管理组件 -->
<template>
  <div class="file-dynamic-sec">
    <component
      :is="componentConfig.select.component"
      v-if="slots.select.length"
      :key="componentConfig.select.prop || 'selectComponent'"
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
      v-if="slots.header.length"
      :key="componentConfig.header.prop || 'headerComponent'"
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
                <div v-for="(subItem3, slotItem3Index) in slotItem3" :key="subItem3.text + slotItem3Index">
                  {{ subItem3.text }}
                </div>
              </template>
            </template>
          </component>
        </template>
      </template>
    </component>

    <component
      :is="componentConfig.table.component"
      v-if="slots.table.length"
      :key="componentConfig.table.prop || 'tableComponent'"
      ref="tableAttribute"
      v-model="value"
      v-bind="componentConfig.table.componentProperty"
      v-on="componentConfig.table.listeners"
    >
      <template
        v-for="item in slots.table"
        #[item.prop]="{ scope }"
      >
        <!--如果不设置组件，默认展示字符串-->
        <div
          v-if="!item.component"
          :key="item.prop"
          class="text-slot"
        >
          {{ scope.row[item.prop] }}
        </div>

        <template v-else>
          <component
            :is="item.component"
            v-if="item.modelConfig.modelBind && item.modelConfig.modelType === 'default'"
            :key="item.prop"
            v-model="scope.row[item.prop]"
            v-bind="item.dynamicClass.getScopeProperty(scope, item)"
            v-on="item.dynamicClass.getScopeEvent(scope)"
          />

          <component
            :is="item.component"
            v-else-if="item.modelConfig.modelBind && item.modelConfig.modelType === 'global'"
            :key="item.prop"
            v-model="scope"
            v-bind="item.dynamicClass.getScopeProperty(scope, item)"
            v-on="item.dynamicClass.getScopeEvent(scope)"
          />

          <component
            :is="item.component"
            v-else-if="item.modelConfig.modelBind && item.modelConfig.modelType === 'one'"
            :key="item.prop"
            v-model="scope[item.modelConfig.modelParam[0]]"
            v-bind="item.dynamicClass.getScopeProperty(scope, item)"
            v-on="item.dynamicClass.getScopeEvent(scope)"
          />

          <component
            :is="item.component"
            v-else-if="item.modelConfig.modelBind && item.modelConfig.modelType === 'two'"
            :key="item.prop"
            v-model="scope[item.modelConfig.modelParam[0]][item.modelConfig.modelParam[1]]"
            v-bind="item.dynamicClass.getScopeProperty(scope, item)"
            v-on="item.dynamicClass.getScopeEvent(scope)"
          />

          <component
            :is="item.component"
            v-else-if="item.modelConfig.modelBind && item.modelConfig.modelType === 'three'"
            :key="item.prop"
            v-model="scope[item.modelConfig.modelParam[0]][item.modelConfig.modelParam[1]][item.modelConfig.modelParam[2]]"
            v-bind="item.dynamicClass.getScopeProperty(scope, item)"
            v-on="item.dynamicClass.getScopeEvent(scope)"
          />

          <component
            :is="item.component"
            v-else
            :key="item.prop"
            v-bind="item.dynamicClass.getScopeProperty(scope, item)"
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
                <template v-for="subItem3 in slotItem3">
                  <div :key="subItem3.prop">
                    {{ subItem3.text }}
                  </div>
                </template>
              </template>
            </template>
          </component>
        </template>
      </template>
    </component>
  </div>
</template>

<script>
import ExportExcel from 'lib@/components/export-excel'
import MainHeader from 'lib@/components/Table/MainHeader'
import FileManager from 'modb@/basicSetting/views/formPage/mixins/file-manager'
import BaseTableBind from '@/library/components/BaseTable/BaseTableBind'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { sceneFileCompApi } from '@/api/fileApi'
import { ADD_KEY, EDITABLE_KEY, UPDATE_KEY } from '@/library/components/BaseTable/utils'

let seed = 0

function generateTemplateId () {
  const index = ++seed
  return `custom_file_id_${index}`
}

export default {
  name: 'FileDynamic',
  components: {
    ExportExcel,
    MainHeader,
    BaseTableBind,
    FormWrapper
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
    },
    defaultFileDisabled: {
      // 默认附件禁用：不能删除
      type: Boolean,
      default: () => {
        return true
      }
    },
    useDefault: {
      type: Boolean,
      default: () => {
        return false
      }
    },
    needInit: {
      type: Boolean,
      default: () => {
        return true
      }
    },
    // 前置处理模版文件列表数据
    handlerSceneTemplateData: {
      type: Function
    },
    // 当模版文件列表数据更新后下一次DOM刷新触发, 目前看因为包多了一层component导致并不完全准确，暂时先用延迟执行去规避下
    onSceneTemplateNextTick: {
      type: Function
    }
  },
  data () {
    return {
      requestUrl: null,
      defaultObj: {},
      sceneFileTypeMap: {}, // fileType, sceneTemplate
      hasInit: false,

      dataList: [],
      selection: []
    }
  },
  computed: {
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
    },
    editable: {
      handler (val) {
        this.initComponent()
      }
    }
  },
  created () {
    this.customMethods['selectionChange'] = this.selectionChange
    this.customMethods['multiDeleteFiles'] = this.multiDeleteFiles
    this.customMethods['changeAttachmentType'] = this.changeAttachmentType
    this.customMethods['selectionShow'] = this.editable

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
    async loadFileInfo () {
      const dataList = []
      if (!this.hasInit) {
        this.hasInit = true
        let res = await sceneFileCompApi.sceneTemplateListAll({ sceneModuleCode: this.useDefault ? 'SCENE_DEFAULT_ATTACHMENT' : this.getPageCode() })
        let currentList = res.data

        // 转换处理模版附件列表数据
        if (this.handlerSceneTemplateData) {
          currentList = this.handlerSceneTemplateData(currentList)
        }

        // let isDefault = currentList.length === 1 && currentList[0].attachmentType === 'SCENE_FILE_DEFAULT'
        if (currentList.length > 0) {
          const templateItem = currentList[0]
          this.defaultObj = {
            sceneTemplateId: null,
            businessId: this.businessId,
            sceneCode: null,
            templateFileId: null,
            sceneUniqueCode: null,
            sceneModuleCode: this.sceneModuleCode,
            attachmentType: null,
            attachmentSourceName: null,
            attachmentName: null,
            required: 'N',
            defaultFile: 'N',
            [ADD_KEY]: true,
            [EDITABLE_KEY]: true,
            [UPDATE_KEY]: true
          }
        }
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
            required: templateItem.required,
            defaultFile: 'Y',
            [ADD_KEY]: true,
            [EDITABLE_KEY]: true,
            [UPDATE_KEY]: true
          }
          this.sceneFileTypeMap[templateItem.attachmentType] = templateItem
          if (this.needInit) {
            dataList.push(dataItem)
          }
        }
      }

      const renderNexTick = () => {
        this.onSceneTemplateNextTick && this.onSceneTemplateNextTick(this.dataList)
      }

      if (!this.businessId) {
        if (this.dataList.length === 0) {
          this.dataList = dataList
          this.$nextTick(() => {
            renderNexTick()
          })
        } else {
          // 已有数据调用 赋予KEY值 用于手动渲染的数据
          this.dataList = this.dataList.map(item => {
            return {
              ...item,
              [ADD_KEY]: true,
              [EDITABLE_KEY]: true,
              [UPDATE_KEY]: true
            }
          })
          this.$nextTick(() => {
            renderNexTick()
          })
        }
        return
      }

      const resFile = await sceneFileCompApi.sceneFileListAll({ businessId: this.businessId, sceneModuleCode: this.getPageCode() })
      const list = resFile.data
      for (let i = 0; i < list.length; i++) {
        const attrItem = list[i]
        attrItem['$index'] = this.generateTemplateId()
        attrItem[EDITABLE_KEY] = true
        attrItem[ADD_KEY] = true
        attrItem[UPDATE_KEY] = true
      }
      // this.$refs.tableAttribute.pushRowBatch(dataList)
      this.dataList = list
      this.$nextTick(() => {
        renderNexTick()
      })
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
          // 非默认附件，则判断是否选中，如果是默认附件，则
          if (this.defaultFileDisabled) {
            if (this.dataList[i].defaultFile === 'Y') {
              exist = false
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
        this.$set(scope.row, 'required', null)
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
.text-slot {
  font-size: 12px;
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
}
.file-dynamic-sec{
  .main-header{
    padding: 0 0 10px;
  }
}
</style>
