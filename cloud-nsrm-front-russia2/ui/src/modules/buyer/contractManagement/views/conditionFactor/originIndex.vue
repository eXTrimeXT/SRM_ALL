<template>
  <el-container
    direction="vertical"
    class="flex-container"
  >
    <el-main>
      <EasyTable
        ref="table"
        :selection="false"
        :methods="methods"
        :columns="columns"
        row-key="condFactorId"
        table-name="condFactor"
        :query-params.sync="queryParams"
      >
        <template #btns>
          <AuthorityButton
            type="primary"
            code="cm:conditionFactor:add"
            @click="add"
          >
            {{ $t("common.add") }}
          </AuthorityButton>
          <AuthorityButton
            type="primary"
            code="cm:conditionFactor:save"
            @click="save"
          >
            {{ $t("common.submit") }}
          </AuthorityButton>
        </template>
        <template #condFactor="{ scope }">
          <el-input v-model="scope.row.condFactor" />
        </template>
        <template #menuName="{ scope }">
          <el-input v-model="scope.row.menuName" />
        </template>
        <template #systemField="{ scope }">
          <el-input v-model="scope.row.systemField" />
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
      </EasyTable>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import { parseTime } from '@/utils'
import EasyTable from 'lib@/components/BaseTable/EasyTable'
import { contractManagement } from 'modb@/contractManagement/api/index'

export default {
  name: 'ContractTypeElements',
  components: {
    EasyTable
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      name: 'conditionFactors',
      tableName: 'conditionFactor',
      queryParams: {},
      methods: {
        listPage: async params => {
          const res = await contractManagement.condFactor.listPage(params)
          return res
        }
      },
      columns: [
        {
          attrs: {
            label: () => this.$t('contractMod.condFactor'),
            prop: 'condFactor'
          },
          slot: 'condFactor',
          search: {},
          rules: { required: true, message: this.$t('dataConfMod.required') }
        },
        {
          attrs: {
            label: () => this.$t('contractMod.menuName'),
            prop: 'menuName'
          },
          slot: 'menuName',
          search: {},
          rules: { required: true, message: this.$t('dataConfMod.required') }
        },
        {
          attrs: {
            label: () => this.$t('contractMod.systemField'),
            prop: 'systemField'
          },
          slot: 'systemField',
          search: {},
          rules: { required: true, message: this.$t('dataConfMod.required') }
        },
        {
          attrs: {
            label: () => this.$t('contractMod.startDate'),
            prop: 'startDate',
            sortable: true
          },
          slot: 'startDate'
        },
        {
          attrs: {
            label: () => this.$t('contractMod.endDate'),
            prop: 'endDate',
            sortable: true
          },
          slot: 'endDate'
        },
        {
          attrs: {
            label: () => this.$t('contractMod.createdBy'),
            prop: 'createdUserName'
          }
        },
        {
          attrs: {
            label: () => this.$t('contractMod.creationDate'),
            prop: 'creationDate',
            formatter: val => this.$parseTime(val),
            sortable: true
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
              func: this.deleteItem
            }
          ]
        }
      ]
    }
  },
  methods: {
    deleteItem (scope, data) {
      data.splice(scope.$index, 1)
    },
    add () {
      this.$refs.table.add({
        editable: true,
        startDate: parseTime(new Date(), '{y}-{m}-{d}', true)
      })
    },
    save () {
      const list = this.$refs.table.getUpdatedRows()
      this.$refs.table.validate(f => {
        if (f) {
          contractManagement.condFactor.save(list).then(res => {
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
<style scoped lang="scss"></style>
