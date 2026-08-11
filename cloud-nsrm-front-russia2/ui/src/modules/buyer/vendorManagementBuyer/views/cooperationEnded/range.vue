<template>
  <div>
    <!-- 控制范围 -->
    <el-collapse-item
      v-if="['ORGANIZATION_EXIT', 'ORGANIZATION_FORZEN', 'ORGANIZATION_THAW', 'CATEGORY_EXIT', 'CATEGORY_FORZEN', 'CATEGORY_THAW' ].includes(rangeType)"
      :title="$t('vendorMod.controlRange')"
      name="3"
    >
      <!-- 控制范围,组织退出 -->
      <el-table
        v-if="['ORGANIZATION_EXIT', 'ORGANIZATION_FORZEN', 'ORGANIZATION_THAW' ].includes(rangeType)"
        ref="multipleTable"
        :data="datas"
        border
        style="width: 100%"
        @select="handleSelectionChange"
        @select-all="handleSelectionChange"
      >
        <el-table-column
          prop="selected"
          type="selection"
          width="50"
          align="center"
          :selectable="setSelectable"
        />
        <!-- 序号 -->
        <el-table-column
          type="index"
          :label="$t('vendorMod.numericalOrder')"
          width="55"
          align="center"
        />
        <!-- 采购组织 -->
        <el-table-column
          prop="orgName"
          :label="$t('common.orgName')"
        />
      </el-table>

      <!-- 控制范围,品类提出 -->
      <el-table
        v-if="['CATEGORY_EXIT', 'CATEGORY_FORZEN','CATEGORY_THAW' ].includes(rangeType)"
        ref="multipleTable"
        :data="datas"
        border
        style="width: 100%"
        @select="handleSelectionChange"
        @select-all="handleSelectionChange"
      >
        <el-table-column
          prop="selected"
          type="selection"
          width="55"
          align="center"
          :selectable="setSelectable"
        />
        <!-- 序号 -->
        <el-table-column
          type="index"
          :label="$t('vendorMod.numericalOrder')"
          width="55"
          align="center"
        />
        <!-- 品类 -->
        <el-table-column
          prop="categoryName"
          :label="$t('vendorMod.categoryName')"
        />
      </el-table>
    </el-collapse-item>

    <!-- 控制明细 -->
    <el-collapse-item
      :title="$t('vendorMod.controlDetail')"
      name="4"
    >
      <el-table
        ref="category"
        :data="listDataShow"
        border
        style="width: 100%"
        @select="handleSelectionChange2"
        @select-all="handleSelectionChange2"
      >
        <!-- 品类退出 -->
        <el-table-column
          v-if="['CATEGORY_EXIT', 'CATEGORY_FORZEN','CATEGORY_THAW' ].includes(rangeType)"
          prop="selected"
          type="selection"
          width="50"
          align="center"
          :selectable="setSelectable"
        />
        <!-- 序号 -->
        <el-table-column
          type="index"
          :label="$t('vendorMod.numericalOrder')"
          width="55"
          align="center"
        />
        <!-- 采购组织 -->
        <el-table-column
          prop="orgName"
          :label="$t('common.orgName')"
        />
        <!-- 品类 -->
        <el-table-column
          prop="categoryName"
          :label="$t('vendorMod.categoryName')"
        />
      </el-table>
      <el-pagination
        class="paginationStyle"
        background
        layout="prev, pager, next"
        :total="listDataTotal"
        :current-page="listPageNum"
        @current-change="handleCurrentChange"
      />
    </el-collapse-item>
  </div>
</template>

<script>
/**
 * CATEGORY_EXIT 品类退出 | ORGANIZATION_EXIT 组织退出 | OVERALL_EXIT 整体退出
 * CATEGORY_FORZEN 品类冻结 | ORGANIZATION_FORZEN 组织冻结 | OVERALL_FORZEN 整体冻结
 * CATEGORY_THAW 品类解冻 |  ORGANIZATION_THAW 组织解冻 | OVERALL_THAW 整体解冻
*/
export default {
  name: 'Range',
  components: {},
  props: {
    datas: {
      type: Array,
      default: () => []
    },
    listDataAll: {
      type: Array,
      default: () => []
    },
    rangeType: { // ORGANIZATION_EXIT,OVERALL_EXIT,CATEGORY_EXIT
      type: String,
      default: () => null
    },
    curOpt: {
      type: String,
      default: () => null
    }
  },
  data () {
    return {
      categoryList: [],
      rangeList: [],
      listData: [],
      listDataShow: [], // 显示出来的分页数据
      listDataTotal: 0, // 控制明细的总条数
      listPageNum: 1 // 分页的页数
    }
  },
  watch: {
    datas: {
      handler () {

      },
      deep: true
    },
    listData: {
      handler () {
        let listNum = this.listData.length
        if (listNum > 10) {
          let listDataShow = JSON.parse(JSON.stringify(this.listData))
          listDataShow = listDataShow.slice(0, 10)
          this.listDataShow = listDataShow
        } else {
          this.listDataShow = this.listData
        }
        this.listDataTotal = listNum
      },
      deep: true
    },
    listDataAll: {
      handler () {
        // 整体退出 整体冻结 整体解冻
        if (['OVERALL_EXIT', 'OVERALL_FORZEN', 'OVERALL_THAW'].includes(this.rangeType)) {
          this.listData = this.listDataAll
        } else {
          this.listData = []
        }

        let selectedArr = [] // 选中列
        this.datas.forEach(datass => {
          if (datass.selected == 'Y') {
            selectedArr.push(datass)
          }
        })
        this.$nextTick(() => {
          selectedArr.forEach(selectedItem => {
            this.$refs.multipleTable.toggleRowSelection(selectedItem)
            // 品类退出 品类冻结 品类解冻
            if (['CATEGORY_EXIT', 'CATEGORY_FORZEN', 'CATEGORY_THAW'].includes(this.rangeType)) {
              this.listDataAll.forEach(catItem => {
                if (selectedItem.categoryId == catItem.categoryId) {
                  this.listData.push(catItem)
                }
              })
            }
            // 组织退出 组织冻结 组织解冻
            if (['ORGANIZATION_EXIT', 'ORGANIZATION_FORZEN', 'ORGANIZATION_THAW'].includes(this.rangeType)) {
              this.listDataAll.forEach(orgItem => {
                if (selectedItem.orgId == orgItem.orgId) {
                  this.listData.push(orgItem)
                }
              })
            }
          })
          // 品类退出 品类冻结
          if (['CATEGORY_EXIT', 'CATEGORY_FORZEN', 'CATEGORY_THAW'].includes(this.rangeType)) {
            this.listData.forEach(catItem => {
              if (catItem.selected == 'Y') {
                console.log(catItem)
                this.$refs.category.toggleRowSelection(catItem)
              }
            })
          }
        })
      },
      deep: true
    }
  },

  mounted () {

  },
  created () {},

  methods: {
    getCategoryList () {
      return this.$refs.category.selection || []
    },
    getRangeList () {
      return this.$refs.multipleTable.selection || []
    },
    setSelectable () {
      if (this.curOpt == 'add' || this.curOpt == 'edit') {
        return true
      } else {
        return false
      }
    },
    // 分页change 前端分页
    handleCurrentChange (val) {
      let num = (val - 1) * 10
      let num10 = num + 10
      let listDataShow = this.listData
      listDataShow = listDataShow.slice(num, num10)
      this.listDataShow = listDataShow
    },
    // 控制明细 change
    handleSelectionChange2 (val) {
      this.categoryList = val
    },
    // 品类退出 组织退出 change
    handleSelectionChange (val) {
      const listDataAll = this.listDataAll
      let listData = []
      this.rangeList = val
      listDataAll.forEach(datas => {
        if (['CATEGORY_EXIT', 'CATEGORY_FORZEN', 'CATEGORY_THAW'].includes(this.rangeType)) {
          val.forEach(datas2 => {
            if (datas.categoryId == datas2.categoryId) {
              listData.push(datas)
            }
          })
        }
        if (['ORGANIZATION_EXIT', 'ORGANIZATION_FORZEN', 'ORGANIZATION_THAW'].includes(this.rangeType)) {
          val.forEach(datas2 => {
            if (datas.orgId == datas2.orgId) {
              listData.push(datas)
            }
          })
        }
      })
      this.listData = listData
      this.categoryList = []
    }
  }
}
</script>

<style scoped lang="scss">
:deep(.paginationStyle){
  float:right;
  margin-top:15px
}
</style>
