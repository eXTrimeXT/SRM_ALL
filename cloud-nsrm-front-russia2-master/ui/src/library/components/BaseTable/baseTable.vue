<!--需要使用组件实例ref属性只能在父组件定义ref属性，使用this.$refs.<父组件ref属性名>.$children[0]获取-->
<template>
  <el-container
    direction="vertical"
    class="base-table"
  >
    <div class="form-wrapper table-wrapper">
      <el-form
        ref="form"
        :model="form"
        class="form-wrapper"
      >
        <el-table
          ref="table"
          v-loading="loading"
          v-bind="$attrs"
          :row-key="rowKey"
          height="100%"
          :data="form.dataSource"
          :empty-text="emptyText"
          :stripe="stripe"
          :element-loading-text="$t('common.loading')"
          v-on="$listeners"
          @row-dblclick="rowDblclickClick"
        >
          <!-- <slot name="font" /> -->
          <!--checkbox-->
          <template v-if="selection">
            <el-table-column
              type="selection"
              :reserve-selection="true"
              align="center"
              fixed="left"
            />
          </template>
          <!--序号-->
          <template v-if="index">
            <el-table-column
              type="index"
              align="center"
              fixed="left"
              :label="$t('bidMod.tableIndex')"
              width="60px"
            />
          </template>

          <template v-for="(column, columnIndex) in columns">
            <el-table-column
              v-if="getHidden(column)"
              :key="column.attrs.prop + columnIndex"
              :width="column.attrs.width"
              :min-width="column.attrs.minWidth"
              v-bind="getAttrs(column.attrs)"
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
                    <em
                      v-if="getRequried(column, scope.column.label)"
                      class="required"
                    >*</em>
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
              <template slot-scope="scope">
                <span v-if="column.slot">
                  <template v-if="getEditable(column)">
                    <el-form-item
                      v-if="scope.row[editKey]"
                      :prop="`dataSource.${scope.$index}.${column.attrs.prop}`"
                      :rules="column.rules || null"
                    >
                      <slot
                        :name="column.slot"
                        :scope="scope"
                      />
                    </el-form-item>
                    <template v-else>
                      <span v-if="column.showType==='statusCol'" :class="getStyle(scope, column)">{{ getLabel(scope, column) }}</span>
                      <span v-else>{{ getLabel(scope, column) }}</span>
                    </template>
                  </template>
                  <template v-else>
                    <el-form-item
                      :prop="`dataSource.${scope.$index}.${column.attrs.prop}`"
                      :rules="column.rules || null"
                    >
                      <slot
                        :name="column.slot"
                        :scope="scope"
                      />
                    </el-form-item>
                  </template>
                </span>

                <!--操作-->
                <div v-else-if="column.operations">
                  <template v-for="operation in column.operations">
                    <AuthorityButton
                      v-if="getOperationItemShow(operation, scope)"
                      :key="operation.event"
                      type="text"
                      :code="operation.code"
                      @click.stop.prevent="handleOperation(operation.func, scope)"
                    >
                      {{ operation.name }}
                    </AuthorityButton>
                  </template>
                </div>

                <template v-else>
                  <span v-if="column.showType==='statusCol'" :class="getStyle(scope, column)">{{ getLabel(scope, column) }}</span>
                  <span v-else>{{ getLabel(scope, column) }}</span>
                </template>
              </template>
            </el-table-column>
          </template>

          <slot />
        </el-table>
      </el-form>
    </div>
  </el-container>
</template>

<script>
import { findComponentUpwardByProp, proxyProp } from '@/utils/util'
import { ADD_KEY, EDITABLE_KEY, UPDATE_KEY } from './utils'
import isEqualWith from 'lodash/isEqualWith'
import permission from 'lib@/mixins/permission'
import cloneDeep from 'lodash/cloneDeep'

export default {
  name: 'BaseTable',
  mixins: [permission],
  props: {
    initialize: {
      type: Boolean,
      default: true
    },
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
      type: Boolean,
      default: false
    },
    columns: {
      type: Array,
      required: true
    },
    columnsName: {
      type: String,
      default: 'columns'
    },
    dataSource: {
      type: Array,
      required: true
    },
    loading: Boolean
  },
  data () {
    return {
      editKey: EDITABLE_KEY,
      queryForm: {},
      form: {
        dataSource: []
      }
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
    dataSource: {
      handler () {
        const cloneData = cloneDeep(this.dataSource)
        let data = [...cloneData]
        if (this.editable) {
          data = cloneData.map(i => ({
            ...i,
            [EDITABLE_KEY]: i[EDITABLE_KEY] || false,
            [ADD_KEY]: i[ADD_KEY] || false,
            [UPDATE_KEY]: i[UPDATE_KEY] || false
          }))
        }
        this.form.dataSource = data
      },
      deep: true,
      immediate: true
    },
    'form.dataSource': {
      handler (newValue, oldValue) {
        if (JSON.stringify(newValue) !== JSON.stringify(this.dataSource)) {
          this.$emit('asyncGetRealDataSource', this.form.dataSource)
        }
        if (this.editable) {
          this.compare()
        }
      },
      immediate: true,
      deep: true
    }
  },
  activated () {
    this.doLayout()
  },
  mounted () {
    // 代理父组件的columns属性
    let parentComponent = findComponentUpwardByProp(this, this.columnsName)
    if (parentComponent) {
      parentComponent[this.columnsName] = parentComponent[this.columnsName].map(column => proxyProp(column))
    } else {
      throw new Error('can not find parentComponent')
    }
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
    compare () {
      if (!this.rowKey) {
        throw new Error(this.$t('components.table.notDefineRowKey')) // 未定义rowKey
      }
      const origin = [...this.dataSource]
      const current = this.form.dataSource.filter(row => !row[ADD_KEY])
      origin.forEach(row => {
        const rowKey = row[this.rowKey]
        const index = this.form.dataSource.findIndex(
          r => r[this.rowKey] === rowKey
        )
        if (index > -1) {
          const {
            [UPDATE_KEY]: updateKey,
            [ADD_KEY]: addKey,
            [EDITABLE_KEY]: editableKey,
            ...target
          } = this.form.dataSource[index]
          const flag = isEqualWith(target, row, function (v1, v2) {
            const v1t = typeof v1
            const v2t = typeof v2
            if (
              ['string', 'number'].includes(v1t) &&
              ['string', 'number'].includes(v2t)
            ) {
              return v1 == v2
            }
          })
          if (this.form.dataSource[index][UPDATE_KEY] !== !flag) {
            this.$set(this.form.dataSource[index], UPDATE_KEY, !flag)
          }
        }
      })
    },
    add (row) {
      this.form.dataSource.unshift({
        ...row,
        [ADD_KEY]: true,
        [EDITABLE_KEY]: true,
        [UPDATE_KEY]: true
      })
    },
    rowDblclickClick (row, column, event) {
      console.log(row)
      if (this.editable) row[EDITABLE_KEY] = true
      const rowDblclickClick = this.$listeners['row-dblclick']
      if (rowDblclickClick && typeof rowDblclickClick === 'function') {
        rowDblclickClick(row, column, event)
      }
    },
    doLayout () {
      this.$refs.table.doLayout()
    },
    tbDoLayout () {
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
        column.attrs.formatter &&
        typeof column.attrs.formatter === 'function'
      ) {
        return column.attrs.formatter(scope.row[column.attrs.prop], scope.row)
      }
      return scope.row[column.attrs.prop]
    },
    getStyle (scope, column) {
      let styleClass = ''
      let value = scope.row[column.attrs.prop]
      let statusList = column.statusList || []
      console.log(value)
      styleClass = this.$getStatusClass(statusList, value)
      return styleClass
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
    validate (callback) {
      if (callback) {
        this.$refs.form.validate(callback)
      } else {
        return new Promise((resolve) => {
          this.$refs.form.validate((flag, obj) => {
            resolve({ flag, obj })
          })
        })
      }
    },
    getAttrs (attrs) {
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
      console.log(func, scope, 'handleOperation')
      if (func && typeof func === 'function') {
        func(scope, this.form.dataSource)
      }
      // this.$emit(event, row, this.dataSource);
    },
    clearSelection () {
      this.$refs.table.clearSelection()
    }
  }
}
</script>
<style scoped>
/* .table-wrapper {
  padding-left: 16px;
  padding-right: 4px;
} */
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
.form-wrapper {
  display: flex;
  min-height: 0;
  height: 100%;
  flex-direction: column;
  flex: 1;
}
.highlight {
  color: #ef2f24;
}

</style>
