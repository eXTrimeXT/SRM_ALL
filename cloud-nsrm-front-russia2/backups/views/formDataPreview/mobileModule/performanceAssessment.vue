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
          <el-col
            :span="12"
          >
            <div class="bg-purple">
              {{ $t('perfMod.assessmentNo')+' :' }}
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.assessmentNo }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              {{ $t('perfMod.assessmentDate')+' :' }}
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.assessmentDate }}
            </div>
          </el-col>
        </el-row>
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
            :span="12"
          >
            <div class="bg-purple">
              {{ $t('perfMod.indicatorDimension')+' :' }}
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ $getDictLabel("INDICATORS_DIM",requirementHead.indicatorDimension) }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              {{ $t('perfMod.indicatorName')+' :' }}
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.indicatorName }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              评价结果：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ $getDictLabel("EVALUATION_RESULTS",requirementHead.ceeaIndicatorLineDes) }}
            </div>
          </el-col>
        </el-row>
        <!-- <el-row>
          <el-col :span="12"><div class="bg-purple">{{$t('perfMod.assessmentPenalty')+' :'}}</div></el-col>
          <el-col :span="12"
            ><div class="bg-purple-light">
              {{ this.requirementHead.assessmentPenalty }}
            </div></el-col
          >
        </el-row>
        <el-row>
          <el-col :span="12"><div class="bg-purple">{{$t('perfMod.taxCode')+' :'}}</div></el-col>
          <el-col :span="12"
            ><div class="bg-purple-light">
              {{ this.requirementHead.taxCode }}
            </div></el-col
          >
        </el-row> -->
        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              {{ $t('perfMod.actualAssessmentAmountY')+' :' }}
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.actualAssessmentAmountY }}
            </div>
          </el-col>
        </el-row>
        <!-- <el-row>
          <el-col :span="12"><div class="bg-purple">{{$t('perfMod.actualAssessmentAmountN')+' :'}}</div></el-col>
          <el-col :span="12"
            ><div class="bg-purple-light">
              {{ this.requirementHead.actualAssessmentAmountN }}
            </div></el-col
          >
        </el-row> -->
        <el-row>
          <el-col
            :span="12"
          >
            <div class="bg-purple">
              业务实体：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.organizationName }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col
            :span="5"
          >
            <div class="bg-purple">
              {{ $t('perfMod.vendorName')+' :' }}
            </div>
          </el-col>
          <el-col
            :span="19"
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
              {{ $t('perfMod.currencyCode')+' :' }}
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ $getDictLabel("currency",requirementHead.currencyCode) }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col
            :span="12"
          >
            <div class="bg-purple">
              {{ $t('perfMod.categoryName')+' :' }}
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ $getDictLabel("currency",requirementHead.categoryName) }}
            </div>
          </el-col>
        </el-row>
        <!-- <el-row>
          <el-col :span="12"
            ><div class="bg-purple">{{$t('perfMod.materialCode')+' :'}}</div></el-col
          >
          <el-col :span="12"
            ><div class="bg-purple-light">
              {{ $getDictLabel("currency",requirementHead.materialCode)  }}
            </div></el-col
          >
        </el-row>
        <el-row>
          <el-col :span="12"
            ><div class="bg-purple">{{$t('perfMod.materialName')+' :'}}</div></el-col
          >
          <el-col :span="12"
            ><div class="bg-purple-light">
              {{ $getDictLabel("currency",requirementHead.materialName)  }}
            </div></el-col
          >
        </el-row> -->
        <el-row>
          <el-col
            :span="12"
          >
            <div class="bg-purple">
              {{ $t('perfMod.respFullName')+' :' }}
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ $getDictLabel("currency",requirementHead.respFullName) }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col
            :span="5"
          >
            <div class="bg-purple">
              {{ $t('perfMod.assessmentFiles')+' :' }}
            </div>
          </el-col>
          <el-col
            :span="19"
          >
            <div class="bg-purple-light">
              <c-download-link
                :id="requirementHead.fileUploadId"
                :name="requirementHead.fileSourceName"
                ellipsis
                class="download-link-item"
              />
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col
            :span="12"
          >
            <div class="bg-purple">
              建议降级至：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ $getDictLabel("CATEGORY_STATUS",requirementHead.ceeaCategoryStatus) }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col
            :span="12"
          >
            <div class="bg-purple">
              {{ $t('perfMod.ProcessStatus')+' :' }}
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ $getDictLabel("VENDOR_ASSES_STATUS",requirementHead.status) }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col
            :span="12"
          >
            <div class="bg-purple">
              考核类型：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ $getDictLabel("ASSESSMENT_BILL_TYPE",requirementHead.ceeaAssessmentType) }}
            </div>
          </el-col>
        </el-row>

        <el-row>
          <el-col
            :span="12"
          >
            <div class="bg-purple">
              标题：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ $getDictLabel("ASSESSMENT_BILL_TYPE",requirementHead.ceeaAssessmentTitle) }}
            </div>
          </el-col>
        </el-row>

        <!-- <el-row>
          <el-col :span="12"
            ><div class="bg-purple">是否结算关联：</div></el-col
          >
          <el-col :span="12"
            ><div class="bg-purple-light">
              {{this.requirementHead.ceeaAssociatedStates}}
            </div></el-col
          >
        </el-row>
          <el-row>
          <el-col :span="8"
            ><div class="bg-purple">BPM起草人意见：</div></el-col
          >
          <el-col :span="16"
            ><div class="bg-purple-light">
              {{this.requirementHead.ceeaDrafterOpinion}}
            </div></el-col
          >
        </el-row> -->
        <el-row>
          <el-col
            :span="5"
          >
            <div class="bg-purple">
              考核说明：
            </div>
          </el-col>
          <el-col
            :span="19"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.explanation }}
            </div>
          </el-col>
        </el-row>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>
<script>
import CUploadFile from '@/library/components/c-upload-file'
import CDownloadLink from 'lib@/components/c-download-link'

import SceneAttachment from './SceneAttachment'
import OrganizationSelector from 'lib@/components/organization-selector'

export default {
  components: {
    CUploadFile,
    CDownloadLink,
    SceneAttachment,
    OrganizationSelector
  },
  data () {
    return {
      indicatorNameList: [
        { value: 'gfgf', label: '指标1' },
        { value: 'bnbnb', label: '指标2' }
      ],
       yesNoList: [
        { value: 'Y', label: this.$t('common.yes') },
        { value: 'N', label: this.$t('common.no') }
      ],
      curOpt: 'add',
      curOrderId: null, // 单据ID
      reviewFormExpsData: [],
      siteJournalsData: [],
      bankJournalsData: [],
      categoryData: [],
      BUList: [],
      buObj: {},
      tableData: [],
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
    console.log('this.$attrs.params', this.$attrs.params)
    this.getFormDetail(this.$attrs.params.orderId)

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
     getFormDetail (vendorAssesId) {
      try {
         if (!vendorAssesId) return
       this.$http({
        url: '/api-pef/vendorAsses/queryById',
        method: 'GET',
        params: { vendorAssesId },
        loading: true
      })
        .then(async (data) => {
          if (data.data) {
            let requirementHead = data.data
            console.log('data.data', data.data)

                    // 根据指标类型和指标维度查询指标
                      let params = {}
              params.indicatorType = 'ASSESSMENT'
              params.indicatorDimension = data.data.indicatorDimension
              let indicatorDimension = await this.getPefTempIndicatorByDim(params) // 查询考核指标下拉
              // console.log("indicatorDimension",indicatorDimension)
              this.yesNoList.forEach(item => {
                if (item.value == requirementHead.ceeaAssociatedStates) {
                  requirementHead.ceeaAssociatedStates = item.label
                }
              })
              let depName = requirementHead.createdBy || ''
            let deptName = await this.getDeptName(depName)
            console.log('[deptName]', deptName)
            if (deptName) {
               requirementHead.createdBy = deptName.data.nickName || ''
                requirementHead.ceeaDeptName = deptName.data.department || ''
            }

           this.requirementHead = requirementHead
          }
        })
        .catch((err) => {
          console.log(err)
        })
      } catch (err) {

      }
    },
    // 根基类型和维度获取指标
          getPefTempIndicatorByDim (params) {
            return this.$http({
            url: '/api-pef/template/findIndicatorsHeaderByDimension',
            method: 'GET',
            params,
            loading: false
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
