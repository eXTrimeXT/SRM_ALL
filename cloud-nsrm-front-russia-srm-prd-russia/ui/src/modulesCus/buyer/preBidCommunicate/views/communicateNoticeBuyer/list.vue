<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main>
      <FormWrapper :form-array="searchFormConfig" @getFormData="getQueryData" />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <AuthorityButton code="communicateNoticeBuyer:add" type="primary" @click="editTab('add',{})">
            创建标前交流通知
          </AuthorityButton>
        </template>
      </MainHeader>

      <TableView
        ref="list"
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :com-active="$attrs['changeTab']"
        :checkbox="false"
        :checkChange="handleCurrentChange"
        open-custom-table
        :url="tableViewUrl"
        :adeptMeiQl="true"
      />
    </el-main>
  </el-container>
</template>

<script>
import { tabTodoWatch } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import CommunicateNoticeBuyerDetail from './edit'
import { commuNoticeBuyerHttp } from 'modcb@/preBidCommunicate/api'
import { transformMQL, transformTimeQuery } from 'lib@/utils/util'
import { mapGetters } from 'vuex'

export default {
  name: 'CommunicateNoticeBuyerList',

  components: {
    TableView,
    MainHeader,
    FormWrapper,
    MImport
  },

  mixins: [tabTodoWatch],

  data () {
    return {
      tableViewUrl: commuNoticeBuyerHttp.listPageUrl,
      tableHeader: [],
      tableData: [],
      searchFormConfig: [
        {
          prop: 'bidNoticeNo',
          label: '交流通知单号'
        },
        {
          prop: 'projectName',
          label: '项目名称'
        },
        {
          prop: 'status',
          label: '单据状态',
          type: 'dict',
          code: 'PRE_BID_NOTICE_STATUS'
        },
        {
          prop: 'createdFullName',
          label: '创建人'
        },
        {
          prop: 'creationDate',
          label: '创建日期',
          type: 'daterange'
        }
      ],
      queryParam: {},
      selectedRows: [] // 标记勾选行
    }
  },

  computed: {
    ...mapGetters(['userInfo'])
  },

  watch: {
    '$route.params': {
      // 当前功能已经打开的时候监听是否是从其他功能跳转过来的
      handler (nVal) {
        const { from, funName, row, taskIndex } = nVal
        if (from === 'fromFun' && funName === 'communicateNoticeBuyer') {
          let rowObj = { bidNoticeId: row.formId, bidNoticeNo: row.formNo }
          if (taskIndex === 1) { // 待办 // row.formType : 'add'、'edit'
            this.editTab(row.formType, row.formType === 'add' ? {} : rowObj)
          } else if (taskIndex === 2) { // 已办
            this.editTab('view', rowObj)
          }
        }
      },
      immediate: true,
      deep: true
    }
  },

  mounted () {
    console.log('userInfo', this.userInfo)
    this.tableHeader = [
      {
        prop: 'bidNoticeNo',
        label: '交流通知单号',
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: (row) => {
          console.log('row:::', row)
          this.editTab('view', row)
        }
      },
      {
        prop: 'orgBuName',
        label: '板块',
        minWidth: 130
      },
      {
        prop: 'orgName',
        label: '公司',
        minWidth: 130
      },
      {
        prop: 'projectName',
        label: '项目名称',
        minWidth: 120
      },
      {
        prop: 'vendorUserNickname',
        label: '供应商负责人',
        minWidth: 120
      },
      {
        prop: 'bidUserNickname',
        label: '招标负责人',
        minWidth: 120
      },
      {
        prop: 'status',
        label: '单据状态',
        dataType: 'dict',
        code: 'PRE_BID_NOTICE_STATUS',
        minWidth: 120
      },
      {
        prop: 'createdFullName',
        label: '创建人'
      },
      {
        prop: 'creationDate',
        label: '创建日期',
        minWidth: 130
      },
      {
        prop: 'operation',
        label: this.$t('bidMod.operation'),
        minWidth: 150,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          /**
           * 1.创建人可以编辑、删除
           * 2.供应商负责人、需求人可编辑
           * 3.拟定状态可以编辑、删除
           */
          // 拟定可以编辑
          {
            show: row => ['DRAFT'].includes(row.status) && (this.userInfo.username === row.createdBy || this.userInfo.nickname === row.vendorUserNickname || this.userInfo.nickname === row.demandUserNickname),
            formattor: () => this.$t('common.edit'),
            code: 'communicateNoticeBuyer:edit',
            callback: row => {
              this.editTab('edit', row)
            }
          },
          // 拟定可以删除
          {
            show: row => ['DRAFT'].includes(row.status) && this.userInfo.username === row.createdBy,
            formattor: () => this.$t('common.delete'),
            code: 'communicateNoticeBuyer:delete',
            callback: row => {
              this.deleteRows(row)
            }
          }
        ]
      }
    ]
    this.$nextTick(() => {
      this.getQueryData()
    })
  },

  methods: {
    /* 查询列表数据 */
    getQueryData (params = {}) {
      transformTimeQuery(['creationDate'], params)
      this.queryParam = transformMQL.listPageData({
        type: 'PreBidNoticeBuyer',
        action: 'query',
        params,
        filterOperator: {
          creationDate: 'between'
        }
      })
      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },

    editTab (type, row) {
      const map = new Map([
        // 新增
        [
          'add',
          {
            component: CommunicateNoticeBuyerDetail,
            params: {
              flag: type,
              row,
              tabName: 'communicateNoticeBuyer'
            },
            title: '标前交流通知',
            name: 'communicateNoticeBuyer'
          }
        ],
        // 编辑
        [
          'edit',
          {
            component: CommunicateNoticeBuyerDetail,
            params: {
              flag: type,
              row,
              tabName: row.bidNoticeNo
            },
            title: '标前交流通知' + (row.bidNoticeNo || ''),
            name: row.bidNoticeNo
          }
        ],
        // 查看
        [
          'view',
          {
            component: CommunicateNoticeBuyerDetail,
            params: {
              flag: type,
              row,
              tabName: row.bidNoticeNo
            },
            title: '标前交流通知' + (row.bidNoticeNo || ''),
            name: row.bidNoticeNo
          }
        ]
      ])
      this.$emit('tab-add', map.get(type))
    },

    /* 选中行 */
    handleCurrentChange (val) {
      console.log('val:::', val)
      this.selectedRows = val
    },

    /** 删除 */
    async deleteRows (row) {
      const confirmResult = await this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => {})
      if (confirmResult !== 'confirm') return
      let transformParams = transformMQL.save('PreBidNoticeBuyer', [row.bidNoticeId], 'delete')
      const response = await commuNoticeBuyerHttp.delete(transformParams)
      if (response) {
        this.$message.success(this.$t('common.successDelete'))
        this.getQueryData()
      }
    }
  }
}
</script>
