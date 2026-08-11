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

          <!--自定义函数配置-->
          <el-button @click="openDetailTab('fun')">
            {{ $t('quoteTemplate.fun.btnTitle') }}
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
import quoteAttrDetail from './quoteAttrDetail.vue'
import quoteFunConfig from './quoteFunConfig.vue'

export default {
  name: 'QuoteAttrList',

  components: {
    TableView,
    MainHeader,
    FormWrapper
  },

  mixins: [tabTodoWatch, tabTodoMixin],

  data () {
    return {
      tableViewUrl: quoteBuyerHttp.attr.listPageUrl,
      tableHeader: [
        // 属性编码
        {
          prop: 'attrNo',
          label: this.$t('quoteTemplate.attrNo'),
          minWidth: 150,
          showType: 'button',
          btnStyle: 'text',
          callback: row => this.openDetailTab('view', row)
        },
        // 属性名称
        {
          prop: 'attrName',
          label: this.$t('quoteTemplate.attrName'),
          minWidth: 180
        },
        // 状态
        {
          prop: 'attrStatus',
          label: this.$t('common.status'),
          minWidth: 100,
          dataType: 'dict',
          code: 'SOU_QUOTE_TEMP_ATTR_STATUS'
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
              show: row => row.attrStatus === 'DRAFT',
              callback: row => this.openDetailTab('edit', row),
              formattor: () => this.$t('common.edit')
            },
            // 删除
            {
              // 拟定
              show: row => row.attrStatus === 'DRAFT',
              callback: row => this.deleteRow(row),
              formattor: () => this.$t('common.delete')
            },
            // 生效
            {
              // 拟定, 失效
              show: row => ['DRAFT', 'INVALID'].includes(row.attrStatus),
              callback: row => this.operationRow('valid', row),
              formattor: () => this.$t('common.active')
            },
            // 失效
            {
              // 生效
              show: row => row.attrStatus === 'VALID',
              callback: row => this.operationRow('invalid', row),
              formattor: () => this.$t('common.inactive')
            },
            // 复制
            {
              // 生效
              show: row => ['VALID', 'INVALID'].includes(row.attrStatus),
              callback: row => this.operationRow('copy', row),
              formattor: () => this.$t('common.copy')
            }
          ]
        }
      ],
      tableData: [],
      searchFormConfig: [
        // 属性编码
        { prop: 'attrNo', label: this.$t('quoteTemplate.attrNo') },
        // 属性名称
        { prop: 'attrName', label: this.$t('quoteTemplate.attrName') },
        // 更新日期范围
        {
          prop: 'dateList',
          label: this.$t('quoteTemplate.updateDateRange'),
          type: 'daterange'
        },
        // 状态
        {
          prop: 'attrStatus',
          label: this.$t('common.status'),
          type: 'dict',
          code: 'SOU_QUOTE_TEMP_ATTR_STATUS'
        },
        // 创建人
        {
          prop: 'createdBy',
          label: this.$t('common.creator'),
          type: 'quicksearch',
          propKey: 'username',
          showKey: 'nickname',
          name: 'scc_rbac_user_display'
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

      const response = await quoteBuyerHttp.attr.delete(row.attrId)
      if (response) {
        this.$message.success(this.$t('common.successDelete'))
        this.getQueryData()
      }
    },

    /* 生效 / 失效 / 复制 */
    async operationRow (type, row) {
      const response = await quoteBuyerHttp.attr[type](row.attrId)
      if (response) {
        if (type === 'copy') {
          // 复制 跳转详情页
          this.openDetailTab('edit', response.data)
        }
        this.$message.success(this.$t('common.success'))
        this.getQueryData()
      }
    },

    /* 新增add，查看view，编辑edit */
    openDetailTab (type, row) {
      let tab = {
        component: quoteAttrDetail,
        params: {
          flag: type,
          tabName: 'quoteAttrDetail'
        },
        title: '',
        name: 'quoteAttrDetail'
      }
      if (type === 'add') {
        // 新增
        tab.title = this.$t('quoteTemplate.addQuoteAttr')
      } else if (type === 'fun') {
        // 自定义函数配置
        tab = {
          component: quoteFunConfig,
          params: {
            ...tab.params,
            tabName: 'quoteFunConfig'
          },
          title: this.$t('quoteTemplate.fun.tabTitle'),
          name: 'quoteFunConfig'
        }
      } else {
        // 编辑 查看
        tab.params = {
          ...tab.params,
          row,
          tabName: `quoteAttrDetail${row.attrNo}`
        }
        tab.title = row.attrNo
        tab.name = tab.params.tabName
      }
      this.$emit('tab-add', tab)
    }
  }
}
</script>
