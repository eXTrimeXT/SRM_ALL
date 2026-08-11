<template>
  <el-container
    class="the-biddingScoreDetail-detail"
    direction="vertical"
  >
    <el-main>
      <el-collapse v-model="activeNum">
        <el-collapse-item :title="$t('cusEntry.biddingSettings.techFile')" name="1">
          <el-table
            border
            max-height="300"
            style="width: 100%"
            :data="techFileList"
            highlight-current-row
          >
            <el-table-column
              align="center"
              type="index"
              fixed="left"
              :label="$t('common.sort')"
              width="55"
            />
            <el-table-column
              align="center"
              prop="vendorName"
              :label="$t('bidMod.vendorName')"
              min-width="120"
              show-overflow-tooltip
            />
            <el-table-column
              align="center"
              prop="fileType"
              :label="$t('dataConfMod.fileExportType')"
              :formatter="(row, column, cellValue) => $getDictLabel('SOU_FILE_CONFIG_TYPE', cellValue)"
              min-width="120"
              show-overflow-tooltip
            />
            <el-table-column
              align="center"
              prop="orderDocId"
              :label="$t('cusEntry.biddingSettings.techFile')"
              min-width="180"
              show-overflow-tooltip
            >
              <template slot-scope="scope">
                <SrmCommonFile
                  :default-file="{
                    fileId: scope.row.orderDocId,
                    fileName: scope.row.orderFileName
                  }"
                  readonly
                />
              </template>
            </el-table-column>
            <!-- <el-table-column
              v-if="mergeFlag"
              align="center"
              prop="extPackageName"
              :label="$t('cusEntry.biddingSettings.bagName')"
              min-width="120"
              show-overflow-tooltip
            /> -->
            <!-- 标段 -->
            <el-table-column
              v-if="techFileList.some(item => !!item.extBidSection)"
              align="center"
              prop="extBidSection"
              :label="$t('cusEntry.bidMod.extBidSection')"
              min-width="120"
              show-overflow-tooltip
            />
            <el-table-column
              align="center"
              prop="orderRemark"
              :label="$t('common.remark')"
              min-width="120"
              show-overflow-tooltip
            />
          </el-table>

          <div style="margin: 20px 0">
            <MImport
              up-load-url="/api-sou/ext/buyer/bid/init/importScoreExcel"
              :extra-data="extraData"
              :disabled="isReadOnly"
              @downloadTemplate="downloadTemplate"
              @handleSuccess="handleSuccess"
            />
            <!-- 智能评标 -->
            <!-- <el-button type="primary" @click="goInteBidEval">
              {{ $t('cusEntry.supplement20250205.intelligentBidEva') }}
            </el-button>
            <el-button type="primary" @click="refresh" v-if="!isReadOnly">{{ $t('common.refresh') }}</el-button> -->
          </div>

          <el-table
            border
            :data="scoreList"
            max-height="500"
            style="width:100%"
            :span-method="spanMethod"
          >
            <el-table-column
              align="center"
              type="index"
              fixed="left"
              :label="$t('common.sort')"
              width="60"
            />
            <el-table-column
              align="center"
              prop="scoreItem"
              :label="$t('cusEntry.biddingSettings.scoreItem')"
              :formatter="(row, column, cellValue) => $getDictLabel('SOU_SCORE_CONFIG_ITEM', cellValue)"
              min-width="150"
              show-overflow-tooltip
            />
            <el-table-column
              align="center"
              prop="reviewItem"
              :label="$t('cusEntry.biddingSettings.reviewItem')"
              min-width="150"
              show-overflow-tooltip
            />
            <el-table-column
              align="center"
              prop="maxScore"
              :label="$t('cusEntry.biddingSettings.maxScore')"
              min-width="100"
              show-overflow-tooltip
            />
            <el-table-column
              align="center"
              prop="scoreDesc"
              :label="$t('cusEntry.biddingSettings.scoreDesc')"
              min-width="150"
              show-overflow-tooltip
            />
            <el-table-column
              v-for="(item,index) in scoreList[0]?.vendorScoreList"
              :key="item.configDetailId"
              :label="item.vendorName"
              align="center"
              show-overflow-tooltip
            >
              <el-table-column
                align="center"
                prop="extDescription"
                :label="$t('cusEntry.bidMod.extDescription')"
                :render-header="_addStarToColumn"
                min-width="150"
                show-overflow-tooltip
              >
                <template slot-scope="scope">
                  <el-input
                    v-if="scope.row.scoreItem !== $t('perfMod.totalScore')"
                    v-model="scope.row.vendorScoreList[index].extDescription"
                    :disabled="isReadOnly"
                  />
                  <span v-else>{{ scope.row.vendorScoreList[index].extDescription }}</span>
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="score"
                :label="$t('cusEntry.bidMod.score')"
                :render-header="_addStarToColumn"
                min-width="100"
                show-overflow-tooltip
              >
                <template slot-scope="scope">
                  <el-input
                    v-if="scope.row.scoreItem === 'TEH_REVIEW'"
                    v-model="scope.row.vendorScoreList[index].score"
                    v-input-format="{
                      type: 'float',
                      negative: false,
                      digits: 2
                    }"
                    min="0"
                    :disabled="isReadOnly"
                  />
                  <span v-else>{{ scope.row.vendorScoreList[index].score }}</span>
                </template>
              </el-table-column>
            </el-table-column>
          </el-table>
        </el-collapse-item>
        <el-collapse-item :title="$t('contractMod.addUploadFile')" name="2">
          <el-button
            v-if="!isReadOnly"
            type="primary"
            style="margin-bottom: 10px;"
            @click="addFile"
          >
            {{ $t('common.add') }}
          </el-button>
          <el-table
            border
            :data="fileList"
            style="width: 100%"
            max-height="200"
          >
            <el-table-column
              align="center"
              type="index"
              :label="$t('common.sort')"
              width="60"
            />
            <el-table-column
              align="center"
              prop="fileName"
              :label="$t('bidMod.fileName')"
              min-width="150"
            >
              <template slot-scope="scope">
                <SrmCommonFile
                  :extra-data="fileInfo"
                  :default-file="{
                    fileId: scope.row.fileId,
                    fileName: scope.row.fileName
                  }"
                  :readonly="isReadOnly"
                  @on-change="({file}) => uploadSuccess(file,scope.row)"
                />
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              prop="createdFullName"
              :label="$t('quota.uploadBy')"
              min-width="100"
            />
            <el-table-column
              align="center"
              prop="creationDate"
              :label="$t('components.fileupload.uploadDate')"
              min-width="100"
            >
              <template slot-scope="scope">
                {{$parseTime(scope.row.creationDate)}}
              </template>
            </el-table-column>
            <el-table-column
              v-if="!isReadOnly"
              align="center"
              :label="$t('common.operation')"
              width="100"
            >
              <template slot-scope="scope">
                <el-button type="text" @click="deleteItem(scope.$index,scope.row)">
                  {{ $t('common.delete') }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-collapse-item>
      </el-collapse>

      <CToolbar>
        <template slot="right">
          <el-button @click="backBill">
            {{ !isReadOnly ? $t('common.cancel') : $t('common.close') }}
          </el-button>
          <el-button
            v-if="!isReadOnly"
            type="primary"
            @click="saveScoreItem('SAVE')"
          >
            {{ $t('common.save') }}
          </el-button>
          <el-button
            v-if="!isReadOnly"
            type="primary"
            @click="submitScoreItem('SUBMIT')"
          >
            {{ $t('common.submit') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
    <!-- 评标须知 -->
    <srm-dialog
      :visible.sync="showGuide"
      :title="$t('cusEntry.supplement20250205.bidEvaTip1')"
      size="large"
    >
      <div>
        <p>{{ $t('cusEntry.supplement20250205.bidEvaTip2') }}</p>
        <p>{{ $t('cusEntry.supplement20250205.bidEvaTip3') }}</p>
        <p>{{ $t('cusEntry.supplement20250205.bidEvaTip4') }}</p>
        <p>{{ $t('cusEntry.supplement20250205.bidEvaTip5') }}</p>
        <p>{{ $t('cusEntry.supplement20250205.bidEvaTip6') }}</p>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="showGuide = false">{{ $t('common.cancel') }}</el-button>
        <el-button type="primary" @click="nextStep">{{ $t('components.homepage.next') }}</el-button>
      </div>
    </srm-dialog>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import MImport from 'lib@/components/import'
import { downloadFileLinkByPost } from 'lib@/utils/file'
import InteBidEval from './inteBidEval'
import { getFileResult } from 'modcb@/biddingBuyer/api/analysis'

export default {
  name: 'BiddingScoreDetail',
  components: {
    MImport,
    CToolbar
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      mergeFlag: false,
      isReadOnly: false,
      projectId: null,
      groupId: null,
      techScoreHeadId: null,
      extProjectNo: null,
      souName: null,
      showGuide: false,
      activeNum: ['1', '2'],
      extraData: {
        uploadType: 'EXCEL',
        sourceType: 'XLSX',
        fileModular: 'BID',
        fileFunction: 'PRICE_TEMPLATE',
        fileType: 'XLSX'
      },
      techFileList: [],
      scoreList: [],
      fileInfo: {
        uploadType: 'DEF', // 固定
        sourceType: 'WEB_APP', // 固定
        fileModular: 'base', // 模块
        fileFunction: 'BID_EVALUATION', // 功能
        fileType: 'images' // 类型
      },
      fileList: [],
      supplySelectList: []
    }
  },
  created () {
    const { flag, row } = this.$attrs.params
    this.projectId = row.projectId
    this.groupId = row.groupId
    this.techScoreHeadId = row.techScoreHeadId
    this.extProjectNo = row.extProjectNo
    this.souName = row.souName
    this.isReadOnly = flag == 'view'
    this.getFormDetail()
  },
  methods: {
    // 刷新
    refresh () {
      this.getFormDetail()
    },
    // 评标须知
    goInteBidEval () {
      getFileResult({ projectId: this.projectId }).then(res => {
        if(res && res.code + '' === '0') {
          if (res.data) {
            // 弹出评标须知
            this.showGuide = true
          }else {
            // 文件正在解析中，请稍后...
            this.$message.warning(this.$t('cusEntry.supplement20250205.bidEvaTip7'))
          }
        }
      })
    },
    // 智能评标
    nextStep () {
      this.showGuide = false
      let tab = {
        component: InteBidEval,
        params: {
          projectId: this.projectId,
          groupId: this.groupId,
          techScoreHeadId: this.techScoreHeadId,
          extProjectNo: this.extProjectNo,
          souName: this.souName,
          supplySelectList: this.supplySelectList,
          isReadOnly: this.isReadOnly
        },
        title: `${$t('cusEntry.supplement20250205.intelligentBidEva')}-${this.extProjectNo}`,
        name: `${$t('cusEntry.supplement20250205.intelligentBidEva')}-${this.extProjectNo}`
      }
      this.$emit('tab-add', tab)
    },
    addFile () {
      this.fileList.push({
        fileId: null,
        fileName: null,
        createdFullName: null,
        creationDate: null
      })
    },
    uploadSuccess (file, row) {
      const { fileId = null, fileName, createdFullName, creationDate } = file || {}
      row.fileId = fileId
      row.fileName = fileName
      row.createdFullName = createdFullName
      row.creationDate = creationDate
    },
    // 删除
    deleteItem (index, row) {
      this.fileList.splice(index, 1)
    },
    backBill () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('biddingScoreList.getQueryData')
    },
    getFormDetail () {
      const params = {
        projectId: this.projectId,
        techScoreHeadId: this.techScoreHeadId,
        groupId: this.groupId,
        extendReview: 'N'
      }
      this.$http({
        url: '/api-sou/ext/buyer/bid/init/getExtScoreDetail',
        method: 'POST',
        data: params,
        loading: true
      }).then(res => {
        if (res && res.data) {
          const { techFileList = [], scoreRuleList = [], fileList = [] } = res.data
          this.techFileList = techFileList
          this.scoreList = this.setSumStruct(scoreRuleList)
          if (scoreRuleList.length) {
            this.supplySelectList = scoreRuleList[0].vendorScoreList
          }
          this.fileList = fileList
          // 合并招标标志：mergeFlag 为true时展示包名
          this.mergeFlag = res.data.mergeFlag
          this.extraData.businessId = res.data.projectId
          this.extraData.groupId = res.data.groupId
          this.extraData.techScoreHeadId = res.data.techScoreHeadId
          this.extraData.projectId = res.data.projectId
        }
      })
    },
    /* 构造合计数据结构 */
    setSumStruct (arr) {
      if (arr.length === 0) return arr
      const firstItem = arr[0]
      return [
        ...arr,
        {
          scoreItem: this.$t('perfMod.totalScore'),
          vendorScoreList: firstItem?.vendorScoreList.map(item => ({
            extDescription: item.totalScore,
            score: item.totalScore
          }))
        }
      ]
    },
    // 设置合并表格行列
    spanMethod ({ row, column, rowIndex, columnIndex }) {
      const length = this.scoreList.length
      const mergeRowIndexs = [length - 1]
      if (mergeRowIndexs.includes(rowIndex)) {
        if (column.property == 'scoreItem') {
          return [1, 4]
        } else if (['reviewItem', 'maxScore', 'scoreDesc'].includes(column.property)) {
          return [0, 0]
        } else if (column.property == 'extDescription') {
          return [1, 2]
        } else if (column.property == 'score') {
          return [0, 0]
        }
      }
    },
    saveScoreItem (type) { // tempSave 保存传true, 提交传false
      const params = {
        tempSave: type == 'SAVE',
        projectId: this.projectId,
        techScoreHeadId: this.techScoreHeadId,
        fileList: this.fileList,
        scoreRuleDtoList: this.scoreList.slice(0, -1) // 保存数据不包括数据最后一条总计
      }
      this.$http({
        url: '/api-sou/ext/buyer/bid/init/editScore',
        method: 'POST',
        data: params,
        loading: true
      }).then(res => {
        this.$message.success(res.message)
        this.getFormDetail()
        type == 'SUBMIT' && this.backBill()
      })
    },
    submitScoreItem (type) {
      let flag = false
      let errorMsg = ''
      let scoreList = this.scoreList.slice(0, -1) // 校验数据不包括数据最后一条总计
      /* 获取需要校验的目标数据 */
      const targetList = scoreList.reduce((pre, cur) => {
        /*将行的打分项冗余到每一校验项*/
        const vendorScoreList = cur.vendorScoreList?.map(item => ({
          reviewItem: cur.reviewItem,
          maxScore: cur.maxScore,
          scoreItem: cur.scoreItem,
          ...item 
        }))
        return pre.concat(vendorScoreList)
      }, [])
      /*非技术评分的评分项不校验*/
      targetList.some(item => {
        if (!item.extDescription || (!item.score && item.score !== 0 && item.scoreItem === 'TEH_REVIEW')) {
          flag = true
          // 请填写完所有带*必填项
          errorMsg = this.$t('cusEntry.supplement20250205.bidEvaTip8')
          return false
        }
      })
      /*获取供应商技术评分总和*/
      const vendorIdList = [...new Set(targetList.map(item => item.vendorId))]
      vendorIdList.some(item => {
        /* 获取供应技术评分数据 */
        const sumItem = targetList.filter(itm => itm.vendorId === item && itm.scoreItem === 'TEH_REVIEW')
        // 校验评分是否超过最高分
        sumItem.map(item => {
          if (item.score > item.maxScore) {
            flag = true
            //供应商技术评分项超过最高分值
            errorMsg = `${this.$t('common.vendor')}(${sumItem[0].vendorName})${item.reviewItem}${this.$t('cusEntry.supplement20250205.bidEvaTip9')}`
            return false
          }
        })
        /* 计算总分 */
        const totalScore = sumItem.reduce((pre, cur) => {
          /* 评分分数放大100倍，避免小数相加失真 */
          const itemScore = Number(cur.score) * 100 || 0
          return pre + itemScore
        }, 0)
        if (totalScore > 10000) {
          flag = true
          // 供应商(${sumItem[0].vendorName})技术评分总和超过100
          errorMsg = `${this.$t('common.vendor')}(${sumItem[0].vendorName})${this.$t('cusEntry.supplement20250205.bidEvaTip10')}`
          return true
        }
      })
      if (flag) {
        this.$message.warning(errorMsg)
        return
      }
      const bol = this.fileList.some(item => !item.fileId)
      if (bol) {
        // 请上传附件
        this.$message.warning(this.$t('cusEntry.supplement20250121.promptTips12'))
        return
      }
      this.saveScoreItem(type)
    },
    handleSuccess () {
      this.getFormDetail()
    },
    downloadTemplate () {
      downloadFileLinkByPost(
        '/api-sou/ext/buyer/bid/init/exportScoreExcelTemplate',
        this.$t('logisticsMod.importTemplateXLSX'),
        {
          groupId: this.groupId,
          techScoreHeadId: this.techScoreHeadId,
          projectId: this.projectId
        }
      ).catch(() => {
        this.$message.error(this.$t('perfMod.downLoadError')) // "下载失败"
      })
    }
  }
}
</script>
<style lang="scss" scoped>
.the-biddingScoreDetail-detail {
  padding-bottom: 22px;
}
</style>
