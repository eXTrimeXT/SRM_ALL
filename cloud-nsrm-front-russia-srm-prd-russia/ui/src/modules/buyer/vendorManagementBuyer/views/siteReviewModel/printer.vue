<template>
  <div>
    <div ref="printer">
      <el-collapse v-model="activeDims">
        <el-collapse-item
          v-for="(item,index) in editableTabs"
          :ref="item.name"
          :key="item.name"
          :title="item.title"
          :name="'sec_'+index"
        >
          <!-- 表单情况 -->
          <template v-if="item.type == 'FORM'">
            <el-form
              ref="form"
            >
              <srm-row>
                <srm-col
                  v-for="(item2, indexF) in item.content"
                  :key="indexF + 't'"
                  :initCol="item2.fieldType == 'textarea' ? 2 : 4"
                >
                  <el-form-item
                    :prop="item2.fieldContent"
                    :label="item2.fieldName"
                    :rules="[{
                      required: item2.necessaryFlag == 'true',
                      message: '必填项'
                    }]"
                  >
                    <el-date-picker
                      v-if="item2.fieldType == 'timer'"
                      v-model="item2.fieldContent"
                      type="date"
                      :placeholder="$t('common.pleaseSelectDate')"
                      format="yyyy-MM-dd"
                      value-format="yyyy-MM-dd"
                      :readonly="readOnly"
                    />
                    <DictSelect
                      v-if="item2.fieldType == 'select'"
                      v-model="item2.fieldContent"
                      :code="item2.dictCode"
                      :readonly="readOnly"
                    />
                    <el-input
                      v-if="
                        item2.fieldType == 'textarea' || item2.fieldType == 'text'
                      "
                      v-model="item2.fieldContent"
                      :value="item2.fieldContent"
                      :type="item2.fieldType == 'textarea' ? 'textarea' : 'text'"
                      :readonly="readOnly"
                    />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-form>
          </template>
          <!-- 表格情况 -->
          <template v-if="item.type == 'TABLE'">
            <el-button
              class="detail-pbtn"
              style="margin-bottom:10px"
              type="primary"
              @click="addList"
            >
              {{ $t('bidMod.affairsIncreased') }}
            </el-button>
            <el-table
              :data="tableData"
              style="width: 100%"
              border
              stripe
            >
              <el-table-column
                :key="index"
                :label="$t('contractMod.order')"
                align="center"
                type="index"
                width="50"
              />
              <el-table-column
                v-for="(item2, indexT) in item.content"
                :key="indexT + 'q'"
                :prop="item2.fieldCode"
                :label="item2.fieldName"
              >
                <template slot-scope="scope">
                  <el-date-picker
                    v-if="item2.fieldType == 'timer'"
                    v-model="scope.row[item2.fieldCode]"
                    type="date"
                    :placeholder="$t('common.pleaseSelectDate')"
                    format="yyyy-MM-dd"
                    value-format="yyyy-MM-dd"
                    :readonly="readOnly"
                  />
                  <DictSelect
                    v-if="item2.fieldType == 'select'"
                    v-model="scope.row[item2.fieldCode]"
                    :code="item2.dictCode"
                    :readonly="readOnly"
                  />
                  <el-input
                    v-if="
                      item2.fieldType == 'textarea' || item2.fieldType == 'text'
                    "
                    v-model="scope.row[item2.fieldCode]"
                    :value="scope.row[item2.fieldCode]"
                    :type="item2.fieldType == 'textarea' ? 'textarea' : 'text'"
                    :readonly="readOnly"
                  />
                </template>
              </el-table-column>
            </el-table>
          </template>
        </el-collapse-item>
      </el-collapse>
    </div>
  </div>
</template>

<script>
import { adaptDictData } from '@/utils'
import { getDictItem } from '@/api/common'
import DictSelect from 'lib@/components/c-select/dict-select'
export default {
  name: 'Printer',
  components: {
    DictSelect
  },
  props: {
    editableTabs: {
      type: Array,
      default: () => {}
    },
    readOnly: {
      type: Boolean,
      default: () => {}
    },
    tableData: {
      type: Array,
      default: () => {
        return [{}]
      }
    }
  },
  data () {
    return {
      activeDims: [
        'sec_0',
        'sec_1',
        'sec_2',
        'sec_3',
        'sec_4',
        'sec_5',
        'sec_6',
        'sec_7',
        'sec_8',
        'sec_9',
        'sec_10',
        'sec_11',
        'sec_12'
      ],
      selectList: []
    }
  },
  watch: {
    editableTabs: {
      handler () {
        console.log(this.editableTabs,'editableTabs')
      },
      deep: true,
      immediate: true
    },
    tableData: {
      handler () {
        console.log(this.tableData,'tableData')
      },
      deep: true,
      immediate: true
    }
  },
  mounted () {},
  created () {},

  methods: {
    addList () {
      this.tableData.push({})
    }
  }
}
</script>

<style scoped lang="scss">
.el-date-editor.el-input {
  width: 100%;
}
</style>
