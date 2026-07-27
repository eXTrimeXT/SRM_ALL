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
          label: '调查表编号',
          width: 150,
          showType: 'button',
          btnStyle: 'text',
          callback: function (row) {
            this.editTab('view', row)
          }.bind(this)
        },
        {
          prop: 'questName',
          label: '调查表名称',
          width: 150
        },
        {
          prop: 'approvalStatus',
          label: '调查表状态',
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
          label: '业务组织',
          width: 200
        },
        {
          prop: 'questTemplateCode',
          label: '调查模板编码',
          width: 100,
          hidden: this.$store.getters.userType === 'VENDOR'
        },
        {
          prop: 'questTemplateName',
          label: '调查表模板名称',
          minWidth: 180,
          hidden: this.$store.getters.userType === 'VENDOR'
        },
        {
          prop: 'modelCode',
          label: '车型编码',
          width: 100
        },
        {
          prop: 'stage',
          label: '阶段',
          width: 100
        },
        {
          prop: 'recoveryTime',
          label: '回收时间',
          width: 100,
          sortMethod: (a, b) => {
            return new Date(a.recoveryTime).getTime() - new Date(b.recoveryTime).getTime()
          },
          formattor: val => {
            if (val) {
              return /\d{4}-\d{1,2}-\d{1,2}/.exec(val)
            }
          }
        },
        {
          prop: 'recyclingTime',
          label: '反馈提交时间',
          width: 120,
          sortMethod: (a, b) => {
            return new Date(a.recyclingTime).getTime() - new Date(b.recyclingTime).getTime()
          },
          formattor: val => {
            if (val) {
              return /\d{4}-\d{1,2}-\d{1,2}/.exec(val)
            }
          }
        },
        {
          prop: 'rangeType',
          label: '范围类型',
          width: 100,
          dataType: 'dict',
          code: 'QuestionnaireRangeType'
        },

        {
          prop: 'createdFullName',
          label: '创建人',
          width: 100
          // hidden:this.$store.getters.userType === 'VENDOR'
        },
        {
          prop: 'responsibleForHealth',
          label: '生准担当',
          width: 130,
          hidden: this.$store.getters.userType === 'BUYER'
        },
        {
          prop: 'creationDate',
          label: '创建时间',
          width: 150
        },
        {
          prop: 'lastUpdateDate',
          label: '修改时间',
          width: 150
        },
        {
          prop: 'operation',
          label: '操作',
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
                return '查看数据'
              },
              code: 'sup:questManagement:view'
            },
            {
              callback: row => this.closeHandle(row),
              show: row => this.curRole === 'BUYER' && ['DRAW_UP', 'PUBLISHED'].includes(row.approvalStatus),
              formattor: () => {
                return '关闭'
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
                return '填写'
              }
            }
          ]
        }
      ],
      tableData: [],
      filterConfig: [
        { prop: 'questNoForQuery', label: '调查表编号' },
        { prop: 'questTemplateOrgId', label: '业务组织', type: 'OUorganizationSelector' },
        { prop: 'companyIdForQuery', label: '供应商名称', type: 'quicksearch', showKey: 'companyName', propKey: 'companyId', name: 'scc_sup_company_info' },
        { prop: 'creationDateBegin', label: '创建日期起', type: 'datetime' },
        { prop: 'creationDateEnd', label: '创建日期止', type: 'datetime' },
        { prop: 'approvalStatus', label: '调查表状态', type: 'dict', code: 'QUESTIONNAIRE_STATUS' },
        { prop: 'vehicleTypeCode', label: '车型编码' },
        { prop: 'stage', label: '阶段', type: 'dict', code: 'QUESTIONNAIRE_STAGE' },
        { prop: 'questName', label: '调查表名称' },
        { prop: 'createdFullName', label: '创建人', type: 'quicksearch', showKey: 'nickname', name: 'scc_rbac_user_display' },
        { prop: 'recoveryDate', label: '回收时间', type: 'daterange' }
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
      this.$confirm('确认关闭？', {
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
        title: () => row ? '调查表' + row.questNo : this.$t('route.questSupplierAdd'), // '调查表新增',
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
