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
              供应商名称：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.vendorName }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col
            :span="12"
          >
            <div class="bg-purple">
              供应商引入单号：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.importNum }}
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
          <el-col :span="12">
            <div class="bg-purple">
              原业务实体：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.oldOrgName }}
            </div>
          </el-col>
        </el-row>

        <!-- <el-row>
          <el-col :span="12"><div class="bg-purple">状态</div></el-col>
          <el-col :span="12"
            ><div class="bg-purple-light">
              {{ this.requirementHead.importStatus }}
            </div></el-col
          >
        </el-row>
        <el-row>
          <el-col :span="12"><div class="bg-purple">创建时间</div></el-col>
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
              供应商引入说明
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.importExplain }}
            </div>
          </el-col>
        </el-row>
      </el-collapse-item>

      <el-collapse-item
        title="引入至业务实体"
        name="2"
        class="model_LineList"
      >
        <div class="LineList">
          <el-row>
            <el-col :span="12">
              <div>
                <span>共{{ this.requirementLineList.length }}条</span>
                <span @click="pre">上一条</span>
                <span @click="next">下一条</span>
              </div>
            </el-col>
          </el-row>
        </div>
        <div class="card">
          <el-carousel
            id="el-carousel"
            ref="carousel"
            :autoplay="false"
            height="60px"
          >
            <el-carousel-item
              v-for="(item, index) in this.requirementLineList"
              :key="index"
              name="index"
            >
              <el-row>
                <el-col :span="12">
                  <div class="bg-purple">
                    引入OU:
                  </div>
                </el-col>
                <el-col
                  :span="12"
                >
                  <div class="bg-purple-light">
                    {{ item.orgId }}
                  </div>
                </el-col>
              </el-row>
              <el-row>
                <el-col
                  :span="12"
                >
                  <div class="bg-purple">
                    引入品类:
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
            </el-carousel-item>
          </el-carousel>
        </div>
      </el-collapse-item>
      <el-collapse-item
        title="引入采购品类"
        name="3"
      >
        <el-table
          :data="displayCatData"
          style="width: 100%"
        >
          <el-table-column
            type="index"
            label="序号"
          />
          <el-table-column
            prop="categoryName"
            label="品类"
          />
        </el-table>
      </el-collapse-item>
      <el-collapse-item
        title="附件"
        name="4"
      >
        <el-table
          :data="fileuploads"
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
            min-width="150"
          >
            <template slot-scope="scope">
              <c-download-link
                :id="scope.row.fileuploadId"
                :name="scope.row.fileFullname"
                ellipsis
                class="download-link-item"
              />
            </template>
          </el-table-column>
        </el-table>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>
<script>
import CUploadFile from '@/library/components/c-upload-file'
import CDownloadLink from 'lib@/components/c-download-link'

import { parseTime, adaptDictData } from '@/utils'

import {
  getDictItemList
} from '@/api/common'
export default {
  components: {
    CUploadFile,
    CDownloadLink

  },
  data () {
    return {
      fileuploads: [],
      displayCatData: [], // 扩展品类
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
        this.getFormDetail(this.$attrs.params.row.importId)
      })
    }
  },
  created () {
    this.fatchDictData()

    console.log('this.$attrs.params', this.$attrs.params)
    // this.getFormDetail(this.$attrs.params.row.importId);
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
    fatchDictData () {
      // 批量查询字典
      let dictParamsArr = [
        { dictCode: 'VENDORIMPORTSTATUS' } //
      ]
      getDictItemList(dictParamsArr).then((res) => {
        const [VENDORIMPORTSTATUS] = res.data
        this.currencyList = adaptDictData(
          VENDORIMPORTSTATUS.VENDORIMPORTSTATUS,
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
    getFormDetail (importId) {
      this.$http({
        url: '/api-sup/vendorImport/getVendorImportDetail',
        method: 'GET',
        params: { importId },
        loading: true
      })
        .then(async (res) => {
          if (res) {
            try {
            let vendorImport = res.data.vendorImport
            this.currencyList.forEach((item) => {
              if (item.value == vendorImport.importStatus) {
                vendorImport.importStatus = item.label
              }
            })

             let getDept = vendorImport.createdBy || ''
          let getDeptName = await this.getDeptName(getDept)
          if (getDeptName) {
             vendorImport.createdBy = getDeptName.data.nickName
            vendorImport.ceeaDeptName = getDeptName.data.department
          }
          // console.log("[getDeptName]",getDeptName)

            this.requirementHead = vendorImport
            this.requirementLineList = res.data.vendorImportDetails

            let displayCatDataArr = this.requirementLineList.map(
              (v) => v.categoryName
            )
            displayCatDataArr = Array.from(new Set(displayCatDataArr))
            this.displayCatData = []
            for (let i = 0; i < displayCatDataArr.length; i++) {
              this.displayCatData.push({
                categoryName: displayCatDataArr[i]
              })
            }

            this.fileuploads = res.data.fileuploads
            } catch (err) {
              console.log(err)
            }
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
