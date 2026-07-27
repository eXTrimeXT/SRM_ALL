<template>
  <div class="element-attr-wrap">
    <!--新增-->
    <el-button
      v-if="!readonly"
      type="primary"
      @click="addRow"
    >
      {{ $t('common.add') }}
    </el-button>

    <el-table
      ref="attrFieldTable"
      :data="attrFieldData"
      border
      row-key="prop"
      max-height="465"
      style="margin-top: 10px;"
    >
      <!--拖拽排序-->
      <el-table-column
        v-if="!readonly"
        align="center"
        type="index"
        width="60"
        :label="$t('components.headers.sort')"
      >
        <template v-slot="scope">
          <em class="iconfont icondrag" />
        </template>
      </el-table-column>

      <!--序号-->
      <el-table-column
        align="center"
        type="index"
        width="60"
        :label="$t('common.sort')"
      />

      <!--字段名称-->
      <el-table-column
        align="center"
        prop="fieldName"
        :label="$t('quoteTemplate.fieldName')"
        min-width="150"
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <el-input
            v-model="scope.row.fieldName"
            :disabled="readonly"
            @blur="fieldNameBlur(scope)"
          />
        </template>
      </el-table-column>

      <!--字段描述-->
      <el-table-column
        align="center"
        prop="fieldDesc"
        :label="$t('dataConfMod.questTemplatePropFieldDesc')"
        min-width="150"
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <el-input
            v-model="scope.row.fieldDesc"
            :disabled="readonly"
          />
        </template>
      </el-table-column>

      <!--字段类型-->
      <el-table-column
        align="center"
        prop="fieldType"
        :label="$t('quoteTemplate.fieldType')"
        min-width="150"
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <DictSelect
            v-model="scope.row.fieldType"
            code="SOU_QUOTE_TEMP_FIELD_TYPE"
            :disabled="readonly"
            @change="value => fieldTypeChange(value, scope)"
          />
        </template>
      </el-table-column>

      <!--字段值-->
      <el-table-column
        align="center"
        prop="fieldValue"
        :label="$t('quoteTemplate.fieldValue')"
        min-width="170"
      >
        <template #header="{ column, $index }">
          <span>{{ column.label }}</span>
          <el-tooltip
            effect="dark"
            :content="$t('quoteTemplate.fieldValueTooltip')"
            placement="top"
            class="table-column-tooltip"
          >
            <em class="el-icon-warning tip-icon" />
          </el-tooltip>
        </template>

        <template v-slot="scope">
          <!--枚举类型和字典类型才能填-->
          <el-input
            v-if="fieldTypeIsEnum(scope.row.fieldType) || fieldTypeIsDict(scope.row.fieldType)"
            v-model="scope.row.fieldValue"
            :disabled="readonly"
          />
        </template>
      </el-table-column>

      <!--默认值-->
      <el-table-column
        align="center"
        prop="defaultValue"
        :label="$t('quoteTemplate.defaultValue')"
        min-width="170"
      >
        <template v-slot="scope">
          <!--日期类型，选择日期-->
          <el-date-picker
            v-if="fieldTypeIsDate(scope.row.fieldType)"
            v-model="scope.row.defaultValue"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            :placeholder="$t('common.pleaseSelectDate')"
            :disabled="readonly"
            style="width: 100%"
          />

          <!--输入框-->
          <template v-else>
            <!--数字类型 限制输入数字-->
            <el-input
              v-if="fieldTypeIsNumber(scope.row.fieldType)"
              v-model="scope.row.defaultValue"
              type="number"
              :disabled="readonly || fieldTypeIsFormula(scope.row.fieldType)"
            />

            <!--非数字类型-->
            <el-input
              v-else
              v-model="scope.row.defaultValue"
              :disabled="readonly || fieldTypeIsFormula(scope.row.fieldType)"
            />
          </template>
        </template>
      </el-table-column>

      <!--必填-->
      <el-table-column
        align="center"
        prop="required"
        :label="$t('quoteTemplate.required')"
        min-width="90"
      >
        <template v-slot="scope">
          <el-checkbox
            v-model="scope.row.required"
            true-label="Y"
            false-label="N"
            :disabled="readonly || fieldTypeIsFormula(scope.row.fieldType)"
          />
        </template>
      </el-table-column>

      <!--禁止编辑-->
      <el-table-column
        align="center"
        prop="forbidEdit"
        :label="$t('quoteTemplate.forbidEdit')"
        min-width="90"
      >
        <template v-slot="scope">
          <el-checkbox
            v-model="scope.row.forbidEdit"
            true-label="Y"
            false-label="N"
            :disabled="readonly || fieldTypeIsFormula(scope.row.fieldType)"
          />
        </template>
      </el-table-column>

      <!--是否为总价 只能存在一个-->
      <el-table-column
        align="center"
        prop="isTotal"
        :label="$t('quoteTemplate.isTotal')"
        min-width="90"
      >
        <template v-slot="scope">
          <el-checkbox
            v-model="scope.row.isTotal"
            true-label="Y"
            false-label="N"
            :disabled="readonly"
            @change="value => isTotalChange(value, scope)"
          />
        </template>
      </el-table-column>

      <!--操作-->
      <el-table-column
        v-if="!readonly"
        align="center"
        :label="$t('common.operation')"
        width="90"
      >
        <template v-slot="{ row, $index }">
          <!--删除-->
          <el-button type="text" @click="deleteRow($index)">
            {{ $t('common.delete') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
/**
 * 要素属性
 */
import {
  fieldTypeIsDate,
  fieldTypeIsFormula,
  fieldTypeIsEnum,
  fieldTypeIsNumber,
  fieldTypeIsDict
} from 'lib@/composition/quoteTemplate/utils'
import { validateRequiredColumn } from 'lib@/mixins/addStarToColumn'
import Sortable from 'sortablejs'

export default {
  name: 'AttrField',

  props: {
    readonly: {
      type: Boolean,
      default: false
    },
    pageFlag: {
      type: Object,
      required: true
    },
    fieldList: {
      type: Array,
      required: true
    }
  },

  data () {
    return {
      attrFieldData: [],
      editIndex: null,
      editRow: null,
      sortableTable: null,
      fieldTypeIsDate,
      fieldTypeIsFormula,
      fieldTypeIsEnum,
      fieldTypeIsNumber,
      fieldTypeIsDict
    }
  },

  watch: {
    attrFieldData: {
      handler (val) {
        this.$emit('attr-field-update', val)
      },
      immediate: true,
      deep: true
    },
    fieldList: {
      handler (val) {
        this.attrFieldData = JSON.parse(JSON.stringify((val || [])))
      },
      immediate: true,
      deep: true
    }
  },

  mounted () {
    this.$nextTick(() => {
      if (!this.readonly) {
        this.initSortable()
      }
    })
  },

  destroyed () {
    if (this.sortableTable) {
      // 销毁
      this.sortableTable.destroy()
    }
  },

  methods: {
    /* 初始化表格拖拽排序 */
    initSortable () {
      const el = (this.$refs.attrFieldTable || {}).$el
      const tbody = el ? el.querySelector('.el-table__body-wrapper tbody') : null
      const that = this

      if (tbody) {
        this.sortableTable = Sortable.create(tbody, {
          animation: 180,
          delay: 0,
          handle: '.icondrag',
          preventOnFilter: false,
          onEnd ({ newIndex, oldIndex }) {
            const $row = tbody.children[newIndex]
            const $oldRow = tbody.children[oldIndex]

            // 先删除移动的节点
            tbody.removeChild($row)
            // 再插入移动的节点到原有节点，还原了移动的操作
            if (newIndex > oldIndex) {
              tbody.insertBefore($row, $oldRow)
            } else {
              tbody.insertBefore($row, $oldRow.nextSibling)
            }
            // 更新items数组 vue会重新更新dom
            const item = that.attrFieldData.splice(oldIndex, 1)
            that.attrFieldData.splice(newIndex, 0, item[0])
          }
        })
      }
    },

    /* 新增行 */
    addRow () {
      this.attrFieldData.push({
        fieldName: '',
        fieldType: '',
        fieldValue: '',
        defaultValue: '',
        // 默认必填
        required: 'Y',
        forbidEdit: ''
      })
    },

    /* 删除 */
    deleteRow ($index) {
      this.attrFieldData.splice($index, 1)
    },

    /* 改变字段类型 */
    fieldTypeChange (value, { row }) {
      if (fieldTypeIsFormula(value)) {
        // 禁止编辑，非必填，且都不允许编辑
        row.forbidEdit = 'Y'
        row.required = 'N'
      } else {
        row.forbidEdit = 'N'
        row.required = 'Y'
      }
      // 变更类型，清空字段值
      row.fieldValue = ''
    },

    /* 字段名称失去焦点 */
    fieldNameBlur ({ row, $index }) {
      // 校验字段名称是否存在重复
      const findIndex = this.attrFieldData.findIndex(item => item.fieldName === row.fieldName)
      if (findIndex && findIndex !== $index) {
        this.$message.warning(this.$t('quoteTemplate.fieldNameRepeat', [findIndex + 1, $index + 1]))
      }
    },

    /* 是否为总价变更 */
    isTotalChange (value, { $index }) {
      if (value === 'N') {
        // 取消不用限制
        return
      }

      // 检查是否为总价唯一 兼容存在多个的情况
      this.attrFieldData.forEach((item, index) => {
        if (item.isTotal === 'Y' && index !== $index) {
          // 找到设置为总价的项 且不是当前行
          this.attrFieldData[index].isTotal = 'N'
        }
      })
    },

    /* 校验表单 */
    validateForm () {
      return new Promise(resolve => {
        // 校验表格必填项
        if (
          !validateRequiredColumn(
            this.$refs.attrFieldTable,
            this.attrFieldData,
            {
              validateScope: true,
              tableTitle: this.$t('quoteTemplate.attrField')
            }
          )
        ) {
          resolve({
            status: false
          })
          return
        }

        resolve({
          status: true,
          data: this.attrFieldData.concat()
        })
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.tip-icon {
  vertical-align: inherit;
  font-size: 12px;
  color: #96999C;
  margin-left: 5px;
}
.icondrag {
  cursor: move;
}
</style>
