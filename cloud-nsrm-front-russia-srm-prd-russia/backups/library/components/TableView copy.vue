<template>
  <el-container
    direction="vertical"
    style="min-height: 0;"
    class="the_TableView"
  >
    <div
      class="table-wrapper"
      :style="{ height: tableHeight, maxHeight: tableMaxHeight }"
    >
      <el-table
        ref="tableGrid"
        v-loading="loading"
        :stripe="stripe"
        :fit="fitTable"
        border
        highlight-current-row
        size="mini"
        :data="
          frontPaging
            ? tableData.slice((currentPage - 1) * currentPageSize, currentPage * currentPageSize)
            : tableData
        "
        :element-loading-text="$t('components.table.msgFaster')"
        element-loading-spinner="el-icon-loading"
        element-loading-background="rgba(0, 0, 0, 0.3)"
        :default-expand-all="expandAll"
        lazy
        :tree-props="treeProps"
        style="flex: 1;"
        height="100%"
        :header-cell-class-name="headerCellClassName"
        :row-class-name="rowClass"
        :row-key="rowKey"
        :load="load"
        :cell-class-name="cellClass"
        :cell-style="cellStyle"
        :header-cell-style="headerCellStyle"
        :span-method="spanMethod"
        @current-change="currentChange"
        @row-dblclick="rowDblclick"
        @selection-change="checkChange"
        @cell-click="cellclick"
        @row-click="rowClick"
        @select="selectRow"
        @header-dragend="headerDragend"
      >
        <el-table-column
          v-if="checkbox"
          type="selection"
          align="center"
          :reserve-selection="reserveSelection"
          :selectable="setSelectable"
          :fixed="rowIndexFixed"
        />
        <!-- 序号 -->
        <el-table-column
          v-if="rowIndex"
          class="index-row"
          type="index"
          align="left"
          :label="$t('common.sort')"
          :fixed="rowIndexFixed"
          width="58px"
        />
        <template v-for="(col, key) in innerHeader">
          <el-table-column
            v-if="!col.hidden && !col.children"
            :key="key"
            :prop="col.dataType === 'dict' ? col.prop + 'View' : col.prop"
            :sortable="col.showType === 'buttons' || col.unsortable ? false : true"
            :label="typeof col.label === 'function' ? col.label() : col.label"
            :fixed="col.fixed ? col.fixed : false"
            :min-width="col.minWidth"
            :width="col.width"
            :align="col.align ? col.align : 'left'"
            :header-align="col.headerAlign ? col.headerAlign : 'left'"
            :show-overflow-tooltip="col.prop === 'operation' ? false : col.showOverflowTooltip ? (typeof col.showOverflowTooltip === 'function' ? col.showOverflowTooltip() : col.showOverflowTooltip) : true"
            :class-name="col.class"
          >
            <template
              v-if="col.desc || col.addStarToColumn"
              slot="header"
            >
              <!-- v-html="" -->
              <span>
                <i
                  v-if="col.addStarToColumn"
                  style="color:red"
                >*</i>
                {{ typeof col.label === 'function' ? col.label() : col.label }}
              </span>
              <el-tooltip
                v-if="col.desc"
                class="item"
                effect="dark"
                :content="col.desc"
                placement="top"
              >
                <i
                  style="font-size: 14px;margin-left: 3px;cursor: help;"
                  class="el-icon-question"
                />
              </el-tooltip>
            </template>
            <template slot-scope="scope">
              <template v-if="col.showType === 'button'">
                <AuthorityButton
                  v-if="col.show ? col.show(scope.row) : true"
                  :type="col.btnStyle ? col.btnStyle : 'primary'"
                  :icon="col.icon"
                  :loading="col.loading"
                  :code="col.code"
                  @click.stop.prevent="callback(col, scope.row, scope)"
                  @contextmenu.prevent.native="copyValue(col, scope.row)"
                  v-html="formatter(scope, col)"
                />
                <span
                  v-else
                  v-html="formatter(scope, col)"
                />
              </template>
              <template v-else-if="col.showType === 'slot' && col.slot">
                <slot
                  v-if="col.show ? col.show(scope.row) : true"
                  :name="col.slot"
                  :scope="scope"
                />
                <span
                  v-else
                  v-html="formatter(scope, col)"
                />
              </template>
              <template v-else-if="col.showType === 'switch'">
                <el-switch
                  v-model="scope.row[col.prop]"
                  :inactive-value="col.switchValues.inactive"
                  :active-value="col.switchValues.active"
                  :disabled="col.disabled"
                  @change="callback(col, scope.row)"
                />
              </template>
              <template v-else-if="col.showType === 'input'">
                <el-input
                  v-if="col.editable ? col.editable(scope.row) : true"
                  v-model="scope.row[col.prop]"
                  :type="col.inputType || 'string'"
                  @change="callback(col, scope.row)"
                />
                <span
                  v-else
                  v-html="formatter(scope, col)"
                />
              </template>
              <template v-else-if="col.showType === 'inputNumber'">
                <el-input-number
                  v-if="col.editable ? col.editable(scope.row) : true"
                  v-model="scope.row[col.prop]"
                  :min="
                    col.min ? (typeof col.min === 'function' ? col.min(scope.row) : col.min) : 0
                  "
                  :max="
                    col.max ? (typeof col.max === 'function' ? col.max(scope.row) : col.max) : null
                  "
                  @change="callback(col, scope.row)"
                />
                <span
                  v-else
                  v-html="formatter(scope, col)"
                />
              </template>
              <template v-else-if="col.showType === 'select'">
                <el-select
                  v-if="col.editable ? col.editable(scope.row) : true"
                  v-model="scope.row[col.prop]"
                  :type="col.inputType || 'string'"
                  @change="callback(col, scope.row, scope)"
                >
                  <el-option
                    v-for="item in col.options"
                    :key="item.id"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
                <span
                  v-else
                  v-html="formatter(scope, col, scope)"
                />
              </template>
              <template v-else-if="col.showType === 'dictSelect'">
                <dictSelect
                  v-if="col.editable ? col.editable(scope.row) : true"
                  v-model="scope.row[col.prop]"
                  :code="col.code"
                  @change="callback(col, scope.row, scope)"
                />
                <span
                  v-else
                  v-html="formatter(scope, col, scope)"
                />
              </template>
              <template v-else-if="col.showType === 'date'">
                <el-date-picker
                  v-if="col.editable ? col.editable(scope.row) : true"
                  v-model="scope.row[col.prop]"
                  :type="col.type"
                  :placeholder="col.placeholder || $t('common.selectDate')"
                  :format="col.format || 'yyyy-MM-dd'"
                  :value-format="col.valueFormat || 'yyyy-MM-dd'"
                  :picker-options="col.pickerOptions && typeof col.pickerOptions === 'function' ? col.pickerOptions(scope) : col.pickerOptions"
                  @change="callback(col, scope.row, scope)"
                />
                <span
                  v-else-if="!!col.formatter"
                  v-html="formatter(scope, col)"
                />
                <span v-else>{{ $dayjs(scope.row[col.prop]).format('YYYY-MM-DD') }}</span>
              </template>
              <template v-else-if="col.showType === 'buttons'">
                <el-button-group>
                  <template v-for="button in col.buttons">
                    <AuthorityButton
                      v-if="button.show ? button.show(scope.row) : true"
                      :key="button.text"
                      style="margin:0 3px;"
                      :code="button.code"
                      :disabled="button.disabled"
                      :type="button.btnStyle ? button.btnStyle : 'text'"
                      @click.stop.prevent="callback(button, scope.row, scope)"
                      v-html="formatter(scope, button)"
                    />
                  </template>
                </el-button-group>
              </template>
              <template v-else-if="col.showType === 'OUorganizationSelector'">
                <organization-selector
                  v-if="col.editable ? col.editable(scope.row) : true"
                  ref="ouSelector"
                  v-model="scope.row[col.prop]"
                  :parent-id="-1"
                  node-type="OU"
                  :placeholder="$t('common.pleaseSelect')"
                  :multiple="col.multiple"
                  :collapse-tags="col.collapseTags"
                />
                <span
                  v-else
                  v-html="formatter(scope, col)"
                />
              </template>
              <template v-else-if="col.showType === 'quicksearch'">
                <quick-search
                  v-if="col.editable ? col.editable(scope.row) : true"
                  ref="quickSearchTool"
                  :show-key="col.showKey"
                  :scope-data="scope.row"
                  :show-input="scope.row[col.prop]"
                  :name="col.name"
                  :pre-query-data="col.preQueryData"
                  :disabled="col.disabled"
                  @close-quicksearch="col.getObj($event, scope.row, col.prop, scope)"
                />
                <span
                  v-else
                  v-html="formatter(scope, col)"
                />
              </template>
              <template v-else-if="col.showType === 'statusCol'">
                <span
                  :class="getStyle(scope, col)"
                  v-html="formatter(scope, col)"
                />
              </template>
              <span
                v-else
                v-html="formatter(scope, col)"
              />
            </template>
          </el-table-column>
          <el-table-column
            v-if="col.children && col.children.length > 0"
            :key="key"
            :prop="col.prop"
            :label="typeof col.label === 'function' ? col.label() : col.label"
            :fixed="col.fixed ? col.fixed : false"
            :min-width="col.minWidth"
            :width="col.width"
            :align="col.align ? col.align : 'center'"
            :header-align="col.headerAlign ? col.headerAlign : 'center'"
            :show-overflow-tooltip="true"
          >
            <template
              v-if="col.desc || col.addStarToColumn"
              slot="header"
            >
              <!-- v-html="" -->
              <span>
                <i
                  v-if="col.addStarToColumn"
                  style="color:red"
                >*</i>
                {{ typeof col.label === 'function' ? col.label() : col.label }}
              </span>
              <el-tooltip
                v-if="col.desc"
                class="item"
                effect="dark"
                :content="col.desc"
                placement="top"
              >
                <i
                  style="font-size: 14px;margin-left: 3px;cursor: help;"
                  class="el-icon-question"
                />
              </el-tooltip>
            </template>

            <template v-for="(value, k) in col.children">
              <el-table-column
                :key="k"
                :prop="value.prop"
                :sortable="value.showType === 'buttons' ? false : true"
                :label="typeof value.label === 'function' ? value.label() : value.label"
                :fixed="value.fixed ? value.fixed : false"
                :min-width="value.minWidth"
                :width="value.width"
                :align="value.align ? value.align : 'center'"
                :header-align="value.headerAlign ? value.headerAlign : 'center'"
                :show-overflow-tooltip="true"
              >
                <template
                  v-if="value.desc || value.addStarToColumn"
                  slot="header"
                >
                  <span>
                    <i
                      v-if="value.addStarToColumn"
                      style="color:red"
                    >*</i>
                    {{ typeof value.label === 'function' ? value.label() : value.label }}
                  </span>
                  <el-tooltip
                    v-if="value.desc"
                    class="item"
                    effect="dark"
                    :content="value.desc"
                    placement="top"
                  >
                    <i
                      style="font-size: 14px;margin-left: 3px;cursor: help;"
                      class="el-icon-question"
                    />
                  </el-tooltip>
                </template>
                <template slot-scope="scope">
                  <template v-if="value.showType === 'input'">
                    <el-input
                      v-if="value.editable ? value.editable(scope.row) : true"
                      v-model="scope.row[value.prop]"
                      :type="value.inputType || 'string'"
                      @change="callback(value, scope.row)"
                    />
                    <span
                      v-else
                      v-html="formatter(scope, value)"
                    />
                  </template>
                  <template v-else-if="value.showType === 'slot' && value.slot">
                    <slot
                      v-if="value.show ? value.show(scope.row) : true"
                      :name="value.slot"
                      :scope="scope"
                    />
                    <span
                      v-else
                      v-html="formatter(scope, value)"
                    />
                  </template>
                  <template v-else-if="col.showType === 'statusCol'">
                    <span
                      :class="getStyle(scope, col)"
                      v-html="formatter(scope, col)"
                    />
                  </template>
                  <span
                    v-else
                    v-html="formatter(scope, value)"
                  />
                </template>
              </el-table-column>
            </template>
          </el-table-column>
        </template>
      </el-table>
    </div>

    <el-footer
      v-if="pageEnabled"
      class="page-bar"
    >
      <el-row>
        <!-- 全选所有 -->
        <el-col
          v-if="openAllCheck && frontPaging"
          :span="4"
        >
          <el-checkbox
            v-model="allCheck"
            @change="allCheckEvent"
          >
            {{ $t('components.table.selectAll') }}
          </el-checkbox>
        </el-col>
        <el-col :span="openAllCheck && frontPaging ? 20 : 24">
          <c-pagination
            ref="queryPagination"
            style="margin: 0"
            class="c-query-table-pagination"
            :total="queryTotal"
            :page-num="viewIndex"
            :page-size="viewSize"
            @current-change="changeCurrentIndex"
            @size-change="changeCurrentSize"
          />
        </el-col>
      </el-row>
    </el-footer>
    <custom-table
      v-if="openCustomTable"
      class="custom-table"
      :table-config-change="tableConfigChange"
    />
  </el-container>
</template>

<script>
import { debounce, isNull } from '@/utils'
import FilterColumn from './FilterHeaderColumn'
import { formatNumber } from '@/utils/util'
import HeaderSort from './HeaderSort'
import CPagination from 'lib@/components/c-pagination'

import CustomTable from 'lib@/components/custom-table'
import http from '@/utils/http'
import { STORE_COMMON_CACHE } from '@/config/store-config'
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'

export default {
  name: 'TableView',
  components: {
    // eslint-disable-next-line vue/no-unused-components
    FilterColumn,
    // eslint-disable-next-line vue/no-unused-components
    HeaderSort,
    CustomTable,
    CPagination,
    OrganizationSelector,
    QuickSearch
  },
  props: {
    // 是否前端分页
    frontPaging: {
      type: Boolean,
      default: false
    },
    // 是否显示全选所有
    openAllCheck: {
      type: Boolean,
      default: false
    },
    stripe: {
      type: Boolean,
      default: true
    },
    openCustomTable: {
      type: Boolean,
      default: false
    },
    tableName: {
      required: false,
      type: String
    },
    autoQuery: {
      type: Boolean,
      default: function () {
        return false
      }
    },
    requestMethod: {
      // HTTP请求方式---默认是post,
      type: String,
      default: function () {
        return 'post'
      }
    },
    url: {
      // 查询数据url
      type: String,
      default: function () {
        return ''
      }
    },
    source: {
      type: Function,
      default: null
    },
    transformData: {
      type: Function,
      default: null
    },
    fitTable: {
      // 列自适应
      type: Boolean,
      default: function () {
        return true
      }
    },
    pageEnabled: {
      type: Boolean,
      default: function () {
        return true
      }
    },
    pageIndex: {
      // 当前页码,从1开始
      type: Number,
      default: function () {
        return 1
      }
    },
    pageSize: {
      // 一页数量
      type: Number,
      default: function () {
        return 15
      }
    },
    pageStep: {
      // 页数递增步数
      type: Array,
      default: function () {
        return []
      }
    },
    preQueryData: {
      // 前置查询条件
      type: Object,
      default: function () {
        return {}
      }
    },
    postQueryData: {
      // 后置查询条件
      type: Object,
      default: function () {
        return {}
      }
    },
    rowDblclick: {
      // 行双击事件
      type: Function,
      default: (row, event, column) => {
        console.log('default: ' + row + '---' + event + '---' + column)
      }
    },
    cellclick: {
      // 单元格事件
      type: Function,
      default: (row, column, cell, event) => {
        // console.log('default: ' + row + '---' + column + '---' + cell)
      }
    },
    currentChange: {
      // 选中行改变事件
      type: Function,
      default: val => {}
    },
    tableHeader: {
      // 表头数据
      type: Array,
      default: function () {
        return []
      }
    },
    showFilterBar: {
      // 是否显示过滤行
      type: Boolean,
      default: false
    },
    checkbox: {
      type: Boolean,
      default: false
    },
    rowClickCheck: {
      type: Boolean,
      default: function () {
        return true
      }
    },
    tableHeight: {
      type: String,
      default: '100%'
    },
    checkChange: {
      type: Function,
      default: function () {
        return null
      }
    },
    // CheckBox 是否可以勾选
    setSelectable: {
      type: Function,
      default: function () {
        return true
      }
    },
    // 返回row的class方法
    rowClass: {
      type: Function,
      default: function () {
        return true
      }
    },
    // 表头样式
    headerCellClassName: {
      type: [Function, String],
      default: null
    },
    treeProps: {
      type: Object,
      default: function () {
        return {}
      }
    },
    // 行数据的 Key，值须唯一, 类型string或者是function
    rowKey: {
      type: [String, Function],
      default: null
    },
    // 跨页选择
    reserveSelection: {
      type: Boolean,
      default: false
    },
    // 加载子节点数据的函数
    load: {
      type: Function,
      default: function () {
        return null
      }
    },
    // 分页参数名称
    laypage: {
      type: Object,
      default: () => ({
        pageNum: 'pageNum',
        pageSize: 'pageSize'
      })
    },
    apiInfo: {
      type: Object,
      default: function () {
        return {
          LIST_API: '',
          UPDATE_API: '',
          DELETE_API: '',
          ADD_API: '',
          ACTIVE_API: '',
          ABANDON_API: '',
          EXPAND_API: ''
        }
      }
    },
    // 单元格的 className 的回调方法
    cellClass: {
      type: Function,
      default: function () {
        return null
      }
    },
    // 单元格的 style 的回调方法
    cellStyle: {
      type: Function,
      default: function () {
        return null
      }
    },
    expandAll: {
      type: Boolean,
      default: false
    },
    rowIndex: {
      type: Boolean,
      default: true
    },
    rowIndexFixed: {
      type: Boolean,
      default: true
    },
    // data
    tableInfor: {
      type: Array,
      default: function () {
        return []
      }
    },
    // 表格最大高度
    tableMaxHeight: {
      type: String,
      default: '100%'
    },
    // 组件激活
    comActive: {
      type: [String, Number],
      default: null
    },
    // 表格表头样式
    headerCellStyle: {
      type: Function,
      required: false
    },
    // 表格合并行
    spanMethod: {
      type: Function,
      required: false
    }
  },
  provide () {
    return { context: this }
  },
  data () {
    return {
      $_resizeHandler: null,
      allCheck: false, // 全选所有 只有在开启前端分页时可用
      loading: false,
      queryData: {}, // 查询条件存储
      tableData: [], // 表格数据
      sortData: {},
      dataCount: 0,
      queryTotal: 0,
      viewSize: this.pageSize,
      viewStep: this.pageStep,
      viewIndex: this.pageIndex,
      innerHeader: [],
      currentRow: null,
      showFilter: this.showFilterBar,
      currentPageSize: this.pageSize,
      currentPage: 1
    }
  },
  computed: {
    defaultTableHeader () {
      return this.tableHeader.map(item => ({
        label: typeof item.label === 'function' ? item.label() : item.label,
        prop: item.prop
      }))
    }
  },
  watch: {
    tableHeader: {
      immediate: true,
      handler: function (n, o) {
        if (JSON.stringify(n) !== JSON.stringify(o)) {
          // 去掉是因为加上的话,自定义表格的tableConfigChange会先执行，然后再执行innerHeader的赋值初次进入界面还是会使用tableHeader的值，不会使用缓存的loacalstorage的值
          // this.$nextTick(() => {
          this.innerHeader = n
          this.loadDict(this.tableData)
          // })
        }
      },
      deep: true
    },
    showFilterBar: function (data) {
      this.showFilter = data
    },
    comActive: {
      handler: function (n, o) {
        if (n) {
          this.doLayout()
        }
      },
      deep: true
    }
  },
  mounted () {
    if (!this.url) {
      this.$nextTick(async () => {
        if (this.tableInfor) {
          await this.loadDict(this.tableInfor)
        }
        this.tableData = this.tableInfor || []
        this.$refs.tableGrid.doLayout() // 暂时解决表格错位的问题
      })
    }
    this.$_resizeHandler = debounce(() => {
      if (this.$refs.tableGrid) {
        this.$refs.tableGrid.doLayout()
      }
    }, 100)

    this.$_initResizeEvent()
  },
  created () {
    if (this.autoQuery) {
      this.query()
    }
  },
  beforeUpdate () {
    this.queryData = Object.assign({}, this.queryData)
  },
  activated () {
    this.doLayout()
  },
  beforeDestroy () {
    this.$_destroyResizeEvent()
  },
  methods: {
    $_initResizeEvent () {
      window.addEventListener('resize', this.$_resizeHandler)
    },
    $_destroyResizeEvent () {
      window.removeEventListener('resize', this.$_resizeHandler)
    },
    doLayout () {
      if (this.$refs.tableGrid) {
        this.$refs.tableGrid.doLayout()
      }
    },
    async tableConfigChange (config) {
      if (!config || !config.length) {
        this.innerHeader = this.tableHeader.filter(i => !!i)
      } else {
        this.innerHeader = config
          .filter(i => i.show)
          .map(i => {
            return this.tableHeader.find(j => j.prop === i.prop)
          })
          .filter(i => !!i)
        this.innerHeader.forEach(i => {
          if (i.showType !== 'buttons' && i.width) {
            i.minWidth = i.width
            delete i.width
          }
        })
      }
      await this.loadDict(this.tableData)
      this.$nextTick(() => {
        this.$refs.tableGrid.doLayout() // 暂时解决表格错位的问题
      })
    },
    addOneEditableColumn (row = { editable: true, isNew: true }) {
      this.tableData.unshift(row)
      this.$nextTick(() => {
        this.$refs.tableGrid.doLayout() // 暂时解决表格错位的问题
      })
    },
    deleteRow (index) {
      this.tableData.splice(index, 1)
    },
    setTableData (callback) {
      callback(this.tableData)
    },
    callback (col, row, scope) {
      if (col.callback) {
        col.callback(row, scope)
      }
    },
    getParam () {
      let param = Object.assign({}, this.preQueryData, this.queryData, this.postQueryData)

      if (this.pageEnabled) {
        param.__page = this.viewIndex
        param.__pagesize = this.viewSize
      }

      return param
    },

    query () {
      this.viewIndex = 1 // 非分页触发查询，页码跳回首页
      this.queryTotal = 0
      let queryParam = this.getParam()
      this.find(queryParam)
      this.$refs.tableGrid.clearSelection() // 按钮查询的时候清空已选的数据
    },
    find (queryParam) {
      this.reloadData(queryParam)
    },

    // 改变 currentNum
    changeCurrentIndex (currentNum) {
      this.$emit('getFooter', { value: currentNum })
      if (this.frontPaging) {
        this.currentPage = currentNum
        return
      }
      let queryParam = this.getParam()
      queryParam.__pagesize = this.currentPageSize
      queryParam.__page = currentNum

      this.reloadData(queryParam)
    },
    // 改变 currentSize
    changeCurrentSize (currentSize) {
      this.$emit('getFooterSize', { value: currentSize })
      if (this.frontPaging) {
        this.currentPageSize = currentSize
        return
      }
      let queryParam = this.getParam()
      this.currentPageSize = currentSize
      this.viewSize = currentSize
      queryParam.__pagesize = currentSize
      queryParam.__page = 1

      this.reloadData(queryParam)
    },
    reloadData (queryParam) {
      this.loading = true
      let paramsExt = Object.assign(
        {
          [this.laypage.pageNum]: queryParam.__page,
          [this.laypage.pageSize]: queryParam.__pagesize
        },
        queryParam
      )

      var promiseInfo = null
      if (this.url) {
        if (this.requestMethod.toLowerCase() === 'post'.toLowerCase()) {
          promiseInfo = http({
            url: this.url,
            method: this.requestMethod,
            data: paramsExt
          })
        } else {
          // get
          promiseInfo = http({
            url: this.url,
            method: this.requestMethod,
            params: paramsExt
          })
        }
      } else if (this.source) {
        promiseInfo = this.source(paramsExt)
      } else if (this.apiInfo.LIST_API) {
        promiseInfo = this.$store.dispatch(this.apiInfo.LIST_API, paramsExt)
      }
      if (!promiseInfo) {
        this.loading = false
        return
      }
      promiseInfo
        .then(async data => {
          if (this.source) {
            if (this.transformData && typeof this.transformData === 'function') {
              data = this.transformData(data)
            }
          }

          if (data && data.data) {
            if (data.data.list == null || data.data.list.length === 0) {
              this.dataCount = 0
              this.tableData = []
            } else {
              await this.loadDict(data.data.list)
              this.tableData = data.data.list
              this.dataCount = data.data.total
              this.queryTotal = data.data.total
            }
            this.loading = false
            this.$emit('afterQuery', this.tableData) // afterQuery 事件
          }
        })
        .catch(err => {
          console.log(err)
          this.loading = false
          this.$emit('afterQuery', err) // afterQuery 事件
        })
        .finally(() => {
          this.$nextTick(() => {
            this.$refs.tableGrid.doLayout() // 暂时解决表格错位的问题
          })
        })
    },
    async loadDict (tableData) {
      var dictSet = []
      for (let i = 0; i < this.innerHeader.length; i++) {
        var columnItem = this.innerHeader[i]
        if (columnItem.dataType === 'dict') {
          dictSet.push(columnItem.code)
        } else if (columnItem.showType === 'dictSelect') {
          dictSet.push(columnItem.code)
        }
      }
      if (dictSet.length === 0) {
        return
      }
      const dictBatchMap = await this.$store.dispatch(STORE_COMMON_CACHE.LIST_DICT_BATCH, {
        dictCodeList: dictSet
      })
      // console.log(dictBatchMap)
      for (let i = 0; i < this.innerHeader.length; i++) {
        var columnItemHeader = this.innerHeader[i]
        if (columnItemHeader.dataType === 'dict' || columnItemHeader.showType === 'dictSelect') {
          for (let j = 0; j < tableData.length; j++) {
            if (!dictBatchMap[columnItemHeader.code]) {
              continue
            }
            var label = dictBatchMap[columnItemHeader.code][tableData[j][columnItemHeader.prop]]
            // var label = await this.$store.dispatch(STORE_COMMON_CACHE.LIST_DICT_DETAIL, { dictCode: columnItemHeader.code, dictItemCode: tableData[j][columnItemHeader.prop] })
            tableData[j][columnItemHeader.prop + 'View'] = label
          }
        }
      }
    },
    getTableData () {
      return [...this.tableData]
    },
    getStyle (scope, col) {
      let styleClass = ''
      let value = scope.row[col.prop]
      let statusList = col.statusList || []
      styleClass = this.$getStatusClass(statusList, value)
      return styleClass
    },
    formatter (scope, col) {
      let icon = ''
      let text = ''
      if (col.hasOwnProperty('formattor')) {
        text = col.formattor(col.prop ? this.getPropData(scope.row, col) : col.label, scope.row)
      } else {
        text = col.prop ? this.getPropData(scope.row, col) : ''
      }

      if (col.icon) {
        icon = '<i class="' + col.icon + '"></i>'
      }
      if (!isNull(text)) {
        if (text && col.openThousand) {
          text = formatNumber(text, col.fixBit)
          return icon + '<span>' + text + '</span>'
        }
        if (text && col.fixBit) {
          text = Number(text).toFixed(col.fixBit)
          return icon + '<span>' + text + '</span>'
        }
        return icon + '<span>' + text + '</span>'
      } else {
        return icon
      }
    },
    // 获取属性对应的值
    getPropData (data, col) {
      let props = (col.mapProp || col.prop).split('.')
      let propData = data || {}
      let getRow = !!col.getRow // 格式的时候需要用到row
      let relObj = {}

      if (col.dataType === 'dict') {
        return propData[col.prop + 'View']
      }
      if (col.showType === 'dictSelect') {
        return propData[col.prop + 'View']
      }
      for (let item of props) {
        if (typeof data === 'object') {
          propData = propData[item]
        } else {
          return propData
        }
      }
      if (getRow) {
        // formattor 的时候有时候需要显示 row 的其他字段
        relObj.row = data
        relObj.col = propData
        return relObj
      } else {
        return propData
      }
    },

    setCurrentRow (row) {
      this.$refs.tableGrid.setCurrentRow(row)
      this.$emit('selectRow', row)
    },

    rowClick (row, column, event) {
      if (!this.checkbox || !this.rowClickCheck) return // 非多选不执行以下操作

      // 排除禁止选择
      let el = event.currentTarget.querySelector('input')
      if (el.hasAttribute('disabled')) return

      this.$refs.tableGrid.toggleRowSelection(row)
    },
    allCheckEvent () {
      let _this = this
      if (_this.allCheck) {
        // 全选选中时当前页所有数据选中
        _this.tableData.forEach(row => {
          if (row) {
            _this.$refs.tableGrid.toggleRowSelection(row, true)
          }
        })
      } else {
        _this.$refs.tableGrid.clearSelection()
      }
    },
    clearSelection () {
      this.$refs.tableGrid.clearSelection()
    },
    selectRow (selection, row) {
      this.setCurrentRow(row)
    },
    headerDragend (newWidth, oldWidth, column, event) {
      this.$nextTick(() => {
        this.$refs.tableGrid.doLayout() // 解决表格错位的问题
      })
    },
    copyValue (col, row) {
      // console.log(row[col.prop]);
      // text: 要复制的内容----row[col.prop]
      let tag = document.createElement('input')
      tag.setAttribute('id', 'cp_hgz_input')
      tag.value = row[col.prop]
      document.getElementsByTagName('body')[0].appendChild(tag)
      document.getElementById('cp_hgz_input').select()
      document.execCommand('copy')
      document.getElementById('cp_hgz_input').remove()
      this.$message({
        type: 'success',
        message: this.$t('components.table.msgCopySuccess')
      }) // 复制单号成功!
    }
  }
}
</script>
<style scoped lang="scss">
.the_TableView {
  padding-left: 0px;
  padding-right: 0px;
  position: relative;
  .table-wrapper {
    display: flex;
    min-height: 0;
    height: 100%;
    flex: 1;
    flex-direction: column;
  }
  .custom-table{
    position: absolute;
    right: 0px;
    top: -38px;
  }
  .page-bar{
    padding: 0;
  }

  .el-input-number {
    width: 100%;
  }

  .el-input-number .el-input {
    padding: 0px;
  }

  .el-select > .el-input {
    padding: 0px;
  }
}

</style>
