<template>
  <el-container
    class="flex-container questSupplier_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="filterConfig"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left" />
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :checkbox="true"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-sup/quest/questSupplier/listPageByParm"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import questSupplierDetail from '../questSupplier/questSupplierDetail'
import { parseTime, adaptDictData } from '@/utils'

export default {
  name: 'QuestSupplierList',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  watch: {
    $route: {
      // 当前功能已经打开的时候监听是否是从其他功能跳转过来的
      deep: true,
      immediate: true,
      handler () {
        if (
          this.$route.params.from === 'fromFun'
        ) {
          let questSupId = Number(this.$route.params.formId)
          let questNo = this.$route.params.formNo // 流程标题
          let row = {
            questSupId,
            questNo // tab 标题显示
          }
          this.writeData( row)
        }
      }
    }
  },
  data () {
    return {
      name: 'questSupplierList',
      tableName: 'questSupplierTable',
      pageSize: 15,
      gridId: 'list',
      currentRows: [],
      curRole: this.$store.getters.userType, // vendor buyer
      tableHeader: [
        {
          prop: 'questNo',
          label: this.$t('quest.questNo'),
          width: 120,
          showType: 'button',
          btnStyle: 'text',
          callback: function (row) {
            this.editTab('view', row)
          }.bind(this)
        },
        {
          prop: 'questName',
          label: this.$t('vendorMod.questName'),
          width: 150
        },
        {
          prop: 'approvalStatus',
          label: this.$t('quest.approvalStatus'),
          width: 120,
          dataType: 'dict', // 数据类型为字典
          code: 'QUEST_SUPPLIER_APPROVE_STATUS' // 字典code
        },
        {
          prop: 'companyCode',
          label: this.$t('common.vendorCode'),
          width: 150
        },
        {
          prop: 'companyName',
          label: this.$t('common.companyName'),
          minWidth: 150
        },
        {
          prop: 'questTemplateOrgName',
          label: this.$t('vendorMod.questTemplateOrgName'),
          width: 200
        },
        {
          prop: 'questTemplateCode',
          label: this.$t('quest.questTemplateCode'),
          width: 140
        },
        {
          prop: 'questTemplateName',
          label: this.$t('quest.questTemplateName'),
          minWidth: 150
        },
        {
          prop: 'createdFullName',
          label: this.$t('common.creator'),
          width: 100
        },
        {
          prop: 'creationDate',
          label: this.$t('common.creationTime'),
          width: 150,
          dataType: 'dateTime'
        },
        {
          prop: 'operation',
          label: this.$t('components.headers.operation'),
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          width: 100,
          buttons: [
            {
              callback: (row) => this.writeData(row),
              // code: "pr:requirementApply:edit",
              show: (row) =>
                this.curRole === 'VENDOR' &&
                ['PUBLISH', 'WRITING', 'PRE_REJECTED', 'REJECTED', 'WITHDRAW'].includes(
                  row.approvalStatus
                ),
              formattor: () => {
                return this.$t('bidMod.input')
              }
            }
          ]
        }
      ],
      filterConfig: [
        { prop: 'questNoForQuery', label: this.$t('quest.questNo') },
        { prop: 'questTemplateOrgId', label: this.$t('vendorMod.questTemplateOrgName'), type: 'OUorganizationSelector' },
        {
          prop: 'companyIdForQuery',
          label: this.$t('common.companyName'),
          type: 'quicksearch',
          showKey: 'companyName',
          propKey: 'companyId',
          name: 'scc_sup_company_info_display'
        },
        { prop: 'creationDateBegin', label: this.$t('dashboard.datetimeStart'), type: 'datetime' },
        { prop: 'creationDateEnd', label: this.$t('dashboard.datetimeEnd'), type: 'datetime' },
        {
          prop: 'approvalStatus',
          label: this.$t('quest.approvalStatus'),
          type: 'dict', // 字典类型
          code: 'QUEST_SUPPLIER_APPROVE_STATUS'
        }
      ],
      queryParam: {},
      approvalStatusList: [], // 审批状态
      questTemplateTypeList: [], // 调查模板类型
      yesOrNoList: [] // 是否
    }
  },
  created () {
    if (this.$store.getters.userType === 'VENDOR') {
      this.filterConfig = this.filterConfig.filter(item => item.prop !== 'companyIdForQuery')
    }
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    getQuerydata (params) {
      const companyId = this.$store.getters.user.companyId
      this.queryParam = {
        companyIdForQuery: companyId,
        approvalStatus:
          'PUBLISH,WRITING,WRITED,PRE_PASS,PRE_REJECTED,SUBMITTED,REJECTED,APPROVED,ABANDONED,WITHDRAW',
        ...params
      }
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    syncFilterParams (values) {
      this.queryParam = values
    },
    deleteHandle (row) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url: '/api-sup/quest/questSupplier/deleteById',
            method: 'GET',
            params: { id: row.questSupId }
          }).then((res) => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
        .catch(() => {})
    },
    writeData (row) {
      let tab = {}
      tab = {
        component: questSupplierDetail,
        params: {
          flag: 'edit',
          row: row,
          tabName: 'questSupplierDetail' + row.questSupId
        },
        title: this.$t('dashboard.enumerationForm') + row.questNo,
        name: 'questSupplierDetail' + row.questSupId
      }
      this.$emit('tab-add', tab)
    },
    // 编辑tab
    editTab (type, row) {
      let tab = {}
      if (type === 'view') {
        // 查看
        tab = {
          component: questSupplierDetail,
          params: {
            flag: 'view',
            row: row,
            tabName: 'questSupplierDetail' + row.questSupId
          },
          title: this.$t('dashboard.enumerationForm') + row.questNo,
          name: 'questSupplierDetail' + row.questSupId
        }
      }
      this.$emit('tab-add', tab)
    },
    handleCurrentChange (val) {
      this.currentRows = val
    },
    // 上传附件成功
    handleUploadSuccess (file, row, key) {
      const { id, name } = file
      row[key] = id.toString()
    },
    // 删除文件
    handleAttachmentRemove (row, key) {
      row[key] = ''
    }
  }
}
</script>
