package com.denzo.daschess.dao;

import android.database.Cursor;

public class LocalUserDao extends AbstractDao<LocalUser, String> {

    public static final String TABLENAME = "LOCAL_USER";

    /**
     * Properties of entity LocalUser.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Login = new Property(0, String.class, "login", true, "LOGIN");
        public final static Property Name = new Property(1, String.class, "name", false, "NAME");
        public final static Property AvatarUrl = new Property(2, String.class, "avatarUrl", false, "AVATAR_URL");
        public final static Property Followers = new Property(3, Integer.class, "followers", false, "FOLLOWERS");
        public final static Property Following = new Property(4, Integer.class, "following", false, "FOLLOWING");
    }


    public LocalUserDao(DaoConfig config) {
        super(config);
    }

    public LocalUserDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"LOCAL_USER\" (" + //
                "\"LOGIN\" TEXT PRIMARY KEY NOT NULL ," + // 0: login
                "\"NAME\" TEXT," + // 1: name
                "\"AVATAR_URL\" TEXT," + // 2: avatarUrl
                "\"FOLLOWERS\" INTEGER," + // 3: followers
                "\"FOLLOWING\" INTEGER);"); // 4: following
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"LOCAL_USER\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, LocalUser entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getLogin());

        String name = entity.getName();
        if (name != null) {
            stmt.bindString(2, name);
        }

        String avatarUrl = entity.getAvatarUrl();
        if (avatarUrl != null) {
            stmt.bindString(3, avatarUrl);
        }

        Integer followers = entity.getFollowers();
        if (followers != null) {
            stmt.bindLong(4, followers);
        }

        Integer following = entity.getFollowing();
        if (following != null) {
            stmt.bindLong(5, following);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, LocalUser entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getLogin());

        String name = entity.getName();
        if (name != null) {
            stmt.bindString(2, name);
        }

        String avatarUrl = entity.getAvatarUrl();
        if (avatarUrl != null) {
            stmt.bindString(3, avatarUrl);
        }

        Integer followers = entity.getFollowers();
        if (followers != null) {
            stmt.bindLong(4, followers);
        }

        Integer following = entity.getFollowing();
        if (following != null) {
            stmt.bindLong(5, following);
        }
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.getString(offset + 0);
    }

    @Override
    public LocalUser readEntity(Cursor cursor, int offset) {
        LocalUser entity = new LocalUser( //
                cursor.getString(offset + 0), // login
                cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // name
                cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // avatarUrl
                cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // followers
                cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4) // following
        );
        return entity;
    }

    @Override
    public void readEntity(Cursor cursor, LocalUser entity, int offset) {
        entity.setLogin(cursor.getString(offset + 0));
        entity.setName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setAvatarUrl(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setFollowers(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setFollowing(cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4));
    }

    @Override
    protected final String updateKeyAfterInsert(LocalUser entity, long rowId) {
        return entity.getLogin();
    }

    @Override
    public String getKey(LocalUser entity) {
        if(entity != null) {
            return entity.getLogin();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(LocalUser entity) {
        throw new UnsupportedOperationException("Unsupported for entities with a non-null key");
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }

}