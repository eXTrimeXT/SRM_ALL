<template>
  <div class="tech-info">
    <p>
      <span style="padding: 0 11px">{{ $t("bidMod.techInfo") }}</span>
      <!--新增-->
      <el-button type="primary" @click="addRow">
        {{ $t("common.add") }}
      </el-button>
    </p>

    <div class="table2">
      <el-table
        :data="vendorFileListData"
        style="width: 100%"
        border
      >
        <el-table-column
          type="index"
          :label="$t('common.sort')"
          width="50"
        />

        <!--招标要求-->
        <el-table-column
          prop="fileRequire"
          :label="$t('bidMod.bidRequire')"
          show-overflow-tooltip
        />

        <!--参考附件-->
        <SrmCommonFile
          type="table-column"
          :table-column-options="{
            label: $t('bidMod.refAttachment'),
            prop: 'requireDocId',
            nameProp: 'requireFileName'
          }"
          readonly
        />

        <!--采购商备注-->
        <el-table-column
          prop="requireRemark"
          :label="$t('bidMod.vendorRemark')"
          show-overflow-tooltip
        />

        <!--文件类型-->
        <el-table-column
          prop="fileType"
          :label="$t('bid_mod.referenceFileType')"
        >
          <template v-slot="scope">
            <DictSelect
              v-model="scope.row.fileType"
              :transform-options="transformOptions"
              code="SOU_FILE_CONFIG_TYPE"
              :disabled="!!scope.row.requireDocId"
            />
          </template>
        </el-table-column>

        <!--投标附件-->
        <SrmCommonFile
          type="table-column"
          :table-column-options="{
            label: $t('bidMod.bidAttachment'),
            prop: 'orderDocId',
            nameProp: 'orderFileName'
          }"
          @on-change="fileChange"
        />

        <!--备注-->
        <el-table-column
          prop="orderRemark"
          :label="$t('bidMod.remark')"
        >
          <template v-slot="scope">
            <el-input v-model="scope.row.orderRemark" />
          </template>
        </el-table-column>

        <!--操作-->
        <el-table-column
          :label="$t('common.operation')"
          width="80"
        >
          <template v-slot="{ $index, row }">
            <el-button
              v-if="!row.requireDocId"
              type="text"
              @click="deleteRow($index, row)"
            >
              {{ $t("common.delete") }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
/**
 * 技术信息
 */
import { SOU_BRG_TYPE_ENUM } from 'lib@/composition/biddingLts/utils'
import { SOU_FILE_CONFIG_TYPE_ENUM } from 'lib@/composition/origin/enum'

export default {
  name: 'TechInfo',

  props: {
    biddingData: {
      type: Object,
      default: () => ({}),
      required: true
    },
    vendorFileList: {
      type: Array,
      required: true
    }
  },

  computed: {
    vendorFileListData: {
      get: function () {
        return this.vendorFileList
      },
      set: function (val) {
        return this.$emit('update:vendorFileList', val)
      }
    }
  },

  methods: {
    /* 编排文件类型 */
    transformOptions (options) {
      // 如果招标类型是 【商务】 那么参考模板文件类型只能是 【商务标】
      if (this.biddingData.biddingType === SOU_BRG_TYPE_ENUM.BUSINESS) {
        return options.map(opt => {
          if (opt.value === SOU_FILE_CONFIG_TYPE_ENUM.TECH_FILE) {
            return { ...opt, disabled: true }
          }
          return opt
        })
      }
      return options
    },

    /* 新增一行 */
    addRow () {
      this.vendorFileListData.push({
        orderFileName: '',
        orderDocId: '',
        fileType: '',
        orderRemark: '',
        buyerConfig: 'N'
      })
    },

    /* 文件变更 */
    fileChange ({ file, $index }) {
      const { fileId = '', fileName = '' } = file || {}
      this.vendorFileListData[$index].orderDocId = fileId
      this.vendorFileListData[$index].orderFileName = fileName
    },

    /* 删除行 */
    deleteRow (index, row) {
      if (row.buyerConfig === 'N' || !row.requireDocId) {
        this.vendorFileListData.splice(index, 1)
      }
    }
  }
}
</script>
