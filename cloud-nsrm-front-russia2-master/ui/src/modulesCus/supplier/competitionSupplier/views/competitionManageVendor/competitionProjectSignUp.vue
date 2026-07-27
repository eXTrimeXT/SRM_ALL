<template>
  <el-container class="flex-container sign-up-wrap" direction="vertical">
    <el-main>
      <div class="sign-up-progress">
        <el-steps :active="1">
          <el-step :title="$t('bidMod.published')" />
          <el-step :title="$t('bidMod.signingUp')" />
          <el-step :title="$t('bidMod.registered')" />
          <el-step :title="$t('bidMod.eligibilityConfirm')" />
          <el-step :title="$t('bidMod.bidding')" />
          <el-step :title="$t('bidMod.finishBid')" />
        </el-steps>
      </div>

      <!--距离报名截止还剩余: -->
      <div class="sign-up-deadline">
        <DynamicCutoffTime :label="$t('bidMod.registerDeadline')" :deadline-time="paramsRow.signUpEndTime" />
      </div>
      <el-collapse v-model="activeNames" class="tab-form-style">
        <el-collapse-item name="1" :title="$t('cusEntry.competition.signUpFileUplaod')">
          <div class="vendor-file-table-wrap">
            <el-button
              type="primary"
              style="margin-bottom:10px;"
              @click="addSignUpFile"
            >
              {{ $t('common.add') }}
            </el-button>
            <el-table
              ref="vendorSignUpFilesTable"
              :data="vendorSignUpFiles"
              style="width: 100%"
              border
            >
              <el-table-column
                align="center"
                type="index"
                width="50"
              />
              <!--附件名称-->
              <SrmCommonFile
                type="table-column"
                :extra-data="fileInfo"
                :table-column-options="{
                  label: $t('cusEntry.competition.signFile'),
                  prop: 'signUpDocId',
                  nameProp: 'signUpFileName',
                  renderHeader: _addStarToColumn
                }"
                @on-change="vendorSignUpFileChange"
              />

              <!--备注-->
              <el-table-column
                align="center"
                prop="signUpRemark"
                :label="$t('common.remark')"
                min-width="130"
                show-overflow-tooltip
              >
                <template slot-scope="scope">
                  <el-input v-model="scope.row.signUpRemark" />
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                :label="$t('common.operation')"
                width="60"
                fixed="right"
              >
                <template slot-scope="scope">
                  <el-button type="text" @click="deleteSignUpFile(scope.$index)">
                    {{ $t('common.delete') }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-collapse-item>
        <el-collapse-item name="2" :title="$t('cusEntry.competition.bondPayment')">
          <div class="bond-info-wrap">
            <srm-row>
              <!--保证金金额（万元）-->
              <srm-col :init-col="3" class="info-col">
                <span>{{ $t('bidMod.bondAmount') }}: </span>{{ bondPayInfoData.bondAmount }}
              </srm-col>
              <!--保证金提交方式-->
              <srm-col :init-col="3" class="info-col">
                <span>{{ $t('bidMod.bondMethod') }}: </span>{{ $getDictLabel('BID_BOND_SUBMISSION', bondPayInfoData.bondMethod) }}
              </srm-col>
              <!--保证金缴纳账号-->
              <srm-col :init-col="3" class="info-col">
                <span>{{ $t('bidMod.bankAccountNum') }}: </span>{{ bondPayInfoData.bankAccountNum }}
              </srm-col>
              <!--账户名称-->
              <srm-col :init-col="3" class="info-col">
                <span>{{ $t('vendorMod.bankAccountName') }}: </span>{{ bondPayInfoData.bankAccountName }}
              </srm-col>
              <!--开户支行-->
              <srm-col :init-col="2" class="info-col">
                <span>{{ $t('bidMod.bankBranchName') }}: </span>{{ bondPayInfoData.bankBranchName }}
              </srm-col>
            </srm-row>
          </div>

          <el-form
            ref="bondPayForm"
            :model="bondPayFormData"
            :rules="bondPayFormRules"
          >
            <srm-row :gutter="50">
              <srm-col :init-col="3">
                <!-- 缴纳时间 -->
                <el-form-item
                  :label="$t('bidMod.common.payDateOrigin')"
                  prop="depositPayTime"
                  style="margin-bottom: 20px"
                >
                  <el-date-picker
                    v-model="bondPayFormData.depositPayTime"
                    type="datetime"
                    :format="$formatDatePickerTime"
                    value-format="yyyy-MM-dd HH:mm:ss"
                  />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="3">
                <!--上传附件-->
                <SrmCommonFile
                  type="form-item"
                  :default-file="{ fileId: bondPayFormData.souDocId, fileName: bondPayFormData.souFileName }"
                  :form-item-options="{
                    label: $t('bidMod.common.payCert'),
                    prop: 'souDocId',
                    nameProp: 'souFileName'
                  }"
                  @on-change="bondPayFilesChange"
                />
              </srm-col>
            </srm-row>
          </el-form>
        </el-collapse-item>
      </el-collapse>
      <CToolbar>
        <template slot="right">
          <!--保存-->
          <!-- <el-button
            v-if="vendorSignUpFiles.length"
            type="primary"
            @click="saveOrSubmit('SAVE')"
          >
            {{ $t('common.save') }}
          </el-button> -->

          <!--提交-->
          <el-button type="primary" @click="saveOrSubmit('SUBMIT')">
            {{ $t('common.submit') }}
          </el-button>

          <!--返回-->
          <el-button @click="backTab">
            {{ $t('common.backTo') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>

<script>
/**
 * 报名
 */
import { carVendorHttp } from 'modcs@/competitionSupplier/api'
import { tabTodoMixin } from '@/utils/mixins'
import { validateRequiredColumn } from 'lib@/mixins/addStarToColumn'
import MainHeader from 'lib@/components/Table/MainHeader'
import DynamicCutoffTime from 'lib@/components/dynamic-cutoff-time'
import { transformMQL } from 'lib@/utils/util'
import CToolbar from 'lib@/components/c-toolbar'
export default {
  name: 'CompetitionProjectSignUp',

  components: {
    MainHeader,
    DynamicCutoffTime,
    CToolbar
  },

  mixins: [tabTodoMixin],

  data () {
    return {
      activeNames: ['1', '2'],
      vendorSignUpFiles: [],
      fileInfo: {
        fileModular: 'comp',
        fileFunction: 'competitionProjectSignUp',
        fileType: 'images'
      },
      paramsRow: {
        projectId: this.$attrs.params.row.projectId,
        signUpEndTime: this.$attrs.params.row.signUpEndTime || ''
      },
      bondPayInfoData: {},
      bondPayFormData: {
        souDocId: null,
        souFileName: '',
        depositPayTime: ''
      },
      bondPayFormRules: {
        depositPayTime: [
          { required: true, message: this.$t('bidMod.common.payDateMsg1') },  // '请选择缴纳时间'
          {
            validator: (rule, value, callback) => {
              if (value) {
                const startDate = new Date()
                const endDate = new Date(value)
                if (startDate.getTime() < endDate.getTime()) {
                  callback(new Error(this.$t('bidMod.common.payDateMsg2')))  // '缴纳时间需要小于当前时间'
                }
              }
              callback()
            },
            trigger: ['change', 'blur']
          }
        ],
        souDocId: [{ required: true, message: '请上传缴纳证明' }]
      }
    }
  },

  created () {
    this.getSignUpInfo()
  },

  methods: {
    /* 删除报名附件行 */
    deleteSignUpFile (index) {
      this.vendorSignUpFiles.splice(index, 1)
    },
    /* 报名附件新增 */
    addSignUpFile () {
      this.vendorSignUpFiles.unshift({
        signUpDocId: null,
        signUpDocName: ''
      })
    },
    /* 文件上传改变 */
    bondPayFilesChange ({ file }) {
      if (file) {
        this.bondPayFormData.souDocId = file.fileId.toString()
        this.bondPayFormData.souFileName = file.fileName
      } else {
        this.bondPayFormData.souDocId = ''
        this.bondPayFormData.souFileName = ''
      }
    },
    /* 查询报名详情 */
    async getSignUpInfo () {
      // let transformParams = transformMQL.save('AuctSouProjectForVendor', [{ projectId: this.paramsRow.projectId }], 'getVendorSignUpDetail')
      const response = await carVendorHttp.signUp.getSignUpInfo(this.paramsRow.projectId)
      if (response?.data) {
        const { signUpFileList, compSouProject, bondFileList, depositPayTime } = response.data
        if (signUpFileList && Array.isArray(signUpFileList)) {
          this.vendorSignUpFiles = signUpFileList || []
        }
        this.bondPayInfoData = compSouProject || {}
        /* 获取保证金附件 */
        const bondFile = bondFileList[0] || {}
        const {
          souDocId,
          souFileName
        } = bondFile
        this.bondPayFormData.souDocId = souDocId
        this.bondPayFormData.souFileName = souFileName
        this.bondPayFormData.depositPayTime = depositPayTime
      }
    },

    /* 文件变更 */
    vendorSignUpFileChange ({ file, $index }) {
      const { fileId = '', fileName = '' } = file || {}
      this.vendorSignUpFiles[$index].signUpDocId = fileId
      this.vendorSignUpFiles[$index].signUpFileName = fileName
    },

    /* 提交保存 */
    saveOrSubmit (type) {
      if (!validateRequiredColumn(
        this.$refs.vendorSignUpFilesTable,
        this.vendorSignUpFiles,
        {
          validateScope: false,
          tableTitle: this.$t('bidMod.signupFile')  // '报名附件'
        }
      )) {
        return
      }
      /* 保证金缴纳时间和证明校验 */
      this.$refs.bondPayForm.validate(async valid => {
        if (valid) {
          let params = {
            projectId: this.paramsRow.projectId,
            signUpFileList: this.vendorSignUpFiles,
            depositPayTime: this.bondPayFormData.depositPayTime,
            bondFileList: [
              {
                projectId: this.paramsRow.projectId,
                souDocId: this.bondPayFormData.souDocId,
                souFileName: this.bondPayFormData.souFileName,
                fileType: 'BOND'
              }
            ],
            isTempSave: type === 'SAVE'
          }
          // let transformParams = transformMQL.save('AuctSouProjectForVendor', [params], 'vendorSignUp')
          const response = await carVendorHttp.signUp.vendorSignUp(params)
          if (response) {
            this.$message.success(this.$t('common.successSubmit'))

            if (type === 'SUBMIT') {
              this.backTab('refresh')
            } else {
              await this.getSignUpInfo()
            }
          }
        } else {
          this.$message.warning(this.$t('cusEntry.tipMessage.required'))
          return false
        }
      })
    },

    /* 返回标签页 */
    backTab (type) {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      if (type === 'refresh') {
        // 刷新列表
        this.__setTabTodo('CompetitionProjectList.getQueryData')
      }
    }
  }
}
</script>

<style scoped lang="scss">
:deep(.sign-up-wrap) {
  .sign-up-progress {
    padding: 11px;
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
.bond-info-wrap {
  .el-row {
    margin-bottom: 11px;
    .info-col {
      font-size: 12px;
      position: relative;
      line-height: 30px;
      min-height: 30px;
      text-overflow: ellipsis;
      white-space: nowrap;
      overflow: hidden;
    }
  }
}
</style>
