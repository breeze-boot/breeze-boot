package com.breeze.boot.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.breeze.boot.admin.dto.FileDTO;
import com.breeze.boot.admin.entity.SysFile;

/**
 * 系统文件服务
 *
 * @author breeze
 * @date 2022-09-02
 */
public interface SysFileService extends IService<SysFile> {

    /**
     * 列表文件
     *
     * @param fileDTO 文件dto
     * @return {@link Page}<{@link SysFile}>
     */
    Page<SysFile> listFile(FileDTO fileDTO);
}
