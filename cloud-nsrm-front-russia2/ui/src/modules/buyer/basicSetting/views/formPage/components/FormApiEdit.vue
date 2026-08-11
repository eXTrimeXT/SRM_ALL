<template>
  <srm-dialog
    :title="$t('dataConfMod.editCompProp')"
    :visible.sync="visible"
    :close-on-click-modal="false"
    :content-max-height-limit="false"
    fullscreen
    size="midden"
    @close="cancel"
    @closed="cancel"
  >
    <el-tabs
      v-model="configType"
      type="border-card"
    >
      <el-tab-pane
        :label="$t('dataConfMod.attributeManage')"
        name="PROP"
      >
        <el-row>
          <el-col :span="24">
            <el-button
              type="primary"
              :disabled="readOnly"

              style="margin-bottom: 5px;"
              @click="addProp()"
            >
              {{ $t('quoteTemplate.selectAttr') }}
            </el-button>
          </el-col>
        </el-row>

        <el-row>
          <el-col
            v-for="(propItem, index) in propList"
            :key="index"
            :span="12"
          >
            <el-card class="box-card">
              <div
                slot="header"
                class="clearfix"
              >
                <span>{{ propItem.propName }}</span>
                <el-button
                  style="float: right; padding: 3px 0"
                  type="text"
                >
                  {{ $t('orderMod.buyerOrderSynergy.management') }}
                </el-button>
                <el-button
                  style="float: right; padding: 3px 0"
                  type="text"
                  @click="removeProp(propItem, index)"
                >
                  {{ $t('components.common.delete') }}
                </el-button>
              </div>
              <div>
                <el-form :ref="'propForm' + index">
                  <el-row :gutter="32">
                    <el-col :span="24">
                      <el-form-item
                        prop="propName"
                        :label="$t('customTable.propName')"
                      >
                        <el-input v-model="propItem.propName" />
                      </el-form-item>
                    </el-col>
                    <el-col :span="24">
                      <el-form-item
                        prop="propValueType"
                        :label="$t('reportMod.propValueType')"
                      >
                        <DictSelect
                          v-model="propItem.propValueType"
                          code="PROP_VALUE_TYPE"
                        />
                      </el-form-item>
                    </el-col>
                    <el-col :span="24">
                      <el-form-item
                        prop="propValue"
                        :label="$t('priceModel.costElement.attributeValue')"
                      >
                        <el-input v-model="propItem.propValue" />
                      </el-form-item>
                    </el-col>
                    <el-col :span="24">
                      <el-form-item
                        prop="propDescribe"
                        :label="$t('reportSetting.propertyDescribe')"
                      >
                        <el-input v-model="propItem.propDescribe" />
                      </el-form-item>
                    </el-col>
                    <el-col
                      v-if="propItem.propValueType === 'FUNCTION' || propItem.propValueType === 'FUNCTION_VALUE'"
                      :span="24"
                    >
                      <el-form-item
                        prop="methodType"
                        :label="$t('reportMod.methodType')"
                      >
                        <DictSelect
                          v-model="propItem.methodType"
                          code="FORM_EVENT_TYPE"
                        />
                      </el-form-item>
                    </el-col>
                    <el-col
                      v-if="propItem.methodType === 'NAME'"
                      :span="24"
                    >
                      <el-form-item
                        prop="methodName"
                        :label="$t('reportSetting.methodName')"
                      >
                        <el-input v-model="propItem.methodName" />
                      </el-form-item>
                    </el-col>
                    <el-col
                      v-if="propItem.propValueType === 'FUNCTION' || propItem.propValueType === 'FUNCTION_VALUE'"
                      :span="24"
                    >
                      <el-form-item
                        prop="methodDescribe"
                        :label="$t('reportSetting.methodDescribe')"
                      >
                        <el-input v-model="propItem.methodDescribe" />
                      </el-form-item>
                    </el-col>
                    <el-col
                      v-if="propItem.methodType === 'DETAIL'"
                      :span="24"
                    >
                      <el-form-item
                        prop="methodParams"
                        :label="$t('reportMod.methodParamsDes')"
                      >
                        <el-input v-model="propItem.methodParams" />
                      </el-form-item>
                    </el-col>
                    <el-col
                      v-if="propItem.methodType === 'DETAIL'"
                      :span="24"
                    >
                      <el-form-item
                        prop="methodBody"
                        :label="$t('reportMod.methodBody')"
                      >
                        <XmlEditor
                          v-model="propItem.methodBody"
                          :show-btns="false"
                          :lang="'zh'"
                          :mode="{name: 'xml'}"
                        />
                      </el-form-item>
                    </el-col>
                  </el-row>
                </el-form>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>
      <el-tab-pane
        :label="$t('reportMod.interfaceMent')"
        name="API"
      >
        <el-form ref="apiForm">
          <el-row :gutter="32">
            <el-col :span="24">
              <el-form-item
                prop="apiType"
                :label="$t('reportMod.apiType')"
              >
                <DictSelect
                  v-model="formApi.apiType"
                  code="FORM_API_TYPE"
                />
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item
                prop="httpMethod"
                :label="$t('reportMod.httpMethod')"
              >
                <DictSelect
                  v-model="formApi.httpMethod"
                  code="FORM_HTTP_METHOD"
                />
              </el-form-item>
            </el-col>
            <el-col
              v-if="formApi.apiType === 'CUSTOM'"
              :span="24"
            >
              <el-form-item
                prop="apiUrl"
                label="url"
              >
                <el-input v-model="formApi.apiUrl" />
              </el-form-item>
            </el-col>
            <el-col
              v-if="formApi.apiType === 'DEFAULT'"
              :span="24"
            >
              <el-form-item
                prop="apiDataModule"
                :label="$t('reportMod.apiDataModule')"
              >
                <DictSelect
                  v-model="formApi.apiDataModule"
                  code="MODULE_DIVISION"
                />
              </el-form-item>
            </el-col>
            <el-col
              v-if="formApi.apiType === 'DEFAULT'"
              :span="24"
            >
              <el-form-item
                prop="apiSql"
                :label="$t('reportMod.defaultSql')"
              >
                <XmlEditor
                  v-model="formApi.apiSql"
                  :show-btns="false"
                  :lang="'zh'"
                  :mode="{name: 'xml'}"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </el-tab-pane>
      <el-tab-pane
        :label="$t('reportMod.eventMent')"
        name="EVENT"
      >
        <el-row>
          <el-col :span="24">
            <el-button
              type="primary"
              :disabled="readOnly"

              style="margin-bottom: 5px;"
              @click="addEvent()"
            >
              {{ $t('reportMod.addEvent') }}
            </el-button>
          </el-col>
        </el-row>

        <el-row>
          <el-col
            v-for="(eventItem, index) in eventList"
            :key="index"
            :span="12"
          >
            <el-card class="box-card">
              <div
                slot="header"
                class="clearfix"
              >
                <span>{{ eventItem.eventName }} - {{ eventItem.methodDescribe }}</span>
                <el-button
                  style="float: right; padding: 3px 0"
                  type="text"
                >
                  {{ $t('orderMod.buyerOrderSynergy.management') }}
                </el-button>
                <el-button
                  style="float: right; padding: 3px 0"
                  type="text"
                  @click="removeEvent(eventItem, index)"
                >
                  {{ $t('components.common.delete') }}
                </el-button>
              </div>
              <div>
                <el-form :ref="'eventForm' + index">
                  <el-row :gutter="32">
                    <el-col :span="24">
                      <el-form-item
                        prop="eventName"
                        :label="$t('reportMod.eventName')"
                      >
                        <el-input v-model="eventItem.eventName" />
                      </el-form-item>
                    </el-col>
                    <el-col :span="24">
                      <el-form-item
                        prop="methodType"
                        :label="$t('dataConfMod.eventType')"
                      >
                        <DictSelect
                          v-model="eventItem.methodType"
                          code="FORM_EVENT_TYPE"
                        />
                      </el-form-item>
                    </el-col>
                    <el-col
                      v-if="eventItem.methodType === 'NAME'"
                      :span="24"
                    >
                      <el-form-item
                        prop="methodName"
                        :label="$t('reportSetting.methodName')"
                      >
                        <el-input v-model="eventItem.methodName" />
                      </el-form-item>
                    </el-col>
                    <el-col :span="24">
                      <el-form-item
                        prop="methodDescribe"
                        :label="$t('statusConfig.eventName')"
                      >
                        <el-input v-model="eventItem.methodDescribe" />
                      </el-form-item>
                    </el-col>
                    <el-col
                      v-if="eventItem.methodType === 'DETAIL'"
                      :span="24"
                    >
                      <el-form-item
                        prop="methodParams"
                        :label="$t('reportMod.methodParamsInfo')"
                      >
                        <el-input v-model="eventItem.methodParams" />
                      </el-form-item>
                    </el-col>
                    <el-col
                      v-if="eventItem.methodType === 'DETAIL'"
                      :span="24"
                    >
                      <el-form-item
                        prop="methodBody"
                        :label="$t('reportMod.eventBody')"
                      >
                        <XmlEditor
                          v-model="eventItem.methodBody"
                          :show-btns="false"
                          :lang="'zh'"
                          :mode="{name: 'xml'}"
                        />
                      </el-form-item>
                    </el-col>
                  </el-row>
                </el-form>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>
      <el-tab-pane
        :label="$t('reportSetting.functionMent')"
        name="METHOD"
      >
        {{ $t('reportSetting.functionMent') }}
      </el-tab-pane>
      <el-tab-pane
        :label="$t('reportMod.ruleMent')"
        name="RULE"
      >
        {{ $t('reportMod.ruleMent') }}
      </el-tab-pane>
      <el-tab-pane
        :label="$t('reportMod.classMent')"
        name="CLASS"
      >
        {{ $t('reportMod.classMent') }}
      </el-tab-pane>
      <el-tab-pane
        :label="$t('reportMod.styleMent')"
        name="STYLE"
      >
        {{ $t('reportMod.styleMent') }}
      </el-tab-pane>
    </el-tabs>
    <template #footer>
      <el-button @click="cancel">
        {{ $t("common.cancel") }}
      </el-button>
      <el-button
        type="primary"
        @click="saveConfig"
      >
        {{ $t("common.confirm") }}
      </el-button>
    </template>
  </srm-dialog>
</template>

<script>
import XmlEditor from '@/components/XmlEditor'
export default {
  name: 'FormApiEdit',
  components: {
    XmlEditor
  },
  props: {
    visible: {
      type: Boolean,
      default: () => {
        return false
      }
    },
    totalConfig: {
      type: Object,
      default: () => {
        return {
          formApi: {},
          eventList: []
        }
      }
    }
  },
  data () {
    return {
      configType: 'PROP',
      readOnly: false,
      formApi: {
        apiType: null,
        httpMethod: null,
        apiUrl: null,
        apiDataModule: null,
        apiSql: null
      },
      eventList: [],
      propList: []
    }
  },
  watch: {
    totalConfig: {
      handler () {
        this.formApi = this.totalConfig.formApi
        this.eventList = this.totalConfig.eventList
        this.propList = this.totalConfig.propList
      },
      deep: true
    }
  },

  methods: {
    cancel () {
      this.$emit('cancel', this.getConfig())
    },
    saveConfig () {
      this.$emit('confirm', this.getConfig())
    },
    getConfig () {
      return {
        formApi: this.formApi,
        eventList: this.eventList,
        propList: this.propList
      }
    },
    addProp () {
      this.propList.push({
        formLineId: null,
        lineType: 'PROP',
        propName: '',
        propValueType: 'STRING',
        propValue: null,
        propDescribe: null,
        methodType: null,
        methodName: null,
        methodDescribe: null,
        methodParams: null,
        methodBody: null
      })
    },
    addEvent () {
      const currentEditEvent = {
        index: this.eventList.length,
        formLineId: null,
        lineType: 'EVENT',
        eventName: null,
        methodType: null,
        methodName: null,
        methodDescribe: null,
        methodParams: null,
        methodBody: null
      }
      this.eventList.push(currentEditEvent)
    },
    removeEvent (eventItem, index) {
      this.eventList.splice(index, 1)
    },
    removeProp (propItem, index) {
      this.propList.splice(index, 1)
    }
  }
}
</script>
