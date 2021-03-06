/*
 *  Copyright 2016 Kings College London and The Hyve
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.radarcns.mongodb;

import com.mongodb.MongoClientOptions;
import com.mongodb.MongoException;

import org.apache.kafka.common.config.AbstractConfig;
import org.bson.Document;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.radarcns.mongodb.MongoDbSinkConnector.COLLECTION_FORMAT;
import static org.radarcns.mongodb.MongoDbSinkConnector.MONGO_DATABASE;
import static org.radarcns.mongodb.MongoDbSinkConnector.MONGO_HOST;
import static org.radarcns.mongodb.MongoDbSinkConnector.MONGO_PASSWORD;
import static org.radarcns.mongodb.MongoDbSinkConnector.MONGO_PORT;
import static org.radarcns.mongodb.MongoDbSinkConnector.MONGO_PORT_DEFAULT;
import static org.radarcns.mongodb.MongoDbSinkConnector.MONGO_USERNAME;

public class MongoWrapperTest {
    @Test
    public void checkConnectionWithoutCredentials() throws Exception {
        Map<String, Object> configMap = new HashMap<>();
        configMap.put(MONGO_USERNAME, null);
        configMap.put(MONGO_PASSWORD, null);
        configMap.put(MONGO_PORT, MONGO_PORT_DEFAULT);
        configMap.put(MONGO_HOST, "localhost");
        configMap.put(MONGO_DATABASE, "mydb");
        configMap.put(COLLECTION_FORMAT, "{$topic}");

        Field credentialsField = MongoWrapper.class.getDeclaredField("credentials");
        credentialsField.setAccessible(true);
        MongoClientOptions timeout = MongoClientOptions.builder()
                .connectTimeout(1)
                .socketTimeout(1)
                .serverSelectionTimeout(1)
                .build();
        MongoWrapper wrapper = new MongoWrapper(new AbstractConfig(configMap), timeout);

        assertThat((List<?>)credentialsField.get(wrapper), empty());
        assertFalse(wrapper.checkConnection());

        try {
            wrapper.store("mytopic", new Document());
            assertTrue(false);
        } catch (MongoException | NullPointerException ex) {
            assertTrue(true);
        }

        wrapper.close();
    }

    @Test
    public void checkConnectionWithCredentials() throws Exception {
        Map<String, Object> configMap = new HashMap<>();
        configMap.put(MONGO_USERNAME, "myuser");
        configMap.put(MONGO_PASSWORD, "mypassword");
        configMap.put(MONGO_PORT, MONGO_PORT_DEFAULT);
        configMap.put(MONGO_HOST, "localhost");
        configMap.put(MONGO_DATABASE, "mydb");
        configMap.put(COLLECTION_FORMAT, "{$topic}");

        Field credentialsField = MongoWrapper.class.getDeclaredField("credentials");
        credentialsField.setAccessible(true);
        MongoClientOptions timeout = MongoClientOptions.builder()
                .connectTimeout(1)
                .socketTimeout(1)
                .serverSelectionTimeout(1)
                .build();
        MongoWrapper wrapper = new MongoWrapper(new AbstractConfig(configMap), timeout);

        assertThat((List<?>)credentialsField.get(wrapper), hasSize(1));
        assertFalse(wrapper.checkConnection());

        try {
            wrapper.store("mytopic", new Document());
            assertTrue(false);
        } catch (MongoException | NullPointerException ex) {
            assertTrue(true);
        }

        wrapper.close();
    }
}