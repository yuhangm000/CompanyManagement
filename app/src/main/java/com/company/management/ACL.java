package com.company.management;

import android.app.Application;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ACL  extends Application{
    class Role2Acl {
        private String role;
        private String acl;

        public Role2Acl(String role, String acl) {
            this.role = role;
            this.acl = acl;
        }
        public String getRole() {
            return role;
        }
        public String getAcl() {
            return acl;
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Role2Acl role2Acl = (Role2Acl) obj;
            if (role != null && acl != null) {
                if (role.equals(role2Acl.getRole()) && acl.equals((role2Acl.getAcl()))) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hashCode = 0;
            if (role != null) {
                hashCode += role.hashCode();
            }
            if (acl != null) {
                hashCode += acl.hashCode();
            }
            return hashCode;
        }
    }
    private Map<String, String> user2Role;
    private Map<Role2Acl, Integer> permissionMap;
    public ACL() {
        user2Role = new HashMap<>();
        permissionMap = new HashMap<>();
    }
    public void setUser2Role(String user, String role) {
        /**
         * user2Role: 用用户名做键，用角色做值的HashMap
         */
        this.user2Role.put(user, role);
    }
    public void setRole2Acl(String role, String acl) {
        /**
         * role2Acl: 用角色做键，用权限做值的HashMap
         */
        Role2Acl role2Acl = new Role2Acl(role, acl);
        int size = this.permissionMap.size();
        this.permissionMap.put(role2Acl,size);
    }
    public void addAcl(String role, String permission) {
        this.setRole2Acl(role, permission);
    }
    public boolean hasPermission(String username, String permission) {
        /**
         * username: 用户名
         * permission: 需要检测的权限
         * return: true|false
         */
        String role = user2Role.get(username);
        Role2Acl role2Acl = new Role2Acl(role, permission);
        if (permissionMap.get(role2Acl) != null) {
            return true;
        } else {
            return false;
        }
    }
    public Set<String> getPermissions() {
        Set<String> permissions = new HashSet<>();
        Set<Role2Acl> ps = permissionMap.keySet();
        for (Role2Acl r2a: ps) {
            String p = r2a.getAcl();
            permissions.add(p);
        }
        return permissions;
    }
    @Override
    public void onCreate() {
        super.onCreate();
    }
    public void clear() {
        user2Role.clear();
        permissionMap.clear();
    }
}
