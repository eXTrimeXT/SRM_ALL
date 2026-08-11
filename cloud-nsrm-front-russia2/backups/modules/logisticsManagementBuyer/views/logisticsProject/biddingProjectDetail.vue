<template>
  <el-container
    class="flex-container-aside-biding the_biddingProjectApproval_wrapper"
    style="min-width:900px"
  >
    <el-aside
      width="153px"
      style="padding: 0px;"
    >
      <el-container
        class="flex-container"
        direction="vertical"
      >
        <el-main>
          <el-menu
            :default-openeds="openeds"
            :default-active="defaultCheckedKeys"
            class="el-menu-vertical-demo bidMentLeftSlide"
          >
            <template v-for="item in treedata">
              <el-submenu
                v-if="item.children"
                :key="item.id"
                :index="item.id"
              >
                <template slot="title">
                  {{ item.label }}
                </template>
                <el-menu-item
                  v-for="node in item.children"
                  :key="node.id"
                  :index="node.id"
                  :disabled="node.disabled"
                  @click="menuClick(node)"
                >
                  <i :class="node.iconClass" /><span slot="title">{{
                    node.label
                  }}</span>
                </el-menu-item>
              </el-submenu>
              <el-menu-item
                v-else
                :key="item.id"
                :index="item.id"
                :disabled="item.disabled"
                @click="menuClick(item)"
              >
                <i :class="item.iconClass" /><span slot="title">{{
                  item.label
                }}</span>
              </el-menu-item>
            </template>
          </el-menu>
        </el-main>
      </el-container>
    </el-aside>
    <!-- 右边-条目区域 -->
    <el-container
      class="flex-container flex-container-right"
      direction="vertical"
    >
      <el-main>
        <el-tabs
          v-model="editableTabsValue"
          type="border-card"
        >
          <el-tab-pane
            :label="$t('bidMod.projectInformation')"
            name="t11"
          >
            <biddingProjectDetailInfo
              ref="detailsInfo"
              :all-params="allParams"
              :isdisabled-tab="isdisabledTab"
              :bid-process-config-id-list="bidProcessConfigIdList"
              :inner-files="innerFiles"
              :outer-files="outerFiles"
              @getprocessConfigId="getprocessConfigId"
              @getTemplateLines="getTemplateLines"
            />
          </el-tab-pane>
          <!-- 项目需求 -->
          <el-tab-pane
            :label="$t('bidMod.projectRequirement')"
            name="t12"
          >
            <el-form
              ref="form2"
              :model="tab2form"
              :disabled="isdisabledTab"
            >
              <div>
                <div style="padding: 10px 0">
                  <el-button
                    type="primary"
                    class="detail-pbtn"
                    :disabled="
                      allParams.biding.sourceFrom == 'PURCHASE_REQUEST'
                    "
                    @click="addbidRequirementLineList"
                  >
                    {{ $t("common.add") }}
                  </el-button>
                  <el-button
                    type="primary"
                    class="detail-pbtn"
                    :disabled="
                      allParams.biding.sourceFrom == 'PURCHASE_REQUEST'
                    "
                    @click="delbidRequirementLineList"
                  >
                    {{ $t("common.delete") }}
                  </el-button>
                  <!-- <div
                    style="padding: 0 10px;float: left;"
                    v-if="allParams.biding.sourceFrom !== 'PURCHASE_REQUEST'"
                  >
                    <m-import
                      ref="import"
                      :title="iModal.title"
                      :extraData="iModal.extraData"
                      :upLoadUrl="iModal.upLoadUrl"
                      @beforeUpload="beforeUpload"
                      @downloadTemplate="downloadTemplate"
                      @handleSuccess="uploadSuccess"
                      :showSuccessDeal="true"
                    ></m-import>
                  </div> -->
                </div>
              </div>
              <el-table
                ref="tableGrid"
                :data="bidRequirementLineList"
                style="width: 100%"
                border
                :row-height="30"
                max-height="390px"
                highlight-current-row
                @selection-change="checkLineList"
              >
                <el-table-column
                  fixed="left"
                  type="selection"
                />
                <!-- 行号 -->
                <el-table-column
                  fixed="left"
                  align="center"
                  prop="rowNum"
                  :label="$t('purchaseDemand.lineNum')"
                  width="60"
                />
                <templateList
                  ref="templateListId"
                  :table-header="tableHeader4"
                  :requirement-head="allParams.biding"
                  operate-flag-type="purchaseOperateFlag"
                  visible-flag-type="purchaseVisibleFlag"
                  :requirement-line-list="bidRequirementLineList"
                />
                <el-table-column
                  :label="$t('common.operation')"
                  fixed="right"
                  width="100"
                >
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      :disabled="
                        allParams.biding.sourceFrom == 'PURCHASE_REQUEST'
                      "
                      @click="deleteOneContent(scope.$index, scope.row)"
                    >
                      {{ $t("common.delete") }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-form>
          </el-tab-pane>
          <!-- 邀请供应商 -->
          <el-tab-pane
            :label="$t('bidMod.inviteVendor')"
            name="t13"
          >
            <biddingProjectDetailVendors
              ref="detailsVendor"
              :table-form="tableForm"
              :scope-biding-id="scopeBidingId"
              :isdisabled-tab="isdisabledTab"
            />
          </el-tab-pane>
          <!-- 投标控制 -->
          <el-tab-pane
            :label="$t('bidMod.bidingControl')"
            name="t5"
          >
            <biddingProjectDetailControl
              :biding-con-form="bidingConForm"
              :scope-biding-id="scopeBidingId"
              :biding-status="bidingStatus"
              :bid-control-item-list="bidControlItemList"
              @getTab5List="getTab5List"
              @getFormDetail="getFormDetail"
              @endImmediatelyDoBidding="endImmediatelyDoBidding"
            />
          </el-tab-pane>
          <!-- 技术标管理 -->
          <el-tab-pane
            :label="$t('bidMod.technicalManagement')"
            name="t62"
          >
            <biddingProjectDetailTechBiding
              :lgt-vendor-files="lgtVendorFiles"
              :schedule-form="scheduleForm"
              :all-params="allParams"
              :scope-biding-id="scopeBidingId"
              :is-group="isGroup"
              :table-header="tableHeader4"
              :is-show="bidingStatus == 'TENDER_ENDING'"
              :form="techBidingform"
            />
          </el-tab-pane>
          <!-- 商务标管理 -->
          <el-tab-pane
            :label="$t('bidMod.commercialManagement')"
            name="t63"
          >
            <commercialBiding
              :business-item-list="businessItemList"
              :scope-biding-id="scopeBidingId"
              :current-round="currentRound"
              :biding-status="bidingStatus"
              @getTab63List="getTab63List"
            />
          </el-tab-pane>
          <!-- 评选 -->
          <el-tab-pane
            :label="$t('bidMod.quoteEvaluation')"
            name="t64"
          >
            <biddingProjectDetailSelection
              ref="t64"
              :all-params="allParams"
              :current-round="currentRound"
              :table-header="tableHeader4"
              :business-mode-code="allParams.biding.businessModeCode"
              :scope-biding-id="scopeBidingId"
              :is-group="isGroup"
              :biding-status="bidingStatus"
              @getFormDetail="getFormDetail"
            />
          </el-tab-pane>
          <!-- 流程审批 -->
          <el-tab-pane
            :label="$t('bidMod.processApproval')"
            name="t15"
          >
            <!-- 流程审批 -->
            <biddingProjectDetailApproval
              :lgt-biding="lgtBiding"
              :all-params="allParams"
              :table-header="tableHeader4"
              :current-round="currentRound"
              :lgt-vendor-quoted-sums="lgtVendorQuotedSums"
              :lgt-vendor-quoted-lines="lgtVendorQuotedLines"
              :schedule-form="scheduleForm"
              @getApprovalDetails="getApprovalDetails"
            />
          </el-tab-pane>
        </el-tabs>
      </el-main>
      <div class="c-toolbar">
        <div class="c-toolbar-center">
          <el-button
            :disabled="isReadOnly"
            @click="backTo"
          >
            {{
              $t("common.cancel")
            }}
          </el-button>
          <el-button
            v-if="auditStatus !== 'APPROVED'"
            type="primary"
            :disabled="isReadOnly"
            @click="tempStorage"
          >
            {{ $t("common.staging") }}
          </el-button>
          <el-button
            v-show="editableTabsValue !== 't11'"
            :disabled="isReadOnly"
            type="primary"
            @click="prevOne"
          >
            {{ $t("bidMod.prevOne") }}
          </el-button>
          <el-button
            v-if="editableTabsValue == 't13' && bidingStatus == 'DRAW_UP'"
            :disabled="isReadOnly"
            type="primary"
            @click="nextOne"
          >
            {{ $t("common.publish") }}
          </el-button>
          <el-button
            v-else
            :disabled="isReadOnly"
            type="primary"
            @click="nextOne"
          >
            {{ $t("bidMod.nextOne") }}
          </el-button>
        </div>
      </div>
    </el-container>
  </el-container>
</template>
<script>
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import biddingProjectDetailInfo from './biddingProjectDetailInfo'
import biddingProjectDetailVendors from './biddingProjectDetailVendors'
import biddingProjectDetailControl from './biddingProjectDetailControl'
import biddingProjectDetailTechBiding from './biddingProjectDetailTechBiding'
import commercialBiding from './commercialBiding'
import biddingProjectDetailSelection from './biddingProjectDetailSelection'
import biddingProjectDetailApproval from './biddingProjectDetailApproval'

import {
  getDictItem,
  getDictItemList
} from '@/api/common'
import CToolbar from 'lib@/components/c-toolbar'
import MainHeader from 'lib@/components/Table/MainHeader'
import { adaptDictData, parseTime } from '@/utils'
import { tabTodoMixin } from '@/utils/mixins'
import { isMobile, isEmail } from 'lib@/utils/validate'
import costElementListVue from '../../../priceModel/views/priceModel/costElementList.vue'
import templateList from '../logisticsPurchaseApply/templateList'
import { geti18n } from '@/main'
const i18n = geti18n()

export default {
  name: 'BiddingProjectDetail',
  components: {
    templateList,
    biddingProjectDetailInfo,
    biddingProjectDetailVendors,
    biddingProjectDetailControl,
    biddingProjectDetailTechBiding,
    biddingProjectDetailSelection,
    commercialBiding,
    biddingProjectDetailApproval,
    MainHeader,
    CToolbar,
    MImport
  },
  mixins: [tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      // 新增 by chenzp20 <start>
      showSubmitDesc: false,
      auditStatus: null,
      proxyQuoteVisible: false,
      pricingType: 'SIMPLE_PRICING', // 默认普通报价
      // 新增 by chenzp20 <end>
      bidingStatus: 'DRAW_UP', // 招标状态
      tableName: 'bidingEvaluationListItem',
      iModal: {
        title: this.$t('common.import'),
        extraData: {
          uploadType: 'FASTDFS', // 固定参数
          sourceType: 'WEB_APP', // 固定参数
          fileModular: 'bid', // 文件所属模块 -》基础模块
          fileFunction: 'biddingProject', // 文件所属功能
          fileType: 'file' // 文件所属类型
        },
        upLoadUrl:
          '/api-pd/bidInitiating/bidRequirementLine/importExcel'
      },
      currentRound: '',
      scopecbpmInstaceId: '',
      funParams: {}, // 立项审批流程参数
      projectFlowOpen: false,
      bidProcessConfigIdList: [],
      editableTabsValue: 't11',
      scoperequirementId: '',
      scopeBidingNum: '',
      scopeBidingId: '',
      requireDesc: '',
      tab2form: {},
      openeds: ['t1', 't2', 't6', 't7'],
      treedata: [
        {
          id: 't1',
          label: this.$t('bidMod.addNewProj'), // 招标立项
          children: [
            {
              id: 't11',
              label: this.$t('bidMod.projectInformation'),
              key: 'projectInformation'
            },
            {
              id: 't12',
              label: this.$t('bidMod.projectRequirement'),
              key: 'projectRequirement'
            },
            {
              id: 't13',
              label: this.$t('bidMod.inviteSupplier'),
              key: 'inviteSupplier'
            }
          ]
        },
        {
          id: 't2',
          label: this.$t('bidMod.bidingControl'),
          children: [
            {
              id: 't5',
              label: this.$t('bidMod.bidingControl'),
              key: 'bidingControl'
            }
          ]
        },
        {
          id: 't6',
          label: this.$t('bidMod.openEvalateBid'),
          children: [
            {
              id: 't62',
              label: this.$t('bidMod.technicalManagement'),
              key: 'technicalManagement'
            },
            {
              id: 't63',
              label: this.$t('bidMod.commercialManagement'),
              key: 'commercialManagement'
            },
            {
              id: 't64',
              label: this.$t('bidMod.bidEvaluation'),
              key: 'bidEvaluation'
            }
          ]
        },
        {
          id: 't7',
          label: this.$t('bidMod.processApproval'),
          children: [
            {
              id: 't15',
              label: this.$t('bidMod.processApproval'),
              key: 'processApproval'
            }
          ]
        }
      ],
      defaultCheckedKeys: 't11',
      isdisabledTab: false,
      lgtBiding: {
        bidingNum: null,
        bidingName: null,
        businessModeCode: null,
        transportModeCode: null,
        unitCode: null,
        projectTotal: null,
        demandDate: null,
        budgetAmount: null,
        priceTimeStart: null,
        priceTimeEnd: null,
        comments: null,
        summaryDescription: null
      },
      scheduleForm: {
        lgtBidShipPeriods: [],
        bidShipPeriodList: []
      },
      lgtVendorQuotedLines: [],
      lgtVendorQuotedSums: [],
      allParams: {
        biding: {
          processConfigId: null,
          bidingNum: null,
          templateHeadId: null,
          templateCode: null,
          templateName: null,
          businessModeCode: null,
          transportModeCode: null,
          businessType: null,
          unitId: null,
          unitCode: null,
          unitName: null,
          projectTotal: null,
          bidingName: null,
          demandDate: null,
          budgetAmount: null,
          currencyCode: null,
          largestModel: null,
          priceTimeStart: null,
          priceTimeEnd: null,
          loading: null,
          enrollEndDatetime: null,
          taxCode: null,
          taxKey: null,
          ifSubmitShippingSchedule: null,
          contractType: null,
          ifVendorSubmitShipDate: null,
          companyId: null,
          companyCode: null,
          companyName: null,
          specifySupReason: null,
          comments: null,
          requirementHeadNum: null,
          applyBy: null,
          applyDepartmentName: null,
          serviceProjectCode: null,
          serviceProjectName: null,
          templateFileId: null,
          templateFileName: null,
          // 投标控制
          withdrawBiding: 'N',
          publicTargetPrice: 'N',
          publicCodeRanking: 'N',
          publicLowestPrice: 'N',
          publicTotalRank: 'N',
          visibleTargetPrice: 'N',
          visibleRankResult: 'N',
          visibleFinalPrice: 'N',
          visibleTotalRanking: 'N',
          visibleWinVendor: 'N',
          // 商务信息部分
          bondAmount: '0',
          bondMethod: '',
          bondEndDatetime: '',
          taxInclusivePrice: 'N',
          bidingCurrency: '',
          decimalAccuracy: '',
          bankAccountNum: '',
          bankAccountName: '',
          bankBranchName: '',
          bondDesc: '',
          // 第三部分
          bidUserEmail: '',
          bidUserPhone: '',
          bidUserName: '',
          // 新增报价币种设置 [start] by chenzp20
          standardCurrency: 'CNY',
          pricePrecision: '4',
          exchangeRateType: 'Corporate',
          showRateType: 'N',
          currencyChangeDate: new Date().getTime() // +'-'+ (new Date().getMonth()+1 )+'-'+ (new Date().getDate()+1 )
          // 新增报价币种设置 [end] by chenzp20
        },
        fileList: [],
        groupList: [],
        bidFileConfigList: []
      },
      ruleItems: [],
      bidRequirementLineList: [],
      checkLine: [],
      innerFiles: [],
      outerFiles: [],
      tableHeader4: [],
      isGroup: false,
      tableForm: {
        t13table: []
      },
      templatetitle: '',
      templatetotalScore: '',
      bidingConForm: {
        currentRoundSupplierCount: '',
        submitSupplierCount: '',
        endTime: ''
      },
      bidControlItemList: [],
      businessItemList: [],
      lgtVendorFiles: [],
      techBidingform: {
        technoSelection: null
      },
      isReadOnly: '' // 兼容传入禁用相关按钮
    }
  },
  computed: {},
  created () {
    //  console.log("this.$attrs.",this.$attrs)
    let menus = this.$store.getters.userInfo.menus // 当前用户下面的菜单
    let curRouter = this.$route.path // 当前路由路径
    this.isReadOnly = this.$attrs.params.isReadOnly // 兼容获取嵌入界面禁用按钮 byliwenhong
    // 兼容获取嵌入界面的路径 byliwenhong
    let queRouter = this.$route.query.funName || {}
    if (queRouter == 'biddingProject') {
      curRouter = '/biddingManagement/biddingProject'
    }

    this.getbidProcessConfigIdList() // 查询招标流程配置
    if (this.$attrs.params.flag == 'edit') {
      this.scopeBidingId = this.$attrs.params.row.bidingId
      // 新增报价币种设置信息获取 [end] by chenzp20
      this.getFormDetail(this.scopeBidingId)
      this.getFileList(this.scopeBidingId)
      this.getGroupList(this.scopeBidingId)
      this.getbidFileConfigList(this.scopeBidingId)
      this.getprocessNode(this.scopeBidingId)

      if (this.$attrs.params.readOnly) {
        this.isdisabledTab = true
        this.treedata = [
          {
            id: 't1',
            label: this.$t('bidMod.addNewProj'),
            children: [
              {
                id: 't11',
                label: this.$t('bidMod.projectInformation'),
                key: 'projectInformation'
              },
              {
                id: 't12',
                label: this.$t('bidMod.projectRequirement'),
                key: 'projectRequirement'
              },
              {
                id: 't13',
                label: this.$t('bidMod.inviteSupplier'),
                key: 'inviteSupplier'
              },
              {
                id: 't14',
                label: this.$t('bidMod.scoringRule'),
                key: 'scoringRule'
              },
              {
                id: 't15',
                label: this.$t('bidMod.processApproval'),
                key: 'processApproval'
              }
            ]
          }
        ]
      }
    } else {
      // 默认加载采购商联系方式，如果没有才需要填写
      if (this.$store.state.user && this.$store.state.user.userInfo) {
        this.allParams.biding.bidUserName = this.$store.state.user.userInfo.nickname
        this.allParams.biding.bidUserPhone = this.$store.state.user.userInfo.phone
        this.allParams.biding.bidUserEmail = this.$store.state.user.userInfo.email
      }
    }
  },
  methods: {
    getTemplateLines (templateHeadId) {
      this.$http({
        url:
          '/api-pd/logistics/logistics-template-head/listTemplateLinesByHeadId',
        method: 'GET',
        params: { headId: templateHeadId },
        loading: true
      })
        .then(data => {
          if (data.data) {
            this.tableHeader4 = data.data.templateLines
            this.allParams.biding.templateFileId =
              data.data.templateHead.templateFileId
            this.allParams.biding.templateFileName =
              data.data.templateHead.templateFileName
            this.allParams.biding.ifNeedVendorComfirm =
              data.data.templateHead.ifNeedVendorSubmit
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    // 项目左侧菜单点击
    menuClick (data) {
      if (!this.scopeBidingId) {
        this.$message.warning(this.$t('bidMod.msgInputProInfo')) // 请先输入项目信息！
        return
      }
      if (
        ['t5', 't62', 't63', 't64', 't15'].includes(data.id) &&
        this.bidingStatus == 'DRAW_UP'
      ) {
        this.$message.warning(this.$t('logisticsMod.msgPurchaseApply[32]')) // 当前节点还不能操作
        return
      }
      if (
        ['t64', 't15'].includes(data.id) &&
        this.bidingStatus == 'ACCEPT_BID'
      ) {
        this.$message.warning(this.$t('logisticsMod.msgPurchaseApply[32]'))
        return
      }
      if (data.id === 't12') {
        // 项目需求
        this.getTab2List(this.scopeBidingId)
      } else if (data.id === 't13') {
        // 邀请供应商
        this.getTab3List(this.scopeBidingId)
      } else if (data.id === 't5') {
        // 投标控制tab
        this.getTab5List(this.scopeBidingId)
      } else if (data.id === 't62') {
        // 技术标管理
        this.getProgressOfTechBidList()
      } else if (data.id === 't63') {
        // 商务标管理
        this.getTab63List(this.scopeBidingId)
      } else if (data.id === 't64') {
        // 评选
        this.$refs.t64.getQuerydata()
      } else if (data.id === 't15') {
        // 流程审批
        this.getApprovalDetails(this.currentRound)
      }
      this.editableTabsValue = data.id
    },
    prevOne () {
      let curtabName = this.editableTabsValue
      switch (curtabName) {
        case 't12':
          this.editableTabsValue = 't11'
          this.defaultCheckedKeys = 't11'
          break
        case 't13':
          this.editableTabsValue = 't12'
          this.defaultCheckedKeys = 't12'
          this.getTab2List(this.scopeBidingId)
          break
        case 't5':
          this.editableTabsValue = 't13'
          this.defaultCheckedKeys = 't13'
          // 邀请供应商
          this.getTab3List(this.scopeBidingId)
          break
        case 't62':
          this.editableTabsValue = 't5'
          this.defaultCheckedKeys = 't5'
          // 投标控制
          this.getTab5List(this.scopeBidingId)
          break
        case 't63':
          this.editableTabsValue = 't62'
          this.defaultCheckedKeys = 't62'
          // 技术标管理
          this.getProgressOfTechBidList()
          break
        case 't64':
          this.editableTabsValue = 't63'
          this.defaultCheckedKeys = 't63'
          this.getTab63List(this.scopeBidingId)
          break
      }
    },
    // 流程审批详情
    getApprovalDetails (round) {
      this.$http({
        url: '/api-pd/logistics/biding/queryResultApproveInfo',
        method: 'GET',
        params: { bidingId: this.scopeBidingId, round: round },
        loading: true
      })
        .then(data => {
          this.lgtBiding = data.data.lgtBiding
          this.scheduleForm.scheduleList = data.data.lgtBidShipPeriods.map(
            i => ({
              ...i,
              provinceList: [
                {
                  value: i.fromProvinceCode,
                  label: i.fromProvince
                }
              ],
              endProvinceList: [
                {
                  value: i.toProvinceCode,
                  label: i.toProvince
                }
              ],
              startCityList: [
                {
                  value: i.fromCityCode,
                  label: i.fromCity
                }
              ],
              startCountyList: [
                {
                  value: i.fromCountyCode,
                  label: i.fromCounty
                }
              ],
              endCityList: [
                {
                  value: i.toCityCode,
                  label: i.toCity
                }
              ],
              endCountyList: [
                {
                  value: i.toCountyCode,
                  label: i.toCounty
                }
              ]
            })
          )
          this.lgtVendorQuotedLines = data.data.lgtVendorQuotedLines
          this.lgtVendorQuotedSums = data.data.lgtVendorQuotedSums
        })
        .catch(err => {
          console.log(err)
        })
    },
    // 移除
    getprocessNode (scopeBidingId) {
      if (!scopeBidingId) return
      this.$http({
        url: '/api-pd/logistics/biding/getNodeStatus',
        method: 'GET',
        params: { bidingId: scopeBidingId },
        loading: true
      })
        .then(data => {
          let nodeList = data.data.map(x => x.nodeCode)
          let keyList = data.data
            .filter(v => v.dataFlag === 'Y')
            .map(x => x.nodeCode)
          for (let i of this.treedata) {
            if (!i.children) {
              i.disabled = !nodeList.includes(i.key)
              if (keyList.includes(i.key)) {
                i.iconClass = 'el-icon-success'
              } else {
                i.iconClass = 'el-icon-circle-check'
              }
            } else {
              for (let j of i.children) {
                j.disabled = !nodeList.includes(j.key)
                if (keyList.includes(j.key)) {
                  j.iconClass = 'el-icon-success'
                } else {
                  j.iconClass = 'el-icon-circle-check'
                }
              }
            }
          }
          this.$forceUpdate()
        })
        .catch(err => {
          console.log(err)
        })
    },
    // 下一步
    nextOne () {
      this.enabledArr = []
      for (let i of this.treedata) {
        if (!i.children) {
          this.enabledArr.push(i.id)
        } else {
          for (let j of i.children) {
            if (!j.disabled) {
              this.enabledArr.push(j.id)
            }
          }
        }
      }
      if (this.editableTabsValue === 't11') {
        if (this.bidingStatus !== 'DRAW_UP') {
          if (this.enabledArr.includes('t12')) {
            this.editableTabsValue = 't12'
            this.defaultCheckedKeys = 't12'
            this.getTab2List(this.scopeBidingId)
          }
          return false
        }
        // 项目信息
        this.$refs.detailsInfo.validate(valid => {
          if (valid) {
            this.$refs.detailsInfo.validate1(valid1 => {
              if (!this.allParams.groupList.length) {
                this.$message({
                  message: this.$t('logisticsMod.msgPurchaseApply[33]'), // 请至少维护一条工作小组信息
                  type: 'error'
                })
                return false
              }
              if (valid1) {
                this.allParams.fileList = []
                for (let i of this.innerFiles) {
                  this.allParams.fileList.push({
                    fileType: 'Enterprise',
                    docId: i.docId,
                    fileName: i.fileName,
                    comments: i.comments
                  })
                }
                for (let j of this.outerFiles) {
                  this.allParams.fileList.push({
                    fileType: 'Supplier',
                    docId: j.docId,
                    fileName: j.fileName,
                    comments: j.comments
                  })
                }
                // 项目信息
                let url = '/api-pd/logistics/biding/add'
                if (this.scopeBidingId) {
                  // 修改模式
                  url = '/api-pd/logistics/biding/modify'
                }
                this.$http({
                  url: url,
                  method: 'POST',
                  data: this.allParams,
                  loading: true
                })
                  .then(data => {
                    this.scopeBidingId = this.scopeBidingId || data.data
                    this.getprocessNode(this.scopeBidingId)
                    this.getFormDetail(this.scopeBidingId)
                    this.getGroupList(this.scopeBidingId)
                    // console.log("enabledArrB",this.enabledArr)
                    if (this.enabledArr.includes('t12')) {
                      this.editableTabsValue = 't12'
                      this.defaultCheckedKeys = 't12'
                      this.getTab2List(this.scopeBidingId)
                    }
                  })
                  .catch(err => {
                    console.log(err)
                  })
              } else {
                this.$message({
                  message: this.$t('vendorMod.pleasefinishRequired'), // '请输入单据必填信息'
                  type: 'error'
                })
                return false
              }
            })
          } else {
            this.$message({
              message: this.$t('vendorMod.pleasefinishRequired'), // '请输入单据必填信息'
              type: 'error'
            })
            return false
          }
        })
      } else if (this.editableTabsValue === 't12') {
        if (this.bidingStatus !== 'DRAW_UP') {
          if (this.enabledArr.includes('t13')) {
            // 当前所在的节点邀请供应商
            this.editableTabsValue = 't13'
            this.defaultCheckedKeys = 't13'
            this.getTab3List(this.scopeBidingId)
          }
          return false
        }
        // 需求信息
        if (this.bidRequirementLineList.length === 0) {
          this.$message.error(this.$t('bidMod.bidMsgList[27]')) // 请先录入需求明细!
          return
        }
        this.$http({
          url: '/api-pd/logistics/biding/updateLgtBidRequirementLine',
          method: 'POST',
          data: this.bidRequirementLineList,
          loading: true
        })
          .then(data => {
            this.getprocessNode(this.scopeBidingId)
            if (this.enabledArr.includes('t13')) {
              // 当前所在的节点邀请供应商
              this.editableTabsValue = 't13'
              this.defaultCheckedKeys = 't13'
              this.getTab3List(this.scopeBidingId)
            }
          })
          .catch(err => {
            console.log(err)
          })
      } else if (this.editableTabsValue === 't13') {
        if (this.bidingStatus !== 'DRAW_UP') {
          if (this.enabledArr.includes('t5')) {
            // 当前所在的节点投标控制
            this.getTab5List(this.scopeBidingId)
            this.editableTabsValue = 't5'
            this.defaultCheckedKeys = 't5'
          }
          return false
        }
        this.$refs.detailsVendor.validate(valid => {
          if (valid) {
            // 供应商信息
            this.$http({
              url: '/api-pd/logistics/biding/saveLgtBidVendor',
              method: 'POST',
              data: this.tableForm.t13table,
              loading: true
            })
              .then(data => {
                this.release()
              })
              .catch(err => {
                console.log(err)
              })
          } else {
            this.$message({
              message: this.$t('vendorMod.pleasefinishRequired'), // '请输入单据必填信息'
              type: 'error'
            })
            return false
          }
        })
      } else if (this.editableTabsValue === 't5') {
        // 投标控制
        if (this.enabledArr.includes('t62')) {
          // 当前所在的节点技术标管理
          this.editableTabsValue = 't62'
          this.defaultCheckedKeys = 't62'
        }
      } else if (this.editableTabsValue === 't62') {
        // 技术标管理
        if (this.enabledArr.includes('t63')) {
          // 当前所在的节点商务标管理
          this.editableTabsValue = 't63'
          this.defaultCheckedKeys = 't63'
          this.getTab63List(this.scopeBidingId)
        }
      } else if (this.editableTabsValue === 't63') {
        // 技术标管理
        if (this.enabledArr.includes('t64')) {
          // 当前所在的节点评选
          this.editableTabsValue = 't64'
          this.defaultCheckedKeys = 't64'
          this.$refs.t64.getQuerydata()
        }
      }
    },
    // 暂存 zhaomz
    tempStorage () {
      if (this.editableTabsValue === 't11') {
        // 项目信息
        let url = '/api-pd/logistics/biding/add'
        if (this.scopeBidingId) {
          // 修改模式
          url = '/api-pd/logistics/biding/modify'
        }
        this.allParams.fileList = []
        for (let i of this.innerFiles) {
          this.allParams.fileList.push({
            fileType: 'Enterprise',
            docId: i.docId,
            fileName: i.fileName,
            comments: i.comments
          })
        }
        for (let j of this.outerFiles) {
          this.allParams.fileList.push({
            fileType: 'Supplier',
            docId: j.docId,
            fileName: j.fileName,
            comments: j.comments
          })
        }
        this.$http({
          url: url,
          method: 'POST',
          data: this.allParams,
          loading: true
        })
          .then(data => {
            this.scopeBidingId = this.scopeBidingId || data.data
            this.allParams.biding.bidingId = this.scopeBidingId
            this.$message.success(this.$t('common.success')) // zhaomz
          })
          .catch(err => {
            console.log(err)
          })
      } else if (this.editableTabsValue === 't12') {
        // 需求信息
        if (this.bidRequirementLineList.length === 0) {
          this.$message.error(this.$t('bidMod.bidMsgList[27]'))
          return
        }
        this.$http({
          url: '/api-pd/logistics/biding/updateLgtBidRequirementLine',
          method: 'POST',
          data: this.bidRequirementLineList,
          loading: true
        })
          .then(data => {
            this.$message({
              type: 'success',
              message: data.message
            })
          })
          .catch(err => {
            console.log(err)
          })
      } else if (this.editableTabsValue === 't13') {
        // 供应商信息
        this.$http({
          url: '/api-pd/logistics/biding/saveLgtBidVendor',
          method: 'POST',
          data: this.tableForm.t13table,
          loading: true
        })
          .then(data => {
            this.$message.success(data.message)
            this.getTab3List(this.scopeBidingId)
          })
          .catch(err => {
            console.log(err)
          })
      }
    },
    // 发布
    release () {
      this.$http({
        url: '/api-pd/logistics/biding/release',
        method: 'get',
        params: {
          bidingId: this.scopeBidingId
        },
        loading: true
      })
        .then(data => {
          this.$message.success(data.message)
          this.getprocessNode(this.scopeBidingId)
          this.getFormDetail(this.scopeBidingId)
          this.getTab5List(this.scopeBidingId)
          if (this.enabledArr.includes('t5')) {
            // 当前所在的节点投标控制
            this.editableTabsValue = 't5'
            this.defaultCheckedKeys = 't5'
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    getprocessConfigId (val) {
      let row = this.bidProcessConfigIdList.filter(
        v => v.processConfigId === val
      )
        ? this.bidProcessConfigIdList.filter(v => v.processConfigId === val)[0]
        : ''
      if (!row) return
      let keyList = []
      for (let i in row) {
        if (row[i] === 'Y') {
          keyList.push(i)
        }
      }
      for (let i of this.treedata) {
        if (!i.children) {
          i.disabled = !keyList.includes(i.key)
          i.iconClass = 'el-icon-circle-check'
        } else {
          for (let j of i.children) {
            j.disabled = !keyList.includes(j.key)
            // if (j.key == "projectResult") j.disabled = false;
            j.iconClass = 'el-icon-circle-check'
          }
        }
      }
      this.treedata.push({})
      this.treedata.splice(this.treedata.length - 1, 1)
    },
    getbidProcessConfigIdList () {
      this.$http({
        url: '/api-bid/bidProcessConfig/bidProcessConfig/listAll',
        method: 'POST',
        data: {},
        loading: true
      })
        .then(data => {
          if (data && data.data) {
            this.bidProcessConfigIdList = data.data
            // .filter(
            //   i => i.bidingType == "Logistics"
            // );
            if (this.bidProcessConfigIdList.length == 1) {
              this.allParams.biding.processConfigId = this.bidProcessConfigIdList[0].processConfigId
              this.getprocessConfigId(this.allParams.biding.processConfigId)
            }
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    getFormDetail (bidingId) {
      this.$http({
        url: '/api-pd/logistics/biding/getLgtBidInfoVo',
        method: 'GET',
        params: { bidingId: bidingId },
        loading: true
      })
        .then(data => {
          if (data && data.data) {
            let bidingData = data.data.biding
            let nodeList = data.data.lgtProcessNodes.map(x => x.nodeCode)
            let keyList = data.data.lgtProcessNodes
              .filter(v => v.dataFlag === 'Y')
              .map(x => x.nodeCode)
            for (let i of this.treedata) {
              if (!i.children) {
                i.disabled = !nodeList.includes(i.key)
                if (keyList.includes(i.key)) {
                  i.iconClass = 'el-icon-success'
                } else {
                  i.iconClass = 'el-icon-circle-check'
                }
              } else {
                for (let j of i.children) {
                  j.disabled = !nodeList.includes(j.key)
                  if (keyList.includes(j.key)) {
                    j.iconClass = 'el-icon-success'
                  } else {
                    j.iconClass = 'el-icon-circle-check'
                  }
                }
              }
            }
            this.allParams.biding = bidingData
            this.scopeBidingId = bidingData.bidingId
            this.getTemplateLines(bidingData.templateHeadId)
            this.allParams.biding.currencyChangeDate = bidingData.currencyChangeDate
              ? new Date(bidingData.currencyChangeDate).getTime()
              : null
            this.allParams.biding.bidingEndDatetime = bidingData.bidingEndDatetime
              ? new Date(bidingData.bidingEndDatetime).getTime()
              : null
            this.allParams.biding.enrollEndDatetime = bidingData.enrollEndDatetime
              ? new Date(bidingData.enrollEndDatetime).getTime()
              : null
            this.currentRound = bidingData.currentRound
            this.scopecbpmInstaceId = bidingData.initCbpmInstanceId // 立项审批查询流程ID
            this.scopeBidingNum = bidingData.bidingNum // 招标单号
            this.bidingStatus = bidingData.bidingStatus // 招标状态
            this.auditStatus = bidingData.auditStatus // 审批状态
            if (this.isReadOnly) {
              this.auditStatus = 'APPROVED'
            }

            // 状态控制 根据查询返回的状态再进行判断 通过row传过来的 从其他单据跳转过来的时候信息不全
            // 项目状态=='拟定' && 审批状态=='草稿',可编辑，否则都不可编辑
            // 项目状态=='拟定' && 审批状态=='草稿'
            if (
              this.bidingStatus == 'DRAW_UP' &&
              ['DRAFT', 'REJECTED', 'WITHDRAW'].includes(this.auditStatus)
            ) {
              this.isdisabledTab = false
            } else {
              this.isdisabledTab = true
            }
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    getGroupList (bidingId) {
      this.$http({
        url: '/api-pd/bidInitiating/group/listAll',
        method: 'POST',
        data: { bidingId: bidingId },
        loading: true
      })
        .then(data => {
          if (data && data.data) {
            this.allParams.groupList = data.data
            let userName = this.$store.getters.userInfo.username
            if (data.data.length) {
              this.isGroup = data.data.some(
                i => i.userName == userName && i.judgeFlag == 'Y'
              )
              if (this.isGroup) {
                this.treedata = [
                  {
                    id: 't1',
                    label: this.$t('bidMod.addNewProj'),
                    children: [
                      {
                        id: 't11',
                        label: this.$t('bidMod.projectInformation'),
                        key: 'projectInformation'
                      },
                      {
                        id: 't12',
                        label: this.$t('bidMod.projectRequirement'),
                        key: 'projectRequirement'
                      },
                      {
                        id: 't13',
                        label: this.$t('bidMod.inviteSupplier'),
                        key: 'inviteSupplier'
                      }
                    ]
                  },
                  {
                    id: 't6',
                    label: this.$t('bidMod.openEvalateBid'),
                    children: [
                      {
                        id: 't62',
                        label: this.$t('bidMod.technicalManagement'),
                        key: 'technicalManagement'
                      }
                    ]
                  },
                  {
                    id: 't7',
                    label: this.$t('bidMod.processApproval'),
                    children: [
                      {
                        id: 't15',
                        label: this.$t('bidMod.processApproval'),
                        key: 'processApproval'
                      }
                    ]
                  }
                ]
              }
            }
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    getFileList (bidingId) {
      this.$http({
        url: '/api-pd/bidInitiating/bidFile/listAll',
        method: 'POST',
        data: { bidingId: bidingId },
        loading: true
      })
        .then(data => {
          if (data && data.data) {
            this.innerFiles = data.data.filter(
              v => v.fileType === 'Enterprise'
            )
            this.outerFiles = data.data.filter(v => v.fileType === 'Supplier')
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    getbidFileConfigList (bidingId) {
      this.$http({
        url: '/api-pd/bidInitiating/bidFileConfig/listAll',
        method: 'POST',
        data: { bidingId: bidingId },
        loading: true
      })
        .then(data => {
          if (data && data.data) {
            this.allParams.bidFileConfigList = data.data
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    getTab5List (bidingId) {
      this.$http({
        url: '/api-pd/logistics/biding/getTopInfo',
        method: 'GET',
        params: { bidingId: bidingId },
        loading: true
      })
        .then(data => {
          if (data && data.data) {
            this.bidingConForm = data.data.bidControlTopInfoVO
            this.bidControlItemList = data.data.lgtVendorQuotedHeads
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    // 立即结束投标 [start] by chenzp20
    endImmediatelyDoBidding () {
      this.$http({
        url: '/api-pd/logistics/biding/dueImmediately',
        method: 'get',
        params: {
          bidingId: this.scopeBidingId
        },
        loading: true
      })
        .then(res => {
          this.getFormDetail(this.scopeBidingId)
          this.$message.success(res.message)
        })
        .catch(err => {
          console.log(err)
        })
    },
    getTab63List (bidingId) {
      this.$http({
        url: '/api-pd/logistics/biding/detailCommercialBiding',
        method: 'GET',
        params: { bidingId: bidingId },
        loading: true
      })
        .then(data => {
          if (data && data.data) {
            this.businessItemList = data.data.vendorQuotedHeadList
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    getTab2List (bidingId) {
      this.$http({
        url:
          '/api-pd/logistics/biding/getLgtBidRequirementLineByBidingId',
        method: 'get',
        params: { bidingId: bidingId },
        loading: true
      })
        .then(data => {
          if (data && data.data) {
            this.bidRequirementLineList = data.data.map(i => ({
              ...i,
              expenseItemList: [
                {
                  chargeCode: i.expenseItem,
                  chargeName: i.expenseItemName
                }
              ],
              unitList: [
                {
                  chargeUnit: i.chargeUnit,
                  chargeUnitName: i.chargeUnitName
                }
              ],
              provinceList: [
                {
                  value: i.fromProvinceCode,
                  label: i.fromProvince
                }
              ],
              endProvinceList: [
                {
                  value: i.toProvinceCode,
                  label: i.toProvince
                }
              ],
              startCityList: [
                {
                  value: i.fromCityCode,
                  label: i.fromCity
                }
              ],
              startCountyList: [
                {
                  value: i.fromCountyCode,
                  label: i.fromCounty
                }
              ],
              endCityList: [
                {
                  value: i.toCityCode,
                  label: i.toCity
                }
              ],
              endCountyList: [
                {
                  value: i.toCountyCode,
                  label: i.toCounty
                }
              ]
            }))
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    getTab3List (bidingId) {
      // 邀请供应商
      this.$http({
        url: '/api-pd/logistics/biding/getLgtBidVendorByBidingId',
        method: 'GET',
        params: { bidingId: bidingId },
        loading: true
      })
        .then(data => {
          if (data && data.data) {
            this.tableForm.t13table = data.data
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    backTo () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('biddingProjectList.getQuerydata')
    },
    addbidRequirementLineList () {
      this.bidRequirementLineList.push({
        id: Math.floor(Math.random() * 1000000),
        bidingId: this.scopeBidingId,
        rowNum: this.bidRequirementLineList.length + 1
      })
    },
    delbidRequirementLineList () {
      if (!this.checkLine.length) {
        this.$message({
          message: this.$t('logisticsMod.msgPurchaseApply[10]'), // 请选择要删除的行
          type: 'error'
        })
      }
      let arr = []
      this.bidRequirementLineList.map(i => {
        if (
          !this.checkLine.includes(i.id) &&
          !this.checkLine.includes(i.bidRequirementLineId)
        ) {
          arr.push(i)
        }
      })
      this.bidRequirementLineList = arr
      this.bidRequirementLineList.forEach((r, i) => {
        this.$set(this.bidRequirementLineList[i], 'rowNum', i + 1)
      })
    },
    checkLineList (data) {
      this.checkLine = data.map(i => i.id || i.bidRequirementLineId)
    },
    deleteOneContent (index, row) {
      this.bidRequirementLineList.splice(index, 1)
      this.bidRequirementLineList.forEach((r, i) => {
        this.$set(this.bidRequirementLineList[i], 'rowNum', i + 1)
      })
    },
    downloadTemplate () {
      // 下载模板
      if (!this.scopeBidingId) return
      downloadFileLink(
        '/api-pd/bidInitiating/bidRequirementLine/importModelDownload',
        this.scopeBidingNum + this.$t('bidMod.exportXLS')
      ).catch(err => {
        this.$message.error(err.message)
      })
    },
    // 上传之前 前置参数
    beforeUpload () {
      this.iModal.extraData.bidingId = this.scopeBidingId
      this.iModal.extraData.requireDesc = this.requireDesc
      this.iModal.extraData.pricingType = this.pricingType
      this.iModal.extraData.requirementId = this.scoperequirementId
    },
    // 物料需求上传成功
    uploadSuccess (val) {
      this.getTab2List(this.scopeBidingId) // 查询
      // console.log(val)
      // this.bidRequirementLineList = val ? val.data.data : [];
    },
    // 代理报价 start
    proxyQuote () {
      // 接口查询未报价的供应商，供应商可以代理报价。只允许直接提交不允许暂存。
      this.proxyQuoteVisible = true
    },
    getProgressOfTechBidList () {
      this.$http({
        url: '/api-pd/logistics/biding/detailTechBiding',
        method: 'GET',
        params: {
          bidingId: this.scopeBidingId
        },
        loading: true
      })
        .then(data => {
          this.lgtVendorFiles = data.data.lgtVendorFiles
          this.scheduleForm.scheduleList = data.data.bidShipPeriodList.map(
            i => ({
              ...i,
              provinceList: [
                {
                  value: i.fromProvinceCode,
                  label: i.fromProvince
                }
              ],
              endProvinceList: [
                {
                  value: i.toProvinceCode,
                  label: i.toProvince
                }
              ],
              startCityList: [
                {
                  value: i.fromCityCode,
                  label: i.fromCity
                }
              ],
              startCountyList: [
                {
                  value: i.fromCountyCode,
                  label: i.fromCounty
                }
              ],
              endCityList: [
                {
                  value: i.toCityCode,
                  label: i.toCity
                }
              ],
              endCountyList: [
                {
                  value: i.toCountyCode,
                  label: i.toCounty
                }
              ]
            })
          )
          this.techBidingform.technoSelection =
            data.data.technoSelection || null
        })
        .catch(err => {
          console.log(err)
        })
    }
  }
}
</script>
<style scoped lang="scss">
.the_biddingProjectApproval_wrapper /deep/ {
  .el-button {
    margin-left: 0px;
    margin-right: 10px;
  }
  .el-tabs__header {
    display: none;
  }
  .the_top_item {
    margin: 0;
    padding: 15px 15px 5px;
    // border-bottom: 1px solid #ddd;
    display: flex;
    > li {
      flex: 1;
      list-style: none;
      white-space: nowrap;
      text-overflow: ellipsis;
      overflow: hidden;
      span:first-child {
        font-weight: bolder;
      }
      span:last-child {
        padding: 0 5px;
      }
    }
  }

  .main-header {
    border: none;
  }
  .the_display_content {
    .el-row {
      margin-bottom: 11px;
      font-size: 16px;
      span {
        padding-right: 11px;
        display: inline-block;
        color: #999;
      }
    }
    .the_display_footer {
      text-align: center !important;
    }
  }
  .the_biding_control_row {
    padding: 3px;
    .el-col > span {
      padding-right: 12px;
    }
    .el-col > .el-input {
      width: 122px;
    }
  }
  .el-tabs__content {
    padding: 0;
  }
  .biding_text .el-row--flex {
    margin-bottom: 11px;
  }
  .the_btns > .el-button {
    float: right;
    margin-left: 11px;
  }
  .the_QuickSearch_wrapper {
    display: inline-flex;
    width: 300px;
    padding: 5px;
    span {
      line-height: 33px;
      padding-right: 8px;
    }
  }
  .the_btn_check {
    display: inline;
    padding-left: 10px;
    .el-button {
      min-width: 50px !important;
      padding: 9px 12px !important;
    }
  }
  .the_follow_tender_dialog .el-row {
    margin-bottom: 11px;
    .el-col > span {
      padding-right: 11px;
    }
  }
  .the_check_groups {
    padding: 11px;
    padding-top: 0;
    .el-row {
      margin-bottom: 10px;
    }
  }
  .el-menu-item,
  .el-submenu .el-menu-item {
    height: 26px;
    line-height: 25px;
    padding: 0 20px !important;
    min-width: 150px;
    font-size: 12px;
  }
  .el-submenu__title {
    height: 30px;
    line-height: 30px;
  }
  .el-menu-item.is-active {
    background-color: #88c1f4;
    &::before{
      height: 100% !important;
    }
  }
  .el-menu-item.is-disabled {
    display: none;
  }
  .el-menu-item .el-icon-success {
    color: #13cc33;
  }
  .el-menu-vertical-demo {
    height: 100vh;
    border: solid 1px #e6e6e6;
    border-top: 0;
  }
  .the_projectResult_btns {
    padding: 5px;
  }
  .tab-form-style {
    // padding-bottom: 0 !important;
    // border: none;
  }
  .the_pingxuan_former {
    padding-top: 5px;
    .el-row .el-col {
      height: 40px;
    }
  }
  .el-tabs--border-card {
    margin: 0;
    border: none;
  }
  .flex-container-right {
    position: relative;
    padding-bottom: 50px;
    .c-toolbar {
      position: absolute;
      width: 100%;
      padding: 9px 24px;
      left: 0;
      bottom: 0;
      transition: padding-left 0.28s;
      box-sizing: border-box;
      background-color: #ffffff;
      box-shadow: 0 -1px 2px 0 rgba(182, 182, 182, 0.5);
      overflow: hidden;
      z-index: 999;
    }
    .c-toolbar-center {
      float: right;
      display: flex;
      align-items: center;
    }
  }
}
</style>
<style>
#app .hideSidebar .bidMentLeftSlide .el-submenu > .el-submenu__title {
  padding-left: 15px !important;
}
.paddingBt {
  padding-bottom: 5px;
}
</style>
