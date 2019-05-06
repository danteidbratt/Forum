package se.donut.postservice;

import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.jdbi3.bundles.JdbiExceptionsBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.jdbi.v3.core.Jdbi;
import se.donut.postservice.auth.AuthenticatedUser;
import se.donut.postservice.auth.UserAuthenticator;
import se.donut.postservice.auth.UserAuthorizer;
import se.donut.postservice.model.mapper.*;
import se.donut.postservice.repository.*;
import se.donut.postservice.repository.postgresql.*;
import se.donut.postservice.resource.*;
import se.donut.postservice.service.*;

public class App extends Application<AppConfig> {

    public static void main(String[] args) throws Exception {
        App app = new App();
        app.run(args);
    }

    @Override
    public String getName() {
        return "postservice";
    }

    @Override
    public void run(AppConfig appConfig, Environment environment) throws Exception {
        JdbiFactory jdbiFactory = new JdbiFactory();
        Jdbi jdbi = jdbiFactory.build(environment, appConfig.getDatabase(), "postgresql");
        jdbi.registerRowMapper(new UserMapper());
        jdbi.registerRowMapper(new ForumMapper());
        jdbi.registerRowMapper(new PostMapper());
        jdbi.registerRowMapper(new CommentMapper());
        jdbi.registerRowMapper(new VoteMapper());
        jdbi.registerRowMapper(new SubscriptionMapper());

        CommentAccessor commentAccessor = new PostgresCommentDAO(jdbi);
        PostAccessor postAccessor = new PostgresPostDAO(jdbi);
        CommentDAO commentDAO = jdbi.onDemand(CommentDAO.class);
        PostDAO postDAO = jdbi.onDemand(PostDAO.class);
        UserDAO userDAO = jdbi.onDemand(UserDAO.class);
        ForumDAO forumDAO = jdbi.onDemand(ForumDAO.class);
        SubscriptionDAO subscriptionDAO = jdbi.onDemand(SubscriptionDAO.class);

        UserService userService = new UserService(userDAO);
        ForumService forumService = new ForumService(userDAO, forumDAO, subscriptionDAO);
        CommentService commentService = new CommentService(commentDAO, postDAO, userDAO);
        PostService postService = new PostService(userDAO, postDAO, forumDAO);

        UserResource userResource = new UserResource(userService);
        PostResource postResource = new PostResource(postService);
        ForumGuestResource forumResource = new ForumGuestResource(forumService);
        CommentResource commentResource = new CommentResource(commentService);

        environment.jersey().register(new AuthDynamicFeature(
                new BasicCredentialAuthFilter.Builder<AuthenticatedUser>()
                        .setAuthenticator(new UserAuthenticator(userService))
                        .setAuthorizer(new UserAuthorizer())
                        .setRealm("SUPER SECRET STUFF")
                        .buildAuthFilter()));
        environment.jersey().register(RolesAllowedDynamicFeature.class);
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(AuthenticatedUser.class));

        environment.jersey().register(new AuthResource(userService));
        environment.jersey().register(userResource);
        environment.jersey().register(postResource);
        environment.jersey().register(forumResource);
        environment.jersey().register(commentResource);
        environment.jersey().register(JdbiExceptionsBundle.class);
    }

    @Override
    public void initialize(Bootstrap<AppConfig> bootstrap) {
        bootstrap.addBundle(new MigrationsBundle<AppConfig>() {
            public PooledDataSourceFactory getDataSourceFactory(AppConfig appConfig) {
                return appConfig.getDatabase();
            }
        });
    }
}
