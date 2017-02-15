package com.kazyle.hgclient.entity;

/**
 * Created by Kazyle on 2016/8/23.
 */
public enum ScriptType {

    Touchelper, // 触摸精灵脚本
    TouchSprite; // 触动精灵脚本

    public static ScriptType valueOf(int value) {
        switch (value) {
            case 0:
                return Touchelper;
            default:
                return TouchSprite;

        }
    }
}
