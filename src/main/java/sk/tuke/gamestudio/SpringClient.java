package sk.tuke.gamestudio;

import org.springframework.beans.factory.annotation.Autowired;
import sk.tuke.gamestudio.game.consoleui.ConsoleUI;
import sk.tuke.gamestudio.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import java.io.IOException;

@SpringBootApplication
@Configuration
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX,
        pattern = "sk.tuke.gamestudio.game.server.*"))
public class SpringClient {
    public static void main(String[] args) throws IOException {
        new SpringApplicationBuilder(SpringClient.class).web(WebApplicationType.NONE).run(args);
    }

    @Autowired
    @Bean
    public CommandLineRunner runner(ScoreService scoreService, CommentService commentService, RatingService ratingService) {
//        return args -> ui.play();
        return args -> new ConsoleUI(scoreService, commentService, ratingService).play();
    }

    @Bean
    public ScoreService scoreService() {
        return new ScoreServiceRestClient();
    }

    @Bean
    public CommentService commentService() {
        return new CommentServiceRestClient();
    }

    @Bean
    public RatingService ratingService() {
        return new RatingServiceRestClient();
    }
}