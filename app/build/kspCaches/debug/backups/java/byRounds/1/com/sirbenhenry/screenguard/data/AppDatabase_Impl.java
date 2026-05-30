package com.sirbenhenry.screenguard.data;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.sirbenhenry.screenguard.data.dao.CooldownSessionDao;
import com.sirbenhenry.screenguard.data.dao.CooldownSessionDao_Impl;
import com.sirbenhenry.screenguard.data.dao.DailyOpenCountDao;
import com.sirbenhenry.screenguard.data.dao.DailyOpenCountDao_Impl;
import com.sirbenhenry.screenguard.data.dao.GoodAppDao;
import com.sirbenhenry.screenguard.data.dao.GoodAppDao_Impl;
import com.sirbenhenry.screenguard.data.dao.MonitoredAppDao;
import com.sirbenhenry.screenguard.data.dao.MonitoredAppDao_Impl;
import com.sirbenhenry.screenguard.data.dao.StreakRecordDao;
import com.sirbenhenry.screenguard.data.dao.StreakRecordDao_Impl;
import com.sirbenhenry.screenguard.data.dao.UsageRecordDao;
import com.sirbenhenry.screenguard.data.dao.UsageRecordDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile MonitoredAppDao _monitoredAppDao;

  private volatile GoodAppDao _goodAppDao;

  private volatile UsageRecordDao _usageRecordDao;

  private volatile StreakRecordDao _streakRecordDao;

  private volatile CooldownSessionDao _cooldownSessionDao;

  private volatile DailyOpenCountDao _dailyOpenCountDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `monitored_apps` (`packageName` TEXT NOT NULL, `appName` TEXT NOT NULL, `dailyLimitMinutes` INTEGER NOT NULL, `baseCooldownSeconds` INTEGER NOT NULL, `addedAt` INTEGER NOT NULL, `isEnabled` INTEGER NOT NULL, `weekendLimitMinutes` INTEGER NOT NULL, `focusBlockEnabled` INTEGER NOT NULL, PRIMARY KEY(`packageName`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `good_apps` (`packageName` TEXT NOT NULL, `appName` TEXT NOT NULL, `sortOrder` INTEGER NOT NULL, PRIMARY KEY(`packageName`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `usage_records` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `packageName` TEXT NOT NULL, `dateKey` TEXT NOT NULL, `totalMinutes` INTEGER NOT NULL, `sessions` INTEGER NOT NULL, `limitMinutes` INTEGER NOT NULL, `underLimit` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `streak_records` (`dateKey` TEXT NOT NULL, `allUnderLimit` INTEGER NOT NULL, `totalMinutesUsed` INTEGER NOT NULL, `totalLimitMinutes` INTEGER NOT NULL, PRIMARY KEY(`dateKey`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `cooldown_sessions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `packageName` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `completedFully` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `daily_open_counts` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `packageName` TEXT NOT NULL, `dateKey` TEXT NOT NULL, `openCount` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'e61582120605accdd485f2f58c2866ac')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `monitored_apps`");
        db.execSQL("DROP TABLE IF EXISTS `good_apps`");
        db.execSQL("DROP TABLE IF EXISTS `usage_records`");
        db.execSQL("DROP TABLE IF EXISTS `streak_records`");
        db.execSQL("DROP TABLE IF EXISTS `cooldown_sessions`");
        db.execSQL("DROP TABLE IF EXISTS `daily_open_counts`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsMonitoredApps = new HashMap<String, TableInfo.Column>(8);
        _columnsMonitoredApps.put("packageName", new TableInfo.Column("packageName", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMonitoredApps.put("appName", new TableInfo.Column("appName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMonitoredApps.put("dailyLimitMinutes", new TableInfo.Column("dailyLimitMinutes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMonitoredApps.put("baseCooldownSeconds", new TableInfo.Column("baseCooldownSeconds", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMonitoredApps.put("addedAt", new TableInfo.Column("addedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMonitoredApps.put("isEnabled", new TableInfo.Column("isEnabled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMonitoredApps.put("weekendLimitMinutes", new TableInfo.Column("weekendLimitMinutes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMonitoredApps.put("focusBlockEnabled", new TableInfo.Column("focusBlockEnabled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMonitoredApps = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMonitoredApps = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoMonitoredApps = new TableInfo("monitored_apps", _columnsMonitoredApps, _foreignKeysMonitoredApps, _indicesMonitoredApps);
        final TableInfo _existingMonitoredApps = TableInfo.read(db, "monitored_apps");
        if (!_infoMonitoredApps.equals(_existingMonitoredApps)) {
          return new RoomOpenHelper.ValidationResult(false, "monitored_apps(com.sirbenhenry.screenguard.data.entity.MonitoredApp).\n"
                  + " Expected:\n" + _infoMonitoredApps + "\n"
                  + " Found:\n" + _existingMonitoredApps);
        }
        final HashMap<String, TableInfo.Column> _columnsGoodApps = new HashMap<String, TableInfo.Column>(3);
        _columnsGoodApps.put("packageName", new TableInfo.Column("packageName", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGoodApps.put("appName", new TableInfo.Column("appName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGoodApps.put("sortOrder", new TableInfo.Column("sortOrder", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysGoodApps = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesGoodApps = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoGoodApps = new TableInfo("good_apps", _columnsGoodApps, _foreignKeysGoodApps, _indicesGoodApps);
        final TableInfo _existingGoodApps = TableInfo.read(db, "good_apps");
        if (!_infoGoodApps.equals(_existingGoodApps)) {
          return new RoomOpenHelper.ValidationResult(false, "good_apps(com.sirbenhenry.screenguard.data.entity.GoodApp).\n"
                  + " Expected:\n" + _infoGoodApps + "\n"
                  + " Found:\n" + _existingGoodApps);
        }
        final HashMap<String, TableInfo.Column> _columnsUsageRecords = new HashMap<String, TableInfo.Column>(7);
        _columnsUsageRecords.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsageRecords.put("packageName", new TableInfo.Column("packageName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsageRecords.put("dateKey", new TableInfo.Column("dateKey", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsageRecords.put("totalMinutes", new TableInfo.Column("totalMinutes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsageRecords.put("sessions", new TableInfo.Column("sessions", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsageRecords.put("limitMinutes", new TableInfo.Column("limitMinutes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsageRecords.put("underLimit", new TableInfo.Column("underLimit", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUsageRecords = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUsageRecords = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUsageRecords = new TableInfo("usage_records", _columnsUsageRecords, _foreignKeysUsageRecords, _indicesUsageRecords);
        final TableInfo _existingUsageRecords = TableInfo.read(db, "usage_records");
        if (!_infoUsageRecords.equals(_existingUsageRecords)) {
          return new RoomOpenHelper.ValidationResult(false, "usage_records(com.sirbenhenry.screenguard.data.entity.UsageRecord).\n"
                  + " Expected:\n" + _infoUsageRecords + "\n"
                  + " Found:\n" + _existingUsageRecords);
        }
        final HashMap<String, TableInfo.Column> _columnsStreakRecords = new HashMap<String, TableInfo.Column>(4);
        _columnsStreakRecords.put("dateKey", new TableInfo.Column("dateKey", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStreakRecords.put("allUnderLimit", new TableInfo.Column("allUnderLimit", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStreakRecords.put("totalMinutesUsed", new TableInfo.Column("totalMinutesUsed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStreakRecords.put("totalLimitMinutes", new TableInfo.Column("totalLimitMinutes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysStreakRecords = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesStreakRecords = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoStreakRecords = new TableInfo("streak_records", _columnsStreakRecords, _foreignKeysStreakRecords, _indicesStreakRecords);
        final TableInfo _existingStreakRecords = TableInfo.read(db, "streak_records");
        if (!_infoStreakRecords.equals(_existingStreakRecords)) {
          return new RoomOpenHelper.ValidationResult(false, "streak_records(com.sirbenhenry.screenguard.data.entity.StreakRecord).\n"
                  + " Expected:\n" + _infoStreakRecords + "\n"
                  + " Found:\n" + _existingStreakRecords);
        }
        final HashMap<String, TableInfo.Column> _columnsCooldownSessions = new HashMap<String, TableInfo.Column>(4);
        _columnsCooldownSessions.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCooldownSessions.put("packageName", new TableInfo.Column("packageName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCooldownSessions.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCooldownSessions.put("completedFully", new TableInfo.Column("completedFully", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCooldownSessions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCooldownSessions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCooldownSessions = new TableInfo("cooldown_sessions", _columnsCooldownSessions, _foreignKeysCooldownSessions, _indicesCooldownSessions);
        final TableInfo _existingCooldownSessions = TableInfo.read(db, "cooldown_sessions");
        if (!_infoCooldownSessions.equals(_existingCooldownSessions)) {
          return new RoomOpenHelper.ValidationResult(false, "cooldown_sessions(com.sirbenhenry.screenguard.data.entity.CooldownSession).\n"
                  + " Expected:\n" + _infoCooldownSessions + "\n"
                  + " Found:\n" + _existingCooldownSessions);
        }
        final HashMap<String, TableInfo.Column> _columnsDailyOpenCounts = new HashMap<String, TableInfo.Column>(4);
        _columnsDailyOpenCounts.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyOpenCounts.put("packageName", new TableInfo.Column("packageName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyOpenCounts.put("dateKey", new TableInfo.Column("dateKey", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyOpenCounts.put("openCount", new TableInfo.Column("openCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDailyOpenCounts = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDailyOpenCounts = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDailyOpenCounts = new TableInfo("daily_open_counts", _columnsDailyOpenCounts, _foreignKeysDailyOpenCounts, _indicesDailyOpenCounts);
        final TableInfo _existingDailyOpenCounts = TableInfo.read(db, "daily_open_counts");
        if (!_infoDailyOpenCounts.equals(_existingDailyOpenCounts)) {
          return new RoomOpenHelper.ValidationResult(false, "daily_open_counts(com.sirbenhenry.screenguard.data.entity.DailyOpenCount).\n"
                  + " Expected:\n" + _infoDailyOpenCounts + "\n"
                  + " Found:\n" + _existingDailyOpenCounts);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "e61582120605accdd485f2f58c2866ac", "874ead61c7ba7282446657d578169ffd");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "monitored_apps","good_apps","usage_records","streak_records","cooldown_sessions","daily_open_counts");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `monitored_apps`");
      _db.execSQL("DELETE FROM `good_apps`");
      _db.execSQL("DELETE FROM `usage_records`");
      _db.execSQL("DELETE FROM `streak_records`");
      _db.execSQL("DELETE FROM `cooldown_sessions`");
      _db.execSQL("DELETE FROM `daily_open_counts`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(MonitoredAppDao.class, MonitoredAppDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(GoodAppDao.class, GoodAppDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(UsageRecordDao.class, UsageRecordDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(StreakRecordDao.class, StreakRecordDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CooldownSessionDao.class, CooldownSessionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(DailyOpenCountDao.class, DailyOpenCountDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public MonitoredAppDao monitoredAppDao() {
    if (_monitoredAppDao != null) {
      return _monitoredAppDao;
    } else {
      synchronized(this) {
        if(_monitoredAppDao == null) {
          _monitoredAppDao = new MonitoredAppDao_Impl(this);
        }
        return _monitoredAppDao;
      }
    }
  }

  @Override
  public GoodAppDao goodAppDao() {
    if (_goodAppDao != null) {
      return _goodAppDao;
    } else {
      synchronized(this) {
        if(_goodAppDao == null) {
          _goodAppDao = new GoodAppDao_Impl(this);
        }
        return _goodAppDao;
      }
    }
  }

  @Override
  public UsageRecordDao usageRecordDao() {
    if (_usageRecordDao != null) {
      return _usageRecordDao;
    } else {
      synchronized(this) {
        if(_usageRecordDao == null) {
          _usageRecordDao = new UsageRecordDao_Impl(this);
        }
        return _usageRecordDao;
      }
    }
  }

  @Override
  public StreakRecordDao streakRecordDao() {
    if (_streakRecordDao != null) {
      return _streakRecordDao;
    } else {
      synchronized(this) {
        if(_streakRecordDao == null) {
          _streakRecordDao = new StreakRecordDao_Impl(this);
        }
        return _streakRecordDao;
      }
    }
  }

  @Override
  public CooldownSessionDao cooldownSessionDao() {
    if (_cooldownSessionDao != null) {
      return _cooldownSessionDao;
    } else {
      synchronized(this) {
        if(_cooldownSessionDao == null) {
          _cooldownSessionDao = new CooldownSessionDao_Impl(this);
        }
        return _cooldownSessionDao;
      }
    }
  }

  @Override
  public DailyOpenCountDao dailyOpenCountDao() {
    if (_dailyOpenCountDao != null) {
      return _dailyOpenCountDao;
    } else {
      synchronized(this) {
        if(_dailyOpenCountDao == null) {
          _dailyOpenCountDao = new DailyOpenCountDao_Impl(this);
        }
        return _dailyOpenCountDao;
      }
    }
  }
}
