<template>
  <el-container class="flex-container" dierection="verticval">
    <el-main>
      <el-collapse v-model="activaNames" class="tab-form-style">
        <el-collapse-item :title="$t('cusEntry.technicalFlow.openTechnicalBidDetail')" name="1">
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
                <el-form-item prop="extTechPrincipal" :label="$t('cusEntry.technicalFlow.technicalDirector')">
                  <el-input v-model="baseInfo.extTechPrincipal" />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="3">
                <el-form-item prop="createdFullName" :label="$t('cusEntry.technicalFlow.bidDirector')">
                  <el-input v-model="baseInfo.createdFullName" />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-form>
        </el-collapse-item>
      </el-collapse>
      <CToolbar v-if="showBtn">
        <template slot="right">
          <el-button
            type="primary"
            @click="openTechnicalBid"
          >
            {{ $t('cusEntry.common.openTechnicalBid') }}
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
      showBtn: true,
      evaGroupList: [],
      detailForm: {}
    }
  },
  created () {
    const projectId = this.$attrs.params.projectId
    this.getFormDetail(projectId)
    this.getOpenBidPermission(projectId)
  },
  methods: {
    // 技术开标
    openTechnicalBid () {
      let extBidEvaluatorNum = this.detailForm.extBidEvaluatorNum
      let evaGroupListNum = this.evaGroupList.length
      if (extBidEvaluatorNum != evaGroupListNum) {
        this.$message.warning(this.$t('cusEntry.tipMessage.inconsistentNumberOfPeople'))
        return
      }
      bidBuyerHttp.tech.openTech(this.$attrs.params.projectId).then(res => {
        this.$message.success(this.$t('cusEntry.common.operationSuccess'))
        this.getOpenBidPermission(this.$attrs.params.projectId)
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
    // 是否具备操作开标权限
    getOpenBidPermission (projectId) {
      bidBuyerHttp.tech.getTechManagement(projectId).then(res => {
        if (res.data) {
          const {
            openUserList,
            evaGroupList,
            ...form
          } = res.data
          this.detailForm = form
          this.evaGroupList = evaGroupList
          this.showBtn = openUserList.find(item => item.userName === this.$store.getters.userInfo.username)?.openStatus === 'PENDING'
        }
      })
    }
  }
}
</script>
