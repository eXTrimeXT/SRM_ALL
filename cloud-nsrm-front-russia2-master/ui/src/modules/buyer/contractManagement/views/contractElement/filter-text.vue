<template>
  <div>
    <div v-show="!isEdit">
      {{ filterText }}
    </div>
    <DictSelect
      v-show="isEdit && filterType === 'componentMap'"
      v-model="selectedValue"
      code="COMPONENT_TYPE"
      :dict-class="dictClass"
      @change-value="(value, dictItem) => addMethodF(value, dictItem)"
    />
    <el-button
      v-show="isEdit && filterType === 'elemRanges'"
      v-if="['SELECT', 'TABLE', 'MATERIALTABLE'].includes(addMethod)"
      type="text"
      @click="showElemRangesDialog"
    >
      {{
        value && value.length
          ? value.map((i) => i.elemValue).join(',')
          : $t('common.edit')
      }}
    </el-button>

    <srm-dialog
      :title="$t('contractMod.elemRanges')"
      :visible.sync="elemRangesDialogVisible"
      appendToBody
    >
      <div style="margin: 0 0 15px 0">
        <el-button
          type="primary"
          @click="addRange"
        >
          {{ $t('common.add') }}
        </el-button>
      </div>
      <BaseTable
        stripe
        :data="elemRangesTable"
        :columns="columns"
        :empty-text="$t('components.noData')"
        border
        @deleteRange="deleteRange"
      >
        <template #elemValue="scope">
          <el-input v-model="elemRangesTable[scope.$index].elemValue" />
        </template>
        <template #startDate="scope">
          <el-date-picker
            v-model="elemRangesTable[scope.$index].startDate"
            type="date"
            :format="$formatDatePicker"
            value-format="yyyy-MM-dd"
          />
        </template>
        <template #endDate="scope">
          <el-date-picker
            v-model="elemRangesTable[scope.$index].endDate"
            type="date"
            :format="$formatDatePicker"
            value-format="yyyy-MM-dd"
          />
        </template>
      </BaseTable>
      <template
        #footer
        class="dialog-footer"
      >
        <el-button @click="elemRangesDialogVisible = false">
          {{ $t('common.cancel') }}
        </el-button>
        <el-button
          type="primary"
          @click="saveRanges"
        >
          {{ $t('common.submit') }}
        </el-button>
      </template>
    </srm-dialog>
  </div>
</template>

<script>
import { componentMap } from 'modb@/contractManagement/views/contractManager/parser/enum'
import { createDictClass } from '@/library/utils/dict/dict-utils'
import DictSelect from '@/library/components/c-select/dict-select'
import { defineSchemas, generateXindexInOrder, expression, i18nExpression } from '@meicloud/render-engine'
import BaseTable from 'lib@/components/BaseTable'

export default {
  components: {
    BaseTable
  },
  props: {
    filterType: {
      type: String,
      default: ''
    },
    isEdit: {
      type: Boolean,
      default: false
    },
    value: {
      type: [String, Array],
      default: ''
    },
    addMethod: {
      type: String,
      default: ''
    },
    elemMaintainId: {
      type: String,
      default: ''
    }
  },
  data () {
    return {
      filterText: '',
      dictClass: null,
      selectedValue: '',
      elemRangesDialogVisible: false,
      elemRangesTable: [],
      columns: [
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: (t) => t.$t('contractMod.elemValue'),
            prop: 'elemValue'
          },
          slot: 'elemValue'
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: (t) => t.$t('contractMod.startDate'),
            prop: 'startDate'
          },
          slot: 'startDate'
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: (t) => t.$t('contractMod.endDate'),
            prop: 'endDate'
          },
          slot: 'endDate'
        },
        {
          attrs: {
            align: 'center',
            label: (t) => t.$t('common.operation'),
            fixed: 'right',
            width: 80
          },
          operations: [
            {
              key: 'deleteRange',
              event: 'deleteRange',
              name: this.$t('common.delete'),
              // show: () => !this.readOnly,
              attrs: { type: 'text' }
            }
          ]
        }
      ]
    }
  },
  watch: {
    value: {
      handler: function (newV) {
        if (!this.dictClass) {
          this.dictClass = createDictClass({
            COMPONENT_TYPE: Object.keys(componentMap).map((i) => ({
              id: i,
              label: componentMap[i],
              value: i
            }))
          }, false)
        }

        switch (this.filterType) {
          case 'componentMap':
            this.filterText = this.componentFunction(newV)
            this.selectedValue = newV
            break
          case 'getDictLabel':
            this.filterText = this.$getDictLabel('ELEM_CONTRACT_TYPE', newV)
          break
          case 'elemRanges':
            this.filterText = this.initElemRanges(newV)
          break
          default:
            this.filterText = newV
            break
        }
      },
      immediate: true
    }
  },
  methods: {
    showElemRangesDialog () {
      this.elemRangesDialogVisible = true
      if (this.value) {
        this.elemRangesTable = JSON.parse(JSON.stringify(this.value))
      } else {
        this.elemRangesTable = []
      }
    },
    addMethodF (value, dictItem) {
      this.$emit('change', value)
    },
    componentFunction (val) {
      let listType = this.dictClass.dictStore.dictStates.COMPONENT_TYPE
      let label = ''
      listType.forEach(e => {
        if (e.value == val) {
          label = e.label
        }
      })
      return label
    },
    initElemRanges (v) {
      if (v && v.length) {
        return v.map((i) => i.elemValue).join(',')
      } else {
        return '-'
      }
    },
    addRange () {
      this.elemRangesTable.push({
        startDate: this.$dayjs().format('YYYY-MM-DD')
      })
    },
    deleteRange (scope) {
      this.elemRangesTable.splice(scope.$index, 1)
    },
    saveRanges () {
      let that = this
      // if (this.elemMaintainId) {
      //   this.$refs.table.realDataSource.forEach((item) => {
      //     if (item.elemMaintainId === that.elemMaintainId) {
      //       this.$set(item, 'elemRanges', JSON.parse(JSON.stringify(that.elemRangesTable)))
      //     }
      //   })
      // }
      // console.log('###### 日志 this.elemRangesTable ######')
      // console.log(this.elemRangesTable)
      this.$emit('change', this.elemRangesTable)
      this.elemRangesDialogVisible = false
    }
  }

}
</script>
