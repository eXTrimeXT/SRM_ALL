<template>
  <div class="workflow-top-bottom">
    <div class="title">
      <div>{{myInitData?.title}}</div>
      <div v-if="!['', null].includes(myInitData?.statusName)" class="status">{{myInitData?.statusName}}</div>
    </div>
    <div>
      <!--通过-->
      <el-button v-if="isShowButton('approve')" @click="approve(true)" type="primary">{{ $t('components.approvalHead.headers.pass') }}</el-button>
      <!-- 不通过 -->
      <el-button v-if="isShowButton('approve') && showUnpass" @click="approve(false)" type="primary">{{ $t('cusEntry.supplement20250211.unPass') }}</el-button>
      <!-- 驳回 -->
      <el-button v-if="isShowButton('overrule')" @click="overrule">{{ $t('common.toRefuse') }}</el-button>
      <!-- 提交 -->
      <el-button v-if="isShowButton('start')" @click="start" type="primary">{{ $t('common.submit') }}</el-button>
      <!-- 暂存 -->
      <!-- <el-button v-if="isShowButton('hold')" @click="staging">{{ $t('common.staging') }}</el-button> -->
      <!-- 转单 -->
      <el-button v-if="isShowButton('transfer')" @click="transfer">{{ $t('common.transferOrder') }}</el-button>
      <!-- 作废 -->
      <el-button v-if="isShowButton('discard')" @click="discard">{{ $t('common.cancelled') }}</el-button>
      <!-- 撤回 -->
      <el-button v-if="isShowButton('recall')" @click="recall">{{ $t('common.recall') }}</el-button>
      <!-- 返回 -->
      <el-button v-if="isShowButton('start')" @click="backTo">{{ $t('common.backTo') }}</el-button>
    </div>
    <div class="right">
      <div v-if="metadataList[0]?.assignees[0]?.assigneeName">{{ $t('purchaseDemand.applicant') }}:{{ metadataList[0]?.assignees[0]?.assigneeName }}</div>
    </div>
  </div>
</template>

<script>

import CCategorySelect from 'lib@/components/c-category-select/index.vue'
import MImport from 'lib@/components/import'

export default {
  name: 'History',
  components: {
    MImport, CCategorySelect
  },
  props: {
    buttonList: {
      type: Array,
      default: function () {
        return []
      }
    },
    // 是否显示不通过按钮，实际是调用通过接口，只是传入参数不同，不影响审批流进度（eg: 供应商库）
    showUnpass: {
      type: Boolean,
      default: () => {
        return false
      }
    },
    businessId: {
      type: Number,
    },
    businessType: {
      type: String,
    },
    metadataList: {
      type: Array,
    },
    myInitData: {
      type: Object,
    }
  },
  data () {
    return {
      dialogFormVisible: false
    }
  },
  watch: {
  },
  created () {

  },
  methods: {
    isShowButton (type) {
      if (this.buttonList?.length) {
        return this.buttonList.some(item => type == item.type)
      } else {
        return false
      }
    },
    backTo () {
      this.$emit("goBack")
    },
    // 提交
    start () {
      this.$emit("clickButtom",'start')
    },
    // 暂存
    staging () {
      this.$emit("clickButtom",'staging')
    },
    // 驳回
    overrule () {
      this.$emit("clickButtom",'overrule')
    },
    // 作废
    discard () {
      this.$emit("clickButtom",'discard')
    },
    // 转单
    transfer () {
      this.$emit("clickButtom",'transfer')
    },
    // 通过
    approve (isPass) {
      let res = isPass ? 'approve' : 'approveNo'
      this.$emit("clickButtom", res)
    },
    recall () {
      this.$emit("clickButtom",'recall')
    }
  }
}
</script>

<style lang="scss" scoped>
.workflow-top-bottom {
  position: sticky;
  top: 0px;
  z-index: 999;
  background: #fff;
  height: 67px;
  box-shadow: 5px 5px 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 5px;
  .right{
    position: absolute;
    top: 0;
    right: 10px;
    line-height: 67px;
    color: #9b9ea1;
  }
  .title{
    font-size: 16px;
    color: #161C24;
    letter-spacing: 0;
    line-height: 22px;
    font-weight: 700;
    margin-bottom: 9px;
    display: flex;
    .status{
      background: #E7F2FF;
      border: 1px solid #A6D0FF;
      border-radius: 2px;
      font-size: 12px;
      color: #0077FF;
      font-weight: 400;
      padding: 0px 8px;
      height: 22px;
      margin-left: 10px;
    }
  }
}
</style>
