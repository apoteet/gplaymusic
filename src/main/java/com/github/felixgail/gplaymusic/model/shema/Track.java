package com.github.felixgail.gplaymusic.model.shema;

import com.github.felixgail.gplaymusic.api.GPlayMusic;
import com.github.felixgail.gplaymusic.api.exceptions.NetworkException;
import com.github.felixgail.gplaymusic.cache.LibraryTrackCache;
import com.github.felixgail.gplaymusic.model.abstracts.Signable;
import com.github.felixgail.gplaymusic.model.enums.Provider;
import com.github.felixgail.gplaymusic.model.enums.ResultType;
import com.github.felixgail.gplaymusic.model.enums.StreamQuality;
import com.github.felixgail.gplaymusic.model.enums.SubscriptionType;
import com.github.felixgail.gplaymusic.model.interfaces.Result;
import com.github.felixgail.gplaymusic.model.requestbodies.IncrementPlaycountRequest;
import com.github.felixgail.gplaymusic.model.shema.snippets.ArtRef;
import com.github.felixgail.gplaymusic.util.language.Language;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

public class Track extends Signable implements Result, Serializable {
  public final static ResultType RESULT_TYPE = ResultType.TRACK;
  private static Gson gsonPrettyPrinter = new GsonBuilder().setPrettyPrinting().create();
  private static LibraryTrackCache libraryTrackCache = new LibraryTrackCache();
  private final static String UNKNOWN_VALUE = "UNKNOWN";

  //TODO: Not all Attributes added (eg. PrimaryVideo, ID? where is id used).
  @Expose
  private String title;
  @Expose
  private String artist;
  @Expose
  private String composer;
  @Expose
  private String album;
  @Expose
  private String albumArtist;
  @Expose
  private int year;
  @Expose
  private int trackNumber;
  @Expose
  private String genre;
  @Expose
  private String durationMillis;
  @Expose
  private List<ArtRef> albumArtRef;
  @Expose
  private List<ArtRef> artistArtRef;
  @Expose
  private int discNumber;
  @Expose
  private String estimatedSize;
  @Expose
  private String trackType;
  @Expose
  private String storeId;
  @Expose
  private String albumId;
  @Expose
  private List<String> artistId;
  @Expose
  private String nid;
  @Expose
  private boolean trackAvailableForSubscription;
  @Expose
  private boolean trackAvailableForPurchase;
  @Expose
  private boolean albumAvailableForPurchase;
  @Expose
  private String explicitType;
  @Expose
  private int playCount;
  @Expose
  private String rating;
  @Expose
  private int beatsPerMinute;
  @Expose
  private String clientId;
  @Expose
  private String comment;
  @Expose
  private int totalTrackCount;
  @Expose
  private int totalDiscCount;
  @Expose
  private String lastRatingChangeTimestamp;
  @Expose
  private String lastModifiedTimestamp;
  @Expose
  private String contentType;
  @Expose
  private String creationTimestamp;
  @Expose
  private String recentTimestamp;
  @Expose
  @SerializedName("id")
  private String uuid;

  private String sessionToken;

  //This attribute is only set when the track is retrieved from a station.
  @Expose
  @SerializedName("wentryid")
  private String wentryID;

  public Track(@NotNull String id, @NotNull String title, @NotNull String artist, @NotNull String album,
               int trackNumber, long durationMillis, int discNumber, long estimatedSize, @NotNull String albumId,
               @NotNull String contentType) {
    this.title = title;
    this.artist = artist;
    this.album = album;
    this.albumArtist = "";
    this.durationMillis = String.valueOf(durationMillis);
    this.trackNumber = trackNumber;
    this.discNumber = discNumber;
    this.estimatedSize = String.valueOf(estimatedSize);
    this.albumId = albumId;
    this.storeId = id;
    this.contentType = contentType;
  }


  public Track(@NotNull String id) {
    this(id, UNKNOWN_VALUE, UNKNOWN_VALUE, UNKNOWN_VALUE, 0, 0, 0,
        0, UNKNOWN_VALUE, UNKNOWN_VALUE);
  }

  public static Track getTrack(String trackID) throws IOException {
    Track track = null;
    if (trackID.startsWith("T")) {
      track = GPlayMusic.getApiInstance().getService().fetchTrack(trackID).execute().body();
    } else {
      track = libraryTrackCache.find(trackID).orElseThrow(() ->
          new IllegalArgumentException(String.format("No track with id '%s' found.", trackID)));
    }
    if (track == null || track.getID() == null) {
      throw new IOException(String.format("'%s' did not return a valid track", trackID));
    }
    return track;
  }

  public String getTitle() {
    return title;
  }

  public String getArtist() {
    return artist;
  }

  public String getComposer() {
    return composer;
  }

  public String getAlbum() {
    return album;
  }

  public String getAlbumArtist() {
    return albumArtist;
  }

  public OptionalInt getYear() {
    return OptionalInt.of(year);
  }

  public int getTrackNumber() {
    return trackNumber;
  }

  //TODO: Return genre instead of string
  public Optional<String> getGenre() {
    return Optional.ofNullable(genre);
  }

  public Long getDurationMillis() {
    return Long.parseLong(durationMillis);
  }

  public Optional<List<ArtRef>> getAlbumArtRef() {
    return Optional.ofNullable(albumArtRef);
  }

  public Optional<List<ArtRef>> getArtistArtRef() {
    return Optional.ofNullable(artistArtRef);
  }

  public int getDiscNumber() {
    return discNumber;
  }

  public long getEstimatedSize() {
    return Long.parseLong(estimatedSize);
  }

  public Optional<String> getTrackType() {
    return Optional.ofNullable(trackType);
  }

  /**
   * Returns how often the song has been played. Not valid, when song has been fetched via
   * {@link Track#getTrack(String)} as the server response does not contain this key.
   */
  public OptionalInt getPlayCount() {
    return OptionalInt.of(playCount);
  }

  public Optional<String> getRating() {
    return Optional.ofNullable(rating);
  }

  public OptionalInt getBeatsPerMinute() {
    return OptionalInt.of(beatsPerMinute);
  }

  public Optional<String> getClientId() {
    return Optional.ofNullable(clientId);
  }

  public Optional<String> getComment() {
    return Optional.ofNullable(comment);
  }

  @Override
  public String getID() {
    return getStoreId().orElseGet(
        () -> getUuid().orElseThrow(
            () -> new NullPointerException("Track contains neither StoreID nor UUID.")));
  }

  public Optional<String> getStoreId() {
    return Optional.ofNullable(storeId);
  }

  public String getAlbumId() {
    return albumId;
  }

  public Optional<List<String>> getArtistId() {
    return Optional.ofNullable(artistId);
  }

  public Optional<String> getNid() {
    return Optional.ofNullable(nid);
  }

  public boolean isTrackAvailableForSubscription() {
    return trackAvailableForSubscription;
  }


  public boolean isTrackAvailableForPurchase() {
    return trackAvailableForPurchase;
  }

  public boolean isAlbumAvailableForPurchase() {
    return albumAvailableForPurchase;
  }

  public Optional<String> getExplicitType() {
    return Optional.ofNullable(explicitType);
  }

  public String getWentryID() {
    return wentryID;
  }

  public OptionalInt getTotalTrackCount() {
    return OptionalInt.of(totalTrackCount);
  }

  public OptionalInt getTotalDiscCount() {
    return OptionalInt.of(totalDiscCount);
  }

  public Optional<String> getLastRatingChangeTimestamp() {
    return Optional.ofNullable(lastRatingChangeTimestamp);
  }

  public Optional<String> getLastModifiedTimestamp() {
    return Optional.ofNullable(lastModifiedTimestamp);
  }

  public Optional<String> getContentType() {
    return Optional.ofNullable(contentType);
  }

  public Optional<String> getCreationTimestamp() {
    return Optional.ofNullable(creationTimestamp);
  }

  public Optional<String> getRecentTimestamp() {
    return Optional.ofNullable(recentTimestamp);
  }

  public Optional<String> getUuid() {
    return Optional.ofNullable(uuid);
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof Track) && ((Track) o).getID().equals(this.getID());
  }

  @Override
  public Signature getSignature() {
    return super.createSignature(this.getID());
  }

  @Override
  public ResultType getResultType() {
    return RESULT_TYPE;
  }

  /**
   * Returns a URL to download the song in set quality.
   * URL will only be valid for 1 minute.
   * You will likely need to handle redirects.
   * <br>
   * <b>Please note that this function is available for Subscribers only.
   * On free accounts use {@link #getStationTrackURL(StreamQuality)}.</b>
   *
   * @param quality quality of the stream
   * @return temporary url to the title
   * @throws IOException Throws an IOException on severe failures (no internet connection...)
   *                     or a {@link NetworkException} on request failures.
   */
  @Override
  public String getStreamURL(StreamQuality quality)
      throws IOException {
    if (GPlayMusic.getApiInstance().getConfig().getSubscription() == SubscriptionType.FREE) {
      throw new IOException(Language.get("users.free.NotAllowed"));
    }
    return urlFetcher(quality, Provider.STREAM, EMPTY_MAP);
  }

  /**
   * Fetch the URL for a track from a free Station.
   * Make sure this Track was returned by a {@link Station}. Otherwise an {@link IOException} will be thrown.
   * <br>
   * <b>Subscribers will be redirected to {@link #getStreamURL(StreamQuality)}</b>
   *
   * @param quality - quality of the stream
   * @return a url to download songs from.
   * @throws IOException on severe failures (no internet connection...)
   *                     or a {@link NetworkException} on request failures.
   */
  public String getStationTrackURL(StreamQuality quality)
      throws IOException {
    if (GPlayMusic.getApiInstance().getConfig().getSubscription() == SubscriptionType.ALL_ACCESS) {
      return getStreamURL(quality);
    }
    if (getWentryID() == null || getWentryID().isEmpty()) {
      throw new IOException(Language.get("track.InvalidWentryID"));
    }
    if (!getSessionToken().isPresent()) {
      throw new IOException(Language.get("station.InvalidSessionToken"));
    }
    Map<String, String> map = new HashMap<>();
    map.putAll(STATION_MAP);
    map.put("wentryid", getWentryID());
    map.put("sesstok", getSessionToken().get());
    return urlFetcher(quality, Provider.STATION, map);
  }

  /**
   * Increments the playcount of this song by {@code count}.
   *
   * @param count amount of plays that will be added to the current count.
   * @return whether the incrementation was successful.
   */
  public boolean incrementPlaycount(int count) throws IOException {
    MutationResponse response = GPlayMusic.getApiInstance().getService().incremetPlaycount(
        new IncrementPlaycountRequest(count, this)).execute().body();
    if (response.checkSuccess()) {
      playCount += count;
      return true;
    }
    return false;
  }

  public String string() {
    return gsonPrettyPrinter.toJson(this);
  }

  /**
   * Downloads the song to the provided path. Existing files will be replaced.
   */
  public void download(StreamQuality quality, Path path) throws IOException {
    URL url = new URL(getStationTrackURL(quality));
    Files.copy(url.openStream(), path, StandardCopyOption.REPLACE_EXISTING);
  }

  /**
   * Library tracks can only be fetched as whole. To shorten wait times, collected songs are cached.
   * Please consider updating the cache (asynchronously) when using the library over a long period of time, or when
   * new songs could be added to the library during runtime.
   * <br>
   * If outside access to the library is expected during runtime, disabling caching via {@link #useCache(boolean)}
   * should also be considered.
   */
  public static void updateCache() throws IOException {
    libraryTrackCache.update();
  }

  /**
   * Enables/Disables caching of library tracks.
   */
  public static void useCache(boolean useCache) {
    libraryTrackCache.setUseCache(useCache);
  }

  void setSessionToken(String token) {
    this.sessionToken = token;
  }

  private Optional<String> getSessionToken() {
    return Optional.ofNullable(sessionToken);
  }
}
