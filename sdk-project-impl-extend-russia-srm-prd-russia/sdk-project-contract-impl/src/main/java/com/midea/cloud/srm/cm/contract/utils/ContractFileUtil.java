package com.midea.cloud.srm.cm.contract.utils;





import com.itextpdf.text.*;

import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.ElementList;


import com.midea.cloud.common.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Entities;
import org.springframework.mock.web.MockMultipartFile;

import java.io.*;

/**
 * @author 100014336  ganyh19
 */
@Slf4j
public class ContractFileUtil {

    public static MockMultipartFile getPdfUploadFile(byte[] data,String fileName){
        return new MockMultipartFile(fileName, fileName, "application/pdf", data);
    }

    public static MockMultipartFile getPdfFile (String htmlText,String fileName) {
        byte [] bytes = htmlToPdf(htmlText);
        return new MockMultipartFile(fileName, fileName, "application/pdf", bytes);
    }


    public static byte[]  htmlToPdf(String htmlText){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] data = null;

        try {
            // 创建文档
            Document document = new Document(PageSize.A4,60,60,15,40);
            // 创建输入流
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            document.open();
            // 制作大文本数据
            org.jsoup.nodes.Document doc = Jsoup.parse(htmlText);

            // jsoup标准化标签，生成闭合标签
            doc.outputSettings().syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml);
            doc.outputSettings().escapeMode(Entities.EscapeMode.xhtml);
            Paragraph context = new Paragraph();
            // html 的处理
            ElementList elementList = IeTextXmlHelper.MyFontsProvider.parseToElementList(doc.html(), null);

            // 写入到 段落 Paragraph
            for (Element element : elementList) {
                context.add(element);
            }
            context.setSpacingBefore(10f);
            document.add(context);
            document.close();
            writer.close();
            data = outputStream.toByteArray();
        }catch (IOException e) {
            throw new RuntimeException(e);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } finally {

            try {
                outputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return data;
    }

    public static String  createPdfSaveFile(){
//        String path = "d:\\upload\\textffff.pdf";
//        try {
//            // 判断路径中文件夹是否存在,不存在则自动创建,防止因为文件夹不存在而报错
//            creatNewFile(path);
//            // 创建文档
//            Document document = new Document(PageSize.A4,60,60,15,40);
//            // 创建输入流
//            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path));
//            document.open();
//
//            // 制作大文本数据
//            StringBuilder stringBuilder = new StringBuilder();
//            for (int i = 0; i <= 100; i++){
//                stringBuilder.append("如果说荷兰是橙色的，那阿姆斯特丹就是缤纷的彩色。");
//            }
//            // 添加数据
//            String pocketDescription = "" +
//                    "<<div id=\"contract-page\" style=\"position: relative;\"><p><strong>采</strong><strong>&nbsp;购 年 度 合 同${[合同名称]$contractName:key_1675241511302_1}</strong></p><p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p><p>合同编号：${[合同编号]$contractCode:key_1675241522175_2}&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp;</p><p>签订地点：${[签约地址]$signingAddress:key_1675241552188_3} &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp;</p><p>签订时间：2022年10月21日</p><p>甲方（需方）： ${[甲方]$party:key_1675241559342_4}</span></span></p><p>地&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;址：${[甲方地址]$partyAddress:key_1675241566365_5}</p><p>乙方（供方）：${[乙方]$secondParty:key_1675241572256_6}</span></span></p><p>地&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;址：${[乙方地址]$secondPartyAddress:key_1675241580828_7}</p><p>根据《中华人民共和国民法典》有关规定，甲、乙双方本着平等互利，友好合作的原则，经双方充分协商，达成如下购货合同，共同遵守：</p><p><strong>第一条</strong><strong>&nbsp;&nbsp;&nbsp;供货对象</strong></p><p>1.1 &nbsp;甲方在年度合作期间，向乙方采购 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;。</p><p>序号 采购物品 规格/型号 单位 含税单价（万元） 厂家</p><p>1 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;袋装/50kg 吨 随行就市 山东正奇饲料</p><p>物料金额大写：${[物料金额合计(大写)]$totalMaterialAmount:key_1675241603737_8}</p><p>1.2 &nbsp;在合同执行期内，甲方以订单的形式下达具体的产品购货合同，‘订单’作为本合同的一部分，所签订的内容与本合同具有同等法律效力。</p><p>1.3 &nbsp;1.1表格中无法确定内容在订单中确定，订单所载内容与上述表格冲突的以订单为准。订单内规定的产品规格、数量、单价、供货时间等内容不能随意更改，如有特殊情况需经双方书面同意，否则视为违约行为。</p><p><strong>第二条</strong><strong>&nbsp;&nbsp;&nbsp;合同期限</strong></p><p style=\"text-align: justify;\">2.1 &nbsp;本合同执行期限为1年：自2022年10月21日起至2023年10月20日止。</p><p>2.2 &nbsp;甲方根据乙方供货质量、交期等综合判定因素，决定合同期满后是否与乙方续签合同。</p><p>第三条&nbsp;&nbsp;&nbsp;质量标准;收货标准（水分≤12.5%，粗蛋白≥26.0/24.0%，粗灰分≤6.0%，粗纤维≤8.0%，粗脂肪≥8.0/10.0%，黄曲霉毒素B1 ug/kg≤50，赤霉烯酮ug/kg≤1500，呕吐毒素mg/kg≤3，拒收标准（水分≥14.0%，粗蛋白≤23.0%，粗灰分≥8.0%，粗纤维≥9.0%，粗脂肪≤7.0%，黄曲霉毒素B1 ug/kg&gt;50，赤霉烯酮ug/kg&gt;1500，呕吐毒素mg/kg&gt;3，）感官要求（颜色：金黄色至黄褐色，味道：略带酒香味，无霉味。无发热、无霉变、无虫蛀，无其它异臭。杂质：本品不得掺有本品以外的其它物品。）扣款设定(水分超一扣一，粗灰分超一扣一，粗蛋白加粗脂肪不足34扣款公式为单价除以34*（34-实测值）（单位元/吨）。</p><p><strong>第四条</strong><strong>&nbsp;&nbsp;&nbsp;价格及付款方式</strong></p><p>4.1 &nbsp;产品价格：根据双方协商同意的价格执行，具体以订单为准。运费在订单中单独注明。</p><p>4.2 &nbsp;付款时间：</p><p>4.3 &nbsp;乙方为甲方开具符合国家规定税率的增值税发票。</p><p>4.4 &nbsp;乙方收款账户为：</p><p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p><p><strong>第五条</strong><strong>&nbsp;&nbsp;&nbsp;&nbsp;交货、运输方式、货物的交付</strong></p><p>5.1 &nbsp;交货期限：按订单内容执行</p><p>5.2 &nbsp;交货方式：甲乙双方选择以下 &nbsp;1 &nbsp;&nbsp;&nbsp;种作为交货方式。</p><p>（1） 由乙方负责运送至甲方指定交货地点，运输过程中产生的一切费用和风险由乙方自行承担。（2） 甲方自提货物，乙方按照订单内容加工完毕后应及时通知甲方提货，并附协助甲方验收货品，装载货品提供便利的义务。5.3 &nbsp;交货地点：甲方厂内5.4 &nbsp;运输方式：汽运方式。5.5 &nbsp;包装标准及计算方式 ：乙方为甲方提供的产品包装必须正规完好，按照国家或厂家出厂专业标准进行包装（包装物不回收），包装应适应于防潮、防震及远距离运输等，确保货物安全无损运抵甲方指定地点。由于包装不善所引起的货物破损按不合格计算，甲方按照实际收到的数量作为结算数量，其余数量乙方补足，产生的额外费用由乙方自行承担。</p><p>5.6 &nbsp;货物的交付：乙方产品，录入甲方入库（供应链）系统后，视为乙方完成货物的交付，货物交付后货物所有权及风险转移到甲方。</p><p><strong>第六条</strong><strong>&nbsp;&nbsp;&nbsp;&nbsp;验货条款</strong></p><p>6.1 甲方收到货物后，立即清点数量，甲方按照实际入库数量作为结算数量。</p><p>6.2 甲方在该批货物的验收交接文件上签字行为，并不能免除乙方于本合同项下的其它义务，并不视作乙方对该批货品的质量责任的免责。</p><p><strong>第七条</strong><strong>&nbsp;&nbsp;&nbsp;乙方订单及结算信息确认方式</strong></p><p>7.1 &nbsp;乙方指定联系人__ &nbsp;&nbsp;&nbsp;&nbsp;__ ， 作为乙方订单确认及收货结算信息的接收人。甲方根据己方需求安排订单后把信息推送到此联系人处，乙方收到信息核对确认；</p><p>7.2 合同执行过程中，如因乙方原因需要变更联系人，乙方需提交变更申请。</p><p><strong>第八条</strong><strong>&nbsp;&nbsp;&nbsp;&nbsp;违约责任</strong></p><p>8.1 &nbsp;乙方延期交货，延期一日，每日向甲方支付合同总金额的 &nbsp;5 &nbsp;%作为违约金，延期超过 10 日，甲方有权解除本合同；8.2 &nbsp;如果乙方交付的货物不符合合同约定的质量、包装等标准或无法正常使用，甲方有权单方解除合同，乙方应自行退换货的费用，并按合同标的 &nbsp;10 &nbsp;&nbsp;%向甲方承担违约责任，违约金不足以赔偿甲方损失的，应予以补足；</p><p>8.3 &nbsp;本条约定的违约金及其他乙方应支付给甲方的款项，甲方有权从应付乙方的货款中直接予以扣除，不足部分乙方应受到甲方通知之日起【30个】工作日内支付；</p><p>8.4 &nbsp;在产品无任何问题的前提下，甲方未及时支付款项，延期一日，每日向乙方支付未付款金额的 10 &nbsp;%作为违约金，延期支付超过10日，乙方有权解除本合同。</p><p><strong>第九条</strong><strong>&nbsp;&nbsp;&nbsp;&nbsp;不可抗力</strong></p><p>甲、乙双方的任何一方由于不可抗力的原因不能执行合同时，应及时向对方通报不能履行或不能完全履行的理由，在取得有关主管机关证明以后，允许延期履行，部分履行或者不履行合同，并根据情况可部分或全部免于承担违约责任。</p><p><strong>第十条</strong><strong>&nbsp;&nbsp;&nbsp;&nbsp;纠纷处理及送达</strong></p><p>10.1 本合同履行过程中发生争议，由双方协商解决：协商不成，提交甲方所在地有管辖权的人民法院诉讼解决。</p><p>10.2 &nbsp;送达地址：合同载明的双方地址应作为送达催款函、对账单、司法文书等函件的送达地址，并适用于各个司法阶段，包括但不限于一审、二审、再审、执行以及督促程序。因载明地址有误或未及时告知变更后的地址，导致相关文书记诉讼文书未能实际被接收的，快递邮寄送达的，同省邮寄发出日起第3日为送达之日，跨省的邮寄发出日起第5日为送达之日。10.3 &nbsp;甲乙双方同意司法机关可以通过手机短信或电子邮件等现代通讯方式送达法律文书，乙方指定接收法律文书的手机号码 &nbsp;_ &nbsp;。</p><p><strong>第十一条</strong><strong>&nbsp;&nbsp;&nbsp;&nbsp;商业贿赂禁止</strong></p><p>本合同签定、履行过程中不得向对方经办人员实行赠送等违反商业道德的行为，如有以上行为出现，应视为违约，守约方有权利将所赠送的钱款（物）予以没收，违约方并承担十万元的违约金，且守约方有权终止合同。</p><p><strong>第十二条</strong><strong></strong><strong>&nbsp;&nbsp;&nbsp;保密协议</strong></p><p>甲乙双方必须对本合同所签订的一切内容（包括所有附件）予以保密，如因一方泄密对另一方造成的一切损失由泄密方全部承担。</p><p><strong>第十三条</strong><strong>&nbsp;&nbsp;&nbsp;其他</strong></p><p>13.1 &nbsp;本合同正文共3页，一式两份，经甲乙双方签字（个人）、盖章后生效，双方各执一份，具有同等法律效力。</p><p>13.2 &nbsp;订单是本合同的组成部分，本合同与订单冲突的以订单为准。本合同的修订、补充需经双方协商后以书面形式作出，加盖公章后生效。</p><p>&nbsp;</p><p>甲&nbsp;&nbsp;方： &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p><p>&nbsp;乙方：经办人： &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;经办人：</p><p>签订日期：2022年10月21日 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;签订日期：2022年10月21日</p></div>";
//            org.jsoup.nodes.Document doc = Jsoup.parse(pocketDescription);
//
//            // jsoup标准化标签，生成闭合标签
//            doc.outputSettings().syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml);
//            doc.outputSettings().escapeMode(Entities.EscapeMode.xhtml);
//            Paragraph context = new Paragraph();
//            // html 的处理
//            ElementList elementList = IETextXmlHelper.MyFontsProvider.parseToElementList(doc.html(), null);
//
//            // 写入到 段落 Paragraph
//            for (Element element : elementList) {
//                context.add(element);
//            }
//            context.setSpacingBefore(10f);
//            document.add(context);
//            document.close();
//            writer.close();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        } catch (DocumentException e) {
//            throw new RuntimeException(e);
//        }
        return "ok";
    }

    /**
     * 检查是否存在文件夹并创建
     *
     * @param path
     * @throws IOException
     */
    public static File creatNewFile(String path) throws IOException {
        File file = new File(path);
        File fileParent = file.getParentFile();
        if (!fileParent.exists()) {
            fileParent.mkdirs();
        }
        file.createNewFile();
        return file;
    }


    public static void main(String[] args) {
        createPdfSaveFile();
    }
}