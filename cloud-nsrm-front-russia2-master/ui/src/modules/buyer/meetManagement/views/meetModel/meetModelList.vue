<template>
  <el-container class="flex-container mouldheader_list_wrapper" direction="vertical">
    <el-main>
      <FormWrapper :form-array="searchFormConfig" @getFormData="getQueryData" />

      <MainHeader :l-span="22" :r-span="2">
        <template #left>
          <!--新增模板-->
          <el-button type="primary" @click="openDetailTab('add')">
            {{ $t('meeting.addTemplate') }}
          </el-button>
        </template>
      </MainHeader>

      <TableView
        ref="list"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :url="meetModelApi.listPageUrl"
        :com-active="$attrs['changeTab']"
      />
    </el-main>
  </el-container>
</template>

<script>
import { tabTodoMixin, tabTodoWatch } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MeetModelDetail from './meetModelDetail'
import { meetModelApi } from 'modb@/meetManagement/api'

export default {
  name: 'MeetModelList',

  components: {
    TableView,
    MainHeader,
    FormWrapper
  },

  mixins: [tabTodoWatch, tabTodoMixin],

  data () {
    return {
      meetModelApi: meetModelApi,
      tableHeader: [],
      searchFormConfig: [
        // 上会类型
        {
          prop: 'topicType',
          label: this.$t('meeting.topicType'),
          type: 'dict',
          code: 'MEET_TYPE'
        },
        // 创建人
        { prop: 'createdFullName', label: this.$t('common.creator') },
        // 创建时间
        {
          prop: 'creationDate',
          label: this.$t('common.creationTime'),
          type: 'daterange'
        },
        // 状态
        {
          prop: 'status',
          label: this.$t('common.status'),
          type: 'dict',
          code: 'MEET_MODEL_STATUS'
        },
        // 议题模板名称
        { prop: 'modelName', label: this.$t('meeting.topicModelName') }
      ],
      queryParam: {}
    }
  },

  created () {
    this.tableHeader = [
      // 上会类型
      {
        prop: 'topicType',
        label: this.$t('meeting.topicType'),
        minWidth: 100,
        dataType: 'dict',
        code: 'MEET_TYPE'
      },
      // 议题模板编码
      {
        prop: 'modelCode',
        label: this.$t('meeting.topicModelCode'),
        minWidth: 120,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.openDetailTab('view', row)
      },
      // 议题模板名称
      {
        prop: 'modelName',
        label: this.$t('meeting.topicModelName'),
        minWidth: 120
      },
      // 状态
      {
        prop: 'status',
        label: this.$t('common.status'),
        minWidth: 100,
        dataType: 'dict',
        code: 'MEET_MODEL_STATUS'
      },
      // 创建人
      {
        prop: 'createdFullName',
        label: this.$t('common.creator'),
        minWidth: 100
      },
      // 创建时间
      {
        prop: 'creationDate',
        label: this.$t('common.creationTime'),
        minWidth: 100,
        dataType: 'dateTime'
      },
      // 更新人
      {
        prop: 'lastUpdatedFullName',
        label: this.$t('common.lastUpdatedFullName'),
        minWidth: 100
      },
      // 更新日期
      {
        prop: 'lastUpdateDate',
        label: this.$t('common.lastUpdateDate'),
        minWidth: 100,
        dataType: 'dateTime'
      },
      {
        prop: 'operation',
        label: this.$t('common.operation'),
        showType: 'buttons',
        btnStyle: 'text',
        fixed: 'right',
        width: 130,
        buttons: [
          // 禁用
          {
            code: 'meet:model:invalid',
            show: row => row.status === 'Y',
            formattor: () => this.$t('common.disable'),
            callback: row => this.disabledOrEnable('disabled', row.modelId)
          },
          // 启用
          {
            code: 'meet:model:valid',
            show: row => row.status === 'N',
            formattor: () => this.$t('common.enable'),
            callback: row => this.disabledOrEnable('enable', row.modelId)
          },
          // 编辑
          {
            code: 'meet:model:edit',
            show: row => row.status === 'N',
            formattor: () => this.$t('common.edit'),
            callback: row => this.openDetailTab('edit', row)
          }
        ]
      }
    ]

    this.$nextTick(() => {
      this.getQueryData()
    })
  },

  methods: {
    /* 查询 */
    getQueryData (params) {
      this.queryParam = JSON.parse(JSON.stringify(params || {}))
      let { creationDate } = this.queryParam
      if (creationDate && creationDate.length) {
        this.queryParam.startDate = creationDate[0]
        this.queryParam.endDate = creationDate[1]
      }
      delete this.queryParam.creationDate
      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },

    /* 打开详情页 */
    openDetailTab (type, row) {
      let tabName = type === 'add' ? 'meetModelDetail' : row.modelCode
      const tab = {
        component: MeetModelDetail,
        params: {
          row,
          flag: type,
          tabName
        },
        title: type === 'add' ? this.$t('meeting.addTemplate') : this.$t('meeting.issueTemplate') + row.modelCode,
        name: tabName
      }
      this.$emit('tab-add', tab)
    },

    /* 禁用 / 启用 */
    async disabledOrEnable (type, modelId) {
      if (!modelId) {
        return
      }

      const response = await meetModelApi[type === 'disabled' ? 'disabledModel' : 'enableModel'](modelId)

      if (response) {
        this.$message.success(response.message)
        this.getQueryData()
      }
    }
  }
}
</script>
