// 只有供应商变更才用这个业务组件,这个是相关认证信息模块
<template>
  <div class="sceneAttachment">
    <p
      v-if="curOpt !== 'view'"
      class="sub_header"
    >
      <el-button
        v-if="!disabledBol"
        type="primary"
        class="detail-pbtn"
        @click="addSceneAttachment"
      >
        {{ $t('common.new') }}
      </el-button>
    </p>
    <el-table
      ref="sceneAttachmentTable"
      :disabled="disabledBol"
      :data="sceneAttachmentData"
      style="width: 100%"
      border
      max-height="250px"
    >
      <el-table-column
        align="center"
        type="index"
        width="50"
      />
      <!-- 认证类型 -->
      <el-table-column
        align="center"
        prop="authFile"
        :label="$t('vendorMod.authType')"
        min-width="150"
        :show-overflow-tooltip="true"
      >
        <template slot="header">
          <i class="toRequired">*</i>{{ $t('vendorMod.authType') }}
        </template>
        <template slot-scope="scope">
          <SrmCommonFile
            :extra-data="fileInfo"
            :default-file="{
              fileId: scope.row.fileuploadId,
              fileName: scope.row.authType
            }"
            :readonly="!(scope.row.opType === 'add' || scope.row.opType === 'update')"
            @on-change="({file}) => innerHandleUploadSuccess(file,scope.row)"
          />
        </template>
      </el-table-column>
      <!-- 认证描述 -->
      <el-table-column
        align="center"
        prop="authDescription"
        :label="$t('vendorMod.authDesc')"
        min-width="150"
        :show-overflow-tooltip="true"
      >
        <template slot-scope="scope">
          <el-input
            v-if="scope.row.opType === 'add' || scope.row.opType === 'update'"
            v-model="scope.row.authDescription"
            :class="sceneAttachmentDataY[scope.$index]?(sceneAttachmentDataY[scope.$index].authDescription!=scope.row.authDescription?'redColorFont':null):'redColorFont'"
            :disabled="disabledBol"
          />
          <span v-else>{{ scope.row.authDescription }}</span>
        </template>
      </el-table-column>
      <!-- 认证编号 -->
      <el-table-column
        align="center"
        prop="authNum"
        :label="$t('vendorMod.authNum')"
        min-width="150"
        :show-overflow-tooltip="true"
      >
        <template slot-scope="scope">
          <el-input
            v-if="scope.row.opType === 'add' || scope.row.opType === 'update'"
            v-model="scope.row.authNum"
            :class="sceneAttachmentDataY[scope.$index]?(sceneAttachmentDataY[scope.$index].authNum!=scope.row.authNum?'redColorFont':null):'redColorFont'"
            :disabled="disabledBol"
            @change="setFormatValue(scope.row)"
          />
          <span v-else>{{ scope.row.authNum }}</span>
        </template>
      </el-table-column>
      <!-- 认证时间 -->
      <el-table-column
        align="center"
        prop="authDate"
        :label="$t('vendorMod.authDate')"
        width="160"
        :show-overflow-tooltip="true"
      >
        <template slot-scope="scope">
          <el-date-picker
            v-if="scope.row.opType === 'add' || scope.row.opType === 'update'"
            v-model="scope.row.authDate"
            :class="sceneAttachmentDataY[scope.$index]?(sceneAttachmentDataY[scope.$index].authDate!=scope.row.authDate?'redColorFont':null):'redColorFont'"
            :disabled="disabledBol"
            type="date"
            format="yyyy-MM-dd"
            value-format="yyyy-MM-dd"
          />
          <span v-else>{{ scope.row.authDate }}</span>
        </template>
      </el-table-column>
      <!-- 认证机构 -->
      <el-table-column
        align="center"
        prop="authOrg"
        :label="$t('vendorMod.authOrg')"
        min-width="150"
        :show-overflow-tooltip="true"
      >
        <template slot-scope="scope">
          <el-input
            v-if="scope.row.opType === 'add' || scope.row.opType === 'update'"
            v-model="scope.row.authOrg"
            :class="sceneAttachmentDataY[scope.$index]?(sceneAttachmentDataY[scope.$index].authOrg!=scope.row.authOrg?'redColorFont':null):'redColorFont'"
            :disabled="disabledBol"
          />
          <span v-else>{{ scope.row.authOrg }}</span>
        </template>
      </el-table-column>
      <!-- 证件有效期至 -->
      <el-table-column
        align="center"
        prop="endDate"
        :label="$t('vendorMod.certUntil')"
        width="170"
      >
        <template slot-scope="scope">
          <el-date-picker
            v-if="scope.row.opType === 'add' || scope.row.opType === 'update'"
            v-model="scope.row.endDate"
            :class="sceneAttachmentDataY[scope.$index]?(sceneAttachmentDataY[scope.$index].endDate!=scope.row.endDate?'redColorFont':null):'redColorFont'"
            :disabled="disabledBol"
            type="date"
            format="yyyy-MM-dd"
            value-format="yyyy-MM-dd"
          />
          <span v-else>{{ scope.row.endDate }}</span>
        </template>
      </el-table-column>
      <!-- 操作 -->
      <el-table-column
        align="center"
        prop="operation"
        :label="$t('common.operation')"
        width="100"
        fixed="right"
      >
        <template slot-scope="scope">
          <el-button
            v-if="scope.row.opType === 'delete' || scope.row.opType === ''"
            type="text"
            :disabled="disabledBol"
            @click="sceneAttachmentHandel(scope.$index, scope.row, 'update')"
          >
            {{ $t('common.edit') }}
          </el-button>
          <el-button
            v-if="scope.row.opType === 'delete' || scope.row.opType === 'update'"
            type="text"
            :disabled="disabledBol"
            @click="sceneAttachmentHandel(scope.$index, scope.row, 'cancel')"
          >
            {{ $t('common.cancel') }}
          </el-button>
          <el-button
            type="text"
            :disabled="disabledBol"
            @click="relevantRowDelHandel(scope.$index, scope.row)"
          >
            {{ $t('common.delete') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script>

import QuickSearch from 'lib@/components/QuickSearch'

export default {
  name: 'SceneAttachment',
  components: {
    QuickSearch
  },
  model: {
    prop: 'data',
    event: 'change'
  },
  props: {
    disabledBol: {
      type: Boolean,
      default: false
    },
    sceneAttachmentData: {
      type: Array,
      default () {
        return []
      }
    },
    sceneAttachmentDataY: {
      type: Array,
      default () {
        return []
      }
    },
    managementInfo: {
      type: Array,
      default () {
        return []
      }
    },
    acceptFileType: {
      type: Array,
      default () {
        return []
      }
    },
    fileInfo: {
      type: Object,
      default () {
        return {}
      }
    },
    rowClassName: {
      type: [String, Function],
      default () {
        return []
      }
    },
    curOpt: {
      type: String,
      default () {
        return 'add'
      }
    }
  },
  data () {
    return {

    }
  },
  // 'innerHandleUploadSuccess',
  inject: ['addSceneAttachment', 'innerButtonClick', 'handleAttachmentRemove1', 'setFormatValue', 'sceneAttachmentHandel', 'relevantRowDelHandel'],
  computed: {

  },
  watch: {

  },
  mounted () {

  },
  methods: {
    innerHandleUploadSuccess (file, row) {
      const { fileId = '', fileName = '' } = file || {}
      this.$set(row, 'fileuploadId', fileId)
      this.$set(row, 'authType', fileName)
    }
  }
}
</script>

<style scope>
/*.formClassAll .el-select,.formClassAll .el-input-all,.formClassAll .el-date-editor,.formClassAll .el-input-group{*/
/*  width: 320px !important;*/
/*}*/
.formClassAll form{
  padding-left: 18px
}
.changeTitle{
  background-color: #F6F6F6;
  font-size: 14px ;
  color: #393E45 ;
  overflow: hidden;
  line-height: 40px;
  margin-bottom:20px;
  font-weight: 400;
}
.changeTitle i{
  width: 4px;
  height: 18px;
  background-color: #0077FF;
  margin: 11px 10px 11px 16px;
  display: block;
  float: left;
}
</style>
