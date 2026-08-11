<template>
  <el-container
    class="flex-container-notab studyDemoPage"
    direction="vertical"
  >
    <el-main>
      <!-- uploadFile 附件上传- 正常方式 -->
      {{ $t("cusEntry.supplement20250211.uploadFileNormalWay") }}
      <div style="width:300px;margin:20px 50px;">
        <uploadFile
          :show-file-list="false"
          :cus-data="fileInfo"
          :table="false"
          :file-name="fileName"
          :file-id="fileId"
          @upload-success="uploadSuccessHandel"
          @remove-file="removeFileHandel"
          @on-preview="previewHandel"
        />
      </div>
      <!-- uploadFile 附件上传 - 表格里面 -->
      {{ $t("cusEntry.supplement20250211.uploadFileAttachmentTable") }}
      <div style="width:300px;margin:20px 50px;">
        <uploadFile
          :show-file-list="false"
          :cus-data="fileInfo"
          :table="true"
          :file-name="fileName"
          :file-id="fileId"
          @upload-success="uploadSuccessHandel"
          @remove-file="removeFileHandel"
          @on-preview="previewHandel"
        />
      </div>
      <!-- uploadFile 附件上传 - 多文件上传 -->
      {{ $t("cusEntry.supplement20250211.uploadFileAttachmentMultiUpload") }}
      <div style="width:300px;margin:20px 50px;">
        <uploadFile
          :show-file-list="true"
          :cus-data="fileInfo"
          :table="false"
          :file-list="cpkFileList"
          :limit="10000"
          @upload-success="multiUploadSuccessHandel"
          @remove-file="multiRemoveFileHandel"
        />
      </div>
      <!-- <div>
        <pageHeader
          ref="header"
          :title="pageT"
          isReflash
          @reflash="reflashHandel"
        >
          <template #right>
            <el-button ref="headerBtn" @click="saveHandel">
              保存
            </el-button>
            <el-button>
              暂存
            </el-button>
          </template>
        </pageHeader>
        <div class="section" ref="pagediv">
          <span>刷新次数:{{reflashNum}}</span>
          <p>我的第一个vue页面</p>
        </div>

        <div class="section">
          <h3>表单双向绑定</h3>
          <div class="formStyle">
            <el-input v-model="inputValue"/>
            <span>{{inputValue}}</span>
          </div>

          <h3>实时监听</h3>
          <p>{{inputValueNew}}</p>
          <h3>computed计算属性</h3>
          <p><span>isDisabled:</span>{{isDisabled}}</p>

          <h3>属性绑定</h3>
          <div class="divRad"></div>
          <br>
          <div :class="['divRad',{divBlue : isChange }]"></div>

          <h3>指令</h3>

          <div v-for="item in dataList" :key="item.id"><span>{{item.name}}</span> --- <span>{{item.desc}}</span></div>

          <div v-if="showDom"> v-if显示节点 </div>
          <div v-else>不显示</div>
          <div v-show="showDom"> v-show显示节点 </div>
        </div>
      </div> -->
    </el-main>
  </el-container>
</template>
<script>
import pageHeader from 'modb@/demo/components/pageHeader.vue'
import uploadFile from '@/library/components/c-upload-file'
import { downloadWithParam } from '@/library/utils/file'
export default {
  name: 'StudyDemo',
  components: { pageHeader, uploadFile },
  data () {
    return {
      pageT: this.$t('cusEntry.supplement20250211.pageA'),  // '页面A'
      title: this.$t('cusEntry.supplement20250211.studyExample'),  // '学习例子'
      reflashNum: 0,
      inputValue: '',
      inputValueNew: '',
      opt: 'add',
      isChange: true,
      divRad: 'divRad',
      divBlue: 'divBlue',
      dataList: [
        { id: 'dsds1', name: this.$t('cusEntry.supplement20250211.name1'), desc: this.$t('cusEntry.supplement20250211.englishdescription1') },
        { id: 'dsds2', name: this.$t('cusEntry.supplement20250211.nameTwo'), desc: this.$t('cusEntry.supplement20250211.describeTwo') },
        { id: 'dsds3', name: this.$t('cusEntry.supplement20250211.nameThree'), desc: this.$t('cusEntry.supplement20250211.describeThree') }
      ],
      showDom: false,
      fileId: '',
      fileName: '',
      fileInfo: {
        uploadType: 'DEF',
        sourceType: 'WEB_APP',
        fileModular: 'sup',
        fileFunction: 'biddingProject',
        fileType: 'images'
      },
      cpkFileList: []
    }
  },
  computed: {
    isDisabled () {
      return this.opt == 'view'
    }
  },
  watch: {
    inputValue: {
      handler (val_new, val_old) {
        if (val_new) {
          this.inputValueNew = 'watch:' + val_new
          //
        }
      },
      deep: true,
      immediate: true
    }
  },
  // 页面创建
  created () {

  },
  // 页面挂载完成
  mounted () {
    console.log(this.$refs.header)
    console.log(this.$refs.headerBtn)
    console.log(this.$refs.pagediv)
  },
  methods: {
    uploadSuccessHandel (file) {
      const { id, name } = file
      this.fileId = id.toString()
      this.fileName = name
    },
    removeFileHandel () {

    },
    previewHandel () {

    },

    // 多文件上传成功
    multiUploadSuccessHandel (file, fileList) {
      downloadWithParam(
        file.id,
        file.name
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
      })

      this.cpkFileList = fileList
      // row.specificationList.push({
      //   fileuploadId: id.toString(),
      //   name:name,
      //   status:status,
      //   materialFileType: 'specificationList'
      // })
      console.log('fileList')
      console.log(this.cpkFileList)
    },
     // 删除
    multiRemoveFileHandel (id) {
      if (id) {
        let delIndex = this.cpkFileList.findIndex(i => (i.fileuploadId == id))
        this.cpkFileList.splice(delIndex, 1)// 删除
      }
    },
    // 多附件保存处理
    multiSaveFile () {
       // 处理附件回显和保存
      this.cpkFileList.forEach((elm) => {
        let specificationList = elm.specificationList
        specificationList.forEach((file) => {
          if (file.response) {
            file.fileuploadId = file.response.data.fileuploadId // 附件ID 必传
            file.materialFileType = 'specificationList' // 附件类型 必传
            // file.name = file.response.data.fileuploadName // 附件名字 必传
          }
        })
      })
    },
    reflashHandel () {
      this.reflashNum++
      /// / todo
      // 查询数据
    },
    saveHandel () {
      let query = { a: '3' }
    }
  }
}
</script>
<style scoped lang="scss">
.studyDemoPage {
  .section{
    padding: 5px;
  }
  .formStyle{
    width: 400px;
  }
  .divRad{
    width: 100px;
    height: 50px;
    background: red;
  }
  .divBlue{
    background: blue;
  }
  .b{
    height: 200px;
    background: blue;
  }
  #a{
    height: 100px;
    background: blue;
  }
}
</style>
