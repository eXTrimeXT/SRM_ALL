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
        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              申请单号：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.changeApplyNo }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              申请人：
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
          <el-col
            :span="12"
          >
            <div class="bg-purple">
              变更原因说明：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.changeExplain }}
            </div>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              变更前内容：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light" />
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              变更后内容：
            </div>
          </el-col>
          <el-col :span="12">
            <div class="bg-purple-light" />
          </el-col>
        </el-row>
      </el-collapse-item>
      <el-collapse-item
        title="登记信息"
        name="2"
      >
        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              公司名称：
            </div>
          </el-col>
          <el-col :span="12">
            <div class="bg-purple-light" />
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              付款方式：
            </div>
          </el-col>
          <el-col :span="12">
            <div class="bg-purple-light" />
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              付款条款：
            </div>
          </el-col>
          <el-col :span="12">
            <div class="bg-purple-light" />
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              税率：
            </div>
          </el-col>
          <el-col :span="12">
            <div class="bg-purple-light" />
          </el-col>
        </el-row>
      </el-collapse-item>
      <el-collapse-item
        title="法人信息"
        name="3"
      >
        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              姓名：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.legalPerson }}
            </div>
          </el-col>
        </el-row>
      </el-collapse-item>
      <el-collapse-item
        title="地址信息"
        name="4"
        class="model_LineList"
      >
        <div class="LineList">
          <el-row>
            <el-col :span="12">
              <div>
                <span>共{{ this.requirementLineList.length }}条</span>
                <span
                  :data-radius="requirementLineList.length"
                  @click="pre($event,4)"
                >上一条</span>
                <span
                  :data-radius="requirementLineList.length"
                  @click="next($event,4)"
                >下一条</span>
              </div>
            </el-col>
          </el-row>
        </div>
        <div class="card">
          <el-carousel
            ref="carousel4"
            :autoplay="false"
          >
            <el-carousel-item
              v-for="(item, index) in this.requirementLineList"
              :key="index"
              name="index"
            >
              <el-row>
                <el-col
                  :span="12"
                >
                  <div class="bg-purple">
                    地点名称:
                  </div>
                </el-col>
                <el-col
                  :span="12"
                >
                  <div class="bg-purple-light">
                    {{ item.vendorSiteCode }}
                  </div>
                </el-col>
              </el-row>
              <el-row>
                <el-col
                  :span="12"
                >
                  <div class="bg-purple">
                    详细地址:
                  </div>
                </el-col>
                <el-col
                  :span="12"
                >
                  <div class="bg-purple-light">
                    {{ item.addressDetail }}
                  </div>
                </el-col>
              </el-row>
            </el-carousel-item>
          </el-carousel>
        </div>
      </el-collapse-item>
      <el-collapse-item
        title="账户信息"
        name="5"
        class="model_LineList"
      >
        <div class="LineList">
          <el-row>
            <el-col :span="12">
              <div>
                <span>共{{ this.requirementAccess.length }}条</span>
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
            <el-col :span="12">
              <!-- <div class="document" @click="hideDialog">
                <i class="el-icon-copy-document"></i>全屏表格
              </div> -->
            </el-col>
          </el-row>
        </div>
        <div>
          <el-carousel
            id="el-carousel"
            ref="carousel5"
            :autoplay="false"
          >
            <el-carousel-item
              v-for="(item, index) in this.requirementAccess"
              :key="index"
              name="index"
            >
              <el-row>
                <el-col
                  :span="12"
                >
                  <div class="bg-purple">
                    开户行名称:
                  </div>
                </el-col>
                <el-col
                  :span="12"
                >
                  <div class="bg-purple-light">
                    {{ item.openingBank }}
                  </div>
                </el-col>
              </el-row>
              <el-row>
                <el-col
                  :span="12"
                >
                  <div class="bg-purple">
                    银行账号:
                  </div>
                </el-col>
                <el-col
                  :span="12"
                >
                  <div class="bg-purple-light">
                    {{ item.bankAccount }}
                  </div>
                </el-col>
              </el-row>
            </el-carousel-item>
          </el-carousel>
        </div>
      </el-collapse-item>
      <el-collapse-item
        title="审批附件信息"
        name="6"
      >
        <scene-attachment
          ref="sceneAttachment"
          v-model="dataAtt"
          sence-code="companyInfoMaintain"
          :business-id="companyId"
          :change-data="filesChangeData"
          att-opt="view"
          :up-file-info="fileInfo"
          :file-refresh="fileRefresh"
        />
      </el-collapse-item>
    </el-collapse>
  </div>
</template>
<script>
import CUploadFile from '@/library/components/c-upload-file'
import CDownloadLink from 'lib@/components/c-download-link'
import { parseTime, adaptDictData } from '@/utils'
import SceneAttachment from 'mod@/vendorManagementBuyer/components/SceneAttachment'
import {
  getDictItemList
} from '@/api/common'
export default {
  components: {
    CUploadFile,
    CDownloadLink,
    SceneAttachment
  },
  data () {
    return {
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
        fileFunction: 'vendorBiddingManagement', // 文件所属功能
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
      isFullScreen: false
    }
  },

  watch: {
    currencyList () {
      this.$nextTick(() => {
        // 此时就可以获取到在created赋值后的assessmentTypeList了
        this.getFormDetail(this.$attrs.params.changeId)
      })
    }
  },
  created () {
    this.fatchDictData()
    this.companyId = this.$store.getters.companyId
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
      let dictParamsArr = [
        { dictCode: 'VENDOR_SITE_CODE' } //
      ]
      getDictItemList(dictParamsArr).then((res) => {
        const [VENDOR_SITE_CODE] = res.data
        this.currencyList = adaptDictData(
          VENDOR_SITE_CODE.VENDOR_SITE_CODE,
          'dict'
        )
      })
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

    getFormDetail (changeId) {
      this.$http({
        url: '/api-sup/change/infoChange/getInfoByChangeId',
        method: 'GET',
        params: { changeId },
        loading: true
      })
        .then((res) => {
          if (res.data) {
            let requirementHeadObject = Object.assign(
              res.data.infoChange,
              res.data.companyInfoChange
            )
            console.log('[requirementHeadObject]', requirementHeadObject)
            this.requirementHead = requirementHeadObject
            console.log('currencyList', this.currencyList)

            // 供应商地址信息
            let siteInfoChanges = res.data.siteInfoChanges

            siteInfoChanges.forEach((item) => {
              this.currencyList.forEach((elm) => {
                if (elm.value === item.vendorSiteCode) {
                  item.vendorSiteCode = elm.label
                }
              })
            })
            // console.log("siteInfoChanges",siteInfoChanges)
            this.requirementLineList = siteInfoChanges

            // 银行信息
            this.requirementAccess = res.data.bankInfoChanges
          }
        })
        .catch((err) => {
          console.log(err)
        })
    },
     pre (event, number) {
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
      } else if (number == 4) {
            if (this.num > event.target.dataset.radius - 1) {
        this.num = 0
            this.$refs.carousel4.setActiveItem(this.num)
        } else {
        this.$refs.carousel4.setActiveItem(this.num)
        }
      }
    },
    next (event, number) {
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
