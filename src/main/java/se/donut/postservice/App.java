package se.donut.postservice;

import io.dropwizard.Application;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.jdbi3.bundles.JdbiExceptionsBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.jdbi.v3.core.Jdbi;
import se.donut.postservice.model.mapper.CommentMapper;
import se.donut.postservice.model.mapper.ForumMapper;
import se.donut.postservice.model.mapper.PostMapper;
import se.donut.postservice.model.mapper.UserMapper;
import se.donut.postservice.repository.CommentAccessor;
import se.donut.postservice.repository.ForumAccessor;
import se.donut.postservice.repository.PostAccessor;
import se.donut.postservice.repository.UserAccessor;
import se.donut.postservice.repository.postgresql.PostgresCommentDAO;
import se.donut.postservice.repository.postgresql.PostgresForumDAO;
import se.donut.postservice.repository.postgresql.PostgresPostDAO;
import se.donut.postservice.repository.postgresql.PostgresUserDAO;
import se.donut.postservice.resource.CommentResource;
import se.donut.postservice.resource.ForumResource;
import se.donut.postservice.resource.PostResource;
import se.donut.postservice.resource.UserResource;
import se.donut.postservice.service.CommentService;
import se.donut.postservice.service.ForumService;
import se.donut.postservice.service.PostService;
import se.donut.postservice.service.UserService;

public class App extends Application<AppConfig> {

    public static void main(String[] args) throws Exception {
        App app = new App();
        app.run(args);
    }

    @Override
    public String getName() {
        return "postservice";
    }

//    @Override
//    public void run(AppConfig appConfig, Environment environment) throws Exception {
//        Firestore firestoreDB = setupFirestore();
//
//        UserAccessor userAccessor = new FirestoreUserDAO(firestoreDB);
//        UserService userService = new UserService(userAccessor);
//        UserResource userResource = new UserResource(userService);
//
//        PostAccessor postAccessor = new FirestorePostDAO(firestoreDB);
//        CommentAccessor commentAccessor = new FirestoreCommentDAO(firestoreDB);
//        PostService postService = new PostService(postAccessor, commentAccessor);
//        PostResource postResource = new PostResource(postService);
//
//        ForumAccessor forumAccessor = new FirestoreForumDAO(firestoreDB);
//        ForumService forumService = new ForumService(forumAccessor);
//        ForumResource forumResource = new ForumResource(forumService);
//
//        environment.jersey().register(userResource);
//        environment.jersey().register(postResource);
//        environment.jersey().register(forumResource);
//        environment.jersey().register(JdbiExceptionsBundle.class);
//    }
//
//    private Firestore setupFirestore() throws IOException {
//        InputStream serviceAccount = new FileInputStream("../keys/forum-55ac6-firebase-adminsdk-j0pgj-82454860ff.json");
//        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
//
//        FirebaseOptions options = new FirebaseOptions.Builder()
//                .setCredentials(credentials)
//                .setDatabaseUrl("https://forum-55ac6.firebaseio.com/")
//                .build();
//
//        FirebaseApp.initializeApp(options);
//        return FirestoreClient.getFirestore();
//    }

    @Override
    public void run(AppConfig appConfig, Environment environment) throws Exception {
        JdbiFactory jdbiFactory = new JdbiFactory();
        Jdbi jdbi = jdbiFactory.build(environment, appConfig.getDatabase(), "postgresql");

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
        CommentService commentService = new CommentService(commentAccessor);
        CommentResource commentResource = new CommentResource(commentService);

        jdbi.registerRowMapper(new UserMapper());
        jdbi.registerRowMapper(new ForumMapper());
        jdbi.registerRowMapper(new PostMapper());
        jdbi.registerRowMapper(new CommentMapper());

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
