<template>
  <div class="material-list-wrapper">
    <template v-if="visible">
      <base-table
        stripe
        :data="value"
        :columns="columns"
        border
        empty-text="点击下方加号新增数据"
        @deleteItem="deleteItem"
      >
        <template
          slot="materialName"
          slot-scope="scope"
        >
          <el-input
            v-model="value[scope.$index].materialName"
            @change="changeHandle"
          />
        </template>
        <template
          slot="categoryName"
          slot-scope="scope"
        >
          <el-input
            v-model="value[scope.$index].categoryName"
            @change="changeHandle"
          />
        </template>
        <template
          slot="untaxedPrice"
          slot-scope="scope"
        >
          <el-input
            v-model="value[scope.$index].untaxedPrice"
            @change="changeHandle"
          />
        </template>
        <template
          slot="contractQuantity"
          slot-scope="scope"
        >
          <el-input
            v-model="value[scope.$index].contractQuantity"
            @change="changeHandle"
          />
        </template>
      </base-table>
      <div
        title="添加"
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
          :key="item"
          valign="top"
          style="word-break: break-all;"
          scope="col"
        >
          {{ $t(`contractMod.${item}`) }}
        </th>
      </tr>
      <tr
        v-for="(item, index) in payPlans"
        :key="index"
      >
        <td
          v-for="field in fields"
          :key="field"
          class="pay-plan-td"
          valign="top"
          style="word-break: break-all;"
        >
          {{ item ? item[field] : "" }}
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
      width: 206,
      columns: [
        {
          slot: 'materialName',
          attrs: {
            align: 'center',
            porp: 'materialName',
            label: context => context.$t('contractMod.materialName')
          }
        },
        {
          slot: 'categoryName',
          attrs: {
            align: 'center',
            porp: 'categoryName',
            label: context => context.$t('contractMod.categoryName')
          }
        },
        {
          slot: 'untaxedPrice',
          attrs: {
            align: 'center',
            porp: 'untaxedPrice',
            label: context => context.$t('contractMod.untaxedPrice')
          }
        },
        {
          slot: 'contractQuantity',
          attrs: {
            align: 'center',
            porp: 'contractQuantity',
            label: context => context.$t('contractMod.contractQuantity')
          }
        },
        {
          attrs: {
            align: 'center',
            label: '操作'
          },
          operations: [
            {
              key: 'deleteAction',
              event: 'deleteItem',
              name: '删除',
              attrs: { type: 'text' }
            }
          ]
        }
      ]
    }
  },
  computed: {},
  methods: {
    addRow () {
      this.value.push({})
    },
    deleteItem (scope) {
      console.log('material-list: deleteItem', scope)
      this.$emit('deleteItem', scope)
      this.$emit('change', this.value)
    },
    changeHandle () {
      this.$emit('change', this.value)
    }
  }
}
</script>
<style scoped>
.material-list-wrapper /deep/ .el-table th {
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
