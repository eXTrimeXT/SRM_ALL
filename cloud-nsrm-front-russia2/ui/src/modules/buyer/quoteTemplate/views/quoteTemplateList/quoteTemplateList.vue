<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <FormWrapper :form-array="searchFormConfig" @getFormData="getQueryData" />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <!--新增-->
          <el-button
            type="primary"
            @click="openDetailTab('add')"
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
        :url="tableViewUrl"
      />
    </el-main>
  </el-container>
</template>

<script>
import { quoteBuyerHttp } from 'modb@/quoteTemplate/api'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import quoteTemplateDetail from './quoteTemplateDetail.vue'

export default {
  name: 'QuoteTemplateList',

  components: {
    TableView,
    MainHeader,
    FormWrapper
  },

  mixins: [tabTodoWatch, tabTodoMixin],

  data () {
    return {
      tableViewUrl: quoteBuyerHttp.template.listPageUrl,
      tableHeader: [
        // 模板编码
        {
          prop: 'tempNo',
          label: this.$t('quoteTemplate.tempNo'),
          minWidth: 150,
          showType: 'button',
          btnStyle: 'text',
          callback: row => this.openDetailTab('view', row)
        },
        // 模板名称
        {
          prop: 'tempName',
          label: this.$t('quoteTemplate.tempName'),
          minWidth: 180
        },
        // 状态
        {
          prop: 'tempStatus',
          label: this.$t('common.status'),
          minWidth: 100,
          dataType: 'dict',
          code: 'SOU_QUOTE_TEMP_STATUS'
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
              show: row => row.tempStatus === 'DRAFT',
              callback: row => this.openDetailTab('edit', row),
              formattor: () => this.$t('common.edit')
            },
            // 删除
            {
              // 拟定
              show: row => row.tempStatus === 'DRAFT',
              callback: row => this.deleteRow(row),
              formattor: () => this.$t('common.delete')
            },
            // 生效
            {
              // 拟定,失效
              show: row => ['DRAFT', 'INVALID'].includes(row.tempStatus),
              callback: row => this.operationRow('valid', row),
              formattor: () => this.$t('common.active')
            },
            // 失效
            {
              // 生效
              show: row => row.tempStatus === 'VALID',
              callback: row => this.operationRow('invalid', row),
              formattor: () => this.$t('common.inactive')
            }
          ]
        }
      ],
      tableData: [],
      searchFormConfig: [
        // 模板编码
        { prop: 'tempNo', label: this.$t('quoteTemplate.tempNo') },
        // 模板名称
        { prop: 'tempName', label: this.$t('quoteTemplate.tempName') },
        // 状态
        {
          prop: 'tempStatus',
          label: this.$t('common.status'),
          type: 'dict',
          code: 'SOU_QUOTE_TEMP_STATUS'
        },
        // 更新日期范围
        {
          prop: 'dateList',
          label: this.$t('quoteTemplate.updateDateRange'),
          type: 'daterange'
        }
      ],
      queryParam: {}
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
      if (val && val.dateList) {
        // 设置日期筛选范围
        val.lastUpdateDateFrom = val.dateList[0]
        val.lastUpdateDateTo = val.dateList[1]
      } else if (val && !val.dateList) {
        delete val.lastUpdateDateFrom
        delete val.lastUpdateDateTo
      }

      this.queryParam = Object.assign({}, val)

      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },

    /* 删除 */
    async deleteRow (row) {
      const confirmResult = await this.$confirm(this.$t('common.delRow'), {
        confirmButtonText: this.$t('common.affirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => { /* nothing */ })

      if (confirmResult !== 'confirm') {
        return
      }

      const response = await quoteBuyerHttp.template.delete(row.tempId)
      if (response) {
        this.$message.success(this.$t('common.successDelete'))
        this.getQueryData()
      }
    },

    /* 生效 / 失效 */
    async operationRow (type, row) {
      const response = await quoteBuyerHttp.template[type](row.tempId)
      if (response) {
        this.$message.success(this.$t('common.success'))
        this.getQueryData()
      }
    },

    /* 新增add，查看view，编辑edit */
    openDetailTab (type, row) {
      let tab = {
        component: quoteTemplateDetail,
        params: {
          flag: type,
          tabName: 'quoteTemplateDetail'
        },
        title: '',
        name: 'quoteTemplateDetail'
      }
      if (type === 'add') {
        // 新增
        tab.title = this.$t('quoteTemplate.addQuoteTemplate')
      } else {
        // 编辑 查看
        tab.params = {
          ...tab.params,
          row,
          tabName: `quoteTemplateDetail${row.tempNo}`
        }
        tab.title = row.tempNo
        tab.name = tab.params.tabName
      }
      this.$emit('tab-add', tab)
    }
  }
}
</script>
