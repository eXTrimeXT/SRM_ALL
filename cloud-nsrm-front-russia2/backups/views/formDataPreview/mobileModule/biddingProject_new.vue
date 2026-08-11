<template>
  <div class="model">
    <el-tabs
      v-model="activeName"
      class="tabs_top"
      style="height: 40px"
      @tab-click="handleClick"
    >
      <el-tab-pane
        label="项目信息"
        name="first"
        style="padding-top: 30px"
      >
        <el-collapse
          v-model="activeNames"
          class="modelA"
          @change="handleChange"
        >
          <el-collapse-item
            title="项目信息"
            name="1"
          >
            <!-- <el-row>
              <el-col :span="12"
                ><div class="bg-purple">流程模板：</div></el-col
              >
              <el-col :span="12"
                ><div class="bg-purple-light">
                  {{ requirementAttaches.processConfigName }}
                </div></el-col
              >
            </el-row> -->
            <el-row>
              <el-col :span="12">
                <div class="bg-purple">
                  创建人:
                </div>
              </el-col>
              <el-col
                :span="12"
              >
                <div class="bg-purple-light">
                  {{ requirementAttaches.createdByName }}
                </div>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="12">
                <div class="bg-purple">
                  部门:
                </div>
              </el-col>
              <el-col
                :span="12"
              >
                <div class="bg-purple-light">
                  {{ requirementAttaches.createdByDept }}
                </div>
              </el-col>
            </el-row>
            <el-row>
              <el-col
                :span="12"
              >
                <div class="bg-purple">
                  项目编号：
                </div>
              </el-col>
              <el-col
                :span="12"
              >
                <div class="bg-purple-light">
                  {{ requirementAttaches.bidingNum }}
                </div>
              </el-col>
            </el-row>

            <el-row>
              <el-col
                :span="12"
              >
                <div class="bg-purple">
                  项目名称：
                </div>
              </el-col>
              <el-col
                :span="12"
              >
                <div class="bg-purple-light">
                  {{ requirementAttaches.bidingName }}
                </div>
              </el-col>
            </el-row>
            <el-row>
              <el-col
                :span="12"
              >
                <div class="bg-purple">
                  报价开始时间：
                </div>
              </el-col>
              <el-col
                :span="12"
              >
                <div class="bg-purple-light">
                  {{ requirementAttaches.bidingStartDatetime }}
                </div>
              </el-col>
            </el-row>
            <el-row>
              <el-col
                :span="12"
              >
                <div class="bg-purple">
                  预计询价地点：
                </div>
              </el-col>
              <el-col
                :span="12"
              >
                <div class="bg-purple-light">
                  {{ requirementAttaches.bidingSite }}
                </div>
              </el-col>
            </el-row>
            <el-row>
              <el-col
                :span="12"
              >
                <div class="bg-purple">
                  询价截止时间：
                </div>
              </el-col>
              <el-col
                :span="12"
              >
                <div class="bg-purple-light">
                  {{ requirementAttaches.enrollEndDatetime }}
                </div>
              </el-col>
            </el-row>
            <el-row>
              <el-col
                :span="12"
              >
                <div class="bg-purple">
                  询价范围：
                </div>
              </el-col>
              <el-col
                :span="12"
              >
                <div class="bg-purple-light">
                  {{
                    $getDictLabel(
                      "BID_SCOPE",
                      requirementAttaches.bidingScope
                    )
                  }}
                </div>
              </el-col>
            </el-row>
            <el-row>
              <el-col
                :span="12"
              >
                <div class="bg-purple">
                  预算金额：
                </div>
              </el-col>
              <el-col
                :span="12"
              >
                <div class="bg-purple-light">
                  {{ requirementAttaches.budgetAmount + " (万元)" }}
                </div>
              </el-col>
            </el-row>
            <el-row>
              <el-col
                :span="12"
              >
                <div class="bg-purple">
                  询价类型：
                </div>
              </el-col>
              <el-col
                :span="12"
              >
                <div class="bg-purple-light">
                  {{
                    $getDictLabel("BID_TYPE", requirementAttaches.bidingType)
                  }}
                </div>
              </el-col>
            </el-row>
            <el-row>
              <el-col
                :span="12"
              >
                <div class="bg-purple">
                  评分规则：
                </div>
              </el-col>
              <el-col
                :span="12"
              >
                <div class="bg-purple-light">
                  {{
                    $getDictLabel(
                      "BID_GRADING",
                      requirementAttaches.evaluateMethod
                    )
                  }}
                </div>
              </el-col>
            </el-row>
            <el-row>
              <el-col
                :span="12"
              >
                <div class="bg-purple">
                  决标方式：
                </div>
              </el-col>
              <el-col
                :span="12"
              >
                <div class="bg-purple-light">
                  {{
                    $getDictLabel(
                      "BID_DECIDE_METHOD",
                      requirementAttaches.bidingAwardWay
                    )
                  }}
                </div>
              </el-col>
            </el-row>
            <el-row>
              <el-col
                :span="12"
              >
                <div class="bg-purple">
                  是否进价格库：
                </div>
              </el-col>
              <el-col
                :span="12"
              >
                <div class="bg-purple-light">
                  {{
                    $getDictLabel(
                      "YES_OR_NO",
                      requirementAttaches.isSyncToPriceLibrary
                    )
                  }}
                </div>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="12">
                <div class="bg-purple">
                  月利率：
                </div>
              </el-col>
              <el-col
                :span="12"
              >
                <div class="bg-purple-light">
                  {{ requirementAttaches.monthlyInterest }}
                </div>
              </el-col>
            </el-row>
            <el-row>
              <el-col
                :span="12"
              >
                <div class="bg-purple">
                  价格有效期自：
                </div>
              </el-col>
              <el-col
                :span="12"
              >
                <div class="bg-purple-light">
                  {{ requirementAttaches.defaultPriceValidFrom }}
                </div>
              </el-col>
            </el-row>
            <el-row>
              <el-col
                :span="12"
              >
                <div class="bg-purple">
                  价格有效期至：
                </div>
              </el-col>
              <el-col
                :span="12"
              >
                <div class="bg-purple-light">
                  {{ requirementAttaches.defaultPriceValidTo }}
                </div>
              </el-col>
            </el-row>
            <el-row>
              <el-col
                :span="12"
              >
                <div class="bg-purple">
                  配额分配方法：
                </div>
              </el-col>
              <el-col
                :span="12"
              >
                <div class="bg-purple-light">
                  {{
                    $getDictLabel(
                      "QUOTA_DISTRIBUTE_TYPE",
                      requirementAttaches.quotaDistributeType
                    )
                  }}
                </div>
              </el-col>
            </el-row>
          </el-collapse-item>

          <el-collapse-item
            title="工作小组"
            name="2"
            class="model_LineList"
          >
            <template v-if="requirementLineList.length > 10">
              <div class="LineList">
                <el-pagination
                  :page-size="pagesize"
                  layout="total,prev, next"
                  :total="requirementLineList.length"
                  @current-change="current_change"
                />
              </div>
              <div class="card">
                <el-table
                  :data="
                    requirementLineList.slice(
                      (currentPage - 1) * pagesize,
                      currentPage * pagesize
                    )
                  "
                  style="width: 100%; margin-top: 6px;"
                >
                  <el-table-column
                    type="index"
                    label="序号"
                  />
                  <el-table-column
                    prop="userName"
                    label="成员账号"
                  />
                  <el-table-column
                    prop="fullName"
                    label="成员姓名"
                  />
                  <el-table-column
                    prop="phone"
                    label="电话"
                  />
                  <el-table-column
                    prop="email"
                    label="电子邮箱"
                  />
                  <el-table-column
                    prop="position"
                    label="岗位"
                  />
                  <el-table-column
                    prop="lastUpdatedBy"
                    label="更新人"
                  />
                </el-table>
              </div>
            </template>
            <div
              v-else
              class="card"
            >
              <el-table
                :data="
                  requirementLineList.slice(
                    (currentPage - 1) * pagesize,
                    currentPage * pagesize
                  )
                "
                style="width: 100%; margin-top: 6px;"
              >
                <el-table-column
                  type="index"
                  label="序号"
                />
                <el-table-column
                  prop="userName"
                  label="成员账号"
                />
                <el-table-column
                  prop="fullName"
                  label="成员姓名"
                />
                <el-table-column
                  prop="phone"
                  label="电话"
                />
                <el-table-column
                  prop="email"
                  label="电子邮箱"
                />
                <el-table-column
                  prop="position"
                  label="岗位"
                />
                <el-table-column
                  prop="lastUpdatedBy"
                  label="更新人"
                />
              </el-table>
            </div>
            <!-- <div class="LineList">
              <el-row>
                <el-col :span="12">
                  <div>
                    <span>共{{ requirementLineList.length }}条</span>
                    <span
                      :data-radius="requirementLineList.length"
                      @click="pre($event, 1)"
                      >上一条</span
                    >
                    <span
                      :data-radius="requirementLineList.length"
                      @click="next($event, 1)"
                      >下一条</span
                    >
                  </div></el-col
                >
              </el-row>
            </div> -->
            <!-- <div class="card">
              <el-carousel :autoplay="false" ref="carousel1" heigth="20px">
                <el-carousel-item
                  v-for="(item, index) in this.requirementLineList"
                  :key="index"
                  name="index"
                >
                  <el-row>
                    <el-col :span="12"
                      ><div class="bg-purple">成员账号:</div></el-col
                    >
                    <el-col :span="12"
                      ><div class="bg-purple-light">
                        {{ item.userName }}
                      </div></el-col
                    >
                  </el-row>
                  <el-row>
                    <el-col :span="12"
                      ><div class="bg-purple">成员姓名:</div></el-col
                    >
                    <el-col :span="12"
                      ><div class="bg-purple-light">
                        {{ item.fullName }}
                      </div></el-col
                    >
                  </el-row>
                  <el-row>
                    <el-col :span="12"
                      ><div class="bg-purple">电话:</div></el-col
                    >
                    <el-col :span="12"
                      ><div class="bg-purple-light">
                        {{ item.phone }}
                      </div></el-col
                    >
                  </el-row>
                  <el-row>
                    <el-col :span="4"
                      ><div class="bg-purple">电子邮箱:</div></el-col
                    >
                    <el-col :span="20"
                      ><div class="bg-purple-light">
                        {{ item.email }}
                      </div></el-col
                    >
                  </el-row>
                  <el-row>
                    <el-col :span="12"
                      ><div class="bg-purple">岗位:</div></el-col
                    >
                    <el-col :span="12"
                      ><div class="bg-purple-light">
                        {{ item.position }}
                      </div></el-col
                    >
                  </el-row>
                  <el-row>
                    <el-col :span="12"
                      ><div class="bg-purple">技术评委:</div></el-col
                    >
                    <el-col :span="12"
                      ><div class="bg-purple-light">
                        <el-checkbox
                          v-model="item.judgeFlag"
                          true-label="Y"
                          false-label="N"
                          disabled
                        /></div
                    ></el-col>
                  </el-row>

                  <el-row>
                    <el-col :span="12"
                      ><div class="bg-purple">更新人:</div></el-col
                    >
                    <el-col :span="12"
                      ><div class="bg-purple-light">
                        {{ item.lastUpdatedBy }}
                      </div></el-col
                    >
                  </el-row>
                </el-carousel-item>
              </el-carousel>
            </div> -->
          </el-collapse-item>
          <el-collapse-item
            title="查看附件"
            name="3"
          >
            <el-row>
              <el-col
                :span="12"
              >
                <div class="bg-purple">
                  内部查看附件：
                </div>
              </el-col>
              <el-col :span="12">
                <div class="bg-purple-light" />
              </el-col>
            </el-row>
            <el-table
              :data="innerFiles"
              style="width: 100%"
              border
              height="133px"
            >
              <el-table-column
                align="center"
                type="index"
                width="30"
              />
              <el-table-column
                align="center"
                prop="fileName"
                :label="$t('bidMod.fileName')"
                min-width="150"
              >
                <template slot-scope="scope">
                  <c-upload-file
                    v-if="!scope.row.fileName"
                    :limit="1"
                    show-progress
                    :show-file-list="false"
                    :file-list="scope.row.fileList"
                    :cus-data="fileInfo"
                    @upload-success="innerHandleUploadSuccess"
                  />
                  <div
                    v-else
                    class="download-link-wrap"
                  >
                    <c-download-link
                      :id="scope.row.docId"
                      :name="scope.row.fileName"
                      ellipsis
                      class="download-link-item"
                    />
                    <i
                      class="el-icon-close close-icon"
                      @click="innerHandleAttachmentRemove(scope.row)"
                    />
                  </div>
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="comments"
                :label="$t('bidMod.remark')"
                min-width="100"
              >
                <template slot-scope="scope">
                  <el-input v-model="scope.row.comments" />
                </template>
              </el-table-column>
            </el-table>
            <el-row>
              <el-col
                :span="12"
              >
                <div
                  class="bg-purple"
                  style="margin-top: 20px"
                >
                  供方查看附件：
                </div>
              </el-col>
              <el-col :span="12">
                <div class="bg-purple-light" />
              </el-col>
            </el-row>
            <el-table
              :data="outerFiles"
              style="width: 100%"
              border
              height="133px"
            >
              <el-table-column
                align="center"
                type="index"
                width="30"
              />
              <el-table-column
                align="center"
                prop="fileName"
                :label="$t('bidMod.fileName')"
                min-width="150"
              >
                <template slot-scope="scope">
                  <c-upload-file
                    v-if="!scope.row.fileName"
                    :limit="1"
                    show-progress
                    :show-file-list="false"
                    :file-list="scope.row.fileList"
                    :cus-data="fileInfo"
                    @upload-success="outerHandleUploadSuccess"
                  />
                  <div
                    v-else
                    class="download-link-wrap"
                  >
                    <c-download-link
                      :id="scope.row.docId"
                      :name="scope.row.fileName"
                      ellipsis
                      class="download-link-item"
                    />
                    <i
                      class="el-icon-close close-icon"
                      @click="outerHandleAttachmentRemove(scope.row)"
                    />
                  </div>
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="comments"
                :label="$t('bidMod.remark')"
                min-width="100"
              >
                <template slot-scope="scope">
                  <el-input v-model="scope.row.comments" />
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>
          <!-- <el-collapse-item title="模板参考" name="4" class="model_LineList">
            <div class="LineList">
              <el-row>
                <el-col :span="12">
                  <div>
                    <span>共{{ bidFileConfigList.length }}条</span>
                    <span
                      :data-radius="bidFileConfigList.length"
                      @click="pre($event, 2)"
                      >上一条</span
                    >
                    <span
                      :data-radius="bidFileConfigList.length"
                      @click="next($event, 2)"
                      >下一条</span
                    >
                  </div></el-col
                >
              </el-row>
            </div>
            <div class="card">
              <el-carousel :autoplay="false" ref="carousel2" heigth="20px">
                <el-carousel-item
                  v-for="(item, index) in this.bidFileConfigList"
                  :key="index"
                  name="index"
                >
                  <el-row>
                    <el-col :span="12"
                      ><div class="bg-purple">参考文件:</div></el-col
                    >
                    <el-col :span="12"
                      ><div class="bg-purple-light">
                        <c-download-link
                          :id="item.referenceFileId"
                          :name="item.referenceFileName"
                          ellipsis
                          class="download-link-item"
                        /></div
                    ></el-col>
                  </el-row>
                  <el-row>
                    <el-col :span="12"
                      ><div class="bg-purple">资料要求:</div></el-col
                    >
                    <el-col :span="12"
                      ><div class="bg-purple-light">
                        {{ item.fileName }}
                      </div></el-col
                    >
                  </el-row>
                  <el-row>
                    <el-col :span="12"
                      ><div class="bg-purple">文件类型:</div></el-col
                    >
                    <el-col :span="12"
                      ><div class="bg-purple-light">
                        {{ item.referenceFileType }}
                      </div></el-col
                    >
                  </el-row>
                  <el-row>
                    <el-col :span="12"
                      ><div class="bg-purple">分行编码:</div></el-col
                    >
                    <el-col :span="12"
                      ><div class="bg-purple-light">
                        {{ item.unionCode }}
                      </div></el-col
                    >
                  </el-row>
                  <el-row>
                    <el-col :span="12"
                      ><div class="bg-purple">备注:</div></el-col
                    >
                    <el-col :span="12"
                      ><div class="bg-purple-light">
                        {{ item.comments }}
                      </div></el-col
                    >
                  </el-row>
                </el-carousel-item>
              </el-carousel>
            </div>
          </el-collapse-item> -->
          <el-collapse-item
            title="商务要求"
            name="5"
            class="model_LineList"
          >
            <el-row>
              <el-col
                :span="12"
              >
                <div class="bg-purple">
                  保证金金额：
                </div>
              </el-col>
              <el-col
                :span="12"
              >
                <div class="bg-purple-light">
                  {{ requirementAttaches.bondAmount }}
                </div>
              </el-col>
            </el-row>
            <el-row>
              <el-col
                :span="12"
              >
                <div class="bg-purple">
                  保证金提交方式：
                </div>
              </el-col>
              <el-col
                :span="12"
              >
                <div class="bg-purple-light">
                  {{
                    $getDictLabel(
                      "BID_BOND_SUBMISSION",
                      requirementAttaches.bondMethod
                    )
                  }}
                </div>
              </el-col>
            </el-row>
            <el-row>
              <el-col
                :span="12"
              >
                <div class="bg-purple">
                  保证金提交截止时间：
                </div>
              </el-col>
              <el-col
                :span="12"
              >
                <div class="bg-purple-light">
                  {{ requirementAttaches.bondEndDatetime }}
                </div>
              </el-col>
            </el-row>
            <el-row>
              <el-col
                :span="12"
              >
                <div class="bg-purple">
                  保证金缴纳账号：
                </div>
              </el-col>
              <el-col
                :span="12"
              >
                <div class="bg-purple-light">
                  {{ requirementAttaches.bankAccountNum }}
                </div>
              </el-col>
            </el-row>
            <el-row>
              <el-col
                :span="12"
              >
                <div class="bg-purple">
                  账户名称：
                </div>
              </el-col>
              <el-col
                :span="12"
              >
                <div class="bg-purple-light">
                  {{ requirementAttaches.bankAccountName }}
                </div>
              </el-col>
            </el-row>
            <el-row>
              <el-col
                :span="12"
              >
                <div class="bg-purple">
                  {{ $t("bidMod.bankBranchName") + " :" }}
                </div>
              </el-col>
              <el-col
                :span="12"
              >
                <div class="bg-purple-light">
                  {{ requirementAttaches.bankBranchName }}
                </div>
              </el-col>
            </el-row>
            <el-row>
              <el-col
                :span="12"
              >
                <div class="bg-purple">
                  {{ $t("bidMod.bondDesc") + " :" }}
                </div>
              </el-col>
              <el-col
                :span="12"
              >
                <div class="bg-purple-light">
                  {{ requirementAttaches.bondDesc }}
                </div>
              </el-col>
            </el-row>
          </el-collapse-item>

          <el-collapse-item
            title="报价币种设置"
            name="6"
            class="model_LineList"
          >
            <el-row>
              <el-col
                :span="12"
              >
                <div class="bg-purple">
                  {{ $t("bid_mod.standardCurrency") + " :" }}
                </div>
              </el-col>
              <el-col
                :span="12"
              >
                <div class="bg-purple-light">
                  {{
                    $getDictLabel(
                      "currency",
                      requirementAttaches.standardCurrency
                    )
                  }}
                </div>
              </el-col>
            </el-row>
            <el-row>
              <el-col
                :span="12"
              >
                <div class="bg-purple">
                  {{ $t("bid_mod.pricePrecision") + " :" }}
                </div>
              </el-col>
              <el-col
                :span="12"
              >
                <div class="bg-purple-light">
                  {{
                    $getDictLabel(
                      "PRICE_PRECISION",
                      requirementAttaches.pricePrecision
                    )
                  }}
                </div>
              </el-col>
            </el-row>
            <el-row>
              <el-col
                :span="12"
              >
                <div class="bg-purple">
                  {{ $t("bid_mod.exchangeRateType") + " :" }}
                </div>
              </el-col>
              <el-col
                :span="12"
              >
                <div class="bg-purple-light">
                  {{
                    $getDictLabel(
                      "EXCHANGE_RATE_TYPE",
                      requirementAttaches.exchangeRateType
                    )
                  }}
                </div>
              </el-col>
            </el-row>
            <el-row>
              <el-col
                :span="12"
              >
                <div class="bg-purple">
                  {{ $t("bid_mod.showRateType") + " :" }}
                </div>
              </el-col>
              <el-col
                :span="12"
              >
                <div class="bg-purple-light">
                  {{
                    $getDictLabel(
                      "YES_OR_NO",
                      requirementAttaches.showRateType
                    )
                  }}
                </div>
              </el-col>
            </el-row>
            <el-row>
              <el-col
                :span="12"
              >
                <div class="bg-purple">
                  {{ $t("bid_mod.currencyChangeDate") + " :" }}
                </div>
              </el-col>
              <el-col
                :span="12"
              >
                <div class="bg-purple-light">
                  {{ requirementAttaches.currencyChangeDate }}
                </div>
              </el-col>
            </el-row>
            <template v-if="quoteCurrencyList.length > 10">
              <div
                class="LineList"
                style="margin-top: 14px"
              >
                <el-pagination
                  :page-size="pagesizeB"
                  layout="total,prev, next"
                  :total="quoteCurrencyList.length"
                  @current-change="current_changeB"
                />
              </div>
              <div class="card">
                <el-table
                  :data="
                    quoteCurrencyList.slice(
                      (currentPageB - 1) * pagesizeB,
                      currentPageB * pagesizeB
                    )
                  "
                  style="width: 100%; margin-top: 6px;"
                >
                  <el-table-column
                    type="index"
                    label="序号"
                  />
                  <el-table-column
                    prop="currencyCode"
                    label="币种"
                  >
                    <template slot-scope="scope">
                      {{
                        $getDictLabel("BID_TENDER_CURRENCY", scope.row.currencyCode)
                      }}
                    </template>
                  </el-table-column>
                  <el-table-column
                    prop="currencyDesc"
                    label="描述"
                  />
                  <el-table-column
                    prop="priceTax"
                    label="汇率"
                    width="60"
                  />
                  <el-table-column
                    prop="email"
                    label="价格精度"
                  >
                    <template slot-scope="scope">
                      {{
                        $getDictLabel("PRICE_PRECISION", scope.row.pricePrecision)
                      }}
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </template>
            <div
              v-else
              class="card"
              style="margin-top: 14px"
            >
              <el-table
                :data="
                  quoteCurrencyList.slice(
                    (currentPageB - 1) * pagesizeB,
                    currentPageB * pagesizeB
                  )
                "
                style="width: 100%; margin-top: 6px;"
              >
                <el-table-column
                  type="index"
                  label="序号"
                />
                <el-table-column
                  prop="currencyCode"
                  label="币种"
                >
                  <template slot-scope="scope">
                    {{
                      $getDictLabel("BID_TENDER_CURRENCY", scope.row.currencyCode)
                    }}
                  </template>
                </el-table-column>
                <el-table-column
                  prop="currencyDesc"
                  label="描述"
                />
                <el-table-column
                  prop="priceTax"
                  label="汇率"
                  width="60"
                />
                <el-table-column
                  prop="email"
                  label="价格精度"
                >
                  <template slot-scope="scope">
                    {{
                      $getDictLabel("PRICE_PRECISION", scope.row.pricePrecision)
                    }}
                  </template>
                </el-table-column>
              </el-table>
            </div>
            <!-- <div class="LineList" style="margin-top: 14px">
              <el-row>
                <el-col :span="10">
                  <div>
                    <span>共{{ quoteCurrencyList.length }}条</span>
                    <span
                      :data-radius="quoteCurrencyList.length"
                      @click="pre($event, 3)"
                      >上一条</span
                    >
                    <span
                      :data-radius="quoteCurrencyList.length"
                      @click="next($event, 3)"
                      >下一条</span
                    >
                  </div></el-col
                >
                <el-col :span="14">
                  <div>以下是报价人可以使用的外币清单</div></el-col
                >
              </el-row>
            </div> -->
            <!-- <div class="card">
              <el-carousel :autoplay="false" ref="carousel3" heigth="50px">
                <el-carousel-item
                  v-for="(item, index) in this.quoteCurrencyList"
                  :key="index"
                  name="index"
                >
                  <el-row>
                    <el-col :span="12"
                      ><div class="bg-purple">币种:</div></el-col
                    >
                    <el-col :span="12"
                      ><div class="bg-purple-light">
                        {{
                          d_getlabel("BID_TENDER_CURRENCY", item.currencyCode)
                        }}
                      </div></el-col
                    >
                  </el-row>
                  <el-row>
                    <el-col :span="4"
                      ><div class="bg-purple">描述:</div></el-col
                    >
                    <el-col :span="20"
                      ><div class="bg-purple-light">
                        {{ item.currencyDesc }}
                      </div></el-col
                    >
                  </el-row>
                  <el-row>
                    <el-col :span="12"
                      ><div class="bg-purple">汇率:</div></el-col
                    >
                    <el-col :span="12"
                      ><div class="bg-purple-light">
                        {{ item.priceTax }}
                      </div></el-col
                    >
                  </el-row>
                  <el-row>
                    <el-col :span="12"
                      ><div class="bg-purple">价格精度:</div></el-col
                    >
                    <el-col :span="12"
                      ><div class="bg-purple-light">
                        {{ d_getlabel("PRICE_PRECISION", item.pricePrecision) }}
                      </div></el-col
                    >
                  </el-row>
                </el-carousel-item>
              </el-carousel>
            </div> -->
          </el-collapse-item>

          <!-- <el-collapse-item
            title="向供应商展示的联系方式"
            name="7"
            class="model_LineList"
          >
            <el-row>
              <el-col :span="12"
                ><div class="bg-purple">
                  {{ $t("bidMod.bidContactName") + " :" }}
                </div></el-col
              >
              <el-col :span="12"
                ><div class="bg-purple-light">
                  {{ requirementAttaches.bidContactName }}
                </div></el-col
              >
            </el-row>
            <el-row>
              <el-col :span="12"
                ><div class="bg-purple">
                  {{ $t("bidMod.bidMobilePhone") + " :" }}
                </div></el-col
              >
              <el-col :span="12"
                ><div class="bg-purple-light">
                  {{ requirementAttaches.bidMobilePhone }}
                </div></el-col
              >
            </el-row>
            <el-row>
              <el-col :span="8"
                ><div class="bg-purple">
                  {{ $t("bidMod.email") + " :" }}
                </div></el-col
              >
              <el-col :span="16"
                ><div class="bg-purple-light">
                  {{ requirementAttaches.bidEmail }}
                </div></el-col
              >
            </el-row>
          </el-collapse-item> -->
          <el-collapse-item
            title="报价控制"
            name="8"
            class="model_LineList"
          >
            <h5
              v-if="
                !(
                  requirementAttaches.withdrawBiding == 'N' &&
                  requirementAttaches.partPrice == 'N' &&
                  requirementAttaches.publicLowestPrice == 'N' &&
                  requirementAttaches.publicTotalRank == 'N'
                )
              "
            >
              报价控制
            </h5>

            <el-row v-if="requirementAttaches.withdrawBiding == 'Y'">
              <el-col
                :span="24"
              >
                {{ $t("bidMod.withdrawBiding1") }}
                <!-- <el-checkbox
                  disabled
                  true-label="Y"
                  false-label="N"
                  v-model="requirementAttaches.withdrawBiding"
                  >{{ $t("bidMod.withdrawBiding1") }}</el-checkbox
                > -->
              </el-col>
            </el-row>
            <el-row v-if="requirementAttaches.partPrice == 'Y'">
              <el-col
                :span="24"
              >
                {{ $t("bidMod.partPrice") }}
                <!-- <el-checkbox
                disabled
                  true-label="Y"
                  false-label="N"
                  v-model="requirementAttaches.partPrice"
                  >{{ $t("bidMod.partPrice") }}</el-checkbox
                > -->
              </el-col>
            </el-row>

            <el-row v-if="requirementAttaches.publicLowestPrice == 'Y'">
              <el-col
                :span="24"
              >
                {{ $t("bidMod.publicLowestPrice") }}
                <!-- <el-checkbox
                disabled
                  true-label="Y"
                  false-label="N"
                  v-model="requirementAttaches.publicLowestPrice"
                  >{{ $t("bidMod.publicLowestPrice") }}</el-checkbox
                > -->
              </el-col>
            </el-row>
            <el-row v-if="requirementAttaches.publicTotalRank == 'Y'">
              <el-col
                :span="24"
              >
                {{ $t("bidMod.publicTotalRank") }}
                <!-- <el-checkbox
                disabled
                  true-label="Y"
                  false-label="N"
                  v-model="requirementAttaches.publicTotalRank"
                  ></el-checkbox
                > -->
              </el-col>
            </el-row>
            <el-row>
              <span />
            </el-row>
            <h5
              v-if="
                !(
                  requirementAttaches.visibleRankResult == 'N' &&
                  requirementAttaches.visibleFinalPrice == 'N' &&
                  requirementAttaches.visibleWinVendor == 'N'
                )
              "
            >
              开标控制
            </h5>
            <el-row v-if="requirementAttaches.visibleRankResult == 'Y'">
              <el-col
                :span="24"
              >
                {{ $t("bidMod.visibleCodeRanking") }}
                <!-- <el-checkbox
                        disabled
                          true-label="Y"
                          false-label="N"
                          v-model="requirementAttaches.visibleCodeRanking"
                          >{{ $t("bidMod.visibleCodeRanking") }}</el-checkbox
                        > -->
              </el-col>
            </el-row>
            <el-row v-if="requirementAttaches.visibleFinalPrice == 'Y'">
              <el-col
                :span="24"
              >
                {{ $t("bidMod.visibleLowestPrice") }}
                <!-- <el-checkbox
                        disabled
                          true-label="Y"
                          false-label="N"
                          v-model="requirementAttaches.visibleLowestPrice"
                          >{{ $t("bidMod.visibleLowestPrice") }}</el-checkbox
                        > -->
              </el-col>
            </el-row>
            <el-row v-if="requirementAttaches.visibleWinVendor == 'Y'">
              <el-col
                :span="24"
              >
                {{ $t("bidMod.visibleWinVendor") }}
                <!-- <el-checkbox
                        disabled
                          true-label="Y"
                          false-label="N"
                          v-model="requirementAttaches.visibleWinVendor"
                          >{{ $t("bidMod.visibleWinVendor") }}</el-checkbox
                        > -->
              </el-col>
            </el-row>
          </el-collapse-item>
        </el-collapse>
      </el-tab-pane>
      <el-tab-pane
        label="项目需求"
        name="second"
        style="padding-top: 30px"
      >
        <el-collapse
          v-model="activeNamesA"
          @change="handleChangeA"
        >
          <el-collapse-item
            title="需求明细"
            name="2"
            class="model_LineList"
          >
            <template v-if="displayBidingItem.length > 10">
              <div class="LineList">
                <el-pagination
                  :page-size="pagesizeA"
                  layout="total,prev, next"
                  :total="displayBidingItem.length"
                  @current-change="current_changeA"
                />
              </div>
              <div class="card">
                <el-table
                  :data="
                    displayBidingItem.slice(
                      (currentPageA - 1) * pagesizeA,
                      currentPageA * pagesizeA
                    )
                  "
                  style="width: 100%; margin-top: 6px;"
                >
                  <el-table-column
                    type="index"
                    label="序号"
                  />
                  <el-table-column
                    prop="orgName"
                    label="业务实体"
                  />
                  <el-table-column
                    prop="invName"
                    label="库存组织"
                  />
                  <el-table-column
                    prop="ouName"
                    label="OU组名称"
                  />
                  <el-table-column
                    prop="targetNum"
                    label="物料编码"
                  />
                  <el-table-column
                    prop="targetDesc"
                    label="物料描述"
                    width="200"
                  />
                  <el-table-column
                    prop="quantity"
                    label="预计数量"
                  />
                  <el-table-column
                    prop="comments"
                    label="备注"
                  />
                </el-table>
              </div>
            </template>
            <div
              v-else
              class="card"
            >
              <el-table
                :data="
                  displayBidingItem.slice(
                    (currentPageA - 1) * pagesizeA,
                    currentPageA * pagesizeA
                  )
                "
                style="width: 100%; margin-top: 6px;"
              >
                <el-table-column
                  type="index"
                  label="序号"
                />
                <el-table-column
                  prop="orgName"
                  label="业务实体"
                />
                <el-table-column
                  prop="invName"
                  label="库存组织"
                />
                <el-table-column
                  prop="ouName"
                  label="OU组名称"
                />
                <el-table-column
                  prop="targetNum"
                  label="物料编码"
                />
                <el-table-column
                  prop="targetDesc"
                  label="物料描述"
                  width="200"
                />
                <el-table-column
                  prop="quantity"
                  label="预计数量"
                />
                <el-table-column
                  prop="comments"
                  label="备注"
                />
              </el-table>
            </div>
            <!-- <div class="LineList" >
              <el-row>
                <el-col :span="10">
                  <div>
                    <span>共{{ displayBidingItem.length }}条</span>
                    <span
                      :data-radius="displayBidingItem.length"
                      @click="pre($event, 4)"
                      >上一条</span
                    >
                    <span
                      :data-radius="displayBidingItem.length"
                      @click="next($event, 4)"
                      >下一条</span
                    >
                  </div></el-col
                >

              </el-row>
            </div>
            <div style="height:1000px" >
              <el-carousel :autoplay="false" ref="carousel4" height="1000px">
                <el-carousel-item
                  v-for="(item, index) in this.displayBidingItem"
                  :key="index"
                  name="index"
                >
                  <el-row>
                    <el-col :span="12"
                      ><div class="bg-purple">{{$t('bid_mod.ouNumber')+" :"}}</div></el-col
                    >
                    <el-col :span="12"
                      ><div class="bg-purple-light">
                        {{
                          item.ouNumber
                        }}
                      </div></el-col
                    >
                  </el-row>
                  <el-row>
                    <el-col :span="12"
                      ><div class="bg-purple">{{$t('bid_mod.ouName')+" :"}}</div></el-col
                    >
                    <el-col :span="12"
                      ><div class="bg-purple-light">
                        {{
                          item.ouName
                        }}
                      </div></el-col
                    >
                  </el-row>
                  <el-row>
                    <el-col :span="12"
                      ><div class="bg-purple">{{$t('bid_mod.baseOu')+" :"}}</div></el-col
                    >
                    <el-col :span="12"
                      ><div class="bg-purple-light">
                        {{
                          d_getlabel("YES_OR_NO",item.baseOu)
                        }}
                      </div></el-col
                    >
                  </el-row>
                   <el-row>
                    <el-col :span="12"
                      ><div class="bg-purple">{{$t('bid_mod.businessEntity')+" :"}}</div></el-col
                    >
                    <el-col :span="12"
                      ><div class="bg-purple-light">
                        {{
                          item.orgName
                        }}
                      </div></el-col
                    >
                  </el-row>
                  <el-row>
                    <el-col :span="12"
                      ><div class="bg-purple">{{$t('bid_mod.inv')+" :"}}</div></el-col
                    >
                    <el-col :span="12"
                      ><div class="bg-purple-light">
                        {{
                          item.invName
                        }}
                      </div></el-col
                    >
                  </el-row>
                  <el-row v-if="requirementAttaches.bidingAwardWay === 'COMBINED_DECISION'">
                    <el-col :span="12"
                      ><div class="bg-purple">{{$t('bid_mod.itemGroup')+" :"}}</div></el-col
                    >
                    <el-col :span="12"
                      ><div class="bg-purple-light">
                        {{
                          item.itemGroup
                        }}
                      </div></el-col
                    >
                  </el-row>
                  <el-row v-if="requirementAttaches.bidingAwardWay === 'COMBINED_DECISION'">
                    <el-col :span="12"
                      ><div class="bg-purple">{{$t('bid_mod.materialMatching')+" :"}}</div></el-col
                    >
                    <el-col :span="12"
                      ><div class="bg-purple-light">
                        {{
                          item.materialMatching
                        }}
                      </div></el-col
                    >
                  </el-row>
                  <el-row >
                    <el-col :span="12"
                      ><div class="bg-purple">{{$t('bid_mod.deliveryPlace')+" :"}}</div></el-col
                    >
                    <el-col :span="12"
                      ><div class="bg-purple-light">
                       <provice-city v-model="item.deliveryPlace" />
                      </div></el-col
                    >
                  </el-row>
                    <el-row >
                    <el-col :span="12"
                      ><div class="bg-purple">{{$t('bid_mod.priceType')+" :"}}</div></el-col
                    >
                    <el-col :span="12"
                      ><div class="bg-purple-light">
                      {{d_getlabel("PRICE_TYPE",item.priceType)}}
                      </div></el-col
                    >
                  </el-row>
                   <el-row >
                    <el-col :span="12"
                      ><div class="bg-purple">{{$t('bid_mod.formulaValue')+" :"}}</div></el-col
                    >
                    <el-col :span="12"
                      ><div class="bg-purple-light">
                      {{item.formulaValue}}
                      </div></el-col
                    >
                  </el-row>
                   <el-row >
                    <el-col :span="12"
                      ><div class="bg-purple">{{$t('bid_mod.purchaseType')+" :"}}</div></el-col
                    >
                    <el-col :span="12"
                      ><div class="bg-purple-light">
                      {{d_getlabel("PURCHASE_TYPE",item.purchaseType) }}
                      </div></el-col
                    >
                  </el-row>
                  <el-row >
                    <el-col :span="12"
                      ><div class="bg-purple">{{$t('bid_mod.tradeTerm')+" :"}}</div></el-col
                    >
                    <el-col :span="12"
                      ><div class="bg-purple-light">
                      {{d_getlabel("trade_clause",item.tradeTerm) }}
                      </div></el-col
                    >
                  </el-row>
                  <el-row >
                    <el-col :span="12"
                      ><div class="bg-purple">{{$t('bid_mod.transportType')+" :"}}</div></el-col
                    >
                    <el-col :span="12"
                      ><div class="bg-purple-light">
                      {{d_getlabel("TRANSF_TYPE",item.transportType) }}
                      </div></el-col
                    >
                  </el-row>
                   <el-row >
                    <el-col :span="12"
                      ><div class="bg-purple">{{$t('bid_mod.showRequireNum')+" :"}}</div></el-col
                    >
                    <el-col :span="12"
                      ><div class="bg-purple-light">
                      {{d_getlabel("YES_OR_NO",item.showRequireNum) }}
                      </div></el-col
                    >
                  </el-row>
                   <el-row >
                    <el-col :span="12"
                      ><div class="bg-purple">{{$t('bid_mod.warrantyPeriod')+" :"}}</div></el-col
                    >
                    <el-col :span="12"
                      ><div class="bg-purple-light">
                      {{item.warrantyPeriod}}
                      </div></el-col
                    >
                  </el-row>
                  <el-row >
                    <el-col :span="12"
                      ><div class="bg-purple">{{$t('bid_mod.purchaseRequest')+" :"}}</div></el-col
                    >
                    <el-col :span="12"
                      ><div class="bg-purple-light">
                      {{item.purchaseRequest}}
                      </div></el-col
                    >
                  </el-row>
                  <el-row >
                    <el-col :span="12"
                      ><div class="bg-purple">{{$t('bid_mod.purchaseRequest')+" :"}}</div></el-col
                    >
                    <el-col :span="12"
                      ><div class="bg-purple-light">
                      {{item.purchaseRequestRowNum}}
                      </div></el-col
                    >
                  </el-row>
                    <el-row >
                    <el-col :span="12"
                      ><div class="bg-purple">{{$t('bid_mod.purchaseRequestRowNum')+" :"}}</div></el-col
                    >
                    <el-col :span="12"
                      ><div class="bg-purple-light">
                      {{item.purchaseRequestRowNum}}
                      </div></el-col
                    >
                  </el-row>
                   <el-row >
                    <el-col :span="12"
                      ><div class="bg-purple">{{$t('bid_mod.awardedSupplierName')+" :"}}</div></el-col
                    >
                    <el-col :span="12"
                      ><div class="bg-purple-light">
                      {{item.awardedSupplierName}}
                      </div></el-col
                    >
                  </el-row>
                   <el-row >
                    <el-col :span="12"
                      ><div class="bg-purple">指定供应商名称：</div></el-col
                    >
                    <el-col :span="12"
                      ><div class="bg-purple-light">
                      {{item.targetNum}}
                      </div></el-col
                    >
                  </el-row>
                  <el-row >
                    <el-col :span="12"
                      ><div class="bg-purple">物料编码：}</div></el-col
                    >
                    <el-col :span="12"
                      ><div class="bg-purple-light">
                      {{item.targetDesc}}
                      </div></el-col
                    >
                  </el-row>
                   <el-row >
                    <el-col :span="12"
                      ><div class="bg-purple">单位：</div></el-col
                    >
                    <el-col :span="12"
                      ><div class="bg-purple-light">
                      {{d_getlabel("unit",item.uomCode) }}
                      </div></el-col
                    >
                  </el-row>
                  <el-row >
                    <el-col :span="12"
                      ><div class="bg-purple">采购分类：</div></el-col
                    >
                    <el-col :span="12"
                      ><div class="bg-purple-light">
                      {{item.categoryName}}
                      </div></el-col
                    >
                  </el-row>
                  <el-row >
                    <el-col :span="12"
                      ><div class="bg-purple">定价开始时间：</div></el-col
                    >
                    <el-col :span="12"
                      ><div class="bg-purple-light">
                      {{item.priceStartTime}}
                      </div></el-col
                    >
                  </el-row>
                   <el-row >
                    <el-col :span="12"
                      ><div class="bg-purple">定价结束时间：</div></el-col
                    >
                    <el-col :span="12"
                      ><div class="bg-purple-light">
                      {{item.priceEndTime}}
                      </div></el-col
                    >
                  </el-row>
                   <el-row >
                    <el-col :span="12"
                      ><div class="bg-purple">{{$t('bidMod.quantity')+" :"}}</div></el-col
                    >
                    <el-col :span="12"
                      ><div class="bg-purple-light">
                      {{item.quantity}}
                      </div></el-col
                    >
                  </el-row>
                   <el-row >
                    <el-col :span="12"
                      ><div class="bg-purple">{{$t('bidMod.amount')+" :"}}</div></el-col
                    >
                    <el-col :span="12"
                      ><div class="bg-purple-light">
                      {{item.amount}}
                      </div></el-col
                    >
                  </el-row>
                    <el-row >
                    <el-col :span="12"
                      ><div class="bg-purple">{{$t('bidMod.remark')+" :"}}</div></el-col
                    >
                    <el-col :span="12"
                      ><div class="bg-purple-light">
                      {{item.comments}}
                      </div></el-col
                    >
                  </el-row>

                </el-carousel-item>
              </el-carousel>
            </div> -->
          </el-collapse-item>
        </el-collapse>
      </el-tab-pane>
      <el-tab-pane
        label="邀请供应商"
        name="third"
        style="padding-top: 30px"
      >
        <el-table
          :data="t13table"
          style="width: 100%; margin-top: 6px;"
        >
          <el-table-column
            type="index"
            label="序号"
          />
          <el-table-column
            prop="vendorName"
            label="供应商名称列表:"
          />
        </el-table>
        <!-- <el-collapse
          v-model="activeNamesB"
          @change="handleChangeB"
          >
            <el-collapse-item title="邀请供应商" name="1" class="model_LineList">
        <div class="LineList">
          <el-row>
            <el-col :span="12">
              <div>
                <span>共{{ t13table.length }}条</span>
               <span
                      :data-radius="t13table.length"
                      @click="pre($event, 5)"
                      >上一条</span
                    >
                    <span
                      :data-radius="t13table.length"
                      @click="next($event, 5)"
                      >下一条</span
                    >
              </div></el-col
            >

          </el-row>
        </div>
        <div class="card">
          <el-carousel height="200" :autoplay="false" ref="carousel5">
            <el-carousel-item
              v-for="(item, index) in this.t13table"
              :key="index"
              name="index"
            >
              <el-row>
                <el-col :span="12"><div class="bg-purple">{{$t('bidMod.vendorCode')+" :"}}</div></el-col>
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.vendorCode }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"><div class="bg-purple">{{$t('bidMod.vendorName')+" :"}}</div></el-col>
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.vendorName }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"><div class="bg-purple">{{$t('bidMod.linkMan')+" :"}}</div></el-col>
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.linkManName }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"><div class="bg-purple">{{$t('bidMod.linkMan')+" :"}}</div></el-col>
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.linkManName }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"><div class="bg-purple">{{$t('bidMod.phone')+" :"}}</div></el-col>
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.phone }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="6"><div class="bg-purple">{{$t('bidMod.remark')+" :"}}</div></el-col>
                <el-col :span="18"
                  ><div class="bg-purple-light">
                    {{ item.comments }}
                  </div></el-col
                >
              </el-row>

            </el-carousel-item>
          </el-carousel>
        </div>
      </el-collapse-item>
          </el-collapse> -->
      </el-tab-pane>
      <!-- <el-tab-pane label="评分规则" name="fourth" style="padding-top: 40px">
          <el-collapse
          v-model="activeNamesC"
          @change="handleChangeC"
          >
          <el-collapse-item title="评分规则" name="1">
                <el-row>
              <el-col :span="8"
                ><div class="bg-purple">评分模板名称：</div></el-col
              >
              <el-col :span="16"
                ><div class="bg-purple-light">
                  {{ templatetitle }}
                </div></el-col
              >
            </el-row>
            <el-row>
              <el-col :span="8"
                ><div class="bg-purple">总分：</div></el-col
              >
              <el-col :span="16"
                ><div class="bg-purple-light">
                  {{ templatetotalScore  }}
                </div></el-col
              >
            </el-row>
            </el-collapse-item>
            <el-collapse-item title="邀请供应商" name="2" class="model_LineList">
        <div class="LineList">
          <el-row>
            <el-col :span="12">
              <div>
                <span>共{{ ruleItems.length }}条</span>
               <span
                      :data-radius="ruleItems.length"
                      @click="pre($event, 6)"
                      >上一条</span
                    >
                    <span
                      :data-radius="ruleItems.length"
                      @click="next($event, 6)"
                      >下一条</span
                    >
              </div></el-col
            >

          </el-row>
        </div>
        <div class="card">
          <el-carousel height="200" :autoplay="false" ref="carousel6">
            <el-carousel-item
              v-for="(item, index) in this.ruleItems"
              :key="index"
              name="index"
            >
              <el-row>
                <el-col :span="12"><div class="bg-purple">{{$t('bidMod.dimension')+" :"}}</div></el-col>
                <el-col :span="12"
                  ><div class="bg-purple-light">
                     {{d_getlabel("BIDDING_SCOR_DIM",item.scoreDimension) }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"><div class="bg-purple">{{$t('bidMod.scoreItem')+" :"}}</div></el-col>
                <el-col :span="12"
                  ><div class="bg-purple-light">
                   {{item.scoreItem}}
                  </div></el-col
                >
              </el-row>
                <el-row>
                <el-col :span="12"><div class="bg-purple">{{$t('bidMod.scoreStandard')+" :"}}</div></el-col>
                <el-col :span="12"
                  ><div class="bg-purple-light">
                   {{item.scoreStandard}}
                  </div></el-col
                >
              </el-row>
                <el-row>
                <el-col :span="12"><div class="bg-purple">{{$t('bidMod.scoreSource')+" :"}}</div></el-col>
                <el-col :span="12"
                  ><div class="bg-purple-light">
                  {{d_getlabel("SOURCE_VALUE",item.scoreSource) }}
                  </div></el-col
                >
              </el-row>
                <el-row>
                <el-col :span="12"><div class="bg-purple">{{$t('bidMod.scoreWeight')+" :"}}</div></el-col>
                <el-col :span="12"
                  ><div class="bg-purple-light">
                   {{item.scoreWeight}}
                  </div></el-col
                >
              </el-row>
               <el-row>
                <el-col :span="12"><div class="bg-purple">{{$t('bidMod.fullScore')+" :"}}</div></el-col>
                <el-col :span="12"
                  ><div class="bg-purple-light">
                   {{item.fullScore}}
                  </div></el-col
                >
              </el-row>

            </el-carousel-item>
          </el-carousel>
        </div>
      </el-collapse-item>
          </el-collapse>
      </el-tab-pane> -->
    </el-tabs>
  </div>
</template>
<script>
import CUploadFile from '@/library/components/c-upload-file'
import CDownloadLink from 'lib@/components/c-download-link'
import http from '@/utils/http'
import ProviceCity from '@/library/components/provice-city'
import { geti18n } from '@/main'

import {
  getRegion
} from '@/api/common'
const i18n = geti18n()

export default {
  components: {
    CUploadFile,
    CDownloadLink,
    ProviceCity
  },
  data () {
    return {
      currentPage: 1, // 采购品类初始页
      pagesize: 10, //    每页的数据
      currentPageA: 1, // 采购品类初始页
      pagesizeA: 10, //    每页的数据
      currentPageB: 1, // 采购品类初始页
      pagesizeB: 10, //    每页的数据
      bidProcessConfigIdList: {},
      activeName: 'first',
      fileuploadsList: [],
      managementAttaches: [], // 认证情况
      managementInfo: {}, // 管理体系信息
      orgInfos: [], // 业务实体
      orgCategorys: [], // 合作小类
      siteInfos: [], // 供应商地点信息
      bankInfos: [], // 银行信息
      cityList: [], // 市
      provinceList: [], // 省
      curRel: '',
      companyInfo: {
        businessLicense: ''
      }, // 企业基本信息
      currencyList_NATURE: [], // 企业性质
      currencyList_RELATION: [], // 境内外关系
      pay_plan: [],
      fileuploads: [],
      displayCatData: [], // 扩展品类
      dataAtt: [],
      fileRefresh: false,
      companyId: null,
      filesChangeData: [], // 附件变更数据
      curType: '',
      approvalFileList: [], // 审批附件信息
      orderFileList: [], // 订单附件
      requirementAffix: [], // 附件
      currencyList: [],
      currencyListSTAGE: [], // 到货款
      currencyListPERIOD: [], // 付款账期
      currencyListMODE: [], // 支付方式
      dialog: false,
      dialogVisible: false,
      // 文件上传配置信息
      fileInfo: {
        uploadType: 'FASTDFS', // 固定参数
        sourceType: 'WEB_APP', // 固定参数
        fileModular: 'sup', // 文件所属模块 -》基础模块
        fileFunction: 'vendorBiddingManagement', // 文件所属功能
        fileType: 'images' // 文件所属类型
      },
      activeNames: ['1', '2', '3', '4', '5', '6', '7', '8'],
      activeNamesA: ['1', '2', '3', '4', '5', '6'],
      activeNamesB: ['1', '2', '3', '4', '5', '6'],
      activeNamesC: ['1', '2', '3', '4', '5', '6'],
      userInfo: {},
      requirementAttaches: {
        processConfigId: '',
        bidingNum: '',
        bidingName: '',
        bidingSite: '',
        bidingScope: '',
        budgetAmount: '',
        targetType: 'SERVICE', // 默认
        bidingType: '',
        evaluateMethod: '',
        bidingAwardWay: '',
        enrollEndDatetime: '',
        bidingStartDatetime: '',
        sourceFrom: '',
        orgId: '',
        orgCode: '',
        orgName: '',
        monthlyInterest: '0.0435', // 月利率 默认值 0.0435
        quotaDistributeType: 'NULL_ALLOCATION', // 配额分配方法 默认值:空
        // 报价控制
        withdrawBiding: 'N',
        partPrice: 'Y',
        publicTargetPrice: 'N',
        publicCodeRanking: 'N',
        publicLowestPrice: 'N',
        publicTotalRank: 'N',
        visibleTargetPrice: 'N',
        visibleCodeRanking: 'N',
        visibleLowestPrice: 'N',
        visibleTotalRanking: 'N',
        visibleWinVendor: 'N',
        // 商务信息部分
        bondAmount: '0',
        bondMethod: '',
        bondEndDatetime: '',
        taxInclusivePrice: 'N',
        bidingCurrency: '',
        decimalAccuracy: '',
        bankAccountNum: '',
        bankAccountName: '',
        bankBranchName: '',
        bondDesc: '',
        // 第三部分
        bidEmail: '',
        bidMobilePhone: '',
        bidContactName: '',
        // 新增报价币种设置 [start] by chenzp20
        standardCurrency: 'CNY',
        pricePrecision: '4',
        exchangeRateType: 'COMPANY',
        showRateType: 'N',
        currencyChangeDate: new Date().getTime() // +'-'+ (new Date().getMonth()+1 )+'-'+ (new Date().getDate()+1 )
        // 新增报价币种设置 [end] by chenzp20
      },
      requirementLineList: [],
      // index:0,
      num: 0,
      fullscreen: false,
      n: 0,
      isFullscreen: true,
      browserKernel: '',
      canFullScreen: false,
      isFullScreen: false,

      innerFiles: [],
      outerFiles: [],
      bidFileConfigList: [],
      quoteCurrencyList: [],
      checkoutHandle: '', // 选择标签触发
      requireDesc: '',
      requirementId: '',
      pricingType: '',
      displayBidingItem: [],
      unit: [],
      t13table: [],
      templatetotalScore: '',
      templatetitle: '',
      ruleItems: []
    }
  },

  watch: {},
  created () {
    this.fatchDictData()

    // this.getData();
    console.log('this.$attrs.params', this.$attrs.params)
    let id = this.$attrs.params.row.bidingId
    this.$api.inq.bidding.getCurrencyByBidingId(id).then(({ data }) => {
      this.quoteCurrencyList = data
    })
    this.getFormDetail({ id })
    this.getFileList(id)
    this.getGroupList(id)
    this.getbidFileConfigList(id)

    // this.getlistPage({businessId:companyId});
    //  this.getFormDetail(this.$attrs.params.rowId );
    // 检查浏览器是否支持全屏
    // this.canFullScreen =
    //   document.fullscreenEnabled ||
    //   document.webkitFullscreenEnabled ||
    //   document.mozFullScreenEnabled ||
    //   document.msFullscreenEnabled;
    // if (document.webkitFullscreenEnabled) {
    //   this.browserKernel = "webkit";
    // } else if (document.mozFullScreenEnabled) {
    //   this.browserKernel = "gecko";
    // } else if (document.msFullscreenEnabled) {
    //   this.browserKernel = "trident";
    // } else if (document.fullscreenEnabled) {
    //   this.browserKernel = "others";
    // }
    // if (this.canFullScreen) {
    //   this.addFullScreenListener();
    // }
  },

  mounted () {},
  methods: {
    fatchDictData () {
      // 加载省
      getRegion({ queryType: 'province' }).then(res => {
        if (res.data) {
          this.provinceList = this.adaptProvinceCity(res.data, 'province')
        }
      })
    },
    // 适配省 市

    adaptProvinceCity (data, type) {
      let arr = []
      if (data && data.length > 0) {
        if (type === 'province') {
          // 省
          data.forEach(element => {
            arr.push({
              id: element.provinceId,
              value: element.provinceId.toString(),
              label: element.province
            })
          })
        } else if (type === 'city') {
          // 市
          data.forEach(element => {
            arr.push({
              id: element.cityId,
              value: element.cityId.toString(),
              label: element.city
            })
          })
        }
      }
      return arr
    },
    outerButtonClick (index) {
      this.bankRowIndex = index
    },
    outerHandleUploadSuccess (file) {
      const { id, name, createdBy, creationDate } = file
      this.requirementAttaches[this.bankRowIndex].fileuploadId = id.toString()
      this.requirementAttaches[this.bankRowIndex].attachName = name
      this.requirementAttaches[this.bankRowIndex].createdBy = createdBy
      this.requirementAttaches[this.bankRowIndex].creationDate = creationDate
    },

    // 移除
    outerHandleRemove (fileuploadId) {},
    handleScriptProgress (percent) {},
    current_change: function (currentPage) {
      console.log(currentPage)
      this.currentPage = currentPage
      // debugger
    },
    current_changeA: function (currentPage) {
      console.log(currentPage)
      this.currentPageA = currentPage
      // debugger
    },
    current_changeB: function (currentPage) {
      console.log(currentPage)
      this.currentPageB = currentPage
      // debugger
    },

    bidingGet (data) {
      return this.$http({
        url: '/api-brg/bidInitiating/biding/get',
        method: 'GET',
        params: data,
        loading: true
      })
    },
    async getFormDetail (params) {
      try {
        this.bidProcessConfigIdList = await this.getbidProcessConfigIdList() // 流程模板

        const { data } = await this.bidingGet(params) // 项目信息

        let requirementAttaches = data
        if (requirementAttaches) {
          //  7983382463578112
          this.bidProcessConfigIdList.data =
            this.bidProcessConfigIdList.data || []
          let row = this.bidProcessConfigIdList.data.filter(
            v => v.processConfigId === requirementAttaches.processConfigId
          )
          // console.log("row",row.processConfigName)
          requirementAttaches.processConfigName = row[0].processConfigName
        }
        requirementAttaches.currencyChangeDate = requirementAttaches.currencyChangeDate
          ? requirementAttaches.currencyChangeDate.split(' ')[0]
          : null
        // requirementAttaches.bidingEndDatetime =
        //   requirementAttaches.bidingEndDatetime

        // requirementAttaches.bidingStartDatetime =
        //   requirementAttaches.bidingStartDatetime

        // requirementAttaches.enrollEndDatetime =
        //   requirementAttaches.enrollEndDatetime
        requirementAttaches.budgetAmount = requirementAttaches.budgetAmount
          ? requirementAttaches.budgetAmount
          : ''

        this.requirementAttaches = requirementAttaches
      } catch (e) {
        console.log(e)
      }
    },
    // 模板参考
    getbidFileConfigList (bidingId) {
      this.$http({
        url: '/api-brg/bidInitiating/bidFileConfig/listAll',
        method: 'POST',
        data: { bidingId: bidingId },
        loading: true
      })
        .then(data => {
          if (data && data.data) {
            this.bidFileConfigList = data.data
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    // 查看附件
    getFileList (bidingId) {
      this.$http({
        url: '/api-brg/bidInitiating/bidFile/listAll',
        method: 'POST',
        data: { bidingId: bidingId },
        loading: true
      })
        .then(data => {
          if (data && data.data) {
            // console.log("data",data)
            this.innerFiles = data.data.filter(
              v => v.fileType === 'Enterprise'
            )
            this.outerFiles = data.data.filter(v => v.fileType === 'Supplier')
          }
        })
        .catch(err => {
          console.log(err)
        })
    },

    // 工作小组
    getGroupList (bidingId) {
      this.$http({
        url: '/api-brg/bidInitiating/group/listAll',
        method: 'POST',
        data: { bidingId: bidingId },
        loading: true
      })
        .then(data => {
          if (data && data.data) {
            let requirementLineList = data.data
            requirementLineList.filter(v => v.judgeFlag == 'Y')
            // console.log("requirementLineList",requirementLineList)
            this.requirementLineList = requirementLineList
          }
        })
        .catch(err => {
          console.log(err)
        })
    },

    // 获取流程模板
    getbidProcessConfigIdList () {
      return this.$http({
        url: '/api-brg/bidProcessConfig/bidProcessConfig/listAll',
        method: 'POST',
        data: {},
        loading: true
      })
    },

    getTab2List (bidingId) {
      this.$http({
        url: '/api-brg/bidInitiating/bidRequirementLine/listPage',
        method: 'POST',
        data: { bidingId },
        loading: true
      })
        .then(data => {
          if (data && data.data) {
            // console.log("data1",data)
            this.displayBidingItem = data.data.list
          }
        })
        .catch(err => {
          console.log(err)
        })
      this.$http({
        url: '/api-brg/bidInitiating/bidRequirement/getByBidingId',
        method: 'GET',
        params: { bidingId: bidingId },
        loading: true
      })
        .then(data => {
          if (data && data.data) {
            console.log('data2', data)
            this.requireDesc = data.data.requireDesc
            this.scoperequirementId = data.data.requirementId
            this.pricingType = data.data.pricingType
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    getTab3List (bidingId) {
      // 邀请供应商
      this.$http({
        url: '/api-brg/inviteVendor/bidVendor/listPage',
        method: 'POST',
        data: { bidingId: bidingId },
        loading: true
      })
        .then(data => {
          if (data && data.data) {
            this.t13table = data.data.list
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    getTab4List (bidingId) {
      // 查询头表
      this.$http({
        url: '/api-brg/evaluation/scoreRule/getByBidingId',
        method: 'GET',
        params: { bidingId: bidingId },
        loading: true
      })
        .then(data => {
          if (data && data.data) {
            this.templatetitle = data.data.ruleConfigName
            this.templatetotalScore = data.data.totalScore
          }
        })
        .catch(err => {
          console.log(err)
        })
      // 查询明细行---评分规则
      this.$http({
        url: '/api-brg/evaluation/scoreRuleLine/listPage',
        method: 'POST',
        data: { bidingId: bidingId },
        loading: true
      })
        .then(data => {
          if (data && data.data) {
            this.ruleItems = data.data.list
          }
        })
        .catch(err => {
          console.log(err)
        })
    },

    getlistPage (data) {
      this.$api.base.getFileListByBusinessId(data).then(async res => {
        console.log('附件', res)
        this.fileuploadsList = res.data.list
      })
    },
    next (event, number) {
      // console.log(" this.requirementAttaches.length",event.target.dataset.radius)
      this.num += 1
      if (number == 1) {
        if (this.num > event.target.dataset.radius - 1) {
          this.num = 0
          this.$refs.carousel1.setActiveItem(this.num)
        } else {
          this.$refs.carousel1.setActiveItem(this.num)
        }
      } else if (number == 2) {
        if (this.num > event.target.dataset.radius - 1) {
          this.num = 0
          this.$refs.carousel2.setActiveItem(this.num)
        } else {
          this.$refs.carousel2.setActiveItem(this.num)
        }
      } else if (number == 3) {
        if (this.num > event.target.dataset.radius - 1) {
          this.num = 0
          this.$refs.carousel3.setActiveItem(this.num)
        } else {
          this.$refs.carousel3.setActiveItem(this.num)
        }
      } else if (number == 4) {
        if (this.num > event.target.dataset.radius - 1) {
          this.num = 0
          this.$refs.carousel4.setActiveItem(this.num)
        } else {
          this.$refs.carousel4.setActiveItem(this.num)
        }
      } else if (number == 5) {
        if (this.num > event.target.dataset.radius - 1) {
          this.num = 0
          this.$refs.carousel5.setActiveItem(this.num)
        } else {
          this.$refs.carousel5.setActiveItem(this.num)
        }
      } else if (number == 6) {
        if (this.num > event.target.dataset.radius - 1) {
          this.num = 0
          this.$refs.carousel6.setActiveItem(this.num)
        } else {
          this.$refs.carousel6.setActiveItem(this.num)
        }
      }
    },
    pre (event, number) {
      // console.log(" this.requirementAttaches.length1",event.target.dataset.radius)
      this.num -= 1
      if (number == 1) {
        if (this.num < 0) {
          this.num = event.target.dataset.radius - 1
          this.$refs.carousel1.setActiveItem(this.num)
        } else {
          this.$refs.carousel1.setActiveItem(this.num)
        }
      } else if (number == 2) {
        if (this.num < 0) {
          this.num = event.target.dataset.radius - 1
          this.$refs.carousel2.setActiveItem(this.num)
        } else {
          this.$refs.carousel2.setActiveItem(this.num)
        }
      } else if (number == 3) {
        if (this.num < 0) {
          this.num = event.target.dataset.radius - 1
          this.$refs.carousel3.setActiveItem(this.num)
        } else {
          this.$refs.carousel3.setActiveItem(this.num)
        }
      } else if (number == 4) {
        if (this.num < 0) {
          this.num = event.target.dataset.radius - 1
          this.$refs.carousel4.setActiveItem(this.num)
        } else {
          this.$refs.carousel4.setActiveItem(this.num)
        }
      } else if (number == 5) {
        if (this.num < 0) {
          this.num = event.target.dataset.radius - 1
          this.$refs.carousel5.setActiveItem(this.num)
        } else {
          this.$refs.carousel5.setActiveItem(this.num)
        }
      } else if (number == 6) {
        if (this.num < 0) {
          this.num = event.target.dataset.radius - 1
          this.$refs.carousel6.setActiveItem(this.num)
        } else {
          this.$refs.carousel6.setActiveItem(this.num)
        }
      }
    },
    // 设置全屏
    // 全屏设置
    fullTable () {
      console.log('1111')
      if (this.canFullScreen) {
        if (this.isFullScreen) {
          // 关闭全屏
          this.exitFullScreen()
          this.isFullScreen = false
        } else {
          // 打开全屏
          console.log('2222')
          this.Full(document.getElementsByClassName('card')[0])
          this.isFullScreen = true
        }
      } else {
        this.$message.warning({
          content: '当前浏览器暂不支持全屏模式，请切换浏览器后重新尝试！',
          duration: 3
        })
      }
    },
    Full (element) {
      // 判断各种浏览器，找到正确的方法
      console.log('3333')
      const requestMethod =
        element.requestFullScreen || // W3C
        element.webkitRequestFullScreen || // Chrome, safari
        element.mozRequestFullScreen || // FireFox
        element.msRequestFullscreen // IE11
      if (requestMethod) {
        console.log('4444')
        requestMethod.call(element)
      }
    },
    hideDialog () {
      this.dialog = true
    },
    exitFullScreen () {
      var exitMethod =
        document.exitFullscreen || // W3C
        document.mozCancelFullScreen || // FireFox
        document.webkitExitFullscreen || // Chrome等
        document.msExitFullscreen // IE11
      if (exitMethod) {
        exitMethod.call(document)
      }
    },
    addFullScreenListener () {
      const self = this
      document.onkeydown = function (e) {
        if (e && e.keyCode === 122) {
          // 捕捉F11键盘动作
          e.preventDefault() // 阻止F11默认动作
          self.toggleFullScreen()
        }
      }
      // 监听不同浏览器的全屏事件，并件执行相应的代码
      switch (self.browserKernel) {
        case 'webkit':
          document.onwebkitfullscreenchange = function () {
            if (document.webkitIsFullScreen) {
              self.isFullScreen = true
            } else {
              self.isFullScreen = false
            }
          }
          break
        case 'gecko':
          document.onmozfullscreenchange = function () {
            if (document.mozFullScreen) {
              self.isFullScreen = true
            } else {
              self.isFullScreen = false
            }
          }
          break
        case 'trident':
          document.onmsfullscreenchange = function () {
            if (document.msFullscreenElement) {
              self.isFullScreen = true
            } else {
              self.isFullScreen = false
            }
          }
          break
        case 'others':
          document.onfullscreenchange = function () {
            if (document.fullscreen) {
              self.isFullScreen = true
            } else {
              self.isFullScreen = false
            }
          }
          break
        default:
          break
      }
    },

    getFullCreeen () {
      this.n++
      this.n % 3 == 0
        ? this.outFullCreeen(document)
        : this.inFullCreeen(document.getElementsByClassName('card')[0])
    },
    inFullCreeen (element) {
      console.log(
        'ocument.getElementsByClassName(\'.card\')[0]',
        document.getElementsByClassName('card')[0]
      )
      let el = document.getElementsByClassName('card')[0]
      let rfs =
        el.requestFullScreen ||
        el.webkitRequestFullScreen ||
        el.mozRequestFullScreen ||
        el.msRequestFullScreen
      if (typeof rfs !== 'undefined' && rfs) {
        rfs.call(el)
      } else if (typeof window.ActiveXObject !== 'undefined') {
        let wscript = new ActiveXObject('WScript.Shell')
        if (wscript != null) {
          wscript.SendKeys('{F11}')
        }
      }
    },
    outFullCreeen (element) {
      let el = element
      let cfs =
        el.cancelFullScreen ||
        el.webkitCancelFullScreen ||
        el.mozCancelFullScreen ||
        el.exitFullScreen
      if (typeof cfs !== 'undefined' && cfs) {
        cfs.call(el)
      } else if (typeof window.ActiveXObject !== 'undefined') {
        let wscript = new ActiveXObject('WScript.Shell')
        if (wscript != null) {
          wscript.SendKeys('{F11}')
        }
      }
    },

    handleChange (val) {
      console.log(val)
    },
    handleChangeA (val) {
      console.log(val)
    },
    handleChangeB (val) {
      console.log(val)
    },
    handleChangeC (val) {
      console.log(val)
    },
    handleClick (tab, event) {
      console.log(tab, event)
      this.checkoutHandle = tab.index
      if (this.checkoutHandle === '1') {
        this.getTab2List(this.$attrs.params.row.bidingId)
      }
      if (this.checkoutHandle === '2') {
        this.getTab3List(this.$attrs.params.row.bidingId)
      }
      if (this.checkoutHandle === '3') {
        this.getTab4List(this.$attrs.params.row.bidingId)
      }
    },
    message () {}
  }
}
</script>
<style scoped lang="scss">
.popContainer {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
  img {
    width: 100%;
    height: 100%;
    object-fit: contain;
  }
}

.model /deep/ .el-tabs__nav {
  width: 100%;
  position: fixed !important;
  left: 0;
  top: 0;
  z-index: 100;
  background: #fff;
  text-align: center;
}
.model /deep/ .el-tabs__active-bar {
  width: 0px !important;
}

.model {
  width: 100%;

  .bg-purple {
    text-align: left;
  }
  .bg-purple-light {
    text-align: right;
    min-width: 100px;
  }
  .bg {
    text-align: left;
    line-height: 32px;
  }

  .model_LineList {
    width: 100%;

    .LineList {
      border-bottom: 1px solid #e6ebf5;
      background-color: #f4f5f7;

      margin-top: -10px;
      margin-left: -10px;
      margin-right: -10px;

      span {
        margin-right: 4px;
      }
      .document {
        color: aqua;
        text-align: right;
        float: right;
      }
    }
    .card {
      width: 100%;
    }
  }
  // .el-collapse-item__wrap{
  //   padding: 0px;
  // }
}
</style>
