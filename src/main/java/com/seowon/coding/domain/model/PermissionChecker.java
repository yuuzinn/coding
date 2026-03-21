package com.seowon.coding.domain.model;


import lombok.Builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class PermissionChecker {

    /**
     * TODO #7: 코드를 최적화하세요
     * 테스트 코드`PermissionCheckerTest`를 활용하시면 작업 결과를 검증 할 수 있습니다.
     */
    public static boolean hasPermission(
            String userId,
            String targetResource,
            String targetAction,
            List<User> users,
            List<UserGroup> groups,
            List<Policy> policies
    ) {
        Map<String, User> userMap = users.stream()
                .collect(Collectors.toMap(u -> u.id, u -> u));

        User targetUser = userMap.get(userId);
        if (targetUser == null) {
            return false;
        }

        Map<String, UserGroup> userGroupMap = groups.stream()
                .collect(Collectors.toMap(g -> g.id, g -> g));


        Map<String, Policy> policyMap = policies.stream()
                .collect(Collectors.toMap(p -> p.id, p -> p));


        for (String groupId : targetUser.groupIds) {
            UserGroup userGroup = userGroupMap.get(groupId);
            if (userGroup == null) {
                continue;
            }
            for (String policyId : userGroup.policyIds) {
                Policy policy = policyMap.get(policyId);
                if (policy == null) {
                    continue;
                }

                for (Statement st : policy.statements) {
                    if (st.actions.contains(targetAction) &&
                            st.resources.contains(targetResource)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}

class User {
    String id;
    List<String> groupIds;

    public User(String id, List<String> groupIds) {
        this.id = id;
        this.groupIds = groupIds;
    }
}

class UserGroup {
    String id;
    List<String> policyIds;

    public UserGroup(String id, List<String> policyIds) {
        this.id = id;
        this.policyIds = policyIds;
    }
}

class Policy {
    String id;
    List<Statement> statements;

    public Policy(String id, List<Statement> statements) {
        this.id = id;
        this.statements = statements;
    }
}

class Statement {
    List<String> actions;
    List<String> resources;

    @Builder
    public Statement(List<String> actions, List<String> resources) {
        this.actions = actions;
        this.resources = resources;
    }
}