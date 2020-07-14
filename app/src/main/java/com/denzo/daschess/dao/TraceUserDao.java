package com.denzo.daschess.dao;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

public class TraceUserDao extends AbstractDao<TraceUser, String> {

    public static final String TABLENAME = "TRACE_USER";

    /**
     * Properties of entity TraceUser.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Login = new Property(0, String.class, "login", true, "LOGIN");
        public final static Property Name = new Property(1, String.class, "name", false, "NAME");
        public final static Property AvatarUrl = new Property(2, String.class, "avatarUrl", false, "AVATAR_URL");
        public final static Property Followers = new Property(3, Integer.class, "followers", false, "FOLLOWERS");
        public final static Property Following = new Property(4, Integer.class, "following", false, "FOLLOWING");
        public final static Property StartTime = new Property(5, java.util.Date.class, "startTime", false, "START_TIME");
        public final static Property LatestTime = new Property(6, java.util.Date.class, "latestTime", false, "LATEST_TIME");
        public final static Property TraceNum = new Property(7, Integer.class, "traceNum", false, "TRACE_NUM");
    }


    public TraceUserDao(DaoConfig config) {
        super(config);
    }

    public TraceUserDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"TRACE_USER\" (" + //
                "\"LOGIN\" TEXT PRIMARY KEY NOT NULL ," + // 0: login
                "\"NAME\" TEXT," + // 1: name
                "\"AVATAR_URL\" TEXT," + // 2: avatarUrl
                "\"FOLLOWERS\" INTEGER," + // 3: followers
                "\"FOLLOWING\" INTEGER," + // 4: following
                "\"START_TIME\" INTEGER," + // 5: startTime
                "\"LATEST_TIME\" INTEGER," + // 6: latestTime
                "\"TRACE_NUM\" INTEGER);"); // 7: traceNum
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"TRACE_USER\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, TraceUser entity) {
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

        java.util.Date startTime = entity.getStartTime();
        if (startTime != null) {
            stmt.bindLong(6, startTime.getTime());
        }

        java.util.Date latestTime = entity.getLatestTime();
        if (latestTime != null) {
            stmt.bindLong(7, latestTime.getTime());
        }

        Integer traceNum = entity.getTraceNum();
        if (traceNum != null) {
            stmt.bindLong(8, traceNum);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, TraceUser entity) {
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

        java.util.Date startTime = entity.getStartTime();
        if (startTime != null) {
            stmt.bindLong(6, startTime.getTime());
        }

        java.util.Date latestTime = entity.getLatestTime();
        if (latestTime != null) {
            stmt.bindLong(7, latestTime.getTime());
        }

        Integer traceNum = entity.getTraceNum();
        if (traceNum != null) {
            stmt.bindLong(8, traceNum);
        }
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.getString(offset + 0);
    }

    @Override
    public TraceUser readEntity(Cursor cursor, int offset) {
        TraceUser entity = new TraceUser( //
                cursor.getString(offset + 0), // login
                cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // name
                cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // avatarUrl
                cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // followers
                cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4), // following
                cursor.isNull(offset + 5) ? null : new java.util.Date(cursor.getLong(offset + 5)), // startTime
                cursor.isNull(offset + 6) ? null : new java.util.Date(cursor.getLong(offset + 6)), // latestTime
                cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7) // traceNum
        );
        return entity;
    }

    @Override
    public void readEntity(Cursor cursor, TraceUser entity, int offset) {
        entity.setLogin(cursor.getString(offset + 0));
        entity.setName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setAvatarUrl(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setFollowers(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setFollowing(cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4));
        entity.setStartTime(cursor.isNull(offset + 5) ? null : new java.util.Date(cursor.getLong(offset + 5)));
        entity.setLatestTime(cursor.isNull(offset + 6) ? null : new java.util.Date(cursor.getLong(offset + 6)));
        entity.setTraceNum(cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7));
    }

    @Override
    protected final String updateKeyAfterInsert(TraceUser entity, long rowId) {
        return entity.getLogin();
    }

    @Override
    public String getKey(TraceUser entity) {
        if(entity != null) {
            return entity.getLogin();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(TraceUser entity) {
        throw new UnsupportedOperationException("Unsupported for entities with a non-null key");
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }

}