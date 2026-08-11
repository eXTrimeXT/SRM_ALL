<template>
  <el-container class="flex-container" dierection="verticval">
    <el-main>
      <el-collapse v-model="activaNames" class="tab-form-style">
        <el-collapse-item :title="$t('cusEntry.technicalFlow.openBusniessBidDetail')" name="1">
          <el-form :model="baseInfo" :disabled="true">
            <srm-row>
              <srm-col :initCol="3">
                <el-form-item prop="extProjectNo" :label="$t('cusEntry.technicalFlow.bidProjectNo')">
                  <el-input v-model="baseInfo.extProjectNo" />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="3">
                <el-form-item prop="souName" :label="$t('cusEntry.technicalFlow.bidProjectName')">
                  <el-input v-model="baseInfo.souName" />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="3">
                <el-form-item prop="createdFullName" :label="$t('cusEntry.technicalFlow.bidDirector')">
                  <el-input v-model="baseInfo.createdFullName" />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-form>
          <el-table
            boder
            :data="tableData"
          >
            <el-table-column
              :label="$t('cusEntry.technicalFlow.supplierName')"
              prop="vendorName"
              align="center"
            />
            <el-table-column
              :label="$t('cusEntry.technicalFlow.taxTotalPrice')"
              prop="extTaxAmount"
              align="center"
            />
            <el-table-column
              :label="$t('cusEntry.technicalFlow.quoteNum')"
              prop="tenderTimes"
              align="center"
            />
          </el-table>
        </el-collapse-item>
      </el-collapse>
      <CToolbar v-if="showBtn">
        <template slot="right">
          <el-button
            type="primary"
            @click="openBusinessBid"
          >
            {{ $t('cusEntry.common.openBusinessBid') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>

<script>
import { bidBuyerHttp } from 'modcb@/biddingBuyer/api'
import CToolbar from 'lib@/components/c-toolbar'
export default {
  name: 'TechnicalFlow',
  components: {
    CToolbar
  },
  data () {
    return {
      activaNames: ['1'],
      baseInfo: {},
      tableData: [],
      showBtn: true
    }
  },
  async created () {
    const projectId = this.$attrs.params.projectId
    const round = this.$attrs.params.round
    await this.getFormDetail(projectId)
    this.getQuote({ projectId, round })
  },
  methods: {
    // 商务开标
    openBusinessBid () {
      bidBuyerHttp.control.businessOpen(this.$attrs.params.projectId).then(res => {
        this.$message.success(this.$t('cusEntry.common.operationSuccess'))
        this.getQuote({ projectId: this.$attrs.params.projectId, round: this.$attrs.params.round })
      })
    },
    // 获取详情
    async getFormDetail (projectId) {
      const response = await bidBuyerHttp.init.getProjectInfo(projectId)
      if (!response || !response.data) {
        return
      }
      const {
        groupList = [],
        applyFileList = [],
        bidFileList = [],
        planList = [],
        project = {}
      } = response.data
      this.baseInfo = project
    },
    // 获取报价情况
    getQuote (params) {
      bidBuyerHttp.control.getQuoteDetail(params).then(res => {
        if (res.data) {
          const {
            extSouOrderDtoList,
            showOpenButton
          } = res.data
          this.tableData = extSouOrderDtoList
          this.showBtn = showOpenButton === 'Y'
        }
      })
    },
    // 展示价格明细
    // showPriceDetail () {
    //   const round = Number(this.$attrs.params.round)
    //   const currentRound = Number(this.baseInfo.currentRound)
    //   return round < currentRound || (round == currentRound && ['BUS_BID_OPEN', 'WIN_LOSS_NOTICE', 'NOTICE_ING', 'ARCHIVE_TODO', 'ARCHIVE_DONE'].includes(this.baseInfo.projectStatus))
    // }
  }
}
</script>
