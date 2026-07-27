<template>
  <div>
    <el-collapse
      v-model="activeDims"
      class="tab-form-style"
    >
      <!-- 控制范围 -->
      <el-collapse-item
        v-if="[
          'ORGANIZATION_EXIT',
          'ORGANIZATION_FORZEN',
          'ORGANIZATION_THAW',
          'CATEGORY_EXIT',
          'CATEGORY_FORZEN',
          'CATEGORY_THAW',
          'CATEGORY_LIMIT_FLAG',
          'CATEGORY_LIMIT_FLAG_REMOVE',
          'POSITION_LIMIT_FLAG',
          'POSITION_LIMIT_FLAG_REMOVE'
        ].includes(rangeType)"
        :title="$t('vendorMod.controlRange')"
        name="3"
      >
        <FormWrapper
          v-if="[
            'ORGANIZATION_EXIT',
            'ORGANIZATION_FORZEN',
            'ORGANIZATION_THAW',
            'POSITION_LIMIT_FLAG',
            'POSITION_LIMIT_FLAG_REMOVE'
          ].includes(rangeType)"
          :form-array="queryOrgConfig"
          @getFormData="filterOrgHandler"
        />
        <!-- 控制范围,组织退出 -->
        <el-table
          v-if="[
            'ORGANIZATION_EXIT',
            'ORGANIZATION_FORZEN',
            'ORGANIZATION_THAW',
            'POSITION_LIMIT_FLAG',
            'POSITION_LIMIT_FLAG_REMOVE'
          ].includes(rangeType)"
          ref="multipleTable"
          :row-key="row => getRowKey(row, 'categoryCode')"
          :data="showOrgRecords"
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
            :label="$t('cusEntry.vendorMod.orgName')"
          />
        </el-table>

        <!-- 控制范围,品类提出 -->
        <el-table
          v-if="[
            'CATEGORY_EXIT',
            'CATEGORY_FORZEN',
            'CATEGORY_THAW',
            'CATEGORY_LIMIT_FLAG',
            'CATEGORY_LIMIT_FLAG_REMOVE',
            'CATEGORY_LIMIT_FLAG'
          ].includes(rangeType)"
          ref="multipleTable"
          :row-key="row => getRowKey(row, 'categoryCode')"
          :data="showOrgRecords"
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
        v-if="['CATEGORY_EXIT', 'ORGANIZATION_EXIT', 'OVERALL_EXIT', 'CATEGORY_LIMIT_FLAG', 'CATEGORY_LIMIT_FLAG_REMOVE'].includes(rangeType)"
        :title="$t('vendorMod.controlDetail')"
        name="4"
      >
        <FormWrapper
          :form-array="queryConfig"
          @getFormData="filterHandler"
        />
        <el-table
          ref="category"
          :data="listDataShow"
          border
          style="width: 100%"
          :row-key="row => getRowKey(row, 'orgId')"
          @select="handleSelectionChange2"
          @select-all="handleSelectionChange2"
        >
          <!-- 品类退出 -->
          <el-table-column
            v-if="['CATEGORY_EXIT', 'CATEGORY_FORZEN','CATEGORY_THAW', 'CATEGORY_LIMIT_FLAG', 'CATEGORY_LIMIT_FLAG_REMOVE' ].includes(rangeType)"
            prop="selected"
            type="selection"
            width="50"
            align="center"
            reserve-selection
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
            :label="$t('cusEntry.vendorMod.orgName')"
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
          layout="prev, pager, next, sizes, jumper"
          :total="targetList.length"
          :current-page="listPageNum"
          :page-sizes="[10, 20, 30, 40, 50, 100, 500, 1000]"
          @current-change="handleCurrentChange"
          @size-change="sizeChange"
        />
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<script>
/**
 * CATEGORY_EXIT 品类退出 | ORGANIZATION_EXIT 组织退出 | OVERALL_EXIT 整体退出
 * CATEGORY_FORZEN 品类冻结 | ORGANIZATION_FORZEN 组织冻结 | OVERALL_FORZEN 整体冻结
 * CATEGORY_THAW 品类解冻 |  ORGANIZATION_THAW 组织解冻 | OVERALL_THAW 整体解冻
*/
import FormWrapper from 'lib@/components/Table/FormWrapper'
import uniqueId from 'lodash/uniqueId'
export default {
  name: 'Range',
  components: {
    FormWrapper
  },
  props: {
    value: {
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
      queryForm: {},
      queryConfig: [{
        prop: 'companyName',
        label: () => this.$t('cusEntry.vendorMod.companyName')
      }],
      queryOrgConfig: [
        {
          prop: 'companyName',
          label: () => this.$t('cusEntry.vendorMod.companyName')
        }
      ],
      paginationInfo: {
        pageSize: 10,
        pageNum: 1
      },
      categoryList: [],
      rangeList: [],
      targetList: [],
      listData: [],
      showOrgRecords: [], // 显示的组织数据
      listDataShow: [], // 显示出来的分页数据
      listPageNum: 1, // 分页的页数
      activeDims: ['1', '2', '3', '4', '5', '6'],
      bol: 0 // listDataAll会触发多次，最后一次的时候控制范围没有勾上，暂时没有找到原因，所以先写变量防止多次触发
    }
  },
  watch: {
    listData: {
      handler (newValue) {
        let listNum = this.listData.length
        if (listNum > 10) {
          let listDataShow = JSON.parse(JSON.stringify(this.listData))
          listDataShow = listDataShow.slice(0, 10)
          this.listDataShow = listDataShow
        } else {
          this.listDataShow = this.listData
        }
        this.targetList = newValue
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
        // let selectedArr = [] // 选中列
        // this.showOrgRecords.forEach(datass => {
        //   if (datass.selected == 'Y' || this.rangeList.map(item => item.categoryCode).includes(datass.categoryCode)) {
        //     selectedArr.push(datass)
        //   }
        // })
        // this.$nextTick(() => {
        //   selectedArr.forEach(selectedItem => {
        //     if (selectedItem) {
        //       this.$refs.multipleTable.toggleRowSelection(selectedItem)
        //       setTimeout(() => {
        //         this.$refs.multipleTable.doLayout()
        //       }, 100)
        //     }
        //     // 品类退出 品类冻结 品类解冻
        //     if (['CATEGORY_EXIT', 'CATEGORY_FORZEN', 'CATEGORY_THAW', 'CATEGORY_LIMIT_FLAG', 'CATEGORY_LIMIT_FLAG_REMOVE'].includes(this.rangeType)) {
        //       this.listDataAll.forEach(catItem => {
        //         if (selectedItem.categoryId == catItem.categoryId) {
        //           this.listData.push(catItem)
        //         }
        //       })
        //     }
        //     // 组织退出 组织冻结 组织解冻
        //     if (['ORGANIZATION_EXIT', 'ORGANIZATION_FORZEN', 'ORGANIZATION_THAW'].includes(this.rangeType)) {
        //       this.listDataAll.forEach(orgItem => {
        //         if (selectedItem.orgId == orgItem.orgId) {
        //           this.listData.push(orgItem)
        //         }
        //       })
        //     }
        //   })
        //   // if (selectedArr?.length) {
        //   //   this.bol = 1
        //   // }
        //   // 品类退出 品类冻结
        //   if (['CATEGORY_EXIT', 'CATEGORY_FORZEN', 'CATEGORY_THAW', 'CATEGORY_LIMIT_FLAG', 'CATEGORY_LIMIT_FLAG_REMOVE'].includes(this.rangeType)) {
        //     this.listData.forEach(catItem => {
        //       if (catItem.selected == 'Y') {
        //         this.$refs.category.toggleRowSelection(catItem)
        //         setTimeout(() => {
        //           this.$refs.category.doLayout()
        //         }, 100)
        //       }
        //     })
        //   }
        // })
      },
      deep: true
    },
    value: {
      immediate: true,
      deep: true,
      handler (newValue) {
        this.showOrgRecords = newValue || []
        let selectedArr = [] // 选中列
        newValue.forEach(datass => {
          if (datass.selected == 'Y' || this.rangeList.map(item => item.categoryCode).includes(datass.categoryCode)) {
            selectedArr.push(datass)
          }
        })
        this.$nextTick(() => {
          selectedArr.forEach(selectedItem => {
            if (selectedItem) {
              this.$refs.multipleTable.toggleRowSelection(selectedItem)
              setTimeout(() => {
                this.$refs.multipleTable.doLayout()
              }, 100)
            }
            // 品类退出 品类冻结 品类解冻
            if (['CATEGORY_EXIT', 'CATEGORY_FORZEN', 'CATEGORY_THAW', 'CATEGORY_LIMIT_FLAG', 'CATEGORY_LIMIT_FLAG_REMOVE'].includes(this.rangeType)) {
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
          // if (selectedArr?.length) {
          //   this.bol = 1
          // }
          // 品类退出 品类冻结
          if (['CATEGORY_EXIT', 'CATEGORY_FORZEN', 'CATEGORY_THAW', 'CATEGORY_LIMIT_FLAG', 'CATEGORY_LIMIT_FLAG_REMOVE'].includes(this.rangeType)) {
            this.listData.forEach(catItem => {
              if (catItem.selected == 'Y') {
                this.$refs.category.toggleRowSelection(catItem)
                setTimeout(() => {
                  this.$refs.category.doLayout()
                }, 100)
              }
            })
          }
        })
      }
    }
  },

  mounted () {

  },
  created () {},

  methods: {
    // 获取唯一key
    getRowKey (row, key) {
      return uniqueId(`${row[key]}_${Date.now()}`)
    },
    // 过滤公司
    filterHandler (queryForm) {
      this.queryForm = queryForm
      const {
        companyName
      } = queryForm || {}
      if (companyName) {
        const {
          pageSize,
          pageNum
        } = this.paginationInfo
        const targetList = this.listData.filter(item => item.orgName.includes(companyName))
        this.targetList = targetList
        this.listDataShow = targetList.slice((pageNum - 1) * pageSize, pageNum * pageSize)
      } else {
        this.targetList = this.listData
        this.listDataShow = this.targetList.slice(0, 10)
      }
    },
    filterOrgHandler (queryForm) {
      const {
        companyName
      } = queryForm || {}
      if (companyName) {
        const targetList = this.value.filter(item => item.orgName.includes(companyName))
        this.showOrgRecords = targetList
      } else {
        this.showOrgRecords = this.value
      }
    },
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
    handleCurrentChange (pageNum) {
      this.paginationInfo.pageNum = pageNum
      const pageSize = this.paginationInfo.pageSize
      this.listDataShow = this.targetList.slice((pageNum - 1) * pageSize, pageNum * pageSize)
    },
    // 每页条数变更
    sizeChange (pageSize) {
      this.paginationInfo.pageSize = pageSize
      const pageNum = this.paginationInfo.pageNum
      this.listDataShow = this.targetList.slice((pageNum - 1) * pageSize, pageNum * pageSize)
    },
    // 控制明细 change
    handleSelectionChange2 (val) {
      this.categoryList = val
      this.$emit('listData', val)
    },
    // 品类退出 组织退出 change
    handleSelectionChange (val) {
      const listDataAll = this.listDataAll
      let listData = []
      this.rangeList = val
      listDataAll.forEach(datas => {
        if (['CATEGORY_EXIT', 'CATEGORY_FORZEN', 'CATEGORY_THAW', 'CATEGORY_LIMIT_FLAG', 'CATEGORY_LIMIT_FLAG_REMOVE'].includes(this.rangeType)) {
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
      // this.$emit('listData', listData)
      // this.$emit('rangeChange', val)
    }
  }
}
</script>

<style scoped lang="scss">
:deep(.paginationStyle){
  float:right;
  margin-top:15px;
  margin-bottom: 15px;
}
.tab-form-style{
  border-top: 0;
}
</style>
