package com.kazyle.hgclient.converter;

import android.database.Cursor;

import com.kazyle.hgclient.entity.ScriptType;

import org.xutils.db.converter.ColumnConverter;
import org.xutils.db.sqlite.ColumnDbType;

/**
 * Created by Kazyle on 2016/8/28.
 */
public class ScriptTypeConverter implements ColumnConverter<ScriptType> {

    @Override
    public ScriptType getFieldValue(Cursor cursor, int index) {
        int value = cursor.getInt(index);
        return ScriptType.valueOf(value);
    }

    @Override
    public Object fieldValue2DbValue(ScriptType fieldValue) {
        return fieldValue.ordinal();
    }

    @Override
    public ColumnDbType getColumnDbType() {
        return ColumnDbType.INTEGER;
    }
}
