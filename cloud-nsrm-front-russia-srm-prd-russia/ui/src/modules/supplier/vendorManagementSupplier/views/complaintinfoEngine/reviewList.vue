<template>
  <div class="review-wrapper">
    <div class="review-list">
      <div v-for="(item,index) in content" :key="index" class="review-item">
        <div class="title">
          <div class="user">
            *{{ item.createdFullName }}
          </div>
          <div class="time">
            {{ item.creationDate }}
          </div>
        </div>
        <div class="content">
          {{ item.reviewContent }}
        </div>
        <div class="file">
          <div v-for="(innerItem,innerIndex) in item.fileUploads" :key="innerIndex" class="file-item">
            <srm-common-file :default-file="{fileId:innerItem.fileuploadId,fileName:innerItem.fileName}" readonly />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  name: 'ReviewList',
  props: {
    content: {
      type: Array,
      default: () => []
    }
  },
  data () {
    return {
      list: this.content
    }
  }
}
</script>
<style lang="scss" scoped>
.review-wrapper {
  max-height: 500px;
  overflow-y: scroll;
  .review-list {
    .review-item {
      padding: 10px 0;
      border-bottom:1px solid #ccc;
      .title {
        display:flex;
        align-items: center;
        justify-content: space-between;
        .user {
          font-weight:bold;
        }
        .time {
          font-weight:bold;
        }
      }
      .content {
        line-height:20px;
        margin:10px 0;
      }
    }
  }
}
</style>
<style>
.file-item .file-item-name {
  flex:none !important;
}
</style>
