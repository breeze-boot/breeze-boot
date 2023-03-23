/*
 * Copyright (c) 2023, gaoweixuan (breeze-cloud@foxmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.breeze.boot.sys.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.breeze.boot.sys.domain.SysRoleMenu;
import com.breeze.boot.sys.mapper.SysRoleMenuMapper;
import com.breeze.boot.sys.params.MenuPermissionParam;
import com.breeze.boot.sys.service.SysRoleMenuService;
import com.breeze.core.utils.Result;
import com.breeze.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统菜单角色服务impl
 *
 * @author gaoweixuan
 * @date 2021-12-06 22:03:39
 */
@Service
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements SysRoleMenuService {

    /**
     * 用户token服务
     */
    @Autowired
    private UserTokenService userTokenService;

    /**
     * 编辑权限
     *
     * @param menuPermissionParam 菜单权限参数
     * @return {@link Result}<{@link Boolean}>
     */
    @Override
    public Result<Boolean> modifyPermission(MenuPermissionParam menuPermissionParam) {
        this.remove(Wrappers.<SysRoleMenu>lambdaQuery().eq(SysRoleMenu::getRoleId, menuPermissionParam.getRoleId()));
        List<SysRoleMenu> sysRoleMenuList = menuPermissionParam.getPermissionIds().stream().map(menuId -> {
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setMenuId(menuId);
            sysRoleMenu.setRoleId(menuPermissionParam.getRoleId());
            return sysRoleMenu;
        }).collect(Collectors.toList());
        boolean batch = this.saveBatch(sysRoleMenuList);
        if (batch) {
            // 刷新菜单权限
            this.userTokenService.refreshUser(SecurityUtils.getUsername());
        }
        return Result.ok(batch);
    }

}
