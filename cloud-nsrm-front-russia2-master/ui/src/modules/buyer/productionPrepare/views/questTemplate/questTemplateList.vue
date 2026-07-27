<template>
  <el-container class="flex-container questtemplate_list_wrapper" direction="vertical">
    <el-main>
      <FormWrapper
        :formArray="filterConfig"
        @getFormData="getQueryData"
        @synchronous-value="syncFilterParams"
      />
      <MainHeader :lSpan="22" :rSpan="2">
        <template slot="left">
          <AuthorityButton
            type="primary"
            code="sup:questTemplate:add"
            @click="editTab('add')"
          >
            {{ $t('common.add') }}
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :checkChange="handleCurrentChange"
        :page-size="pageSize"
        :checkbox="true"
        :preQueryData="queryParam"
        :openCustomTable="true"
        :rowClass="rowClassFunc"
        :comActive="$attrs['changeTab']"
        url="/api-ppap/quest/questTemplate/listPageByParm"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import questTemplateDetail from './questTemplateDetail.vue'
import { parseTime, adaptDictData } from '@/utils'
import { questTemplate } from 'modb@/productionPrepare/api'

export default {
  name: 'QuestTemplateList',
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
      name: 'questTemplateList',
      tableName: 'questTemplateTable',
      pageSize: 15,
      gridId: 'list',
      currentRows: [],
      tableHeader: [
        {
          prop: 'questTemplateCode',
          // '调查模板编码'
          label: this.$t('quest.questTemplateCode'),
          showType: 'button',
          btnStyle: 'text',
          callback: function (row) {
            this.editTab('view', row)
          }.bind(this)
        },
        {
          prop: 'questTemplateName',
          // '调查模板名称'
          label: this.$t('dataConfMod.questTemplateName'),
          minWidth: 150
        },
        {
          prop: 'questTemplateType',
          // '调查模板类型'
          label: this.$t('vendorMod.questTemplateType'),
          minWidth: 120,
          dataType: 'dict',
          code: 'QUEST_TEMPLATE_TYPE'
        },
        {
          prop: 'questTemplateRemark',
          // '备注'
          label: this.$t('components.eio.headers.remark')
        },
        {
          prop: 'questTemplateStatus',
          // '状态'
          label: this.$t('components.stratProcess.headers.docStatusValue'),
          minWidth: 100,
          formattor: (val) => {
            if (val === 'Y') {
              // '生效'
              return this.$t('common.active')
            } else if (val === 'N') {
              // '未生效'
              return this.$t('cusEntry.supplement20250211.uneffective')
            } else if (val === 'D') {
              // '拟定'
              return this.$t('vendorMod.DRAFT')
            } else {
              return val
            }
          }
        },
        {
          prop: 'createdFullName',
          // '创建人'
          label: this.$t('common.creator'),
          minWidth: 90
        },
        {
          prop: 'creationDate',
          // '创建时间'
          label: this.$t('common.creationTime'),
          minWidth: 100,
          dataType: 'dateTime'
        },
        {
          prop: 'operation',
          // '操作'
          label: this.$t('components.headers.operation'),
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          width: 200,
          buttons: [
            {
              callback: row => this.editTab('edit', row),
              formattor: () => {
                // '模板定义'
                return this.$t('route.logisticsTemplate')
              },
              show: row => row.questTemplateStatus === 'D',
              code: 'sup:questTemplate:edit'
            },
            {
              callback: row => this.templateStatus('Y', row),
              formattor: () => {
                // '生效'
                return this.$t('common.active')
              },
              show: row => row.questTemplateStatus === 'N' || row.questTemplateStatus === 'D',
              code: 'sup:questTemplate:valid'
            },

            {
              callback: row => this.templateStatus('N', row),
              formattor: () => {
                // '失效'
                return this.$t('common.inactive')
              },
              show: row => row.questTemplateStatus === 'Y',
              code: 'sup:questTemplate:invalid'
            },

            {
              callback: row => this.editTab('add', row),
              formattor: () => {
                // '复制新增'
                return this.$t('cusEntry.supplement20250211.copyNew')
              },
              show: _ => true,
              code: 'sup:questTemplate:copyAdd'
            }
          ]
        }
      ],
      filterConfig: [
        { prop: 'questTemplateName', label: this.$t('dataConfMod.questTemplateName') },  // '调查模板名称'
        // '调查模板类型'
        { prop: 'questTemplateType', label: this.$t('vendorMod.questTemplateType'), type: 'dict', code: 'QUEST_TEMPLATE_TYPE' },
        { prop: 'createdFullName', label: this.$t('common.creator') },  // '创建人'
        // '状态'
        { prop: 'questTemplateStatus', 
        label: this.$t('components.stratProcess.headers.docStatusValue'), 
        type: 'select', 
        options: [{ value: 'Y', label: this.$t('common.active') }, 
        { value: 'N', label: this.$t('cusEntry.supplement20250211.uneffective') }, 
        { value: 'D', label: this.$t('vendorMod.DRAFT') }] }
      ],
      queryParam: {}
    }
  },
  created () {
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQueryData()
    })
  },
  methods: {
    rowClassFunc ({ row, rowIndex }) {
      if (row.questTemplateStatus === 'N') {
        return 'red-bg'
      }
    },
    getQueryData (params) {
      this.queryParam = params
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    syncFilterParams (values) {
      this.queryParam = values
    },
    templateStatus (status, row) {
      let updateData = {
        questTemplateId: row.questTemplateId,
        questTemplateStatus: status
      }
      questTemplate.modify(updateData).then(res => {
        this.getQueryData()
      }).catch(err => {
        console.log(err)
      })
    },
    // 编辑tab
    editTab (type, row) {
      let tab = {}
      if (type === 'add') {
        // 新增
        tab = {
          component: questTemplateDetail,
          params: {
            flag: type,
            row: row,
            tabName: 'questTemplateDetail'
          },
          title: () => this.$t('route.questTemplateAdd'), // '调查表模板定义新增',
          name: 'questTemplateDetail'
        }
      } else {
        let questTemplateCode = row.questTemplateCode
        // 查看和修改
        tab = {
          component: questTemplateDetail,
          params: {
            flag: type,
            row: row,
            tabName: 'questTemplateDetail' + questTemplateCode
          },
          title: questTemplateCode,
          name: 'questTemplateDetail' + questTemplateCode
        }
      }
      this.$emit('tab-add', tab)
    },
    handleCurrentChange (val) {
      this.currentRows = val
    }
  }
}
</script>
<style lang="scss">
.red-bg td{
  background-color: #FCBEA6 !important;
}
</style>
