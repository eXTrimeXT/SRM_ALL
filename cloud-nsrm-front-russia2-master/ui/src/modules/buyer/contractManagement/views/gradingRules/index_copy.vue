<template>
  <el-container
    class="flex-container contract_element_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        form-label-width="120px"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <!-- 新增 -->
        <template slot="left">
          <AuthorityButton
            type="primary"
            code="cm:gradingRules:add"
            @click="add"
          >
            {{
              $t('common.add')
            }}
          </AuthorityButton>
          <MImport
            ref="import"
            type="default"
            :title="iModal.title"
            :up-load-url="iModal.upLoadUrl"
            :extra-data="extraData"
            @downloadTemplate="downloadTemplate"
            @handleSuccess="handleSuccess"
          />
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :row-index-fixed="false"
        :page-size="pageSize"
        :checkbox="false"
        :pre-query-data="queryParam"
        url="/api-cm/contract/level-maintain/listPage"
        :open-custom-table="true"
      >
        <template #categoryName="props">
          <QuickSearch
            show-key="categoryName"
            :show-input="props.scope.row.categoryName"
            name="scc_base_purchase_category2"
            :scope-data="props.scope"
            @close-quicksearch="getMaterial"
          />
        </template>
        <template #operational="{ scope }">
          <DictSelect
            v-model="scope.row.operational"
            code="OPERATOR"
          />
        </template>
        <template #level="{ scope }">
          <DictSelect
            v-model="scope.row.level"
            code="CONTARCT_LEVEL"
          />
        </template>
      </TableView>
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import BaseTable from 'lib@/components/BaseTable'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import QuickSearch from 'lib@/components/QuickSearch'
import DictSelect from '@/library/components/c-select/dict-select'
import { contractManagement } from 'modb@/contractManagement/api/index'

export default {
  name: 'ContractElements',
  components: {
    TableView,
    MainHeader,
    BaseTable,
    FormWrapper,
    QuickSearch,
    DictSelect,
    MImport
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      extraData: {
        fileModular: 'cm',
        fileFunction: 'gradingRules',
        fileType: 'excel'
      },
      iModal: {
        title: this.$t('common.excelImport'), // Excel导入
        upLoadUrl: '/api-cm/contract/level-maintain/importExcel'
      },
      elemRangesTable: [],
      elemRangesDialogVisible: false,
      defaultTableHeader: [],
      name: 'contractElement',
      tableName: 'contractElement',
      pageSize: 15,
      gridId: 'list',
      tableHeader: [],
      tableData: [],
      relations: [],
      relations1: [],
      relations2: [],
      queryObj: {},
      expLoading: false,
      queryForm: [],
      queryParam: {}
    }
  },
  updated () {
    this.defaultTableHeader = this.tableHeader
  },
  created () {
    this.queryForm = [
      {
        label: () => this.$t('contractMod.categoryName'),
        type: 'catSelect',
        showKey: 'struct',
        prop: 'struct'
      },
      {
        label: () => this.$t('contractMod.level'),
        type: 'dict',
        code: 'CONTARCT_LEVEL',
        prop: 'level'
      },
      {
        label: () => this.$t('contractMod.isValid'),
        type: 'dict',
        code: 'YES_OR_NO',
        prop: 'isValid'
      }
    ]
    this.tableHeader = [
      {
        label: () => this.$t('contractMod.categoryName'),
        prop: 'categoryName',
        showType: 'slot',
        slot: 'categoryName',
        show: (row) => { try { return row.editable } catch (error) {} },
        minWidth: 150
      },
      {
        label: () => this.$t('contractMod.categoryFullName'),
        prop: 'categoryFullName',
        minWidth: 150
      },
      {
        label: () => this.$t('contractMod.amount'),
        prop: 'amount',
        showType: 'input',
        editable: (row) => { try { return row.editable } catch (error) {} },
        minWidth: 180
      },
      {
        label: () => this.$t('contractMod.operational'),
        prop: 'operational',
        showType: 'slot',
        slot: 'operational',
        show: (row) => { try { return row.editable } catch (error) {} },
        dataType: 'dict',
        code: 'OPERATOR',
        minWidth: 150
      },
      {
        label: () => this.$t('contractMod.level'),
        prop: 'level',
        showType: 'slot',
        slot: 'level',
        show: (row) => { try { return row.editable } catch (error) {} },
        formattor: (val) => this.$getDictLabel('CONTARCT_LEVEL', val),
        minWidth: 150
      },
      {
        label: () => this.$t('contractMod.startDate'),
        prop: 'startData',
        minWidth: 150,
        showType: 'date',
        formatter: (val) => (this.$parseTime(val) || null),
        editable: (row) => { try { return row.editable } catch (error) {} }
      },
      {
        label: () => this.$t('contractMod.endDate'),
        prop: 'endData',
        minWidth: 150,
        showType: 'date',
        formatter: (val) => (this.$parseTime(val) || null),
        editable: (row) => { try { return row.editable } catch (error) {} }
      },
      {
        label: () => this.$t('contractMod.lastUpdatedBy'),
        prop: 'lastUpdatedUserName', // lastUpdatedBy
        minWidth: 130
      },
      {
        label: () => this.$t('contractMod.lastUpdateDate'),
        prop: 'lastUpdateDate',
        minWidth: 150,
        dataType: 'dateTime'
      },
      {
        prop: 'operation',
        label: () => this.$t('common.operation'),
        showType: 'buttons',
        btnStyle: 'text',
        fixed: 'right',
        minWidth: 130,
        buttons: [
          {
            callback: (row, socpe) => this.edit(row, socpe),
            show: (row) => { try { return !row.editable } catch (error) {} },
            code: 'cm:gradingRules:edit',
            formattor: () => {
              return this.$t('common.edit')
            }
          },
          {
            callback: (row, socpe) => this.save(row, socpe),
            show: (row) => row.editable,
            code: 'cm:gradingRules:save',
            formattor: () => {
              return this.$t('common.save')
            }
          },
          {
            callback: (row, socpe) => this.delete(row, socpe),
            code: 'cm:gradingRules:delete',
            show: (row) => {
              if (row.isNew) {
                return false
              }
              return true
            },
            formattor: () => {
              return this.$t('common.delete')
            }
          },
          {
            callback: (row, socpe) => this.cancelEdit(row, socpe),
            code: 'cm:gradingRules:cancelEdit',
            show: (row) => { try { return row.editable } catch (error) {} },
            formattor: () => {
              return this.$t('common.cancel')
            }
          }
        ]
      }
    ]
  },
  mounted () {
    this.getQuerydata()
  },
  methods: {
    syncFilterParams (values) {
      this.queryObj = values
    },
    getMaterial (value, scope) {
      console.log(value, 33333)
      scope.row.categoryCode = value.categoryCode || ''
      scope.row.categoryId = value.categoryId || ''
      scope.row.categoryName = value.categoryName || ''
      scope.row.struct = value.struct || ''
      scope.row.categoryFullName = value.categoryFullName || ''
      this.$refs[this.gridId].setTableData((tableData) => {
        this.$set(tableData, scope.$index, scope.row)
      })
    },
    handleSuccess () {
      this.getQuerydata()
    },
    downloadTemplate () {
      downloadFileLink(
        '/api-cm/contract/level-maintain/importModelDownload',
        this.$t('contractMod.contractGradingRuleImp')
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    },
    add () {
      this.$refs[this.gridId].addOneEditableColumn()
    },
    edit (row, scope) {
      // scope.row.editable = true;
      this.$set(scope.row, 'editable', true)
    },
    save (row, scope) {
      if (row.isNew) {
        if (!row.startData) row.startData = this.$dayjs().format('YYYY-MM-DD')
        contractManagement.gradingRules.add(row).then((res) => {
          this.$message.success(res.message)
          this.getQuerydata()
        })
      } else {
        contractManagement.gradingRules.modify(row).then((res) => {
          this.$message.success(res.message)
          this.getQuerydata()
        })
      }
    },
    delete (row, scope) {
      contractManagement.gradingRules.delete(row.levelMaintainId).then((res) => {
        this.$message.success(res.message)
        this.getQuerydata()
      })
    },
    cancelEdit (row, scope) {
      scope.row.editable = false
      this.getQuerydata()
    },
    getQuerydata (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    }
  }
}
</script>
<style scoped lang="scss">
.contract_element_wrapper {
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
.contract_element_wrapper .el-table th > .cell {
  display: flex;
  justify-content: center;
}
</style>
