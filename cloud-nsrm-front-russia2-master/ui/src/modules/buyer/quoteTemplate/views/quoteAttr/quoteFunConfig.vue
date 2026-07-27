<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <FormWrapper :form-array="searchFormConfig" @getFormData="getQueryData" />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <!--新增-->
          <el-button
            type="primary"
            size="mini"
            @click="openDetailDialog('add', null)"
          >
            {{ $t('common.add') }}
          </el-button>
        </template>
      </MainHeader>

      <TableView
        ref="list"
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :com-active="$attrs['changeTab']"
        open-custom-table
        custom-table-key="quoteFunConfig"
        :url="tableViewUrl"
      />

      <!--新增编辑查看弹窗-->
      <DetailDialog
        v-if="detailDialogVisible"
        :visible.sync="detailDialogVisible"
        :flag="detailDialogFlag"
        :edit-row="editRow"
        @submit-success="getQueryData"
      />
    </el-main>
  </el-container>
</template>

<script>
import { quoteBuyerHttp } from 'modb@/quoteTemplate/api'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import DetailDialog from './quoteFunConfig/detailDialog.vue'

export default {
  name: 'QuoteFunConfig',

  components: {
    TableView,
    MainHeader,
    FormWrapper,
    DetailDialog
  },

  data () {
    return {
      tableViewUrl: quoteBuyerHttp.fun.listPageUrl,
      tableHeader: [
        // 函数名称
        {
          prop: 'apiName',
          label: this.$t('quoteTemplate.fun.name'),
          minWidth: 150,
          showType: 'button',
          btnStyle: 'text',
          callback: row => this.openDetailDialog('view', row)
        },
        // 函数类型
        {
          prop: 'apiType',
          label: this.$t('quoteTemplate.fun.type'),
          minWidth: 180
        },
        // 状态
        {
          prop: 'apiStatus',
          label: this.$t('common.status'),
          minWidth: 100,
          dataType: 'dict',
          code: 'SOU_QUOTE_TEMP_API_STATUS'
        },
        // 创建人
        {
          prop: 'createdUserName',
          label: this.$t('common.creator'),
          minWidth: 100
        },
        // 创建时间
        {
          prop: 'creationDate',
          label: this.$t('common.creationTime'),
          minWidth: 100,
          formattor: val => this.$parseTime(val)
        },
        // 最后更新人
        {
          prop: 'lastUpdatedFullName',
          label: this.$t('common.lastUpdatePeople'),
          minWidth: 120
        },
        // 最后更新日期
        {
          prop: 'lastUpdateDate',
          label: this.$t('common.lastUpdateDate2'),
          minWidth: 120,
          formattor: val => this.$parseTime(val)
        },
        {
          prop: 'operation',
          label: this.$t('common.operation'),
          minWidth: 160,
          showType: 'buttons',
          fixed: 'right',
          btnStyle: 'text',
          buttons: [
            // 编辑
            {
              // 拟定
              show: row => row.apiStatus === 'DRAFT',
              callback: row => this.openDetailDialog('edit', row),
              formattor: () => this.$t('common.edit')
            },
            // API测试
            {
              // 生效
              show: row => row.apiStatus === 'VALID',
              callback: row => this.openDetailDialog('test', row),
              formattor: () => this.$t('quoteTemplate.fun.apiTest')
            }
          ]
        }
      ],
      tableData: [],
      searchFormConfig: [
        // 函数名称
        { prop: 'apiName', label: this.$t('quoteTemplate.fun.name') },
        // 状态
        {
          prop: 'apiStatus',
          label: this.$t('common.status'),
          type: 'dict',
          code: 'SOU_QUOTE_TEMP_API_STATUS'
        }
      ],
      queryParam: {},
      detailDialogVisible: false,
      detailDialogFlag: 'add',
      editRow: null
    }
  },

  mounted () {
    this.$nextTick(() => {
      this.getQueryData()
    })
  },

  methods: {
    /* 查询列表数据 */
    getQueryData (val) {
      this.queryParam = Object.assign({}, val)

      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },

    /* 新增 / 编辑 / 查看 */
    openDetailDialog (type, row) {
      this.editRow = row
      this.detailDialogFlag = type
      this.detailDialogVisible = true
    }
  }
}
</script>
