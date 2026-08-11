<template>
  <el-container
    class="flex-container the_usersAccessList_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        @getFormData="getQuerydata"
      />
      <MainHeader>
        <template slot="left">
          <AuthorityButton
            type="primary"
            code="flow:workflowSetting:add"
            @click="editTab('add')"
          >
            <!-- 新增 -->
            {{ $t("common.add") }}
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
        :com-active="$attrs['changeTab']"
        url="/api-base/flow/processTemplent/listPage"
      />
    </el-main>
  </el-container>
</template>

<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { parseTime } from '@/utils'
import workflowTempInfo from './workflowTempInfo'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import { workflowSetting } from 'modb@/basicSetting/api/basicSetting'

export default {
  name: 'WorkflowList',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      pageSize: 15,
      gridId: 'workflowList',
      selectList: [],
      currentRow: null,
      tableHeader: [],
      dialogFormVisible: false,
      formLabelWidth: '100px',
      isActive: false,
      preArr: [
        {
          prop: 'businessName',
          label: () => this.$t('dataConfMod.businessName') // "业务名称"
        },
        {
          prop: 'templateCode',
          label: () => this.$t('dataConfMod.templateCode') // "模板编码"
        },
        { prop: 'modelId', label: () => this.$t('dataConfMod.modeId') } // 模型ID
      ],
      tableData: [],
      templateNames: [],
      queryParam: {},
      integrationMode: [
        { value: 'Product', label: this.$t('cusEntry.supplement20250211.productWorkflowIntegrationMode') },  //'产品工作流集成模式'
        { value: 'IdeFlow', label: this.$t('cusEntry.supplement20250211.ideWorkflowIntegrationMode') },  // 'IDE工作流集成模式'
        { value: 'Iframe', label: this.$t('cusEntry.supplement20250211.iframeEmbeddedPageMode') },  // 'iframe嵌入页面模式'
        { value: 'Self', label: this.$t('cusEntry.supplement20250211.selfPageMode') },  // '自带页面模式'
        { value: 'Push', label: this.$t('cusEntry.supplement20250211.noPagePushMode') },  // '无页面推送模式'
        { value: 'IdeSdk', label: this.$t('cusEntry.supplement20250211.ideSdkPushMode') },  // 'IdeSdk推送模式'
        { value: 'None', label: this.$t('cusEntry.supplement20250211.noWorkflowMode') }  // '无工作流模式'
      ]
    }
  },
  created () {
    this.fatchDictData() // 字典
    let _this = this
    this.tableHeader = [
      {
        prop: 'businessName',
        label: () => this.$t('dataConfMod.businessName'), // "模板名称"
        formattor (val) {
          return _this.$getDictLabelByValue
            ? _this.$getDictLabelByValue(_this.templateNames, val)
            : val
        }
      },
      {
        prop: 'templateCode',
        label: () => this.$t('dataConfMod.templateCode'), // '模板编码'
        width: 150
      },
      {
        prop: 'enableFlag',
        label: () => this.$t('dataConfMod.enabledUse'), // '是否启用'
        align: 'center',
        showType: 'switch',
        switchValues: {
          inactive: 'N',
          active: 'Y'
        },
        callback: function (row) {
          this.enableHandel(row)
        }.bind(this),
        formattor: function (val) {
          return val === 'Y' ? 'Y' : 'N'
        }
      },
      {
        prop: 'integrationMode',
        label: () => this.$t('dataConfMod.integrationMode'), // '模板编码'
        width: 150
      },
      {
        prop: 'lastUpdateDate',
        label: () => this.$t('common.updateTime'), // '更新时间'
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'lastUpdatedUserName', // lastUpdatedBy
        label: () => this.$t('common.updatePeople')
      }, // "更新人"
      {
        prop: 'operation',
        label: () => this.$t('common.operation'), // "操作"
        width: 80,
        btnStyle: 'text',
        fixed: 'right',
        showType: 'buttons',
        buttons: [
          {
            callback: function (row) {
              this.editTab('edit', row)
            }.bind(this),
            code: 'flow:workflowSetting:edit',
            formattor () {
              return _this.$t('common.edit') // "编辑";
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
    // 获取数据字典
    fatchDictData () {
      // 查询魔板名称
      workflowSetting.queryEnablePermission().then(res => {
        if (res) {
          let arr = []
          res.data.map(item => {
            arr.push({
              label: item.functionName,
              value: item.functionId
            })
          })
          this.templateNames = arr
          this.preArr[0].options = this.templateNames
        }
      })
    },
    getQuerydata (v, isReset = true) {
      if (isReset) {
        this.queryParam = v
      }
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    // 编辑tab
    editTab (type, row) {
      let tab = {}
      if (type === 'add') {
        // 新增
        tab = {
          component: workflowTempInfo,
          params: { flag: 'add' },
          title: () => this.$tabsAddFormTitle, // this.$t('dataConfMod.addProcessTemplate'), // "新增流程模板"
          name: 'workflowInfo'
        }
      } else {
        // 修改
        tab = {
          component: workflowTempInfo,
          params: {
            flag: 'edit',
            row: row,
            templateHeadId: row.templateHeadId
          },
          title: row.templateCode, // "编辑流程模板"
          name: 'workflowInfo' + row.templateCode
        }
      }
      this.$emit('tab-add', tab)
    },
    // 是否启用方法
    enableHandel (row) {
      let parame = {}
      parame.templateHeadId = row.templateHeadId
      parame.enableFlag = row.enableFlag
      workflowSetting.processTempUpdateEnable(parame).then(res => {
        this.getQuerydata() // 查询数据
        this.$message({
          message: res.message,
          type: 'success'
        })
      })
    },
    handleCurrentChange (val) {
      this.currentRow = val
    }
  }
}
</script>
