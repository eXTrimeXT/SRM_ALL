<template>
  <el-container
    class="flex-container-notab basicPrice_wrapper"
    direction="vertical"
  >
    <el-main>
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template #left>
          <AuthorityButton
            code="bid:attribute:add"
            type="primary"
            @click="addRow"
          >
            {{ $t('common.add') }}
          </AuthorityButton>
        </template>
      </MainHeader>

      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :row-index-fixed="false"
        :page-size="15"
        :checkbox="false"
        :pre-query-data="queryParam"
        :source="attr.listPage"
        :open-custom-table="true"
      >
        <template #attributeName="props">
          <el-input v-model="props.scope.row.attributeName" />
        </template>
      </TableView>
    </el-main>
  </el-container>
</template>

<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import { parseTime } from '@/utils'
import { attr } from 'modb@/biddingManagementBuyer/api'

export default {
  name: 'Attribute',
  components: {
    TableView,
    MainHeader
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      attr: attr,
      defaultTableHeader: [],
      gridId: 'list',
      tableHeader: [
        // 属性编码
        {
          label: () => this.$t('attr.attributeCode'),
          prop: 'attributeCode',
          minWidth: 150
        },
        // 属性名称
        {
          label: () => this.$t('attr.attributeName'),
          prop: 'attributeName',
          showType: 'slot',
          slot: 'attributeName',
          editable: row => row.editable,
          minWidth: 150
        },
        // 创建人
        {
          prop: 'createdUserName',
          label: this.$t('common.creator'),
          width: 100
        },
        // 创建时间
        {
          prop: 'creationDate',
          label: this.$t('common.creationTime'),
          width: 100,
          formattor: cellValue => cellValue ? parseTime(cellValue, '{y}-{m}-{d}') : ''
        },
        {
          prop: 'operation',
          label: () => this.$t('common.operation'),
          showType: 'buttons',
          btnStyle: 'text',
          width: 150,
          buttons: [
            // b 保存
            {
              show: row => row.editable,
              callback: row => this.saveRow(row),
              formattor: () => this.$t('common.save')
            },
            // b 取消
            {
              show: row => row.editable,
              callback: (row, scope) => this.cancelRow(scope),
              formattor: () => this.$t('common.cancel')
            },
            // 删除
            {
              // 只能删除非编辑状态的
              show: row => !row.editable,
              callback: row => this.deleteItem(row),
              formattor: () => this.$t('common.delete')
            }
          ]
        }
      ],
      tableData: [],
      queryParam: {}
    }
  },
  updated () {
    this.defaultTableHeader = this.tableHeader
  },
  created () {
    this.getQueryData()
  },
  methods: {
    /* 查询 */
    getQueryData (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },

    /* 删除 */
    deleteItem ({ materialAttributeId }) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        attr.deleteItem(materialAttributeId).then(res => {
          this.$message.success(res.message)
          this.getQueryData()
        })
      })
    },

    /* 新增行 */
    addRow () {
      this.$refs[this.gridId].addOneEditableColumn()
    },

    /* 取消行 */
    cancelRow ({ $index }) {
      // 现阶段只能取消新增行，故直接从表格删除即可
      this.$refs[this.gridId].deleteRow($index)
    },

    /* 保存行 */
    saveRow (row) {
      if (!row.attributeName) {
        this.$message.warning(this.$t('dataConfMod.msgFieldName'))
        return
      }
      attr.add(row).then(res => {
        this.$message.success(res.message)
        this.getQueryData()
      })
    }
  }
}
</script>
