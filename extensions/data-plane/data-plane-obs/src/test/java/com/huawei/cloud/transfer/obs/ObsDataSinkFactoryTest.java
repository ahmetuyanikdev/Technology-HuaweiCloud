/*
 *  Copyright (c) 2024 Huawei Technologies
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Huawei Technologies - initial API and implementation
 *
 */

package com.huawei.cloud.transfer.obs;

import com.huawei.cloud.obs.ObsBucketSchema;
import com.huawei.cloud.obs.ObsClientProvider;
import org.eclipse.edc.json.JacksonTypeManager;
import org.eclipse.edc.junit.assertions.AbstractResultAssert;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.security.Vault;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.eclipse.edc.spi.types.domain.transfer.DataFlowStartMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.huawei.cloud.obs.TestFunctions.dataAddressWithCredentials;
import static com.huawei.cloud.obs.TestFunctions.dataAddressWithoutCredentials;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ObsDataSinkFactoryTest {

    private final Vault vaultMock = mock();
    private final ExecutorService executor = Executors.newFixedThreadPool(1);
    private final ObsClientProvider obsClientProviderMock = mock();
    private final ObsDataSinkFactory factory = new ObsDataSinkFactory(vaultMock, new JacksonTypeManager(), mock(), executor, obsClientProviderMock);

    @BeforeEach
    void setUp() {
        when(obsClientProviderMock.obsClient(anyString(), any())).thenReturn(mock());
    }

    @Test
    void validate_success() {
        var addr = DataAddress.Builder.newInstance()
                .type(ObsBucketSchema.TYPE)
                .keyName("test-keyname")
                .property(ObsBucketSchema.BUCKET_NAME, "test-bucket")
                .property(ObsBucketSchema.ENDPOINT, "test-endpoint")
                .build();
        AbstractResultAssert.assertThat(factory.validateRequest(createRequest(addr))).isSucceeded();
    }

    @Test
    void validate_whenBucketNameMissing_shouldFail() {
        var addr = DataAddress.Builder.newInstance()
                .type(ObsBucketSchema.TYPE)
                .keyName("test-keyname")
                .property(ObsBucketSchema.ENDPOINT, "test-endpoint")
                .build();
        AbstractResultAssert.assertThat(factory.validateRequest(createRequest(addr)))
                .isFailed()
                .detail().contains("Must contain property 'bucketName'. Path: bucketName. Illegal value: null");
    }

    @Test
    void validate_whenEndpointMissing_shouldFail() {
        var addr = DataAddress.Builder.newInstance()
                .type(ObsBucketSchema.TYPE)
                .keyName("test-keyname")
                .property(ObsBucketSchema.BUCKET_NAME, "test-bucket")
                .build();
        AbstractResultAssert.assertThat(factory.validateRequest(createRequest(addr)))
                .isFailed()
                .detail().contains("Must contain property 'endpoint'. Path: endpoint. Illegal value: null");
    }

    @Test
    void createSink_whenNotValid() {
        var addr = DataAddress.Builder.newInstance()
                .type(ObsBucketSchema.TYPE)
                .keyName("test-keyname")
                .property(ObsBucketSchema.BUCKET_NAME, "test-bucket")
                .build();
        assertThatThrownBy(() -> factory.createSink(createRequest(addr))).isInstanceOf(EdcException.class)
                .hasMessage("Must contain property 'endpoint'. Path: endpoint. Illegal value: null");
    }

    @Test
    void createSink_credentialsOnDataAddress() {
        var dest = dataAddressWithCredentials();
        var sink = factory.createSink(createRequest(dest));
        assertThat(sink).isNotNull().isInstanceOf(ObsDataSink.class);
        verify(vaultMock).resolveSecret(eq(dest.getKeyName()));
    }

    @Test
    void createSink_credentialsFromVault() {
        var dest = dataAddressWithoutCredentials();
        when(vaultMock.resolveSecret("aKey")).thenReturn("""
                {
                  "edctype": "dataspaceconnector:obssecrettoken",
                  "ak": "test-ak",
                  "sk": "test-sk"
                }
                """);
        var sink = factory.createSink(createRequest(dest));
        assertThat(sink).isNotNull().isInstanceOf(ObsDataSink.class);
        verify(vaultMock).resolveSecret(eq(dest.getKeyName()));
    }

    @Disabled("Untestable, as there is no (easy) way to mock the env var access")
    @Test
    void createSink_credentialsFromEnv() {
        // no-op
    }

    private DataFlowStartMessage createRequest(DataAddress destination) {
        return DataFlowStartMessage.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .processId(UUID.randomUUID().toString())
                .sourceDataAddress(DataAddress.Builder.newInstance().type(ObsBucketSchema.TYPE).build())
                .destinationDataAddress(destination)
                .build();
    }
}