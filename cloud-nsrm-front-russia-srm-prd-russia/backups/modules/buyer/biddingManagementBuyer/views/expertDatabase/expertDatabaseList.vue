<template>
  <el-container
    class="flex-container-notab the_expertDatabase_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="formWrapperConfig"
        @getFormData="getQueryData"
      />
      <MainHeader>
        <template slot="left">
          <!--新增-->
          <AuthorityButton
            code="bid:bidExpert:add"
            type="primary"
            @click="navTabsAdd('add')"
          >
            {{ $t('common.add') }}
          </AuthorityButton>
        </template>
      </MainHeader>

      <TableView
        ref="expertDatabaseTable"
        :table-data="tableData"
        :table-header="tableHeader"
        :page-size="15"
        :pre-query-data="queryParam"
        :com-active="$attrs['changeTab']"
        url="/api-bid/bidExpert/listPage"
      />
    </el-main>
  </el-container>
</template>

<script>
import { parseTimeYMD } from 'lib@/composition/origin/composition'
import { tabTodoMixin, tabTodoWatch } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import expertDatabaseDetail from './expertDatabaseDetail'

export default {
  name: 'ExpertDatabase',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      queryParam: {},
      tableHeader: [],
      tableData: [],
      formWrapperConfig: [
        // 专家姓名
        { prop: 'expertName', label: this.$t('bidMod.expertName') },
        // 专家岗位
        { prop: 'expertJob', label: this.$t('bidMod.expertType') },
        // 状态
        {
          prop: 'status',
          label: this.$t('common.status'),
          type: 'dict',
          code: 'BASE_INFO_STATUS'
        },
        // 创建人
        { prop: 'createdFullName', label: this.$t('common.creator') },
        // 创建时间
        {
          prop: 'dateList',
          label: this.$t('common.creationTime'),
          type: 'daterange'
        }
      ]
    }
  },
  created () {
    this.tableHeader = [
      // 专家姓名
      {
        prop: 'expertName',
        label: this.$t('bidMod.expertName'),
        minWidth: 130,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.navTabsAdd('view', row),
        formattor: val => val || '--'
      },
      // 系统账号
      {
        prop: 'expertJobName',
        label: this.$t('bidMod.expertType'),
        width: 110
      },
      // 系统账号
      {
        prop: 'expertAccount',
        label: this.$t('bidMod.expertAccount'),
        width: 130
      },
      // 职称专业
      {
        prop: 'profession',
        label: this.$t('dataConfMod.profession'),
        width: 150
      },
      // 专业工作年限
      {
        prop: 'workYears',
        label: this.$t('dataConfMod.workYears'),
        width: 150
      },
      // 状态
      {
        prop: 'status',
        label: this.$t('bidMod.status'),
        width: 100,
        formattor: val => this.$getDictLabel('BASE_INFO_STATUS', val)
      },
      // 登记日期
      {
        prop: 'startDate',
        label: this.$t('bidMod.startDate'),
        width: 110,
        formattor: val => parseTimeYMD(val)
      },
      // 失效日期
      {
        prop: 'endDate',
        label: this.$t('bidMod.endDate'),
        width: 110,
        formattor: val => parseTimeYMD(val)
      },
      // 创建人
      {
        prop: 'createdUserName',
        label: this.$t('common.creator'),
        width: 110
      },
      {
        prop: 'operation',
        label: this.$t('bidMod.operation'),
        width: 150,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          // 编辑
          {
            show: row => row.status === 'DRAFT',
            formattor: () => this.$t('common.edit'),
            callback: row => this.navTabsAdd('edit', row)
          },
          // 删除
          {
            show: row => row.status === 'DRAFT',
            formattor: () => this.$t('common.delete'),
            callback: row => this.deleteExpertDatabase(row)
          },
          // 生效
          {
            show: row => ['INVALID', 'DRAFT'].includes(row.status),
            formattor: () => this.$t('common.active'),
            callback: row => this.validExpertDatabase(row)
          },
          // 失效
          {
            show: row => row.status === 'VALID',
            formattor: () => this.$t('common.inactive'),
            callback: row => this.invalidExpertDatabase(row)
          }
        ]
      }
    ]

    this.$nextTick(() => {
      this.getQueryData()
    })
  },
  methods: {
    getQueryData (payload = {}) {
      if (payload && payload.dateList && Array.isArray(payload.dateList) && payload.dateList.length === 2) {
        // 编排创建时间范围
        payload = {
          ...payload,
          creationDateFrom: payload.dateList[0],
          creationDateTo: payload.dateList[1]
        }
      }

      this.queryParam = payload
      this.$nextTick(() => {
        this.$refs.expertDatabaseTable.query()
      })
    },

    /* 删除 */
    deleteExpertDatabase (row) {
      this.$confirm(this.$t('common.delRow'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$http({
          url: `api-bid/bidExpert/delete/${row.expertId}`,
          method: 'DELETE',
          loading: true
        }).then(() => {
          // 删除成功
          this.$message.success(this.$t('common.successDelete'))
          this.getQueryData()
        })
      })
    },

    /* 生效 */
    validExpertDatabase (row) {
      this.$confirm(this.$t('dataConfMod.confirmationValid'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$http({
          url: `/api-bid/bidExpert/valid/${row.expertId}`,
          method: 'POST',
          loading: true
        }).then(() => {
          // 失效成功
          this.$message.success(this.$t('dataConfMod.effectiveSuccessfully'))
          this.getQueryData()
        })
      })
    },

    /* 失效 */
    invalidExpertDatabase (row) {
      // 确认失效吗？
      this.$confirm(this.$t('bidMod.confirmInvalid'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$http({
          url: `/api-bid/bidExpert/invalid/${row.expertId}`,
          method: 'POST',
          loading: true
        }).then(() => {
          // 失效成功
          this.$message.success(this.$t('bidMod.failedSuccess'))
          this.getQueryData()
        })
      })
    },

    /* 打开 新增 / 编辑 / 查看 标签页 */
    navTabsAdd (type, row) {
      // 默认新增
      let tabObj = {
        component: expertDatabaseDetail,
        params: {
          flag: type
        },
        title: this.$t('common.add'),
        name: 'expertDatabaseDetail'
      }
      if (type !== 'add') {
        // 修改 / 查看
        tabObj = {
          ...tabObj,
          params: {
            ...tabObj.params,
            row: row,
            readOnly: type === 'view'
          },
          title: row.expertName,
          name: 'expertDatabaseDetail' + row.expertId
        }
      }
      this.$emit('tab-add', tabObj)
    }
  }
}
</script>
