<template>
  <el-container direction="vertical" style="min-height: 240px;" :class="['the_TableView',{'openCustomTable': openCustomTable}]">
    <div
      v-loading="loading"
      :element-loading-text="$t('components.table.msgFaster')"
      class="table-wrapper"
      :style="{ height: tableHeight, maxHeight: tableMaxHeight }"
    >
      <!-- vxe-table 模式 bigData -->
      <vxe-table
        ref="tableGrid"
        class="table-view-vxe-table"
        style="flex: 1;"
        height="100%"
        border
        auto-resize
        sync-resize
        :show-footer="showFooter"
        :footer-method="footerMethod"
        :stripe="stripe"
        :data="
          frontPaging
            ? tableData.slice((currentPage - 1) * currentPageSize, currentPage * currentPageSize)
            : tableData
        "
        :column-config="{isCurrent: false, isHover: false,resizable: true}"
        :row-id="rowKey"
        :row-config="{isCurrent: true, isHover: true,useKey:true}"
        :checkbox-config="checkbox&&isTriggerRow ? {trigger: 'row',reserve:reserveSelection} : null"
        :header-cell-class-name="headerCellClassName"
        :row-class-name="rowClass"
        :cell-class-name="cellClass"
        :header-cell-style="headerCellStyle"
        :cell-style="cellStyle"
        @current-change="({newValue,oldValue}) => currentChange(newValue,oldValue)"
        @checkbox-change="(selected) => checkChange(selected.records)"
        @checkbox-all="(selected) => checkChange(selected.records)"
        @cell-click="({row,column,cell}) => cellClick(row,column,cell)"
        @cell-dblclick="({row,column,cell}) => rowDblclick(row,column,cell)"
        @radio-change="({ row }) => handleTableRadioChange(row)"
      >
        <vxe-column
          v-if="checkbox"
          type="checkbox"
          width="50"
          align="center"
          :fixed="!xsDevice ? 'left' : ''"
          :resizable="true"
        />
        <vxe-column
          v-if="rowIndex"
          type="seq"
          width="54"
          :title="$t('common.sort')"
          :fixed="!xsDevice ? 'left' : ''"
          :resizable="true"
        />
        <template v-if="radioOptions.enabled">
          <vxe-column
            type="radio"
            width="50"
            :fixed="!xsDevice ? 'left' : ''"
          />
          <vxe-column
            v-if="!rowIndex"
            type="seq"
            width="54"
            :title="$t('common.sort')"
            :fixed="!xsDevice ? 'left' : ''"
            :resizable="true"
          />
        </template>

        <template v-for="(col, key) in innerHeader">
          <vxe-column
            v-if="!col.hidden && !col.children"
            :key="col.prop +'_'+ key+'_'+col.version"
            :field="col.dataType === 'dict' ? col.prop + 'View' : col.prop"
            :sortable="sortable ? !(col.showType === 'buttons' || col.unsortable) : false"
            :title="typeof col.label === 'function' ? col.label() : col.label"
            :fixed="(!xsDevice && col.fixed) ? col.fixed : ''"
            :min-width="col.minWidth || col.width || 100"
            :align="col.align ? col.align : 'left'"
            :header-align="col.headerAlign ? col.headerAlign : 'left'"
            :show-overflow="col.prop === 'operation' ? false : col.showOverflowTooltip ? (typeof col.showOverflowTooltip === 'function' ? col.showOverflowTooltip() : col.showOverflowTooltip) : true"
            :class-name="col.class"
            :resizable="true"
          >
            <template v-if="col.desc || col.addStarToColumn" slot="header">
              <span>
                <i v-if="col.addStarToColumn" class="toRequired">*</i>
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
            <template #default="scope">
              <template v-if="col.showType === 'button'">
                <AuthorityButton
                  v-if="col.show ? col.show(scope.row) : true"
                  class="cel-btn-single"
                  :type="col.btnStyle ? col.btnStyle : 'primary'"
                  :icon="col.icon"
                  :loading="col.loading"
                  :code="col.code"
                  :disabled="typeof col.disabled === 'function' ? col.disabled(scope.row) : false"
                  @click.stop.prevent="callback(col, scope.row, scope)"
                  v-html="formatter(scope, col)"
                />
                <span v-else v-html="formatter(scope, col)" />
              </template>
              <template v-else-if="col.showType === 'slot' && col.slot">
                <slot
                  v-if="col.show ? col.show(scope.row) : true"
                  :name="col.slot"
                  :scope="scope"
                />
                <span v-else v-html="formatter(scope, col)" />
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
                <span v-else v-html="formatter(scope, col)" />
              </template>
              <template v-else-if="col.showType === 'inputNumber'">
                <el-input-number
                  v-if="col.editable ? col.editable(scope.row) : true"
                  v-model="scope.row[col.prop]"
                  v-bind="col.attrs || {}"
                  :controls="col.hasOwnProperty('controls') ? col.controls : true"
                  :min="
                    col.min ? (typeof col.min === 'function' ? col.min(scope.row) : col.min) : 0
                  "
                  :max="
                    col.max ? (typeof col.max === 'function' ? col.max(scope.row) : col.max) : Infinity
                  "
                  @change="callback(col, scope.row)"
                />
                <span v-else v-html="formatter(scope, col)" />
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
                <span v-else v-html="formatter(scope, col, scope)" />
              </template>
              <template v-else-if="col.showType === 'dictSelect'">
                <dictSelect
                  v-if="col.editable ? col.editable(scope.row) : true"
                  v-model="scope.row[col.prop]"
                  :code="col.code"
                  @change-value="(value, dictItem) => callback(col, scope.row, scope, dictItem)"
                />
                <span v-else v-html="formatter(scope, col, scope)" />
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
                <span v-else-if="!!col.formatter" v-html="formatter(scope, col)" />
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
                      :disabled="typeof button.disabled === 'function' ? button.disabled(scope.row) : button.disabled"
                      :type="button.btnStyle ? button.btnStyle : 'text'"
                      @click.stop.prevent="callback(button, scope.row, scope)"
                      v-html="formatter(scope, button)"
                    />
                  </template>
                </el-button-group>
              </template>
              <template v-else-if="col.showType === 'OUorganizationSelector'">
                <OrganizationSelector
                  v-if="col.editable ? col.editable(scope.row) : true"
                  ref="ouSelector"
                  v-model="scope.row[col.prop]"
                  :parent-id="-1"
                  node-type="OU"
                  :placeholder="$t('common.pleaseSelect')"
                  :multiple="col.multiple"
                  :collapse-tags="col.collapseTags"
                  :custom-props="col.customProps"
                  @select="value => callback(col, scope.row, scope, value)"
                />
                <span v-else v-html="formatter(scope, col)" />
              </template>
              <template v-else-if="col.showType === 'quicksearch'">
                <QuickSearch
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
                <span v-else v-html="formatter(scope, col)" />
              </template>
              <template v-else-if="col.showType === 'statusCol'">
                <span :class="getStyle(scope, col)" v-html="formatter(scope, col)" />
              </template>
              <span v-else v-html="formatter(scope, col)" />
            </template>
          </vxe-column>
          <vxe-colgroup
            v-if="!col.hidden&&col.children && col.children.length > 0"
            :key="col.prop +'_'+ key+'_'+col.version"
            :field="col.prop"
            :title="typeof col.label === 'function' ? col.label() : col.label"
            :fixed="(!xsDevice && col.fixed) ? col.fixed : ''"
            :min-width="col.minWidth || col.width || 100"
            :align="col.align ? col.align : 'center'"
            :header-align="col.headerAlign ? col.headerAlign : 'left'"
            :show-overflow="true"
            :resizable="true"
          >
            <template v-if="col.desc || col.addStarToColumn" slot="header">
              <span>
                <i v-if="col.addStarToColumn" class="toRequired">*</i>
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
              <vxe-column
                v-if="!value.hidden"
                :key="value.prop +'_'+ k+'_'+value.version"
                :field="value.prop"
                :sortable="sortable ? !(value.showType === 'buttons' || value.unsortable) : false"
                :title="typeof value.label === 'function' ? value.label() : value.label"
                :fixed="(!xsDevice && value.fixed) ? value.fixed : ''"
                :min-width="col.minWidth || col.width || 100"
                :align="value.align ? value.align : 'left'"
                :header-align="value.headerAlign ? value.headerAlign : 'left'"
                :show-overflow="true"
                :resizable="true"
              >
                <template v-if="value.desc || value.addStarToColumn" slot="header">
                  <span>
                    <i v-if="value.addStarToColumn" class="toRequired">*</i>
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
                    <span v-else v-html="formatter(scope, value)" />
                  </template>
                  <template v-else-if="value.showType === 'slot' && value.slot">
                    <slot
                      v-if="value.show ? value.show(scope.row) : true"
                      :name="value.slot"
                      :scope="scope"
                    />
                    <span v-else v-html="formatter(scope, value)" />
                  </template>
                  <template v-else-if="value.showType === 'statusCol'">
                    <span :class="getStyle(scope, value)" v-html="formatter(scope, value)" />
                  </template>
                  <template v-else-if="value.showType === 'button'">
                    <AuthorityButton
                      v-if="value.show ? value.show(scope.row) : true"
                      :type="value.btnStyle ? value.btnStyle : 'primary'"
                      :icon="value.icon"
                      :loading="value.loading"
                      :code="value.code"
                      @click.stop.prevent="callback(value, scope.row, scope)"
                      v-html="formatter(scope, value)"
                    />
                    <span v-else v-html="formatter(scope, value)" />
                  </template>
                  <span v-else v-html="formatter(scope, value)" />
                </template>
              </vxe-column>
            </template>
          </vxe-colgroup>
        </template>
        <template #empty>
          <div style="color: #96999c;">
            <p>没有更多数据了！</p>
          </div>
        </template>
      </vxe-table>
    </div>

    <el-footer v-if="pageEnabled" class="page-bar" height="auto">
      <el-row>
        <!-- 全选所有 -->
        <el-col v-if="openAllCheck && frontPaging" :span="4">
          <el-checkbox v-model="allCheck" @change="allCheckEvent">
            {{ $t('components.table.selectAll') }}
          </el-checkbox>
        </el-col>
        <el-col :span="openAllCheck && frontPaging ? 20 : 24">
          <CPagination
            ref="queryPagination"
            style="margin: 0;padding-bottom: 4px;"
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
    <CustomTable
      v-if="openCustomTable"
      class="custom-table"
      :page-view-config-code="pageViewConfigCode"
      @updataConfig="updataConfig"
    />
  </el-container>
</template>

<script>
import { mapState } from 'vuex'
import { debounce, isNull } from '@/utils'
import { formatNumber } from '@/utils/util'
import http from '@/utils/axios/http'
import { STORE_COMMON_CACHE } from '@/config/store-config'
import CPagination from 'lib@/components/c-pagination'
import CustomTable from 'lib@/components/custom-table'
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
// 表格配置key前缀
const localStorageKeyPerfix = 'custom_table_key'
export default {
  name: 'TableView',
  components: {
    CustomTable,
    CPagination,
    OrganizationSelector,
    QuickSearch
  },
  props: {
    // 是否触发行
    isTriggerRow: {
      type: Boolean,
      default: true
    },
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
    cellClick: {
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
      // 没用上
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
    // checkAll: {
    //   type: Function,
    //   default: function () {
    //     return null
    //   }
    // },
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
        return null
      }
    },
    // 表头样式
    headerCellClassName: {
      type: [Function, String],
      default: null
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
    rowIndex: {
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
    // 表格汇总行开关
    showFooter: {
      type: Boolean,
      default: false
    },
    // 表格汇总行函数
    footerMethod: {
      type: Function,
      required: false
    },
    /**
     * 表格汇总行函数示例
       footerMethod ({ columns, data }) {
        let sumNum = (list, field)=> {
          let count = 0
          list.forEach(item => {
            count += Number(item[field])
          })
          return count
        }
        return [
          columns.map((column, columnIndex) => {
            if (columnIndex === 0) {
              return '合计'
            }
            if (['rate', 'num1'].includes(column.property)) {
              return sumNum(data, column.property)
            }
            return null
          })
        ]
      },
     */

    // 表格配置key值，默认取当前路由name,如果一个路由页面有多个列表页需要开启配置，这个值必传
    customTableKey: {
      type: String,
      default: ''
    },
    // 单选配置
    radioOptions: {
      type: Object,
      default: () => {
        return {
          // 是否启用
          enabled: false,
          // 单选值行key，一般指向唯一主键
          labelKey: ''
        }
      }
    },
    // 整个表格排序 默认开启，关闭时忽略列的排序配置
    sortable: {
      type: Boolean,
      default: true
    },
    // 列表查询数据适配meiql
    adeptMeiQl: {
      type: Boolean,
      default: false
    },
    // 是否启用编辑模式
    editMode: {
      type: Boolean,
      default: false
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
      currentPage: 1,
      defaultTableHeaderBak: [],
      pageViewConfigCode: '', // 配置key 对应后台配置
      tableRadioValue: ''
    }
  },
  computed: {
    // 默认配置
    defaultTableHeader () {
      return this.tableHeader.map(item => ({
        label: typeof item.label === 'function' ? item.label() : item.label,
        prop: item.prop,
        width: item.width,
        minWidth: item.minWidth,
        show: typeof item.show === 'function' ? item.show() : item.show || true,
        fixed: item.fixed || undefined,
        version: 'v1'
      }))
    },
    ...mapState({
      device: state => state.app.device
    }),
    xsDevice () {
      return this.device === 'device-xs'
    },
    attrs () {
      return this.$attrs
    }
  },
  watch: {
    tableHeader: {
      handler (n, o) {
        if (JSON.stringify(n) !== JSON.stringify(o)) {
          // tableHeader会更新
          this.innerHeader = (n || []).concat()
          if (n.length > 0) {
            this.defaultTableHeaderBak = [...this.tableHeader] //  异步请求的后面重新赋值
          }
        }
      },
      immediate: true,
      deep: true
    },
    innerHeader: {
      immediate: true,
      deep: true,
      handler: function (n, o) {
        if (JSON.stringify(n) !== JSON.stringify(o)) {
          this.loadDict(this.tableData)
          this.$forceUpdate()
          this.$nextTick(() => {
            this.doLayout()
          })
        }
      }
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
    },
    tableInfor: {
      handler (data) {
        this.tableData = data
      },
      immediate: true,
      deep: true
    }
  },
  mounted () {
    if (!this.url) {
      this.$nextTick(async () => {
        if (this.tableInfor.length) {
          await this.loadDict(this.tableInfor)
          this.tableData = this.tableInfor || [] // 解决多次刷新界面会出现没有更多数据的问题
        }
        this.doLayout()
      })
    }
    this.$_resizeHandler = debounce(() => {
      if (this.$refs.tableGrid) {
        this.doLayout()
      }
    }, 100)

    this.$_initResizeEvent()
  },
  async created () {
    this.pageViewConfigCode = this.customTableKey || this.$route.name
    this.defaultTableHeaderBak = [...this.tableHeader]
    await this.initComp() // 初始化表格配置
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
      const xTable = this.$refs.tableGrid
      if (xTable) {
        xTable.refreshColumn().then(() => xTable.refreshScroll())
        xTable.updateData()
        xTable.recalculate(true)
        this.$forceUpdate()
      }
    },
    // 获取当前列表实例，用于使用table的方法
    getCurrentTableRef () {
      return this.$refs.tableGrid
    },
    // 用于 checkbox-config.reserve，获取已保留选中的行数据（不包含当前列表，如果 isFull=true 则不包含全部列表）
    getCheckboxReserveRecords (isFull) {
      return this.$refs.tableGrid.getCheckboxReserveRecords(isFull)
    },
    // 用于 type=checkbox，获取当前已选中的行数据（当前列表，如果 isFull=true 则获取全表已选中的数据）
    getCheckboxRecords (isFull) {
      return this.$refs.tableGrid.getCheckboxRecords(isFull)
    },
    // 清除所有行
    clearSelection () {
      this.$refs.tableGrid.clearCheckboxRow()
    },
    // 设置选中行
    setCheckboxRow (row) {
      let _this = this
      _this.$refs.tableGrid.setCheckboxRow(row, true)
    },
    // 选中所有行
    selectionAlldata () {
      let _this = this
      _this.$refs.tableGrid.setAllCheckboxRow(true)
    },
    // 编辑状态下添加一行事件
    addOneEditableColumn (row = { editable: true, isNew: true }) {
      this.tableData.unshift(row)
      this.$nextTick(() => {
        this.doLayout()
      })
    },
    deleteRow (index) {
      this.tableData.splice(index, 1)
    },
    setTableData (callback) {
      callback(this.tableData)
    },
    callback (col, row, scope, other) {
      if (col.callback) {
        col.callback(row, scope, other)
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
    // 查询数据
    query () {
      this.viewIndex = 1 // 非分页触发查询，页码跳回首页
      this.queryTotal = 0
      let queryParam = this.getParam()
      this.find(queryParam)
      this.clearSelection() // 按钮查询的时候清空已选的数据
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
      console.log(queryParam, 'queryParam')
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
    // 加载数据
    reloadData (queryParam) {
      let paramsExt = {}
      // 适配meiql 分页查询处理
      let bol = true // 是否再请求接口
      if (this.adeptMeiQl) {
        paramsExt = { ...queryParam }
        try {
          if (paramsExt.payload?.pageNum) {
            paramsExt.payload.pageSize = queryParam.__pagesize
            paramsExt.payload.pageNum = queryParam.__page
          }
        } catch (e) {
          console.log(e)
        }
        try {
          if (paramsExt.payload.page) {
            paramsExt.payload.page.pageSize = queryParam.__pagesize
            paramsExt.payload.page.pageNum = queryParam.__page
          } else {
            paramsExt.payload.pageSize = queryParam.__pagesize
            paramsExt.payload.pageNum = queryParam.__page
          }
        } catch (e) {
          try {
            paramsExt.payload[0].pageSize = queryParam.__pagesize
            paramsExt.payload[0].pageNum = queryParam.__page
          } catch (e) {
            bol = false
          }
        }
      } else {
        paramsExt = Object.assign(
          {
            [this.laypage.pageNum]: queryParam.__page,
            [this.laypage.pageSize]: queryParam.__pagesize
          },
          queryParam,
        )
      }
      // if (!bol) {
      //   return false
      // }
      // 数据请求
      var promiseInfo = null
      if (this.url) {
        this.loading = true
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
        this.loading = true
        promiseInfo = this.source(paramsExt)
      } else if (this.apiInfo.LIST_API) {
        this.loading = true
        promiseInfo = this.$store.dispatch(this.apiInfo.LIST_API, paramsExt)
      }
      if (!promiseInfo) {
        this.loading = false
        return
      }
      // 数据返回处理
      promiseInfo
        .then(async data => {
          if (this.source) {
            if (this.transformData && typeof this.transformData === 'function') {
              data = this.transformData(data)
            }
          }
          // 适配meiql 列表数据
          if (this.adeptMeiQl) {
            if (data && data.data) {
              let records = data.data.records || []
              if (records.length > 0) {
                await this.loadDict(records)
                this.tableData = records
                this.dataCount = data.data.total
                this.queryTotal = data.data.total
              } else {
                this.dataCount = 0
                this.tableData = []
              }
            }
          } else {
            // 传统处理方式
            if (data && data.data) {
              let dataList = data.data.list || []
              if (dataList.length > 0) {
                await this.loadDict(dataList)
                this.tableData = dataList
                this.dataCount = data.data.total
                this.queryTotal = data.data.total
              } else {
                this.dataCount = 0
                this.tableData = []
              }
            }
          }
          this.loading = false
          this.$emit('afterQuery', this.tableData) // afterQuery 事件
        })
        .catch(err => {
          this.loading = false
          this.$emit('afterQuery', err) // afterQuery 事件
        })
        .finally(() => {
          this.$nextTick(() => {
            this.doLayout()
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
        // 开启编辑模式添加editable字段作为编辑中间态控制
        if (this.editMode) {
          for (let k = 0; k < tableData.length; k++) {
            tableData[k]['editable'] = false
          }
        }
        return
      }
      const dictBatchMap = await this.$store.dispatch(STORE_COMMON_CACHE.LIST_DICT_BATCH, {
        dictCodeList: dictSet
      })
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
            // 开启编辑模式添加editable字段作为编辑中间态控制
            if (this.editMode) {
              tableData[j]['editable'] = false
            }
          }
        }
      }
    },
    getTableData () {
      return [...this.tableData]
    },
    // 状态列样式处理
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
    // 自定义全选事件
    allCheckEvent () {
      let _this = this
      if (_this.allCheck) {
        // 全选选中时当前页所有数据选中
        this.selectionAlldata() // 选中所有行
      } else {
        this.clearSelection() // 按钮查询的时候清空已选的数据
      }
    },
    selectRow (selection, row) {
      this.setCurrentRow(row)
    },
    // 表格自定义配置start
    // 配置信息
    getParams () {
      const userId = this.$store.getters.user.userId
      const key = `${localStorageKeyPerfix}_${userId}_${this.pageViewConfigCode}`
      return key
    },
    // 查询接口配置信息
    async fatchConfig () {
      let tableConfig = ''
      if (this.openCustomTable) {
        let query = { pageViewConfigCode: this.pageViewConfigCode }
        const { data = {} } = await this.$api.base.pageConfig.getCurrentConfig(query)
        tableConfig = data.tableConfig || ''
      }
      return tableConfig
    },
    // 查询配置
    async queryConfig () {
      const key = this.getParams()
      const JSON_CONFIG = localStorage.getItem(key) // 本地
      const JSON_CONFIG_SERVICE = await this.fatchConfig() // 获取后台配置信息
      const CONFIG_RES = JSON_CONFIG_SERVICE || JSON_CONFIG
      const config = CONFIG_RES ? JSON.parse(CONFIG_RES) : undefined
      let resConfig = await this.setColumn(config)
      return resConfig
    },
    // 设置显示列
    setColumn (configData) {
      const defaultTConfig = this.defaultTableHeaderBak
      let meta = []
      if (configData) {
        let filterShow = configData.filter(i => i.show)
        meta = filterShow.map(({ prop, width, minWidth, fixed, version }) => {
          const target = defaultTConfig.find(i => i.prop === prop)
          let initFixed = fixed || target?.fixed
          if (target?.prop == 'operation') { // 操作列默认固定右侧
            initFixed = 'right'
          }
          return { ...target, width, minWidth, version, fixed: initFixed }
        })
      } else {
        meta = defaultTConfig
      }
      return meta
    },
    // 初始化表格列
    async initComp () {
      if (this.openCustomTable) {
        let tHeader = await this.queryConfig()
        this.innerHeader = tHeader
      } else {
        this.innerHeader = this.defaultTableHeaderBak
      }
    },
    // 配置后更新表格
    async updataConfig (isupdate) {
      if (isupdate) {
        await this.initComp()
        this.doLayout()
        this.$forceUpdate()
      }
    },

    /* 单选变更 */
    handleTableRadioChange (row) {
      this.$emit('radio-change', row)
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
  .custom-table {
    position: absolute;
    right: 0px;
    top: -38px;
  }
  .page-bar {
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
<style lang="scss">
.the_TableView {
  .vxe-table--render-default .vxe-body--column.col--ellipsis>.vxe-cell{
    .el-button--text.cel-btn-single{
      width: 100%;
      text-align: left;
      >span{
        overflow: hidden;
        line-height: 40px;
        text-overflow: ellipsis;
        white-space: nowrap;
        width: 100%;
        display: inline-block;
      }
    }
  }
}
</style>
