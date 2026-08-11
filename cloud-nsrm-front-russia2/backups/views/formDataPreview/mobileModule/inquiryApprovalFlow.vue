<template>
  <div class="model">
    <el-collapse
      v-model="activeNames"
      class="modelA"
      @change="handleChange"
    >
      <el-collapse-item
        title="价格审批单"
        name="1"
      >
        <el-row>
          <el-col :span="6">
            <div class="bg-purple">
              创建人：
            </div>
          </el-col>
          <el-col
            :span="18"
          >
            <div class="bg-purple-light">
              {{ this.createByName }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col
            :span="8"
          >
            <div class="bg-purple">
              部门：
            </div>
          </el-col>
          <el-col
            :span="16"
          >
            <div class="bg-purple-light">
              {{ this.createByDept }}
            </div>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              价格审批单号:
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.approvalNo }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col
            :span="4"
          >
            <div class="bg-purple">
              标题:
            </div>
          </el-col>
          <el-col
            :span="20"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.ceeaTitle }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              寻源方式:
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ $getDictLabel("Sourc_Type",this.requirementHead.sourceType) }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              决标方式:
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ $getDictLabel("BID_DECIDE_METHOD",this.requirementHead.ceeaAwareWay) }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              配额分配方法:
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ $getDictLabel("CeeaQuotaAllocationType",this.requirementHead.ceeaAllocationType) }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="6">
            <div class="bg-purple">
              需求概述:
            </div>
          </el-col>
          <el-col
            :span="18"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.ceeaRequirementOverview }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="6">
            <div class="bg-purple">
              说明:
            </div>
          </el-col>
          <el-col
            :span="18"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.ceeaDescription }}
            </div>
          </el-col>
        </el-row>
      </el-collapse-item>

      <el-collapse-item
        title="附件"
        name="3"
        class="model_LineList"
      >
        <div class="card">
          <el-table
            :data="approvalFileList"
            style="width: 100%"
          >
            <el-table-column
              type="index"
              label="序号"
              width="100"
            />
            <el-table-column
              prop="fileName"
              label="附件名称"
              min-width="200"
            >
              <template slot-scope="scope">
                <c-download-link
                  :id="scope.row.fileRelationId"
                  :name="scope.row.fileName"
                  ellipsis
                  class="download-link-item"
                />
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-collapse-item>
      <el-collapse-item
        title="中标行信息"
        name="4"
        class="model_LineList"
      >
        <template v-if="tableData.length > 10">
          <div class="LineList">
            <el-pagination
              :page-size="pagesize"
              layout="total,prev, next"
              :total="tableData.length"
              @current-change="current_change"
            />
          </div>
          <div class="card">
            <el-table
              :data="
                tableData.slice(
                  (currentPage - 1) * pagesize,
                  currentPage * pagesize
                )
              "
              style="width: 100%; margin-top: 6px;"
            >
              <el-table-column
                type="index"
                label="序号"
              />
              <el-table-column
                prop="ouName"
                label="OU组名称"
              />
              <el-table-column
                prop="orgName"
                label="业务实体"
              />
              <el-table-column
                prop="organizationName"
                label="库存组织"
                width="150"
              />
              <el-table-column
                prop="vendorName"
                label="供应商名称"
                width="150"
              />
              <el-table-column
                prop="itemCode"
                label="物料编码"
              />
              <el-table-column
                prop="itemName"
                label="物料描述"
                width="150"
              />
              <el-table-column
                prop="taxPrice"
                label="含税单价"
              />
              <el-table-column label="币种">
                <template slot-scope="scope">
                  {{ scope.row.currencyName }}
                </template>
              </el-table-column>
              <el-table-column
                prop="taxRate"
                label="税率%"
              />
              <el-table-column
                prop=""
                label="付款条款"
              >
                <template slot-scope="scope">
                  <el-button
                    :ref="'the_read_item' + scope.$index"
                    type="primary"
                    @click="openOneBidItem(scope.$index, scope.row)"
                  >
                    查看
                  </el-button>
                </template>
              </el-table-column>
              <el-table-column
                prop="lAndT"
                label="L/T"
              />
              <el-table-column
                align="center"
                prop="startTime"
                label="价格有效期自"
                width="170"
              >
                <template slot-scope="scope">
                  <el-date-picker
                    v-model="scope.row.startTime"
                    disabled
                    type="date"
                    format="yyyy-MM-dd"
                    value-format="yyyy-MM-dd"
                  />
                </template>
              </el-table-column>
              <el-table-column

                align="center"
                prop="endTime"
                label="价格有效期至"
                width="170"
              >
                <template slot-scope="scope">
                  <el-date-picker
                    v-model="scope.row.endTime"
                    disabled
                    type="date"
                    format="yyyy-MM-dd"
                    value-format="yyyy-MM-dd"
                  />
                </template>
              </el-table-column>
            </el-table>
          </div>
        </template>
        <div
          v-else
          class="card"
        >
          <el-table
            :data="tableData"
            style="width: 100%"
          >
            <el-table-column
              type="index"
              label="序号"
            />
            <el-table-column
              prop="ouName"
              label="OU组名称"
            />
            <el-table-column
              prop="orgName"
              label="业务实体"
            />
            <el-table-column
              prop="organizationName"
              label="库存组织"
              width="150"
            />
            <el-table-column
              prop="vendorName"
              label="供应商名称"
              width="150"
            />
            <el-table-column
              prop="itemCode"
              label="物料编码"
            />
            <el-table-column
              prop="itemName"
              label="物料描述"
              width="150"
            />
            <el-table-column
              prop="taxPrice"
              label="含税单价"
            />
            <el-table-column label="币种">
              <template slot-scope="scope">
                {{ scope.row.currencyName }}
              </template>
            </el-table-column>
            <el-table-column
              prop="taxRate"
              label="税率%"
            />

            <el-table-column
              prop="historyTaxPrice"
              label="历史价格"
            />
            <el-table-column
              prop=""
              label="付款条款"
            >
              <template slot-scope="scope">
                <el-button
                  :ref="'the_read_item' + scope.$index"
                  type="primary"
                  @click="openOneBidItem(scope.$index, scope.row)"
                >
                  查看
                </el-button>
              </template>
            </el-table-column>
            <el-table-column
              prop="lAndT"
              label="L/T"
            />
            <el-table-column
              align="center"
              prop="startTime"
              label="价格有效期自"
              width="170"
            >
              <template slot-scope="scope">
                <el-date-picker
                  v-model="scope.row.startTime"
                  disabled
                  type="date"
                  format="yyyy-MM-dd"
                  value-format="yyyy-MM-dd"
                />
              </template>
            </el-table-column>
            <el-table-column

              align="center"
              prop="endTime"
              label="价格有效期至"
              width="170"
            >
              <template slot-scope="scope">
                <el-date-picker
                  v-model="scope.row.endTime"
                  disabled
                  type="date"
                  format="yyyy-MM-dd"
                  value-format="yyyy-MM-dd"
                />
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-collapse-item>
    </el-collapse>
    <!-- 弹框---付款条款查看--->
    <srm-dialog
      title="付款条款查看"
      size="small"
      :visible.sync="diaVisible"
      :close-on-click-modal="false"
    >
      <el-table
        :data="globalApprovalBiddingItemPaymentTermList"
        style="width: 100%"
        border
        height="251px"
      >
        <el-table-column
          align="center"
          type="index"
          width="50"
        />
        <el-table-column
          align="center"
          prop="paymentDayCode"
          label="付款账期"
          min-width="150"
        >
          <template slot-scope="scopeSon">
            {{ $getDictLabel("PAYMENT_PERIOD",scopeSon.row.paymentDayCode) }}
            <!-- <el-select
                        v-model="scopeSon.row.paymentDayCode"
                        @change="setPaymentObj(scopeSon.row)"
                      >
                        <el-option
                          v-for="item in paymentDayList"
                          :key="item.dictItemCode"
                          :label="item.dictItemName"
                          :value="item.dictItemCode"
                        />
                      </el-select> -->
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          prop="paymentTerm"
          label="付款条件"
          min-width="150"
        >
          <template slot-scope="scopeSon">
            {{ $getDictLabel("PAYMENT_MODE",scopeSon.row.paymentTerm) }}
            <!-- <el-select v-model="scopeSon.row.paymentTerm">
                        <el-option
                          v-for="item in paymentTermList"
                          :key="item.value"
                          :label="item.label"
                          :value="item.value + ''"
                        />
                      </el-select> -->
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          prop="paymentWay"
          label="付款方式"
          min-width="150"
        >
          <template slot-scope="scopeSon">
            {{ $getDictLabel("PAYMENT_MODE",scopeSon.row.paymentWay) }}
            <!-- <el-select v-model="scopeSon.row.paymentWay">
                        <el-option
                          v-for="item in paymentWayList"
                          :key="item.value"
                          :label="item.label"
                          :value="item.value"
                        />
                      </el-select> -->
          </template>
        </el-table-column>
      </el-table>
    </srm-dialog>
  </div>
</template>
<script>
import CUploadFile from '@/library/components/c-upload-file'
import CDownloadLink from 'lib@/components/c-download-link'
export default {
  components: {
    CUploadFile,
    CDownloadLink
  },
  data () {
    return {
      createByName: '', // 创建人
      createByDept: '', // 部门
      globalApprovalBiddingItemPaymentTermList: [],
      currentPage: 1, // 采购品类初始页
      currentPageA: 1, // 资质审查初始页
      currentPageB: 1, // 认证结果初始页
      pagesize: 10, //    每页的数据
      pagesizeB: 1,
      cityList: [], // 市
      provinceList: [], // 省
      assessmentTypeList: [],
      approveStatus: [],
      ceeaAuthResult: [], // 认证结果
      ceeaReviewLink: [], // 评审环节
      allParams: [],
      catData: [],
      tableData: [],
      dialogVisible: false,
      buObj: {},
      // 文件上传配置信息
      fileInfo: {
        uploadType: 'FASTDFS', // 固定参数
        sourceType: 'WEB_APP', // 固定参数
        fileModular: 'sup', // 文件所属模块 -》基础模块
        fileFunction: 'vendorBiddingManagement', // 文件所属功能
        fileType: 'images' // 文件所属类型
      },
      activeNames: ['1', '2', '3', '4', '5'],
      requirementHead: {},
      requirementAttaches: [],
      requirementLineList: [],
      // index:0,
      num: 0,
      fullscreen: false,
      n: 0,
      isFullscreen: true,
      browserKernel: '',
      canFullScreen: false,
      isFullScreen: false,
      approvalFileList: [],
      diaVisible: false,
      paymentTermList: []
    }
  },
watch: {
		// assessmentTypeList(){
		// 	this.$nextTick(()=>{
    //     //此时就可以获取到在created赋值后的assessmentTypeList了
    //      this.getFormDetail(this.$attrs.params.siteFormId);
		// 	})
		// }
	},

  created () {
      this.getPaymentTermList()
      if (this.$attrs.params.row) {
           this.getSiteFormDetail(this.$attrs.params.row.approvalHeaderId)
      }
  },
  mounted () {

  },
  methods: {
    openOneBidItem (index, row) {
        // this.globalApprovalBiddingItemPaymentTermList =
        // row.approvalBiddingItemPaymentTermList;
        // console.log("index",index,"globalApprovalBiddingItemPaymentTermList",this.globalApprovalBiddingItemPaymentTermList)
        let globalApprovalBiddingItemPaymentTermList = row.approvalBiddingItemPaymentTermList
        let paymentTermList = this.paymentTermList
        globalApprovalBiddingItemPaymentTermList.forEach(item => {
          paymentTermList.forEach(elm => {
              if (elm.value == item.paymentTerm) {
                item.paymentTerm = elm.label
              }
          })
        })
        this.globalApprovalBiddingItemPaymentTermList = globalApprovalBiddingItemPaymentTermList
       this.diaVisible = true
    },

    // 移除
    outerHandleRemove (fileuploadId) {},
    handleScriptProgress (percent) {},

    getformattor (val) {
      return this.$getLabelByValue(this.assessmentTypeList, val)
    },
     getPaymentTermList () {
      this.$http({
        url: '/api-cm/template/payType/getActivationPaymentTerms',
        method: 'GET',
        data: {}
      }).then(res => {
        this.paymentTermList = res.data.map(i => ({
          id: i.payTypeId,
          label: i.payExplain,
          value: i.payTypeId
        }))
      })
    },
    // 招标
    getSourceTENDER (data) {
      // requirementHead.sourceType
       this.$http({
        url: '/api-bid/bidingResult/sourcingResultReport/generate',
        method: 'GET',
        params: data
      })
    },
    // 询比价
     getSourceRFQ (data) {
      // requirementHead.sourceType
       this.$http({
        url: '/api-brg/bidingResult/sourcingResultReport/generate',
        method: 'GET',
        params: data
      })
    },

    getSiteFormDetail (siteFormId) {
      if (!siteFormId) return
      this.$http({
        url: '/api-inq/price/approval/approvalDetail',
        method: 'GET',
        params: { approvalHeaderId: siteFormId },
        loading: true
      })
        .then(async (res) => {
          if (res.data) {
              console.log('res', res)
              this.requirementHead = res.data.approvalHeader || {}
              if (this.requirementHead.sourceType == 'TENDER') {
                let biddingNum = this.requirementHead.ceeaSourceNo
                let catA = await this.getSourceTENDER({ biddingNum })
                console.log('catA', catA)
              } else if (this.requirementHead.sourceType == 'RFQ') {
                // let biddingNum = this.requirementHead.ceeaSourceNo
                let biddingNum = this.requirementHead.ceeaSourceNo
                  let catB = await this.getSourceRFQ({ biddingNum })
                  console.log('catB', catB)
              }
              this.approvalFileList = res.data.approvalFileList || []
              this.createByName = res.data.createByName
              this.createByDept = res.data.createByDept
              this.tableData = res.data.approvalBiddingItemList || []
          }
        })
        .catch((err) => {
          console.log(err)
        })
    },

    // 采购品类
    getPurchase (reviewFormId) {
      this.$http({
        url:
          '/api-sup/review/reviewForm/listOrgAndCategoryByReviewId',
        method: 'GET',
        params: { reviewFormId },
        loading: true
      })
        .then((res) => {
          if (res.data) {
            this.catData = res.data.orgCategorys
          }
        })
        .catch((err) => {
          console.log(err)
        })
    },
    getDetail (siteFormId) {
      return this.$http({
        url: '/api-sup/review/siteForm/getSiteFormDTO',
        method: 'GET',
        params: { siteFormId },
        loading: true
      })
    },

    // 供应商评审
    async getFormDetail (siteFormId) {
          try {
        //
          let { data } = await this.getDetail(siteFormId)
          console.log('provinceList', this.provinceList)
          let requirementHead = {}
          this.getPurchase(data.siteForm.reviewFormId)// this.$attrs.params.reviewFormId
          requirementHead = data.siteForm
          // console.log("this.assessmentTypeList", this.assessmentTypeList);
          this.assessmentTypeList.forEach((item) => {
            if (item.value == requirementHead.assessmentType) {
              // console.log(item.label, 222);
              requirementHead.assessmentType = item.label
            }
          })
          // console.log("siteProvince", requirementHead.assessmentType);

           let siteProvince = this.provinceList.find(v => {
             return v.value == requirementHead.siteProvince
           })
           requirementHead.siteProvince = siteProvince.label
           this.requirementHead = requirementHead
            this.getCityData(siteProvince.value)
          //  console.log("cityList",this.cityList)
          // this.getCityData(requirementHead.siteProvince);

          this.orgJournals = data.orgJournals
          let num = 1
          let arrBuName = []
          this.orgJournals.forEach((item) => {
            let obj = {}
            obj.index = num++
            obj.orgName = item.orgName
            this.$http({
              url:
                '/api-base/organization/organization/getBuByOrgId',
              method: 'GET',
              params: { organizationId: item.orgId },
              loading: true
            }).then((res) => {
              obj.buName = res.data.organizationName
            })

            arrBuName.push(obj)
          })
          // this.tableData = arrBuName;
          } catch (e) {
            console.log(e)
          }
    },
    current_change: function (currentPage) {
      console.log(currentPage)
      this.currentPage = currentPage
      // debugger
    },
      current_changeA: function (currentPageA) {
      console.log(currentPageA)
      this.currentPageA = currentPageA
      // debugger
    },
    current_changeB: function (currentPageB) {
      this.currentPageB = currentPageB
      // debugger
    },
    handleChange (val) {
      console.log(val)
    }
  }
}
</script>
<style scoped lang="scss">
.model {
  width: 100%;
  .bg-purple {
    text-align: left;
  }
  .bg-purple-light {
    text-align: right;
    min-width: 100px;
  }
  .bg {
    text-align: left;
    line-height: 32px;
  }

  .model_LineList {
    width: 100%;

    .LineList {
      border-bottom: 1px solid #e6ebf5;
      background-color: #f4f5f7;

      margin-top: -10px;
      margin-left: -10px;
      margin-right: -10px;

      span {
        margin-right: 4px;
      }
      .document {
        color: aqua;
        text-align: right;
        float: right;
      }
    }
    .card {
      width: 100%;
      // min-height: 200px;
    }
  }
  // .el-collapse-item__wrap{
  //   padding: 0px;
  // }
}
</style>
