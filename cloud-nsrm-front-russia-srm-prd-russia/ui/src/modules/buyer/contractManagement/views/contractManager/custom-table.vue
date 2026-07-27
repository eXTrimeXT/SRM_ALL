<template>
  <div
    :data-key="tableKey"
    class="material-list-wrapper"
  >
    <template v-if="visible">
      <base-table
        stripe
        :data="value"
        :columns="columns"
        border
        :empty-text="this.$t('contractMod.clickToNewData')"
        @deleteItem="deleteItem"
      >
        <template
          v-for="item in fields"
          :slot="item.field"
          slot-scope="scope"
        >
          <el-input
            :key="item.field"
            v-model="value[scope.$index][item.field]"
            @change="changeHandle"
          />
        </template>
      </base-table>
      <div
        :title="this.$t('common.new')"
        class="plus-wrapper"
        @click="addRow"
      >
        <i class="el-icon-plus plus" />
      </div>
    </template>
    <table
      v-else
      cellspacing="0"
      cellpadding="0"
      width="100%"
      border="1"
    >
      <tr class="firstRow">
        <th
          v-for="item in fields"
          :key="item.field"
          valign="top"
          style="word-break: break-all;"
          scope="col"
        >
          {{ item.name }}
        </th>
      </tr>
      <tr
        v-for="(item, index) in value"
        :key="index"
      >
        <td
          v-for="field in fields"
          :key="field.field"
          class="pay-plan-td"
          valign="top"
          style="word-break: break-all;"
        >
          {{ item ? item[field.field] : "" }}
        </td>
      </tr>
    </table>
  </div>
</template>
<script>
import BaseTable from 'lib@/components/BaseTable'

// TODO: 1.数据格式化 2.根据visible属性展示可编辑表格或者是普通表格（ps: 覆盖默认样式）

export default {
  name: 'MaterialList',
  components: { BaseTable },
  model: {
    prop: 'value',
    event: 'change'
  },
  props: {
    // 展示可以编辑表格或者是普通表格
    visible: {
      type: Boolean,
      default: false
    },
    tableKey: {
      type: String
    },
    value: {
      type: Array,
      default: () => []
    },
    fields: {
      type: Array,
      default: () => []
    }
  },
  data () {
    return {
      columns: []
    }
  },
  computed: {},
  created () {
    const columns = this.fields.map(({ field, name }) => ({
      slot: field,
      attrs: {
        minWidth: '120',
        align: 'center',
        porp: field,
        label: name
      }
    }))
    columns.push({
      attrs: {
        align: 'center',
        label: () => this.$t('common.operation'),
        fixed: 'right'
      },
      operations: [
        {
          key: 'deleteAction',
          event: 'deleteItem',
          name: this.$t('common.delete'),
          attrs: { type: 'text' }
        }
      ]
    })
    this.columns = columns
  },
  methods: {
    addRow () {
      this.value.push({})
    },
    deleteItem (scope) {
      this.value.splice(scope.$index, 1)
      // console.log("material-list: deleteItem", scope);
      // this.$emit("deleteItem", scope);
      this.$emit('change', this.value)
    },
    changeHandle () {
      this.$emit('change', this.value)
    }
  }
}
</script>
<style>
.material-list-wrapper .el-table th {
  background-color: #fff !important;
}
.plus-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 30px;
  cursor: pointer;
  border: 1px solid #ddd;
  border-top: none;
}
.plus {
  font-size: 18px;
  font-weight: bold;
}
.material-td {
  text-align: center;
}
</style>
