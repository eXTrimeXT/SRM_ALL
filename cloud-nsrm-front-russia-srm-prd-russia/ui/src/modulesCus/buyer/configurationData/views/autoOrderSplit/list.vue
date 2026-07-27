<template>
  <el-container
    class="flex-container the_vendorPurchaseOrderList_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        form-label-width="100px"
        @getFormData="getQuerydata"
      />

      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <!-- 新增 -->
          <AuthorityButton
            type="primary"
            @click="handelAdd"
          >
            {{ $t('orderMod.buyerOrderSynergy.add') }}
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :bigData="true"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :auto-query="false"
        :url="tableUrl"
        :open-custom-table="true"
        :reserve-selection="true"
        :adeptMeiQl="true"
        row-key="orderNumber"
        :comActive="$attrs['changeTab']"
      />
    </el-main>
  </el-container>
</template>
<script>
import { parseTime } from '@/utils'
import { tabTodoMixin, tabTodoWatch } from '@/utils/mixins'
import ExportExcel from 'lib@/components/export-excel'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import TableView from 'lib@/components/Table/TableView'
import { transformMQL } from 'lib@/utils/util'
import OrganizationSelector from 'lib@/components/organization-selector'
import Treeselect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import { newOrganaztionTreehttp } from '@/api/common'
import detail from './detail'
export default {
  name: 'AutoOrderSplitList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel,
    OrganizationSelector,
    Treeselect
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      integrationMode: '',
      gridId: 'list',
      currentRows: null,
      tableUrl: '/api-sup-ce/api-ql/PrPushConfig/query',
      pageSize: 15,
      preArr: [
        {
          prop: 'orgName',
          label: '管理单元名称'
        }
      ],
      tableHeader: [
        {
          prop: 'orgCode',
          label: '管理单元编码',
          width: 120
        },
        {
          prop: 'orgName',
          label: '管理单元名称',
          width: 150
        },
        {
          prop: 'status',
          label: '状态',
          dataType: 'dict',
          code: 'RECENT_STATUS',
          width: 120
        },
        {
          prop: 'pushDate',
          label: '周期',
          showType: 'dict',
          code: 'WEEK',
          width: 120,
          formattor: val => this.getLabel(val ? val.split(',') : [])
        },
        {
          prop: 'pushTime',
          label: '定时器',
          width: 120
        },
        {
          prop: 'creatorOrgName',
          label: '创建组织',
          width: 120
        },
        {
          prop: 'creationDate',
          label: '创建时间',
          width: 150,
          formattor: val => (val ? parseTime(val, '{y}-{m}-{d} {h}:{i}:{s}') : '')
        },
        {
          prop: 'operation',
          label: () => this.$t('common.operation'),
          width: 150,
          btnStyle: 'text',
          fixed: 'right',
          showType: 'buttons',
          buttons: [
            {
              callback: row => this.handelEdit(row),
              formattor: () => this.$t('common.edit')
            },
            {
              callback: row => this.handelDel(row),
              formattor: () => this.$t('common.delete')
            }
          ]
        }
      ],
      queryParam: {}
    }
  },
  created () {
    this.getQuerydata()
  },

  methods: {
    getLabel (array) {
      let str = ''
      let index = array.length
      for (let item of array) {
        str += this.$getDictLabel('WEEK', item)
        --index
        if (index != 0) {
          str += ','
        }
      }
      return str
    },
    getQuerydata (params = {}) {
      this.queryParam = transformMQL.listPageData({
        type: 'PrPushConfig',
        action: 'query',
        params
      })
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    async getFlowIntegrationMode () {
      let res = await this.$api.base.flowAPI.getFlowIntegrationMode({ businessType: 'ORDER' })
      if (res.data) {
        this.integrationMode = res.data
      }
    },
    handelDel (item) {
      this.$confirm('此操作将永久删除该自动分单任务, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        let params = transformMQL.save('PrPushConfig', [item.configId], 'delete')
        this.$http({
          url: '/api-sup-ce/api-ql/PrPushConfig/delete',
          method: 'POST',
          data: params,
          loading: true
        }).then(res => {
          this.getQuerydata()
        })
      })
    },
    handelAdd () {
      this.$emit('tab-add', {
        component: detail,
        params: {
          flag: 'add',
          tabName: 'detail'
        },
        title: '新增自动分单任务',
        name: 'detail'
      })
    },
    handelEdit (item) {
      this.$emit('tab-add', {
        component: detail,
        params: {
          flag: 'edit',
          tabName: 'detail',
          row: item
        },
        title: '编辑自动分单任务',
        name: 'detail'
      })
    }
  }
}
</script>
<style scoped lang="scss">
.el-input-group__append >div{
    display: flex;
    justify-content: center;
}
</style>
