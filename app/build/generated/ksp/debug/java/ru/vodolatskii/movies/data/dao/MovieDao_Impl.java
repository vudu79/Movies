package ru.vodolatskii.movies.data.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.collection.LongSparseArray;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomDatabaseKt;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.RelationUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import ru.vodolatskii.movies.data.entity.Genre;
import ru.vodolatskii.movies.data.entity.MovieWithGenre;
import ru.vodolatskii.movies.data.entity.MovieWithoutGenre;
import ru.vodolatskii.movies.domain.models.Movie;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class MovieDao_Impl implements MovieDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MovieWithoutGenre> __insertionAdapterOfMovieWithoutGenre;

  private final EntityInsertionAdapter<Genre> __insertionAdapterOfGenre;

  private final SharedSQLiteStatement __preparedStmtOfDeleteMovieWithoutGenre;

  public MovieDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMovieWithoutGenre = new EntityInsertionAdapter<MovieWithoutGenre>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `movies` (`id`,`api_id`,`title`,`description`,`poster_url`,`rating`,`release_date`,`release_date_times_tump`,`is_favorite`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MovieWithoutGenre entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getApiId());
        statement.bindString(3, entity.getTitle());
        statement.bindString(4, entity.getDescription());
        statement.bindString(5, entity.getPosterUrl());
        statement.bindDouble(6, entity.getRating());
        statement.bindString(7, entity.getReleaseDate());
        statement.bindLong(8, entity.getReleaseDateTimeStump());
        final int _tmp = entity.isFavorite() ? 1 : 0;
        statement.bindLong(9, _tmp);
      }
    };
    this.__insertionAdapterOfGenre = new EntityInsertionAdapter<Genre>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `Genre` (`idGenre`,`id_genre_fk`,`genre`) VALUES (nullif(?, 0),?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Genre entity) {
        statement.bindLong(1, entity.getIdGenre());
        statement.bindLong(2, entity.getIdGenreFK());
        statement.bindLong(3, entity.getGenre());
      }
    };
    this.__preparedStmtOfDeleteMovieWithoutGenre = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM movies WHERE title = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertMovieWithoutGenre(final MovieWithoutGenre movie,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfMovieWithoutGenre.insertAndReturnId(movie);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertGenres(final List<Genre> genre,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfGenre.insert(genre);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertMovie(final Movie movie,
                            final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> MovieDao.DefaultImpls.insertMovie(MovieDao_Impl.this, movie, __cont), $completion);
  }

  @Override
  public Object insertMovies(final List<Movie> movies,
                             final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> MovieDao.DefaultImpls.insertMovies(MovieDao_Impl.this, movies, __cont), $completion);
  }

  @Override
  public Object deleteMovie(final Movie movie,
                            final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> MovieDao.DefaultImpls.deleteMovie(MovieDao_Impl.this, movie, __cont), $completion);
  }

  @Override
  public Object deleteMovieWithoutGenre(final String title,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteMovieWithoutGenre.acquire();
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
          __preparedStmtOfDeleteMovieWithoutGenre.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public List<MovieWithGenre> getFavoriteMovies() {
    final String _sql = "SELECT * FROM movies";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfApiId = CursorUtil.getColumnIndexOrThrow(_cursor, "api_id");
      final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
      final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
      final int _cursorIndexOfPosterUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "poster_url");
      final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
      final int _cursorIndexOfReleaseDate = CursorUtil.getColumnIndexOrThrow(_cursor, "release_date");
      final int _cursorIndexOfReleaseDateTimeStump = CursorUtil.getColumnIndexOrThrow(_cursor, "release_date_times_tump");
      final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "is_favorite");
      final LongSparseArray<ArrayList<Genre>> _collectionGenreList = new LongSparseArray<ArrayList<Genre>>();
      while (_cursor.moveToNext()) {
        final long _tmpKey;
        _tmpKey = _cursor.getLong(_cursorIndexOfId);
        if (!_collectionGenreList.containsKey(_tmpKey)) {
          _collectionGenreList.put(_tmpKey, new ArrayList<Genre>());
        }
      }
      _cursor.moveToPosition(-1);
      __fetchRelationshipGenreAsruVodolatskiiMoviesDataEntityGenre(_collectionGenreList);
      final List<MovieWithGenre> _result = new ArrayList<MovieWithGenre>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final MovieWithGenre _item;
        final MovieWithoutGenre _tmpMovie;
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
        final boolean _tmpIsFavorite;
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
        _tmpIsFavorite = _tmp != 0;
        _tmpMovie = new MovieWithoutGenre(_tmpId,_tmpApiId,_tmpTitle,_tmpDescription,_tmpPosterUrl,_tmpRating,_tmpReleaseDate,_tmpReleaseDateTimeStump,_tmpIsFavorite);
        final ArrayList<Genre> _tmpGenreListCollection;
        final long _tmpKey_1;
        _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
        _tmpGenreListCollection = _collectionGenreList.get(_tmpKey_1);
        _item = new MovieWithGenre(_tmpMovie,_tmpGenreListCollection);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private void __fetchRelationshipGenreAsruVodolatskiiMoviesDataEntityGenre(
      @NonNull final LongSparseArray<ArrayList<Genre>> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      RelationUtil.recursiveFetchLongSparseArray(_map, true, (map) -> {
        __fetchRelationshipGenreAsruVodolatskiiMoviesDataEntityGenre(map);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `idGenre`,`id_genre_fk`,`genre` FROM `Genre` WHERE `idGenre` IN (");
    final int _inputSize = _map.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _stmt = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (int i = 0; i < _map.size(); i++) {
      final long _item = _map.keyAt(i);
      _stmt.bindLong(_argIndex, _item);
      _argIndex++;
    }
    final Cursor _cursor = DBUtil.query(__db, _stmt, false, null);
    try {
      final int _itemKeyIndex = CursorUtil.getColumnIndex(_cursor, "idGenre");
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _cursorIndexOfIdGenre = 0;
      final int _cursorIndexOfIdGenreFK = 1;
      final int _cursorIndexOfGenre = 2;
      while (_cursor.moveToNext()) {
        final long _tmpKey;
        _tmpKey = _cursor.getLong(_itemKeyIndex);
        final ArrayList<Genre> _tmpRelation = _map.get(_tmpKey);
        if (_tmpRelation != null) {
          final Genre _item_1;
          final long _tmpIdGenre;
          _tmpIdGenre = _cursor.getLong(_cursorIndexOfIdGenre);
          final long _tmpIdGenreFK;
          _tmpIdGenreFK = _cursor.getLong(_cursorIndexOfIdGenreFK);
          final int _tmpGenre;
          _tmpGenre = _cursor.getInt(_cursorIndexOfGenre);
          _item_1 = new Genre(_tmpIdGenre,_tmpIdGenreFK,_tmpGenre);
          _tmpRelation.add(_item_1);
        }
      }
    } finally {
      _cursor.close();
    }
  }
}
