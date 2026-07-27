<template>
  <!-- 业务流程任务 -->
  <div class="grid-content mid-pos">
    <h3 class="grid-title">
      <!-- 待办事项  -->
      {{ $t("flowMod.myTask") }}
      <!-- 更多 -->
      <span
        v-if="isShowMoreBtn"
        class="info-more"
        @click="moreTask"
      >
        {{ $t("common.more") }}
        >
      </span>
    </h3>
    <div class="select-wrapper">
      <div class="selected-list">
        <SortableList
          v-model="vendorTask"
          :press-delay="200"
          axis="xy"
          :lock-to-container-edges="true"
          @input="getChangeList"
        >
          <el-row>
            <!-- 2023.7.18 添加disabled属性，禁止拖拽 -->
            <SortableItem
              v-for="(item, index) in vendorTask"
              :key="index"
              class="SortableItem"
              :index="index"
              :item="item"
              disabled
            />
          </el-row>
        </SortableList>
      </div>
    </div>
  </div>
</template>
<script>
import { vendorTask, vendorTaskSaveSort } from '../api'
import { SortableList, SortableItem } from '../sortableA'
import { mapGetters } from 'vuex'
import { toTreeArray } from 'xe-utils'

export default {
  name: 'BusinessProcessTask',
  components: { SortableList, SortableItem },
  data () {
    return {
      menuModel: [],
      vendorTask: []
    }
  },
  computed: {
    ...mapGetters(['userInfo']),
    // 将树型的用户菜单转平铺的数组，并筛选需要的数据和属性，减低每次递归遍历树菜单的成本
    userMenusToTreeArray () {
      let menus = JSON.parse(JSON.stringify((this.userInfo || {}).menus || []))
      // 过滤不可跳转的父级菜单
      if (menus && Array.isArray(menus)) {
        // 平铺的数组
        menus = toTreeArray(menus, {
          clear: true,
          children: 'childPermissions'
        })

        // 筛选可点击菜单
        menus = menus.filter(item => item.functionAddress)
          .map(item => {
            // 只取需要用的属性
            return {
              functionAddress: item.functionAddress,
              permissionName: item.permissionName
            }
          })
        return menus
      }
      return []
    },
    isShowMoreBtn () {
      return !!this.userMenusToTreeArray.find(item => item.functionAddress === '/agentCenterVendor/approvalFlowCenterVendor')
    }
  },
  created () {
    this.fatchVendorTask() // 供应商任务
    let arrA = []
  },
  methods: {
    getChangeList (value) {
      // 拖拽排序后以新排序的列表结束后触发
      // console.log(value);
      let arrA = []
      value.forEach(item => {
        let obj = {}
        obj.listName = item.listName
        arrA.push(obj)
      })
      console.log(arrA, 222)
      // console.log("arrA",arrA)
      vendorTaskSaveSort(arrA).then(res => {
        console.log(res)
      })
    },
    // 查询供应商任务列表
    fatchVendorTask () {
      let taskArry = []
      vendorTask().then(res => {
        console.log('res', res.data, 333)
        if (res) {
          let data = res.data.length > 8 ? res.data.splice(0, 8) : res.data
          this.vendorTask = this.adaptTask(data)
        }
      })
    },
    adaptTask (data) {
      var arr = []
      if (data && data.length > 0) {
        data.map(item => {
          let urlQuery = item.url
          let routeName = urlQuery.split('/')[2] // url 路由名字
          let query = item.condition // 查询条件 {orderStatus:"APPROVED"} //
          let queryObj = {
            ...query,
            funName: routeName,
            from: 'workCount'
          }
          // 显示任务数目大于0 的项
          if (item.count > 0) {
            arr.push({
              count: item.count, // 条数
              title: item.title, // 标题
              params: queryObj, // 查询条件
              routeName: routeName, // 路由名
              listName: item.listName // 列表名字
            })
          }
        })
      }
      return arr
    },
    // 更多任务
    moreTask () {
      this.$router.push({ name: 'approvalFlowCenterVendor' })
    }
  }
}
</script>
<style lang="scss">
.SortableItem {
  position: relative;
  user-select: none;
  &::after {
    content: "";
    position: absolute;
    top: 7px;
    right: 0;
    height: 50px;
    width: 1px;
    background-color: #e8e9ea;
  }
  &:last-child{
    &::after{
      display: none;
    }
  }

  @media screen and (min-width: 1200px) {
    &:nth-child(8n + 8){
      &::after{
        display: none;
      }
    }
  }
  @media screen and (min-width: 992px) and (max-width: 1200px){
    &:nth-child(6n + 6){
      &::after{
        display: none;
      }
    }
  }
  @media screen and (min-width: 768px) and (max-width: 992px){
    &:nth-child(4n + 4){
      &::after{
        display: none;
      }
    }
  }
  @media screen and (max-width: 768px){
    &:nth-child(2n + 2){
      &::after{
        display: none;
      }
    }
  }
}
// .el-main{
//   overflow: auto !important;
//   .the_body{
//     overflow: auto !important;
//   }
// }
.grid-content {
  .select-wrapper {
    height: auto;
    // overflow: auto;
    position: relative;

    .selected-list {
      // height: 100px;
      width: 100%;
      // position: absolute;
      // top: 0;
      // left: 0;
    }
  }
}
</style>
