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
import se.donut.postservice.auth.UserAuthenticator;
import se.donut.postservice.auth.UserAuthorizer;
import se.donut.postservice.auth.AuthenticatedUser;
import se.donut.postservice.model.mapper.CommentMapper;
import se.donut.postservice.model.mapper.ForumMapper;
import se.donut.postservice.model.mapper.PostMapper;
import se.donut.postservice.model.mapper.UserMapper;
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

        VaultAccessor vaultAccessor = new PostgresVaultDAO(jdbi);
        AuthService authService = new AuthService(vaultAccessor);

        UserAccessor userAccessor = new PostgresUserDAO(jdbi);
        UserService userService = new UserService(userAccessor);
        UserResource userResource = new UserResource(userService);

        PostAccessor postAccessor = new PostgresPostDAO(jdbi);
        PostService postService = new PostService(postAccessor);
        PostResource postResource = new PostResource(postService);

        ForumAccessor forumAccessor = new PostgresForumDAO(jdbi);
        ForumService forumService = new ForumService(forumAccessor);
        ForumResource forumResource = new ForumResource(forumService);

        CommentAccessor commentAccessor = new PostgresCommentDAO(jdbi);
        CommentService commentService = new CommentService(commentAccessor, postAccessor);
        CommentResource commentResource = new CommentResource(commentService);

        environment.jersey().register(new AuthDynamicFeature(
                new BasicCredentialAuthFilter.Builder<AuthenticatedUser>()
                        .setAuthenticator(new UserAuthenticator(authService, userService))
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
