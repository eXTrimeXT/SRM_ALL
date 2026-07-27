<template>
  <SrmDialog
    :title="`投标公司标书查重结果——${extProjectNo}——${souName}`"
    size="fullscreen"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    class="resultDialog"
  >
    <div class="content">
      <div v-if="resultList.length">
        <div v-for="result in resultList" :key="result.taskId" class="result-box">
          <div class="text-num">
            <span>识别字数：{{ result.compareWordSize }}个字</span>
            <span>识别时间：{{ result.createTime }}</span>
          </div>
          <div id="myResult" ref="myResult" class="result">
            <el-table
              :data="result.tableData"
              border
              :cell-style="setCellClass"
              style="width:100%;"
              :height="result.heightFlag ? '293' : ''"
              @cell-click="handleCellClick"
            >
              <el-table-column
                v-for="(column, index) in result.tableColumns"
                :key="index"
                :prop="column.prop"
                :label="column.label"
                :fixed="column.prop === 'ycompanyName'"
                header-align="center"
                align="center"
                width="290"
                show-overflow-tooltip
              >
                <template slot-scope="scope">
                  {{ scope.row[column.prop] || '——' }}
                </template>
              </el-table-column>
            </el-table>
          </div>
          <div class="conclusion">
            <p style="margin-bottom:4px;">
              识别结论
            </p>
            <p>{{ result.conclusion }}</p>
          </div>
        </div>
      </div>
      <div v-else class="no-data">
        <img src="../../../../../../../assets/images/no_data.png" alt="">
        <p>结果正在识别中，请耐心等待......</p>
      </div>
    </div>
  </SrmDialog>
</template>
<script>
import Vue from 'vue'
import { getOrderFileCheckDetail } from 'modcb@/biddingBuyer/api/analysis'
export default {
  name: 'ResultDialog',
  props: {
    projectId: {
      type: [String, Number],
      default: ''
    },
    visible: {
      type: Boolean,
      default: false
    },
    textNum: {
      type: [String, Number],
      default: ''
    },
    souName: {
      type: String,
      default: ''
    },
    extProjectNo: {
      type: [String, Number],
      default: ''
    }
  },
  data () {
    return {
      tableData: [],
      tableColumns: [
        {
          prop: 'ycompanyName',
          label: '公司名称-文件名称'
        }
      ],
      resultList: []
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
  watch: {
    visible: {
      handler (nVal) {
        if (nVal) {
          this.getFileCheckDetail()
          this.tableColumns = [
            {
              prop: 'ycompanyName',
              label: '公司名称-文件名称'
            }
          ]
        }
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    uniqueByValue (array, key) {
      return array.filter((item, index) => {
        const value = item[key]
        return array.findIndex(i => i[key] === value) === index
      })
    },
    // 根据属性分组
    groupByKey (array, key1, key2) {
      let dataArr = []
      array.map((mapItem) => {
        let index = dataArr.findIndex(item => item[key1] == mapItem[key1])
        if (index === -1) {
          dataArr.push({
            [key1]: mapItem[key1],
            [key2]: mapItem[key2],
            list: [mapItem]
          })
        } else {
          dataArr[index].list.push(mapItem)
        }
      })
      return dataArr
    },
    // 处理表格数据
    processData () {
      this.resultList.forEach(item => {
        item.dynamicHeaders = {}
        item.subTaskList.forEach(attr => {
          // if (!item.dynamicHeaders[attr.xcompanyName]) {
          //   item.dynamicHeaders[attr.xcompanyName] = { prop: `similarityRate_${attr.xcompanyId}_${item.compareWordSize}`, label: `${attr.xcompanyName}-${attr.xfileName}`, id: `${attr.xcompanyId}` }
          // }
          if (!item.dynamicHeaders[attr.xfileId]) {
            item.dynamicHeaders[attr.xfileId] = { prop: `similarityRate_${attr.xfileId}_${item.compareWordSize}`, label: `${attr.xcompanyName}-${attr.xfileName}`, id: `${attr.xcompanyId}`, xfileId: `${attr.xfileId}` }
          }
        })
        item.tableColumns = [
          ...this.tableColumns,
          ...Object.values(item.dynamicHeaders)
        ]
        item.resultData = item.subTaskList.map(subItem => {
          const data = {
            ycompanyName: subItem.ycompanyName + '-' + subItem.yfileName,
            yfileId: subItem.yfileId,
            xfileId: subItem.xfileId,
            similarityRate: subItem.similarityRate
          }
          return data
        })
        item.tableData = this.groupByKey(item.resultData, 'yfileId', 'ycompanyName').map(resultItem => {
          const data = {
            ycompanyName: resultItem.ycompanyName
          }
          resultItem.list.forEach(rate => {
            data[`similarityRate_${rate.xfileId}_${item.compareWordSize}`] = rate.similarityRate
          })
          return data
        })
        item.tableData.forEach(row => {
          if (item.tableData.length > 5) {
            item.heightFlag = true
          } else {
            item.heightFlag = false
          }
        })
      })
    },
    // 设置单元格样式
    setCellClass ({ row, column, rowIndex, columnIndex }) {
      if (row[column.property]) {
        if (Number(row[column.property].split('%')[0]) >= 40) {
          return 'color:#FF1818;cursor: pointer'
        } else if (Number(row[column.property].split('%')[0]) >= 20 && Number(row[column.property].split('%')[0]) < 40) {
          return 'color:#FD8201;cursor: pointer'
        } else {
          if (column.property !== 'ycompanyName') {
            return 'cursor: pointer'
          } else {
            return ''
          }
        }
      }
    },
    // 单元格点击事件
    handleCellClick (row, column, cell, event) {
      const prop = column.property.split('_')[1]
      const size = column.property.split('_')[2]
      const yNameArr = row['ycompanyName'].split('-')
      yNameArr.shift()
      const yName = yNameArr.join('-')
      const list = this.resultList.filter(item => item.compareWordSize == size)
      let obj = {}
      list[0].subTaskList.forEach(item => {
        if (item.xfileId === prop && item.yfileName === yName) {
          obj = item
        }
      })
      // 调取大脑对比详情页面
      const param = {
        embedClient: 'SRMclient',
        ...obj,
        projectId: this.projectId
      }
      if (Vue.prototype.embedHelper) {
        this.$store.commit('user/SET_FRAME', true)
        Vue.prototype.embedHelper.call('pdfData', JSON.stringify(param))
      } else {
        this.$handleFrameClick('detail', param)
      }
    },
    // 获取识别结果
    async getFileCheckDetail () {
      const params = {
        type: 'OrderFileCheck',
        action: 'detail',
        payload: {
          projectId: this.projectId
        },
        lang: 'zh-cn',
        query: {
          '*': {}
        },
        tree: true
      }
      const res = await getOrderFileCheckDetail(params)
      if (res && res.data) {
        this.resultList = res.data.records[0].taskList
        if (this.resultList) {
          this.processData()
        }
      }
    }
  }
}
</script>
<style lang="scss">
  .resultDialog .srm-dialog-content {
    max-height: 100% !important;
  }
</style>
<style lang="scss" scoped>
  .red {
    color: #FF1818;
  }
  .yellow {
    color: #FD8201;
  }
  .blue {
    color: #188FFF;
  }
  .result-box {
    background: linear-gradient( 180deg, #FFFFFF 0%, rgba(255,255,255,0.75) 100%);
    box-shadow: 0px 0px 20px 0px rgba(143,115,156,0.13);
    border-radius: 16px;
    padding: 20px;
    margin-bottom: 20px;
    .conclusion {
      background: #F6F6F6;
      border-radius: 10px;
      padding: 10px;
      font-size: 12px;
      color: #1D2129;
      p {
        margin: 0;
        padding: 0;
        line-height: 18px;
      }
    }
    .result {
      margin: 18px 0;
    }
    .text-num {
      color: #7E8AA2;
      span {
        margin-right: 10px;
      }
    }
  }
  .no-data{
    img{
      width:180px;
      height: 162px;
      display: block;
      margin: 180px auto 0;
    }
    p {
      text-align: center;
      margin-top: 30px;
      color: #7E8AA2;
    }
  }
  :deep(.el-table th) {
    height: 48px;
    max-height: 48px;
  }
  :deep(.el-table td) {
    height: 48px;
  }
  :deep(.el-table .custom-column){
    width:300px;
  }
  :deep(.el-table .my-cell) {
    width: 100%;
    height: 48px;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2; /* 控制显示行数 */
    -webkit-box-orient: vertical;
    line-height: 24px;
  }
</style>
