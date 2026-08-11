<template>
  <div>
    <div class="mb10" style="display: flex">
      <el-button
        type="primary"
        class="detail-pbtn"
        style="margin-top:5px;margin-bottom:5px;"
        @click="add"
      >
        {{ $t('common.add') }}
      </el-button>
    </div>

    <el-table
      :data="datas"
      style="width: 100%"
      border
      height="250px"
      highlight-current-row
      @selection-change="handleItemSelection"
    >
      <el-table-column
        align="center"
        type="index"
        :label="$t('contractMod.tabindex')"
        width="60"
      />
      <!-- 节点编码 -->
      <el-table-column
        align="center"
        prop="nodeCode"
        :label="$t('common.nodeCode')"
        min-width="150"
        :show-overflow-tooltip="true"
      >
        <template slot-scope="scope">
          <span v-if="scope.$index <= 2">{{scope.row.nodeCode}}</span>
          <el-input v-else v-model="scope.row.nodeCode" />
        </template>
      </el-table-column>
      <!-- 节点名称 -->
      <el-table-column
        align="center"
        prop="nodeName"
        :label="$t('common.nodeName')"
        min-width="150"
        :show-overflow-tooltip="true"
      >
        <template slot-scope="scope">
          <span v-if="scope.$index <= 2">{{scope.row.nodeName}}</span>
          <el-input v-else v-model="scope.row.nodeName" />
        </template>
      </el-table-column>
      <!-- 是否启用该节点 -->
      <el-table-column
        align="center"
        prop="enableFlag"
        :label="$t('common.nodeEnableFlag')"
        min-width="150"
      >
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.enableFlag"
            active-value="Y"
            inactive-value="N"
          />
        </template>
      </el-table-column>
      <!-- 操作 -->
      <el-table-column
        :label="$t('common.operation')"
        width="60"
      >
        <template slot-scope="scope">
          <!-- 删除 -->
          <el-button
            v-if="scope.$index > 2"
            type="text"
            @click="deleteOneContent(scope.$index, scope.row)"
          >
            {{
              $t('common.delete')
            }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { accessProcessConfiguration } from 'modb@/basicSetting/api/basicSetting'

export default {
  name: 'Node',
  data () {
    return {
      datas: [],
      ids: ''
    }
  },
  methods: {
    add () {
      const obj = {
        entryConfigId: this.ids
      }
      this.datas.push(obj)
    },
    changes (row) {
      this.datas = []
      this.ids = row.entryConfigId
      accessProcessConfiguration.listConfigNodeById(row.entryConfigId).then(res => {
        if (res.code == '0') {
          this.datas = res.data
        }
      })
    },
    save () {
      accessProcessConfiguration.saveOrUpdateNode(this.datas).then(res => {
        if (res.code == '0') {
          this.$message.success(res.message)
          this.$emit('colseNode')
        }
      })
    },
    deleteOneContent (index, row) {
      this.datas.splice(index, 1)
    }
  }
}
</script>

<style scoped>

</style>
