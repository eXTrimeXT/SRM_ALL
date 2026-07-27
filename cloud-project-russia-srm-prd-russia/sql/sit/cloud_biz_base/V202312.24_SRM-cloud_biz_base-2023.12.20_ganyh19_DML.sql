update scc_base_quicksearch_config set QUERY_LANGUAGE='SELECT * FROM (
SELECT
t.PROJECT_ID
,t.SOU_NO
,t.SOU_NAME
,t.SOU_TYPE
,t.EXT_PROJECT_NO
,t.PROJECT_STATUS
,t.CREATED_FULL_NAME
,t.CREATION_DATE
,gr.USER_ID
,gr.USERNAME
,gr.FULL_NAME
from scc_sou_project t
LEFT JOIN scc_npm_sou_demand d
ON t.PROJECT_ID = d.PROJECT_ID
LEFT JOIN meicloud_usrm_cloud_biz_supplier_cooperate.scc_pr_requirement_head cd
ON cd.REQUIREMENT_HEAD_NUM = d.APPLICANT_NO
LEFT JOIN meicloud_usrm_cloud_biz_supplier_cooperate.scc_npm_pr_require_group gr
ON gr.REQUIREMENT_HEAD_ID = cd.REQUIREMENT_HEAD_ID AND gr.GROUP_TYPE = ''SOU''
WHERE EXISTS (
SELECT 1 FROM scc_sou_group g
WHERE g.user_id = ${user.userId}
AND t.PROJECT_ID = g.Project_id
AND g.GROUP_ROLE IN (''PRINCIPAL'')
)
and t.CREATED_ID = gr.USER_ID
AND t.project_status NOT IN (''DRAW_UP'')
UNION
SELECT
t.PROJECT_ID
,t.SOU_NO
,t.SOU_NAME
,t.SOU_TYPE
,t.EXT_PROJECT_NO
,t.PROJECT_STATUS
,t.CREATED_FULL_NAME
,t.CREATION_DATE
,gr.USER_ID
,gr.USERNAME
,gr.FULL_NAME
from scc_sou_project t
LEFT JOIN scc_npm_sou_demand d
ON t.PROJECT_ID = d.PROJECT_ID
LEFT JOIN meicloud_usrm_cloud_biz_supplier_cooperate.scc_pr_requirement_head cd
ON cd.REQUIREMENT_HEAD_NUM = d.APPLICANT_NO
LEFT JOIN meicloud_usrm_cloud_biz_supplier_cooperate.scc_npm_pr_require_group gr
ON gr.REQUIREMENT_HEAD_ID = cd.REQUIREMENT_HEAD_ID AND gr.GROUP_TYPE = ''SOU''
WHERE EXISTS (
SELECT 1 FROM scc_sou_group g
WHERE g.user_id = ${user.userId}
AND t.PROJECT_ID = g.Project_id
AND g.GROUP_ROLE IN (''LEADER'')
)
AND t.project_status IN (''TECH_BID_EVA'',''BUS_BID'',''BUS_BID_END'',''BUS_BID_OPEN'',''CONFIRM_BID'',''WIN_LOSS_NOTICE'',''ARCHIVE_TODO'',''ARCHIVE_DONE'')
) t
WHERE 1=1
AND t.sou_name like :QUERY OR t.SOU_NO like :QUERY AND t.EXT_PROJECT_NO like :query',
DIALOG_QUERY_LANGUAGE='SELECT * FROM (
SELECT
t.PROJECT_ID
,t.SOU_NO
,t.SOU_NAME
,t.SOU_TYPE
,t.EXT_PROJECT_NO
,t.PROJECT_STATUS
,t.CREATED_FULL_NAME
,t.CREATION_DATE
,gr.USER_ID
,gr.USERNAME
,gr.FULL_NAME
from scc_sou_project t
LEFT JOIN scc_npm_sou_demand d
ON t.PROJECT_ID = d.PROJECT_ID
LEFT JOIN meicloud_usrm_cloud_biz_supplier_cooperate.scc_pr_requirement_head cd
ON cd.REQUIREMENT_HEAD_NUM = d.APPLICANT_NO
LEFT JOIN meicloud_usrm_cloud_biz_supplier_cooperate.scc_npm_pr_require_group gr
ON gr.REQUIREMENT_HEAD_ID = cd.REQUIREMENT_HEAD_ID AND gr.GROUP_TYPE = ''SOU''
WHERE EXISTS (
SELECT 1 FROM scc_sou_group g
WHERE g.user_id = ${user.userId}
AND t.PROJECT_ID = g.Project_id
AND g.GROUP_ROLE IN (''PRINCIPAL'')
)
and t.CREATED_ID = gr.USER_ID
AND t.project_status NOT IN (''DRAW_UP'')
UNION
SELECT
t.PROJECT_ID
,t.SOU_NO
,t.SOU_NAME
,t.SOU_TYPE
,t.EXT_PROJECT_NO
,t.PROJECT_STATUS
,t.CREATED_FULL_NAME
,t.CREATION_DATE
,gr.USER_ID
,gr.USERNAME
,gr.FULL_NAME
from scc_sou_project t
LEFT JOIN scc_npm_sou_demand d
ON t.PROJECT_ID = d.PROJECT_ID
LEFT JOIN meicloud_usrm_cloud_biz_supplier_cooperate.scc_pr_requirement_head cd
ON cd.REQUIREMENT_HEAD_NUM = d.APPLICANT_NO
LEFT JOIN meicloud_usrm_cloud_biz_supplier_cooperate.scc_npm_pr_require_group gr
ON gr.REQUIREMENT_HEAD_ID = cd.REQUIREMENT_HEAD_ID AND gr.GROUP_TYPE = ''SOU''
WHERE EXISTS (
SELECT 1 FROM scc_sou_group g
WHERE g.user_id = ${user.userId}
AND t.PROJECT_ID = g.Project_id
AND g.GROUP_ROLE IN (''LEADER'')
)
AND t.project_status IN (''TECH_BID_EVA'',''BUS_BID'',''BUS_BID_END'',''BUS_BID_OPEN'',''CONFIRM_BID'',''WIN_LOSS_NOTICE'',''ARCHIVE_TODO'',''ARCHIVE_DONE'')
) t
WHERE 1=1' where NAME = 'sou_answer_projet';