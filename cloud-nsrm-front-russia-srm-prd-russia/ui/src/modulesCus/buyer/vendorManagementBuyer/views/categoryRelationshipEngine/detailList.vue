<template>
  <el-container
    class="flex-container the_approvalFlowList_wrapper"
    direction="vertical"
  >
    <el-main>
      <el-container direction="vertical">
        <el-main style="flex-grow: 1;display: flex;flex-direction: column;position:relative;">
          <div class="tableForm">
            <el-table
              ref="flowTable"
              stripe
              border
              height="100%"
              :data="flowData"
              style="height: 100%;"
            >
              <el-table-column type="index" />
              <!-- 供应商编码 -->
              <el-table-column
                min-width="120"
                prop="companyCode"
                :label="$t('common.vendorCode')"
                sortable
                :formatter="(row, column, cellValue) => companyCode"
              />
              <!-- 供应商名称 -->
              <el-table-column
                min-width="150"
                prop="companyName"
                :label="$t('common.vendorName')"
                sortable
                :formatter="(row, column, cellValue) => companyName"
              />
              <!-- 公司 -->
              <el-table-column
                min-width="150"
                prop="orgName"
                :label="$t('cusEntry.vendorMod.orgName')"
                sortable
              />

              <!-- 品类全称 -->
              <el-table-column
                min-width="150"
                prop="categoryFullName"
                :label="$t('cusEntry.vendorMod.categoryFullName')"
                sortable
              />
              <!-- 品类 -->
              <el-table-column
                min-width="150"
                prop="categoryName"
                :label="$t('cusEntry.vendorMod.categoryLittle')"
                sortable
              />
              <!-- 单位状态 -->
              <el-table-column
                min-width="150"
                prop="pjOrgStatus"
                :label="$t('cusEntry.vendorMod.unitStatus')"
                sortable
                :formatter="(row, column, cellValue) => $getDictLabel('UNIT_STATUS', cellValue)"
              />

              <!-- 品类状态 -->
              <el-table-column
                min-width="150"
                prop="pjCategoryStatus"
                :label="$t('vendorMod.catServiceStatus')"

                sortable
                :formatter="(row, column, cellValue) => $getDictLabel('UNIT_STATUS', cellValue)"
              />
              <!-- 供应商品类状态 -->
              <el-table-column
                min-width="150"
                prop="serviceStatus"
                :label="$t('cusEntry.vendorMod.vendorCategoryStatus')"
                sortable
                :formatter="(row, column, cellValue) => $getDictLabel('CATEGORY_STATUS', cellValue)"
              />
              <!-- 更新时间 -->
              <el-table-column
                min-width="150"
                prop="lastUpdateDate"
                :label="$t('common.updateTime')"
                sortable
              />
            </el-table>
          </div>
        </el-main>
        <el-footer
          class="pageFooter"
        >
          <CPagination
            ref="queryPagination"
            class="c-query-table-pagination"
            :total="pageInfo.total"
            :page-num="pageInfo.pageNum"
            :page-size="pageInfo.pageSize"
            @current-change="handleCurrentChange"
            @size-change="handleSizeChange"
          />
        </el-footer>
      </el-container>
    </el-main>
  </el-container>
</template>
<script>
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import CPagination from 'lib@/components/c-pagination'
import { validateLowerCase } from '@/library/utils/validate'
import { tabTodoMixin } from '@/utils/mixins'
import { parseTime } from '@/utils'
import { transformMQL } from 'lib@/utils/util'
export default {
  name: 'ProcessTaskList',
  components: {
    CPagination,
    MainHeader,
    FormWrapper
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      gridId: 'list',
      queryParam: {
        pageNum: 1,
        pageSize: 15
      },
      flowData: [],
      pageInfo: {
        total: 0,
        pageNum: 1,
        pageSize: 15
      },
      companyCode: null,
      companyName: null
    }
  },
  mounted () {
    const { flag, row, query } = this.$attrs.params
    this.companyCode = row.companyCode
    this.companyName = row.companyName
    this.queryParam.companyId = row.companyId
    this.queryParam.categoryId = row.categoryId
    query.categoryId && (this.queryParam.categoryId = query.categoryId)
    query.orgIdList && (this.queryParam.orgIdList = query.orgIdList)
    query.serviceStatus && (this.queryParam.serviceStatus = query.serviceStatus)
    query.pjCategoryStatus && (this.queryParam.pjCategoryStatus = query.pjCategoryStatus)
    query.pjOrgStatus && (this.queryParam.pjOrgStatus = query.pjOrgStatus)
    this.getQuerydata()
  },
  methods: {
    getLabel (array) {
      let str = ''
      let index = array.length
      for (let item of array) {
        str += this.$getDictLabel('CATEGORY_STATE_STORE', item)
        --index
        if (index != 0) {
          str += ','
        }
      }
      return str
    },
    handleCurrentChange (num) {
      this.queryParam.pageNum = num
      this.getQuerydata(this.queryParam)
    },
    handleSizeChange (size) {
      this.queryParam.pageNum = 1
      this.queryParam.pageSize = size
      this.getQuerydata(this.queryParam)
    },
    getQuerydata () {
      this.$http({
        url: '/api-sup/pj/orgCategory/listPageDetailByHeader',
        method: 'POST',
        data: this.queryParam,
        loading: true
      }).then(res => {
        this.flowData = res.data.list || []
        this.pageInfo.total = res.data.total
        this.pageInfo.pageNum = res.data.pageNum
        this.pageInfo.pageSize = res.data.pageSize
      })
    },
    openTable (row) {

    }
  }
}
</script>
<style scoped lang="scss">
.tableForm {
  position: absolute;
  top: 0;
  bottom: 0;
  height: 100%;
  width: 100%;
  .el-table {
    height: 100%;
  }
}
.butHeader {
  padding: 10px;
}
.pageFooter {
  .c-pagination {
    // margin: 0 !important;
  }
}
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  .tabs {
    display: flex;
    .btn {
      font-size: 12px;
      padding: 0px 0 4px;
      font-weight: normal;
      height: 24px;
      line-height: 24px;
      cursor: pointer;
      box-sizing: content-box;
      color: #393E45;
      &.active {
        border-bottom: 2px solid  #0077FF;
        color:#0077FF;
        font-weight: bold;
      }
      &+.btn {
        margin-left: 32px;
      }
      &:hover{
        color: #0077FF;
      }
    }
  }
}
</style>
