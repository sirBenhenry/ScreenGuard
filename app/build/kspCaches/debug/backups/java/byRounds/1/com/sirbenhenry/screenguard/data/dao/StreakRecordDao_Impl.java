package com.sirbenhenry.screenguard.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.sirbenhenry.screenguard.data.entity.StreakRecord;
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
public final class StreakRecordDao_Impl implements StreakRecordDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<StreakRecord> __insertionAdapterOfStreakRecord;

  public StreakRecordDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfStreakRecord = new EntityInsertionAdapter<StreakRecord>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `streak_records` (`dateKey`,`allUnderLimit`,`totalMinutesUsed`,`totalLimitMinutes`) VALUES (?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final StreakRecord entity) {
        statement.bindString(1, entity.getDateKey());
        final int _tmp = entity.getAllUnderLimit() ? 1 : 0;
        statement.bindLong(2, _tmp);
        statement.bindLong(3, entity.getTotalMinutesUsed());
        statement.bindLong(4, entity.getTotalLimitMinutes());
      }
    };
  }

  @Override
  public Object insert(final StreakRecord record, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfStreakRecord.insert(record);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<StreakRecord>> getAllFlow() {
    final String _sql = "SELECT * FROM streak_records ORDER BY dateKey DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"streak_records"}, new Callable<List<StreakRecord>>() {
      @Override
      @NonNull
      public List<StreakRecord> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDateKey = CursorUtil.getColumnIndexOrThrow(_cursor, "dateKey");
          final int _cursorIndexOfAllUnderLimit = CursorUtil.getColumnIndexOrThrow(_cursor, "allUnderLimit");
          final int _cursorIndexOfTotalMinutesUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "totalMinutesUsed");
          final int _cursorIndexOfTotalLimitMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "totalLimitMinutes");
          final List<StreakRecord> _result = new ArrayList<StreakRecord>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final StreakRecord _item;
            final String _tmpDateKey;
            _tmpDateKey = _cursor.getString(_cursorIndexOfDateKey);
            final boolean _tmpAllUnderLimit;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfAllUnderLimit);
            _tmpAllUnderLimit = _tmp != 0;
            final int _tmpTotalMinutesUsed;
            _tmpTotalMinutesUsed = _cursor.getInt(_cursorIndexOfTotalMinutesUsed);
            final int _tmpTotalLimitMinutes;
            _tmpTotalLimitMinutes = _cursor.getInt(_cursorIndexOfTotalLimitMinutes);
            _item = new StreakRecord(_tmpDateKey,_tmpAllUnderLimit,_tmpTotalMinutesUsed,_tmpTotalLimitMinutes);
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
  public Object getLast365(final Continuation<? super List<StreakRecord>> $completion) {
    final String _sql = "SELECT * FROM streak_records ORDER BY dateKey DESC LIMIT 365";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<StreakRecord>>() {
      @Override
      @NonNull
      public List<StreakRecord> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDateKey = CursorUtil.getColumnIndexOrThrow(_cursor, "dateKey");
          final int _cursorIndexOfAllUnderLimit = CursorUtil.getColumnIndexOrThrow(_cursor, "allUnderLimit");
          final int _cursorIndexOfTotalMinutesUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "totalMinutesUsed");
          final int _cursorIndexOfTotalLimitMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "totalLimitMinutes");
          final List<StreakRecord> _result = new ArrayList<StreakRecord>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final StreakRecord _item;
            final String _tmpDateKey;
            _tmpDateKey = _cursor.getString(_cursorIndexOfDateKey);
            final boolean _tmpAllUnderLimit;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfAllUnderLimit);
            _tmpAllUnderLimit = _tmp != 0;
            final int _tmpTotalMinutesUsed;
            _tmpTotalMinutesUsed = _cursor.getInt(_cursorIndexOfTotalMinutesUsed);
            final int _tmpTotalLimitMinutes;
            _tmpTotalLimitMinutes = _cursor.getInt(_cursorIndexOfTotalLimitMinutes);
            _item = new StreakRecord(_tmpDateKey,_tmpAllUnderLimit,_tmpTotalMinutesUsed,_tmpTotalLimitMinutes);
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
  public Object getForDate(final String date,
      final Continuation<? super StreakRecord> $completion) {
    final String _sql = "SELECT * FROM streak_records WHERE dateKey = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, date);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<StreakRecord>() {
      @Override
      @Nullable
      public StreakRecord call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDateKey = CursorUtil.getColumnIndexOrThrow(_cursor, "dateKey");
          final int _cursorIndexOfAllUnderLimit = CursorUtil.getColumnIndexOrThrow(_cursor, "allUnderLimit");
          final int _cursorIndexOfTotalMinutesUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "totalMinutesUsed");
          final int _cursorIndexOfTotalLimitMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "totalLimitMinutes");
          final StreakRecord _result;
          if (_cursor.moveToFirst()) {
            final String _tmpDateKey;
            _tmpDateKey = _cursor.getString(_cursorIndexOfDateKey);
            final boolean _tmpAllUnderLimit;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfAllUnderLimit);
            _tmpAllUnderLimit = _tmp != 0;
            final int _tmpTotalMinutesUsed;
            _tmpTotalMinutesUsed = _cursor.getInt(_cursorIndexOfTotalMinutesUsed);
            final int _tmpTotalLimitMinutes;
            _tmpTotalLimitMinutes = _cursor.getInt(_cursorIndexOfTotalLimitMinutes);
            _result = new StreakRecord(_tmpDateKey,_tmpAllUnderLimit,_tmpTotalMinutesUsed,_tmpTotalLimitMinutes);
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
  public Object countGoodDaysSince(final String fromDate,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM streak_records WHERE allUnderLimit = 1 AND dateKey >= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, fromDate);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
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
