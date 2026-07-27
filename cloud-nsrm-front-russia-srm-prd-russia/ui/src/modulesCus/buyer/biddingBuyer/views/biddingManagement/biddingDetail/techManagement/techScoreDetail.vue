<template>
  <el-container direction="vertical">
    <el-main>
      <el-collapse v-model="activeNames">
        <el-collapse-item title="评分分析" name="1">
          <el-table
            v-if="mergeFlag"
            border
            :data="vendorList"
            style="width:100%; margin-bottom:30px;"
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
              prop="vendorName"
              :label="$t('common.vendorName')"
              min-width="150"
              show-overflow-tooltip
            />
            <el-table-column
              align="center"
              prop="extPackageName"
              :label="$t('cusEntry.biddingSettings.bagName')"
              min-width="150"
              show-overflow-tooltip
            />
          </el-table>
          <el-table
            :data="formDataList"
            border
            style="width:100%;"
            max-height="400"
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
              prop="fullName"
              label="人员"
              min-width="150"
              show-overflow-tooltip
            />
            <el-table-column
              v-for="(it, index) in formDataList[0]?.scoreAnalysisVendorDtoList"
              :key="it.vendorCode"
              :label="it.vendorName"
              align="center"
              min-width="150"
              show-overflow-tooltip
            >
              <el-table-column
                align="center"
                prop="totalScore "
                label="单个人评分"
                min-width="150"
                show-overflow-tooltip
              >
                <template slot-scope="scope">
                  {{ scope.row.scoreAnalysisVendorDtoList[index].totalScore }}
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="averageScore "
                label="平均评分"
                min-width="150"
                show-overflow-tooltip
              >
                <template slot-scope="scope">
                  {{ scope.row.scoreAnalysisVendorDtoList[index].averageScore }}
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="differenceRatio"
                label="差异率（%）"
                min-width="150"
                show-overflow-tooltip
              >
                <template slot-scope="scope">
                  {{ differenceRatioFormate(scope.row.scoreAnalysisVendorDtoList[index].differenceRatio) }}
                </template>
              </el-table-column>
            </el-table-column>
          </el-table>
        </el-collapse-item>
        <el-collapse-item title="评分详情" name="2">
          <el-button
            type="primary"
            style="margin-bottom: 16px"
            @click="techScoreHistory"
          >
            查看评标历史
          </el-button>
          <ExportDirect
            exprotUrl="/api-sou/ext/buyer/bid/init/exportScoreExcelTemplate"
            requstType="POST"
            :filterParams="exportParam"
            filename="技术评标结果_导出文件.xlsx"
          />
          <ExportDirect
            exprotUrl="/api-sou/ext/buyer/bid/init/exportScoreExcelBatch"
            requstType="POST"
            btnText="导出汇总"
            :filterParams="{
              projectId: editRow.projectId
            }"
            filename="技术评标结果导出汇总.xlsx"
          />
          <el-tabs v-model="activeName" type="card" @tab-click="handleClick">
            <el-tab-pane
              v-for="item in groupList"
              :key="item.groupId"
              :label="item.name"
              :name="item.groupId"
            >
              <el-table
                ref="scoreList"
                :data="scoreList"
                border
                style="width:100%"
                :span-method="spanMethod"
                max-height="400"
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
                  min-width="60"
                  show-overflow-tooltip
                />
                <el-table-column
                  v-for="(it, index) in scoreList[0]?.vendorScoreList"
                  :key="it.configDetailId"
                  align="center"
                  :label="it.vendorName"
                  min-width="150"
                  show-overflow-tooltip
                >
                  <el-table-column
                    align="center"
                    prop="extDescription"
                    :label="$t('cusEntry.bidMod.extDescription')"
                    min-width="150"
                    show-overflow-tooltip
                  >
                    <template slot-scope="scope">
                      {{ scope.row.vendorScoreList[index].extDescription }}
                    </template>
                  </el-table-column>
                  <el-table-column
                    align="center"
                    prop="score"
                    :label="$t('cusEntry.bidMod.score')"
                    min-width="60"
                    show-overflow-tooltip
                  >
                    <template slot-scope="scope">
                      {{ scope.row.vendorScoreList[index].score }}
                    </template>
                  </el-table-column>
                </el-table-column>
              </el-table>

              <!-- 评分附件 -->
              <el-table
                ref="fileList"
                :data="fileList"
                border
                style="marginTop: 30px; width: 100%"
                max-height="400"
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
                      :default-file="{
                        fileId: scope.row.fileId,
                        fileName: scope.row.fileName
                      }"
                      readonly
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
                />
              </el-table>
            </el-tab-pane>
          </el-tabs>
        </el-collapse-item>
      </el-collapse>
    </el-main>
  </el-container>
</template>
<script>
/**
 * 查看评分详情
 */
import { bidBuyerHttp } from 'modcb@/biddingBuyer/api'
import { adaptDictData } from '@/utils'
import { getDictItem } from '@/api/common'
import TechScoreHistory from './techScoreHistory'
import ExportDirect from 'lib@/components/export-direct'

export default {
  name: 'TechScoreDetail',
  components: { ExportDirect },

  data () {
    return {
      editRow: {},
      mergeFlag: false,
      activeName: null,
      projectId: null,
      exportParam: {},
      groupList: [],
      vendorList: [], // 上方供应商表格数据
      scoreMap: {}, // 所有评分人对应的评分表格
      scoreList: [], // 评分表格数据源
      scoreMapFile: {}, // 所有评分人对应的评分文件
      fileList: [], // 评分文件
      activeTab: 'scoreAnalysis',
      activeNames: ['1', '2'],
      formDataList: [] // 评分分析数据源
    }
  },

  async created () {
    this.editRow = this.$attrs.params.editRow
    await this.fatchDictData()
    this.getDetail()
  },

  methods: {
    // 差异率展示百分数
    differenceRatioFormate (val) {
      return parseFloat(Number(val * 100 || 0).toFixed(8))
    },
    // 获取数据字典
    fatchDictData () {
      // 评标角色
      getDictItem('SOU_GROUP_ROLE').then(res => {
        this.groupRoleList = adaptDictData(res.data, 'dict')
      })
    },
    techScoreHistory () {
      if (this.$attrs.params.editRow.from === 'fromFun') {
        const str = encodeURI(`from=fromFun&funName=bidTechScoreHistory&formId=${this.editRow.projectId}&row=${this.editRow}&groupRoleList=${this.groupRoleList}&extProjectNo=${this.editRow.extProjectNo}&souName=${this.editRow.souName}`)
        const encodeStr = btoa(str)
        const pathname = window.location.pathname
        const systemUrl = window.location.origin + pathname.substring(0, pathname.length - 1)
        window.open(`${systemUrl}/#/flowTaskViewBase/${encodeStr}`, '_blank')
      } else {
        let tab = {
          component: TechScoreHistory,
          params: {
            row: this.editRow,
            groupRoleList: this.groupRoleList,
            tabName: `TechScoreHistory${this.editRow.projectId}`
          },
          title: this.editRow.extProjectNo || this.editRow.souName + '-' + '评分历史',
          name: `TechScoreHistory${this.editRow.projectId}`
        }
        this.$emit('tab-add', tab)
      }
    },
    getDetail () {
      bidBuyerHttp.tech.getExtScore({ projectId: this.editRow.projectId }).then(res => {
        if (res && res.data) {
          const { vendorList = [], groupList = [], scoreAnalysisDynamicForm, scoreMap, scoreMapFile } = res.data
          const { formDataList = [] } = scoreAnalysisDynamicForm
          this.formDataList = formDataList
          this.vendorList = vendorList
          this.groupList = groupList.map(item => {
            let groupRoleName = this.$getDictLabelByValue(this.groupRoleList || [], item.groupRole) || ''
            let name = groupRoleName.slice(2)
            return {
              groupId: String(item.groupId),
              name: name + '-' + item.fullName
            }
          })
          this.mergeFlag = res.data.mergeFlag
          this.scoreMap = scoreMap
          this.scoreMapFile = scoreMapFile
          // 由于bpm跳转过来的groupId是undefined字符串导致不能正常显示activeName
          this.activeName = (this.editRow?.groupId && this.editRow?.groupId !== 'undefined') ? String(this.editRow.groupId) : String(this.groupList[0]?.groupId)
          this.exportParam = { groupId: this.activeName, projectId: this.editRow.projectId }
          this.fileList = this.scoreMapFile[this.activeName] || []
          this.scoreList = this.setSumStruct(scoreMap[this.activeName])
          this.$nextTick(() => {
            let index = this.groupList.findIndex(it => it.groupId == this.activeName)
            if (index != -1) this.$refs.scoreList[index].doLayout()
          })
        }
      })
    },
    handleClick () {
      this.exportParam = { groupId: this.activeName, projectId: this.editRow.projectId }
      this.fileList = this.scoreMapFile[this.activeName] || []
      this.scoreList = this.setSumStruct(this.scoreMap[this.activeName])
      this.$nextTick(() => {
        let index = this.groupList.findIndex(it => it.groupId == this.activeName)
        if (index != -1) this.$refs.scoreList[index].doLayout()
      })
    },
    /* 构造合计数据结构 */
    setSumStruct (arr = []) {
      if (arr.length === 0) return arr
      const firstItem = arr[0]
      return [
        ...arr,
        {
          scoreItem: '总得分',
          vendorScoreList: firstItem?.vendorScoreList.map(item => ({
            extDescription: item.totalScore,
            score: item.totalScore
          }))
        },
        {
          scoreItem: '平均得分',
          vendorScoreList: firstItem?.vendorScoreList.map(item => ({
            extDescription: item.averageScore,
            score: item.averageScore
          }))
        }
      ]
    },
    // 设置合并表格行列
    spanMethod ({ row, column, rowIndex, columnIndex }) {
      const length = this.scoreList.length
      const mergeRowIndexs = [length - 1, length - 2]
      if (mergeRowIndexs.includes(rowIndex)) {
        if (columnIndex === 1) {
          return [1, 3]
        } else if ([2, 3].includes(columnIndex)) {
          return [0, 0]
        }
        if (column.property == 'extDescription') {
          return [1, 2]
        } else if (column.property == 'score') {
          return [0, 0]
        }
      }
    }
  }
}
</script>
