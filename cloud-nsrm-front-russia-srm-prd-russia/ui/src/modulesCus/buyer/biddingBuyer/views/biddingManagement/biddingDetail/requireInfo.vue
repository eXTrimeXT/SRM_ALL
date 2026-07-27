<template>
  <!-- 报价信息 -->
  <el-form :model="projectRequirementsForm">
    <div style="margin: 20px 0 10px 0">
      <!--编制报价模板-->
      <el-button
        type="primary"
        :disabled="readonlyNew"
        @click="openQuotaTemplate"
      >
        {{ $t("cusEntry.bidMod.doQuotaTemplate") }}
      </el-button>
      <el-button
        type="primary"
        :disabled="readonlyNew"
        @click="addRow"
      >
        {{ $t("common.add") }}
      </el-button>
      <!-- 导入报价模板 -->
      <MImport
        type="default"
        :title="$t('cusEntry.bidMod.importQuotaTemplate')"
        up-load-url="/api-sou/ext/buyer/bid/init/importPriceExcel"
        :extra-data="extraData"
        :disabled="readonlyNew"
        @downloadTemplate="downloadTemplate"
        @handleSuccess="handleSuccess"
      />
      <!-- 更新报价单按钮展示: 招标流程=标准招标 && 已技术评分状态-->
      <el-button
        v-if="biddingBaseInfo.extSouProcess==='STANDARD' && biddingBase.projectStatus==='TECH_BID_EVA_DONE' && updateDataVisible"
        type="primary"
        @click="updateData"
      >
        更新报价单
      </el-button>
      <el-button
        v-if="!updateDataVisible"
        type="primary"
        @click="saveRequirement"
      >
        {{ $t('common.save') }}
      </el-button>
      <el-button
        v-if="!updateDataVisible"
        type="primary"
        @click="cancelUpdate"
      >
        {{ $t('common.cancel') }}
      </el-button>
    </div>

    <!-- 表格 -->
    <el-table
      :key="itemListKey"
      border
      :data="itemList"
      style="width: 100%"
    >
      <el-table-column
        align="center"
        type="index"
        fixed="left"
        :label="$t('common.sort')"
        width="50"
      />
      <el-table-column
        v-if="biddingBase.mergeFlag"
        align="center"
        prop="extPackageName"
        :label="$t('cusEntry.biddingSettings.bagName')"
        :render-header="_addStarToColumn"
        show-overflow-tooltip
        min-width="150px"
      >
        <template slot-scope="scope">
          <el-select v-model="scope.row.extPackageName" :disabled="readonlyNew">
            <el-option
              v-for="(item,index) in packNameList"
              :key="index"
              :label="item"
              :value="item"
            />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column
        v-for="(item,index) in templateDataRight"
        :key="item.columnCode"
        :prop="item.columnCode"
        :label="item.columnName"
        align="center"
        min-width="150"
        :render-header="item.starFlag ? _addStarToColumn : null"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <el-input
            v-if="templateDataRight[index].inputFlag && templateDataRight[index].columnType == 'STRING'"
            v-model="scope.row[item.columnCode]"
            :disabled="readonlyNew"
          />
          <el-input-number
            v-else-if="templateDataRight[index].inputFlag && templateDataRight[index].columnType == 'NUMBER'"
            v-model="scope.row[item.columnCode]"
            :disabled="readonlyNew"
            style="width:100%"
            :controls="false"
            :min="0"
          />
          <dict-select
            v-else-if="templateDataRight[index].columnType == 'LIST'"
            v-model="scope.row[item.columnCode]"
            :code="templateDataRight[index].code"
            :disabled="readonlyNew || !templateDataRight[index].inputFlag"
          />
          <span v-else>{{ scope.row[item.columnCode] }}</span>
        </template>
      </el-table-column>
      <el-table-column
        v-if="!readonlyNew"
        :label="$t('common.operation')"
        fixed="right"
        width="80px"
      >
        <template slot-scope="scope">
          <el-button type="text" @click="deleteRow(scope.$index)">
            {{ $t('common.delete') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <srm-dialog
      :visible.sync="quotaTemplateVisible"
      :title="$t('cusEntry.bidMod.doQuotaTemplate')"
      size="large"
      append-to-body
      :close-on-click-modal="false"
    >
      <div class="template-body">
        <!-- 左边 -->
        <div class="template-body-left">
          <div class="title-style">
            {{ $t('cusEntry.bidMod.alternateField') }}
          </div>
          <el-table
            border
            style="width: 465px"
            :data="templateDataLeft"
            @selection-change="selectionChangeLeft"
          >
            <el-table-column
              align="center"
              type="selection"
              width="55"
            />
            <el-table-column
              align="center"
              prop="columnName"
              :label="$t('cusEntry.bidMod.columnName')"
              minWidth="100"
            />
            <el-table-column
              align="center"
              prop="columnType"
              :label="$t('cusEntry.bidMod.columnType')"
              :formatter="(row, column, val) => val ? $getDictLabel('SOU_BID_PRICE_COLTYPE', val) : ''"
              minWidth="100"
            />
            <el-table-column
              align="center"
              prop="columnSource"
              :label="$t('cusEntry.bidMod.columnSource')"
              :formatter="(row, column, val) => val ? $getDictLabel('SOU_BID_PRICE_COLSOURCE', val) : ''"
              minWidth="100"
            />
            <el-table-column
              align="center"
              prop="colnmnInput"
              :label="$t('cusEntry.bidMod.columnInput')"
              :formatter="(row, column, val) => val ? $getDictLabel('YES_OR_NO', val) : ''"
              minWidth="100"
            />
          </el-table>
        </div>
        <!-- 中间操作按钮 -->
        <div class="template-body-center">
          <div>
            <el-button
              type="primary"
              icon="el-icon-arrow-right"
              :disabled="leftSelected.length==0"
              @click="selectedAdd"
            >
              {{ $t("components.common.new") }}
            </el-button>
          </div>
          <div>
            <el-button
              type="primary"
              icon="el-icon-arrow-left"
              style="margin-top: 16px;"
              :disabled="rightSelected.length==0"
              @click="selectedDel"
            >
              {{ $t("components.common.delete") }}
            </el-button>
          </div>
        </div>
        <!-- 右边选中内容 -->
        <div class="template-body-right">
          <div class="title-style">
            {{ $t('cusEntry.bidMod.quotaTemplateField') }}
          </div>
          <el-table
            border
            style="width: 465px"
            :data="templateDataRight"
            @selection-change="selectionChangeRight"
          >
            <el-table-column
              align="center"
              type="selection"
              width="55"
            />
            <el-table-column
              align="center"
              prop="columnName"
              :label="$t('cusEntry.bidMod.columnName')"
              minWidth="100"
            />
            <el-table-column
              align="center"
              prop="columnType"
              :label="$t('cusEntry.bidMod.columnType')"
              :formatter="(row, column, val) => val ? $getDictLabel('SOU_BID_PRICE_COLTYPE', val) : ''"
              minWidth="100"
            />
            <el-table-column
              align="center"
              prop="columnSource"
              :label="$t('cusEntry.bidMod.columnSource')"
              :formatter="(row, column, val) => val ? $getDictLabel('SOU_BID_PRICE_COLSOURCE', val) : ''"
              minWidth="100"
            />
            <el-table-column
              align="center"
              prop="colnmnInput"
              :label="$t('cusEntry.bidMod.columnInput')"
              :formatter="(row, column, val) => val ? $getDictLabel('YES_OR_NO', val) : ''"
              minWidth="100"
            />
          </el-table>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="quotaTemplateVisible = false">
          {{ $t("common.close") }}
        </el-button>
        <el-button
          type="primary"
          @click="confirmQuotaTemplate"
        >
          {{ $t("components.common.confirm") }}
        </el-button>
      </div>
    </srm-dialog>
  </el-form>
</template>

<script>
import { bidBuyerHttp } from 'modcb@/biddingBuyer/api'
import QuickSearch from 'lib@/components/QuickSearch'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'

export default {
  name: 'RequireInfo',

  components: {
    QuickSearch,
    MImport
  },

  props: {
    biddingBase: {
      type: Object,
      default: () => ({})
    },
    packNameList: {
      type: Array,
      default: () => []
    },
    isActiveMenu: {
      type: Boolean,
      required: true
    },
    pricingType: {
      type: Object,
      required: true
    },
    readonly: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      itemListKey: 0,
      updateDataVisible: true,
      quotaTemplateVisible: false,
      extraData: {
        uploadType: 'DEF',
        sourceType: 'WEB_APP',
        fileModular: 'bid',
        fileFunction: 'commonFile',
        fileType: 'excel'
      },
      templateDataLeft: [],
      templateDataRight: [],
      leftSelected: [],
      rightSelected: [],
      // 组合1必选
      groupNumListMust: ['数量/工程量', '含税单价（万元）', '含税总价（万元）'],
      // 组合1
      groupNumList: ['数量/工程量', '含税单价（万元）', '含税总价（万元）', '未税单价（万元）', '未税总价（万元）'],
      // 组合2必选
      groupTempNumListMust: ['暂定数量/工程量', '固定含税单价（万元）', '暂定含税总价（万元）'],
      // 组合2
      groupTempNumList: ['暂定数量/工程量', '固定含税单价（万元）', '暂定含税总价（万元）', '固定未税单价（万元）', '暂定未税总价（万元）'],
      projectRequirementsForm: {},
      itemList: []
    }
  },

  computed: {
    biddingBaseInfo: {
      get: function () {
        return this.biddingBase
      },
      set: function (val) {
        this.$emit('update:biddingBase', val)
      }
    }
  },

  watch: {
    isActiveMenu: {
      async handler (val) {
        if (val) {
          this.readonlyNew = this.readonly
          this.updateDataVisible = true
          this.extraData.businessId = this.biddingBaseInfo.projectId
          await this.getProjectRequirementsData()
        }
      },
      immediate: true
    }
  },

  methods: {
    selectionChangeLeft (val) {
      this.leftSelected = val
    },
    selectionChangeRight (val) {
      this.rightSelected = val
    },
    // 添加
    selectedAdd () {
      // 模板字段校验
      let groupNumFlag = false
      let groupTempNumFlag = false
      let rightRemove = []
      let leftSelected = this.leftSelected.map(item => item.columnName)
      this.leftSelected.map(item => {
        if (this.groupNumList.includes(item.columnName)) {
          groupNumFlag = true
        }
        if (this.groupTempNumList.includes(item.columnName)) {
          groupTempNumFlag = true
        }
      })
      // 同时选中组合1、组合2字段
      if (groupNumFlag && groupTempNumFlag) {
        this.$message.warning('组合1与组合2中的字段不可同时选中')
        return
      }
      // 选中组合1 groupNumList
      if (groupNumFlag && !groupTempNumFlag) {
        leftSelected = [...this.groupNumList, ...leftSelected]
        rightRemove = this.groupTempNumList // 如果右边存在组合2 移除右边 加到左边
      }
      // 选中组合2 groupTempNumList
      if (!groupNumFlag && groupTempNumFlag) {
        leftSelected = [...this.groupTempNumList, ...leftSelected]
        rightRemove = this.groupNumList // 如果右边存在组合1 移除右边 加到左边
      }
      // 组合1与组合2字段不可同时存在，以选中的组合作为替换
      !!rightRemove && rightRemove.forEach(item => {
        // 如果右边包含需要移除的内容
        if (this.templateDataRight.some(v => v.columnName == item)) {
          let hasIndex = this.templateDataLeft.findIndex(i => (i.columnName == item))
          if (hasIndex < 0) {
            let column = this.templateDataRight.find(i => (i.columnName == item))
            this.templateDataLeft.push(column) // 左边添加
          }
          let index = this.templateDataRight.findIndex(v => (v.columnName == item))
          if (index > -1) {
            this.templateDataRight.splice(index, 1) // 右边删除
          }
        }
      })
      // 正常添加选中的字段，没有涉及组合1 组合2
      leftSelected.forEach(item => {
        let hasIndex = this.templateDataRight.findIndex(i => (i.columnName == item))
        if (hasIndex < 0) {
          let column = this.templateDataLeft.find(i => (i.columnName == item))
          this.templateDataRight.push(column) // 右边添加
        }
        let index = this.templateDataLeft.findIndex(v => (v.columnName == item))
        if (index > -1) {
          this.templateDataLeft.splice(index, 1) // 左边删除
        }
      })
    },
    // 删除
    selectedDel () {
      let rightSelected = this.rightSelected.map(i => i.columnName)
      this.rightSelected.map(item => {
        if (this.groupNumListMust.includes(item.columnName)) {
          rightSelected = [...this.groupNumList, ...rightSelected]
        }
        if (this.groupTempNumListMust.includes(item.columnName)) {
          rightSelected = [...this.groupTempNumList, ...rightSelected]
        }
      })

      rightSelected.forEach(item => {
        let hasIndex = this.templateDataLeft.findIndex(i => (i.columnName == item))
        if (hasIndex < 0) {
          let column = this.templateDataRight.find(i => (i.columnName == item))
          this.templateDataLeft.push(column) // 左边添加
        }
        let index = this.templateDataRight.findIndex(v => (v.columnName == item))
        if (index > -1) {
          this.templateDataRight.splice(index, 1) // 右边删除
        }
      })
    },
    // 确认
    async confirmQuotaTemplate () {
      const params = {
        projectId: this.biddingBaseInfo.projectId,
        alternativeList: this.templateDataLeft,
        selectedList: this.templateDataRight
      }
      const response = await bidBuyerHttp.init.confirmQuotaTemplate(params)
      if (response && response.data) {
        this.$message.success(this.$t('common.success'))
        this.quotaTemplateVisible = false
        this.getProjectRequirementsData()
      }
    },
    //  打开编制报价模板
    async openQuotaTemplate () {
      const response = await bidBuyerHttp.init.getQuotaTemplateInfo(this.biddingBaseInfo.projectId)
      if (response && response.data) {
        this.templateDataLeft = response.data.alternativeList
        this.templateDataRight = response.data.selectedList
        this.quotaTemplateVisible = true
        // 再调用接口是为了不点击确定时，新增也能编辑
        this.getProjectRequirementsData()
      }
    },

    // 获取报价信息
    async getProjectRequirementsData () {
      // 先查询动态列
      const res = await bidBuyerHttp.init.getQuotaTemplateInfo(this.biddingBaseInfo.projectId)
      if (res && res.data) {
        this.templateDataLeft = res.data.alternativeList
        this.templateDataRight = res.data.selectedList.map(item => {
          // 采购商输入
          if (item.columnSource == 'BUYER' && item.colnmnInput == 'Y') {
            item.inputFlag = true
          } else {
            item.inputFlag = false
          }
          // 带星必填项: 合并招标包名、名称、单位、暂定数量/工程量、数量/工程量必填
          if (['itemDesc', 'unit', 'requireQuantity', 'extQuantity'].includes(item.columnCode)) {
            item.starFlag = true
          } else {
            item.starFlag = false
          }
          // 【发票类型】和【币种】为字典
          if (item.columnCode == 'extCurrency') {
            item.code = 'currency'
          } else if (item.columnCode == 'extInvoiceType') {
            item.code = 'SOU_BIDPRICE_INVOICE_TYPE'
          }
          return item
        })
        // 重新渲染表格必填项
        this.itemListKey++
      }
      // 再查询表格数据
      const response = await bidBuyerHttp.init.getRequireInfo(this.biddingBaseInfo.projectId)
      if (response && response.data) {
        this.itemList = response.data
      }
    },

    handleSuccess () {
      this.getProjectRequirementsData()
    },

    downloadTemplate () {
      downloadFileLink(
        `/api-sou/ext/buyer/bid/init/exportPriceExcelTemplate?projectId=${this.biddingBaseInfo.projectId}`,
        '导入模板.xlsx'
      ).catch(() => {
        this.$message.error(this.$t('purchaseDemand.downloadFail'))
      })
    },

    addRow () {
      this.itemList.push({})
    },

    /* 删除物料行 */
    deleteRow (index) {
      this.itemList.splice(index, 1)
    },

    updateData () {
      this.readonlyNew = false
      this.updateDataVisible = false
    },

    cancelUpdate () {
      this.readonlyNew = true
      this.updateDataVisible = true
      this.getProjectRequirementsData()
    },

    /* 保存项目需求信息 */
    async saveRequirement (type) {
      // 校验表格字段必填(包名、其他模板字段)
      let valid = false
      let tipMsg = ''
      this.templateDataRight.map(item => {
        if (item.starFlag) {
          this.itemList.some((it, index) => {
            // 校验报价信息必填项
            if (!it[item.columnCode] || (this.biddingBase.mergeFlag && !it.extPackageName)) {
              valid = true
              tipMsg = `报价信息第${index + 1}行缺少必填项`
              return true
            }
          })
        }
      })
      if (valid) {
        this.$message.error(tipMsg)
        return
      }
      const response = await bidBuyerHttp.init.editRequireInfo({
        // 是否是暂存
        tempSave: type !== 'nextOne',
        projectId: this.biddingBaseInfo.projectId,
        itemList: this.itemList
      })

      if (response) {
        this.$message.success(this.$t('common.success'))
        // 查询
        await this.getProjectRequirementsData()
        // 发起保存成功回调
        this.$emit('temp-save-success', type)
      }
    }
  }
}
</script>
<style lang="scss" scoped>
.template-body{
  display: flex;
  flex-direction: row;
  align-items: flex-start;
  justify-content: space-around;

  .template-body-center{
    margin-top: 120px;
  }

  .title-style {
    margin-bottom: 16px;
    font-weight: bold;
  }
}
</style>
