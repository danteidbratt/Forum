package se.donut.postservice.util;

import se.donut.postservice.model.domain.Submission;

import java.time.Duration;
import java.util.Date;

import static java.lang.Math.*;

public final class Ranking {

    private static final long earliestPossibleTimestamp = 1557187200;
    private static final double timeInfluence = Duration.ofMinutes(5).getSeconds();

    private static long epochSeconds(Date date) {
        return (date.getTime() - new Date(0).getTime()) / 1000;
    }

    public static double calculateHeat(Submission submission) {
        long seconds = epochSeconds(submission.getCreatedAt()) - earliestPossibleTimestamp;
        int score = submission.getScore();
        double order = log10(max(abs(score), 1));
        int sign = Integer.compare(score, 0);
        return sign * order + seconds / timeInfluence;
    }

}
