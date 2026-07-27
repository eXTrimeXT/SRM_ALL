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
              申请编号：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.requirementHeadNum }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              物料大类：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.categoryName }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              业务实体：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.orgName }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              申请部门：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.ceeaDepartmentName }}
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
              {{ this.requirementHead.createdFullName }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              申请日期：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.applyDate }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              立项编号：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.ceeaProjectNum }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              项目名：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.ceeaProjectName }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              资产类别：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ $getDictLabel("ASSET_TYPE", this.requirementHead.ceeaAssetType) }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="6">
            <div class="bg-purple">
              备注：
            </div>
          </el-col>
          <el-col
            :span="18"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.comments }}
            </div>
          </el-col>
        </el-row>
        <el-row v-if="requirementHead.ceeaPurchaseType=='URGENT'">
          <el-col :span="8">
            <div class="bg-purple">
              紧急情况说明：
            </div>
          </el-col>
          <el-col
            :span="16"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.ceeaUrgencyExplain }}
            </div>
          </el-col>
        </el-row>
        <el-row v-if="requirementHead.ceeaPurchaseType=='APPOINT'">
          <el-col :span="6">
            <div class="bg-purple">
              指定原因：
            </div>
          </el-col>
          <el-col
            :span="18"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.ceeaAppointReason }}
            </div>
          </el-col>
        </el-row>
      </el-collapse-item>
      <el-collapse-item
        title="物资明细"
        name="2"
        class="model_LineList"
      >
        <template v-if="requirementLineList.length > 10">
          <div class="LineList">
            <el-pagination
              :page-size="pagesize"
              layout="total,prev, next"
              :total="requirementLineList.length"
              @current-change="current_change"
            />
          </div>
          <div class="card">
            <!-- categoryData.slice((currentPage-1)*pagesize,currentPage*pagesize) -->
            <el-table
              :data="
                requirementLineList.slice(
                  (currentPage - 1) * pagesize,
                  currentPage * pagesize
                )
              "
              style="width: 100%; margin-top: 6px;"
            >
              <el-table-column
                type="index"
                label="序号"
                width="100"
              >
                <!-- <template slot-scope="scope">{{ scope.row.index +1 }}</template> -->
              </el-table-column>
              <el-table-column
                prop="organizationName"
                label="库存组织:"
              />
              <el-table-column
                prop="ceeaDeliveryPlace"
                label="交货地点:"
              />
              <el-table-column
                prop="materialName"
                label="物资名称:"
              />
              <el-table-column
                prop="requirementQuantity"
                label="数量:"
              />
              <el-table-column
                prop="unit"
                label="单位:"
              />
              <el-table-column
                prop="notaxPrice"
                label="建议单价:"
              />
              <el-table-column
                prop="vendorName"
                label="指定公司名称:"
              />
              <el-table-column
                prop="requirementDate"
                label="需求日期:"
              />
              <el-table-column
                prop="totalAmount"
                label="总金额:"
              />
              <el-table-column
                prop="internalComments"
                label="备注:"
              />
            </el-table>
          </div>
        </template>
        <div
          v-else
          class="card"
        >
          <!-- categoryData.slice((currentPage-1)*pagesize,currentPage*pagesize) -->
          <el-table
            :data="
              requirementLineList.slice(
                (currentPage - 1) * pagesize,
                currentPage * pagesize
              )
            "
            style="width: 100%; margin-top: 6px;"
          >
            <el-table-column
              type="index"
              label="序号"
              width="100"
            >
              <!-- <template slot-scope="scope">{{ scope.row.index }}</template> -->
            </el-table-column>
            <el-table-column
              prop="organizationName"
              label="库存组织:"
            />
            <el-table-column
              prop="ceeaDeliveryPlace"
              label="交货地点:"
            />
            <el-table-column
              prop="materialName"
              label="物资名称:"
            />
            <el-table-column
              prop="requirementQuantity"
              label="数量:"
            />
            <el-table-column
              prop="unit"
              label="单位:"
            />
            <el-table-column
              prop="notaxPrice"
              label="建议单价:"
            />
            <el-table-column
              prop="vendorName"
              label="指定公司名称:"
            />
            <el-table-column
              prop="requirementDate"
              label="需求日期:"
            />
            <el-table-column
              prop="totalAmount"
              label="总金额:"
            />
            <el-table-column
              prop="internalComments"
              label="备注:"
            />
          </el-table>
        </div>
        <!-- <div class="LineList">
          <el-row>
            <el-col :span="12">
              <div>
                <span>共{{ this.requirementLineList.length }}条</span>
                <span @click="pre">上一条</span>
                <span @click="next">下一条</span>
              </div></el-col
            >
            <el-col :span="12">
              <div class="document" @click="hideDialog">
                <i class="el-icon-copy-document"></i>全屏表格
              </div>
              <el-button type="text" class="document" @click="dialogVisible = true"><i class="el-icon-copy-document"></i>全屏表格</el-button>
            </el-col>
          </el-row>
        </div> -->
        <!-- <div class="card">
          <el-carousel height="200" :autoplay="false" ref="carousel">
            <el-carousel-item
              v-for="(item, index) in this.requirementLineList"
              :key="index"
              name="index"
            >
              <el-row>
                <el-col :span="12"><div class="bg-purple">行号：</div></el-col>
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ index + 1 }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">库存组织:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.organizationName }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">交货地点:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.ceeaDeliveryPlace }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="6"
                  ><div class="bg-purple">物资名称:</div></el-col
                >
                <el-col :span="18"
                  ><div class="bg-purple-light">
                    {{ item.materialName }}
                  </div></el-col
                >
              </el-row>
              <el-row>

                <el-col :span="12"><div class="bg-purple">数量:</div></el-col>
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.requirementQuantity }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"><div class="bg-purple">单位:</div></el-col>
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.unit }}
                  </div></el-col
                >
              </el-row>
              <el-row>

                <el-col :span="12"
                  ><div class="bg-purple">建议单价:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.notaxPrice }}
                  </div></el-col
                >
              </el-row>
              <el-row>

                <el-col :span="8"
                  ><div class="bg-purple">指定公司名称:</div></el-col
                >
                <el-col :span="16"
                  ><div class="bg-purple-light">
                    {{ item.vendorName }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">需求日期:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.requirementDate }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"><div class="bg-purple">总金额:</div></el-col>
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.totalAmount }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"><div class="bg-purple">备注:</div></el-col>
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.internalComments }}
                  </div></el-col
                >
              </el-row>
            </el-carousel-item>
          </el-carousel>
        </div> -->

        <!-- <div>
          <el-row>
            <el-col :span="12"><div class="bg-purple">申请编号：</div></el-col>
            <el-col :span="12"
              ><div class="bg-purple-light">
                {{ this.requirementHead.requirementHeadNum }}
              </div></el-col
            >
          </el-row>
        </div> -->
      </el-collapse-item>
      <el-collapse-item
        title="附件信息"
        name="3"
      >
        <el-table
          :data="requirementAttaches"
          style="width: 100%"
          border
          max-height="250px"
        >
          <el-table-column
            align="center"
            type="index"
            :label="$t('purSettlementMod.tabindex')"
            width="50"
          />
          <el-table-column
            align="center"
            prop="attachName"
            label="附件"
          >
            <template slot-scope="scope">
              <c-download-link
                :id="scope.row.fileuploadId"
                :name="scope.row.attachName"
                ellipsis
                class="download-link-item"
              />
              <!-- <i
                class="el-icon-close close-icon"
                @click="outerHandleAttachmentRemove(requirementHead)"
              /> -->
            </template>
          </el-table-column>
        </el-table>
      </el-collapse-item>
    </el-collapse>
    <!-- <div v-show="dialog" class="popContainer">
      <img
        src="https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg"
        alt=""
      />
    </div> -->
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
      currentPage: 1, // 采购品类初始页
      pagesize: 10, //    每页的数据
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
      activeNames: ['1', '2', '3'],
      requirementHead: {},
      requirementAttaches: [],
      requirementLineList: [],
      receivedFactoryOpts: [],
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
    console.log('this.$attrs.params', this.$attrs.params)
    this.getFormDetail(this.$attrs.params.requirementHeadId)

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

  },
  methods: {
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

    getFormDetail (requirementHeadId) {
      this.$http({
        url: '/api-sup-ce/pr/requirementHead/getByHeadId',
        method: 'GET',
        params: { requirementHeadId: requirementHeadId },
        loading: true
      })
        .then((data) => {
          if (data.data) {
            this.requirementHead = data.data.requirementHead
            this.requirementAttaches = data.data.requirementAttaches
            this.requirementLineList = data.data.requirementLineList
            // this.getLocationsList(this.requirementHead.organizationCode);
            console.log(
              'this.requirementLineList',
              this.requirementLineList.length
            )
            // console.log("data.data.requirementLineList",data.data.requirementLineList,222 )
          }
        })
        .catch((err) => {
          console.log(err)
        })
    },
    pre () {
      this.num += 1
      if (this.num > this.requirementLineList.length - 1) {
        this.num = 0
        this.$refs.carousel.setActiveItem(this.num)
      } else {
        this.$refs.carousel.setActiveItem(this.num)
      }
    },
    next () {
      this.num -= 1
      if (this.num < 0) {
        this.num = this.requirementLineList.length - 1
        this.$refs.carousel.setActiveItem(this.num)
      } else {
        this.$refs.carousel.setActiveItem(this.num)
      }
    },
    current_change: function (currentPage) {
      console.log(currentPage)
      this.currentPage = currentPage
      // debugger
    },
    // 设置全屏
    // 全屏设置
    fullTable () {
      // console.log("1111")
      // if (this.canFullScreen) {
      //   if (this.isFullScreen) {
      //     // 关闭全屏
      //     this.exitFullScreen()
      //     this.isFullScreen = false
      //   } else {
      //     // 打开全屏
      //     console.log("2222")
      //     this.Full(document.getElementsByClassName("card")[0])
      //     this.isFullScreen = true
      //   }
      // } else {
      //   this.$message.warning({
      //     content: '当前浏览器暂不支持全屏模式，请切换浏览器后重新尝试！',
      //     duration: 3
      //   })
      // }
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
    }
  }
}
</script>
<style scoped lang="scss">
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
      // min-height: 200px;
    }
  }
  // .el-collapse-item__wrap{
  //   padding: 0px;
  // }
}
</style>
