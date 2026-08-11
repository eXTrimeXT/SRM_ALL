<template>
  <el-container
    class="flex-container org-type-page"
    direction="vertical"
  >
    <el-main>
      <div class="warp-tip">
        <div class="org-type-header">
          <srm-row>
            <srm-col
              :xs="3"
              :sm="3"
              :md="3"
              :lg="3"
              :xl="3"
            >
              <div class="org-type-header-icon">
                <em class="el-icon-warning" />
              </div>
            </srm-col>
            <srm-col
              :xs="16"
              :sm="16"
              :md="16"
              :lg="16"
              :xl="16"
            >
              <div class="org-type-header-des">
                <!-- 本属性维度是指企业用于管控们供应商属性类别的一种划分，系统新增一个属性需要增加对应的数据表，因此尽量使用现有属性维度去扩充属性字段满足客户需求，减少开发量。 -->
                <p>{{ $t("dataConfMod.dimensionCtrlTips")[0] }}</p>
                <!-- *请注意：属性维度一旦新增后将不允许修改，只能新增启用其他属性维度！ -->
                <p style="color:red">
                  {{ $t("dataConfMod.dimensionCtrlTips")[1] }}
                </p>
              </div>
            </srm-col>
          </srm-row>
        </div>
        <div class="btn-area">
          <srm-row>
            <srm-col :init-col="1">
              <el-button
                type="primary"
                :disabled="selectList.length == 0"
                @click="saveHandle"
              >
                <!-- 保存 -->
                {{ $t("common.submit") }}
              </el-button>

              <el-button
                type="primary"
                @click="addHandle"
              >
                <!-- 新增 -->
                {{ $t("common.add") }}
              </el-button>
            </srm-col>
          </srm-row>
        </div>
      </div>
      <div class="org-type-table-List tablePd">
        <el-table
          v-loading="listLoading"
          :data="tableData"
          border
          style="width: 100%"
          max-height="450"
          @selection-change="selectionChange"
        >
          <el-table-column
            type="selection"
            width="50"
          />
          <el-table-column
            type="index"
            width="55"
          />
          <!-- 属性维度 -->
          <el-table-column
            min-width="80px"
            :label="$t('dataConfMod.attributeDim')"
            prop="dimName"
          >
            <template slot-scope="{ row }">
              <template v-if="row.edit || row.add">
                <el-input
                  v-model="row.dimName"
                  class="edit-input"
                />
              </template>
              <span v-else>{{ row.dimName }}</span>
            </template>
          </el-table-column>
          <!-- 属性维度编码 -->
          <el-table-column
            min-width="80px"
            :label="$t('dataConfMod.dimCode')"
            prop="dimCode"
          >
            <template slot-scope="{ row }">
              <template v-if="row.add">
                <el-input
                  v-model="row.dimCode"
                  class="edit-input"
                />
              </template>
              <span v-else>{{ row.dimCode }}</span>
            </template>
          </el-table-column>
          <!-- 序号设置 -->
          <el-table-column
            min-width="80px"
            :label="$t('dataConfMod.sortSetting')"
            prop="orderNum"
          >
            <template slot-scope="{ row }">
              <template v-if="row.add">
                <el-input
                  v-model="row.orderNum"
                  class="edit-input"
                />
              </template>
              <span v-else>{{ row.orderNum }}</span>
            </template>
          </el-table-column>
          <!-- 更新人 -->
          <el-table-column
            min-width="80px"
            :label="$t('common.updatePeople')"
            prop="lastUpdatedUserName"
          >
            <template slot-scope="{ row }">
              <span>{{ row.lastUpdatedUserName }}</span>
            </template>
          </el-table-column>
          <!-- 更新时间 -->
          <el-table-column
            min-width="80px"
            :label="$t('common.updateTime')"
            prop="lastUpdateDate"
          >
            <template slot-scope="{ row }">
              <span>{{ $parseTime(row.lastUpdateDate) }}</span>
            </template>
          </el-table-column>
          <el-table-column
            min-width="80px"
            :label="$t('dataConfMod.dataType')"
            prop="dimShowType"
          >
            <template slot-scope="{ row }">
              <span>{{ row.dimShowType?(row.dimShowType=='FORM'? $t('dataConfMod.menu'):$t('dataConfMod.table')):'' }}</span>
            </template>
          </el-table-column>
          <!-- 操作 -->
          <el-table-column
            align="center"
            :label="$t('common.operation')"
            width="200"
            fixed="right"
          >
            <template slot-scope="{ row }">
              <!-- 删除 -->
              <el-button
                v-if="row.originalDimFlag == 'N'"
                type="text"
                @click="deleteEdit(row)"
              >
                {{ $t("components.userSelection.delete") }}
              </el-button>

              <!-- 编辑 -->
              <el-button
                v-if="row.originalDimFlag == 'N'"
                type="text"
                @click="rowEdit(row)"
              >
                {{ $t('common.edit') }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <srm-dialog
        :title="$t('dataConfMod.attributeManage')"
        :visible.sync="dialogFormVisible"
        :close-on-click-modal="false"
        size="middle"
      >
        <el-form
          :rules="rules"
          :model="attributeDimension"
        >
          <srm-row>
            <srm-col :init-col="2">
              <!-- 属性维度 -->
              <el-form-item
                :label="$t('dataConfMod.attributeDim')"
                prop="dimName"
              >
                <el-input v-model="attributeDimension.dimName" />
              </el-form-item>
            </srm-col>
            <srm-col :init-col="2">
              <!-- 属性编码 -->
              <el-form-item
                :label="$t('dataConfMod.attributeCode')"
                prop="dimCode"
              >
                <el-input
                  v-model="attributeDimension.dimCode"
                  :disabled="bolEide"
                />
              </el-form-item>
            </srm-col>
            <srm-col :init-col="2">
              <!-- 序号设置 -->
              <el-form-item
                :label="$t('dataConfMod.sortSetting')"
                prop="orderNum"
              >
                <el-input v-model="attributeDimension.orderNum" />
              </el-form-item>
            </srm-col>
            <srm-col :init-col="2">
              <!-- 维度展示类型 -->
              <el-form-item
                :label="$t('dataConfMod.showType')"
                prop="dimShowType"
              >
                <el-select
                  v-model="attributeDimension.dimShowType"
                  :placeholder="$t('common.pleaseSelect')"
                  :disabled="bolEide"
                >
                  <el-option
                    v-for="item in showTypeAll"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </el-form-item>
            </srm-col>
            <srm-col :init-col="3">
              <!-- 是否启用 -->
              <el-form-item
                :label="$t('dataConfMod.enabledUse')"
                prop="enableFlag"
              >
                <el-switch
                  v-model="attributeDimension.enableFlag"
                  :active-text="$t('common.yes')"
                  :inactive-text="$t('common.no')"
                  active-value="Y"
                  inactive-value="N"
                />
              </el-form-item>
            </srm-col>
          </srm-row>
        </el-form>
        <div
          slot="footer"
          class="dialog-footer"
        >
          <el-button @click="dialogFormVisible = false">
            <!-- 取 消 -->
            {{ $t("common.cancel") }}
          </el-button>
          <el-button
            type="primary"
            @click="comfirmSave"
          >
            <!-- 确 定 -->
            {{ $t("common.confirm") }}
          </el-button>
        </div>
      </srm-dialog>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import { vendorAttributeControl } from 'modb@/basicSetting/api/basicSetting'
export default {
  name: 'AttributeDimension',
  mixins: [tabTodoMixin],
  data () {
    return {
      fieldDataModel: {
        fieldDataform: {
          fieldLength: '123'
        }
        // rules: {}
      },
      showTypeAll: [
        {
          value: 'FORM',
          label: this.$t('dataConfMod.menu')
        },
        {
          value: 'TABLE',
          label: this.$t('dataConfMod.table')
        }
      ],
      bolEide: false,
      attributeDimension: {
        dimName: '',
        dimCode: '',
        dimId: '',
        orderNum: '',
        dimShowType: '',
        enableFlag: 'Y'
      },
      rules: {
        orderNum: [{ required: true, message: this.$t('dataConfMod.requiredFields'), trigger: 'blur' }],
        dimCode: [{ required: true, message: this.$t('dataConfMod.requiredFields'), trigger: 'blur' }],
        dimName: [{ required: true, message: this.$t('dataConfMod.requiredFields'), trigger: 'blur' }],
        dimShowType: [{ required: true, message: this.$t('dataConfMod.requiredFields'), trigger: 'blur' }]
      },
      dialogFormVisible: false,
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
    // 点击新增
    addHandle () {
      this.attributeDimension.dimName = ''
      this.attributeDimension.dimCode = ''
      this.attributeDimension.orderNum = ''
      this.attributeDimension.dimShowType = 'TABLE'
      this.attributeDimension.enableFlag = 'Y'
      this.attributeDimension.dimId = ''
      this.bolEide = false
      this.dialogFormVisible = true
    },
    // 新增后弹窗点击确认
    comfirmSave () {
      let postData = this.attributeDimension
      if (this.bolEide == false) {
        vendorAttributeControl.addFieldDim(postData).then(() => {
          this.dialogFormVisible = false
          this.fatchOrgTypeList()
        })
      } else {
        vendorAttributeControl.editFieldDim(postData).then(() => {
          this.dialogFormVisible = false
          this.fatchOrgTypeList()
        })
      }
    },
    // 获取数据
    fatchOrgTypeList () {
      this.listLoading = false
      let obj = { pageNum: 1, pageSize: 50 }
      vendorAttributeControl.getFieldDim(obj).then(res => {
        if (res.data) {
          this.tableData = res.data
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
        vendorAttributeControl.updateDimBasicData(addData).then((res) => {
          if (res.code == '0') {
            this.$message.success(res.message)
          }
          this.fatchOrgTypeList()
        })
      }
    },
    // 编辑
    rowEdit (row) {
      this.attributeDimension.dimName = row.dimName
      this.attributeDimension.dimCode = row.dimCode
      this.attributeDimension.orderNum = row.orderNum
      this.attributeDimension.dimShowType = row.dimShowType
      this.attributeDimension.enableFlag = row.enableFlag
      this.attributeDimension.dimId = row.dimId
      this.bolEide = true
      this.dialogFormVisible = true
    },
    // 删除
    deleteEdit (row) {
      let thisId = { id: row.dimId }
      vendorAttributeControl.deleteFieldDim(thisId).then(() => {
        this.fatchOrgTypeList()
        this.$message({
          message: this.$t('common.successDelete')
        })
      })
    },
    cancelEdit (row) {
      row.title = row.originalTitle
      row.edit = false
      this.$message({
        message: this.$t('common.cancelUpdate'), // '取消更新',
        type: 'warning'
      })
    },
    confirmEdit (row) {
      row.edit = false
      row.originalTitle = row.title
      this.$message({
        message: this.$t('common.successUpdate'), // '更新成功',
        type: 'success'
      })
    }
  }
}
</script>
<style scoped lang="scss">
body .el-scrollbar__wrap {
  height: 100% !important;
}
.org-type-page {
  .org-type-header {
    &-icon {
      text-align: right;
      padding-top: 20px;
      // padding-right: 26px;
      i {
        font-size: 30px;
        color: #409eff;
      }
    }
    &-des {
      font-size: 14px;
      padding-bottom: 15px;
      p {
        margin: 0;
        line-height: 24px;
      }
    }
    height: 100px;
    padding-top: 10px;
  }
  .btn-area {
  }
  .org-type-table-List {
    padding-bottom: 20px;
    flex: 1;
  }
  .warp-tip {
    // border-left: 1px solid #dfe4ed;
    // border-right: 1px solid #dfe4ed;
    padding: 10px;
    p {
      margin: 0;
      line-height: 22px;
    }
  }
}
</style>
