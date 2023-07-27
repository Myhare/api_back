package com.ming.web.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件上传相对路径枚举
 */
@Getter
@AllArgsConstructor
public enum UploadPathEnum {

    AVATAR("avatar/","用户头像路径");

    private final String path;

    private final String desc;

}
