<template>
  <srm-dialog
    top="10vh"
    :title="$t('bidMod.questionList')"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    destroy-on-close
  >
    <div class="the_clarification_dialog">
      <el-form ref="form" :model="form" label-position="top" label-width="140px">
        <srm-row :gutter="16">
          <srm-col>
            <!-- 质疑编号 -->
            <el-form-item :label="$t('bidMod.questionNum')">
              <el-input v-model="form.questionNum" disabled />
            </el-form-item>
          </srm-col>

          <srm-col>
            <!-- 质疑状态 -->
            <el-form-item :label="$t('bidMod.questionStatus')">
              <dict-select v-model="form.questionStatus" code="QUERY_STATUS" disabled />
            </el-form-item>
          </srm-col>

          <!-- 项目名称 -->
          <srm-col>
            <el-form-item prop="bidingNum" :label="$t('bidMod.bidingName')">
              <el-input v-model="form.bidingName" disabled />
            </el-form-item>
          </srm-col>

          <!-- 项目编号 -->
          <srm-col>
            <el-form-item :label="$t('bidMod.bidingNum')">
              <el-input v-model="form.bidingNum" disabled />
            </el-form-item>
          </srm-col>
        </srm-row>

        <!-- 质疑信息 -->
        <h3>{{ $t("bidMod.questionInfo") }}</h3>
        <srm-row>
          <srm-col>
            <!-- 质疑标题 -->
            <el-form-item :label="$t('bidMod.questionTitle')" prop="questionTitle">
              <el-input v-model="form.questionTitle" disabled />
            </el-form-item>
          </srm-col>
        </srm-row>
      </el-form>

      <!-- 附件 -->
      <h3>{{ $t("bidMod.attachment") }}</h3>

      <el-table :data="fileList" style="width: 100%" border height="121px">
        <el-table-column align="center" type="index" width="50" />

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

        <!-- 备注 -->
        <el-table-column align="center" prop="remark" :label="$t('bidMod.remark')">
          <template slot-scope="scope">
            <el-input v-model="scope.row.remark" disabled />
          </template>
        </el-table-column>
      </el-table>

      <!-- 质疑内容 -->
      <h3>{{ $t("bidMod.questionContent") }}</h3>
      <tinymce
        id="biddingBuyerChallengeTinymce"
        v-model="mainClarification"
        :height="260"
        @setup="ready"
      />
    </div>

    <div slot="footer">
      <el-button @click="cancel">
        {{ $t('common.cancel') }}
      </el-button>
    </div>
  </srm-dialog>
</template>

<script>
/**
 * 查看质疑弹窗
 */
import Tinymce from '@/components/Tinymce'

export default {
  name: 'ChallengeDetailDialog',
  components: { Tinymce },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    editRow: {
      type: Object
    }
  },
  data () {
    return {
      form: {
        expertName: '',
        expertType: '',
        expertID: '',
        telephone: '',
        email: '',
        status: '',
        enableDate: '',
        disableDate: ''
      },
      fileList: [],
      mainClarification: ''
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
    }
  },
  created () {
    this.getBidingQuestionById()
  },
  methods: {
    ready (editorInstance) {
      // 禁用
      if (this.isReadOnly) {
        editorInstance.setMode('readonly')
      }
    },
    /* 查询详情数据 */
    getBidingQuestionById () {
      if (!this.editRow) return

      this.$http({
        url: '/api-bid/bidingQuestion/getBidingQuestionById',
        method: 'GET',
        params: { questionId: this.editRow.questionId },
        loading: true
      }).then(res => {
        const { files, questionComtent, ...rest } = res.data
        this.form = { ...rest }
        this.fileList = files
        this.mainClarification = questionComtent
      })
    },

    /* 取消 */
    cancel () {
      this.dialogVisible = false
      this.mainClarification = null
    }
  }
}
</script>
