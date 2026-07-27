<template>
  <div style="width:100%;">
    <el-select
      v-if="!disabled"
      v-model="assigneeIdList"
      :multiple="multiple"
      filterable
      remote
      :remote-method="remoteMethod"
      :placeholder="$t('common.pleaseSelect')"
      :loading="loading"
      @change="selectPersonnel"
    >
      <el-option
        v-for="item in options"
        :key="item.assigneeId"
        :label="item.assigneeName"
        :value="item.assigneeId">
      </el-option>
    </el-select>
    <!--如果disabled为true的时候显示-->
    <div v-else>{{ assigneeNameList }}</div>
  </div>
</template>

<script>
export default {
  name: 'Personnel',
  components: {
  },
  props: {
    value: {
      type: Array,
      default: function () {
        return []
      }
    },
    disabled: {
      type: Boolean,
      default: false
    },
    searchData: {
      type: Object,
      default: function () {
        return {}
      }
    },
    multiple: {
      type: Boolean,
      default: true
    }
  },
  data () {
    return {
      options: [],
      loading: false,
      assigneeIdList: [],
      assigneeNameList: '',
      optionsOldList: [],
    }
  },
  watch: {
    value: {
      immediate: true,
      handler (newValue) {
        if (newValue) {
          let attr = []
          let attrName = []
          for (let item of newValue) {
            attr.push(item.assigneeId)
            attrName.push(item.assigneeName)
          }
          this.assigneeIdList = attr
          this.assigneeNameList = attrName.join(',')
        }
      },
      deep: true
    }
  },
  created () {
    console.log(this.value , 'value')
    this.createdFun()
  },
  methods: {
    selectPersonnel (personnel) {
      console.log(personnel, 'personnel')
      console.log(this.optionsOldList, 'this.optionsOldList')
      let attr = []
      if (this.multiple) {
        for (let item of personnel) {
          let dataList = this.optionsOldList.filter(itemF => itemF?.assigneeId == item)
          attr.push(dataList?.[0])
        }
      } else {
        let dataList = this.optionsOldList.filter(itemF => itemF?.assigneeId == personnel)
        attr.push(dataList?.[0])
      }

      this.$emit('change', attr)
    },
    remoteMethod (data) {
      this.loading = true
      this.$http({
        url: '/api-base/ext/flow/instance/task/user/query',
        method: 'POST',
        data: {
          searchKey: data,   // 搜索值
          taskKey: this.searchData?.taskKey,    // 节点名称
          formDataId: this.searchData?.formDataId,       // 单据ID
          actionCode: this.searchData?.actionCode,   // 流程编码
          pageSize: 15,
          pageNum: 1,
        }
      }).then(({ data }) => {
        this.loading = false
        this.$set(this, 'options', data)
        let uniqueArray = this.optionsOldList.concat(data)
        this.optionsOldList = [...new Set(uniqueArray)]
      })
    },
    createdFun () {
      this.options = JSON.parse(JSON.stringify(this.value))
      this.optionsOldList = JSON.parse(JSON.stringify(this.value)) || []
    }
  }
}
</script>
<style scoped>
.el-select{
  width: 100%;
}
::v-deep .el-tag.el-tag--info{
  background-color: #E7F2FF;
  border: 1px solid #A6D0FF;
  color: #0077FF;
}
::v-deep .el-tag.el-tag--info .el-tag__close{
  color: #0077FF;
}
</style>
