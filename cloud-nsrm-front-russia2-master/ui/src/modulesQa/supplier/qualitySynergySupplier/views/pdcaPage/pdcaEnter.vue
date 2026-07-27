<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <el-form
        ref="PCDAEnterForm"
        class="enter-form"
        :model="dialogModle"
        :rules="rules"
      >
        <el-collapse v-model="activeDims" class="tab-form-style">
          <el-collapse-item :title="$t('pdca.baseData')" name="1">
            <srm-row :gutter="50">
              <srm-col :initCol="4">
                <!-- todo -- 检查 SPC项目编号-->
                <el-form-item :label="$t('quality.monitorListNo')">
                  <el-input v-model="dialogModle.pageSelectEntity.monitorListNo" disabled />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="4">
                <el-form-item :label="$t('quality.spSpcPdcaReport')">
                  <el-input v-model="dialogModle.spSpcPdcaReport.recordId" disabled />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="4">
                <el-form-item :label="$t('quality.proposeDate')">
                  <el-date-picker
                    v-model="dialogModle.spSpcPdcaReport.proposeDate"
                    disabled
                    type="datetime"
                    :placeholder="$t('quality.proposeDate')"
                    value-format="yyyy-MM-dd HH:mm:ss"
                  />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="4">
                <el-form-item :label="$t('quality.proposerName')" :prop="'spSpcPdcaReport.proposerName'" :rules="rules.proposerName">
                  <el-input v-model="dialogModle.spSpcPdcaReport.proposerName" :disabled="isDisabled" />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="4">
                <el-form-item :label="$t('quality.productCode')">
                  <el-input v-model="dialogForm.productCode" disabled />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="4">
                <el-form-item :label="$t('quality.productModel')">
                  <el-input v-model="dialogForm.productModel" disabled />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="4">
                <el-form-item :label="$t('quality.customerOrgName')">
                  <el-input v-model="dialogForm.customerOrgName" disabled />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="4">
                <el-form-item :label="$t('quality.lineCode')">
                  <el-input v-model="dialogForm.lineCode" disabled />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="4">
                <el-form-item :label="$t('quality.workCenter')">
                  <el-input v-model="dialogForm.workCenter" disabled />
                </el-form-item>
              </srm-col>
              <!-- todo -- 检查下面2个字段 -->
              <srm-col :initCol="4">
                <el-form-item :label="$t('quality.characterUnit')">
                  <el-input v-model="dialogModle.pageSelectEntity.characterUnit" disabled />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="4">
                <el-form-item :label="$t('quality.drawingsArea')">
                  <el-input v-model="dialogModle.pageSelectEntity.drawingsArea" disabled />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="4">
                <el-form-item :label="$t('quality.onLineEquipment')">
                  <el-input v-model="dialogForm.onLineEquipment" disabled />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="4">
                <el-form-item :label="$t('quality.monitoringFeature')">
                  <el-input v-model="dialogForm.monitoringFeature" disabled />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="4">
                <el-form-item :label="$t('quality.standardMin2')">
                  <el-input v-model="dialogForm.standardMin" disabled />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="4">
                <el-form-item :label="$t('quality.rectificationDate')" :prop="'spSpcPdcaReport.rectificationDate'" :rules="rules.rectificationDate">
                  <el-date-picker
                    v-model="dialogModle.spSpcPdcaReport.rectificationDate"
                    type="datetime"
                    :disabled="isDisabled"
                    :placeholder="$t('quality.rectificationDate')"
                    value-format="yyyy-MM-dd HH:mm:ss"
                  />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="4">
                <el-form-item :label="$t('quality.exceptionCatelog')" :prop="'spSpcPdcaReport.exceptionCatelog'">
                  <el-input v-model="dialogModle.spSpcPdcaReport.exceptionCatelog" disabled />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="4">
                <el-form-item :label="$t('quality.exceptionEvent')" :prop="'spSpcPdcaReport.exceptionEvent'">
                  <el-input v-model="dialogModle.spSpcPdcaReport.exceptionEvent" disabled />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="4">
                <el-form-item :label="$t('quality.subGroupSize')">
                  <el-input v-model="dialogForm.subGroupSize" disabled />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="4">
                <el-form-item :label="$t('quality.warningAvgValue')">
                  <el-input v-model="dialogForm.warningAvgValue" disabled />
                </el-form-item>
              </srm-col>
            </srm-row>
            <el-form-item :label="$t('quality.remarks')">
              <el-input v-model="dialogModle.spSpcPdcaReport.remarks" type="textarea" :disabled="isDisabled" />
            </el-form-item>
            <el-form-item :label="$t('quality.measuringDetail')">
              <el-input v-model="dialogModle.spSpcPdcaReport.measuringDetail" type="textarea" :disabled="isDisabled" />
            </el-form-item>
          </el-collapse-item>
          <el-collapse-item title="PDCA" name="2">
            <el-button v-if="!isDisabled" type="primary" @click="addPCDAOne">
              {{ $t('common.add') }}
            </el-button>
            <el-button v-if="!isDisabled" @click="delPCDAOne">
              {{ $t('common.delete') }}
            </el-button>
            <el-table
              :data="dialogModle.causeList"
              border
              class="table"
              style="width: 100%"
              @selection-change="checkPCDAChange"
            >
              <el-table-column
                type="selection"
              />
              <el-table-column
                type="index"
                :label="$t('common.sort')"
                width="80"
                align="center"
              />
              <el-table-column align="center" prop="exceptionCatelog" :label="$t('quality.exceptionCatelog2')">
                <template slot-scope="scope">
                  <el-form-item label-width="0" :prop="'causeList[' + scope.$index + '].exceptionCatelog'" :rules="rules.exceptionCatelog">
                    <el-input v-model="scope.row.exceptionCatelog" :placeholder="$t('quality.exceptionCatelog2')" :disabled="isDisabled" />
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column align="center" prop="reasonAnalysis" :label="$t('quality.reasonAnalysis')">
                <template slot-scope="scope">
                  <el-form-item label-width="0" :prop="'causeList[' + scope.$index + '].reasonAnalysis'" :rules="rules.reasonAnalysis">
                    <el-input v-model="scope.row.reasonAnalysis" :placeholder="$t('quality.reasonAnalysis')" :disabled="isDisabled" />
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column align="center" prop="improvePlan" :label="$t('quality.improvePlan')">
                <template slot-scope="scope">
                  <el-form-item label-width="0" :prop="'causeList[' + scope.$index + '].improvePlan'" :rules="rules.improvePlan">
                    <el-input v-model="scope.row.improvePlan" :placeholder="$t('quality.improvePlan')" :disabled="isDisabled" />
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column align="center" minWidth="210" prop="improvedCompleteDate" :label="$t('quality.improvedCompleteDate')">
                <template slot-scope="scope">
                  <el-form-item label-width="0" :prop="'causeList[' + scope.$index + '].improvedCompleteDate'" :rules="rules.improvedCompleteDate">
                    <el-date-picker
                      v-model="scope.row.improvedCompleteDate"
                      type="datetime"
                      :disabled="isDisabled"
                      value-format="yyyy-MM-dd HH:mm:ss"
                      :placeholder="$t('quality.improvedCompleteDate')"
                    />
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column align="center" prop="improvedName" :label="$t('quality.improvedName')">
                <template slot-scope="scope">
                  <el-form-item label-width="0" :prop="'causeList[' + scope.$index + '].improvedName'" :rules="rules.improvedName">
                    <el-input v-model="scope.row.improvedName" :placeholder="$t('quality.improvedName')" :disabled="isDisabled" />
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column align="center" prop="improvedDeptName" :label="$t('quality.improvedDeptName')">
                <template slot-scope="scope">
                  <el-form-item label-width="0" :prop="'causeList[' + scope.$index + '].improvedDeptName'" :rules="rules.improvedDeptName">
                    <el-input v-model="scope.row.improvedDeptName" :placeholder="$t('quality.improvedDeptName')" :disabled="isDisabled" />
                  </el-form-item>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>
          <el-collapse-item :title="$t('quality.verification')" name="3">
            <el-button v-if="!isDisabled" type="primary" @click="addOne">
              {{ $t('common.add') }}
            </el-button>
            <el-button v-if="!isDisabled" @click="delOne">
              {{ $t('common.delete') }}
            </el-button>
            <el-table
              :data="dialogModle.verificationList"
              border
              class="table"
              style="width: 100%"
              @selection-change="checkChange"
            >
              <el-table-column
                type="selection"
              />
              <el-table-column
                type="index"
                align="center"
                width="80"
                :label="$t('common.sort')"
              />
              <el-table-column width="120" align="center" prop="veriflcationUser" :label="$t('quality.veriflcationUser')">
                <template slot-scope="scope">
                  <el-form-item label-width="0" :prop="'verificationList[' + scope.$index + '].veriflcationUser'" :rules="rules.veriflcationUser">
                    <el-input v-model="scope.row.veriflcationUser" :placeholder="$t('quality.veriflcationUser')" :disabled="isDisabled" />
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column width="210" align="center" prop="veriflcationDate" :label="$t('quality.veriflcationDate')">
                <template slot-scope="scope">
                  <el-form-item label-width="0" :prop="'verificationList[' + scope.$index + '].veriflcationDate'" :rules="rules.veriflcationDate">
                    <el-date-picker
                      v-model="scope.row.veriflcationDate"
                      type="datetime"
                      :disabled="isDisabled"
                      value-format="yyyy-MM-dd HH:mm:ss"
                      :placeholder="$t('quality.veriflcationDate')"
                    />
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column width="210" align="center" prop="result" :label="$t('quality.result')">
                <template slot-scope="scope">
                  <el-form-item label-width="0" :prop="'verificationList[' + scope.$index + '].result'" :rules="rules.result">
                    <el-select v-model="scope.row.result" :disabled="isDisabled">
                      <el-option :label="$t('quality.validateSuccess')" value="Y" />
                      <el-option :label="$t('quality.validateErr')" value="N" />
                    </el-select>
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column align="center" prop="resultDetail" :label="$t('quality.resultDetail')">
                <template slot-scope="scope">
                  <el-form-item label-width="0" :prop="'verificationList[' + scope.$index + '].resultDetail'" :rules="rules.resultDetail">
                    <el-input v-model="scope.row.resultDetail" :placeholder="$t('quality.resultDetail')" :disabled="isDisabled" />
                  </el-form-item>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>
        </el-collapse>
      </el-form>
    </el-main>
    <CToolbar>
      <template slot="right">
        <el-button @click="back">
          {{ $t('bidMod.cancel') }}
        </el-button>
        <template v-if="!isDisabled">
          <el-button size="primary" @click="submit('save')">
            {{ $t('common.save') }}
          </el-button>
          <el-button type="primary" @click="submit('submit')">
            {{ $t('common.submit') }}
          </el-button>
        </template>
      </template>
    </CToolbar>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'
import { pdcaPage } from '@/modulesQa/supplier/qualitySynergySupplier/api'
import CToolbar from 'lib@/components/c-toolbar'
import { transformMQL } from '@/library/utils/util'
import { parseTime } from '@/utils'
const { pcdaSubmit, findReportByRecordId, saveOrUpdate } = pdcaPage
export default {
  name: 'PCDAEnter',
  components: {
    MainHeader, CToolbar
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      activeDims: ['1', '2', '3'],
      dialogForm: {

      },
      dialogModle: {
        recordId: null,
        pageSelectEntity: {},
        spSpcPdcaReport: {
          'recordId': '',
          'proposerName': '',
          'proposeDate': new Date(),
          'rectificationDate': '',
          'exceptionCatelog': '',
          'exceptionEvent': '',
          'remarks': '',
          'measuringDetail': ''
        },
        causeList: [
          {
            'exceptionCatelog': '',
            'reasonAnalysis': '',
            'improvePlan': '',
            'improvedCompleteDate': '',
            'improvedName': '',
            'improvedDeptName': '',
            'improvedDeptId': ''
          }
        ],
        verificationList: [
          {
            'veriflcationUser': '',
            'veriflcationDate': '',
            'resultDetail': '',
            'result': ''
          }
        ]
      },
      rules: {
        proposerName: [{ required: true, message: this.$t('common.pleaseInput') }],
        rectificationDate: [{ required: true, message: this.$t('common.pleaseSelect') }],
        exceptionCatelog: [{ required: true, message: this.$t('common.pleaseSelect') }],
        reasonAnalysis: [{ required: true, message: this.$t('common.pleaseInput') }],
        improvePlan: [{ required: true, message: this.$t('common.pleaseInput') }],
        improvedCompleteDate: [{ required: true, message: this.$t('common.pleaseSelect') }],
        improvedName: [{ required: true, message: this.$t('common.pleaseInput') }],
        improvedDeptName: [{ required: true, message: this.$t('common.pleaseInput') }],
        veriflcationUser: [{ required: true, message: this.$t('common.pleaseInput') }],
        veriflcationDate: [{ required: true, message: this.$t('common.pleaseSelect') }],
        result: [{ required: true, message: this.$t('common.pleaseSelect') }],
        resultDetail: [{ required: true, message: this.$t('common.pleaseInput') }]
      },
      isDisabled: this.$attrs.params.flag == 'detail',
      formLabelWidth: '100px',
      pcdaSelectList: [],
      SelectList: []
    }
  },
  created () {
    let data = this.$attrs.params.params
    console.log(data)
    // 获取提交的内容
    if (data.recordId) this.getRecord(data.recordId)
    this.dialogModle.spSpcPdcaReport.recordId = data.recordId // 整改编号
    this.dialogForm.productModel = data.productModel // 规格型号
    this.dialogForm.productCode = data.productCode // 物料编码
    this.dialogForm.subGroupSize = data.subGroupSize // 物料编码
    this.dialogForm.customerOrgName = data.customerOrgName
    this.dialogForm.lineCode = data.lineCode // 产线
    this.dialogForm.workCenter = data.workCenter // 工作中心
    this.dialogForm.onLineEquipment = data.onLineEquipment // 联机设备
    this.dialogForm.monitoringFeature = data.monitoringFeature // 监控特性
    this.dialogModle.spSpcPdcaReport.exceptionCatelog = data.exceptionCatelog // 异常分类
    this.dialogModle.spSpcPdcaReport.exceptionEvent = data.exceptionEvent // 异常事件
    this.dialogForm.standardMin = this.$t('quality.minLine') + data.standardMin + this.$t('quality.maxLine') + data.standardMax // 标准
    this.dialogForm.warningAvgValue = data.warningAvgValue
  },
  methods: {
    back () {
      this.$emit('tab-remove', 'PCDAEnter')
    },
    // 选择项变化
    checkPCDAChange (selection) {
      this.pcdaSelectList = selection
    },
    // 选择项变化
    checkChange (selection) {
      this.SelectList = selection
    },
    // 获取提交的内容
    getRecord (recordId) {
      let transformParams = transformMQL.save('spcPdcaReport', [{ recordId }], 'findReportByRecordId')
      findReportByRecordId(transformParams).then(response => {
        const data = response.data.records[0]
        let spSpcPdcaReport = data.spSpcPdcaReport || {}
        this.dialogModle.causeList = data.causeList || []
        Object.keys(spSpcPdcaReport).forEach(key => {
          if (spSpcPdcaReport[key]) this.dialogModle.spSpcPdcaReport[key] = spSpcPdcaReport[key]
        })
        this.dialogModle.verificationList = data.verificationList || []
        this.dialogModle.pageSelectEntity = data.pageSelectEntity || {}
      })
    },
    addPCDAOne () {
      let causeList = {
        'exceptionCatelog': '',
        'reasonAnalysis': '',
        'improvePlan': '',
        'improvedCompleteDate': '',
        'improvedName': '',
        'improvedDeptName': '',
        'improvedDeptId': '',
        __i: Date.now()
      }
      this.dialogModle.causeList.unshift(causeList)
    },
    delPCDAOne () {
      let selection = this.pcdaSelectList
      if (selection.length > 0) {
        selection.forEach((val, index) => {
          // 遍历源数据
          this.dialogModle.causeList.forEach((v, i) => {
            // 如果选中数据和源数据的某一条唯一标识符相等，删除对应的源数据
            if (val.id && val.id === v.id) {
              this.dialogModle.causeList.splice(i, 1)
            }
            if (val.causeId && val.causeId === v.causeId) {
              this.dialogModle.causeList.splice(i, 1)
            }
            if (val.__i && val.__i === v.__i) {
              this.dialogModle.causeList.splice(i, 1)
            }
          })
        })
      } else {
        return false
      }
    },
    addOne () {
      let verificationList = {
        'veriflcationUser': '',
        'veriflcationDate': '',
        'resultDetail': '',
        'result': '',
        __i: Date.now()
      }
      this.dialogModle.verificationList.unshift(verificationList)
    },
    delOne () {
      let selection = this.SelectList
      if (selection.length > 0) {
        selection.forEach((val, index) => {
          // 遍历源数据
          this.dialogModle.verificationList.forEach((v, i) => {
            // 如果选中数据和源数据的某一条唯一标识符相等，删除对应的源数据
            if (val.id && val.id === v.id) {
              this.dialogModle.verificationList.splice(i, 1)
            }
            if (val.verificationId && val.verificationId === v.verificationId) {
              this.dialogModle.verificationList.splice(i, 1)
            }
            if (val.__i && val.__i === v.__i) {
              this.dialogModle.verificationList.splice(i, 1)
            }
          })
        })
      } else {
        return false
      }
    },
    // 保存、提交
    submit (type) {
      let { proposeDate, recordId } = this.dialogModle.spSpcPdcaReport
      if (proposeDate) this.dialogModle.spSpcPdcaReport.proposeDate = parseTime(proposeDate)
      this.dialogModle.recordId = recordId
      let transformParams = transformMQL.save('spcPdcaReport', [this.dialogModle], type === 'save' ? 'saveOrUpdate' : 'submit')
      if (type === 'save') {
        saveOrUpdate(transformParams).then(response => {
          this.$message({
            message: this.$t('common.successSave'),
            type: 'success'
          })
          this.$emit('tab-remove', 'PCDAEnter')
          this.__setTabTodo('PdcaPageList.getQuerydata')
        })
      }
      if (type === 'submit') {
        this.$refs.PCDAEnterForm.validate((valid) => {
          if (valid) {
            pcdaSubmit(transformParams).then(response => {
              this.$message({
                message: this.$t('common.successSubmit'),
                type: 'success'
              })
              this.$emit('tab-remove', 'PCDAEnter')
              this.__setTabTodo('PdcaPageList.getQuerydata')
            })
          } else {
            console.log('error submit!!')
            return false
          }
        })
      }
    }
  }
}
</script>
<style scoped lang="scss">
.flex-container {
  padding-bottom: 40px;
}
.table {
  margin-top: 10px;
}
.el-collapse-item__content>.el-button {margin-bottom: 5px}
</style>
