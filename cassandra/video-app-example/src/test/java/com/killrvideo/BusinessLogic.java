package com.killrvideo;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.serializers.UUIDSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.CounterSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.exceptions.HectorException;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.ColumnQuery;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.SliceCounterQuery;
import me.prettyprint.hector.api.query.SliceQuery;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class BusinessLogic {

	private static StringSerializer stringSerializer = StringSerializer.get();
	private static UUIDSerializer uuidSerializer = UUIDSerializer.get();
    private static final String CF_USERS = "users";

    public static final String FIRSTNAME = "firstname";
    public static final String CF_COMMENTS = "comments";
    public static final String CF_VIDEO_RATING = "video_rating";
    public static final String RATING_COUNT = "rating_count";
    public static final String RATING_TOTAL = "rating_total";
    public static final String CF_VIDEO_EVENT = "video_event";

    public void setUser(User user, Keyspace keyspace) {

		Mutator<String> mutator = HFactory.createMutator(keyspace, stringSerializer);

		try {

			mutator.addInsertion(user.getUsername(), CF_USERS,
					HFactory.createStringColumn(FIRSTNAME, user.getFirstname()));
			mutator.addInsertion(user.getUsername(), CF_USERS,
					HFactory.createStringColumn("lastname", user.getLastname()));
			mutator.addInsertion(user.getUsername(), CF_USERS,
					HFactory.createStringColumn("password", user.getPassword()));

			mutator.execute();
		} catch (HectorException he) {
			throw he;
		}
	}

	public User getUser(String username, Keyspace keyspace) {
        ColumnQuery<String, String, String> columnQuery = HFactory.createColumnQuery(keyspace, stringSerializer, stringSerializer, stringSerializer);
        columnQuery.setColumnFamily(CF_USERS);
        columnQuery.setKey(username);
        columnQuery.setName(FIRSTNAME);
        QueryResult<HColumn<String, String>> result = columnQuery.execute();

        User user = new User();
        user.setFirstname(result.get().getValue());
		// TODO use slice query or something better?
		return user;
	}

	public void setVideo(Video video, Keyspace keyspace) {

		Mutator<UUID> mutator = HFactory.createMutator(keyspace, UUIDSerializer.get());

		try {
			mutator.addInsertion(video.getVideoId(), "videos",
					HFactory.createStringColumn("videoname", video.getVideoName()));
			mutator.addInsertion(video.getVideoId(), "videos",
					HFactory.createStringColumn("username", video.getUsername()));
			mutator.addInsertion(video.getVideoId(), "videos",
					HFactory.createStringColumn("description", video.getDescription()));
			mutator.addInsertion(video.getVideoId(), "videos",
					HFactory.createStringColumn("tags", video.getDelimitedTags()));

			mutator.execute();
		} catch (HectorException he) {
			he.printStackTrace();
		}
	}

	public Video getVideoByUUID(UUID videoId, Keyspace keyspace) {

		Video video = new Video();

		// Create a slice query. We'll be getting specific column names
		SliceQuery<UUID, String, String> sliceQuery = HFactory.createSliceQuery(keyspace, uuidSerializer,
				stringSerializer, stringSerializer);
		sliceQuery.setColumnFamily("videos");
		sliceQuery.setKey(videoId);

		sliceQuery.setColumnNames("videoname", "username", "description", "tags");

		// Execute the query and get the list of columns
		ColumnSlice<String, String> result = sliceQuery.execute().get();

		// Get each column by name and add them to our video object
		video.setVideoName(result.getColumnByName("videoname").getValue());
		video.setUsername(result.getColumnByName("username").getValue());
		video.setDescription(result.getColumnByName("description").getValue());
		video.setTags(result.getColumnByName("tags").getValue().split(","));

		return video;
	}

	public void setVideoWithTagIndex(Video video, Keyspace keyspace) {

		Mutator<UUID> UUIDmutator = HFactory.createMutator(keyspace, UUIDSerializer.get());
		Mutator<String> mutator = HFactory.createMutator(keyspace, stringSerializer);

		try {

			UUIDmutator.addInsertion(video.getVideoId(), "videos",
					HFactory.createStringColumn("videoname", video.getVideoName()));
			UUIDmutator.addInsertion(video.getVideoId(), "videos",
					HFactory.createStringColumn("username", video.getUsername()));
			UUIDmutator.addInsertion(video.getVideoId(), "videos",
					HFactory.createStringColumn("description", video.getDescription()));
			UUIDmutator.addInsertion(video.getVideoId(), "videos",
					HFactory.createStringColumn("tags", video.getDelimitedTags()));

			for (String tag : video.getTags()) {
				mutator.addInsertion(tag, "tag_index", HFactory.createStringColumn(video.getVideoId().toString(), ""));
			}

			UUIDmutator.execute();
			mutator.execute();

		} catch (HectorException he) {
			he.printStackTrace();
		}

	}

	public void setVideoWithUserIndex(Video video, Keyspace keyspace) {
		// TODO Implement this method
		/*
		 * This mthod is similar to the setVideo but with a subtle twist. When
		 * you insert a new video, you will need to insert into the
		 * username_video_index at the same time for username to video lookups.
		 */

	}

	public void setComment(Video video, String comment, Timestamp timestamp, Keyspace keyspace) {

		Mutator<UUID> mutator = HFactory.createMutator(keyspace, UUIDSerializer.get());

		try {
			String columnName = video.getUsername() + ":" + timestamp;
			mutator.addInsertion(video.getVideoId(), CF_COMMENTS, HFactory.createStringColumn(columnName, comment));

			mutator.execute();
		} catch (HectorException he) {
			he.printStackTrace();
		}
	}

	public ArrayList<String> getComments(UUID videoId, Keyspace keyspace) {
		SliceQuery<UUID, String, String> sliceQuery = HFactory.createSliceQuery(keyspace, uuidSerializer, stringSerializer, stringSerializer);
        sliceQuery.setColumnFamily(CF_COMMENTS);
        sliceQuery.setKey(videoId);
        sliceQuery.setRange(null , null, false, 200);
        QueryResult<ColumnSlice<String, String>> result = sliceQuery.execute();

        ArrayList<String> comments = new ArrayList<String>();
        for(HColumn<String, String> column : result.get().getColumns()) {
            comments.add(column.getValue());
        }
		// TODO Implement
		/*
		 * Each video can have a unbounded list of comments associated with it.
		 * This method should return all comments associated with one video.
		 */

		return comments;
	}

	public ArrayList<String> getCommentsOnTimeSlice(Timestamp startTimestamp, Timestamp stopTimestamp, UUID videoId) {
		// TODO Implement
		/*
		 * Each video can have a unbounded list of comments associated with it.
		 * This method should return comments from one timestamp to another.
		 */

		return null;
	}

	public void setRating(UUID videoId, long ratingNumber, Keyspace keyspace) {
		Mutator<UUID> mutator = HFactory.createMutator(keyspace, UUIDSerializer.get());

		try {

			mutator.addCounter(videoId, CF_VIDEO_RATING, HFactory.createCounterColumn(RATING_COUNT, 1));
			mutator.addCounter(videoId, CF_VIDEO_RATING, HFactory.createCounterColumn(RATING_TOTAL, ratingNumber));

			mutator.execute();
		} catch (HectorException he) {
			he.printStackTrace();
		}
	}

	public float getRating(UUID videoId, Keyspace keyspace) {
		// TODO Implement
		/*
		 * Each video has two things. a rating_count and rating_total. The
		 * average rating is calculated by taking the total and dividing by the
		 * count. Build the logic to get both numbers and return the average.
		 */

        SliceCounterQuery<UUID, String> counterQuery = HFactory.createCounterSliceQuery(keyspace, uuidSerializer, stringSerializer);
        counterQuery.setColumnFamily(CF_VIDEO_RATING);
        counterQuery.setKey(videoId);
        counterQuery.setColumnNames(RATING_COUNT, RATING_TOTAL);
        QueryResult<CounterSlice<String>> result = counterQuery.execute();
        CounterSlice<String> stringCounterSlice = result.get();
        return stringCounterSlice.getColumnByName(RATING_TOTAL).getValue()/ stringCounterSlice.getColumnByName(RATING_COUNT).getValue();

	}

	public void setVideoStartEvent(UUID videoId, String username, Timestamp timestamp, Keyspace keyspace) {

		Mutator<String> mutator = HFactory.createMutator(keyspace, stringSerializer);

		try {

			mutator.addInsertion(username + ":" + videoId, CF_VIDEO_EVENT,
					HFactory.createStringColumn("start:" + timestamp, ""));

			mutator.execute();
		} catch (HectorException he) {
			he.printStackTrace();
		}

	}

	public void setVideoStopEvent(UUID videoId, String username, Timestamp stopEvent, Timestamp videoTimestamp,
			Keyspace keyspace) {
		Mutator<String> mutator = HFactory.createMutator(keyspace, stringSerializer);

		try {
            System.out.println("Adding stop: " + stopEvent);
			mutator.addInsertion(username + ":" + videoId, CF_VIDEO_EVENT,
					HFactory.createStringColumn("stop:" + stopEvent, videoTimestamp.toString()));

			mutator.execute();
		} catch (HectorException he) {
			he.printStackTrace();
		}
	}

	public Timestamp getVideoLastStopEvent(UUID videoId, String username, Keyspace keyspace) {

        SliceQuery<String, String, String> sliceQuery = HFactory.createSliceQuery(keyspace, stringSerializer, stringSerializer, stringSerializer);
        sliceQuery.setColumnFamily(CF_VIDEO_EVENT);
        sliceQuery.setKey(username + ":" + videoId);
        try{
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        String stopWithCurrentTimestamp = "stop:" + new Timestamp(new Date().getTime());
        sliceQuery.setRange(stopWithCurrentTimestamp, null, true, 2);
        QueryResult<ColumnSlice<String, String>> result = sliceQuery.execute();

        // TODO Implement
		/*
		 * This method will return the video timestamp of the last stop event
		 * for a given video identified by videoid. As a hint, you will be using
		 * a getSlice to find certain strings to narrow the search.
		 */

		return null;
	}
}
