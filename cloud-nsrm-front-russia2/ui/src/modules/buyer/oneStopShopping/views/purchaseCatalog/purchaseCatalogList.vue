<template>
  <el-container class="flex-container the_purchaseCatalogList_wrapper">
    <FormWrapper
      :form-array="preArr"
      :pre-form-obj="preFormObj"
      @getFormData="getQuerydata"
      @synchronous-value="val => (queryformData = val)"
    />
    <div class="header-btn">
      <em
        :class="['iconfont', listShowType === 'card' ? 'iconunorderedList' : 'icontupianliebiao']"
        class="toggle-list"
        @click="toggleList"
      />
    </div>
    <el-container>
      <el-aside width="230px" class="purchaseCatalogListAside">
        <div class="slide-tree-cat">
          <el-tree
            ref="tree"
            lazy
            node-key="categoryId"
            :data="orgTreeData"
            :default-expanded-keys="treeData"
            :props="orgTreeProps"
            :load="loadNode"
            :highlight-current="false"
            :expand-on-click-node="false"
            @node-click="nodeClickHandle"
          >
            <template slot-scope="{ node }">
              <span class="tree-node" :title="node.label">{{ node.label }}</span>
            </template>
          </el-tree>
        </div>
      </el-aside>
      <el-main class="purchaseCatalogListMain">
        <list-show-card
          v-if="listShowType === 'card'"
          :itemResData="itemResData"
          @itemDetail="itemDetail"
          @addShoppingCart="addShoppingCart"
        />

        <TableView
          v-if="listShowType === 'list'"
          ref="tableRef"
          class="method-list"
          style="height: 100%;"
          :cell-style="cellStyle"
          :table-infor="itemResData"
          :row-index="false"
          :table-header="tableHeader"
          :auto-query="false"
          row-key="catalogOnShelvesId"
          :page-enabled="false"
          :comActive="$attrs['changeTab']"
        >
          <template #imgUrl="{ scope }">
            <div class="table-img">
              <div
                v-if="listShowType === 'list'"
                class="logo-small"
                :style="{padding: scope.row.fileuploadId ? 0 : '0 4px', backgroundColor: scope.row.fileuploadId ? none : '#75C8FF'}"
              >
                <img
                  width="100%"
                  :src="scope.row.imgUrl"
                  :alt="scope.row.materialName"
                  @click="itemDetail(scope.row)"
                >
              </div>
            </div>
          </template>
        </TableView>
      </el-main>
    </el-container>
    <el-footer height="auto" style="padding: 0;">
      <CPagination
        ref="queryPagination"
        class="c-query-table-pagination"
        style="padding:16px 0 4px 0;"
        :total="pageInfo.total"
        :page-num="pageInfo.pageNum"
        :page-size="pageInfo.pageSize"
        :pageSizes="pageInfo.pageSizes"
        @current-change="handlePageNumChange"
        @size-change="handleSizeChange"
      />
    </el-footer>
  </el-container>
</template>
<script>
import { tabTodoMixin, tabTodoWatch } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import purchaseCatalogDetail from './purchaseCatalogDetail'
import CPagination from 'lib@/components/c-pagination'
import { calcDate } from 'lib@/utils/date-format'
import listShowCard from './components/listShowCard'
import { getImgSrc } from 'lib@/utils/file'
import { purchaseCatalogApi } from 'modb@/oneStopShopping/api'

const smallLogo = '@/assets/images/catalogLogoSmall.png'
const bigLogo = '@/assets/images/catalogLogoBig.png'

export default {
  name: 'PurchaseCatalogList',
  components: {
    TableView,
    FormWrapper,
    purchaseCatalogDetail,
    CPagination,
    listShowCard
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      defaultLogo: '',
      listShowType: 'card',
      treeData: [],
      approvalFiles: [],
      show_tab2: false,
      // pageSize: 15,
      pageInfo: {
        total: 0,
        pageNum: 1,
        pageSize: 3,
        pageSizes: [3, 15, 30, 60, 120, 300, 600, 1000, 1500]
      },
      queryPage: {
        pageNum: 1,
        pageSize: 3
      },
      gridId: 'purchaseCatalogList',
      selectList: [],
      currentRow: null,
      itemResData: [],
      tableName: 'purchaseCatalogList',
      tableHeader: [
        {
          prop: 'imgUrl',
          showType: 'slot',
          slot: 'imgUrl',
          unsortable: true,
          width: 100
        },
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.materialCode'), // 物料编码
          prop: 'materialCode',
          showType: 'button',
          btnStyle: 'text',
          callback: row => this.itemDetail(row)
        },
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.materialName'), // 物料名称
          prop: 'materialName'
        },
        {
          label: () => this.$t('materialMainData.specification'), // 型号规格
          prop: 'specification'
        },
        {
          label: () => this.$t('materialMainData.unit'), // 单位
          prop: 'unitCode',
          formattor: (val) => this.$getDictLabel('unit', val)
        },
        {
          label: () => this.$t('common.vendor'), // 供应商
          prop: 'vendorName'
        },
        {
          label: () => this.$t('purchaseDemand.taxPrice'), // 含税单价
          prop: 'taxPrice'
        },
        {
          label: () => this.$t('oneStopShopping.distanceShelf'), // 距离下架
          prop: 'remainingTime'
        },
        {
          prop: 'operation',
          label: () => this.$t('common.operation'), // 操作
          showType: 'button',
          btnStyle: 'text',
          callback: row => this.addShoppingCart(row),
          formattor: _ => this.$t('common.addShoppingCart')
        }
      ],
      tableData: [],
      tableData2: [],
      funParams: {},
      queryParam: {},
      preFormObj: {
        categoryId: '',
        orgId: null,
        organizationId: null
      },
      queryformData: {},
      editableTabsValue: 'tab1',
      formLabelWidth: '100px',
      isActive: false,
      preArr: [
        {
          prop: 'orgId',
          label: () => this.$t('dataConfMod.orgId'), // 业务实体
          type: 'OUorganizationSelector',
          multiple: false
        },
        {
          prop: 'organizationId',
          label: () => this.$t('dataConfMod.organizationId'), // 库存组织
          type: 'INVorganizationSelector',
          parentId: 'orgId',
          multiple: false
        },
        {
          prop: 'categoryId',
          label: () => this.$t('common.categoryName'), // 品类名称
          type: 'quicksearch',
          showKey: 'categoryName', // 显示值
          propKey: 'categoryId', // 取值
          name: 'scc_base_purchase_category2'
        },
        {
          prop: 'materialId',
          label: () => this.$t('common.materialName'), // 物料名称
          type: 'quicksearch',
          showKey: 'materialName',
          propKey: 'materialId',
          name: 'scc_base_material_item'
        }
      ],
      // 部门树数据
      orgTreeData: [
        {
          childrens: [],
          categoryId: ''
        }
      ],
      // 部门树配置选项
      orgTreeProps: {
        children: 'children',
        label: 'categoryName',
        isLeaf: data => {
          return data.isLeaf
        }
      },
      curTreeCatVal: '' // 点击树以后查询
    }
  },
  computed: {
    pageSize () {
      return this.listShowType === 'list' ? 15 : 3
    }
  },
  provide () {
    return { context: this }
  },
  created () {
    this.getQuerydata()
    this.defaultLogo = bigLogo
  },
  methods: {
    // 设置价格和下架时间红色字
    cellStyle ({ row, column }) {
      if (['price', 'remainingTime'].includes(column.property)) {
        return 'font-weight: bold;color: #FF4A4D;'
      }
    },
    // 计算时间差
    getTimeDiff (time1, time2) {
      let timeObj = calcDate(time1, time2)
      let day = timeObj.days > 0 ? timeObj.days : 0
      let hours = timeObj.hours > 0 ? timeObj.hours : 0
      return day === 0 && hours === 0 ? 0 : day + this.$t('time.day') + hours + this.$t('time.hour')
    },
    // 获取倒计时时间
    getCountDown (time) {
      let expirationDateTimes = new Date(time).getTime() // 获取截止时间戳
      // let expirationDateTimes1 = new Date('2022-08-18 15:29:26').getTime() // 获取时间 getTimeDiff((new Date()).getTime(),expirationDateTimes)//
      return this.getTimeDiff(new Date().getTime(), expirationDateTimes) // 计算离距截止时间的剩余时间
    },
    // 切换列表样式或者卡片样式
    toggleList () {
      // 切换图片、切换每页多少条数据
      this.defaultLogo = this.listShowType === 'card' ? smallLogo : bigLogo
      let pageSize = this.listShowType === 'card' ? 15 : 3

      this.getQuerydata(this.queryformData, pageSize).then(_ => {
        // 重新请求数据之后再切换卡片，避免大图小路切换闪烁
        if (this.listShowType === 'card') {
          this.listShowType = 'list'
        } else {
          this.listShowType = 'card'
        }
      })
    },
    // 加载子节点
    getDepartmentTree (parmes, res) {
      if (this.firstLoad) {
        this.departmentLoading = true
      }
      return new Promise(resolve => {
        purchaseCatalogApi.getCatChildrenData(parmes)
          .then(response => {
            if (response && response.data) {
            // 左侧的分类只显示“备品备件”、综合类物资
            // if (parmes.categoryId == -1) {
            //   response.data = response.data.filter(
            //     v => v.categoryCode == 30 || v.categoryCode == 70
            //   )
            // }
              res && res(response.data)
            // 嵌套promise返回筛选后数据再进行回调拿到第一个节点数据
            // resolve(response.data)
            } else {
              this.$message({
                message: this.$t('dataConfMod.loadDataFail') + response.msg, // 数据获取失败：
                type: 'error'
              })
            }
          })
          .finally(() => {
            this.firstLoad = false
            this.departmentLoading = false
          })
      })
    },
    // 异步树叶子节点懒加载逻辑
    loadNode (node, resolve) {
      this.getDepartmentTree({ categoryId: node.data.categoryId || -1 }, resolve)
      // let data = await this.getDepartmentTree({ categoryId: node.data.categoryId || -1 }, resolve)
      // // 拿到根节点数据之后再调用取子节点，再让他展开
      // data.forEach(async item => {
      //   if (item.level === 1) {
      //     await this.getDepartmentTree({ categoryId: item.categoryId })
      //     this.treeData.push(item.categoryId)
      //   }
      // })
    },
    nodeClickHandle (data, node, context) {
      if (data.level !== 3) return
      this.queryParam.categoryId = data.categoryId
      this.fatchItemDataList(this.queryParam, { pageSize: this.pageSize, pageNum: 1 })
    },
    // v: 查询参数，pageSize: toggle传入数据限定返回展示多少数据
    getQuerydata (v, pageSize) {
      this.queryParam = v || {}
      return new Promise(async resolve => {
        await this.fatchItemDataList(this.queryParam, { pageSize: pageSize || this.pageSize, pageNum: 1 })
        // 用于toggle接口请求完再做卡片切换，避免图片切换闪烁
        resolve(true)
      })
    },
    // 查询物料
    fatchItemDataList (p1, p2) {
      let queryObj = { ...p1, ...p2 }
      purchaseCatalogApi.purCatlistPage(queryObj).then(res => {
        if (res.data && res.data.list) {
          this.pageInfo.total = res.data.total
          this.pageInfo.pageNum = res.data.pageNum
          this.pageInfo.pageSize = res.data.pageSize
          this.itemResData = this.adaptData(res.data.list)
        }
      })
    },
    // 获取图片
    formatImgUrl (id) {
      if (id) {
        return getImgSrc(id)
      } else {
        return this.defaultLogo
      }
    },

    // 数据处理
    adaptData (data) {
      return data.map(item => {
        let remainingTime = this.getCountDown(item.expirationDate) // 获取倒计时时间
        let imgUrl = this.formatImgUrl(item.fileuploadId) // 获取图片信息链接
        return {
          ...item,
          remainingTime: remainingTime, // 剩余时间
          imgUrl: imgUrl // 图片链接
        }
      })
    },
    handlePageNumChange (num) {
      this.queryPage.pageNum = num
      this.queryPage.pageSize = this.pageSize
      let query = {}
      if (this.curTreeCatVal) {
        query = { ...this.queryformData, categoryId: this.curTreeCatVal }
      } else {
        query = { ...this.queryformData }
      }
      this.fatchItemDataList(query, this.queryPage)
    },
    handleSizeChange (size) {
      this.queryPage.pageSize = size
      let query = {}
      if (this.curTreeCatVal) {
        query = { ...this.queryformData, categoryId: this.curTreeCatVal }
      } else {
        query = { ...this.queryformData }
      }
      this.fatchItemDataList(query, this.queryPage)
    },
    // 加入购物车
    addShoppingCart (data) {
      if (!data.unitCode || !data.categoryCode) {
        return this.$message({
          type: 'error',
          message: this.$t('oneStopShopping.addShoppingCartMsg1')
        })
      }
      if (!data.orgId || !data.organizationId) {
        return this.$message({
          type: 'error',
          message: this.$t('oneStopShopping.addShoppingCartMsg2')
        })
      }
      purchaseCatalogApi.addToShoppingCart({ catalogOnShelvesId: data.catalogOnShelvesId }).then(res => {
        this.$message.success(this.$t('dataConfMod.addShoppingSuccess'))
      })
    },
    itemDetail (row) {
      this.$emit('tab-add', {
        component: purchaseCatalogDetail,
        params: {
          flag: 'view',
          row: row,
          tabName: 'purchaseCatalogDetail' + row.catalogOnShelvesId
        },
        title: () => this.$t('oneStopShopping.purchaseCatalogDetail') + row.materialId,
        name: 'purchaseCatalogDetail' + row.catalogOnShelvesId
      })
    },
    handleCurrentChange (val) {
      this.currentRow = val
    }
  }
}
</script>
<style lang="scss" scoped>
.el-container {
  overflow: hidden !important;
}
.the_purchaseCatalogList_wrapper {
  .slide-tree-cat {
    .el-tree {
      width: 100%;
      height: 100%;
      padding: 16px 8px;
      overflow-x: scroll;
      overflow-y: hidden;
    }
    .tree-node {
      width: 170px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
  }
  .header-btn {
    padding: 0 0 14px 0;
    display: flex;
    justify-content: flex-end;
    align-items: center;
  }
  .toggle-list {
    font-size: 20px;
    width: 30px;
    height: 30px;
    line-height: 30px;
    text-align: center;
    border-radius: 4px;
    color: #979A9D;
    border: 1px solid #979A9D;
  }
  :deep(.el-aside.purchaseCatalogListAside) {
    border: 1px solid #dfe6ec;
    border-right: 0;
    padding: 0;
    margin: 0;
    background: #fff;
    overflow: auto !important;
  }
  :deep(.el-main.purchaseCatalogListMain) {
    overflow: hidden;
    border: 1px solid #dfe6ec;
  }
  .method-list {
    padding: 0;
    height: 100%;
    :deep(.vxe-cell) {
      max-height: 80px !important;
    }
    .table-img {
      height: 80px;
      padding: 4px 0;
      display: flex;
      align-items: center;
      justify-content: center;
      .logo-small {
        height: 100%;
        width: 100%;
        display: flex;
        justify-content: center;
        align-items: center;
      }
    }
  }
}
</style>
