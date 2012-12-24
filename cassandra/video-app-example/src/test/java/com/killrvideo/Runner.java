package com.killrvideo;


import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.factory.HFactory;

import java.sql.Timestamp;
import java.util.UUID;

public class Runner {


	// CHANGE ME!! If you have a different cluster name, this is the place to change it.
	private static final String CLUSTER_NAME = "Test Cluster";
	
	// If you used the database setup file with this project, the default should be fine. Change if you need to.
	private static final String KEYSPACE = "Killervideo";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		System.out.println(UUID.randomUUID().toString());

		UUID videoId = UUID.randomUUID();

		Cluster myCluster = HFactory.getOrCreateCluster(CLUSTER_NAME, "localhost:9160");
		Keyspace keyspace = HFactory.createKeyspace(KEYSPACE, myCluster);

		// Create a user and video for testing
        String username = "pmcfadin";
        User user = new User(username, "secretPassword123", "Patrick", "McFadin");
		Video video = new Video(videoId, "Funny Cat Video", username, "A video about a cat. It's pretty funny.", new String[]{"Cats","Funny","lol"});
		
		BusinessLogic bl = new BusinessLogic();

        User userRead = bl.getUser(username, keyspace);
		System.out.println("Setting a user" + userRead);
		bl.setUser(user, keyspace);

		System.out.println("Setting a video");
		bl.setVideo(video, keyspace);

		System.out.println("Setting a video with tag index");
		bl.setVideoWithTagIndex(video, keyspace);

		System.out.println("Getting video by UUID");
		Video videoReturned = bl.getVideoByUUID(videoId, keyspace);
        System.out.println("Getting video by UUID: " + videoReturned);

		System.out.println("Setting a comment for a video : " + bl.getComments(video.getVideoId(), keyspace));
		bl.setComment(video, "Kinda meh. I like southpark better", new Timestamp(new java.util.Date().getTime()), keyspace);
        System.out.println("Setting a comment for a video : " + bl.getComments(video.getVideoId(), keyspace));

		System.out.println("Rating a video");
		bl.setRating(videoId, 4, keyspace);
        System.out.println("Rating a video : " + bl.getRating(videoId, keyspace));


		System.out.println("Setting a start event");
		Timestamp startEvent = new Timestamp(new java.util.Date().getTime());
		bl.setVideoStartEvent(videoId, username, startEvent, keyspace);

		System.out.println("Setting a stop event");
		Timestamp stopEvent = new Timestamp(new java.util.Date().getTime());
		Timestamp videoTimestamp = new Timestamp(new java.util.Date().getTime());
		bl.setVideoStopEvent(videoId, username, stopEvent, videoTimestamp, keyspace);

        bl.getVideoLastStopEvent(videoId, username, keyspace);
	}


}
