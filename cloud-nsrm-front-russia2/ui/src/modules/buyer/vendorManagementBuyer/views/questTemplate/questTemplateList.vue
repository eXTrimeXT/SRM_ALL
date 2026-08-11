<template>
  <el-container
    class="flex-container questtemplate_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="filterConfig"
        @getFormData="getQueryData"
        @synchronous-value="syncFilterParams"
      />
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            type="primary"
            @click="editTab('add')"
          >
            {{
              $t('common.add')
            }}
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :checkbox="true"
        :comActive="$attrs['changeTab']"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        url="/api-sup/quest/questTemplate/listPageByParm"
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
      yesOrNo: [],
      questTemplateTypeList: [],
      tableHeader: [
        {
          prop: 'questTemplateCode',
          label: this.$t('quest.questTemplateCode'),
          showType: 'button',
          btnStyle: 'text',
          width: 150,
          callback: function (row) {
            this.editTab('view', row)
          }.bind(this)
        },
        {
          prop: 'questTemplateName',
          label: this.$t('dataConfMod.questTemplateName'),
          minWidth: 150
        },
        {
          prop: 'questTemplateType',
          label: this.$t('vendorMod.questTemplateType'),
          minWidth: 120,
          dataType: 'dict', // 数据类型为字典
          code: 'QUEST_TEMPLATE_TYPE' // 字典code
        },
        {
          prop: 'questTemplateRemark',
          label: this.$t('dataConfMod.remark')
        },
        {
          prop: 'questTemplateStatus',
          label: this.$t('dataConfMod.triggerState'),
          minWidth: 100,
          formattor: (val) => {
            if (val) {
              return val === 'Y' ? this.$t('dataConfMod.valid') : this.$t('common.inactive') // yes no
            }
          }
        },
        {
          prop: 'operation',
          label: this.$t('common.operation'),
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          width: 200,
          buttons: [
            {
              callback: (row) => this.editTab('edit', row),
              formattor: () => {
                return this.$t('route.logisticsTemplate')
              },
              show: (row) => row.questTemplateStatus === 'N'
            },
            {
              callback: (row) => this.templateStatus('Y', row),
              formattor: () => {
                return this.$t('dataConfMod.valid')
              },
              show: (row) => row.questTemplateStatus === 'N'
            },
            {
              callback: (row) => this.templateStatus('N', row),
              formattor: () => {
                return this.$t('common.inactive')
              },
              show: (row) => row.questTemplateStatus === 'Y'
            }
          ]
        }
      ],

      filterConfig: [
        { prop: 'questTemplateName', label: this.$t('dataConfMod.questTemplateName') },
        {
          prop: 'questTemplateType',
          label: this.$t('vendorMod.questTemplateType'),
          type: 'dict', // 字典类型
          code: 'QUEST_TEMPLATE_TYPE' // 字典code
        }
      ],
      queryParam: {}
    }
  },
  activated () {
    this.$refs[this.gridId].doLayout()
  },
  created () {
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQueryData()
    })
  },
  methods: {
    dolayout () {
      this.$refs[this.gridId].doLayout()
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
      // this.$set(this.obj, 'b', 'obj.b')
      let updateData = {
        questTemplateId: row.questTemplateId,
        questTemplateStatus: status
      }
      this.$http({
        url: '/api-sup/quest/questTemplate/modify',
        method: 'POST',
        data: updateData,
        loading: true
      })
        .then((res) => {
          this.getQueryData()
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
