<template>
  <el-container
    class="flex-container the_dictionary_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        queryName="vendorGreenChannel"
        :form-array="queryForm"
        @getFormData="getQuerydata"
      />
      <MainHeader>
        <template slot="left">
          <AuthorityButton
            type="primary"
            @click="editTab('add')"
          >
            <!-- code="sup:vendorGreenChannelList:add" -->
            {{ $t('common.add') }}
          </AuthorityButton>
          <!-- 导入供应商 -->
          <MImport
            ref="import"
            :title="iModal.title"
            type="default"
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
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-sup/info/companyInfo/listPageByDTO"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import vendorGreenChannelDetail from './vendorGreenChannelDetail'
import { parseTime, adaptDictData } from '@/utils'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import { vendorGreenApi } from 'modb@/vendorManagementBuyer/api/vendorManagement'
export default {
  name: 'VendorGreenChannelList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    MImport
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      tableName: 'vendorGreenChannelList',
      defaultTableHeader: [],
      iModal: {
        // 导入供应商
        title: this.$t('vendorMod.importVendor'),
        upLoadUrl: '/api-sup/info/companyInfo/importDatasExcel'
      },
      extraData: {
        fileModular: 'sup',
        fileFunction: 'companyInfoImportExcel',
        fileType: 'excel'
      },
      name: '',
      reviewFormNumber: '',
      gridData: [],
      pageSize: 15,
      gridId: 'vendorGreenChannelList',
      selectList: [],
      currentRow: null,
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      statusList: [],
      relations: [], // 境内外管理
      natureList: [], // 企业性质
      approveStatus: [], // 审批状态
      dataSource: [], // 数据来源
      isModify: false,
      dialogFormVisible: false,
      formLabelWidth: '100px',
      queryForm: [],
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
          this.$route.params.funName === 'vendorGreenChannel'
        ) {
          let companyId = Number(this.$route.params.formId)
          let formNo = this.$route.params.formNo // 流程标题
          let row = {
            ...this.$route.params,
            companyId,
            companyName: formNo // tab 标题显示
          }
          this.editTab('readOnly', row)
        }
      }
    }
  },
  created () {
    let _this = this
    _this.queryForm = [
      {
        prop: 'companyName',
        label: () => _this.$t('common.vendorName'), // '供应商名称'
        type: 'quicksearch',
        showKey: 'companyName',
        name: 'scc_sup_company_info_display_buyer'
      },
      {
        prop: 'lcCode',
        label: () => _this.$t('vendorMod.lcCode') // '社会统一信用代码'
      },
      {
        prop: 'isBacklist',
        label: () => _this.$t('vendorMod.isBacklist'), // '是否黑名单'
        type: 'select',
        options: [
          { value: 'Y', label: _this.$t('common.yes') },
          { value: 'N', label: _this.$t('common.no') }
        ]
      },
      {
        prop: 'overseasRelation',
        label: () => _this.$t('vendorMod.overseasRelation'), // '境内外关系'
        type: 'dict', // 字典类型
        code: 'RELATION' // 字典code
      },
      {
        prop: 'companyType',
        label: () => _this.$t('vendorMod.companyType'), // '企业性质'
        type: 'dict', // 字典类型
        code: 'COMPANY_NATURE' // 字典code
      },
      {
        prop: 'status',
        label: () => _this.$t('vendorMod.approveStatus'), // '审批状态'
        type: 'dict', // 字典类型
        code: 'APPROVE_STATUS_TYPE' // 字典code
      },
      {
        prop: 'legalPerson',
        label: () => _this.$t('vendorMod.legalPerson') // '法定代表人'
      },
      {
        prop: 'dateList',
        label: () => _this.$t('vendorMod.permitDate'), // '准入日期'
        type: 'daterange'
      },
      {
        prop: 'supplierType',
        label: () => _this.$t('supplierRating.supplierType'), // '供应商类型'
        type: 'dict', // 字典类型
        code: 'SUPPLIER_TYPE' // 字典code
      }
    ]
    _this.tableHeader = [
      {
        prop: 'companyCode',
        label: () => _this.$t('common.vendorCode'), // '供应商编码'
        width: 120
      },
      {
        prop: 'companyName',
        label: () => _this.$t('common.vendorName'), // '供应商名称'
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.editTab('readOnly', row)
        }.bind(this)
      },
      {
        prop: 'supplierType',
        label: () => _this.$t('supplierRating.supplierType'), // '供应商类型'
        width: 150,
        dataType: 'dict', // 数据类型为字典
        code: 'SUPPLIER_TYPE' // 字典code
      },
      {
        prop: 'overseasRelation',
        label: () => _this.$t('vendorMod.overseasRelation'), // '境内外关系'
        width: 150,
        dataType: 'dict', // 数据类型为字典
        code: 'RELATION' // 字典code
      },
      {
        prop: 'companyType',
        label: () => _this.$t('vendorMod.companyType'), // '企业性质'
        width: 100,
        dataType: 'dict', // 数据类型为字典
        code: 'COMPANY_NATURE' // 字典code
      },
      {
        prop: 'lcCode',
        label: () => _this.$t('vendorMod.lcCode'), // '社会统一信用代码'
        width: 150
      },
      {
        prop: 'legalPerson',
        label: () => _this.$t('vendorMod.legalPerson'), // '法定代表人'
        width: 120
      },
      {
        prop: 'isBacklist',
        label: () => _this.$t('vendorMod.isBacklist'), // '是否黑名单'
        width: 120,
        formattor (val) {
          return val === 'Y' ? _this.$t('common.yes') : _this.$t('common.no') // '是' : '否'
        }
      },
      {
        prop: 'status',
        label: () => _this.$t('vendorMod.approveStatus'), // '审批状态'
        width: 100,
        dataType: 'dict', // 数据类型为字典
        code: 'APPROVE_STATUS_TYPE' // 字典code
      },
      {
        prop: 'approvedDate',
        label: () => _this.$t('vendorMod.permitDate'), // '准入日期'
        width: 120
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
            code: 'sup:vendorGreenChannelList:edit',
            formattor (val) {
              return _this.$t('common.edit') // '编辑'
            },
            show: (row) => ['DRAFT', 'WITHDRAW', 'REJECTED'].includes(row.status)
          },
          {
            callback: function (row) {
              this.delRowData(row)
            }.bind(this),
            code: 'sup:vendorGreenChannelList:delete',
            formattor (val) {
              return _this.$t('common.delete') // '删除'
            },
            show: (row) => ['DRAFT'].includes(row.status)
          },
          {
            callback: function (row) {
              this.toApproval(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('vendorMod.doApproval') // 审批
            },
            show: (row) => ['SUBMITTED'].includes(row.status)
          },
          {
            callback: (row) => this.toApproval(row),
            code: 'sup:vendorGreenChannelList:abandon',
            formattor: () => _this.$t('common.abandon'), // 废弃
            show: (row) => ['WITHDRAW', 'REJECTED'].includes(row.status) // change by liwenhong
          }
        ]
      }
    ]
    this.defaultTableHeader = _this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    // 审批跳转到详情页
    toApproval (row) {
      this.editTab('approve', row)
    },
    downloadTemplate () {
      // 供应商导入模板.xlsx
      downloadFileLink(
        '/api-sup/info/companyInfo/importModelDownload',
        this.$t('vendorMod.vendorImportTemplateXLXS')
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
      })
    },
    handleSuccess () {
      this.getQuerydata()
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
    // 删除数据
    delRowData (row) {
      let companyId = row.companyId
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          vendorGreenApi.companyGreenChannelDel([companyId]).then((res) => {
            this.$message({
              message: res.message,
              type: 'success'
            })
            this.getQuerydata()
          })
        })
        .catch(() => {})
    },
    // 编辑编辑tab
    editTab (type, row) {
      // type add edit readOnly approve
      let tab = {}
      if (type === 'add') {
        // 新增
        tab = {
          component: vendorGreenChannelDetail,
          params: {
            flag: 'add',
            tabName: 'vendorGreenChannelDetail'
          },
          title: () => this.$t('vendorMod.addVendor'), // '新增供应商',
          name: 'vendorGreenChannelDetail'
        }
      } else {
        let companyId = row.companyId
        tab = {
          component: vendorGreenChannelDetail,
          params: {
            flag: type,
            companyId: companyId,
            tabName: 'vendorGreenChannelDetail' + row.companyName
          },
          title: row.companyName,
          name: 'vendorGreenChannelDetail' + row.companyName
        }
      }

      this.$emit('tab-add', tab)
    },
    handleCurrentChange (val) {
      this.currentRow = val
    }
  }
}
</script>
<style scoped lang="scss"></style>
