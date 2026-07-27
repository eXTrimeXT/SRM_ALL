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
      ref="formulaTable"
      :data="formulaData"
      border
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
          <el-select
            v-model="scope.row.fieldName"
            :disabled="readonly"
            @change="value => fieldNameChange(value, scope)"
          >
            <el-option
              v-for="(item, index) in formulaFieldList"
              :key="`fieldName-${index}`"
              :label="item.fieldName"
              :value="item.fieldName"
            />
          </el-select>
        </template>
      </el-table-column>

      <!--应用条件-->
      <el-table-column
        align="center"
        prop="conditionNameShow"
        :label="$t('quoteTemplate.condition.label')"
        min-width="150"
      >
        <template v-slot="{ row, $index }">
          <!--公式-属性不能编辑应用条件-->
          <el-tooltip
            effect="dark"
            :content="row.conditionNameShow ? row.conditionNameShow : $t('quoteTemplate.condition.edit')"
            placement="top"
          >
            <el-button
              type="text"
              class="condition-name-button"
              @click="openConditionDialog($index, row)"
            >
              {{ row.conditionNameShow ? row.conditionNameShow : $t(`quoteTemplate.condition.${readonly ? 'view' : 'edit'}`) }}
            </el-button>
          </el-tooltip>
        </template>
      </el-table-column>

      <!--价格公式-->
      <el-table-column
        align="center"
        prop="formulaValue"
        :label="$t('quoteTemplate.priceFormula')"
        min-width="180"
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <el-input
            v-model="scope.row.formulaValue"
            :disabled="readonly"
          >
            <template slot="prepend">
              {{ $t('quoteTemplate.price') }}=
            </template>

            <!--弹出输入文本域框-->
            <el-popover
              slot="append"
              placement="top-start"
              width="550"
              trigger="click"
            >
              <el-input
                v-model="scope.row.formulaValue"
                type="textarea"
                :rows="4"
                :disabled="readonly"
              />

              <el-button slot="reference" icon="el-icon-edit-outline" />
            </el-popover>
          </el-input>
        </template>
      </el-table-column>

      <el-table-column
        v-if="!readonly"
        align="center"
        :label="$t('common.operation')"
        width="90"
      >
        <template v-slot="{ $index }">
          <!--删除-->
          <el-button type="text" @click="deleteRow($index)">
            {{ $t('common.delete') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!--应用条件-->
    <ConditionDialog
      v-if="conditionDialogVisible"
      :visible.sync="conditionDialogVisible"
      :attr-field-data="attrFieldData"
      :detail-data="editRow"
      :readonly="readonly"
      @confirm="confirmCondition"
    />
  </div>
</template>

<script>
/**
 * 公式定义
 */
import { fieldTypeIsFormula } from 'lib@/composition/quoteTemplate/utils'
import { validateRequiredColumn } from '@/library/mixins/addStarToColumn'
import ConditionDialog from './formula/applicationConditionDialog.vue'
import Sortable from 'sortablejs'

export default {
  name: 'Formula',

  components: { ConditionDialog },

  props: {
    readonly: {
      type: Boolean,
      default: false
    },
    attrFieldData: {
      type: Array,
      required: true
    },
    formulaList: {
      type: Array,
      required: true
    }
  },

  data () {
    return {
      formulaData: [],
      fieldNameList: [],
      editIndex: '',
      editRow: null,
      conditionDialogVisible: false,
      sortableTable: null
    }
  },

  computed: {
    // 可选的公式字段列表
    formulaFieldList () {
      return this.attrFieldData.filter(item => fieldTypeIsFormula(item.fieldType) && item.fieldName)
    }
  },

  watch: {
    formulaList: {
      handler (val) {
        this.formulaData = val || []
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
      const el = (this.$refs.formulaTable || {}).$el
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
            const item = that.formulaData.splice(oldIndex, 1)
            that.formulaData.splice(newIndex, 0, item[0])
          }
        })
      }
    },

    /* 新增行 */
    addRow () {
      this.formulaData.push({
        fieldName: '',
        formulaValue: ''
      })
    },

    /* 删除行 */
    deleteRow ($index) {
      this.formulaData.splice($index, 1)
    },

    /* 字段名称改变 */
    fieldNameChange (value, scope) {
      // 冗余类型
      const attributeObj = this.formulaFieldList.find(item => item.fieldName === value)
      if (attributeObj && attributeObj.fieldType !== this.formulaData[scope.$index].fieldType) {
        // 类型变更了才去更新
        this.$set(
          this.formulaData[scope.$index],
          'fieldType',
          attributeObj.fieldType
        )
      }
    },

    /* 应用条件 START */
    // 打开弹窗
    openConditionDialog ($index, row) {
      this.editIndex = $index
      this.editRow = {
        conditionName: row.conditionName,
        conditionNameJson: row.conditionNameJson
      }
      this.conditionDialogVisible = true
    },
    // 保存应用条件
    confirmCondition (value) {
      this.$set(
        this.formulaData,
        this.editIndex,
        {
          ...this.formulaData[this.editIndex],
          conditionName: value.conditionName,
          conditionNameShow: value.conditionNameShow,
          conditionNameJson: value.conditionNameJson
        }
      )
    },
    /* END */

    /* 校验表单并返回数据 */
    validateForm () {
      return new Promise(resolve => {
        // 校验表格必填项
        if (
          !validateRequiredColumn(
            this.$refs.formulaTable,
            this.formulaData,
            {
              validateScope: true,
              tableTitle: this.$t('quoteTemplate.formula')
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
          data: this.formulaData
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

.condition-name-button {
  max-width: 100%;
  & :deep(> span) {
    display: inline-block;
    max-width: 100%;
    text-overflow: ellipsis;
    overflow: hidden;
    white-space: nowrap;
  }
}

.icondrag {
  cursor: move;
}
</style>
