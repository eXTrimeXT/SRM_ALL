<template>
  <el-container
    class="flex-container the_vendorBiddingSignUp_wrapper"
    direction="vertical"
  >
    <el-main>
      <div class="form-container the_progress">
        <el-steps :active="2">
          <el-step :title="$t('bidMod.published')" />
          <el-step :title="$t('bidMod.signingUp')" />
          <el-step :title="$t('bidMod.registered')" />
          <el-step :title="$t('bidMod.eligibilityConfirm')" />
          <el-step :title="$t('bidMod.bidding')" />
          <el-step :title="$t('bidMod.finishBid')" />
        </el-steps>
        <div class="the_footer_row">
          {{ $t("bidMod.registerDeadline")
          }}<span style="color:#f44">{{ showTimeText }}</span>
        </div>
      </div>
      <main-header>
        <template slot="left">
          <span style="padding-right: 11px">{{
            $t("bidMod.inputRegisterInfo")
          }}</span>
          <span style="padding-right: 11px">{{
            $t("bidMod.attachmentUpload")
          }}</span>
          <!-- :disabled="!scopeCompanyCode" -->
          <el-button
            type="primary"
            @click="addOne"
          >
            {{
              $t("common.add")
            }}
          </el-button>
          <el-button
            type="primary"
            @click="saveOne('SAVE')"
          >
            {{
              $t("common.staging")
            }}
          </el-button>
          <el-button
            type="primary"
            @click="saveOne('SUBMIT')"
          >
            {{
              $t("common.submit")
            }}
          </el-button>
          <el-button
            type="primary"
            @click="backOne"
          >
            {{
              $t("common.backTo")
            }}
          </el-button>
        </template>
      </main-header>
      <el-table
        :data="allParams.vendorFileVOs"
        style="width: 100%"
        border
        height="133px"
      >
        <el-table-column
          align="center"
          type="index"
          width="50"
        />
        <!-- <el-table-column align="center" prop="reqFileName" label="资料要求" width="250" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <el-input v-model="scope.row.reqFileName"/>
            </template>
          </el-table-column> -->
        <!-- 附件名称 -->
        <el-table-column
          align="center"
          prop="fileName"
          :label="$t('bidMod.fileName')"
          width="350"
        >
          <template slot-scope="scope">
            <SrmCommonFile
              :extra-data="fileInfo"
              :default-file="{
                fileId: scope.row.docId,
                fileName: scope.row.fileName
              }"
              :readonly="false"
              @on-change="({file}) => outerHandleUploadSuccess(file,scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column
          :label="$t('common.operation')"
          width="100"
        >
          <template slot-scope="scope">
            <el-button
              type="text"
              @click="handleDelClick(scope.$index, scope.row)"
            >
              {{ $t("common.delete") }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import { adaptDictData, parseTime } from '@/utils'
import { tabTodoMixin } from '@/utils/mixins'

export default {
  name: 'VendorBiddingSignUp',
  components: {
    TableView,
    MainHeader
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      showTimeText: this.$t('time.initTime'),
      allParams: {
        vendorFileVOs: []
      },
      form: {
        deadline: '2020-5-1'
      },
      timer: '',
      // 文件上传配置信息
      fileInfo: {
        uploadType: 'FASTDFS', // 固定参数
        sourceType: 'WEB_APP', // 固定参数
        fileModular: 'sup', // 文件所属模块 -》基础模块
        fileFunction: 'vendorBiddingList', // 文件所属功能
        fileType: 'images' // 文件所属类型
      },
      scopeBidingId: '',
      scopeCompanyCode: '',
      scopeOrganizationCode: ''
    }
  },
  created () {
    this.getListDetail(this.$attrs.params.row.bidingId)
    this.form.deadline = this.$attrs.params.row.enrollEndDatetime
    let date3 = new Date(this.form.deadline).getTime() - new Date().getTime() // 时间差的毫秒数
    if (date3 > 0) {
      this.timer = setInterval(() => {
        this.refreshDate()
      }, 1000)
    } else {
      clearInterval(this.timer)
    }
  },
  methods: {
    getListDetail (bidingId) {
      this.$http({
        url: '/api-pd/supplierCooperate/bidSingUp/getSignUpInfo',
        method: 'POST',
        data: this.$attrs.params.row,
        loading: true
      })
        .then(data => {
          if (data && data.data) {
            this.allParams = data.data
            this.scopeBidingId = data.data.bidingId
            this.allParams.vendorFileVOs = data.data.vendorFileVOs
            if (data.data.vendorFileVOs && data.data.vendorFileVOs[0]) {
              this.scopeCompanyCode = data.data.vendorFileVOs[0].companyCode
              this.scopeOrganizationCode =
                data.data.vendorFileVOs[0].organizationCode
            } else {
              this.scopeCompanyCode = ''
              this.scopeOrganizationCode = ''
            }
          }
        })
        .catch(err => {
          console.log(err)
          this.$emit(
            'tab-remove',
            'vendorBiddingSignUp' + this.$attrs.params.row.bidingName
          )
          this.__setTabTodo('vendorBiddingList.getQuerydata')
        })
    },
    refreshDate () {
      let date3 = new Date(this.form.deadline).getTime() - new Date().getTime() // 时间差的毫秒数
      if (date3 < 0) return
      // 计算出相差天数
      var days = Math.floor(date3 / (24 * 3600 * 1000)) // 计算出小时数
      var leave1 = date3 % (24 * 3600 * 1000) // 计算天数后剩余的毫秒数
      var hours = Math.floor(leave1 / (3600 * 1000))
      // 计算相差分钟数
      var leave2 = leave1 % (3600 * 1000) // 计算小时数后剩余的毫秒数
      var minutes = Math.floor(leave2 / (60 * 1000))
      // 计算相差秒数
      var leave3 = leave2 % (60 * 1000) // 计算分钟数后剩余的毫秒数
      var seconds = Math.round(leave3 / 1000)
      this.showTimeText =
        days +
        this.$t('time.day') +
        hours +
        this.$t('time.hour') +
        minutes +
        this.$t('time.minute') +
        seconds +
        this.$t('time.second')
    },
    // 行删除
    handleDelClick (index, row) {
      this.allParams.vendorFileVOs.splice(index, 1)
    },
    addOne () {
      this.allParams.vendorFileVOs.push({
        reqFileName: '',
        docId: '',
        fileName: '',
        bidingId: this.scopeBidingId,
        companyCode: this.scopeCompanyCode,
        organizationCode: this.scopeOrganizationCode,
        comments: ''
      })
    },
    addprocessNode () {
      this.$http({
        url: '/api-pd/bidProcessConfig/processNode/updateNodeStatus',
        method: 'POST',
        data: {
          bidingId: this.$attrs.params.row.bidingId,
          nodeCode: 'entryManagement'
        },
        loading: true
      })
        .then(data => {
          console.log(data)
        })
        .catch(err => {
          console.log(err)
        })
    },
    saveOne (type) {
      for (let i of this.allParams.vendorFileVOs) {
        if (!i.fileName) {
          this.$message.success(this.$t('bidMod.pleaseUploadFile'))
          return
        }
      }
      let url = ''
      if (type === 'SAVE') {
        url = '/api-pd/supplierCooperate/bidSingUp/saveSignUpInfo'
      } else if (type === 'SUBMIT') {
        url = '/api-pd/supplierCooperate/bidSingUp/signUp'
      }
      this.$http({
        url: url,
        method: 'POST',
        data: this.allParams,
        loading: true
      })
        .then(data => {
          if (type === 'SUBMIT') {
            this.addprocessNode()
          }
          this.$message.success(this.$t('common.successSave'))
          this.$emit(
            'tab-remove',
            'vendorBiddingSignUp' + this.$attrs.params.row.bidingName
          )
          this.__setTabTodo('vendorBiddingList.getQuerydata')
        })
        .catch(err => {
          console.log(err)
        })
    },
    outerHandleUploadSuccess (file, row) {
      const { fileId = '', fileName = '' } = file || {}
      fileName.docId = fileId.toString()
      fileName.fileName = fileName
    },
    backOne () {
      this.$emit(
        'tab-remove',
        'vendorBiddingSignUp' + this.$attrs.params.row.bidingName
      )
      this.__setTabTodo('vendorBiddingList.getQuerydata')
    }
  }
}
</script>
<style scoped lang="scss">
.the_vendorBiddingSignUp_wrapper /deep/ {
  .the_progress {
    width: 100%;
    height: 110px;
    background: #eee;
    .el-steps {
      padding-bottom: 0;
    }
    .the_footer_row {
      float: right;
      font-size: 20px;
    }
  }
}
</style>
