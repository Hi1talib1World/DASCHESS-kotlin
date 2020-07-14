package com.denzo.daschess.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.internal.DaoConfig;

public class AuthUserDao extends AbstractDao<AuthUser, String> {

    public static final String TABLENAME = "AUTH_USER";

    /**
     * Properties of entity AuthUser.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property AccessToken = new Property(0, String.class, "accessToken", true, "ACCESS_TOKEN");
        public final static Property AuthTime = new Property(1, java.util.Date.class, "authTime", false, "AUTH_TIME");
        public final static Property ExpireIn = new Property(2, int.class, "expireIn", false, "EXPIRE_IN");
        public final static Property Scope = new Property(3, String.class, "scope", false, "SCOPE");
        public final static Property Selected = new Property(4, boolean.class, "selected", false, "SELECTED");
        public final static Property LoginId = new Property(5, String.class, "loginId", false, "LOGIN_ID");
        public final static Property Name = new Property(6, String.class, "name", false, "NAME");
        public final static Property Avatar = new Property(7, String.class, "avatar", false, "AVATAR");
    }


    public AuthUserDao(DaoConfig config) {
        super(config);
    }

    public AuthUserDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"AUTH_USER\" (" + //
                "\"ACCESS_TOKEN\" TEXT PRIMARY KEY NOT NULL ," + // 0: accessToken
                "\"AUTH_TIME\" INTEGER NOT NULL ," + // 1: authTime
                "\"EXPIRE_IN\" INTEGER NOT NULL ," + // 2: expireIn
                "\"SCOPE\" TEXT NOT NULL ," + // 3: scope
                "\"SELECTED\" INTEGER NOT NULL ," + // 4: selected
                "\"LOGIN_ID\" TEXT NOT NULL ," + // 5: loginId
                "\"NAME\" TEXT," + // 6: name
                "\"AVATAR\" TEXT);"); // 7: avatar
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"AUTH_USER\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, AuthUser entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getAccessToken());
        stmt.bindLong(2, entity.getAuthTime().getTime());
        stmt.bindLong(3, entity.getExpireIn());
        stmt.bindString(4, entity.getScope());
        stmt.bindLong(5, entity.getSelected() ? 1L: 0L);
        stmt.bindString(6, entity.getLoginId());

        String name = entity.getName();
        if (name != null) {
            stmt.bindString(7, name);
        }

        String avatar = entity.getAvatar();
        if (avatar != null) {
            stmt.bindString(8, avatar);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, AuthUser entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getAccessToken());
        stmt.bindLong(2, entity.getAuthTime().getTime());
        stmt.bindLong(3, entity.getExpireIn());
        stmt.bindString(4, entity.getScope());
        stmt.bindLong(5, entity.getSelected() ? 1L: 0L);
        stmt.bindString(6, entity.getLoginId());

        String name = entity.getName();
        if (name != null) {
            stmt.bindString(7, name);
        }

        String avatar = entity.getAvatar();
        if (avatar != null) {
            stmt.bindString(8, avatar);
        }
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.getString(offset + 0);
    }

    @Override
    public AuthUser readEntity(Cursor cursor, int offset) {
        AuthUser entity = new AuthUser( //
                cursor.getString(offset + 0), // accessToken
                new java.util.Date(cursor.getLong(offset + 1)), // authTime
                cursor.getInt(offset + 2), // expireIn
                cursor.getString(offset + 3), // scope
                cursor.getShort(offset + 4) != 0, // selected
                cursor.getString(offset + 5), // loginId
                cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // name
                cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7) // avatar
        );
        return entity;
    }

    @Override
    public void readEntity(Cursor cursor, AuthUser entity, int offset) {
        entity.setAccessToken(cursor.getString(offset + 0));
        entity.setAuthTime(new java.util.Date(cursor.getLong(offset + 1)));
        entity.setExpireIn(cursor.getInt(offset + 2));
        entity.setScope(cursor.getString(offset + 3));
        entity.setSelected(cursor.getShort(offset + 4) != 0);
        entity.setLoginId(cursor.getString(offset + 5));
        entity.setName(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setAvatar(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
    }

    @Override
    protected final String updateKeyAfterInsert(AuthUser entity, long rowId) {
        return entity.getAccessToken();
    }

    @Override
    public String getKey(AuthUser entity) {
        if(entity != null) {
            return entity.getAccessToken();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(AuthUser entity) {
        throw new UnsupportedOperationException("Unsupported for entities with a non-null key");
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }

}
