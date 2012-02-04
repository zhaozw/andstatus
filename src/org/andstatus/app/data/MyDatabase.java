/* 
 * Copyright (C) 2008 Torgny Bjers
 * Copyright (c) 2012 yvolk (Yuri Volkov), http://yurivolkov.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.andstatus.app.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Convenience definitions for MyProvider
 */
public final class MyDatabase {

	public static final String TWITTER_DATE_FORMAT = "EEE MMM dd HH:mm:ss Z yyyy";

	// This class cannot be instantiated
	private MyDatabase() {
	}

	/**
	 * Tweets table
	 * 
	 */
	public static final class Tweets implements BaseColumns {
		public static final Uri CONTENT_URI = Uri.parse("content://" + MyProvider.AUTHORITY + "/tweets");
		public static final Uri SEARCH_URI = Uri.parse("content://" + MyProvider.AUTHORITY + "/tweets/search");
        public static final Uri CONTENT_COUNT_URI = Uri.parse("content://" + MyProvider.AUTHORITY + "/tweets/count");
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/org.andstatus.provider.status";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/org.andstatus.provider.status";
		public static final String DEFAULT_SORT_ORDER = "sent DESC";

		// Table columns
		public static final String AUTHOR_ID = "author_id";
		public static final String MESSAGE = "message";
		public static final String SOURCE = "source";
		public static final String TWEET_TYPE = "tweet_type";
		public static final String IN_REPLY_TO_STATUS_ID = "in_reply_to_status_id";
		public static final String IN_REPLY_TO_AUTHOR_ID = "in_reply_to_author_id";
		public static final String FAVORITED = "favorited";
		public static final String CREATED_DATE = "created";
		public static final String SENT_DATE = "sent";

	}

	/**
	 * Direct Messages table
	 */
	public static final class DirectMessages implements BaseColumns {
		public static final Uri CONTENT_URI = Uri.parse("content://" + MyProvider.AUTHORITY + "/directmessages");
		public static final Uri SEARCH_URI = Uri.parse("content://" + MyProvider.AUTHORITY + "/directmessages/search");
        public static final Uri CONTENT_COUNT_URI = Uri.parse("content://" + MyProvider.AUTHORITY + "/directmessages/count");
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/org.andstatus.provider.directmessage";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/org.andstatus.provider.directmessage";
		public static final String DEFAULT_SORT_ORDER = "sent DESC";

		// Table columns
		public static final String AUTHOR_ID = "author_id";
		public static final String MESSAGE = "message";
		public static final String CREATED_DATE = "created";
		public static final String SENT_DATE = "sent";
	}

	/**
	 * Users table (they are both senders AND recipients)
	 */
	public static final class Users implements BaseColumns {
		public static final Uri CONTENT_URI = Uri.parse("content://" + MyProvider.AUTHORITY + "/users");
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/org.andstatus.provider.user";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/org.andstatus.provider.user";
		public static final String DEFAULT_SORT_ORDER = "author_id ASC";

		// Table columns
		public static final String AUTHOR_ID = "author_id";
		public static final String FOLLOWING = "following";
		public static final String AVATAR_IMAGE = "avatar_blob";
		public static final String MODIFIED_DATE = "modified";
		public static final String CREATED_DATE = "created";
	}
}