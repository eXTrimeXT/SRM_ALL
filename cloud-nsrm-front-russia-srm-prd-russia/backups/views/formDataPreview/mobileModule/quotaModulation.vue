<template>
  <div class="model">
    <el-collapse
      v-model="activeNames"
      class="modelA"
      @change="handleChange"
    >
      <el-collapse-item
        title="配额配置详情"
        name="1"
      >
        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              单据编号：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.quotaAdjust.quotaAdjustCode }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              单据标题：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.quotaAdjust.quotaAdjustName }}
            </div>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              寻源单号：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.quotaAdjust.ceeaSourceNo }}
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
              {{ this.quotaAdjust.createdBy }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              创建日期：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.quotaAdjust.creationDate }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              调整类型：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ $getDictLabel("ADJUSTMENT_TYPE",quotaAdjust.adjustType) }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              状态：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ $getDictLabel("ADJUST_STATUS",quotaAdjust.status) }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="4">
            <div class="bg-purple">
              备注：
            </div>
          </el-col>
          <el-col
            :span="20"
          >
            <div class="bg-purple-light">
              {{ this.quotaAdjust.remark }}
            </div>
          </el-col>
        </el-row>
      </el-collapse-item>

      <el-collapse-item
        title="中标行"
        name="2"
        class="model_LineList"
      >
        <div class="LineList">
          <el-row>
            <el-col :span="12">
              <div>
                <span>共{{ this.quotaSourceDTOList.length }}条</span>
                <span
                  :data-radius="quotaSourceDTOList.length"
                  @click="pre($event, 2)"
                >上一条</span>
                <span
                  :data-radius="quotaSourceDTOList.length"
                  @click="next($event, 2)"
                >下一条</span>
              </div>
            </el-col>
          </el-row>
        </div>
        <div class="card">
          <el-carousel
            ref="carousel2"
            :autoplay="false"
            heigth="20px"
          >
            <el-carousel-item
              v-for="(item, index) in this.quotaSourceDTOList"
              :key="index"
              name="index"
            >
              <el-row>
                <el-col :span="12">
                  <div class="bg-purple">
                    寻源单号:
                  </div>
                </el-col>
                <el-col
                  :span="12"
                >
                  <div class="bg-purple-light">
                    {{ item.ceeaSourceNo }}
                  </div>
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="12">
                  <div class="bg-purple">
                    寻源类型:
                  </div>
                </el-col>
                <el-col
                  :span="12"
                >
                  <div class="bg-purple-light">
                    {{ item.sourceType }}
                  </div>
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="12">
                  <div class="bg-purple">
                    业务实体:
                  </div>
                </el-col>
                <el-col
                  :span="12"
                >
                  <div class="bg-purple-light">
                    {{ item.orgName }}
                  </div>
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="12">
                  <div class="bg-purple">
                    小类:
                  </div>
                </el-col>
                <el-col
                  :span="12"
                >
                  <div class="bg-purple-light">
                    {{ item.categoryName }}
                  </div>
                </el-col>
              </el-row>
              <el-row>
                <el-col
                  :span="12"
                >
                  <div class="bg-purple">
                    物料编码:
                  </div>
                </el-col>
                <el-col
                  :span="12"
                >
                  <div class="bg-purple-light">
                    {{ item.itemCode }}
                  </div>
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="12">
                  <div class="bg-purple">
                    物料名称:
                  </div>
                </el-col>
                <el-col
                  :span="12"
                >
                  <div class="bg-purple-light">
                    {{ item.itemName }}
                  </div>
                </el-col>
              </el-row>
              <el-row>
                <el-col
                  :span="12"
                >
                  <div class="bg-purple">
                    需求数量:
                  </div>
                </el-col>
                <el-col
                  :span="12"
                >
                  <div class="bg-purple-light">
                    {{ item.isNeedTotal }}
                  </div>
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="12">
                  <div class="bg-purple">
                    供应商编码:
                  </div>
                </el-col>
                <el-col
                  :span="12"
                >
                  <div class="bg-purple-light">
                    {{ item.vendorCode }}
                  </div>
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="12">
                  <div class="bg-purple">
                    供应商名称:
                  </div>
                </el-col>
                <el-col
                  :span="12"
                >
                  <div class="bg-purple-light">
                    {{ item.vendorName }}
                  </div>
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="12">
                  <div class="bg-purple">
                    含税单价:
                  </div>
                </el-col>
                <el-col
                  :span="12"
                >
                  <div class="bg-purple-light">
                    {{ item.taxPrice }}
                  </div>
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="12">
                  <div class="bg-purple">
                    价格有效期自:
                  </div>
                </el-col>
                <el-col
                  :span="12"
                >
                  <div class="bg-purple-light">
                    {{ item.startTime }}
                  </div>
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="12">
                  <div class="bg-purple">
                    价格有效期至:
                  </div>
                </el-col>
                <el-col
                  :span="12"
                >
                  <div class="bg-purple-light">
                    {{ item.endTime }}
                  </div>
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="12">
                  <div class="bg-purple">
                    实际比例%:
                  </div>
                </el-col>
                <el-col
                  :span="12"
                >
                  <div class="bg-purple-light">
                    {{ item.quotaProportion }}
                  </div>
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="12">
                  <div class="bg-purple">
                    目标比例%:
                  </div>
                </el-col>
                <el-col
                  :span="12"
                >
                  <div class="bg-purple-light">
                    {{ item.targetProportion }}
                  </div>
                </el-col>
              </el-row>
            </el-carousel-item>
          </el-carousel>
        </div>
      </el-collapse-item>
      <el-collapse-item
        title="附件信息"
        name="3"
        class="model_LineList"
      >
        <div class="LineList">
          <el-row>
            <el-col :span="12">
              <div>
                <span>共{{ this.quotaAdjustFile.length }}条</span>
                <span
                  :data-radius="quotaAdjustFile.length"
                  @click="pre($event, 3)"
                >上一条</span>
                <span
                  :data-radius="quotaAdjustFile.length"
                  @click="next($event, 3)"
                >下一条</span>
              </div>
            </el-col>
          </el-row>
        </div>
        <div class="card">
          <el-carousel
            ref="carousel3"
            :autoplay="false"
            heigth="20px"
          >
            <el-carousel-item
              v-for="(item, index) in this.quotaAdjustFile"
              :key="index"
              name="index"
            >
              <el-row>
                <el-col
                  :span="12"
                >
                  <div class="bg-purple">
                    附件上传:
                  </div>
                </el-col>
                <el-col
                  :span="12"
                >
                  <div class="bg-purple-light">
                    <c-download-link
                      :id="item.fileuploadId"
                      :name="item.fileSourceName || {}"
                    />
                  </div>
                </el-col>
              </el-row>
              <el-row>
                <el-col
                  :span="12"
                >
                  <div class="bg-purple">
                    上传人:
                  </div>
                </el-col>
                <el-col
                  :span="12"
                >
                  <div class="bg-purple-light">
                    {{ item.createdBy }}
                  </div>
                </el-col>
              </el-row>
              <el-row>
                <el-col
                  :span="12"
                >
                  <div class="bg-purple">
                    上传时间:
                  </div>
                </el-col>
                <el-col
                  :span="12"
                >
                  <div class="bg-purple-light">
                    {{ item.creationDate }}
                  </div>
                </el-col>
              </el-row>
              <el-row>
                <el-col
                  :span="4"
                >
                  <div class="bg-purple">
                    备注:
                  </div>
                </el-col>
                <el-col
                  :span="20"
                >
                  <div class="bg-purple-light">
                    {{ item.comment }}
                  </div>
                </el-col>
              </el-row>
            </el-carousel-item>
          </el-carousel>
        </div>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>
<script>
import CUploadFile from '@/library/components/c-upload-file'
import CDownloadLink from 'lib@/components/c-download-link'
import http from '@/utils/http'

export default {
  components: {
    CUploadFile,
    CDownloadLink
  },
  data () {
    return {
      quotaAdjust: {
        quotaAdjustId: null,
        quotaAdjustCode: null,
        quotaAdjustName: null,
        ceeaSourceNo: null,
        createdBy: null,
        creationDate: null,
        adjustType: null,
        status: 'DRAFT',
        remark: null
      },
      quotaAdjustFile: [],
      quotaSourceDTOList: [],
      fileuploadsList: [],
      managementAttaches: [], // 认证情况
      managementInfo: {}, // 管理体系信息
      orgInfos: [], // 业务实体
      orgCategorys: [], // 合作小类
      siteInfos: [], // 供应商地点信息
      bankInfos: [], // 银行信息
      cityList: [], // 市
      provinceList: [], // 省
      curRel: '',
      currencyList_CURRENCY: [], // 币种
      companyInfo: {
        businessLicense: ''
      }, // 企业基本信息
      currencyList_NATURE: [], // 企业性质
      currencyList_RELATION: [], // 境内外关系
      payExplainData: [], // 存储payExplainHttp返回来的数据
      pay_plan: [],
      fileuploads: [],
      displayCatData: [], // 扩展品类
      dataAtt: [],
      fileRefresh: false,
      companyId: null,
      filesChangeData: [], // 附件变更数据
      curType: '',
      requirementAccess: [], // 账户信息
      approvalFileList: [], // 审批附件信息
      orderFileList: [], // 订单附件
      requirementAffix: [], // 附件
      currencyList: [],
      currencyListA: [],
      currencyListB: [],
      currencyListC: [],
      currencyListSTAGE: [], // 到货款
      currencyListPERIOD: [], // 付款账期
      currencyListMODE: [], // 支付方式
      dialog: false,
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
      requirementAttaches: [],
      requirementLineList: [],
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

  watch: {
    // currencyList_RELATION() {
    //   this.$nextTick(() => {
    //     //此时就可以获取到在created赋值后的assessmentTypeList了
    //   let companyId = this.$attrs.params.companyId;
    // this.getFormDetail({ companyId });
    //   });
    // },
  },
  created () {
    console.log('this.$attrs.params', this.$attrs.params)
    let id = this.$attrs.params.row.quotaAdjustId
    this.getFormDetail({ id })
    // this.getlistPage({businessId:companyId});
    //  this.getFormDetail(this.$attrs.params.rowId );
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

  mounted () {},
  methods: {
    getFormDetail (data) {
      this.$http({
        url: '/api-inq/inquiry/quotaAdjust/getQuotaAdjust',
        method: 'GET',
        params: data,
        loading: true
      })
        .then(async (res) => {
          if (res) {
            // console.log("res",res)
            this.quotaAdjust = res.data.quotaAdjust || {}
            this.quotaSourceDTOList = res.data.quotaSourceDTOList || []
            this.quotaAdjustFile = res.data.quotaAdjustFile || []
          }
        })
        .catch((err) => {
          console.log(err)
        })
    },

    getlistPage (data) {
      this.$api.base.getFileListByBusinessId(data).then(async (res) => {
          this.fileuploadsList = res.data.list
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
      } else if (number == 3) {
        if (this.num > event.target.dataset.radius - 1) {
          this.num = 0
          this.$refs.carousel3.setActiveItem(this.num)
        } else {
          this.$refs.carousel3.setActiveItem(this.num)
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
      } else if (number == 3) {
        if (this.num < 0) {
          this.num = event.target.dataset.radius - 1
          this.$refs.carousel3.setActiveItem(this.num)
        } else {
          this.$refs.carousel3.setActiveItem(this.num)
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
      this.n % 3 == 0
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
    }
  }
}
</script>
<style scoped lang="scss">
.banner,
.banner .block,
.banner >>> .el-carousel .el-carousel--horizontal,
.block >>> .el-carousel__container {
  height: 100% !important;
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
