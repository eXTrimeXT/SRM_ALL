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
          label: '调查模板编码',
          showType: 'button',
          btnStyle: 'text',
          callback: function (row) {
            this.editTab('view', row)
          }.bind(this)
        },
        {
          prop: 'questTemplateName',
          label: '调查模板名称',
          minWidth: 150
        },
        {
          prop: 'questTemplateType',
          label: '调查模板类型',
          minWidth: 120,
          dataType: 'dict',
          code: 'QUEST_TEMPLATE_TYPE'
        },
        {
          prop: 'questTemplateRemark',
          label: '备注'
        },
        {
          prop: 'questTemplateStatus',
          label: '状态',
          minWidth: 100,
          formattor: (val) => {
            if (val === 'Y') {
              return '生效'
            } else if (val === 'N') {
              return '未生效'
            } else if (val === 'D') {
              return '拟定'
            } else {
              return val
            }
          }
        },
        {
          prop: 'createdFullName',
          label: '创建人',
          minWidth: 90
        },
        {
          prop: 'creationDate',
          label: '创建时间',
          minWidth: 100
        },
        {
          prop: 'operation',
          label: '操作',
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          width: 200,
          buttons: [
            {
              callback: row => this.editTab('edit', row),
              formattor: () => {
                return '模板定义'
              },
              show: row => row.questTemplateStatus === 'D',
              code: 'sup:questTemplate:edit'
            },
            {
              callback: row => this.templateStatus('Y', row),
              formattor: () => {
                return '生效'
              },
              show: row => row.questTemplateStatus === 'N' || row.questTemplateStatus === 'D',
              code: 'sup:questTemplate:valid'
            },

            {
              callback: row => this.templateStatus('N', row),
              formattor: () => {
                return '失效'
              },
              show: row => row.questTemplateStatus === 'Y',
              code: 'sup:questTemplate:invalid'
            },

            {
              callback: row => this.editTab('add', row),
              formattor: () => {
                return '复制新增'
              },
              show: _ => true,
              code: 'sup:questTemplate:copyAdd'
            }
          ]
        }
      ],
      filterConfig: [
        { prop: 'questTemplateName', label: '调查模板名称' },
        { prop: 'questTemplateType', label: '调查模板类型', type: 'dict', code: 'QUEST_TEMPLATE_TYPE' },
        { prop: 'createdFullName', label: '创建人' },
        { prop: 'questTemplateStatus', label: '状态', type: 'select', options: [{ value: 'Y', label: '生效' }, { value: 'N', label: '未生效' }, { value: 'D', label: '拟定' }] }
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
