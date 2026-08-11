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
          <el-col :span="12"><div class="bg-purple">申请单号：</div></el-col>
          <el-col :span="12"
            ><div class="bg-purple-light">
              {{ this.requirementHead.changeApplyNo }}
            </div></el-col
          >
        </el-row>
        <el-row>
          <el-col :span="12"><div class="bg-purple">申请人：</div></el-col>
          <el-col :span="12"
            ><div class="bg-purple-light">
              {{ this.requirementHead.companyName }}
            </div></el-col
          >
        </el-row> -->
        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              供应商名称：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.companyName }}
            </div>
          </el-col>
        </el-row>
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
              {{ this.requirementHead.createdBy }}
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

        <el-row>
          <el-col
            :span="8"
          >
            <div class="bg-purple">
              变更原因说明：
            </div>
          </el-col>
          <el-col
            :span="16"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.changeExplain }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col
            :span="12"
          >
            <div class="bg-purple">
              是否4M变更：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.enable4MChange }}
            </div>
          </el-col>
        </el-row>

        <!-- <el-row>
          <el-col :span="12"><div class="bg-purple">变更前内容：</div></el-col>
          <el-col :span="12"
            ><div class="bg-purple-light">
              </div
          ></el-col>
        </el-row>

        <el-row>
          <el-col :span="12"><div class="bg-purple">变更后内容：</div></el-col>
          <el-col :span="12"><div class="bg-purple-light"></div></el-col>
        </el-row> -->
      </el-collapse-item>
      <!-- JSON.stringify(get_Newbase) !== '{}' -->
      <el-collapse-item
        v-if="Object.keys(get_Newbase).length !=0"
        title="变更内容"
        name="2"
      >
        <el-row v-if="get_Newbase.overseasRelation || ''">
          <el-col :span="12">
            <div class="bg-purple">
              境内外关系：
            </div>
          </el-col>
          <el-col :span="12">
            <div class="bg-purple-light">
              {{ $getDictLabel("RELATION",get_Newbase.overseasRelation || '') }}
            </div>
          </el-col>
        </el-row>
        <el-row v-if="get_Newbase.companyType || ''">
          <el-col :span="12">
            <div class="bg-purple">
              企业性质：
            </div>
          </el-col>
          <el-col :span="12">
            <div class="bg-purple-light">
              {{ $getDictLabel("COMPANY_NATURE",get_Newbase.companyType) }}
            </div>
          </el-col>
        </el-row>
        <el-row v-if="get_Newbase.businessLicenseFileId || ''">
          <el-col :span="12">
            <div class="bg-purple">
              营业执照：
            </div>
          </el-col>
          <el-col :span="12">
            <div class="bg-purple-light">
              <c-download-link
                :id="
                  this.get_Newbase.businessLicenseFileId
                "
                :name="this.get_Newbase.businessLicense"
                ellipsis
                class="download-link-item"
              />
              <i
                class="el-icon-close close-icon"
                @click="handleAttachmentRemove()"
              />
            </div>
          </el-col>
        </el-row>
        <el-row v-if="get_Newbase.companyName">
          <el-col :span="12">
            <div class="bg-purple">
              企业名称：
            </div>
          </el-col>
          <el-col :span="12">
            <div class="bg-purple-light">
              {{ this.get_Newbase.companyName }}
            </div>
          </el-col>
        </el-row>

        <el-row v-if="get_Newbase.registeredCapital">
          <el-col :span="12">
            <div class="bg-purple">
              注册资本（万元）：
            </div>
          </el-col>
          <el-col :span="12">
            <div class="bg-purple-light">
              {{ get_Newbase.registeredCapital + " ("+$getDictLabel("BID_TENDER_CURRENCY",this.get_Newbase.registCurrency)+")" }}
            </div>
          </el-col>
        </el-row>
        <el-row v-if="get_Newbase.companyCreationDate">
          <el-col :span="12">
            <div class="bg-purple">
              成立日期：
            </div>
          </el-col>
          <el-col :span="12">
            <div class="bg-purple-light">
              {{ get_Newbase.companyCreationDate }}
            </div>
          </el-col>
        </el-row>
        <el-row v-if="get_Newbase.companyShortName">
          <el-col :span="12">
            <div class="bg-purple">
              企业简称：
            </div>
          </el-col>
          <el-col :span="12">
            <div class="bg-purple-light">
              {{ get_Newbase.companyShortName }}
            </div>
          </el-col>
        </el-row>
        <!-- 只有境内供应商有 -->
        <el-row v-if="get_Newbase.companyShortName && curRel === 'INSIDE'">
          <el-col :span="12">
            <div class="bg-purple">
              {{ $t('vendorMod.lcCode')+" :" }}
            </div>
          </el-col>
          <el-col :span="12">
            <div class="bg-purple-light">
              {{ get_Newbase.lcCode }}
            </div>
          </el-col>
        </el-row>
        <!-- 只有境外供应商有 -->
        <el-row v-if="get_Newbase.dunsCode && curRel === 'OUT'">
          <el-col :span="12">
            <div class="bg-purple">
              {{ $t('vendorMod.dunsCode')+" :" }}
            </div>
          </el-col>
          <el-col :span="12">
            <div class="bg-purple-light">
              {{ get_Newbase.dunsCode }}
            </div>
          </el-col>
        </el-row>
        <el-row v-if="get_Newbase.legalPerson">
          <el-col :span="12">
            <div class="bg-purple">
              {{ $t('vendorMod.legalPerson')+" :" }}
            </div>
          </el-col>
          <el-col :span="12">
            <div class="bg-purple-light">
              {{ get_Newbase.legalPerson }}
            </div>
          </el-col>
        </el-row>
        <el-row v-if="get_Newbase.registrationAuthority">
          <el-col :span="12">
            <div class="bg-purple">
              {{ $t('vendorMod.registrationAuthority')+" :" }}
            </div>
          </el-col>
          <el-col :span="12">
            <div class="bg-purple-light">
              {{ get_Newbase.registrationAuthority }}
            </div>
          </el-col>
        </el-row>
        <!-- 个体户不用显示 -->
        <!-- 营业日期从 -->
        <el-row v-if="get_Newbase.businessStartDate && curType !== 'GETI'">
          <el-col :span="12">
            <div class="bg-purple">
              {{ $t('vendorMod.businessStartFrom')+" :" }}
            </div>
          </el-col>
          <el-col :span="12">
            <div class="bg-purple-light">
              {{ get_Newbase.businessStartDate }}
            </div>
          </el-col>
        </el-row>
        <!-- 个体户不用显示 -->
        <!-- 营业日期至 -->
        <el-row v-if="get_Newbase.businessEndDate && curType !== 'GETI'">
          <el-col :span="12">
            <div class="bg-purple">
              {{ $t('vendorMod.businessEndAt')+" :" }}
            </div>
          </el-col>
          <el-col :span="12">
            <div class="bg-purple-light">
              {{ this.get_Newbase.businessEndDate }}
            </div>
          </el-col>
        </el-row>
        <el-row v-if="get_Newbase.companyCountry ">
          <el-col :span="12">
            <div class="bg-purple">
              {{ $t('vendorMod.businessAddr')+" :" }}
            </div>
          </el-col>
          <el-col :span="12">
            <div class="bg-purple-light">
              {{ $getDictLabel("country",get_Newbase.companyCountry) }}
            </div>
          </el-col>
        </el-row>
        <el-row v-if="get_Newbase.province ">
          <el-col :span="12">
            <div class="bg-purple">
              {{ $t('vendorMod.province')+" :" }}
            </div>
          </el-col>
          <el-col :span="12">
            <div class="bg-purple-light">
              {{ get_Newbase.province }}
            </div>
          </el-col>
        </el-row>
        <el-row v-if="get_Newbase.city ">
          <el-col :span="12">
            <div class="bg-purple">
              {{ $t('vendorMod.city')+" :" }}
            </div>
          </el-col>
          <el-col :span="12">
            <div class="bg-purple-light">
              {{ get_Newbase.city }}
            </div>
          </el-col>
        </el-row>
        <el-row v-if="get_Newbase.companyAddress ">
          <el-col :span="12">
            <div class="bg-purple">
              {{ $t('components.address.detailAddress')+" :" }}
            </div>
          </el-col>
          <el-col :span="12">
            <div class="bg-purple-light">
              {{ get_Newbase.companyAddress }}
            </div>
          </el-col>
        </el-row>
        <el-row v-if="get_Newbase.businessScope ">
          <el-col :span="12">
            <div class="bg-purple">
              {{ $t('vendorMod.businessScope')+" :" }}
            </div>
          </el-col>
          <el-col :span="12">
            <div class="bg-purple-light">
              {{ get_Newbase.businessScope }}
            </div>
          </el-col>
        </el-row>
      </el-collapse-item>
      <!-- <el-collapse-item title="登记信息" name="2">
        <el-row>
          <el-col :span="12"><div class="bg-purple">公司名称：</div></el-col>
          <el-col :span="12"><div class="bg-purple-light"></div></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><div class="bg-purple">付款方式：</div></el-col>
          <el-col :span="12"><div class="bg-purple-light"></div></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><div class="bg-purple">付款条款：</div></el-col>
          <el-col :span="12"><div class="bg-purple-light"></div></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><div class="bg-purple">税率：</div></el-col>
          <el-col :span="12"><div class="bg-purple-light"></div></el-col>
        </el-row>
      </el-collapse-item> -->
      <!-- <el-collapse-item title="法人信息" name="3">
        <el-row>
          <el-col :span="12"><div class="bg-purple">姓名：</div></el-col>
          <el-col :span="12"
            ><div class="bg-purple-light">
              {{ this.requirementHead.legalPerson }}
            </div></el-col
          >
        </el-row>

      </el-collapse-item> -->
      <!-- <el-collapse-item title="地址信息" name="4" class="model_LineList" >
        <div class="LineList">
          <el-row>
            <el-col :span="12">
              <div>
                <span>共{{ this.requirementLineList.length }}条</span>
               <span :data-radius="requirementLineList.length" @click="pre($event,4)">上一条</span>
                <span :data-radius="requirementLineList.length"  @click="next($event,4)">下一条</span>
              </div></el-col
            >

          </el-row>
        </div>
        <div class="card">
          <el-carousel :autoplay="false" ref="carousel4" height="100px" >
            <el-carousel-item
              v-for="(item, index) in this.requirementLineList"
              :key="index"
              name="index"
            >
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">地点名称:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.vendorSiteCode }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">详细地址:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.addressDetail }}
                  </div></el-col
                >
              </el-row>
            </el-carousel-item>
          </el-carousel>
        </div>
      </el-collapse-item> -->
      <el-collapse-item
        v-if="requirementAccess.length"
        title="银行账户信息变更内容"
        name="5"
        class="model_LineList"
      >
        <div class="LineList">
          <el-row>
            <el-col :span="12">
              <div>
                <span>共{{ requirementAccess.length }}条</span>
                <span
                  :data-radius="requirementAccess.length"
                  @click="pre($event,5)"
                >上一条</span>
                <span
                  :data-radius="requirementAccess.length"
                  @click="next($event,5)"
                >下一条</span>
              </div>
            </el-col>
            <el-col :span="12" />
          </el-row>
        </div>
        <div>
          <el-carousel
            id="el-carousel"
            ref="carousel5"
            :autoplay="false"
            height="220px"
          >
            <el-carousel-item
              v-for="(item, index) in requirementAccess"
              :key="index"
              name="index"
            >
              <el-row>
                <el-col
                  :span="12"
                >
                  <div class="bg-purple">
                    银行代码:
                  </div>
                </el-col>
                <el-col
                  :span="12"
                >
                  <div class="bg-purple-light">
                    {{ item.bankCode }}
                  </div>
                </el-col>
              </el-row>
              <el-row>
                <el-col
                  :span="8"
                >
                  <div class="bg-purple">
                    银行名称:
                  </div>
                </el-col>
                <el-col
                  :span="16"
                >
                  <div class="bg-purple-light">
                    {{ item.bankName }}
                  </div>
                </el-col>
              </el-row>
              <el-row>
                <el-col
                  :span="8"
                >
                  <div class="bg-purple">
                    开户行名称:
                  </div>
                </el-col>
                <el-col
                  :span="16"
                >
                  <div class="bg-purple-light">
                    {{ item.openingBank }}
                  </div>
                </el-col>
              </el-row>
              <el-row>
                <el-col
                  :span="8"
                >
                  <div class="bg-purple">
                    分行编码:
                  </div>
                </el-col>
                <el-col
                  :span="16"
                >
                  <div class="bg-purple-light">
                    {{ item.unionCode }}
                  </div>
                </el-col>
              </el-row>
              <el-row>
                <el-col
                  :span="8"
                >
                  <div class="bg-purple">
                    账户名称:
                  </div>
                </el-col>
                <el-col
                  :span="16"
                >
                  <div class="bg-purple-light">
                    {{ item.bankAccountName }}
                  </div>
                </el-col>
              </el-row>
              <el-row>
                <el-col
                  :span="8"
                >
                  <div class="bg-purple">
                    银行账号:
                  </div>
                </el-col>
                <el-col
                  :span="16"
                >
                  <div class="bg-purple-light">
                    {{ item.bankAccount }}
                  </div>
                </el-col>
              </el-row>
              <el-row>
                <el-col
                  :span="8"
                >
                  <div class="bg-purple">
                    币种:
                  </div>
                </el-col>
                <el-col
                  :span="16"
                >
                  <div class="bg-purple-light">
                    {{ $getDictLabel("BID_TENDER_CURRENCY",item.currencyCode) }}
                  </div>
                </el-col>
              </el-row>
              <el-row>
                <el-col
                  :span="8"
                >
                  <div class="bg-purple">
                    是否主账户:
                  </div>
                </el-col>
                <el-col
                  :span="16"
                >
                  <div class="bg-purple-light">
                    {{ item.ceeaMainAccount }}
                  </div>
                </el-col>
              </el-row>
              <el-row>
                <el-col
                  :span="8"
                >
                  <div class="bg-purple">
                    启用:
                  </div>
                </el-col>
                <el-col
                  :span="16"
                >
                  <div class="bg-purple-light">
                    {{ item.ceeaEnabled }}
                  </div>
                </el-col>
              </el-row>
            </el-carousel-item>
          </el-carousel>
        </div>
      </el-collapse-item>
      <el-collapse-item
        title="相关变更认证信息"
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
import {
   getRegion
} from '@/api/common'

export default {
  components: {
    CUploadFile,
    CDownloadLink
  },
  data () {
    return {

      fileuploadsList: [],
       bannerHeight: 640,
        screenWidth: 1920,
      dataAtt: [],
      fileRefresh: false,
      companyId: null,
      filesChangeData: [], // 附件变更数据

      requirementAccess: [], // 账户信息
      approvalFileList: [], // 审批附件信息
      orderFileList: [], // 订单附件
      requirementAffix: [], // 附件
      currencyList: [], // 币种
      dialog: false,
      dialogVisible: false,
      // 文件上传配置信息
      fileInfo: {
        uploadType: 'FASTDFS', // 固定参数
        sourceType: 'WEB_APP', // 固定参数
        fileModular: 'sup', // 文件所属模块 -》基础模块
        fileFunction: 'companyInfoMaintain', // 文件所属功能
        fileType: 'images' // 文件所属类型
      },
      activeNames: ['1', '2', '3', '4', '5', '6'],
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
      baseInfoOldData: {}, // 旧的基础信息
      get_Newbase: {}, // 新的基础信息
      curRel: '',
      curType: '',
     provinceList: [], // 省
      cityList: [], // 市
      curOpt: 'add'
    }
  },

  watch: {
    provinceList () {
      this.$nextTick(() => {
        // 此时就可以获取到在created赋值后的assessmentTypeList了
        this.getFormDetail(this.$attrs.params.changeId)
      })
    }
  },
  created () {
    this.fatchDictData()
    // this.companyId = this.$store.getters.companyId;
    console.log('this.$attrs.params', this.$attrs.params)

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
  },

  mounted () {
    //  this.setSize1();
      // const that = this;
      // 监听浏览器窗口大小改变
      // window.addEventListener('resize', function() {
      //   var width = window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth;
      //   that.screenWidth = width;
      //   that.setSize();
      // }, false);
  },
  methods: {

    fatchDictData () {
      // 批量查询字典
      // let dictParamsArr = [
      //   { dictCode: "VENDOR_SITE_CODE" }, //
      // ];
      // getDictItemList(dictParamsArr).then((res) => {
      //   const [VENDOR_SITE_CODE] = res.data;
      //   this.currencyList = adaptDictData(
      //     VENDOR_SITE_CODE.VENDOR_SITE_CODE,
      //     "dict"
      //   );
      // });

       // 加载省
      getRegion({ queryType: 'province' }).then((res) => {
        if (res.data) {
          this.provinceList = this.adaptProvinceCity(res.data, 'province')
        }
      })
    },
      // 加载市
    getRegionData (val) {
      let parame = { queryType: 'city', parentId: val }
      getRegion(parame).then(res => {
        if (res.data) {
          this.cityList = this.adaptProvinceCity(res.data, 'city')
        }
      })
    },

     // 适配省 市
    adaptProvinceCity (data, type) {
      let arr = []
      if (data && data.length > 0) {
        if (type === 'province') {
          // 省
          data.forEach(element => {
            arr.push({
              id: element.provinceId,
              value: element.provinceId.toString(),
              label: element.province
            })
          })
        } else if (type === 'city') {
          // 市
          data.forEach(element => {
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

    // 移除
    outerHandleRemove (fileuploadId) {},
    handleScriptProgress (percent) {},
    // 银行账户
    getCompanyForEdit (data) {
        return this.$http({
          url: '/api-sup/info/companyInfo/getInfoByParam',
          method: 'POST',
          params: data,
          loading: true
        })
    },

    // 获取变更附件
    getlistPage (data) {
      return this.$api.base.getFileListByBusinessId(data)
    },
    // .then(async (res) => {
    //         console.log("附件",res)
    //         this.fileuploadsList = res.data.list

    //     })
      // 获取创建人、部门
    getDeptName (data) {
      if (data) {
        return this.$http({
          url: '/api-rbac/rbac-anon/getUserInfoByAccount',
          method: 'GET',
          params: { account: data }
        })
      }
    },

    getFormDetail (changeId) {
       this.$http({
        url: '/api-sup/change/infoChange/getInfoByChangeId',
        method: 'GET',
        params: { changeId },
        loading: true
      })
        .then(async (res) => {
          if (res.data) {
            try {
            // 获取旧的基础信息
            let companyId = res.data.companyInfoChange.companyId || ''
            this.companyId = companyId
            // this.getlistPage();

            let oldData = await this.getCompanyForEdit({ companyId })
            console.log('oldData', oldData)
            let baseInfoOldData = oldData.data.companyInfo || {}
            this.curOpt = this.$attrs.params.flag
            this.curRel = baseInfoOldData.overseasRelation || ''
            this.curType = baseInfoOldData.companyType || ''
            let requirementHeadObject = Object.assign(
              res.data.infoChange,
              res.data.companyInfoChange
            )

                let getDeptName = await this.getDeptName(requirementHeadObject.createdBy)
                let createdBy = ''
                let ceeaDeptName = ''
                if (getDeptName) {
                    createdBy = getDeptName.data.nickName
                    ceeaDeptName = getDeptName.data.department
                }

            console.log('createdBy', createdBy, 'ceeaDeptName', ceeaDeptName)
            // console.log("[requirementHeadObject]", requirementHeadObject);
            // 基础信息对比新旧数据合并
            // let get_baseInfoOldData = Object.assign(this.baseInfoOldData,requirementHeadObject)
            // console.log("get_baseInfoOldData",get_baseInfoOldData)
            let get_Newbase = {

            }

            for (let key in requirementHeadObject) {
              for (let item in baseInfoOldData) {
                if (key == item && requirementHeadObject[key] != baseInfoOldData[item]) {
                    get_Newbase[key] = requirementHeadObject[key]
                }
              }
            }
            console.log('get_Newbase', get_Newbase)
            if (get_Newbase.province) {
                let get_provice = this.provinceList.find(v => v.value == get_Newbase.province)
              get_Newbase.province = get_provice.province
              }
              if (get_Newbase.city) {
                this.getRegionData(get_Newbase.city)
                let get_city = this.cityList.find(v => v.value == get_Newbase.city)
                get_Newbase.city = get_provice.city
              }

              this.get_Newbase = get_Newbase
              console.log('Object.keys(this.get_Newbase).length', Object.keys(this.get_Newbase).length)
              // console.log("Object.getOwnPropertyNames(get_Newbase).length",Object.getOwnPropertyNames(get_Newbase).length)
            if (requirementHeadObject.enable4MChange == 'N') {
              requirementHeadObject.enable4MChange = '否'
            } else if (requirementHeadObject.enable4MChange == 'Y') {
              requirementHeadObject.enable4MChange = '是'
            } else {
              requirementHeadObject.enable4MChange = ''
            }
            requirementHeadObject.createdBy = createdBy
            requirementHeadObject.ceeaDeptName = ceeaDeptName
            this.requirementHead = requirementHeadObject

            // console.log("provinceList", this.provinceList);

            // 供应商地址信息
            // let siteInfoChanges = res.data.siteInfoChanges;

            // siteInfoChanges.forEach((item) => {
            //   this.currencyList.forEach((elm) => {
            //     if (elm.value === item.vendorSiteCode) {
            //       item.vendorSiteCode = elm.label;
            //     }
            //   });
            // });
            // // console.log("siteInfoChanges",siteInfoChanges)
            // this.requirementLineList = siteInfoChanges;
            // 附件信息
            // let {data} = await this.getlistPage({businessId:companyId})
            //  console.log("dataFILE",data)

            //  this.fileuploadsList = data.list;
            // 变更的附件信息
            this.fileuploadsList = res.data.fileuploadChanges
            // //银行信息
          let bankInfoChanges = res.data.bankInfoChanges || []
             console.log('bankInfoChangesA', bankInfoChanges)

             bankInfoChanges = bankInfoChanges.filter(v => (v.opType == 'add' || v.opType == 'update' || v.opType == 'delete'))
             console.log('bankInfoChangesB', bankInfoChanges)
           if (bankInfoChanges.length) {
             bankInfoChanges.forEach(item => {
               if (item.ceeaMainAccount == 'Y') {
                 item.ceeaMainAccount = '是'
               } else if (item.ceeaMainAccount == 'N') {
                 item.ceeaMainAccount = '否'
               } else if (item.ceeaEnabled == 'Y') {
                 item.ceeaEnabled = '是'
               } else if (item.ceeaEnabled == 'N') {
                 item.ceeaEnabled = '否'
               }
             })

             this.requirementAccess = bankInfoChanges
           } else {
             console.log('AAAA')
           }
            } catch (err) {
            console.log(err)
          }
          }
        })
        .catch((err) => {
          console.log(err)
        })
    },
     next (event, number) {
      // console.log(" this.requirementAttaches.length",event.target.dataset.radius)
      this.num += 1
      if (number == 2) {
          if (this.num > event.target.dataset.radius - 1) {
        this.num = 0
            this.$refs.carousel2.setActiveItem(this.num)
        } else {
        this.$refs.carousel2.setActiveItem(this.num)
        }
      } else if (number == 5) {
            if (this.num > event.target.dataset.radius - 1) {
        this.num = 0
            this.$refs.carousel5.setActiveItem(this.num)
        } else {
        this.$refs.carousel5.setActiveItem(this.num)
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
      // console.log(" this.requirementAttaches.length1",event.target.dataset.radius)
      this.num -= 1
      if (number == 2) {
          if (this.num < 0) {
          this.num = event.target.dataset.radius - 1
          this.$refs.carousel2.setActiveItem(this.num)
        } else {
          this.$refs.carousel2.setActiveItem(this.num)
        }
      } else if (number == 5) {
            if (this.num < 0) {
        this.num = event.target.dataset.radius - 1
            this.$refs.carousel5.setActiveItem(this.num)
        } else {
        this.$refs.carousel5.setActiveItem(this.num)
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
    hideDialog () {
      this.dialog = true
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

    handleChange (val) {
      console.log(val)
    },

    setSize1: function () {
        // var width = window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth;
        // this.screenWidth = width;
        // //图片                高 / 宽  500 / 1920
        // this.bannerHeight = 640 / 1103 * this.screenWidth
        document.getElementById('el-carousel').style.height = 50 + 'px'
      },
      setSize: function () {
        // this.bannerHeight = 640 / 1103 * this.screenWidth
        document.getElementById('el-carousel').style.height = 50 + 'px'
      }
  }
}
</script>
<style scoped lang="scss">
.banner, .banner .block, .banner >>> .el-carousel .el-carousel--horizontal, .block >>> .el-carousel__container{
    height: 100%!important;
    overflow: hidden;
}

.popContainer {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
  img {
    width: 100%;
    height: 100%;
    object-fit: contain;
  }
}

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
    // height: 100px;

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
    }
  }
  // .el-collapse-item__wrap{
  //   padding: 0px;
  // }
}
</style>
