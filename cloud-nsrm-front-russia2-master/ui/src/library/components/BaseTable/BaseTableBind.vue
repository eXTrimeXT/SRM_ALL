<!--需要使用组件实例ref属性只能在父组件定义ref属性，使用this.$refs.<父组件ref属性名>.$children[0]获取-->
<template>
  <el-table
    ref="table"
    v-loading="loading"
    :max-height="360"
    v-bind="$attrs"
    :row-key="rowKey"
    :data="dataList"
    :empty-text="emptyText"
    :stripe="stripe"
    :element-loading-text="$t('common.loading')"
    v-on="$listeners"
    @row-dblclick="rowDblclickClick"
  >
    <!-- <slot name="font" /> -->
    <template v-if="(typeof selection === 'boolean' && selection) || (typeof selection === 'string' && selection.toUpperCase() === 'TRUE')">
      <el-table-column type="selection" :selectable="selectable" />
    </template>
    <template v-if="index">
      <el-table-column
        type="index"
        align="center"
        :label="$t('common.sort')"
        width="58px"
      />
    </template>
    <template v-for="(column, columnIndex) in columns">
      <el-table-column
        v-if="getHidden(column)"
        :key="column.prop + columnIndex"
        v-bind="getAttrs(column)"
        show-overflow-tooltip
      >
        <template #header="scope">
          <span
            v-if="column.headerSlot"
            @click.stop.prevent="() => {}"
          >
            <slot
              :name="column.headerSlot"
              :scope="scope"
            />
          </span>
          <span
            v-else
            @click.stop.prevent="() => {}"
          >
            <span>
              <i
                v-if="getRequried(column, scope.column.label)"
                class="required"
              >*</i>
              <span :class="getCellClasses(scope)">{{
                scope.column.label
              }}</span>
            </span>
            <span v-if="getSearchVisible(column)">
              <el-popover
                placement="bottom"
                trigger="click"
              >
                <component
                  :is="getComponent(column)"
                  v-model="queryForm[scope.column.property]"
                  v-bind="getProps(column)"
                  v-on="getListeners(column)"
                />
                <div style="margin-top: 10px;">
                  <el-button
                    type="primary"
                    @click="queryTable"
                  >
                    <!-- 搜索 -->
                    {{ $t("common.search") }}
                  </el-button>
                  <el-button @click="reset(scope.column.property)">
                    <!-- 重置 -->
                    {{ $t("common.reset") }}
                  </el-button>
                </div>
                <div
                  slot="reference"
                  :class="getSearchClasses(scope)"
                />
              </el-popover>
            </span>
          </span>
        </template>
        <template v-slot="scope">
          <template v-if="column.slot">
            <template v-if="getEditable(column)">
              <slot
                v-if="scope.row[editKey] || column.forceSlot"
                :name="column.slot"
                :scope="scope"
              />
              <span v-else>{{ getLabel(scope, column) }}</span>
            </template>
            <template v-else>
              <slot
                :name="column.slot"
                :scope="scope"
              />
            </template>
          </template>

          <!--操作-->
          <div v-else-if="column.operations">
            <template v-for="operation in column.operations">
              <el-button
                v-if="getOperationItemShow(operation, scope)"
                :key="operation.event"
                type="text"
                @click="handleOperation(operation.func, scope)"
              >
                {{ operation.name }}
              </el-button>
            </template>
          </div>

          <span v-else>{{ getLabel(scope, column) }}</span>
        </template>
      </el-table-column>
    </template>

    <slot />
  </el-table>
</template>

<script>
import { formatNumber } from '@/utils/util'
import { ADD_KEY, EDITABLE_KEY, UPDATE_KEY } from './utils'
import permission from 'lib@/mixins/permission'

export default {
  name: 'BaseTableBind',
  mixins: [permission],
  props: {
    initialize: {
      type: Boolean,
      default: true
    },
    // eslint-disable-next-line vue/require-prop-types
    rowKey: {
      type: String
    },
    editable: {
      type: Boolean,
      default: true
    },
    index: {
      type: Boolean,
      default: true
    },
    selection: {
      type: [Boolean, String],
      default: false
    },
    selectable: {
      type: Function,
      default: () => {
        const selectableFunc = (row, index) => { return true }
        return selectableFunc
      }
    },
    columns: {
      type: Array,
      required: true
    },
    value: {
      type: Array,
      required: true
    },
    loading: Boolean
  },
  data () {
    return {
      editKey: EDITABLE_KEY,
      queryForm: {},
      dataList: []
    }
  },
  computed: {
    emptyText () {
      return this.$attrs['empty-text'] || this.$t('common.noData') // 暂无数据
    },
    stripe () {
      return this.$attrs.stripe !== false
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
  mounted () {
    if (this.initialize) {
      this.queryTable()
    }
  },
  methods: {
    getOperationItemShow (operation, scope) {
      const { permission, show } = operation
      const showFlag = show ? show(scope) : true
      return this.hasPermission(permission) && showFlag
    },
    getEditable (column) {
      if (typeof column.editable === 'boolean') {
        return column.editable
      }
      return this.editable
    },
    getSearchVisible (column) {
      if (column.search && column.search.show) {
        if (typeof column.search.show === 'function') {
          return column.search.show()
        }
        return column.search.show
      }
      return !!column.search
    },
    getHidden (column) {
      if (column.hidden && typeof column.hidden === 'function') {
        return !column.hidden()
      }
      return !column.hidden
    },
    add (row) {
      this.dataList.unshift({
        ...row,
        [ADD_KEY]: true,
        [EDITABLE_KEY]: true,
        [UPDATE_KEY]: true
      })
    },
    pushRow (row) {
      this.dataList.push({
        ...row,
        [ADD_KEY]: true,
        [EDITABLE_KEY]: true,
        [UPDATE_KEY]: true
      })
    },
    pushRowBatch (rowArray) {
      for (let i = 0; i < rowArray.length; i++) {
        const row = rowArray[i]
        this.dataList.push({
          ...row,
          [ADD_KEY]: true,
          [EDITABLE_KEY]: true,
          [UPDATE_KEY]: true
        })
      }
    },
    rowDblclickClick (row, column, event) {
      console.log(row)
      const rowDblclickClick = this.$listeners['row-dblclick']
      if (rowDblclickClick && typeof rowDblclickClick === 'function') {
        rowDblclickClick(row, column, event)
      }
      if (this.editable) row[EDITABLE_KEY] = true
    },
    doLayout () {
      this.$refs.table.doLayout()
    },
    getSearchClasses (scope) {
      const baseClasses = ['search-button', 'el-icon-search']
      const highlight =
        !!this.queryForm[scope.column.property] ||
        this.queryForm[scope.column.property] === 0
      return [...baseClasses, highlight ? 'highlight' : '']
    },
    getCellClasses (scope) {
      const highlight =
        !!this.queryForm[scope.column.property] ||
        this.queryForm[scope.column.property] === 0
      return { highlight }
    },
    getRequried (column, label) {
      // console.log('[getRequried]', column, label, (column.rules || {}).required);
      return !!(column.rules || {}).required
    },
    getLabel (scope, column) {
      if (
        column.formatter &&
        typeof column.formatter === 'function'
      ) {
        return column.formatter(scope.row[column.prop])
      }
      if (!scope.row[column.prop] && column.openThousand && column.fixBit) {
        scope.row[column.prop] = 0
      }
      if ((scope.row[column.prop] || scope.row[column.prop] === 0) && column.openThousand) {
        return formatNumber(scope.row[column.prop], column.fixBit)
      } else if ((scope.row[column.prop] || scope.row[column.prop] === 0) && column.fixBit) {
        return Number(scope.row[column.prop]).toFixed(column.fixBit)
      } else {
        return scope.row[column.prop]
      }
    },
    getComponent (column) {
      if (!column.search.component) {
        return 'el-input'
      }
      return column.search.component
    },
    getProps (column) {
      if (!column.search.props) {
        return {}
      }
      return column.search.props
    },
    getListeners (column) {
      if (!column.search.listeners) {
        return {}
      }
      return column.search.listeners
    },
    queryTable () {
      this.$emit('search', this.queryForm)
      // 查询表格数据
      console.log('[queryForm]', this.queryForm)
    },
    reset (prop) {
      this.queryForm[prop] = ''
      this.$emit('reset', prop, this.queryForm)
    },
    // 表单校验，已经去掉，默认返回成功
    validate (callback) {
      return true
    },
    getAttrs (column) {
      var attrs = {
        label: column.label,
        prop: column.prop,
        minWidth: column.minWidth,
        width: column.width,
        formatter: column.formatter,
        align: column.align
      }
      if (attrs.label && typeof attrs.label === 'function') {
        const label = attrs.label(this)
        attrs.label = label
      }
      if (!attrs.align) {
        attrs.align = 'center'
      }
      return attrs
    },
    // 是否是一个常规的table-column(有以下标签就不是常规table-column)
    isCommonTableColumn (column) {
      const specialColumnList = ['slot', 'operations']
      return !specialColumnList.some(option => column[option])
    },
    // 点击操作按钮触发的事件
    handleOperation (func, scope) {
      if (func && typeof func === 'function') {
        func(scope, this.dataList)
      }
    }
  }
}
</script>
<style scoped>
.base-table {
  min-height: 0;
}
.required {
  color: #ff4949;
  padding-right: 2px;
}
.header-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
}
.search-button {
  color: #502c2c;
  margin-left: 10px;
  cursor: pointer;
  font-weight: bolder;
  margin-right: 10px;
}
.highlight {
  color: #ef2f24;
}
</style>
