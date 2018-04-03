package com.itheima.bos.service.system.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ctc.wstx.util.StringUtil;
import com.itheima.bos.dao.system.MenuRepository;
import com.itheima.bos.dao.system.PermissonRepository;
import com.itheima.bos.dao.system.RoleRepository;
import com.itheima.bos.domain.system.Menu;
import com.itheima.bos.domain.system.Permission;
import com.itheima.bos.domain.system.Role;
import com.itheima.bos.service.system.RoleService;

/**
 * ClassName:RoleServiceImpl <br/>
 * Function: <br/>
 * Date: 2018年3月28日 上午10:54:27 <br/>
 */
@Service
@Transactional
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private PermissonRepository permissonRepository;

    @Override
    public Page<Role> findAll(Pageable pageable) {

        return roleRepository.findAll(pageable);
    }

    @Override
    public void save(Role role, String menuIds, Long[] permissionIds) {
        roleRepository.save(role);
        
        if (StringUtils.isNotEmpty(menuIds)) {
            String[] split = menuIds.split(",");
            for (String menuId : split) {
                Menu menu = new Menu();
                menu.setId(Long.parseLong(menuId));
                role.getMenus().add(menu);
            }
        }

        if (permissionIds != null && permissionIds.length > 0) {
            for (Long permissionId : permissionIds) {
                Permission permission = new Permission();
                permission.setId(permissionId);
                role.getPermissions().add(permission);

            }
        }

    }

    private void method1(Role role, String menuIds, Long[] permissionIds) {
        if (StringUtils.isNotEmpty(menuIds)) {
            String[] split = menuIds.split(",");
            for (String menuId : split) {
                Menu menu = menuRepository.findOne(Long.parseLong(menuId));
                role.getMenus().add(menu);
            }
        }

        if (permissionIds != null && permissionIds.length > 0) {
            for (Long permissionId : permissionIds) {
                Permission permission =
                        permissonRepository.findOne(permissionId);
                role.getPermissions().add(permission);

            }
        }
    }

    //根据角色ID获取权限IDS
    @Override
    public List<Permission> findByRoleId(Long id) {
       
        return roleRepository.findByRoleId(id);
    }

    @Override
    public List<Menu> findByRoleId4Ztree(Long id) {
        return roleRepository.findByRoleId4Ztree(id);
    }

    //修改角色
    @Override
    public void edit(Role model, String menuIds, Long[] permissionIds) {
            Long id = model.getId();
            if(id!=null){
                Role role = roleRepository.findOne(id);
               /* role.getMenus().clear();
                role.getPermissions().clear();*/
                role.setMenus(null);
                role.setPermissions(null);
                if (StringUtils.isNotEmpty(menuIds)) {
                    String[] split = menuIds.split(",");
                    Set<Menu> menus=new HashSet<Menu>();
                    for (String menuId : split) {
                        Menu menu = new Menu();
                        menu.setId(Long.parseLong(menuId));
                        menus.add(menu);
                    }
                    role.setMenus(menus);
                }

                if (permissionIds != null && permissionIds.length > 0) {
                    Set<Permission> permissions=new HashSet<Permission>();
                    for (Long permissionId : permissionIds) {
                        Permission permission = new Permission();
                        permission.setId(permissionId);
                        //role.getPermissions().add(permission);
                        permissions.add(permission);
                    }
                    role.setPermissions(permissions);
                }
            }
        
       

    }

}
