<template>
  <el-container class="flex-container the_usersAccessList_wrapper" direction="vertical">
    <el-main>
      <FormWrapper :form-array="preArr" @getFormData="getQuerydata" />
      <MainHeader :l-span="23" :r-span="1">
        <template slot="left">
          <ExportExcel
            page-url="/api-rbac/user/listUserOrganizationByParam"
            export-mode="front"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            :filter-params="queryParams"
            :title="$t('components.eio.customExport')"
            code="usersAccessOrg:export"
            :fileName="$t('dataConfMod.usersAccessOrgExport')"
            type="default"
          />
        </template>
      </MainHeader>

      <TableView
        :ref="gridId"
        :pre-query-data="queryParams"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :com-active="$attrs['changeTab']"
        url="/api-rbac/user/listUserOrganizationByParam"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { parseTime } from '@/utils'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import ExportExcel from 'lib@/components/export-excel'

export default {
  name: 'OrgAccessList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      queryParams: {},
      pageSize: 15,
      gridId: 'orgAccessList',
      currentRow: null,
      tableHeader: [],
      tableData: [],
      dictCodes: {
        userType: 'USER_TYPE'
      },
      preArr: [
        {
          prop: 'userType',
          label: () => this.$t('dataConfMod.userType'), // "账号类型"
          type: 'dict', // 字典类型
          code: 'USER_TYPE'
        },
        { prop: 'username', label: () => this.$t('dataConfMod.userID') }, // "账号"
        { prop: 'nickname', label: () => this.$t('dataConfMod.userName') }, // "姓名"
        {
          prop: 'organizationTypeCode',
          label: () => this.$t('dataConfMod.orgType'), // "组织类型"
          type: 'custom-dict',
          code: 'ORG_TYPE_ALL',
          customSelectType: 'ORG_TYPE_ALL'
        },
        {
          prop: 'organizationCode',
          label: () => this.$t('common.orgCode') // 组织编码
        },
        {
          prop: 'organizationName',
          label: () => this.$t('dataConfMod.orgName') // 组织名称
        }
      ]
    }
  },
  created () {
    var _this = this
    this.tableHeader = [
      {
        prop: 'organizationTypeName',
        width: 120,
        align: 'center',
        label: () => this.$t('dataConfMod.orgType') // "组织类型"
        // code: 'ORG_TYPE_ALL',
        // customSelectType: 'ORG_TYPE_ALL'
      },
      {
        prop: 'organizationName',
        label: () => this.$t('dataConfMod.orgName'), // 组织名称
        align: 'center'
      },
      {
        prop: 'organizationCode',
        label: () => this.$t('common.orgCode'), // 组织编码
        align: 'center'
      },
      {
        prop: 'userType',
        width: 120,
        align: 'center',
        label: () => this.$t('dataConfMod.userType'), // "账号类型"
        dataType: 'dict',
        code: 'USER_TYPE'
      },
      {
        prop: 'username',
        label: () => this.$t('dataConfMod.userID'), // 账号
        align: 'center'
      },
      {
        prop: 'nickname',
        label: () => this.$t('dataConfMod.userName'), // 姓名
        width: 150,
        align: 'center'
      },
      {
        prop: 'startDate',
        align: 'center',
        label: () => this.$t('vendorMod.startDate'), // "生效日期"
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'endDate',
        align: 'center',
        label: () => this.$t('dataConfMod.endDate'), // "失效日期"
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      }
    ]
  },
  mounted () {
    this.$refs[this.gridId].query()
  },
  methods: {
    handleSuccess () {
      this.getQuerydata()
    },
    getQuerydata (v) {
      this.queryParams = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      this.currentRow = val
    }
  }
}
</script>
