/*
 *  Copyright (c) 2023 Otavio
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0.
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations
 *  under the License.
 *
 */

package expert.os.integration.microstream;

import one.microstream.collections.lazy.LazyHashMap;
import one.microstream.persistence.types.Persister;
import one.microstream.persistence.types.Storer;
import one.microstream.storage.types.StorageManager;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;


/**
 * The data structure used at Microstream on both {@link jakarta.nosql.Template} and any {@link jakarta.data.repository.DataRepository}
 * implementation.
 * <p>
 * It is a wrapper of {@link LazyHashMap}
 */
class DataStorage {

    private final Map<Object, Object> data;
    private final Persister persister;

    DataStorage(Map<Object, Object> data, Persister persister) {
        this.data = data;
        this.persister = persister;
    }

    /**
     * Associates the specified value with the specified key in this map.
     *
     * @param key   the key
     * @param value the entity
     * @param <K>   the key type
     * @param <V>   the entity type
     */
    public synchronized <K, V> void put(K key, V value) {
        Objects.requireNonNull(key, "key is required");
        Objects.requireNonNull(value, "value is required");
        this.data.put(key, value);
        commit();
    }

    /**
     * Inserts multiples entries on the data storage
     * @param entries the entries
     */
    public synchronized void put(List<Entry> entries) {
        Objects.requireNonNull(entries, "entries is required");
        Map<Object, Object> entities = entries.stream().collect(toMap(Entry::key,Entry::value));
        this.data.putAll(entities);
        this.commit();
    }

    /**
     * * Returns the value to which the specified key is mapped,
     * or {@code Optional#empty()} if this map contains no mapping for the key.
     *
     * @param key the key or ID
     * @param <K> the key type
     * @param <V> the entity type
     * @return the entity of {@link Optional#empty()}
     */
    public synchronized <K, V> Optional<V> get(K key) {
        Objects.requireNonNull(key, "key is required");
        return (Optional<V>) Optional.ofNullable(this.data.get(key));
    }

    /**
     * Removes the mapping for a key from this map if it is present
     *
     * @param key the key
     * @param <K> the key type
     */
    public synchronized <K> void remove(K key) {
        Objects.requireNonNull(key, "key is required");
        this.data.remove(key);
        this.commit();
    }

    /**
     * Removes the mapping for a key from this map if it is present as Bulk operation.
     * @param keys the keys entries
     * @param <K> the key type
     */
    public synchronized <K> void remove(Iterable<K> keys) {
        Objects.requireNonNull(keys, "keys is required");
        keys.forEach(this.data::remove);
        this.commit();
    }
    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return the number of key-value mappings in this map
     */
    public synchronized int size() {
        return this.data.size();
    }

    /**
     * Returns {@code true} if this map contains no key-value mappings.
     *
     * @return {@code true} if this map contains no key-value mappings
     */
    public synchronized boolean isEmpty() {
        return this.data.isEmpty();
    }

    /**
     * Returns a {@link Collection} view of the values contained in this map.
     *
     * @param <V> the entity type
     * @return a collection view of the values contained in this map
     */
    public synchronized <V> Stream<V> values() {
        if (data.isEmpty()) {
            return Stream.empty();
        }
        return (Stream<V>) this.data.values().stream();
    }

    /**
     * Removes all entities from this structure .
     * The map will be empty after this call returns.
     */
    public void clear() {
        this.data.clear();
        this.commit();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataStorage that = (DataStorage) o;
        return Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(data);
    }

    @Override
    public String toString() {
        return "DataStructure{" +
                "data=" + data +
                '}';
    }

    static DataStorage of(Map<Object, Object> data, StorageManager manager) {
        Objects.requireNonNull(data, "data is required");
        Objects.requireNonNull(manager, "manager is required");
        return new DataStorage(data, manager);
    }

    private synchronized void commit() {
        Storer eagerStorer = persister.createEagerStorer();
        eagerStorer.store(this.data);
        eagerStorer.commit();
    }


}
