<template>
  <div class="model">
    <el-collapse
      v-model="activeNames"
      class="modelA"
      @change="handleChange"
    >
      <!-- <el-collapse-item title="供应商账号信息" name="1">
        <el-row>
          <el-col :span="12"><div class="bg-purple">用户名：</div></el-col>
          <el-col :span="12"
            ><div class="bg-purple-light">
              {{ this.userInfo.username }}
            </div></el-col
          >
        </el-row>
        <el-row>
          <el-col :span="12"><div class="bg-purple">昵称：</div></el-col>
          <el-col :span="12"
            ><div class="bg-purple-light">
              {{ this.userInfo.nickname }}
            </div></el-col
          >
        </el-row>

        <el-row>
          <el-col :span="12"><div class="bg-purple">邮箱：</div></el-col>
          <el-col :span="12"
            ><div class="bg-purple-light">
              {{ this.userInfo.email }}
            </div></el-col
          >
        </el-row>
      </el-collapse-item> -->
      <el-collapse-item
        title="基本信息"
        name="2"
      >
        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              供应商名称：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.companyInfo.companyName }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              创建人：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.companyInfo.createdBy }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              部门：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.companyInfo.ceeaDeptName }}
            </div>
          </el-col>
        </el-row>
        <!-- <el-row>
          <el-col :span="12"><div class="bg-purple">境内外关系：</div></el-col>
          <el-col :span="12"
            ><div class="bg-purple-light">
              {{ $getDictLabel("RELATION",companyInfo.overseasRelation)  }}
            </div></el-col
          >
        </el-row>
        <el-row v-if="curRel === 'INSIDE'">
          <el-col :span="12"><div class="bg-purple">企业性质：</div></el-col>
          <el-col :span="12"
            ><div class="bg-purple-light">
              {{ $getDictLabel("COMPANY_NATURE",companyInfo.companyType) }}
            </div></el-col
          >
        </el-row>
        <el-row>
          <el-col :span="6"><div class="bg-purple">营业执照：</div></el-col>
          <el-col :span="18"
            ><div class="bg-purple-light">
              <c-download-link
                :id="companyInfo.businessLicenseFileId"
                :name="companyInfo.businessLicense"
                ellipsis

              /></div
          ></el-col>
        </el-row> -->
        <!-- <el-row>
          <el-col :span="12"><div class="bg-purple">企业名称：</div></el-col>
          <el-col :span="12"
            ><div class="bg-purple-light">
              {{ this.companyInfo.companyName }}
            </div></el-col
          >
        </el-row> -->

        <el-row v-if="curType !== 'GETI'">
          <el-col :span="12">
            <div class="bg-purple">
              注册资本：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{
                this.companyInfo.registeredCapital +
                  this.companyInfo.registCurrency
              }}
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              成立日期：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.companyInfo.companyCreationDate }}
            </div>
          </el-col>
        </el-row>
        <!-- <el-row>
          <el-col :span="12"><div class="bg-purple">企业简称：</div></el-col>
          <el-col :span="12"
            ><div class="bg-purple-light">
              {{ this.companyInfo.companyShortName }}
            </div></el-col
          >
        </el-row> -->
        <!-- 只有境内供应商有 -->
        <!-- <el-row v-if="curRel === 'INSIDE'">
          <el-col :span="12"
            ><div class="bg-purple">社会统一信用代码：</div></el-col
          >
          <el-col :span="12"
            ><div class="bg-purple-light">
              {{ this.companyInfo.lcCode }}
            </div></el-col
          >
        </el-row> -->
        <!-- 只有境外供应商有 -->
        <!-- <el-row v-if="curRel === 'OUT'">
          <el-col :span="12"><div class="bg-purple">DUNS编号：</div></el-col>
          <el-col :span="12"
            ><div class="bg-purple-light">
              {{ this.companyInfo.dunsCode }}
            </div></el-col
          >
        </el-row> -->
        <!-- 法人代表 -->
        <el-row>
          <el-col :span="12">
            <div class="bg-purple">
              法定代表人：
            </div>
          </el-col>
          <el-col
            :span="12"
          >
            <div class="bg-purple-light">
              {{ this.companyInfo.legalPerson }}
            </div>
          </el-col>
        </el-row>
        <!-- <el-row>
          <el-col :span="12"><div class="bg-purple">登记机关：</div></el-col>
          <el-col :span="12"
            ><div class="bg-purple-light">
              {{ this.companyInfo.registrationAuthority }}
            </div></el-col
          >
        </el-row> -->

        <!-- 个体户不用显示 -->
        <!-- <el-row v-if="curType !== 'GETI'">
          <el-col :span="12"
            ><div class="bg-purple">
              {{ $t("vendorMod.businessStartFrom") + "：" }}
            </div></el-col
          >
          <el-col :span="12"
            ><div class="bg-purple-light">
              {{ this.companyInfo.businessStartDate }}
            </div></el-col
          >
        </el-row>
        <el-row>
          <el-col :span="12"
            ><div class="bg-purple">是否长期供应商：</div></el-col
          >
          <el-col :span="12"
            ><div class="bg-purple-light">
              <el-checkbox
                :disabled="companyInfo.ifLongPeriod == 'N'"
                v-model="companyInfo.ifLongPeriod"
                true-label="Y"
                false-label="N"
              /></div
          ></el-col>
        </el-row> -->
        <!-- 个体户不用显示 -->
        <!-- 营业日期至 -->
        <!-- <el-row v-if="curType !== 'GETI' && companyInfo.ifLongPeriod !== 'Y'">
          <el-col :span="12"
            ><div class="bg-purple">
              {{ $t("vendorMod.businessEndAt") + "：" }}
            </div></el-col
          >
          <el-col :span="12"
            ><div class="bg-purple-light">
              {{ this.companyInfo.businessEndDate }}
            </div></el-col
          >
        </el-row>
        <el-row>
          <el-col :span="12"
            ><div class="bg-purple">
              {{ $t("vendorMod.businessAddr") + "：" }}
            </div></el-col
          >
          <el-col :span="12"
            ><div class="bg-purple-light">
              {{ this.companyInfo.companyCountry }}
            </div></el-col
          >
        </el-row>
        <el-row v-if="curRel === 'INSIDE'">
          <el-col :span="12"
            ><div class="bg-purple">
              {{ $t("vendorMod.province") + "：" }}
            </div></el-col
          >
          <el-col :span="12"
            ><div class="bg-purple-light">
              {{ this.companyInfo.companyProvince }}
            </div></el-col
          >
        </el-row>

        <el-row v-if="curRel === 'INSIDE'">
          <el-col :span="12"
            ><div class="bg-purple">
              {{ $t("vendorMod.city") + "：" }}
            </div></el-col
          >
          <el-col :span="12"
            ><div class="bg-purple-light">
              {{ this.companyInfo.companyCity }}
            </div></el-col
          >
        </el-row>

        <el-row>
          <el-col :span="12"
            ><div class="bg-purple">
              {{ $t("components.address.detailAddress") + "：" }}
            </div></el-col
          >
          <el-col :span="12"
            ><div class="bg-purple-light">
              {{ this.companyInfo.companyAddress }}
            </div></el-col
          >
        </el-row>
        <el-row>
          <el-col :span="6"
            ><div class="bg-purple">
              {{ $t("vendorMod.businessScope") + "：" }}
            </div></el-col
          >
          <el-col :span="18"
            ><div class="bg-purple-light">
              {{ this.companyInfo.businessScope }}
            </div></el-col
          >
        </el-row> -->
      </el-collapse-item>

      <!-- <el-collapse-item title="联系人信息" name="3" class="model_LineList">
        <div class="LineList">
          <el-row>
            <el-col :span="12">
              <div>
                <span>共{{ this.requirementLineList.length }}条</span>
                <span
                  :data-radius="requirementLineList.length"
                  @click="pre($event, 3)"
                  >上一条</span
                >
                <span
                  :data-radius="requirementLineList.length"
                  @click="next($event, 3)"
                  >下一条</span
                >
              </div></el-col
            >
          </el-row>
        </div>
        <div class="card">
          <el-carousel :autoplay="false" ref="carousel3" heigth="20px">
            <el-carousel-item
              v-for="(item, index) in this.requirementLineList"
              :key="index"
              name="index"
            >
              <el-row>
                <el-col :span="12"><div class="bg-purple">姓名:</div></el-col>
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.contactName }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"><div class="bg-purple">性别:</div></el-col>
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ $getDictLabel('GENDER', item.ceeaGender) }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"><div class="bg-purple">部门:</div></el-col>
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.ceeaDeptName }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"><div class="bg-purple">职位:</div></el-col>
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.position }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">联系方式:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.ceeaContactMethod }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"><div class="bg-purple">邮箱:</div></el-col>
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.email }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">默认联系人:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    <el-checkbox
                      disabled
                      v-model="item.ceeaDefaultContact"
                      true-label="Y"
                      false-label="N"
                    /></div
                ></el-col>
              </el-row>
              <el-row>
                <el-col :span="12"><div class="bg-purple">备注:</div></el-col>
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.ceeaComments }}
                  </div></el-col
                >
              </el-row>
            </el-carousel-item>
          </el-carousel>
        </div>
      </el-collapse-item>
      <el-collapse-item title="银行信息" name="4" class="model_LineList">
        <div class="LineList">
          <el-row>
            <el-col :span="12">
              <div>
                <span>共{{ this.bankInfos.length }}条</span>
                <span :data-radius="bankInfos.length" @click="pre($event, 4)"
                  >上一条</span
                >
                <span :data-radius="bankInfos.length" @click="next($event, 4)"
                  >下一条</span
                >
              </div></el-col
            >
          </el-row>
        </div>
        <div class="card">
          <el-carousel :autoplay="false" ref="carousel4" heigth="20px">
            <el-carousel-item
              v-for="(item, index) in this.bankInfos"
              :key="index"
              name="index"
            >
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">银行代码:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.bankCode }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">银行名称:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.bankName }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">开户行名称:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.openingBank }}
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
                  ><div class="bg-purple">账户名称:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.bankAccountName }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">银行账号:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.bankAccount }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"><div class="bg-purple">币种:</div></el-col>
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.currencyCode }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">是否主账户:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    <el-checkbox
                      v-model="item.ceeaMainAccount"
                      disabled
                      true-label="Y"
                      false-label="N"
                    /></div
                ></el-col>
              </el-row>
              <el-row>
                <el-col :span="12"><div class="bg-purple">启用:</div></el-col>
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    <el-checkbox
                      v-model="item.ceeaEnabled"
                      disabled
                      true-label="Y"
                      false-label="N"
                    /></div
                ></el-col>
              </el-row>
            </el-carousel-item>
          </el-carousel>
        </div>
      </el-collapse-item> -->
      <!-- <el-collapse-item title="供应商地点信息" name="5" class="model_LineList">
        <div class="LineList">
          <el-row>
            <el-col :span="12">
              <div>
                <span>共{{ this.siteInfos.length }}条</span>
                <span :data-radius="siteInfos.length" @click="pre($event, 5)"
                  >上一条</span
                >
                <span :data-radius="siteInfos.length" @click="next($event, 5)"
                  >下一条</span
                >
              </div></el-col
            >
          </el-row>
        </div>
        <div class="card">
          <el-carousel :autoplay="false" ref="carousel5" heigth="20px">
            <el-carousel-item
              v-for="(item, index) in this.siteInfos"
              :key="index"
              name="index"
            >
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">业务实体:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.orgName }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">地点名称:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.vendorSiteCode }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"><div class="bg-purple">国家:</div></el-col>
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.country }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"><div class="bg-purple">地区:</div></el-col>
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.province }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"><div class="bg-purple">城市:</div></el-col>
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.city }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">详细地址:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.addressDetail }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">邮政编码:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.postCode }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">地址备注:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.siteComment }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"><div class="bg-purple">启用:</div></el-col>
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    <el-checkbox
                      v-model="item.enabledFlag"
                      true-label="Y"
                      false-label="N"
                      disabled
                    /></div
                ></el-col>
              </el-row>
            </el-carousel-item>
          </el-carousel>
        </div>
      </el-collapse-item> -->
      <el-collapse-item
        title="业务实体"
        name="6"
        class="model_LineList"
      >
        <template v-if="orgInfos.length > 6">
          <div class="LineList">
            <el-pagination
              :page-size="pagesize"
              layout="total,prev, next"
              :total="orgInfos.length"
              @current-change="current_change"
            />
          </div>
          <div class="card">
            <!-- categoryData.slice((currentPage-1)*pagesize,currentPage*pagesize) -->
            <el-table
              :data="
                orgInfos.slice(
                  (currentPage - 1) * pagesize,
                  currentPage * pagesize
                )
              "
              style="width: 100%; margin-top: 6px"
            >
              <el-table-column
                prop="index"
                label="序号"
                width="100"
              />
              <el-table-column
                prop="orgCode"
                label="OU"
                align="center"
              />
            </el-table>
          </div>
        </template>
        <div
          v-else
          class="card"
        >
          <el-table
            :data="orgInfos"
            style="width: 100%"
          >
            <el-table-column
              type="index"
              label="序号"
              width="100"
            />
            <el-table-column
              prop="orgCode"
              label="OU"
              align="center"
            />
            <!-- <el-table-column prop="buName" label="事业部"> </el-table-column> -->
          </el-table>
        </div>
      </el-collapse-item>
      <el-collapse-item
        title="采购品类"
        name="7"
        class="model_LineList"
      >
        <template v-if="orgCategorys.length > 10">
          <div class="LineList">
            <el-pagination
              :page-size="pagesize"
              layout="total,prev, next"
              :total="orgCategorys.length"
              @current-change="current_change"
            />
          </div>
          <div class="card">
            <!-- categoryData.slice((currentPage-1)*pagesize,currentPage*pagesize) -->
            <el-table
              :data="
                orgCategorys.slice(
                  (currentPage - 1) * pagesize,
                  currentPage * pagesize
                )
              "
              style="width: 100%; margin-top: 6px;"
            >
              <!-- <el-table-column type="index" label="序号" width="100">
              </el-table-column> -->
              <el-table-column
                prop="orgName"
                label="OU"
                align="center"
              />
              <el-table-column
                prop="categoryName"
                label="采购品类"
              />
            </el-table>
          </div>
        </template>
        <div
          v-else
          class="card"
        >
          <el-table
            :data="orgCategorys"
            style="width: 100%"
          >
            <el-table-column
              prop="orgName"
              label="OU"
              align="center"
            />
            <el-table-column
              prop="categoryName"
              label="采购品类"
            />
          </el-table>
        </div>
      </el-collapse-item>

      <!-- <el-collapse-item title="管理体系信息" name="8" class="model_LineList">
        <el-form v-model="managementInfo" class="rel-form-select">
          <el-row :gutter="50">
            <el-col :span="24">
              <el-form-item label="是否通过ISO9001质量体系认证(如是请上传附件)">
                <el-radio-group v-model="managementInfo.ifIsoQuality" disabled>
                  <el-radio label="Y">是</el-radio>
                  <el-radio label="N">否</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item
                label="是否通过ISO14001环境体系认证(如是请上传附件)"
              >
                <el-radio-group v-model="managementInfo.ifIsoEnviron" disabled>
                  <el-radio label="Y">是</el-radio>
                  <el-radio label="N">否</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item
                label="是否通过OHSAS18000职业、健康安全体系认证(如是请上传附件)"
              >
                <el-radio-group v-model="managementInfo.ifOhsasSafe" disabled>
                  <el-radio label="Y">是</el-radio>
                  <el-radio label="N">否</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="其他认证情况(如是请上传附件)">
                <el-input v-model="managementInfo.otherAuthSit" disabled />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </el-collapse-item>

          <el-collapse-item title="认证情况" name="9" class="model_LineList">
        <div class="LineList">
          <el-row>
            <el-col :span="12">
              <div>
                <span>共{{ this.managementAttaches.length }}条</span>
                <span :data-radius="managementAttaches.length" @click="pre($event, 9)"
                  >上一条</span
                >
                <span :data-radius="managementAttaches.length" @click="next($event, 9)"
                  >下一条</span
                >
              </div></el-col
            >
          </el-row>
        </div>
        <div class="card">
          <el-carousel :autoplay="false" ref="carousel9" heigth="20px">
            <el-carousel-item
              v-for="(item, index) in this.managementAttaches"
              :key="index"
              name="index"
            >
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">认证类型:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    <c-download-link  :id="item.fileuploadId" :name="item.authType || {}" class="download-link-item" />
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">认证描述:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.authDescription }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">认证编号:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.authNum }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">认证时间:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.authDate }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">认证机构:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.authOrg }}
                  </div></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><div class="bg-purple">证件有效期至:</div></el-col
                >
                <el-col :span="12"
                  ><div class="bg-purple-light">
                    {{ item.endDate }}
                  </div></el-col
                >
              </el-row>

            </el-carousel-item>
          </el-carousel>
        </div>
      </el-collapse-item> -->

      <el-collapse-item
        title="相关认证信息"
        name="10"
      >
        <el-table
          :data="fileuploadsList"
          style="width: 100%"
          border
          max-height="250px"
        >
          <el-table-column
            align="center"
            type="index"
            label="序号"
            width="50"
          />
          <el-table-column
            align="center"
            prop="fileFullname"
            label="附件"
            min-width="80"
          >
            <template slot-scope="scope">
              <c-download-link
                :id="scope.row.fileuploadId"
                :name="scope.row.fileSourceName"
                ellipsis
                class="download-link-item"
              />
            </template>
          </el-table-column>
          <el-table-column

            align="center"
            prop="filePureName"
            label="附件名称"
            width="120"
          />
        </el-table>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>
<script>
import CUploadFile from '@/library/components/c-upload-file'
import CDownloadLink from 'lib@/components/c-download-link'
import http from '@/utils/http'
import { parseTime, adaptDictData } from '@/utils'

import {
  getDictItemList,
  getRegion
} from '@/api/common'

export default {
  components: {
    CUploadFile,
    CDownloadLink
  },
  data () {
    return {
       currentPage: 1, // 采购品类初始页
      currentPageA: 1, // 资质审查初始页
      pagesize: 10, //    每页的数据
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
      currencyList_CURRENCY: [], // 币种
      companyInfo: {
        businessLicense: ''
      }, // 企业基本信息
      currencyList_NATURE: [], // 企业性质
      currencyList_RELATION: [], // 境内外关系
      payExplainData: [], // 存储payExplainHttp返回来的数据
      pay_plan: [],
      fileuploads: [],
      displayCatData: [], // 扩展品类
      dataAtt: [],
      fileRefresh: false,
      companyId: null,
      filesChangeData: [], // 附件变更数据
      curType: '',
      requirementAccess: [], // 账户信息
      approvalFileList: [], // 审批附件信息
      orderFileList: [], // 订单附件
      requirementAffix: [], // 附件
      currencyList: [],
      currencyListA: [],
      currencyListB: [],
      currencyListC: [],
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
      activeNames: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10'],
      userInfo: {},
      requirementAttaches: [],
      requirementLineList: [],
      // index:0,
      num: 0,
      fullscreen: false,
      n: 0,
      isFullscreen: true,
      browserKernel: '',
      canFullScreen: false,
      isFullScreen: false
    }
  },

  watch: {
    // currencyList_RELATION() {
    //   this.$nextTick(() => {
    //     //此时就可以获取到在created赋值后的assessmentTypeList了
    //   let companyId = this.$attrs.params.companyId;
    // this.getFormDetail({ companyId });
    //   });
    // },
  },
  created () {
    this.fatchDictData()

    console.log('this.$attrs.params', this.$attrs.params)
    let companyId = this.$attrs.params.companyId
    this.getFormDetail({ companyId })
    this.getlistPage({ businessId: companyId })
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
      // 批量查询字典
      let dictParamsArr = [
        // { dictCode: "RELATION" },
        // { dictCode: "COMPANY_NATURE" },
        { dictCode: 'BID_TENDER_CURRENCY' },
        { dictCode: 'country' },
        { dictCode: 'GENDER' }, // 性别
        { dictCode: 'VENDOR_SITE_CODE' } // 地点名称
        //  GENDER
      ]

      getDictItemList(dictParamsArr).then((res) => {
        const [
          // RELATION,
          // COMPANY_NATURE,
          BID_TENDER_CURRENCY,
          country,
          GENDER,
          VENDOR_SITE_CODE
        ] = res.data
        // this.currencyList_RELATION = adaptDictData(RELATION.RELATION, "dict");
        // this.currencyList_NATURE = adaptDictData(
        //   COMPANY_NATURE.COMPANY_NATURE,
        //   "dict"
        // );
        this.currencyList_CURRENCY = adaptDictData(
          BID_TENDER_CURRENCY.BID_TENDER_CURRENCY,
          'dict'
        )
        this.currencyList_country = adaptDictData(country.country, 'dict')
        this.currencyList_GENDER = adaptDictData(GENDER.GENDER, 'dict')
        this.currencyList_SITE_CODE = adaptDictData(
          VENDOR_SITE_CODE.VENDOR_SITE_CODE,
          'dict'
        )
      })
      // 加载省
      getRegion({ queryType: 'province' }).then((res) => {
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
          data.forEach((element) => {
            arr.push({
              id: element.provinceId,
              value: element.provinceId.toString(),
              label: element.province
            })
          })
        } else if (type === 'city') {
          // 市
          data.forEach((element) => {
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
  // 获取创建人、部门
       getDeptName (data) {
      if (data) {
        return this.$http({
          url: '/api-rbac/rbac-anon/getUserInfoByAccount',
          method: 'GET',
          params: { account: data }
        })
      }
    },
    getFormDetail (data) {
      this.$http({
        url: '/api-sup/info/companyInfo/getInfoByParam',
        method: 'POST',
        params: data,
        loading: true
      })
        .then(async (res) => {
          if (res) {
            console.log('res', res)
            // this.userInfo = res.data.userInfo || {};

            let obj = res.data.companyInfo || {}
            this.curType = obj.companyType
            this.curRel = obj.overseasRelation

            this.currencyList_CURRENCY = this.currencyList_CURRENCY || []
            this.currencyList_CURRENCY.forEach((item) => {
              if (item.value == obj.registCurrency) {
                obj.registCurrency = item.label
              }
            })
            this.currencyList_country = this.currencyList_country || []
            this.currencyList_country.forEach((item) => {
              if (item.value == obj.companyCountry) {
                obj.companyCountry = item.label
              }
            })
            // console.log("provinceList",this.provinceList)

            let parame = { queryType: 'city', parentId: obj.companyProvince }
            await getRegion(parame).then((res) => {
              if (res.data) {
                this.cityList = this.adaptProvinceCity(res.data, 'city')
              }
            })
            // console.log("cityList",this.cityList)
            this.provinceList = this.provinceList || []
            this.provinceList.forEach((item) => {
              if (item.value == obj.companyProvince) {
                obj.companyProvince = item.label
              }
            })
            this.cityList.forEach((item) => {
              if (item.value == obj.companyCity) {
                obj.companyCity = item.label
              }
            })
            let depName = obj.createdBy || ''
            let deptName = await this.getDeptName(depName)
            console.log('[deptName]', deptName)
            if (deptName) {
               obj.createdBy = deptName.data.nickName || ''
                obj.ceeaDeptName = deptName.data.department || ''
            }
            this.companyInfo = obj // 企业基本信息

            let contactInfos = res.data.contactInfos || []

            contactInfos.forEach((item) => {
              this.currencyList_GENDER.forEach((elm) => {
                if (elm.value == item.ceeaGender) {
                  item.ceeaGender = elm.label
                }
              })
            })
            this.requirementLineList = contactInfos // 联系人信息

            let bankInfos = res.data.bankInfos || []
            bankInfos.forEach((item) => {
              this.currencyList_CURRENCY.forEach((elm) => {
                if (elm.value == item.currencyCode) {
                  item.currencyCode = elm.label
                }
              })
            })
            this.bankInfos = bankInfos // 银行信息

            let siteInfos = res.data.siteInfos || [] // 供应商地点信息
              siteInfos.forEach((item) => {
                this.currencyList_SITE_CODE.forEach((elm) => {
                  if (elm.value == item.vendorSiteCode) {
                    item.vendorSiteCode = elm.label
                  }
                })
                this.currencyList_country.forEach((elm) => {
                  if (elm.value == item.country) {
                    item.country = elm.label
                  }
                })
                // console.log(22222)
                let parame = { queryType: 'city', parentId: item.province }
                 getRegion(parame).then((res) => {
                  if (res.data) {
                    this.cityList = this.adaptProvinceCity(res.data, 'city') || []
                    // console.log("this.cityList",this.cityList)

                     this.cityList.forEach((elm) => {
                      if (elm.value == item.city) {
                        item.city = elm.label
                      }
                    })
                  }
                })

                // console.log("cityList",this.cityList)

                this.provinceList.forEach((elm) => {
                  if (elm.value == item.province) {
                    item.province = elm.label
                  }
                })
              })
            this.siteInfos = res.data.siteInfos || []
            this.orgInfos = res.data.orgInfos || [] // 业务实体

            this.orgCategorys = res.data.orgCategorys || [] // 合作小类
            this.managementInfo = res.data.managementInfo || [] // 管理体系信息

            this.managementAttaches = res.data.managementAttaches || []
          }
        })
        .catch((err) => {
          console.log(err)
        })
    },

    getlistPage (data) {
      this.$api.base.getFileListByBusinessId(data).then(async (res) => {
        console.log('附件', res)
        this.fileuploadsList = res.data.list
      })
    },
    next (event, number) {
      // console.log(" this.requirementAttaches.length",event.target.dataset.radius)
      this.num += 1
      if (number == 3) {
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
      } else if (number == 9) {
        if (this.num > event.target.dataset.radius - 1) {
          this.num = 0
          this.$refs.carousel9.setActiveItem(this.num)
        } else {
          this.$refs.carousel9.setActiveItem(this.num)
        }
      }
    },
    pre (event, number) {
      // console.log(" this.requirementAttaches.length1",event.target.dataset.radius)
      this.num -= 1
      if (number == 3) {
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
      } else if (number == 9) {
        if (this.num < 0) {
          this.num = event.target.dataset.radius - 1
          this.$refs.carousel9.setActiveItem(this.num)
        } else {
          this.$refs.carousel9.setActiveItem(this.num)
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
    current_change: function (currentPage) {
      console.log(currentPage)
      this.currentPage = currentPage
      // debugger
    },
    current_changeA: function (currentPage) {
      console.log(currentPage)
      this.currentPage = currentPage
      // debugger
    },

    handleChange (val) {
      console.log(val)
    }
  }
}
</script>
<style scoped lang="scss">
.banner,
.banner .block,
.banner >>> .el-carousel .el-carousel--horizontal,
.block >>> .el-carousel__container {
  height: 100% !important;
  overflow: hidden;
}

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
