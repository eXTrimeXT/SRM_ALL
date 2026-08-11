<template>
  <srm-dialog
    :title="$t('bidMod.technicalScore')"
    size="large"
    style="padding: 11px"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    append-to-body
  >
    <!--供应商基本信息-->
    <vendor-info :vendor-info-data="vendorInfoData">
      <srm-row>
        <srm-col :init-col="1">
          <!-- 评审意见 -->
          <div>{{ $t('bidMod.reviewOpinion') }}</div>
          <div style="margin-top: 5px">
            <el-input
              v-model="techComments"
              type="textarea"
              :disabled="isReadOnly"
            />
          </div>
        </srm-col>
        <el-col />
      </srm-row>
    </vendor-info>

    <!-- 供应商方案附件 -->
    <h3>{{ $t('bidMod.vendorPlanFile') }}</h3>
    <el-table
      :data="vendorFiles"
      style="width: 100%"
      border
      max-height="250px"
    >
      <el-table-column
        align="center"
        type="index"
        width="50"
      />

      <!--附件名称-->
      <SrmCommonFile
        type="table-column"
        :table-column-options="{
          label: $t('bidMod.attachmentName'),
          prop: 'docId',
          nameProp: 'fileName'
        }"
        readonly
      />

      <el-table-column
        align="center"
        prop="comments"
        :label="$t('bidMod.remark')"
        min-width="150"
        show-overflow-tooltip
      />
    </el-table>

    <!-- 评分区 -->
    <h3>{{ $t('bidMod.scoreArea') }}</h3>
    <el-table
      :data="scoreDetailList"
      style="width: 100%"
      border
      max-height="250px"
    >
      <el-table-column
        align="center"
        type="index"
        width="50"
      />
      <!--评分项-->
      <el-table-column
        align="center"
        prop="scoreItem"
        :label="$t('bidMod.scoreItem')"
        min-width="150"
        show-overflow-tooltip
      />
      <!--评分标准-->
      <el-table-column
        align="center"
        prop="scoreStandard"
        :label="$t('bidMod.scoreStandard')"
        min-width="150"
        show-overflow-tooltip
      />
      <!--权重（%）-->
      <el-table-column
        align="center"
        prop="scoreWeight"
        :label="$t('bidMod.scoreWeight')"
        min-width="100"
        show-overflow-tooltip
      />
      <!--评分-->
      <el-table-column
        align="center"
        prop="score"
        :label="$t('bidMod.score1')"
        min-width="150"
      >
        <template slot-scope="scope">
          <el-input
            v-model="scope.row.score"
            :disabled="isReadOnly"
          />
        </template>
      </el-table-column>
    </el-table>

    <div
      slot="footer"
      class="dialog-footer"
    >
      <!--取消-->
      <el-button @click="dialogVisible = false">
        {{ $t('common.cancel') }}
      </el-button>
      <!--提交-->
      <el-button
        v-if="isEdit && !isReadOnly"
        type="primary"
        @click="saveOrSubmit('SUBMIT')"
      >
        {{ $t('common.submit') }}
      </el-button>
      <!--暂存-->
      <el-button
        v-if="isEdit && !isReadOnly"
        type="primary"
        @click="saveOrSubmit('SAVE')"
      >
        {{ $t('common.staging') }}
      </el-button>
    </div>
  </srm-dialog>
</template>

<script>
/**
 * 技术评分评审弹窗
 */
import vendorInfo from '@/library/composition/origin/vendorInfo'

export default {
  name: 'ScoringDetailsDialog',
  components: { vendorInfo },
  props: {
    visible: {
      type: Boolean
    },
    editRow: {
      type: Object
    },
    bargainId: {
      type: [String, Number]
    },
    isReadOnly: {
      type: Boolean,
      default: false
    },
    isProxyScore: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      vendorInfoData: {
        address: '',
        companyName: '',
        companyCreationDate: '',
        overseasRelationName: '',
        registeredCapital: '',
        companyType: '',
        legalPerson: '',
        businessStartDate: '',
        businessEndDate: '',
        businessScope: ''
      },
      techComments: '',
      scoreDetailList: [],
      vendorFiles: []
    }
  },
  computed: {
    dialogVisible: {
      get: function () {
        return this.visible
      },
      set: function (val) {
        this.$emit('update:visible', val)
      }
    },
    isEdit () {
      // 未完成
      return (this.editRow || {}).scoreStatus === 'UNFINISHED'
    }
  },
  watch: {
    dialogVisible: {
      handler (newVal) {
        if (newVal) {
          this.getEvaluateList()
        }
      },
      immediate: true
    }
  },
  methods: {
    /* 查询评分数据 */
    getEvaluateList () {
      this.$http({
        url: '/api-brg/techProposal/queryTechProgressInfoDetails',
        method: 'GET',
        params: {
          bargainId: this.bargainId,
          vendorId: this.editRow.vendorId,
          groupId: this.editRow.groupId || ''
        },
        loading: true
      }).then(data => {
        if (data && data.data) {
          const responseData = data.data
          this.vendorInfoData = responseData.vendorInfo || {}
          this.vendorFiles = responseData.vendorFiles || []
          this.techComments = (responseData.techScoreHead || {}).techComments || ''
          this.scoreDetailList = responseData.scoreDetailList || []
        }
      })
    },

    /* 提交 / 保存 */
    async saveOrSubmit (type) {
      for (let item of this.scoreDetailList) {
        if (!item.score) {
          // 请输入评分
          this.$message.warning(this.$t('bidMod.msgEnterARating'))
          return
        }
        if (item.score > item.fullScore) {
          // 评分不能超过最大评分数值，最大评分数值为
          this.$message.warning(this.$t('bidMod.msgMaxScore') + `[${item.fullScore}]`)
          return
        }
      }
      let paramData = {
        bargainId: this.bargainId,
        vendorId: this.editRow.vendorId,
        isTempSave: type === 'SAVE',
        // 是否代理评分
        isProxyScore: this.isProxyScore,
        techComments: this.techComments,
        techScoreDetails: this.scoreDetailList.map(item => {
          return {
            ruleLineId: item.ruleLineId,
            score: item.score
          }
        })
      }
      if (this.isProxyScore) {
        // 代理报价，加上groupId
        paramData = {
          ...paramData,
          groupId: this.editRow.groupId
        }
      }
      const data = await this.$http({
        url: '/api-brg/techProposal/techScore',
        method: 'POST',
        data: paramData,
        loading: true
      })
      if (data) {
        this.$message.success(type === 'SAVE' ? this.$t('common.successSave') : this.$t('common.successSubmit'))
        if (type === 'SUBMIT') {
          this.$emit('scoringSubmitSuccess')
          this.dialogVisible = false
        }
      }
    }
  }
}
</script>
