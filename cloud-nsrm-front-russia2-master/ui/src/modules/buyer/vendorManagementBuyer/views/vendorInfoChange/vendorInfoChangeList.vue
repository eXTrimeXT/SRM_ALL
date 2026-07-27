<template>
  <el-container
    class="flex-container the_dictionary_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />
      <MainHeader>
        <template slot="left">
          <AuthorityButton
            type="primary"
            code="sup:vendorInfoChangeList:add"
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
        :com-active="$attrs['changeTab']"
        url="/api-sup/change/infoChange/listPageByParam"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import vendorInfoChangeDetail from './vendorInfoChangeDetail'
import { parseTime } from '@/utils'
import { vendorChangeApi, saveOrUpdateOrderByUrl } from 'modb@/vendorManagementBuyer/api/vendorManagement'

export default {
  name: 'VendorInfoChangeList',
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
      tableName: 'vendorInfoChangeList',
      defaultTableHeader: [],
      name: '',
      reviewFormNumber: '',
      gridData: [],
      pageSize: 15,
      gridId: 'vendorInfoChangeList',
      selectList: [],
      currentRow: null,
      showFilterBar: 1,
      userType: this.$store.getters.userType, // VENDOR BUYER
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
      queryParam: {},
      companyIdAdd: this.$store.getters.companyId, // 当前登录公司ID
      companyIdName: this.$store.getters.userInfo.companyName // 当前登录公司名称
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
          this.$route.params.funName === 'vendorInfoChange'
        ) {
          // let changeId = Number(this.$route.params.fdFormInstanceId);
          // let fdSubject = this.$route.params.fdSubject; // 流程标题
          let changeId = Number(this.$route.params.formId)
          let fromNo = this.$route.params.formNo // 流程标题
          let row = {
            ...this.$route.params,
            changeId,
            companyName: fromNo // tab 标题显示
          }
          this.editTab('doApproval', row)
        }
      }
    }
  },
  created () {
    let _this = this
    this.queryForm = [
      // '变更编号'
      { prop: 'changeApplyNo', label: this.$t('vendorMod.changeApplyNo') },
      // '供应商名称'
      {
        prop: 'companyName',
        label: this.$t('common.vendorName'),
        type: 'quicksearch',
        showKey: 'companyName',
        name: 'scc_sup_company_info_display_buyer'
      },
      // '创建日期'
      {
        prop: 'dateList',
        label: this.$t('common.creationTime'),
        type: 'daterange'
      },
      // '变更状态'
      {
        prop: 'changeStatus',
        label: this.$t('vendorMod.changeStatus'),
        type: 'dict',
        code: 'INFO_CHANGE_STATUS'
      },
      // '法定代表人'
      { prop: 'legalPerson', label: this.$t('vendorMod.legalPerson') },
      // '社会统一信用代码'
      { prop: 'lcCode', label: this.$t('vendorMod.lcCode') }
    ]

    if (this.userType === 'VENDOR') {
      this.queryForm.splice(1, 1)
    }

    this.tableHeader = [
      {
        prop: 'changeStatus',
        label: () => this.$t('vendorMod.changeStatus'), // '变更状态'
        minWidth: 100,
        dataType: 'dict', // 数据类型为字典
        code: 'INFO_CHANGE_STATUS' // 字典code
      },
      {
        prop: 'changeApplyNo',
        label: () => this.$t('vendorMod.changeApplyNo'), // '变更单号'
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.editTab('view', row)
      },
      {
        prop: 'companyCode',
        label: () => this.$t('common.vendorCode'), // '供应商编码'
        minWidth: 120
      },
      {
        prop: 'companyName',
        label: () => this.$t('common.vendorName'), // '供应商名称'
        minWidth: 150
      },
      {
        prop: 'overseasRelation',
        label: () => this.$t('vendorMod.overseasRelation'), // '境内外关系'
        minWidth: 110,
        dataType: 'dict', // 数据类型为字典
        code: 'RELATION' // 字典code
      },
      {
        prop: 'lcCode',
        label: () => this.$t('vendorMod.lcCode'), // '社会统一信用代码'
        minWidth: 150
      },
      {
        prop: 'legalPerson',
        label: () => this.$t('vendorMod.legalPerson'), // '法定代表人'
        minWidth: 120
      },
      {
        prop: 'lastUpdateDate',
        label: () => this.$t('vendorMod.changeApprovedDate'), // '审批日期'
        minWidth: 130,
        formattor (val, row) {
          if (row.changeStatus === 'APPROVED') {
            return val ? parseTime(val, '{y}-{m}-{d}') : ''
          }
        }
      },
      {
        prop: 'createdUserName', // createdBy
        label: () => this.$t('common.creator'), // '创建人'
        minWidth: 100,
        formattor (val, row) {
          return val || row.createdBy
        }
      },
      {
        prop: 'creationDate',
        label: () => this.$t('common.creationTime'), // '创建日期'
        minWidth: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'operation',
        label: () => this.$t('common.operation'), // '操作'
        width: 120,
        btnStyle: 'text',
        fixed: 'right',
        showType: 'buttons',
        buttons: [
          // 编辑
          {
            callback: row => this.editTab('edit', row),
            code: 'sup:vendorInfoChangeList:edit',
            formattor: () => this.$t('common.edit'),
            // 不同角色各自创建的单只能各自角色编辑 拟定
            show: row => (this.userType === row.userType && row.changeStatus === 'DRAFT') ||
              // 采购商 [已驳回, 已撤回]
              (this.userType === 'BUYER' && ['REJECTED', 'WITHDRAW'].includes(row.changeStatus)) ||
              // 供应商 [已供应商撤回, 已采购商驳回]
              (this.userType === 'VENDOR' && ['VENDOR_WITHDRAW', 'VENDOR_REJECTED'].includes(row.changeStatus))
          },
          // 删除
          {
            callback: row => this.delRowData(row),
            code: 'sup:vendorInfoChangeList:delete',
            formattor: () => this.$t('common.delete'),
            // 不同角色各自创建的单只能各自角色编辑 拟定
            show: row => (this.userType === row.userType && row.changeStatus === 'DRAFT') ||
              // 采购商 [已驳回, 已撤回]
              (this.userType === 'BUYER' && ['REJECTED'].includes(row.changeStatus)) ||
              // 供应商 [已供应商撤回, 已采购商驳回]
              (this.userType === 'VENDOR' && ['VENDOR_WITHDRAW', 'VENDOR_REJECTED'].includes(row.changeStatus))
          },
          // 废弃
          {
            callback: row => this.editTab('doApproval', row),
            code: 'sup:vendorInfoChangeList:abandon',
            formattor: () => this.$t('common.abandon'),
            show: row => ['REJECTED', 'WITHDRAW'].includes(row.changeStatus)
          },
          // '审批'
          {
            callback: row => this.editTab('doApproval', row),
            code: 'sup:vendorInfoChangeList:doApproval',
            formattor: () => this.$t('vendorMod.doApproval'),
            show: (row) => row.changeStatus === 'SUBMITTED' && this.userType === 'BUYER'
          },
          // 撤回 [供应商]
          {
            show: row => row.changeStatus === 'VENDOR_SUBMITTED' && this.userType === 'VENDOR',
            formattor: () => this.$t('common.recall'),
            callback: row => this.rejectOrRecallRow('RECALL', row)
          },
          // 管理 [采购商]
          {
            show: row => row.changeStatus === 'VENDOR_SUBMITTED' && this.userType === 'BUYER',
            formattor: () => this.$t('contractMod.manage'),
            callback: row => this.editTab('view', row)
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
    getQuerydata (v) {
      if (v && v.dateList) {
        v.startDate = v.dateList[0]
        v.endDate = v.dateList[1]
      } else if (v && !v.dateList) {
        delete v.startDate
        delete v.endData
      }
      this.queryParam = v || this.queryParam
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    syncFilterParams (values) {
      this.queryParam = values
    },
    // 删除数据
    delRowData (row) {
      let changeId = row.changeId
      // '当前操将永久删除这条数据，确认删除这条数据？'
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          vendorChangeApi.changeInfoDel({ changeId }).then((res) => {
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
    async editTab (type, row) {
      let tab = {}
      if (type === 'add') {
        if (this.userType === 'VENDOR') {
          let res = await vendorChangeApi.ifAddInfoChange(this.companyIdAdd)
          if (res.data.changeStatus === 'Y') {
            tab = {
              component: vendorInfoChangeDetail,
              params: {
                flag: 'add',
                companyId: this.companyIdAdd,
                companyNameAdd: this.companyIdName,
                tabName: 'vendorInfoChangeDetail'
              },
              title: () => this.$t('vendorMod.addVendorInfoChange'), // '新增供应商信息变更',
              name: 'vendorInfoChangeDetail'
            }
          } else {
            this.$message({
              type: 'error',
              message: this.$t('vendorMod.msgRetry') // 对不起，您存在正在变更的单据，请提交审批后重试。
            })
          }
        } else {
          // 新增;
          tab = {
            component: vendorInfoChangeDetail,
            params: {
              flag: 'add',
              companyId: '',
              tabName: 'vendorInfoChangeDetail'
            },
            title: () => this.$t('vendorMod.addVendorInfoChange'), // '新增供应商信息变更',
            name: 'vendorInfoChangeDetail'
          }
        }
      } else if (type === 'view') {
        let changeId = row.changeId
        let companyId = row.companyId
        tab = {
          component: vendorInfoChangeDetail,
          params: {
            flag: 'view',
            changeId: changeId,
            companyId: companyId,
            tabName: 'vendorInfoChangeDetail' + row.companyName
          },
          title: row.companyName,
          name: 'vendorInfoChangeDetail' + row.companyName
        }
      } else if (type === 'doApproval') {
        let changeId = row.changeId
        let companyId = row.companyId
        tab = {
          component: vendorInfoChangeDetail,
          params: {
            flag: 'doApproval',
            row,
            changeId,
            companyId,
            tabName: 'vendorInfoChangeDetail' + row.companyName
          },
          title: row.companyName,
          name: 'vendorInfoChangeDetail' + row.companyName
        }
      } else {
        // 修改
        let changeId = row.changeId
        let companyId = row.companyId
        tab = {
          component: vendorInfoChangeDetail,
          params: {
            flag: 'edit',
            changeId: changeId,
            companyId: companyId,
            tabName: 'vendorInfoChangeDetail' + row.companyName
          },
          title: row.companyName,
          name: 'vendorInfoChangeDetail' + row.companyName
        }
      }
      this.$emit('tab-add', tab)
    },

    handleCurrentChange (val) {
      this.currentRow = val
    },

    /*  驳回 [采购商] or 撤回 [供应商] */
    async rejectOrRecallRow (type, row) {
      const title = type === 'REJECT' ? this.$t('common.toRefuse') : this.$t('common.recall')
      const apiName = type === 'REJECT' ? 'buyerReject' : 'buyerWithdraw'

      const promptResult = await this.$prompt(
        '',
        `${title}原因`,
        {
          confirmButtonText: this.$t('common.confirm'),  // '确定'
          cancelButtonText: '取消',
          inputValidator: value => !(!value || value.length > 500),
          // 原因必填并且长度不能超过500字符！
          inputErrorMessage: `${title}${this.$t('cusEntry.supplement20250211.reasonRequiredAndMaxLength500')}`
        }
      )

      if (!promptResult) {
        return
      }

      let datas = {
        changeId: row.changeId,
        flowRemark: promptResult.value
      }
      let url = `/api-sup/change/infoChange/${apiName}`
      saveOrUpdateOrderByUrl(url, datas).then(() => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    }
  }
}
</script>
