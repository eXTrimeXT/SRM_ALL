<!--  -->
<!--suppress AllyHtmlVueInspection -->
<template>
  <el-container class="flex-container">
    <el-main>
      <el-collapse
        v-model="activeDims"
        class="tab-form-style"
      >
        <el-collapse-item
          :title="$t('interfaceconfig.title1')"
          name="1"
        >
          <el-form
            ref="form"
            :model="allParams.form"
            label-width="100px"
            label-position="top"
            :rules="rules"
            class="form-incontainer"
          >
            <srm-row>
              <srm-col>
                <el-form-item
                  :label="$t('interfaceconfig.title2')"
                  prop="systemName"
                >
                  <QuickSearch
                    :show-input="allParams.form.systemName"
                    show-key="systemName"
                    :scope-data="allParams.form"
                    name="scc_api_system_config"
                    @close-quicksearch="getSystem"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  :label="$t('vendorMod.interfaceName')"
                  prop="interfaceName"
                >
                  <el-input v-model="allParams.form.interfaceName" />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  :label="$t('vendorMod.interfaceCode')"
                  prop="interfaceCode"
                >
                  <el-input v-model="allParams.form.interfaceCode" />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  :label="$t('interfaceconfig.title3')"
                  prop="source"
                >
                  <el-select v-model="allParams.form.source">
                    <el-option
                      v-for="item in sourceTempOpts"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </el-form-item>
              </srm-col>
            </srm-row>
            <!-- JDBC start  -->
            <srm-row
              v-if="allParams.form.source === 'JDBC'"
            >
              <srm-col>
                <el-form-item
                  :label="$t('interfaceconfig.title4')"
                  prop="interfaceUrl"
                  :rules="[{ required: true, message: $t('interfaceconfig.title44') }]"
                >
                  <el-input v-model="allParams.form.interfaceUrl" />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  :label="$t('interfaceconfig.title5')"
                  prop="dataConfig"
                  :rules="[{ required: true, message: $t('interfaceconfig.title55') }]"
                >
                  <DictSelect
                    v-model="allParams.form.dataConfig"
                    code="MODULE_DIVISION"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  :label="$t('interfaceconfig.title6')"
                  prop="dataType"
                  :rules="[{ required: true, message: $t('interfaceconfig.title66') }]"
                >
                  <DictSelect
                    v-model="allParams.form.dataType"
                    code="API_DATA_TYPE"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  :label="$t('interfaceconfig.title7')"
                  prop="outType"
                  :rules="[{ required: true, message: $t('interfaceconfig.title77') }]"
                >
                  <DictSelect
                    v-model="allParams.form.outType"
                    code="API_DATA_TYPE"
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
            <srm-row
              v-if="allParams.form.source === 'JDBC'"
            >
              <srm-col>
                <el-form-item
                  :label="$t('interfaceconfig.title8')"
                  prop="method"
                  :rules="[{ required: true, message: $t('interfaceconfig.title88') }]"
                >
                  <DictSelect
                    v-model="allParams.form.method"
                    code="API_METHOD"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  :label="$t('interfaceconfig.title9')"
                  prop="paramStruct"
                  :rules="[{ required: true, message: $t('interfaceconfig.title99') }]"
                >
                  <DictSelect
                    v-model="allParams.form.paramStruct"
                    code="API_PARAM_STRUCT"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('interfaceconfig.title10')">
                  <el-input v-model="allParams.form.returnClass" />
                </el-form-item>
              </srm-col>

              <srm-col>
                <el-form-item
                  :label="$t('interfaceconfig.title11')"
                  prop="ifSyn"
                >
                  <DictSelect
                    v-model="allParams.form.ifSyn"
                    code="YES_OR_NO"
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
            <srm-row
              v-if="allParams.form.source === 'JDBC'"
            >
              <srm-col :initCol="1">
                <el-form-item
                  :label="$t('interfaceconfig.title12')"
                  prop="dataSource"
                  :rules="[{ required: true, message: $t('interfaceconfig.title122') }]"
                >
                  <el-input
                    v-model="allParams.form.dataSource"
                    type="textarea"
                    :rows="2"
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
            <!-- JDBC end -->
            <!-- http start -->
            <srm-row
              v-if="allParams.form.source === 'HTTP'"
            >
              <srm-col>
                <el-form-item
                  :label="$t('interfaceconfig.title13')"
                  prop="interfaceUrl"
                  :rules="[{ required: true, message: $t('interfaceconfig.title133') }]"
                >
                  <el-input v-model="allParams.form.interfaceUrl" />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  :label="$t('interfaceconfig.title14')"
                  prop="method"
                  :rules="[{ required: true, message: $t('interfaceconfig.title88') }]"
                >
                  <DictSelect
                    v-model="allParams.form.method"
                    code="API_METHOD"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  :label="$t('interfaceconfig.title15')"
                  prop="dataType"
                  :rules="[{ required: true, message: $t('interfaceconfig.title66') }]"
                >
                  <DictSelect
                    v-model="allParams.form.dataType"
                    code="API_DATA_TYPE"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  :label="$t('interfaceconfig.title16')"
                  prop="outType"
                  :rules="[{ required: true, message: $t('interfaceconfig.title77') }]"
                >
                  <DictSelect
                    v-model="allParams.form.outType"
                    code="API_DATA_TYPE"
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
            <srm-row v-if="allParams.form.source === 'HTTP'">
              <srm-col>
                <el-form-item
                  :label="$t('interfaceconfig.title17')"
                  prop="paramStruct"
                  :rules="[{ required: true, message: $t('interfaceconfig.title99') }]"
                >
                  <DictSelect
                    v-model="allParams.form.paramStruct"
                    code="API_PARAM_STRUCT"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('interfaceconfig.title18')">
                  <el-input v-model="allParams.form.returnClass" />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  :label="$t('interfaceconfig.title19')"
                  prop="ifSyn"
                >
                  <DictSelect
                    v-model="allParams.form.ifSyn"
                    code="YES_OR_NO"
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
            <srm-row
              v-if="allParams.form.source === 'HTTP' && allParams.form.method === 'POST' && allParams.form.paramStruct === 'JSON'"
            >
              <srm-col :initCol="1">
                <el-form-item
                  :label="$t('interfaceconfig.title20')"
                  prop="demoText"
                  :rules="[{ required: true, message: $t('interfaceconfig.title200') }]"
                >
                  <el-input
                    v-model="allParams.form.demoText"
                    type="textarea"
                    :rows="2"
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
            <!-- http end -->
            <!-- JDBC_EXCUTE start -->
            <srm-row
              v-if="allParams.form.source === 'JDBC_EXCUTE'"
            >
              <srm-col :initCol="1">
                <el-form-item
                  :label="$t('todolistConfig.sqlLanguage')"
                  prop="dataSource"
                  :rules="[{ required: true, message: $t('interfaceconfig.title122') }]"
                >
                  <el-input
                    v-model="allParams.form.dataSource"
                    type="textarea"
                    :rows="20"
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
            <!-- JDBC_EXCUTE end -->

            <!-- JDBC_RE start -->
            <srm-row
              v-if="allParams.form.source === 'JDBC_RE'"
            >
              <srm-col>
                <el-form-item
                  :label="$t('interfaceconfig.title8')"
                  prop="method"
                  :rules="[{ required: true, message: $t('interfaceconfig.title88') }]"
                >
                  <DictSelect
                    v-model="allParams.form.method"
                    code="API_METHOD"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <!--suppress AllyHtmlVueInspection -->
                <el-form-item
                  :label="$t('interfaceconfig.title21')"
                  prop="dataFormat"
                >
                  <DictSelect
                    v-model="allParams.form.dataFormat"
                    code="API_DATA_FORMAT"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  :label="$t('interfaceconfig.title6')"
                  prop="dataType"
                  :rules="[{ required: true, message: $t('interfaceconfig.title66') }]"
                >
                  <DictSelect
                    v-model="allParams.form.dataType"
                    code="API_DATA_TYPE"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  :label="$t('interfaceconfig.title7')"
                  prop="outType"
                  :rules="[{ required: true, message: $t('interfaceconfig.title77') }]"
                >
                  <DictSelect
                    v-model="allParams.form.outType"
                    code="API_DATA_TYPE"
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
            <srm-row
              v-if="allParams.form.source === 'JDBC_RE'"
            >
              <srm-col>
                <el-form-item
                  :label="$t('interfaceconfig.title5')"
                  prop="dataConfig"
                  :rules="[{ required: true, message: $t('interfaceconfig.title55') }]"
                >
                  <DictSelect
                    v-model="allParams.form.dataConfig"
                    code="MODULE_DIVISION"
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
            <srm-row
              v-if="allParams.form.source === 'JDBC_RE'"
            >
              <srm-col :initCol="1">
                <el-form-item
                  :label="$t('todolistConfig.sqlLanguage')"
                  prop="dataSource"
                  :rules="[{ required: true, message: $t('interfaceconfig.title122') }]"
                >
                  <el-input
                    v-model="allParams.form.dataSource"
                    type="textarea"
                    :rows="2"
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
            <!-- JDBC_RE end -->

            <!-- HTTP_RE start -->
            <srm-row
              v-if="allParams.form.source === 'HTTP_RE'"
            >
              <srm-col>
                <el-form-item
                  :label="$t('interfaceconfig.title8')"
                  prop="method"
                  :rules="[{ required: true, message: $t('interfaceconfig.title88') }]"
                >
                  <DictSelect
                    v-model="allParams.form.method"
                    code="API_METHOD"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  :label="$t('interfaceconfig.title21')"
                  prop="dataFormat"
                >
                  <DictSelect
                    v-model="allParams.form.dataFormat"
                    code="API_DATA_FORMAT"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  :label="$t('interfaceconfig.title6')"
                  prop="dataType"
                  :rules="[{ required: true, message: $t('interfaceconfig.title66') }]"
                >
                  <DictSelect
                    v-model="allParams.form.dataType"
                    code="API_DATA_TYPE"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  :label="$t('interfaceconfig.title7')"
                  prop="outType"
                  :rules="[{ required: true, message: $t('interfaceconfig.title77') }]"
                >
                  <DictSelect
                    v-model="allParams.form.outType"
                    code="API_DATA_TYPE"
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
            <srm-row
              v-if="allParams.form.source === 'HTTP_RE'"
            >
              <srm-col>
                <el-form-item
                  :label="$t('interfaceconfig.title11')"
                  prop="ifSyn"
                >
                  <DictSelect
                    v-model="allParams.form.ifSyn"
                    code="YES_OR_NO"
                  />
                </el-form-item>
              </srm-col>
              <srm-col
                v-if="allParams.form.method === 'POST' "
              >
                <el-form-item
                  :label="$t('interfaceconfig.title22')"
                  prop="ifNeedParam"
                >
                  <DictSelect
                    v-model="allParams.form.ifNeedParam"
                    code="YES_OR_NO"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  :label="$t('interfaceconfig.title23')"
                  prop="dataSource"
                  :rules="[{ required: true, message: $t('interfaceconfig.title233') }]"
                >
                  <el-input v-model="allParams.form.dataSource" />
                </el-form-item>
              </srm-col>
            </srm-row>
            <srm-row
              v-if="allParams.form.source === 'HTTP_RE' && allParams.form.method === 'POST'"
            >
              <srm-col :initCol="1">
                <el-form-item
                  :label="$t('interfaceconfig.title20')"
                  prop="demoText"
                  :rules="[{ required: true, message: $t('interfaceconfig.title200') }]"
                >
                  <el-input
                    v-model="allParams.form.demoText"
                    type="textarea"
                    :rows="2"
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
            <!-- HTTP_RE end -->
          </el-form>
        </el-collapse-item>
        <!--SQL参数 -->
        <el-collapse-item
          v-if="
            allParams.form.source === 'JDBC' && allParams.form.ifNeedParam === 'Y' "
          :title="$t('interfaceconfig.title24')"
          name="2"
        >
          <p style="margin: 0 0 10px 0;">
            <el-button
              type="primary"
              @click="addSqlParam"
            >
              {{ $t('components.viewSwitcher.add') }}
            </el-button>
          </p>
          <el-table
            :data="allParams.sqlParams"
            style="width: 100%"
            border
            use-virtual
            :row-height="37"
            max-height="410px"
          >
            <el-table-column
              align="center"
              type="index"
              width="50"
              fixed="left"
            />

            <el-table-column
              align="center"
              prop="columnDesc"
              :label="$t('quoteTemplate.fun.argName')"
              min-width="100"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.columnDesc"
                />
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              prop="columnName"
              :label="$t('quoteTemplate.fun.argName1')"
              min-width="100"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.columnName"
                />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="columnType"
              :label="$t('quoteTemplate.fun.argType')"
              min-width="100"
            >
              <template slot-scope="scope">
                <DictSelect
                  v-model="scope.row.columnType"
                  code="API_COLUMN_TYPE"
                  style="width:100%"
                />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="ifRequirye"
              :label="$t('dataConfMod.isRequested')"
              min-width="80"
            >
              <template slot-scope="scope">
                <el-checkbox
                  v-model="scope.row.ifRequirye"
                  true-label="Y"
                  false-label="N"
                />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="defaultValue"
              :label="$t('quoteTemplate.defaultValue')"
              min-width="100"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.defaultValue"
                />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="operation"
              :label="$t('formula.handle')"
              width="140"
            >
              <template slot-scope="scope">
                <el-button
                  :disabled="curOpt === 'view'"
                  type="text"
                  @click="delSqlParam(scope.$index, scope.row)"
                >
                  {{ $t('components.common.delete') }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-collapse-item>

        <!--参数 -->
        <el-collapse-item
          v-if="
            ( allParams.form.source === 'JDBC' ||
              allParams.form.source === 'HTTP' ||
              allParams.form.source === 'JDBC_RE' ||
              allParams.form.source === 'HTTP_RE' )
              && allParams.form.ifNeedParam === 'Y'
          "
          :title="$t('interfaceconfig.title25')"
          name="2"
        >
          <p style="margin: 0 0 10px 0;">
            <el-button
              v-if="allParams.form.source === 'HTTP'
                && allParams.form.method === 'POST' && allParams.form.paramStruct === 'JSON'"
              type="primary"
              @click="refreshParam"
            >
              {{ $t("common.refresh") }}
            </el-button>
            <el-button
              v-if="allParams.form.source === 'JDBC'"
              type="primary"
              @click="refreshSql"
            >
              {{ $t("common.refresh") }}
            </el-button>
            <el-button
              type="primary"
              @click="addParam"
            >
              {{ $t('components.viewSwitcher.add') }}
            </el-button>
          </p>
          <el-table
            :data="allParams.params"
            style="width: 100%"
            border
            use-virtual
            :row-height="37"
            max-height="410px"
          >
            <el-table-column
              align="center"
              type="index"
              width="50"
              fixed="left"
            />

            <el-table-column
              align="center"
              prop="columnDesc"
              :label="$t('quoteTemplate.fun.argName')"
              min-width="100"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.columnDesc"
                />
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              prop="columnName"
              :label="$t('quoteTemplate.fun.argName1')"
              min-width="100"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.columnName"
                />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="columnType"
              :label="$t('quoteTemplate.fun.argType')"
              min-width="100"
            >
              <template slot-scope="scope">
                <DictSelect
                  v-model="scope.row.columnType"
                  code="API_COLUMN_TYPE"
                  style="width:100%"
                />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="ifRequirye"
              :label="$t('dataConfMod.isRequested')"
              min-width="80"
            >
              <template slot-scope="scope">
                <el-checkbox
                  v-model="scope.row.ifRequirye"
                  true-label="Y"
                  false-label="N"
                />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="converName"
              :label="$t('interfaceconfig.title26')"
              min-width="100"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.converName"
                />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="defaultValue"
              :label="$t('quoteTemplate.defaultValue')"
              min-width="100"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.defaultValue"
                />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="source"
              :label="$t('interfaceconfig.title27')"
              min-width="100"
            >
              <template slot-scope="scope">
                <DictSelect
                  v-model="scope.row.source"
                  code="API_COLUMN_SOURCE"
                  style="width:100%"
                />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="dataConfig"
              :label="$t('interfaceconfig.title5')"
              min-width="100"
            >
              <template slot-scope="scope">
                <DictSelect
                  v-model="scope.row.dataConfig"
                  code="MODULE_DIVISION"
                  style="width:100%"
                  :disabled="scope.row.source !== 'SQL'"
                />
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              prop="sqlText"
              label="SQL"
              min-width="200"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.sqlText"
                  :disabled="scope.row.source !== 'SQL'"
                />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="operation"
              :label="$t('formula.handle')"
              width="140"
            >
              <template slot-scope="scope">
                <el-button
                  v-if="
                    scope.row.columnType === 'Map' ||
                      scope.row.columnType === 'LIST'
                  "
                  type="text"
                  @click="addChildP(scope.$index, scope.row)"
                >
                  {{ $t('interfaceconfig.title33') }}
                </el-button>
                <el-button
                  :disabled="curOpt === 'view'"
                  type="text"
                  @click="delParam(scope.$index, scope.row)"
                >
                  {{ $t('components.common.delete') }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-collapse-item>
        <!--字段 -->
        <el-collapse-item
          v-if="
            (allParams.form.source === 'JDBC' ||
              allParams.form.source === 'HTTP' ||
              allParams.form.source === 'JDBC_RE' ||
              allParams.form.source === 'HTTP_RE' ) && allParams.form.ifNeedResult === 'Y'
          "
          :title="$t('interfaceconfig.title28')"
          name="3"
        >
          <p style="margin: 0 0 10px 0;">
            <el-button
              type="primary"
              @click="refreshColumn"
            >
              {{ $t("common.refresh") }}
            </el-button>
            <el-button
              type="primary"
              @click="addColumn"
            >
              {{ $t("common.new") }}
            </el-button>
          </p>
          <el-table
            :data="allParams.columns"
            style="width: 100%"
            border
            use-virtual
            :row-height="37"
            max-height="410px"
          >
            <el-table-column
              align="center"
              type="index"
              width="50"
              fixed="left"
            />

            <el-table-column
              align="center"
              prop="columnDesc"
              :label="$t('quoteTemplate.fieldName')"
              min-width="100"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.columnDesc"
                />
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              prop="columnName"
              :label="$t('monitorBizConfig.columnName')"
              min-width="100"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.columnName"
                />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="columnType"
              :label="$t('contract_mod.fieldType')"
              min-width="100"
            >
              <template slot-scope="scope">
                <DictSelect
                  v-model="scope.row.columnType"
                  code="API_COLUMN_TYPE"
                  style="width:100%"
                />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="converName"
              :label="$t('interfaceconfig.title29')"
              min-width="100"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.converName"
                />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="converType"
              :label="$t('interfaceconfig.title30')"
              min-width="100"
            >
              <template slot-scope="scope">
                <DictSelect
                  v-model="scope.row.converType"
                  code="API_COLUMN_TYPE"
                  style="width:100%"
                />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="defaultValue"
              :label="$t('quoteTemplate.defaultValue')"
              min-width="100"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.defaultValue"
                />
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              prop="correctValue"
              :label="$t('interfaceconfig.title31')"
              min-width="100"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.correctValue"
                />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="source"
              :label="$t('interfaceconfig.title32')"
              min-width="100"
            >
              <template slot-scope="scope">
                <DictSelect
                  v-model="scope.row.source"
                  code="API_COLUMN_SOURCE"
                  style="width:100%"
                />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="dataConfig"
              :label="$t('interfaceconfig.title5')"
              min-width="100"
            >
              <template slot-scope="scope">
                <DictSelect
                  v-model="scope.row.dataConfig"
                  code="MODULE_DIVISION"
                  style="width:100%"
                  :disabled="scope.row.source !== 'SQL'"
                />
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              prop="sqlText"
              label="SQL"
              min-width="200"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.sqlText"
                  :disabled="scope.row.source !== 'SQL'"
                />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="operation"
              :label="$t('formula.handle')"
              width="140"
            >
              <template slot-scope="scope">
                <el-button
                  v-if="
                    scope.row.columnType === 'Map' ||
                      scope.row.columnType === 'LIST'
                  "
                  type="text"
                  @click="addChild(scope.$index, scope.row)"
                >
                  {{ $t('interfaceconfig.title33') }}
                </el-button>
                <el-button
                  :disabled="curOpt === 'view'"
                  type="text"
                  @click="delColumn(scope.$index, scope.row)"
                >
                  {{ $t('components.common.delete') }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-collapse-item>
      </el-collapse>
    </el-main>
    <srm-dialog
      :close-on-click-modal="false"
      :visible.sync="dialogColumnVisible"
      :title="$t('common.add')"
      size="large"
    >
      <el-collapse
        v-model="activeDims"
        class="tab-form-style"
      >
        <el-collapse-item
          :title="$t('interfaceconfig.title34')"
          name="4"
        >
          <el-button
            type="primary"
            @click="refreshChildColumn"
          >
            {{ $t('bidMod.refresh') }}
          </el-button>
          <el-button
            type="primary"
            @click="addChildColumn"
          >
            {{ $t('components.viewSwitcher.add') }}
          </el-button>
          <el-table
            :data="childColumns"
            style="width: 100%"
            border
            use-virtual
            :row-height="37"
            max-height="250px"
          >
            <el-table-column
              align="center"
              type="index"
              width="50"
              fixed="left"
            />

            <el-table-column
              align="center"
              prop="columnDesc"
              :label="$t('quoteTemplate.fieldName')"
              min-width="100"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.columnDesc"
                />
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              prop="columnName"
              :label="$t('monitorBizConfig.columnName')"
              min-width="100"
            >
              <template slot-scope="scope">
                <el-input v-model="scope.row.columnName" />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="columnType"
              :label="$t('contract_mod.fieldType')"
              min-width="100"
            >
              <template slot-scope="scope">
                <DictSelect
                  v-model="scope.row.columnType"
                  code="API_COLUMN_TYPE"
                  style="width:100%"
                />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="converName"
              :label="$t('interfaceconfig.title29')"
              min-width="100"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.converName"
                  :disabled="curOpt === 'view'"
                />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="converType"
              :label="$t('interfaceconfig.title30')"
              min-width="100"
            >
              <template slot-scope="scope">
                <DictSelect
                  v-model="scope.row.converType"
                  code="API_COLUMN_TYPE"
                />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="defaultValue"
              :label="$t('quoteTemplate.defaultValue')"
              min-width="100"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.defaultValue"
                />
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              prop="correctValue"
              :label="$t('interfaceconfig.title31')"
              min-width="100"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.correctValue"
                />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="source"
              :label="$t('interfaceconfig.title32')"
              min-width="100"
            >
              <template slot-scope="scope">
                <DictSelect
                  v-model="scope.row.source"
                  code="API_COLUMN_SOURCE"
                  style="width:100%"
                />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="dataConfig"
              :label="$t('interfaceconfig.title5')"
              min-width="100"
            >
              <template slot-scope="scope">
                <DictSelect
                  v-model="scope.row.dataConfig"
                  code="MODULE_DIVISION"
                  style="width:100%"
                  :disabled="scope.row.source !== 'SQL'"
                />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="sqlText"
              label="SQL"
              min-width="200"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.sqlText"
                  :disabled="scope.row.source !== 'SQL'"
                />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="operation"
              :label="$t('formula.handle')"
              width="120px"
            >
              <template slot-scope="scope">
                <el-button
                  v-if="
                    scope.row.columnType === 'Map' ||
                      scope.row.columnType === 'LIST'
                  "
                  type="text"
                  @click="addColumnChild(scope.$index, scope.row)"
                >
                  {{ $t('interfaceconfig.title33') }}
                </el-button>
                <el-button
                  :disabled="curOpt === 'view'"
                  type="text"
                  @click="delChildColumn(scope.$index, scope.row)"
                >
                  {{ $t('components.common.delete') }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-collapse-item>
      </el-collapse>
      <div
        slot="footer"
        class="dialog-footer"
      >
        <el-button
          @click="dialogColumnVisible = false"
        >
          {{
            $t("common.cancel")
          }}
        </el-button>
        <el-button
          type="primary"
          @click="sure"
        >
          {{
            $t("common.confirm")
          }}
        </el-button>
      </div>
    </srm-dialog>

    <srm-dialog
      :close-on-click-modal="false"
      :visible.sync="dialogParamVisible"
      :title="$t('common.view')"
      size="large"
    >
      <el-collapse
        v-model="activeDims"
        class="tab-form-style"
      >
        <el-collapse-item
          :title="$t('interfaceconfig.title35')"
          name="5"
        >
          <el-button
            type="primary"
            @click="addChildParam"
          >
            {{ $t('components.viewSwitcher.add') }}
          </el-button>
          <el-table
            :data="childParam"
            style="width: 100%"
            border
            use-virtual
            :row-height="37"
            max-height="410px"
          >
            <el-table-column
              align="center"
              type="index"
              width="50"
              fixed="left"
            />

            <el-table-column
              align="center"
              prop="columnDesc"
              :label="$t('quoteTemplate.fun.argName')"
              min-width="100"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.columnDesc"
                />
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              prop="columnName"
              :label="$t('quoteTemplate.fun.argName1')"
              min-width="100"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.columnName"
                />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="columnType"
              :label="$t('quoteTemplate.fun.argType')"
              min-width="100"
            >
              <template slot-scope="scope">
                <DictSelect
                  v-model="scope.row.columnType"
                  code="API_COLUMN_TYPE"
                  style="width:100%"
                />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="ifRequirye"
              :label="$t('dataConfMod.isRequested')"
              min-width="80"
            >
              <template slot-scope="scope">
                <el-checkbox
                  v-model="scope.row.ifRequirye"
                  true-label="Y"
                  false-label="N"
                />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="converName"
              :label="$t('interfaceconfig.title26')"
              min-width="100"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.converName"
                />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="defaultValue"
              :label="$t('quoteTemplate.defaultValue')"
              min-width="100"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.defaultValue"
                />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="source"
              :label="$t('interfaceconfig.title27')"
              min-width="100"
            >
              <template slot-scope="scope">
                <DictSelect
                  v-model="scope.row.source"
                  code="API_COLUMN_SOURCE"
                  style="width:100%"
                />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="dataConfig"
              :label="$t('interfaceconfig.title5')"
              min-width="100"
            >
              <template slot-scope="scope">
                <DictSelect
                  v-model="scope.row.dataConfig"
                  code="MODULE_DIVISION"
                  style="width:100%"
                  :disabled="scope.row.source !== 'SQL'"
                />
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              prop="sqlText"
              label="SQL"
              min-width="200"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.sqlText"
                  :disabled="scope.row.source !== 'SQL'"
                />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="operation"
              :label="$t('formula.handle')"
              width="140"
            >
              <template slot-scope="scope">
                <el-button
                  v-if="
                    scope.row.columnType === 'Map' ||
                      scope.row.columnType === 'LIST'
                  "
                  type="text"
                  @click="addParamChild(scope.$index, scope.row)"
                >
                  {{ $t('interfaceconfig.title33') }}
                </el-button>
                <el-button
                  :disabled="curOpt === 'view'"
                  type="text"
                  @click="delChildParams(scope.$index, scope.row)"
                >
                  {{ $t('components.common.delete') }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-collapse-item>
      </el-collapse>
      <div
        slot="footer"
        class="dialog-footer"
      >
        <el-button
          @click="dialogParamVisible = false"
        >
          {{
            $t("common.cancel")
          }}
        </el-button>
        <el-button
          type="primary"
          @click="sureParam"
        >
          {{
            $t("common.confirm")
          }}
        </el-button>
      </div>
    </srm-dialog>

    <srm-dialog
      :close-on-click-modal="false"
      :visible.sync="dialogShowDocVisible"
      title="showDoc"
      size="large"
    >
      <el-collapse
        v-model="activeDims"
        class="tab-form-style"
      >
        <srm-row>
          <srm-col :initCol="1">
            <el-input
              id="showDoc"
              v-model="showDoc"
              type="textarea"
              :rows="23"
            />
          </srm-col>
        </srm-row>
      </el-collapse>
    </srm-dialog>

    <CToolbar>
      <template #right>
        <el-button
          @click="cancel"
        >
          {{
            $t("common.cancel")
          }}
        </el-button>
        <el-button
          type="primary"
          @click="showDocClick"
        >
          <!-- 生成showDoc -->
          {{ $t('cusEntry.supplement20250211.showDocGenerator') }}
        </el-button>
        <el-button
          type="primary"
          @click="testInterface"
        >
          <!-- 调试 -->
          {{ $t('cusEntry.supplement20250211.debugVariable') }}
        </el-button>
        <el-button
          type="primary"
          @click="save"
        >
          {{
            $t("common.submit")
          }}
        </el-button>
      </template>
    </CToolbar>
  </el-container>
</template>

<script>
import CToolbar from 'lib@/components/c-toolbar'
import QuickSearch from 'lib@/components/QuickSearch'
import { getDictItemList } from '@/api/common'
import { adaptDictData } from '@/utils'
import { tabTodoMixin, tabTodoWatch } from '@/utils/mixins'
import { interfaceApi } from 'mod@/common/userManage/api'

export default {
  name: 'InterfaceconfigEdit',
  components: { CToolbar, QuickSearch },
  mixins: [tabTodoMixin, tabTodoWatch],
  props: {
    params: {
      type: Object,
      default () {
        return {}
      }
    }
  },
  data () {
    return {
      dialogColumnVisible: false,
      dialogParamVisible: false,
      dialogShowDocVisible: false,
      activeDims: ['1', '2', '3', '4', '5'],
      curOpt: 'add',
      childColumns: [],
      childParam: [],
      paramParentId: null,
      allColumns: [],
      allParam: [],
      showDoc: null,
      allParams: {
        form: {
          interfaceId: null,
          interfaceName: null,
          interfaceCode: null,
          interfaceUrl: null,
          systemId: null,
          systemName: '',
          source: null,
          dataSource: null,
          ifChange: null,
          dataFormat: null,
          dataStructure: null,
          returnClass: null,
          dataType: null,
          dataConfig: null,
          method: null,
          paramStruct: null,
          ifNeedParam: null,
          ifNeedResult: null,
          ifSyn: null

        },
        params: [],
        columns: [],
        childParam: [],
        childColumns: [],
        opType: '',
        // 子层
        childSource: '',
        parentId: null,
        dataConfig: null,
        sqlText: '',
        // 字典相关
        sourceOpts: [],
        sourceTempOpts: [],
        sqlParams: []
      },
      rules: {
        systemName: [
          { required: true, message: this.$t('vendor.selectSystem'), trigger: 'blur' }
        ],
        interfaceName: [{ required: true, message: this.$t('vendor.enterInterface') }],
        interfaceCode: [{ required: true, message: this.$t('vendor.enterInterfaceCode') }],
        source: [{ required: true, message: this.$t('vendor.selectScene') }]
      }
    }
  },
  computed: {},
  watch: {},
  created () {
  },
  async mounted () {
    await this.initDictionary()
    await this.initData()
  },
  methods: {
    cancel () {
      if (this.params.flag === 'add') {
        this.$emit('tab-remove', 'interfaceconfigEdit')
      } else {
        this.$emit(
          'tab-remove',
          'interfaceconfigEdit' + this.params.interfaceId
        )
      }
      this.__setTabTodo('interfaceconfigList.getQuerydata')
    },
    async initData () {
      if (this.params.flag === 'edit') {
        const interfaceId = this.params.interfaceId
        const res = await interfaceApi.getInterfaceconfig({ interfaceId })
        this.allParams = res.data
        this.allColumns = res.data.childColumns
        this.allParam = res.data.childParams
        this.allParams.sqlParams = (res.data.sqlParams == null ? [] : res.data.sqlParams)
      } else {
        this.allParams.form.ifSyn = 'N'
        this.allParams.form.ifNeedParam = 'Y'
        this.allParams.form.ifNeedResult = 'Y'
      }
    },
    async initDictionary () {
      const codes = [
        'API_SOURCE'
      ].map(i => ({
        dictCode: i
      }))
      const res = await getDictItemList(codes)
      const [
        API_SOURCE
      ] = res.data
      this.sourceOpts = adaptDictData(API_SOURCE.API_SOURCE)
    },
    save () {
      this.$refs.form.validate(valid => {
        if (!valid) {
          this.$message({
            message: this.$t('vendorMod.pleasefinishRequired'),
            type: 'error'
          })
          return false
        }
        if (!this.checkLine()) {
          return
        }
        this.saveDataHandle()
      })
    },
    checkLine () {
      if (this.allParams.form.source == 'JDBC') {
        for (let i = 0; i < this.allParams.sqlParams.length; i++) {
          if (!this.checkColumn(this.allParams.sqlParams[i], this.$t('interfaceconfig.title24'))) {
            return false
          }
        }
      }
      for (let i = 0; i < this.allParams.params.length; i++) {
        if (!this.checkColumn(this.allParams.params[i], this.$t('interfaceconfig.title25'))) {
          return false
        }
      }
      for (let i = 0; i < this.allParam.length; i++) {
        if (!this.checkColumn(this.allParam[i], this.$t('interfaceconfig.title36'))) {
          return false
        }
      }
      for (let i = 0; i < this.allParams.columns.length; i++) {
        if (!this.checkColumn(this.allParams.columns[i], this.$t('interfaceconfig.title28'))) {
          return false
        }
      }
      for (let i = 0; i < this.allColumns.length; i++) {
        if (!this.checkColumn(this.allColumns[i], this.$t('interfaceconfig.title37'))) {
          return false
        }
      }
      return true
    },
    checkColumn (column, remark) {
      if (!column.columnDesc) {
        this.$message.warning(remark + ', ' + this.$t('vendor.fieldNameRequired'))
        return
      }
      if (!column.columnName) {
        this.$message.warning(remark + ', ' + this.$t('vendor.fieldCodeRequired'))
        return
      }
      if (!column.columnType) {
        this.$message.warning(remark + ', ', + this.$t('vendor.fieldTypeRequired'))
        return
      }
      return true
    },
    // 保存数据操作
    saveDataHandle () {
      let submitData = this.allParams
      if (this.allColumns && this.allColumns.length > 0) {
        submitData.childColumns = this.allColumns
      }
      if (this.allParam && this.allParam.length > 0) {
        submitData.childParams = this.allParam
      }
      interfaceApi.interfaceconfigSave(submitData)
        .then(res => {
          this.$message.success(res.message)
          this.cancel()
        })
        .catch(err => {
          console.log(err)
        })
    },
    // 对话框
    closeDialog () {
      this.allParams.form = {}
      this.allParams.columns = []
      this.allParams.params = []
      this.dialogVisible = false
    },
    showDialog () {
      this.dialogVisible = true
    },
    addParam () {
      this.allParams.params.push({
        columnId: new Date().getTime(),
        source: 'MANUAL',
        ifRequirye: 'Y'
      })
    },
    delParam (index, row) {
      this.allParams.params.splice(index, 1)
    },
    addColumn () {
      this.allParams.columns.push({
        columnId: new Date().getTime(),
        interfaceId: null,
        columnDesc: null,
        columnName: null,
        columnType: null,
        converName: null,
        converType: null,
        parentId: null,
        childSource: null,
        source: 'MANUAL',
        sqlText: null
      })
    },
    delColumn (index, row) {
      this.allParams.columns.splice(index, 1)
    },
    async refreshColumn () {
      let submitData = this.allParams
      if (this.allColumns && this.allColumns.length > 0) {
        submitData.childColumns = this.allColumns
      }
      if (this.allParam && this.allParam.length > 0) {
        submitData.childParams = this.allParam
      }
      const res = await interfaceApi.getHttpColumns(submitData)
      if (res.data.length > 0) {
        this.allParams.columns = []
        this.allColumns = []
        for (var i = 0; i < res.data.length; i++) {
          if (res.data[i].parentId === null) {
            this.allParams.columns.push(res.data[i])
          } else {
            this.allColumns.push(res.data[i])
          }
        }
      }
    },
    addChild (index, row) {
        this.parentId = row.columnId
        this.dataConfig = row.dataConfig
        this.childSource = row.childSource
        this.sqlText = row.sqlText
        this.dialogColumnVisible = true
        this.childColumns = this.allColumns.filter(i => i.parentId === this.parentId)
    },
    addColumnChild (index, row) {
      // 把当前字段放入集合
      this.pushAllColumn()
      // 获取子层信息
      this.addChild(index, row)
    },
    sure () {
      this.pushAllColumn()
      this.dialogColumnVisible = false
      this.dialogParamVisible = false
      this.dialogShowDocVisible = false
      this.parentId = null
      this.resultParentId = null
      this.dataConfig = null
      this.childColumns = []
      this.childResults = []
      this.childSource = ''
    },
    pushAllColumn () {
      var tempColumn = []
      if (this.allColumns.length > 0) {
        for (var i = 0; i < this.allColumns.length; i++) {
          if (this.allColumns[i].parentId != this.parentId) {
            tempColumn.push(this.allColumns[i])
          }
        }
      }
      this.allColumns = tempColumn.concat(this.childColumns)
    },
    async refreshChildColumn () {
      var sql = this.sqlText
      var dataConfig = this.dataConfig
      const res = await interfaceApi.getColumnBySql({ sql, dataConfig })
      if (res.data.length > 0) {
        for (var i = 0; i < res.data.length; i++) {
          var id = new Date().getTime() * -1
          res.data[i].parentId = this.parentId
          res.data[i].columnId = id
          id++
          this.childColumns.push(res.data[i])
        }
      }
    },
    addChildColumn () {
      this.childColumns.push({
        columnId: new Date().getTime(),
        interfaceId: null,
        columnDesc: null,
        columnName: null,
        columnType: null,
        converName: null,
        converType: null,
        parentId: this.parentId,
        childSource: null,
        source: 'MANUAL',
        sqlText: null
      })
    },
    delChildColumn (index, row) {
      this.childColumns.splice(index, 1)
    },
    getSystem (val, data) {
      data.systemId = val ? val.systemId : ''
      data.systemName = val ? val.systemName : ''
      data.type = val ? val.type : ''
      data.protocol = val ? val.protocol : ''
      let sourceOpts =
      this.sourceTempOpts = this.sourceOpts.filter(i => i.desc === data.type)
    },
    async refreshResult () {
      let submitData = this.allParams
      submitData.childColumns = this.allColumns
      let res = null
      if (this.allParams.form.source === 'HTTP') {
        res = await interfaceApi.getHttpColumns(submitData)
      } else {
        res = await interfaceApi.getSqlResults(submitData)
      }
      if (res.data && res.data.length > 0) {
        this.allParams.results = res.data.filter(i => !i.parentId)
        this.allResults = res.data.filter(i => i.parentId)
        }
    },
    addChildParam () {
      this.childParam.push({
        columnId: new Date().getTime(),
        source: 'MANUAL',
        ifRequirye: 'Y',
        parentId: this.paramParentId
      })
    },
    delChildParams (index, row) {
      this.childParam.splice(index, 1)
    },
    addChildP (index, row) {
      this.paramParentId = row.columnId
      this.dialogParamVisible = true
      this.childParam = this.allParam.filter(i => i.parentId === row.columnId)
    },
    addParamChild (index, row) {
      // 把当前字段放入集合
      this.pushAllParam()
      // 获取子层信息
      this.addChildP(index, row)
    },
    pushAllParam () {
      var tempParam = []
      if (this.allParam.length > 0) {
        for (var i = 0; i < this.allParam.length; i++) {
          if (this.allParam[i].parentId != this.paramParentId) {
            tempParam.push(this.allParam[i])
          }
        }
      }
      this.allParam = tempParam.concat(this.childParam)
    },
    sureParam () {
      this.pushAllParam()
      this.dialogParamVisible = false
      this.paramParentId = null
      this.childParam = []
    },
    addSqlParam () {
      this.allParams.sqlParams.push({
        columnId: new Date().getTime(),
        ifRequirye: 'Y',
        source: 'MANUAL'
      })
    },
    delSqlParam (index, row) {
      this.allParams.sqlParams.splice(index, 1)
    },
    async refreshSql () {
      var sql = this.allParams.form.dataSource
      var dataConfig = this.allParams.form.dataConfig
      const res = await interfaceApi.getColumnBySql({ sql, dataConfig })
      this.allParams.params = res.data
      var id = new Date().getTime() * -1
      for (var i = 0; i < this.allParams.params.length; i++) {
        this.allParams.params[i].columnId = id
        id++
      }
    },
    async showDocClick () {
      let submitData = this.allParams
      if (this.allColumns && this.allColumns.length > 0) {
        submitData.childColumns = this.allColumns
      }
      if (this.allParam && this.allParam.length > 0) {
        submitData.childParams = this.allParam
      }
      const res = await interfaceApi.showDoc(submitData)
      if (res.data.length > 0) {
        this.dialogShowDocVisible = true
        this.showDoc = res.data
      }
    },
    async testInterface () {
      let submitData = this.allParams
      if (this.allColumns && this.allColumns.length > 0) {
        submitData.childColumns = this.allColumns
      }
      if (this.allParam && this.allParam.length > 0) {
        submitData.childParams = this.allParam
      }
      const res = await interfaceApi.testInterface(submitData)
      if (res.data.length > 0) {
        this.$message.success(res.data)
      }
    },
    async refreshParam () {
      let submitData = this.allParams
      if (this.allColumns && this.allColumns.length > 0) {
        submitData.childColumns = this.allColumns
      }
      if (this.allParam && this.allParam.length > 0) {
        submitData.childParams = this.allParam
      }
      const res = await interfaceApi.getHttpParam(submitData)
      this.allParams.params = []
        this.allParam = []
        for (var i = 0; i < res.data.length; i++) {
          if (res.data[i].parentId === null) {
            this.allParams.params.push(res.data[i])
          } else {
            this.allParam.push(res.data[i])
          }
        }
    }
  }
}
</script>
<style scoped lang="scss"></style>
