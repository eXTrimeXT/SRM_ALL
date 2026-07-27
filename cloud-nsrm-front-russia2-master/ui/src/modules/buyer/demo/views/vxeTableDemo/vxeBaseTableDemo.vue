<template>
  <el-container class="flex-container the_vxeBaseTableDemo_wrapper" direction="vertical">
    <el-main>
      <el-collapse v-model="activeDims" class="tab-form-style">
        <!-- 行表 -->
        <el-collapse-item :title="$t('common.rowList')" name="1">
          <div style="margin-bottom:10px;">
            <el-button type="primary">
              {{
                $t('common.add')
              }}
            </el-button>
          </div>
          <VxeBaseTable
            :tableData="tableData"
            :table-column="tableColumn"
            :showTooltipFieldList="['field1']"
            showCustomTable
            @checkChange="checkChange"
          >
            <template #field4="{ scope }">
              {{ scope.row.field4 }}
            </template>
          </VxeBaseTable>
        </el-collapse-item>
      </el-collapse>
    </el-main>
    <CToolbar>
      <template slot="right">
        <el-button>
          {{ $t('common.close') }}
        </el-button>
        <el-button
          type="primary"
          @click="save"
        >
          {{ $t('common.staging') }}
        </el-button>
      </template>
    </CToolbar>
  </el-container>
</template>
<script>
import VxeBaseTable from 'lib@/components/VxeBaseTable'
import CToolbar from 'lib@/components/c-toolbar'

export default {
  name: 'VxeBaseTableDemo',
  components: {
    VxeBaseTable,
    CToolbar
  },
  data () {
    return {
      activeDims: ['1'],
      tableData: [],
      tableColumn: [
        {
          field: 'field1',
          title: this.$t('cusEntry.supplement20250211.normalRendering'),  // '正常渲染'
          width: 120,
          formatter: ({ field1 }) => 'cellValue：' + field1
        },
        { columnType: 'input',
          disabled: false,
          field: 'field2',
          title: this.$t('contractMod.inputBox'),  // '输入框'
          width: 120,
          addStarToColumn: true,
          callback: ({ row }) => { console.log(row) }
        },
        { columnType: 'inputNumber',
          field: 'field3',
          title: this.$t('cusEntry.supplement20250211.digitInputBox'),  // '数字输入框'
          width: 120, 
          desc: this.$t('cusEntry.supplement20250211.iAmHint'),  // '我是提示'
          callback: ({ row }) => { console.log(row) }
        },
        { columnType: 'slot',
          field: 'field4',
          title: this.$t('cusEntry.supplement20250211.slot'),  // '插槽'
          width: 120,
          slot: 'field4'
        },
        { columnType: 'switch',
          field: 'field5',
          title: this.$t('cusEntry.supplement20250211.switchState'),  // '开关'
          width: 120,
          switchValues: { inactive: 1, active: 0 },
          callback: ({ row }) => { console.log(row) }
        },
        { columnType: 'select',
          field: 'field6',
          title: this.$t('bidMod.selectBox'), // '下拉框'
          width: 120,
          options: [{ id: 0, label: this.$t('cusEntry.supplement20250211.firstOne'), value: 0 }, { id: 1, label: this.$t('cusEntry.supplement20250211.secondVariable'), value: 1 }],
          callback: ({ row }) => { console.log(row) }
        },
        { columnType: 'date',
          field: 'field7',
          title: this.$t('components.date'),  // '日期'
          width: 120,
          type: 'date',
          callback: ({ row }) => { console.log(row) }
        },
        { columnType: 'dictSelect',
          field: 'field8',
          title: this.$t('dataConfMod.dictionary'),  // '字典'
          width: 120,
          code: 'COST_LINK_MATERIAL_PRICE_EXECUTE_STATUS',
          callback: ({ row }, data) => { console.log(row, data) }
        },
        {
          columnType: 'OUorganizationSelector',
          field: 'field9',
          title: this.$t('components.organization.ORG'),  // '业务实体'
          width: 120,
          callback: ({ row }, data) => { console.log(row, data) }
        },
        {
          columnType: 'quicksearch',
          field: 'field10',
          title: this.$t('reportSetting.quickSearch'),  // '快查'
          width: 120,
          showKey: 'companyName',
          propKey: 'companyId',
          name: 'scc_sup_company_info_all',
          callback: ({ row }, data) => { console.log(row, data) }
        },
        {
          columnType: 'buttons',
          field: 'operation',
          title: this.$t('components.headers.operation'),  // '操作'
          width: 120,
          fixed: 'right',
          buttons: [
            {
              text: this.$t('common.delete'),
              show: ({ field5 }) => field5 !== 1,
              callback: ({ row, $index }) => this.deleteRow(row, $index)
            }
          ]
        }
      ]
    }
  },
  created () {
    this.getList()
  },
  methods: {
    getList () {
      setTimeout(() => {
        this.tableData = [
          { field1: 'field1field1field1field1field1field1field1field1field1field1field1field1field1field1field1field1field1field1field1',
            field2: 'field2',
            field3: '100',
            field4: this.$t('cusEntry.supplement20250211.slot'),  // '插槽'
            field5: 1,
            field6: 0,
            field7: '',
            field8: '',
            field9: '',
            field10: ''
          },
          { field1: '',
            field2: 'field2',
            field3: '100',
            field4: this.$t('cusEntry.supplement20250211.slot'),  // '插槽'
            field5: 0,
            field6: 1,
            field7: '',
            field8: '',
            field9: '',
            field10: ''
          },
          { field1: 'field1',
            field2: 'field2',
            field3: '100',
            field4: this.$t('cusEntry.supplement20250211.slot'),  // '插槽'
            field5: 0,
            field6: 1,
            field7: '',
            field8: '',
            field9: '',
            field10: ''
          },
          { field1: 'field1',
            field2: 'field2',
            field3: '100',
            field4: this.$t('cusEntry.supplement20250211.slot'),  // '插槽'
            field5: 0,
            field6: 1,
            field7: '',
            field8: '',
            field9: '',
            field10: ''
          },
          { field1: 'field1',
            field2: 'field2',
            field3: '100',
            field4: this.$t('cusEntry.supplement20250211.slot'),  // '插槽'
            field5: 0,
            field6: 1,
            field7: '',
            field8: '',
            field9: '',
            field10: ''
          },
          { field1: 'field1',
            field2: 'field2',
            field3: '100',
            field4: this.$t('cusEntry.supplement20250211.slot'),  // '插槽'
            field5: 0,
            field6: 1,
            field7: '',
            field8: '',
            field9: '',
            field10: ''
          },
          { field1: 'field1',
            field2: 'field2',
            field3: '100',
            field4: this.$t('cusEntry.supplement20250211.slot'),  // '插槽'
            field5: 0,
            field6: 1,
            field7: '',
            field8: '',
            field9: '',
            field10: ''
          },
          { field1: 'field1',
            field2: 'field2',
            field3: '100',
            field4: this.$t('cusEntry.supplement20250211.slot'),  // '插槽'
            field5: 0,
            field6: 1,
            field7: '',
            field8: '',
            field9: '',
            field10: ''
          }
        ]
      }, 500)
    },
    checkChange (rows) {
      console.log(rows)
    },
    deleteRow (row, $index) {
      this.tableData.splice($index, 1)
    },
    save () {
      console.log(this.tableData)
    }
  }
}
</script>
