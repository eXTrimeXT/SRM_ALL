<template>
  <el-container
    class="flex-container the_siteAssessment_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />
      <MainHeader>
        <template slot="left">
          <AuthorityButton
            code="sup:crossOrgImportList:add"
            type="primary"
            @click="editTab('add')"
          >
            {{ $t('common.add') }}
          </AuthorityButton>
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
        url="/api-sup/vendorImport/listPageByParam"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import crossOrgImportDetail from './crossOrgImportDetail'
import { adaptDictData, parseTime } from '@/utils'
import { crossOrgImport } from 'modb@/vendorManagementBuyer/api/supApi'

export default {
  name: 'CrossOrgImportList',
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
      tableName: 'crossOrgImportList',
      defaultTableHeader: [],
      reviewResultList: [],
      approveStatusList: [],
      quaReviewTypeList: [],
      siteTypeList: [],
      yesOrNoList: [],
      pageSize: 15,
      gridId: 'crossOrgImportList',
      selectList: [],
      currentRow: null,
      tableHeader: [],
      tableData: [],
      statusList: [],
      preArr: [
        {
          prop: 'importNum',
          label: () => this.$t('vendorMod.importNum') // 供应商引入单号
        },
        {
          prop: 'vendorName',
          label: () => this.$t('common.vendorName'), // '供应商名称'
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info_all'
        },
        {
          prop: 'importStatus',
          label: () => this.$t('vendorMod.orderStatus'), // '状态'
          type: 'dict', // 字典类型
          code: 'VENDORIMPORTSTATUS' // 字典code
        }
      ],
      queryParam: {}
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
          this.$route.params.funName === 'crossOrgImport'
        ) {
          let importId = Number(this.$route.params.formId)
          let importNum = this.$route.params.formNo // 流程标题
          let row = {
            ...this.$route.params,
            importId,
            importNum: importNum // tab 标题显示
          }
          this.editTab('view', row)
        }
      }
    }
  },
  created () {
    let _this = this
    _this.tableHeader = [
      {
        prop: 'importStatus',
        label: () => _this.$t('vendorMod.orderStatus'), // '状态'
        width: 90,
        dataType: 'dict', // 数据类型为字典
        code: 'VENDORIMPORTSTATUS' // 字典code
      },
      {
        prop: 'vendorCode',
        label: () => _this.$t('common.vendorCode'), // '供应商编码'
        width: 150
      },
      {
        prop: 'vendorName',
        label: () => _this.$t('common.vendorName'), // '供应商名称'
        minWidth: 150
      },
      {
        prop: 'importNum',
        label: () => _this.$t('vendorMod.importNum'), // 供应商引入单号
        width: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.editTab('view', row)
        }.bind(this)
      },
      {
        prop: 'createdUserName', // createdBy
        label: () => _this.$t('common.creator'), // '创建人'
        width: 120
      },
      {
        prop: 'creationDate',
        label: () => _this.$t('common.creationTime'), // '创建时间'
        width: 120,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'operation',
        label: () => _this.$t('common.operation'), // '操作'
        width: 120,
        btnStyle: 'text',
        fixed: 'right',
        showType: 'buttons',
        buttons: [
          {
            callback: function (row) {
              this.editTab('edit', row)
            }.bind(this),
            code: 'sup:crossOrgImportList:edit',
            formattor (val) {
              return _this.$t('common.edit') // '编辑'
            },
            show: (row) => ['DRAFT', 'REJECTED', 'WITHDRAW'].includes(row.importStatus)
          },
          {
            callback: function (row) {
              this.approvalOne(row)
            }.bind(this),
            code: 'sup:crossOrgImportList:approval',
            formattor (val) {
              return _this.$t('vendorMod.doApproval') // 审批
            },
            show: (row) => row.importStatus === 'SUBMITTED'
          },
          {
            callback: function (row) {
              this.delRowData(row)
            }.bind(this),
            code: 'sup:crossOrgImportList:delete',
            formattor (val) {
              return _this.$t('common.delete') // '删除'
            },
            show: function (row) {
              if (row.importStatus === 'DRAFT') {
                return true
              } else {
                return false
              }
            }
          }
          // {
          //   callback: function (row) {
          //     this.adandonOne(row)
          //   }.bind(this),
          //   code: 'sup:crossOrgImportList:abandon',
          //   formattor (val) {
          //     return _this.$t('common.abandon') // 废弃
          //   },
          //   show: (row) => ['REJECTED', 'WITHDRAW'].includes(row.importStatus)
          // }
        ]
      }
    ]
    this.defaultTableHeader = this.tableHeader // 自定义表格表头
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    getQuerydata (v) {
      this.queryParam = v || this.queryParam
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    syncFilterParams (values) {
      this.queryParam = values
    },

    adandonOne (row) {
      crossOrgImport.abandon(row.importId).then((data) => {
          this.$message.success(this.$t('common.success')) // 操作成功
          this.getQuerydata()
        })
        .catch((err) => {
          console.log(err)
        })
    },
    // 编辑tab
    editTab (type, row) {
      let tab = {}
      if (type === 'add') {
        // 新增
        tab = {
          component: crossOrgImportDetail,
          params: {
            flag: 'add',
            tabName: 'crossOrgImportDetail'
          },
          title: () => this.$t('vendorMod.addCrossImport'), // 跨组织引入新增
          name: 'crossOrgImportDetail'
        }
      } else if (type === 'view') {
        // 查看
        tab = {
          component: crossOrgImportDetail,
          params: {
            flag: 'view',
            row: row,
            importId: row.importId,
            tabName: 'crossOrgImportDetail' + row.importNum
          },
          title: row.importNum,
          name: 'crossOrgImportDetail' + row.importNum
        }
      } else {
        // 修改
        tab = {
          component: crossOrgImportDetail,
          params: {
            flag: 'edit',
            row: row,
            importId: row.importId,
            tabName: 'crossOrgImportDetail' + row.importNum || row.importId
          },
          title: row.importNum,
          name: 'crossOrgImportDetail' + row.importNum || row.importId
        }
      }
      this.$emit('tab-add', tab)
    },
    // 删除数据
    delRowData (row) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          crossOrgImport.delRowData({ importId: row.importId }).then((res) => {
              this.$message({
                message: res.message,
                type: 'success'
              })
              this.getQuerydata()
            })
            .catch((err) => {
              console.log(err)
            })
        })
        .catch(() => {})
    },
    approvalOne (row) {
      this.$emit('tab-add', {
        component: crossOrgImportDetail,
        ctrlHeight: true,
        params: {
          activeWorkflowTab: true,
          flag: 'view',
          row: row,
          importId: row.importId,
          tabName: 'crossOrgImportDetail' + row.importNum || row.importId
        },
        title: row.importNum,
        name: 'crossOrgImportDetail' + row.importNum || row.importId
      })
    },
    handleCurrentChange (val) {
      this.currentRow = val
    }
  }
}
</script>
<style scoped lang="scss"></style>
