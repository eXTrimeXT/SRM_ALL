<template>
  <div class="workflow-server">
    <!--    <div class="colorC"></div>-->

    <el-container
      class="flex-container workflowReport_wrapper"
      direction="vertical"
    >
      <el-main
        style="position:relative;padding-top:10px"
      >
        <vxe-table
          ref="metadataListTable"
          border
          stripe
          align="left"
          min-height="200"
          :data="changeListData(metadataList)"
        >
          <template #empty>
            <div style="color: #96999c;">
              <img style="margin-top: 16px" :src="emptyIcon" alt="">
              <p>{{ $t('components.common.noMoreData') }}</p>
            </div>
          </template>
          <!--序号-->
          <vxe-column :title="$t('common.sort')" type="seq" width="60" align="center" />
          <!--节点-->
          <vxe-column
            field="columnName"
            :title="$t('components.processTable.headers.fdNodeName')"
            min-width="120"
            align="center"
          >
            <template #default="{ row }">
              <span v-if="row.required === true" class="red">*</span>
              {{ row.taskName }}
            </template>
          </vxe-column>
          <!--处理人-->
          <vxe-column
            field="columnName"
            :title="$t('vendorMod.operator')"
            min-width="120"
            align="center"
          >
            <template #default="{ row, rowIndex }" style="height: 80px">
              <PeopleSelector
                v-model="row.assignees"
                :searchData="{
                  taskKey: row.taskKey,
                  formDataId: businessId,
                  actionCode: actionCode
                }"
                :flag = flag
                :userSelected="rowIndex === 0 ? true : row.userSelected"
                :isShowButton="isShowButton"
                :disabled="rowIndex === 0 || !isShowButton || !row.editable"
                @on-confirm="selectedData => personnelChange(selectedData, row)"
              />
            </template>
          </vxe-column>
          <!--状态-->
          <vxe-column
            field="columnName"
            :title="$t('vendorMod.status')"
            min-width="120"
            align="center"
          >
            <template #default="{ row }">
              <i v-if="!['', '-', null, undefined, '审批中'].includes(row.comment)" class="el-icon-circle-check" />
              <div v-else>
                {{ row.comment === '审批中' ? $t('closeTask.auditCount') : row.comment }}
              </div>
            </template>
          </vxe-column>
        </vxe-table>
      </el-main>
    </el-container>
  </div>
</template>

<script>

import emptyIcon from '@/assets/table/empty.svg'
import History from './components/history.vue'
import Personnel from './components/personnel.vue'
import PeopleSelector from './components/peopleSelector'

export default {
  name: 'WorkflowReportSelf',
  components: {
    History,
    Personnel,
    PeopleSelector
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
    isShowButton: {
      type: Boolean,
      default: true
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
      flag: false, // 是否手动选择审批人
      activeName: 'first',
      emptyIcon: emptyIcon,
      businessId: null,
      actionCode: null,
      buttonList: [],
      popupType: null,
      metadataList: [],
      flowSctiveLine: ['flowHis']
    }
  },
  watch: {
    funParams: {
      handler (data) {
        this.businessId = this.funParams?.businessId
        this.actionCode = this.funParams?.businessType
        this.createdFun()
      },
      immediate: true,
      deep: true
    }
  },
  created () {
    // this.createdFun()
  },
  methods: {
    // 修改传入的数据并抛出处理后的数据
    changeListData (dataList) {
      let attr = []
      dataList.forEach(item => {
        if (item.childTasks && item.childTasks.length > 0) { // 如果有子节点的情况
          item.childTasks.forEach(itemChild => {
            attr.push(itemChild)
          })
        } else {
          attr.push(item)
        }
      })
      return attr
    },
    personnelChange (data, row) {
      this.flag = true
      this.$set(row, 'assignees', data)
    },
    handleClick () {

    },
    createdFun () {
      console.log(this.funParams, 'funParams')

      // 请求显示按钮接口
      this.$http({
        url: '/api-base/ext/flow/event/v2/task/config/list',
        method: 'POST',
        data: {
          'actionCode': this.actionCode,
          'formDataId': this.businessId // 业务单据ID(如果不传递，则是默认流程配置数据)
        }
      }).then(({ data }) => {
        console.log(data, 'buttons')
        this.buttonList = data?.buttons
        this.getListData('start')
      })
    },

    getListData (type) {
      let url = null
      // 如果是未提交的情况 // 发起审批的请求
      if (type === 'start') {
        url = '/api-base/flow/event/v2/instance/info/list'
      } else { // 其他还没有发起审批的请求
        url = '/api-base/flow/event/v2/instance/record/list'
      }

      if (this.businessId && this.actionCode) {
        this.$http({
          url,
          method: 'POST',
          data: {
            'actionCode': this.actionCode,
            'formDataId': this.businessId, // 业务单据ID(如果不传递，则是默认流程配置数据)
            // 'formData': this.funParams.businessVariables.formData
            'formData': this.funParams.businessVariables
          }
        }).then(({ data }) => {
          const newData = this.transformData(data)
          console.log(newData, 'newData')

          newData.forEach((item, index) => {
            this.$set(this.metadataList, [index], item)
          })

          this.$emit('changeMetadataList', this.metadataList)
          this.$forceUpdate()
        })
      }
    },

    transformData (data) {
      let attr = []
      data.forEach(item => {
        console.log(this.isShowButton, 'isShowButton')
        // 可编辑，且用户未默认全选。则清空当前节点的用户 且为新增
        if (item.editable === true && item.userSelected === false && this.isShowButton === true) {
          attr.push({
            ...item,
            assignees: []
          })
        } else {
          attr.push(item)
        }
      })
      return attr
    }
  }
}
</script>

<style lang="scss" scoped>
.workflow-server{
  margin-top: 12px;
  .colorC{
    height: 5px;
  }
}
.footer{
  position:absolute;
  bottom: 10px;
  width: 100%;
  display: flex;
  justify-content: flex-end;
}
.el-icon-circle-check{
  font-size: 30px;
  color: rgb(12, 146, 224);
}
.red{
  color: red;
}
</style>
