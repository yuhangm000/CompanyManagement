package com.company.management;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ACL  extends Application{
    private final String ACLFILE = "aclFile";
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
    public void saveAcl(Context context) {
        SharedPreferences sp = context.getSharedPreferences(ACLFILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Set<String> roles = getRoles();
        Set<String> us = user2Role.keySet();
        String user = null;
        for (String u: us) {
            user = u;
            break;
        }
        editor.putStringSet(user, roles);
        for (String role: roles) {
            Set<String> permissions = getPermissions(role);
            editor.putStringSet(role, permissions);
        }
        editor.commit();
    }
    public void clearAcl(Context context) {
        SharedPreferences sp = context.getSharedPreferences(ACLFILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }
    public boolean hasPermission(String username, String permission, Context context) {
        /**
         * username: 用户名
         * permission: 需要检测的权限
         * return: true|false
         */
        if (user2Role == null || user2Role.isEmpty()) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(ACLFILE, Context.MODE_PRIVATE);
            user2Role = new HashMap<>();
            Set<String> roles = sharedPreferences.getStringSet(username, null);
            for (String role : roles) {
                Set<String> permissions = sharedPreferences.getStringSet(role, null);
                user2Role.put(username, role);
                for (String perm: permissions) {
                    Role2Acl role2Acl = new Role2Acl(role, perm);
                    permissionMap.put(role2Acl, this.permissionMap.size());
                }
            }
        }
        String role = user2Role.get(username);
        Role2Acl role2Acl = new Role2Acl(role, permission);
        if (permissionMap.get(role2Acl) != null) {
            return true;
        } else {
            return false;
        }
    }
    public Set<String> getPermissions(String role) {
        Set<String> permissions = new HashSet<>();
        Set<Role2Acl> ps = permissionMap.keySet();
        for (Role2Acl r2a: ps) {
            if (role.equals(r2a.getRole())){
                String p = r2a.getAcl();
                permissions.add(p);
            } else if (role == null) {
                String p = r2a.getAcl();
                permissions.add(p);
            }
        }
        return permissions;
    }
    public Set<String> getRoles() {
        Set<String> roles = new HashSet<>();
        Set<String> ur = user2Role.keySet();
        for (String u: ur) {
            String p = user2Role.get(u);
            roles.add(p);
        }
        return roles;
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
