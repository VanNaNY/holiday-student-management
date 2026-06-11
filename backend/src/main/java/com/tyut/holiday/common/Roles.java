package com.tyut.holiday.common;

import java.util.List;

/** 角色码常量与默认优先级。 */
public final class Roles {

    private Roles() {
    }

    public static final String STUDENT = "STUDENT";
    public static final String COUNSELOR = "COUNSELOR";
    public static final String SECRETARY = "SECRETARY";
    public static final String ADMIN = "ADMIN";

    /**
     * 默认激活角色优先级：学生只有 STUDENT；教职工多角色时默认 COUNSELOR。
     * 取用户拥有角色中、在此列表里排序最靠前者作为默认 activeRole。
     */
    public static final List<String> DEFAULT_PRIORITY =
            List.of(STUDENT, COUNSELOR, SECRETARY, ADMIN);
}
