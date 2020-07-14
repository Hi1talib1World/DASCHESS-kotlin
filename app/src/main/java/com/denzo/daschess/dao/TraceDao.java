package com.denzo.daschess.dao;

import android.os.Trace;

public class TraceDao extends AbstractDao<Trace, String> {

    public static final String TABLENAME = "TRACE";

    /**
     * Properties of entity Trace.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, String.class, "id", true, "ID");
        public final static Property Type = new Property(1, String.class, "type", false, "TYPE");
        public final static Property UserId = new Property(2, String.class, "userId", false, "USER_ID");
        public final static Property RepoId = new Property(3, Long.class, "repoId", false, "REPO_ID");
        public final static Property StartTime = new Property(4, java.util.Date.class, "startTime", false, "START_TIME");
        public final static Property LatestTime = new Property(5, java.util.Date.class, "latestTime", false, "LATEST_TIME");
        public final static Property TraceNum = new Property(6, Integer.class, "traceNum", false, "TRACE_NUM");
    }


    public TraceDao(DaoConfig config) {
        super(config);
    }

    public TraceDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"TRACE\" (" + //
                "\"ID\" TEXT PRIMARY KEY NOT NULL ," + // 0: id
                "\"TYPE\" TEXT," + // 1: type
                "\"USER_ID\" TEXT," + // 2: userId
                "\"REPO_ID\" INTEGER," + // 3: repoId
                "\"START_TIME\" INTEGER," + // 4: startTime
                "\"LATEST_TIME\" INTEGER," + // 5: latestTime
                "\"TRACE_NUM\" INTEGER);"); // 6: traceNum
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"TRACE\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Trace entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getId());

        String type = entity.getType();
        if (type != null) {
            stmt.bindString(2, type);
        }

        String userId = entity.getUserId();
        if (userId != null) {
            stmt.bindString(3, userId);
        }

        Long repoId = entity.getRepoId();
        if (repoId != null) {
            stmt.bindLong(4, repoId);
        }

        java.util.Date startTime = entity.getStartTime();
        if (startTime != null) {
            stmt.bindLong(5, startTime.getTime());
        }

        java.util.Date latestTime = entity.getLatestTime();
        if (latestTime != null) {
            stmt.bindLong(6, latestTime.getTime());
        }

        Integer traceNum = entity.getTraceNum();
        if (traceNum != null) {
            stmt.bindLong(7, traceNum);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Trace entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getId());

        String type = entity.getType();
        if (type != null) {
            stmt.bindString(2, type);
        }

        String userId = entity.getUserId();
        if (userId != null) {
            stmt.bindString(3, userId);
        }

        Long repoId = entity.getRepoId();
        if (repoId != null) {
            stmt.bindLong(4, repoId);
        }

        java.util.Date startTime = entity.getStartTime();
        if (startTime != null) {
            stmt.bindLong(5, startTime.getTime());
        }

        java.util.Date latestTime = entity.getLatestTime();
        if (latestTime != null) {
            stmt.bindLong(6, latestTime.getTime());
        }

        Integer traceNum = entity.getTraceNum();
        if (traceNum != null) {
            stmt.bindLong(7, traceNum);
        }
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.getString(offset + 0);
    }

    @Override
    public Trace readEntity(Cursor cursor, int offset) {
        Trace entity = new Trace( //
                cursor.getString(offset + 0), // id
                cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // type
                cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // userId
                cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3), // repoId
                cursor.isNull(offset + 4) ? null : new java.util.Date(cursor.getLong(offset + 4)), // startTime
                cursor.isNull(offset + 5) ? null : new java.util.Date(cursor.getLong(offset + 5)), // latestTime
                cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6) // traceNum
        );
        return entity;
    }

    @Override
    public void readEntity(Cursor cursor, Trace entity, int offset) {
        entity.setId(cursor.getString(offset + 0));
        entity.setType(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setUserId(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setRepoId(cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3));
        entity.setStartTime(cursor.isNull(offset + 4) ? null : new java.util.Date(cursor.getLong(offset + 4)));
        entity.setLatestTime(cursor.isNull(offset + 5) ? null : new java.util.Date(cursor.getLong(offset + 5)));
        entity.setTraceNum(cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6));
    }

    @Override
    protected final String updateKeyAfterInsert(Trace entity, long rowId) {
        return entity.getId();
    }

    @Override
    public String getKey(Trace entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Trace entity) {
        throw new UnsupportedOperationException("Unsupported for entities with a non-null key");
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }

}