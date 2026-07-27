<template>
  <el-container
    class="flex-container workflowReport_wrapper"
    direction="vertical"
  >
    <el-main
      style="position:relative;padding-top:10px"
    >
      <el-tabs v-model="activeName" @tab-click="handleClick">
        <!--设置审批人-->
        <el-tab-pane :label="$t('workflowSelf.setApprover')" name="first">
          <vxe-table
            ref="metadataListTable"
            border
            show-overflow="tooltip"
            keep-source
            stripe
            align="left"
            min-height="500"
            :data="metadataList"
          >
            <template #empty>
              <div style="color: #96999c;">
                <img style="margin-top: 16px" :src="emptyIcon" alt="">
                <p>{{ $t('components.common.noMoreData') }}</p>
              </div>
            </template>
            <!--序号-->
            <vxe-column :title="$t('common.sort')" type="seq" width="60" />
            <!--节点-->
            <vxe-column
              field="columnName"
              :title="$t('components.processTable.headers.fdNodeName')"
              min-width="120"
            >
              <template #default="{ row }">
                {{ row.columnName }}
              </template>
            </vxe-column>
            <!--处理人-->
            <vxe-column
              field="columnName"
              :title="$t('vendorMod.operator')"
              min-width="120"
            >
              <template #default="{ row }">
                {{ row.columnName }}
              </template>
            </vxe-column>
          </vxe-table>
        </el-tab-pane>
        <!--审批历史-->
        <el-tab-pane :label="$t('workflowSelf.history')" name="second">
          <History
            :funParams="funParams"
          />
        </el-tab-pane>
      </el-tabs>
      <div class="footer">
        <!--通过-->
        <el-button @click="approve">{{ $t('components.approvalHead.headers.pass') }}</el-button>
        <!-- 转单 -->
        <el-button @click="approve">{{ $t('common.transferOrder') }}</el-button>
        <!-- 作废 -->
        <el-button @click="approve">{{ $t('common.cancelled') }}</el-button>
        <!-- 驳回 -->
        <el-button @click="approve">{{ $t('common.toRefuse') }}</el-button>
        <!-- 暂存 -->
        <el-button @click="approve">{{ $t('common.staging') }}</el-button>
        <!-- 提交 -->
        <el-button @click="submit" type="primary">{{ $t('common.submit') }}</el-button>

        <!-- <button v-if="isShowButton('transfer')" @click="transfer" plain class="button">转单</button>
      <button v-if="isShowButton('discard')" @click="discard" plain class="button">作废</button>
      <button v-if="isShowButton('overrule')" @click="overrule" type="warn" plain class="button">驳回</button>
      <button v-if="isShowButton('hold')" plain @click="staging" class="button">暂存</button>
      <button v-if="isShowButton('start')" @click="submit" class="button" type="primary">提交</button> -->
      </div>
    </el-main>
  </el-container>
</template>

<script>

import emptyIcon from '@/assets/table/empty.svg'
import History from './components/history.vue'

export default {
  name: 'WorkflowReportSelf',
  components: {
    History
  },
  props: {
    needInit: {
      type: Boolean,
      default: false
    },
    isNested: {
      type: Boolean,
      default: false
    },
    funParams: {
      type: Object,
      default: function () {
        return {}
      }
    }
  },
  data () {
    return {
      activeName: 'first',
      emptyIcon: emptyIcon,
      businessId: null,
      actionCode: null,
      buttonList: []
    }
  },
  watch: {
  },
  created () {
    this.createdFun()
  },
  methods: {
    handleClick () {

    },
    createdFun () {
      this.businessId = this.funParams?.businessId;
      this.actionCode = this.funParams?.businessType;
      console.log(this.funParams, 'funParams')

      // 请求显示按钮接口
      this.$http({
        url: '/cloud-srm/api-base/flow/ide-v4/api/task/config/list',
        method: 'POST',
        data: {
          "actionCode": this.actionCode,
          "formDataId": this.businessId, // 业务单据ID(如果不传递，则是默认流程配置数据)
        },
        success: (res) => {
          console.log(res.data, 'buttons');
          this.buttonList = res.data?.buttons
          const isShowButton = this.isShowButton('start')
          if (isShowButton) { // 如果有提交按钮就是未提起审批
            this.getListData('start')
            // this.items = ['申请信息']
          } else {
            // this.items = ['申请信息', '申请历史']
            this.getListData('start')
            // this.getListData('other')
          }
        }
      })
    },
    isShowButton (type) {
      if (this.buttonList?.length) {
        return this.buttonList.some(item => type == item.type)
      } else {
        return false
      }

    }
  }
}
</script>

<style lang="scss" scoped>
.footer{
  position:absolute;
  bottom: 10px;
  width: 100%;
  display: flex;
  justify-content: flex-end;
}
</style>
