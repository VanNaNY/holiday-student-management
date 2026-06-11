package com.tyut.holiday.common;

/**
 * 业务状态常量集中定义。
 */
public final class Constants {

    private Constants() {
    }

    /** 离校/返校登记状态 */
    public static final class RegStatus {
        public static final String NOT_REG = "NOT_REG";       // 未登记
        public static final String REGISTERED = "REGISTERED"; // 已登记
        public static final String ON_THE_WAY = "ON_THE_WAY"; // 离校/返校途中
        public static final String ARRIVED = "ARRIVED";       // 已到目的地/已返校
    }

    /** 留校审批状态 */
    public static final class ApprovalStatus {
        public static final String PENDING = "PENDING";     // 待审批
        public static final String APPROVED = "APPROVED";   // 通过
        public static final String REJECTED = "REJECTED";   // 驳回
        public static final String WITHDRAWN = "WITHDRAWN"; // 已撤回
    }

    /** 审批节点 */
    public static final class ApprovalNode {
        public static final String COUNSELOR = "COUNSELOR"; // 辅导员
        public static final String SECRETARY = "SECRETARY"; // 副书记
        public static final String DONE = "DONE";           // 已终结
    }

    /** 审批动作 */
    public static final class ApprovalAction {
        public static final String SUBMIT = "SUBMIT";     // 发起
        public static final String APPROVE = "APPROVE";   // 通过
        public static final String REJECT = "REJECT";     // 驳回
        public static final String WITHDRAW = "WITHDRAW"; // 撤回
    }

    /** 行程归属类型 */
    public static final class TripType {
        public static final String LEAVE = "LEAVE";   // 离校
        public static final String RETURN = "RETURN"; // 返校
    }

    /** 签到类型 */
    public static final class CheckinType {
        public static final String STAY = "STAY";     // 留校签到
        public static final String RETURN = "RETURN"; // 返校报到
    }

    /** 签到状态 */
    public static final class CheckinStatus {
        public static final String DONE = "DONE";       // 已签到
        public static final String NOT_YET = "NOT_YET"; // 未签到
    }

    /** 行程最大段数 */
    public static final int MAX_TRIPS = 10;

    /** 留校附件最大张数 */
    public static final int MAX_STAY_ATTACHMENTS = 5;
}
