<template>
  <el-container
    class="flex-container-notab the_manage_level_wrapper"
    direction="vertical"
  >
    <el-main>
      <el-tabs
        v-model="activeName"
        @tab-click="handleClick"
      >
        <!-- 产品与服务配置 -->
        <el-tab-pane
          :label="$t('dataConfMod.baseServiceConfig')[0]"
          name="first"
          class="tablePd"
        >
          <div class="warp-tip">
            <div class="the_tetx">
              <!-- 产品与服务层级指企业管控采购分类和物料所属的采购类别层级，如：启用一级采购类别，表示企业管理物料及供应商按一级采购类别管控，多级采购分类同理类推。 -->
              <p>{{ $t('dataConfMod.baseServiceConfig')[1] }}</p>
              <!-- *请注意：此配置影响后续业务流程，配置后不可修改！ -->
              <h4 style="color:red;margin-bottom: 24px;margin-top: 16px;">
                {{ $t('dataConfMod.baseServiceConfig')[2] }}
              </h4>
            </div>
            <div>
              <el-button
                type="primary"
                :disabled="isSave"
                @click="saveItem"
              >
                {{ $t('common.save') }}
              </el-button>
            </div>
          </div>
          <el-table
            :data="tableData"
            style="width: 100%"
            border
            stripe
            :max-height="200"
          >
            <el-table-column
              type="index"
              width="50"
            />
            <!-- 产品与服务层级 -->
            <el-table-column
              prop="serviceLevel"
              :label="$t('dataConfMod.baseServiceLevel')"
              width="180"
              :show-overflow-tooltip="true"
              align="center"
            >
              <template slot-scope="scope">
                <!-- 请选择 -->
                <el-select
                  v-model="scope.row.serviceLevel"
                  :placeholder="$t('common.pleaseSelect')"
                >
                  <el-option
                    v-for="item in langList"
                    :key="item.dictItemCode"
                    :label="item.dictItemName"
                    :value="item.dictItemCode"
                  />
                </el-select>
              </template>
            </el-table-column>
            <!-- 生效日期 -->
            <el-table-column
              prop="activeDate"
              :label="$t('dataConfMod.startDate')"
              width="180"
              :show-overflow-tooltip="true"
              :formatter="(row, column, cellValue) => $parseTime(cellValue)"
              align="center"
            />
            <!-- 最后更新人 -->
            <el-table-column
              prop="lastUpdatedUserName"
              :label="$t('common.updatePeople')"
              width="180"
              :show-overflow-tooltip="true"
              align="center"
            />
            <!-- 最后更新时间 -->
            <el-table-column
              prop="lastUpdateDate"
              :label="$t('common.updateTime')"
              min-width="180"
              :show-overflow-tooltip="true"
              align="center"
              :formatter="(row, column, cellValue) => $parseTime(cellValue)"
            />
          </el-table>
        </el-tab-pane>
        <!-- 供方管理配置 -->
        <el-tab-pane
          :label="$t('dataConfMod.supplierConfig')[0]"
          name="second"
          class="tablePd"
        >
          <div class="warp-tip">
            <div class="the_tetx">
              <!-- 供应商管理按组织、采购类别、物料三个层级管控，供方管理配置可以对组织层和采购类别层进行选择管控。 -->
              <p>{{ $t('dataConfMod.supplierConfig')[1] }}</p>
              <!-- 1.如启用组织和采购类别控制：供应商须按组织和采购类别引入，下达采购订单等须满足供应商对应的组织和采购类别是有效状态； -->
              <p>{{ $t('dataConfMod.supplierConfig')[2] }}</p>
              <!-- 2.如只启用组织控制，表示按组织管理供应商，无须考虑对应的采购类别状态； -->
              <p>{{ $t('dataConfMod.supplierConfig')[3] }}</p>
              <!-- 3.如只启用采购类别控制，表示按采购类别即按品类管理供应商，不区分采购商组织。 -->
              <p>{{ $t('dataConfMod.supplierConfig')[4] }}</p>
              <!-- *请注意：此配置影响后续业务流程，配置后不可修改！ -->
              <h4 style="color:red;margin-bottom: 24px;margin-top: 16px;">
                {{ $t('dataConfMod.supplierConfig')[5] }}
              </h4>
            </div>
            <div>
              <!-- 保存 -->
              <el-button
                type="primary"
                :disabled="isSave2"
                @click="saveItem2"
              >
                {{ $t('common.save') }}
              </el-button>
            </div>
          </div>
          <el-table
            :data="tableData2"
            style="width: 100%"
            border
            stripe
            :max-height="200"
          >
            <el-table-column
              type="index"
              width="50"
            />
            <!-- 是否启用组织 -->
            <el-table-column
              prop="enableOrg"
              :label="$t('dataConfMod.enableOrg')"
              width="180"
              :show-overflow-tooltip="true"
              align="center"
            >
              <template slot-scope="scope">
                <el-checkbox
                  v-model="scope.row.enableOrg"
                  true-label="Y"
                  false-label="N"
                />
              </template>
            </el-table-column>
            <!-- 是否启用采购类别 -->
            <el-table-column
              prop="enableCategory"
              :label="$t('dataConfMod.enableCategory')"
              width="180"
              :show-overflow-tooltip="true"
              align="center"
            >
              <template slot-scope="scope">
                <el-checkbox
                  v-model="scope.row.enableCategory"
                  true-label="Y"
                  false-label="N"
                />
              </template>
            </el-table-column>
            <!-- 生效日期 -->
            <el-table-column
              prop="activeDate"
              :label="$t('dataConfMod.startDate')"
              width="180"
              :show-overflow-tooltip="true"
              align="center"
              :formatter="(row, column, cellValue) => $parseTime(cellValue)"
            />
            <!-- 最后更新人 -->
            <el-table-column
              prop="lastUpdatedUserName"
              :label="$t('common.updatePeople')"
              width="180"
              :show-overflow-tooltip="true"
              align="center"
            />
            <!-- 最后更新时间 -->
            <el-table-column
              prop="lastUpdateDate"
              :label="$t('common.updateTime')"
              min-width="180"
              :show-overflow-tooltip="true"
              align="center"
              :formatter="(row, column, cellValue) => $parseTime(cellValue)"
            />
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-main>
  </el-container>
</template>

<script>
export default {
  name: 'ManageLevelSetting',

  data () {
    return {
      isSave: true,
      isSave2: true,
      activeName: 'first',
      tableData: [],
      tableData2: [],
      langList: []
    }
  },
  created () {
    this.getList()
    this.getBaseConfig()
  },
  mounted () {
    // '即将进行【管理层级配置】，您需要完成：1、设置公司产品与服务控制层级；2、设置公司对供应商管理管控层级；' '提示' 开始'
    let mantLevelTip = localStorage.getItem('mantLevelTip') || 'Y'
    if (mantLevelTip === 'Y') {
      this.$confirm(
        this.$t('dataConfMod.manageLevelSettingAlert'),
        this.$t('common.tips'), {
          distinguishCancelAndClose: true,
          confirmButtonText: this.$t('common.start'),
          cancelButtonText: this.$t('common.toNotshowTip')
        }).then(() => {
        // 点击开始
      }).catch(() => {
        // 不再提示
        localStorage.setItem('mantLevelTip', 'N')
      })
    }
  },
  methods: {
    handleClick (tab) {
      if (tab.name == 'first') {
        this.getBaseConfig()
      } else if (tab.name == 'second') {
        this.getSupplierConfig()
      }
    },
    getList () {
      // 获取状态
      this.$http({
        url: '/api-base/dict/base-dict-item/listAllByDictCode',
        method: 'GET',
        params: { dictCode: 'SERVICELEVEL' }
      }).then(data => {
        this.langList = data.data
      })
    },
    getBaseConfig () {
      // 管理配置查询全部
      this.$http({
        url: '/api-base/serviceConfig/base-service-config/listAll',
        method: 'GET',
        params: {}
      }).then(data => {
        if (data && data.data && data.data.length > 0) {
          this.tableData = data.data
          this.isSave = !!data.data[0].serviceLevel
        }
      })
    },
    getSupplierConfig () {
      // 供方管理配置查询全部
      this.$http({
        url: '/api-base/serviceConfig/supplier-config/listAll',
        method: 'GET',
        params: {}
      }).then(data => {
        if (data && data.data && data.data.length > 0) {
          this.tableData2 = data.data
          this.isSave2 = !!(data.data[0].enableOrg || data.data[0].enableCategory)
        }
      })
    },
    saveItem () {
      if (!this.tableData || this.tableData.length !== 1) {
        // '请录入一条数据'
        this.$message.error(this.$t('common.pleaseEditOne'))
        return
      }
      this.$http({
        url: '/api-base/serviceConfig/base-service-config/saveForOne',
        method: 'POST',
        data: this.tableData[0]
      }).then(res => {
        this.$message({
          message: res.message,
          type: 'success'
        })
        this.isSave = true
        this.getBaseConfig() // 查询
      })
    },
    saveItem2 () {
      if (!this.tableData2 || this.tableData2.length !== 1) {
        // '请录入一条数据'
        this.$message.error(this.$t('common.pleaseEditOne'))
        return
      }
      this.$http({
        url: '/api-base/serviceConfig/supplier-config/saveForOne',
        method: 'POST',
        data: this.tableData2[0]
      }).then(res => {
        this.$message({
          message: res.message,
          type: 'success'
        })
        this.isSave2 = true
        this.getSupplierConfig() // 查询
      })
    }
  }
}
</script>
<style scoped lang="scss">
.the_manage_level_wrapper {
  .the_tetx {
        font-size: 14px; color:#666;
  }
  .warp-tip{
    // border-left: 1px solid #dfe4ed;
    // border-right: 1px solid #dfe4ed;
    padding: 0 0 16px 0;
    p{
      margin: 0;
      line-height: 22px;
    }
  }
}
</style>
