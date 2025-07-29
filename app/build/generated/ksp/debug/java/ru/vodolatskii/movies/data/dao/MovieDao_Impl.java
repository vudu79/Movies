package ru.vodolatskii.movies.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.rxjava3.EmptyResultSetException;
import androidx.room.rxjava3.RxRoom;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import io.reactivex.rxjava3.core.Single;
import java.lang.Class;
import java.lang.Exception;
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
import ru.vodolatskii.movies.data.entity.GenreConverter;
import ru.vodolatskii.movies.data.entity.MovieEntity;
import ru.vodolatskii.movies.domain.models.Movie;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class MovieDao_Impl implements MovieDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MovieEntity> __insertionAdapterOfMovieEntity;

  private final GenreConverter __genreConverter = new GenreConverter();

  private final EntityInsertionAdapter<MovieEntity> __insertionAdapterOfMovieEntity_1;

  private final SharedSQLiteStatement __preparedStmtOfUpdateMovieToFavorite;

  private final SharedSQLiteStatement __preparedStmtOfDeleteMovie;

  public MovieDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMovieEntity = new EntityInsertionAdapter<MovieEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `movies` (`id`,`api_id`,`title`,`description`,`poster_url`,`rating`,`release_date`,`release_date_time_stump`,`release_date_year`,`is_favorite`,`genres`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MovieEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getApiId());
        statement.bindString(3, entity.getTitle());
        statement.bindString(4, entity.getDescription());
        statement.bindString(5, entity.getPosterUrl());
        statement.bindDouble(6, entity.getRating());
        statement.bindString(7, entity.getReleaseDate());
        statement.bindLong(8, entity.getReleaseDateTimeStump());
        statement.bindLong(9, entity.getReleaseDateYear());
        final int _tmp = entity.isFavorite() ? 1 : 0;
        statement.bindLong(10, _tmp);
        final String _tmp_1 = __genreConverter.fromGenres(entity.getGenres());
        statement.bindString(11, _tmp_1);
      }
    };
    this.__insertionAdapterOfMovieEntity_1 = new EntityInsertionAdapter<MovieEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `movies` (`id`,`api_id`,`title`,`description`,`poster_url`,`rating`,`release_date`,`release_date_time_stump`,`release_date_year`,`is_favorite`,`genres`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MovieEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getApiId());
        statement.bindString(3, entity.getTitle());
        statement.bindString(4, entity.getDescription());
        statement.bindString(5, entity.getPosterUrl());
        statement.bindDouble(6, entity.getRating());
        statement.bindString(7, entity.getReleaseDate());
        statement.bindLong(8, entity.getReleaseDateTimeStump());
        statement.bindLong(9, entity.getReleaseDateYear());
        final int _tmp = entity.isFavorite() ? 1 : 0;
        statement.bindLong(10, _tmp);
        final String _tmp_1 = __genreConverter.fromGenres(entity.getGenres());
        statement.bindString(11, _tmp_1);
      }
    };
    this.__preparedStmtOfUpdateMovieToFavorite = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE movies SET is_favorite = ? WHERE title = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteMovie = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM movies WHERE title = ?";
        return _query;
      }
    };
  }

  @Override
  public long insertMovie(final MovieEntity movie) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfMovieEntity.insertAndReturnId(movie);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void insertMovies(final List<MovieEntity> movies) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfMovieEntity_1.insert(movies);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void updateMovieToFavorite(final boolean isFavorite, final String title) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateMovieToFavorite.acquire();
    int _argIndex = 1;
    final int _tmp = isFavorite ? 1 : 0;
    _stmt.bindLong(_argIndex, _tmp);
    _argIndex = 2;
    _stmt.bindString(_argIndex, title);
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfUpdateMovieToFavorite.release(_stmt);
    }
  }

  @Override
  public Object deleteMovie(final String title, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteMovie.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, title);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteMovie.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Single<List<MovieEntity>> getAllMovies() {
    final String _sql = "SELECT * FROM movies";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return RxRoom.createSingle(new Callable<List<MovieEntity>>() {
      @Override
      @Nullable
      public List<MovieEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfApiId = CursorUtil.getColumnIndexOrThrow(_cursor, "api_id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfPosterUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "poster_url");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfReleaseDate = CursorUtil.getColumnIndexOrThrow(_cursor, "release_date");
          final int _cursorIndexOfReleaseDateTimeStump = CursorUtil.getColumnIndexOrThrow(_cursor, "release_date_time_stump");
          final int _cursorIndexOfReleaseDateYear = CursorUtil.getColumnIndexOrThrow(_cursor, "release_date_year");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "is_favorite");
          final int _cursorIndexOfGenres = CursorUtil.getColumnIndexOrThrow(_cursor, "genres");
          final List<MovieEntity> _result = new ArrayList<MovieEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MovieEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpApiId;
            _tmpApiId = _cursor.getLong(_cursorIndexOfApiId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpPosterUrl;
            _tmpPosterUrl = _cursor.getString(_cursorIndexOfPosterUrl);
            final double _tmpRating;
            _tmpRating = _cursor.getDouble(_cursorIndexOfRating);
            final String _tmpReleaseDate;
            _tmpReleaseDate = _cursor.getString(_cursorIndexOfReleaseDate);
            final long _tmpReleaseDateTimeStump;
            _tmpReleaseDateTimeStump = _cursor.getLong(_cursorIndexOfReleaseDateTimeStump);
            final int _tmpReleaseDateYear;
            _tmpReleaseDateYear = _cursor.getInt(_cursorIndexOfReleaseDateYear);
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final List<String> _tmpGenres;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfGenres);
            _tmpGenres = __genreConverter.toGenre(_tmp_1);
            _item = new MovieEntity(_tmpId,_tmpApiId,_tmpTitle,_tmpDescription,_tmpPosterUrl,_tmpRating,_tmpReleaseDate,_tmpReleaseDateTimeStump,_tmpReleaseDateYear,_tmpIsFavorite,_tmpGenres);
            _result.add(_item);
          }
          if (_result == null) {
            throw new EmptyResultSetException("Query returned empty result set: " + _statement.getSql());
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
  public Object getMoviesByRatingByYear(final double rating, final int year,
      final Continuation<? super List<MovieEntity>> $completion) {
    final String _sql = "SELECT * FROM movies WHERE (? = 0.0 OR rating >= ?) AND (? = 0 OR release_date_year = ?)";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 4);
    int _argIndex = 1;
    _statement.bindDouble(_argIndex, rating);
    _argIndex = 2;
    _statement.bindDouble(_argIndex, rating);
    _argIndex = 3;
    _statement.bindLong(_argIndex, year);
    _argIndex = 4;
    _statement.bindLong(_argIndex, year);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MovieEntity>>() {
      @Override
      @NonNull
      public List<MovieEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfApiId = CursorUtil.getColumnIndexOrThrow(_cursor, "api_id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfPosterUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "poster_url");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfReleaseDate = CursorUtil.getColumnIndexOrThrow(_cursor, "release_date");
          final int _cursorIndexOfReleaseDateTimeStump = CursorUtil.getColumnIndexOrThrow(_cursor, "release_date_time_stump");
          final int _cursorIndexOfReleaseDateYear = CursorUtil.getColumnIndexOrThrow(_cursor, "release_date_year");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "is_favorite");
          final int _cursorIndexOfGenres = CursorUtil.getColumnIndexOrThrow(_cursor, "genres");
          final List<MovieEntity> _result = new ArrayList<MovieEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MovieEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpApiId;
            _tmpApiId = _cursor.getLong(_cursorIndexOfApiId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpPosterUrl;
            _tmpPosterUrl = _cursor.getString(_cursorIndexOfPosterUrl);
            final double _tmpRating;
            _tmpRating = _cursor.getDouble(_cursorIndexOfRating);
            final String _tmpReleaseDate;
            _tmpReleaseDate = _cursor.getString(_cursorIndexOfReleaseDate);
            final long _tmpReleaseDateTimeStump;
            _tmpReleaseDateTimeStump = _cursor.getLong(_cursorIndexOfReleaseDateTimeStump);
            final int _tmpReleaseDateYear;
            _tmpReleaseDateYear = _cursor.getInt(_cursorIndexOfReleaseDateYear);
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final List<String> _tmpGenres;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfGenres);
            _tmpGenres = __genreConverter.toGenre(_tmp_1);
            _item = new MovieEntity(_tmpId,_tmpApiId,_tmpTitle,_tmpDescription,_tmpPosterUrl,_tmpRating,_tmpReleaseDate,_tmpReleaseDateTimeStump,_tmpReleaseDateYear,_tmpIsFavorite,_tmpGenres);
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
  public Single<List<MovieEntity>> getFavoriteMovies() {
    final String _sql = "SELECT * FROM movies WHERE is_favorite = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return RxRoom.createSingle(new Callable<List<MovieEntity>>() {
      @Override
      @Nullable
      public List<MovieEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfApiId = CursorUtil.getColumnIndexOrThrow(_cursor, "api_id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfPosterUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "poster_url");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfReleaseDate = CursorUtil.getColumnIndexOrThrow(_cursor, "release_date");
          final int _cursorIndexOfReleaseDateTimeStump = CursorUtil.getColumnIndexOrThrow(_cursor, "release_date_time_stump");
          final int _cursorIndexOfReleaseDateYear = CursorUtil.getColumnIndexOrThrow(_cursor, "release_date_year");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "is_favorite");
          final int _cursorIndexOfGenres = CursorUtil.getColumnIndexOrThrow(_cursor, "genres");
          final List<MovieEntity> _result = new ArrayList<MovieEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MovieEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpApiId;
            _tmpApiId = _cursor.getLong(_cursorIndexOfApiId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpPosterUrl;
            _tmpPosterUrl = _cursor.getString(_cursorIndexOfPosterUrl);
            final double _tmpRating;
            _tmpRating = _cursor.getDouble(_cursorIndexOfRating);
            final String _tmpReleaseDate;
            _tmpReleaseDate = _cursor.getString(_cursorIndexOfReleaseDate);
            final long _tmpReleaseDateTimeStump;
            _tmpReleaseDateTimeStump = _cursor.getLong(_cursorIndexOfReleaseDateTimeStump);
            final int _tmpReleaseDateYear;
            _tmpReleaseDateYear = _cursor.getInt(_cursorIndexOfReleaseDateYear);
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final List<String> _tmpGenres;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfGenres);
            _tmpGenres = __genreConverter.toGenre(_tmp_1);
            _item = new MovieEntity(_tmpId,_tmpApiId,_tmpTitle,_tmpDescription,_tmpPosterUrl,_tmpRating,_tmpReleaseDate,_tmpReleaseDateTimeStump,_tmpReleaseDateYear,_tmpIsFavorite,_tmpGenres);
            _result.add(_item);
          }
          if (_result == null) {
            throw new EmptyResultSetException("Query returned empty result set: " + _statement.getSql());
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
  public int getCountMovies() {
    final String _sql = "SELECT COUNT(*) FROM movies";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _result;
      if (_cursor.moveToFirst()) {
        _result = _cursor.getInt(0);
      } else {
        _result = 0;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public Object deleteMovie(final Movie movie, final Continuation<? super Unit> $completion) {
    return MovieDao.DefaultImpls.deleteMovie(MovieDao_Impl.this, movie, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
