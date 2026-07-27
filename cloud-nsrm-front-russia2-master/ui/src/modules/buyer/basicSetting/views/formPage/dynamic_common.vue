<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main>
      <template v-for="componentItem in componentArray">
        <component
          :is="componentItem.component"
          :key="componentItem.prop"
          v-model="formData[componentItem.prop]"
          v-bind="componentItem.componentProperty"
          v-on="componentItem.listeners"
        >
          <template
            v-for="(slotItem, slotName) in componentItem.slots"
            #[slotName]="{ scope }"
          >
            <template v-for="subItem in slotItem">
              <component
                :is="subItem.component"
                :key="subItem.prop"
                v-model="formData[subItem.prop]"
                v-bind="subItem.componentProperty"
                v-on="subItem.listeners"
              >
                <template
                  v-for="(slotItem2, slotName) in subItem.slots"
                  #[slotName]="{ scope }"
                >
                  <template v-for="subItem2 in slotItem2">
                    <component
                      :is="subItem2.component"
                      :key="subItem2.prop"
                      v-model="formData[subItem2.prop]"
                      v-bind="subItem2.componentProperty"
                      v-on="subItem2.listeners"
                    >
                      <template
                        v-for="(slotItem3, slotName) in slotItem2.slots"
                        #[slotName]="{ scope }"
                      >
                        <template v-for="subItem3 in slotItem3">
                          <component
                            :is="subItem3.component"
                            :key="subItem3.prop"
                            v-model="formData[subItem3.prop]"
                            v-bind="subItem3.componentProperty"
                            v-on="subItem3.listeners"
                          />
                        </template>
                      </template>
                    </component>
                  </template>
                </template>
              </component>
            </template>
          </template>
        </component>
      </template>
    </el-main>
  </el-container>
</template>

<script>
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from './components/FormWrapper'
import { tabTodoMixin, tabTodoWatch } from '@/utils/mixins'
import ExportExcel from 'lib@/components/export-excel'
import { formPageAPI } from 'modb@/basicSetting/api/basicSetting'
import MainHeader from 'lib@/components/Table/MainHeader'
import ModelInfo from 'modb@/basicSetting/views/formPage/dynamic-render'
import createTreeClass from '@/utils/tree-utils'
import FormItem from './components/FormItem'

function guid () {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
    let r = Math.random() * 16 | 0
    let v = c == 'x' ? r : (r & 0x3 | 0x8)
    return v.toString(16)
  })
}
const PAGE_GUID = 'U' + guid().replace(/-/g, '')
export default {
  name: 'FormPageDynamic',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    FormItem,
    ExportExcel,
    ModelInfo
  },
  mixins: [tabTodoMixin, tabTodoWatch],
  provide () {
    return { context: this }
  },
  data () {
    return {
      modelValue: null,
      componentData: self => {
        return {
          tag: 'div',
          domProps: {
            value: self.value
          },
          slots: ['test view span model value1'],
          children: [
            {
              tag: ModelInfo,
              componentData: self => {
                return {
                  tag: 'input',
                  defaultValue: '',
                  domProps: {
                    value: self.value
                  },
                  slots: ['test view span model value3'],
                  on: {
                    input: value => {
                      self.$emit('input', value)
                    }
                  }
                }
              }
            }
          ],
          on: {
            input: value => {
              self.$emit('input', value)
            }
          }
        }
      },

      formData: {},
      gridId: 'tableList',
      queryParam: {},
      formConfig: [],
      formSlots: [],
      dataList: [],
      tableHeader: [],
      defaultTableHeader: [],
      tableFormGlobal: {},
      requestUrl: null,
      componentArray: [],

      dictCodes: {},
      columnSlots: [],
      dateFilter: [],
      formQuery: {},
      pageCode: '',
      valueAttr: '',
      defaultValues: {},
      dataLimit: {},
      testValue: 'testvaluesss==ss'
    }
  },
  computed: {
  },
  updated () {
    this.defaultTableHeader = this.tableHeader // 动态表头才在updated中这样写
  },
  created () {
    this.defaultTableHeader = this.tableHeader

    window[PAGE_GUID] = this
  },
  mounted () {
    this.queryDynamicConfig()
  },
  activated () {
    this.doLayout()
  },
  methods: {
    getPageCode () {
      let pageCode = null
      if (this.$attrs && this.$attrs.params) {
        pageCode = this.$attrs.params.pageCode
      }
      if (!pageCode) {
        pageCode = this.$route.params.pageCode
      }
      return pageCode
    },
    attributeTransfer (list) {
      const componentMap = {}
      for (let i = 0; i < list.length; i++) {
        const attribute = list[i]
        if (!attribute.viewable) {
          continue
        }
        let parentSlotName = attribute.parentSlotName
        if (!parentSlotName) {
          parentSlotName = 'default'
        }
        let componentSlotItem = {
          component: attribute.elementTag,
          componentProperty: {},
          prop: attribute.propertyName,
          listeners: {},
          isModel: false,
          slotName: parentSlotName,
          attribute: attribute,
          slots: {}
        }
        if (attribute.componentType === 'FORM_ITEM') {
          componentSlotItem.componentProperty['label'] = attribute.propertyDescribe

          if (attribute.formSpan) {
            componentSlotItem.componentProperty['colSpan'] = attribute.formSpan
          }
        }

        if (attribute.dictCode) {
          componentSlotItem.componentProperty['code'] = attribute.dictCode
        }
        if (attribute.dictCode) {
          componentSlotItem.componentProperty['dictCode'] = attribute.dictCode

          if (attribute.componentType === 'DICT_SELECT') {
            componentSlotItem.componentProperty['code'] = attribute.dictCode
            // componentSlotItem.listeners["change"] = (dictValue) => {
            // }
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
        if (attribute.slots) {
          const slotMap = this.attributeTransfer(attribute.slots)
          componentSlotItem.slots = slotMap
        }

        if (!componentMap[parentSlotName]) {
          componentMap[parentSlotName] = []
        }
        componentMap[parentSlotName].push(componentSlotItem)
      }

      return componentMap
    },
    queryDynamicConfig () {
      this.pageCode = this.getPageCode()
      formPageAPI.getPageCode(this.pageCode).then(res => {
        this.componentArray = []
        if (res.data.formAttributeList) {
          const tempAttributeDataList = res.data.formAttributeList
          let selectAttributeDataList = tempAttributeDataList.sort((a, b) => parseInt(!a.sort ? 100000 : a.sort) - parseInt(!b.sort ? 100000 : b.sort))
          selectAttributeDataList = selectAttributeDataList.filter(attribute => attribute.viewable)
          const treeClass = createTreeClass('formAttributeId', 'parentAttributeId', 'slots')
          selectAttributeDataList = treeClass.buildTree(selectAttributeDataList)
          const tempMap = this.attributeTransfer(selectAttributeDataList)
          this.componentArray = tempMap['default']
        }
      })
    },
    doLayout () {
      if (
        this.$refs.table &&
        this.$refs.table.doLayout &&
        typeof this.$refs.table.doLayout === 'function'
      ) {
        this.$refs.table.doLayout()
      }
    },
    getFormData (params = {}) {
      if (!this.tableFormGlobal) {
        return
      }
      if (this.tableFormGlobal.apiType === 'DEFAULT') {
        this.requestUrl = '/api-base/base/form_page/listDynamicGlobal'
      } else if (this.tableFormGlobal.apiType === 'CUSTOM') {
        this.requestUrl = this.tableFormGlobal.apiUrl
      }
      const realParams = params || this.queryParam
      this.queryParam = {
        formGlobalId: this.tableFormGlobal.formGlobalId,
        params: realParams
      }
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    }
  }
}
</script>
