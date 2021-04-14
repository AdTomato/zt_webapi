package com.authine.cloudpivot.web.api.utils;

import com.authine.cloudpivot.web.api.bean.EvaluationTable;
import com.authine.cloudpivot.web.api.bean.LeaderEvaluationTable;
import com.authine.cloudpivot.web.api.bean.deptSeasonAssess.DeptSeasonAssChild;
import com.authine.cloudpivot.web.api.bean.deputyassess.LaunchDeputyAssChild;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 批量创建机关部门考核评价表
 */
public class CreateEvaluationTableUtils {


    public static List<EvaluationTable> getEvaluationTable(String parentId) {
        List<EvaluationTable> evaluationTables = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            EvaluationTable evaluationTable = new EvaluationTable();
            // id
            evaluationTable.setId(UUID.randomUUID().toString().replaceAll("-", ""));

            // parentId
            evaluationTable.setParentId(parentId);

            // 评分标准
            evaluationTable.setScaleOfMark("优秀：9-10；良好：7-8；一般：5-6； 较差：1-4");

            // 设置测评项目和测评内容
            switch (i) {
                case 0: {
                    evaluationTable.setAssessmentProject("理论学习及执行力");
                    evaluationTable.setAssessmentContent("着力加强学习型团队建设,注重学习与本部门相关的党和国家方针、政策、制度规定及业务知识,干部员工积极参加局内外培训和授课;部门坚决执行局重大战略决策部署,认真开展各项工作,干部员工较好地履行岗位职责。");
                    evaluationTable.setSortKey(new BigDecimal("10"));
                }
                break;
                case 1: {
                    evaluationTable.setAssessmentProject("敬业精神");
                    evaluationTable.setAssessmentContent("干部员工忠诚企业,坚持高标准、严要求,敢于争创一流,具有强烈的事业心和责任感,自觉维护企业利益");
                    evaluationTable.setSortKey(new BigDecimal("20"));

                }
                break;
                case 2: {
                    evaluationTable.setAssessmentProject("部门合作");
                    evaluationTable.setAssessmentContent("干部员工主动加强与基层单位、相关部门的沟通联系,工作不推诿、不扯皮;积极主动配合其他部门工作,高效保质完成所分配的工作任务,及时进行结果反馈和后续跟进。");
                    evaluationTable.setSortKey(new BigDecimal("30"));

                }
                break;
                case 3: {
                    evaluationTable.setAssessmentProject("管理思路");
                    evaluationTable.setAssessmentContent("部门整体工作思路清晰,具有系统性和前瞻性,加强业务系统的管理体制机制建设,重视系统人才培养,不断提升系统业务管理水平。");
                    evaluationTable.setSortKey(new BigDecimal("40"));

                }
                break;
                case 4: {
                    evaluationTable.setAssessmentProject("专业水平");
                    evaluationTable.setAssessmentContent("部门干部员工业务能力较强,能够解决本业务系统重难点问题;坚持深入基层开展调研检查,具有发现问题、提出问题和解决问题的能力。");
                    evaluationTable.setSortKey(new BigDecimal("50"));

                }
                break;
                case 5: {
                    evaluationTable.setAssessmentProject("创新能力");
                    evaluationTable.setAssessmentContent("整体创新意识较强,联系企业和基层实际创新开展工作;主动提出建议,为领导当好参谋;注重检验创新成果,具有自我修正及再创新的意识和能力。");
                    evaluationTable.setSortKey(new BigDecimal("60"));

                }
                break;
                case 6: {
                    evaluationTable.setAssessmentProject("工作实效");
                    evaluationTable.setAssessmentContent("部门年度工作目标圆满完成，改革创新工作成效显著，推动基层单位业务管理工作规范化、标准化、精细化水平显著提升。 ");
                    evaluationTable.setSortKey(new BigDecimal("70"));

                }
                break;
                case 7: {
                    evaluationTable.setAssessmentProject("服务意识");
                    evaluationTable.setAssessmentContent("能够始终坚持基层导向，注重联系基层客观实际，注重开展调查研究；部门干部员工能够积极反馈基层单位诉求，对待基层单位及员工热情周到，解决问题不拖延、不推诿。");
                    evaluationTable.setSortKey(new BigDecimal("80"));

                }
                break;
                case 8: {
                    evaluationTable.setAssessmentProject("组织纪律");
                    evaluationTable.setAssessmentContent("部门严格遵守国家法律法规、党内法规以及股份公司、局各项管理制度,模范遵守工作纪律;干部员工工作积极主动,敢于担当,乐于奉献。");
                    evaluationTable.setSortKey(new BigDecimal("90"));

                }
                break;
                case 9: {
                    evaluationTable.setAssessmentProject("廉洁从业");
                    evaluationTable.setAssessmentContent("部门干部员工能够严格执行中央“八项规定”，坚持作风正派，自觉抵制不良嗜好；严格要求自己，坚持廉洁自律，不对基层单位搞摊派，不以工作关系为个人谋私利。");
                    evaluationTable.setSortKey(new BigDecimal("100"));

                }
                break;
                default:
                    throw new IllegalStateException("Unexpected value: " + i);
            }

            evaluationTables.add(evaluationTable);
        }
        return evaluationTables;
    }

    public static List<LeaderEvaluationTable> getLeaderEvaluationTable(String parentId) {
        List<LeaderEvaluationTable> leaderevaluationTables = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            LeaderEvaluationTable evaluationTable = new LeaderEvaluationTable();
            // id
            evaluationTable.setId(UUID.randomUUID().toString().replaceAll("-", ""));

            // parentId
            evaluationTable.setParentId(parentId);

            // 设置测评项目和测评内容
            switch (i) {
                case 0: {
                    evaluationTable.setAssess_content("素质");
                    evaluationTable.setAssess_index("政治素质");
                    evaluationTable.setAssess_criteria("增强\"四个意识\"，坚定\"四个自信\"，做到\"两个维护\"，深入贯彻落实习近平新时代中国特色社会主义思想，牢固树立正确的事业观、权力观和业绩观；遵守党的政治纪律，自觉加强党性锻炼，提高党性修养。");
                    evaluationTable.setSortKey(new BigDecimal("10"));
                }
                break;
                case 1: {
                    evaluationTable.setAssess_content("素质");
                    evaluationTable.setAssess_index("职业操守");
                    evaluationTable.setAssess_criteria("忠诚企业，具有高度职业认同感；勤勉敬业，始终充满干事创业的激情和动力；遵守商业道德，诚信践诺，坚守职业精神。");
                    evaluationTable.setSortKey(new BigDecimal("20"));
                }
                break;
                case 2: {
                    evaluationTable.setSortKey(new BigDecimal("30"));
                    evaluationTable.setAssess_content("素质");
                    evaluationTable.setAssess_index("作风建设");
                    evaluationTable.setAssess_criteria("贯彻落实中央八项规定精神和局十条措施，坚决反对形式主义、官僚主义，享乐主义和奢靡之风；密切联系职工群众，切实解决实际问题，在职工群众中威信高。");
                }
                break;
                case 3: {
                    evaluationTable.setSortKey(new BigDecimal("40"));
                    evaluationTable.setAssess_content("素质");
                    evaluationTable.setAssess_index("廉洁从业");
                    evaluationTable.setAssess_criteria("正确履职行权，严格执行\"三重一大\"集体决策制度，自觉遵守廉洁从业各项规定；严于律己，落实\"一岗双责\"，严格约束亲属和身边工作人员，自觉接受组织和职工群众监督。");
                }
                break;
                case 4: {
                    evaluationTable.setSortKey(new BigDecimal("50"));
                    evaluationTable.setAssess_content("能力");
                    evaluationTable.setAssess_index("科学决策能力");
                    evaluationTable.setAssess_criteria("坚持按程序办事，坚持科学决策、民主决策、依法决策；注重调查研究，能够针对市场和管理形式变化及时调整思路和对策，工作有前瞻性，管理工作有思路、有办法。");
                }
                break;
                case 5: {
                    evaluationTable.setSortKey(new BigDecimal("60"));
                    evaluationTable.setAssess_content("能力");
                    evaluationTable.setAssess_index("推动执行能力");
                    evaluationTable.setAssess_criteria("认真贯彻落实上级精神，驾驭大局和应对复杂局面的能力强，能够解决企业发展中的疑难问题；大胆管理，勇于承担责任；善于优化配置资源，协调各方力量，有序推进所负责的企业管理工作。");
                }
                break;
                case 6: {
                    evaluationTable.setSortKey(new BigDecimal("70"));
                    evaluationTable.setAssess_content("能力");
                    evaluationTable.setAssess_index("学习创新能力");
                    evaluationTable.setAssess_criteria("注重学习，学以致用，结合企业和基层实际创新开展工作，不断推动企业技术创新和管理创新，努力提升管理水平和管理效率。");
                }
                break;
                case 7: {
                    evaluationTable.setSortKey(new BigDecimal("80"));
                    evaluationTable.setAssess_content("能力");
                    evaluationTable.setAssess_index("团结建设能力");
                    evaluationTable.setAssess_criteria("注重班子团结，注重发现、培养和使用人才，对下属能有效指导，带领的团队有活力、凝聚力和战斗力，有良好的群众基础。");
                }
                break;
                case 8: {
                    evaluationTable.setSortKey(new BigDecimal("90"));
                    evaluationTable.setAssess_content("业绩");
                    evaluationTable.setAssess_index("履职绩效");
                    evaluationTable.setAssess_criteria("认真履行岗位职责，所管理的企业或分管的业务成效明显，保质保量完成每年度的各项发展目标和经济指标。");
                }
                break;
                case 9: {
                    evaluationTable.setSortKey(new BigDecimal("100"));
                    evaluationTable.setAssess_content("业绩");
                    evaluationTable.setAssess_index("协同绩效");
                    evaluationTable.setAssess_criteria("积极协同配合班子其他成员推动工作；坚持从企业改革发展大局出发谋划和推动工作，研究讨论重大问题时积极建言献策，在推动企业整体发展中发挥建设性作用。");
                }
                break;
                default:
                    throw new IllegalStateException("Unexpected value: " + i);
            }
            leaderevaluationTables.add(evaluationTable);
        }
        return leaderevaluationTables;
    }

    public static List<LaunchDeputyAssChild> getDeptDeputyAssessTables(List<LaunchDeputyAssChild> deputy_assesselement, String parentId){
        for (int i = 0; i < deputy_assesselement.size(); i++) {
            deputy_assesselement.get(i).setId(UUID.randomUUID().toString().replaceAll("-", ""));
            deputy_assesselement.get(i).setParentId(parentId);
            deputy_assesselement.get(i).setSortKey(new BigDecimal(i).add(new BigDecimal(10)));
        }
        return deputy_assesselement;
    }

    public static List<DeptSeasonAssChild> getDeptSeasonAssTables(String parentId){
        List<DeptSeasonAssChild> evaluationTables = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            DeptSeasonAssChild evaluationTable = new DeptSeasonAssChild();
        // id
        evaluationTable.setId(UUID.randomUUID().toString().replaceAll("-", ""));

        // parentId
        evaluationTable.setParentId(parentId);

        // 设置测评项目和测评内容
        switch (i) {
            case 0: {
                evaluationTable.setAssessIndex("理论学习及执行力");
                evaluationTable.setAssessContent("着力加强学习型团队建设,注重学习与本部门相关的党和国家方针、政策、制度规定及业务知识,干部员工积极参加局内外培训和授课;部门坚决执行局重大战略决策部署,认真开展各项工作,干部员工较好地履行岗位职责。");
                evaluationTable.setScoreCriterion("优秀：9-10；良好：7-8；一般：5-6；较差：1-4");
                evaluationTable.setSortKey(new BigDecimal("10"));
            }
            break;
            case 1: {
                evaluationTable.setAssessIndex("敬业精神");
                evaluationTable.setAssessContent("干部员工忠诚企业,坚持高标准、严要求,敢于争创一流,具有强烈的事业心和责任感,自觉维护企业利益");
                evaluationTable.setScoreCriterion("优秀：9-10；良好：7-8；一般：5-6；较差：1-4");
                evaluationTable.setSortKey(new BigDecimal("20"));
            }
            break;
            case 2: {
                evaluationTable.setAssessIndex("部门合作");
                evaluationTable.setAssessContent("干部员工主动加强与基层单位、相关部门的沟通联系,工作不推诿、不扯皮;积极主动配合其他部门工作,高效保质完成所分配的工作任务,及时进行结果反馈和后续跟进。");
                evaluationTable.setScoreCriterion("优秀：9-10；良好：7-8；一般：5-6；较差：1-4");
                evaluationTable.setSortKey(new BigDecimal("30"));
            }
            break;
            case 3: {
                evaluationTable.setAssessIndex("管理思路");
                evaluationTable.setAssessContent("部门整体工作思路清晰,具有系统性和前瞻性,加强业务系统的管理体制机制建设,重视系统人才培养,不断提升系统业务管理水平。");
                evaluationTable.setScoreCriterion("优秀：9-10；良好：7-8；一般：5-6；较差：1-4");
                evaluationTable.setSortKey(new BigDecimal("40"));
                }
            break;
            case 4: {
                evaluationTable.setAssessIndex("专业水平");
                evaluationTable.setAssessContent("部门干部员工业务能力较强,能够解决本业务系统重难点问题;坚持深入基层开展调研检查,具有发现问题、提出问题和解决问题的能力。");
                evaluationTable.setScoreCriterion("优秀：9-10；良好：7-8；一般：5-6；较差：1-4");
                evaluationTable.setSortKey(new BigDecimal("50"));
                }
            break;
            case 5: {
                evaluationTable.setAssessIndex("创新能力");
                evaluationTable.setAssessContent("整体创新意识较强,联系企业和基层实际创新开展工作;主动提出建议,为领导当好参谋;注重检验创新成果,具有自我修正及再创新的意识和能力。");
                evaluationTable.setScoreCriterion("优秀：9-10；良好：7-8；一般：5-6；较差：1-4");
                evaluationTable.setSortKey(new BigDecimal("60"));
                }
            break;
            case 6: {
                evaluationTable.setAssessIndex("工作实效");
                evaluationTable.setAssessContent("部门年度工作目标圆满完成,改革创新工作成效显著,推动基层单位业务管理工作规范化、标准化、精细化水平显著提升。");
                evaluationTable.setScoreCriterion("优秀：9-10；良好：7-8；一般：5-6；较差：1-4");
                evaluationTable.setSortKey(new BigDecimal("70"));
                }
            break;
            case 7: {
                evaluationTable.setAssessIndex("服务意识");
                evaluationTable.setAssessContent("能够始终坚持基层导向,注重联系基层客观实际,注重开展调查研究;部门干部员工能够积极反馈基层单位诉求,对待基层单位及员工热情周到,解决问题不拖延、不推诿。");
                evaluationTable.setScoreCriterion("优秀：9-10；良好：7-8；一般：5-6；较差：1-4");
                evaluationTable.setSortKey(new BigDecimal("80"));
                }
            break;
            case 8: {
                evaluationTable.setAssessIndex("组织纪律");
                evaluationTable.setAssessContent("部门严格遵守国家法律法规、党内法规以及股份公司、局各项管理制度,模范遵守工作纪律;干部员工工作积极主动,敢于担当,乐于奉献。");
                evaluationTable.setScoreCriterion("优秀：9-10；良好：7-8；一般：5-6；较差：1-4");
                evaluationTable.setSortKey(new BigDecimal("90"));
                }
            break;
            case 9: {
                evaluationTable.setAssessIndex("廉洁从业");
                evaluationTable.setAssessContent("部门干部员工能够严格执行中央“八项规定”,坚持作风正派,自觉抵制不良嗜好;严格要求自己,坚持廉洁自律,不对基层单位搞摊派,不以工作关系为个人谋私利。");
                evaluationTable.setScoreCriterion("优秀：9-10；良好：7-8；一般：5-6；较差：1-4");
                evaluationTable.setSortKey(new BigDecimal("100"));
                }
            break;
            default:
                throw new IllegalStateException("Unexpected value: " + i);
        }

        evaluationTables.add(evaluationTable);
    }
        return evaluationTables;
    }

}
