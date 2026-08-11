<template>
  <el-container
    class="flex-container org-type-page"
    direction="vertical"
  >
    <el-main>
      <div class="warp-tip">
        <div class="org-type-header">
          <el-row type="flex">
            <el-col :span="3">
              <div class="org-type-header-icon">
                <em class="el-icon-warning" />
              </div>
            </el-col>
            <el-col :span="16">
              <div class="org-type-header-des">
                <!-- 组织类型是指企业用于管控组织架构层级的一种划分，请根据企业目前的组织架构层级划分启用组织层级，也可预留未来企业的发展需求所用层级。下面实例可参考： -->
                <p>{{ $t('dataConfMod.orgTypeTab')[0] }}</p>
                <!-- 1.实例A企业：集团-事业部-公司-业务实体-库存组织； -->
                <p>{{ $t('dataConfMod.orgTypeTab')[1] }}</p>
                <!-- 2.实例B企业：集团-采购组织-公司-业务实体-库存组织； -->
                <p>{{ $t('dataConfMod.orgTypeTab')[2] }}</p>
                <!-- 3.实例C企业：公司-采购组织-业务实体-库存组织。 -->
                <p>{{ $t('dataConfMod.orgTypeTab')[3] }}</p>
                <!-- *请注意：此设置影响后续功能，组织类型一旦新增后将不允许修改，只能新增启用其他组织类型！ -->
                <p style="color:red">
                  {{ $t('dataConfMod.orgTypeTab')[4] }}
                </p>
              </div>
            </el-col>
          </el-row>
        </div>
        <!-- <div class="btn-area">
          <el-row type="flex">
            <el-col :span="24">
              <el-button
                type="primary"

                @click="addOne"
              >
                {{ $t('common.add') }}
              </el-button>
              <el-button

                :disabled="selectList.length == 0"
                @click="saveHandle"
              >
                {{ $t('common.submit') }}
              </el-button>
            </el-col>
          </el-row>
        </div> -->
      </div>
      <div class="org-type-table-List tablePd">
        <el-table
          ref="orgTypeTable"
          v-loading="listLoading"
          element-loading-background="rgba(0, 0, 0, 0.4)"
          :data="tableData"
          border
          style="width: 100%"
          max-height="390"
          @selection-change="selectionChange"
          @cell-click="cellClick"
        >
          <el-table-column
            type="selection"
            width="50"
          />
          <el-table-column
            type="index"
            width="55"
          />
          <!-- 组织类型名称 -->
          <el-table-column
            min-width="80px"
            :label="$t('dataConfMod.orgTypeName')"
            prop="organizationTypeName"
          >
            <template slot-scope="scope">
              <!-- <el-input
                v-if="scope.row.editType==='add' || scope.row.editType==='edit'"
                v-model="scope.row.organizationTypeName"
                class="edit-input"
              /> -->
              <span>{{ scope.row.organizationTypeName }}</span>
            </template>
          </el-table-column>
          <!-- 组织类型编码 -->
          <el-table-column
            min-width="80px"
            :label="$t('dataConfMod.orgTypeCode')"
            prop="organizationTypeCode"
          >
            <template slot-scope="scope">
              <!-- <el-input
                v-if="scope.row.editType==='add'"
                v-model="scope.row.organizationTypeCode"
                class="edit-input"
              /> -->
              <span>{{ scope.row.organizationTypeCode }}</span>
            </template>
          </el-table-column>
          <!-- 更新人 -->
          <el-table-column
            min-width="80px"
            :label="$t('common.updatePeople')"
            prop="lastUpdatedUserName"
          >
            <template slot-scope="scope">
              <span>{{ scope.row.lastUpdatedUserName }}</span>
            </template>
          </el-table-column>
          <!-- 更新时间 -->
          <el-table-column
            min-width="80px"
            :label="$t('common.updateTime')"
            prop="lastUpdateDate"
          >
            <template slot-scope="scope">
              <span>{{ $parseTime(scope.row.lastUpdateDate) }}</span>
            </template>
          </el-table-column>
          <!-- 操作 -->
          <el-table-column
            align="center"
            :label="$t('common.operation')"
            width="160"
            fixed="right"
          >
            <template slot-scope="scope">
              <el-button
                v-if="scope.row.editType ==='add'"
                type="text"
                @click="handleDelete(scope.$index, scope.row)"
              >
                <!-- 删除 -->
                {{ $t('common.delete') }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import { organizationSetting } from 'modb@/basicSetting/api/basicSetting'
export default {
  name: 'OrgType',
  mixins: [ tabTodoMixin ],
  data () {
    return {
      listLoading: true,
      isDisabled: this.$attrs.params.flag === 'edit',
      gridId: 'orgTypeList',
      pageSize: 15,
      dataCount: 0,
      queryTotal: -1,
      all: -1,
      tableData: [],
      showDialog: false,
      tableHeader: [],
      selectList: [],
      currentRow: ''
    }
  },
  created () {
    this.fatchOrgTypeList() // 获取列表数据
  },
  methods: {
    cellClick (row) {
      this.$refs.orgTypeTable.toggleRowSelection(row, true)
    },
    // 获取数据
    fatchOrgTypeList () {
      organizationSetting.getOrgTypeList().then(res => {
        if (res.data) {
          this.tableData = res.data.map(i => ({ ...i, editType: 'edit' }))
          this.listLoading = false
        }
      })
    },
    // 选择项变化
    selectionChange (selection) {
      this.selectList = selection
    },
    handleCurrentChange (row) {
      this.currentRow = row
    },
    // 新增
    addOne () {
      this.tableData.unshift({
        editType: 'add',
        organizationTypeName: '',
        organizationTypeCode: ''
      })
    },
    // 保存新增数据
    saveHandle () {
      let addData = this.selectList
      if (addData.length < 1) {
        this.$message({
          message: this.$t('common.cannotSave'), // '请选择需要保存的数据'
          type: 'warning'
        })
        return false
      } else {
        organizationSetting.addOrgType(addData).then(res => {
          this.$message({
            message: res.message,
            type: 'success'
          })
          this.fatchOrgTypeList()
        })
      }
    },
    // 删除
    handleDelete (index) {
      this.tableData.splice(index, 1)
    },
    handleEdit (index) {
      this.tableData[index].editType = 'edit'
      this.$nextTick(() => {
        this.$refs.orgTypeTable.doLayout()
      })
    },
    cancelEdit (row) {
      row.title = row.originalTitle
      row.edit = false
      this.$message({
        message: this.$t('common.cancelUpdate'), // '取消更新'
        type: 'warning'
      })
    },
    confirmEdit (row) {
      row.edit = false
      row.originalTitle = row.title
      this.$message({
        message: this.$t('common.successUpdate'), // '更新成功'
        type: 'success'
      })
    }
  }
}
</script>
<style scoped lang="scss">
.org-type-page{
  .org-type-header{
    .org-type-header-icon{
      text-align: right;
      padding-top: 20px;
      padding-right: 26px;
      em{
        font-size: 30px;
        color: #409eff;
      }
    }
    .org-type-header-des{
      font-size:14px;
      padding-bottom: 15px;
      p{
        margin: 0;
        line-height: 24px;
      }
    }
    height: 166px;
    padding-top: 10px;
  }
  .org-type-table-List{
    padding-bottom: 20px;
    flex: 1;
  }
  .warp-tip{
    padding: 10px;
    p{
      margin: 0;
      line-height: 22px;
    }
  }
}
</style>
