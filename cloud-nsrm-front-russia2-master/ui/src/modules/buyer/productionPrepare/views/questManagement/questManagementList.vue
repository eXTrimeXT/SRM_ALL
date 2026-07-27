<template>
  <el-container class="flex-container questManagement_list_wrapper" direction="vertical">
    <el-main>
      <FormWrapper
        :formArray="filterConfig"
        :preFormObj="preFormObj"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />
      <MainHeader :lSpan="22" :rSpan="2">
        <template slot="left">
          <AuthorityButton
            v-if="curRole === 'BUYER'"
            code="sup:questManagement:add"
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
        :checkChange="handleCurrentChange"
        :page-size="pageSize"
        :checkbox="false"
        :preQueryData="queryParam"
        :openCustomTable="false"
        url="/api-ppap/quest/questSupplier/listPageByParm"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import questManagementDetail from './questManagementDetail'
import { getDictItem, getDictItemList, getAllPurCurrency } from '@/api/common'
import { parseTime, adaptDictData, findMenuIdByPath } from '@/utils'
import { questManagement } from 'modb@/productionPrepare/api'

export default {
  name: 'QuestManagementList',
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
      preFormObj: {},
      name: 'questManagementList',
      tableName: 'questSupplierTable',
      pageSize: 15,
      gridId: 'list',
      currentRows: [],
      curRole: this.$store.getters.userType, // vendor buyer
      menuId: null,
      integrationMode: '', // 工作流模板
      tableHeader: [
        {
          prop: 'questNo',
          // '调查表编号'
          label: this.$t('quest.questNo'),
          width: 150,
          showType: 'button',
          btnStyle: 'text',
          callback: function (row) {
            this.editTab('view', row)
          }.bind(this)
        },
        {
          prop: 'questName',
          // '调查表名称'
          label: this.$t('vendorMod.questName'),
          width: 150
        },
        {
          prop: 'approvalStatus',
          // '调查表状态'
          label: this.$t('quest.approvalStatus'),
          width: 120,
          dataType: 'dict',
          code: 'QUESTIONNAIRE_STATUS'
        },
        // {
        //   prop: "companyCode",
        //   label: "供应商编码",
        //   width: 150,
        // },
        // {
        //   prop: "companyName",
        //   label: "供应商名称",
        //   minWidth: 150,
        // },
        {
          prop: 'questTemplateOrgName',
          // '业务组织'
          label: this.$t('vendorMod.questTemplateOrgName'),
          width: 200
        },
        {
          prop: 'questTemplateCode',
          // '调查模板编码'
          label: this.$t('quest.questTemplateCode'),
          width: 100,
          hidden: this.$store.getters.userType === 'VENDOR'
        },
        {
          prop: 'questTemplateName',
          // '调查表模板名称'
          label: this.$t('quest.questTemplateName'),
          minWidth: 180,
          hidden: this.$store.getters.userType === 'VENDOR'
        },
        {
          prop: 'modelCode',
          // '车型编码'
          label: this.$t('problemManagement.motorcycleTypeCode'),
          width: 100
        },
        {
          prop: 'stage',
          // '阶段'
          label: this.$t('productionPrepare.stage'),
          width: 100
        },
        {
          prop: 'recoveryTime',
          // '回收时间'
          label: this.$t('productionPrepare.recoveryTime'),
          width: 100,
          sortMethod: (a, b) => {
            return new Date(a.recoveryTime).getTime() - new Date(b.recoveryTime).getTime()
          },
          dataType: 'dateTime'
        },
        {
          prop: 'recyclingTime',
          // '反馈提交时间'
          label: this.$t('productionPrepare.recyclingTime'),
          width: 120,
          sortMethod: (a, b) => {
            return new Date(a.recyclingTime).getTime() - new Date(b.recyclingTime).getTime()
          },
          dataType: 'dateTime'
        },
        {
          prop: 'rangeType',
          // '范围类型'
          label: this.$t('productionPrepare.rangeType'),
          width: 100,
          dataType: 'dict',
          code: 'QuestionnaireRangeType'
        },

        {
          prop: 'createdFullName',
          // '创建人'
          label: this.$t('common.creator'),
          width: 100
          // hidden:this.$store.getters.userType === 'VENDOR'
        },
        {
          prop: 'responsibleForHealth',
          // '生准担当'
          label: this.$t('productionPrepare.responsibleForHealth'),
          width: 130,
          hidden: this.$store.getters.userType === 'BUYER'
        },
        {
          prop: 'creationDate',
          // '创建时间'
          label: this.$t('common.creationTime'),
          width: 150,
          dataType: 'dateTime'
        },
        {
          prop: 'lastUpdateDate',
          // '修改时间'
          label: this.$t('vendorMod.modifyTime'),
          width: 150,
          dataType: 'dateTime'
        },
        {
          prop: 'operation',
          //  '操作'
          label: this.$t('components.headers.operation'),
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          minWidth: 200,
          buttons: [
            // DRAW_UP:拟定;PUBLISHED:已发布;CLOSED:已关闭;
            {
              callback: row => this.editTab('edit', row),
              show: row => this.curRole === 'BUYER' && ['DRAW_UP'].includes(
                row.approvalStatus
              ),
              formattor: () => {
                return this.$t('common.edit')
              },
              code: 'sup:questManagement:edit'
            },
            {
              callback: row => this.deleteHandle(row),
              show: row => this.curRole === 'BUYER' && row.approvalStatus === 'DRAW_UP',
              formattor: () => {
                return this.$t('common.delete')
              },
              code: 'sup:questManagement:delete'
            },
            {
              callback: row => {
                this.editTab('view', { ...row, activeList: '3' })
              },
              show: row => this.curRole === 'BUYER',
              formattor: () => {
                // '查看数据'
                return this.$t('dashboard.viewData')
              },
              code: 'sup:questManagement:view'
            },
            {
              callback: row => this.closeHandle(row),
              show: row => this.curRole === 'BUYER' && ['DRAW_UP', 'PUBLISHED'].includes(row.approvalStatus),
              formattor: () => {
                // '关闭'
                return this.$t('base.tagsView.close')
              },
              code: 'sup:questManagement:close'
            },
            {
              callback: row => {
                this.editTab('edit', row) // 待修改
              },
              show: row => this.curRole === 'VENDOR' && ['PUBLISHED'].includes(row.approvalStatus),
              code: 'sup:questManagement:write',
              formattor: () => {
                // '填写'
                return this.$t('bidMod.input')
              }
            }
          ]
        }
      ],
      tableData: [],
      filterConfig: [
        // '调查表编号'
        { prop: 'questNoForQuery', label: this.$t('quest.questNo') },
        // '业务组织'
        { prop: 'questTemplateOrgId', label: this.$t('vendorMod.questTemplateOrgName'), type: 'OUorganizationSelector' },
        // '供应商名称'
        { prop: 'companyIdForQuery', label: this.$t('common.companyName'), type: 'quicksearch', showKey: 'companyName', propKey: 'companyId', name: 'scc_sup_company_info' },
        // '创建日期起'
        { prop: 'creationDateBegin', label: this.$t('dashboard.datetimeStart'), type: 'datetime' },
        // '创建日期止'
        { prop: 'creationDateEnd', label: this.$t('dashboard.datetimeEnd'), type: 'datetime' },
        // '调查表状态'
        { prop: 'approvalStatus', label: this.$t('quest.approvalStatus'), type: 'dict', code: 'QUESTIONNAIRE_STATUS' },
        //  '车型编码'
        { prop: 'vehicleTypeCode', label:this.$t('problemManagement.motorcycleTypeCode') },
        // '阶段'
        { prop: 'stage', label: this.$t('productionPrepare.stage'), type: 'dict', code: 'QUESTIONNAIRE_STAGE' },
        // '调查表名称'
        { prop: 'questName', label: this.$t('vendorMod.questName') },
        // '创建人'
        { prop: 'createdFullName', label: this.$t('common.creator'), type: 'quicksearch', showKey: 'nickname', name: 'scc_rbac_user_display' },
        // '回收时间'
        { prop: 'recoveryDate', label: this.$t('productionPrepare.recoveryTime'), type: 'daterange' }
      ],
      queryParam: {}
    }
  },
  watch: {
    $route: {
      deep: true,
      immediate: true,
      handler () {
        let { from, formNo } = this.$route.params
        if (from === 'fromFun') {
          this.queryParam.questNoForQuery = formNo
          this.preFormObj = Object.assign({}, { questNoForQuery: formNo })
        }
      }
    }
  },
  async created () {
    if (this.curRole === 'VENDOR') {
      this.filterConfig = this.filterConfig.filter(item => item.prop !== 'companyIdForQuery')
    }
    this.$nextTick(() => {
      this.getQuerydata(this.queryParam)
    })
  },
  methods: {
    getQuerydata (params) {
      if (params === 'queryByParams') {
        this.$nextTick(() => {
          this.$refs[this.gridId].query()
        })
      } else {
        this.queryParam = params || {}
        let { recoveryDate } = this.queryParam
        if (recoveryDate && recoveryDate.length) {
          this.queryParam.recoveryDateBegin = recoveryDate[0]
          this.queryParam.recoveryDateEnd = recoveryDate[1]
        } else {
          delete this.queryParam.recoveryDateBegin
          delete this.queryParam.recoveryDateEnd
        }
        this.$nextTick(() => {
          this.$refs[this.gridId].query()
        })
      }
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
          questManagement.deleteById({ id: row.questNo }).then(res => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
        .catch(() => {})
    },
    closeHandle (row) {
      // '确认关闭？'
      this.$confirm(this.$t('productionPrepare.questManagementTip1'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          questManagement.closeQuestInventory({ questNoForQuery: row.questNo, approvalStatus: 'CLOSED' }).then(res => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
        .catch(() => {})
    },
    // 编辑tab
    editTab (type, row) {
      let tab = {}
      let name = 'questManagementDetail' + (row ? row.questNo : '')
      tab = {
        component: questManagementDetail,
        params: {
          flag: type,
          row: row,
          tabName: name
        },
        // '调查表'
        title: () => row ? this.$t('dashboard.enumerationForm') + row.questNo : this.$t('route.questSupplierAdd'), // '调查表新增',
        name
      }
      this.$emit('tab-add', tab)
    },
    handleCurrentChange (val) {
      this.currentRows = val
    }
  }
}
</script>
