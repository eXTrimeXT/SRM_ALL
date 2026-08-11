<template>
  <div class="grid-content">
    <h3 class="grid-title">
      <!-- 服务中心 -->
      {{ $t("dashboard.serviceCenter") }}
    </h3>
    <div class="content">
      <div class="service-list">
        <div v-for="(item,index) in serviceList" :key="index" class="service-item" @click="handleClick(item)">
          <div class="info">
            <img :src="item.imgUrl">
            <span class="desc">{{ item.desc }}</span>
          </div>
          <i class="el-icon-arrow-right" />
        </div>
      </div>
    </div>
    <!-- 请选择需要下载的操作指引 -->
    <srm-dialog
      :visible.sync="showFileDownload"
      :title="$t('dashboard.msgDownload')"
      size="large"
    >
      <srm-row
        style="flex-wrap: wrap;padding: 15px 25px;"
      >
        <srm-col
          v-for="item in fileList"
          :key="item.templateFileId"
          class="file-item"
          :initCol="2"
        >
          <label :title="item.attachmentName">{{ item.attachmentName }}:</label>
          <SrmCommonFile
            :default-file="{
              fileId: item.templateFileId,
              fileName: item.attachmentSourceName
            }"
            :readonly="true"
          />
        </srm-col>
      </srm-row>
    </srm-dialog>
  </div>
</template>
<script>
import { sceneFileCompApi } from '@/api/fileApi'
import cloud4Img from '@/assets/images/cloud4@2x.png'
export default {
  name: 'ServiceCenter',
  components: { },
  data () {
    return {
      curRole: this.$store.getters.userType,
      serviceList: [
        {
          imgUrl: cloud4Img,
          desc: this.$t('cusEntry.dashboard.srmUserGuide'), // SRM平台使用指南
          handler: 'showFileDownload'
        }
      ],
      showFileDownload: false,
      fileList: [],
      userType: this.$store.getters.userType // VENDOR | BUYER
    }
  },
  methods: {
    handleClick (row) {
      // this[row.handler] = true
      this.fileDownload() // 加载下载文件
    },
    fileDownload () {
      let query = {
        sceneCode: 'SCENE_OPERATION_MANUAL',
        enabled: 'Y',
        sceneModuleCode: ''
      }
      if (this.curRole === 'BUYER') {
        query.sceneModuleCode = 'SCENE_OPERATION_MANUAL_PURCHASER_ATTACHMENT'
      } else {
        query.sceneModuleCode = 'SCENE_OPERATION_MANUAL_SUPPLIER_ATTACHMENT'
      }
      sceneFileCompApi.sceneTemplateListAll(query).then(res => {
        this.showFileDownload = true
        this.fileList = res.data || []
      })
    }
  }
}
</script>
<style lang="scss" scoped>
.service-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #F6F6F6;
  border-radius: 4px;
  cursor:pointer;
  .info {
    display:flex;
    align-items:center;
    img {
      width:64px;
      height:58px;
    }
    .desc {
      font-size:14px;
      color: #51555B;
      line-height: 22px;
      margin-left:2px;
      flex: 1;
    }
  }
  i {
    margin-right:16px;
    font-size:16px;
  }
}
</style>
