<template>
  <el-container
    class="flex-container the_dictionary_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        :pre-form-obj="preFormObj"
        @getFormData="getQuerydata"
      />
      <MainHeader>
        <template slot="left">
          <AuthorityButton code="bid:sourcingPublicityConfig:add" type="primary" @click="editTab('add')">
            {{ $t('common.add') }}
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-pj/source/pubconfig/queryPage"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import sourcingPublicityConfigDetail from './detail'

export default {
  name: 'SourcingPublicityConfigList',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      pageSize: 15,
      gridId: 'list',
      queryParam: {},
      tableHeader: [],
      tableData: [],
      preFormObj: {},
      queryForm: []
    }
  },
  created () {
    let _this = this
    _this.queryForm = [
      {
        prop: 'pubconfigName',
        label: () => this.$t('cusEntry.biddingSettings.pubconfigName') // 寻源公示模板名称
      },
      {
        prop: 'status',
        label: () => this.$t('perfMod.templateStatus'), // '模板状态'
        type: 'dict',
        code: 'SOURCE_PUBCONFIG_STATUS'
      }
    ]
    _this.tableHeader = [
      {
        prop: 'pubconfigName',
        label: () => this.$t('cusEntry.biddingSettings.pubconfigName'), // 寻源公示模板名称
        minWidth: 200,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.editTab('view', row)
      },
      {
        prop: 'status',
        label: () => this.$t('perfMod.templateStatus'), // '模板状态'
        minWidth: 120,
        dataType: 'dict', // 数据类型为字典
        code: 'SOURCE_PUBCONFIG_STATUS' // 字典code
      },
      {
        prop: 'createdFullName',
        label: () => this.$t('common.creator'), // '创建人'
        minWidth: 120
      },
      {
        prop: 'creationDate',
        label: () => this.$t('common.creationDate'), // '创建日期'
        minWidth: 120
      },
      {
        prop: 'operation',
        label: () => this.$t('common.operation'), // 操作
        width: 100,
        btnStyle: 'text',
        fixed: 'right',
        showType: 'buttons',
        buttons: [
          {
            callback: row => this.editTab('edit', row),
            code: 'bid:sourcingPublicityConfig:edit',
            formattor (val) {
              return _this.$t('common.edit')
            },
            show: row => ['DRAFT', 'INVALID'].includes(row.status)
          },
          {
            callback: row => this.deleteOneItem(row),
            code: 'bid:sourcingPublicityConfig:delete',
            formattor (val) {
              return _this.$t('common.delete')
            },
            show: row => row.status === 'DRAFT'
          },
          {
            callback: row => this.handleFailure(row),
            code: 'bid:sourcingPublicityConfig:invalid',
            formattor (val) {
              return _this.$t('common.inactive') // 失效
            },
            show: row => row.status === 'VALID'
          }
        ]
      }
    ]
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    getQuerydata (v) {
      this.queryParam = v || {}
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    // 编辑
    editTab (type, row) {
      let tab = {}
      if (type === 'add') {
        tab = {
          component: sourcingPublicityConfigDetail,
          params: {
            flag: type,
            row: row,
            tabName: 'sourcingPublicityConfigDetail'
          },
          title: this.$t('meeting.addTemplate'),
          name: 'sourcingPublicityConfigDetail'
        }
      } else if (type === 'view') {
        tab = {
          component: sourcingPublicityConfigDetail,
          params: {
            flag: type,
            row: row,
            tabName: 'sourcingPublicityConfigDetail' + row.pubconfigId
          },
          title: row.pubconfigName,
          name: 'sourcingPublicityConfigDetail' + row.pubconfigId
        }
      } else if (type === 'edit') {
        tab = {
          component: sourcingPublicityConfigDetail,
          params: {
            flag: type,
            row: row,
            tabName: 'sourcingPublicityConfigDetail' + row.pubconfigId
          },
          title: this.$t('cusEntry.common.editTemplate') + '-' + row.pubconfigName,
          name: 'sourcingPublicityConfigDetail' + row.pubconfigId
        }
      }
      this.$emit('tab-add', tab)
    },
    // 删除数据
    deleteOneItem (row) {
      this.$confirm(this.$t('common.delRow'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$http({
          url: '/api-pj/source/pubconfig/delPubconfigSingle',
          method: 'GET',
          params: { pubconfigId: row.pubconfigId },
          loading: true
        }).then(res => {
          this.$message.success(res.message)
          this.getQuerydata()
        })
      })
    },
    // 失效
    handleFailure (row) {
      this.$confirm(this.$t('cusEntry.common.confirmInvalidate'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$http({
          url: '/api-pj/source/pubconfig/invalidPubconfigSingle',
          method: 'GET',
          params: { pubconfigId: row.pubconfigId },
          loading: true
        }).then(res => {
          this.$message.success(res.message)
          this.getQuerydata()
        })
      })
    }
  }
}
</script>
<style scoped lang="scss"></style>
