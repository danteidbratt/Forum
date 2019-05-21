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
import se.donut.postservice.filter.CorsFilter;
import se.donut.postservice.model.mapper.*;
import se.donut.postservice.repository.postgresql.*;
import se.donut.postservice.resource.CommentResource;
import se.donut.postservice.resource.ForumResource;
import se.donut.postservice.resource.PostResource;
import se.donut.postservice.resource.UserResource;
import se.donut.postservice.service.CommentService;
import se.donut.postservice.service.ForumService;
import se.donut.postservice.service.PostService;
import se.donut.postservice.service.UserService;

public class App extends Application<Config> {

    public static void main(String[] args) throws Exception {
        App app = new App();
        app.run(args);
    }

    @Override
    public String getName() {
        return "postservice";
    }

    @Override
    public void run(Config config, Environment environment) throws Exception {
        JdbiFactory jdbiFactory = new JdbiFactory();
        Jdbi jdbi = jdbiFactory.build(environment, config.getDatabase(), "postgresql");

        jdbi.registerRowMapper(new UserMapper());
        jdbi.registerRowMapper(new ForumMapper());
        jdbi.registerRowMapper(new PostMapper());
        jdbi.registerRowMapper(new CommentMapper());
        jdbi.registerRowMapper(new VoteMapper());
        jdbi.registerRowMapper(new SubscriptionMapper());
        jdbi.registerRowMapper(new VaultEntryMapper());

        CommentDAO commentDAO = jdbi.onDemand(CommentDAO.class);
        PostDAO postDAO = jdbi.onDemand(PostDAO.class);
        UserDAO userDAO = jdbi.onDemand(UserDAO.class);
        ForumDAO forumDAO = jdbi.onDemand(ForumDAO.class);
        VaultDAO vaultDAO = jdbi.onDemand(VaultDAO.class);

        UserService userService = new UserService(userDAO);
        ForumService forumService = new ForumService(userDAO, forumDAO);
        CommentService commentService = new CommentService(commentDAO, postDAO, userDAO);
        PostService postService = new PostService(userDAO, postDAO, forumDAO);

        UserResource userResource = new UserResource(userService, postService, commentService);
        PostResource postResource = new PostResource(postService, commentService);
        ForumResource forumResource = new ForumResource(forumService, postService);
        CommentResource commentResource = new CommentResource(commentService);

        environment.jersey().register(new AuthDynamicFeature(
                new BasicCredentialAuthFilter.Builder<AuthenticatedUser>()
                        .setAuthenticator(new UserAuthenticator(userDAO, vaultDAO))
                        .setAuthorizer(new UserAuthorizer())
                        .setRealm("authentication")
                        .buildAuthFilter()));
        environment.jersey().register(RolesAllowedDynamicFeature.class);
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(AuthenticatedUser.class));

        environment.jersey().register(userResource);
        environment.jersey().register(postResource);
        environment.jersey().register(forumResource);
        environment.jersey().register(commentResource);
        environment.jersey().register(new CorsFilter());
        environment.jersey().register(JdbiExceptionsBundle.class);
    }

    @Override
    public void initialize(Bootstrap<Config> bootstrap) {
        bootstrap.addBundle(new MigrationsBundle<Config>() {
            public PooledDataSourceFactory getDataSourceFactory(Config config) {
                return config.getDatabase();
            }
        });
    }
}
