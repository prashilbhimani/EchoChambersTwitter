package communitydetection.restapi;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TweetController {

    private static final String template = "My tweet:, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/greeting")
    public TweetClass tweet(@RequestParam(value="tweetText", defaultValue="I am eating at McDonald's :)") String tweet) {
        return new TweetClass(counter.incrementAndGet(),
                            String.format(template, tweet));
    }
}


//http://localhost:8080/greeting?tweetText='I am eating at Subway'  -to pass tweettext as input. We can use an orm for database queries.