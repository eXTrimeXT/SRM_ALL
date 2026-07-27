<template>
    <div class="intBidEval-box">
        <div class="header-box">
            <div class="title-box">
                <span style="margin-right: 12px;font-size: 16px;">{{ souName }}-技术标评审</span>
                <el-select v-model="curSupplier" placeholder="请选择" @change="handleSupplyChange">
                    <el-option
                               v-for="item in supplySelectList"
                               :key="item.vendorId"
                               :label="item.vendorName"
                               :value="item.vendorId">
                    </el-option>
                </el-select>
            </div>
            <div class="dialogue" @click="goDialogue">
                <img src="../../../../../assets/images/dialogue.png" alt="">
                <span>去对话</span>
            </div>
        </div>
        <div class="content">
            <div class="eval-box" :style="{ width: isExpand ? 'calc(50% - 130px)' : 'calc(50% - 5px)' }">
                <div class="title-box">
                    <div class="title">评审要求</div>
                    <el-select v-model="selectReviewItem" clearable filterable placeholder="搜索"
                               @change="handleReviewChange">
                        <el-option
                                   v-for="item in scoreList"
                                   :key="item.scoreRuleId"
                                   :label="$getDictLabel('SOU_SCORE_CONFIG_ITEM', item.scoreItem) + `-` + item.reviewItem"
                                   :value="item.scoreRuleId">
                        </el-option>
                    </el-select>
                </div>
                <div class="eval-item">
                    <div class="item-box" v-for="item in scoreList" :key="item.reviewItem"
                         :style="{ height: item.isOpen ? 'auto' : '44px' }">
                        <div class="item-title">
                            <div style="cursor: pointer;" @click="handleOpen(item.reviewItem)"><i
                                   :class="item.isOpen ? 'el-icon-caret-bottom' : 'el-icon-caret-right'"></i><span>{{
                                       $getDictLabel('SOU_SCORE_CONFIG_ITEM', item.scoreItem) }}-{{ item.reviewItem }}</span>
                            </div>
                        </div>
                        <div class="item-con">
                            <div class="item-con-inner" v-for="temp in item.vendorScoreList" :key="temp.vendorId">
                                <div class="tip" v-if="temp.isSave && temp.vendorId === curSupplier">已填写评审说明</div>
                                <div class="according" v-show="temp.vendorId === curSupplier">
                                    <div class="title" v-if='temp.answerAndQuotationList.length && temp.isAnswer'>原文引用依据</div>
                                    <div class="text-box">
                                        <div class="text-item" v-for="(answer, index) in temp.answerAndQuotationList"
                                             :key="index">
                                             <div v-if="answer.answer != 'no_answer'" style="padding-bottom:13px;">
                                                <div class="text">{{ answer.answer }}</div>
                                                <div class="file">
                                                    <div class="page" v-for="file in answer.quotationList" :key="file.fileId">
                                                        <el-tooltip effect="dark" :content="file.fileName" placement="top">
                                                            <img v-if="/(\.pdf|\.PDF)$/i.test(file.fileName)"
                                                                src="../../../../../assets/images/pdf_icon.png" alt="">
                                                            <img v-else src="../../../../../assets/images/doc_icon.png" alt="">
                                                        </el-tooltip>
                                                        <div v-for="(doc, docIndex) in file.location" :key="docIndex"
                                                            class="annotation-box">
                                                            <span
                                                                @click.self="chooseAnnotation(JSON.parse(doc)[0], file.fileId)">{{
                                                                    docIndex
                                                                    + 1 }}</span>
                                                            <template v-if="JSON.parse(doc).length > 1">
                                                                [<i
                                                                v-for="(docData, i) in JSON.parse(doc)"
                                                                :key="i"
                                                                @click.self="chooseAnnotation(docData, file.fileId)">{{ i + 1
                                                                }}</i>
                                                                ]
                                                            </template>
                                                        </div>
                                                    </div>
                                                </div>
                                             </div>
                                            
                                        </div>
                                    </div>
                                </div>
                                <div class="describe" v-show="temp.vendorId === curSupplier">
                                    <div class="desc-box">
                                        <div class="title">评审说明</div>
                                        <el-input
                                            type="textarea"
                                            :rows="3"
                                            :placeholder="item.scoreDesc"
                                            style="margin-top: 8px"
                                            v-model="temp.extDescription"
                                            :disabled="isReadOnly"
                                            @blur="handleDescOrScore(temp, item.maxScore)">
                                        </el-input>
                                    </div>
                                    <div class="score-box" v-if="item.maxScore && Number(item.maxScore) > 0">
                                        <div class="title" style="margin-top: 12px;">评分(此项最高分{{ item.maxScore }}分)</div>
                                        <el-input
                                            placeholder="请输入评分"
                                            style="margin-top: 8px;"
                                            v-model="temp.score"
                                            :disabled="isReadOnly"
                                            v-input-format="{
                                                type: 'float',
                                                negative: false,
                                                digits: 2
                                            }"
                                            min="0"
                                            @blur="handleDescOrScore(temp, item.maxScore)">
                                        </el-input>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="preview-box" :style="{ width: isExpand ? 'calc(50% - 130px)' : 'calc(50% - 5px)' }">
                <ComFilePreview :fileData="fileData"
                                :clarifyFileId="clarifyFileId"
                                :previewType="previewType"
                                :previewLoading="previewLoading" />
            </div>
            <div class="file-box">
                <div class="arrow" :class="isExpand ? 'isExpand' : 'isCollapse'" @click="handleExpand"></div>
                <div class="file-box-con"
                     :style="{ width: isExpand ? '260px' : '0px', padding: isExpand ? '20px 20px 0' : '0px' }">
                    <div class="file-title">澄清文件</div>
                    <div class="file-item">
                        <div v-for="fileItem in replayFileList" :key="fileItem.fileId">
                            <div class="item" :class="fileItem.isActive ? 'active' : ''"
                                 v-if="fileItem.vendorId === curSupplier"
                                 @click="handleFileClick(fileItem.fileId, 'replayFileList')">
                                <img v-if="/(\.pdf|\.PDF)$/i.test(fileItem.fileName)"
                                     src="../../../../../assets/images/pdf_icon.png" alt="">
                                <img v-else-if="/(\.doc|\.DOC|\.docx|\.DOCX)$/i.test(fileItem.fileName)"
                                     src="../../../../../assets/images/doc_icon.png" alt="">
                                <img v-else-if="/(\.xlsx|\..xlsm|\..xlsm|\.xltm|\.xml|\.xlsb)$/i.test(fileItem.fileName)"
                                     src="../../../../../assets/images/excel.png" alt="">
                                <img v-else src="../../../../../assets/images/ppt.png" alt="">
                                <div class="file-name">{{ fileItem.fileName }}</div>
                            </div>
                        </div>
                    </div>
                    <div class="file-title">投标文件</div>
                    <div class="file-item">
                        <div v-for="fileItem in techFileList" :key="fileItem.orderDocId">
                            <div class="item" :class="fileItem.isActive ? 'active' : ''"
                                 v-if="fileItem.vendorId === curSupplier"
                                 @click="handleFileClick(fileItem.orderDocId, 'techFileList')">
                                <img v-if="/(\.pdf|\.PDF)$/i.test(fileItem.orderFileName)" src="../../../../../assets/images/pdf_icon.png" alt="">
                                <img v-else-if="/(\.doc|\.DOC|\.docx|\.DOCX)$/i.test(fileItem.orderFileName)" src="../../../../../assets/images/doc_icon.png" alt="">
                                <img v-else-if="/(\.xlsx|\..xlsm|\..xlsm|\.xltm|\.xml|\.xlsb)$/i.test(fileItem.orderFileName)" src="../../../../../assets/images/excel.png" alt="">
                                <img v-else src="../../../../../assets/images/ppt.png" alt="">
                                <div class="file-name">{{ fileItem.orderFileName }}</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
<script>
import Vue from 'vue'
import ComFilePreview from './components/filePreview'
export default {
    name: 'InteBidEval',
    components: {
        ComFilePreview
    },
    data() {
        return {
            isReadOnly: false,
            projectId: '',
            groupId: '',
            techScoreHeadId: '',
            extProjectNo: '',
            souName: '',
            scoreList: [],
            curSupplier: '', // 当前供应商
            supplySelectList: [],
            selectReviewItem: null,
            fileList: [], // 附件列表
            replayFileList: [], // 澄清文件
            techFileList: [], // 技术文件
            isExpand: true,
            previewLoading: true,// 文档预览加载loading
            previewType: 'clarify',// 文档预览 展示类型
            fileData: {}, // 文件预览展示数据
            clarifyFileId: null // 澄清文件展示id
        }
    },
    created() {
        const param = this.$attrs.params
        this.projectId = param.projectId
        this.groupId = param.groupId
        this.techScoreHeadId = param.techScoreHeadId
        this.extProjectNo = param.extProjectNo
        this.souName = param.souName
        this.supplySelectList = param.supplySelectList
        this.isReadOnly = param.isReadOnly
        this.curSupplier = param.supplySelectList.length ? this.supplySelectList[0].vendorId : ''
        this.$nextTick(() => {
            this.getDetailInfo()
        })
    },
    methods: {
        // 数据初始化
        getDetailInfo() {
            const params = {
                projectId: this.projectId,
                techScoreHeadId: this.techScoreHeadId,
                groupId: this.groupId,
                extendReview: 'Y'
            }
            this.$http({
                url: '/api-sou/ext/buyer/bid/init/getExtScoreDetail',
                method: 'POST',
                data: params,
                loading: true
            }).then(res => {
                if (res && res.data) {
                    const { techFileList = [], scoreRuleList = [], fileList = [], replayFileList = [] } = res.data
                    replayFileList.forEach(item => {
                        item.isActive = false
                    })
                    techFileList.forEach(item => {
                        item.isActive = false
                    })
                    scoreRuleList.forEach((item, index) => {
                        if (index === 0) {
                            item.isOpen = true
                        } else {
                            item.isOpen = false
                        }
                    })
                    this.fileList = fileList
                    this.isSaveFun(scoreRuleList)
                    this.scoreList = scoreRuleList
                    this.replayFileList = replayFileList
                    this.techFileList = techFileList
                    // 展示第一个澄清文件预览
                    this.previewLoading = false
                    this.handleSupplyChange()
                    // this.clarifyFileId = this.replayFileList.length ? this.replayFileList[0].fileId : this.techFileList[0].orderDocId // 澄清文件没有 就展示第一个招标文件
                }
            })
        },
        // 切换供应商
        handleSupplyChange () {
            const curReplyFile = this.replayFileList.find(item => item.vendorId === this.curSupplier)
            const curtechFile = this.techFileList.find(item => item.vendorId === this.curSupplier)
            if (curReplyFile) {
                this.replayFileList.forEach(item => {
                    item.isActive = false
                    if (item.fileId === curReplyFile.fileId) {
                        item.isActive = true
                    }
                })
            }
            if (!curReplyFile && curtechFile) {
                this.techFileList.forEach(item => {
                    item.isActive = false
                    if (item.orderDocId === curtechFile.orderDocId) {
                        item.isActive = true
                    }
                })
            }
            this.previewType = 'clarify'
            this.clarifyFileId = curReplyFile ? curReplyFile.fileId : curtechFile.orderDocId
        },
        // 评审项展开收起
        handleOpen(name) {
            this.scoreList.map(item => {
                if (item.reviewItem === name) {
                    item.isOpen = !item.isOpen
                }
                return item
            })
        },
        // 评审项快速索引
        handleReviewChange(val) {
            if (val) {
                this.scoreList.map(item => {
                    item.isOpen = false
                    if (item.scoreRuleId === val) {
                        item.isOpen = true
                    }
                    return item
                })
            }
        },
        // 保存评审说明、评分
        handleDescOrScore(val, maxScore) {
            setTimeout(() => {
                const params = {
                    tempSave: true,
                    projectId: this.projectId,
                    techScoreHeadId: this.techScoreHeadId,
                    fileList: this.fileList,
                    scoreRuleDtoList: this.scoreList
                }
                this.$http({
                    url: '/api-sou/ext/buyer/bid/init/editScore',
                    method: 'POST',
                    data: params,
                    loading: true
                }).then(res => {
                    if (maxScore && Number(maxScore) > 0) {
                        if (val.extDescription && val.score && Number(val.score) >= 0) {
                            val.isSave = true
                        } else {
                            val.isSave = false
                        }
                    } else {
                        if (val.extDescription) {
                            val.isSave = true
                        } else {
                            val.isSave = false
                        }
                    }
                })
            }, 100)
        },
        // 初始化判断评审说明是否已填写及是否展示原文依据
        isSaveFun(data) {
            data.forEach(dataItem => {
                dataItem.vendorScoreList.forEach(item => {
                    if (dataItem.maxScore && Number(dataItem.maxScore) > 0) {
                        if (item.extDescription && item.score && Number(item.score) >= 0) {
                            item.isSave = true
                        } else {
                            item.isSave = false
                        }
                    } else {
                        if (item.extDescription) {
                            item.isSave = true
                        } else {
                            item.isSave = false
                        }
                    }
                    item.isAnswer = false
                    item.answerAndQuotationList.forEach(answer => {
                        if (answer.answer != 'no_answer') {
                            item.isAnswer = true
                        }
                    })
                })
            })
        },
        // 打开助手对话
        goDialogue() {
            const params = {
                projectId: this.projectId,
                companyId: this.curSupplier
            }
            if (Vue.prototype.embedHelper) {
                setTimeout(() => {
                    Vue.prototype.embedHelper.call('navTabIndex', 2)
                    Vue.prototype.embedHelper.call('biddingData', params)
                }, 300)
            }
            this.$handleFrameClick('dialogue', params)
        },
        // 文件树展开收起
        handleExpand() {
            this.isExpand = !this.isExpand
        },
        // 文件树文件点击
        handleFileClick(fileId, type) {
            this[type].forEach(item => {
                item.isActive = item.fileId === fileId || item.orderDocId === fileId
            })
            // 展示文件
            this.handlePreviewLoading()
            this.previewType = 'clarify'
            this.clarifyFileId = fileId
        },
        // 点击文件 标注 
        chooseAnnotation(data, fileId) {
            this.handlePreviewLoading()
            this.previewType = 'quote'
            const _list = JSON.parse(data)
            this.fileData = {
                ..._list,
                fileId,
                projectId: this.projectId
            }
            console.log('fileData', this.fileData)
        },
        // 文档预览模拟加载
        handlePreviewLoading() {
            this.previewLoading = true
            let timer = setTimeout(() => {
                clearTimeout(timer)
                this.previewLoading = false
            }, 1000)
        }
    }
}
</script>
<style lang="scss" scoped>
.intBidEval-box {
    width: 100%;
    height: 100%;

    .header-box {
        height: 42px;
        margin-bottom: 12px;
        display: flex;
        justify-content: space-between;

        .title-box {
            display: flex;
            align-items: center;

            :deep(.el-select) {
                height: 40px;
                width: 280px;
            }

            :deep(.el-input) {
                height: 40px;
            }

            :deep(.el-input__inner) {
                height: 40px;
                border-radius: 8px;
                font-size: 14px;
            }
        }

        .dialogue {
            width: 136px;
            height: 42px;
            border: 1px solid #D6D6D6;
            cursor: pointer;
            border-radius: 8px;
            line-height: 42px;
            text-align: center;

            img {
                display: inline-block;
                width: 40px;
                height: 40px;
                vertical-align: top;
            }
        }
    }

    .content {
        height: calc(100% - 54px);
        display: flex;

        .file-box-con,
        .eval-box,
        .preview-box {
            height: 100%;
            border-radius: 16px;
            background-color: #fff;
            box-shadow: 0px 0px 20px 0px #7d668821;
        }

        .file-box-con,
        .preview-box {
            overflow-y: auto;
        }

        .file-box {
            padding-left: 10px;
            position: relative;

            .arrow {
                position: absolute;
                left: 3px;
                width: 4px;
                height: 16px;
                border: 2px solid #DCDCDC;
                top: 50%;
                border-radius: 5px;
                margin-top: -8px;
                cursor: pointer;

                &:hover {
                    border: 0;
                }
            }

            .isExpand {
                background: url(../../../../../assets/images/arrow_r.png) no-repeat center;
                background-size: 115%;
            }

            .isCollapse {
                background: url(../../../../../assets/images/arrow_l.png) no-repeat center;
                background-size: 115%;
            }
        }

        .file-box-con {
            width: 260px;
            padding: 20px 20px 0;

            .file-title {
                font-size: 14px;
                color: #666666;
                padding: 5px 0;
            }

            .file-item {
                .item {
                    margin: 20px 0;
                    border-radius: 10px;
                    border: 1px solid #F2F2F2;
                    min-height: 100px;
                    padding: 16px;
                    cursor: pointer;

                    &:hover {
                        border-color: #188FFF;
                        box-shadow: 0px 0px 20px 0px #0000001A;
                    }

                    img {
                        display: block;
                        width: 30px;
                        height: 30px;
                        margin: 0 auto 10px;
                    }

                    .file-name {
                        text-align: center;
                        line-height: 20px;
                    }
                }

                .active {
                    border-color: #188FFF;
                    box-shadow: 0px 0px 20px 0px #0000001A;
                }
            }
        }

        .eval-box {
            width: calc(50% - 130px);
            padding: 20px 0;

            .title-box {
                height: 44px;
                display: flex;
                justify-content: space-between;
                align-items: center;
                padding: 0 20px;

                :deep(.el-input) {
                    height: 44px;
                }

                :deep(.el-input__inner) {
                    height: 44px;
                    border-radius: 10px;
                    font-size: 14px;
                }
            }

            .eval-item {
                padding: 0 20px;
                overflow-y: auto;
                height: calc(100% - 44px);

                .item-box {
                    margin-top: 20px;
                    border-radius: 16px;
                    border: 1px solid #EFF0F4;
                    overflow: hidden;

                    .item-title {
                        height: 44px;
                        background-color: #EFF0F4;
                        color: #333;
                        padding: 0 20px;
                        display: flex;
                        align-items: center;

                        span {
                            font-size: 16px;
                            margin-left: 8px;
                        }
                    }

                    .item-con {
                        padding: 20px;

                        .item-con-inner {
                            position: relative;
                        }

                        .tip {
                            position: absolute;
                            height: 28px;
                            background-color: #fff;
                            border-radius: 20px;
                            line-height: 28px;
                            border: 1px solid #F9F9F9;
                            color: #696969;
                            padding: 0 12px;
                            right: 0;
                            top: -56px;
                        }

                        .according {
                            .title {
                                font-size: 14px;
                                color: #1D2129;
                                margin-bottom: 8px;
                            }

                            .text-box {
                                color: #414C5E;
                                font-size: 14px;
                                line-height: 18px;

                                .text {
                                    margin-bottom: 5px;
                                }

                                .file {
                                    display: flex;
                                    justify-content: end;
                                    flex-wrap: wrap;

                                    .page {
                                        display: flex;
                                        margin-left: 6px;
                                        flex-wrap: wrap;

                                        img {
                                            display: block;
                                            width: 18px;
                                            height: 18px;
                                            margin-right: 2px;
                                        }

                                        div {
                                            // width: 18px;
                                            // height: 18px;
                                            // line-height: 18px;
                                            text-align: center;
                                            color: #0077FF;
                                            margin: 0 3px;
                                            display: flex;
                                        }
                                    }

                                    .annotation-box {

                                        i,
                                        span {
                                            cursor: pointer;

                                            &:hover {
                                                opacity: .8;
                                            }
                                        }

                                        i {
                                            font-size: 12px;
                                            font-style: normal;
                                            margin: 0 2px;
                                        }

                                        span {
                                            margin-right: 5px;
                                            text-decoration: underline;
                                        }
                                    }
                                }
                            }
                        }

                        .describe {
                            .title {
                                font-size: 14px;
                                color: #1D2129;
                                padding-left: 10px;
                                position: relative;

                                &:before {
                                    display: block;
                                    content: '*';
                                    color: red;
                                    position: absolute;
                                    left: 0;
                                    top: 3px;
                                }
                            }

                            :deep(.el-textarea__inner) {
                                border-radius: 8px;
                                border: 1px solid #F0F0F5;
                                background: #FBFBFF;
                                font-size: 14px;
                            }

                            :deep(.el-input__inner) {
                                border-radius: 8px;
                                border: 1px solid #F0F0F5;
                                height: 32px;
                            }

                            :deep(.el-input__count) {
                                display: none;
                            }
                        }
                    }
                }
            }
        }

        .preview-box {
            width: calc(50% - 130px);
            margin-left: 10px;
        }
    }
}
</style>