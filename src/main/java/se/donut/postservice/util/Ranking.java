package se.donut.postservice.util;

import se.donut.postservice.model.domain.Submission;

import java.time.Duration;
import java.util.Date;

import static java.lang.Math.*;

public final class Ranking {

    private static final long EARLIEST_POSSIBLE_TIMESTAMP = 1557187200;
    private static final double TIME_THRESHOLD = Duration.ofMinutes(5).getSeconds();

    /**
     * @param date The date at which the submission was created.
     * @return The number of seconds that have
     * passed since the beginning of this service.
     */
    private static long calculateSeconds(Date date) {
        return (date.getTime() / 1000) - EARLIEST_POSSIBLE_TIMESTAMP;
    }

    /**
     *
     * @param submission The submission that heat
     *                   value will be calculated from.
     * @return The heat value by which submissions
     * may be sorted according to the Hot sorting algorithm.
     */
    public static double calculateHeat(Submission submission) {
        long seconds = calculateSeconds(submission.getCreatedAt());
        int score = submission.getScore();
        double order = log10(max(abs(score), 1));
        int sign = Integer.compare(score, 0);
        return sign * order + seconds / TIME_THRESHOLD;
    }

}
