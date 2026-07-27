<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <FormWrapper
        :form-array="preArr"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />

      <MainHeader v-if="userType === 'BUYER'" :l-span="22" :r-span="2">
        <template slot="left">
          <el-button type="primary" @click="addOne">
            {{ $t('common.add') }}
          </el-button>
        </template>
      </MainHeader>

      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-pef/itemExceptionHandle/listPage"
        @getFooter="getFooter"
        @getFooterSize="getFooterSize"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import incomingExceptionDetail from './incomingExceptionDetail'
import ExportExcel from 'lib@/components/export-excel'
import { excHandlingNotice } from 'mods@/qualitySynergySupplier/api'

export default {
  name: 'IncomingExceptionList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    incomingExceptionDetail,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      curRole: this.$store.getters.userType,
      name: 'incomingExceptionTable',
      tableName: 'incomingExceptionList',
      reviewFormNumber: '',
      gridData: [],
      pageSize: 15,
      gridId: 'list',
      currentRow: null,
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      getFooterNum: null,
      getFooterSizeNum: null,
      isModify: false,
      globalNickname: null,
      preList: [
        // 异常单号
        {
          prop: 'itemExceptionHeadId',
          label: this.$t('qualitySynergy.exceptionOrderId')
        },
        // 需要8D报告
        {
          prop: 'report8D',
          label: this.$t('qualitySynergy.need8DReport'),
          type: 'select',
          options: [
            { label: this.$t('common.yes'), value: this.$t('common.yes') },
            { label: this.$t('common.no'), value: this.$t('common.no') }
          ]
        },
        {
          // 物料编码
          prop: 'materialCode',
          label: this.$t('common.materialCode'),
          type: 'quicksearch',
          showKey: 'materialCode',
          propKey: 'materialCode',
          name: 'scc_base_material_item'
        },
        // 发布时间
        {
          prop: 'creationDate',
          label: this.$t('qualitySynergy.releaseDate'),
          type: 'date'
        }
      ],
      queryParam: {},
      statusList: [],
      purchaseTypeList: []
    }
  },
  computed: {
    userType () {
      return this.$store.getters.userType
    },
    preArr () {
      if (this.userType === 'BUYER') {
        return [
          ...this.preList,
          // 供应商名称
          {
            prop: 'vendorName',
            label: this.$t('common.vendor'),
            type: 'quicksearch',
            showKey: 'companyName',
            name: 'scc_sup_company_info_display_buyer'
          },

          {
            // 单据状态
            prop: 'itemExHandleStatus',
            label: this.$t('qualitySynergy.paymentPlanStatus'),
            width: 180,
            type: 'dict',
            code: 'PERF_ITEM_EX_HANDLER_STATUS'
          }
        ]
      } else {
        return this.preList
      }
    }
  },
  watch: {
    $route: {
      // 当前功能已经打开的时候监听是否是从其他功能跳转过来的
      deep: true,
      immediate: true,
      handler () {
        if (
          this.$route.params.from === 'fromFun' &&
          this.$route.params.funName === 'incomingException'
        ) {
          let itemExceptionHeadId = Number(this.$route.params.formId)
          let formNo = this.$route.params.formNo // 流程标题
          let row = {
            ...this.$route.params,
            itemExceptionHeadId,
            requirementHeadNum: formNo // tab 标题显示
          }
          this.readOne(row)
        }
      }
    }
  },
  created () {
    this.globalNickname = this.$store.getters.userInfo
      ? this.$store.getters.userInfo.username
      : null
    let _this = this
    let tableHeader = [
      {
        prop: 'itemExceptionHeadId',
        label: this.$t('qualitySynergy.exceptionOrderId'), // 异常单号
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.readOne(row)
        }.bind(this),
        formattor (val) {
          return val || '--'
        }
      },
      {
        prop: 'orgName',
        label: this.$t('bid_mod.businessEntity'), // 业务实体
        minWidth: 130
      },
      {
        prop: 'organizationName',
        label: this.$t('qualitySynergy.organizationName'), // 库存组织
        minWidth: 130
      },
      {
        prop: 'itemExHandleStatus',
        label: this.$t('qualitySynergy.paymentPlanStatus'),
        minWidth: 150,
        dataType: 'dict', // 数据类型为字典
        code: 'PERF_ITEM_EX_HANDLER_STATUS' // 字典code
      },
       {
        prop: 'vendorName',
        label: this.$t('qualitySynergy.vendor'), // 供应商
        width: 120
      },
      {
        prop: 'materialCode',
        label: this.$t('common.materialCode'), // 物料编码
        width: 120
      },
      {
        prop: 'materialName',
        label: this.$t('common.materialName'), // 物料名称
        minWidth: 150
      },
      {
        prop: 'deliveryNumber',
        label: this.$t('qualitySynergy.deliveryNumber'), // 送货单号
        minWidth: 120
      },
      {
        prop: 'checkListType',
        label: this.$t('qualitySynergy.checkListType'), // 检验单类型
        width: 120
      },
      {
        prop: 'checkStandard',
        label: this.$t('qualitySynergy.checkStandard'), // 检验标准
        width: 120
      },
      {
        prop: 'itemAgent',
        label: this.$t('qualitySynergy.itemAgent'), // 材料责任人
        width: 120
      },
      {
        prop: 'epAgent',
        label: this.$t('qualitySynergy.epAgent'), // 环保责任人
        width: 120
      },
      {
        prop: 'reworkTotal',
        label: this.$t('qualitySynergy.reworkTotal'), // 返工数量
        width: 120
      },
      {
        prop: 'reworkConclusion',
        label: this.$t('qualitySynergy.reworkConclusion'), // 返工结论
        minWidth: 120,
        dataType: 'dict',
        code: 'INS_ITEM_REWORK_CONCLUSION'
      }
    ]
    if (this.userType === 'BUYER') {
      let obj1 = {
        prop: 'vendorName',
        label: this.$t('common.vendor'), // 供应商
        minWidth: 120
      }
      let obj2 = {
        // 操作

        prop: 'operation',
        label: _this.$t('common.operation'),
        width: 150,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            // 编辑
            callback: function (row) {
              this.editOne(row)
            }.bind(this),
            show: row => row.itemExHandleStatus !== 'PUBLISHED',
            formattor (val, row) {
              return _this.$t('common.edit')
            }
          },
          {
            // 删除
            callback: function (row) {
              this.deleteOne(row)
            }.bind(this),
            show: row => row.itemExHandleStatus !== 'PUBLISHED',
            formattor (val) {
              return _this.$t('common.delete')
            }
          }
        ]
      }

      tableHeader.splice(9, 0, obj1)
      tableHeader.push(obj2)
    }
    this.tableHeader = tableHeader
    // this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
    if (
      this.$route.params.from === 'demandPoolManagement' &&
      this.$route.params.funName === 'incomingException'
    ) {
      let row = this.$route.params.fdSubject
      this.$emit('tab-add', {
        component: incomingExceptionDetail,
        params: {
          flag: 'readOnly',
          row: row
        },
        title: row.itemExceptionHeadId,
        name: 'incomingExceptionDetail' + row.itemExceptionHeadId
      })
    }
  },
  methods: {
    getQuerydata (v) {
      this.queryParam = Object.assign({}, v)
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    syncFilterParams (values) {
      this.queryParam = values
    },
    handleCurrentChange (val) {
      this.currentRow = val
    },
    getFooter (data) {
      this.getFooterNum = data.value
    },
    getFooterSize (data) {
      this.getFooterSizeNum = data.value
    },
    exportList () {
      let params = {}
      if (this.getFooterNum && this.getFooterSizeNum) {
        params = Object.assign(
          {},
          this.queryParam,
          { pageNum: this.getFooterNum },
          { pageSize: this.getFooterSizeNum },
        )
      } else if (this.getFooterNum) {
        params = Object.assign(
          {},
          this.queryParam,
          { pageNum: this.getFooterNum },
          { pageSize: 15 },
        )
      } else if (this.getFooterSizeNum) {
        params = Object.assign(
          {},
          this.queryParam,
          { pageNum: 1 },
          { pageSize: this.getFooterSizeNum },
        )
      } else {
        params = Object.assign({}, this.queryParam, { pageNum: 1 }, { pageSize: 15 })
      }
    },
    addOne () {
      this.$emit('tab-add', {
        component: incomingExceptionDetail,
        params: {
          flag: 'add',
          tabName: 'incomingExceptionDetail'
        },
        title: this.$t('qualitySynergy.addIncomingException'), // 创建来料异常处理单
        name: 'incomingExceptionDetail'
      })
    },
    editOne (row) {
      this.$emit('tab-add', {
        component: incomingExceptionDetail,
        params: {
          flag: 'edit',
          row: row,
          tabName: 'incomingExceptionDetail_edit' + row.itemExceptionHeadId
        },
        title: this.$t('common.edit') + ' - ' + row.itemExceptionHeadId,
        name: 'incomingExceptionDetail_edit' + row.itemExceptionHeadId
      })
    },
    readOne (row) {
      this.$emit('tab-add', {
        component: incomingExceptionDetail,
        params: {
          flag: 'readOnly',
          row: row,
          tabName: 'incomingExceptionDetail_show' + row.itemExceptionHeadId
        },
        title: this.$t('common.view') + ' - ' + row.itemExceptionHeadId,
        name: 'incomingExceptionDetail_show' + row.itemExceptionHeadId
      })
    },
    enableOne () {},
    disableOne () {},
    deleteOne (row) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          excHandlingNotice.incomingExceptionDelete({ itemExceptionHeadId: row.itemExceptionHeadId }).then(data => {
              this.$message.success(this.$t('common.successDelete'))
              this.getQuerydata()
            })
            .catch(err => {
              console.log(err)
            })
        })
        .catch(() => {})
    }
  }
}
</script>
