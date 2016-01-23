package vn.hoangphan.karafind.models;

/**
 * Created by eastagile-tc on 1/12/16.
 */
public class Song {
    private String name;
    private String lyric;
    private String author;
    private String id;
    private String utf;
    private String stype;

    private boolean favorited;

    private int vol;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLyric() {
        return lyric;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getVol() {
        return vol;
    }

    public void setVol(int vol) {
        this.vol = vol;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public String getUtf() {
        return utf;
    }

    public void setUtf(String utf) {
        this.utf = utf;
    }

    public String getStype() {
        return stype;
    }

    public void setStype(String stype) {
        this.stype = stype;
    }
}
