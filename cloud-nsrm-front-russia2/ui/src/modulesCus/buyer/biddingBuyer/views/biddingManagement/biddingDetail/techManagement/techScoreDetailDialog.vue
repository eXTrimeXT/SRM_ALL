<template>
  <SrmDialog
    :title="$t('cusEntry.bidMod.viewTechScheme')"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    append-to-body
  >
    <el-table
      v-if="mergeFlag"
      border
      :data="vendorList"
      style="width:100%; margin-bottom:20px;"
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
      <!-- <el-table-column
        align="center"
        prop="extPackageName"
        :label="$t('cusEntry.biddingSettings.bagName')"
        min-width="150"
        show-overflow-tooltip
      /> -->
    </el-table>
    <el-tabs v-model="activeName" type="card" @tab-click="handleClick">
      <el-tab-pane
        v-for="item in groupList"
        :key="item.groupId"
        :label="item.name"
        :name="item.groupId"
      >
        <el-table
          border
          :data="scoreList"
          max-height="200"
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
    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t('common.close') }}
      </el-button>
    </div>
  </SrmDialog>
</template>

<script>
/**
 * 查看评分详情
 */
import { bidBuyerHttp } from 'modcb@/biddingBuyer/api'
import { adaptDictData } from '@/utils'
import { getDictItem } from '@/api/common'

export default {
  name: 'TechScoreDetailDialog',

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    editRow: {
      type: Object,
      required: true
    }
  },

  data () {
    return {
      mergeFlag: false,
      activeName: null,
      projectId: null,
      groupList: [],
      vendorList: [], // 上方供应商表格数据
      scoreMap: {}, // 所有评分人对应的评分表格
      scoreList: [] // 评分表格数据源
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

  async created () {
    await this.fatchDictData()
    this.getDetail()
  },

  methods: {
    // 获取数据字典
    fatchDictData () {
      // 评标角色
      getDictItem('SOU_GROUP_ROLE').then(res => {
        this.groupRoleList = adaptDictData(res.data, 'dict')
      })
    },
    getDetail () {
      bidBuyerHttp.tech.getExtScore({ projectId: this.editRow.projectId }).then(res => {
        if (res && res.data) {
          const { vendorList, groupList, scoreMap } = res.data
          this.vendorList = vendorList
          this.groupList = groupList.map(item => {
            let groupRoleName = this.$getDictLabelByValue(this.groupRoleList, item.groupRole)
            let name = groupRoleName.slice(2)
            return {
              groupId: String(item.groupId),
              name: name + '-' + item.fullName
            }
          })
          this.mergeFlag = res.data.mergeFlag
          this.scoreMap = scoreMap
          this.activeName = String(this.editRow?.groupId || this.groupList[0]?.groupId)
          this.scoreList = this.setSumStruct(scoreMap[this.activeName])
        }
      })
    },
    handleClick () {
      this.scoreList = this.setSumStruct(this.scoreMap[this.activeName])
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
        },
        {
          scoreItem: this.$t('cusEntry.supplement20250205.averageScore1'), // 平均得分
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
