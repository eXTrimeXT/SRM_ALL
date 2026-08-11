<template>
  <el-container
    class="flex-container-notab the_quickSearchDemo_wrapper"
    direction="vertical"
  >
    <el-main>
      <el-collapse
        v-model="activeNames"
        class="tab-form-style"
      >
        <el-form
          ref="form"
          :rules="rules"
          :model="form"
          label-width="80px"
          label-position="top"
          class="form-incontainer"
        >
          <el-collapse-item name="1">
            <template slot="title">
              <!-- 订单交付分析设置 -->
              {{ $t("dataConfMod.orderAnalysisSetting") }}
              <el-button
                class="tab-form-reset"
                type="text"
                @click.stop="resetPurchase"
              >
                <!-- 重置 -->
                {{ $t("common.reset") }}
              </el-button>
            </template>
            <el-row
              type="flex"
              style="margin-bottom: 12px;"
              :gutter="45"
            >
              <el-col :span="12">
                <el-button
                  type="primary"
                  class="detail-pbtn"
                  @click="addPurchase"
                >
                  {{ $t("common.add") }}
                </el-button>
                <el-button
                  class="detail-wbtn"
                  @click="delPurchase"
                >
                  {{ $t("common.delete") }}
                </el-button>
              </el-col>
            </el-row>
            <el-table
              ref="mtTable"
              stripe
              border
              :data="form.purchaseList"
              @selection-change="handleSelectionChange"
            >
              <el-table-column
                type="selection"
                align="center"
              />
              <!-- 组织选择 -->
              <el-table-column
                prop="organizationNames"
                :label="$t('dataConfMod.orgSelection')"
                align="center"
                min-width="150"
                show-overflow-tooltip
              >
                <template slot-scope="scope">
                  <el-form-item
                    :prop="
                      'purchaseList.' + scope.$index + '.organizationNames'
                    "
                    :rules="rules.organizationNames"
                  >
                    <el-input
                      v-model="scope.row.organizationNames"
                      :placeholder="$t('dataConfMod.selectOrg')"
                      class="input-with-select"
                    >
                      <el-button
                        slot="append"
                        type="text"
                        @click="selectionOrg(scope.row, scope.$index)"
                      >
                        {{ $t("common.select") }}
                      </el-button>
                    </el-input>
                  </el-form-item>
                </template>
              </el-table-column>
              <!-- 入库偏差天数 -->
              <el-table-column
                prop="days"
                :label="$t('dataConfMod.warehouseDeviationDays')"
                align="center"
                show-overflow-tooltip
              >
                <template slot-scope="scope">
                  <el-form-item
                    :prop="'purchaseList.' + scope.$index + '.days'"
                    :rules="rules.days"
                  >
                    <el-input
                      v-model="scope.row.days"
                      :placeholder="$t('dataConfMod.msgInputDeviationDay')"
                      class="input-with-select"
                    >
                      <el-select
                        slot="prepend"
                        v-model="scope.row.type"
                        :placeholder="$t('common.pleaseSelect')"
                      >
                        <el-option
                          v-for="item in purchaseTypeList"
                          :key="item.id"
                          :label="item.label"
                          :value="item.value"
                        />
                      </el-select>
                    </el-input>
                  </el-form-item>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>
        </el-form>
        <el-form
          ref="form1"
          :rules="rules"
          :model="supplier"
          label-width="80px"
          label-position="top"
          class="form-incontainer"
        >
          <el-collapse-item name="2">
            <template slot="title">
              <!-- 供应商分析设置 -->
              {{ $t("dataConfMod.vendorAnalysisSetting") }}
              <el-button
                class="tab-form-reset"
                type="text"
                @click.stop="resetSupplier"
              >
                {{ $t("common.reset") }}
              </el-button>
            </template>
            <el-row
              style="margin-bottom:20px;"
              class="supplierAnalysis"
              :gutter="20"
            >
              <el-col :span="4">
                <div class="supplierAnalysis-title blod-title">
                  <!-- 活跃供应商设置 -->
                  {{ $t("dataConfMod.activeVendorSetting") }}
                </div>
              </el-col>
              <el-col :span="8">
                <el-row
                  type="flex"
                  :gutter="10"
                >
                  <el-col
                    :span="6"
                  >
                    <div class="supplierAnalysis-title">
                      <!-- 订单金额 -->
                      {{ $t("dataConfMod.orderAmount") }}
                    </div>
                  </el-col>
                  <el-col
                    :span="14"
                    class="supplierAnalysis-input"
                  >
                    <el-form-item prop="orderAmount">
                      <el-input
                        v-model="supplier.orderAmount"
                        :placeholder="$t('common.pleaseSelect')"
                        type="number"
                      >
                        <!-- 大于等于 -->
                        <template slot="prepend">
                          {{
                            $t("components.condition.ge")
                          }}
                        </template>
                      </el-input>
                    </el-form-item>
                  </el-col>
                  <el-col :span="4">
                    <div class="supplierAnalysis-unit">
                      <!-- 万元 -->
                      {{ $t("dataConfMod.tenThousand") }}
                    </div>
                  </el-col>
                </el-row>
              </el-col>
            </el-row>
            <el-row
              style="margin-bottom:20px;"
              class="supplierAnalysis"
              :gutter="20"
            >
              <el-col :span="4">
                <div class="supplierAnalysis-title blod-title">
                  <!-- 供应商区域分布设置 -->
                  {{ $t("dataConfMod.vendorAreaSetting") }}
                </div>
              </el-col>
              <el-col :span="8">
                <el-row
                  type="flex"
                  :gutter="10"
                >
                  <el-col :span="6">
                    <!-- 阶段一 -->
                    <div class="supplierAnalysis-title">
                      {{ $t("dataConfMod.stageOne") }}
                    </div>
                  </el-col>
                  <el-col
                    :span="14"
                    class="supplierAnalysis-input"
                  >
                    <el-form-item prop="areaOne">
                      <el-input
                        v-model="supplier.areaOne"
                        :placeholder="$t('common.pleaseSelect')"
                        type="number"
                      >
                        <!-- 小于 -->
                        <template slot="prepend">
                          {{
                            $t("components.condition.lt")
                          }}
                        </template>
                      </el-input>
                    </el-form-item>
                  </el-col>
                  <el-col
                    :span="4"
                  >
                    <div class="supplierAnalysis-unit">
                      <!-- 家 -->
                      {{ $t("dataConfMod.home") }}
                    </div>
                  </el-col>
                </el-row>
              </el-col>
            </el-row>
            <el-row
              style="margin-bottom:20px;"
              class="supplierAnalysis"
              :gutter="20"
            >
              <el-col :span="4">
                <p />
              </el-col>
              <el-col :span="8">
                <el-row
                  type="flex"
                  :gutter="10"
                >
                  <el-col
                    :span="6"
                  >
                    <div class="supplierAnalysis-title">
                      <!-- 阶段二 -->
                      {{ $t("dataConfMod.stageTwo") }}
                    </div>
                  </el-col>
                  <el-col
                    :span="14"
                    class="supplierAnalysis-input"
                  >
                    <el-form-item prop="areaTwoStart">
                      <el-input
                        v-model="supplier.areaTwoStart"
                        :placeholder="$t('common.pleaseSelect')"
                        type="number"
                      >
                        <template slot="prepend">
                          <!-- 大于等于 -->
                          {{ $t("components.condition.ge") }}
                        </template>
                      </el-input>
                    </el-form-item>
                  </el-col>
                  <el-col
                    :span="4"
                  >
                    <div class="supplierAnalysis-unit">
                      <!-- 家 -->
                      {{ $t("dataConfMod.home") }}
                    </div>
                  </el-col>
                </el-row>
              </el-col>
              <el-col :span="8">
                <el-row
                  type="flex"
                  :gutter="10"
                >
                  <el-col
                    :span="14"
                    class="supplierAnalysis-input"
                  >
                    <el-form-item prop="areaTwoEnd">
                      <el-input
                        v-model="supplier.areaTwoEnd"
                        :placeholder="$t('common.pleaseSelect')"
                        type="number"
                      >
                        <template slot="prepend">
                          <!-- 小于 -->
                          {{ $t("components.condition.lt") }}
                        </template>
                      </el-input>
                    </el-form-item>
                  </el-col>
                  <el-col
                    :span="4"
                  >
                    <div class="supplierAnalysis-unit">
                      <!-- 家 -->
                      {{ $t("dataConfMod.home") }}
                    </div>
                  </el-col>
                </el-row>
              </el-col>
            </el-row>
            <el-row
              style="margin-bottom:20px;"
              class="supplierAnalysis"
              :gutter="20"
            >
              <el-col :span="4">
                <p />
              </el-col>
              <el-col :span="8">
                <el-row
                  type="flex"
                  :gutter="10"
                >
                  <el-col
                    :span="6"
                  >
                    <div class="supplierAnalysis-title">
                      <!-- 阶段三 -->
                      {{ $t("dataConfMod.stageThree") }}
                    </div>
                  </el-col>
                  <el-col
                    :span="14"
                    class="supplierAnalysis-input"
                  >
                    <el-form-item prop="areaThreeStart">
                      <el-input
                        v-model="supplier.areaThreeStart"
                        :placeholder="$t('common.pleaseSelect')"
                        type="number"
                      >
                        <template slot="prepend">
                          <!-- 大于等于 -->
                          {{ $t("components.condition.ge") }}
                        </template>
                      </el-input>
                    </el-form-item>
                  </el-col>
                  <el-col
                    :span="4"
                  >
                    <div class="supplierAnalysis-unit">
                      <!-- 家 -->
                      {{ $t("dataConfMod.home") }}
                    </div>
                  </el-col>
                </el-row>
              </el-col>
              <el-col :span="8">
                <el-row
                  type="flex"
                  :gutter="10"
                >
                  <el-col
                    :span="14"
                    class="supplierAnalysis-input"
                  >
                    <el-form-item prop="areaThreeEnd">
                      <el-input
                        v-model="supplier.areaThreeEnd"
                        :placeholder="$t('common.pleaseSelect')"
                        type="number"
                      >
                        <template slot="prepend">
                          <!-- 小于 -->
                          {{ $t("components.condition.lt") }}
                        </template>
                      </el-input>
                    </el-form-item>
                  </el-col>
                  <el-col
                    :span="4"
                  >
                    <div class="supplierAnalysis-unit">
                      <!-- 家 -->
                      {{ $t("dataConfMod.home") }}
                    </div>
                  </el-col>
                </el-row>
              </el-col>
            </el-row>
            <el-row
              style="margin-bottom:20px;"
              class="supplierAnalysis"
              :gutter="20"
            >
              <el-col :span="4">
                <p />
              </el-col>
              <el-col :span="8">
                <el-row
                  type="flex"
                  :gutter="10"
                >
                  <el-col
                    :span="6"
                  >
                    <div class="supplierAnalysis-title">
                      <!-- 阶段四 -->
                      {{ $t("dataConfMod.stageFour") }}
                    </div>
                  </el-col>
                  <el-col
                    :span="14"
                    class="supplierAnalysis-input"
                  >
                    <el-form-item prop="areaFour">
                      <el-input
                        v-model="supplier.areaFour"
                        :placeholder="$t('common.pleaseSelect')"
                        type="number"
                      >
                        <template slot="prepend">
                          <!-- 大于等于 -->
                          {{ $t("components.condition.ge") }}
                        </template>
                      </el-input>
                    </el-form-item>
                  </el-col>
                  <el-col
                    :span="4"
                  >
                    <div class="supplierAnalysis-unit">
                      <!-- 家 -->
                      {{ $t("dataConfMod.home") }}
                    </div>
                  </el-col>
                </el-row>
              </el-col>
            </el-row>
            <el-row
              style="margin-bottom:20px;"
              class="supplierAnalysis"
              :gutter="20"
            >
              <el-col :span="4">
                <div class="supplierAnalysis-title blod-title">
                  <!-- 采购金额供方占比设置 -->
                  {{ $t("dataConfMod.purchaseAmountPropSetting") }}
                </div>
              </el-col>
              <el-col :span="8">
                <el-row
                  type="flex"
                  :gutter="10"
                >
                  <el-col
                    :span="6"
                  >
                    <div class="supplierAnalysis-title">
                      <!-- 阶段一 -->
                      {{ $t("dataConfMod.stageOne") }}
                    </div>
                  </el-col>
                  <el-col
                    :span="14"
                    class="supplierAnalysis-input"
                  >
                    <el-form-item prop="purchaseOne">
                      <el-input
                        v-model="supplier.purchaseOne"
                        :placeholder="$t('common.pleaseSelect')"
                        type="number"
                      >
                        <template slot="prepend">
                          <!-- 小于 -->
                          {{ $t("components.condition.lt") }}
                        </template>
                      </el-input>
                    </el-form-item>
                  </el-col>
                  <el-col
                    :span="4"
                  >
                    <div class="supplierAnalysis-unit">
                      <!-- 万元 -->
                      {{ $t("dataConfMod.tenThousand") }}
                    </div>
                  </el-col>
                </el-row>
              </el-col>
            </el-row>
            <el-row
              style="margin-bottom:20px;"
              class="supplierAnalysis"
              :gutter="20"
            >
              <el-col :span="4">
                <p />
              </el-col>
              <el-col :span="8">
                <el-row
                  type="flex"
                  :gutter="10"
                >
                  <el-col
                    :span="6"
                  >
                    <div class="supplierAnalysis-title">
                      <!-- 阶段二 -->
                      {{ $t("dataConfMod.stageTwo") }}
                    </div>
                  </el-col>
                  <el-col
                    :span="14"
                    class="supplierAnalysis-input"
                  >
                    <el-form-item prop="purchaseTwoStart">
                      <el-input
                        v-model="supplier.purchaseTwoStart"
                        :placeholder="$t('common.pleaseSelect')"
                        type="number"
                      >
                        <template
                          slot="prepend"
                        >
                          <!-- 大于等于 -->
                          {{ $t("components.condition.ge") }}
                        </template>
                      </el-input>
                    </el-form-item>
                  </el-col>
                  <el-col
                    :span="4"
                  >
                    <div class="supplierAnalysis-unit">
                      <!-- 万元 -->
                      {{ $t("dataConfMod.tenThousand") }}
                    </div>
                  </el-col>
                </el-row>
              </el-col>
              <el-col :span="8">
                <el-row
                  type="flex"
                  :gutter="10"
                >
                  <el-col
                    :span="14"
                    class="supplierAnalysis-input"
                  >
                    <el-form-item prop="purchaseTwoEnd">
                      <el-input
                        v-model="supplier.purchaseTwoEnd"
                        :placeholder="$t('common.pleaseSelect')"
                        type="number"
                      >
                        <template
                          slot="prepend"
                        >
                          <!-- 小于 -->
                          {{ $t("components.condition.lt") }}
                        </template>
                      </el-input>
                    </el-form-item>
                  </el-col>
                  <el-col
                    :span="4"
                  >
                    <div class="supplierAnalysis-unit">
                      <!-- 万元 -->
                      {{ $t("dataConfMod.tenThousand") }}
                    </div>
                  </el-col>
                </el-row>
              </el-col>
            </el-row>
            <el-row
              style="margin-bottom:20px;"
              class="supplierAnalysis"
              :gutter="20"
            >
              <el-col :span="4">
                <p />
              </el-col>
              <el-col :span="8">
                <el-row
                  type="flex"
                  :gutter="10"
                >
                  <el-col
                    :span="6"
                  >
                    <div class="supplierAnalysis-title">
                      <!-- 阶段三 -->
                      {{ $t("dataConfMod.stageThree") }}
                    </div>
                  </el-col>
                  <el-col
                    :span="14"
                    class="supplierAnalysis-input"
                  >
                    <el-form-item prop="purchaseThreeStart">
                      <el-input
                        v-model="supplier.purchaseThreeStart"
                        :placeholder="$t('common.pleaseSelect')"
                        type="number"
                      >
                        <template
                          slot="prepend"
                        >
                          <!-- 大于等于 -->
                          {{ $t("components.condition.ge") }}
                        </template>
                      </el-input>
                    </el-form-item>
                  </el-col>
                  <el-col
                    :span="4"
                  >
                    <div class="supplierAnalysis-unit">
                      <!-- 万元 -->
                      {{ $t("dataConfMod.tenThousand") }}
                    </div>
                  </el-col>
                </el-row>
              </el-col>
              <el-col :span="8">
                <el-row
                  type="flex"
                  :gutter="10"
                >
                  <el-col
                    :span="14"
                    class="supplierAnalysis-input"
                  >
                    <el-form-item prop="purchaseThreeEnd">
                      <el-input
                        v-model="supplier.purchaseThreeEnd"
                        :placeholder="$t('common.pleaseSelect')"
                        type="number"
                      >
                        <template
                          slot="prepend"
                        >
                          <!-- 小于 -->
                          {{ $t("components.condition.lt") }}
                        </template>
                      </el-input>
                    </el-form-item>
                  </el-col>
                  <el-col
                    :span="4"
                  >
                    <div class="supplierAnalysis-unit">
                      <!-- 万元 -->
                      {{ $t("dataConfMod.tenThousand") }}
                    </div>
                  </el-col>
                </el-row>
              </el-col>
            </el-row>
            <el-row
              style="margin-bottom:20px;"
              class="supplierAnalysis"
              :gutter="20"
            >
              <el-col :span="4">
                <p />
              </el-col>
              <el-col :span="8">
                <el-row
                  type="flex"
                  :gutter="10"
                >
                  <el-col
                    :span="6"
                  >
                    <div class="supplierAnalysis-title">
                      <!-- 阶段四 -->
                      {{ $t("dataConfMod.stageFour") }}
                    </div>
                  </el-col>
                  <el-col
                    :span="14"
                    class="supplierAnalysis-input"
                  >
                    <el-form-item prop="purchaseFour">
                      <el-input
                        v-model="supplier.purchaseFour"
                        :placeholder="$t('common.pleaseSelect')"
                        type="number"
                      >
                        <template
                          slot="prepend"
                        >
                          <!-- 大于等于 -->
                          {{ $t("components.condition.ge") }}
                        </template>
                      </el-input>
                    </el-form-item>
                  </el-col>
                  <el-col
                    :span="4"
                  >
                    <div class="supplierAnalysis-unit">
                      <!-- 家 -->
                      {{ $t("dataConfMod.home") }}
                    </div>
                  </el-col>
                </el-row>
              </el-col>
            </el-row>
            <el-row
              style="margin-bottom:20px;"
              class="supplierAnalysis"
              :gutter="20"
            >
              <el-col :span="4">
                <div class="supplierAnalysis-title blod-title">
                  <!-- 品类供方数占比设置 -->
                  {{ $t("dataConfMod.categoryPropSetting") }}
                </div>
              </el-col>
              <el-col :span="8">
                <el-row
                  type="flex"
                  :gutter="10"
                >
                  <el-col
                    :span="6"
                  >
                    <div class="supplierAnalysis-title">
                      <!-- 阶段一 -->
                      {{ $t("dataConfMod.stageOne") }}
                    </div>
                  </el-col>
                  <el-col
                    :span="14"
                    class="supplierAnalysis-input"
                  >
                    <el-form-item prop="categoryOne">
                      <el-input
                        v-model="supplier.categoryOne"
                        :placeholder="$t('common.pleaseSelect')"
                        type="number"
                      >
                        <template
                          slot="prepend"
                        >
                          <!-- 小于 -->
                          {{ $t("components.condition.lt") }}
                        </template>
                      </el-input>
                    </el-form-item>
                  </el-col>
                  <el-col
                    :span="4"
                  >
                    <div class="supplierAnalysis-unit">
                      <!-- 家 -->
                      {{ $t("dataConfMod.home") }}
                    </div>
                  </el-col>
                </el-row>
              </el-col>
            </el-row>
            <el-row
              style="margin-bottom:20px;"
              class="supplierAnalysis"
              :gutter="20"
            >
              <el-col :span="4">
                <p />
              </el-col>
              <el-col :span="8">
                <el-row
                  type="flex"
                  :gutter="10"
                >
                  <el-col
                    :span="6"
                  >
                    <div class="supplierAnalysis-title">
                      <!-- 阶段二 -->
                      {{ $t("dataConfMod.stageTwo") }}
                    </div>
                  </el-col>
                  <el-col
                    :span="14"
                    class="supplierAnalysis-input"
                  >
                    <el-form-item prop="categoryTwoStart">
                      <el-input
                        v-model="supplier.categoryTwoStart"
                        :placeholder="$t('common.pleaseSelect')"
                        type="number"
                      >
                        <template
                          slot="prepend"
                        >
                          <!-- 大于等于 -->
                          {{ $t("components.condition.ge") }}
                        </template>
                      </el-input>
                    </el-form-item>
                  </el-col>
                  <el-col
                    :span="4"
                  >
                    <div class="supplierAnalysis-unit">
                      <!-- 家 -->
                      {{ $t("dataConfMod.home") }}
                    </div>
                  </el-col>
                </el-row>
              </el-col>
              <el-col :span="8">
                <el-row
                  type="flex"
                  :gutter="10"
                >
                  <el-col
                    :span="14"
                    class="supplierAnalysis-input"
                  >
                    <el-form-item prop="categoryTwoEnd">
                      <el-input
                        v-model="supplier.categoryTwoEnd"
                        :placeholder="$t('common.pleaseSelect')"
                        type="number"
                      >
                        <template
                          slot="prepend"
                        >
                          <!-- 小于 -->
                          {{ $t("components.condition.lt") }}
                        </template>
                      </el-input>
                    </el-form-item>
                  </el-col>
                  <el-col
                    :span="4"
                  >
                    <div class="supplierAnalysis-unit">
                      <!-- 家 -->
                      {{ $t("dataConfMod.home") }}
                    </div>
                  </el-col>
                </el-row>
              </el-col>
            </el-row>
            <el-row
              style="margin-bottom:20px;"
              class="supplierAnalysis"
              :gutter="20"
            >
              <el-col :span="4">
                <p />
              </el-col>
              <el-col :span="8">
                <el-row
                  type="flex"
                  :gutter="10"
                >
                  <el-col
                    :span="6"
                  >
                    <div class="supplierAnalysis-title">
                      <!-- 阶段三 -->
                      {{ $t("dataConfMod.stageThree") }}
                    </div>
                  </el-col>
                  <el-col
                    :span="14"
                    class="supplierAnalysis-input"
                  >
                    <el-form-item prop="categoryThreeStart">
                      <el-input
                        v-model="supplier.categoryThreeStart"
                        :placeholder="$t('common.pleaseSelect')"
                        type="number"
                      >
                        <template
                          slot="prepend"
                        >
                          <!-- 大于等于 -->
                          {{ $t("components.condition.ge") }}
                        </template>
                      </el-input>
                    </el-form-item>
                  </el-col>
                  <el-col
                    :span="4"
                  >
                    <div class="supplierAnalysis-unit">
                      <!-- 家 -->
                      {{ $t("dataConfMod.home") }}
                    </div>
                  </el-col>
                </el-row>
              </el-col>
              <el-col :span="8">
                <el-row
                  type="flex"
                  :gutter="10"
                >
                  <el-col
                    :span="14"
                    class="supplierAnalysis-input"
                  >
                    <el-form-item prop="categoryThreeEnd">
                      <el-input
                        v-model="supplier.categoryThreeEnd"
                        :placeholder="$t('common.pleaseSelect')"
                        type="number"
                      >
                        <template
                          slot="prepend"
                        >
                          <!-- 小于 -->
                          {{ $t("components.condition.lt") }}
                        </template>
                      </el-input>
                    </el-form-item>
                  </el-col>
                  <el-col
                    :span="4"
                  >
                    <div class="supplierAnalysis-unit">
                      <!-- 家 -->
                      {{ $t("dataConfMod.home") }}
                    </div>
                  </el-col>
                </el-row>
              </el-col>
            </el-row>
            <el-row
              style="margin-bottom:20px;"
              class="supplierAnalysis"
              :gutter="20"
            >
              <el-col :span="4">
                <p />
              </el-col>
              <el-col :span="8">
                <el-row
                  type="flex"
                  :gutter="10"
                >
                  <el-col
                    :span="6"
                  >
                    <div class="supplierAnalysis-title">
                      <!-- 阶段四 -->
                      {{ $t("dataConfMod.stageFour") }}
                    </div>
                  </el-col>
                  <el-col
                    :span="14"
                    class="supplierAnalysis-input"
                  >
                    <el-form-item prop="categoryFour">
                      <el-input
                        v-model="supplier.categoryFour"
                        :placeholder="$t('common.pleaseSelect')"
                        type="number"
                      >
                        <template
                          slot="prepend"
                        >
                          <!-- 大于等于 -->
                          {{ $t("components.condition.ge") }}
                        </template>
                      </el-input>
                    </el-form-item>
                  </el-col>
                  <el-col
                    :span="4"
                  >
                    <div class="supplierAnalysis-unit">
                      <!-- 家 -->
                      {{ $t("dataConfMod.home") }}
                    </div>
                  </el-col>
                </el-row>
              </el-col>
            </el-row>
          </el-collapse-item>
        </el-form>
        <el-collapse-item name="3">
          <el-form
            ref="form2"
            :rules="rules"
            :model="suppliers"
            label-width="80px"
            label-position="top"
            class="form-incontainer"
          >
            <el-row
              style="margin-bottom:20px;"
              class="supplierAnalysis"
              :gutter="20"
            >
              <el-col :span="4">
                <div class="supplierAnalysis-title blod-title">
                  <!-- 降本金额区间占比设置 -->
                  {{ $t("dataConfMod.reduceAmountPropSetting") }}
                </div>
              </el-col>
              <el-col :span="8">
                <el-row
                  type="flex"
                  :gutter="10"
                >
                  <el-col
                    :span="6"
                  >
                    <div class="supplierAnalysis-title">
                      <!-- 阶段一 -->
                      {{ $t("dataConfMod.stageOne") }}
                    </div>
                  </el-col>
                  <el-col
                    :span="14"
                    class="supplierAnalysis-input"
                  >
                    <el-form-item prop="categoryCrOne">
                      <el-input
                        v-model="suppliers.categoryCrOne"
                        :placeholder="$t('common.pleaseSelect')"
                        type="number"
                      >
                        <template
                          slot="prepend"
                        >
                          <!-- 小于 -->
                          {{ $t("components.condition.lt") }}
                        </template>
                      </el-input>
                    </el-form-item>
                  </el-col>
                  <el-col
                    :span="4"
                  >
                    <div class="supplierAnalysis-unit">
                      <!-- 万元 -->
                      {{ $t("dataConfMod.tenThousand") }}
                    </div>
                  </el-col>
                </el-row>
              </el-col>
            </el-row>
            <el-row
              style="margin-bottom:20px;"
              class="supplierAnalysis"
              :gutter="20"
            >
              <el-col :span="4">
                <p />
              </el-col>
              <el-col :span="8">
                <el-row
                  type="flex"
                  :gutter="10"
                >
                  <el-col
                    :span="6"
                  >
                    <div class="supplierAnalysis-title">
                      <!-- 阶段二 -->
                      {{ $t("dataConfMod.stageTwo") }}
                    </div>
                  </el-col>
                  <el-col
                    :span="14"
                    class="supplierAnalysis-input"
                  >
                    <el-form-item prop="categoryCrTwoStart">
                      <el-input
                        v-model="suppliers.categoryCrTwoStart"
                        :placeholder="$t('common.pleaseSelect')"
                        type="number"
                      >
                        <template
                          slot="prepend"
                        >
                          <!-- 大于等于 -->
                          {{ $t("components.condition.ge") }}
                        </template>
                      </el-input>
                    </el-form-item>
                  </el-col>
                  <el-col
                    :span="4"
                  >
                    <div class="supplierAnalysis-unit">
                      <!-- 万元 -->
                      {{ $t("dataConfMod.tenThousand") }}
                    </div>
                  </el-col>
                </el-row>
              </el-col>
              <el-col :span="8">
                <el-row
                  type="flex"
                  :gutter="10"
                >
                  <el-col
                    :span="14"
                    class="supplierAnalysis-input"
                  >
                    <el-form-item prop="categoryCrTwoEnd">
                      <el-input
                        v-model="suppliers.categoryCrTwoEnd"
                        :placeholder="$t('common.pleaseSelect')"
                        type="number"
                      >
                        <template
                          slot="prepend"
                        >
                          <!-- 小于 -->
                          {{ $t("components.condition.lt") }}
                        </template>
                      </el-input>
                    </el-form-item>
                  </el-col>
                  <el-col
                    :span="4"
                  >
                    <div class="supplierAnalysis-unit">
                      <!-- 万元 -->
                      {{ $t("dataConfMod.tenThousand") }}
                    </div>
                  </el-col>
                </el-row>
              </el-col>
            </el-row>
            <el-row
              style="margin-bottom:20px;"
              class="supplierAnalysis"
              :gutter="20"
            >
              <el-col :span="4">
                <p />
              </el-col>
              <el-col :span="8">
                <el-row
                  type="flex"
                  :gutter="10"
                >
                  <el-col
                    :span="6"
                  >
                    <div class="supplierAnalysis-title">
                      <!-- 阶段三 -->
                      {{ $t("dataConfMod.stageThree") }}
                    </div>
                  </el-col>
                  <el-col
                    :span="14"
                    class="supplierAnalysis-input"
                  >
                    <el-form-item prop="categoryCrThreeStart">
                      <el-input
                        v-model="suppliers.categoryCrThreeStart"
                        :placeholder="$t('common.pleaseSelect')"
                        type="number"
                      >
                        <template
                          slot="prepend"
                        >
                          <!-- 大于等于 -->
                          {{ $t("components.condition.ge") }}
                        </template>
                      </el-input>
                    </el-form-item>
                  </el-col>
                  <el-col
                    :span="4"
                  >
                    <div class="supplierAnalysis-unit">
                      <!-- 万元 -->
                      {{ $t("dataConfMod.tenThousand") }}
                    </div>
                  </el-col>
                </el-row>
              </el-col>
              <el-col :span="8">
                <el-row
                  type="flex"
                  :gutter="10"
                >
                  <el-col
                    :span="14"
                    class="supplierAnalysis-input"
                  >
                    <el-form-item prop="categoryCrThreeEnd">
                      <el-input
                        v-model="suppliers.categoryCrThreeEnd"
                        :placeholder="$t('common.pleaseSelect')"
                        type="number"
                      >
                        <template
                          slot="prepend"
                        >
                          <!-- 小于 -->
                          {{ $t("components.condition.lt") }}
                        </template>
                      </el-input>
                    </el-form-item>
                  </el-col>
                  <el-col
                    :span="4"
                  >
                    <div class="supplierAnalysis-unit">
                      <!-- 万元 -->
                      {{ $t("dataConfMod.tenThousand") }}
                    </div>
                  </el-col>
                </el-row>
              </el-col>
            </el-row>
            <el-row
              style="margin-bottom:20px;"
              class="supplierAnalysis"
              :gutter="20"
            >
              <el-col :span="4">
                <p />
              </el-col>
              <el-col :span="8">
                <el-row
                  type="flex"
                  :gutter="10"
                >
                  <el-col
                    :span="6"
                  >
                    <div class="supplierAnalysis-title">
                      <!-- 阶段四 -->
                      {{ $t("dataConfMod.stageFour") }}
                    </div>
                  </el-col>
                  <el-col
                    :span="14"
                    class="supplierAnalysis-input"
                  >
                    <el-form-item prop="categoryCrFour">
                      <el-input
                        v-model="suppliers.categoryCrFour"
                        :placeholder="$t('common.pleaseSelect')"
                        type="number"
                      >
                        <template
                          slot="prepend"
                        >
                          <!-- 大于等于 -->
                          {{ $t("components.condition.ge") }}
                        </template>
                      </el-input>
                    </el-form-item>
                  </el-col>
                  <el-col
                    :span="4"
                  >
                    <div class="supplierAnalysis-unit">
                      <!-- 万元 -->
                      {{ $t("dataConfMod.tenThousand") }}
                    </div>
                  </el-col>
                </el-row>
              </el-col>
            </el-row>
          </el-form>
          <template slot="title">
            <!-- 采购降本分析设置 -->
            {{ $t("dataConfMod.reduceAmountAnalysisSetting") }}
            <el-button
              class="tab-form-reset"
              type="text"
              @click.stop="resetCostReductionList"
            >
              <!-- 重置 -->
              {{ $t("common.reset") }}
            </el-button>
          </template>
          <el-row
            type="flex"
            style="margin-bottom: 12px;"
            :gutter="45"
          >
            <el-col :span="12">
              <el-button
                type="primary"
                class="detail-pbtn"
                @click="addCostReductionList"
              >
                {{ $t("common.add") }}
              </el-button>
              <el-button
                class="detail-wbtn"
                @click="delCostReductionList"
              >
                {{ $t("common.delete") }}
              </el-button>
            </el-col>
          </el-row>
          <el-form
            ref="costReductionList"
            :rules="rules"
            :model="costReductionForm"
            class="form-incontainer"
          >
            <el-table
              ref="mtTable"
              stripe
              border
              :data="costReductionForm.costReductionList"
              @selection-change="selectionCostReduction"
            >
              <el-table-column
                type="selection"
                align="center"
              />
              <!-- 年份 -->
              <el-table-column
                prop="year"
                :label="$t('dataConfMod.year')"
                align="center"
                show-overflow-tooltip
              >
                <template slot-scope="scope">
                  <el-form-item
                    :prop="'costReductionList.' + scope.$index + '.year'"
                    :rules="rules.year"
                  >
                    <!-- 选择年份 -->
                    <el-date-picker
                      v-model="scope.row.year"
                      type="year"
                      value-format="yyyy"
                      :format="$formatDatePicker"
                      :placeholder="$t('dataConfMod.selectYear')"
                    />
                  </el-form-item>
                </template>
              </el-table-column>
              <!-- 设置年度总体采购目标降本率（ -->
              <el-table-column
                prop="rate"
                :label="$t('dataConfMod.setAllReductionRate')"
                align="center"
                show-overflow-tooltip
              >
                <template slot-scope="scope">
                  <el-form-item
                    :prop="'costReductionList.' + scope.$index + '.rate'"
                    :rules="rules.rate"
                  >
                    <el-input
                      v-model="scope.row.rate"
                      :placeholder="$t('common.pleaseSelect')"
                      type="number"
                    />
                  </el-form-item>
                </template>
              </el-table-column>
              <!-- 设置年度采购品类目标降本率 -->
              <el-table-column
                prop="organizationNames"
                :label="$t('dataConfMod.setReductionRate')"
                align="center"
                show-overflow-tooltip
              >
                <template slot-scope="scope">
                  <el-button
                    type="text"
                    @click="selectionCat(scope.row, scope.$index)"
                  >
                    <!-- 设置 -->
                    {{ $t("dataConfMod.setting") }}
                  </el-button>
                </template>
              </el-table-column>
              <!-- 设置采购物料去年冻结单价 -->
              <el-table-column
                prop="organizationNames"
                :label="$t('dataConfMod.setLastYearFrozenPrice')"
                align="center"
                show-overflow-tooltip
              >
                <template slot-scope="scope">
                  <el-button
                    type="text"
                    @click="selectMaterial(scope.row, scope.$index)"
                  >
                    <!-- 设置 -->
                    {{ $t("dataConfMod.setting") }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-form>
        </el-collapse-item>
      </el-collapse>
      <category
        v-model="categoryDialog"
        :form="categoryForm"
        @success="categorySave"
        @parentDataCurrentChange="categoryChange"
        @parentDataSizeChange="categoryChange"
      />
      <material-list
        v-model="materialDialog"
        :form="materialForm"
        @success="materialSave"
        @getCrSetMaterial="getCrSetMaterial"
      />
      <!-- 组织选择 -->
      <srm-dialog
        :visible.sync="showOrgDialog"
        :title="$t('dataConfMod.orgSelection')"
        size="large"
      >
        <div style="height: 360px;overflow: auto;">
          <treeselect
            v-model="currentRows"
            :normalizer="normalizer"
            :no-children-text="$t('dataConfMod.noChildrenText')"
            :no-options-text="$t('dataConfMod.noOptionsText')"
            :no-results-text="$t('dataConfMod.noResultsText')"
            :placeholder="$t('dataConfMod.msgSelectOrgName')"
            :append-to-body="false"
            :searchable="true"
            :options="options"
            multiple
            value-format="object"
            auto-select-descendants
            :flatten-search-results="true"
            auto-deselect-descendants
            flat
            :always-open="true"
          />
        </div>
        <div slot="footer">
          <el-button @click="onCancel">
            {{ $t("common.cancel") }}
          </el-button>
          <el-button
            type="primary"
            @click="addOneOrg"
          >
            {{ $t("common.confirm") }}
          </el-button>
        </div>
      </srm-dialog>
      <c-toolbar>
        <template slot="right">
          <el-button>
            {{ $t("common.cancel") }}
          </el-button>
          <el-button

            type="primary"
            @click="save"
          >
            {{ $t("common.submit") }}
          </el-button>
        </template>
      </c-toolbar>
    </el-main>
  </el-container>
</template>
<script>
import { getDictItem, newOrganaztionTreehttp } from '@/api/common'
import Treeselect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import { adaptDictData } from '@/utils'
import CToolbar from 'lib@/components/c-toolbar'
import Category from './category'
import MaterialList from './materialList'
const findMenuInfoByPath = (leafId, nodes) => {
  for (let i = 0; i < nodes.length; i++) {
    const tmpObj = nodes[i]
    if (leafId == nodes[i].fullPathId) {
      return tmpObj
    }
    if (nodes[i].childOrganRelation) {
      const findResult = findMenuInfoByPath(
        leafId,
        nodes[i].childOrganRelation,
        tmpObj
      )
      if (findResult) {
        return findResult
      }
    }
  }
}
const findOrg = (arr, addArr) => {
  arr.forEach(item => {
    addArr.push(item)
    if (item.childOrganRelation) {
      return findOrg(item.childOrganRelation, addArr)
    } else {
      return false
    }
  })
  return addArr
}
export default {
  name: 'ReportSetting',
  components: {
    Treeselect,
    Category,
    MaterialList,
    CToolbar
  },
  data () {
    return {
      activeNames: ['1', '2', '3', '4', '5', '6', '7'],
      purchaseTypeList: [],
      currentRows: [],
      options: [],
      showOrgDialog: false,
      showOrgDialogType: '',
      selectIndex: -1,
      form: {
        purchaseList: []
      },
      purchaseList: [],
      selectPurchaseList: [],
      supplier: {
        orderAmount: null,
        areaOne: null,
        areaTwoStart: null,
        areaTwoEnd: null,
        areaThreeStart: null,
        areaThreeEnd: null,
        areaFour: null,
        purchaseOne: null,
        purchaseTwoStart: null,
        purchaseTwoEnd: null,
        purchaseThreeStart: null,
        purchaseThreeEnd: null,
        purchaseFour: null,
        categoryOne: null,
        categoryTwoStart: null,
        categoryTwoEnd: null,
        categoryThreeStart: null,
        categoryThreeEnd: null,
        categoryFour: null
      },
      suppliers: {
        categoryCrOne: null,
        categoryCrTwoStart: null,
        categoryCrTwoEnd: null,
        categoryCrThreeStart: null,
        categoryCrThreeEnd: null,
        categoryCrFour: null
      },
      resetSupplierData: {
        id: Math.floor(Math.random() * 1000000),
        orderAmount: 100,
        areaOne: 5,
        areaTwoStart: 5,
        areaTwoEnd: 10,
        areaThreeStart: 10,
        areaThreeEnd: 20,
        areaFour: 20,
        purchaseOne: 10,
        purchaseTwoStart: 10,
        purchaseTwoEnd: 50,
        purchaseThreeStart: 50,
        purchaseThreeEnd: 100,
        purchaseFour: 100,
        categoryOne: 2,
        categoryTwoStart: 2,
        categoryTwoEnd: 5,
        categoryThreeStart: 5,
        categoryThreeEnd: 10,
        categoryFour: 10,
        categoryCrOne: null,
        categoryCrTwoStart: null,
        categoryCrTwoEnd: null,
        categoryCrThreeStart: null,
        categoryCrThreeEnd: null,
        categoryCrFour: null
      },
      rules: {
        organizationNames: [
          {
            required: true,
            message: this.$t('common.msgSelectOrg') // 请选择组织
          }
        ],
        days: [
          {
            required: true,
            message: this.$t('dataConfMod.msgInputDeviationDay') // 请输入入库偏差天数
          }
        ],
        orderAmount: [
          {
            required: true,
            message: this.$t('common.pleaseInput'),
            trigger: 'blur'
          }
        ],
        areaOne: [
          {
            required: true,
            message: this.$t('common.pleaseInput'),
            trigger: 'blur'
          }
        ],
        areaTwoStart: [
          {
            required: true,
            message: this.$t('common.pleaseInput'),
            trigger: 'blur'
          }
        ],
        areaTwoEnd: [
          {
            required: true,
            message: this.$t('common.pleaseInput'),
            trigger: 'blur'
          }
        ],
        areaThreeStart: [
          {
            required: true,
            message: this.$t('common.pleaseInput'),
            trigger: 'blur'
          }
        ],
        areaThreeEnd: [
          {
            required: true,
            message: this.$t('common.pleaseInput'),
            trigger: 'blur'
          }
        ],
        areaFour: [
          {
            required: true,
            message: this.$t('common.pleaseInput'),
            trigger: 'blur'
          }
        ],
        purchaseOne: [
          {
            required: true,
            message: this.$t('common.pleaseInput'),
            trigger: 'blur'
          }
        ],
        purchaseTwoStart: [
          {
            required: true,
            message: this.$t('common.pleaseInput'),
            trigger: 'blur'
          }
        ],
        purchaseTwoEnd: [
          {
            required: true,
            message: this.$t('common.pleaseInput'),
            trigger: 'blur'
          }
        ],
        purchaseThreeStart: [
          {
            required: true,
            message: this.$t('common.pleaseInput'),
            trigger: 'blur'
          }
        ],
        purchaseFour: [
          {
            required: true,
            message: this.$t('common.pleaseInput'),
            trigger: 'blur'
          }
        ],
        purchaseThreeEnd: [
          {
            required: true,
            message: this.$t('common.pleaseInput'),
            trigger: 'blur'
          }
        ],
        categoryOne: [
          {
            required: true,
            message: this.$t('common.pleaseInput'),
            trigger: 'blur'
          }
        ],
        categoryTwoStart: [
          {
            required: true,
            message: this.$t('common.pleaseInput'),
            trigger: 'blur'
          }
        ],
        categoryTwoEnd: [
          {
            required: true,
            message: this.$t('common.pleaseInput'),
            trigger: 'blur'
          }
        ],
        categoryThreeStart: [
          {
            required: true,
            message: this.$t('common.pleaseInput'),
            trigger: 'blur'
          }
        ],
        categoryThreeEnd: [
          {
            required: true,
            message: this.$t('common.pleaseInput'),
            trigger: 'blur'
          }
        ],
        categoryFour: [
          {
            required: true,
            message: this.$t('common.pleaseInput'),
            trigger: 'blur'
          }
        ],
        categoryCrOne: [
          {
            required: true,
            message: this.$t('common.pleaseInput'),
            trigger: 'blur'
          }
        ],
        categoryCrTwoStart: [
          {
            required: true,
            message: this.$t('common.pleaseInput'),
            trigger: 'blur'
          }
        ],
        categoryCrTwoEnd: [
          {
            required: true,
            message: this.$t('common.pleaseInput'),
            trigger: 'blur'
          }
        ],
        categoryCrThreeStart: [
          {
            required: true,
            message: this.$t('common.pleaseInput'),
            trigger: 'blur'
          }
        ],
        categoryCrThreeEnd: [
          {
            required: true,
            message: this.$t('common.pleaseInput'),
            trigger: 'blur'
          }
        ],
        categoryCrFour: [
          {
            required: true,
            message: this.$t('common.pleaseInput'),
            trigger: 'blur'
          }
        ],
        year: [
          {
            required: true,
            message: this.$t('common.pleaseInput'),
            trigger: 'blur'
          }
        ],
        rate: [
          {
            required: true,
            message: this.$t('common.pleaseInput'),
            trigger: 'blur'
          }
        ]
      },
      catLavel: this.$store.getters.catLavel,
      catEdit: false,
      costReductionForm: {
        costReductionList: []
      },
      categoryDialog: false,
      categoryForm: {
        categoryList: [],
        total: -1,
        pageNum: 1,
        pageSize: 15
      },
      materialDialog: false,
      materialForm: {
        materialList: [],
        total: -1,
        pageNum: 1,
        pageSize: 15
      },
      selectionCostReductionList: []
    }
  },
  async created () {
    // 状态
    getDictItem('REPORT_CONFIG_PURCHASE_TYPE').then(res => {
      this.purchaseTypeList = adaptDictData(res.data, 'dict')
    })
    const { data } = await newOrganaztionTreehttp({})
    this.options = data
    this.init()
    this.getDetail()
  },
  methods: {
    init () {
      let fullPathIds = []
      let organizationNames = []
      let arr = findOrg(this.options, [])
      arr.forEach(({ fullPathId, organizationName }) => {
        fullPathIds.push(fullPathId)
        organizationNames.push(organizationName)
      })
      this.purchaseList = [
        {
          fullPathIds: fullPathIds.join(','),
          organizationNames: organizationNames.join(','),
          type: 'DELAY',
          days: 3
        }
      ]
      this.form.purchaseList = this.purchaseList
    },
    resetPurchase () {
      // 当前操作将重置数据，确认是否重置数据？
      this.$confirm(this.$t('common.ifResetData'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.form.purchaseList = this.purchaseList
        })
    },
    resetSupplier () {
      // 当前操作将重置数据，确认是否重置数据？
      this.$confirm(this.$t('common.ifResetData'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.supplier = {
            id: Math.floor(Math.random() * 1000000),
            orderAmount: 100,
            areaOne: 5,
            areaTwoStart: 5,
            areaTwoEnd: 10,
            areaThreeStart: 10,
            areaThreeEnd: 20,
            areaFour: 20,
            purchaseOne: 10,
            purchaseTwoStart: 10,
            purchaseTwoEnd: 50,
            purchaseThreeStart: 50,
            purchaseThreeEnd: 100,
            purchaseFour: 100,
            categoryOne: 2,
            categoryTwoStart: 2,
            categoryTwoEnd: 5,
            categoryThreeStart: 5,
            categoryThreeEnd: 10,
            categoryFour: 10
          }
        })
    },
    getDetail () {
      this.$http({
        url: '/api-report/config/queryConfig',
        method: 'post',
        params: {},
        loading: true
      })
        .then(res => {
          this.form.purchaseList = res.data.purchaseList
          this.supplier = res.data.supplier
          this.suppliers.categoryCrFour = this.supplier.categoryCrFour || null
          this.suppliers.categoryCrOne = this.supplier.categoryCrOne || null
          this.suppliers.categoryCrThreeEnd =
            this.supplier.categoryCrThreeEnd || null
          this.suppliers.categoryCrThreeStart =
            this.supplier.categoryCrThreeStart || null
          this.suppliers.categoryCrTwoEnd =
            this.supplier.categoryCrTwoEnd || null
          this.suppliers.categoryCrTwoStart =
            this.supplier.categoryCrTwoStart || null
          this.costReductionForm.costReductionList = res.data.costReductionList.map(
            i => {
              i.year = i.year + '-01-01'
              return i
            }
          )
        })
    },
    addPurchase () {
      this.form.purchaseList.push({
        id: Math.floor(Math.random() * 1000000),
        fullPathIds: null,
        organizationNames: null,
        type: 'DELAY',
        days: null
      })
    },
    handleSelectionChange (val) {
      this.selectPurchaseList = val.map(i => i.id || i.configId)
    },
    delPurchase () {
      if (!this.selectPurchaseList.length) {
        this.$message({
          message: this.$t('common.msgSelectDelData'), // 请勾选要删除的数据
          type: 'error'
        })
        return
      }
      // 当前操作将删除数据，确认是否删除数据？
      this.$confirm(this.$t('common.ifDeleteData'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          let arr = []
          this.form.purchaseList.forEach(item => {
            if (item.id) {
              if (this.selectPurchaseList.indexOf(item.id) < 0) {
                arr.push(item)
              }
            }
            if (item.configId) {
              if (this.selectPurchaseList.indexOf(item.configId) < 0) {
                arr.push(item)
              }
            }
          })
          this.form.purchaseList = arr
        })
    },
    selectionOrg (row, index) {
      this.showOrgDialog = true
      this.showOrgDialogType = 'purchase'
      this.selectIndex = index
      this.currentRows = []
      if (this.showOrgDialogType == 'purchase') {
        if (
          this.form.purchaseList.length &&
          this.form.purchaseList[this.selectIndex].fullPathIds
        ) {
          let fullPathIds = this.form.purchaseList[this.selectIndex].fullPathIds.split(',')
          this.currentRows = fullPathIds
            .map(item => {
              return findMenuInfoByPath(item, this.options)
            })
            .filter(i => !!i)
        }
      }
    },
    normalizer (node) {
      const NODE = {
        id: node.fullPathId,
        label: node.organizationName
      }
      if (node.childOrganRelation && node.childOrganRelation.length) { NODE.children = node.childOrganRelation }
      return NODE
    },
    onCancel () {
      this.showOrgDialog = false
    },
    addOneOrg () {
      let fullPathIds = []
      let organizationNames = []
      this.currentRows.forEach(
        ({ fullPathId, organizationName }) => {
          fullPathIds.push(fullPathId)
          organizationNames.push(organizationName)
        }
      )
      if (this.showOrgDialogType == 'purchase') {
        let fullPathIdsList = []
        this.form.purchaseList.forEach((item, index) => {
          if (this.selectIndex != index) {
            if (item.fullPathIds) {
              fullPathIdsList = [
                ...fullPathIdsList,
                ...item.fullPathIds.split(',')
              ]
            }
          }
        })
        if (fullPathIdsList.some(i => fullPathIds.join(',').indexOf(i) > -1)) {
          this.$message({
            message: this.$t('dataConfMod.msgNotRepeatOrg'), // 添加组织不能重复
            type: 'error'
          })
          return
        }
        this.form.purchaseList[this.selectIndex].fullPathIds = fullPathIds.join(
          ','
        )
        this.form.purchaseList[this.selectIndex].organizationNames = organizationNames.join(',')
        this.form.purchaseList.push({})
        this.form.purchaseList.splice(this.form.purchaseList.length - 1, 1)
      }
      this.showOrgDialog = false
    },
    categoryChange (data) {
      this.getCrSetCategory(data)
    },
    selectionCat (row) {
      this.getCrSetCategory({
        pageNum: 1,
        pageSize: 15,
        setId: row.setId
      })
    },
    getCrSetCategory (data) {
      this.$http({
        url: '/api-report/config/getCrSetCategory',
        method: 'post',
        data: data,
        loading: true
      })
        .then(res => {
          this.categoryForm.total = res.data.total
          this.categoryForm.pageNum = res.data.pageNum
          this.categoryForm.pageSize = res.data.pageSize
          this.categoryForm.categoryList = res.data.list
          this.categoryForm.setId = data.setId
          this.categoryDialog = true
        })
    },
    categorySave (data) {
      this.$http({
        url: '/api-report/config/saveCrSetCategory',
        method: 'post',
        data: data,
        loading: true
      })
        .then(res => {
          this.$message({
            message: res.message,
            type: 'success'
          })
        })
    },
    selectMaterial (row) {
      this.getCrSetMaterial({
        pageNum: 1,
        pageSize: 15,
        setId: row.setId
      })
    },
    getCrSetMaterial (data) {
      this.$http({
        url: '/api-report/config/getCrSetMaterial',
        method: 'post',
        data: data
      })
        .then(res => {
          this.materialForm.materialList = res.data
          this.materialForm.setId = data.setId
          this.materialDialog = true
        })
    },
    materialSave (data) {
      this.$http({
        url: '/api-report/config/saveCrSetMaterial',
        method: 'post',
        data: data,
        loading: true
      })
    },
    addCostReductionList () {
      this.$http({
        url: '/api-report/config/getID',
        method: 'post',
        params: {},
        loading: true
      })
        .then(res => {
          this.costReductionForm.costReductionList.push({
            setId: res.data,
            year: null,
            rate: null
          })
        })
    },
    delCostReductionList () {
      if (!this.selectionCostReductionList.length) {
        this.$message({
          message: this.$t('common.msgSelectDelData'), // 请勾选要删除的数据
          type: 'error'
        })
        return
      }
      // 当前操作将删除数据，确认是否删除数据？
      this.$confirm(this.$t('common.ifDeleteData'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          let arr = []
          this.costReductionForm.costReductionList.forEach(item => {
            if (item.setId) {
              if (this.selectionCostReductionList.indexOf(item.setId) < 0) {
                arr.push(item)
              }
            }
          })
          this.costReductionForm.costReductionList = arr
        })
    },
    selectionCostReduction (val) {
      this.selectionCostReductionList = val.map(i => i.setId)
    },
    resetCostReductionList () {
      this.costReductionForm.costReductionList = []
      this.suppliers.categoryCrFour = 200
      this.suppliers.categoryCrOne = 50
      this.suppliers.categoryCrThreeEnd = 200
      this.suppliers.categoryCrThreeStart = 100
      this.suppliers.categoryCrTwoEnd = 100
      this.suppliers.categoryCrTwoStart = 50
    },
    save () {
      if (!this.form.purchaseList.length) {
        this.$message({
          message: this.$t('dataConfMod.msgAddOneOrderAnalysis'), // 至少添加一行订单交付分析
          type: 'error'
        })
        return
      }
      this.$refs.form.validate(valid => {
        if (valid) {
          this.$refs.form1.validate(valid1 => {
            if (valid1) {
              this.$refs.form2.validate(valid1 => {
                if (valid1) {
                  if (!this.costReductionForm.costReductionList.length) {
                    this.$message({
                      message: this.$t('dataConfMod.msgAddReduceData'), // 至少添加一行采购降本分析数据
                      type: 'error'
                    })
                    return
                  }
                  this.$refs.costReductionList.validate(valid1 => {
                    if (valid1) {
                      this.saveData()
                    } else {
                      this.$message({
                        message: this.$t('vendorMod.pleasefinishRequired'), // '请输入单据必填信息'
                        type: 'error'
                      })
                    }
                  })
                } else {
                  this.$message({
                    message: this.$t('vendorMod.pleasefinishRequired'), // '请输入单据必填信息'
                    type: 'error'
                  })
                }
              })
            } else {
              this.$message({
                message: this.$t('vendorMod.pleasefinishRequired'), // '请输入单据必填信息'
                type: 'error'
              })
            }
          })
        } else {
          this.$message({
            message: this.$t('vendorMod.pleasefinishRequired'), // '请输入单据必填信息'
            type: 'error'
          })
        }
      })
    },
    saveData () {
      this.$http({
        url: '/api-report/config/saveConfig',
        method: 'post',
        data: {
          purchaseList: this.form.purchaseList,
          supplier: { ...this.supplier, ...this.suppliers },
          costReductionList: this.costReductionForm.costReductionList.map(i => {
            i.year = i.year.substring(0, 4)
            return i
          })
        },
        loading: true
      })
        .then(res => {
          this.$message({
            message: res.message,
            type: 'success'
          })
          this.getDetail()
        })
    }
  }
}
</script>

<style scoped lang="scss">
.tab-form-style {
  :deep(.el-select .el-input) {
    width: 100px;
  }
  .input-with-select {
    :deep(.el-input-group__append),
    :deep(.el-input-group__prepend) {
      width: 60px;
      color: #46a6ff;
    }
    :deep(.el-input__suffix) {
      right: 25px;
    }
  }
  .tab-form-reset {
    position: absolute;
    right: 15px;
  }
  .supplierAnalysis {
    line-height: 32px;
    .supplierAnalysis-title {
      text-align: right;
      color: #242526;
    }
    .blod-title {
      font-weight: 600;
    }
    .supplierAnalysis-unit {
      color: #666;
    }
    :deep(.el-input-group__prepend) {
      width: 90px;
      text-align: center;
    }
  }
}
.supplierAnalysis-input {
  :deep(.el-form-item:first-child)  {
    margin: 0;
    padding: 0;
  }
}
</style>
<style>
.vue-treeselect__list
  .vue-treeselect__option.vue-treeselect__option--disabled
  > .vue-treeselect__label-container
  .vue-treeselect__checkbox-container {
  display: none;
}
.vue-treeselect__option--disabled .vue-treeselect__label-container {
  color: rgba(0, 0, 0, 0.65);
}
</style>
