<template>
  <div
    :data-key="code"
    class="material-list-wrapper"
  >
    <template v-if="editable">
      <!-- 点击下方加号新增数据 -->
      <base-table
        stripe
        :data="value"
        :columns="columns"
        border
        :empty-text="$t('contractMod.clickToNewData')"
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
        :title="$t('components.viewSwitcher.add')"
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
          scope="col"
          valign="top"
          style="word-break: break-all;"
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
import minix from './minix'

const GenNonDuplicateID = randomLength => {
  return Number(
    Math.random()
      .toString()
      .substr(3, randomLength) + Date.now()
  ).toString(36)
}

export default {
  name: 'ZTable',
  components: { BaseTable },
  mixins: [minix],
  data () {
    return {
      columns: [],
      fields: []
    }
  },
  created () {
    const { elemRanges } = this.componentInfo
    this.fields = elemRanges.map(({ elemValue }, index) => ({
      name: elemValue,
      field: `${this.code}${index}`
    }))
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
        // 操作
        label: this.$t('components.headers.operation'),
        fixed: 'right'
      },
      operations: [
        {
          key: 'deleteAction',
          event: 'deleteItem',
          // 删除
          name: this.$t('components.common.delete'),
          attrs: { type: 'text' }
        }
      ]
    })
    this.columns = columns
  },
  methods: {
    addRow () {
      if (!Array.isArray(this.value)) {
        this.$emit('change', [])
        this.value = []
        this.$nextTick(() => this.value.push({}))
      }
      this.value.push({})
    },
    deleteItem (scope) {
      this.value.splice(scope.$index, 1)
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
