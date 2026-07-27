<template>
  <div class="vxe-base-table">
    <CustomTable
      v-if="showCustomTable"
      class="custom-table"
      :tableColumn="tableColumnData"
      @updataConfig="updataTableColumn"
      @resetConfig="resetTableColumn"
    />
    <vxe-table
      ref="xTable"
      round
      v-bind="$attrs"
      :data="tableData"
      :stripe="stripe"
      :border="border"
      :column-config="{ resizable: true }"
      :height="height"
      :row-config="{ isCurrent: true, isHover: true }"
      :tooltip-config="{ showAll: true, enterable: true, contentMethod: showTooltipMethod }"
      show-overflow
      v-on="$listeners"
      @checkbox-all="checkChange"
      @checkbox-change="checkChange"
    >
      <vxe-column v-if="checkbox" type="checkbox" lign="center" width="60" />
      <vxe-column v-if="isSeq" type="seq" title="序号" width="60" />
      <template v-for="(col, key) in tableColumnData">
        <vxe-column
          v-if="!(typeof col.hidden === 'function' ? col.hidden() : col.hidden)"
          :key="col.field + '_' + key"
          :min-width="col.minwidth || col.width || 100"
          :field="col.field"
          :title="typeof col.title === 'function' ? col.title() : col.title"
          :fixed="col.fixed"
          :align="col.align ? col.align : 'left'"
          :show-overflow="col.prop === 'operation' ? false : (col.showOverflowTooltip ? (typeof col.showOverflowTooltip === 'function' ? col.showOverflowTooltip() : col.showOverflowTooltip) : true)"
        >
          <template v-if="col.desc || col.addStarToColumn" #header>
            <el-tooltip
              v-if="col.desc"
              class="item"
              effect="dark"
              :content="col.desc"
              placement="top"
            >
              <i style="font-size: 14px;margin-left: 3px;cursor: help;" class="el-icon-question" />
            </el-tooltip>
            <span>
              <i v-if="col.addStarToColumn" class="toRequired">*</i>
              {{ typeof col.title === 'function' ? col.title() : col.title }}
            </span>
          </template>

          <template #default="scope">
            <template v-if="col.columnType === 'dictSelect'">
              <dictSelect
                v-model="scope.row[col.field]"
                :disabled="(typeof col.disabled === 'function' ? col.disabled(scope.row) : col.disabled) || isReadOnly"
                :code="col.code"
                @change-value="(val, node) => { callback(col, scope, { val, node }) }"
              />
            </template>

            <template v-else-if="col.columnType === 'OUorganizationSelector'">
              <OrganizationSelector
                ref="ouSelector"
                v-model="scope.row[col.field]"
                :disabled="(typeof col.disabled === 'function' ? col.disabled(scope.row) : col.disabled) || isReadOnly"
                :parent-id="-1"
                node-type="OU"
                :placeholder="$t('common.pleaseSelect')"
                :multiple="col.multiple"
                :collapse-tags="col.collapseTags"
                @select="(node, value) => { callback(col, scope, { node, value }) }"
              />
            </template>

            <template v-else-if="col.columnType === 'quicksearch'">
              <QuickSearch
                ref="quickSearchTool"
                :disabled="(typeof col.disabled === 'function' ? col.disabled(scope.row) : col.disabled) || isReadOnly"
                :show-key="col.showKey"
                :prop-key="col.propKey"
                :scope-data="scope.row"
                :name="col.name"
                :pre-query-data="col.preQueryData"
                @close-quicksearch="(val) => {
                  scope.row[col.field] = col.propKey ? val[col.propKey] : val[col.showKey]
                  callback(col, scope, { val })
                }"
              />
            </template>

            <template v-else-if="col.columnType === 'slot' && col.slot">
              <slot :name="col.slot" :scope="scope" />
            </template>

            <template v-else-if="col.columnType === 'buttons'">
              <el-button-group>
                <template v-for="button in col.buttons">
                  <AuthorityButton
                    v-if="typeof button.show === 'function' ? button.show(scope.row) : true"
                    :key="button.text"
                    :disabled="(typeof col.disabled === 'function' ? col.disabled(scope.row) : col.disabled) || isReadOnly"
                    style="margin:0 3px;"
                    :code="button.code"
                    :type="button.btnStyle ? button.btnStyle : 'text'"
                    @click.stop.prevent="callback(button, scope)"
                  >
                    {{ button.text }}
                  </AuthorityButton>
                </template>
              </el-button-group>
            </template>

            <template v-else>
              <VxeColumnSlot
                v-if="col.columnType"
                v-model="scope.row[col.field]"
                :disabled="(typeof col.disabled === 'function' ? col.disabled(scope.row) : col.disabled) || isReadOnly"
                :type="col.columnType"
                :col="col"
                @change="(val) => { callback(col, scope) }"
              />

              <span v-else>{{ typeof col.formatter === 'function' ? col.formatter(scope.row) : (scope.row[col.field] ||
                '-')
              }}</span>
            </template>
          </template>
        </vxe-column>
      </template>

      <template #empty>
        <div style="color: #96999c;">
          <p>没有更多数据了！</p>
        </div>
      </template>
    </vxe-table>
  </div>
</template>
<script>
import OrganizationSelector from 'lib@/components/organization-selector'
import QuickSearch from 'lib@/components/QuickSearch'
import VxeColumnSlot from './vxeColumnSlot'
import CustomTable from './customTable'

export default {
  name: 'VxeBaseTable',
  components: {
    OrganizationSelector,
    QuickSearch,
    VxeColumnSlot,
    CustomTable
  },
  props: {
    // 是否需要序号列
    isSeq: {
      type: Boolean,
      default: true
    },
    // 边框
    border: {
      type: Boolean,
      default: true
    },
    // 斑马纹
    stripe: {
      type: Boolean,
      default: true
    },
    height: {
      type: String,
      default: '350'
    },
    // 表格数据
    tableData: {
      type: Array,
      default: () => []
    },
    // 表格列配置
    tableColumn: {
      type: Array,
      default: () => []
    },
    checkbox: {
      type: Boolean,
      default: false
    },
    // 只读
    isReadOnly: {
      type: Boolean,
      default: false
    },
    // 控制所有列显示tooltip，除了operation
    isShowTooltip: {
      type: Boolean,
      default: false
    },
    // 需要显示tooltip的列
    showTooltipFieldList: {
      type: Array,
      default: () => []
    },
    // 是否展示表头配置
    showCustomTable: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      tableColumnData: []
    }
  },
  watch: {
    tableColumn: {
      handler (val) {
        this.tableColumnData = val
      },
      deep: true,
      immediate: true
    }
  },
  methods: {
    showTooltipMethod ({ type, column, row, items, _columnIndex }) {
      const { property } = column
      if (this.isShowTooltip) {
        if (property === 'operation') {
          return ''
        }
        return null
      } else if (this.showTooltipFieldList.includes(property)) {
        return null
      } else {
        return ''
      }
    },
    callback (col, scope, data) {
      if (col.callback) {
        col.callback(scope, data)
      }
    },
    checkChange () {
      let selectRecords = this.$refs.xTable.getCheckboxRecords()
      this.$emit('checkChange', selectRecords)
    },
    updataTableColumn (tableConfig) {
      let newTableColumn = []
      tableConfig.forEach(configItem => {
        this.tableColumnData.forEach(item => {
          if (configItem.prop === item.field) {
            let obj = { ...item }
            obj.hidden = !configItem.show
            obj.fixed = configItem.lockLeft === 'Y' ? 'left' : (configItem.lockRight === 'Y' ? 'right' : undefined)
            obj.width = configItem.width
            newTableColumn.push(obj)
          }
        })
      })

      this.tableColumnData = newTableColumn
    },
    resetTableColumn () {
      this.tableColumnData = this.tableColumn
    }
  }
}
</script>
<style scoped lang="scss">
.vxe-base-table {
  position: relative;
}

.custom-table {
  position: absolute;
  right: 0px;
  top: -30px;
}
</style>
