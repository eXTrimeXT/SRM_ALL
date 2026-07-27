<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="formArray"
        @getFormData="getFormData"
        @synchronous-value="syncFilterParams"
      >
        <template
          v-for="item in filterSlots"
          #[item.prop]="{ scope }"
        >
          <component
            :is="item.component"
            :key="item.prop"
            v-model="scope[item.prop]"
            v-bind="item.componentProperty"
          />
        </template>
      </FormWrapper>
      <EasyTable
        v-if="!!columns.length"
        ref="table"
        :initialize="false"
        :selection="false"
        :methods="methods"
        :columns="columns"
        :row-key="valueAttr"
        :table-name="`dynamic_report_page_${sqlCode}_table`"
        :query-params.sync="queryParams"
        :com-active="$attrs['changeTab']"
        :editable="false"
        :open-custom-table="true"
        :customTableKey="`dynamic_report_page_${sqlCode}_table`"
      >
        <template #btns>
          <ExportExcel
            page-url="/api-base/dynamicsql/listByFormCondition"
            export-mode="front"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            :filter-params="expQueryParams"
            :timeout="10000000"
          />
        </template>
        <template
          v-for="item in columnSlots"
          #[item.prop]="{ scope }"
        >
          <el-button
            v-if="item.component === 'pop-content'"
            :key="item.prop"
            type="text"
            @click.stop.prevent="viewContent(item.prop, scope.row)"
          >
            {{ $t('supRisk.readOnly') }}
          </el-button>
          <component
            :is="item.component"
            v-if="item.component === 'el-button'"
            :key="item.prop"
            v-model="scope.row[item.prop]"
            v-bind="item.componentProperty"
            @click.stop.prevent="editLinkTab(item.componentProperty, scope.row)"
            v-html="scope.row[item.prop]"
          />
          <component
            :is="item.component"
            v-else-if="item.component !== 'c-download-link'"
            :key="item.prop"
            v-model="scope.row[item.prop]"
            v-bind="item.componentProperty"
          />
          <component
            :is="item.component"
            v-else
            :id="scope.row[item.prop]"
            :key="item.prop"
            v-bind="item.componentProperty"
            :name="scope.row[item.prop]"
          />
        </template>
      </EasyTable>
    </el-main>
    <srm-dialog
      :title="$t('reportMod.viewContent')"
      :visible.sync="dialogVisible"
      size="middle"
    >
      <div class="filedContent" v-html="filedContent" />
    </srm-dialog>
  </el-container>
</template>

<script>
import EasyTable from 'lib@/components/BaseTable/EasyTable'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { tabTodoMixin, tabTodoWatch } from '@/utils/mixins'
import camelCase from 'lodash/camelCase'
import ExportExcel from 'lib@/components/export-excel'
import OrganizationSelector from 'lib@/components/organization-selector'
import QuickSearch from 'lib@/components/QuickSearch'
import vendorProfileDetailRead from 'modb@/vendorManagementBuyer/views/vendorProfile/vendorProfileDetailRead'
import quotaflowEdit from 'modb@/quotaManagement/views/quotaoffset/edit'
import { dynamicSqlApi } from 'modb@/basicSetting/api/basicSetting'
import { createDictClass } from '@/library/utils/dict/dict-utils'

const DETAIL_COMPONENTS = {
  'vendorProfileDetailRead': vendorProfileDetailRead,
  'quotaflowEdit': quotaflowEdit
}

const dictClass = createDictClass()
export default {
  name: 'DynamicReportPageList',
  components: {
    EasyTable,
    FormWrapper,
    ExportExcel,
    OrganizationSelector,
    QuickSearch
  },
  mixins: [tabTodoMixin, tabTodoWatch],
  data () {
    return {
      dictClass: dictClass,
      queryParams: {},
      expQueryParams: {},
      formArray: [],
      dictCodes: {},
      tableHeader: [],
      columnSlots: [],
      filterSlots: [],
      dateFilter: [],
      paramMap: {},
      formQuery: {},
      sqlCode: '',
      valueAttr: '',
      defaultValues: {},
      dataLimit: {},
      columns: [],
      methods: {
        listPage: async params => {
          if (!params.queryParam) {
            params.queryParam = {}
          }
          const res = await dynamicSqlApi.listByFormCondition({
            ...params,
            sqlKey: this.getSqlCode()
          })
          return res
        }
      },
      dialogVisible: false,
      filedContent: ''
    }
  },
  computed: {
    id () {
      return this.getSqlCode()
    }
  },
  mounted () {
    this.queryDynamicConifg()
  },
  activated () {
    this.dolayout()
  },
  methods: {
    syncFilterParams (values) {
      let queryData = this.transformQuery(values)
      this.expQueryParams.queryParam = queryData
      this.expQueryParams.sqlKey = this.getSqlCode()
    },
    getSqlCode () {
      let sqlCode = null
      if (this.$attrs && this.$attrs.params) {
        sqlCode = this.$attrs.params.sqlCode
      }
      if (!sqlCode) {
        let path = this.$route.path.split('/')
        sqlCode = path[path.length - 1]
      }
      return sqlCode
    },
    queryDynamicConifg () {
      this.sqlCode = this.getSqlCode()
      dynamicSqlApi.getByName(this.sqlCode).then(res => {
        const { attrs = [], valueAttr } = res.data
        this.valueAttr = camelCase(valueAttr) // 显示记录的主键
        const config = attrs.reduce(
          (last, item) => {
            const {
              displayItemEnabled,
              queryItemEnabled,
              title,
              componentProperty,
              componentType,
              attr,
              alias,
              attrOrder
            } = item
            let prop = camelCase(attr) // 表格的属性：驼峰，
            let formProp = alias ? alias + '_' + attr : attr // 表单Prop属性
            let propAlias = alias ? alias + '.' + attr : attr
            this.paramMap[formProp] = propAlias

            const compProps = JSON.parse(
              componentProperty || '{}'
            )
            if (compProps.dataLimit) {
              last.dataLimit[prop] = compProps.dataLimit
            }
            if (componentType === 'DICTIONARY' && compProps.code) {
              this.dictCodes[prop] = compProps.code
            }
            // 表格显示
            if (displayItemEnabled === 'Y') {
              const obj = {
                attrs: { label: title, prop, sortable: true },
                attrOrder: attrOrder
              }
              switch (componentType) {
              // 字典
              case 'DICTIONARY':
                obj.attrs.formatter = value =>
                  this.dictClass.getDictLabel(compProps.code, value)
                break
                // 按钮链接
              case 'LINK':
                obj.slot = prop
                last.columnSlots.push({
                  component: 'el-button',
                  componentProperty: { type: 'text', ...compProps },
                  callBack: row => {
                    this.editLinkTab(compProps, row)
                  },
                  prop
                })
                break
                // 下载
              case 'DOWNLOAD':
                obj.slot = prop
                last.columnSlots.push({
                  component: 'c-download-link',
                  componentProperty: compProps,
                  prop
                })
                break
                // 弹出内容
              case 'POPCONTENT':
                obj.slot = prop
                last.columnSlots.push({
                  component: 'pop-content',
                  componentProperty: compProps,
                  prop
                })
                break
              default:
              }
              last.columns.push(obj)
            }
            // 条件查询
            if (queryItemEnabled === 'Y') {
              const obj = { label: title, prop: formProp, propAlias: propAlias }
              switch (componentType) {
              // 日期
              case 'DATE':
                last.dateFilter.push(formProp)
                last.filterSlots.push({
                  component: 'el-date-picker',
                  componentProperty: {
                    type: 'daterange',
                    'value-format': 'yyyy-MM-dd',
                    'range-separator': '~',
                    'start-placeholder': this.$t('dataConfMod.startDay'), // 开始日期
                    'end-placeholder': this.$t('dataConfMod.endDay'), // 结束日期
                    ...compProps
                  },
                  prop: formProp
                })
                obj.slot = formProp
                obj.type = 'slot'
                break
                // 字典
              case 'DICTIONARY':
                obj.slot = formProp
                obj.type = 'slot'
                last.filterSlots.push({
                  component: 'DictSelect',
                  componentProperty: { ...compProps },
                  prop: formProp
                })
                break
                // 快查
              case 'QUICKSEARCH':
                obj.type = 'quicksearch'
                break
                // 组织组件
              case 'ORGANIZATION':
                obj.slot = formProp
                obj.type = 'slot'
                last.filterSlots.push({
                  component: 'organization-selector',
                  componentProperty: { ...compProps },
                  prop: formProp
                })
                break
              default:
              }
              last.filters.push({ ...obj, ...compProps })
            }
            return last
          },
          {
            columns: [],
            filters: [],
            filterSlots: [],
            columnSlots: [],
            dateFilter: [],
            dataLimit: {}
          }
        )
        // 渲染表格
        this.columns = config.columns.sort((a, b) => parseInt(!a.attrOrder ? 100 : a.attrOrder) - parseInt(!b.attrOrder ? 100 : b.attrOrder))
        console.log(this.columns)
        this.filterSlots = config.filterSlots
        this.columnSlots = config.columnSlots
        // 查询条件
        this.formArray = config.filters
        console.log(this.paramMap)
        this.defaultValues = config.filters.reduce((l, i) => {
          const { compProps } = i
          if (compProps && (compProps.defaultValue || compProps.defaultValue === 0)) {
            l[i.prop] = compProps.defaultValue
          }
          return l
        }, {})
        this.dataLimit = config.dataLimit
        this.dateFilter = config.dateFilter
        // 格式化表头配置
        for (let i = 0; i < this.columns.length; i++) {
          const columnItem = this.columns[i]
          if (columnItem.attrs.needAllAttr) {
            this.tableHeader.push(columnItem)
          } else {
            this.tableHeader.push({
              prop: columnItem.attrs.prop,
              label: columnItem.attrs.label
            })
          }
        }
        this.$nextTick(() => this.getFormData())
      })
    },
    editLinkTab (customProps, row) {
      const componentInfo = DETAIL_COMPONENTS[customProps.componentName]
      const params = Object.assign({}, { tabName: customProps.componentName + row[customProps.tabNameKey] }, customProps.paramsStatic)
      if (customProps.paramsKeyMap) {
        for (const paramKey in customProps.paramsKeyMap) {
          params[paramKey] = row[paramKey]
        }
      }
      if (customProps.rowParamName) {
        params[customProps.rowParamName] = row
      }
      const tagArgs = {
        component: componentInfo,
        params: params,
        title: row[customProps.titleKey],
        name: customProps.componentName + row[customProps.tabNameKey]
      }
      this.$emit('tab-add', tagArgs)
    },
    dolayout () {
      if (
        this.$refs.table &&
        this.$refs.table.doLayout &&
        typeof this.$refs.table.doLayout === 'function'
      ) {
        this.$refs.table.doLayout()
      }
    },
    // 字段转化
    transformQuery (obj = {}) {
      const queryParam = {}
      Object.keys(obj).forEach(key => {
        const currentKey = this.paramMap[key] ? this.paramMap[key] : key
        // 控件类型为时间的查询参数，格式化 startTime,endTime
        if (this.dateFilter.includes(key)) {
          if (Array.isArray(obj[key])) {
            const [start, end] = obj[key]
            queryParam[currentKey] = `${start},${end}`
          }
        } else {
          queryParam[currentKey] = obj[key]
        }
      })
      Object.keys(this.defaultValues).forEach(key => {
        if (!Object.keys(obj).includes(key)) {
          const currentKey = this.paramMap[key] ? this.paramMap[key] : key
          queryParam[currentKey] = this.defaultValues[key]
        }
      })
      Object.keys(this.dataLimit).forEach(key => {
        const currentKey = this.paramMap[key] ? this.paramMap[key] : key
        queryParam[currentKey] = this.$store.getters.user.userInfo[this.dataLimit[key]]
      })
      return queryParam
    },
    getFormData (params = {}) {
      let queryData = this.transformQuery(params)
      this.formQuery = queryData
      const querys = { pageSize: params.pageSize, pageNum: params.pageNum, queryParam: queryData }
      this.$refs.table.search(querys)
    },
    viewContent (prop, row) {
      this.filedContent = row[prop]
      this.dialogVisible = true
    }
  }
}
</script>

<style lang="scss" scoped>
.filedContent{
  padding:0 0 20px 0;
  img{
    max-width: 100%;
  }
}
</style>
