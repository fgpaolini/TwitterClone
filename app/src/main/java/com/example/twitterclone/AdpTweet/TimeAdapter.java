package com.example.twitterclone.AdpTweet;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;


public class TimeAdapter {

    private ZonedDateTime currentDateTime;
    private ZonedDateTime tweetTime;
    private Duration duration;
    private String timeofTweet;
    private long epochTweetTime;
    private long epochCurrentTime;

    private String tweetTimeString;

    private String currentTimeString;

    private String DATE_TIME_OF_TWEET;

    public TimeAdapter(String tweetTimeString, String currentTimeString) {
        this.tweetTimeString = tweetTimeString;
        this.currentTimeString = currentTimeString;
    }

    public ZonedDateTime tweetTime() {

        long epochTweetTime = Long.parseLong(tweetTimeString);

        tweetTime = Instant.ofEpochMilli(epochTweetTime).atZone(ZoneId.of("Europe/Madrid"));

        return tweetTime;
    }

    public ZonedDateTime getCurrentDateTime() {

        long epochCurrentTime = Long.parseLong(currentTimeString);

        currentDateTime = Instant.ofEpochMilli(epochCurrentTime).atZone(ZoneId.of("Europe/Madrid"));

        return currentDateTime;
    }



    public String getTime() {


        duration = Duration.between(tweetTime, currentDateTime);

        int seconds = 0;
        int minutes = 0;
        int hours = 0;
        int days = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            seconds = duration.toSecondsPart();
            hours = duration.toHoursPart();
            minutes = duration.toMinutesPart();
            days = (int) duration.toDaysPart();
        }

        if (days <= 7) {

            if (days >= 1 && days < 7) {
                DATE_TIME_OF_TWEET = days + "d";
            }
            else if (hours >= 1 && hours <= 23) {
                DATE_TIME_OF_TWEET = hours + "h";
            }
            else if (minutes >= 1 && minutes <=59) {
                DATE_TIME_OF_TWEET =  minutes + "m";
            }
            else if (seconds >= 1 && seconds <= 59) {
                DATE_TIME_OF_TWEET =  seconds + "s";
            }
        }
        else {

            DATE_TIME_OF_TWEET =  tweetTime.getDayOfMonth() + "/" + tweetTime.getMonthValue() + "/" + tweetTime.getYear() + " " + tweetTime.getHour() + ":" + tweetTime.getMinute() + ":" + tweetTime.getSecond();

        }

        return DATE_TIME_OF_TWEET;
    }

}
