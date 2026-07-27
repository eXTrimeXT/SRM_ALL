<template>
  <el-container direction="vertical" class="flex-container single-table">
    <el-main>
      <EasyTable
        ref="table"
        :selection="true"
        :methods="methods"
        :columns="columns"
        row-key="redisCacheId"
        table-name="redisCache"
        :query-params.sync="queryParams"
        @selection-change="selectionChangeHandler"
      >
        <template #btns>
          <el-button type="primary" @click="add">
            {{ $t('common.add') }}
          </el-button>
          <el-button type="primary" @click="save">
            {{ $t('common.save') }}
          </el-button>
          <el-button type="primary" @click="batchDelete">
            {{ $t('common.delete') }}
          </el-button>
          <div style="display: inline-block;width: 400px;margin: 0 10px;">
            <el-input v-model="keyPrefix">
              <el-button slot="append" type="primary" @click="delRedisCacheByPrefix">
                <!-- 根据KEY清除缓存 -->
                {{ $t('dataConfMod.clearCacheAccKey') }}
              </el-button>
            </el-input>
          </div>
        </template>

        <template #interfaceName="{ scope }">
          <el-input v-model="scope.row.interfaceName" />
        </template>
        <template #interfaceUrl="{ scope }">
          <el-input v-model="scope.row.interfaceUrl" />
        </template>
        <template #cacheKey="{ scope }">
          <el-input v-model="scope.row.cacheKey" />
        </template>
        <template #remark="{ scope }">
          <el-input v-model="scope.row.remark" />
        </template>
        <template #ifOpen="{ scope }">
          <el-switch v-model="scope.row.ifOpen" active-value="Y" inactive-value="N" />
        </template>
        <template #ifCacheContent="{ scope }">
          <!-- 查看 -->
          <el-button
            v-if="scope.row.ifCacheContent === 'Y'"
            type="text"
            @click="showContent(scope)"
          >
            {{ $t('common.view') }}
          </el-button>
        </template>
      </EasyTable>
    </el-main>

    <!-- 查看 -->
    <srm-dialog
      :title="$t('common.view')"
      :visible.sync="viewVisible"
      :close-on-click-modal="false"
      size="large"
    >
      <vue-json-editor
        v-if="viewVisible"
        v-model="json"
        :show-btns="false"
        :lang="'zh'"
        :mode="mode"
      />
      <div slot="footer" class="dialog-footer">
        <!-- 确 定 -->
        <el-button type="primary" @click="viewVisible = false">
          {{ $t('common.confirm') }}
        </el-button>
      </div>
    </srm-dialog>
  </el-container>
</template>

<script>
import vueJsonEditor from 'vue-json-editor'
import EasyTable from 'lib@/components/BaseTable/EasyTable'
import DictSelect from '@/library/components/c-select/dict-select'
import { redisCacheList } from 'modb@/basicSetting/api/basicSetting'

export default {
  name: 'RedisCacheList',
  components: { EasyTable, vueJsonEditor },
  data () {
    return {
      selectList: [],
      queryParams: {},
      viewVisible: false,
      json: '',
      mode: 'tree',
      keyPrefix: '',
      methods: {
        listPage: async params => {
          const res = await redisCacheList.listPage(params)
          return res
        }
      },
      columns: [
        {
          attrs: {
            label: () => this.$t('dataConfMod.interfaceUrl'), // 接口URL
            prop: 'interfaceUrl',
            sortable: true,
            width: 140
          },
          slot: 'interfaceUrl',
          search: {},
          rules: { required: true, message: this.$t('dataConfMod.required') }
        },
        {
          attrs: {
            label: () => this.$t('dataConfMod.cacheKey'), // 缓存KEY
            prop: 'cacheKey',
            sortable: true,
            width: 135
          },
          slot: 'cacheKey',
          search: {},
          rules: { required: true, message: this.$t('dataConfMod.required') }
        },
        {
          attrs: {
            label: () => this.$t('dataConfMod.cacheTime'), // 缓存有效时间(s)
            prop: 'cacheTime',
            width: 135,
            sortable: true,
            formatter: val => this.$parseTime(val)
          }
        },
        {
          attrs: {
            label: () => this.$t('dataConfMod.cacheContent'), // 缓存内容
            prop: 'ifCacheContent',
            sortable: true
          },
          editable: false,
          slot: 'ifCacheContent'
        },
        {
          attrs: {
            label: () => this.$t('common.remark'), // 备注
            prop: 'remark'
          },
          slot: 'remark'
        },
        {
          attrs: {
            label: () => this.$t('dataConfMod.ifOpen'), // 是否开启
            prop: 'ifOpen',
            width: 125,
            sortable: true,
            formatter: val => (val === 'Y' ? this.$t('common.yes') : this.$t('common.no'))
          },
          slot: 'ifOpen',
          search: {
            component: DictSelect,
            props: { code: 'YES_OR_NO' }
          }
        },

        {
          attrs: {
            prop: 'operation',
            label: () => this.$t('common.operation'), // 操作
            width: 150,
            fixed: 'right'
          },
          operations: [
            {
              event: 'deleteItem',
              name: this.$t('common.delete'), // 删除
              permission: 'base:rediscache:delete',
              func: this.deleteItem
            },
            {
              event: 'deleteCache',
              name: this.$t('dataConfMod.deleteCache'), // 清除缓存
              func: this.deleteCache
            }
          ]
        }
      ]
    }
  },
  methods: {
    add () {
      this.$refs.table.add({})
    },
    async save () {
      const rows = this.$refs.table.getUpdatedRows()
      const updated = rows.filter(row => row.redisCacheId)
      const added = rows.filter(row => !row.redisCacheId)
      await redisCacheList.redisCacheAdd(added)
      await redisCacheList.redisCacheModify(updated)
      await this.$refs.table.search(this.queryParams)
    },
    async batchDelete () {
      if (this.selectList.length === 0) {
        return this.$message.error(this.$t('components.approvalHead.tips.selectData')) // 请选择数据
      }
      const idArr = this.selectList.map(v => v.redisCacheId).filter(v => !!v)
      await redisCacheList.redisBatchDelete(idArr)
      await this.$refs.table.search(this.queryParams)
    },
    async delRedisCacheByPrefix () {
      await redisCacheList.delRedisCacheByPrefix({ keyPrefix: this.keyPrefix })
      await this.$refs.table.search(this.queryParams)
    },
    async showContent (scope) {
      const { data } = await redisCacheList.getRedisCacheContent({
        redisKey: scope.row.cacheKey
      })
      this.viewVisible = true
      try {
        this.json = JSON.parse(data)
      } catch (e) {
        this.json = data
      }
    },
    async deleteItem (scope, data) {
      if (!scope.row.redisCacheId) return data.splice(scope.$index, 1)
      await redisCacheList.redisCacheDelete({
        redisCacheId: scope.row.redisCacheId
      })
      await this.$refs.table.search(this.queryParams)
    },
    async deleteCache (scope) {
      await redisCacheList.delRedisCacheByKey({
        redisKey: scope.row.cacheKey
      })
      await this.$refs.table.search(this.queryParams)
    },
    selectionChangeHandler (data) {
      this.selectList = data
    }
  }
}
</script>

<style scoped lang="scss">
.single-table {
  height: 100%;
}
.pagination {
  padding-top: 10px;
}
.main {
  display: flex;
  flex-direction: column;
  flex-grow: 1;
}
</style>
