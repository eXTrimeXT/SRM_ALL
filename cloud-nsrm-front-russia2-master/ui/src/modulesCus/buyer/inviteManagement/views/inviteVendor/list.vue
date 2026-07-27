<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main>
      <FormWrapper :form-array="searchFormConfig" @getFormData="getQueryData" />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <!-- <AuthorityButton code="inviteVendor:publicity" type="primary" @click="openInviteDialog('Y')">
            公示邀请供应商
          </AuthorityButton> -->

          <!-- <QuickSearch
            showButton
            class="quickBtn"
            btnTitle="公示邀请供应商"
            name="sou_req_head"
            @before-open="beforeOpen"
            @close-quicksearch="(val) => getPublicHeadNum(val)"
          /> -->
          <QuickSearch
            showButton
            class="quickBtn"
            :btnTitle="$t('cusEntry.supplement20250121.publiclyInviteSuppliers')"
            name="sou_req_head"
            @before-open="beforeOpen"
            @close-quicksearch="(val) => getPublicHeadNum(val)"
          />
          <!-- <QuickSearch
            showButton
            class="quickBtn"
            btnTitle="不公示邀请供应商"
            btnType="ghost"
            name="pr_requirement_head2"
            :pre-query-data="{
              'b.NEED_PUBLIC':'N'
            }"
            @before-open="beforeOpen"
            @close-quicksearch="(val) => getNotPublicHeadNum(val)"
          /> -->
          <QuickSearch
            showButton
            class="quickBtn"
            :btnTitle="$t('cusEntry.supplement20250121.notPubliclyInvitingSuppliers')"
            btnType="ghost"
            name="pr_requirement_head2"
            :pre-query-data="{
              'b.NEED_PUBLIC':'N'
            }"
            @before-open="beforeOpen"
            @close-quicksearch="(val) => getNotPublicHeadNum(val)"
          />
          <!-- <AuthorityButton code="inviteVendor:noPublicity" @click="openInviteDialog('N')">
            不公示邀请供应商
          </AuthorityButton> -->

          <!-- <AuthorityButton code="inviteVendor:viewHistory" @click="viewHistory">
            查看邀请历史
          </AuthorityButton> -->
          <AuthorityButton code="inviteVendor:viewHistory" @click="viewHistory">
             {{ $t("cusEntry.supplement20250121.viewInvitationHistory") }}

          </AuthorityButton>
          <!-- <AuthorityButton code="inviteVendor:recommend" @click="inteligentRecommend">
            智能推荐
          </AuthorityButton> -->
          <AuthorityButton code="inviteVendor:recommend" @click="inteligentRecommend">
            {{ $t("bidMod.smartRecommond") }}
          </AuthorityButton>
          <!-- 自定义导出 -->
          <ExportExcel
            :page-url="tableViewUrl"
            :filter-params="computedQueryParam"
            :table-header="tableHeader.filter(item => item.prop !== 'viewJoinHistory')"
            :dict-codes="dictCodes"
            code="inviteVendor:export"
            export-mode="front"
            type="default"
          />
        </template>
      </MainHeader>

      <TableView
        ref="list"
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :com-active="$attrs['changeTab']"
        :checkbox="true"
        :checkChange="handleCurrentChange"
        open-custom-table
        :url="tableViewUrl"
        :adeptMeiQl="true"
      />
    </el-main>

    <!-- 邀请供应商弹窗 -->
    <InviteDialog
      v-if="inviteDialogVisible"
      :visible.sync="inviteDialogVisible"
      :editRows="selectedRows"
      @confirm="inviteDialogConfirm"
    />

    <!-- 查看邀请历史 -->
    <InviteHistoryDialog
      v-if="inviteHisDialogVisible"
      :visible.sync="inviteHisDialogVisible"
    />

    <!-- 智能推荐 -->
    <RecommendDialog
      v-if="recommendDialogVisible"
      ref="recommendDialog"
      :visible.sync="recommendDialogVisible"
      @confirm="recommendDialogConfirm"
    />
  </el-container>
</template>

<script>
import { SOU_PROJECT_STATUS_ENUM } from 'lib@/composition/origin/enum'
import { tabTodoWatch } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import InviteDialog from './components/inviteDialog'
import InviteHistoryDialog from './components/inviteHistoryDialog'
import RecommendDialog from './components/recommendDialog'
import InviteVendorDetail from './edit'
import inviteHttp from '../../api'
import { transformMQL, transformTimeQuery } from 'lib@/utils/util'
import QuickSearch from '@/library/components/QuickSearch'
import ExportExcel from 'lib@/components/export-excel'

export default {
  name: 'InviteVendorList',

  components: {
    TableView,
    MainHeader,
    FormWrapper,
    InviteDialog,
    InviteHistoryDialog,
    RecommendDialog,
    QuickSearch,
    ExportExcel
  },

  mixins: [tabTodoWatch],

  data () {
    return {
      tableViewUrl: inviteHttp.irListPageUrl,
      tableHeader: [],
      tableData: [],
      searchFormConfig: [
        // 品类
        {
          prop: 'categoryName',
          // label: '品类',
          label: () => this.$t('common.category'),
          type: 'catSelect',
          showKey: 'categoryName',
          propKey: 'categoryId'
        },
        // 招标时间
        {
          prop: 'bidCreationDate',
          // label: '招标时间',
          label: () => this.$t('cusEntry.supplement20250121.biddingTime'),
          type: 'daterange'
        },
        // 品牌
        {
          prop: 'ceeaAgentBrand',
          // label: '品牌'
          label: () => this.$t('dataConfMod.band'),
        },
        // 注册资金从
        {
          prop: 'beginRegisteredCapital',
          // label: '注册资金从（卢布）'
          label: () => this.$t('cusEntry.supplement20250121.registeredCapitalFromRubles'),
        },
        // 注册资金至
        {
          prop: 'endRegisteredCapital',
          // label: '注册资金至（卢布）'
          label: () => this.$t('cusEntry.supplement20250121.registeredCapitalUpToRubles'),
        },
        // 成立年限从
        {
          prop: 'beginCompanyCreationYear',
          // label: '成立年限从'
          label: () => this.$t('cusEntry.supplement20250121.establishmentPeriodFrom'),
        },
        // 成立年限至
        {
          prop: 'endCompanyCreationYear',
          // label: '成立年限至' 
          label: () => this.$t('cusEntry.supplement20250121.establishmentPeriodTo'),
        },
        // 主要客户
        // {
        //   prop: 'mainCustomers',
        //   label: () => this.$t('sourcingBuyer.mainCustomer'),
        // },
        // 板块
        {
          prop: 'orgBuName',
          // label: '板块'
          label: () => this.$t('cusEntry.bidSuperviseReport.extOrgBuName'),
        },
        {
          prop: 'orgName',
          // label: '所属公司'
          label: () => this.$t('cusEntry.supplement20250121.affiliatedCompany')
          // type: 'OUorganizationSelector'
        },
        {
          prop: 'companyAddress',
          // label: '供应商地址'
          label: () => this.$t('cusEntry.supplement20250121.supplierAddress')
        },
        // {
        //   prop: 'pjQualifications',
        //   label: '资质'
        // },
        {
          prop: 'projectName',
          // label: '项目名称'
          label: () => this.$t('bidMod.bidingName')
        },
        {
          prop: 'vendorName',
          label: () => this.$t('common.vendorName'), // 供应商名称
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info_display'
        }
      ],
      queryParam: {},
      selectedRows: [], // 标记勾选行
      inviteDialogVisible: false,
      inviteHisDialogVisible: false,
      recommendDialogVisible: false,
      dictCodes: {
        isIntelligentBid: 'YES_OR_NO'
      }
    }
  },

  computed: {
    computedQueryParam () {
      let { pageNum, pageSize } = this.queryParam
      return {
        meiqlPayload: {
          ...this.queryParam
        },
        pageNum,
        pageSize
      }
    }
  },

  mounted () {
    this.tableHeader = [
      // 智能推荐中标
      {
        prop: 'isIntelligentBid',
        // label: '智能推荐中标',
        label: () => this.$t('cusEntry.supplement20250121.intelligentRecommendationForWinningTheBid'),
        minWidth: 120,
        dataType: 'dict',
        code: 'YES_OR_NO'
      },
      // 供应商名称
      {
        prop: 'vendorName',
        // label: '供应商名称',
        label: () => this.$t('common.companyName'),
        minWidth: 150
      },
      // 联系人
      {
        prop: 'contactName',
        // label: '联系人',
        label: () => this.$t('vendorMod.contactPerson'),
        minWidth: 120
      },
      // 电话
      {
        prop: 'phone',
        // label: '电话',
        label:  () => this.$t('common.phone'),
        minWidth: 120
      },
      // 邮箱
      {
        prop: 'email',
        // label: '邮箱',
        label: () => this.$t('common.email'),
        minWidth: 120
      },
      // {
      //   prop: 'pjQualifications',
      //   label: '资质',
      //   minWidth: 120
      // },
      // {
      //   prop: 'smartRecomm',
      //   label: '是否智能推荐',
      //   minWidth: 150
      // },
      // {
      //   prop: 'ceeaAgentBrand',
      //   label: '品牌',
      //   minWidth: 120
      // },
      // {
      //   prop: 'mainCustomers',
      //   label: () => this.$t('sourcingBuyer.mainCustomer'),
      //   minWidth: 120
      // },
      {
        prop: 'categoryName',
        // label: '品类',
        label: () => this.$t('common.category'),
        minWidth: 120
      },
      {
        prop: 'companyCreationYear',
        // label: '成立年限',
        label: () => this.$t('cusEntry.supplement20250121.establishmentYear'),
        minWidth: 120
      },
      {
        prop: 'registeredCapital',
        // label: '注册资金',
        label: () => this.$t('vendorMod.registerMoney'),
        minWidth: 140
      },
      {
        prop: 'registCurrency',
        // label: '注册资金币种',
        label: () => this.$t('cusEntry.supplement20250121.currencyOfRegisteredCapital'),
        dataType: 'dict',
        code: 'currency',
        minWidth: 120
      },
      {
        prop: 'companyAddress',
        // label: '供应商地址',
        label: () => this.$t('cusEntry.supplement20250121.supplierAddress'),
        minWidth: 120
      },
      {
        prop: 'bidCount',
        // label: '投标次数',
        label: () => this.$t('cusEntry.supplement20250121.numberOfBids'),
        minWidth: 120
      },
      {
        prop: 'succBidCount',
        // label: '中标次数',
        label: () => this.$t('supRisk.winBidsNum'),
        minWidth: 120
      },
      {
        prop: 'invalidBidCount',
        // label: '废标次数',
        label: () => this.$t('cusEntry.supplement20250121.numberOfInvalidBids'),
        minWidth: 120
      },
      {
        prop: 'viewJoinHistory',
        // label: '参与历史',
        label: this.$t('cusEntry.supplement20250121.participateInHistory'),
        showType: 'button',
        btnStyle: 'text',
        unsortable: true,
        // formattor: () => '查看详情',
        formattor: () => this.$t('orderMod.viewDetail'),
        fixed: 'right',
        callback: (row) => {
          console.log('row:::', row)
          this.editTab(row)
        }
      }
    ]
    this.$nextTick(() => {
      this.getQueryData()
    })
  },

  methods: {
    /* 查询列表数据 */
    getQueryData (params = {}) {
      console.log(params)
      transformTimeQuery(['bidCreationDate'], params)
      const { bidCreationDate, ...rest } = params
      this.queryParam = transformMQL.listPageData({
        type: 'SouInviteHeadBuyer',
        action: 'listPage',
        params,
        filterOperator: {
          bidCreationDate: 'between',
          beginRegisteredCapital: 'ge',
          endRegisteredCapital: 'le',
          beginCompanyCreationYear: 'ge',
          endCompanyCreationYear: 'le'
        }
      })

      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },

    beforeOpen (value, callback) {
      if (!this.selectedRows.length) {
        // this.$message.warning('请勾选列表')
        this.$message.warning(this.$t('outsource.pleaseCheckList'))
        callback(null)
      }
    },

    async getNotPublicHeadNum (val) {
      console.log('val', val)
      let params = this.selectedRows.map(item => ({
        ...item,
        ...val,
        projectName: val.projectName,
        souId: val.requirementHeadId,
        souNo: val.requirementHeadNum,
        souType: 'PR'
      }))
      let transformParams = transformMQL.save('SouInviteHistoryBuyer', params, 'notPublicInvite')
      const response = await inviteHttp.notPublicInvite(transformParams)
      if (response) {
        this.$message.success(this.$t('common.success'))
        this.getQueryData()
      }
    },

    async getPublicHeadNum (val) {
      console.log('val', val)
      let params = this.selectedRows.map(item => ({
        ...item,
        ...val,
        projectName: val.projectName,
        souId: val.reqHeadId,
        souNo: val.reqHeadNo,
        souType: 'RFP'
      }))
      let transformParams = transformMQL.save('SouInviteHistoryBuyer', params, 'publicInvite')
      const response = await inviteHttp.publicInvite(transformParams)
      if (response) {
        this.$message.success(this.$t('common.success'))
        this.getQueryData()
      }
    },

    editTab (row) {
      const tab = {
        component: InviteVendorDetail,
        params: {
          row,
          // tabName: '邀请供应商详情' + row.inviteHeadId
          tabName: this.$t('outsource.pleaseCheckList') + row.inviteHeadId
        },
        // title: '邀请供应商详情',
        title: () => this.$t('outsource.pleaseCheckList'),
        // name: '邀请供应商详情' + row.inviteHeadId
        name: this.$t('outsource.pleaseCheckList') + row.inviteHeadId
      }
      this.$emit('tab-add', tab)
    },

    /* 选中行 */
    handleCurrentChange (val) {
      console.log('val:::', val)
      this.selectedRows = val
    },

    /**
     * 公示邀请供应商, Y
     * 不公示邀请供应商, N
     */
    openInviteDialog (flag) {
      if (!this.selectedRows || !this.selectedRows.length) {
        // this.$message.warning('请勾选供应商')
        this.$message.warning(this.$t('cusEntry.supplement20250121.pleaseSelectTheSupplier'))
        return
      }
      this.inviteDialogVisible = true
    },

    /* 查看邀请历史  */
    viewHistory () {
      this.inviteHisDialogVisible = true
    },

    /* 智能推荐 */
    inteligentRecommend () {
      this.recommendDialogVisible = true
      this.$nextTick(() => {
        this.$refs.recommendDialog.resetFields()
      })
    },

    /* 智能推荐 - 确定 */
    recommendDialogConfirm (val) {
      console.log('params', this.queryParam)
      this.recommendDialogVisible = false
      this.queryParam = transformMQL.listPageData({
        type: 'SouInviteHeadBuyer',
        action: 'listPage',
        params: {
          ...val,
          isIr: 'Y' // 是否智能推荐
        },
        filterOperator: {
          isIr: 'eq'
        }
      })
      this.tableViewUrl = inviteHttp.irListPageUrl
      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },

    /** 邀请供应商 start */
    inviteDialogConfirm (rows) {

    }
    /** 邀请供应商 end */
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
