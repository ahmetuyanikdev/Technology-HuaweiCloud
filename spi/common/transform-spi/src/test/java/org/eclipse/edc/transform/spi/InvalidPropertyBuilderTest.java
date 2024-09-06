/*
 *  Copyright (c) 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
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

package org.eclipse.edc.transform.spi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class InvalidPropertyBuilderTest {
    private TransformerContext context;
    private InvalidPropertyBuilder builder;

    @BeforeEach
    void setUp() {
        context = mock(TransformerContext.class);
        builder = new InvalidPropertyBuilder(context);
    }

    @Test
    void verify_reportNoData() {
        builder.report();
        verify(context).reportProblem(eq("Property 'null' was invalid: unknown"));
    }

    @Test
    void verify_report() {
        builder.type("test").property("property").value("value").report();
        verify(context).reportProblem(eq("test property 'property' was invalid: value"));
    }

    @Test
    void verify_reportProperty() {
        builder.property("property").value("value").report();
        verify(context).reportProblem(eq("Property 'property' was invalid: value"));
    }

    @Test
    void verify_reportNoProperty() {
        builder.type("test").value("value").report();
        verify(context).reportProblem(eq("test property 'null' was invalid: value"));
    }

}
