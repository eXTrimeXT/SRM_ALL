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
              订单编号：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.orderNumber }}
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
              {{ this.requirementHead.ceeaOrgName }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              采购员：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.buyerName }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              创建时间：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.ceeaPurchaseOrderDate }}
            </div>
          </el-col>
        </el-row>

        <el-row>
          <el-col
            :span="12"
          >
            <div class="bg-purple">
              是否供应商评审：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{
                this.requirementHead.ceeaIfSupplierConfirm == "Y" ? "是" : "否"
              }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="6">
            <div class="bg-purple">
              供应商：
            </div>
          </el-col>
          <el-col
            :span="18"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.vendorName }}
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
              {{ this.requirementHead.comments }}
            </div>
          </el-col>
        </el-row>
      </el-collapse-item>
      <el-collapse-item
        title="收获/收单信息"
        name="2"
      >
        <el-row>
          <el-col :span="6">
            <div class="bg-purple">
              收获地址：
            </div>
          </el-col>
          <el-col
            :span="18"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.ceeaReceiveAddress }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              收单地址：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.ceeaReceiveOrderAddress }}
            </div>
          </el-col>
        </el-row>
      </el-collapse-item>
      <el-collapse-item
        title="合计信息"
        name="3"
      >
        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              合计数量：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.ceeaTotalNum }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col
            :span="12"
          >
            <div class="bg-purple">
              总额（含税）：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.requirementHead.ceeaTaxAmount }}
            </div>
          </el-col>
        </el-row>
      </el-collapse-item>
      <el-collapse-item
        title="采购订单行"
        name="4"
        class="model_LineList"
      >
        <template v-if="requirementLineList.length > 6">
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
                prop="index"
                label="序号"
                width="100"
              />
              <el-table-column
                prop="materialName"
                label="物资名称"
              />
              <el-table-column
                prop="unit"
                label="单位"
              />
              <el-table-column
                prop="currencyName"
                label="币种"
              />
              <el-table-column
                prop="ceeaUnitTaxPrice"
                label="单价"
              />
              <el-table-column
                prop="ceeaTaxRate"
                label="税率"
              />
              <el-table-column
                prop="orderNum"
                label="数量"
              />
              <el-table-column
                prop="ceeaAmountIncludingTax"
                label="总价"
              />
              <el-table-column
                prop="ceeaPlanReceiveDate"
                label="需求日期"
              />
              <el-table-column
                prop="ceeaPromiseReceiveDate"
                label="承诺日期"
              />
              <el-table-column
                prop="ceeaContractNo"
                label="合同"
              />
              <el-table-column
                prop="comments"
                label="备注"
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
            />
            <el-table-column
              prop="materialName"
              label="物资名称"
            />
            <el-table-column
              prop="unit"
              label="单位"
            />
            <el-table-column
              prop="currencyName"
              label="币种"
            />
            <el-table-column
              prop="ceeaUnitTaxPrice"
              label="单价"
            />
            <el-table-column
              prop="ceeaTaxRate"
              label="税率"
            />
            <el-table-column
              prop="orderNum"
              label="数量"
            />
            <el-table-column
              prop="ceeaAmountIncludingTax"
              label="总价"
            />
            <el-table-column
              prop="ceeaPlanReceiveDate"
              label="需求日期"
            />
            <el-table-column
              prop="ceeaPromiseReceiveDate"
              label="承诺日期"
            />
            <el-table-column
              prop="ceeaContractNo"
              label="合同"
            />
            <el-table-column
              prop="comments"
              label="备注"
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
                  ><div class="bg-purple">物资名称:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.materialName }}
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
                <el-col :span="12"><div class="bg-purple">币种:</div></el-col>
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.currencyName }}
                  </div></el-col
                >
              </el-row>
              <el-row>

                <el-col :span="12"><div class="bg-purple">单价:</div></el-col>
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.ceeaUnitTaxPrice }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"><div class="bg-purple">税率:</div></el-col>
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.ceeaTaxRate }}
                  </div></el-col
                >
              </el-row>
              <el-row>

                <el-col :span="12"><div class="bg-purple">数量:</div></el-col>
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.orderNum }}
                  </div></el-col
                >
              </el-row>
              <el-row>

                <el-col :span="12"><div class="bg-purple">总价:</div></el-col>
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.ceeaAmountIncludingTax }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">需求日期:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.ceeaPlanReceiveDate }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">承诺日期:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.ceeaPromiseReceiveDate }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"><div class="bg-purple">合同:</div></el-col>
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.ceeaContractNo }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="4"><div class="bg-purple">备注:</div></el-col>
                <el-col :span="20"
                  ><div class="bg-purple-light">
                    {{ item.comments }}
                  </div></el-col
                >
              </el-row>
            </el-carousel-item>
          </el-carousel>
        </div> -->
      </el-collapse-item>
      <el-collapse-item
        title="审批附件信息"
        name="5"
      >
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
            prop="reviewReason"
            label="附件名称"
            style="width: 40%"
          >
            <template slot-scope="scope">
              <div class="download-link-wrap">
                <c-download-link
                  :id="scope.row.fileuploadId"
                  :name="scope.row.attachName"
                  ellipsis
                  class="download-link-item"
                />
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-collapse-item>
      <el-collapse-item
        title="订单附件信息"
        name="6"
      >
        <el-table
          :data="orderFileList"
          style="width: 100%"
        >
          <el-table-column
            type="index"
            label="序号"
            width="100"
          />
          <el-table-column
            prop="reviewReason"
            label="附件名称"
            style="width: 40%"
          >
            <template slot-scope="scope">
              <div class="download-link-wrap">
                <c-download-link
                  :id="scope.row.fileuploadId"
                  :name="scope.row.attachName"
                  ellipsis
                  class="download-link-item"
                />
              </div>
            </template>
          </el-table-column>
          <!-- <el-table-column align="center" prop="createdBy" label="上传人" width="150">
            </el-table-column> -->
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
      currentPage: 1, // 采购品类初始页
      pagesize: 10, //    每页的数据
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
        this.getFormDetail(this.$attrs.params.requirementHeadId)
      })
    }
  },
  created () {
    this.fatchDictData()
    console.log('this.$attrs.params', this.$attrs.params)
    // this.getFormDetail(this.$attrs.params.requirementHeadId);

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
        { dictCode: 'BID_TENDER_CURRENCY' } //
      ]
      getDictItemList(dictParamsArr).then((res) => {
        const [BID_TENDER_CURRENCY] = res.data
        this.currencyList = adaptDictData(
          BID_TENDER_CURRENCY.BID_TENDER_CURRENCY,
          'dict'
        )
        console.log('assessmentTypeList111', this.currencyList)
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
    current_change: function (currentPage) {
      console.log(currentPage)
      this.currentPage = currentPage
      // debugger
    },
    // 移除
    outerHandleRemove (fileuploadId) {},
    handleScriptProgress (percent) {},

    getFormDetail (orderId) {
      this.$http({
        url: '/api-sup-ce/po/order/queryOrderById',
        method: 'GET',
        params: { orderId: orderId },
        loading: true
      })
        .then((res) => {
          if (res.data) {
            console.log('res', res)
            let order = res.data.order
            order.ceeaPurchaseOrderDate = order.ceeaPurchaseOrderDate.split(
              ' '
            )[0]
            this.requirementHead = order
            let detailList = res.data.detailList
            detailList.forEach((item) => {
              item.ceeaPlanReceiveDate = item.ceeaPlanReceiveDate.split(' ')[0]

              this.currencyList.forEach((elm) => {
                if (elm.value == item.currencyName) {
                  item.currencyName = elm.label
                }
              })
              item.ceeaPromiseReceiveDate = item.ceeaPromiseReceiveDate.split(
                ' '
              )[0]
            })

            this.requirementLineList = detailList
             if (res.data.attachList) {
              this.approvalFileList = res.data.attachList.filter(v => v.orderAttachType == 'APPROVAL')
              this.orderFileList = res.data.attachList.filter(v => v.orderAttachType == 'ORDER')
            }
          }
        })
        .catch((err) => {
          console.log(err)
        })
    },
    next () {
      this.num += 1
      if (this.num > this.requirementLineList.length - 1) {
        this.num = 0
        this.$refs.carousel.setActiveItem(this.num)
      } else {
        this.$refs.carousel.setActiveItem(this.num)
      }
    },
    pre () {
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
