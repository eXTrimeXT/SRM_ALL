<template>
  <div class="tech-info">
    <!-- 技术标投标 -->
    <div style="margin-bottom: 14px">
      <div>
        <span v-if="techFlag" style="font-size: 14px;font-weight:bold">
          {{ $t("cusEntry.biddingSettings.supplierTender") }}
        </span>
        <span v-else style="font-size: 14px;font-weight:bold">
          {{ $t("cusEntry.biddingSettings.techFile") }}
        </span>
      </div>
      <div style="margin: 8px 0">
        <p style="color: red;margin: 6px 0;">
          注：
        </p>
        <p style="color: red;margin: 6px 0;">
          ①<b style="font-size: 14px;margin-right:4px;">投标文件</b>为招标邀请函中提供的模板文件；
        </p>
        <p style="color: red;margin: 6px 0;">
          ②<b style="font-size: 14px;margin-right:4px;">方案文件</b>需上传至“技术方案”处且<b style="font-size: 14px;margin:0 4px;">优先Word类型文件</b>；其余文件均需上传至”其他文件“处；
        </p>
        <p style="color: red;margin: 6px 0;">
          ③单个签章附件大小不可超过20M，超过20MB无法线上签署；
        </p>
      </div>

      <!-- <el-button
        v-if="!readonly"
        type="primary"
        @click="addRow('TECH_BID')"
      >
        上传技术文件
      </el-button> -->
      <el-button
        v-if="!readonly"
        type="primary"
        @click="addRow('TECH_SOLUTION_BID')"
      >
        上传技术方案
      </el-button>
      <!-- <el-button
        v-if="!readonly"
        type="primary"
        @click="addRow('TECH_QUA_PERF')"
      >
        上传资质业绩文件
      </el-button> -->
      <el-button
        v-if="!readonly"
        type="primary"
        @click="addRow('TECH_OTHER')"
      >
        上传其他文件
      </el-button>
      <!--线上签署-->
      <el-button v-if="!readonly && contractVerification=='Y'" type="primary" @click="onlineSign">
        {{ $t("cusEntry.biddingSettings.onlineSign") }}
      </el-button>
      <el-button
        v-if="!readonly && contractVerification=='Y'"
        type="primary"
        @click="refresh"
      >
        刷新签署状态
      </el-button>
    </div>
    <div style="margin: 12px 0 14px 0">
      <!-- 技术标投标不限制表格高度 -->
      <el-table
        v-if="techFlag"
        :data="techOrderFileList"
        style="width: 100%"
        border
      >
        <el-table-column
          align="center"
          type="index"
          fixed="left"
          :label="$t('common.sort')"
          width="50"
        />
        <el-table-column
          align="center"
          prop="orderFileName"
          :label="$t('bidMod.fileName')"
          :render-header="_addStarToColumn"
          show-overflow-tooltip
          minWidth="180px"
        >
          <template slot-scope="scope">
            <SrmCommonFile
              :extra-data="fileInfo"
              :default-file="{
                fileId: scope.row.orderDocId,
                fileName: scope.row.orderFileName
              }"
              :validate-options="scope.row.fileType === 'TECH_BID' ? { accept: ['pdf'] } : scope.row.fileType === 'TECH_SOLUTION_BID' ? { accept: ['xls', 'xlsx', 'doc', 'docx', 'ppt', 'pptx', 'pdf', 'ofd', 'cad', 'wps'] } : {}"
              :readonly="readonly"
              @on-change="value => fileChange(value, scope.$index)"
            />
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          prop="fileType"
          label="文件类型"
          :formatter="(row, column, cellValue) => $getDictLabel('SOU_FILE_CONFIG_TYPE', cellValue)"
          show-overflow-tooltip
          minWidth="150px"
        />
        <!--包名-->
        <el-table-column
          v-if="mergeFlag"
          align="center"
          prop="extPackageNameList"
          :label="$t('cusEntry.biddingSettings.bagName')"
          :render-header="_addStarToColumn"
          show-overflow-tooltip
          minWidth="150px"
        >
          <template slot-scope="scope">
            <el-select
              v-if="scope.row.fileType == 'TECH_BID'"
              v-model="scope.row.extPackageNameList"
              :disabled="readonly"
              multiple
            >
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
          align="center"
          prop="orderRemark"
          :label="$t('bidMod.remark')"
          minWidth="150px"
        >
          <template v-slot="scope">
            <el-input v-model="scope.row.orderRemark" :disabled="readonly" />
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          prop="extSignStatus"
          label="签署状态"
          :formatter="(row, column, cellValue) => $getDictLabel('SOU_BID_SIGN_STATUS', cellValue)"
          show-overflow-tooltip
          minWidth="150px"
        />
        <el-table-column
          v-if="readonly"
          align="center"
          prop="orderStatus"
          label="投标状态"
          :formatter="(row, column, cellValue) => $getDictLabel('SOU_ORDER_STATUS', cellValue)"
          show-overflow-tooltip
          minWidth="150px"
        />
        <el-table-column
          v-if="readonly"
          align="center"
          prop="extSubmitTime"
          label="投标时间"
          show-overflow-tooltip
          minWidth="150px"
        />
        <!--操作-->
        <el-table-column
          v-if="!readonly"
          :label="$t('common.operation')"
          fixed="right"
          width="80"
        >
          <template v-slot="scope">
            <el-button
              v-if="scope.row.fileType !== 'TECH_BID'"
              type="text"
              @click="deleteRow(scope.$index)"
            >
              {{ $t("common.delete") }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 非技术标投标 限制表格高度 -->
      <el-table
        v-else
        :data="techOrderFileList"
        style="width: 100%"
        max-height="200"
        border
      >
        <el-table-column
          align="center"
          type="index"
          fixed="left"
          :label="$t('common.sort')"
          width="50"
        />
        <el-table-column
          align="center"
          prop="orderFileName"
          :label="$t('bidMod.fileName')"
          :render-header="_addStarToColumn"
          show-overflow-tooltip
          minWidth="180px"
        >
          <template slot-scope="scope">
            <SrmCommonFile
              :extra-data="fileInfo"
              :default-file="{
                fileId: scope.row.orderDocId,
                fileName: scope.row.orderFileName
              }"
              :validate-options="scope.row.fileType === 'TECH_BID' ? { accept: ['pdf'] } : scope.row.fileType === 'TECH_SOLUTION_BID' ? { accept: ['xls', 'xlsx', 'doc', 'docx', 'ppt', 'pptx', 'pdf', 'ofd', 'cad', 'wps'] } : {}"
              :readonly="readonly"
              @on-change="value => fileChange(value, scope.$index)"
            />
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          prop="fileType"
          label="文件类型"
          :formatter="(row, column, cellValue) => $getDictLabel('SOU_FILE_CONFIG_TYPE', cellValue)"
          show-overflow-tooltip
          minWidth="150px"
        />
        <!--包名-->
        <el-table-column
          v-if="mergeFlag"
          align="center"
          prop="extPackageNameList"
          :label="$t('cusEntry.biddingSettings.bagName')"
          :render-header="_addStarToColumn"
          show-overflow-tooltip
          minWidth="150px"
        >
          <template slot-scope="scope">
            <el-select
              v-if="scope.row.fileType == 'TECH_BID'"
              v-model="scope.row.extPackageNameList"
              :disabled="readonly"
              multiple
            >
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
          align="center"
          prop="orderRemark"
          :label="$t('bidMod.remark')"
          minWidth="150px"
        >
          <template v-slot="scope">
            <el-input v-model="scope.row.orderRemark" :disabled="readonly" />
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          prop="extSignStatus"
          label="签署状态"
          :formatter="(row, column, cellValue) => $getDictLabel('SOU_BID_SIGN_STATUS', cellValue)"
          show-overflow-tooltip
          minWidth="150px"
        />
        <el-table-column
          v-if="readonly"
          align="center"
          prop="extOrderStatus"
          label="投标状态"
          :formatter="(row, column, cellValue) => $getDictLabel('SOU_ORDER_STATUS', cellValue)"
          show-overflow-tooltip
          minWidth="150px"
        />
        <el-table-column
          v-if="readonly"
          align="center"
          prop="extSubmitTime"
          label="投标时间"
          show-overflow-tooltip
          minWidth="150px"
        />
        <!--操作-->
        <el-table-column
          v-if="!readonly"
          :label="$t('common.operation')"
          fixed="right"
          width="80"
        >
          <template v-slot="scope">
            <el-button
              v-if="scope.row.fileType !== 'TECH_BID'"
              type="text"
              @click="deleteRow(scope.$index)"
            >
              {{ $t("common.delete") }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <!--上传签章文件-->
    <EleSignDialog
      :visible.sync="eleSignDialogVisible"
      :orderId="orderId"
      orderType="BID_TECH"
    />
  </div>
</template>

<script>
/**
 * 技术信息
 */
import EleSignDialog from './composition/eleSignDialog'

export default {
  name: 'TechInfo',
  components: { EleSignDialog },
  props: {
    mergeFlag: {
      type: Boolean,
      default: false
    },
    contractVerification: {
      type: String,
      default: 'N'
    },
    packNameList: {
      type: Array,
      required: true
    },
    vendorFileList: {
      type: Array,
      required: true
    },
    readonly: {
      type: Boolean,
      default: false
    },
    techFlag: {
      type: Boolean,
      default: false
    },
    orderId: {
      type: Number
    }
  },
  data () {
    return {
      fileInfo: {
        uploadType: 'DEF',
        sourceType: 'WEB_APP',
        fileModular: 'bid',
        fileFunction: 'quote',
        fileType: 'images'
      },
      eleSignDialogVisible: false
    }
  },
  computed: {
    techOrderFileList: {
      get: function () {
        const list = this.vendorFileList.filter(item => item.fileType === 'TECH_BID')
        if (!list.length) {
          // eslint-disable-next-line vue/no-side-effects-in-computed-properties
          this.vendorFileList.unshift({
            orderFileName: '',
            orderDocId: '',
            fileType: 'TECH_BID',
            extPackageName: '',
            extPackageNameList: [],
            orderRemark: '',
            extSignStatus: ''
          })
        }
        return this.vendorFileList
      },
      set: function (val) {
        return this.$emit('update:vendorFileList', val)
      }
    }
  },
  methods: {
    refresh () {
      this.$emit('refresh')
    },
    /* 新增一行 */
    addRow (fileType) {
      this.techOrderFileList.push({
        orderFileName: '',
        orderDocId: '',
        fileType,
        extPackageName: '',
        extPackageNameList: [],
        orderRemark: '',
        extSignStatus: ''
      })
    },
    /* 文件变更 */
    fileChange ({ file }, index) {
      const { fileId = '', fileName = '' } = file || {}
      this.techOrderFileList[index].orderDocId = fileId
      this.techOrderFileList[index].orderFileName = fileName
    },
    /* 删除行 */
    deleteRow (index) {
      this.techOrderFileList.splice(index, 1)
    },
    // 电子签章
    onlineSign () {
      this.$http({
        url: '/api-sou/ext/vendor/bid/pushSgin',
        method: 'POST',
        data: {
          orderId: this.orderId,
          orderType: 'BID_TECH',
          signFileList: this.techOrderFileList
        },
        loading: true
      }).then(res => {
        this.$message.success(res.message)
        this.$emit('success')
        window.open(res.data)
      })
    }
  }
}
</script>
