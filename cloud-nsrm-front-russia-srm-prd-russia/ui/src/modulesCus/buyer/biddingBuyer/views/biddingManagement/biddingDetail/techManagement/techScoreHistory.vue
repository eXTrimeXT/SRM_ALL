<template>
  <el-container direction="vertical">
    <el-main>
      <p>
        <span style="padding-right: 12px; font-size: 14px;">评标人</span>
        <el-select v-model="groupId" @change="groupIdChange('groupIdChange')">
          <el-option
            v-for="item in groupList"
            :key="item.groupId"
            :label="item.name"
            :value="item.groupId"
          />
        </el-select>
      </p>
      <el-tabs v-model="activeName" type="card" @tab-click="handleClick">
        <el-tab-pane
          v-for="item in submiteDateList"
          :key="item"
          :label="item"
          :name="item"
        >
          <el-table
            ref="scoreList"
            border
            :data="scoreList"
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
              min-width="150"
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
                min-width="150"
                show-overflow-tooltip
              >
                <template slot-scope="scope">
                  {{ scope.row.vendorScoreList[index].score }}
                </template>
              </el-table-column>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-main>
  </el-container>
</template>
<script>
/**
 * 查看评分历史
 */
export default {
  name: 'TechScoreHistory',

  data () {
    return {
      groupId: '',
      activeName: '',
      editRow: {},
      groupRoleList: [],
      groupList: [],
      submiteDate: '',
      submiteDateList: [],
      scoreMap: {}, // 所有评分人对应的评分表格
      scoreList: [] // 评分表格数据源
    }
  },

  created () {
    this.editRow = this.$attrs.params.row
    this.groupRoleList = this.$attrs.params.groupRoleList
    this.groupId = this.$attrs.params.row.groupId
    this.getDetail('groupIdChange')
  },

  methods: {
    groupIdChange (flag) {
      this.submiteDate = ''
      this.getDetail(flag)
    },
    getDetail (flag) {
      const params = {
        projectId: this.editRow.projectId,
        groupId: this.groupId,
        submiteDate: this.submiteDate
      }
      this.$http({
        url: '/api-sou/ext/buyer/bid/init/getExtScoreHistory',
        method: 'POST',
        data: params,
        loading: true
      }).then(res => {
        if (res && res.data) {
          const { groupList, scoreMap, submiteDateList } = res.data
          this.scoreMap = scoreMap
          this.submiteDateList = submiteDateList
          this.groupList = groupList.map(item => {
            let groupRoleName = this.$getDictLabelByValue(this.groupRoleList || [], item.groupRole) || ''
            return {
              groupId: item.groupId,
              name: groupRoleName + '：' + item.fullName
            }
          })
          this.activeName = flag == 'groupIdChange' ? this.submiteDateList[0] : this.activeName
          this.scoreList = this.setSumStruct(scoreMap[this.groupId] || [])
          this.$nextTick(() => {
            let index = this.submiteDateList.findIndex(it => it == this.activeName)
            if (index != -1) this.$refs.scoreList[index].doLayout()
          })
        }
      })
    },
    handleClick () {
      this.submiteDate = this.activeName
      this.getDetail()
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
        }
      ]
    },
    // 设置合并表格行列
    spanMethod ({ row, column, rowIndex, columnIndex }) {
      const length = this.scoreList.length
      const mergeRowIndexs = [length - 1]
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
