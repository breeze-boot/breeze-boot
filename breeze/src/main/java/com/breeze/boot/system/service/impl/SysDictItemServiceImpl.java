/*
 * Copyright (c) 2021-2022, gaoweixuan (gaoweixuan@foxmail.com).
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

package com.breeze.boot.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.breeze.boot.system.domain.SysDictItem;
import com.breeze.boot.system.dto.DictDTO;
import com.breeze.boot.system.mapper.SysDictItemMapper;
import com.breeze.boot.system.service.SysDictItemService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统字典项服务impl
 *
 * @author gaoweixuan
 * @date 2022-09-02
 */
@Service
public class SysDictItemServiceImpl extends ServiceImpl<SysDictItemMapper, SysDictItem> implements SysDictItemService {

    /**
     * 字典列表项
     *
     * @param dictDTO dict dto
     * @return {@link List}<{@link SysDictItem}>
     */
    @Override
    public List<SysDictItem> listDictItem(DictDTO dictDTO) {
        return this.baseMapper.listDictDetailByDictId(dictDTO.getId());
    }
}
