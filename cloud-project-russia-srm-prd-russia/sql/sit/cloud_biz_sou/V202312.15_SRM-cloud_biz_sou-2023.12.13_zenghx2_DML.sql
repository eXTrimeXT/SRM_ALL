ALTER TABLE `scc_sou_jc_agreement_org`
    ADD INDEX `scc_sou_jc_agreement_org_BUY_ORG_ID` (`BUY_ORG_ID`);
ALTER TABLE `scc_sou_jc_agreement_org`
    ADD INDEX `scc_sou_jc_agreement_org_AGREEMENT_ID` (`AGREEMENT_ID`);
ALTER TABLE `scc_sou_jc_agreement_info`
    ADD INDEX `scc_sou_jc_agreement_info_AGREEMENT_ID` (`AGREEMENT_ID`);
ALTER TABLE `scc_sou_jc_agreement_info`
    ADD INDEX `scc_sou_jc_agreement_info_MATERIAL_ID` (`MATERIAL_ID`);