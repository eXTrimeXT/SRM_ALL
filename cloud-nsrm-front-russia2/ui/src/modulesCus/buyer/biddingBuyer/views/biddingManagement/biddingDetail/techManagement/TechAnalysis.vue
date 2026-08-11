<template>
  <div class="analysis-box">
    <div class="btn-box">
      <div class="text-num">
        <!-- 字数识别选择 -->
        <span>{{ $t('cusEntry.supplement20250205.recognized1') }}</span>
        <el-select v-model="textNum" placeholder="">
          <!-- ${item.dictItemCode}个字 -->
          <el-option
            v-for="item in textNumList"
            :key="item.dictItemCode"
            :label="$t('cusEntry.supplement20250205.recognized2', {num: item.dictItemCode})"
            :value="item.dictItemCode"
          />
        </el-select>
      </div>
      <el-button type="primary" :disabled="isResultFlag" @click="handleResult">
        <!-- 查看识别结果 -->
        {{ $t('cusEntry.supplement20250205.recognized3') }}
      </el-button>
      <el-button type="primary" icon="el-icon-s-promotion" :loading="identifyLoad" @click="goIdentify">
        <!-- 发起围串标识别 -->
        {{ $t('cusEntry.supplement20250205.recognized4') }}
      </el-button>
    </div>
    <div class="content">
      <div v-for="(item, index) in fileList" :key="index" class="file-box">
        <div class="sup-name" style="font-size:14px;font-weight: 600;">
          {{ item.companyName }}
        </div>
        <div class="file-item">
          <div
            v-for="file in item.fileList"
            :key="file.orderDocId"
            class="item"
            :class="{'active': selectFileList.some(item => item.fileId === file.orderDocId)}"
            @click="handleSelect(file)"
          >
            <img v-if="/(\.pdf|\.PDF)$/i.test(file.orderFileName)" src="../../../../../../../assets/images/pdf_icon.png" alt="">
            <img v-else src="../../../../../../../assets/images/doc_icon.png" alt="">
            <div class="file-title">
              {{ file.orderFileName }}
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- 查看识别结果 -->
    <IdentifyResult
      :visible.sync="resultVisible"
      :projectId="projectId"
      :textNum="textNum"
      :extProjectNo="extProjectNo"
      :souName="souName"
    />
  </div>
</template>
<script>
import Vue from 'vue'
import { mapState, mapGetters } from 'vuex'
import { getOrderFileCheck, getFileList } from 'modcb@/biddingBuyer/api/analysis'
import IdentifyResult from './identifyResult'
export default {
  name: 'TechAnalysis',
  components: {
    IdentifyResult
  },
  data () {
    return {
      isResultFlag: true,
      projectId: null,
      extProjectNo: null,
      souName: '',
      textNumList: [],
      fileList: [],
      textNum: '',
      selectFileList: [],
      selectCompanyList: [],
      resultVisible: false
    }
  },
  computed: {
    ...mapState({
      identifyLoad: state => state.user.identifyLoad
    })
  },
  async created () {
    this.projectId = this.$attrs.params.projectId
    this.textNumList = this.$attrs.params.textNumList
    this.textNum = this.$attrs.params.textNumList[0].dictItemCode
    this.souName = this.$attrs.params.souName
    this.extProjectNo = this.$attrs.params.extProjectNo
    this.$store.commit('user/SET_IDENTIFY_LOAD', false)
    await this.getOrderFileCheck()
    await this.getFileList()
  },
  methods: {
    isPDFOrDoc (str) {
      return /(\.pdf|\.PDF|\.doc|\.DOC|\.docx|\.DOCX)$/i.test(str)
    },
    // 供应商分组
    groupBy (array, key1, key2) {
      let dataArr = []
      array.map((mapItem) => {
        let index = dataArr.findIndex(item => item.companyName == mapItem[key1])
        if (index === -1) {
          const obj = {
            companyName: mapItem[key1],
            fileList: [mapItem]
          }
          if (key2) {
            obj[key2] = mapItem[key2]
          }
          dataArr.push(obj)
        } else {
          dataArr[index].fileList.push(mapItem)
        }
      })
      return dataArr
    },
    // 获取流水号
    getOrderFileCheck () {
      const params = {
        type: 'OrderFileCheck',
        action: 'query',
        payload: {
          filter: {
            projectId: {
              eq: this.projectId
            }
          },
          page: {
            pageNum: 1,
            pageSize: 15,
            sort: 'lastUpdateDate desc'
          }
        },
        query: {
          '*': {}
        },
        lang: 'zh-cn',
        tree: true,
        __page: 1,
        __pagesize: 15
      }
      getOrderFileCheck(params).then(res => {
        if (res && res.data) {
          if (res.data.records.length > 0) {
            this.isResultFlag = false
          }
        }
      })
    },
    // 获取供应商及文件列表
    getFileList () {
      getFileList({ projectId: this.projectId }).then(res => {
        if (res && res.data) {
          const list = res.data.orderFileList.filter(item => this.isPDFOrDoc(item.orderFileName) === true && (item.fileType === 'TECH_BID' || item.fileType === 'TECH_SOLUTION_BID'))
          this.fileList = this.groupBy(list, 'vendorName')
        }
      })
    },
    // 发起围串标识别
    goIdentify () {
      const list = this.groupBy(this.selectFileList, 'companyName', 'companyId')
      if (!list.length || list.length == 1) {
        // 请至少选择两个不同供应商的文件和一个识别字数进行围串标分析。
        this.$alert(this.$t('cusEntry.supplement20250205.recognized5'), this.$t('common.tips'), {
          confirmButtonText: this.$t('common.confirm'),
          callback: action => {}
        })
      } else {
        // 确定要发起围串标识别分析吗？一旦发起，直至识别结果出具后，才可以重新发起。
        this.$confirm(this.$t('cusEntry.supplement20250205.recognized6'), this.$t('common.tips'), {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        }).then(() => {
          const param = {
            embedClient: 'SRMclient',
            projectId: this.projectId,
            compareWordSize: Number(this.textNum),
            companyList: list
          }
          this.$store.commit('user/SET_IDENTIFY_LOAD', true)
          this.$store.commit('user/SET_COMPARE_DATA', JSON.stringify(param))
          if (Vue.prototype.embedHelper) {
            setTimeout(() => {
              Vue.prototype.embedHelper.call('compareData', JSON.stringify(param))
            }, 900)
          }
          this.$handleFrameClick('file')
        }).catch(() => {})
      }
    },
    // 查看识别结果
    handleResult () {
      this.resultVisible = true
    },
    // 选择文件
    handleSelect (file) {
      let isHasFile = this.selectFileList.some(item => item.fileId === file.orderDocId)
      if (isHasFile) {
        this.selectFileList = this.selectFileList.filter(item => item.fileId != file.orderDocId)
      } else {
        const obj = {
          fileName: file.orderFileName,
          fileId: file.orderDocId,
          companyId: file.vendorId,
          companyName: file.vendorName
        }
        this.selectFileList.push(obj)
      }
    }
  }
}
</script>
<style lang="scss" scoped>
.analysis-box {
  height: 100%;
  .btn-box {
    padding-bottom: 10px;
    border-bottom: 1px solid #F2F2F2;
    display: flex;
    justify-content: end;
    .text-num {
      border-radius: 5px;
      border: 1px solid #d6d6d6;
      height: 36px;
      padding: 0 2px 0 15px;
      // color: #1E1E1E;
      margin-right: 10px;
      display: flex;
      align-items: center;
      justify-content: space-between;
    }
    .el-select {
      width: 80px;
      :deep(.el-input__inner) {
        border: 0!important;
      }
      :deep(.el-input.is-focus .el-input__inner) {
        border: 0!important;
      }
      :deep(.el-input__inner:focus) {
        box-shadow: none!important;
        -webkit-box-shadow: none!important;
      }
    }
  }
  .content {
    height: calc(100% - 58px);
    overflow-y: auto;
  }
  .file-box{
    border: 1px solid #ECECEC;
    border-radius: 20px;
    margin-top: 20px;
    padding: 18px 0 0px 20px;
    .sup-name {
      color: #1D2129;
      padding-bottom: 15px;
    }
    .file-item {
      display: flex;
      flex-wrap: wrap;
    }
    .item {
      width: 240px;
      height: 104px;
      padding: 18px 8px 0;
      border-radius: 10px;
      border: 1px solid #F2F2F2;
      margin:0 20px 20px 0;
      cursor: pointer;
      &.active {
        box-shadow: 0px 0px 20px 0px rgba(0,0,0,0.1);
        border-color: #188FFF;
      }
      img {
        width: 30px;
        height: 30px;
        margin: 0 auto;
        display: block;
      }
      .file-title {
        color: #1D2129;
        text-align: center;
        line-height: 16px;
        padding-top: 8px;
      }
    }
  }
}
</style>
