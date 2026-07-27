<template>
  <div class="element-attr-wrap">
    <!--新增-->
    <QuickSearch
      v-if="!readonly"
      ref="tempAttrQuickSearch"
      :btn-title="$t('quoteTemplate.selectAttr')"
      name="sou_quote_temp_attr"
      show-button
      multi-select
      :pre-query-data="{ 't.attr_status': 'VALID' }"
      @close-quicksearch="addRows"
    />

    <el-table
      ref="tempLineListTable"
      :data="tempLineListData"
      style="margin-top: 10px;"
      border
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

      <!--属性编码-->
      <el-table-column
        align="center"
        prop="attrNo"
        :label="$t('quoteTemplate.attrNo')"
        min-width="150"
      />

      <!--属性名称-->
      <el-table-column
        align="center"
        prop="attrName"
        :label="$t('quoteTemplate.attrName')"
        min-width="150"
      />

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
            :disabled="readonly"
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
        min-width="90"
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
 * 详细信息
 */
import QuickSearch from 'lib@/components/QuickSearch'
import Sortable from 'sortablejs'

export default {
  name: 'DetailInfo',

  components: {
    QuickSearch
  },

  props: {
    readonly: {
      type: Boolean,
      default: false
    },
    tempLineList: {
      type: Array,
      required: true
    }
  },

  data () {
    return {
      tempLineListData: [],
      sortableTable: null
    }
  },

  watch: {
    tempLineList: {
      handler (val) {
        this.tempLineListData = JSON.parse(JSON.stringify(val || []))
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
      const el = (this.$refs.tempLineListTable || {}).$el
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
            const item = that.tempLineListData.splice(oldIndex, 1)
            that.tempLineListData.splice(newIndex, 0, item[0])
          }
        })
      }
    },

    /* 添加属性 多选 */
    async addRows (value) {
      const currentAttrIdList = this.tempLineListData.map(item => item.attrId)
      // 筛选数据，移除重复添加项
      const filterValue = (value || []).filter(item => !currentAttrIdList.includes(item.attrId))
      if (filterValue.length !== value.length) {
        // 已剔除添加项中的重复属性项，属性项需唯一
        this.$message.warning(this.$t('quoteTemplate.filterSelectAttrValue'))
      }

      if (this.tempLineListData.length === 0 || filterValue.length > 0) {
        this.tempLineListData = this.tempLineListData.concat(filterValue.map(item => {
          return {
            ...item,
            required: 'Y',
            isTotal: 'N'
          }
        }))
      }
    },

    /* 删除 */
    deleteRow ($index) {
      this.tempLineListData.splice($index, 1)
    },

    /* 是否为总价变更 */
    isTotalChange (value, { $index }) {
      if (value === 'N') {
        // 取消不用限制
        return
      }

      // 检查是否为总价唯一 兼容存在多个的情况
      this.tempLineListData.forEach((item, index) => {
        if (item.isTotal === 'Y' && index !== $index) {
          // 找到设置为总价的项 且不是当前行
          this.tempLineListData[index].isTotal = 'N'
        }
      })
    },

    /* 校验表单 */
    validateForm () {
      return new Promise(resolve => {
        if (this.tempLineListData.length === 0) {
          // 详细信息报价属性数据请至少添加一行
          this.$message.warning(this.$t('quoteTemplate.tempLineListRequired'))
          resolve({
            status: false
          })
          return
        }

        resolve({
          status: true,
          data: this.tempLineListData
        })
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.icondrag {
  cursor: move;
}
</style>
