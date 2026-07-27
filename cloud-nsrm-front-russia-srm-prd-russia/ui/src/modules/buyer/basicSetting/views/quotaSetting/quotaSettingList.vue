<template>
  <el-container
    class="flex-container the_dictionary_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :form-array="preArr"
        @getFormData="getQuerydata"
      />
      <main-header>
        <template slot="left">
          <el-button
            type="primary"
            @click="editTab('add')"
          >
            {{ $t("common.add") }}
          </el-button>
        </template>
      </main-header>
      <table-view
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :com-active="$attrs['changeTab']"
        url="/api-base/quotaorder/listPage"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import quotaSettingDetail from './quotaSettingDetail'
import { getDictItem } from '@/api/common'
import { adaptDictData } from '@/utils'

export default {
  name: 'QuotaSettingList',
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
      tableName: 'quotaSettingList',
      defaultTableHeader: [],
      statusList: [],
      warningBrandList: [],
      pageSize: 15,
      gridId: 'quotaSettingList',
      selectList: [],
      currentRow: null,
      tableHeader: [],
      tableData: [],
      preArr: [],
      queryParam: {}
    }
  },
  created () {
    let _this = this
    _this.fatchDictData() // 字典
    _this.preArr = [
      {
        prop: 'organizationId',
        label: () => this.$t('dataConfMod.organizationId'),
        type: 'INVorganizationSelector'
      },
      {
        prop: 'materialCode',
        label: () => this.$t('common.materialCode'), // '物料编码'
        type: 'quicksearch',
        showKey: 'materialCode',
        name: 'scc_base_material_item'
      },
      {
        prop: 'categoryName',
        label: () => this.$t('dataConfMod.category'), // '品类'
        type: 'catSelect',
        showKey: 'categoryName'
      },
      {
        prop: 'vendorId',
        label: () => this.$t('common.vendorName'), // '供应商名称'
        type: 'quicksearch',
        showKey: 'companyName',
        name: 'scc_sup_company_info_display_buyer'
      },
      {
        prop: 'createdUserName', // createdBy
        label: () => this.$t('dataConfMod.createdBy') // '创建人'
      },
      {
        prop: 'dateList',
        label: () => this.$t('dataConfMod.creationDate'),
        type: 'daterange'
      }
    ]
    this.tableHeader = [
      {
        prop: 'organizationName',
        label: () => this.$t('dataConfMod.organizationId'),
        minWidth: 180
      },
      {
        prop: 'materialCode',
        label: () => this.$t('common.materialCode'),
        minWidth: 150
      },
      {
        prop: 'materialName',
        label: () => this.$t('common.materialName'),
        minWidth: 180
      },
      {
        prop: 'categoryName',
        label: () => this.$t('dataConfMod.category'),
        minWidth: 150
      },
      {
        prop: 'unitName',
        label: () => this.$t('dataConfMod.unit'),
        minWidth: 100
      },
      {
        prop: 'startDate',
        label: () => this.$t('dataConfMod.startDate'),
        minWidth: 150
      },
      {
        prop: 'endDate',
        label: () => this.$t('dataConfMod.endDate'),
        minWidth: 150
      },
      {
        prop: 'companyName',
        label: () => this.$t('common.vendorName'),
        minWidth: 180
      },
      {
        prop: 'allocatedAmount',
        label: () => this.$t('dataConfMod.allocatedAmount'),
        minWidth: 100
      },
      {
        prop: 'quota',
        label: () => this.$t('dataConfMod.quota'),
        minWidth: 100
      },
      {
        prop: 'createdUserName', // createdBy
        label: () => this.$t('dataConfMod.createdBy'),
        minWidth: 140
      },
      {
        prop: 'creationDate',
        label: () => this.$t('dataConfMod.creationDate'),
        minWidth: 150
      },
      {
        prop: 'operation',
        label: _this.$t('bidMod.operation'),
        width: 120,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            callback: function (row) {
              this.editTab('edit', row)
            }.bind(this),
            formattor () {
              return _this.$t('common.edit') // '编辑'
            }
          },
          {
            callback: function (row) {
              this.deleteOne(row)
            }.bind(this),
            formattor () {
              return _this.$t('common.delete') // '删除'
            }
          }
        ]
      }
    ]
    this.defaultTableHeader = this.tableHeader // 自定义表格表头
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    // 获取数据字典
    fatchDictData () {
      // 状态---PRICE_CALCULATE_STATUS
      getDictItem('PRICE_CALCULATE_STATUS').then(res => {
        this.statusList = adaptDictData(res.data, 'dict')
        this.preArr[3].options = this.statusList
      })
      // 警告牌类型---MAINTAIN_WARN_BRAND_TYPE
      getDictItem('MAINTAIN_WARN_BRAND_TYPE').then(res => {
        this.warningBrandList = adaptDictData(res.data, 'dict')
      })
    },
    getQuerydata (v) {
      if (v && v.dateList) {
        v.startDate = v.dateList[0]
        v.endDate = v.dateList[1]
      } else if (v && !v.dateList) {
        delete v.startDate
        delete v.endDate
      }
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    // 编辑tab
    editTab (type, row) {
      let tab = {}
      if (type === 'add') {
        // 新增
        tab = {
          component: quotaSettingDetail,
          ctrlHeight: true,
          params: {
            flag: 'add',
            tabName: 'quotaSettingDetail'
          },
          title: () => this.$t('dataConfMod.quotaSettingDetail'), // '配额详情',
          name: 'quotaSettingDetail'
        }
      } else if (type === 'view') {
        // 查看
        tab = {
          component: quotaSettingDetail,
          ctrlHeight: true,
          params: {
            flag: 'view',
            row: row,
            tabName: 'quotaSettingDetail' + row.calculateNum
          },
          title: () => this.$t('dataConfMod.quotaSettingDetail'), // '配额详情',
          name: 'quotaSettingDetail' + row.calculateNum
        }
      } else {
        // 修改
        tab = {
          component: quotaSettingDetail,
          ctrlHeight: true,
          params: {
            flag: 'edit',
            row: row,
            tabName: 'quotaSettingDetail' + row.calculateNum
          },
          title: () => this.$t('dataConfMod.quotaSettingDetail'), // '配额详情',
          name: 'quotaSettingDetail' + row.calculateNum
        }
      }
      this.$emit('tab-add', tab)
    },
    // 删除数据
    deleteOne (row) {
      // '当前操将永久删除这条数据，确认删除这条数据？'
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url: '/api-base/quotaorder/delete',
            method: 'get',
            params: { quotaHeadId: row.quotaHeadId },
            loading: true
          })
            .then(res => {
              this.$message({
                message: res.message,
                type: 'success'
              })
              this.getQuerydata()
            })
        })
    },
    handleCurrentChange (val) {
      this.currentRow = val
    }
  }
}
</script>
