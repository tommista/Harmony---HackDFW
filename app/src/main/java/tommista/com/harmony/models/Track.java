package tommista.com.harmony.models;

/**
 * Created by tbrown on 2/28/15.
 */
public class Track {

    public Track(String title, String artist){
        this.title = title;
        this.artist = artist;
        this.imageURL = "http://imgs.xkcd.com/comics/dress_color.png";
    }

    public String title;
    public String artist;
    public String imageURL;

}
