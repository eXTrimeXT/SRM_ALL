<template>
  <el-container class="the_demotion_detail flex-container" direction="vertical">
    <el-main>
      <el-collapse v-model="activeDims" class="tab-form-style">
        <!-- 头信息 -->
        <el-collapse-item
          ref="headerInform"
          :title="$t('priceModel.costElement.baseInfo')"
          name="1"
        >
          <el-form
            ref="companyDemotion"
            :model="companyDemotion"
            :rules="demotionRules"
            class="form-fill-style"
            :disabled="curOpt === 'view'"
          >
            <srm-row>
              <!-- 升降机供应商 -->
              <srm-col :initCol="4">
                <el-form-item
                  :label="$t('vendorMod.relegation.relegationVendor')"
                  prop="companyName"
                >
                  <QuickSearch
                    :show-input="companyDemotion.companyName"
                    show-key="companyName"
                    :scope-data="companyDemotion"
                    name="scc_sup_company_info2"
                    @close-quicksearch="getCompany"
                  />
                </el-form-item>
              </srm-col>
              <!-- 升降级类型 -->
              <srm-col :initCol="4">
                <el-form-item
                  :label="$t('vendorMod.relegation.relegationType')"
                  prop="demotionType"
                >
                  <DictSelect
                    v-model="companyDemotion.demotionType"
                    code="DEMOTION_TYPE"
                    @change="setDemotionType"
                  />
                </el-form-item>
              </srm-col>
              <!-- 单据名称 -->
              <srm-col :initCol="2">
                <el-form-item :label="$t('vendorMod.relegation.estimateName')" prop="demotionName">
                  <el-input v-model="companyDemotion.demotionName" disabled />
                </el-form-item>
              </srm-col>
              <!-- 创建人 -->
              <srm-col :initCol="4">
                <el-form-item :label="$t('vendorMod.relegation.creator')">
                  <el-input v-model="companyDemotion.createdBy" disabled />
                </el-form-item>
              </srm-col>
              <!-- 创建时间 -->
              <srm-col :initCol="4">
                <el-form-item :label="$t('vendorMod.relegation.creationTime')">
                  <el-date-picker
                    v-model="companyDemotion.creationDate"
                    :format="$formatDatePickerTime"
                    value-format="yyyy-MM-dd HH:mm:ss"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <!-- 降级生效时间 -->
              <srm-col v-if="companyDemotion.demotionType === 'DEMOTION_TO_RED' || companyDemotion.demotionType === 'DEMOTION_TO_BLACK'" :initCol="4">
                <el-form-item
                  :label="$t('vendorMod.relegation.effectTimeDowngrade')"
                  prop="demotionDate"
                >
                  <el-date-picker
                    v-model="companyDemotion.demotionDate"
                    type="date"
                    :format="$formatDatePicker"
                    value-format="yyyy-MM-dd"
                    :picker-options="pickerOptions"
                    :placeholder="$t('vendorMod.relegation.optionDate')"
                  />
                </el-form-item>
              </srm-col>
              <!-- 评审人 -->
              <srm-col :initCol="4">
                <el-form-item :label="$t('vendorMod.relegation.assessor')">
                  <QuickSearch
                    multi-select
                    :show-input="companyDemotion.reviewUserNicknames"
                    show-key="nickname"
                    :scope-data="companyDemotion"
                    name="scc_rbac_user_display"
                    @close-quicksearch="getUserdemandObj"
                  />
                </el-form-item>
              </srm-col>
              <!-- 降级说明 -->
              <srm-col :initCol="2">
                <p>
                  <el-tooltip effect="dark" placement="left" popper-class="atooltip">
                    <!-- <div slot="content" style="font-size: 14px; color: black; font-weight: 400">
                      {{ $t('vendorMod.relegation.operational') }}<br>
                      需要根据升降级的具体原因，选择采购部门或采购以外部门人员评审。<br>
                      <br>
                      （1）绿牌供应商降级黄牌——升降级类型选择“降级至黄牌”，单据提交审批通过后，品类预警状态从“绿牌”降级到“黄牌”。<br>
                      <br>
                      （2）绿牌供应商降级红牌——升降级类型选择“降级至红牌”，单据提交审批通过后，品类预警状态从“绿牌”降级到“红牌”。<br>
                      <br>
                      （3）黄牌供应商降级红牌——将降级类型选择“降级至红牌”，单据提交审批通过后，品类预警状态从“黄牌”降级到“红牌”。<br>
                      <br>
                      （4）黄牌供应商升级绿牌——将降级类型选择“黄牌改善升级”，单据提交审批通过后，品类预警状态从“黄牌”升级到“绿牌”。<br>
                      <br>
                      （5）红牌供应商升级黄牌——将降级类型选择“红牌改善升级”，单据提交审批通过后，品类预警状态从“红牌”升级到“黄牌”。<br>
                      <br>
                    </div> -->
                    <div slot="content" style="font-size: 14px; color: black; font-weight: 400">
                      {{ $t('vendorMod.relegation.operational') }}<br>
                      {{ $t("vendorMod.relegation.operationalInfo.0") }}<br>
                      <br>
                      {{ $t("vendorMod.relegation.operationalInfo.1") }}<br>
                      <br>
                      {{ $t("vendorMod.relegation.operationalInfo.2") }}<br>
                      <br>
                      {{ $t("vendorMod.relegation.operationalInfo.3") }}<br>
                      <br>
                      {{ $t("vendorMod.relegation.operationalInfo.4") }}<br>
                      <br>
                      {{ $t("vendorMod.relegation.operationalInfo.5") }}<br>
                      <br>
                    </div>
                    <el-button type="text">
                      {{
                        $t('vendorMod.relegation.demotionInstructions')
                      }}
                    </el-button>
                  </el-tooltip>
                </p>
              </srm-col>
              <!-- 升降级原因说明 -->
              <srm-col :initCol="1">
                <el-form-item
                  :label="$t('vendorMod.relegation.relegationReasons')"
                  prop="demotionDesrc"
                >
                  <el-input
                    v-model="companyDemotion.demotionDesrc"
                    type="textarea"
                    :autosize="{ minRows: 2, maxRows: 5 }"
                    maxlength="500"
                    show-word-limit
                  />
                </el-form-item>
              </srm-col>
              <!-- 起草人意见 -->
              <srm-col :initCol="1">
                <el-form-item
                  :label="$t('vendorMod.relegation.drafterOpinion')"
                  prop="drafterOpinion"
                >
                  <el-input
                    v-model="companyDemotion.drafterOpinion"
                    type="textarea"
                    :autosize="{ minRows: 2, maxRows: 5 }"
                    maxlength="500"
                    show-word-limit
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-form>
        </el-collapse-item>
        <!--升降级品类-->
        <el-collapse-item
          ref="relegationCategory"
          :title="$t('vendorMod.relegation.relegationCategory')"
          name="2"
        >
          <template slot="title">
            <em class="red">*</em>{{ $t('vendorMod.relegation.relegationCategory') }}
          </template>
          <QuickSearch
            v-if="curOpt !== 'view'"
            name="scc_sup_company_demotion_category_url"
            :disabled="!companyDemotion.companyName || !companyDemotion.demotionType"
            :preQueryData="preQueryData"
            :btnTitle="$t('common.new')"
            showButton
            multiSelect
            style="margin-bottom: 5px"
            @close-quicksearch="catSelectHandel"
          />
          <el-table
            :data="companyDemotionCategories"
            style="width: 100%"
            border
            stripe
            max-height="250px"
          >
            <el-table-column type="index" align="left" width="60px" :label="$t('common.sort')" />
            <el-table-column
              align="left"
              prop="categoryFullName"
              :label="$t('vendorMod.relegation.categoryFullPath')"
              min-width="150"
            />
            <el-table-column
              align="left"
              prop="categoryName"
              :label="$t('vendorMod.relegation.categoryName')"
              min-width="150"
            />
            <el-table-column
              align="left"
              prop="operation"
              :label="$t('common.operation')"
              width="90"
            >
              <template slot-scope="scope">
                <el-button
                  v-if="curOpt !== 'view'"
                  :disabled="companyDemotion.demotionType === 'DEMOTION_TO_BLACK'"
                  type="text"
                  @click="delIndicatorLine(scope.$index, scope.row)"
                >
                  {{ $t('common.delete') }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-collapse-item>
        <!-- 升降机业务实体 -->
        <el-collapse-item
          ref="relegationEntity"
          :title="$t('vendorMod.relegation.relegationEntity')"
          name="3"
        >
          <p v-if="companyDemotion.demotionType === 'DEMOTION_TO_REGISTERED'" style="margin: 0">
            <el-button
              type="primary"
              class="detail-pbtn"
              style="float: left"
              @click="confirmCheckOne"
            >
              {{
                checkEnableFlag === 'Y'
                  ? $t('vendorMod.relegation.checkAll')
                  : $t('vendorMod.relegation.deselectAll')
              }}
            </el-button>
          </p>
          <el-table
            :data="companyDemotionOrgs"
            style="width: 100%"
            border
            stripe
            :cell-class-name="cellClassFn"
            max-height="250px"
          >
            <el-table-column type="index" align="left" width="60px" :label="$t('common.sort')" />
            <el-table-column
              align="left"
              prop="categoryName"
              :label="$t('vendorMod.relegation.relegationCategory')"
              width="120px"
              sortable
            />
            <el-table-column
              align="left"
              prop="orgName"
              :label="$t('vendorMod.relegation.relegationEntity')"
              min-width="130px"
              sortable
            />
            <!-- 目前预警状态 -->
            <el-table-column prop="warningStatus" :label="$t('relegationEntity.key1')" sortable>
              <template slot-scope="scope">
                <span>{{ $getDictLabel('WARNING_STATUS',scope.row.warningStatus) }}</span>
              </template>
            </el-table-column>
            <!-- 最近第一次绩效成绩 -->
            <el-table-column prop="firstScore" label="$t('relegationEntity.key2')" sortable />
            <!-- 最近第二次绩效成绩 -->
            <el-table-column prop="secondScore" label="$t('relegationEntity.key3')" sortable />
            <!-- 最近第三次绩效成绩 -->
            <el-table-column prop="thirdScore" label="$t('relegationEntity.key4')" sortable />
            <!-- 近三次的绩效成绩 -->
            <el-table-column label="$t('cusEntry.supplement20250205.recentThreePerformanceScores')">
              <template slot-scope="scope">
                <el-button type="text" @click="seePerformence(scope.row)">
                  <!-- 查看明细 -->
                  {{ $t("accountMod.viewDetail") }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-collapse-item>
        <!-- 改善单据 -->
        <!-- 改善单据 -->
        <el-collapse-item ref="improveDocuments" title="$t('vendorMod.relegation.improveDocuments')" name="4">
          <el-table :data="vendorImproveData" border stripe max-height="250px">
            <el-table-column type="index" width="60px" :label="$t('common.sort')" />
            <el-table-column
              v-for="(item,index) in vendorImproveColumn"
              :key="index"
              :prop="item.prop"
              :label="item.label"
              show-overflow-tooltip
            >
              <template slot-scope="scope">
                <template v-if="item.prop === 'improveProject'">
                  <el-button type="text" @click="item.callback(scope.row)">
                    {{ scope.row[item.prop] }}
                  </el-button>
                </template>
                <template v-else-if="item.prop === 'status'">
                  <span>{{ $getDictLabel('VENDOR_IMPROVE_STATUS',scope.row[item.prop]) }}</span>
                </template>
                <template v-else>
                  <span>{{ scope.row[item.prop] }}</span>
                </template>
              </template>
            </el-table-column>
          </el-table>
        </el-collapse-item>
        <!--附件-->
        <el-collapse-item ref="attachment" :title="$t('vendorMod.relegation.accessory')" name="5">
          <FileDynamic
            ref="sceneAttachment"
            v-model="dataAtt"
            scene-module-code="SCENE_VENDOR_DEMOTION_ATTACHMENT"
            :business-id="companyDemotion.companyDemotionId"
            :editable="curOpt === 'add' || curOpt === 'edit'"
          />
        </el-collapse-item>
      </el-collapse>
      <CToolbar>
        <template slot="right">
          <el-button @click="toBack">
            {{ $t('common.backTo') }}
          </el-button>
          <el-button v-if="curOpt !== 'view'" type="primary" @click="saveHandle">
            {{ $t('common.save') }}
          </el-button>
          <el-button v-if="curOpt !== 'view'" type="primary" @click="submitHandle">
            {{ $t('common.submit') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
    <!-- 请再次确认供应商升降级的品类与OU范围正确，否则会造成不可逆的后果 -->
    <el-dialog
      :title="$t('vendorMod.relegation.reminder')"
      :visible.sync="dialogVisible"
      width="30%"
    >
      <span>{{ $t('vendorMod.relegation.OUrange') }}</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">
          {{ $t('common.cancel') }}
        </el-button>
        <el-button type="primary" @click="submitSend">
          {{ $t('common.confirm') }}
        </el-button>
      </span>
    </el-dialog>
    <!-- 查看绩效明细 -->
    <!-- 绩效明细 -->
    <el-dialog title="$t('perfMod.detailsReview')" width="90%" :visible.sync="performenceDialog.show">
      <el-table :data="performenceDialog.tableData" border stripe>
        <el-table-column type="index" width="60px" :label="$t('common.sort')" />
        <el-table-column
          v-for="(item,index) in performenceDialog.tableColumn"
          :key="item.prop + index"
          :prop="item.prop"
          :label="item.label"
          show-overflow-tooltip
        />
      </el-table>
      <div slot="footer" />
    </el-dialog>
  </el-container>
</template>
<script>
import { createDictClass } from 'lib@/utils/dict/dict-utils'
import CCategorySelect from 'lib@/components/c-category-select'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import QuickSearch from 'lib@/components/QuickSearch'
import FileDynamic from '@/library/components/c-file-management/file-dynamic'

export default {
  name: 'VendorDemotionDetail',
  components: {
    CToolbar,
    QuickSearch,
    FileDynamic,
    CCategorySelect
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      dictClass: createDictClass({
        DEMOTION_TYPE: []
      }),
      activeDims: ['1', '2', '3', '4', '5'],
      companyDemotion: {
        companyId: null,
        companyCode: null,
        companyName: null,
        demotionType: null,
        demotionName: null,
        createdBy: null,
        creationDate: null,
        demotionDate: null,
        reviewUserNicknames: null,
        reviewUserIdList: null,
        demotionDesrc: null,
        demotionNumber: null,
        companyDemotionId: null,
        status: null
      },
      demotionRules: {
        demotionType: [
          {
            required: true,
            message: this.$t('vendorMod.relegation.relegationType')
          }
        ],
        companyName: [
          {
            required: true,
            message: this.$t('vendorMod.relegation.selectSupplier')
          }
        ],
        demotionDesrc: [
          {
            required: true,
            message: this.$t('vendorMod.relegation.reasonsPromotionDemotion')
          }
        ],
        demotionDate: [
          {
            required: true,
            message: this.$t('vendorMod.relegation.degradedTime')
          }
        ]
      },
      pickerOptions: {
        disabledDate (time) {
          return time.getTime() < Date.now() - 8.64e7
        }
      },
      companyDemotionCategories: [],
      companyDemotionOrgs: [],
      vendorImproveData: [],
      vendorImproveColumn: [
        {
          prop: 'organizationName',
          label: this.$t('components.organization.ORG')  // '业务实体'
        },
        {
          prop: 'categoryName',
          label: this.$t('common.category')  // '品类'
        },
        {
          prop: 'improveTitle',
          label: this.$t('perfMod.improveTitle')  // '改善主题'
        },
        {
          prop: 'improveProject',
          label: this.$t('perfMod.improveProject'),  // '改善项目'
          callback: row => {
            console.log('row:::', row)
            this.$router.push({
              name: 'vendorImprovement',
              params: {
                from: 'portrait',
                row
              }
            })
          }
        },
        {
          prop: 'explanation',
          label: this.$t('vendorMod.operationMemo')  // '说明'
        },
        {
          prop: 'respFullName',
          label: this.$t('relegationEntity.key22')  // '责任人跟进'
        },
        {
          prop: 'status',
          label: this.$t('components.stratProcess.headers.docStatusValue')  // '状态'
        }
      ],
      dataAtt: [],
      dialogVisible: false,
      performenceDialog: {
        show: false,
        tableData: [],
        tableColumn: [
          {
            prop: 'projectName',
            label: this.$t('bidMod.bidingName')  // '项目名称'
          },
          {
            prop: 'companyName',
            label: this.$t('common.companyName')  // '供应商名称'
          },
          {
            prop: 'organizationName',
            label: this.$t('components.organization.ORG')  // '业务实体'
          },
          {
            prop: 'perStartMonth',
            label: this.$t('perfMod.perStartMonth')  // '绩效开始月份'
          },
          {
            prop: 'perEndMonth',
            label: this.$t('perfMod.perEndMonth')  // '绩效结束月份'
          },
          {
            prop: 'categoryName',
            label: this.$t('common.category')  // '品类'
          },
          {
            prop: 'scoreAttribute1',
            label: this.$t('supplierRating.averageScore')  // '平均品质得分'
          },
          {
            prop: 'scoreAttribute2',
            label: this.$t('supplierRating.averageCostScore')  // '平均成本得分'
          },
          {
            prop: 'scoreAttribute3',
            label: this.$t('supplierRating.averageDeliveryScore')  // '平均交付得分'
          },
          {
            prop: 'scoreAttribute4',
            label: this.$t('supplierRating.averageServiceScore')  // '平均服务得分'
          },
          {
            prop: 'scoreAttribute5',
            label: this.$t('supplierRating.averageTechnicalScore')  // '平均技术得分'
          },
          {
            prop: 'score',
            label: this.$t('supplierRating.meanCompositeScore')  // '平均综合得分'
          },
          {
            prop: 'levelName',
            label: this.$t('bidMod.perfLevelName')  // '绩效等级'
          },
          {
            prop: 'rank',
            label: this.$t('perfMod.rankAll')  // '绩效排名'
          }
        ]
      },
      checkEnableFlag: 'Y',
      curOpt: 'add',
      curOrderId: null // 单据Id
    }
  },
  computed: {
    preQueryData () {
      return { 't.COMPANY_ID': this.companyDemotion.companyId, 't.DEMOTION_TYPE': this.companyDemotion.demotionType }
    },
    demotionTypeList () {
      // 升降级类型
      return this.dictClass.getDict('DEMOTION_TYPE')
    }
  },
  created () {
    if (this.$attrs.params.flag !== 'add') {
      this.curOrderId = this.$attrs.params.orderId // 单据Id
      this.getOrderFormDetail(this.$attrs.params.orderId)
    } else {
      this.$nextTick(() => {
        this.$refs.sceneAttachment.loadFileInfo()
      })
    }
    this.curOpt = this.$attrs.params.flag
  },
  methods: {
    getCompany (val, scope) {
      scope.companyId = val ? val.companyId : null
      scope.companyCode = val ? val.companyCode : null
      scope.companyName = val ? val.companyName : null
      this.companyDemotionOrgs = []
      this.companyDemotionCategories = []
      this.setDemotionName()
      if (val.companyId) this.getImproveData(val.companyId)
    },
    getImproveData (vendorId) {
      this.$http({
        url: '/api-pef/vendorImprove/getImproveFormDtoByVendorId',
        method: 'GET',
        params: {
          vendorId
        }
      }).then(res => {
        console.log('res:::', res)
        this.vendorImproveData = res.data || []
      })
    },
    setDemotionType (val) {
      this.companyDemotionOrgs = []
      this.companyDemotionCategories = []
      this.setDemotionName()
    },
    setDemotionName () {
      if (!this.companyDemotion.companyName || !this.companyDemotion.demotionType) return
      let demotionTypeName = (
        this.demotionTypeList.find(v => v.value === this.companyDemotion.demotionType) || {}
      ).label
      this.companyDemotion.demotionName = `${this.companyDemotion.companyName}-${demotionTypeName}`
    },
    getUserdemandObj (val, scope) {
      this.companyDemotion.reviewUserIdList = ''
      this.companyDemotion.reviewUserNicknames = ''
      if (val.length > 0) {
        scope.reviewUserIdList = val.map(v => v.userId)
        scope.reviewUserNicknames = val.map(v => v.nickname).join(';')
      } else {
        scope.reviewUserIdList = val.userId || ''
        scope.reviewUserNicknames = val.nickname || ''
      }
    },
    catSelectHandel (data) {
      if (data.length > 0) {
        let categoryIdArr = this.companyDemotionCategories.map(v => v.categoryId)
        for (let item of data) {
          if (!categoryIdArr.includes(item.categoryId)) {
            delete item.companyDemotionCategoryId
            this.companyDemotionCategories.push(item)
          }
        }
        this.$http({
          url: '/api-sup/demotion/company-demotion-org/queryOrgsByParam',
          method: 'POST',
          data: {
            companyId: this.companyDemotion.companyId,
            categoryIds: this.companyDemotionCategories.map(v => v.categoryId),
            demotionType: this.companyDemotion.demotionType
          },
          loading: true
        }).then(data => {
          this.companyDemotionOrgs = data.data.map(({ enableFlag, ...rest }) => {
            return {
              ...rest,
              enableFlag: 'Y'
            }
          })

          if (this.companyDemotion.demotionType === 'DEMOTION_TO_REGISTERED') {
            for (let item of this.companyDemotionOrgs) {
              item['enableFlag'] = 'N'
            }
          }

          let companyFlagArray = this.companyDemotionOrgs.filter(v => v.enableFlag === 'N')
          if (companyFlagArray.length > 0) {
            this.checkEnableFlag = 'Y'
          } else if (companyFlagArray.length === 0) {
            this.checkEnableFlag = 'N'
          }
        })
      }
    },
    // 删除指标行数据
    delIndicatorLine (index, row) {
      this.companyDemotionCategories.splice(index, 1)
      this.getOrgListByCategoryIds(row.categoryId)
    },
    getOrgListByCategoryIds (categoryId) {
      this.$http({
        url: '/api-sup/demotion/company-demotion-org/queryOrgsByParam',
        method: 'POST',
        data: {
          companyId: this.companyDemotion.companyId,
          categoryIds: [categoryId],
          demotionType: this.companyDemotion.demotionType || ''
        },
        loading: true
      }).then(data => {
        let deleteOrgItemsArr = data.data.map(v => `${v.categoryId}-${v.orgId}`)
        let companyDemotionOrgsArray = this.companyDemotionOrgs
        let setArray = []
        // 需要删除的业务实体的信息--去到总的里面遍历剔除掉这些
        for (let i = 0; i < companyDemotionOrgsArray.length; i++) {
          if (
            deleteOrgItemsArr.includes(
              `${companyDemotionOrgsArray[i].categoryId}-${companyDemotionOrgsArray[i].orgId}`,
            )
          ) {
            delete companyDemotionOrgsArray[i]
          }
        }
        setArray = companyDemotionOrgsArray.filter(v => v !== undefined)
        this.companyDemotionOrgs = setArray
      })
    },
    confirmCheckOne () {
      if (this.checkEnableFlag === 'Y') {
        this.companyDemotionOrgs.forEach(item => {
          item.enableFlag = 'Y'
        })
        this.checkEnableFlag = 'N'
      } else if (this.checkEnableFlag === 'N') {
        this.companyDemotionOrgs.forEach(item => {
          item.enableFlag = 'N'
        })
        this.checkEnableFlag = 'Y'
      }
    },
    cellClassFn ({ row, column }) {
      if (['firstScore', 'secondScore', 'thirdScore'].includes(column.property)) {
        return 'primary-color'
      }
    },
    seePerformence (row) {
      this.performenceDialog.show = true
      this.getPerformenceScore(row)
    },
    getPerformenceScore (row) {
      let { companyId } = this.companyDemotion
      if (!companyId) return
      this.$http({
        url: '/api-pef/scoring/perfOverallScore/listPerfByCompanyIdAndOrgIdListAndCategoryIdList',
        method: 'POST',
        data: { ...row, companyId, orgIdList: [row.orgId], categoryIdList: [row.categoryId] },
        loading: true
      }).then(res => {
        console.log('res:::', res)
        let { data } = res || []
        this.performenceDialog.tableData = data
      })
    },
    /// /////////////////////
    // 查询单据详情
    getOrderFormDetail (companyDemotionId) {
      this.$http({
        url: '/api-sup/demotion/company-demotion/getDemotionById',
        method: 'GET',
        params: { companyDemotionId: companyDemotionId },
        loading: true
      }).then(data => {
        this.companyDemotion = data.data.companyDemotion || {}
        // 控制附件操作
        if (['DRAFT', 'REJECTED', 'WITHDRAW'].includes(data.data.companyDemotion.status)) {
          this.curOpt = 'edit'
        } else {
          this.curOpt = 'view'
        }
        this.companyDemotionOrgs = data.data.companyDemotionOrgs
        let companyDemotionOrgsArray = this.companyDemotionOrgs.filter(v => v.enableFlag === 'N')
        if (companyDemotionOrgsArray.length > 0) {
          this.checkEnableFlag = 'Y'
        }

        this.companyDemotionCategories = data.data.companyDemotionCategories
        this.vendorImproveData = data.data.vendorImproveFormList || []

        this.$nextTick(() => {
          this.$refs.sceneAttachment.loadFileInfo()
        })
      })
    },
    getNowFormatDate () {
      let date = new Date()
      let seperator1 = '-'
      let year = date.getFullYear()
      let month = date.getMonth() + 1
      let strDate = date.getDate()
      let h = date.getHours()
      let mm = date.getMinutes()
      let s = date.getSeconds()
      if (month >= 1 && month <= 9) {
        month = '0' + month
      }
      if (strDate >= 0 && strDate <= 9) {
        strDate = '0' + strDate
      }
      if (h >= 0 && h <= 9) {
        h = '0' + h
      }
      if (mm >= 0 && mm <= 9) {
        mm = '0' + mm
      }
      if (s >= 0 && s <= 9) {
        s = '0' + s
      }
      let currentdate =
        year + seperator1 + month + seperator1 + strDate + ' ' + h + ':' + mm + ':' + s
      return currentdate
    },

    indexClickTo (code) {
      const anchorEle = this.$refs[code].$el
      if (anchorEle) {
        anchorEle.scrollIntoView(true)
      }
    },
    // 返回
    toBack () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('vendorDemotionList.getQuerydata')
    },
    // 提交
    submitHandle () {
      this.$refs.companyDemotion.validate(valid => {
        if (!valid) {
          this.__jump_error__(
            'companyDemotion',
            null,
            this.$t('vendorMod.relegation.requiredFields')
          )
          this.indexClickTo('headerInform')
          return false
        } else {
          this.dialogVisible = true
        }
      })
    },
    submitSend () {
      if (this.companyDemotion.demotionType === 'DEMOTION_TO_YELLOW') {
        let nowDate = this.getNowFormatDate()
        this.companyDemotion.demotionDate = nowDate
      }
      this.$http({
        url: '/api-sup/demotion/company-demotion/submit',
        method: 'POST',
        data: {
          companyDemotion: this.companyDemotion,
          companyDemotionOrgs: this.companyDemotionOrgs,
          companyDemotionCategories: this.companyDemotionCategories,
          fileUploads: this.dataAtt,
          vendorImproveFormList: this.vendorImproveData
        },
        loading: true
      })
        .then(data => {
          this.$message.success(this.$t('vendorMod.relegation.operateSuccessfully'))
          this.toBack()
        })
        .catch(err => {
          console.log(err)
        })
    },
    saveHandle () {
      this.$refs.companyDemotion.validate(valid => {
        if (!valid) {
          this.__jump_error__(
            'companyDemotion',
            null,
            this.$t('vendorMod.relegation.requiredFields')
          )
          this.indexClickTo('headerInform')
          return false
        } else {
          this.$http({
            url: '/api-sup/demotion/company-demotion/saveTemporary',
            method: 'POST',
            data: {
              companyDemotion: this.companyDemotion,
              companyDemotionOrgs: this.companyDemotionOrgs,
              companyDemotionCategories: this.companyDemotionCategories,
              fileUploads: this.dataAtt,
              vendorImproveFormList: this.vendorImproveData
            },
            loading: true
          })
            .then(data => {
              if (data.code == '0') {
                this.$message.success(data.message)
                this.getOrderFormDetail(data.data)
              } else {
                this.$message.error(data.message)
              }
            })
            .catch(err => {
              console.log(err)
            })
        }
      })
    }
  }
}
</script>
<style scoped lang="scss">
.the_demotion_detail {
  .el-table .el-date-editor {
    width: 135px;
  }
  .el-collapse-item__content > .el-button {
    margin-bottom: 5px;
  }
  .pefScoreInput {
    .el-input-number__decrease {
      display: none !important;
    }
    .el-input-number__increase {
      display: none !important;
    }
  }
}
</style>
<style>
.orgCatPage .c-pagination {
  margin: 10px 5px;
}
.orgCatPage .c-pagination .el-input__inner {
  height: 24px !important;
}
.the_demotion_detail .pefScoreInput .el-input-number__increase,
.the_demotion_detail .pefScoreInput .el-input-number__decrease {
  display: none;
}
.red {
  color: red;
  margin-right: 5px;
}
.atooltip {
  background-color: rgba(250, 193, 7, 0.993) !important;
}
.primary-color {
  color: #0077ff;
}
.el-collapse{
  margin-bottom: 25px;
}
</style>
