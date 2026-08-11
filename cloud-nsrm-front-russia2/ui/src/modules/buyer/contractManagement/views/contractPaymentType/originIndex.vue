<template>
  <el-container direction="vertical" style="height:100%" class="flex-container">
    <div class="main">
      <easy-table
        ref="table"
        :selection="false"
        :methods="methods"
        :columns="columns"
        row-key="payTypeId"
        table-name="payType"
        :query-params.sync="queryParams"
      >
        <template #btns>
          <AuthorityButton type="primary" code="cm:contractPaymentType:add" @click="add">
            {{ $t("common.add") }}
          </AuthorityButton>
          <!-- <AuthorityButton
            type="primary"
            @click="save"
            code="cm:contractPaymentType:save"
            >{{ $t("common.submit") }}</AuthorityButton
          > -->
        </template>
        <template #payExplain="{ scope }">
          <el-input v-model="scope.row.payExplain" />
        </template>
        <template #condFactor="{ scope }">
          <dict-select v-model="scope.row.condFactor" code="CONDFACTOR" />
        </template>
        <template #valueRange="{ scope }">
          <dict-select v-model="scope.row.valueRange" code="VALUE_RANGE" />
        </template>
        <template #startDate="{ scope }">
          <el-date-picker
            v-model="scope.row.startDate"
            type="date"
            :format="$formatDatePicker"
            value-format="yyyy-MM-dd"
            :placeholder="$t('vendorMod.datePicker')"
          />
        </template>
        <template #endDate="{ scope }">
          <el-date-picker
            v-model="scope.row.endDate"
            type="date"
            :format="$formatDatePicker"
            value-format="yyyy-MM-dd"
            :placeholder="$t('vendorMod.datePicker')"
          />
        </template>
      </easy-table>
    </div>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import EasyTable from 'lib@/components/BaseTable/EasyTable'
import { geti18n } from '@/main'
import DictSelect from '@/library/components/c-select/dict-select'
import { createDictClass } from '@/library/utils/dict/dict-utils'
const i18n = geti18n()
const dictClass = createDictClass({
  VALUE_RANGE: [
    { id: 'MAX', label: i18n.t('contractMod.max'), value: 'MAX' },
    { id: 'MIN', label: i18n.t('contractMod.minimum'), value: 'MIN' }
  ]
}, false)

export default {
  name: 'ContractTypeElements',
  components: {
    EasyTable,
    DictSelect
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      dictClass: dictClass,
      name: 'contractTypeElement',
      tableName: 'contractTypeElement',
      queryParams: {},
      methods: {
        listPage: async params => {
          const res = await this.$api.cm.buyer.main.payType.paymentTermsPage(params)
          return res
        }
      },
      columns: [
        {
          attrs: {
            label: () => this.$t('contractMod.payExplain'),
            prop: 'payExplain',
            minWidth: 150
          },
          slot: 'payExplain',
          search: {},
          rules: { required: true, message: this.$t('dataConfMod.required') }
        },
        {
          attrs: {
            label: () => this.$t('contractMod.condFactor'),
            prop: 'condFactor',
            minWidth: 150
          },
          slot: 'condFactor',
          search: {
            component: DictSelect,
            props: { code: 'CONDFACTOR' }
          },
          rules: { required: true, message: this.$t('dataConfMod.required') }
        },
        {
          attrs: {
            label: () => this.$t('contractMod.valueRange'),
            prop: 'valueRange',
            minWidth: 150
          },
          slot: 'valueRange',
          rules: { required: true, message: this.$t('dataConfMod.required') }
        },
        {
          attrs: {
            label: () => this.$t('contractMod.startDate'),
            prop: 'startDate',
            minWidth: 150,
            sortable: true
          },
          slot: 'startDate',
          search: {
            component: 'el-date-picker',
            props: {
              valueFormat: 'yyyy-MM-dd'
            }
          }
        },
        {
          attrs: {
            label: () => this.$t('contractMod.endDate'),
            prop: 'endDate',
            minWidth: 150,
            sortable: true
          },
          slot: 'endDate'
        },
        {
          attrs: {
            label: () => this.$t('contractMod.createdBy'),
            prop: 'createdUserName',
            minWidth: 150
          }
        },
        {
          attrs: {
            label: () => this.$t('contractMod.creationDate'),
            prop: 'creationDate',
            sortable: true,
            minWidth: 150,
            formatter: val => this.$parseTime(val)
          }
        },
        {
          attrs: {
            label: () => this.$t('contractMod.lastUpdatedBy'),
            prop: 'lastUpdatedUserName',
            minWidth: 150
          }
        },
        {
          attrs: {
            label: () => this.$t('contractMod.lastUpdateDate'),
            prop: 'lastUpdateDate',
            sortable: true,
            minWidth: 150,
            formatter: val => this.$parseTime(val)
          }
        },
        {
          attrs: {
            prop: 'operation',
            label: () => this.$t('common.operation'),
            width: 100,
            fixed: 'right'
          },
          operations: [
            {
              event: 'deleteItem',
              name: this.$t('common.delete'),
              show: scope => {
                return scope.row.editable
              },
              func: this.deleteItem
            }
          ]
        }
      ]
    }
  },
  updated () {
    this.defaultTableHeader = this.tableHeader
  },
  created () { },
  mounted () {
  },
  methods: {
    deleteItem (scope, data) {
      data.splice(scope.$index, 1)
    },
    add () {
      // this.$refs.table.add({
      //   editable: true,
      //   startDate: parseTime(new Date(), "{y}-{m}-{d}")
      // });
    },
    save () {
      const list = this.$refs.table.getUpdatedRows()
      this.$refs.table.validate(f => {
        if (f) {
          this.$api.cm.buyer.main.payType.save(list).then(res => {
            this.$message.success(res.message)
            this.$refs.table.search(this.queryParams, true)
          })
        } else {
          this.$message({
            message: this.$t('vendorMod.pleasefinishRequired'), // '请输入单据必填信息'
            type: 'error'
          })
        }
      })
    }
  }
}
</script>
<style scoped lang="scss">
.contract_type_element_wrapper {
  .order-uploader {
    display: inline-block;
    margin: 0 10px;
  }
  .block {
    display: flex;
    justify-content: center;
  }
}
</style>
<style>
.contract_type_element_wrapper .el-table th > .cell {
  display: flex;
  justify-content: center;
}
.main {
  display: flex;
  flex-direction: column;
  flex-grow: 1;
}
</style>
