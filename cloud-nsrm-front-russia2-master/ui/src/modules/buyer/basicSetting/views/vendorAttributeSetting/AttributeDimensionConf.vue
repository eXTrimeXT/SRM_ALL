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
                <!-- 为适应不同企业对供应商信息不同管控的需求，系统适应如下可定义： -->
                <p>{{ $t("dataConfMod.attributeDimensionConf")[1] }}</p>
                <!-- 1.是否注册填写：定义属性是否在供应商注册页面显示并需要填写； -->
                <p>{{ $t("dataConfMod.attributeDimensionConf")[2] }}</p>
                <!-- 2.由供应商/采购商变更：定义属性变更是由采购商/供应商发起变更； -->
                <p>{{ $t("dataConfMod.attributeDimensionConf")[3] }}</p>
                <!-- 3.是否需要变更流程：定义变更是否需要走流程； -->
                <p>{{ $t("dataConfMod.attributeDimensionConf")[4] }}</p>
                <!-- 4.是否资质审查确认：定义信息是否在资质审查单界面显示且允许编辑，银行信息和财务信息都默认不显示； -->
                <p>{{ $t("dataConfMod.attributeDimensionConf")[5] }}</p>
                <!-- 5.是否生效确认：定义信息是否在生效单界面显示且允许编辑，银行信息和财务信息都默认显示。 -->
                <p>{{ $t("dataConfMod.attributeDimensionConf")[6] }}</p>
                <!-- 6.属性管控维度：若按头层管控，信息应用时则不区分组织；若按地点层管控，信息应用时则区分组织； -->
                <p>{{ $t("dataConfMod.attributeDimensionConf")[7] }}</p>
                <!-- *请注意：财务信息和银行信息的管控维度配置影响后续数据规则，配置后不可修改！ -->
                <p style="color:red">
                  {{ $t("dataConfMod.attributeDimensionConf")[8] }}
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

                @click="saveHandle"
              >
                <!-- 保存 -->
                {{ $t("common.submit") }}
              </el-button>
            </srm-col>
          </srm-row>
        </div>
      </div>
      <div class="org-type-table-List tablePd">
        <el-table
          v-loading="listLoading"
          element-loading-background="rgba(0, 0, 0, 0.4)"
          :data="tableData"
          border
          style="width: 100%"
          max-height="450"
          @selection-change="selectionChange"
        >
          <el-table-column
            type="selection"
            align="center"
            width="50"
          />
          <!-- 属性维度 -->
          <el-table-column
            min-width="100px"
            :label="$t('dataConfMod.attributeDim')"
            align="center"
            prop="dimName"
          >
            <template slot-scope="{ row }">
              <span>{{ row.dimName }}</span>
            </template>
          </el-table-column>
          <!-- 顺序设置 -->
          <el-table-column
            min-width="100px"
            :label="$t('dataConfMod.sortSetting')"
            align="center"
            prop="orderNum"
          >
            <template slot-scope="{ row }">
              <el-input
                v-model="row.orderNum"
                class="edit-input"
              />
            </template>
          </el-table-column>
          <!-- 是否注册填写 -->
          <el-table-column
            min-width="110px"
            :label="$t('dataConfMod.isRegistInput')"
            align="center"
            prop="isRegist"
          >
            <template slot-scope="{ row }">
              <el-switch
                v-model="row.isRegist"
                active-value="Y"
                inactive-value="N"
              />
            </template>
          </el-table-column>
          <!-- 由供应商变更 -->
          <el-table-column
            min-width="110px"
            :label="$t('dataConfMod.isSupplyChange')"
            align="center"
            prop="isSupply"
          >
            <template slot-scope="{ row }">
              <el-switch
                v-model="row.isSupply"
                active-value="Y"
                inactive-value="N"
              />
            </template>
          </el-table-column>
          <!-- 由采购商变更 -->
          <el-table-column
            min-width="110px"
            :label="$t('dataConfMod.isBuyerChange')"
            align="center"
            prop="isBuyer"
          >
            <template slot-scope="{ row }">
              <el-switch
                v-model="row.isBuyer"
                active-value="Y"
                inactive-value="N"
              />
            </template>
          </el-table-column>
          <!-- 需要变更流程 -->
          <el-table-column
            min-width="110px"
            :label="$t('dataConfMod.isChangeFlow')"
            align="center"
            prop="isFlow"
          >
            <template slot-scope="{ row }">
              <el-switch
                v-model="row.isFlow"
                active-value="Y"
                inactive-value="N"
              />
            </template>
          </el-table-column>
          <!-- 是否资质审查确认 -->
          <el-table-column
            min-width="130px"
            :label="$t('dataConfMod.isAuditSure')"
            align="center"
            prop="isAudit"
          >
            <template slot-scope="{ row }">
              <el-switch
                v-model="row.isAudit"
                active-value="Y"
                inactive-value="N"
              />
            </template>
          </el-table-column>
          <!-- 是否生效确认 -->
          <el-table-column
            min-width="130px"
            :label="$t('dataConfMod.isUseSure')"
            align="center"
            prop="isUse"
          >
            <template slot-scope="{ row }">
              <el-switch
                v-model="row.isUse"
                active-value="Y"
                inactive-value="N"
              />
            </template>
          </el-table-column>
          <!-- 定义管控维度 -->
          <el-table-column
            min-width="110px"
            :label="$t('dataConfMod.defineDimType')[0]"
            align="center"
            prop="dimType"
          >
            <template slot-scope="{ row }">
              <el-select
                v-if="
                  row.dimCode === 'financeInfo' || row.dimCode === 'bankInfo'
                "
                v-model="row.dimType"
              >
                <!-- 头层 -->
                <el-option
                  :label="$t('dataConfMod.defineDimType')[1]"
                  value="HEAD"
                />
                <!-- 地点层 -->
                <el-option
                  :label="$t('dataConfMod.defineDimType')[2]"
                  value="LINE"
                />
              </el-select>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import { vendorAttributeSetting, vendorAttributeControl } from 'modb@/basicSetting/api/basicSetting'
export default {
  name: 'AttributeDimensionConf',
  mixins: [tabTodoMixin],
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
    // 获取数据
    fatchOrgTypeList () {
      this.listLoading = false
      vendorAttributeControl.getFieldDim().then(res => {
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
    // 新增
    addOne () {
      this.tableData.unshift({
        add: true,
        organizationTypeName: '',
        organizationTypeCode: ''
      })
    },
    // 保存新增数据
    saveHandle () {
      let addData = this.selectList
      if (addData.length < 1) {
        this.$message({
          message: this.$t('common.cannotSave'), // "请选择需要保存的数据"
          type: 'warning'
        })
        return false
      } else {
        vendorAttributeSetting.definitionDimData(addData).then(() => {
          this.fatchOrgTypeList()
        })
      }
    },
    // 删除
    delOne () {
      this.$refs[this.gridId].deleteFromView()
    },
    cancelEdit (row) {
      row.title = row.originalTitle
      row.edit = false
      this.$message({
        message: this.$t('common.cancelUpdate'), // "取消更新",
        type: 'warning'
      })
    },
    confirmEdit (row) {
      row.edit = false
      row.originalTitle = row.title
      this.$message({
        message: this.$t('common.successUpdate'), // "更新成功",
        type: 'success'
      })
    }
  }
}
</script>
<style scoped lang="scss">
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
    height: 220px;
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
