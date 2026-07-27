<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <FormWrapper :form-array="searchFormConfig" @getFormData="getQueryData" />
      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <!-- <el-button type="primary" @click="openDetailTab('add')">
            {{ $t("common.add") }}
          </el-button> -->
          <QuickSearch
            ref="creatBidQuickSearch"
            showButton
            class="quickBtn"
            btnTitle="创建招标流程"
            name="REQQIRE_TO_SOU_PROJECT"
            multiSelect
            @close-quicksearch="(val) => createProjectSou(val)"
          />
        </template>
      </MainHeader>

      <TableView
        ref="biddingListTable"
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        open-custom-table
        :com-active="$attrs['changeTab']"
        url="/api-sou/ext/buyer/bid/init/page"
      />
      <SrmDialog
        :visible.sync="showUserVisible"
        :close-on-click-modal="false"
        size="small"
        :title="$t('cusEntry.tipMessage.transferTitle')"
      >
        <el-form
          ref="transferForm"
          :model="transferInfo"
        >
          <el-form-item
            :label="$t('cusEntry.biddingSettings.transfer')"
            prop="userName"
            :rules="{
              required: true,
              message: $t('cusEntry.tipMessage.transferMsg')
            }"
          >
            <QuickSearch
              :show-input="transferInfo.fullName"
              show-key="nickname"
              name="scc_rbac_user_display"
              @close-quicksearch="getTransferUser"
            />
          </el-form-item>
        </el-form>
        <div slot="footer">
          <el-button
            type="primary"
            @click="confirm"
          >
            {{ $t('common.confirm') }}
          </el-button>
          <el-button
            @click="showUserVisible = false"
          >
            {{ $t('common.cancel') }}
          </el-button>
        </div>
      </SrmDialog>
    </el-main>
  </el-container>
</template>

<script>
import { bidBuyerHttp } from 'modcb@/biddingBuyer/api'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import { daterangePayloadFormat } from 'lib@/composition/commonComposition'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import biddingDetail from './biddingDetail'
import QuickSearch from '@/library/components/QuickSearch'
import { transformMQL } from '@/library/utils/util'
import { planPool } from 'modc@/buyer/purchasingDemand/api'

export default {
  name: 'BiddingList',

  components: {
    TableView,
    MainHeader,
    FormWrapper,
    QuickSearch
  },

  mixins: [tabTodoWatch, tabTodoMixin],

  data () {
    return {
      searchFormConfig: [
        // 项目名称
        { prop: 'souName', label: this.$t('bidMod.bidingName') },
        // 项目编号
        { prop: 'extProjectNo', label: this.$t('bidMod.bidingNum') },
        // 招标状态
        {
          prop: 'projectStatus',
          label: this.$t('cusEntry.biddingSettings.projectStatus'),
          type: 'dict',
          code: 'SOU_BIDDING_PRO_STATUS'
        },
        { prop: 'createdFullName', label: this.$t('common.creator') },
        {
          prop: 'creationDate',
          label: this.$t('common.creationDate'),
          type: 'daterange'

        },
        {
          prop: 'publishTime',
          label: this.$t('bidMod.publishDate'),
          type: 'daterange'
        },
        // 招标单号
        { prop: 'souNo', label: this.$t('cusEntry.biddingSettings.bidingNum') },
        // 审批状态
        {
          prop: 'createApprovalStatus',
          label: this.$t('common.approvalStatus'),
          type: 'dict',
          code: 'SOU_APPROVAL_STATUS'
        }
      ],
      tableHeader: [
        // 招标单号
        {
          prop: 'souNo',
          label: this.$t('cusEntry.biddingSettings.bidingNum'),
          minWidth: 150
        },
        // 招标项目编号
        {
          prop: 'extProjectNo',
          label: this.$t('bidMod.bidingNumCla'),
          minWidth: 150
        },
        // 项目名称
        {
          prop: 'souName',
          label: this.$t('bidMod.bidingName'),
          minWidth: 150
        },
        // 公司
        {
          prop: 'extOrgOuName',
          label: this.$t('cusEntry.common.company'),
          minWidth: 150
        },
        // 评分规则
        {
          prop: 'extScoreRule',
          label: this.$t('bidMod.evaluateMethod'),
          minWidth: 120,
          dataType: 'dict',
          code: 'SOU_BID_SCORE_RULE'
        },
        // 招标状态
        {
          prop: 'projectStatus',
          label: this.$t('cusEntry.biddingSettings.projectStatus'),
          minWidth: 100,
          dataType: 'dict',
          code: 'SOU_BIDDING_PRO_STATUS'
        },
        // 审批状态
        {
          prop: 'createApprovalStatus',
          label: this.$t('common.approvalStatus'),
          minWidth: 100,
          dataType: 'dict',
          code: 'SOU_APPROVAL_STATUS'
        },
        // 招标流程
        {
          prop: 'extSouProcess',
          label: this.$t('cusEntry.biddingSettings.bidProcess'),
          minWidth: 120,
          dataType: 'dict',
          code: 'SOU_BID_PROCCESS'
        },
        // 创建人
        {
          prop: 'createdUserName',
          label: this.$t('common.creator'),
          minWidth: 150
        },
        // 创建日期
        {
          prop: 'creationDate',
          label: this.$t('common.creationDate'),
          minWidth: 120,
          formattor: val => this.$dayjsParse(val)
        },
        // 发布日期
        {
          prop: 'publishTime',
          label: this.$t('bidMod.publishDate'),
          minWidth: 120,
          formattor: val => this.$dayjsParse(val)
        },
        {
          prop: 'partCancle',
          label: '是否部分取消',
          dataType: 'dict',
          code: 'YES_OR_NO',
          minWidth: 120
        },
        {
          prop: 'operation',
          label: this.$t('bidMod.operation'),
          width: 120,
          showType: 'buttons',
          fixed: 'right',
          btnStyle: 'text',
          buttons: [
            // 管理
            // 审批状态：已提交、已审批
            {
              show: row => ['SUBMITTED', 'APPROVED'].includes(row.createApprovalStatus),
              formattor: () => this.$t('bidMod.management'),
              callback: row => this.openDetailTab('edit', row)
            },
            // 编辑
            {
              show: row => row.projectStatus === 'DRAW_UP' && ['DRAFT', 'REJECTED', 'WITHDRAW'].includes(row.createApprovalStatus),
              formattor: () => this.$t('common.edit'),
              callback: row => this.openDetailTab('edit', row)
            },
            // 查看
            {
              show: row => row.projectStatus === 'ABANDON' || row.createApprovalStatus === 'ABANDON',
              formattor: () => this.$t('common.view'),
              callback: row => this.openDetailTab('view', row)
            },
            // 删除
            {
              show: row => row.projectStatus === 'DRAW_UP' && row.createApprovalStatus === 'DRAFT',
              formattor: () => this.$t('common.delete'),
              callback: row => this.deleteRow(row)
            },
            // 转办
            {
              formattor: () => this.$t('cusEntry.common.transfer'),
              callback: row => this.transferHandler(row),
              code: 'bid:manage:transfer'
            }
          ]
        }
      ],
      status: false,
      tableData: [],
      queryParam: {},
      showUserVisible: false,
      transferInfo: {
        userName: ''
      },
      transferRow: {}
    }
  },
  watch: {
    '$route.params': {
      // 当前功能已经打开的时候监听是否是从其他功能跳转过来的
      deep: true,
      immediate: true,
      handler (nVal) {
        const { from, funName, row, taskIndex } = nVal
        if (from === 'demandPoolManagementZhaobiao' && funName === 'bid') {
          this.openDetailTab('edit', row)
        } else if (from == 'fromFun' && funName == 'biddingManagementNew') {
          let rowObj = { projectId: row.formId, extProjectNo: row.formNo, formTab: row?.formTab || '', extSouProces: row.extSouProcess }
          if (taskIndex === 1) { // 待办 // 首页待办跳转至指定tab页 formTab || 打开招标流程创建快查弹窗
            if (row.formTab) {
              this.openDetailTab('edit', rowObj)
            } else {
              this.$nextTick(() => {
                this.$refs.creatBidQuickSearch.openDialog('click')
              })
            }
          } else if (taskIndex === 2) { // 已办
            this.openDetailTab('edit', rowObj)
          }
        }
      }
    }
  },
  created () {
    this.$nextTick(() => {
      this.getQueryData()
    })
  },

  methods: {
    // 转办
    transferHandler (row) {
      this.showUserVisible = true
      this.transferRow = row
    },
    // 获取转办人员信息
    getTransferUser (value) {
      this.transferInfo.userName = value ? value.username : ''
      this.transferInfo.userId = value ? value.userId : null
      this.transferInfo.fullName = value ? value.nickname : ''
    },
    // 确认
    confirm () {
      this.$refs.transferForm.validate(valid => {
        if (valid) {
          const data = {
            projectId: this.transferRow.projectId,
            ...this.transferInfo
          }
          bidBuyerHttp.init.transfer(data).then(res => {
            if (res.data) {
              this.$message.success(this.$t('cusEntry.tipMessage.transferSuccess'))
              this.showUserVisible = false
              this.getQueryData(this.queryParam)
            }
          })
        }
      })
    },
    // 取消
    createProjectSou (data) {
      if (!data || !data.length) return this.$message.warning('请勾选列表')
      for (let i = 0; i < data.length; i++) {
        for (let j = i + 1; j < data.length; j++) {
          if (data[i].souNo !== data[j].souNo) {
            return this.$message.warning('推荐供应商单号相同，才能一起创建')
          }
        }
      }
      let reqHeadList = data.map(item => ({
        requirementHeadId: item.requirementHeadId
      }))
      let params = {
        souType: 'bid',
        reqHeadList
      }
      let transformParams = transformMQL.save('PrSouRequirementPoolForBuyer', [params], 'createSou')
      planPool.createSou(transformParams).then((datas) => {
        console.log('datas:', datas)
        let result = datas.data.records || []
        if (result.length) {
          let row = {
            projectId: result[0].souVO.project.projectId,
            souNo: result[0].souVO.project.souNo,
            extSouProces: result[0].souVO.project.extSouProces
          }
          this.openDetailTab('edit', row)
        }
      })
    },
    /* 查询 */
    getQueryData (payload) {
      if (payload) {
        // 格式化时间范围
        payload = daterangePayloadFormat(payload, [
          // 创建时间
          { prop: 'creationDate', fromProp: 'creationDateFrom', toProp: 'creationDateTo' },
          // 发布时间
          { prop: 'publishTime', fromProp: 'publishTimeFrom', toProp: 'publishTimeTo' }
        ])
      }

      this.queryParam = payload || this.queryParam
      this.$nextTick(() => {
        this.$refs.biddingListTable.query()
      })
    },
    /* 删除行 */
    async deleteRow (row) {
      const confirmResult = await this.$confirm(this.$t('common.delRow'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => { /* nothing */ })

      if (confirmResult !== 'confirm') {
        return
      }

      const response = await bidBuyerHttp.init.remove(row.projectId)
      if (response) {
        this.$message.success(this.$t('common.successDelete'))
        this.getQueryData()
      }
    },

    /* 打开新增 or 编辑tab */
    openDetailTab (type, row = {}) {
      let tab = {}
      if (type === 'add') {
        tab = {
          component: biddingDetail,
          params: { flag: type, tabName: 'biddingDetail' },
          // 创建项目
          title: this.$t('bidMod.createProject'),
          name: 'biddingDetail'
        }
      } else {
        tab = {
          component: biddingDetail,
          params: {
            flag: type,
            row: row,
            tabName: `biddingDetail${row.extProjectNo}`
          },
          title: row.extProjectNo || row.souNo,
          name: `biddingDetail${row.extProjectNo}`
        }
      }
      this.$emit('tab-add', tab)
    }
  }
}
</script>
<style lang="scss" scoped>
.quickBtn {
  display:inline-block !important;
  vertical-align: middle;
  margin-right: 8px !important;
}
</style>
