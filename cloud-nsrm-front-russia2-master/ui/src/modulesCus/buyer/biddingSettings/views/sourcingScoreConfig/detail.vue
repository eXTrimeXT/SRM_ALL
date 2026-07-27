<template>
  <el-container
    class="the-sourcingScoreConfigDetail-detail"
    direction="vertical"
  >
    <el-main class="form-container">
      <el-form
        ref="detailForm"
        :model="detailForm"
        :rules="rules"
        :disabled="!editable"
      >
        <srm-row>
          <srm-col :init-col="4">
            <el-form-item
              :label="$t('cusEntry.biddingSettings.scoreTempName')"
              prop="scoreTempName"
            >
              <el-input v-model="detailForm.scoreTempName" :placeholder="$t('cusEntry.common.pleaseFill')" />
            </el-form-item>
          </srm-col>
          <srm-col :init-col="4">
            <el-form-item
              :label="$t('perfMod.templateStatus')"
              prop="status"
            >
              <dict-select v-model="detailForm.status" code="SOURCE_PUBCONFIG_STATUS" disabled />
            </el-form-item>
          </srm-col>
          <srm-col :init-col="4">
            <el-form-item
              :label="$t('common.creator')"
              prop="createdFullName"
            >
              <el-input v-model="detailForm.createdFullName" disabled />
            </el-form-item>
          </srm-col>
          <srm-col :init-col="4">
            <el-form-item
              :label="$t('common.creationDate')"
              prop="creationDate"
            >
              <el-date-picker v-model="detailForm.creationDate" :format="$formatDatePicker" disabled />
            </el-form-item>
          </srm-col>
        </srm-row>
      </el-form>
      <el-button
        type="primary"
        style="margin:10px 0;"
        :disabled="!editable"
        @click="addSoreItem"
      >
        {{ $t('bidMod.addScoreRuleItem') }}
      </el-button>
      <el-table
        border
        :data="souScoreConfigDetailList"
      >
        <el-table-column
          align="center"
          type="index"
          :label="$t('common.sort')"
          width="50"
        />
        <el-table-column
          align="center"
          prop="scoreItem"
          :label="$t('cusEntry.biddingSettings.scoreItem')"
          :render-header="_addStarToColumn"
          minWidth="100"
        >
          <template slot-scope="scope">
            <dict-select
              v-model="scope.row.scoreItem"
              code="SOU_SCORE_CONFIG_ITEM"
              :disabled="!editable"
              @change="val => scoreItemChange(val,scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          prop="reviewItem"
          :label="$t('cusEntry.biddingSettings.reviewItem')"
          :render-header="_addStarToColumn"
          minWidth="100"
        >
          <template slot-scope="scope">
            <dict-select
              v-if="scope.row.scoreItem=='COM_REVIEW'"
              v-model="scope.row.reviewItem"
              code="SCORE_REVIEW_ITEM"
              :disabled="!editable"
            />
            <el-input
              v-else
              v-model="scope.row.reviewItem"
              :placeholder="$t('cusEntry.common.pleaseFill')"
              :disabled="!editable"
            />
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          prop="scoreDesc"
          :label="$t('cusEntry.biddingSettings.scoreDesc')"
          :render-header="_addStarToColumn"
          minWidth="200"
        >
          <template slot-scope="scope">
            <el-input v-model="scope.row.scoreDesc" :placeholder="$t('cusEntry.common.pleaseFill')" :disabled="!editable" />
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          prop="maxScore"
          :label="$t('cusEntry.biddingSettings.maxScore')"
          width="100"
        >
          <template slot-scope="scope">
            <el-input-number
              v-model="scope.row.maxScore"
              :disabled="!editable"
              style="width: 100%"
              :controls="false"
              :min="0"
              :max="100"
            />
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          :label="$t('common.operation')"
          fixed="right"
          width="80"
        >
          <template slot-scope="scope">
            <el-button type="text" :disabled="!editable" @click="delSoreItem(scope.$index, scope.row)">
              {{ $t('common.delete') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <CToolbar>
        <template slot="right">
          <el-button @click="backBill">
            {{ !editable ? $t('common.cancel') : $t('common.close') }}
          </el-button>
          <el-button
            v-if="editable"
            type="primary"
            @click="saveScoreConfig('SAVE')"
          >
            {{ $t('common.save') }}
          </el-button>
          <el-button
            v-if="editable"
            type="primary"
            @click="submitScoreConfig('SUBMIT')"
          >
            {{ $t('common.submit') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'

export default {
  name: 'SourcingScoreConfigDetail',
  components: {
    CToolbar
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      editable: '',
      detailForm: {
        scoreConfigId: '',
        configNumber: '',
        scoreTempName: '',
        status: '',
        configVer: '',
        createdFullName: '',
        creationDate: ''
      },
      rules: {
        scoreTempName: [{ required: true, message: this.$t('cusEntry.common.pleaseFill'), trigger: 'blur' }]
      },
      souScoreConfigDetailList: []
    }
  },
  created () {
    const { createdUserName } = this.$store.getters.userInfo
    const { flag, row } = this.$attrs.params
    this.editable = flag !== 'view'
    if (flag == 'add') {
      this.detailForm.status = 'DRAFT'
      this.detailForm.createdFullName = createdUserName
      this.detailForm.creationDate = new Date()
    } else {
      this.getFormDetail(row.scoreConfigId)
    }
  },
  methods: {
    backBill () {
      if (this.$attrs.params.flag !== 'add') {
        this.$emit('tab-remove', 'sourcingScoreConfigDetail' + this.$attrs.params.row.scoreConfigId)
      } else {
        this.$emit('tab-remove', 'sourcingScoreConfigDetail')
      }
    },
    addSoreItem () {
      this.souScoreConfigDetailList.push({
        scoreItem: '',
        reviewItem: '',
        scoreDesc: '',
        maxScore: null
      })
    },
    delSoreItem (index, row) {
      this.souScoreConfigDetailList.splice(index, 1)
    },
    scoreItemChange (val, row) {
      if (val == 'COM_REVIEW') {
        row.reviewItem = '' // 选择综合评审时, 清空评审项
      }
    },
    getFormDetail (scoreConfigId) {
      this.$http({
        url: '/api-pj/sou/scoreConfig/queryScoreConfig',
        method: 'GET',
        params: { scoreConfigId },
        loading: true
      }).then(res => {
        this.detailForm = res.data.souScoreConfig
        this.souScoreConfigDetailList = res.data.souScoreConfigDetailList
      })
    },
    saveScoreConfig (type) {
      const params = {
        type,
        souScoreConfig: { ...this.detailForm },
        souScoreConfigDetailList: this.souScoreConfigDetailList
      }
      this.$http({
        url: '/api-pj/sou/scoreConfig/saveScoreConfig',
        method: 'POST',
        data: params,
        loading: true
      }).then(res => {
        let scoreConfigId = res.data.souScoreConfig.scoreConfigId
        this.$message.success(res.message)
        type == 'SAVE' && this.getFormDetail(scoreConfigId)
        type == 'SUBMIT' && this.backBill()
        this.__setTabTodo('sourcingScoreConfigList.getQuerydata')
      })
    },
    submitScoreConfig (type) {
      this.$refs.detailForm.validate(valid => {
        if (valid) {
          if (this.souScoreConfigDetailList.length == 0) {
            this.$message.warning(this.$t('cusEntry.biddingSettings.messageTip3'))
            return
          }
          let flag = this.souScoreConfigDetailList.some(item => !item.scoreItem || !item.reviewItem || !item.scoreDesc)
          if (flag) {
            this.$message.warning(this.$t('cusEntry.biddingSettings.messageTip2'))
            return
          }
          // 校验【技术评审】的【最高分值】之和须为100
          let scoreTotal = 0
          let techFlag = false
          this.souScoreConfigDetailList.map(item => {
            if (item.scoreItem == 'TEH_REVIEW') {
              scoreTotal += item.maxScore
              techFlag = true
            }
          })
          if (techFlag && scoreTotal != 100) {
            this.$message.error(this.$t('cusEntry.biddingSettings.messageTip1'))
            return
          }
          this.saveScoreConfig(type)
        } else {
          return this.$message.warning(this.$t('common.pleasefinishRequired'))
        }
      })
    }
  }
}
</script>
<style lang="scss" scoped>
.the-sourcingScoreConfigDetail-detail{
 .form-container{
    margin: 16px;
  }
}
</style>
