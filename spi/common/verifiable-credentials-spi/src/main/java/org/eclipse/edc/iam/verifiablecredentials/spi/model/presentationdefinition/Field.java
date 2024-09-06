/*
 *  Copyright (c) 2024 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Bayerische Motoren Werke Aktiengesellschaft (BMW AG) - initial API and implementation
 *
 */

package org.eclipse.edc.iam.verifiablecredentials.spi.model.presentationdefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Field {
    private List<String> paths = new ArrayList<>();
    private String id;
    private String name;
    private String purpose;
    private FilterExpression expr;

    private Field() {

    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPurpose() {
        return purpose;
    }

    public List<String> getPaths() {
        return paths;
    }

    public FilterExpression getExpr() {
        return expr;
    }


    public static final class Builder {
        private final Field field;

        private Builder() {
            field = new Field();
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder paths(List<String> paths) {
            this.field.paths = paths;
            return this;
        }

        public Builder id(String id) {
            this.field.id = id;
            return this;
        }

        public Builder name(String name) {
            this.field.name = name;
            return this;
        }

        public Builder purpose(String purpose) {
            this.field.purpose = purpose;
            return this;
        }

        public Builder expr(FilterExpression expr) {
            this.field.expr = expr;
            return this;
        }

        public Field build() {
            Objects.requireNonNull(field.paths, "Must contain a paths property.");
            return field;
        }
    }
}
