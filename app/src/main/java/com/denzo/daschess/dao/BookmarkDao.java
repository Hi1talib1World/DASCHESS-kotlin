package com.denzo.daschess.dao;

import android.database.Cursor;

public class BookmarkDao extends AbstractDao<Bookmark, String> {

    public static final String TABLENAME = "BOOKMARK";

    /**
     * Properties of entity Bookmark.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, String.class, "id", true, "ID");
        public final static Property Type = new Property(1, String.class, "type", false, "TYPE");
        public final static Property UserId = new Property(2, String.class, "userId", false, "USER_ID");
        public final static Property RepoId = new Property(3, Long.class, "repoId", false, "REPO_ID");
        public final static Property MarkTime = new Property(4, java.util.Date.class, "markTime", false, "MARK_TIME");
    }


    public BookmarkDao(DaoConfig config) {
        super(config);
    }

    public BookmarkDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"BOOKMARK\" (" + //
                "\"ID\" TEXT PRIMARY KEY NOT NULL ," + // 0: id
                "\"TYPE\" TEXT NOT NULL ," + // 1: type
                "\"USER_ID\" TEXT," + // 2: userId
                "\"REPO_ID\" INTEGER," + // 3: repoId
                "\"MARK_TIME\" INTEGER);"); // 4: markTime
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"BOOKMARK\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Bookmark entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getId());
        stmt.bindString(2, entity.getType());

        String userId = entity.getUserId();
        if (userId != null) {
            stmt.bindString(3, userId);
        }

        Long repoId = entity.getRepoId();
        if (repoId != null) {
            stmt.bindLong(4, repoId);
        }

        java.util.Date markTime = entity.getMarkTime();
        if (markTime != null) {
            stmt.bindLong(5, markTime.getTime());
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Bookmark entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getId());
        stmt.bindString(2, entity.getType());

        String userId = entity.getUserId();
        if (userId != null) {
            stmt.bindString(3, userId);
        }

        Long repoId = entity.getRepoId();
        if (repoId != null) {
            stmt.bindLong(4, repoId);
        }

        java.util.Date markTime = entity.getMarkTime();
        if (markTime != null) {
            stmt.bindLong(5, markTime.getTime());
        }
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.getString(offset + 0);
    }

    @Override
    public Bookmark readEntity(Cursor cursor, int offset) {
        Bookmark entity = new Bookmark( //
                cursor.getString(offset + 0), // id
                cursor.getString(offset + 1), // type
                cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // userId
                cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3), // repoId
                cursor.isNull(offset + 4) ? null : new java.util.Date(cursor.getLong(offset + 4)) // markTime
        );
        return entity;
    }

    @Override
    public void readEntity(Cursor cursor, Bookmark entity, int offset) {
        entity.setId(cursor.getString(offset + 0));
        entity.setType(cursor.getString(offset + 1));
        entity.setUserId(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setRepoId(cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3));
        entity.setMarkTime(cursor.isNull(offset + 4) ? null : new java.util.Date(cursor.getLong(offset + 4)));
    }

    @Override
    protected final String updateKeyAfterInsert(Bookmark entity, long rowId) {
        return entity.getId();
    }

    @Override
    public String getKey(Bookmark entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Bookmark entity) {
        throw new UnsupportedOperationException("Unsupported for entities with a non-null key");
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }

}