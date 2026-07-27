<template>
  <el-container
    class="flex-container the_dictionary_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        :pre-form-obj="preFormObj"
        @getFormData="getQueryData"
      />
      <MainHeader>
        <template slot="left">
          <AuthorityButton code="rm:recruitment:add" type="primary" @click="editTab('add')">
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
        :adeptMeiQl="true"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-sou/api-ql/Recruit/query"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import { transformMQL } from 'lib@/utils/util'
import recruitmentDetail from './detail'

export default {
  name: 'RecruitmentList',
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
  computed: {
    username () {
      return this.$store.getters.userInfo.username || ''
    }
  },
  created () {
    let _this = this
    _this.queryForm = [
      {
        prop: 'name',
        label: () => this.$t('cusEntry.recruitment.recruitName')
      },
      {
        prop: 'status',
        label: () => this.$t('bidMod.billstatus'),
        type: 'dict',
        code: 'RECRUIT_STATUS'
      },
      {
        prop: 'createdUserName',
        label: () => this.$t('common.creator')
      },
      {
        prop: 'creationDate',
        label: () => this.$t('common.creationTime'),
        type: 'daterange'
      },
      {
        prop: 'categoryName',
        label: () => this.$t('common.category')
      }
    ]
    _this.tableHeader = [
      {
        prop: 'name',
        label: () => this.$t('cusEntry.recruitment.recruitName'),
        minWidth: 200,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.editTab('view', row)
      },
      {
        prop: 'title',
        label: () => this.$t('cusEntry.recruitment.subtitle'),
        minWidth: 150
      },
      {
        prop: 'status',
        label: () => this.$t('common.status'),
        minWidth: 120,
        dataType: 'dict',
        code: 'RECRUIT_STATUS'
      },
      {
        prop: 'deadlineTime',
        label: () => this.$t('cusEntry.recruitment.stopTime'),
        formattor: val => this.$dayjsParse(val),
        minWidth: 120
      },
      {
        prop: 'createdUserName',
        label: () => this.$t('common.creator'),
        minWidth: 140
      },
      {
        prop: 'creationDate',
        label: () => this.$t('common.creationTime'),
        formattor: val => this.$dayjsParse(val),
        minWidth: 120
      },
      {
        prop: 'operation',
        label: () => this.$t('common.operation'),
        width: 120,
        btnStyle: 'text',
        fixed: 'right',
        showType: 'buttons',
        buttons: [
          {
            callback: row => this.editTab('approval', row),
            formattor (val) {
              return _this.$t('common.approve')
            },
            // 审批中
            show: row => ['APPROVING'].includes(row.status) && row.startBpmUsername === this.username
          },
          {
            callback: row => this.editTab('edit', row),
            code: 'rm:recruitment:edit',
            formattor (val) {
              return _this.$t('common.edit')
            },
            show: row => ['DRAFT', 'REJECTED', 'WITHDRAW'].includes(row.status)
          },
          {
            callback: row => this.deleteOneItem(row),
            code: 'rm:recruitment:delete',
            formattor (val) {
              return _this.$t('common.delete')
            },
            show: row => row.status === 'DRAFT'
          },
          {
            callback: row => this.abandonOneItem(row),
            code: 'rm:recruitment:abandon',
            formattor (val) {
              return _this.$t('common.abandon')
            },
            show: row => ['REJECTED', 'WITHDRAW'].includes(row.status)
          }
        ]
      }
    ]
    this.$nextTick(() => {
      this.getQueryData()
    })
  },
  methods: {
    getQueryData (v) {
      let params = v || {}
      this.queryParam = transformMQL.listPageData({
        type: 'Recruit',
        action: 'query',
        params,
        filter: {
          name: {
            contains: params.name
          },
          status: {
            contains: params.status
          },
          $or: {
            createdBy: {
              contains: params.createdUserName
            },
            createdFullName: {
              contains: params.createdUserName
            }
          },
          creationDate: {
            between: params.creationDate
          },
          categoryName: {
            contains: params.categoryName
          }
        }
      })
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    editTab (type, row) {
      let tab = {}
      if (type === 'add') {
        tab = {
          component: recruitmentDetail,
          params: {
            flag: type,
            row: row,
            tabName: 'recruitmentDetail'
          },
          title: this.$t('cusEntry.recruitment.addRecruit'),
          name: 'recruitmentDetail'
        }
      } else {
        tab = {
          component: recruitmentDetail,
          params: {
            flag: type,
            row: row,
            tabName: 'recruitmentDetail'
          },
          title: row.name,
          name: 'recruitmentDetail' + row.recruitId
        }
      }
      this.$emit('tab-add', tab)
    },
    // 删除数据
    deleteOneItem (row) {
      this.$confirm(this.$t('cusEntry.common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        const params = transformMQL.save(
          'Recruit',
          [{
            'recruitId': row.recruitId,
            'contentList': [{ $delete: '*' }],
            'vendorList': [{ $delete: '*' }]
          }],
          'delete'
        )
        this.$http({
          url: '/api-sou/api-ql/Recruit/delete',
          method: 'POST',
          data: params,
          loading: true
        }).then(res => {
          this.$message.success(this.$t('common.success'))
          this.getQueryData()
        })
      })
    },
    // 废弃
    abandonOneItem (row) {
      this.$confirm(this.$t('perfMod.confirmAbandonment'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        const params = transformMQL.save('Recruit', [{ recruitId: row.recruitId }], 'abandon')
        this.$http({
          url: '/api-sou/api-ql/Recruit/abandon',
          method: 'POST',
          data: params,
          loading: true
        }).then(res => {
          this.$message.success(this.$t('common.success'))
          this.getQueryData()
        })
      })
    }
  }
}
</script>
<style scoped lang="scss"></style>
