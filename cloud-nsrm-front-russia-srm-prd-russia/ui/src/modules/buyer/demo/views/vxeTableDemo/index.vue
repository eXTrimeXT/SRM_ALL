<template>
  <div style="padding: 0 10px 10px 10px; margin: 10px;">
    <el-tabs v-model="activeName">
      <el-tab-pane
        label="上传组件"
        name="uploadFile"
        lazy
      >
        <UploadFileCommon />
      </el-tab-pane>

      <!--倒计时组件-->
      <el-tab-pane
        label="倒计时"
        name="first"
        lazy
      >
        <div>
          <h2>1. 截止时间与当前时间对比 [2022-04-22 00:00:00]</h2>
          <DynamicCutoffTime
            label="距离截止还剩余:"
            deadline-time="2022-04-22 00:00:00"
          />

          <el-divider />

          <h2>2. 截止时间与当前时间对比 只统计天 [2022-04-22 00:00:00]</h2>
          <DynamicCutoffTime
            label="距离截止还剩余:"
            deadline-time="2022-04-22 00:00:00"
            time-unit="days"
          />

          <el-divider />

          <h2>3. 截止时间与自定义时间对比 非动态 [2022-01-22 00:00:00 - 2022-04-22 00:00:00]</h2>
          <DynamicCutoffTime
            label="距离截止还剩余:"
            deadline-time="2022-04-22 00:00:00"
            deadline-diff-time="2022-01-22 00:00:00"
          />

          <el-divider />

          <h2>4. 截止时间与自定义时间对比 只统计年 非动态 [2020-01-22 00:00:00 - 2022-04-22 00:00:00]</h2>
          <DynamicCutoffTime
            label="距离截止还剩余:"
            deadline-time="2022-04-22 00:00:00"
            deadline-diff-time="2020-01-22 00:00:00"
            time-unit="years"
          />

          <el-divider />

          <h2>5. 剩余时间 824281008/毫秒</h2>
          <DynamicCutoffTime
            label="报名截止还剩余:"
            :remaining-time="824281008"
          />

          <el-divider />

          <h2>6. 剩余时间 824281008/毫秒 只统计天</h2>
          <DynamicCutoffTime
            label="报名截止还剩余:"
            :remaining-time="824281008"
            time-unit="days"
          />

          <el-divider />

          <h2>7. 剩余时间 824281008/毫秒 只统计小时</h2>
          <DynamicCutoffTime
            label="报名截止还剩余:"
            :remaining-time="824281008"
            time-unit="hours"
          />

          <el-divider />

          <p>
            <el-button type="primary" @click="dynamicCutoffTimeDiffToString('2022-04-30 00:00:00')">
              点击计算 2022-04-30 00:00:00 与当前时间对比倒计时
            </el-button>
          </p>
        </div>
      </el-tab-pane>

      <!--input-format指令-->
      <el-tab-pane
        label="input-format指令"
        name="second"
        lazy
      >
        <h2>number 数字</h2>
        <el-input
          v-model="inputValue1"
          v-input-format="{ type: 'number', handle: ['blur'] }"
          max="200"
          min="100"
          style="width: 200px"
        />

        <h2>integer 整数</h2>
        <el-input
          v-model="inputValue2"
          v-input-format="{ type: 'integer', negative: true }"
          style="width: 200px"
        />

        <h2>float 浮点数</h2>
        <el-input
          v-model="inputValue3"
          v-input-format="{ type: 'float', digits: 8, zero: false, handle: ['focus', 'blur'] }"
          style="width: 200px"
        />

        <h2>thousandth 数值分位</h2>
        <el-input
          v-model="inputValue4"
          v-input-format="{ type: 'thousandth', digits: 2, handle: ['blur'] }"
          max="200000"
          min="5"
          style="width: 200px"
        />

        <h2>filterZhCn 过滤中文</h2>
        <el-input
          v-model="inputValue5"
          v-input-format="{ type: 'filterZhCn', handle: ['blur'] }"
          style="width: 200px"
        />

        <h2>letterAndNumber 只允许字母数字</h2>
        <el-input
          v-model="inputValue6"
          v-input-format="{ type: 'letterAndNumber', handle: ['blur'] }"
          style="width: 200px"
        />
      </el-tab-pane>

      <!--vxe table大数据表格-->
      <el-tab-pane
        label="vxe table大数据表格"
        name="three"
        lazy
      >
        <div>
          <vxe-toolbar>
            <template #buttons>
              <el-button @click="insertEvent(-1)">
                在最后行插入
              </el-button>
            </template>
          </vxe-toolbar>

          <vxe-table
            ref="xTable"
            border
            show-overflow
            keep-source
            height="600"
            class="my-xtable-element"
            :loading="loading"
            :data="tableData"
            :edit-config="{ trigger: 'click', mode: 'row', autoClear: false }"
          >
            <vxe-column
              align="center"
              type="checkbox"
              width="60"
            />
            <vxe-column
              align="center"
              type="seq"
              width="80"
            >
              <template #header>
                <span>序号</span>
                <em class="el-icon-question" />
              </template>
            </vxe-column>

            <vxe-column
              field="categoryName"
              :title="$t('bidMod.purcategoryName')"
              width="150"
              :edit-render="{}"
            >
              <template #edit="{ row, $index }">
                <quick-search
                  :show-input="row.categoryName"
                  show-key="categoryName"
                  :scope-data="row"
                  :table-index="$index"
                  clearable
                  name="scc_base_purchase_category2"
                  @close-quicksearch="categoryNameChange"
                />
              </template>
              <template #default="{ row }">
                {{ row.categoryName }}
              </template>
            </vxe-column>

            <vxe-column
              align="center"
              field="name"
              title="ElInput"
              min-width="140"
            >
              <template #edit="scope">
                <el-input
                  v-model="scope.row.name"
                  @input="$refs.xTable.updateStatus(scope)"
                />
              </template>
            </vxe-column>

            <vxe-column
              align="center"
              field="role"
              title="ElAutocomplete"
              min-width="160"
              :edit-render="{}"
            >
              <template #edit="{ row }">
                <el-autocomplete
                  v-model="row.role"
                  :fetch-suggestions="roleFetchSuggestions"
                />
              </template>
            </vxe-column>

            <vxe-column
              align="center"
              field="age"
              title="ElInputNumber"
              width="160"
              :edit-render="{}"
            >
              <template #header="{ column }">
                <span>{{ column.title }}</span>
                <em class="el-icon-warning" />
              </template>
              <template #edit="{ row }">
                <el-input-number
                  v-model="row.age"
                  :max="35"
                  :min="18"
                />
              </template>
            </vxe-column>

            <vxe-column
              align="center"
              field="materialMatching"
              title="ElSelect"
              width="140"
              :edit-render="{}"
            >
              <template #edit="scope">
                <dict-select
                  v-model="scope.row.materialMatching"
                  code="PRICE_TYPE"
                  @change="$refs.xTable.updateStatus(scope)"
                />
              </template>
              <template #default="{ row }">
                {{ $getDictLabel('PRICE_TYPE', row.materialMatching) }}
              </template>
            </vxe-column>

            <vxe-column
              align="center"
              field="showRequireNum"
              :title="$t('bid_mod.showRequireNum')"
              width="200"
              :formatter="({ cellValue }) => $getDictLabel('YES_OR_NO', cellValue)"
            />

            <vxe-column
              align="center"
              field="sex1"
              title="ElSelect"
              width="180"
              :edit-render="{}"
            >
              <template #edit="scope">
                <el-select
                  v-model="scope.row.sex1"
                  multiple
                >
                  <el-option
                    v-for="item in sexList"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </template>
              <template #default="{ row }">
                {{ getSelectMultipleLabel(row.sex1, sexList) }}
              </template>
            </vxe-column>

            <vxe-column
              align="center"
              field="region"
              title="ElCascader"
              width="200"
              :edit-render="{}"
            >
              <template #edit="{ row }">
                <el-cascader
                  v-model="row.region"
                  :options="regionList"
                />
              </template>
              <template #default="{ row }">
                {{ getCascaderLabel(row.region, regionList) }}
              </template>
            </vxe-column>
            <vxe-column
              align="center"
              field="date"
              title="ElDatePicker"
              width="200"
              :edit-render="{}"
            >
              <template #edit="{ row }">
                <el-date-picker
                  v-model="row.date"
                  type="date"
                  format="yyyy/MM/dd"
                />
              </template>
              <template #default="{ row }">
                {{ formatDate(row.date, 'yyyy/MM/dd') }}
              </template>
            </vxe-column>

            <vxe-column
              align="center"
              field="date1"
              title="ElDatePicker"
              width="220"
              :edit-render="{}"
            >
              <template #edit="{ row }">
                <el-date-picker
                  v-model="row.date1"
                  type="datetime"
                  format="yyyy-MM-dd HH:mm:ss"
                />
              </template>
              <template #default="{ row }">
                {{ formatDate(row.date1, 'yyyy-MM-dd HH:mm:ss') }}
              </template>
            </vxe-column>
            <vxe-column
              align="center"
              field="date2"
              title="ElTimePicker"
              width="200"
              :edit-render="{}"
            >
              <template #edit="{ row }">
                <el-time-select
                  v-model="row.date2"
                  :picker-options="{start: '08:30', step: '00:15', end: '18:30'}"
                />
              </template>
            </vxe-column>
            <vxe-column
              align="center"
              field="color1"
              title="ElColorPicker"
              width="140"
              :edit-render="{}"
            >
              <template #edit="{ row }">
                <el-color-picker v-model="row.color1" />
              </template>
            </vxe-column>
            <vxe-column
              align="center"
              field="rate"
              title="ElRate"
              width="200"
            >
              <template #default="{ row }">
                <el-rate v-model="row.rate" />
              </template>
            </vxe-column>
            <vxe-column
              align="center"
              field="flag"
              title="ElSwitch"
              width="100"
            >
              <template #default="{ row }">
                <el-switch v-model="row.flag" />
              </template>
            </vxe-column>
          </vxe-table>
        </div>
      </el-tab-pane>
      <el-tab-pane
        label="VxeBaseTableDemo"
        name="four"
        lazy
      >
        <VxeBaseTableDemo />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
/**
 * vxe-table demo
 */
import XEUtils from 'xe-utils'
import
DynamicCutoffTime,
{
  dynamicCutoffTimeDiffToString,
  parseTimeByMillisecond
} from 'lib@/components/dynamic-cutoff-time'
import UploadFileCommon from 'modb@/demo/views/vxeTableDemo/uploadFileCommon'
import VxeBaseTableDemo from './vxeBaseTableDemo'

export default {
  name: 'VXETableDemo',

  components: {
    DynamicCutoffTime,
    UploadFileCommon,
    VxeBaseTableDemo
  },

  data () {
    return {
      activeName: 'uploadFile',
      loading: false,
      tableData: [],
      sexList: [],
      regionList: [],
      restaurants: [
        { value: '前端', name: '前端' },
        { value: '后端', name: '后端' }
      ],
      priceNodes: {},
      itemId: '291929784573952',
      chartData: null,
      inputValue1: '',
      inputValue2: '111.222',
      inputValue3: '',
      inputValue4: '',
      inputValue5: '',
      inputValue6: ''
    }
  },

  created () {
    console.log(parseTimeByMillisecond(824281008, 'days'))

    this.loading = true
    setTimeout(() => {
      this.tableData = [
        { id: 10001, name: 'Test1', nickname: 'T1', role: 'Develop', sex: '0', sex1: [], region: [], age: 28, date: '', date1: '', date2: '', date7: '', color1: '', rate: 5, flag: false, address: 'Shenzhen', showRequireNum: 'Y' },
        { id: 10002, name: 'Test2', nickname: 'T2', role: 'Test', sex: '1', sex1: [], region: [], age: 22, date: '', date1: '', date2: '', date7: '', color1: '', rate: 2, flag: false, address: 'Guangzhou' },
        { id: 10003, name: 'Test3', nickname: 'T3', role: 'PM', sex: '0', sex1: [], region: [], age: 32, date: '', date1: '', date2: '', date7: '', color1: '', rate: 3, flag: false, address: 'Shanghai' },
        { id: 10004, name: 'Test4', nickname: 'T4', role: 'Designer', sex: '0', sex1: ['1', '0'], region: [], age: 23, date: '', date1: '', date2: '', color1: '', date7: '', rate: 3, flag: true, address: 'Shenzhen' },
        { id: 10005, name: 'Test5', nickname: 'T5', role: 'Develop', sex: '0', sex1: ['1', '0'], region: [], age: 30, date: '', date1: '', date2: '', color1: '', date7: '', rate: 0, flag: true, address: 'Shanghai' },
        { id: 10006, name: 'Test6', nickname: 'T6', role: 'Designer', sex: '0', sex1: [], region: [], age: 21, date: '', date1: '', date2: '', date7: '', color1: '', rate: 3, flag: false, address: 'Shenzhen' },
        { id: 10007, name: 'Test7', nickname: 'T7', role: 'Test', sex: '1', sex1: ['1'], region: [], age: 29, date: '', date1: '', date2: '', date7: '', color1: '', rate: 0, flag: true, address: 'Guangzhou' },
        { id: 10008, name: 'Test8', nickname: 'T8', role: 'Develop', sex: '1', sex1: [], region: [], age: 35, date: '', date1: '', date2: '', date7: '', color1: '', rate: 2, flag: false, address: 'Shenzhen' },
        { id: 10009, name: 'Test9', nickname: 'T9', role: 'Test', sex: '1', sex1: ['0'], region: [], age: 24, date: '', date1: '', date2: '', date7: '', color1: '', rate: 3, flag: false, address: 'Shenzhen' },
        { id: 100010, name: 'Test10', nickname: 'T10', role: 'Develop', sex: '1', sex1: [], region: [], age: 20, date: '', date1: '', date2: '', date7: '', color1: '', rate: 4, flag: false, address: 'Guangzhou' }
      ]
      this.loading = false
    }, 500)
  },

  methods: {
    dynamicCutoffTimeDiffToString (...ret) {
      console.log(parseTimeByMillisecond(824281008, 'days'))

      // $i18n可不传只做演示
      const result = dynamicCutoffTimeDiffToString(ret, '', { $i18n: this.$i18n })
      this.$alert(result.status ? result.value : '计算错误')
    },
    categoryNameChange () {
      console.log('!-----')
    },
    formatDate (value, format) {
      return XEUtils.toDateString(value, format)
    },
    getSelectLabel (value, list, valueProp = 'value', labelField = 'label') {
      const item = list.find(item => item[valueProp] === value)
      return item ? item[labelField] : null
    },
    getSelectMultipleLabel (value, list, valueProp = 'value', labelField = 'label') {
      return value.map(val => {
        const item = list.find(item => item[valueProp] === val)
        return item ? item[labelField] : null
      }).join(', ')
    },
    getCascaderLabel (value, list) {
      const values = value || []
      const labels = []
      const matchCascaderData = function (index, list) {
        const val = values[index]
        if (list && values.length > index) {
          list.forEach(item => {
            if (item.value === val) {
              labels.push(item.label)
              matchCascaderData(++index, item.children)
            }
          })
        }
      }
      matchCascaderData(0, list)
      return labels.join(' / ')
    },
    roleFetchSuggestions (queryString, cb) {
      const restaurants = this.restaurants
      const results = queryString ? restaurants.filter(this.createStateFilter(queryString)) : restaurants
      clearTimeout(this.timeout)
      this.timeout = setTimeout(() => {
        cb(results)
      }, 3000 * Math.random())
    },
    createStateFilter (queryString) {
      return (state) => {
        return (state.name.toLowerCase().indexOf(queryString.toLowerCase()) === 0)
      }
    },
    async insertEvent (row) {
      const $table = this.$refs.xTable
      const record = {
        sex: '1',
        sex1: [],
        date12: '2021-01-01'
      }
      await $table.insertAt(record, row)
    }
  }
}
</script>
