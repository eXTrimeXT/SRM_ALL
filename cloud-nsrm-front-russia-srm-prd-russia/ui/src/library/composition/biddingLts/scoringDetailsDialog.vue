<template>
  <SrmDialog
    :title="$t('bidMod.technicalScore')"
    size="large"
    style="padding: 11px"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    append-to-body
  >
    <!--供应商基本信息-->
    <VendorInfo :vendor-info-data="vendorInfoData">
      <SrmRow>
        <SrmCol :init-col="1">
          <!-- 评审意见 -->
          <div>{{ $t('bidMod.reviewOpinion') }}</div>
          <div style="margin-top: 5px">
            <el-input
              v-model="techComments"
              type="textarea"
              :disabled="readonly"
            />
          </div>
        </SrmCol>
        <el-col />
      </SrmRow>
    </VendorInfo>

    <!-- 供应商方案附件 -->
    <h3>{{ $t('bidMod.vendorPlanFile') }}</h3>
    <el-table
      :data="techFileList"
      style="width: 100%"
      border
      max-height="250px"
    >
      <el-table-column
        type="index"
        :label="$t('common.sort')"
        width="50"
      />

      <!--附件名称-->
      <SrmCommonFile
        type="table-column"
        :table-column-options="{
          label: $t('bidMod.attachmentName'),
          prop: 'orderDocId',
          nameProp: 'orderFileName'
        }"
        readonly
      />

      <el-table-column
        prop="orderRemark"
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
        type="index"
        width="50"
      />

      <!--评分项-->
      <el-table-column
        prop="scoreItem"
        :label="$t('bidMod.scoreItem')"
        min-width="150"
        show-overflow-tooltip
      />

      <!--评分标准-->
      <el-table-column
        prop="scoreStandard"
        :label="$t('bidMod.scoreStandard')"
        min-width="150"
        show-overflow-tooltip
      />

      <!--权重（%）-->
      <el-table-column
        prop="scoreWeight"
        :label="$t('bidMod.scoreWeight')"
        min-width="100"
        show-overflow-tooltip
      />

      <!--评分-->
      <el-table-column
        prop="score"
        :label="$t('bidMod.score1')"
        min-width="150"
      >
        <template slot-scope="scope">
          <el-input v-model="scope.row.score" :disabled="readonly" />
        </template>
      </el-table-column>
    </el-table>

    <div slot="footer" class="dialog-footer">
      <!--取消-->
      <el-button @click="dialogVisible = false">
        {{ $t('common.cancel') }}
      </el-button>
      <!--提交-->
      <el-button
        v-if="isEdit && !readonly"
        type="primary"
        @click="saveOrSubmit('SUBMIT')"
      >
        {{ $t('common.submit') }}
      </el-button>
      <!--暂存-->
      <el-button
        v-if="isEdit && !readonly"
        type="primary"
        @click="saveOrSubmit('SAVE')"
      >
        {{ $t('common.staging') }}
      </el-button>
    </div>
  </SrmDialog>
</template>

<script>
/**
 * 技术评分评审弹窗
 */
import { brgBuyerHttp } from 'modb@/bargain/api'
import { indexWarningMessage } from 'lib@/composition/origin/composition'
import VendorInfo from 'lib@/composition/origin/vendorInfo'

export default {
  name: 'ScoringDetailsDialog',

  components: { VendorInfo },

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    editRow: {
      type: Object,
      required: true
    },
    projectId: {
      type: [String, Number],
      required: true
    },
    readonly: {
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
      techFileList: []
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

  mounted () {
    this.getEvaluateList()
  },

  methods: {
    /* 查询评分数据 */
    async getEvaluateList () {
      const response = await brgBuyerHttp.tech.techProgressInfoDetails({
        projectId: this.projectId,
        vendorId: this.editRow.vendorId,
        groupId: this.editRow.groupId || ''
      })
      if (response && response.data) {
        const {
          techFileList = [],
          techScoreHead = {},
          scoreDetailList = [],
          companyInfo = {}
        } = response.data
        this.vendorInfoData = companyInfo
        this.techFileList = techFileList
        this.techComments = (techScoreHead || {}).techComments || ''
        this.scoreDetailList = scoreDetailList
      }
    },

    /* 提交 / 保存 */
    async saveOrSubmit (type) {
      for (let [index, item] of this.scoreDetailList.entries()) {
        if (!item.score) {
          // 请输入评分
          indexWarningMessage(index, this.$t('bidMod.msgEnterARating'))
          return
        }
        if (item.score > item.fullScore) {
          // 评分不能超过最大评分数值，最大评分数值为
          indexWarningMessage(index, this.$t('bidMod.msgMaxScore') + `[${item.fullScore}]`)
          return
        }
      }

      let paramData = {
        projectId: this.projectId,
        vendorId: this.editRow.vendorId,
        isTempSave: type === 'SAVE',
        // 是否代理评分
        isProxyScore: this.isProxyScore,
        techComments: this.techComments,
        techScoreDetails: this.scoreDetailList.map(item => {
          return {
            techScoreLineId: item.techScoreLineId,
            scoreRuleLineId: item.scoreRuleLineId,
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

      const response = await brgBuyerHttp.tech.techScore(paramData)
      if (response) {
        this.$message.success(type === 'SAVE' ? this.$t('common.successSave') : this.$t('common.successSubmit'))
        if (type === 'SUBMIT') {
          this.$emit('success')
          this.dialogVisible = false
        }
      }
    }
  }
}
</script>
