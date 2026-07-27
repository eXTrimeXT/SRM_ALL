<template>
  <div class="vuedraggable">
    <div class="filter-field-setting-wrap">
      <draggable
        v-model="componentsList"
        :animation="200"
        handle=".handle-operation"
      >
        <div
          v-for="(item, index) in componentsList"
          :key="`draggable-item-${index}`"
          class="draggable-item"
        >
          <div class="draggable-item-wrap">
            <div class="operation-wrap">
              <em class="el-icon-s-operation handle-operation" />
            </div>
            <DictSelect
              v-if="item.component === 'select'"
              v-model="componentsData[item.id]"
              :code="item.type"
              :dict-class="dictClass"
              style="width: 100%;"
            />
            <el-input
              v-else-if="item.component === 'input'"
              v-model="componentsData[item.id]"
              class="model-input"
            />
            <div class="delete-wrap">
              <em class="el-icon-delete" @click="deleteRow(item, index)" />
            </div>
          </div>
        </div>

        <el-dropdown
          slot="footer"
          trigger="click"
          @command="commandHandle"
        >
          <el-button
            type="primary"
            icon="el-icon-circle-plus-outline"
            class="el-button-icon"
          />

          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item
              v-for="item in commandItems"
              :key="item.key"
              :command="item"
            >
              {{ item.label }}
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </draggable>
    </div>

    <p>{{ componentsList }}</p>

    <p>{{ componentsData }}</p>

    <p>{{ $t('quoteTemplate.condition.desc') }}</p>
    <el-input
      v-model="conditionDesc"
      type="textarea"
      disabled
    />

    <p>
      <el-button type="primary" @click="save">
        保存
      </el-button>
    </p>
  </div>
</template>

<script>
import {
  leftBracket,
  rightBracket,
  connector,
  conditionComparator
} from 'lib@/composition/quoteTemplate/utils'
import { createDictClass } from 'lib@/utils/dict/dict-utils'
import draggable from 'vuedraggable'

export default {
  name: 'Vuedraggable',

  components: {
    draggable
  },

  data () {
    return {
      dictClass: createDictClass(
        {
          bracket: [...leftBracket, ...rightBracket],
          connector: [...connector],
          conditionComparator: [...conditionComparator],
          currentField: [
            {
              id: 1,
              value: '单价',
              label: '单价'
            }
          ],
          selectField: [
            {
              id: 1,
              value: '数量',
              label: '数量'
            }
          ]
        },
        false,
      ),
      componentsData: {},
      componentsList: [],
      componentsTable: [],
      commandItems: [
        { key: 'item-1', component: 'select', type: 'bracket', label: '括号' },
        { key: 'item-3', component: 'select', type: 'connector', label: '连接符' },
        { key: 'item-4', component: 'select', type: 'conditionComparator', label: '操作符' },
        { key: 'item-4', component: 'input', type: 'value', label: '属性值' },
        { key: 'item-5', component: 'select', type: 'currentField', label: '当前属性字段' },
        { key: 'item-6', component: 'select', type: 'selectField', label: '所选属性字段' }
      ],
      // 递增的key
      keyIndex: 1000
    }
  },

  computed: {
    // 把应用条件列表转化为文本
    conditionDesc () {
      return this.componentsList.reduce((str, item) => {
        str += (this.componentsData[item.id].toString() || ' ') + ' '
        return str
      }, '')
    }
  },

  methods: {
    commandHandle (value) {
      console.log(value)
      this.add(value)
    },

    add (value) {
      // 写入key, 用于双向绑定
      this.componentsData = {
        ...this.componentsData,
        ['100' + this.keyIndex]: ''
      }
      // 列表添加
      this.componentsList.push({
        id: '100' + this.keyIndex,
        name: '组件-' + this.keyIndex,
        component: value.component,
        type: value.type
      })

      this.keyIndex++
    },

    deleteRow (item, index) {
      delete this.componentsData[item.id]
      this.componentsList.splice(index, 1)
    },

    save () {
      console.log(this.componentsList)
      console.log(this.componentsData)
    },

    // 根据组件类型，返回下拉数据列表
    getSelectedOptionsByType (type) {
      const maps = new Map([
        ['bracket', [...leftBracket, ...rightBracket]],
        ['connector', [...connector]],
        ['conditionComparator', [...conditionComparator]]
      ])

      return maps.get(type)
    }
  }
}
</script>

<style lang="scss" scoped>
.filter-field-setting-wrap :deep(.draggable-item) {
  display: inline-block;
  margin-right: 10px;
  border-radius: 4px;
  .draggable-item-wrap {
    display: flex;
    max-width: 260px;
    background-color: #f5f7fa;
    border: 1px solid #dcdfe6;
    border-radius: 3px;
    .operation-wrap {
      width: 30px;
      text-align: center;
      line-height: 30px;
      .el-icon-s-operation {
        font-size: 14px;
        cursor: move;
      }
    }

    .model-input {
      flex: 1;
    }

    .delete-wrap {
      width: 30px;
      text-align: center;
      line-height: 30px;
      .el-icon-delete {
        font-size: 14px;
        cursor: pointer;
      }
    }
  }
}
</style>
