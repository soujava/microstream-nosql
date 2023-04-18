/*
 *  Copyright (c) 2023 Rudy
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

import jakarta.nosql.Column;
import jakarta.nosql.Id;

public class MutableEntity {

    @Id
    private String id;

    @Column
    private String value;

    public MutableEntity(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    static MutableEntity of(String id, String value) {
        MutableEntity result = new MutableEntity(id);
        result.setValue(value);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MutableEntity that = (MutableEntity) o;

        if (!id.equals(that.id)) {
            return false;
        }
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }
}
