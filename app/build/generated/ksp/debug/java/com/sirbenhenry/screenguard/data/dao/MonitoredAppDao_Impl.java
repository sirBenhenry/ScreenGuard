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
import com.sirbenhenry.screenguard.data.entity.MonitoredApp;
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
public final class MonitoredAppDao_Impl implements MonitoredAppDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MonitoredApp> __insertionAdapterOfMonitoredApp;

  private final EntityDeletionOrUpdateAdapter<MonitoredApp> __deletionAdapterOfMonitoredApp;

  private final EntityDeletionOrUpdateAdapter<MonitoredApp> __updateAdapterOfMonitoredApp;

  public MonitoredAppDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMonitoredApp = new EntityInsertionAdapter<MonitoredApp>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `monitored_apps` (`packageName`,`appName`,`dailyLimitMinutes`,`baseCooldownSeconds`,`addedAt`,`isEnabled`,`weekendLimitMinutes`,`focusBlockEnabled`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MonitoredApp entity) {
        statement.bindString(1, entity.getPackageName());
        statement.bindString(2, entity.getAppName());
        statement.bindLong(3, entity.getDailyLimitMinutes());
        statement.bindLong(4, entity.getBaseCooldownSeconds());
        statement.bindLong(5, entity.getAddedAt());
        final int _tmp = entity.isEnabled() ? 1 : 0;
        statement.bindLong(6, _tmp);
        statement.bindLong(7, entity.getWeekendLimitMinutes());
        final int _tmp_1 = entity.getFocusBlockEnabled() ? 1 : 0;
        statement.bindLong(8, _tmp_1);
      }
    };
    this.__deletionAdapterOfMonitoredApp = new EntityDeletionOrUpdateAdapter<MonitoredApp>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `monitored_apps` WHERE `packageName` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MonitoredApp entity) {
        statement.bindString(1, entity.getPackageName());
      }
    };
    this.__updateAdapterOfMonitoredApp = new EntityDeletionOrUpdateAdapter<MonitoredApp>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `monitored_apps` SET `packageName` = ?,`appName` = ?,`dailyLimitMinutes` = ?,`baseCooldownSeconds` = ?,`addedAt` = ?,`isEnabled` = ?,`weekendLimitMinutes` = ?,`focusBlockEnabled` = ? WHERE `packageName` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MonitoredApp entity) {
        statement.bindString(1, entity.getPackageName());
        statement.bindString(2, entity.getAppName());
        statement.bindLong(3, entity.getDailyLimitMinutes());
        statement.bindLong(4, entity.getBaseCooldownSeconds());
        statement.bindLong(5, entity.getAddedAt());
        final int _tmp = entity.isEnabled() ? 1 : 0;
        statement.bindLong(6, _tmp);
        statement.bindLong(7, entity.getWeekendLimitMinutes());
        final int _tmp_1 = entity.getFocusBlockEnabled() ? 1 : 0;
        statement.bindLong(8, _tmp_1);
        statement.bindString(9, entity.getPackageName());
      }
    };
  }

  @Override
  public Object insert(final MonitoredApp app, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMonitoredApp.insert(app);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final MonitoredApp app, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfMonitoredApp.handle(app);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final MonitoredApp app, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfMonitoredApp.handle(app);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<MonitoredApp>> getAllFlow() {
    final String _sql = "SELECT * FROM monitored_apps ORDER BY appName";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"monitored_apps"}, new Callable<List<MonitoredApp>>() {
      @Override
      @NonNull
      public List<MonitoredApp> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfAppName = CursorUtil.getColumnIndexOrThrow(_cursor, "appName");
          final int _cursorIndexOfDailyLimitMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyLimitMinutes");
          final int _cursorIndexOfBaseCooldownSeconds = CursorUtil.getColumnIndexOrThrow(_cursor, "baseCooldownSeconds");
          final int _cursorIndexOfAddedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "addedAt");
          final int _cursorIndexOfIsEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "isEnabled");
          final int _cursorIndexOfWeekendLimitMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "weekendLimitMinutes");
          final int _cursorIndexOfFocusBlockEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "focusBlockEnabled");
          final List<MonitoredApp> _result = new ArrayList<MonitoredApp>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MonitoredApp _item;
            final String _tmpPackageName;
            _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            final String _tmpAppName;
            _tmpAppName = _cursor.getString(_cursorIndexOfAppName);
            final int _tmpDailyLimitMinutes;
            _tmpDailyLimitMinutes = _cursor.getInt(_cursorIndexOfDailyLimitMinutes);
            final int _tmpBaseCooldownSeconds;
            _tmpBaseCooldownSeconds = _cursor.getInt(_cursorIndexOfBaseCooldownSeconds);
            final long _tmpAddedAt;
            _tmpAddedAt = _cursor.getLong(_cursorIndexOfAddedAt);
            final boolean _tmpIsEnabled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsEnabled);
            _tmpIsEnabled = _tmp != 0;
            final int _tmpWeekendLimitMinutes;
            _tmpWeekendLimitMinutes = _cursor.getInt(_cursorIndexOfWeekendLimitMinutes);
            final boolean _tmpFocusBlockEnabled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfFocusBlockEnabled);
            _tmpFocusBlockEnabled = _tmp_1 != 0;
            _item = new MonitoredApp(_tmpPackageName,_tmpAppName,_tmpDailyLimitMinutes,_tmpBaseCooldownSeconds,_tmpAddedAt,_tmpIsEnabled,_tmpWeekendLimitMinutes,_tmpFocusBlockEnabled);
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
  public Object getEnabled(final Continuation<? super List<MonitoredApp>> $completion) {
    final String _sql = "SELECT * FROM monitored_apps WHERE isEnabled = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MonitoredApp>>() {
      @Override
      @NonNull
      public List<MonitoredApp> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfAppName = CursorUtil.getColumnIndexOrThrow(_cursor, "appName");
          final int _cursorIndexOfDailyLimitMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyLimitMinutes");
          final int _cursorIndexOfBaseCooldownSeconds = CursorUtil.getColumnIndexOrThrow(_cursor, "baseCooldownSeconds");
          final int _cursorIndexOfAddedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "addedAt");
          final int _cursorIndexOfIsEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "isEnabled");
          final int _cursorIndexOfWeekendLimitMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "weekendLimitMinutes");
          final int _cursorIndexOfFocusBlockEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "focusBlockEnabled");
          final List<MonitoredApp> _result = new ArrayList<MonitoredApp>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MonitoredApp _item;
            final String _tmpPackageName;
            _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            final String _tmpAppName;
            _tmpAppName = _cursor.getString(_cursorIndexOfAppName);
            final int _tmpDailyLimitMinutes;
            _tmpDailyLimitMinutes = _cursor.getInt(_cursorIndexOfDailyLimitMinutes);
            final int _tmpBaseCooldownSeconds;
            _tmpBaseCooldownSeconds = _cursor.getInt(_cursorIndexOfBaseCooldownSeconds);
            final long _tmpAddedAt;
            _tmpAddedAt = _cursor.getLong(_cursorIndexOfAddedAt);
            final boolean _tmpIsEnabled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsEnabled);
            _tmpIsEnabled = _tmp != 0;
            final int _tmpWeekendLimitMinutes;
            _tmpWeekendLimitMinutes = _cursor.getInt(_cursorIndexOfWeekendLimitMinutes);
            final boolean _tmpFocusBlockEnabled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfFocusBlockEnabled);
            _tmpFocusBlockEnabled = _tmp_1 != 0;
            _item = new MonitoredApp(_tmpPackageName,_tmpAppName,_tmpDailyLimitMinutes,_tmpBaseCooldownSeconds,_tmpAddedAt,_tmpIsEnabled,_tmpWeekendLimitMinutes,_tmpFocusBlockEnabled);
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
  public Object getByPackage(final String pkg,
      final Continuation<? super MonitoredApp> $completion) {
    final String _sql = "SELECT * FROM monitored_apps WHERE packageName = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, pkg);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<MonitoredApp>() {
      @Override
      @Nullable
      public MonitoredApp call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfAppName = CursorUtil.getColumnIndexOrThrow(_cursor, "appName");
          final int _cursorIndexOfDailyLimitMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyLimitMinutes");
          final int _cursorIndexOfBaseCooldownSeconds = CursorUtil.getColumnIndexOrThrow(_cursor, "baseCooldownSeconds");
          final int _cursorIndexOfAddedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "addedAt");
          final int _cursorIndexOfIsEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "isEnabled");
          final int _cursorIndexOfWeekendLimitMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "weekendLimitMinutes");
          final int _cursorIndexOfFocusBlockEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "focusBlockEnabled");
          final MonitoredApp _result;
          if (_cursor.moveToFirst()) {
            final String _tmpPackageName;
            _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            final String _tmpAppName;
            _tmpAppName = _cursor.getString(_cursorIndexOfAppName);
            final int _tmpDailyLimitMinutes;
            _tmpDailyLimitMinutes = _cursor.getInt(_cursorIndexOfDailyLimitMinutes);
            final int _tmpBaseCooldownSeconds;
            _tmpBaseCooldownSeconds = _cursor.getInt(_cursorIndexOfBaseCooldownSeconds);
            final long _tmpAddedAt;
            _tmpAddedAt = _cursor.getLong(_cursorIndexOfAddedAt);
            final boolean _tmpIsEnabled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsEnabled);
            _tmpIsEnabled = _tmp != 0;
            final int _tmpWeekendLimitMinutes;
            _tmpWeekendLimitMinutes = _cursor.getInt(_cursorIndexOfWeekendLimitMinutes);
            final boolean _tmpFocusBlockEnabled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfFocusBlockEnabled);
            _tmpFocusBlockEnabled = _tmp_1 != 0;
            _result = new MonitoredApp(_tmpPackageName,_tmpAppName,_tmpDailyLimitMinutes,_tmpBaseCooldownSeconds,_tmpAddedAt,_tmpIsEnabled,_tmpWeekendLimitMinutes,_tmpFocusBlockEnabled);
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
  public Object count(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM monitored_apps";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
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
