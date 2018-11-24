package com.company.management;

import android.app.Application;

import java.util.HashMap;
import java.util.Map;

public class ACL  extends Application{
    private Map<String, String> user2Role;
    private Map<String, String> role2Acl;
    public ACL() {
        user2Role = new HashMap<>();
        role2Acl = new HashMap<>();
    }
    public void setUser2Role(Map<String, String> user2Role) {
        /**
         * user2Role: 用用户名做键，用角色做值的HashMap
         */
        this.user2Role = user2Role;
    }
    public void setRole2Acl(Map<String, String> role2Acl) {
        /**
         * role2Acl: 用角色做键，用权限做值的HashMap
         */
        this.role2Acl = role2Acl;
    }
    public void addAcl(String role, String permission) {
        this.role2Acl.put(role, permission);
    }
    public boolean hasPermission(String username, String permission) {
        /**
         * username: 用户名
         * permission: 需要检测的权限
         * return: true|false
         */
        String role = user2Role.get(username);
        if (role2Acl.get(role).equals(permission)) {
            return true;
        } else {
            return false;
        }
    }
}
