package com.sirbenhenry.screenguard.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.sirbenhenry.screenguard.data.entity.UsageRecord;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class UsageRecordDao_Impl implements UsageRecordDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<UsageRecord> __insertionAdapterOfUsageRecord;

  private final EntityDeletionOrUpdateAdapter<UsageRecord> __updateAdapterOfUsageRecord;

  public UsageRecordDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfUsageRecord = new EntityInsertionAdapter<UsageRecord>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `usage_records` (`id`,`packageName`,`dateKey`,`totalMinutes`,`sessions`,`limitMinutes`,`underLimit`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UsageRecord entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getPackageName());
        statement.bindString(3, entity.getDateKey());
        statement.bindLong(4, entity.getTotalMinutes());
        statement.bindLong(5, entity.getSessions());
        statement.bindLong(6, entity.getLimitMinutes());
        final int _tmp = entity.getUnderLimit() ? 1 : 0;
        statement.bindLong(7, _tmp);
      }
    };
    this.__updateAdapterOfUsageRecord = new EntityDeletionOrUpdateAdapter<UsageRecord>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `usage_records` SET `id` = ?,`packageName` = ?,`dateKey` = ?,`totalMinutes` = ?,`sessions` = ?,`limitMinutes` = ?,`underLimit` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UsageRecord entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getPackageName());
        statement.bindString(3, entity.getDateKey());
        statement.bindLong(4, entity.getTotalMinutes());
        statement.bindLong(5, entity.getSessions());
        statement.bindLong(6, entity.getLimitMinutes());
        final int _tmp = entity.getUnderLimit() ? 1 : 0;
        statement.bindLong(7, _tmp);
        statement.bindLong(8, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final UsageRecord record, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfUsageRecord.insert(record);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final UsageRecord record, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfUsageRecord.handle(record);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<UsageRecord>> getForDateFlow(final String date) {
    final String _sql = "SELECT * FROM usage_records WHERE dateKey = ? ORDER BY totalMinutes DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, date);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"usage_records"}, new Callable<List<UsageRecord>>() {
      @Override
      @NonNull
      public List<UsageRecord> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfDateKey = CursorUtil.getColumnIndexOrThrow(_cursor, "dateKey");
          final int _cursorIndexOfTotalMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "totalMinutes");
          final int _cursorIndexOfSessions = CursorUtil.getColumnIndexOrThrow(_cursor, "sessions");
          final int _cursorIndexOfLimitMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "limitMinutes");
          final int _cursorIndexOfUnderLimit = CursorUtil.getColumnIndexOrThrow(_cursor, "underLimit");
          final List<UsageRecord> _result = new ArrayList<UsageRecord>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final UsageRecord _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpPackageName;
            _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            final String _tmpDateKey;
            _tmpDateKey = _cursor.getString(_cursorIndexOfDateKey);
            final int _tmpTotalMinutes;
            _tmpTotalMinutes = _cursor.getInt(_cursorIndexOfTotalMinutes);
            final int _tmpSessions;
            _tmpSessions = _cursor.getInt(_cursorIndexOfSessions);
            final int _tmpLimitMinutes;
            _tmpLimitMinutes = _cursor.getInt(_cursorIndexOfLimitMinutes);
            final boolean _tmpUnderLimit;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfUnderLimit);
            _tmpUnderLimit = _tmp != 0;
            _item = new UsageRecord(_tmpId,_tmpPackageName,_tmpDateKey,_tmpTotalMinutes,_tmpSessions,_tmpLimitMinutes,_tmpUnderLimit);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getForDate(final String date,
      final Continuation<? super List<UsageRecord>> $completion) {
    final String _sql = "SELECT * FROM usage_records WHERE dateKey = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, date);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<UsageRecord>>() {
      @Override
      @NonNull
      public List<UsageRecord> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfDateKey = CursorUtil.getColumnIndexOrThrow(_cursor, "dateKey");
          final int _cursorIndexOfTotalMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "totalMinutes");
          final int _cursorIndexOfSessions = CursorUtil.getColumnIndexOrThrow(_cursor, "sessions");
          final int _cursorIndexOfLimitMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "limitMinutes");
          final int _cursorIndexOfUnderLimit = CursorUtil.getColumnIndexOrThrow(_cursor, "underLimit");
          final List<UsageRecord> _result = new ArrayList<UsageRecord>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final UsageRecord _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpPackageName;
            _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            final String _tmpDateKey;
            _tmpDateKey = _cursor.getString(_cursorIndexOfDateKey);
            final int _tmpTotalMinutes;
            _tmpTotalMinutes = _cursor.getInt(_cursorIndexOfTotalMinutes);
            final int _tmpSessions;
            _tmpSessions = _cursor.getInt(_cursorIndexOfSessions);
            final int _tmpLimitMinutes;
            _tmpLimitMinutes = _cursor.getInt(_cursorIndexOfLimitMinutes);
            final boolean _tmpUnderLimit;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfUnderLimit);
            _tmpUnderLimit = _tmp != 0;
            _item = new UsageRecord(_tmpId,_tmpPackageName,_tmpDateKey,_tmpTotalMinutes,_tmpSessions,_tmpLimitMinutes,_tmpUnderLimit);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getForPackageDate(final String pkg, final String date,
      final Continuation<? super UsageRecord> $completion) {
    final String _sql = "SELECT * FROM usage_records WHERE packageName = ? AND dateKey = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, pkg);
    _argIndex = 2;
    _statement.bindString(_argIndex, date);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<UsageRecord>() {
      @Override
      @Nullable
      public UsageRecord call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfDateKey = CursorUtil.getColumnIndexOrThrow(_cursor, "dateKey");
          final int _cursorIndexOfTotalMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "totalMinutes");
          final int _cursorIndexOfSessions = CursorUtil.getColumnIndexOrThrow(_cursor, "sessions");
          final int _cursorIndexOfLimitMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "limitMinutes");
          final int _cursorIndexOfUnderLimit = CursorUtil.getColumnIndexOrThrow(_cursor, "underLimit");
          final UsageRecord _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpPackageName;
            _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            final String _tmpDateKey;
            _tmpDateKey = _cursor.getString(_cursorIndexOfDateKey);
            final int _tmpTotalMinutes;
            _tmpTotalMinutes = _cursor.getInt(_cursorIndexOfTotalMinutes);
            final int _tmpSessions;
            _tmpSessions = _cursor.getInt(_cursorIndexOfSessions);
            final int _tmpLimitMinutes;
            _tmpLimitMinutes = _cursor.getInt(_cursorIndexOfLimitMinutes);
            final boolean _tmpUnderLimit;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfUnderLimit);
            _tmpUnderLimit = _tmp != 0;
            _result = new UsageRecord(_tmpId,_tmpPackageName,_tmpDateKey,_tmpTotalMinutes,_tmpSessions,_tmpLimitMinutes,_tmpUnderLimit);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getSince(final String fromDate,
      final Continuation<? super List<UsageRecord>> $completion) {
    final String _sql = "SELECT * FROM usage_records WHERE dateKey >= ? ORDER BY dateKey DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, fromDate);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<UsageRecord>>() {
      @Override
      @NonNull
      public List<UsageRecord> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfDateKey = CursorUtil.getColumnIndexOrThrow(_cursor, "dateKey");
          final int _cursorIndexOfTotalMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "totalMinutes");
          final int _cursorIndexOfSessions = CursorUtil.getColumnIndexOrThrow(_cursor, "sessions");
          final int _cursorIndexOfLimitMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "limitMinutes");
          final int _cursorIndexOfUnderLimit = CursorUtil.getColumnIndexOrThrow(_cursor, "underLimit");
          final List<UsageRecord> _result = new ArrayList<UsageRecord>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final UsageRecord _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpPackageName;
            _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            final String _tmpDateKey;
            _tmpDateKey = _cursor.getString(_cursorIndexOfDateKey);
            final int _tmpTotalMinutes;
            _tmpTotalMinutes = _cursor.getInt(_cursorIndexOfTotalMinutes);
            final int _tmpSessions;
            _tmpSessions = _cursor.getInt(_cursorIndexOfSessions);
            final int _tmpLimitMinutes;
            _tmpLimitMinutes = _cursor.getInt(_cursorIndexOfLimitMinutes);
            final boolean _tmpUnderLimit;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfUnderLimit);
            _tmpUnderLimit = _tmp != 0;
            _item = new UsageRecord(_tmpId,_tmpPackageName,_tmpDateKey,_tmpTotalMinutes,_tmpSessions,_tmpLimitMinutes,_tmpUnderLimit);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<UsageRecord>> getLast365Flow() {
    final String _sql = "SELECT * FROM usage_records ORDER BY dateKey DESC LIMIT 365";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"usage_records"}, new Callable<List<UsageRecord>>() {
      @Override
      @NonNull
      public List<UsageRecord> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfDateKey = CursorUtil.getColumnIndexOrThrow(_cursor, "dateKey");
          final int _cursorIndexOfTotalMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "totalMinutes");
          final int _cursorIndexOfSessions = CursorUtil.getColumnIndexOrThrow(_cursor, "sessions");
          final int _cursorIndexOfLimitMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "limitMinutes");
          final int _cursorIndexOfUnderLimit = CursorUtil.getColumnIndexOrThrow(_cursor, "underLimit");
          final List<UsageRecord> _result = new ArrayList<UsageRecord>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final UsageRecord _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpPackageName;
            _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            final String _tmpDateKey;
            _tmpDateKey = _cursor.getString(_cursorIndexOfDateKey);
            final int _tmpTotalMinutes;
            _tmpTotalMinutes = _cursor.getInt(_cursorIndexOfTotalMinutes);
            final int _tmpSessions;
            _tmpSessions = _cursor.getInt(_cursorIndexOfSessions);
            final int _tmpLimitMinutes;
            _tmpLimitMinutes = _cursor.getInt(_cursorIndexOfLimitMinutes);
            final boolean _tmpUnderLimit;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfUnderLimit);
            _tmpUnderLimit = _tmp != 0;
            _item = new UsageRecord(_tmpId,_tmpPackageName,_tmpDateKey,_tmpTotalMinutes,_tmpSessions,_tmpLimitMinutes,_tmpUnderLimit);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object totalMinutesSince(final String pkg, final String fromDate,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT SUM(totalMinutes) FROM usage_records WHERE packageName = ? AND dateKey >= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, pkg);
    _argIndex = 2;
    _statement.bindString(_argIndex, fromDate);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @Nullable
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
