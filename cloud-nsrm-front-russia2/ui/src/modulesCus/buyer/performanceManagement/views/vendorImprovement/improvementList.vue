<template>
  <el-container
    class="flex-container the_vendorEffect_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        :pre-form-obj="preFormObj"
        @synchronous-value="syncFilterParams"
        @getFormData="getQuerydata"
      />
      <MainHeader>
        <template
          v-if="curRole === 'BUYER'"
          slot="left"
        >
          <el-button
            type="primary"
            @click="editTab('add')"
          >
            {{ $t('common.add') }}
          </el-button>
          <el-button @click="notifyVendor">
            {{ $t('perfMod.notifySupplier') }}
          </el-button>
          <MImport
            ref="import"
            :title="iModal.title"
            :up-load-url="iModal.upLoadUrl"
            :extra-data="extraData"
            type="default"
            @downloadTemplate="downloadTemplate"
            @handleSuccess="handleSuccess"
          />
          <ExportExcel
            :filter-params="queryParam"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            export-mode="front"
            page-url="/api-pef/vendorImprove/listPage"
            type="default"
          />
        </template>
      </MainHeader>
      <!-- :current-change="handleCurrentChange" -->
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :check-change="checkChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :checkbox="true"
        :comActive="$attrs['changeTab']"
        url="/api-pef/vendorImprove/listPage"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import improvementDetail from './improvementDetail'
import ExportExcel from 'lib@/components/export-excel'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import { performanceManagement } from 'modb@/performanceManagement/api/index'

export default {
  name: 'ImprovementList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel,
    MImport
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      dictCodes: {
        status: 'VENDOR_IMPROVE_STATUS'
      },
      curRole: this.$store.getters.userType, // VENDOR BUYER curRole==='VENDOR'
      tableName: 'improvementList',
      approveStatusList: [], // 审批状态
      supplierControlType: [], // 控制类型
      pageSize: 15,
      gridId: 'improvementList',
      selectList: [],
      currentRow: null,
      currentRows: null,
      tableHeader: [],
      tableData: [],
      statusList: [],
      selectDictionary: {},
      preArr: [
        { prop: 'improveNo', label: () => this.$t('perfMod.improveNo') },
        {
          prop: 'categoryName',
          label: () => this.$t('perfMod.categoryName'),
          type: 'catSelect',
          showKey: 'categoryName'
        },
        {
          prop: 'vendorName',
          label: () => this.$t('perfMod.vendorName'),
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info_display_buyer'
        },
        {
          prop: 'status',
          label: () => this.$t('perfMod.improveStatus'),
          type: 'dict', // 字典类型
          code: 'VENDOR_IMPROVE_STATUS' // 字典code
        },
        { prop: 'improveTitle', label: () => this.$t('perfMod.improveTitle') },
        {
          prop: 'organizationId',
          label: () => this.$t('perfMod.fullPathId'),
          type: 'OUorganizationSelector',
          placeholder: this.$t('perfMod.selectOrganization')
        }
      ],
      preArrSell: [
        { prop: 'improveNo', label: () => this.$t('perfMod.improveNo') },
        {
          prop: 'categoryName',
          label: () => this.$t('perfMod.categoryName'),
          type: 'catSelect',
          showKey: 'categoryName'
        },
        // { prop: 'vendorName',
        //   label: ()=>this.$t('perfMod.vendorName'),
        //   type: 'quicksearch',
        //   showKey: 'companyName',
        //   name: 'scc_sup_company_info_display_buyer'
        // },
        {
          prop: 'status',
          label: () => this.$t('perfMod.improveStatus'),
          type: 'dict', // 字典类型
          code: 'VENDOR_IMPROVE_STATUS' // 字典code
        },
        { prop: 'improveTitle', label: () => this.$t('perfMod.improveTitle') },
        {
          prop: 'organizationId',
          label: () => this.$t('perfMod.fullPathId'),
          type: 'OUorganizationSelector',
          placeholder: this.$t('perfMod.selectOrganization')
        }
      ],
      queryParam: {},
      filterParams: {},
      extraData: {
        fileModular: 'perf',
        fileFunction: 'vendorImprovement',
        fileType: 'excel'
      },
      iModal: {
        title: this.$t('components.eio.importTitle'),
        upLoadUrl: '/api-pef/vendorImprove/importExcel'
      },
      preFormObj: {}
    }
  },
  watch: {
    $route: {
      // 当前功能已经打开的时候监听是否是从其他功能跳转过来的
      deep: true,
      immediate: true,
      handler () {
        if (Object.keys(this.$route.params).length > 0) {
          if (
            this.$route.params.from === 'workCount' &&
            this.$route.params.funName === 'vendorImprovement'
          ) {
            // 供应商 工作台跳转
            this.queryParam.status = this.$route.params.status
            // this.firstLoad = false;
            this.preFormObj = Object.assign({}, { status: this.$route.params.status })
          } else {
            let { from, row } = this.$route.params
            if (from === 'portrait') {
              this.editTab('view', row)
            }
          }
        }
      }
    }
  },
  created () {
    if (this.curRole === 'VENDOR') {
      this.preArr = this.preArrSell
    }
    // this.fatchDictData() // 字典
    let _this = this
    this.tableHeader = [
      {
        prop: 'improveNo',
        label: () => this.$t('perfMod.improveNo'),
        width: '130'
      },
      {
        prop: 'status',
        label: () => this.$t('perfMod.improveStatus'),
        dataType: 'dict', // 数据类型为字典
        code: 'VENDOR_IMPROVE_STATUS', // 字典code
        width: '120'
      },
      {
        prop: 'vendorName',
        label: () => this.$t('perfMod.vendorName'),
        minWidth: '150'
      },
      {
        prop: 'organizationName',
        label: () => this.$t('perfMod.fullPathId'),
        width: '150'
      },
      {
        prop: 'categoryName',
        label: () => this.$t('perfMod.categoryName'),
        width: '150'
      },
      {
        prop: 'improveTitle',
        label: () => this.$t('perfMod.improveTitle'),
        width: '150'
      },
      {
        prop: 'improveProject',
        label: () => this.$t('perfMod.improveProject'),
        width: '150'
      },
      {
        prop: 'respFullName',
        label: () => this.$t('perfMod.respFullName2'),
        width: '110'
      },
      {
        prop: 'improveDateStart',
        label: () => this.$t('perfMod.improveDateStart'),
        width: '130',
        dataType: 'dateTime'
      },
      {
        prop: 'improveDateEnd',
        label: () => this.$t('perfMod.improveDateEnd'),
        width: '130',
        dataType: 'dateTime'
      },
      {
        prop: 'createdUserName',
        label: () => this.$t('perfMod.createdBy'),
        width: '100'
      },
      {
        prop: 'creationDate',
        label: () => this.$t('perfMod.creationDate'),
        width: '140',
        dataType: 'dateTime'
      },
      {
        prop: 'operation',
        label: () => this.$t('common.operation'),
        width: 120,
        btnStyle: 'text',
        fixed: 'right',
        showType: 'buttons',
        buttons: [
          {
            callback: function (row) {
              this.editTab('edit', row)
            }.bind(this),
            formattor (val) {
              return _this.$t('perfMod.compile')
            },
            show: function (row) {
              // 拟定和撤回的单据可以编辑
              if (_this.curRole === 'BUYER' && row.status === 'DRAFT') {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              this.delRowData(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('perfMod.delete')
            },
            show: function (row) {
              if (_this.curRole === 'BUYER' && row.status === 'DRAFT') {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              this.editTab('process', row)
            }.bind(this),
            formattor (val) {
              return _this.$t('perfMod.evaluate')
            },
            show: function (row) {
              if (
                _this.curRole === 'BUYER' &&
                row.status === 'UNDER_EVALUATION' &&
                row.mIsFeedback === 'N'
              ) {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              this.editTab('feedback', row)
            }.bind(this),
            formattor (val) {
              return _this.$t('perfMod.retroaction')
            },
            show: function (row) {
              if (
                _this.curRole === 'VENDOR' &&
                row.status === 'IMPROVING' &&
                row.vIsFeedback === 'N'
              ) {
                return true
              } else {
                return false
              }
            }
          },
          {
            // 查看
            callback: function (row) {
              this.editTab('view', row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.view')
            },
            show: function (row) {
              if (row.status !== 'DRAFT') {
                return true
              } else {
                return false
              }
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
    downloadTemplate () {
      downloadFileLink(
        '/api-pef/vendorImprove/importModelDownload',
        `${this.$t('perfMod.supplierImprovementTemplate')}.xls`
      ).catch(() => {
        this.$message.error(this.$t('perfMod.downLoadError'))
      })
    },
    handleSuccess () {
      this.getQuerydata()
    },
    getQuerydata (v) {
      let query = v || this.preFormObj
      this.queryParam = query
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    syncFilterParams (values) {
      this.filterParams = values
    },
    // 编辑tab //edit view process feedback  编辑 | 查看 | 处理 | 反馈
    editTab (type, row) {
      let tab = {}
      if (type === 'add') {
        // 新增
        tab = {
          component: improvementDetail,
          ctrlHeight: true,
          params: {
            flag: 'add',
            tabName: 'improvementDetail'
          },
          title: this.$t('perfMod.newSupplierImprovement'),
          name: 'improvementDetail'
        }
      } else {
        // 修改
        tab = {
          component: improvementDetail,
          ctrlHeight: true,
          params: {
            flag: type,
            orderId: row.vendorImproveId,
            tabName: 'improvementDetail' + row.vendorImproveId
          },
          title: row.improveNo,
          name: 'improvementDetail' + row.vendorImproveId
        }
      }
      this.$emit('tab-add', tab)
    },
    // 删除数据
    // 通知供应商
    notifyVendor () {
      if (!this.currentRows || this.currentRows.length === 0) {
        this.$message.error(this.$t('common.pleaseSelectOne'))
        return
      }

      // 拟定可通知供应商
      let draftRows = this.currentRows.filter(item => item.status === 'DRAFT')
      if (draftRows.length === 0) {
        this.$message.warning(this.$t('perfMod.pleaseSelectTheDocumentWhoseImprovementStatusIsProposed'))
        return
      }

      performanceManagement.vendorImproveNotifySupplier(draftRows).then(() => {
        this.$message.success(this.$t('perfMod.notifiedStatusProposed'))
        this.getQuerydata()
      })
    },
    // 删除数据
    delRowData (row) {
      let vendorImproveId = row.vendorImproveId
      this.$confirm(this.$t('perfMod.sureDeleteData'), {
        confirmButtonText: this.$t('common.affirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          performanceManagement.vendorImproveDel({ vendorImproveId }).then(res => {
            this.$message({
              message: res.message,
              type: 'success'
            })
            this.getQuerydata()
          })
        })
        .catch(() => {})
    },
    // 导入
    importHandel () {},
    handleCurrentChange (val) {
      this.currentRow = val
    },
    // 选中
    checkChange (val) {
      this.currentRows = val
    }
  }
}
</script>
<style scoped lang="scss"></style>
