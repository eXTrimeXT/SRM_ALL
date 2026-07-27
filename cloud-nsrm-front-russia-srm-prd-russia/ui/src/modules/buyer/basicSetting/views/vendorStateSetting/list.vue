<template>
  <el-container
    class="flex-container companystate_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        @getFormData="getQuerydata"
      />
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            type="primary"
            @click="addHandle"
          >
            {{ $t('common.add') }}
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :table-data="tableData"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :transform-data="transformData"
        :com-active="$attrs['changeTab']"
        :source="vendorStateSetting.list"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { vendorStateSetting } from 'modb@/basicSetting/api/basicSetting'

export default {
  name: 'CompanystateList',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      vendorStateSetting: vendorStateSetting,
      tableData: [],
      name: 'companystateList',
      tableName: 'companystateTable',
      pageSize: 15,
      gridId: 'list',
      currentRows: [],
      tableHeader: [],
      queryForm: [
        {
          label: this.$t('dataConfMod.categoryStatus'),
          prop: 'categoryStateCode',
          type: 'dict',
          code: 'CATEGORY_STATE_CODE'
        },
        {
          label: () => this.$t('common.creationTime'),
          type: 'daterange',
          prop: 'queryCreationDate'
        },
        {
          label: () => this.$t('dataConfMod.enabled'),
          prop: 'enableFlag',
          code: 'YES_OR_NO',
          type: 'dict'
        }
      ],
      queryParam: {}
    }
  },
  created () {
    this.defaultTableHeader = this.tableHeader
    let _this = this
    this.tableHeader = [
      {
        prop: 'categoryStateCode',
        label: this.$t('dataConfMod.categoryStatus'),
        showType: 'dictSelect',
        code: 'CATEGORY_STATE_CODE',
        formattor: val => _this.$getDictLabel('CATEGORY_STATE_CODE', val),
        editable: row => row.editable,
        width: 120
      },
      {
        prop: 'creationDate',
        label: this.$t('dataConfMod.creationDate'),
        width: 130
      },
      {
        prop: 'startTime',
        label: this.$t('common.effectTime'),
        showType: 'date',
        formatter: val => val || null,
        editable: row => row.editable,
        width: 130
      },
      {
        prop: 'endTime',
        label: this.$t('dataConfMod.endDateTime'),
        showType: 'date',
        formatter: val => val || null,
        editable: row => row.editable,
        width: 130
      },
      {
        prop: 'ifAllowInquiry',
        label: this.$t('vendorMod.ifAllowInquiry'),
        showType: 'switch',
        switchValues: {
          inactive: 'N',
          active: 'Y'
        },
        formattor: val => {
          if (val == 'Y') {
            return this.$t('common.yes')
          } else {
            return this.$t('common.no')
          }
        },
        editable: row => row.editable,
        width: 120
      },
      {
        prop: 'ifAllowBid',
        label: this.$t('vendorMod.ifAllowBid'),
        showType: 'switch',
        switchValues: {
          inactive: 'N',
          active: 'Y'
        },
        formattor: val => {
          if (val == 'Y') {
            return this.$t('common.yes')
          } else {
            return this.$t('common.no')
          }
        },
        editable: row => row.editable,
        width: 120
      },
      {
        prop: 'ifAllowOrder',
        label: this.$t('vendorMod.ifAllowOrder'),
        showType: 'switch',
        switchValues: {
          inactive: 'N',
          active: 'Y'
        },
        formattor: val => {
          if (val == 'Y') {
            return this.$t('common.yes')
          } else {
            return this.$t('common.no')
          }
        },
        editable: row => row.editable,
        width: 120
      },
      {
        prop: 'ifAllowWarehousing',
        label: this.$t('vendorMod.ifAllowWarehousing'),
        showType: 'switch',
        switchValues: {
          inactive: 'N',
          active: 'Y'
        },
        formattor: val => {
          if (val == 'Y') {
            return this.$t('common.yes')
          } else {
            return this.$t('common.no')
          }
        },
        editable: row => row.editable,
        width: 120
      },
      {
        prop: 'ifAllowStatement',
        label: this.$t('vendorMod.ifAllowStatement'),
        showType: 'switch',
        switchValues: {
          inactive: 'N',
          active: 'Y'
        },
        formattor: val => {
          if (val == 'Y') {
            return this.$t('common.yes')
          } else {
            return this.$t('common.no')
          }
        },
        editable: row => row.editable,
        width: 120
      },
      {
        prop: 'ifAllowPay',
        label: this.$t('vendorMod.ifAllowPay'),
        showType: 'switch',
        switchValues: {
          inactive: 'N',
          active: 'Y'
        },
        formattor: val => {
          if (val == 'Y') {
            return this.$t('common.yes')
          } else {
            return this.$t('common.no')
          }
        },
        editable: row => row.editable,
        width: 120
      },
      {
        prop: 'transitDay',
        label: this.$t('dataConfMod.transitDay'),
        showType: 'input',
        editable: row => row.editable,
        width: 150
      },
      {
        prop: 'orderCount',
        label: this.$t('dataConfMod.transitTime'),
        showType: 'input',
        editable: row => row.editable,
        width: 160
      },
      {
        prop: 'amountLimitPerOrder',
        label: this.$t('dataConfMod.amountLimitPerOrder'),
        showType: 'input',
        editable: row => row.editable,
        width: 180
      },
      {
        prop: 'remark',
        label: this.$t('dataConfMod.worksRemarks'),
        showType: 'input',
        editable: row => row.editable,
        width: 100
      },
      {
        prop: 'operation',
        label: this.$t('common.operation'),
        showType: 'buttons',
        btnStyle: 'text',
        fixed: 'right',
        width: 130,
        buttons: [
          {
            callback: (row, socpe) => this.editHandle(row, socpe),
            show: row => !row.editable,
            formattor: () => {
              return this.$t('common.edit')
            }
          },
          {
            callback: row => this.submit(row),
            show: row => row.editable,
            formattor: () => {
              return this.$t('common.submit')
            }
          },
          {
            callback: (row, socpe) => this.deleteHandle(row, socpe),
            show: row => row.editable,
            formattor: () => {
              return this.$t('common.cancel')
            }
          }
        ]
      }
    ]
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    transformData (data) {
      const list = data.data.list
      data.data.list = list.map(item => ({ ...item, editable: false }))
      return data
    },
    // 点击新增或者编辑后调用
    submit (row) {
      const datas = [row]
      vendorStateSetting.saveOrUpdate(datas).then(res => {
        this.$message.success(res.message)
        this.getQuerydata()
      })
    },
    getQuerydata (params) {
      this.queryParam = params
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    deleteHandle (row, scope) {
      const mode = this.mode
      // 新增条件下的删除
      if (mode == 'add') {
        this.$refs[this.gridId].deleteRow(scope.$index)
      } else {
        scope.row.editable = false
      }
    },
    addHandle (row) {
      this.mode = 'add'
      this.$refs[this.gridId].addOneEditableColumn()
    },
    editHandle (row, scope) {
      this.mode = 'edit'
      scope.row.editable = true
    },
    handleCurrentChange (val) {
      this.currentRows = val
    }
  }
}
</script>
