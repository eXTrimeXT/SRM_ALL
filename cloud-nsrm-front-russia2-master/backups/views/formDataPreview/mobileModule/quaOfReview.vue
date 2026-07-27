<template>
  <div class="model">
    <el-collapse
      v-model="activeNames"
      class="modelA"
      @change="handleChange"
    >
      <el-collapse-item
        title="基本信息"
        name="1"
      >
        <!-- <el-row>
          <el-col :span="12"
            ><div class="bg-purple">资质审查类型：</div></el-col
          >
          <el-col :span="12"
            ><div class="bg-purple-light">
              {{d_getlabel("QUA_REVIEW_TYPE",this.requirementHead.quaReviewType)  }}

            </div></el-col
          >
        </el-row>
        <el-row>
          <el-col :span="12"><div class="bg-purple">供应商名称：</div></el-col>
          <el-col :span="12"
            ><div class="bg-purple-light">
              {{ this.requirementHead.vendorName }}
            </div></el-col
          >
        </el-row> -->
        <el-row>
          <el-col
            :span="12"
          >
            <div class="bg-purple">
              资质审查单号：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.reviewFormNumber }}
            </div>
          </el-col>
        </el-row>
        <!-- <el-row>
          <el-col :span="12"><div class="bg-purple">审批状态：</div></el-col>
          <el-col :span="12"
            ><div class="bg-purple-light">
              {{d_getlabel("APPROVE_STATUS_TYPE",this.requirementHead.approveStatus)  }}

            </div></el-col
          >
        </el-row> -->
        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              创建人：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.createdFullName }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              部门：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.ceeaDeptName }}
            </div>
          </el-col>
        </el-row>
        <!-- <el-row>
          <el-col :span="12"><div class="bg-purple">创建时间：</div></el-col>
          <el-col :span="12"
            ><div class="bg-purple-light">
              {{ this.requirementHead.creationDate }}
            </div></el-col
          >
        </el-row> -->
        <el-row>
          <el-col
            :span="12"
          >
            <div class="bg-purple">
              资质审查说明：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.reviewExplain }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              需求分析：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.ceeaDemandAnalysis }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col
            :span="12"
          >
            <div class="bg-purple">
              市场供应分析：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.ceeaSupAnalysis }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col
            :span="12"
          >
            <div class="bg-purple">
              品类本期采购策略：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.ceeaCategoryStrategy }}
            </div>
          </el-col>
        </el-row>
      </el-collapse-item>
      <el-collapse-item
        title="业务实体"
        name="2"
        class="model_LineList"
      >
        <div class="card">
          <el-table :data="tableData">
            <el-table-column
              prop="index"
              label="序号"
              width="50"
            />
            <el-table-column
              prop="orgId"
              align="center"
              label="引入组织"
            >
              <template slot-scope="scope">
                {{ scope.row.orgName }}
                <!-- <organization-selector
                      disabled
                      ref="organizationSelector"
                      :parentId="-1"
                      nodeType="OU" :scope="scope"
                      v-model="scope.row.orgId"

                      /> -->
              </template>
            </el-table-column>
            <!-- <el-table-column prop="buName" label="事业部" style="width: 40%">
            </el-table-column> -->
          </el-table>
        </div>
      </el-collapse-item>
      <el-collapse-item
        title="采购品类"
        name="3"
        class="model_LineList"
      >
        <template v-if="categoryData.length > 6">
          <div class="LineList">
            <el-pagination
              :page-size="pagesize"
              layout="total,prev, next"
              :total="categoryData.length"
              @current-change="current_change"
            />
          </div>
          <div class="card">
            <!-- categoryData.slice((currentPage-1)*pagesize,currentPage*pagesize) -->
            <el-table
              :data="
                categoryData.slice(
                  (currentPage - 1) * pagesize,
                  currentPage * pagesize
                )
              "
              style="width: 100%; margin-top: 6px;"
            >
              <el-table-column
                prop="index"
                label="序号"
                width="100"
              />
              <el-table-column
                prop="categoryName"
                label="引入品类"
                style="width: 40%"
              />
              <el-table-column
                prop="thisYearAmount"
                label="品类本年度采购金额（万元）"
                style="width: 40%"
              />
            </el-table>
          </div>
        </template>

        <div
          v-else
          class="card"
        >
          <el-table
            :data="categoryData"
            style="width: 100%; margin-top: 6px;"
          >
            <el-table-column
              prop="index"
              label="序号"
              width="100"
            />
            <el-table-column
              prop="categoryName"
              label="引入品类"
              style="width: 40%"
            />
            <el-table-column
              prop="thisYearAmount"
              label="品类本年度采购金额（万元）"
              style="width: 40%"
            />
          </el-table>
        </div>
      </el-collapse-item>
      <!-- <el-collapse-item title="银行信息" name="3" class="model_LineList">
        <div class="card">
          <el-table :data="bankJournalsData" style="width: 100%">
            <el-table-column prop="index" align="center" label="序号" width="100">
            </el-table-column>
            <el-table-column prop="bankCode" align="center" label="银行代码" style="width: 40%">
            </el-table-column>
            <el-table-column prop="bankName" align="center" label="银行名称" style="width: 40%">
            </el-table-column>
            <el-table-column prop="openingBank" align="center" label="开户行名称" style="width: 40%">
            </el-table-column>
            <el-table-column prop="unionCode" align="center" label="分行编码" style="width: 40%">
            </el-table-column>
            <el-table-column prop="bankAccountName" align="center" label="账户名称" style="width: 40%">
            </el-table-column>
            <el-table-column prop="bankAccount" align="center" label="银行账户" style="width: 40%">
            </el-table-column>
              <el-table-column prop="currencyCode"  label="币种" width="100" align="center">
                  <template slot-scope="scope">
                      <el-select v-model="scope.row.currencyCode" disabled>
                        <el-option
                          v-for="item in currencyList"
                          :key="item.id"
                          :label="item.label"
                          :value="item.value"
                        />
                    </el-select>
                    </template>
            </el-table-column>
          </el-table>

        </div>
      </el-collapse-item> -->

      <!-- <el-collapse-item title="供应商地点信息" name="4" class="model_LineList">
        <div class="card">
          <el-table :data="siteJournalsData" style="width: 100%">
            <el-table-column prop="index" align="center" label="序号" width="100">
            </el-table-column>
            <el-table-column prop="orgCode" align="center" label="业务实体" style="width: 40%">
            </el-table-column>
            <el-table-column prop="addressName" align="center" label="地点名称" width="100">
               <template slot-scope="scope">
                      <el-select v-model="scope.row.addressName" disabled>
                        <el-option
                          v-for="item in addressList"
                          :key="item.id"
                          :label="item.label"
                          :value="item.value"
                        />
                      </el-select>
                    </template>
            </el-table-column>
            <el-table-column prop="country" align="center" label="国家" width="100">
              <template slot-scope="scope">
                      <el-select v-model="scope.row.country" disabled>
                        <el-option
                          v-for="item in countryList"
                          :key="item.id"
                          :label="item.label"
                          :value="item.value"
                        />
                      </el-select>
                    </template>
            </el-table-column>
            <el-table-column prop="province" align="center" label="地区" width="100">
               <template slot-scope="scope">
                      <el-select v-model="scope.row.province"  disabled>
                        <el-option
                          v-for="item in provinceList"
                          :key="item.id"
                          :label="item.label"
                          :value="item.value"
                        />
                      </el-select>
                    </template>
            </el-table-column>
            <el-table-column prop="city" align="center" label="城市" style="width: 40%">
              <template slot-scope="scope">
               <el-select
                    v-model="scope.row.city"
                    :disabled="true"
                    clearable
                  >
                    <el-option
                      v-for="item in cityList"
                      :key="item.id"
                      :label="item.label"
                      :value="item.value"
                    >
                    </el-option>
                  </el-select>
                  </template>
            </el-table-column>
            <el-table-column prop="addressDetail" align="center" label="详细地址" style="width: 40%">
            </el-table-column>
            <el-table-column prop="postCode" align="center" label="邮政编码" style="width: 40%">
            </el-table-column>
             <el-table-column prop="siteComment" align="center" label="地址备注" style="width: 40%">
            </el-table-column>

          </el-table>

        </div>
      </el-collapse-item> -->
      <el-collapse-item
        title="资质审查原因"
        name="5"
      >
        <template v-if="reviewFormExpsData.length > 6">
          <div class="LineList">
            <el-pagination
              :page-size="pagesize"
              layout="total,prev, next"
              :total="reviewFormExpsData.length"
              @current-change="current_changeA"
            />
          </div>
          <div class="card">
            <el-table
              :data="
                reviewFormExpsData.slice(
                  (currentPageA - 1) * pagesize,
                  currentPageA * pagesize
                )
              "
              style="width: 100%; margin-top: 6px;"
            >
              <el-table-column
                prop="index"
                label="序号"
                width="100"
              />
              <el-table-column
                prop="reviewReason"
                label="原因"
                style="width: 40%"
              >
                <template slot-scope="scope">
                  {{ $getDictLabel("REVIEW_REASON_TYPE", scope.row.reviewReason) }}
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
            :data="reviewFormExpsData"
            style="width: 100%"
          >
            <el-table-column
              prop="index"
              label="序号"
              width="100"
            />
            <el-table-column
              prop="reviewReason"
              label="原因"
              style="width: 40%"
            >
              <template slot-scope="scope">
                {{ $getDictLabel("REVIEW_REASON_TYPE", scope.row.reviewReason) }}
              </template>
            </el-table-column>
            <!-- <el-table-column prop="reasonExplain" label="原因描述" style="width: 40%">
            </el-table-column> -->
          </el-table>
        </div>
      </el-collapse-item>

      <el-collapse-item
        title="附件"
        name="6"
      >
        <el-table
          :data="fileuploadsList"
          style="width: 100%"
          border
          max-height="250px"
        >
          <el-table-column
            align="center"
            type="index"
            label="序号"
            width="50"
          />
          <el-table-column
            align="center"
            prop="fileFullname"
            label="附件"
            min-width="80"
          >
            <template slot-scope="scope">
              <c-download-link
                :id="scope.row.fileuploadId"
                :name="scope.row.fileSourceName"
                ellipsis
                class="download-link-item"
              />
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="filePureName"
            label="附件名称"
            width="120"
          />
        </el-table>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>
<script>
import CUploadFile from '@/library/components/c-upload-file'
import CDownloadLink from 'lib@/components/c-download-link'
import { adaptDictData } from '@/utils'
import SceneAttachment from './SceneAttachment'
import OrganizationSelector from 'lib@/components/organization-selector'
import {
  getDictItem,
  getDictItemList,
  getRegion
} from '@/api/common'

export default {
  components: {
    CUploadFile,
    CDownloadLink,
    SceneAttachment,
    OrganizationSelector
  },
  data () {
    return {
      currentPage: 1, // 采购品类初始页
      currentPageA: 1, // 资质审查初始页
      pagesize: 6, //    每页的数据
      userList: [],
      requirementAttachesLen: 0,
      requirementAttaches: [], // 采购类型超过6条分页
      currencyList: [], // 币种
      exploitReason: [], // 原因
      cityList: [], // 市
      provinceList: [], // 省
      countryList: [], // 国家
      addressList: [], // 地点名称
      approveStatusList: [], // 审批状态
      quaReviewTypeList: [], // 资质审查类型
      fileRefresh: false,
      curOpt: 'add',
      curOrderId: null, // 单据ID
      reviewFormExpsData: [],
      siteJournalsData: [],
      bankJournalsData: [],
      categoryData: [],
      BUList: [],
      tableData: [],
      fileuploadsList: [],
      dialogVisible: false,
      // 文件上传配置信息
      fileInfo: {
        uploadType: 'FASTDFS', // 固定参数
        sourceType: 'WEB_APP', // 固定参数
        fileModular: 'sup', // 文件所属模块 -》基础模块
        fileFunction: 'vendorBiddingManagement', // 文件所属功能
        fileType: 'images' // 文件所属类型
      },
      activeNames: ['1', '2', '3', '4', '5', '6'],
      requirementHead: {},
      requirementLineList: [],
      orgJournals: [],

      // index:0,
      num: 0,
      fullscreen: false,
      n: 0,
      isFullscreen: true,
      browserKernel: '',
      canFullScreen: false,
      isFullScreen: false
    }
  },
  created () {
    let businessId = this.$attrs.params.reviewFormId
    this.getlistPage({ businessId })
    this.fatchDictData()

    console.log('this.$attrs.params', this.$attrs.params)
    this.getFormDetail(this.$attrs.params.reviewFormId)

    // 检查浏览器是否支持全屏
    // this.canFullScreen =
    //   document.fullscreenEnabled ||
    //   document.webkitFullscreenEnabled ||
    //   document.mozFullScreenEnabled ||
    //   document.msFullscreenEnabled;
    // if (document.webkitFullscreenEnabled) {
    //   this.browserKernel = "webkit";
    // } else if (document.mozFullScreenEnabled) {
    //   this.browserKernel = "gecko";
    // } else if (document.msFullscreenEnabled) {
    //   this.browserKernel = "trident";
    // } else if (document.fullscreenEnabled) {
    //   this.browserKernel = "others";
    // }
    // if (this.canFullScreen) {
    //   this.addFullScreenListener();
    // }
    //   setTimeout(()=>{
    //   document.querySelector('.sceneAttachment').querySelectorAll('.el-date-editor').forEach(v=>{
    //     v.style['pointer-events']= 'none';      //设置附件的时间禁用，不可选择
    //     v.style['opacity']= 0.5;
    //   })
    // },3000)
  },

  methods: {
    getlistPage (data) {
      this.$api.base.getFileListByBusinessId(data).then(async (res) => {
        console.log('附件', res)
        this.fileuploadsList = res.data.list
      })
    },
    fatchDictData () {
      // 批量查询字典

      let dictParamsArr = [
        { dictCode: 'QUA_REVIEW_TYPE' }, // 资质审查类型
        { dictCode: 'APPROVE_STATUS_TYPE' }, // 审批状态
        { dictCode: 'country' }, //
        { dictCode: 'VENDOR_SITE_CODE' }, //
        { dictCode: 'REVIEW_REASON_TYPE' },
        { dictCode: 'BID_TENDER_CURRENCY' }
      ]
      getDictItemList(dictParamsArr).then((res) => {
        const [
          QUA_REVIEW_TYPE,
          APPROVE_STATUS_TYPE,
          country,
          VENDOR_SITE_CODE,
          REVIEW_REASON_TYPE,
          BID_TENDER_CURRENCY
        ] = res.data
        this.quaReviewTypeList = adaptDictData(
          QUA_REVIEW_TYPE.QUA_REVIEW_TYPE,
          'dict'
        )
        this.approveStatusList = adaptDictData(
          APPROVE_STATUS_TYPE.APPROVE_STATUS_TYPE,
          'dict'
        )
        this.countryList = adaptDictData(country.country, 'dict')
        this.addressList = adaptDictData(
          VENDOR_SITE_CODE.VENDOR_SITE_CODE,
          'dict'
        )
        this.exploitReason = adaptDictData(
          REVIEW_REASON_TYPE.REVIEW_REASON_TYPE,
          'dict'
        )
        this.currencyList = adaptDictData(
          BID_TENDER_CURRENCY.BID_TENDER_CURRENCY,
          'dict'
        )
      })
      getRegion({ queryType: 'province' }).then((res) => {
        if (res.data) {
          this.provinceList = this.adaptProvinceCity(res.data, 'province')
        }
      })
      // getDictItem("VENDOR_SITE_CODE").then(res => {
      //   this.addressList = adaptDictData(res.data, "dict");
      // });
    },
    outerButtonClick (index) {
      this.bankRowIndex = index
    },
    outerHandleUploadSuccess (file) {
      const { id, name, createdBy, creationDate } = file
      this.requirementAttaches[this.bankRowIndex].fileuploadId = id.toString()
      this.requirementAttaches[this.bankRowIndex].attachName = name
      this.requirementAttaches[this.bankRowIndex].createdBy = createdBy
      this.requirementAttaches[this.bankRowIndex].creationDate = creationDate
    },

    // 适配省 市
    adaptProvinceCity (data, type) {
      let arr = []
      if (data && data.length > 0) {
        if (type === 'province') {
          // 省
          data.forEach((element) => {
            arr.push({
              id: element.provinceId,
              value: element.provinceId.toString(),
              label: element.province
            })
          })
        } else if (type === 'city') {
          // 市
          data.forEach((element) => {
            arr.push({
              id: element.cityId,
              value: element.cityId.toString(),
              label: element.city
            })
          })
        }
      }
      return arr
    },
    // 加载省
    getRegionData (val) {
      let parame = { queryType: 'city', parentId: val }
      getRegion(parame).then((res) => {
        if (res.data) {
          this.cityList = this.adaptProvinceCity(res.data, 'city')
        }
      })
    },
    // 加载所有的城市
    getAllRegionData () {
      this.$http({
        url: '/api-base/region/queryRegionByParam',
        method: 'POST',
        params: { queryType: 'city' },
        loading: true
      }).then((res) => {
        if (res.data) {
          this.cityList = this.adaptProvinceCity(res.data, 'city')
        }
      })
    },
    // 移除
    outerHandleRemove (fileuploadId) {},
    handleScriptProgress (percent) {},

    getFormDetail (reviewFormId) {
      if (!reviewFormId) return
      this.$http({
        url: '/api-sup/review/reviewForm/getReviewFormDTO',
        method: 'GET',
        params: { reviewFormId },
        loading: true
      })
        .then((data) => {
          if (data.data) {
            console.log('data.data', data.data)
            this.requirementHead = data.data.reviewForm
            this.requirementHead.creationDate = this.requirementHead.creationDate.split(
              ' '
            )[0]
            // this.requirementHead.quaReviewType = getLabel(
            //   "QUA_REVIEW_TYPE",
            //   this.requirementHead.quaReviewType
            // );
            // this.requirementHead.approveStatus = getLabel(
            //   "APPROVE_STATUS_TYPE",
            //   this.requirementHead.approveStatus
            // );
            this.orgJournals = data.data.orgJournals
            let num = 1
            let arrBuName = []
            this.orgJournals.forEach(async (item) => {
              let obj = {}
              obj.index = num++
              obj.orgId = item.orgId
              let { data } = await this.$http({
                url: '/api-base/organization/organization/getBuByOrgId',
                method: 'GET',
                params: { organizationId: item.orgId },
                loading: true
              })
              obj.buName = data.organizationName

              obj.orgName = item.orgName

              arrBuName.push(obj)
            })
            this.tableData = arrBuName
            // console.log("arrBuName",arrBuName)
            let numA = 1
            let arrCateJournals = []
            data.data.cateJournals.forEach((item) => {
              let obj = {}
              obj.index = numA++
              obj.categoryName = item.categoryName
              obj.thisYearAmount = item.thisYearAmount
              arrCateJournals.push(obj)
            })
            this.requirementAttachesLen = arrCateJournals.length
            // requirementAttaches

            this.categoryData = arrCateJournals

            console.log('this.categoryData', this.categoryData)

            // 银行信息
            let numB = 1
            let bankJournals = []
            data.data.bankJournals.forEach((item) => {
              let obj = {}
              obj.index = numB++
              obj.bankCode = item.bankCode
              obj.bankName = item.bankName
              obj.openingBank = item.openingBank
              obj.unionCode = item.unionCode
              obj.bankAccountName = item.bankAccountName
              obj.bankAccount = item.bankAccount
              obj.currencyCode = item.currencyCode
              bankJournals.push(obj)
            })
            this.bankJournalsData = bankJournals
            // 供应商地点信息
            let numC = 1
            let siteJournals = []
            data.data.siteJournals.forEach((item) => {
              let obj = {}
              obj.index = numC++
              obj.orgCode = item.orgCode
              obj.addressName = item.addressName
              obj.country = item.country
              obj.province = item.province
              this.getRegionData(item.province)
              obj.city = item.city
              obj.addressDetail = item.addressDetail
              obj.postCode = item.postCode
              obj.siteComment = item.siteComment
              siteJournals.push(obj)
            })
            this.siteJournalsData = siteJournals

            let numD = 1
            let reviewFormExps = []
            data.data.reviewFormExps.forEach((item) => {
              let obj = {}
              obj.index = numD++
              obj.reviewReason = item.reviewReason
              obj.reasonExplain = item.reasonExplain
              reviewFormExps.push(obj)
            })
            this.reviewFormExpsData = reviewFormExps
          }
          //  document.querySelector('.sceneAttachment').querySelectorAll('.el-date-editor').forEach(v=>{
          //   v.style['pointer-events']= 'none';      //设置附件的时间禁用，不可选择
          //   v.style['opacity']= 0.5;
          // })
        })
        .catch((err) => {
          console.log(err)
        })
    },
    // 设置全屏
    // 全屏设置
    fullTable () {
      console.log('1111')
      if (this.canFullScreen) {
        if (this.isFullScreen) {
          // 关闭全屏
          this.exitFullScreen()
          this.isFullScreen = false
        } else {
          // 打开全屏
          console.log('2222')
          this.Full(document.getElementsByClassName('card')[0])
          this.isFullScreen = true
        }
      } else {
        this.$message.warning({
          content: '当前浏览器暂不支持全屏模式，请切换浏览器后重新尝试！',
          duration: 3
        })
      }
    },
    Full (element) {
      // 判断各种浏览器，找到正确的方法
      console.log('3333')
      const requestMethod =
        element.requestFullScreen || // W3C
        element.webkitRequestFullScreen || // Chrome, safari
        element.mozRequestFullScreen || // FireFox
        element.msRequestFullscreen // IE11
      if (requestMethod) {
        console.log('4444')
        requestMethod.call(element)
      }
    },
    exitFullScreen () {
      var exitMethod =
        document.exitFullscreen || // W3C
        document.mozCancelFullScreen || // FireFox
        document.webkitExitFullscreen || // Chrome等
        document.msExitFullscreen // IE11
      if (exitMethod) {
        exitMethod.call(document)
      }
    },
    addFullScreenListener () {
      const self = this
      document.onkeydown = function (e) {
        if (e && e.keyCode === 122) {
          // 捕捉F11键盘动作
          e.preventDefault() // 阻止F11默认动作
          self.toggleFullScreen()
        }
      }
      // 监听不同浏览器的全屏事件，并件执行相应的代码
      switch (self.browserKernel) {
        case 'webkit':
          document.onwebkitfullscreenchange = function () {
            if (document.webkitIsFullScreen) {
              self.isFullScreen = true
            } else {
              self.isFullScreen = false
            }
          }
          break
        case 'gecko':
          document.onmozfullscreenchange = function () {
            if (document.mozFullScreen) {
              self.isFullScreen = true
            } else {
              self.isFullScreen = false
            }
          }
          break
        case 'trident':
          document.onmsfullscreenchange = function () {
            if (document.msFullscreenElement) {
              self.isFullScreen = true
            } else {
              self.isFullScreen = false
            }
          }
          break
        case 'others':
          document.onfullscreenchange = function () {
            if (document.fullscreen) {
              self.isFullScreen = true
            } else {
              self.isFullScreen = false
            }
          }
          break
        default:
          break
      }
    },

    getFullCreeen () {
      this.n++
      this.n % 2 == 0
        ? this.outFullCreeen(document)
        : this.inFullCreeen(document.getElementsByClassName('card')[0])
    },
    inFullCreeen (element) {
      console.log(
        'ocument.getElementsByClassName(\'.card\')[0]',
        document.getElementsByClassName('card')[0]
      )
      let el = document.getElementsByClassName('card')[0]
      let rfs =
        el.requestFullScreen ||
        el.webkitRequestFullScreen ||
        el.mozRequestFullScreen ||
        el.msRequestFullScreen
      if (typeof rfs !== 'undefined' && rfs) {
        rfs.call(el)
      } else if (typeof window.ActiveXObject !== 'undefined') {
        let wscript = new ActiveXObject('WScript.Shell')
        if (wscript != null) {
          wscript.SendKeys('{F11}')
        }
      }
    },
    outFullCreeen (element) {
      let el = element
      let cfs =
        el.cancelFullScreen ||
        el.webkitCancelFullScreen ||
        el.mozCancelFullScreen ||
        el.exitFullScreen
      if (typeof cfs !== 'undefined' && cfs) {
        cfs.call(el)
      } else if (typeof window.ActiveXObject !== 'undefined') {
        let wscript = new ActiveXObject('WScript.Shell')
        if (wscript != null) {
          wscript.SendKeys('{F11}')
        }
      }
    },
    next (event, number) {
      // console.log(" this.requirementAttaches.length",event.target.dataset.radius,"number",number)
      this.num += 1
      if (number == 2) {
        if (this.num > event.target.dataset.radius - 1) {
          this.num = 0
          this.$refs.carousel2.setActiveItem(this.num)
        } else {
          this.$refs.carousel2.setActiveItem(this.num)
        }
      } else if (number == 3) {
        if (this.num > event.target.dataset.radius - 1) {
          this.num = 0

          this.$refs.carousel3.setActiveItem(this.num)
        } else {
          this.$refs.carousel3.setActiveItem(this.num)
        }
      } else if (number == 4) {
        if (this.num > event.target.dataset.radius - 1) {
          this.num = 0
          this.$refs.carousel4.setActiveItem(this.num)
        } else {
          this.$refs.carousel4.setActiveItem(this.num)
        }
      }
    },
    pre (event, number) {
      // console.log(" this.requirementAttaches.length",event.target.dataset.radius,"number",number)
      this.num -= 1
      if (number == 2) {
        if (this.num < 0) {
          this.num = event.target.dataset.radius - 1
          this.$refs.carousel2.setActiveItem(this.num)
        } else {
          this.$refs.carousel2.setActiveItem(this.num)
        }
      } else if (number == 3) {
        if (this.num < 0) {
          this.num = event.target.dataset.radius - 1
          this.$refs.carousel3.setActiveItem(this.num)
        } else {
          this.$refs.carousel3.setActiveItem(this.num)
        }
      } else if (number == 4) {
        if (this.num < 0) {
          this.num = event.target.dataset.radius - 1
          this.$refs.carousel4.setActiveItem(this.num)
        } else {
          this.$refs.carousel4.setActiveItem(this.num)
        }
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
